package com.pay10.paytm;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class PaytmTransformer {

	private Transaction transaction = null;

	public PaytmTransformer(Transaction transaction) {
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

		if ((StringUtils.isNotBlank(transaction.getRESPCODE())) &&
				((transaction.getRESPCODE()).equalsIgnoreCase("01")) &&
					fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName()))


		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			
			if (StringUtils.isNotBlank(transaction.getRESPMSG())){
				pgTxnMsg = transaction.getRESPMSG();
			}
			
			else {
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			}
			

		}
		
		
		else {
			if ((StringUtils.isNotBlank(transaction.getRESPCODE()))) {

				String respCode = null;
				if (StringUtils.isNotBlank(transaction.getRESPCODE())){
					// For Sale
					respCode = transaction.getRESPCODE();
				}
				
				PaytmResultType resultInstance = PaytmResultType.getInstanceFromName(respCode);
				
				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					
					if (StringUtils.isNotBlank(transaction.getRESPMSG())){
						pgTxnMsg = transaction.getRESPMSG();
					}
					
					else {
						pgTxnMsg = resultInstance.getMessage();
					}
					
				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.getInstanceFromCode("007");
					
					if (StringUtils.isNotBlank(transaction.getRESPMSG())){
						pgTxnMsg = transaction.getRESPMSG();
					}
					
					else {
						pgTxnMsg = "Transaction Declined";
					}
					
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				if (StringUtils.isNotBlank(transaction.getRESPMSG())){
					pgTxnMsg = transaction.getRESPMSG();
				}
				else {
					pgTxnMsg = "Transaction Rejected";
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.RRN.getName(), transaction.getBANKTXNID());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTXNID());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getRESPCODE());
		
		if (StringUtils.isNotBlank(transaction.getResultStatus())) {
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getSTATUS());
		}
		else {
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
		}
		
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}
	
	
	public void updateRefundResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		// For Refund 
		if ((StringUtils.isNotBlank(transaction.getResultCode())) &&
				((transaction.getResultCode()).equalsIgnoreCase("601") || (transaction.getResultCode()).equalsIgnoreCase("10")) &&
					fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName()))


		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		// For Sale
		else if ((StringUtils.isNotBlank(transaction.getResultCode())) &&
				((transaction.getRESPCODE()).equalsIgnoreCase("01")) &&
					fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName()))


		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}
		
		
		else {
			if ((StringUtils.isNotBlank(transaction.getRESPCODE())) || (StringUtils.isNotBlank(transaction.getResultCode()))) {

				String respCode = null;
				if (StringUtils.isNotBlank(transaction.getRESPCODE())){
					
					// For Sale
					respCode = transaction.getRESPCODE();
				}
				else {
					
					// For Refund
					respCode = transaction.getResultCode();
				}
				
				PaytmResultType resultInstance = PaytmResultType.getInstanceFromName(respCode);
				
				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					
					if (StringUtils.isNotBlank(transaction.getResultMsg())){
						pgTxnMsg = transaction.getResultMsg();
					}
					
					else {
						pgTxnMsg = resultInstance.getMessage();
					}
					
				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.getInstanceFromCode("007");
					
					if (StringUtils.isNotBlank(transaction.getResultMsg())){
						pgTxnMsg = transaction.getResultMsg();
					}
					
					else {
						pgTxnMsg = "Transaction Declined";
					}
					
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				if (StringUtils.isNotBlank(transaction.getResultMsg())){
					pgTxnMsg = transaction.getResultMsg();
				}
				else {
					pgTxnMsg = "Transaction Rejected";
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.RRN.getName(), transaction.getRefundId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResultCode());
		
		if (StringUtils.isNotBlank(transaction.getResultStatus())) {
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getResultStatus());
		}
		else {
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
		}
		
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

}
