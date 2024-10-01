package com.pay10.freecharge;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.util.FreeChargeResultType;

@Service
public class FreeChargeTransformer {

	private Transaction transaction = null;

	public FreeChargeTransformer(Transaction transaction) {
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
		if ((StringUtils.isNotBlank(transaction.getStatus()))
				&& ((transaction.getStatus()).equalsIgnoreCase("SUCCESS"))) {
			status = StatusType.SENT_TO_BANK.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
		} else {
			if (StringUtils.isNotBlank(transaction.getStatus())) {
				FreeChargeResultType resultInstance = FreeChargeResultType
						.getInstanceFromName(transaction.getErrorCode());

				if (resultInstance == null) {

					if (StringUtils.isNotBlank(transaction.getErrorCode())) {

						if (transaction.getErrorCode().length() >= 4) {
							String errorCodeTag = transaction.getErrorCode().substring(0, 4);
							resultInstance = FreeChargeResultType.getInstanceFrombankCode(errorCodeTag);
						}
					}
				}

				if (resultInstance != null) {
					status = StatusType.FAILED.toString();
					errorType = ErrorType.FAILED;
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.DECLINED.getName();
					errorType = ErrorType.getInstanceFromCode("004");
					pgTxnMsg = "Transaction Declined by acquirer";
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
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getErrorCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

	public void updateStatusResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;
		if ((StringUtils.isNotBlank(transaction.getStatus()))
				&& ((transaction.getStatus()).equalsIgnoreCase("SUCCESS"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
		} else if ((StringUtils.isNotBlank(transaction.getStatus()))
				&& ((transaction.getStatus()).equalsIgnoreCase("INITIATED"))) {
			status = StatusType.PROCESSING.getName();
			errorType = ErrorType.PROCESSING;
			pgTxnMsg = ErrorType.PROCESSING.getResponseMessage();
		} else {
			if (StringUtils.isNotBlank(transaction.getStatus())) {
				FreeChargeResultType resultInstance = FreeChargeResultType
						.getInstanceFromName(transaction.getErrorCode());

				if (resultInstance == null) {

					if (StringUtils.isNotBlank(transaction.getErrorCode())) {

						if (transaction.getErrorCode().length() >= 4) {
							String errorCodeTag = transaction.getErrorCode().substring(0, 4);
							resultInstance = FreeChargeResultType.getInstanceFrombankCode(errorCodeTag);
						}
					}
				}

				if (resultInstance != null) {
					status = StatusType.FAILED.toString();
					errorType = ErrorType.FAILED;
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.DECLINED.getName();
					errorType = ErrorType.getInstanceFromCode("004");
					pgTxnMsg = "Transaction Declined by acquirer";
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
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getErrorCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

	public void updateRefundResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;
		if ((StringUtils.isNotBlank(transaction.getStatus()))
				&& ((transaction.getStatus()).equalsIgnoreCase("Initiated"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
		} else if ((StringUtils.isNotBlank(transaction.getStatus()))
				&& ((transaction.getStatus()).equalsIgnoreCase("SUCCESS"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
		} else {
			if (StringUtils.isNotBlank(transaction.getStatus())) {
				FreeChargeResultType resultInstance = FreeChargeResultType
						.getInstanceFromName(transaction.getErrorCode());

				if (resultInstance == null) {

					if (StringUtils.isNotBlank(transaction.getErrorCode())) {

						if (transaction.getErrorCode().length() >= 4) {
							String errorCodeTag = transaction.getErrorCode().substring(0, 4);
							resultInstance = FreeChargeResultType.getInstanceFrombankCode(errorCodeTag);
						}
					}
				}

				if (resultInstance != null) {
					status = StatusType.FAILED.toString();
					errorType = ErrorType.FAILED;
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.DECLINED.getName();
					errorType = ErrorType.getInstanceFromCode("004");
					pgTxnMsg = "Transaction Declined by acquirer";
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
		fields.put(FieldType.ACQ_ID.getName(), transaction.getRefundTxnId());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}
}
