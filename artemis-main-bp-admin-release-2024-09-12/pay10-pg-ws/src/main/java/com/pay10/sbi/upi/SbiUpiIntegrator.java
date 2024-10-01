package com.pay10.sbi.upi;

import org.apache.commons.lang.StringUtils;
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

@Service
public class SbiUpiIntegrator {
	private static Logger logger = LoggerFactory.getLogger(SbiUpiIntegrator.class.getName());

	@Autowired
	@Qualifier("sbiUpiTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("sbiUpiFactory")
	private TransactionFactory transactionFactory;

	@Autowired
	@Qualifier("sbiUpiTransformer")
	private SbiUpiTransformer upiTransformer;

	@Autowired
	@Qualifier("sbiUpiTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("wssbiUpiUtils")
	private SbiUtils sbiUtils;

	public static void main(String[] str) {
		SbiUpiIntegrator sb = new SbiUpiIntegrator();

	}

	public void process(Fields fields) throws SystemException {

		logger.info("SBI UPI : Method : Process : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName()));
		transactionFactory.getInstance(fields);
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		}
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		if (pgRefNo == null) {
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
		}
		String transactionType = fields.get(FieldType.TXNTYPE.getName());
		if (transactionType.equals(TransactionType.REFUND.getName())) {
			updateRefundresponse(fields);
		} else {
			// handsheking and generate token
			Transaction tHandshake = sbiUtils.getClientSecret(fields.get(FieldType.ACQUIRER_TYPE.getName()));
			String clientSercret = tHandshake.getClientSecret();
			String clientId = tHandshake.getClientId();
			logger.info("add token in table clientSercret : " + clientSercret);
			if (StringUtils.isBlank(clientSercret)) {
				clientId = sbiUtils.getClientId();
				tHandshake = handShaking(fields, clientId);

				if (tHandshake.getStatus().equals("S")) {
					tHandshake.setClientId(clientId);
					tHandshake.setAcquireName(fields.get(FieldType.ACQUIRER_TYPE.getName()));
					clientSercret = tHandshake.getClientSecret();
					logger.info("add token in table");
					sbiUtils.saveToken(tHandshake);
				} else {
					upiTransformer.updateInvalidHandshakeResponse(fields, tHandshake.getStatusDesc());

				}
			}
			String access_token = "";
			if (StringUtils.isNotBlank(clientSercret)) {
				// token refresh ....
				tHandshake = refreshToken(fields, tHandshake);
				access_token = tHandshake.getAccessToken();
				logger.info("add token in table access_token : " + access_token);
				if (StringUtils.isNotBlank(access_token)) {
					tHandshake.setClientId(clientId);
					tHandshake.setClientSecret(clientSercret);
					tHandshake.setAcquireName(fields.get(FieldType.ACQUIRER_TYPE.getName()));
					logger.info("update token in table");
					sbiUtils.updateToken(tHandshake);
				}

			}
			logger.info("token for vpa : " + tHandshake.getRefreshToken());
			if (StringUtils.isNotBlank(tHandshake.getRefreshToken())) {
				if (transactionType.equals(TransactionType.SALE.getName())) {
					String vpaStatus = vpaValidation(fields, access_token);
					if (StringUtils.isNotBlank(vpaStatus)) {
						if (vpaStatus.equalsIgnoreCase(Constants.VPA_SUCCESSFULLY_STATUS_CODE)) {
							send(fields, access_token);
						} else {
							upiTransformer.updateInvalidVpaResponse(fields, vpaStatus);
						}
					}
				} else {
					send(fields, access_token);
				}
			}
		}

	}

	public Transaction handShaking(Fields fields, String clientId) throws SystemException {
		Transaction tHandshake = new Transaction();
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.SBI_UPI_HANDSHAKE_URL);
		JSONObject handShakingRequest = converter.handShakeRequest(fields, clientId);

		String response = communicator.getHandShakeResponse(handShakingRequest, fields, hostUrl);
		if (StringUtils.isNotBlank(response)) {
			tHandshake = converter.toTransactionHandShake(response, fields);

		}
		return tHandshake;
	}

	public Transaction refreshToken(Fields fields, Transaction ttoken) throws SystemException {
//		
//		{
//		    "error": "invalid_token",
//		    "error_description": "Access token expired: 28f39c85-c6cc-41ec-b816-591c65a38108"
//		}
		String token = "";
		Transaction tRefreshToken = new Transaction();
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.SBI_UPI_ACCESS_TOKEN_URL);
		String response = communicator.getRefreshTokenResponse(ttoken, fields, hostUrl);
		if (StringUtils.isNotBlank(response)) {
			tRefreshToken = converter.toTransactionRefreshToken(response, fields);

		}

		return tRefreshToken;
	}

	public String vpaValidation(Fields fields, String access_token) throws SystemException {
		String vpaStatus = "";
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.SBI_UPI_VPA_URL);
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		if (pgRefNo == null) {
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
		}
		JSONObject vpaRequest = converter.vpaValidatorRequest(fields);
		String encryptedVpaResponse = communicator.getVpaResponse(vpaRequest, fields, hostUrl, access_token);
		logger.info("vpa validation API response" + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
				+ fields.get(FieldType.TXN_ID.getName()) + " " + encryptedVpaResponse);
		if (StringUtils.isNotBlank(encryptedVpaResponse)) {
			logger.info("Collect API  VPA Response, if response is decrypted " + fields.get(FieldType.TXNTYPE.getName())
					+ " " + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedVpaResponse);
			Transaction txnVpa = converter.toVpaTransaction(encryptedVpaResponse, fields);
			vpaStatus = txnVpa.getStatus();

		}

		return vpaStatus;
	}



	public boolean vpaValidation(Fields fields) throws SystemException {
		boolean success =false;
		String vpaStatus = "";
		// handsheking and generate token
		Transaction tHandshake = sbiUtils.getClientSecret(fields.get(FieldType.ACQUIRER_TYPE.getName()));
		String clientSercret = tHandshake.getClientSecret();
		String clientId = tHandshake.getClientId();
		logger.info("add token in table clientSercret : " + clientSercret);
		if (StringUtils.isBlank(clientSercret)) {
			clientId = sbiUtils.getClientId();
			tHandshake = handShaking(fields, clientId);

			if (tHandshake.getStatus().equals("S")) {
				tHandshake.setClientId(clientId);
				tHandshake.setAcquireName(fields.get(FieldType.ACQUIRER_TYPE.getName()));
				clientSercret = tHandshake.getClientSecret();
				logger.info("add token in table");
				sbiUtils.saveToken(tHandshake);
			} else {
				upiTransformer.updateInvalidHandshakeResponse(fields, tHandshake.getStatusDesc());

			}
		}
		String access_token = "";
		if (StringUtils.isNotBlank(clientSercret)) {
			// token refresh ....
			tHandshake = refreshToken(fields, tHandshake);
			access_token = tHandshake.getAccessToken();
			logger.info("add token in table access_token : " + access_token);
			if (StringUtils.isNotBlank(access_token)) {
				tHandshake.setClientId(clientId);
				tHandshake.setClientSecret(clientSercret);
				tHandshake.setAcquireName(fields.get(FieldType.ACQUIRER_TYPE.getName()));
				logger.info("update token in table");
				sbiUtils.updateToken(tHandshake);
			}

		}
		logger.info("token for vpa : " + tHandshake.getRefreshToken());
		if (StringUtils.isNotBlank(tHandshake.getRefreshToken())) {
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.SBI_UPI_VPA_URL);
			String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
			if (pgRefNo == null) {
				fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			}
			JSONObject vpaRequest = converter.vpaValidatorRequest(fields);
			String encryptedVpaResponse = communicator.getVpaResponse(vpaRequest, fields, hostUrl, access_token);
			logger.info("vpa validation API response" + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
					+ fields.get(FieldType.TXN_ID.getName()) + " " + encryptedVpaResponse);
			if (StringUtils.isNotBlank(encryptedVpaResponse)) {
				logger.info("Collect API  VPA Response, if response is decrypted " + fields.get(FieldType.TXNTYPE.getName())
						+ " " + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedVpaResponse);
				Transaction txnVpa = converter.toVpaTransaction(encryptedVpaResponse, fields);
				vpaStatus = txnVpa.getStatus();
			}
		}

		if (StringUtils.isNotBlank(vpaStatus)) {
			if (vpaStatus.equalsIgnoreCase(Constants.VPA_SUCCESSFULLY_STATUS_CODE)) {
				success =true;
			}
		}

		return success;
	}

	public void send(Fields fields, String access_token) throws SystemException {
		Transaction transactionResponse = new Transaction();
		logger.info("  sbi upi send method : "+fields.get(FieldType.TXNTYPE.getName()));
		JSONObject request = converter.perpareRequest(fields);
		String encryptedResponse = communicator.getResponse(request, fields, access_token);
		
		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case SALE:

			if (StringUtils.isNotBlank(encryptedResponse)) {
				transactionResponse = converter.toTransaction(encryptedResponse, fields);
				upiTransformer.updateResponse(fields, transactionResponse);
				break;
			} else {
				logger.info(
						"Collect API  Collect Response, if response is blank " + fields.get(FieldType.TXNTYPE.getName())
								+ " " + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedResponse);
				upiTransformer.updateResponse(fields, transactionResponse);
				break;
			}

		case ENQUIRY:
			if (StringUtils.isNotBlank(encryptedResponse)) {
				logger.info("Collect API  ENQUIRY Response, if response is decrypted :  "
						+ fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
						+ fields.get(FieldType.TXN_ID.getName()) + " " + encryptedResponse);
				transactionResponse = converter.toTransactionStatusEnquiry(encryptedResponse, fields);
				upiTransformer.updateResponse(fields, transactionResponse);
				break;
			} else {
				logger.info("Collect API  Status enquiry Response, if response is blank = "
						+ fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
						+ fields.get(FieldType.TXN_ID.getName()) + " " + encryptedResponse);
				upiTransformer.updateResponse(fields, transactionResponse);
				break;
			}
		default:
			break;
		}
	}

	public void updateRefundresponse(Fields fields) {
		// insert dummy entry in db to generate refund file....
		Transaction transactionResponse = new Transaction();
		try {
			logger.info("REFUND Response, if response is blank = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id" + fields.get(FieldType.TXN_ID.getName()));

			transactionResponse = converter.toRefundTransaction(fields);
			upiTransformer.updateResponse(fields, transactionResponse);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}