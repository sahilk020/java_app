package com.pay10.icici.upi;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.icici.upi.Transaction;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

/**
 * @author Rahul
 *
 */
@Service("iciciUpiTransformer")
public class ICICIUpiTransformer {

	private Transaction transaction = null;

	public ICICIUpiTransformer(Transaction transactionResponse) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateSaleResponse(Fields fields, Transaction transaction) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getResponseCode())) && (StringUtils.isNotBlank(transaction.getResponseStatus()))
				&& ((transaction.getResponseCode()).equalsIgnoreCase("0"))
				&& ((transaction.getResponseStatus()).equalsIgnoreCase("SUCCESS")))

		{

			if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName())) {
				status = StatusType.SENT_TO_BANK.getName();
			}

			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else if ((StringUtils.isNotBlank(transaction.getResponseCode())) && (StringUtils.isNotBlank(transaction.getResponseStatus()))
				&& ((transaction.getResponseCode()).equalsIgnoreCase("0"))
				&& ((transaction.getResponseStatus()).equalsIgnoreCase("PENDING")))

		{

			if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName())) {
				status = StatusType.SENT_TO_BANK.getName();
			}

			errorType = ErrorType.PENDING;
			pgTxnMsg = ErrorType.PENDING.getResponseMessage();

		}


		else {
			if ((StringUtils.isNotBlank(transaction.getResponseCode()))) {

				ICICIUpiResultType resultInstance = ICICIUpiResultType.getInstanceFromName(transaction.getResponseCode());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					pgTxnMsg = "Transaction failed at acquirer";
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

		if (StringUtils.isNotBlank(transaction.getRrn())) {
			fields.put(FieldType.AUTH_CODE.getName(), transaction.getRrn());
		}

		else {
			fields.put(FieldType.AUTH_CODE.getName(), "0");
		}

		fields.put(FieldType.RRN.getName(), transaction.getRrn());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getRrn());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponseCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getResponseMessage());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

	public void updateRefundResponse(Fields fields, Transaction transaction) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getResponseCode())) && (StringUtils.isNotBlank(transaction.getResponseStatus()))
				&& ((transaction.getResponseCode()).equalsIgnoreCase("0"))
				&& ((transaction.getResponseStatus()).equalsIgnoreCase("SUCCESS")))

		{

			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else {
			if ((StringUtils.isNotBlank(transaction.getResponseCode()))) {

				ICICIUpiResultType resultInstance = ICICIUpiResultType.getInstanceFromName(transaction.getResponseCode());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					pgTxnMsg = "Transaction failed at acquirer";
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

		if (StringUtils.isNotBlank(transaction.getRrn())) {
			fields.put(FieldType.AUTH_CODE.getName(), transaction.getRrn());
		}

		else {
			fields.put(FieldType.AUTH_CODE.getName(), "0");
		}

		fields.put(FieldType.RRN.getName(), transaction.getRrn());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getRrn());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponseCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getResponseMessage());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

	public void updateInvalidVpaResponse(Fields fields, String response) {

		fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_VPA.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.INVALID_VPA.getResponseMessage());
		// fields.remove(FieldType.ACQUIRER_TYPE.getName());
	}

}
