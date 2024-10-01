package com.pay10.apbl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class APBLTransformer {

	private Transaction transaction = null;

	public APBLTransformer(Transaction transaction) {
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

		if ((StringUtils.isNotBlank(transaction.getCODE())) &&
				(StringUtils.isNotBlank(transaction.getSTATUS())) && 
				((transaction.getCODE()).equalsIgnoreCase("000")) &&
					((transaction.getSTATUS()).equalsIgnoreCase("SUC")) &&
					fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName()))


		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		
		else if ((StringUtils.isNotBlank(transaction.getErrorCode())) &&
				(StringUtils.isNotBlank(transaction.getSTATUS())) && 
				((transaction.getErrorCode()).equalsIgnoreCase("000")) &&
					((transaction.getSTATUS()).equalsIgnoreCase("SUC")) &&
					fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName()))


		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}
		
		else if ((StringUtils.isNotBlank(transaction.getErrorCode())) &&
				(StringUtils.isNotBlank(transaction.getSTATUS())) && 
				((transaction.getErrorCode()).equalsIgnoreCase("000")) &&
					((transaction.getSTATUS()).equalsIgnoreCase("SUC")) &&
					fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.STATUS.getName()))


		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}
		
		
		else {
			if ((StringUtils.isNotBlank(transaction.getCODE()))) {

				APBLResultType resultInstance = APBLResultType.getInstanceFromName(transaction.getCODE());
				
				if (resultInstance == null) {
					
					if (StringUtils.isNotBlank(transaction.getErrorCode())) {
						resultInstance = APBLResultType.getInstanceFromName(transaction.getErrorCode());
					}
				}

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					
					if (StringUtils.isNotBlank(transaction.getMSG())){
						pgTxnMsg = transaction.getMSG();
					}
					
					else if (StringUtils.isNotBlank(transaction.getMessageText())){
						pgTxnMsg = transaction.getMessageText();
					}
					
					else {
						pgTxnMsg = resultInstance.getMessage();
					}
					
				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.getInstanceFromCode("007");
					
					if (StringUtils.isNotBlank(transaction.getMSG())){
						pgTxnMsg = transaction.getMSG();
					}
					
					else if (StringUtils.isNotBlank(transaction.getMessageText())){
						pgTxnMsg = transaction.getMessageText();
					}
					
					else {
						pgTxnMsg = "Transaction Declined";
					}
					
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				if (StringUtils.isNotBlank(transaction.getMSG())){
					pgTxnMsg = transaction.getMSG();
				}
				else {
					pgTxnMsg = "Transaction Rejected";
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.RRN.getName(), transaction.getTRAN_ID());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTRAN_ID());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getCODE());
		
		if (StringUtils.isNotBlank(transaction.getMSG())) {
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getMSG());
		}
		else {
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
		}
		
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

}
