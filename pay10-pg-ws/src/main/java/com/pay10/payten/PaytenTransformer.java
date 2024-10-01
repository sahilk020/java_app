package com.pay10.payten;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;


@Service
public class PaytenTransformer {

	private static Logger logger = LoggerFactory.getLogger(PaytenTransformer.class.getName());
	
	private Transaction transaction = null;

	public PaytenTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getResponseCode())) && (StringUtils.isNotBlank(transaction.getResponseCode()))
				&& ((transaction.getStatus()).equalsIgnoreCase("Captured")) && ((transaction.getResponseCode()).equalsIgnoreCase("000"))
				&& (transaction.getOrderId().equalsIgnoreCase(fields.get(FieldType.ORDER_ID.getName())))) {
			
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
		} 

		else {
			if ((StringUtils.isNotBlank(transaction.getResponseCode()))) {

				String respCode = null;
				if (StringUtils.isNotBlank(transaction.getResponseCode())){
					respCode = transaction.getResponseCode();
				}
				
				PaytenResultType resultInstance = PaytenResultType.getInstanceFromName(respCode);
				
				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
					
				} else {
					status = StatusType.FAILED.getName();
					errorType = ErrorType.FAILED;
					pgTxnMsg = errorType.getResponseMessage();
					
				}

			} else {
				status = StatusType.FAILED.getName();
				errorType = ErrorType.FAILED;
				pgTxnMsg = errorType.getResponseMessage();
			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.RRN.getName(), transaction.getAcqId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getAcqId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponseCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}


	public void updateRefundResponse(Fields fields) {

		String status = null;
		String errorType = null;
		String pgTxnMsg = null;
		String code = null;

		if ((StringUtils.isNotBlank(transaction.getResponseCode())) && (StringUtils.isNotBlank(transaction.getResponseCode())) &&
				((transaction.getStatus()).equalsIgnoreCase("REFUND_INITIATED")) && ((transaction.getResponseCode()).equalsIgnoreCase("000")))

		{
			status = "REFUND_INITIATED";
			errorType = ErrorType.SUCCESS.getResponseMessage();
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			code = ErrorType.SUCCESS.getCode();
		}


		else {
			if ((StringUtils.isNotBlank(transaction.getResponseCode()))) {

				String respCode = null;
				if (StringUtils.isNotBlank(transaction.getResponseCode())){
					respCode = transaction.getResponseCode();
					code = transaction.getResponseCode();
				}

				PaytenRefundResultType resultInstance = PaytenRefundResultType.getInstanceFromName(respCode);

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = resultInstance.getMessage();
					pgTxnMsg = resultInstance.getMessage();
					code = resultInstance.getBankCode();

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.getInstanceFromCode("007").getResponseMessage();
					code = "007";
					pgTxnMsg = "Transaction Declined";

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED.getResponseMessage();
				pgTxnMsg = "Transaction Rejected";
				code = ErrorType.REJECTED.getCode();
			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType);
		fields.put(FieldType.RESPONSE_CODE.getName(), code);

		fields.put(FieldType.RRN.getName(), transaction.getRrn());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getAcqId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponseCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), code);
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
	}
	
	
	public void updateStatusResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

	   if ((StringUtils.isNotBlank(transaction.getResponseCode())) && (StringUtils.isNotBlank(transaction.getResponseCode())) &&
				((transaction.getStatus()).equalsIgnoreCase("Captured")) && ((transaction.getResponseCode()).equalsIgnoreCase("000")))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
		}
		
		
		else {
			if ((StringUtils.isNotBlank(transaction.getResponseCode()))) {

				String respCode = null;
				if (StringUtils.isNotBlank(transaction.getResponseCode())){
					respCode = transaction.getResponseCode();
				}
				
				PaytenResultType resultInstance = PaytenResultType.getInstanceFromName(respCode);
				
				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
					
				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.getInstanceFromCode("007");
					pgTxnMsg = "Transaction Declined";
					
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
					pgTxnMsg = "Transaction Rejected";
			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.RRN.getName(), transaction.getRrn());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getAcqId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponseCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
	}

}
