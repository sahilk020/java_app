package com.pay10.pg.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.json.JSONUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.KotakUpiUtils;
import com.pay10.pg.core.util.UpiHistorian;

public class KotakUpiResponseAction extends AbstractSecureAction implements ServletRequestAware {
	private static Logger logger = LoggerFactory.getLogger(KotakUpiResponseAction.class.getName());

	private static final long serialVersionUID = 2382298172065463916L;

	private HttpServletRequest httpRequest;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;
	@Autowired
	@Qualifier("kotakUpiUtil")
	private KotakUpiUtils kotakUpiUtil;

	@Autowired
	private UpiHistorian upiHistorian;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		httpRequest = request;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {

		logger.info("Inside  KotakUpiResponseAction Execute");

		Fields responseField = null;
		try {

			Object obj = JSONUtil.deserialize(httpRequest.getReader());
			// conveting object to json
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(obj);
			JSONObject res = new JSONObject(json);
			String receivedCheckSum = res.getString(Constants.KOTAK_UPI_CHECKSUM.getValue());

			Fields fields = new Fields();

			logger.info("response received from bank kotak upi " + res);
			
			String generatedCheckSumResponse = kotakUpiUtil.matchChecksum(res);

			String responseCode = "";
			String payeeVpa = "";
			String responseMessage = "";
			String customerReference = "";
			String payerVpa = "";
			String pgTime = "";
			String remarks = "";
			String pgRefNum = "";
			String acqId = "";//to be changed according to requirement
			
			// matching call back checkSum response
			if (receivedCheckSum.equalsIgnoreCase(generatedCheckSumResponse)) {

				responseCode = res.getString(Constants.KOTAK_UPI_STATUS_CODE.getValue());
				payeeVpa = res.getString(Constants.KOTAK_UPI_PAYEEVPA.getValue());
				responseMessage = res.getString(Constants.KOTAK_UPI_STATUS.getValue());
				customerReference = res.getString(Constants.KOTAK_UPI_RRN.getValue());
				payerVpa = res.getString(Constants.KOTAK_UPI_PAYERVPA.getValue());
				pgTime = res.getString(Constants.KOTAK_UPI_TIMESTAMP.getValue());
				remarks = res.getString(Constants.KOTAK_UPI_REMARKS.getValue());
				acqId = res.getString(Constants.KOTAK_UPI_ACQID.getValue());
				
				String transactionid = res.getString(Constants.KOTAK_UPI_TRANSID.getValue());
				pgRefNum = transactionid.substring(8);

			} else {
				String transactionid = res.getString(Constants.KOTAK_UPI_TRANSID.getValue());
				pgRefNum = transactionid.substring(8);
				responseCode = Constants.KOTAK_UPI_CHECKSUM_FAILURE_CODE.getValue();
				responseMessage = Constants.KOTAK_UPI_CHECKSUM_FAILURE_RESPONSE.getValue();
			}

			String status = getStatusType(responseCode, responseMessage);
			ErrorType errorType = getErrorType(responseCode, responseMessage);

			logger.info("Merchant VPA kotak upi " + payeeVpa);

			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.KOTAK.getCode());
			fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			logger.info("fields before historian kotak upi " + fields.getFieldsAsString());
			upiHistorian.findPrevious(fields);
			logger.info("After historian kotak upi" + fields.getFieldsAsString());
			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.PG_RESP_CODE.getName(), responseCode);
			fields.put(FieldType.PG_TXN_STATUS.getName(), responseMessage);
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), remarks);
			fields.put(FieldType.UDF1.getName(), payeeVpa);
			fields.put(FieldType.UDF3.getName(), payerVpa);

			fields.put(FieldType.RRN.getName(), customerReference);
			fields.put(FieldType.ACQ_ID.getName(), acqId);
			fields.put(FieldType.PG_DATE_TIME.getName(), pgTime);
			fields.put(FieldType.STATUS.getName(), status.toString());

			logger.info("fields send to transact " + fields.getFieldsAsString());

			Map<String, String> resp = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_UPI_PROCESSOR.getValue());
			responseField = new Fields(resp);

			logger.info("Response received from WS for kotak upi " + responseField);

			return Action.NONE;
		} catch (Exception e) {

			logger.error("Error in Kotak bank UPI callback = " + e);
		}

		return Action.NONE;
	}

	public String getStatusType(String receivedResponse, String receivedResponseMsg) {
		String status = null;
		if (receivedResponse.equals(Constants.KOTAK_UPI_SUCCESS_CODE.getValue())
				&& receivedResponseMsg.equals(Constants.KOTAK_UPI_RESPONSE.getValue())) {
			status = StatusType.CAPTURED.getName();
		} else if (receivedResponse.equals(Constants.KOTAK_UPI_CHECKSUM_FAILURE_CODE.getValue())
				&& receivedResponseMsg.equals(Constants.KOTAK_UPI_CHECKSUM_FAILURE_RESPONSE.getValue())) {
			status = StatusType.DENIED_BY_FRAUD.getName();
		}

		else {
			status = StatusType.REJECTED.getName();
		}

		return status;
	}

	public ErrorType getErrorType(String receivedResponse, String receivedResponseMsg) {
		ErrorType error = null;

		if (receivedResponse.equals(Constants.KOTAK_UPI_SUCCESS_CODE.getValue())
				&& receivedResponseMsg.equals(Constants.KOTAK_UPI_RESPONSE.getValue())) {
			error = ErrorType.SUCCESS;
		}else if (receivedResponse.equals(Constants.KOTAK_UPI_CHECKSUM_FAILURE_CODE.getValue())
				&& receivedResponseMsg.equals(Constants.KOTAK_UPI_CHECKSUM_FAILURE_RESPONSE.getValue())) {
			error = ErrorType.SIGNATURE_MISMATCH;
		} 
		
		else {
			error = ErrorType.DECLINED;
		}

		return error;
	}

}
