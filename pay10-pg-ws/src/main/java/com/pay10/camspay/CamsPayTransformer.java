package com.pay10.camspay;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.camspay.util.Constants;

@Service
public class CamsPayTransformer {

	private Transaction transaction = null;

	public CamsPayTransformer(Transaction transaction) {
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
		if ((StringUtils.isNotBlank(transaction.getResponseCode()))
				&& ((transaction.getResponseCode()).equalsIgnoreCase(CamsPayResultType.RC111.getBankCode()))
				&& fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
				pgTxnMsg = transaction.getResponseMsg();
			} else {
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			}
		} else {
			if ((StringUtils.isNotBlank(transaction.getResponseCode()))) {
				CamsPayResultType resultInstance = CamsPayResultType.getInstanceFromName(transaction.getResponseCode());
				if (resultInstance != null) {
					status = resultInstance.getStatus();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getCode());
					pgTxnMsg = StringUtils.isNotBlank(transaction.getResponseMsg()) ? transaction.getResponseMsg()
							: resultInstance.getMessage();
				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode(Constants.ERROR022);
					pgTxnMsg = StringUtils.isNotBlank(transaction.getResponseMsg()) ? transaction.getResponseMsg()
							: ErrorType.FAILED.toString();
				}
			} else {
				status = StatusType.FAILED_AT_ACQUIRER.getName();
				errorType = ErrorType.FAILED;
				pgTxnMsg = StringUtils.isNotBlank(transaction.getResponseMsg()) ? transaction.getResponseMsg()
						: ErrorType.FAILED.toString();
			}
		}
		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
		fields.put(FieldType.RRN.getName(), transaction.getBankRefNum());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponseCode());
		String txnStatus = StringUtils.isNotBlank(transaction.getStatus()) ? transaction.getStatus()
				: errorType.getResponseCode();
		fields.put(FieldType.PG_TXN_STATUS.getName(), txnStatus);
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}
}
