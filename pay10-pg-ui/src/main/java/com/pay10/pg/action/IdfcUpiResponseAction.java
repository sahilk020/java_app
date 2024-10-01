package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.json.JSONUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.IdfcUpiHmacAlgo;
import com.pay10.commons.util.IdfcUpiUpiResultType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.UpiHistorian;

public class IdfcUpiResponseAction extends AbstractSecureAction implements ServletRequestAware {
	private static Logger logger = LoggerFactory.getLogger(IdfcUpiResponseAction.class.getName());

	private static final long serialVersionUID = 2382298172065463916L;

	private HttpServletRequest httpRequest;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	@Qualifier("idfcUpiHmacAlgo")
	private IdfcUpiHmacAlgo idfcUpiHmacAlgo;

	@Autowired
	private UpiHistorian upiHistorian;

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

		logger.info("Inside  idfcUpiResponseAction Execute");

		Fields responseField = null;
		try {

			Object obj = JSONUtil.deserialize(httpRequest.getReader());
			// conveting object to json
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(obj);
			logger.info("response received from bank idfc upi 1 " + json);
			JSONObject res = new JSONObject(json);
			Fields fields = new Fields();

			logger.info("response received from bank idfc upi 2" + res.toString());
			String pgRefNum = "";

			String responseCode = res.getString(Constants.RESP_CODE.getValue());
			String responseMessage = res.getString(Constants.RESP_MSG.getValue());

			updateStatusResponse(responseCode, responseMessage);

			String payeeVpa = res.getString(Constants.PAYEE_VIR_ADDR.getValue());
			String customerReference = res.getString(Constants.ORG_CUST_REF_ID.getValue());
			String payerVpa = res.getString(Constants.PAYER_VIR_ADDR.getValue());
			String pgTime = res.getString(Constants.TIME_STAMP.getValue());
			String transactionid = res.getString(Constants.ORG_TXN_ID.getValue());
			String acqId = res.getString(Constants.ORG_CUST_REF_ID.getValue());
			String pgTxn = pgTxnMsg;
			String bankTxnId = res.getString(Constants.BANK_TXNID.getValue());

			pgRefNum = transactionid.substring(5, 21);

			logger.info("Merchant VPA idfc upi " + payeeVpa);

			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.IDFCUPI.getCode());
			fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			logger.info("fields before historian idfc upi " + fields.getFieldsAsString());
			upiHistorian.findPrevious(fields);
			logger.info("After historian idfc upi" + fields.getFieldsAsString());
			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.PG_RESP_CODE.getName(), responseCode);
			fields.put(FieldType.PG_TXN_STATUS.getName(), responseMessage);
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxn);
			fields.put(FieldType.UDF1.getName(), payeeVpa);
			fields.put(FieldType.UDF3.getName(), payerVpa);
			fields.put(FieldType.RRN.getName(), customerReference);
			fields.put(FieldType.IDFC_UPI_TXNID.getName(), bankTxnId);
			fields.put(FieldType.ACQ_ID.getName(), acqId);
			fields.put(FieldType.PG_DATE_TIME.getName(), pgTime);
			fields.put(FieldType.STATUS.getName(), status.toString());
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");

			logger.info("fields send to transact " + fields.getFieldsAsString());

			Map<String, String> resp = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_UPI_PROCESSOR.getValue());
			responseField = new Fields(resp);
			
			//sending bank acknowledgement request 
			String respJson = responseField.get(FieldType.IDFCUPI_RESPONSE_FIELD.getName());
			JSONObject resStatus = new JSONObject(respJson);

			String responseStatusCode = resStatus.getString(Constants.RESP_CODE.getValue());
			String responseStatusMessage = resStatus.getString(Constants.RESP_MSG.getValue());
			String responseStatusTxnId = resStatus.getString(Constants.TXN_ID.getValue());
			String responseStatusOpr = resStatus.getString(Constants.OPERATION_NAME.getValue());
			String responseStatusHmac = resStatus.getString(Constants.HMAC.getValue());
			String responseStatusTimaStamp = resStatus.getString(Constants.TIME_STAMP.getValue());

			acknowledgementReq.put(Constants.RESP_CODE.getValue(), responseStatusCode);
			acknowledgementReq.put(Constants.RESP_MSG.getValue(), responseStatusMessage);
			acknowledgementReq.put(Constants.TXN_ID.getValue(), responseStatusTxnId);
			acknowledgementReq.put(Constants.OPERATION_NAME.getValue(), responseStatusOpr);
			acknowledgementReq.put(Constants.HMAC.getValue(), responseStatusHmac);
			acknowledgementReq.put(Constants.TIME_STAMP.getValue(), responseStatusTimaStamp);
		    setAcknowledgementReq(acknowledgementReq);

			logger.info("Response received from WS for idfc upi " + responseField);
			logger.info("Response received from WS for idfc upi for callback acknowledgement req" + respJson);

			return SUCCESS;
		} catch (Exception e) {

			logger.error("Error in idfc bank UPI callback = " + e);
		}

		return SUCCESS;
	}

	

	public void updateStatusResponse(String receivedResponseCode, String receivedResponse) throws SystemException {
		try {
			logger.info(" inside IDFC UPI Response action in  updateStatusResponse method response code is ==  "
					+ receivedResponseCode);
			if (receivedResponseCode.equals(Constants.IDFC_UPI_SUCCESS_CODE.getValue())
					&& receivedResponse.equals(Constants.IDFC_UPI_RESPONSE_MSG.getValue())) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
			} else {
				if (StringUtils.isNotBlank(receivedResponseCode)) {
					IdfcUpiUpiResultType resultInstance = IdfcUpiUpiResultType
							.getInstanceFromName(receivedResponseCode);
					logger.info(
							" inside IDFC UPI Response action in  updateStatusResponse method resultInstance is : == "
									+ resultInstance);
					if (resultInstance != null) {
						if (resultInstance.getiPayCode() != null) {
							logger.info(
									" inside IDFC UPI Response action in  updateStatusResponse method resultInstance is ==  "
											+ resultInstance.getStatusName() + (resultInstance.getiPayCode()));
							status = resultInstance.getStatusName();
							errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
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
					"unknown exception in  idfcUpiResponseAction");
		}
	}

	public Map<String, String> getAcknowledgementReq() {
		return acknowledgementReq;
	}

	public void setAcknowledgementReq(Map<String, String> acknowledgementReq) {
		this.acknowledgementReq = acknowledgementReq;
	}
	
	
}
