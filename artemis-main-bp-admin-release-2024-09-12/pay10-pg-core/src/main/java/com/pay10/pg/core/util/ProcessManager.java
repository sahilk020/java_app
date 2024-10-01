package com.pay10.pg.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.StatusTypePayout;
import com.pay10.commons.util.TransactionType;

public class ProcessManager {

	private static Logger logger = LoggerFactory.getLogger(ProcessManager.class.getName());

	public static void flow(Processor processor, Fields fields,
			boolean invalidAllowed) {

		// Process security
		try {
			if (invalidAllowed) {
				processor.preProcess(fields);
				processor.process(fields);
				processor.postProcess(fields);
			} else {
				if (!fields.isValid()) {
					return;
				}
				processor.preProcess(fields);

				if (!fields.isValid()) {
					return;
				}
				processor.process(fields);

				if (!fields.isValid()) {
					return;
				}
				processor.postProcess(fields);
			}
		} catch (SystemException systemException) {
			fields.setValid(false);
			 logger.error("inside the processmanager in systemExcption in catch block  :" + systemException);
			String origTxnType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
			String previousOrigTxnId = fields.get(FieldType.ORIG_TXN_ID.getName());
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.VALIDATION_FAILED.getResponseCode())) {  
				String orderId = fields.get(FieldType.ORDER_ID.getName());
				if(StringUtils.isBlank(orderId) || !(new CrmValidator().validateField(CrmFieldType.ORDER_ID, orderId))){
					//Put order id as 000 if its invalid
					fields.put(FieldType.ORDER_ID.getName(), ErrorType.SUCCESS.getCode());
				}
				if(!StringUtils.isBlank(origTxnType)){
					fields.put(FieldType.TXNTYPE.getName(),origTxnType);
				}else{
					fields.put(FieldType.TXNTYPE.getName(),TransactionType.INVALID.getName());
				}
				if(!StringUtils.isBlank(fields.get(FieldType.INTERNAL_TXN_CHANNEL.getName()))){
					fields.put(FieldType.INTERNAL_TXN_CHANNEL.getName(),fields.get(FieldType.INTERNAL_TXN_CHANNEL.getName()));
				}else{
					fields.put(FieldType.INTERNAL_TXN_CHANNEL.getName(),TransactionType.INVALID.getName());
				}
				if(!StringUtils.isBlank(systemException.getMessage())){
					fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getMessage());
				}
			}

			if(fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.ENROLL.getName()) && !StringUtils.isBlank(origTxnType)){
				fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			}
			if(fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName()) && !StringUtils.isBlank(origTxnType)){
				fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			}
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (!txnType.equals(TransactionType.REFUND.getName())) {
				fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			}
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			
			fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());

			//TODO....review message requirement
			logger.info("System Exception throw Process Manager "+systemException.getErrorType().getResponseCode());
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.DENIED_BY_FRAUD.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), fields.get(Constants.PG_FRAUD_TYPE.getValue()));
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.ACQUIRER_NOT_FOUND.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.PG_ACQUIRER_ERROR.getValue());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				 logger.error("inside the processmanager in systemExcption in catch block in acquirer not found  :" + systemException);
			}
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.ACUIRER_DOWN.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.PG_ACQUIRER_ERROR.getValue());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				 logger.error("inside the processmanager in systemExcption in catch block in ACUIRER_DOWN :" + systemException);
			}
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.INTERNAL_SYSTEM_ERROR.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.PG_ACQUIRER_ERROR.getValue());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.REFUND_AMOUNT_MISMATCH.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.REFUND_DENIED.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.REFUND.getName());
				fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
				fields.put(FieldType.ORIG_TXN_ID.getName(), previousOrigTxnId);
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.REFUND_REJECTED.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.REFUND.getName());
				fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
				fields.put(FieldType.ORIG_TXN_ID.getName(), previousOrigTxnId);
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.REFUND_FLAG_AMOUNT_NOT_MATCH.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.REFUND.getName());
				fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
				fields.put(FieldType.ORIG_TXN_ID.getName(), previousOrigTxnId);
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.TRANSACTION_NOT_FOUND.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());

				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.REFUND.getName());
				fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.NO_SUCH_TRANSACTION.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				//fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.REFUND.getName());
				fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());

				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.DUPLICATE_ORDER_ID.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());

				fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
				fields.put(FieldType.ORIG_TXN_ID.getName(), previousOrigTxnId);
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.DUPLICATE_REFUND_ORDER_ID.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());

				fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.REFUND.getName());
				fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
				fields.put(FieldType.ORIG_TXN_ID.getName(), previousOrigTxnId);
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.DUPLICATE.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.STATUS.getName(), StatusType.DUPLICATE.getName());
				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
				fields.put(FieldType.ORIG_TXN_ID.getName(), previousOrigTxnId);
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.DUPLICATE_SUBMISSION.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());

				fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
				fields.put(FieldType.ORIG_TXN_ID.getName(), previousOrigTxnId);
			}
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.TDR_NOT_SET_FOR_THIS_AMOUNT.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.TDR_NOT_SET_FOR_THIS_AMOUNT.getValue());				 logger.error("inside the processmanager in systemExcption in catch block in TDR_NOT_SET_FOR_THIS_AMOUNT For This Acquirer  :" + systemException);
			}
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.MERCHANT_HOSTED_S2S.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.MERCHANT_HOSTED_S2S.getValue());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				 logger.error("inside the processmanager in systemException in catch block in S2S Flag Exception  :" + systemException);
			}

			if(systemException.getErrorType().getResponseCode().equals(ErrorType.EKYC_VERIFICATION.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_VERIFICATION.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), Constants.EKYC_VERIFICATION.getValue());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.EKYC_VERIFICATION.getValue());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				 logger.error("inside the processmanager in systemException in catch block in EKYC Exception  :" + systemException);
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.MERCHANT_HOSTED_S2S.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.MERCHANT_HOSTED_S2S.getValue());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				 logger.error("inside the processmanager in systemException in catch block in S2S Flag Exception  :" + systemException);
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.EKYC_DETAILS_NOT_FOUND.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_DETAILS_NOT_FOUND.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), Constants.EKYC_DETAILS_NOT_FOUND.getValue());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.EKYC_DETAILS_NOT_FOUND.getValue());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				 logger.error("inside the processmanager in systemExcption in catch block in EKYC Exception  :" + systemException);
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.EKYC_SERVICE_DOWN.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_SERVICE_DOWN.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), Constants.EKYC_SERVICE_DOWN.getValue());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.EKYC_SERVICE_DOWN.getValue());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				 logger.error("inside the processmanager in systemExcption in catch block in EKYC Exception  :" + systemException);
			}
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.EKYC_DETAILS_NOT_FOUND.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_DETAILS_NOT_FOUND.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), Constants.EKYC_DETAILS_NOT_FOUND.getValue());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.EKYC_DETAILS_NOT_FOUND.getValue());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				 logger.error("inside the processmanager in systemExcption in catch block in EKYC Exception  :" + systemException);
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.EKYC_SERVICE_DOWN.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_SERVICE_DOWN.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), Constants.EKYC_SERVICE_DOWN.getValue());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), Constants.EKYC_SERVICE_DOWN.getValue());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				 logger.error("inside the processmanager in systemExcption in catch block in EKYC Exception  :" + systemException);
			}
			
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.PO_FRM.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM.getInternalMessage());
				fields.put(FieldType.STATUS.getName(), StatusTypePayout.FAIL.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.PO_FRM_MIN_TICKET_SIZE.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MIN_TICKET_SIZE.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_MIN_TICKET_SIZE.getInternalMessage());
				fields.put(FieldType.STATUS.getName(), StatusTypePayout.FAIL.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.PO_FRM_MAX_TICKET_SIZE.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MAX_TICKET_SIZE.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_MAX_TICKET_SIZE.getInternalMessage());
				fields.put(FieldType.STATUS.getName(), StatusTypePayout.FAIL.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.PO_FRM_DAILY_LIMIT.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_DAILY_LIMIT.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_DAILY_LIMIT.getInternalMessage());
				fields.put(FieldType.STATUS.getName(), StatusTypePayout.FAIL.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.PO_FRM_DAILY_VOLUME.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_DAILY_VOLUME.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_DAILY_VOLUME.getInternalMessage());
				fields.put(FieldType.STATUS.getName(), StatusTypePayout.FAIL.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.PO_FRM_WEEKLY_LIMIT.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_WEEKLY_LIMIT.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_WEEKLY_LIMIT.getInternalMessage());
				fields.put(FieldType.STATUS.getName(),StatusTypePayout.FAIL.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.PO_FRM_WEEKLY_VOLUME.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_WEEKLY_VOLUME.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_WEEKLY_VOLUME.getInternalMessage());
				fields.put(FieldType.STATUS.getName(), StatusTypePayout.FAIL.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.PO_FRM_MONTHLY_LIMIT.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MONTHLY_LIMIT.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_MONTHLY_LIMIT.getInternalMessage());
				fields.put(FieldType.STATUS.getName(), StatusTypePayout.FAIL.getName());
			}
			
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.PO_FRM_MONTHLY_VOLUME.getResponseCode())){
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MONTHLY_VOLUME.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_MONTHLY_VOLUME.getInternalMessage());
				fields.put(FieldType.STATUS.getName(), StatusTypePayout.FAIL.getName());
			}

			if(systemException.getErrorType().getResponseCode().equals(ErrorType.INSUFFICIENT_BALANCE.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
				fields.put(FieldType.STATUS.getName(), StatusTypePayout.FAIL.getName());
				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			}
			if(systemException.getErrorType().getResponseCode().equals(ErrorType.NOT_APPROVED_FROM_ACQUIRER.getResponseCode())){
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
				fields.put(FieldType.STATUS.getName(), StatusTypePayout.FAIL.getName());
				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			}

			logger.error("SystemException", systemException);

		} catch (Exception exception) {
			fields.setValid(false);

 			fields.put(FieldType.RESPONSE_CODE.getName(),
					ErrorType.UNKNOWN.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),
					ErrorType.UNKNOWN.getResponseMessage());
			fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
			logger.error("Exception", exception);
		}
	}
}
