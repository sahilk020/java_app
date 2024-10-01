
package com.pay10.pg.action;

import java.io.BufferedReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.AxisBankUPIResultType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.YesbankCbUpiResultType;
import com.pay10.pg.core.util.AxisBankUpiEncDecService;
import com.pay10.pg.core.util.UpiHistorian;

public class AxisBankUpiResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(AxisBankUpiResponseAction.class.getName());

	private static final long serialVersionUID = 2382298172065463916L;

	private HttpServletRequest httpRequest;

	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	String status = "";
	ErrorType errorType = null;
	String pgTxnMsg = "";

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private UpiHistorian upiHistorian;

	@Autowired
	private AxisBankUpiEncDecService axisBankUpiEncDecService;

	@Autowired
	private PropertiesManager propertiesManager;

	@SuppressWarnings("unchecked")
	public String execute() {

		logger.info("Inside  AxisBankUpiResponseAction Execute");

		Fields responseField = null;
		try {

			logger.info("Response received for axis bank upi ");

			String payId = "";
			String decryptionKey = "";
			if (StringUtils.isNotBlank(httpRequest.getParameter("payId"))) {
				
				 payId = httpRequest.getParameter("payId");
				 
				 	User user = new UserDao().findPayId(payId);
					String acquirerCode = AcquirerType.YESBANKCB.getCode();
					Account account = user.getAccountUsingAcquirerCode(acquirerCode);
					
					AccountCurrency accountCurrency = account
							.getAccountCurrency(Constants.DEFAULT_CURRENCY_CODE.getValue());
					
			}
			else {
				decryptionKey = propertiesManager.propertiesMap.get("AxisBankUpiDecKey");
			}
			
			
			
			BufferedReader rd = httpRequest.getReader();
			String line;
			StringBuffer responseString = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				responseString.append(line);
				responseString.append('\r');
			}
			rd.close();

			if (responseString.toString().isEmpty()) {
				logger.info("Buffered Reader response is empty ");
			} else {
				logger.info("Buffered Reader response contains data ");
				logger.info(" response >>>  " + responseString.toString());
			}

			String encData = responseString.toString();
			JSONObject encDatajson = new JSONObject(encData);
			String data = null;

			if (encDatajson != null && encDatajson.get("data") != null) {

				data = (String) encDatajson.get("data");
			}

		
			String decryptedResponse = "";

			if (decryptionKey == null) {
				logger.info("Decryption key for Axis UPI is null");
			}

			if (data == null) {
				logger.info("Data for Axis UPI is null");
			}

			if (data != null && StringUtils.isNotBlank(decryptionKey) && StringUtils.isNotBlank(decryptionKey)) {
				decryptedResponse = axisBankUpiEncDecService.decrypt(data, decryptionKey);
			}

			logger.info("Axis Upi Decrypted Response " + decryptedResponse);

			updateStatusResponse(decryptedResponse);

			JSONObject response = new JSONObject(decryptedResponse);

			String merchantTransactionId = ((String) response.get("merchantTransactionId"));
			String gatewayTransactionId = ((String) response.get("gatewayTransactionId"));
			String gatewayResponseCode = ((String) response.get("gatewayResponseCode"));
			String gatewayResponseMessage = ((String) response.get("gatewayResponseMessage"));
			String rrn = ((String) response.get("rrn"));
			String merchantVPA = ((String) response.get("customerVpa"));

			Fields fields = new Fields();
			String pgTxn = pgTxnMsg;
			String responseMsg = status;

			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.AXISBANK.getCode());
			fields.put(FieldType.PG_REF_NUM.getName(), merchantTransactionId);
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			upiHistorian.findPrevious(fields);
			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.toString().replaceAll("_", ""));
			fields.put(FieldType.PG_RESP_CODE.getName(), gatewayResponseCode);
			fields.put(FieldType.PG_TXN_STATUS.getName(), responseMsg);
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxn);
			fields.put(FieldType.UDF1.getName(), merchantVPA);
			fields.put(FieldType.PG_DATE_TIME.getName(),"");
			fields.put(FieldType.ACQ_ID.getName(), gatewayTransactionId);
			fields.put(FieldType.RRN.getName(), rrn);
			fields.put(FieldType.PG_DATE_TIME.getName(), gatewayResponseMessage);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");

			logger.info("Axis Upi fields prepapred and sent to upi processsor ");
			Map<String, String> res = transactionControllerServiceProvider.transact(fields,Constants.TXN_WS_UPI_PROCESSOR.getValue());
			res.remove(FieldType.ORIG_TXN_ID.getName());

			responseField = new Fields(res);
			res.remove(FieldType.ORIG_TXN_ID.getName());

			return Action.NONE;

		} catch (Exception e) {
			logger.error("Exception in axis bank upi response ", e);
		}
		return Action.NONE;
	}

	public void updateStatusResponse(String decryptedResponse) throws SystemException {

		try {

			JSONObject response = new JSONObject(decryptedResponse);

			String responseCode = ((String) response.get("gatewayResponseCode"));
			String responseMsg = ((String) response.get("gatewayResponseMessage"));

			logger.info("Axis UPI code = " + responseCode);
			
			if (responseCode.equalsIgnoreCase(Constants.AXISBANK_UPI_SUCCESS_CODE.getValue())
					&& responseMsg.equalsIgnoreCase(Constants.AXISBANK_UPI_RESPONSE.getValue())) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
			} else {
				if (StringUtils.isNotBlank(responseCode)) {
					AxisBankUPIResultType resultInstance = AxisBankUPIResultType.getInstanceFromName(responseCode);
					if (resultInstance != null) {
						if (resultInstance.getiPayCode() != null) {
							status = resultInstance.getStatusCode();
							errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
							pgTxnMsg = resultInstance.getMessage();
						} else {
							status = StatusType.REJECTED.getName();
							errorType = ErrorType.REJECTED;
							pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

						}

					} else {
						status = StatusType.REJECTED.getName();
						errorType = ErrorType.REJECTED;
						pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

				}
			}
		} catch (Exception e) {
			logger.error("Exception in Axis Bank UPI :" + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e, "Exception in Axis Bank UPI");
		}
	}

}
