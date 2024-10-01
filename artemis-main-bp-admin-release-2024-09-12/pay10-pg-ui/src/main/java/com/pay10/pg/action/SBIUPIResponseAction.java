package com.pay10.pg.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
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
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.SBIUpiResultType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.SBIUpiConstants;
import com.pay10.pg.core.util.SBIUpiUtils;
import com.pay10.pg.core.util.UpiHistorian;

public class SBIUPIResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(SBIUPIResponseAction.class.getName());

	private static final long serialVersionUID = 2382296172065463916L;

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
	private SBIUpiUtils sbiUpiUtil;

	@Autowired
	private UpiHistorian upiHistorian;

	@Autowired
	AWSEncryptDecryptService awsEncryptDecryptService;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		httpRequest = request;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {

		logger.info("SBI UPI : METHOD : SBIUPIResponseAction : inside the sbi upi callback ");
		Fields responseField = null;
				
		try {
			httpRequest.setCharacterEncoding("UTF-8");
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> requestMap = new HashMap<String, String>();

			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey(), entry.getValue()[0]);

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}

			logger.info("SBI UPI : METHOD : SBIUPIResponseAction : SBI UPI CALL BACK RESPONSE response: " + requestMap.get(SBIUpiConstants.RESPONSEMSG));
			String encrypted = requestMap.get(SBIUpiConstants.RESPONSEMSG);
			//String encrypted = resStr.substring(4, resStr.length() - 1);
			Fields fields = new Fields();
			JSONObject resp = new JSONObject(encrypted);
			String decryptedString = sbiUpiUtil.encryption(resp.get(SBIUpiConstants.RESP).toString(), fields,SBIUpiConstants.DECRYPTION_TYPE);
			logger.info("SBI UPI : METHOD : SBIUPIResponseAction : SBI UPI CALL BACK RESPONSE : " + decryptedString);
			resp = new JSONObject(decryptedString);

			String upiTransRefNo = "";
			String pspRefNo = "";
			String amount = "";
			String txnAuthDate = "";
			String status = "";
			String statusDesc = "";
			String responseCode = "";
			String approvalNumber = "";
			String payerVPA = "";
			String payeeVPA = "";
			String custRefNo = "";
			String npciTransId = "";
			String txn_type = "";
			String errCode = "";
			String txn_note = "";
			String payer_name = "";
			String addInfo = "";

			if (decryptedString.contains(SBIUpiConstants.APIRESP)) {
				resp = new JSONObject(resp.get(SBIUpiConstants.APIRESP).toString());

				if (decryptedString.contains(SBIUpiConstants.PSPREFNO)) {
					pspRefNo = resp.get(SBIUpiConstants.PSPREFNO).toString();
				}
				if (decryptedString.contains(SBIUpiConstants.UPITRANSREFNO)) {
					upiTransRefNo = resp.get(SBIUpiConstants.UPITRANSREFNO).toString();
				}
				if (decryptedString.contains(SBIUpiConstants.NPCITRANSID)) {
					npciTransId = resp.get(SBIUpiConstants.NPCITRANSID).toString();
				}
				if (decryptedString.contains(SBIUpiConstants.CUSTREFNO)) {
					custRefNo = resp.get(SBIUpiConstants.CUSTREFNO).toString();
				}
				if (decryptedString.contains(SBIUpiConstants.AMOUNT)) {
					amount = resp.get(SBIUpiConstants.AMOUNT).toString();
				}
				if (decryptedString.contains(SBIUpiConstants.TXNAUTHDATE)) {
					txnAuthDate = resp.get(SBIUpiConstants.TXNAUTHDATE).toString();
				}
				if (decryptedString.contains(SBIUpiConstants.STATUS)) {
					status = resp.get(SBIUpiConstants.STATUS).toString();
				}
				if (decryptedString.contains(SBIUpiConstants.STATUSDESC)) {
					statusDesc = resp.get(SBIUpiConstants.STATUSDESC).toString();
				}
//				if (decryptedString.contains(SBIUpiConstants.ADDINFO2)) {
//					transaction.setAddInfo2(resp.get(SBIUpiConstants.ADDINFO2).toString());
//				}
				if (decryptedString.contains(SBIUpiConstants.PAYERVPA)) {
					payerVPA = resp.get(SBIUpiConstants.PAYERVPA).toString();
				}
				if (decryptedString.contains(SBIUpiConstants.PAYEEVPA)) {
					payeeVPA = resp.get(SBIUpiConstants.PAYEEVPA).toString();
				}
			}

			
			//Added by Deep return false if no entry need to add
			boolean checkForMultipleStatus=fieldsDao.findTransactionForPgRefNoStatusWiseForSBIUPI(pspRefNo);
			if(!checkForMultipleStatus) {
				return Action.NONE;
			}
			
			// dual verification by status enquiry
			boolean dualVerification = statusEnquiry(pspRefNo, status);
			
			if(StringUtils.isNotBlank(status) && (status.equalsIgnoreCase("T") || status.equalsIgnoreCase("P"))) {
				logger.info("Inside if block in SBIUPIResponseAction, status={},pspRefNo={} ",status,pspRefNo);
				sendAcknowledgement(pspRefNo);
				return Action.NONE;
			}
			updateStatusResponse(status);

			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.SBI.getCode());
			fields.put(FieldType.PG_REF_NUM.getName(), pspRefNo);
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			logger.info("fields before historian " + fields.getFieldsAsString());
			upiHistorian.findPrevious(fields);
			logger.info("After historian " + fields.getFieldsAsString());
			logger.info("Status After Update : " + this.status);
			fields.put(FieldType.STATUS.getName(), this.status);
			fields.put(FieldType.PG_RESP_CODE.getName(), errorType.getCode());
			fields.put(FieldType.PG_TXN_STATUS.getName(), statusDesc);
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
			fields.put(FieldType.UDF1.getName(), payerVPA);
			fields.put(FieldType.ACQ_ID.getName(), npciTransId);
			fields.put(FieldType.RRN.getName(), custRefNo);
			fields.put(FieldType.PG_DATE_TIME.getName(), txnAuthDate);
			fields.put(FieldType.AUTH_CODE.getName(), upiTransRefNo);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			if (dualVerification) {
				fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.toString().replaceAll("_", ""));
			} else {
				fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
			}
			logger.info("SBI UPI : METHOD : sbiupicallback : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   SBI UPI CALL BACK RESPONSE : " + fields.getFieldsAsString());

			
			Map<String, String> res = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_UPI_PROCESSOR.getValue());
			res.remove(FieldType.ORIG_TXN_ID.getName());

			responseField = new Fields(res);
			res.remove(FieldType.ORIG_TXN_ID.getName());

			sendAcknowledgement(pspRefNo);
			
			return Action.NONE;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in sbi upi callback 2 = " + e);
		}

		return Action.NONE;
	}

	public boolean statusEnquiry(String pgRefNum, String responseCode) throws SystemException {

		// Skip for unsuccessful transactions if
		if (!responseCode.equalsIgnoreCase(SBIUpiConstants.SUCCESS_RESPONSE)) {
			return true;
		}
		Fields fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);

		fields.put(FieldType.TXNTYPE.getName(), TransactionType.ENQUIRY.getName());
		fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
		fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.SBI.getCode());
		fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");

		Map<String, String> res = new HashMap<String, String>();

		try {

			res = transactionControllerServiceProvider.transact(fields, Constants.TXN_WS_INTERNAL.getValue());
			Fields responseMap = new Fields(res);
			logger.info("SBI UPI : METHOD : Double Verification : PG_REF_NUM : " + pgRefNum
					+ "   SBI UPI CALL BACK RESPONSE : " + responseMap.get(FieldType.RESPONSE_MESSAGE.getName()));
			if (SBIUpiConstants.SBI_ENQUIRY_SUCCESS_MESSAGE
					.equalsIgnoreCase(responseMap.get(FieldType.RESPONSE_MESSAGE.getName()))) {
				logger.info("sbi upi UPI : Double Verification Response match.PG_REF_NUM : " + pgRefNum);
				return true;
			} else {
				logger.info("sbi upi : Double Verification Response does not match.PG_REF_NUM : " + pgRefNum);
			}

		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : Double Verification : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   EXCEPTION Double Verification  : "
					+ e.getMessage());
		}

		return false;

	}

	public void updateStatusResponse(String receivedResponseCode) throws SystemException {
		try {
			logger.info(" inside sbi upi Response action in  updateStatusResponse method response code is ==  "
					+ receivedResponseCode);
			if (receivedResponseCode.equals(SBIUpiConstants.SUCCESS_RESPONSE)) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
			} else {
				if (StringUtils.isNotBlank(receivedResponseCode)) {
					SBIUpiResultType resultInstance = SBIUpiResultType.getInstanceFromName(receivedResponseCode);
					logger.info(
							" inside sbi upi Response action in  updateStatusResponse method resultInstance is : == "
									+ resultInstance);
					if (resultInstance != null) {
						if (resultInstance.getiPayCode() != null) {
							logger.info(
									" inside sbi upi Response action in  updateStatusResponse method resultInstance is ==  "
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
			logger.info(
					"SBI UPI : METHOD : updateStatusResponse :  EXCEPTION updateStatusResponse  : " + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in   sbiUpiResponseAction :updateStatusResponse");
		}
	}
	
	private void sendAcknowledgement(String pgRefNum) throws IOException, SystemException {
		logger.info("Send Acknowledgement For SBI-UPI Call Back Response");
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("pspRefNo", pgRefNum);
		jsonObj.put("status", "SUCCESS");
		jsonObj.put("message", "Request Processed Successfully");
		Fields fields = new Fields();
		fields.put(FieldType.PG_REF_NUM.getName(),pgRefNum);
		String ecrypt_data = sbiUpiUtil.encryption(jsonObj.toString(), fields, "ENC");
		JSONObject finalJson = new JSONObject();
		finalJson.put("msg", new JSONObject().put("resp", ecrypt_data));
		HttpServletResponse httpResponse = ServletActionContext.getResponse();
		httpResponse.setContentType("application/x-www-form-urlencoded");
		PrintWriter out = httpResponse.getWriter();
		out.write(finalJson.toString());
		out.flush();
		out.close();
	}
}
