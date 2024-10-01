package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.MerchantAcquirerPropertiesDao;
import com.pay10.commons.dao.PendingMappingRequestDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.mongo.WalletHistoryRepository;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.MerchantAcquirerProperties;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.PendingMappingRequest;
import com.pay10.commons.user.Resellercommision;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.SurchargeDetailsDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.resellercommisiondao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TDRStatus;
import com.pay10.crm.actionBeans.UserMappingEditor;
import com.pay10.crm.audittrail.dto.MerchantMappingPayload;

/**
 * @author Puneet
 *
 */
public class MerchantMappingUpdate extends AbstractSecureAction {

	@Autowired
	private PendingMappingRequestDao pendingMappingRequestDao;

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private SurchargeDetailsDao surchargeDetailsDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserMappingEditor userMappingEditor;

	/*
	 * @Autowired private PendingRequestEmailProcessor pendingRequestEmailProcessor;
	 */

	@Autowired
	private MerchantAcquirerPropertiesDao merchantAcquirerPropertiesDao;

	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(MerchantMappingUpdate.class.getName());
	private static final long serialVersionUID = -9103516274778187455L;

	private String merchantEmailId;
	private String mapString;
	private String acquirer;
	private String response;
	private String accountCurrencySet;
	private String userType;
	private String emailId;

	private boolean international;
	private boolean domestic;
	private boolean commercial;
	private boolean customer;

	@Autowired
	private resellercommisiondao commisiondao;
	
	@Autowired 
	private WalletHistoryRepository walletHistoryRepository;
	@Autowired
	private MultCurrencyCodeDao multCurrencyDao;

	@Override
	public String execute() {

		try {
			System.out.println("Deep : " + mapString);
			if (mapString.contains("pgCheckbox")) {
				mapString = mapString.replace("pgCheckbox,", "");
			}
	
			PendingMappingRequest existingPMR = pendingMappingRequestDao.findPendingMappingRequest(merchantEmailId,
					acquirer);
			logger.info("PendingMappingRequest for existingPMR:::::::"+new Gson().toJson(existingPMR));
			PendingMappingRequest existingActivePMR = pendingMappingRequestDao.findActiveMappingRequest(merchantEmailId,
					acquirer);

		logger.info("PendingMappingRequest for existingActivePMR::::::"+new Gson().toJson(existingActivePMR));
			String prevValue = null;
			String existingMapStr = null;
			if (existingActivePMR != null && StringUtils.isNotBlank(getMerchantEmailId())
					&& StringUtils.isNotBlank(existingActivePMR.getAcquirer())) {
				existingMapStr = existingActivePMR.getMapString();
				MerchantAcquirerProperties merchantAcquirerProperties = merchantAcquirerPropertiesDao
						.getMerchantAcquirerProperties(userDao.getPayIdByEmailId(getMerchantEmailId()),
								existingActivePMR.getAcquirer());

				logger.info("MerchantAcquirerProperties for merchantAcquirerProperties::::::"+new Gson().toJson(merchantAcquirerProperties));
				prevValue = getPreviousPayload(existingActivePMR, merchantAcquirerProperties);
			}
			if (existingPMR != null) {
				updateMapping(existingPMR, TDRStatus.CANCELLED, "", emailId);
			}

			if (existingActivePMR != null) {
//				logger.info("Account Currency Set :::::::"+accountCurrencySet);
				if (mapString.length() <= 0 || accountCurrencySet.length() <= 2) {
					deactiveRulelist();
					updateMapping(existingActivePMR, TDRStatus.INACTIVE, "", emailId);

				} else {
					updateMapping(existingActivePMR, TDRStatus.INACTIVE, "", emailId);

				}

			}

			createNewMappingEntry(TDRStatus.ACTIVE, emailId, emailId);
			if (!StringUtils.equalsIgnoreCase(existingMapStr, getMapString())) {
//				logger.info("getMapString......={}",getMapString());
//				logger.info("existingMapStr......={}",existingMapStr);
				List<String> newMapStrList = Arrays.asList(StringUtils.split(getMapString(), ","));
				if(existingMapStr!=null) {
				List<String> existingMapStrList = Arrays.asList(StringUtils.split(existingMapStr, ","));
				
				newMapStrList = newMapStrList.stream().filter(mapStr -> !existingMapStrList.contains(mapStr))
						.collect(Collectors.toList());
				}
				Resellercommision commision = new Resellercommision();
				User user = userDao.findByEmailId(getMerchantEmailId());
				String payId = user.getPayId();
				String resellerPayId = user.getResellerId();
				commision.setMerchantpayid(payId);
				commision.setResellerpayid(resellerPayId);
				commision.setBaserate("00.00");
				commision.setCommission_amount("00.00");
				commision.setCommission_percent("00.00");
				commision.setMerchant_mdr("00.00");
				newMapStrList.forEach(mappedStr -> {
					String[] mappingDetails = StringUtils.split(mappedStr, "-");
					String currency= mappingDetails[0];
					String transactionType = mappingDetails[1];
					transactionType = PaymentType.getInstanceIgnoreCase(transactionType).getCode();
					String mopCode = mappingDetails[2];
					commision.setMop(mopCode);
					commision.setTransactiontype(transactionType);
					if (commisiondao.mopamdpayment(payId, resellerPayId, transactionType, mopCode,currency).size() == 0) {
						commisiondao.saveandUpdate(commision);
					}
				});
			}
			createAuditTrailEntry(prevValue);
			if (mapString != null && merchantEmailId != null && acquirer != null) {
				createWalletBasedOnPayIdAndCurrency(userDao.findPayIdByEmail(merchantEmailId));
				processMapString();
//				updateSurchargeMapping();
				updateMerchantAcquirerProperties(merchantEmailId);
				// pendingRequestEmailProcessor.processMappingEmail("Active", emailId, userType,
				// merchantEmailId);
				setResponse(ErrorType.MAPPING_SAVED.getResponseMessage());
				return SUCCESS;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			setResponse(ErrorType.MAPPING_NOT_SAVED.getResponseMessage());
			return SUCCESS;
		}
		setResponse(ErrorType.MAPPING_NOT_SAVED.getResponseMessage());
		return SUCCESS;
	}

	private void createWalletBasedOnPayIdAndCurrency(User user) {
		Gson gson=new Gson();
		AccountCurrency[] accountCurrencies = gson.fromJson(accountCurrencySet, AccountCurrency[].class);
		Stream.of(accountCurrencies).forEach(accountCurrency->{
			System.err.println("DEEP : " + accountCurrency.getCurrencyCode()+"\t"+accountCurrency.getCurrencyName());
			
			//deep code added here
			boolean exist = walletHistoryRepository.findMerchantFundByPayIdAndCurrency(user.getPayId(), multCurrencyDao.getCurrencyNamebyCode(accountCurrency.getCurrencyCode()));
			if (!exist) {
				walletHistoryRepository.createMerchantWalletByPayIdAndCurrencyName(user.getPayId(), multCurrencyDao.getCurrencyNamebyCode(accountCurrency.getCurrencyCode()));
			}
			
		});
		
	}

	@SuppressWarnings("unchecked")
	private void createAuditTrailEntry(String prevValue) throws JsonProcessingException {
		Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
				.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
		AuditTrail auditTrail = new AuditTrail(getPayload(), prevValue, actionMessagesByAction.get("mopSetUp"));
		auditTrailService.saveAudit(request, auditTrail);
	}

	private String getPayload() throws JsonProcessingException {
		MerchantMappingPayload payload = new MerchantMappingPayload();
		payload.setEmailId(getEmailId());
		payload.setMerchantEmailId(getMerchantEmailId());
		payload.setMapString(getMapString());
		payload.setUserType(getUserType());
		payload.setAccountCurrencySet(getAccountCurrencySet());
		payload.setAcquirer(getAcquirer());
		payload.setCustomer(isCustomer());
		payload.setDomestic(isDomestic());
		payload.setInternational(isInternational());

//		logger.info("GET PAYLOAD ::::::"+payload);
		return mapper.writeValueAsString(payload);
	}

	private String getPreviousPayload(PendingMappingRequest existingActivePMR, MerchantAcquirerProperties merchantAcquirerProperties) throws JsonProcessingException {
		if (existingActivePMR == null || merchantAcquirerProperties == null) {
			return null;
		}
		MerchantMappingPayload prev = new MerchantMappingPayload();
		prev.setMapString(existingActivePMR.getMapString());
		prev.setAccountCurrencySet(existingActivePMR.getAccountCurrencySet());
		prev.setAcquirer(existingActivePMR.getAcquirer());
		prev.setEmailId(getEmailId());
		prev.setMerchantEmailId(existingActivePMR.getMerchantEmailId());
		prev.setDomestic(isDomestic(merchantAcquirerProperties.getPaymentsRegion()));
		prev.setInternational(isInternational(merchantAcquirerProperties.getPaymentsRegion()));
//		logger.info("Previous payload :::::"+prev);
		return mapper.writeValueAsString(prev);
	}

	private boolean isDomestic(AccountCurrencyRegion paymentRegion) {
		return AccountCurrencyRegion.DOMESTIC.equals(paymentRegion) || AccountCurrencyRegion.ALL.equals(paymentRegion);
	}

	private boolean isInternational(AccountCurrencyRegion paymentRegion) {
		return AccountCurrencyRegion.INTERNATIONAL.equals(paymentRegion) || AccountCurrencyRegion.ALL.equals(paymentRegion);
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

			logger.info("Merchant Acquirer Properties in updateMerchantAcquirerProperties::::::"+merchantAcquirerProperties);
			merchantAcquirerPropertiesDao.addOrUpdateMerchantAcquirerProperties(merchantAcquirerProperties);
			logger.info("Merchant Mapped Successfully");
		}

		catch (Exception e) {
			logger.error("Exception in updateMerchantAcquirerProperties " + e);
		}

	}

	public void deactiveRulelist() {
		try {
			String payId = userDao.getPayIdByEmailId(merchantEmailId);
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
//			logger.info("RequestedBy:::::"+requestedBy);
//			logger.info("ProcessedBy:::::"+processedBy);
			if (!requestedBy.equalsIgnoreCase("")) {
				pendingRequest.setRequestedBy(requestedBy);
			}
			if (!processedBy.equalsIgnoreCase("")) {
				pendingRequest.setProcessedBy(processedBy);
			}

//			logger.info("Pending Request in updateMapping::::::"+pendingRequest);
			session.update(pendingRequest);
			tx.commit();
			session.close();

		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {

		}



	}

	public void updateSurchargeMapping() {

		try {

			String payId = userDao.getPayIdByEmailId(merchantEmailId);
			String acquirerName = AcquirerType.getInstancefromCode(acquirer).getName();

			List<Surcharge> activeSurchargeList = new ArrayList<Surcharge>();
			List<SurchargeDetails> activeSurchargeDetailsList = new ArrayList<SurchargeDetails>();
			Set<String> paymentTypeSet = new HashSet<String>();
			Map<PaymentType, MopType> paymentTypeMopTypeMap = new HashMap<PaymentType, MopType>();

			activeSurchargeList = surchargeDao.findActiveSurchargeListByPayIdAcquirer(payId, acquirerName);
			activeSurchargeDetailsList = surchargeDetailsDao.getActiveSurchargeDetailsByPayId(payId);

			List<String> mapStringlist = new ArrayList<String>(Arrays.asList(mapString.split(",")));
			for (String mapStrings : mapStringlist) {
				String[] tokens = mapStrings.split("-");
				if (tokens[0].equalsIgnoreCase("Credit Card") || tokens[0].equalsIgnoreCase("Debit Card")) {
					PaymentType key = PaymentType.getInstanceIgnoreCase(tokens[0]);
					MopType value = MopType.getmop(tokens[1]);
					paymentTypeMopTypeMap.put(key, value);
				} else if (tokens[0].equalsIgnoreCase("Net Banking")) {
					PaymentType key = PaymentType.getInstanceIgnoreCase(tokens[0]);
					MopType value = MopType.getmop(tokens[1]);
					paymentTypeMopTypeMap.put(key, value);
				}

			}

			for (Surcharge surcharge : activeSurchargeList) {

				boolean isMatch = false;

				for (Map.Entry<PaymentType, MopType> entry : paymentTypeMopTypeMap.entrySet()) {
					if (surcharge.getPaymentType().equals(entry.getKey())
							&& surcharge.getMopType().equals(entry.getValue())) {
						isMatch = true;
					}
				}

				if (isMatch) {
					continue;
				} else {
					deactivateSurcharge(surcharge);
				}

			}

			for (String mapStrings : mapStringlist) {
				String[] tokens = mapStrings.split("-");
				paymentTypeSet.add(tokens[0]);
			}

			for (SurchargeDetails sd : activeSurchargeDetailsList) {

				boolean isMatch = false;

				for (String paymentType : paymentTypeSet) {
					if (sd.getPaymentType().equalsIgnoreCase(paymentType)) {
						isMatch = true;
					}
				}

				if (isMatch) {
					continue;
				} else {
					deactivateSurchargeDetails(sd);
				}
			}

//			logger.info("Payment Type Set"+paymentTypeSet);

		} catch (Exception e) {
			logger.error("Exception occured in MerchantMappingUpdate , updateSurchargeMapping , exception = " + e);
		}
	}

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
			pmr.setMerchantEmailId(merchantEmailId);
			pmr.setMapString(mapString);
			pmr.setAcquirer(acquirer);
			pmr.setAccountCurrencySet(accountCurrencySet);
			pmr.setCreatedDate(date);
			pmr.setUpdatedDate(date);
			pmr.setStatus(status);

			if (!requestedBy.equalsIgnoreCase("")) {
				pmr.setRequestedBy(requestedBy);
			}

			if (!processedBy.equalsIgnoreCase("")) {
				pmr.setProcessedBy(processedBy);
			}

//			logger.info("PMR in createNewMappingEntry ::::::::"+pmr);
			pendingMappingRequestDao.create(pmr);

		} catch (Exception e) {
			logger.error("Exception occured in MerchantMappingUpdate , createNewMappingEntry , exception =  " + e);
		}

	}

	private void processMapString() {
		Gson gson = new Gson();
		AccountCurrency[] accountCurrencies = gson.fromJson(accountCurrencySet, AccountCurrency[].class);
		logger.info("Inside is method processMapString()"+accountCurrencySet);
		userMappingEditor.decideAccountChange(getMerchantEmailId(), getMapString(), getAcquirer(), accountCurrencies,international,domestic);
	}

	@Override
	public void validate() {
		Gson gson = new Gson();
		AccountCurrency[] accountCurrencies = gson.fromJson(accountCurrencySet, AccountCurrency[].class);
		if ((validator.validateBlankField(getAcquirer()))) {
		} else if (!validator.validateField(CrmFieldType.ACQUIRER, getAcquirer())) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if ((validator.validateBlankField(getMapString()))) {
		} else if (!validator.validateField(CrmFieldType.MAP_STRING, getMapString())) {
			addFieldError(CrmFieldType.MAP_STRING.getName(), ErrorType.INVALID_FIELD.getResponseMessage());

		}
		if ((validator.validateBlankField(getMerchantEmailId()))) {
		} else if (!validator.validateField(CrmFieldType.MERCHANT_EMAILID, getMerchantEmailId())) {
			addFieldError(CrmFieldType.MERCHANT_EMAILID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());

		}
		if ((validator.validateBlankField(getResponse()))) {
		} else if (!validator.validateField(CrmFieldType.RESPONSE, getResponse())) {
			addFieldError(CrmFieldType.RESPONSE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());

		}
		// AccountCurrency Class validation
		for (AccountCurrency accountCurrencyFE : accountCurrencies) {
			if ((validator.validateBlankField(accountCurrencyFE.getAcqPayId()))) {
			} else if (!(validator.validateField(CrmFieldType.ACQ_PAYID, accountCurrencyFE.getAcqPayId()))) {
				addFieldError(CrmFieldType.ACQ_PAYID.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(accountCurrencyFE.getMerchantId()))) {
			} else if (!(validator.validateField(CrmFieldType.MERCHANTID, accountCurrencyFE.getMerchantId()))) {
				addFieldError(CrmFieldType.MERCHANTID.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(accountCurrencyFE.getCurrencyCode()))) {
			} else if (!(validator.validateField(CrmFieldType.CURRENCY, accountCurrencyFE.getCurrencyCode()))) {
				addFieldError(CrmFieldType.CURRENCY.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(accountCurrencyFE.getPassword()))) {

			} else if (!(validator.validateField(CrmFieldType.ACCOUNT_PASSWORD, accountCurrencyFE.getPassword()))) {
				addFieldError(CrmFieldType.ACCOUNT_PASSWORD.getName(),
						validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(accountCurrencyFE.getTxnKey()))) {
			} else if (!(validator.validateField(CrmFieldType.TXN_KEY, accountCurrencyFE.getTxnKey()))) {
				addFieldError(CrmFieldType.TXN_KEY.getName(), validator.getResonseObject().getResponseMessage());
			}
		}
	}

	public String getMapString() {
		return mapString;
	}

	public void setMapString(String mapString) {
		this.mapString = mapString;
	}

	public String display() {
		return NONE;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getAccountCurrencySet() {
		return accountCurrencySet;
	}

	public void setAccountCurrencySet(String accountCurrencySet) {
		this.accountCurrencySet = accountCurrencySet;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public boolean isInternational() {
		return international;
	}

	public void setInternational(boolean international) {
		this.international = international;
	}

	public boolean isDomestic() {
		return domestic;
	}

	public void setDomestic(boolean domestic) {
		this.domestic = domestic;
	}

	public boolean isCommercial() {
		return commercial;
	}

	public void setCommercial(boolean commercial) {
		this.commercial = commercial;
	}

	public boolean isCustomer() {
		return customer;
	}

	public void setCustomer(boolean customer) {
		this.customer = customer;
	}

}
