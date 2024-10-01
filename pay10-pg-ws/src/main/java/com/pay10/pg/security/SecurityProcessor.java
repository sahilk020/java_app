package com.pay10.pg.security;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.SmsSender;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.VpaMasterDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.AcquirerTypeService;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.ModeType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.charging.ChargingDetailHelper;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.security.Authenticator;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.history.Historian;

@Service("securityProcessor")
public class SecurityProcessor implements Processor {


	@Autowired
	private VpaMasterDao vapDao;

	@Autowired
	private Authenticator authenticator;

	@Autowired
	private Historian historian;

	@Autowired
	private GeneralValidator generalValidator;

	@Autowired
	private AWSEncryptDecryptService awsEncryptDecryptService;

	@Autowired
	private AcquirerTypeService acquirerTypeService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private ChargingDetailsDao chargingDetailsDao;

	@Autowired
	private SmsSender smsSender;

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private StaticDataProvider staticDataProvider;
	@Autowired
	private ChargingDetailHelper chargingDetailHelper;

	private static int smsCount = 0;

	private static Logger logger = LoggerFactory.getLogger(SecurityProcessor.class.getName());
	/*
	 * private static Map<String, User> userMap = new HashMap<String, User>();
	 * private static Map<String, ServiceTax> serviceTaxMap = new HashMap<String,
	 * ServiceTax>(); private static Map<String, ChargingDetails> chargingDetailsMap
	 * = new HashMap<String, ChargingDetails>(); private static Map<String,
	 * List<Surcharge>> surchargeMap = new HashMap<String, List<Surcharge>>();
	 */

	@Override
	public void preProcess(Fields fields) throws SystemException {

	}

	@Override
	public void process(Fields fields) throws SystemException {
		// Process validations
		validate(fields);//

		if (fields.get(FieldType.TXNTYPE.getName()) != null 
				&& fields.get(FieldType.RESPONSE_CODE.getName()) != null 
				&& fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.STATUS.getName())
				&& fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.INVALID_HASH.getCode())) {
			fields.put(FieldType.STATUS.getName(),"Error");
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
			return;
		}

		fields.addDefaultFields();
		fields.logAllFields("Refined Request:");
		// Process authenticity
		authenticate(fields);
		addPreviousFields(fields);

		// Check if request has already been submitted
		// checkDuplicateSubmit(fields);
		validateDupicateOrderId(fields);
		// Compare refund amount
		compareRefundAmount(fields);
		// Get applicable acquirer and respective fields
		addAcquirerFields(fields);
		chargingDetailHelper.addTransactionDataFields(fields);
		// TODO......make an interface for acquirer specific validation as its
		// taken out of ValidationProcessor

		logger.info("Before processorValidations () ");
		generalValidator.processorValidations(fields);
		logger.info("After processorValidations () ");

		addPspName(fields);


	}

	private void addPspName(Fields fields) {
		if (StringUtils.isNotBlank(fields.get(FieldType.PAYER_ADDRESS.getName()))
				&& StringUtils.isBlank(fields.get(FieldType.PSPNAME.getName()))
				&& fields.get(FieldType.PAYMENT_TYPE.getName()).equals(PaymentType.UPI.getCode())
				&& (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName())
						|| fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.ENROLL.getName()))) {
			String[] pspName = fields.get(FieldType.PAYER_ADDRESS.getName()).split("@");
			fields.put(FieldType.PSPNAME.getName(), vapDao.getDomainName("@".concat(pspName[1])).getPspName());

		}
	}

	public void validate(Fields fields) throws SystemException {
		Processor validationProcessor = new ValidationProcessor();
		validationProcessor.preProcess(fields);
		validationProcessor.process(fields);
		validationProcessor.postProcess(fields);
	}

	public void addPreviousFields(Fields fields) throws SystemException {
		// Ideally previous fields are responsibility of history processor,
		// but we are putting it here for smart router

		String txntype = fields.get(FieldType.TXNTYPE.getName());
		logger.info("addPreviousFields, txntype:"+txntype);
		if (txntype.equals(TransactionType.STATUS.getName())) {
			historian.findPreviousForStatus(fields);

		} else if (txntype.equals(TransactionType.RECO.getName())
				|| txntype.equals(TransactionType.REFUNDRECO.getName())) {
			historian.findPreviousForReco(fields);

		} else if (txntype.equals(TransactionType.VERIFY.getName())) {
			historian.findPreviousForVerify(fields);

		} else {
			historian.findPrevious(fields);

		}
		historian.populateFieldsFromPrevious(fields);

	}

	public void compareRefundAmount(Fields fields) throws SystemException {

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		String origTxnType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());

		if (StringUtils.isEmpty(origTxnType)) {
			origTxnType = "";
		}

		if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())
				&& !origTxnType.equalsIgnoreCase(TransactionType.STATUS.getName())) {

			String responseCode = fields.get(FieldType.RESPONSE_CODE.getName());
			if (responseCode.equals(ErrorType.REFUND_DENIED.getCode())) {
				throw new SystemException(ErrorType.REFUND_DENIED, "");
			} else if (responseCode.equals(ErrorType.REFUND_REJECTED.getCode())) {
				throw new SystemException(ErrorType.REFUND_REJECTED, "");
			} else if (responseCode.equals(ErrorType.TRANSACTION_NOT_FOUND.getCode())) {
				throw new SystemException(ErrorType.TRANSACTION_NOT_FOUND, "");
			}

		} else {
			return;
		}
	}

	public void checkDuplicateSubmit(Fields fields) throws SystemException {

		if (StringUtils.isNotBlank(fields.get(FieldType.TXNTYPE.getName()))
				&& StringUtils.isBlank(fields.get(FieldType.ACQ_ID.getName()))) {

			if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())
					|| fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
				historian.validateDuplicateSubmit(fields);
			}

		}

	}

	public void validateDupicateOrderId(Fields fields) throws SystemException {

		String origTxnType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
		if (StringUtils.isEmpty(origTxnType)) {
			origTxnType = "";
		}

		if (((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())))
				|| ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.REFUND.getName()))
						&& (!origTxnType.equalsIgnoreCase(TransactionType.STATUS.getName())))) {
			historian.validateDuplicateOrderId(fields);
		}

		if ((fields.contains(FieldType.IS_MERCHANT_HOSTED.getName()))
				&& !(StringUtils.isEmpty(fields.get(FieldType.IS_MERCHANT_HOSTED.getName())))
				&& (fields.get(FieldType.IS_MERCHANT_HOSTED.getName()).equals("Y"))) {
			historian.validateDuplicateOrderId(fields);
		}
	}

	public void addAcquirerFields(Fields fields) throws SystemException {

		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName()))) {
			return;
		}
		String internalOrigTxnId = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
		if (internalOrigTxnId != null) {

			if (fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
					|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
					|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
							.equals(TransactionType.REFUNDRECO.getName())) {
				return;
			}
		}
		User user = authenticator.getUser(fields);

		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		if (StringUtils.isEmpty(acquirer)) {
			acquirer = fields.get(FieldType.INTERNAL_ACQUIRER_TYPE.getName());
			if (StringUtils.isEmpty(acquirer)) {
				// authenticator.validatePaymentOptions(fields);
				AcquirerType acquirerType = acquirerTypeService.getDefault(fields, user);

				acquirer = acquirerType.getCode();
				logger.info("Current Acquirer for Order Id " + fields.get(FieldType.ORDER_ID.getName()) + " is "
						+ acquirer);
				fields.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
				fields.put(FieldType.INTERNAL_ACQUIRER_TYPE.getName(), acquirer);
			} else {
				fields.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
		}

		if (user == null) {
			logger.info("User is null for txn ID = " + fields.get(FieldType.TXN_ID.getName()));
		} else {
			logger.info("User payID = " + user.getPayId() + " Txn payID = " + fields.get(FieldType.PAY_ID.getName()));
		}

		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				// logger.info("accountThis "+accountThis.toString());
				// logger.info("acquirer "+acquirer);
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(acquirer).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());

		// Below patch added to update user when a new acquirer is onboarded and we need
		// to update user in static map
		if (null == account) {

			logger.info("Account is null for Order Id = " + fields.get(FieldType.ORDER_ID.getName()));
			logger.info("Updating user from database again to get latest data");
			user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
			if (user != null) {

				logger.info("Found updated user from Database, setting into authenticator now");
				authenticator.setUpdatedUser(user);
			}
			user = authenticator.getUser(fields);
			if (user != null) {
				logger.info("Found updated user and added to static map !!");
			}
			accounts = user.getAccounts();

			if (accounts == null || accounts.size() == 0) {
				logger.info("No account found in updated user for Pay ID = " + fields.get(FieldType.PAY_ID.getName())
						+ " and ORDER ID = " + fields.get(FieldType.ORDER_ID.getName()));
			} else {
				for (Account accountThis : accounts) {
					if (accountThis.getAcquirerName()
							.equalsIgnoreCase(AcquirerType.getInstancefromCode(acquirer).getName())) {
						account = accountThis;
						break;
					}
				}
			}

			if (account != null) {
				logger.info("Found account details in updated user !!");
			}
		}

		if (null == account) {
			smsCount = smsCount + 1;

			String payId = fields.get(FieldType.PAY_ID.getName());

			if (smsCount % 3 == 0) {

				StringBuilder smsBody = new StringBuilder();
				smsBody.append("Alert !");
				smsBody.append("Transaction not processed for merchant  with payId = " + payId);
				if (null != user.getBusinessName()) {
					smsBody.append(" and name  = " + user.getBusinessName());
				}
				smsBody.append(" due to missing account for merchant .");
				smsBody.append("Transaction Id = " + fields.get(FieldType.TXN_ID.getName()));
				if (StringUtils.isNotBlank(fields.get(FieldType.ORDER_ID.getName()))) {
					smsBody.append(" , Order Id = " + fields.get(FieldType.ORDER_ID.getName()));
				}

				String smsSenderList = PropertiesManager.propertiesMap.get("smsAlertList");

				for (String mobile : smsSenderList.split(",")) {
					try {
						smsSender.sendSMS(mobile, smsBody.toString());
					} catch (IOException e) {
						logger.error("SMS not sent for transaction failure when account is null");
					}
				}

			}
			throw new SystemException(ErrorType.NOT_APPROVED_FROM_ACQUIRER,
					"User is not approved from acquirer, PayId=" + payId + " , Acquirer = " + acquirer);
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(currencyCode);
		if (null == accountCurrency) {
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "Merchant not mapped for this currency");

			throw new SystemException(ErrorType.CURRENCY_NOT_MAPPED, ErrorType.CURRENCY_NOT_MAPPED.getResponseMessage()
					+ user.getPayId() + " and currency code " + fields.get(FieldType.CURRENCY_CODE.getName()));
		}

		// Get MerchantId from account
		String merchantId = accountCurrency.getMerchantId();
		if (!StringUtils.isEmpty(merchantId)) {
			fields.put(FieldType.MERCHANT_ID.getName(), merchantId);
		}

		boolean nonsecure = accountCurrency.isDirectTxn();
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (nonsecure && txnType.equals(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),
					ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName());
		}
		// Get Password
		if (!StringUtils.isEmpty(accountCurrency.getPassword())) {
			String decryptedPassword = awsEncryptDecryptService.decrypt(accountCurrency.getPassword());
			fields.put(FieldType.PASSWORD.getName(), decryptedPassword);
		}

		// Get key
		String encryptionKey = accountCurrency.getTxnKey();
		if (!StringUtils.isEmpty(encryptionKey)) {
			fields.put(FieldType.TXN_KEY.getName(), encryptionKey);
		}

		fields.put(FieldType.ADF1.getName(), accountCurrency.getAdf1());
		fields.put(FieldType.ADF2.getName(), accountCurrency.getAdf2());
		fields.put(FieldType.ADF3.getName(), accountCurrency.getAdf3());
		fields.put(FieldType.ADF4.getName(), accountCurrency.getAdf4());
		fields.put(FieldType.ADF5.getName(), accountCurrency.getAdf5());
		fields.put(FieldType.ADF8.getName(), accountCurrency.getAdf8());
		fields.put(FieldType.ADF9.getName(), accountCurrency.getAdf9());
		fields.put(FieldType.ADF10.getName(), accountCurrency.getAdf10());
		fields.put(FieldType.ADF11.getName(), accountCurrency.getAdf11());

		if (!StringUtils.isEmpty(accountCurrency.getAdf6())
				&& !StringUtils.equalsIgnoreCase(acquirer, AcquirerType.CAMSPAY.getCode())) {
			String adf6 = awsEncryptDecryptService.decrypt(accountCurrency.getAdf6());
			fields.put(FieldType.ADF6.getName(), adf6);
		}

		if (!StringUtils.isEmpty(accountCurrency.getAdf7())
				&& !StringUtils.equalsIgnoreCase(acquirer, AcquirerType.CAMSPAY.getCode())) {
			String adf7 = awsEncryptDecryptService.decrypt(accountCurrency.getAdf7());
			fields.put(FieldType.ADF7.getName(), adf7);
		}

		String internalTxnType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
		if (null == internalTxnType) {
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(),
					ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName());
		}
	}

	public void authenticate(Fields fields) throws SystemException {
		User user = null;

		if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
				&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
						.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
			user = staticDataProvider.getUserData(fields.get(FieldType.PAY_ID.getName()));
		}

		else {
			user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		}

		authenticator.setUser(user);
		authenticator.authenticate(fields);
	}

	@Override
	public void postProcess(Fields fields) {
		fields.removeSecureFieldsSubmitted();
		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.REFUND.getName()))) {
			if (StringUtils.isBlank(fields.get(FieldType.REFUND_FLAG.getName()))) {
				fields.put(FieldType.REFUND_FLAG.getName(), "C");
			}
		}
	}
}
