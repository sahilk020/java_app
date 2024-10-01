/**
 *
 */
package com.pay10.pg.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
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
public class UPIRestCallProcessor {

	private static Logger logger = LoggerFactory.getLogger(UPIRestCallProcessor.class.getName());

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private GeneralValidator generalValidator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private YesBankCbIntegrator yesBankCbIntegrator;


	@Autowired
	private SbiUpiIntegrator sbiUpiIntegrator;

	@Autowired
	private CosmosProcessor cosmosProcessor;

	@Autowired
	private SbiProcessor sbiProcessor;

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
	public Map<String, String> process(Fields fields, String processCall,HttpServletRequest request) throws SystemException {

		try{
			String payid = fields.get("PAY_ID");
			String encData = fields.get("ENCDATA");

			logger.info("PAY_ID --" + payid + "---encdata-----" + encData);
			Map<String, String> plainResponseMap = transactionControllerServiceProvider.decrypt(payid, encData);
			String decryptedString = plainResponseMap.get(FieldType.ENCDATA.getName());
			String[] fieldArr = decryptedString.split("~");
			Map<String, String> requestMap = new HashMap<String, String>();

			// Fields fields = null;
			for (String entry : fieldArr) {
				String[] namValuePair = entry.split("=", 2);
				if (namValuePair.length == 2) {
					requestMap.put(namValuePair[0], namValuePair[1]);
				} else {
					requestMap.put(namValuePair[0], "");
				}
			}
			fields = new Fields(requestMap);

			// validate request param....
			if (StringUtils.isBlank(fields.get(FieldType.HASH.getName()))) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + FieldType.HASH.getName());
			}
			// generalValidator.validateReturnUrl(fields);
			generalValidator.validateHash(fields);
			
			User user1= userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
			//commented for future release
			//String S2SFlag=fields.get(FieldType.IS_MERCHANT_S2S.getName());
			/*if(user1.isMerchantS2SFlag() && StringUtils.isNoneBlank(S2SFlag)&&S2SFlag.equalsIgnoreCase("Y")) {
				logger.info("S2S flag enabled....!!");
			}*/
			if(user1.isMerchantS2SFlag()) {
				logger.info("S2S flag enabled....!!");
			}
			else {
				throw new SystemException(ErrorType.MERCHANT_HOSTED_S2S, "S2S flag is not enabled");
			}

			logger.info("Internal_Cust_IP for X-Forwarded-For: " + request.getHeader("X-Forwarded-For"));
			logger.info("Internal_Cust_IP for Remote Addr: " + request.getRemoteAddr());
			String iprem=request.getRemoteAddr();
			String ip = request.getHeader("X-Forwarded-For");
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_IP.getName()))){
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), fields.get(FieldType.CUST_IP.getName()));
				fields.remove(FieldType.CUST_IP.getName());

			}else if(StringUtils.isNotBlank(ip) ){
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), ip);

			}else if (StringUtils.isNotBlank(iprem) ){
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), iprem);

			}

			// only for validate amount fields
			if (processCall.equals(Constants.UPI_S2S_VPA_VALID.getValue())) {
				fields.put(FieldType.AMOUNT.getName(), "1000");
			}
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

			fields.put(FieldType.TXNTYPE.getName(), TransactionType.ENROLL.getName());
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			// Put transaction id as original txnId for subsequent transactions
			fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			fields.remove(FieldType.ORIG_TXN_ID.getName());
			fields.logAllFields("check vpa logs ....");
			// handle surcharge flg and amount
			//handleTransactionCharges(fields);

			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			// put Internal fields back
			fields.put(internalFields);
			fields.logAllFieldsUsingMasking("check vpa  Refine Request:");


			logger.info("logger for ip in S2s call" + fields.getFieldsAsString());
			if (StringUtils.isNotBlank(fields.get(FieldType.PAYER_ADDRESS.getName()))) {
				fields.put(FieldType.CARD_MASK.getName(),
						fields.get(FieldType.PAYER_ADDRESS.getName()));
			}
			logger.info("logger for ip in S2s call" + fields.getFieldsAsString());

			// to check vpa address
			if (processCall.equals(Constants.UPI_S2S_VPA_VALID.getValue())) {
				getValidVpa(fields);
			} else if (processCall.equals(Constants.UPI_S2S_COLLECT_PAY.getValue())) {
				// to do collect pay
				getCollectPay(fields);
			}
			// Generate response for user
			createResponse(fields, processCall);

			return fields.getFields();

		}catch(SystemException systemException){

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
			if (fields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.VALIDATION_FAILED.getCode())) {
				fields.put(FieldType.TXNTYPE.getName(), origTxnType);
				createResponse(fields, processCall);
				return fields.getFields();
			}

			saveInvalidTransaction(fields, origTxnType);

			fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			createResponse(fields, processCall);
			return fields.getFields();
		}
	}

	public void getCollectPay(Fields fields) throws SystemException {
//		Map<String, String> responseMap = router.route(fields);
//		fields = new Fields(responseMap);
//		fields.get(FieldType.ACQUIRER_TYPE.getName());
//		logger.info("acquirer name routes : " + acquirer);

		// Process security
		ProcessManager.flow(securityProcessor, fields, false);

		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		logger.info("acquirer name : " + acquirer);

		// fraud Prevention processor
		ProcessManager.flow(fraudProcessor, fields, false);

		if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKCB.getCode())) {
			// Process transaction with YESBANKCB
			ProcessManager.flow(cSourceProcessor, fields, false);

			ProcessManager.flow(updateProcessor, fields, true);

		}else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.COSMOS.getCode())) {
			// Process transaction with COSMOS
			ProcessManager.flow(cosmosProcessor, fields, false);

			ProcessManager.flow(updateProcessor, fields, true);

		}else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBI.getCode())) {
			// Process transaction with COSMOS
			ProcessManager.flow(sbiProcessor, fields, false);

			ProcessManager.flow(updateProcessor, fields, true);

		}

		else {
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.ACQUIRER_NOT_FOUND.getResponseCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.ACQUIRER_NOT_FOUND.getResponseMessage());
		}
	}

	public void getStatus(Fields fields) throws SystemException {

	}

	public void getValidVpa(Fields fields) throws SystemException {

		// Process security
		ProcessManager.flow(securityProcessor, fields, false);

		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		logger.info("acquirer name : " + acquirer);
		logger.info("ROUTER_CONFIGURATION_ID : " + fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName()));
		
		RouterConfiguration routerConfiguration = routerConfigurationDao.findRule(Long.valueOf(fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName())));
		logger.info("routerConfiguration "+routerConfiguration.toString());
		int vpaFailedCnt = null != routerConfiguration.getVpaCount() ? Integer.valueOf(routerConfiguration.getVpaCount()) : 1;
		for (int i = 1; i<=vpaFailedCnt; i++) {
			if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKCB.getCode())) {
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				try {
					String vpaStatus = yesBankCbIntegrator.vpaValidation(fields);
					if (vpaStatus.equalsIgnoreCase("VE")) {
						updateValidVpaResponse(fields);
					} else {
						updateInvalidVpaResponse(fields);
					}
					break;
				} catch (SystemException systemException) {
					// TODO: handle exception
					logger.info("VPA Validation SystemException for YESBANKCB, vpaTryCnt= "+ i +", vpaFailedCnt="+vpaFailedCnt +" SystemException=" + systemException);
					logger.info("VPA Validation SystemException for YESBANKCB, fields:" + fields.getFieldsAsString());
					if(i == vpaFailedCnt) {
						//TODO Down acquirer.
						routerConfigurationDao.disableRCForVPAFailedCountExceededById(Long.valueOf(fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName())));
						updateInvalidVpaResponse(fields);
					}
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
					break;
				} catch (SystemException systemException) {
					logger.info("VPA Validation SystemException for COSMOS, vpaTryCnt= "+ i +", vpaFailedCnt="+vpaFailedCnt +" SystemException=" + systemException);
					logger.info("VPA Validation SystemException for COSMOS, fields:" + fields.getFieldsAsString());
					if(i == vpaFailedCnt) {
						//TODO Down acquirer.
						routerConfigurationDao.disableRCForVPAFailedCountExceededById(Long.valueOf(fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName())));
						updateInvalidVpaResponse(fields);
					}
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
					break;
				} catch (SystemException systemException) {
					logger.info("VPA Validation SystemException for SBI, vpaTryCnt= "+ i +", vpaFailedCnt="+vpaFailedCnt +" SystemException=" + systemException);
					logger.info("VPA Validation SystemException for SBI, fields:" + fields.getFieldsAsString());
					if(i == vpaFailedCnt) {
						//TODO Down acquirer.
						routerConfigurationDao.disableRCForVPAFailedCountExceededById(Long.valueOf(fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName())));
						updateInvalidVpaResponse(fields);
					}
				}

			} else {
				updateInvalidVpaResponse(fields);
				break;
			}
		}
		
		logger.info("########## Completed getValidVpa ###############");
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
		}else if((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.COSMOS.getCode())){
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			boolean vpaStatus = cosmosProcessor.vpaValidation(fields);
			if (vpaStatus) {

				updateValidVpaResponse(fields);
			} else {
				updateInvalidVpaResponse(fields);
			}

		}else if((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBI.getCode())){
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			boolean vpaStatus = sbiUpiIntegrator.vpaValidation(fields);
			if (vpaStatus) {

				updateValidVpaResponse(fields);
			} else {
				updateInvalidVpaResponse(fields);
			}

		}else {
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
