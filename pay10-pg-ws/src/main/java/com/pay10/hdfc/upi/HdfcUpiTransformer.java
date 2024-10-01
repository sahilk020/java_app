package com.pay10.hdfc.upi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;
import com.pay10.hdfc.upi.Transaction;

/**
 * @author Rahul
 *
 */
@Service("hdfcUpiTransformer")
public class HdfcUpiTransformer {
	private static Logger logger = LoggerFactory.getLogger(HdfcUpiTransformer.class.getName());

	private Transaction transaction = null;

	public HdfcUpiTransformer(Transaction transactionResponse) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields, Transaction transactionResponse) {

		String status = null;
		ErrorType errorType = null;

		status = getStatusType(transactionResponse);
		errorType = getResponse(transactionResponse);

		if (StatusType.FAILED.getName().equals(status)) {
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
			fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
		} else {
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (txnType.equals(TransactionType.STATUS.getName())) {
				fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.HDFC.getCode());
				fields.put(FieldType.PG_REF_NUM.getName(), transactionResponse.getTransactionId());
				fields.put(FieldType.STATUS.getName(), status);
				fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
				fields.put(FieldType.PG_RESP_CODE.getName(), transactionResponse.getResponse());
				fields.put(FieldType.PG_TXN_STATUS.getName(), transactionResponse.getStatus());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), transactionResponse.getResponseMessage());
				fields.put(FieldType.UDF1.getName(), transactionResponse.getMerchantVpa());
				fields.put(FieldType.ACQ_ID.getName(), transactionResponse.getPayeeApprovalNum());
				fields.put(FieldType.RRN.getName(), transactionResponse.getCustomerReference());
				fields.put(FieldType.PG_DATE_TIME.getName(), transactionResponse.getDateTime());
				fields.put(FieldType.AUTH_CODE.getName(), transactionResponse.getReferenceId());
				
			} else {
				fields.put(FieldType.STATUS.getName(), status);
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
				fields.put(FieldType.ACQ_ID.getName(), transactionResponse.getAcq_id());
				fields.put(FieldType.RRN.getName(), transactionResponse.getRrn());
				fields.put(FieldType.PG_RESP_CODE.getName(), transactionResponse.getResponse());
				fields.put(FieldType.PG_TXN_STATUS.getName(), transactionResponse.getStatus());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), transactionResponse.getResponseMessage());
				fields.put(FieldType.UDF1.getName(), transactionResponse.getMerchantVpa());
				fields.put(FieldType.PG_DATE_TIME.getName(), transactionResponse.getDateTime());
			}
		}
	}

	public ErrorType getResponse(Transaction transaction) {
		String result = transaction.getResponseMessage();
		ErrorType errorType = null;

		if (result.equals(Constants.SUCCESS_REPONSE)) {
			errorType = ErrorType.SUCCESS;
		} else {
			errorType = ErrorType.REJECTED;
		}
		return errorType;
	}

	public String getStatusType(Transaction transaction) {
		String result = transaction.getResponseMessage();
		String status = "";

		if (result.equals(Constants.SUCCESS_REPONSE)) {
			status = StatusType.CAPTURED.getName();
		} else {
			status = StatusType.FAILED.getName();
		}

		return status.toString();
	}
	
	public void updateResponsehdfc(Fields fields) {
		ErrorType errorType = null;
		String pgTxnMsg = null;
logger.info("fields data in hdfc"+fields.getFieldsAsString());
		logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}", "HDFC", fields.get(FieldType.HDFC_ACQUIRER_CODE.getName()));

		

		if(fields.get(FieldType.HDFC_DUAL_VERFICATION.getName()).equalsIgnoreCase("true")) {
			ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(fields.get(FieldType.HDFC_ACQUIRER_CODE.getName()), "HDFC");
			logger.info("Error code mapping  : " + errorMappingDTO);
			if (null != errorMappingDTO) {
				fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
				fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
				fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
				
			}else {
				logger.info("ErrorCodeMapping not fount for ATOM Acquirer");
			}
	}
	else {
		fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());

	}
		
	}
}
