package com.pay10.ingenico;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class IngenicoTransformer {

	private Transaction transaction = null;

	public IngenicoTransformer(Transaction transaction) {
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

		if ((StringUtils.isNotBlank(transaction.getTxn_status())) && ((transaction.getTxn_status()).equalsIgnoreCase("0300")))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			
			if (StringUtils.isNotBlank(transaction.getTxn_msg())) {
				pgTxnMsg = transaction.getTxn_msg();
			}
			else {
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			}

		}

		
		else {
			if ((StringUtils.isNotBlank(transaction.getTxn_status()))) {

				IngenicoResultType resultInstance = IngenicoResultType.getInstanceFromName(transaction.getTxn_status());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					
					if (StringUtils.isNotBlank(transaction.getTxn_err_msg())) {
						pgTxnMsg = transaction.getTxn_err_msg();
					}
					else {
						pgTxnMsg = "Transaction failed at acquirer";
					}
					
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		if (StringUtils.isNotBlank(transaction.getRqst_token())) {
			fields.put(FieldType.AUTH_CODE.getName(), transaction.getRqst_token());
		}

		else {
			fields.put(FieldType.AUTH_CODE.getName(), "0");
		}

		fields.put(FieldType.RRN.getName(), transaction.getTpsl_txn_id());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTpsl_txn_id());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getTxn_status());
		
		if (StringUtils.isNotBlank(transaction.getTxn_msg())) {
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getTxn_msg());
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

		if ((StringUtils.isNotBlank(transaction.getTxn_status())) && ((transaction.getTxn_status()).equalsIgnoreCase("0400")))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			
			if (StringUtils.isNotBlank(transaction.getTxn_msg())) {
				pgTxnMsg = transaction.getTxn_msg();
			}
			else {
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			}

		}
		
		else {
			if ((StringUtils.isNotBlank(transaction.getTxn_status()))) {

				IngenicoResultType resultInstance = IngenicoResultType.getInstanceFromName(transaction.getTxn_status());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					
					if (StringUtils.isNotBlank(transaction.getTxn_err_msg())) {
						pgTxnMsg = transaction.getTxn_err_msg();
					}
					else {
						pgTxnMsg = "Transaction failed at acquirer";
					}
					
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				
				if (StringUtils.isNotBlank(transaction.getTxn_err_msg())) {
					pgTxnMsg = transaction.getTxn_err_msg();
				}
				else {
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		if (StringUtils.isNotBlank(transaction.getRqst_token())) {
			fields.put(FieldType.AUTH_CODE.getName(), transaction.getRqst_token());
		}

		else {
			fields.put(FieldType.AUTH_CODE.getName(), "0");
		}

		fields.put(FieldType.RRN.getName(), transaction.getTpsl_rfnd_id());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTpsl_txn_id());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getTxn_status());
		
		if (StringUtils.isNotBlank(transaction.getTxn_msg())) {
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getTxn_msg());
		}
		else {
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
		}
		
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

}
