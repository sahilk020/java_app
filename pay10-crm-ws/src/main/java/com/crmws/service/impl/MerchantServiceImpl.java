package com.crmws.service.impl;

import com.crmws.controller.MerchantController;
import com.crmws.service.MerchantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.dao.*;
import com.pay10.commons.entity.ResetMerchantKey;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.*;
import com.pay10.commons.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MerchantServiceImpl implements MerchantService {

	private static final Logger logger = LoggerFactory.getLogger(MerchantServiceImpl.class.getName());

	@Autowired
	UserAccountServices userAccountServices;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TdrSettingDao tdrDao;

	@Autowired
	private UserMappingEditor userMappingEditor;

	@Autowired
	private MerchantAcquirerPropertiesDao merchantAcquirerPropertiesDao;

	@Autowired
	private PendingMappingRequestDao pendingMappingRequestDao;

	@Autowired
	MerchantKeySaltDao merchantKeySaltDao;

	@Autowired
	AccountDao accountDao;

	@Autowired
	private UserRecordsDao userRecordsDao;
	@Autowired
	private ResetMerchantKeyDao resetMerchantKeyDao;
	List<String> mapList = new ArrayList<>();

	private String accountCurrencySet;
	private String acquirer;
	private String currency;
	private String email;
	boolean international = false;
	boolean domestic = true;
	private String salt;

	@Autowired
	MerchantKeySaltService merchantKeySaltService;

	@Autowired
	RoleDao roleDao;

	@Autowired
	UserGroupDao userGroupDao;

	private static final int emailExpiredInTime = ConfigurationConstants.EMAIL_EXPIRED_HOUR.getValues();

	@Override
	public void createMerchant(Map<String, Object> getData) throws ParseException, SystemException {
		User user = new User();
		User getUser = null;
		UserRecords userRecords = new UserRecords();
		try {

			//SimpleDateFormat date = new SimpleDateFormat("yyyy-mm-dd");
			//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String salt = SaltFactory.generateRandomSalt();
			String keySalt = SaltFactory.generateRandomSalt();

			getUser = userDao.findByPayId((String) getData.get("payId"));

			if (getUser == null) {
				user.setEmailId((String) getData.get("emailId"));
				user.setAccountValidationKey(TransactionManager.getNewTransactionId() + Hasher.getHash( getData.get("payId") + salt));
				user.setMobile((String) getData.get("mobile"));
				logger.info("password...."+ getData.get("password"));
				user.setPassword(Hasher.getHash(((String) getData.get("password")).concat(salt)));
				logger.info("encrypted password...."+ user.getPassword());
			} else {
				user.setEmailId(getUser.getEmailId());
				user.setMobile(getUser.getMobile());
				if (getUser.getAccountValidationKey() == null) {
					user.setAccountValidationKey(TransactionManager.getNewTransactionId()+Hasher.getHash( getData.get("payId") + salt));

				}
				if (getUser.getAccountValidationKey() != null) {
					user.setAccountValidationKey(getUser.getAccountValidationKey()+Hasher.getHash( getData.get("payId") + salt));

				}
				if (getUser.getPassword() != null) {
					user.setPassword(getUser.getPassword());

				}
			}

			logger.info("getData...." + getData);
			logger.info("get User....={}", getUser);
			user.setPayId((String) getData.get("payId"));
			user.setBusinessName((String) getData.get("businessName"));
			user.setContactPerson((String) getData.get("contactPerson"));

			user.setState((String) getData.get("state"));
			user.setCity((String) getData.get("city"));
			user.setUserStatus(UserStatusType.valueOf(getData.get("status").toString().toUpperCase()));

			user.setWebsite((String) getData.get("website"));
			user.setAccountNo((String) getData.get("account_number"));
			user.setIfscCode((String) getData.get("ifsc_code"));
			user.setBankName((String) getData.get("bank_name"));
			user.setAccHolderName((String) getData.get("account_holder"));
			user.setUserType(UserType.valueOf("MERCHANT"));
			user.setAddress((String) getData.get("address"));
			user.setPan((String) getData.get("pan"));
			user.setPanCard((String) getData.get("pan"));
			user.setMerchantGstNo((String) getData.get("gst"));
			user.setResellerId((String) getData.get("reseller_id"));
			user.setPostalCode((String) getData.get("pincode"));
			user.setPanName((String) getData.get("panName"));
			user.setCin((String) getData.get("cin"));
			user.setFirstName((String) getData.get("contactPerson"));
			user.setCompanyName((String) getData.get("businessName"));
			user.setActivationDate(date);
			user.setRegistrationDate(date);
			user.setModeType(ModeType.SALE);
			user.setRole(roleDao.getRole(2));
			user.setUserGroup(userGroupDao.getUserGroup(1));
			boolean merchantKeySaltDetails = merchantKeySaltDao.checkuser((String) getData.get("payId"));
			logger.info("merchantKeySaltDetails exist or not...={}",merchantKeySaltDetails);

			if (!(merchantKeySaltDetails)) {
				ResetMerchantKey resetMerchantKey=new ResetMerchantKey();
				MerchantKeySalt merchantKeySalt = new MerchantKeySalt();
				merchantKeySalt.setPayId(user.getPayId());
				merchantKeySalt.setSalt(salt);
				merchantKeySalt.setKeySalt(keySalt);
				resetMerchantKey.setPayId(user.getPayId());
				resetMerchantKey.setKeySalt(keySalt);
				resetMerchantKey.setSalt(salt);
				resetMerchantKey.setUpdatedOn(date);
				resetMerchantKey.setStartDate(new Date());
				resetMerchantKey.setUpdatedBy(user.getPayId());
				resetMerchantKey.setStatus("Active");
				// merchantKeySalt.setEncryptionKey(merchantEncKey);
		
				// resetMerchantKey.setEncryptionKey(merchantEncKey);
				merchantKeySaltDao.addMerchantInfo(merchantKeySalt);
				resetMerchantKeyDao.saveOrUpdate(resetMerchantKey);
			}

			if (getUser == null) {

				userRecords.setCreateDate(date);
				userRecords.setEmailId(user.getEmailId());
				userRecords.setPassword(user.getPassword());
				userRecords.setPayId(user.getPayId());

			} else {
				userRecords.setCreateDate(date);
				userRecords.setEmailId(getUser.getEmailId());
				userRecords.setPassword(getUser.getPassword());
				userRecords.setPayId(getUser.getPayId());
			}
			List<String> userRecordList = userRecordsDao.getOldPasswords(user.getEmailId());

			logger.info("userRecordList..={}", userRecordList);
			logger.info("Adding/Updating UserDetails.={}", new Gson().toJson(user.toString()));

			if (getUser != null) {

				userDao.update(user);

			} else {
				if (getData.get("status").toString().equalsIgnoreCase("Active")) {
					Date currnetDate = new Date();
					Calendar c = Calendar.getInstance();
					c.setTime(currnetDate);
					c.add(Calendar.HOUR, emailExpiredInTime);
					Date expiryDate = c.getTime();
					user.setEmailExpiryTime(expiryDate);
					userDao.create(user);

					ResponseObject responseObject = new ResponseObject();

					responseObject.setAccountValidationID(user.getAccountValidationKey());
					responseObject.setEmail(user.getEmailId());
					responseObject.setSalt(merchantKeySaltService.getSalt(user.getPayId()));
					responseObject.setPayId(user.getPayId());
					responseObject.setRequestUrl(propertiesManager.getSystemProperty("RequestURL"));
					responseObject.setMerchantHostedEncryptionKey(userAccountServices.generateMerchantHostedEncryptionKey(user.getPayId()));
					logger.info("Going inside the Email Condition::::::");
					//ADD USER EMAIL
					emailControllerServiceProvider.addUser(responseObject,user.getFirstName());
					logger.info("Going inside the merchantTransactionDetails:::::::");
					//MERCHANT TRANSACTION DETAILS EMAIL
					emailControllerServiceProvider.merchantTransactionDetails(responseObject,user.getFirstName());

				}
			}
			if (userRecordList.isEmpty()) {
				logger.info("userRecordList.isEmpty()..={}", userRecordList.isEmpty());

				userRecordsDao.create(userRecords);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String createMerchantMdrDetails(Map<String, Object> getData) {
		TdrSetting user = new TdrSetting();
		boolean getUserMdr = false;
		User getUser = null;
		String payId = String.valueOf((Long) getData.get("payId"));
		String message = null;
		AccountCurrency accountCurrency = null;
		User userData = null;
		try {
			getUser = userDao.findByPayId(payId);
			email = getUser.getEmailId();
			getUserMdr = tdrDao.isTDRSettingSet(payId);

			// List<TdrSetting> getDetails=tdrDao.getTdrAndSurchargeByMerchantId(payId);
			List<TdrSetting> getDetails = tdrDao.getTdrAndSurchargeMerchantId(payId);

			logger.info("getUser Details...." + getUser);
			logger.info("getDetails TDR...." + getDetails);
			logger.info("mdrDetails...." + getData.get("mdrDetails"));
			logger.info("Acquirer Details......" + getData.get("acquirerDetails"));
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			ObjectMapper mapper = new ObjectMapper();
			String mdrDetails = mapper.writeValueAsString(getData.get("mdrDetails"));
			String acquirerDetails = mapper.writeValueAsString(getData.get("acquirerDetails"));
			JSONArray js = new JSONArray(mdrDetails);
			JSONArray jss = new JSONArray(acquirerDetails);
			logger.info("mdrDetails2...." + js);

			JSONArray acqArray = new JSONArray(acquirerDetails);
			for (int j = 0; j < acqArray.length(); j++) {
				JSONObject jo = jss.getJSONObject(j);
				JSONObject jsss = js.getJSONObject(j);
				acquirer = jo.optString("acquirerName");
				currency = jsss.optString("currency");
				AccountCurrency ac = new AccountCurrency();

				ac.setAdf1(jo.optString("adf1"));
				ac.setAdf2(jo.optString("adf2"));
				ac.setAdf3(jo.optString("adf3"));
				ac.setAdf4(jo.optString("adf4"));
				ac.setAdf5(jo.optString("adf5"));
				ac.setAdf6(jo.optString("adf6"));
				ac.setAdf7(jo.optString("adf7"));
				ac.setAdf8(jo.optString("adf8"));
				ac.setAdf9(jo.optString("adf9"));
				ac.setAdf10(jo.optString("adf10"));
				ac.setAdf11(jo.optString("adf11"));
				ac.setCurrencyCode(currency);
				ac.setDirectTxn(false);
				ac.setMerchantId(jo.optString("mid"));
				ac.setPassword(jo.optString("txnPassword"));
				ac.setTxnKey(jo.optString("txnKey"));
				logger.info("Merchant Id...." + ac.getMerchantId());

				userData = userDao.findAcquirerByCode(jo.optString("acquirerName"));
				JSONObject ob = new JSONObject();
				ob.put("currencyCode", ac.getCurrencyCode());
				ob.put("password", ac.getPassword());
				ob.put("txnKey", ac.getTxnKey());
				ob.put("adf1", ac.getAdf1());
				ob.put("adf2", ac.getAdf2());
				ob.put("adf3", ac.getAdf3());
				ob.put("adf4", ac.getAdf4());
				ob.put("adf5", ac.getAdf5());
				ob.put("adf6", ac.getAdf6());
				ob.put("adf7", ac.getAdf7());
				ob.put("adf8", ac.getAdf8());
				ob.put("adf9", ac.getAdf9());
				ob.put("adf10", ac.getAdf10());
				ob.put("adf11", ac.getAdf11());
				ob.put("merchantId", ac.getMerchantId());
				JSONArray array = new JSONArray();
				array.put(ob);
				setAccountCurrencySet(array.toString());

				ac.setAcqPayId(userData.getPayId());
//				accountCurrency= userDao.findAccountCurrencyDetails(userData.getPayId(),ac.getMerchantId());
//				if(accountCurrency==null) {
//				userDao.createAccountCurrency(ac);
//				}
				for (int k = 0; k < js.length(); k++) {
					JSONObject obj = js.getJSONObject(k);
					// details.setMopType();
					if (obj.optString("paymentName").equalsIgnoreCase("Net Banking")) {
						System.out.println(" get mop type ......"+(obj.optString("mopName")));
						if(obj.optString("mopName").contains(" ")) {
							System.out.println("if condition invoked for mop type ......");
							mapList.add(obj.optString("paymentName") + "-"
									+ MopType.getCodeusingInstance(obj.optString("mopName").replace(" ", "_")));
						}
						else {
							System.out.println("else condition invoked for mop type ......");
							mapList.add(obj.optString("paymentName") + "-"
									+ MopType.getCodeusingInstance(MopType.getNameFromInstance(obj.optString("mopName"))));
						}
						
					}
					else {
					mapList.add(obj.optString("paymentName") + "-"
							+ MopType.getCodeusingInstance(obj.optString("mopName")) + "-SALE");
					}

				}
				saveAcquirerMapping(email, jo.optString("acquirerName"));

			}

			for (int i = 0; i < js.length(); i++) {
				JSONObject jo = js.getJSONObject(i);
				TdrSetting details = new TdrSetting();
				Timestamp stDate = new Timestamp(Long.valueOf(jo.get("startDate").toString()));
				Timestamp eDate = new Timestamp(Long.valueOf(jo.get("endDate").toString()));
				Date sdate = new Date(stDate.getTime());
				Date edate = new Date(eDate.getTime());

				details.setAcquirerName(jo.optString("acquirerName"));
				details.setBankPreference((jo.optString("bankMdrType").equals("1")) ? "PERCENTAGE" : "FLAT");
				details.setBankMaxTdrAmt(jo.optDouble("bankMaxMdr"));
				details.setBankMinTdrAmt(jo.optDouble("bankMinMdr"));
				details.setBankTdr(jo.optDouble("bankMdr"));
				details.setEnableSurcharge(jo.optBoolean("surcharge"));

				details.setFromDate(formatter.parse(dateFormat1.format(sdate).toString()));
				details.setMaxTransactionAmount(jo.optDouble("maxAmt"));
				details.setMerchantMaxTdrAmt(jo.optDouble("maxMdr"));
				details.setMerchantMinTdrAmt(jo.optDouble("minMdr"));
				details.setMerchantPreference((jo.optString("mdrType").equals("1")) ? "PERCENTAGE" : "FLAT");
				details.setMerchantTdr(jo.optDouble("mdr"));
				details.setMinTransactionAmount(jo.optDouble("minAmt"));
				details.setMopType(MopType.getInstanceIgnoreCaseForRuleEngine(jo.optString("mopName")).toString());
				details.setPayId(jo.optString("merchantId"));
				details.setPaymentRegion("DOMESTIC");
				details.setPaymentType(PaymentType.getInstanceIgnoreCase(jo.optString("paymentName")).toString());
				details.setStatus("ACTIVE");
				details.setTdrStatus("ACTIVE");
				details.setTransactionType("SALE");
				details.setType((jo.optString("cardType").equals("1")) ? "CONSUMER" : "COMMERCIAL");
				System.out.println(jo.optString("currency"));
				details.setCurrency(jo.optString("currency"));
				// mapList.add(jo.optString("paymentName")
				// +"-"+MopType.getCodeusingInstance(details.getMopType())+"-SALE");
				User userDetail = userDao.find(email);
				Account account = userDetail.getAccountUsingAcquirerCode(details.getAcquirerName());
				logger.info("account Details...." + account);
				if (getUserMdr && getUser.getUserStatus().equals(UserStatusType.ACTIVE)) {
					try {
						for (TdrSetting getDetail : getDetails) {
							logger.info("getDetail.getId()" + getDetail.getId());
							deleteAccount(getDetail.getId());
							// getDetail.setId(getDetail.getId());
							tdrDao.delete(getDetail);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					tdrDao.create(details);
					addAccount(account.getId(), details.getId());
					// accountDao.update(account);
					message = "Mdr Details updated Successfully";

				} else {
					if (getUser.getUserStatus().equals(UserStatusType.ACTIVE)) {
						tdrDao.create(details);
						addAccount(account.getId(), details.getId());
						message = "Mdr Details added Successfully";
					} else {
						message = "Merchant status not Active";
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	private void addAccount(Long account_id, Long tdrSetting_id) {
		// TODO Auto-generated method stub
		accountDao.addAccount(account_id, tdrSetting_id);
	}

	private void deleteAccount(Long id) {
		accountDao.deletAccount(id);

	}

	public void saveAcquirerMapping(String merchantEmailId, String acquirer) {

		try {

			PendingMappingRequest existingPMR = pendingMappingRequestDao.findPendingMappingRequest(merchantEmailId,
					acquirer);
			PendingMappingRequest existingActivePMR = pendingMappingRequestDao.findActiveMappingRequest(merchantEmailId,
					acquirer);
			String prevValue = null;
			String existingMapStr = null;
			if (existingActivePMR != null && StringUtils.isNotBlank(merchantEmailId)
					&& StringUtils.isNotBlank(existingActivePMR.getAcquirer())) {
				existingMapStr = existingActivePMR.getMapString();
				MerchantAcquirerProperties merchantAcquirerProperties = merchantAcquirerPropertiesDao
						.getMerchantAcquirerProperties(userDao.getPayIdByEmailId(merchantEmailId),
								existingActivePMR.getAcquirer());
				// prevValue = getPreviousPayload(existingActivePMR,
				// merchantAcquirerProperties);
			}
			if (existingPMR != null) {
				updateMapping(existingPMR, TDRStatus.CANCELLED, "", merchantEmailId);
			}
			if (existingActivePMR != null) {
				if (mapList.size() <= 0 || accountCurrencySet.length() <= 2) {
					deactiveRulelist();
					updateMapping(existingActivePMR, TDRStatus.INACTIVE, "", merchantEmailId);
				} else {
					updateMapping(existingActivePMR, TDRStatus.INACTIVE, "", merchantEmailId);

				}

			}

			createNewMappingEntry(TDRStatus.ACTIVE, merchantEmailId, merchantEmailId);
			/*
			 * if (!StringUtils.equalsIgnoreCase(existingMapStr, getMapString())) {
			 *
			 * List<String> newMapStrList = Arrays.asList(StringUtils.split(getMapString(),
			 * ",")); List<String> existingMapStrList =
			 * Arrays.asList(StringUtils.split(existingMapStr, ",")); newMapStrList =
			 * newMapStrList.stream().filter(mapStr -> !existingMapStrList.contains(mapStr))
			 * .collect(Collectors.toList()); Resellercommision commision = new
			 * Resellercommision(); User user = userDao.findByEmailId(merchantEmailId);
			 * String payId = user.getPayId(); String resellerPayId = user.getResellerId();
			 * commision.setMerchantpayid(payId); commision.setResellerpayid(resellerPayId);
			 * commision.setBaserate("00.00"); commision.setCommission_amount("00.00");
			 * commision.setCommission_percent("00.00"); commision.setMerchant_mdr("00.00");
			 * newMapStrList.forEach(mappedStr -> { String[] mappingDetails =
			 * StringUtils.split(mappedStr, "-"); String transactionType =
			 * mappingDetails[0]; transactionType =
			 * PaymentType.getInstanceIgnoreCase(transactionType).getCode(); String mopCode
			 * = mappingDetails[1]; commision.setMop(mopCode);
			 * commision.setTransactiontype(transactionType); if
			 * (commisiondao.mopamdpayment(payId, resellerPayId, transactionType,
			 * mopCode).size() == 0) { commisiondao.saveandUpdate(commision); } }); }
			 */
			// createAuditTrailEntry(prevValue);
			if (CollectionUtils.isNotEmpty(mapList) && merchantEmailId != null && acquirer != null) {
				processMapString();
//				updateSurchargeMapping();
				updateMerchantAcquirerProperties(merchantEmailId);
				// pendingRequestEmailProcessor.processMappingEmail("Active", emailId, userType,
				// merchantEmailId);
				// setResponse(ErrorType.MAPPING_SAVED.getResponseMessage());
				// return SUCCESS;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			// setResponse(ErrorType.MAPPING_NOT_SAVED.getResponseMessage());
			// return SUCCESS;
		}
		// setResponse(ErrorType.MAPPING_NOT_SAVED.getResponseMessage());
		// return SUCCESS;
		// return acquirer;
	}

//	@SuppressWarnings("unchecked")
//	private void createAuditTrailEntry(String prevValue) throws JsonProcessingException {
//		Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
//				.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
//		AuditTrail auditTrail = new AuditTrail(getPayload(), prevValue, actionMessagesByAction.get("mopSetUp"));
//		auditTrailService.saveAudit(request, auditTrail);
//	}

//	private String getPayload() throws JsonProcessingException {
//		MerchantMappingPayload payload = new MerchantMappingPayload();
//		payload.setEmailId(getEmailId());
//		payload.setMerchantEmailId(getMerchantEmailId());
//		payload.setMapString(getMapString());
//		payload.setUserType(getUserType());
//		payload.setAccountCurrencySet(getAccountCurrencySet());
//		payload.setAcquirer(getAcquirer());
//		payload.setCustomer(isCustomer());
//		payload.setDomestic(isDomestic());
//		payload.setInternational(isInternational());
//		return mapper.writeValueAsString(payload);
//	}
//
//	private String getPreviousPayload(PendingMappingRequest existingActivePMR, MerchantAcquirerProperties merchantAcquirerProperties) throws JsonProcessingException {
//		if (existingActivePMR == null || merchantAcquirerProperties == null) {
//			return null;
//		}
//		MerchantMappingPayload prev = new MerchantMappingPayload();
//		prev.setMapString(existingActivePMR.getMapString());
//		prev.setAccountCurrencySet(existingActivePMR.getAccountCurrencySet());
//		prev.setAcquirer(existingActivePMR.getAcquirer());
//		prev.setEmailId(getEmailId());
//		prev.setMerchantEmailId(existingActivePMR.getMerchantEmailId());
//		prev.setDomestic(isDomestic(merchantAcquirerProperties.getPaymentsRegion()));
//		prev.setInternational(isInternational(merchantAcquirerProperties.getPaymentsRegion()));
//		return mapper.writeValueAsString(prev);
//	}

	private boolean isDomestic(AccountCurrencyRegion paymentRegion) {
		return AccountCurrencyRegion.DOMESTIC.equals(paymentRegion) || AccountCurrencyRegion.ALL.equals(paymentRegion);
	}

	private boolean isInternational(AccountCurrencyRegion paymentRegion) {
		return AccountCurrencyRegion.INTERNATIONAL.equals(paymentRegion)
				|| AccountCurrencyRegion.ALL.equals(paymentRegion);
	}

	public void updateMerchantAcquirerProperties(String merchantEmailId) {

		Date date = new Date();
		String merchantPayId = userDao.getPayIdByEmailId(merchantEmailId);
		MerchantAcquirerProperties merchantAcquirerProperties = new MerchantAcquirerProperties();

		merchantAcquirerProperties.setMerchantPayId(merchantPayId);
		merchantAcquirerProperties.setAcquirerCode(acquirer);
		merchantAcquirerProperties.setStatus(TDRStatus.ACTIVE);
		merchantAcquirerProperties.setCreateDate(date);
		merchantAcquirerProperties.setUpdateDate(date);

		try {

			/*
			 * if (commercial && customer) {
			 * merchantAcquirerProperties.setCardHolderType(CardHolderType.ALL); } else if
			 * (commercial && !customer) {
			 * merchantAcquirerProperties.setCardHolderType(CardHolderType.COMMERCIAL); }
			 * else if (!commercial && customer) {
			 * merchantAcquirerProperties.setCardHolderType(CardHolderType.CONSUMER); }
			 */

			if (international && domestic) {
				merchantAcquirerProperties.setPaymentsRegion(AccountCurrencyRegion.ALL);
			} else if (international && !domestic) {
				merchantAcquirerProperties.setPaymentsRegion(AccountCurrencyRegion.INTERNATIONAL);
			} else if (!international && domestic) {
				merchantAcquirerProperties.setPaymentsRegion(AccountCurrencyRegion.DOMESTIC);
			} else {
				merchantAcquirerProperties.setStatus(TDRStatus.INACTIVE);
			}

			merchantAcquirerPropertiesDao.addOrUpdateMerchantAcquirerProperties(merchantAcquirerProperties);
		}

		catch (Exception e) {
			logger.error("Exception in updateMerchantAcquirerProperties " + e);
		}

	}

	public void deactiveRulelist() {
		try {
			String payId = userDao.getPayIdByEmailId(email);
			Session session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			session.createQuery(
							"update RouterRule RR set RR.status='INACTIVE' where RR.status='ACTIVE' and RR.merchant=:payId")
					.setParameter("payId", payId).executeUpdate();
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updateMapping(PendingMappingRequest pmr, TDRStatus status, String requestedBy, String processedBy) {

		Date currentDate = new Date();
		try {

			Session session = null;
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			Long id = pmr.getId();
			session.load(pmr, pmr.getId());
			PendingMappingRequest pendingRequest = session.get(PendingMappingRequest.class, id);
			pendingRequest.setStatus(status);
			pendingRequest.setUpdatedDate(currentDate);
			if (!requestedBy.equalsIgnoreCase("")) {
				pendingRequest.setRequestedBy(requestedBy);
			}
			if (!processedBy.equalsIgnoreCase("")) {
				pendingRequest.setProcessedBy(processedBy);
			}

			session.update(pendingRequest);
			tx.commit();
			session.close();

		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {

		}

	}

//	public void updateSurchargeMapping() {
//
//		try {
//
//			String payId = userDao.getPayIdByEmailId(merchantEmailId);
//			String acquirerName = AcquirerType.getInstancefromCode(acquirer).getName();
//
//			List<Surcharge> activeSurchargeList = new ArrayList<Surcharge>();
//			List<SurchargeDetails> activeSurchargeDetailsList = new ArrayList<SurchargeDetails>();
//			Set<String> paymentTypeSet = new HashSet<String>();
//			Map<PaymentType, MopType> paymentTypeMopTypeMap = new HashMap<PaymentType, MopType>();
//
//			activeSurchargeList = surchargeDao.findActiveSurchargeListByPayIdAcquirer(payId, acquirerName);
//			activeSurchargeDetailsList = surchargeDetailsDao.getActiveSurchargeDetailsByPayId(payId);
//
//			List<String> mapStringlist = new ArrayList<String>(Arrays.asList(mapString.split(",")));
//			for (String mapStrings : mapStringlist) {
//				String[] tokens = mapStrings.split("-");
//				if (tokens[0].equalsIgnoreCase("Credit Card") || tokens[0].equalsIgnoreCase("Debit Card")) {
//					PaymentType key = PaymentType.getInstanceIgnoreCase(tokens[0]);
//					MopType value = MopType.getmop(tokens[1]);
//					paymentTypeMopTypeMap.put(key, value);
//				} else if (tokens[0].equalsIgnoreCase("Net Banking")) {
//					PaymentType key = PaymentType.getInstanceIgnoreCase(tokens[0]);
//					MopType value = MopType.getmop(tokens[1]);
//					paymentTypeMopTypeMap.put(key, value);
//				}
//
//			}
//
//			for (Surcharge surcharge : activeSurchargeList) {
//
//				boolean isMatch = false;
//
//				for (Map.Entry<PaymentType, MopType> entry : paymentTypeMopTypeMap.entrySet()) {
//					if (surcharge.getPaymentType().equals(entry.getKey())
//							&& surcharge.getMopType().equals(entry.getValue())) {
//						isMatch = true;
//					}
//				}
//
//				if (isMatch) {
//					continue;
//				} else {
//					deactivateSurcharge(surcharge);
//				}
//
//			}
//
//			for (String mapStrings : mapStringlist) {
//				String[] tokens = mapStrings.split("-");
//				paymentTypeSet.add(tokens[0]);
//			}
//
//			for (SurchargeDetails sd : activeSurchargeDetailsList) {
//
//				boolean isMatch = false;
//
//				for (String paymentType : paymentTypeSet) {
//					if (sd.getPaymentType().equalsIgnoreCase(paymentType)) {
//						isMatch = true;
//					}
//				}
//
//				if (isMatch) {
//					continue;
//				} else {
//					deactivateSurchargeDetails(sd);
//				}
//			}
//
//			System.out.println(paymentTypeSet);
//
//		} catch (Exception e) {
//			logger.error("Exception occured in MerchantMappingUpdate , updateSurchargeMapping , exception = " + e);
//		}
//	}

	public void deactivateSurchargeDetails(SurchargeDetails sd) {

		/*
		 * Date currentDate = new Date(); try {
		 *
		 * Session session = null; session = HibernateSessionProvider.getSession();
		 * Transaction tx = session.beginTransaction(); Long id = sd.getId();
		 * session.load(sd, sd.getId()); SurchargeDetails surchargeDetails =
		 * (SurchargeDetails) session.get(SurchargeDetails.class, id);
		 * surchargeDetails.setStatus(TDRStatus.INACTIVE);
		 * surchargeDetails.setUpdatedDate(currentDate);
		 * surchargeDetails.setProcessedBy(emailId);
		 *
		 * session.update(surchargeDetails); tx.commit(); session.close();
		 *
		 * } catch (HibernateException e) { e.printStackTrace(); } finally {
		 *
		 * }
		 */

	}

	public void deactivateSurcharge(Surcharge sch) {

		/*
		 * Date currentDate = new Date(); try {
		 *
		 * Session session = null; session = HibernateSessionProvider.getSession();
		 * Transaction tx = session.beginTransaction(); Long id = sch.getId();
		 * session.load(sch, sch.getId()); Surcharge surcharge= (Surcharge)
		 * session.get(Surcharge.class, id); surcharge.setStatus(TDRStatus.INACTIVE);
		 * surcharge.setUpdatedDate(currentDate); surcharge.setProcessedBy(emailId);
		 *
		 * session.update(surcharge); tx.commit(); session.close();
		 *
		 * } catch (HibernateException e) { e.printStackTrace(); } finally {
		 *
		 * }
		 */

	}

	public void createNewMappingEntry(TDRStatus status, String requestedBy, String processedBy) {

		try {

			PendingMappingRequest pmr = new PendingMappingRequest();

			Date date = new Date();
			pmr.setMerchantEmailId(email);
			pmr.setMapString(StringUtils.join(mapList, ','));
			pmr.setAcquirer(acquirer);
			// pmr.setAccountCurrencySet(accountCurrencySet);
			pmr.setAccountCurrencySet(getAccountCurrencySet());
			pmr.setCreatedDate(date);
			pmr.setUpdatedDate(date);
			pmr.setStatus(status);

			if (!requestedBy.equalsIgnoreCase("")) {
				pmr.setRequestedBy(requestedBy);
			}

			if (!processedBy.equalsIgnoreCase("")) {
				pmr.setProcessedBy(processedBy);
			}

			pendingMappingRequestDao.create(pmr);

		} catch (Exception e) {
			logger.error("Exception occured in MerchantMappingUpdate , createNewMappingEntry , exception =  " + e);
		}

	}

	private void processMapString() {
		Gson gson = new Gson();
		AccountCurrency[] accountCurrencies = gson.fromJson(accountCurrencySet, AccountCurrency[].class);
		logger.info("222233333sssssssssss"+StringUtils.join(mapList, ','));
		userMappingEditor.decideAccountChange(email, StringUtils.join(mapList, ','), getAcquirer(), accountCurrencies,
				international, domestic);
	}

	public String getAccountCurrencySet() {
		return accountCurrencySet;
	}

	public void setAccountCurrencySet(String accountCurrencySet) {
		this.accountCurrencySet = accountCurrencySet;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

}
