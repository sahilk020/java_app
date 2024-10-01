package com.pay10.crm.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.pay10.commons.dao.*;
import com.pay10.commons.entity.CurrencyCode;
import com.pay10.commons.user.*;
import com.pay10.commons.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.api.WebStoreApiService;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dto.WebStoreApiDTO;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.crm.actionBeans.CurrencyMapProvider;
import com.pay10.crm.actionBeans.IndustryTypeCategoryProvider;
import com.pay10.crm.actionBeans.MerchantRecordUpdater;
import com.pay10.pg.core.fraudPrevention.util.AccountPasswordScrambler;

/**
 * @author Chandan, Puneet, Neeraj, Rahul , Shaiwal, Rajendra
 *
 */
public class MerchantAccountUpdateAction extends AbstractSecureAction
		implements ServletRequestAware, ModelDriven<User> {

	@Autowired
	private ChargingDetailsDao chargingDetailsDao;
	@Autowired
	private SurchargeDetailsDao surchargeDetailsDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CurrencyMapProvider currencyMapProvider;

	@Autowired
	private MerchantRecordUpdater merchantRecordUpdater;

	@Autowired
	private AccountPasswordScrambler accPwdScrambler;

	@Autowired
	private CrmValidator crmValidator;
	@Autowired
	IndustryTypeCategoryProvider industryTypeCategoryProvider;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private AuditTrailService auditTrailService;

	@Autowired
	private FiatDetailsDao fiatDetailsDao;

	@Autowired
	private CryptoDetailsDao cryptoDetailsDao;

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	private List<CurrencyCode> cryptoCurrencies;
	private Boolean google2FAKey;
	private Boolean tfaFlag;

	@Autowired
	private CurrencyCodeDao currencyCodeDao;
	private String onBoardDocList;

	private static Logger logger = LoggerFactory.getLogger(MerchantAccountUpdateAction.class.getName());
	private static final long serialVersionUID = -7290087594947995464L;
	private User user = new User();
	private List<Account> accountList = new ArrayList<Account>();
	private Map<String, List<AccountCurrency>> accountCurrencyMap = new HashMap<String, List<AccountCurrency>>();
	private List<AccountCurrency> accountCurrencyList = new ArrayList<AccountCurrency>();
	private String salt;
	private HttpServletRequest request;
	private String defaultCurrency;
	private Map<String, String> currencyMap = new LinkedHashMap<String, String>();
	private Map<String, String> industryTypesList = new TreeMap<String, String>();
	private ResponseObject responseObject = new ResponseObject();
	private long roleId;
	private List<Role> roleList;
	private long userGroupId;
	private List<UserGroup> userGroups;
	private String paytext;
	private String invoicetext;
	private boolean tncStatus;
	private String settlementDays1;

	private String registrationDate = "";

	private String activationDate = "";

	private String rDate = "";

	private String aDate = "";
	
	@Autowired
	private PropertiesManager propertiesManager;
	@Autowired
	private WebStoreApiService webStoreApiService;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	
	//@Autowired
	//MerchantKeySaltService merchantKeySaltService;
	
	@Autowired
	private UserAccountServices userAccountServices;

	public void setCryptoCurrencies(List<CurrencyCode> cryptoCurrencies) {
		this.cryptoCurrencies = cryptoCurrencies;
	}

	public List<CurrencyCode> getCryptoCurrencies(){
		return this.cryptoCurrencies;
	}

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;
	
	@SuppressWarnings("unchecked")
	public String saveAction() {
		setCryptoCurrencies(currencyCodeDao.findByChannel("CRYPTO"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();

		try {
			if(user.getRegistrationDate()!=null){
				setRegistrationDate(sdf.format(user.getRegistrationDate()));
				setrDate(sdf.format(user.getRegistrationDate()));
			}
			logger.info("#########################################" + user.getAutoMin());
			logger.info("?????????????????????????????????????????" + user.getCryptoAddress());
			User prevUser = userDao.findByEmailId(user.getEmailId());
			prevUser.setAccounts(new HashSet<>());
			prevUser.setRoles(new HashSet<>());
			String prevMerchant = mapper.writeValueAsString(prevUser);
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			setSalt(SaltFactory.getSaltProperty(user));
			Map<String, String> industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();
			industryTypesList.putAll(industryCategoryLinkedMap);
			user.setUserType(sessionUser.getUserType());
			user.setParentPayId(sessionUser.getParentPayId());
			user.setParentPayId(sessionUser.getParentPayId());

			user.setDefaultCurrency(sessionUser.getDefaultCurrency());
			setRoleList(roleDao.getByGroupId(getUserGroupId()));
			List<UserGroup> groups = userGroupDao.getUserGroups();
			groups = groups.stream()
					.filter(group -> StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
					.collect(Collectors.toList());
			setUserGroups(groups);
			user.setRole(roleDao.getRole(getRoleId()));
			user.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
			currencyMap = currencyMapProvider.currencyMap(user);
			addPaymentMapped(user, currencyMap);
			user.setPayText(paytext);
			user.setSettlementDays(Integer.parseInt(settlementDays1));
			user.setInvoiceText(invoicetext);
			logger.info("User additional values : " + user.getPayText() + " \t " + user.getInvoiceText() + " \t " + user.isTncStatus());
			setIndustryTypesList(BusinessType.getIndustryCategoryList());
			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
//				if (StringUtils.equals(userRoleType, RoleType.RISK.name())
//						&& user.getUserStatus() == UserStatusType.RISK_APPROVED) {
				// Added by Sweety
				System.out.println("vijay :: " + user.getPOFlag());
				logger.info("isChargingDetailsSet.....={}", chargingDetailsDao.isChargingDetailsSet(user.getPayId()));
				logger.info("Previous Detail : " + prevUser.getUserStatus() + "-" + prevUser.getBusinessName() + "-" + prevUser.getPayId());
				logger.info("User Detail : " + user.getUserStatus() + "-" + user.getBusinessName() + "-" + user.getPayId());
				
				
					if (!chargingDetailsDao.isChargingDetailsSet(user.getPayId())) {
					responseObject.setResponseCode("1111");
					responseObject.setResponseMessage("TDR not set, unable to update the details!!");
					addActionMessage(responseObject.getResponseMessage());
					return CrmFieldConstants.ADMIN.getValue();
				}
				
				String response = fraudPreventionMongoService.checkFrmFromTdr(user.getPayId());
				
				if (response !=null && !response.equalsIgnoreCase("success")) {

					responseObject.setResponseCode("1111");
					responseObject.setResponseMessage(response);
					addActionMessage(responseObject.getResponseMessage());
					return CrmFieldConstants.ADMIN.getValue();
				}
				if (user.getUserStatus().toString().equals(UserStatusType.ACTIVE.getStatus())) {
				logger.info("user.getUserStatus()=ACTIVE=================================================");
					logger.info("user.getUserStatus()=setActivationDate================================================="+date+" ........................."+sdf.format(date));
					user.setActivationDate(date);
					setActivationDate(sdf.format(date));
					setaDate(getActivationDate());
					if (user.isSurchargeFlag()) {
						if (!surchargeDetailsDao.isSurchargeDetailsSet(user.getPayId())) {
							responseObject.setResponseCode("1111");
							responseObject.setResponseMessage(CrmFieldConstants.USER_SURCHARGE_NOT_SET_MSG.getValue());
							addActionMessage(responseObject.getResponseMessage());
							return CrmFieldConstants.ADMIN.getValue();
						}
					} else if (!(chargingDetailsDao.isChargingDetailsSet(user.getPayId())
							|| surchargeDetailsDao.isSurchargeDetailsSet(user.getPayId()))) {
						responseObject.setResponseCode("1111");
						responseObject
								.setResponseMessage(CrmFieldConstants.USER_CHARGINGDETAILS_NOT_SET_MSG.getValue());
						addActionMessage(responseObject.getResponseMessage());
						return CrmFieldConstants.ADMIN.getValue();
					}
				} else if (user.getUserStatus().toString().equals(UserStatusType.SUSPENDED.getStatus().toString())
						|| user.getUserStatus().toString()
								.equals(UserStatusType.TRANSACTION_BLOCKED.getStatus().toString())) {
					user.setActivationDate(null);
					setActivationDate("");
					setaDate(getActivationDate());
				}

				if (user.getUserStatus() == UserStatusType.ACTIVE) {
					logger.info("user.getUserStatus() == UserStatusType.ACTIVE========================================");
					FraudPreventationConfiguration config = fraudPreventionMongoService
							.getConfigByPayId(user.getPayId(),"");
					if (config == null) {
						config = new FraudPreventationConfiguration();
						config.setId(user.getPayId());
						config.setOnIpBlocking(true);
						config.setOnEmailBlocking(true);
						config.setOnPhoneNoBlocking(true);
						config.setOnTxnAmountVelocityBlocking(true);
						config.setOnCardMaskBlocking(true);
						config.setOnVpaAddressBlocking(true);
						fraudPreventionMongoService.createConfigDoc(config,"");


					}

					List<FraudPreventionObj> rules=new ArrayList<>();
					logger.info("CurrencyMap {}",currencyMap);
					for(String currency:currencyMap.values()) {
						logger.info("Currency List "+currency);
						rules.addAll(fraudPreventionMongoService
								.fraudPreventionListByPayId(user.getPayId(),currency));
					}
					if (CollectionUtils.isEmpty(rules)) {
						List<FraudPreventionObj> allRules=new ArrayList<>();
						logger.info("CurrencyMap {}",currencyMap);
						for(String currency:currencyMap.values()) {
							allRules.addAll(fraudPreventionMongoService
									.fraudPreventionListByPayId("ALL",currency ));
						}
						allRules.forEach(ruleActive -> {
							ruleActive.setId(null);
							ruleActive.setPayId(user.getPayId());
							try {
								fraudPreventionMongoService.create(ruleActive);
							} catch (SystemException e) {
								logger.info("saveAction:: failed.", e);
							}
						});
					}
				}

				// Added by shahzad for webStore
				logger.info("before hitting the webstore api call::");
				WebStoreApiDTO weStoreApiDTO = new WebStoreApiDTO();
				if (user.isWebStoreApiEnableFlag()) {
					logger.info("inside the if check flag :={}",user.isWebStoreApiEnableFlag());
					logger.info("payid:::: :" + user.getPayId());
					User userDetails = userDao.findByPayId(user.getPayId());
					logger.info(userDetails.getBankName());
					String uuidDB = userDetails.getUuId();
					logger.info("uuid from the db" + uuidDB);
					if (StringUtils.isNotBlank(uuidDB)) {
						logger.info("inside the if UUID is Already Persent" + uuidDB);
					} else {
						logger.info("inside else::::::::::::={}",user.isWebStoreApiEnableFlag());
						weStoreApiDTO.setMerchant_name(user.getBusinessName());
						weStoreApiDTO.setPay_id(user.getPayId());
						String keySalt = propertiesManager.getSalt(user.getPayId());
						//String hostedKey = propertiesManager.getKeySalt(user.getPayId());

						//String keySalt=merchantKeySaltService.getMerchantKeySalt(user.getPayId()).getSalt();
						String hostedKey=userAccountServices.generateMerchantHostedEncryptionKey(user.getPayId());
						logger.info("salt of merchant.."+keySalt+"KeySalt of merchant.."+hostedKey);


						if (StringUtils.isNotBlank(keySalt) && StringUtils.isNotBlank(hostedKey)) {
							logger.info("inside the if checking both keySalts :");
							weStoreApiDTO.setSalt(keySalt);
							weStoreApiDTO.setMerchant_hosted_key(hostedKey);
							logger.info("Before calling the GENERATE UUID METHD ");
							String uuid = webStoreApiService.generateUUID(weStoreApiDTO);
							logger.info("After getting response from api:" + uuid);
							user.setUuId(uuid);
							logger.info("After getting response from api:user.getUUID()" + user.getUuId());
							userDetails.setUuId(uuid);
							logger.info("After getting response from api:userDetails.getUUID()" + userDetails.getUuId());

							logger.info("inside the else check uuid :");
							weStoreApiDTO.setEmail(userDetails.getEmailId());
							weStoreApiDTO.setName(userDetails.getBusinessName());

							logger.info("user password ::::" + userDetails.getPassword());
							logger.info("user name :::" + userDetails.getBusinessName());
							logger.info("user uuid :::" + userDetails.getUuId());
							weStoreApiDTO.setPassword(userDetails.getPassword());
							weStoreApiDTO.setPassword_confirmation(userDetails.getPassword());
							weStoreApiDTO.setUuid(userDetails.getUuId());
							String token = webStoreApiService.generateToken(weStoreApiDTO);
							logger.info("token api response :::::" + token);
							user.setWebStoreApiToken(token);
							logger.info("token set to user object :::::" + token);
						} else {
							logger.info("KEY SALT & SALT IS BLANK");
						}
					}
				} else {
					logger.info("WEBSTORE CHAECK IS NOT ENABLE:::");
				}

				Map<String, User> tempMap = new HashMap<String, User>();
				tempMap = merchantRecordUpdater.updateUserPendingDetails(user, sessionUser, accountList,
						accountCurrencyList);
				logger.info("Tree Map : " + tempMap);
				for (Map.Entry<String, User> entry : tempMap.entrySet()) {
					setUser(entry.getValue());
					responseObject.setResponseCode("101");
					responseObject.setResponseMessage(entry.getKey());
					addActionMessage(responseObject.getResponseMessage());
				}
				setUser(accPwdScrambler.retrieveAndDecryptPass(user));
				currencyMap = currencyMapProvider.currencyMap(user);
				// retrieve list for onBoard Docs.
				user.setOnBoardDocListString(DataEncoder.encodeUserOnBoardDocList(user.getOnBoardDocList()));

				user.setAccounts(new HashSet<>());
				user.setRoles(new HashSet<>());
				Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
						.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
				AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(user), prevMerchant,
						actionMessagesByAction.get("merchantSaveAction"));
				auditTrailService.saveAudit(request, auditTrail);
				
				if (!prevUser.isActiveEmailNotificationFlag() && user.getUserStatus().toString().equals(UserStatusType.ACTIVE.getStatus())) {
					logger.info("Email send before : " + prevUser.getPayId() + "=" + prevUser.getBusinessName() + "=" + prevUser.getUserType() + "=" + prevUser.getUserStatus());
					responseObject.setAccountValidationID(prevUser.getAccountValidationKey());
					responseObject.setEmail(prevUser.getEmailId());
					responseObject.setResponseCode(ErrorType.SUCCESS.getResponseCode());

					emailControllerServiceProvider.addUser(responseObject, prevUser.getBusinessName());

					prevUser.setActiveEmailNotificationFlag(true);
					user.setActiveEmailNotificationFlag(prevUser.isActiveEmailNotificationFlag());

					// added vijay

					logger.info("Previous Detail CheckactiveEmailNotificationFlag=======: " + prevUser.getUserStatus()
							+ "-" + prevUser.getBusinessName() + "-" + prevUser.getPayId() + "-"
							+ prevUser.getEmailId());
					logger.info("CheckactiveEmailNotificationFlag====== : " + user.getEmailId() + "-" + user.getPayId());

					userDao.CheckactiveEmailNotificationFlag(prevUser.getEmailId(), prevUser.getPayId());
				}

				return CrmFieldConstants.ADMIN.getValue();
			} else {
				// Never save plain string into database.
				// Called on merchant sign up.
				user.setOnBoardDocListString(null);
				setUser(merchantRecordUpdater.updateUserProfile(user));
				// retrieve list for onBoard Docs.
				user.setOnBoardDocListString(DataEncoder.encodeUserOnBoardDocList(user.getOnBoardDocList()));
				user.setAccounts(new HashSet<>());
				user.setRoles(new HashSet<>());
				Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
						.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
				AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(user), prevMerchant,
						actionMessagesByAction.get("merchantSaveAction"));
				auditTrailService.saveAudit(request, auditTrail);

				sessionMap.put(Constants.USER.getValue(), user);
				return CrmFieldConstants.SIGNUP_PROFILE.getValue();
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}


	public String newMerchantSaveAction() {
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			setSalt(SaltFactory.getSaltProperty(user));
			Map<String, String> industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();
			industryTypesList.putAll(industryCategoryLinkedMap);
			user.setUserType(sessionUser.getUserType());
			user.setParentPayId(sessionUser.getParentPayId());
			user.setDefaultCurrency(sessionUser.getDefaultCurrency());
			user.setEmailId(sessionUser.getEmailId());
			user.setPayId(sessionUser.getPayId());
			currencyMap = currencyMapProvider.currencyMap(user);
			addPaymentMapped(user, currencyMap);
			setIndustryTypesList(BusinessType.getIndustryCategoryList());
			if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
				// Never save plain string into database.
				// Called on merchant sign up.
				user.setOnBoardDocListString(null);
				setUser(merchantRecordUpdater.updateUserProfile(user));
				// retrieve list for onBoard Docs.
				user.setOnBoardDocListString(DataEncoder.encodeUserOnBoardDocList(user.getOnBoardDocList()));
				sessionMap.put(Constants.USER.getValue(), user);
				return CrmFieldConstants.SIGNUP_PROFILE.getValue();
			} else {
				logger.info("User type is not merchant : " + sessionUser.getUserType());
				return ERROR;
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}

	public User addPaymentMapped(User user, Map<String, String> currencyMap) {

		try {

			Set<Account> accountSet = new HashSet<Account>();
			Set<Account> accountSetUpdated = new HashSet<Account>();
			accountSet = user.getAccounts();

			for (Account account : accountSet) {

				Set<Payment> paymentSet = account.getPayments();
				AccountCurrency accountCurrency = account.getAccountCurrency(CrmFieldConstants.INR.getValue());
				Set<AccountCurrency> accountCurrencySetUpdated = new HashSet<AccountCurrency>();

				StringBuilder paymentTypeString = new StringBuilder();

				for (Payment payment : paymentSet) {
					paymentTypeString.append(payment.getPaymentType().getName());
					paymentTypeString.append(" | ");
				}
				accountCurrency.setMappedPaymentTypes(paymentTypeString.toString());
				accountCurrency.setCurrencyName(CrmFieldConstants.INR.toString());
				accountCurrencySetUpdated.add(accountCurrency);
				account.setAccountCurrencySet(accountCurrencySetUpdated);
				accountSetUpdated.add(account);

			}

			user.setAccounts(accountSetUpdated);
			return user;
		}

		catch (Exception e) {
			logger.error("Exception " + e);
		}
		return user;

	}

	public String resetTFAuthentication(){
		logger.info("Email Id: "+user.getEmailId());
		if(user.getEmailId().trim().isEmpty())
			return ERROR;

		userDao.resetTwoFactorAuth(user.getEmailId(),user.getPayId());
		user.setTfaFlag(true);
		user.setGoogle2FASecretkey("");
		setGoogle2FAKey(true);
		setTfaFlag(true);
		return SUCCESS;
	}

	// to provide default country
	public String getDefaultCountry() {
		if (StringUtils.isBlank(user.getCountry())) {
			return BinCountryMapperType.INDIA.getName();
		} else {
			return user.getCountry();
		}
	}

	// to provide default State value
	public String getDefaultState() {
		if (StringUtils.isBlank(user.getState())) {
			return States.SELECT_STATE.getName();
		} else {
			return States.getStatesNames().contains(user.getState().toString()) ? user.getState().toString()
					: States.SELECT_STATE.getName();
		}
	}

	public String getDefaultRegistrationDate() {
		if (!StringUtils.isBlank(user.getRegistrationDate().toString())) {
			return user.getRegistrationDate().toString();
		}
		return null;
	}

	// to provide default Operation State value
	public String getDefaultOperationState() {
		if (StringUtils.isBlank(user.getOperationState())) {
			return States.SELECT_STATE.getName();
		} else {
			return States.getStatesNames().contains(user.getOperationState().toString())
					? user.getOperationState().toString()
					: States.SELECT_STATE.getName();
		}
	}

	@Override
	public void validate() {
		boolean hdfcAcquirerFlag = false;
		boolean yesBankAcquirerFlag = false;
		boolean direcpayAcquirerFlag = false;
		boolean modeTypeFlag = false;
		boolean yesBankNetbnkingPrimaryFlag = false;
		boolean direcpayNetbankingPrimaryFlag = false;
		boolean direcpayExistFlag = false;
		boolean yesBankExistFlag = false;
		boolean hdfcBankExistFlag = false;
		boolean americanExpressAcquirerFlag = false;
		boolean americanExpressExistFlag = false;
		Map<String, String> industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();
		industryTypesList.putAll(industryCategoryLinkedMap);
		Set<String> errorSection = new HashSet<>();

		if ((crmValidator.validateBlankField(user.getFirstName()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.FIRSTNAME, user.getFirstName()))) {
			logger.info("FIRSTNAME Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.FIRSTNAME.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getLastName()))) {

		} else if (!(crmValidator.validateField(CrmFieldType.LASTNAME, user.getLastName()))) {
			logger.info("LASTNAME Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.LASTNAME.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getCompanyName()))) {

		} else if (!(crmValidator.validateField(CrmFieldType.COMPANY_NAME, user.getCompanyName()))) {
			logger.info("COMPANY_NAME Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.COMPANY_NAME.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if (crmValidator.validateBlankField(user.getIndustryCategory())) {
		} else if (!(crmValidator.validateField(CrmFieldType.INDUSTRY_CATEGORY, user.getIndustryCategory()))) {
			logger.info("INDUSTRY_CATEGORY Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.INDUSTRY_CATEGORY.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		if (crmValidator.validateBlankField(user.getIndustrySubCategory())) {
		} else if (!(crmValidator.validateField(CrmFieldType.INDUSTRY_SUB_CATEGORY, user.getIndustrySubCategory()))) {
			logger.info("INDUSTRY_SUB_CATEGORY Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.INDUSTRY_SUB_CATEGORY.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getAddress()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.ADDRESS, user.getAddress()))) {
			logger.info("ADDRESS Validation Failed");
			errorSection.add("Contact Details");
			addFieldError(CrmFieldType.ADDRESS.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getCity()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.CITY, user.getCity()))) {
			logger.info("CITY Validation Failed");
			errorSection.add("Contact Details");
			addFieldError(CrmFieldType.CITY.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getState()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.STATE, user.getState()))) {
			logger.info("STATE Validation Failed");
			errorSection.add("Contact Details");
			addFieldError(CrmFieldType.STATE.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getCountry()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.COUNTRY, user.getCountry()))) {
			logger.info("COUNTRY Validation Failed");
			errorSection.add("Contact Details");
			addFieldError(CrmFieldType.COUNTRY.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getPostalCode()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.POSTALCODE, user.getPostalCode()))) {
			logger.info("POSTALCODE Validation Failed");
			errorSection.add("Contact Details");
			addFieldError(CrmFieldType.POSTALCODE.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getBankName()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.BANK_NAME, user.getBankName()))) {
			logger.info("BANK_NAME Validation Failed");
			errorSection.add("Fiat Details");
			addFieldError(CrmFieldType.BANK_NAME.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getIfscCode()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.IFSC_CODE, user.getIfscCode()))) {
			logger.info("IFSC_CODE Validation Failed");
			errorSection.add("Fiat Details");
			addFieldError(CrmFieldType.IFSC_CODE.getName(), ErrorType.IFSC_CODE.getInternalMessage());
		}
		if ((crmValidator.validateBlankField(user.getAccHolderName()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.ACC_HOLDER_NAME, user.getAccHolderName()))) {
			logger.info("ACC_HOLDER_NAME Validation Failed");
			errorSection.add("Fiat Details");
			addFieldError(CrmFieldType.ACC_HOLDER_NAME.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getCurrency()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.CURRENCY, user.getCurrency()))) {
			logger.info("CURRENCY Validation Failed");
			errorSection.add("Fiat Details");
			addFieldError(CrmFieldType.CURRENCY.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getBranchName()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.BRANCH_NAME, user.getBranchName()))) {
			logger.info("BRANCH_NAME Validation Failed");
			errorSection.add("BRANCH_NAME");
			addFieldError(CrmFieldType.BRANCH_NAME.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getBusinessName()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.BUSINESS_NAME, user.getBusinessName()))) {
			logger.info("BUSINESS_NAME Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getComments()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.COMMENTS, user.getComments()))) {
			logger.info("COMMENTS Validation Failed");
			errorSection.add("COMMENTS");
			addFieldError(CrmFieldType.COMMENTS.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getPanCard()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.PANCARD, user.getPanCard()))) {
			logger.info("PANCARD Validation Failed");
			errorSection.add("PANCARD");
			addFieldError(CrmFieldType.PANCARD.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getAccountNo()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.ACCOUNT_NO, user.getAccountNo()))) {
			logger.info("ACCOUNT_NO Validation Failed");
			errorSection.add("Fiat Details");
			addFieldError(CrmFieldType.ACCOUNT_NO.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getWebsite()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.WEBSITE, user.getWebsite()))) {
			logger.info("WEBSITE Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.WEBSITE.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getOrganisationType()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.ORGANIZATIONTYPE, user.getOrganisationType()))) {
			logger.info("ORGANIZATIONTYPE Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.ORGANIZATIONTYPE.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getMultiCurrency()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.MULTICURRENCY, user.getMultiCurrency()))) {
			logger.info("MULTICURRENCY Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.MULTICURRENCY.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getBusinessModel()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.BUSINESSMODEL, user.getBusinessModel()))) {
			logger.info("BUSINESSMODEL Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.BUSINESSMODEL.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getOperationAddress()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.OPERATIONADDRESS, user.getOperationAddress()))) {
			logger.info("OPERATIONADDRESS Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.OPERATIONADDRESS.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getOperationCity()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.CITY, user.getOperationCity()))) {
			logger.info("CITY Validation Failed");
			errorSection.add("Contact Details");
			addFieldError(CrmFieldType.CITY.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getOperationState()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.STATE, user.getOperationState()))) {
			logger.info("STATE Validation Failed");
			errorSection.add("Contact Details");
			addFieldError(CrmFieldType.STATE.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getOperationPostalCode()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.OPERATION_POSTAL_CODE, user.getOperationPostalCode()))) {
			logger.info("OPERATION_POSTAL_CODE Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.OPERATION_POSTAL_CODE.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getCin()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.CIN, user.getCin()))) {
			logger.info("CIN Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.CIN.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getPan()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.PAN, user.getPan()))) {
			logger.info("PAN Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.PAN.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getPanName()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.PANNAME, user.getPanName()))) {
			logger.info("PANNAME Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.PANNAME.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getNoOfTransactions()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.NO_OF_TRANSACTIONS, user.getNoOfTransactions()))) {
			logger.info("NO_OF_TRANSACTIONS Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.NO_OF_TRANSACTIONS.getName(), crmValidator.getResonseObject().getResponseMessage());
		}

		if ((crmValidator.validateBlankField(user.getAmountOfTransactions()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.AMOUNT_OF_TRANSACTIONS, user.getAmountOfTransactions()))) {
			logger.info("AMOUNT_OF_TRANSACTIONS Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.AMOUNT_OF_TRANSACTIONS.getName(), crmValidator.getResonseObject().getResponseMessage());
		} else {
			BigDecimal txnAmount = new BigDecimal(user.getAmountOfTransactions());
			BigDecimal lowerLimit = new BigDecimal(0);
			BigDecimal upperLimit = new BigDecimal("10000000000");
			logger.info("txn amount : " + txnAmount);
			if ((txnAmount.compareTo(lowerLimit) == 1) && (txnAmount.compareTo(upperLimit) == -1)) {
				logger.info("In range");
			} else {
				logger.error("not in range");
				logger.info("AMOUNT_OF_TRANSACTIONS Validation Failed");
				errorSection.add("Business Details");
				addFieldError(CrmFieldType.AMOUNT_OF_TRANSACTIONS.getName(), "Invalid Expected Amount of Transaction");
			}
		}

		if ((crmValidator.validateBlankField(user.getDateOfEstablishment()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.DATE_OF_ESTABLISHMENT, user.getDateOfEstablishment()))) {
			logger.info("DATE_OF_ESTABLISHMENT Validation Failed");
			errorSection.add("Business Details");
			addFieldError(CrmFieldType.DATE_OF_ESTABLISHMENT.getName(), crmValidator.getResonseObject().getResponseMessage());
		}

		if ((crmValidator.validateBlankField(user.getAccountValidationKey()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.ACCOUNT_VALIDATION_KEY, user.getAccountValidationKey()))) {
			logger.info("ACCOUNT_VALIDATION_KEY Validation Failed");
			errorSection.add("ACCOUNT_VALIDATION_KEY");
			addFieldError(CrmFieldType.ACCOUNT_VALIDATION_KEY.getName(), crmValidator.getResonseObject().getResponseMessage());
		}

		if ((crmValidator.validateBlankField(user.getUploadePhoto()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.UPLOADE_PHOTO, user.getUploadePhoto()))) {
			logger.info("UPLOADE_PHOTO Validation Failed");
			errorSection.add("UPLOADE_PHOTO");
			addFieldError(CrmFieldType.UPLOADE_PHOTO.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getUploadedPanCard()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.UPLOADE_PAN_CARD, user.getUploadedPanCard()))) {
			logger.info("UPLOADE_PAN_CARD Validation Failed");
			errorSection.add("UPLOADE_PAN_CARD");
			addFieldError(CrmFieldType.UPLOADE_PAN_CARD.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getUploadedPhotoIdProof()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.UPLOADE_PHOTOID_PROOF, user.getUploadedPhotoIdProof()))) {
			logger.info("UPLOADE_PHOTOID_PROOF Validation Failed");
			errorSection.add("UPLOADE_PHOTOID_PROOF");
			addFieldError(CrmFieldType.UPLOADE_PHOTOID_PROOF.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getUploadedContractDocument()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.UPLOADE_CONTRACT_DOCUMENT,
				user.getUploadedContractDocument()))) {
			logger.info("UPLOADE_CONTRACT_DOCUMENT Validation Failed");
			errorSection.add("UPLOADE_CONTRACT_DOCUMENT");
			addFieldError(CrmFieldType.UPLOADE_CONTRACT_DOCUMENT.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if (crmValidator.validateBlankField(user.getTransactionEmailId())) {
		} else if (!(crmValidator.isValidBatchEmailId(user.getTransactionEmailId())
				|| (crmValidator.isValidEmailId(user.getTransactionEmailId())))) {
			errorSection.add("Notification Settings");
			addFieldError(CrmFieldType.TRANSACTION_EMAIL_ID.getName(), crmValidator.getResonseObject().getResponseMessage());
		}

		if (crmValidator.validateBlankField(user.getEmailId())) {
			 logger.info("EMAILID Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.EMAILID.getName(), crmValidator.getResonseObject().getResponseMessage());
		} else if (!(crmValidator.isValidEmailId(user.getEmailId()))) {
		 	logger.info("EMAILID Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.EMAILID.getName(), crmValidator.getResonseObject().getResponseMessage());
		}

		if ((crmValidator.validateBlankField(user.getContactPerson()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.CONTACT_PERSON, user.getContactPerson()))) {
			logger.info("CONTACT_PERSON Validation Failed");
			errorSection.add("CONTACT_PERSON");
			addFieldError(CrmFieldType.CONTACT_PERSON.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getPayId()))) {
			logger.info("PAY_ID Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.PAY_ID.getName(), crmValidator.getResonseObject().getResponseMessage());
		} else if (!(crmValidator.validateField(CrmFieldType.PAY_ID, user.getPayId()))) {
			logger.info("PAY_ID Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.PAY_ID.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getPassword()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.PASSWORD, user.getPassword()))) {
			logger.info("PASSWORD Validation Failed");
			errorSection.add("PASSWORD");
			addFieldError(CrmFieldType.PASSWORD.getName(), crmValidator.getResonseObject().getResponseMessage());
		}

		if ((crmValidator.validateBlankField(user.getParentPayId()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.PARENT_PAY_ID, user.getParentPayId()))) {
			logger.info("PARENT_PAY_ID Validation Failed");
			errorSection.add("PARENT_PAY_ID");
			addFieldError(CrmFieldType.PARENT_PAY_ID.getName(), crmValidator.getResonseObject().getResponseMessage());
		}

		if ((crmValidator.validateBlankField(user.getWhiteListIpAddress()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.WHITE_LIST_IPADDRES, user.getWhiteListIpAddress()))) {
			logger.info("WHITE_LIST_IPADDRES Validation Failed");
			errorSection.add("WHITE_LIST_IPADDRES");
			addFieldError(CrmFieldType.WHITE_LIST_IPADDRES.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getFax()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.FAX, user.getFax()))) {
			logger.info("FAX Validation Failed");
			errorSection.add("FAX");
			addFieldError(CrmFieldType.FAX.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getMobile()))) {
		} 
//		else if (!(crmValidator.validateField(CrmFieldType.MOBILE, user.getMobile()))) {
//			logger.info("PAY_ID Validation Failed");
//			addFieldError(CrmFieldType.MOBILE.getName(), crmValidator.getResonseObject().getResponseMessage());
//		}
		if ((crmValidator.validateBlankField(user.getProductDetail()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.PRODUCT_DETAIL, user.getProductDetail()))) {
			logger.info("PRODUCT_DETAIL Validation Failed");
			errorSection.add("PRODUCT_DETAIL");
			addFieldError(CrmFieldType.PRODUCT_DETAIL.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getResellerId()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.RESELLER_ID, user.getResellerId()))) {
			setSalt(SaltFactory.getSaltProperty(user));
			setIndustryTypesList(BusinessType.getIndustryCategoryList());
			logger.info("RESELLER_ID Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.RESELLER_ID.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getMerchantType()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.MERCHANT_TYPE, user.getMerchantType()))) {
			logger.info("MERCHANT_TYPE Validation Failed");
			errorSection.add("MERCHANT_TYPE");
			addFieldError(CrmFieldType.MERCHANT_TYPE.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getBranchName()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.BRANCH_NAME, user.getBranchName()))) {
			logger.info("BRANCH_NAME Validation Failed");
			errorSection.add("BRANCH_NAME");
			addFieldError(CrmFieldType.BRANCH_NAME.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getDefaultCurrency()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.DEFAULT_CURRENCY, user.getDefaultCurrency()))) {
			logger.info("DEFAULT_CURRENCY Validation Failed");
			errorSection.add("DEFAULT_CURRENCY");
			addFieldError(CrmFieldType.DEFAULT_CURRENCY.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getmCC()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.MCC, user.getmCC()))) {
			logger.info("MCC Validation Failed");
			errorSection.add("Merchant category code");
			addFieldError(CrmFieldType.MCC.getName(), crmValidator.getResonseObject().getResponseMessage());
		}
		if ((crmValidator.validateBlankField(user.getAmexSellerId()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.AMEX_SELLER_ID, user.getAmexSellerId()))) {
			logger.info("AMEX_SELLER_ID Validation Failed");
			errorSection.add("Merchant Details");
			addFieldError(CrmFieldType.AMEX_SELLER_ID.getName(), crmValidator.getResonseObject().getResponseMessage());
		}

		// Validate whitelabel URL

		if (user.isEnableWhiteLabelUrl() && (crmValidator.validateBlankField(user.getWhiteLabelUrl()))) {
			logger.info("WHITE_LABEL_URL Validation Failed");
			errorSection.add("Transaction Settings");
			addFieldError(CrmFieldType.WHITE_LABEL_URL.getName(), crmValidator.getResonseObject().getResponseMessage());
		}

		if ((crmValidator.validateBlankField(user.getWhiteLabelUrl()))) {
		} else if (!(crmValidator.validateField(CrmFieldType.WHITE_LABEL_URL, user.getWhiteLabelUrl()))) {
			logger.info("WHITE_LABEL_URL Validation Failed");
			errorSection.add("Transaction Settings");
			addFieldError(CrmFieldType.WHITE_LABEL_URL.getName(), crmValidator.getResonseObject().getResponseMessage());
		}

//		if (StringUtils.isBlank(userRoleType)) {
//			userRoleType = (String) sessionMap.get(Constants.ROLE_TYPE.getValue());
//		}
//		if (StringUtils.equals(userRoleType, RoleType.CHECKER.name())
//				&& (user.getUserStatus() == UserStatusType.OPERATION_APPROVED
//						|| user.getUserStatus() == UserStatusType.RISK_APPROVED)) {
//			addFieldError(CrmFieldType.USERSTATUS.getName(), ErrorType.PERMISSION_DENIED.getResponseMessage());
//		} else if (StringUtils.equals(userRoleType, RoleType.OPERATION.name())
//				&& user.getUserStatus() == UserStatusType.RISK_APPROVED) {
//			addFieldError(CrmFieldType.USERSTATUS.getName(), ErrorType.PERMISSION_DENIED.getResponseMessage());
//		}

//		if ((crmValidator.validateBlankField(user.getSettlementNamingConvention()))) {
//			
//		} else if (!(crmValidator.validateField(CrmFieldType.SETTLEMENT_NAMING_CONVENTION, user.getSettlementNamingConvention()))) {
//			addFieldError(CrmFieldType.SETTLEMENT_NAMING_CONVENTION.getName(), crmValidator.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getRefundValidationNamingConvention()))) {
//			addFieldError(CrmFieldType.REFUNDVALIDATION_NAMING_CONVENTION.getName(), crmValidator.getResonseObject().getResponseMessage());
//		} else if (!(crmValidator.validateField(CrmFieldType.REFUNDVALIDATION_NAMING_CONVENTION, user.getRefundValidationNamingConvention()))) {
//			addFieldError(CrmFieldType.REFUNDVALIDATION_NAMING_CONVENTION.getName(), crmValidator.getResonseObject().getResponseMessage());
//		}

		/*
		 * for (Account accountFE : accountList) { if
		 * ((crmValidator.validateBlankField(accountFE.getMerchantId()))) { } else if
		 * (!(crmValidator.validateField(CrmFieldType.MERCHANTID,
		 * accountFE.getMerchantId()))) {
		 * addFieldError(CrmFieldType.MERCHANTID.getName(),
		 * crmValidator.getResonseObject().getResponseMessage()); }
		 * 
		 * if ((crmValidator.validateBlankField(accountFE.getAcquirerPayId()))) { } else
		 * if (!(crmValidator.validateField(CrmFieldType.ACQUIRER_PAYID,
		 * accountFE.getAcquirerPayId()))) {
		 * addFieldError(CrmFieldType.ACQUIRER_PAYID.getName(),
		 * crmValidator.getResonseObject().getResponseMessage()); } if
		 * ((crmValidator.validateBlankField(accountFE.getAcquirerName()))) { } else if
		 * (!(crmValidator.validateField(CrmFieldType.AQCQUIRER_NAME,
		 * accountFE.getAcquirerName()))) {
		 * addFieldError(CrmFieldType.AQCQUIRER_NAME.getName(),
		 * crmValidator.getResonseObject().getResponseMessage()); } if
		 * ((crmValidator.validateBlankField(accountFE.getPassword()))) { } else if
		 * (!(crmValidator.validateField(CrmFieldType.ACCOUNT_PASSWORD,
		 * accountFE.getPassword()))) {
		 * addFieldError(CrmFieldType.ACCOUNT_PASSWORD.getName(),
		 * crmValidator.getResonseObject().getResponseMessage()); } if
		 * ((crmValidator.validateBlankField(accountFE.getTxnKey()))) { } else if
		 * (!(crmValidator.validateField(CrmFieldType.TXN_KEY, accountFE.getTxnKey())))
		 * { addFieldError(CrmFieldType.TXN_KEY.getName(),
		 * crmValidator.getResonseObject().getResponseMessage()); } }
		 */
		/*
		 * for (AccountCurrency accountCurrency : accountCurrencyList) { if
		 * ((crmValidator.validateBlankField(accountCurrency.getAcqPayId()))) { } else
		 * if (!(crmValidator.validateField(CrmFieldType.ACQ_PAYID,
		 * accountCurrency.getAcqPayId()))) {
		 * addFieldError(CrmFieldType.ACQ_PAYID.getName(),
		 * crmValidator.getResonseObject().getResponseMessage()); } if
		 * ((crmValidator.validateBlankField(accountCurrency.getCurrencyCode()))) { }
		 * else if (!(crmValidator.validateField(CrmFieldType.CURRENCY,
		 * accountCurrency.getCurrencyCode()))) {
		 * addFieldError(CrmFieldType.CURRENCY.getName(),
		 * crmValidator.getResonseObject().getResponseMessage()); } if
		 * ((crmValidator.validateBlankField(accountCurrency.getMerchantId()))) { } else
		 * if (!(crmValidator.validateField(CrmFieldType.MERCHANTID,
		 * accountCurrency.getMerchantId()))) {
		 * addFieldError(CrmFieldType.MERCHANTID.getName(),
		 * crmValidator.getResonseObject().getResponseMessage()); } if
		 * ((crmValidator.validateBlankField(accountCurrency.getPassword()))) { } else
		 * if (!(crmValidator.validateField(CrmFieldType.ACCOUNT_PASSWORD,
		 * accountCurrency.getPassword()))) {
		 * addFieldError(CrmFieldType.ACCOUNT_PASSWORD.getName(),
		 * crmValidator.getResonseObject().getResponseMessage()); } if
		 * ((crmValidator.validateBlankField(accountCurrency.getTxnKey()))) { } else if
		 * (!(crmValidator.validateField(CrmFieldType.TXN_KEY,
		 * accountCurrency.getTxnKey()))) {
		 * addFieldError(CrmFieldType.TXN_KEY.getName(),
		 * crmValidator.getResonseObject().getResponseMessage()); } Map<String,String>
		 * industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();
		 * industryTypesList.putAll(industryCategoryLinkedMap); }
		 */

		// Validation between check boxes
		/*
		 * for (Account accountFE : accountList) { if
		 * (accountFE.getAcquirerName().equalsIgnoreCase(AcquirerType.CITRUS_PAY.getName
		 * ())) { yesBankExistFlag = true; if (accountFE.isPrimaryStatus() == true) {
		 * yesBankAcquirerFlag = true; } if (accountFE.isPrimaryNetbankingStatus() ==
		 * true) { yesBankNetbnkingPrimaryFlag = true; }
		 * 
		 * if (yesBankAcquirerFlag &&
		 * user.getModeType().getName().equalsIgnoreCase(ModeType.AUTH_CAPTURE.getName()
		 * )) { modeTypeFlag = true; } } if
		 * (accountFE.getAcquirerName().equalsIgnoreCase(AcquirerType.FSS.getName())) {
		 * hdfcBankExistFlag = true; if (accountFE.isPrimaryStatus() == true) {
		 * hdfcAcquirerFlag = true; } } if
		 * (accountFE.getAcquirerName().equalsIgnoreCase(AcquirerType.AMEX.getName())) {
		 * americanExpressExistFlag = true; if (accountFE.isPrimaryStatus() == true) {
		 * americanExpressAcquirerFlag = true; } } if
		 * (accountFE.getAcquirerName().equalsIgnoreCase(AcquirerType.DIREC_PAY.getName(
		 * ))) { direcpayExistFlag = true; if (accountFE.isPrimaryStatus() == true) {
		 * direcpayAcquirerFlag = true; } if (accountFE.isPrimaryNetbankingStatus() ==
		 * true) { direcpayNetbankingPrimaryFlag = true; } } }
		 */
		/*
		 * if ((yesBankExistFlag == true || hdfcBankExistFlag == true ||
		 * americanExpressExistFlag == true) && (!yesBankAcquirerFlag &&
		 * !hdfcAcquirerFlag && !americanExpressAcquirerFlag)) {
		 * addFieldError(CrmFieldType.PAY_ID.getName(),
		 * CrmFieldConstants.SELECT_PRIMARY_CARD.getValue());
		 * addActionMessage(CrmFieldConstants.SELECT_PRIMARY_CARD.getValue()); }
		 */
		/*
		 * if ((direcpayExistFlag == true || yesBankExistFlag == true) &&
		 * (!direcpayNetbankingPrimaryFlag && !yesBankNetbnkingPrimaryFlag)) {
		 * addFieldError(CrmFieldType.PAY_ID.getName(),
		 * CrmFieldConstants.SELECT_ONE_NETBANKING.getValue());
		 * addActionMessage(CrmFieldConstants.SELECT_ONE_NETBANKING.getValue()); }
		 */
		if (modeTypeFlag) {
			addFieldError(CrmFieldType.PAY_ID.getName(), CrmFieldConstants.SELECT_SALE.getValue());
			addActionMessage(CrmFieldConstants.SELECT_SALE.getValue());
		}
		if (yesBankNetbnkingPrimaryFlag && direcpayNetbankingPrimaryFlag
				|| direcpayAcquirerFlag && yesBankAcquirerFlag) {
			addFieldError(CrmFieldType.PAY_ID.getName(), CrmFieldConstants.SELECT_DIRECPAY_YES.getValue());
			addActionMessage(CrmFieldConstants.SELECT_DIRECPAY_YES.getValue());
		}
		/*
		 * if (hdfcAcquirerFlag && yesBankAcquirerFlag) {
		 * addFieldError(CrmFieldType.PAY_ID.getName(),
		 * CrmFieldConstants.SELECT_ONLY_ONE.getValue());
		 * addActionMessage(CrmFieldConstants.SELECT_ONLY_ONE.getValue()); }
		 */

		// TODO validation field password encry
		if (!getFieldErrors().isEmpty()) {
			/*
			 * accountList = updateAccount(accountList, accountCurrencyList); Set<Account>
			 * set = new HashSet<Account>(accountList); user.setAccounts(set);
			 */
			User userFromDb = userDao.getUserClass(user.getPayId());
			float extraRefundLimitDB = userFromDb.getExtraRefundLimit();
			user.setExtraRefundLimit(extraRefundLimitDB);
		}
		/**
		 * when validation error occurs, retrieve select list
		 * - userGroups
		 * - roleList
 		 */
		if(hasFieldErrors()){
			responseObject.setResponseCode(ErrorType.INVALID_INPUT.getResponseCode());
			setActionMessages(Collections.singleton("Validation error, please check fields: " + Collections.singleton(String.join(", ", errorSection))));

			List<UserGroup> groups = userGroupDao.getUserGroups();
			groups = groups.stream()
					.filter(group -> StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant"))
					.collect(Collectors.toList());
			setUserGroups(groups);
			if (user.getUserGroup() != null) {
				setUserGroupId(user.getUserGroup().getId());
				setRoleList(roleDao.getByGroupId(user.getUserGroup().getId()));
			} else {
				long merchantGroupId = groups.stream()
						.filter(group -> StringUtils.equalsIgnoreCase(group.getGroup(), "Merchant"))
						.collect(Collectors.toList()).get(0).getId();
				setRoleList(roleDao.getByGroupId(merchantGroupId));
			}
			if (user.getRole() != null) {
				setRoleId(user.getRole().getId());
			}
		}
	}

	private List<Account> updateAccount(List<Account> newAccounts, List<AccountCurrency> accountCurrencyList) {
		List<Account> accountList = new ArrayList<Account>();
		for (Account accountFE : newAccounts) {
			for (AccountCurrency accountCurrencyFE : accountCurrencyList) {
				if (accountCurrencyFE.getAcqPayId().equals(accountFE.getAcquirerPayId())) {
					Set<AccountCurrency> accountCurrencySet = new HashSet<AccountCurrency>();
					accountCurrencySet.add(accountCurrencyFE);
					accountFE.addAccountCurrency(accountCurrencyFE);
				}
			}
			accountList.add(accountFE);
		}
		return accountList;
	}

	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public User getModel() {
		return user;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public Map<String, List<AccountCurrency>> getAccountCurrencyMap() {
		return accountCurrencyMap;
	}

	public void setAccountCurrencyMap(Map<String, List<AccountCurrency>> accountCurrencyMap) {
		this.accountCurrencyMap = accountCurrencyMap;
	}

	public List<AccountCurrency> getAccountCurrencyList() {
		return accountCurrencyList;
	}

	public void setAccountCurrencyList(List<AccountCurrency> accountCurrencyList) {
		this.accountCurrencyList = accountCurrencyList;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public Map<String, String> getIndustryTypesList() {
		return industryTypesList;
	}

	public void setIndustryTypesList(Map<String, String> industryTypesList) {
		this.industryTypesList = industryTypesList;
	}

	public ResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(ResponseObject responseObject) {
		this.responseObject = responseObject;
	}

	public String getOnBoardDocList() {
		return onBoardDocList;
	}

	public void setOnBoardDocList(String onBoardDocList) {
		this.onBoardDocList = onBoardDocList;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	public long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	public String getPaytext() {
		return paytext;
	}

	public void setPaytext(String paytext) {
		this.paytext = paytext;
	}

	public String getInvoicetext() {
		return invoicetext;
	}

	public void setInvoicetext(String invoicetext) {
		this.invoicetext = invoicetext;
	}

	public boolean isTncStatus() {
		return tncStatus;
	}

	public void setTncStatus(boolean tncStatus) {
		this.tncStatus = tncStatus;
	}

	public String getSettlementDays1() {
		return settlementDays1;
	}

	public void setSettlementDays1(String settlementDays1) {
		this.settlementDays1 = settlementDays1;
	}

	public Boolean getGoogle2FAKey() {
		return google2FAKey;
	}

	public void setGoogle2FAKey(Boolean google2FAKey) {
		this.google2FAKey = google2FAKey;
	}

	public Boolean getTfaFlag() {
		return tfaFlag;
	}

	public void setTfaFlag(Boolean tfaFlag) {
		this.tfaFlag = tfaFlag;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}

	public String getrDate() {
		return rDate;
	}

	public void setrDate(String rDate) {
		this.rDate = rDate;
	}

	public String getaDate() {
		return aDate;
	}

	public void setaDate(String aDate) {
		this.aDate = aDate;
	}
}