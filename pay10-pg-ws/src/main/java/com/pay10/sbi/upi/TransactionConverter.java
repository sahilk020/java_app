package com.pay10.sbi.upi;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.SBIUpiUtils;

@Service("sbiUpiTransactionConverter")
public class TransactionConverter {
	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;
	@Autowired
	@Qualifier("sbiUpiUtils")
	private SBIUpiUtils sbiUpiUtil;

	@SuppressWarnings("incomplete-switch")
	public JSONObject perpareRequest(Fields fields) throws SystemException {

		JSONObject request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case REFUND:
			break;
		case SALE:
			request = collectRequest(fields);
			break;
		case ENQUIRY:
			request = statusEnquiryRequest(fields);
			break;
		}
		return request;

	}

	public JSONObject handShakeRequest(Fields fields, String client_id) throws SystemException {
		JSONObject request = new JSONObject();
		try {
			JSONObject json = new JSONObject();
			JSONObject req_info_details = new JSONObject();
			req_info_details.put(Constants.PGMERCHANTID, fields.get(FieldType.MERCHANT_ID.getName()));
			req_info_details.put(Constants.PSPREFNO, fields.get(FieldType.PG_REF_NUM.getName()));
			json.put(Constants.REQUESTINFO, req_info_details);
			JSONObject device_info_details = new JSONObject();
			device_info_details.put(Constants.CLIENTID, client_id);
			json.put(Constants.DEVICEINFO, device_info_details);
			String ecrypt_data = "";
			/// encrypted data
			logger.info("SBI UPI : METHOD : handShakeRequest : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   HANDSHAKING REQUEST : " + json.toString());
			ecrypt_data = sbiUpiUtil.encryption(json.toString(), fields, Constants.ENCRYPTION_TYPE);
			request.put(Constants.REQUESTMSG, ecrypt_data);
			request.put(Constants.PGMERCHANTID, fields.get(FieldType.MERCHANT_ID.getName()));
			logger.info("handshaking encrypt request : " + request.toString());
		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : handShakeRequest : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   EXCEPTION HANDSHAKING : " + e.getMessage());
		}
		return request;
	}

	public JSONObject vpaValidatorRequest(Fields fields) throws SystemException {
		JSONObject request = new JSONObject();
		try {
			JSONObject json = new JSONObject();
			JSONObject req_info_details = new JSONObject();
			req_info_details.put(Constants.PGMERCHANTID, fields.get(FieldType.MERCHANT_ID.getName()));
			req_info_details.put(Constants.PSPREFNO, fields.get(FieldType.PG_REF_NUM.getName()));
			json.put(Constants.REQUESTINFO, req_info_details);
			JSONObject payeeType_details = new JSONObject();
			payeeType_details.put(Constants.VIRTUALADRESS, fields.get(FieldType.PAYER_ADDRESS.getName()));
			json.put(Constants.PAYEEtYPE, payeeType_details);
			json.put(Constants.VAREQTYPE, Constants.VAREQTYPE_VAL);

			/// encrypted data
			String ecrypt_data = "";
			logger.info("SBI UPI : METHOD : vpaValidatorRequest : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   VPA VALIDATE REQUEST : " + json.toString());
			ecrypt_data = sbiUpiUtil.encryption(json.toString(), fields, Constants.ENCRYPTION_TYPE);
			request.put(Constants.REQUESTMSG, ecrypt_data);
			//TODO use ADF4 for pgmerchantId
			//request.put(Constants.PGMERCHANTID, fields.get(FieldType.MERCHANT_ID.getName()));
			request.put(Constants.PGMERCHANTID, fields.get(FieldType.ADF4.getName()));
			logger.info("vpa validation encrypt request : " + request.toString());
		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : vpaValidatorRequest : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   EXCEPTION VPA VALIDATE : " + e.getMessage());
		}
		return request;
	}

	public JSONObject collectRequest(Fields fields) throws SystemException {
		JSONObject request = new JSONObject();

		try {
			String expiryTime = PropertiesManager.propertiesMap.get(Constants.EXPIRY_VALUE);
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			JSONObject json = new JSONObject();
			JSONObject req_info_details = new JSONObject();
			req_info_details.put(Constants.PGMERCHANTID, fields.get(FieldType.MERCHANT_ID.getName()));
			req_info_details.put(Constants.PSPREFNO, fields.get(FieldType.PG_REF_NUM.getName()));
			json.put(Constants.REQUESTINFO, req_info_details);
			json.put(Constants.AMOUNT, amount);
			json.put(Constants.EXPIRYTIME, expiryTime);
			JSONObject payerType_details = new JSONObject();
			payerType_details.put(Constants.VIRTUALADRESS, fields.get(FieldType.PAYER_ADDRESS.getName()));
			json.put(Constants.PAYERTYPE, payerType_details);
			String prod_desc = fields.get(FieldType.PRODUCT_DESC.getName());
			if (StringUtils.isBlank(prod_desc)) {
				json.put(Constants.TRANSACTIONNOTE, Constants.PRODUCT_DESC);
			} else {
				json.put(Constants.TRANSACTIONNOTE, prod_desc);
			}

			JSONObject addinfo_details = new JSONObject();
			payerType_details.put(Constants.ADDINFO09, Constants.LAST_VALUE);
			payerType_details.put(Constants.ADDINFO10, Constants.LAST_VALUE);
			json.put(Constants.ADDINFO, addinfo_details);
			logger.info("SBI UPI : METHOD : collectRequest : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   COLLECT PAY REQUEST : " + json.toString());
			/// encrypted data
			String ecrypt_data = "";
			ecrypt_data = sbiUpiUtil.encryption(json.toString(), fields, Constants.ENCRYPTION_TYPE);
			request.put(Constants.REQUESTMSG, ecrypt_data);
			//request.put(Constants.PGMERCHANTID, fields.get(FieldType.MERCHANT_ID.getName()));
			//TODO use ADF4 for pgmerchantId
			request.put(Constants.PGMERCHANTID, fields.get(FieldType.ADF4.getName()));
		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : collectRequest : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   EXCEPTION COLLECT PAY : " + e.getMessage());
		}
		fields.put(FieldType.CARD_MASK.getName(), fields.get(FieldType.PAYER_ADDRESS.getName())); // show in txn reports
		return request;

	}

	public JSONObject statusEnquiryRequest(Fields fields) throws SystemException {

		JSONObject request = new JSONObject();
		try {
			JSONObject json = new JSONObject();
			JSONObject req_info_details = new JSONObject();
			req_info_details.put(Constants.PGMERCHANTID, fields.get(FieldType.MERCHANT_ID.getName()));
			req_info_details.put(Constants.PSPREFNO, fields.get(FieldType.PG_REF_NUM.getName()));
			json.put(Constants.REQUESTINFO, req_info_details);
			// json.put(Constants.CUSTREFNO, "");
			logger.info("SBI UPI : METHOD : Double Verification Request : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   STATUS ENQUIRY REQUEST : " + json.toString());
			/// encrypted data
			String ecrypt_data = "";
			ecrypt_data = sbiUpiUtil.encryption(json.toString(), fields, Constants.ENCRYPTION_TYPE);
			request.put(Constants.REQUESTMSG, ecrypt_data);
			//TODO use ADF4 for pgmerchantId
			//request.put(Constants.PGMERCHANTID, fields.get(FieldType.MERCHANT_ID.getName()));
			request.put(Constants.PGMERCHANTID, fields.get(FieldType.ADF4.getName()));
		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : Double Verification Request : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   EXCEPTION STATUS ENQUIRY : " + e.getMessage());
		}
		return request;
	}

	public Transaction toTransactionHandShake(String response, Fields fields) throws SystemException {
		Transaction transaction = new Transaction();
		try {
			JSONObject resp = new JSONObject(response);
			String ecrypt_data = sbiUpiUtil.encryption(resp.get(Constants.RESP).toString(), fields,
					Constants.DECRYPTION_TYPE);
			logger.info("SBI UPI : METHOD : handShakeRequest : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   HANDSHAKING RESPONSE : " + ecrypt_data);
			resp = new JSONObject(ecrypt_data);
			if (ecrypt_data.contains(Constants.STATUS)) {
				transaction.setStatus(resp.get(Constants.STATUS).toString());
			}
			if (ecrypt_data.contains(Constants.STATUSDESC)) {
				transaction.setStatusDesc(resp.get(Constants.STATUSDESC).toString());
			}
			if (ecrypt_data.contains(Constants.CLIENTSECRET)) {
				transaction.setClientSecret(resp.get(Constants.CLIENTSECRET).toString());
			}
			if (ecrypt_data.contains(Constants.PSPRESPREFNO)) {
				transaction.setPspRespRefNo(resp.get(Constants.PSPRESPREFNO).toString());
			}
			if (ecrypt_data.contains(Constants.SAFETYNETFLAG)) {
				transaction.setSafetyNetFlag(resp.get(Constants.SAFETYNETFLAG).toString());
			}

			if (ecrypt_data.contains(Constants.ADDINFO)) {
				resp = new JSONObject(resp.get(Constants.ADDINFO).toString());
				if (ecrypt_data.contains(Constants.ADDINFO1)) {
					transaction.setAddInfo1(resp.get(Constants.ADDINFO1).toString());
				}
			}

		} catch (Exception e) {
			logger.info(
					"SBI UPI : METHOD : handShakeRequest : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
							+ "   EXCEPTION HANDSHAKE RESPONSE : " + e.getMessage());
		}

		return transaction;
	}

	public Transaction toTransactionRefreshToken(String response, Fields fields) throws SystemException {
		Transaction transaction = new Transaction();
		try {
			logger.info("SBI UPI : METHOD : Refreshtoken : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   REFRESH TOKEN RESPONSE : " + response);
			JSONObject resp = new JSONObject(response);
			if (response.contains(Constants.REFRESH_TOKEN_ERROR)) {
				transaction.setRefreshTokenError(resp.get(Constants.REFRESH_TOKEN_ERROR).toString());
			}

			if (response.contains(Constants.ACCESS_TOKEN)) {
				transaction.setAccessToken(resp.get(Constants.ACCESS_TOKEN).toString());
			}
			if (response.contains(Constants.TOKEN_TYPE)) {
				transaction.setTokenType(resp.get(Constants.TOKEN_TYPE).toString());
			}
			if (response.contains(Constants.REFRESH_TOKEN)) {
				transaction.setRefreshToken(resp.get(Constants.REFRESH_TOKEN).toString());
			}
			if (response.contains(Constants.EXPIRES_IN)) {
				transaction.setExpireIn(resp.get(Constants.EXPIRES_IN).toString());
			}
		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : refreshtoken : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   EXCEPTION REFRESH TOKEN RESPONSE : " + e.getMessage());
		}
		return transaction;
	}

	public Transaction toTransactionStatusEnquiry(String response, Fields fields) throws SystemException {

		Transaction transaction = new Transaction();
		try {
			JSONObject resp = new JSONObject(response);
			String ecrypt_data = sbiUpiUtil.encryption(resp.get(Constants.RESP).toString(), fields,
					Constants.DECRYPTION_TYPE);
			logger.info("SBI UPI : METHOD : Double Verification : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   Double Verification Response : " + ecrypt_data);
			resp = new JSONObject(ecrypt_data);
			if (ecrypt_data.contains(Constants.APIRESP)) {
				resp = new JSONObject(resp.get(Constants.APIRESP).toString());

				if (ecrypt_data.contains(Constants.PSPREFNO)) {
					transaction.setPspRespRefNo(resp.get(Constants.PSPREFNO).toString());
				}
				if (ecrypt_data.contains(Constants.UPITRANSREFNO)) {
					transaction.setUpiTransRefNo(resp.get(Constants.UPITRANSREFNO).toString());
				}
				if (ecrypt_data.contains(Constants.NPCITRANSID)) {
					transaction.setNpciTransId(resp.get(Constants.NPCITRANSID).toString());
				}
				if (ecrypt_data.contains(Constants.CUSTREFNO)) {
					transaction.setCustRefNo(resp.get(Constants.CUSTREFNO).toString());
				}
				if (ecrypt_data.contains(Constants.AMOUNT)) {
					transaction.setAmount(resp.get(Constants.AMOUNT).toString());
				}
				if (ecrypt_data.contains(Constants.TXNAUTHDATE)) {
					transaction.setTxnAuthDate(resp.get(Constants.TXNAUTHDATE).toString());
				}
				if (ecrypt_data.contains(Constants.RESPONSECODE)) {
					transaction.setResponseCode(resp.get(Constants.RESPONSECODE).toString());
				}
				if (ecrypt_data.contains(Constants.STATUS)) {
					transaction.setStatus(resp.get(Constants.STATUS).toString());
				}
				if (ecrypt_data.contains(Constants.STATUSDESC)) {
					transaction.setStatusDesc(resp.get(Constants.STATUSDESC).toString());
				}
//			if (response.contains(Constants.ADDINFO2)) {
//				transaction.setAddInfo2(resp.get(Constants.ADDINFO2).toString());
//			}
				if (ecrypt_data.contains(Constants.PAYERVPA)) {
					transaction.setPayerVPA(resp.get(Constants.PAYERVPA).toString());
				}
				if (ecrypt_data.contains(Constants.PAYEEVPA)) {
					transaction.setPayeeVPA(resp.get(Constants.PAYEEVPA).toString());
				}

				if (ecrypt_data.contains(Constants.TXN_TYPE)) {
					transaction.setTxnType(resp.get(Constants.TXN_TYPE).toString());
				}
				if (ecrypt_data.contains(Constants.TXN_NOTE)) {
					transaction.setTxnNote(resp.get(Constants.TXN_NOTE).toString());
				}
				if (ecrypt_data.contains(Constants.ERRCODE)) {
					transaction.setErrCode(resp.get(Constants.ERRCODE).toString());
				}
				if (ecrypt_data.contains(Constants.APPROVALNUMBER)) {
					transaction.setApprovalNumber(resp.get(Constants.APPROVALNUMBER).toString());
				}
			}
		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : StatusEnquiryReponse : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   EXCEPTION STATUS ENQUIRY RESPONSE : "
					+ e.getMessage());
		}
		return transaction;

	}

	public Transaction toVpaTransaction(String response, Fields fields) throws SystemException {
		Transaction transaction = new Transaction();
		try {
			JSONObject resp = new JSONObject(response);
			String ecrypt_data = sbiUpiUtil.encryption(resp.get(Constants.RESP).toString(), fields,
					Constants.DECRYPTION_TYPE);
			logger.info("SBI UPI : METHOD : Vpavalidate : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   VPA VALIDATE RESPONSE : " + ecrypt_data);
			resp = new JSONObject(ecrypt_data);
			if (ecrypt_data.contains(Constants.REQUESTINFO)) {
				JSONObject resinfo = new JSONObject(resp.get(Constants.REQUESTINFO).toString());
				transaction.setPspRespRefNo(resinfo.get(Constants.PSPREFNO).toString());
			}
			if (ecrypt_data.contains(Constants.PAYEEtYPE)) {
				JSONObject resinfo = new JSONObject(resp.get(Constants.PAYEEtYPE).toString());
				if (ecrypt_data.contains(Constants.VIRTUALADRESS)) {
					transaction.setPayerVPA(resinfo.get(Constants.VIRTUALADRESS).toString());
				}
				if (ecrypt_data.contains(Constants.PAYERNAME)) {
					transaction.setPayerName(resinfo.get(Constants.PAYERNAME).toString());
					fields.put(FieldType.PAYER_NAME.getName(), resinfo.get(Constants.PAYERNAME).toString());
				}
			}
			if (ecrypt_data.contains(Constants.STATUS)) {
				transaction.setStatus(resp.get(Constants.STATUS).toString());
			}
			if (ecrypt_data.contains(Constants.STATUSDESC)) {
				transaction.setStatusDesc(resp.get(Constants.STATUSDESC).toString());
			}
		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : Vpavalidate : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   EXCEPTION VPA VALIDATE RESPONSE : " + e.getMessage());
		}
		return transaction;

	}

	public Transaction toTransaction(String response, Fields fields) throws SystemException {

		Transaction transaction = new Transaction();
		try {
			JSONObject resp = new JSONObject(response);
			String ecrypt_data = sbiUpiUtil.encryption(resp.get(Constants.RESP).toString(), fields,
					Constants.DECRYPTION_TYPE);
			logger.info("SBI UPI : METHOD : Collectpay : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   COLLECT PAY RESPONSE : " + response);
			resp = new JSONObject(ecrypt_data);
			if (ecrypt_data.contains(Constants.APIRESP)) {
				resp = new JSONObject(resp.get(Constants.APIRESP).toString());

				if (ecrypt_data.contains(Constants.PSPREFNO)) {
					transaction.setPspRespRefNo(resp.get(Constants.PSPREFNO).toString());
				}
				if (ecrypt_data.contains(Constants.UPITRANSREFNO)) {
					transaction.setUpiTransRefNo(resp.get(Constants.UPITRANSREFNO).toString());
				}
				if (ecrypt_data.contains(Constants.NPCITRANSID)) {
					transaction.setNpciTransId(resp.get(Constants.NPCITRANSID).toString());
				}
				if (ecrypt_data.contains(Constants.CUSTREFNO)) {
					transaction.setCustRefNo(resp.get(Constants.CUSTREFNO).toString());
				}
				if (ecrypt_data.contains(Constants.AMOUNT)) {
					transaction.setAmount(resp.get(Constants.AMOUNT).toString());
				}
				if (ecrypt_data.contains(Constants.TXNAUTHDATE)) {
					transaction.setTxnAuthDate(resp.get(Constants.TXNAUTHDATE).toString());
				}
				if (ecrypt_data.contains(Constants.STATUS)) {
					transaction.setStatus(resp.get(Constants.STATUS).toString());
				}
				if (ecrypt_data.contains(Constants.STATUSDESC)) {
					transaction.setStatusDesc(resp.get(Constants.STATUSDESC).toString());
				}
				if (ecrypt_data.contains(Constants.ADDINFO2)) {
					transaction.setAddInfo2(resp.get(Constants.ADDINFO2).toString());
				}
				if (ecrypt_data.contains(Constants.PAYERVPA)) {
					transaction.setPayerVPA(resp.get(Constants.PAYERVPA).toString());
				}
				if (ecrypt_data.contains(Constants.PAYEEVPA)) {
					transaction.setPayeeVPA(resp.get(Constants.PAYEEVPA).toString());
				}
			}
		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : Collectpay  : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   EXCEPTION COLLECT PAY RESPONSE : " + e.getMessage());
		}

		return transaction;
	}

	public Transaction toRefundTransaction(Fields fields) throws SystemException {

		Transaction transaction = new Transaction();
		try {
			logger.info("SBI UPI : METHOD : toRefundTransaction : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()));

			transaction.setUpiTransRefNo(fields.get(FieldType.AUTH_CODE.getName()));
			transaction.setNpciTransId(fields.get(FieldType.ACQ_ID.getName()));
			transaction.setCustRefNo(fields.get(FieldType.RRN.getName()));
			transaction.setAmount(fields.get(FieldType.AMOUNT.getName()));
			transaction.setStatus("S");
			transaction.setStatusDesc("Refund Successfull");
			transaction.setPayerVPA(fields.get(FieldType.PAYER_ADDRESS.getName()));
			transaction.setPayeeVPA(fields.get(FieldType.PAYEE_ADDRESS.getName()));

		} catch (

		Exception e) {
			logger.info("SBI UPI : METHOD : Collectpay  : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   EXCEPTION COLLECT PAY RESPONSE : " + e.getMessage());
		}

		return transaction;
	}

}
