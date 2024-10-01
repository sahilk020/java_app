
package com.pay10.pg.action;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
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
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.YesbankCbUpiResultType;
import com.pay10.pg.core.util.HdfcUpiUtil;
import com.pay10.pg.core.util.UpiHistorian;

public class YesBankUpiResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(YesBankUpiResponseAction.class.getName());

	private static final long serialVersionUID = 2382298172065463916L;

	private HttpServletRequest httpRequest;

	private String payId;

	String status = "";
	ErrorType errorType = null;
	String pgTxnMsg = "";

	@Autowired
	private FieldsDao fieldsDao;
	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private HdfcUpiUtil hdfcUpiUtil;

	@Autowired
	private UpiHistorian upiHistorian;

	@Autowired
	AWSEncryptDecryptService awsEncryptDecryptService;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		httpRequest = request;
		payId = httpRequest.getParameter("payId");

		if (StringUtils.isBlank(payId)) {
			logger.info("YESBANKCB APPROVED CALLBACK RESPONSE NO PAYID ");
		} else {
			logger.info("YESBANKCB APPROVED CALLBACK RESPONSE WITH PAYID " + payId);
		}
	}

	@Override
	public String execute() {

		logger.info("Inside  YesBankUpiResponseAction Execute");

		Fields responseField = null;
		try {

			BufferedReader rd = httpRequest.getReader();
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

			String resStr = response.toString();
			String encrypted = resStr.substring(6, resStr.length() - 1);
			Fields fields = new Fields();

			String key = "";

			if (StringUtils.isBlank(payId)) {

				key = PropertiesManager.propertiesMap.get(Constants.YESBANK_UPI_MERCHANT_KEY.getValue());
			}
			// To extract key from payId present in URL.
			else {
				User user = new UserDao().findPayId(payId);
				String acquirerCode = AcquirerType.YESBANKCB.getCode();
				Account account = user.getAccountUsingAcquirerCode(acquirerCode);

				try {

					logger.info("YESBANKCB APPROVED CALLBACK RESPONSE DEFAULT CURRENCY  "
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

			String decryptedString ="" ;
			try {
				decryptedString = hdfcUpiUtil.decrypt(encrypted, key);

			} catch (Exception e) {

				logger.error("Error in YES bank UPI callback data decryption = " + e);
			}

			String[] value_split = decryptedString.split("\\|");

			String ReferenceId = value_split[0];
			String pgRefNum = value_split[1];
			String type = value_split[2]; // COLLECT_AUTH /PAYMENT_RECV
			String amount = value_split[3];
			String pgDateTime = value_split[4];
			String receivedResponse = value_split[5]; // (S=Success, F=Failure)
			String responseMsg = value_split[6];
			String receivedResponseCode = value_split[7];
			String errorCode = value_split[8]; // in case of failure
			String payer_va_ = value_split[10];
			String npci_txn_id = value_split[11];
			String cust_ref_id = value_split[16];
			String payee_va = value_split[20];
			String pgTxn = pgTxnMsg;
			String accNo = value_split[17];
			String accType = value_split[27];

			// dual verification by status enquiry
			boolean dualVerification = statusEnquiry(pgRefNum, receivedResponseCode,fields);

			if (value_split[2].equalsIgnoreCase(Constants.COLLECT_AUTH.getValue())) {

				logger.info(" YESBANKCB APPROVED CALLBACK RESPONSE DECRYPT VALUE " + decryptedString);

				if (dualVerification) {
					if (receivedResponseCode.equalsIgnoreCase("NA")) {
						updateStatusResponse(errorCode, receivedResponse);// NA,F
					} else {
						updateStatusResponse(receivedResponseCode, receivedResponse);// NA,F
					}
				}
				
				fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.YESBANKCB.getCode());
				fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				logger.info("fields before historian " + fields.getFieldsAsString());
				upiHistorian.findPrevious(fields);
				logger.info("After historian " + fields.getFieldsAsString());
				fields.put(FieldType.STATUS.getName(), status);
				fields.put(FieldType.PG_RESP_CODE.getName(), receivedResponseCode);
				fields.put(FieldType.PG_TXN_STATUS.getName(), receivedResponseCode);
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
				fields.put(FieldType.UDF1.getName(), payee_va);
				fields.put(FieldType.ACQ_ID.getName(), npci_txn_id);
				fields.put(FieldType.RRN.getName(), cust_ref_id);
				fields.put(FieldType.PG_DATE_TIME.getName(), pgDateTime);
				fields.put(FieldType.AUTH_CODE.getName(), cust_ref_id);
				fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
				fields.put(FieldType.UDF7.getName(), accNo);
				fields.put(FieldType.UDF8.getName(),
						StringUtils.equalsIgnoreCase(accType, "Credit") ? "Y" : "N");
				if(dualVerification) {
					fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
					fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.toString().replaceAll("_", ""));		
				}else {
					fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
					fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
					fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
					fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
				}
				logger.info("fields send to transact  FOR YES BANK UPI" + fields.get(FieldType.TXNTYPE.getName()) + " "
						+ "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + fields.getFieldsAsString());

				Map<String, String> res = transactionControllerServiceProvider.transact(fields,
						Constants.TXN_WS_UPI_PROCESSOR.getValue());
				res.remove(FieldType.ORIG_TXN_ID.getName());

				responseField = new Fields(res);
				res.remove(FieldType.ORIG_TXN_ID.getName());
				logger.info("Response received from WS FOR YES BANK UPI" + responseField.getFieldsAsString());

				return Action.NONE;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in YEs bank UPI callback 2 = " + e);
		}

		return Action.NONE;
	}

	public boolean statusEnquiry(String pgRefNum, String responseCode,Fields request) throws SystemException {

		logger.info("Yesbank UPI : responsecode :"+responseCode );
		// Skip for unsuccessful transactions if
		if (!responseCode.equalsIgnoreCase("00")) {
			return true;
		}
		logger.info("Yesbank UPI : sessin :"+sessionMap.get("FIELDS") );
//		Object fieldsObj = sessionMap.get("FIELDS");
//			fields.put((Fields) fieldsObj);
		Fields fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);	
			
			
			logger.info("Yesbank UPI  : payid :"+ fields.get(FieldType.ACQUIRER_TYPE.getName()));
		fields.put(FieldType.TXNTYPE.getName(), TransactionType.ENQUIRY.getName());
		fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
		fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.YESBANKCB.getCode());
		fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");

		Map<String, String> res = new HashMap<String, String>();
		
		try {
			logger.info("YesBank UPI : Double Verification start......PG_REF_NUM : "+pgRefNum);
			logger.info("YesBank UPI : double verification request : order id : "+fields.get(FieldType.ORDER_ID.getName()) +"   PG_REF_NUM : "+pgRefNum);
			res = transactionControllerServiceProvider.transact(fields, Constants.TXN_WS_INTERNAL.getValue());
			Fields responseMap = new Fields(res);
			logger.info("Yesbank UPI dual verfication response "+responseMap.getFieldsAsString());
			String referenceId=responseMap.get(FieldType.ACCT_ID.getName());
			if(StringUtils.isNotBlank(referenceId)) {


				request.put(FieldType.ACCT_ID.getName(), referenceId);
				}
			
			logger.info("Yesbank UPI : double verification Response Status :"+responseMap.get(FieldType.RESPONSE_MESSAGE.getName()) +" PG_REF_NUM : "+pgRefNum);
			if ("SUCCESS".equalsIgnoreCase(responseMap.get(FieldType.RESPONSE_MESSAGE.getName()))) {
				logger.info("YesBank UPI : Double Verification Response match.PG_REF_NUM : "+pgRefNum);
				return true;
			} else {
				logger.info("YesBank UPI : Double Verification Response does not match.PG_REF_NUM : "+pgRefNum);
			}

		} catch (Exception e) {
			logger.error("Error in YesBank UPI : Double Verification = " + e);
		}

		return false;

	}

	public void updateStatusResponse(String receivedResponseCode, String receivedResponse) throws SystemException {
		try {
			logger.info(" inside YESBANKCB Response action in  updateStatusResponse method response code is ==  "
					+ receivedResponseCode);
			if (receivedResponseCode.equals(Constants.YES_UPI_SUCCESS_CODE.getValue())
					&& receivedResponse.equals(Constants.YES_UPI_RESPONSE.getValue())) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
			} else {
				if (StringUtils.isNotBlank(receivedResponseCode)) {
					YesbankCbUpiResultType resultInstance = YesbankCbUpiResultType
							.getInstanceFromName(receivedResponseCode);
					logger.info(
							" inside YESBANKCB Response action in  updateStatusResponse method resultInstance is : == "
									+ resultInstance);
					if (resultInstance != null) {
						if (resultInstance.getiPayCode() != null) {
							logger.info(
									" inside YESBANKCB Response action in  updateStatusResponse method resultInstance is ==  "
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
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e, "unknown exception in  yesUpiResponseAction");
		}
	}
}
