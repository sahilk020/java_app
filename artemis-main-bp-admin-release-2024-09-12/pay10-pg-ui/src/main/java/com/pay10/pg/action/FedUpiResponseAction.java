package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.UpiHistorian;

/**
 * @author Rahul
 *
 */
public class FedUpiResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static final long serialVersionUID = -3715034811160836292L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private UpiHistorian upiHistorian;

	private static Logger logger = LoggerFactory.getLogger(FedUpiResponseAction.class.getName());

	private HttpServletRequest httpRequest;
	private String redirectUrl;
	private Integer count;

	public FedUpiResponseAction() {
	}

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {

		Fields responseField = null;
		try {

			Object obj = JSONUtil.deserialize(httpRequest.getReader());

			String response = obj.toString();

			logger.info("response received from bank " + response);
			if (response.contains(Constants.FED_UPI_RESP_VAL_ADD.getValue())) {
				return Action.NONE;

			}

			int start = response.lastIndexOf("{");
			int end = response.indexOf("}");

			String formattedString = response.substring(start + 1, end);
			String formattedStringArray[] = formattedString.split(", ");

			Map<String, String> responseMap = new HashMap<String, String>();

			for (int i = 0; i < formattedStringArray.length; i++) {

				String str = formattedStringArray[i];
				String strArray[] = str.split(Constants.EQUATOR.getValue());

				responseMap.put(strArray[0], strArray[1]);

			}
			String responseCode = responseMap.get(Constants.FED_UPI_RESPONSE_CODE);
			String payeeApprovalNum = null;
			String responseMessage = responseMap.get(Constants.FED_UPI_RESPONSE);
			String customerReference = responseMap.get(Constants.FED_UPI_CUST_REFERENCE);
			String payerApprovalNum = null;
			String pgTime = responseMap.get(Constants.FED_UPI_APPROVAL_TIME);
			if (responseCode.equals(Constants.FED_UPI_SUCCESS_CODE)) {
				payeeApprovalNum = responseMap.get(Constants.FED_UPI_PAYEE_APPROVAL_NUM);
				payerApprovalNum = responseMap.get(Constants.FED_UPI_PAYER_APPROVAL_NUM);
			}

			String status = getStatusType(responseCode);
			ErrorType errorType = getErrorType(responseCode);

			Fields fields = new Fields();
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.FEDERAL.getCode());
			fields.put(FieldType.UDF5.getName(), customerReference);
			upiHistorian.findPrevious(fields);

			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.PG_RESP_CODE.getName(), responseCode);
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), responseMessage);
			fields.put(FieldType.ACQ_ID.getName(), payeeApprovalNum);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put(FieldType.RRN.getName(), payerApprovalNum);
			fields.put(FieldType.PG_DATE_TIME.getName(), pgTime);

			Map<String, String> res = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_UPI_PROCESSOR.getValue());
			responseField = new Fields(res);

			return Action.NONE;

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return NONE;
		}
	}

	public String getStatusType(String federalResponseCode) {
		String status = null;
		if (federalResponseCode.equals(Constants.FED_UPI_SUCCESS_CODE)) {
			status = StatusType.CAPTURED.getName();
		} else if (federalResponseCode.equals("U19")) {
			status = StatusType.FAILED.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}

		return status;
	}

	public ErrorType getErrorType(String federalResponseCode) {
		ErrorType error = null;

		if (federalResponseCode.equals(Constants.FED_UPI_SUCCESS_CODE)) {
			error = ErrorType.SUCCESS;
		} else if (federalResponseCode.equals("U19")) {
			error = ErrorType.INVALID_REQUEST_FIELD;
		} else if (federalResponseCode.equals("002")) {
			error = ErrorType.CANCELLED;
		} else if (federalResponseCode.equals("003")) {
			error = ErrorType.INTERNAL_SYSTEM_ERROR;
		} else if (federalResponseCode.equals("004")) {
			error = ErrorType.CANCELLED;
		} else {
			error = ErrorType.DECLINED;
		}

		return error;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
