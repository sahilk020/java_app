package com.pay10.icici.mpgs;

import org.apache.commons.lang3.StringUtils;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

/**
 * @author Rahul
 *
 */

public class IciciMpgsTransformer {

	private Transaction transaction = null;

	public IciciMpgsTransformer(Transaction transaction) {
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
			status = getStatusType();
			errorType = getResponse();
		} else {

			if ((transaction.getGatewayCode().equalsIgnoreCase("APPROVED")) && (transaction.getStatus().equalsIgnoreCase("SUCCESS"))) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			} else {
				if (StringUtils.isNotBlank(transaction.getGatewayCode())) {
					MPGSResultType resultInstance = MPGSResultType.getInstanceFromName(transaction.getGatewayCode());
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
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
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getAcquirerCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getGatewayCode());
		fields.put(FieldType.ACS_URL.getName(), transaction.getAcsURL());
		fields.put(FieldType.PAREQ.getName(), transaction.getPaReq());

		fields.put(FieldType.MD.getName(), transaction.getXid());
		fields.put(FieldType.RRN.getName(), transaction.getRrn());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getAcq());

	}

	public String getStatusType() {
		String result = transaction.getGatewayRecommendation();
		String veresEnrolled = transaction.getVeResEnrolled();
		String status = null;

		if ((result.equals("PROCEED")) && (veresEnrolled.equalsIgnoreCase("Y"))) {
			status = StatusType.ENROLLED.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}

		return status.toString();
	}

	public ErrorType getResponse() {
		String result = transaction.getGatewayRecommendation();
		String veresEnrolled = transaction.getVeResEnrolled();
		ErrorType errorType = null;

		if ((result.equals("PROCEED")) && (veresEnrolled.equalsIgnoreCase("Y"))) {
			errorType = ErrorType.SUCCESS;
		} else {
			errorType = ErrorType.REJECTED;
		}

		return errorType;
	}

	/*
	 * public String getSaleStatusType() { String result =
	 * transaction.getGatewayRecommendation(); String status = null;
	 * 
	 * if (result.equals("PROCEED")) { status = StatusType.CAPTURED.getName(); }
	 * else if (result.equals("TIMEOUT")) { status = StatusType.TIMEOUT.getName(); }
	 * else if (result.equals("10")) { status = StatusType.SETTLED.getName(); } else
	 * if (result.equals("141")) { status = StatusType.CANCELLED.getName(); } else
	 * if (result.equals("FAILED")) { status = StatusType.REJECTED.getName(); } else
	 * { status = StatusType.REJECTED.getName(); }
	 * 
	 * return status.toString(); }
	 */
}
