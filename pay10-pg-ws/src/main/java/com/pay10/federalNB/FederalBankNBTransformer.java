package com.pay10.federalNB;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.federalNB.FederalBankNBResultType;
import com.pay10.agreepay.AgreepayResultType;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class FederalBankNBTransformer {

	private Transaction transaction = null;

	public FederalBankNBTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {

		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;
		if ((StringUtils.isNotBlank(transaction.getResponse_code())) && ((transaction.getResponse_code()).equals("0"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else {
			if (StringUtils.isNotBlank(transaction.getResponse_code())) {

				FederalBankNBResultType resultInstance  = FederalBankNBResultType.getInstanceFromName(transaction.getResponse_code());
				if ((resultInstance != null)) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

					if (StringUtils.isNotBlank(transaction.getResponse_message())) {
						pgTxnMsg = transaction.getResponse_message();
					} else {
						pgTxnMsg = resultInstance.getMessage();
					}

				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");

					if (StringUtils.isNotBlank(transaction.getResponse_message())) {
						pgTxnMsg = transaction.getResponse_message();
					} else {
						pgTxnMsg = "Transaction failed at acquirer";
					}

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;

				if (StringUtils.isNotBlank(transaction.getResponse_message())) {
					pgTxnMsg = transaction.getResponse_message();
				} else {
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getResponse_message());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getTransaction_id());

		fields.put(FieldType.RRN.getName(), transaction.getTransaction_id());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTransaction_id());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponse_code());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getResponse_code());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

	public void updateRefundResponse(Fields fields) {

		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getCode()))
						&& ((transaction.getCode()).equalsIgnoreCase("0"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else {
			if (StringUtils.isNotBlank(transaction.getCode())) {

				AgreepayResultType resultInstance = AgreepayResultType
						.getInstanceFromName(transaction.getCode());
				if ((resultInstance != null)) {
					status = resultInstance.getStatusName();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {

					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					pgTxnMsg = "Transaction failed at acquirer";

					if (StringUtils.isNoneBlank(transaction.getMessage())) {
						pgTxnMsg = transaction.getMessage();
					}
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				if (StringUtils.isNoneBlank(transaction.getMessage())) {
					pgTxnMsg = transaction.getMessage();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getResponse_message());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getRefund_id());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getRefund_id());

		if (StringUtils.isNoneBlank(transaction.getCode())) {
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getCode());
		} else {
			errorType.getResponseCode();
		}

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getRefund_id());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getCode());
	}

}

