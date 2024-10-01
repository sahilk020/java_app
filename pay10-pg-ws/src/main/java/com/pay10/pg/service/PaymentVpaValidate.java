/**
 *
 */
package com.pay10.pg.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.VpaValidateMidDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MerchantPaymentType;
import com.pay10.commons.util.ModeType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.cosmos.CosmosProcessor;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.SurchargeAmountCalculator;
import com.pay10.pg.core.util.UpdateProcessor;
import com.pay10.requestrouter.RequestRouter;
import com.pay10.sbi.SbiProcessor;
import com.pay10.sbi.upi.SbiUpiIntegrator;
import com.pay10.yesbankcb.YesBankCbIntegrator;

/**
 * @author Pay10
 *
 */
@Service
public class PaymentVpaValidate {

	private static Logger logger = LoggerFactory.getLogger(PaymentVpaValidate.class.getName());

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private GeneralValidator generalValidator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private YesBankCbIntegrator yesBankCbIntegrator;

	@Autowired
	private VpaValidateMidDao vpaValidateMidDao;

	@Autowired
	private SbiUpiIntegrator sbiUpiIntegrator;

	@Autowired
	private CosmosProcessor cosmosProcessor;

	@Autowired
	private SbiProcessor sbiProcessor;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private SurchargeAmountCalculator calculateSurchargeAmount;

	@Autowired
	@Qualifier("updateProcessor")
	private UpdateProcessor updateProcessor;
	@Autowired
	@Qualifier("securityProcessor")
	private Processor securityProcessor;
	@Autowired
	@Qualifier("cSourceProcessor")
	private Processor cSourceProcessor;

	@Autowired
	@Qualifier("fraudProcessor")
	private Processor fraudProcessor;

	@Autowired
	private RequestRouter router;
	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	RouterConfigurationDao routerConfigurationDao;

	@SuppressWarnings("incomplete-switch")
	public Map<String, String> process(Fields fields) throws SystemException {

		try {
			User user1 = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));

			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);

			User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));

			MerchantPaymentType merchantPaymentType = MerchantPaymentType
					.getInstanceFromCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
			// unsupported/invalid payment type received from merchant
			if (null == merchantPaymentType) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid payment type");
			}

//
			fields.put((FieldType.SURCHARGE_FLAG.getName()), ((user.isSurchargeFlag()) ? "Y" : "N"));

			if (merchantPaymentType.getName().equalsIgnoreCase(PaymentType.UPI.getName())) {
				fields.put(FieldType.PAYMENT_TYPE.getName(), PaymentType.UPI.getCode());
				fields.put(FieldType.MOP_TYPE.getName(), MopType.UPI.getCode());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
				fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
			}
			String origTxnType = ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName();
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			// Put transaction id as original txnId for subsequent transactions
			fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			fields.remove(FieldType.ORIG_TXN_ID.getName());
			fields.logAllFields("check vpa logs ....");
			// handle surcharge flg and amount
			// handleTransactionCharges(fields);

			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			// put Internal fields back
			fields.put(internalFields);
			fields.logAllFieldsUsingMasking("check vpa  Refine Request:");

			logger.info("logger for ip in S2s call" + fields.getFieldsAsString());
			if (StringUtils.isNotBlank(fields.get(FieldType.PAYER_ADDRESS.getName()))) {
				fields.put(FieldType.CARD_MASK.getName(), fields.get(FieldType.PAYER_ADDRESS.getName()));
			}
			logger.info("logger for ip in S2s call" + fields.getFieldsAsString());

			getValidVpa(fields);

			return fields.getFields();

		} catch (SystemException systemException) {

			if (null == fields) {
				throw new RuntimeException(systemException.getMessage());
			}

			if (!StringUtils.isBlank(systemException.getMessage())) {
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getMessage());
			}

			User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
			String origTxnType = ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName();

			fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
			fields.put(FieldType.STATUS.getName(), systemException.getErrorType().getCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getMessage());
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");

			return fields.getFields();
		}
	}

	public void getStatus(Fields fields) throws SystemException {

	}

	public void getValidVpa(Fields fields) throws SystemException {

		// Process security
		ProcessManager.flow(securityProcessor, fields, false);

		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		logger.info("acquirer name : " + acquirer);

		if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKCB.getCode())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			try {
				String vpaStatus = yesBankCbIntegrator.vpaValidation(fields);
				if (vpaStatus.equalsIgnoreCase("VE")) {
					updateValidVpaResponse(fields);
				} else {
					updateInvalidVpaResponse(fields);
				}
			} catch (SystemException systemException) {
				// TODO: handle exception
				logger.info("VPA Validation SystemException for YESBANKCB");
				getVpaValidateOtherAcquirer(fields);

			}
		} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.COSMOS.getCode())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			try {
				boolean vpaStatus = cosmosProcessor.vpaValidation(fields);
				if (vpaStatus) {

					updateValidVpaResponse(fields);
				} else {
					updateInvalidVpaResponse(fields);
				}
			} catch (SystemException systemException) {
				logger.info("VPA Validation SystemException for COSMOS");
				getVpaValidateOtherAcquirer(fields);

			}

		} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBI.getCode())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			try {
				boolean vpaStatus = sbiUpiIntegrator.vpaValidation(fields);
				if (vpaStatus) {

					updateValidVpaResponse(fields);
				} else {
					updateInvalidVpaResponse(fields);
				}
			} catch (SystemException systemException) {
				logger.info("VPA Validation SystemException for SBI");
				getVpaValidateOtherAcquirer(fields);

			}

		} else {

			getVpaValidateOtherAcquirer(fields);
		}

		logger.info("########## Completed getValidVpa ###############");
	}

	public void getVpaValidateOtherAcquirer(Fields fields) {
		fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		try {
			fieldsDao.getLastestCapturedTransationforAllAcquirer(fields);
			getTxnKey(fields, fields.get(FieldType.PAY_ID.getName()), fields.get(FieldType.ACQUIRER_TYPE.getName()));

			switch (fields.get(FieldType.ACQUIRER_TYPE.getName())) {
			case "COSMOS":
				boolean vpaStatus = cosmosProcessor.vpaValidation(fields);
				if (vpaStatus) {

					updateValidVpaResponse(fields);
				} else {
					updateInvalidVpaResponse(fields);
				}
				break;
			case "SBI":
				boolean vpaStatus2 = sbiUpiIntegrator.vpaValidation(fields);
				if (vpaStatus2) {

					updateValidVpaResponse(fields);
				} else {
					updateInvalidVpaResponse(fields);
				}
				break;
			case "YESBANKCB":
				getCapturedTxnMids(fields, "YESBANKCB");
				String vpaStatus1 = yesBankCbIntegrator.vpaValidation(fields);
				if (vpaStatus1.equalsIgnoreCase("VE")) {
					updateValidVpaResponse(fields);
				} else {
					updateInvalidVpaResponse(fields);
				}
				break;

			default:
				updateValidVpaResponse(fields);

				break;
			}

		} catch (SystemException systemException3) {

			try {
				// vpaValidateMidDao.getMidByAcquirer(fields, "COSMOS");
				getCapturedTxnMids(fields, "COSMOS");

				boolean vpaStatus = cosmosProcessor.vpaValidation(fields);
				if (vpaStatus) {

					updateValidVpaResponse(fields);
				} else {
					updateInvalidVpaResponse(fields);
				}
			} catch (SystemException systemException) {
				try {

					// vpaValidateMidDao.getMidByAcquirer(fields, "YESBANKCB");
					getCapturedTxnMids(fields, "YESBANKCB");
					String vpaStatus = yesBankCbIntegrator.vpaValidation(fields);
					if (vpaStatus.equalsIgnoreCase("VE")) {
						updateValidVpaResponse(fields);
					} else {
						updateInvalidVpaResponse(fields);
					}
				} catch (SystemException systemException1) {
					try {
						// vpaValidateMidDao.getMidByAcquirer(fields, "SBI");
						getCapturedTxnMids(fields, "SBI");

						boolean vpaStatus = sbiUpiIntegrator.vpaValidation(fields);
						if (vpaStatus) {

							updateValidVpaResponse(fields);
						} else {
							updateInvalidVpaResponse(fields);
						}
					} catch (SystemException systemException2) {

						updateValidVpaResponse(fields);

					}
				}

			}
		}

	}

	public void getTxnKey(Fields fields, String payid, String acquirer) throws SystemException {

		Map<String, String> keyMap = new HashMap<String, String>();

		User user = userDao.findPayId(payid);
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {

		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(acquirer).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency("356");

		fields.put(FieldType.MERCHANT_ID.getName(), accountCurrency.getMerchantId());
		fields.put(FieldType.TXN_KEY.getName(), accountCurrency.getTxnKey());
		fields.put(FieldType.ADF1.getName(), accountCurrency.getAdf1());
		fields.put(FieldType.ADF2.getName(), accountCurrency.getAdf2());
		fields.put(FieldType.ADF3.getName(), accountCurrency.getAdf3());
		fields.put(FieldType.ADF4.getName(), accountCurrency.getAdf4());
		fields.put(FieldType.ADF5.getName(), accountCurrency.getAdf5());
		fields.put(FieldType.ADF6.getName(), accountCurrency.getAdf6());
		fields.put(FieldType.ADF7.getName(), accountCurrency.getAdf7());
		fields.put(FieldType.ADF8.getName(), accountCurrency.getAdf8());
		fields.put(FieldType.ADF9.getName(), accountCurrency.getAdf9());
		fields.put(FieldType.ADF10.getName(), accountCurrency.getAdf10());
		fields.put(FieldType.ADF11.getName(), accountCurrency.getAdf11());

	}

	public void getCapturedTxnMids(Fields fields, String acquirer) throws SystemException {

		String payId = fieldsDao.getLastestCapturedTransation(acquirer);
		getTxnKey(fields, payId, acquirer);

	}

	public void getValidVpa_BK(Fields fields) throws SystemException {

		// Process security
		ProcessManager.flow(securityProcessor, fields, false);

		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		logger.info("acquirer name : " + acquirer);
		if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKCB.getCode())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			String vpaStatus = yesBankCbIntegrator.vpaValidation(fields);
			if (vpaStatus.equalsIgnoreCase("VE")) {

				updateValidVpaResponse(fields);
			} else {
				updateInvalidVpaResponse(fields);
			}
		} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.COSMOS.getCode())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			boolean vpaStatus = cosmosProcessor.vpaValidation(fields);
			if (vpaStatus) {

				updateValidVpaResponse(fields);
			} else {
				updateInvalidVpaResponse(fields);
			}

		} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBI.getCode())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			boolean vpaStatus = sbiUpiIntegrator.vpaValidation(fields);
			if (vpaStatus) {

				updateValidVpaResponse(fields);
			} else {
				updateInvalidVpaResponse(fields);
			}

		} else {
			updateInvalidVpaResponse(fields);
		}

		// ProcessManager.flow(updateProcessor, fields, true);

	}

	public void createResponse(Fields fields, String processCall) {

		if (processCall.equals(Constants.UPI_S2S_VPA_VALID.getValue())) {
			fields.remove(FieldType.SURCHARGE_FLAG.getName());
			fields.remove(FieldType.TOTAL_AMOUNT.getName());
			fields.remove(FieldType.AMOUNT.getName());

		} else if (processCall.equals(Constants.UPI_S2S_COLLECT_PAY.getValue())) {
			fields.remove(FieldType.CARD_MASK.getName());
		}
		responseCreator.ResponsePostUpiS2s(fields);

	}

	public void updateInvalidVpaResponse(Fields fields) {

		fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_VPA.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.INVALID_VPA.getResponseMessage());
		// fields.remove(FieldType.ACQUIRER_TYPE.getName());
	}

	public void updateValidVpaResponse(Fields fields) {

		fields.put(FieldType.STATUS.getName(), StatusType.SUCCESS.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.VALID_VPA.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.VALID_VPA.getResponseMessage());
		// fields.remove(FieldType.ACQUIRER_TYPE.getName());
	}

	private void handleTransactionCharges(Fields fields) throws SystemException {
		String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
		if (StringUtils.isBlank(paymentsRegion)) {
			paymentsRegion = AccountCurrencyRegion.DOMESTIC.toString();
		}

		if (StringUtils.isEmpty(fields.get(FieldType.AMOUNT.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED,
					FieldType.AMOUNT.getName() + " is a mandatory field.");
		}

		generalValidator.validateField(FieldType.AMOUNT, FieldType.AMOUNT.getName(), fields);

		String amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
				fields.get(FieldType.CURRENCY_CODE.getName()));
		String payId = fields.get(FieldType.PAY_ID.getName());
		String surchargeFlag = fields.get(FieldType.SURCHARGE_FLAG.getName()).toString();
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String gst = "";
		if (surchargeFlag.equals("Y")) {
			fields.put((FieldType.SURCHARGE_FLAG.getName()), surchargeFlag);
			// Add surcharge amount and total amount on the basis of payment type
			String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
			PaymentType paymentTypeS = PaymentType.getInstanceUsingCode(paymentType);
			switch (paymentTypeS) {
			case UPI:
				BigDecimal[] surUPAmount = calculateSurchargeAmount.fetchUPSurchargeDetails(amount, payId,
						paymentsRegion, gst);
				BigDecimal surchargeUPAmount = surUPAmount[1];
				String upiTotalAmount = surchargeUPAmount.toString();
				upiTotalAmount = Amount.formatAmount(upiTotalAmount, currencyCode);
				fields.put(FieldType.TOTAL_AMOUNT.getName(), upiTotalAmount);
				break;
			default:
				// unsupported payment type
				throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
			}
		} else {
			// TDR MODE
			fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
		}
	}

	private void saveInvalidTransaction(Fields fields, String origTxnType) {
		// TODO... validate and sanitize fields
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

		try {
			updateProcessor.preProcess(fields);
			updateProcessor.prepareInvalidTransactionForStorage(fields);
		} catch (SystemException systemException) {
			logger.error("Unable to save invalid transaction", systemException);
		} catch (Exception exception) {
			logger.error("Unhandaled error", exception);
			// Non reachable code for safety
		}
	}

}
