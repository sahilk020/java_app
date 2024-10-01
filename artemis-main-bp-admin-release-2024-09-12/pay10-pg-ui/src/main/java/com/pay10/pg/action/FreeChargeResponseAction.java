package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;
import com.opensymphony.xwork2.Action;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.json.JSONUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.FreeChargeResultType;
import com.pay10.pg.core.util.UpiHistorian;

public class FreeChargeResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static final long serialVersionUID = -4602070286831396667L;
	private static Logger logger = LoggerFactory.getLogger(FreeChargeResponseAction.class.getName());

	private HttpServletRequest httpRequest;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private UpiHistorian upiHistorian;

	@Autowired
	private PropertiesManager propertiesManager;

	String status = "";
	ErrorType errorType = null;
	String pgTxnMsg = "";

	@Override
	public void setServletRequest(HttpServletRequest request) {
		httpRequest = request;
	}

	private Map<String, String> acknowledgementReq = new HashMap<String, String>();

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {

		Fields responseField = null;
		try {

			Object obj = JSONUtil.deserialize(httpRequest.getReader());
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(obj);
			JSONObject res = new JSONObject(json);
			Fields fields = new Fields();

			logger.info("response received from bank Freecharge" + res.toString());

			String callbackRequired = PropertiesManager.propertiesMap.get("FreeChargeCallbackRequired");

			if (callbackRequired.equalsIgnoreCase("Y")) {
				String acqStauts = res.getString("status");

				updateStatusResponse(acqStauts);

				String pgTime = res.getString("time");
				String acqId = res.getString("txnId");
				String pgRefNum = res.getString("merchantTxnId");
				String pgTxn = pgTxnMsg;

				fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
				fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.FREECHARGE.getCode());
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				logger.info("fields before historian freecharge " + fields.getFieldsAsString());
				upiHistorian.findPrevious(fields);
				logger.info("After historian freecharge" + fields.getFieldsAsString());
				fields.put(FieldType.STATUS.getName(), status);
				fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
				fields.put(FieldType.PG_TXN_STATUS.getName(), acqStauts);
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), acqStauts);
				fields.put(FieldType.RRN.getName(), acqId);
				fields.put(FieldType.ACQ_ID.getName(), acqId);
				fields.put(FieldType.PG_DATE_TIME.getName(), pgTime);
				fields.put(FieldType.STATUS.getName(), status.toString());
				fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");

				logger.info("fields send to transact " + fields.getFieldsAsString());

				Map<String, String> resp = transactionControllerServiceProvider.transact(fields,
						Constants.TXN_WS_UPI_PROCESSOR.getValue());
				responseField = new Fields(resp);
				logger.info("Response received from WS for Freecharge " + responseField.getFieldsAsString());
			}

			return Action.NONE;
		} catch (Exception e) {
			logger.error("Error in Freecharge UPI callback = ", e);
		}

		return Action.NONE;
	}

	public void updateStatusResponse(String acqStatus) throws SystemException {
		try {

			if (acqStatus.equals(Constants.FREECHARGE_UPI_SUCCESS_CODE.getValue())) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
			} else {
				if (StringUtils.isNotBlank(acqStatus)) {
					FreeChargeResultType resultInstance = FreeChargeResultType.getInstanceFromName(acqStatus);
					if (resultInstance != null) {
						if (resultInstance.getPGCode() != null) {
							status = resultInstance.getStatusCode();
							errorType = ErrorType.getInstanceFromCode(resultInstance.getPGCode());
							pgTxnMsg = resultInstance.getMessage();
						} else {
							status = StatusType.REJECTED.getName();
							errorType = ErrorType.DECLINED;
							pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

						}

					} else {
						status = StatusType.REJECTED.getName();
						errorType = ErrorType.DECLINED;
						pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.DECLINED;
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

				}
			}
		} catch (Exception e) {
			logger.error("Unknown Exception :" + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in  Freecharge ResponseAction");
		}
	}

}
