package com.pay10.migs;

import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service("migsTransformer")
public class MigsTransformer {

	private static final String separator = "\\|";
	private static final String zero = "0";
	private static final String one = "1";
	private static final String two = "2";
	private Object Y;

	public void updateResponse(Transaction response, Fields fields) {
		// change according to txn type
		String status = null;
		ErrorType errorType = null;

		String txnType = fields.get(FieldType.TXNTYPE.getName());

		if (txnType.equals(TransactionType.ENQUIRY.getName())) {
			status = getEnquiryStatus(response, fields);
			errorType = getEnquiryResponseCode(response);
		} else {
			status = getStatus(response, fields);
			errorType = getResponseCode(response);
		}

		if (!(StatusType.APPROVED.getName().equals(status) || StatusType.CAPTURED.getName().equals(status))) {
			// This is applicable when we sent invalid request to server
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
			fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), response.getMessage());
			return;
		}
		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
		fields.put(FieldType.RRN.getName(), response.getRRN());
		fields.put(FieldType.ACQ_ID.getName(), response.getPgTransactionNo());
		fields.put(FieldType.AUTH_CODE.getName(), response.getAuthorizeId());
	}

	private ErrorType getEnquiryResponseCode(Transaction response) {
		String respCode = response.getDrExists();
		ErrorType errorType = null;
		if (respCode.equals(Y)) {
			errorType = ErrorType.SUCCESS;
		} else {
			errorType = ErrorType.REJECTED;
		}
		return errorType;
	}

	private String getEnquiryStatus(Transaction response, Fields fields) {
		String respCode = response.getDrExists();
		String status = "";
		if (respCode.equals(Y)) {
			status = StatusType.CAPTURED.getName();
		} else {
			status = StatusType.FAILED.getName();
		}
		return status;
	}

	private ErrorType getResponseCode(Transaction response) {
		String respCode = response.getResponseCode();
		ErrorType errorType = null;

		if (null == respCode) {
			errorType = ErrorType.ACQUIRER_ERROR;
		} else {
			errorType = Mapper.getErrorType(respCode);
		}
		return errorType;
	}

	private String getStatus(Transaction response, Fields fields) {
		String respCode = response.getResponseCode();
		String status = "";

		if (null == respCode) {
			status = StatusType.ERROR.getName();
		} else if (respCode.equals(zero)) {
			String txnType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
			if (txnType.equals(TransactionType.SALE.getName()) || txnType.equals(TransactionType.REFUND.getName())) {
				status = StatusType.CAPTURED.getName();
			} else if (txnType.equals(TransactionType.AUTHORISE.getName())) {
				status = StatusType.APPROVED.getName();
			}
		} else if (respCode.equals(one)) {
			status = StatusType.ERROR.getName();
		} else if (respCode.equals(two)) {
			status = StatusType.FAILED.getName();
		} else {
			status = StatusType.ERROR.getName();
		}

		return status;
	}

	private ErrorType getEzeeClickResponse(String respCode) {
		ErrorType errortype = null;

		if (null == respCode) {
			errortype = ErrorType.ACQUIRER_ERROR;
		} else if (respCode.equals("S")) {
			errortype = ErrorType.SUCCESS;
		} else if (respCode.equals("F")) {
			errortype = ErrorType.REJECTED;
		} else {
			errortype = ErrorType.ACQUIRER_ERROR;
		}
		return errortype;
	}

	private StatusType getEzeeClickStatus(String respCode) {
		StatusType status = null;

		if (null == respCode) {
			status = StatusType.ERROR;
		} else if (respCode.equals("S")) {
			status = StatusType.CAPTURED;
		} else if (respCode.equals("F")) {
			status = StatusType.REJECTED;
		} else {
			status = StatusType.ERROR;
		}
		return status;
	}

	public void updateEzeeClickResponse(Fields fields, String responseString) {
		ErrorType errorType = null;
		StatusType status = null;
		String[] ezeeClickResponse = responseString.split(separator);
		;
		if (ezeeClickResponse.length != 35) {
			errorType = getEzeeClickResponse(null);
			fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			return;
		}
		status = getEzeeClickStatus(ezeeClickResponse[2]);
		errorType = getEzeeClickResponse(ezeeClickResponse[2]);

		fields.put(FieldType.ACQ_ID.getName(), ezeeClickResponse[0]);
		fields.put(FieldType.STATUS.getName(), status.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), ezeeClickResponse[34]);
		// fields.put(FieldType.AMOUNT.getName(),ezeeClickResponse[4]);
		fields.put(FieldType.RRN.getName(), ezeeClickResponse[11]);
		fields.put(FieldType.PG_REF_NUM.getName(), ezeeClickResponse[30]);
	}
}
