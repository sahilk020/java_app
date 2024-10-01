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
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.IcicibankUpiResultType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.ICICIUpiUtil;
import com.pay10.pg.core.util.UpiHistorian;

public class IciciBankUpiResponse extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(YesBankUpiResponseAction.class.getName());

	private static final long serialVersionUID = 2382298172065463916L;

	private HttpServletRequest httpRequest;

	private String payId;

	String status = "";
	ErrorType errorType = null;
	String pgTxnMsg = "";

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ICICIUpiUtil iciciUpiUtil;

	@Autowired
	private UpiHistorian upiHistorian;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	AWSEncryptDecryptService awsEncryptDecryptService;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		httpRequest = request;
		payId = httpRequest.getParameter("payId");

		if (StringUtils.isBlank(payId)) {
			logger.info("IciciBankUpi APPROVED CALLBACK RESPONSE NO PAYID ");
		} else {
			logger.info("IciciBankUpi APPROVED CALLBACK RESPONSE WITH PAYID " + payId);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {

		logger.info("Inside  IciciBankUpiResponseAction Execute");

		try {

			BufferedReader rd = httpRequest.getReader();
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				// response.append('\r');
			}
			rd.close();
			logger.info(payId + "----Icici bank upi response --->" + response.toString());
			// String resStr = response.toString();
			String encrypted = response.toString();// resStr.substring(6, resStr.length() - 1);
			Fields fields = new Fields();

			String key = "";

			if (StringUtils.isBlank(payId)) {

				key = PropertiesManager.propertiesMap.get("ICICIUPI_PRIVATE_KEY_PATH");
			}
			// To extract key from payId present in URL.
			else {
				User user = new UserDao().findPayId(payId);
				String acquirerCode = AcquirerType.NB_ICICI_BANK.getCode();
				Account account = user.getAccountUsingAcquirerCode(acquirerCode);

				try {

					logger.info("IciciBankUpi APPROVED CALLBACK RESPONSE DEFAULT CURRENCY  "
							+ Constants.DEFAULT_CURRENCY_CODE.getValue());

					AccountCurrency accountCurrency = account
							.getAccountCurrency(Constants.DEFAULT_CURRENCY_CODE.getValue());

					// Decrypt values

					if (!StringUtils.isEmpty(accountCurrency.getAdf7())) {
						key = awsEncryptDecryptService.decrypt(accountCurrency.getAdf7());
						logger.info("key value if payID is present  " + key);
					}

				} catch (Exception e) {

					logger.error("Error in YEs bank UPI callback for Gpay while getting the key from accountCurrency = "
							+ e);
				}

			}

			String decryptedString = "";
			try {
				logger.info(
						payId + "----Icici bank upi response encrypt key --->" + key + "----response ---" + encrypted);
				decryptedString = iciciUpiUtil.getMessageDiscryption(encrypted, key);

			} catch (Exception e) {

				logger.error("Error in Icici bank UPI callback data decryption = " + e);
			}

			logger.info("IciciBankUpi APPROVED CALLBACK RESPONSE DECRYPT VALUE " + decryptedString);

			if (decryptedString != null) {

				JSONObject resJson = new JSONObject(decryptedString);
				String receivedResponseCode = "";
				if (decryptedString.contains("TxnStatus")) {
					receivedResponseCode = resJson.get("TxnStatus").toString();
				}
				updateStatusResponse(receivedResponseCode);

				fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.NB_ICICI_BANK.getCode());
				fields.put(FieldType.PG_REF_NUM.getName(), resJson.get("merchantTranId").toString());
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				logger.info("fields before historian " + fields.getFieldsAsString());
				upiHistorian.findPrevious(fields);
				logger.info("After historian " + fields.getFieldsAsString());
				fields.put(FieldType.STATUS.getName(), resJson.get("TxnStatus").toString());
				fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.toString().replaceAll("_", ""));
				fields.put(FieldType.PG_RESP_CODE.getName(), "0");
				fields.put(FieldType.PG_TXN_STATUS.getName(), resJson.get("TxnStatus").toString());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), resJson.get("TxnStatus").toString());
				fields.put(FieldType.UDF1.getName(), resJson.get("PayerVA").toString());

				fields.put(FieldType.ACQ_ID.getName(), resJson.get("BankRRN").toString());
				fields.put(FieldType.RRN.getName(), resJson.get("BankRRN").toString());
				fields.put(FieldType.PG_DATE_TIME.getName(), resJson.get("TxnInitDate").toString());
				fields.put(FieldType.AUTH_CODE.getName(), resJson.get("BankRRN").toString());
				fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			}
			logger.info("fields send to transact  FOR YES BANK UPI" + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + fields.getFieldsAsString());

			Map<String, String> res = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_UPI_PROCESSOR.getValue());
			res.remove(FieldType.ORIG_TXN_ID.getName());

			// responseField = new Fields(res);
			// res.remove(FieldType.ORIG_TXN_ID.getName());
//				logger.info("Response received from WS FOR YES BANK UPI" + responseField);

			return Action.NONE;

		} catch (Exception e) {

			logger.error("Error in Icici bank UPI callback 2 = " + e);
		}

		return Action.NONE;
	}

	public void updateStatusResponse(String receivedResponseCode) throws SystemException {
		try {
			logger.info(" inside IciciBank Upi Response action in  updateStatusResponse method response code is ==  "
					+ receivedResponseCode);
			if (receivedResponseCode.equals(Constants.ICICI_UPI_RESPONSE.getValue())) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
			} else {
				if (StringUtils.isNotBlank(receivedResponseCode)) {
					IcicibankUpiResultType resultInstance = IcicibankUpiResultType
							.getInstanceFromName(receivedResponseCode);
					logger.info(
							" inside IciciBank Upi Response action in  updateStatusResponse method resultInstance is : == "
									+ resultInstance);
					if (resultInstance != null) {
						if (resultInstance.getiPayCode() != null) {
							logger.info(
									" inside IciciBankUpi Response action in  updateStatusResponse method resultInstance is ==  "
											+ resultInstance.getStatusName() + (resultInstance.getiPayCode()));
							status = resultInstance.getStatusName();
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
			logger.error("Unknown Exception :" + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in  icicibankUpiResponseAction");
		}
	}
}