package com.pay10.crm.action;


import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.dao.CurrencyCodeDao;
import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.entity.CurrencyCode;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.*;
import com.pay10.commons.util.*;
import com.pay10.crm.actionBeans.CurrencyMapProvider;
import com.pay10.crm.actionBeans.IndustryTypeCategoryProvider;
import com.pay10.pg.core.fraudPrevention.util.AccountPasswordScrambler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Neeraj, Puneet, Rajendra
 *
 */
public class MerchantAccountSetupAction extends AbstractSecureAction implements ModelDriven<User> {

	@Autowired
	private IndustryTypeCategoryProvider industryTypeCategoryProvider;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private CurrencyMapProvider currencyMapProvider;

	@Autowired
	private AccountPasswordScrambler accPwdScrambler;

	@Autowired
	private UserAccountServices userAccountServices;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private CycleDao cycledao;

	@Autowired
	MerchantKeySaltService merchantKeySaltService;

	private List<CycleMaster> listCycle = new ArrayList<CycleMaster>();
	private List<Merchants> listReseller = new ArrayList<Merchants>();

	private List<CurrencyCode> cryptoCurrencies;

	private static Logger logger = LoggerFactory.getLogger(MerchantAccountSetupAction.class.getName());
	private static final long serialVersionUID = -642857372066409390L;

	private User user = new User();
	private String salt;
	private Boolean isFlag;
	private Map<String, String> currencyMap = new LinkedHashMap<String, String>();
	private Map<String, String> currencyMapList = new LinkedHashMap<String, String>();

	private Map<String, String> industryTypesList = new TreeMap<String, String>();
	private String notificationApiEnableFlag;
	private String merchantHostedEncryptionKey;
	private long roleId;
	private Boolean google2FAKey;
	private String rDate;

	private String aDate;
	private List<Role> roleList;
	private long userGroupId;
	private List<UserGroup> userGroups;

	@Autowired
	private CurrencyCodeDao currencyCodeDao;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	private String registrationDate = "";

	private String activationDate = "";
	@Override
	public String execute() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			User userData = userDao.findPayId(user.getPayId());

			setUser(userData);
			if (user.getRegistrationDate() != null) {

				setRegistrationDate(sdf.format(user.getRegistrationDate()));
				logger.info("Registration Date 2:"+getRegistrationDate());
				setrDate(getRegistrationDate());
				logger.info("Rdate: "+getrDate());

			}
			if (user.getActivationDate() != null) {
				setActivationDate(sdf.format(user.getActivationDate()));
				logger.info("Activation Date 2:"+getActivationDate());
				setaDate(getActivationDate());
				logger.info("Adate: "+getaDate());
			}
			List<UserGroup> groups = userGroupDao.getUserGroups();
			groups = groups.stream()
					.filter(group -> StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant"))
					.collect(Collectors.toList());
			logger.info(groups.toString());
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
			user.setOnBoardDocListString(DataEncoder.encodeUserOnBoardDocList(user.getOnBoardDocList()));

			// setUser(accPwdScrambler.retrieveAndDecryptPass(user)); //Decrypt password to
			// display at front end
			// setSalt(SaltFactory.getSaltProperty(user));
			setSalt(merchantKeySaltService.getMerchantKeySalt(user.getPayId()).getSalt());
			setMerchantHostedEncryptionKey(userAccountServices.generateMerchantHostedEncryptionKey(user.getPayId()));
			// set currencies

			currencyMap = currencyMapProvider.currencyMap(user);
			addPaymentMapped(user, currencyMap);
			// set IndustryTypes
			Map<String, String> industryCategoryLinkedMap = industryTypeCategoryProvider.industryTypes(user);

			if (industryCategoryLinkedMap.containsKey("Default")) {
				industryCategoryLinkedMap.remove("Default");
			}

			industryTypesList.putAll(industryCategoryLinkedMap);
			if (currencyMap.isEmpty()) {
				addFieldError(CrmFieldType.DEFAULT_CURRENCY.getName(),
						ErrorType.UNMAPPED_CURRENCY_ERROR.getResponseMessage());
				addActionMessage("No currency mapped!!");
				return SUCCESS;
			}

			List<String> CurrencyList = multCurrencyCodeDao.getMerchantCurrency(user.getPayId());
			for (String curCode : CurrencyList) {

				currencyMapList.put(curCode, multCurrencyCodeDao.getCurrencyNamebyCode(curCode));
			}

			setListReseller(userDao.getResellerList());
			setListCycle(cycledao.findAll());
			if (user.isNotificationApiEnableFlag()) {
				setNotificationApiEnableFlag("true");
			} else {
				setNotificationApiEnableFlag("true");
			}
			setGoogle2FAKey(user.getGoogle2FASecretkey() == null || user.getGoogle2FASecretkey().trim().isEmpty());
			// setGoogle2FAKey(!user.getGoogle2FASecretkey().trim().isEmpty());
			logger.info("Google2FAKey " + getGoogle2FAKey());
			setCryptoCurrencies(currencyCodeDao.findByChannel("CRYPTO"));
			logger.info("Final R date "+getrDate());
			return SUCCESS;
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

	public List<CurrencyCode> getCryptoCurrencies() {
		return this.cryptoCurrencies;
	}

	public void setCryptoCurrencies(List<CurrencyCode> cryptoCurrencies) {
		this.cryptoCurrencies = cryptoCurrencies;
	}

	public Map<String, String> getIndustryTypesList() {
		return industryTypesList;
	}

	public void setIndustryTypesList(Map<String, String> industryTypesList) {
		this.industryTypesList = industryTypesList;
	}

	public void validator() {
		if ((validator.validateBlankField(getSalt()))) {
			/*
			 * addFieldError(CrmFieldType.SALT_KEY.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.SALT_KEY, getSalt()))) {
			addFieldError(CrmFieldType.SALT_KEY.getName(), validator.getResonseObject().getResponseMessage());
		}
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



	
	public String getDefaultRegistrationDate(){
		if(!StringUtils.isBlank(user.getRegistrationDate().toString())){
		//	DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//	logger.info(df.format(user.getRegistrationDate()));
			logger.info("Registration"+user.getRegistrationDate().toString());
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
		if (!validator.validateField(CrmFieldType.PAY_ID, user.getPayId())) {
			logger.info("PAY_ID Validation Failed");
			addFieldError(CrmFieldType.PAY_ID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}

	public String validatePrimary() {
		Set<Account> accounts = null;
		accounts = user.getAccounts();
		for (Account account : accounts) {
			if (account.isPrimaryStatus()) {
				setIsFlag(true);
				return SUCCESS;
			}
		}
		setIsFlag(false);
		return SUCCESS;
	}

	@Override
	public User getModel() {
		return user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Boolean getIsFlag() {
		return isFlag;
	}

	public void setIsFlag(Boolean isFlag) {
		this.isFlag = isFlag;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public String getNotificationApiEnableFlag() {
		return notificationApiEnableFlag;
	}

	public void setNotificationApiEnableFlag(String notificationApiEnableFlag) {
		this.notificationApiEnableFlag = notificationApiEnableFlag;
	}

	public String getMerchantHostedEncryptionKey() {
		return merchantHostedEncryptionKey;
	}

	public void setMerchantHostedEncryptionKey(String merchantHostedEncryptionKey) {
		this.merchantHostedEncryptionKey = merchantHostedEncryptionKey;
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

	public List<CycleMaster> getListCycle() {
		return listCycle;
	}

	public void setListCycle(List<CycleMaster> listCycle) {
		this.listCycle = listCycle;
	}

	public List<Merchants> getListReseller() {
		return listReseller;
	}

	public void setListReseller(List<Merchants> listReseller) {
		this.listReseller = listReseller;
	}

	public Map<String, String> getCurrencyMapList() {
		return currencyMapList;
	}

	public void setCurrencyMapList(Map<String, String> currencyMapList) {
		this.currencyMapList = currencyMapList;
	}

	public Boolean getGoogle2FAKey() {
		return google2FAKey;
	}

	public void setGoogle2FAKey(Boolean google2FAKey) {
		this.google2FAKey = google2FAKey;
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
