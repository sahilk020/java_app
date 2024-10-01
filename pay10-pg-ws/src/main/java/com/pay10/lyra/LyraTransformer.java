package com.pay10.lyra;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class LyraTransformer {

	private Transaction transaction = null;
	public LyraTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;
		if (txnType.equals(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			status = getStatusType();
			errorType = getResponse();
		} else if (txnType.equals(TransactionType.REFUND.getName())) {
			status = getRefundStatusType();
			errorType = getResponse();

		} else {

			if ((StringUtils.isNotBlank(transaction.getStatus()))
					&& ((transaction.getStatus()).equalsIgnoreCase("PAID"))) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			} else {
				if (StringUtils.isNotBlank(transaction.getStatus())) {
					LyraResultType resultInstance = LyraResultType.getInstanceFromName(transaction.getErrorCode());

					if (resultInstance != null) {
						status = resultInstance.getStatusCode();
						errorType = ErrorType.getInstanceFromCode(resultInstance.getPGCode());
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

		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), transaction.getErrorMessage());
		fields.put(FieldType.ACS_URL.getName(), transaction.getAcsUrl());
		fields.put(FieldType.PAREQ.getName(), transaction.getPareq());
		fields.put(FieldType.LYRA_FINAL_REQUEST.getName(), transaction.getSubmitUrl());
		fields.put(FieldType.MD.getName(), transaction.getMerchantId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getErrorCode());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getUuid());
		fields.put(FieldType.RRN.getName(), transaction.getRrn());

	}

	public void updateNBResponse(Fields fields) {
		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getStatus())) && ((transaction.getStatus()).equalsIgnoreCase("PAID"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
		} else {
			if (StringUtils.isNotBlank(transaction.getStatus())) {
				LyraResultType resultInstance = LyraResultType.getInstanceFromName(transaction.getErrorCode());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getPGCode());
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
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getUuid());
		fields.put(FieldType.PG_DATE_TIME.getName(), transaction.getPgDateTime());
	}
	
	public void updateNBRefundResponse(Fields fields) {
		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getStatus())) && ((transaction.getStatus()).equalsIgnoreCase("ACCEPTED"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
		} else {
			if (StringUtils.isNotBlank(transaction.getStatus())) {
				LyraResultType resultInstance = LyraResultType.getInstanceFromName(transaction.getErrorCode());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getPGCode());
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
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getUuid());
		fields.put(FieldType.PG_DATE_TIME.getName(), transaction.getPgDateTime());
	}

	public String getStatusType() {
		String result = transaction.getResult();
		String status = null;

		if (result.equals("SUCCESS")) {
			status = StatusType.SENT_TO_BANK.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}

		return status.toString();
	}

	public String getRefundStatusType() {
		String result = transaction.getResult();
		String status = null;

		if (result.equals("SUCCESS")) {
			status = StatusType.CAPTURED.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}

		return status.toString();
	}

	public ErrorType getResponse() {
		String result = transaction.getResult();
		ErrorType errorType = null;

		if (result.equals("SUCCESS")) {
			errorType = ErrorType.SUCCESS;
		} else {
			errorType = ErrorType.REJECTED;
		}

		return errorType;
	}
}
