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
import com.pay10.pg.core.util.HdfcUpiUtil;

@Service
public class SbiUpiStatusEnquiryProcessor {

	@Autowired
	@Qualifier("sbiUpiTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("sbiUpiTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("wssbiUpiUtils")
	private SbiUtils sbiUtils;

	@Autowired
	@Qualifier("sbiUpiTransformer")
	private SbiUpiTransformer sbiUpiTransformer;

	private static Logger logger = LoggerFactory.getLogger(SbiUpiStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) throws SystemException {
		JSONObject request = statusEnquiryRequest(fields);
		Transaction statusEnquiryResponse = new Transaction();
		String response = null;
		Transaction tHandshake = sbiUtils.getClientSecret(fields.get(FieldType.ACQUIRER_TYPE.getName()));
		String clientSercret = tHandshake.getClientSecret();
		String clientId = tHandshake.getClientId();
		String access_token = "";
		try {
		if (StringUtils.isNotBlank(clientSercret)) {
			// token refresh ....
			tHandshake = refreshToken(fields, tHandshake);
			access_token = tHandshake.getAccessToken();
			logger.info("add token in table access_token : "+access_token);
			if (StringUtils.isNotBlank(access_token)) {
				tHandshake.setClientId(clientId);
				tHandshake.setClientSecret(clientSercret);
				tHandshake.setAcquireName(fields.get(FieldType.ACQUIRER_TYPE.getName()));
				logger.info("update token in table");
				sbiUtils.updateToken(tHandshake);
			}

		}
		
			response = communicator.getResponse(request, fields,access_token);
			statusEnquiryResponse = converter.toTransactionStatusEnquiry(response, fields);
			updateFields(fields, statusEnquiryResponse);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

	}
	public Transaction refreshToken(Fields fields, Transaction ttoken) throws SystemException {

		String token = "";
		Transaction tRefreshToken = new Transaction();
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.SBI_UPI_ACCESS_TOKEN_URL);
		String response = communicator.getRefreshTokenResponse(ttoken, fields, hostUrl);
		if (StringUtils.isNotBlank(response)) {
			tRefreshToken = converter.toTransactionRefreshToken(response, fields);

		}

		return tRefreshToken;
	}
	public JSONObject statusEnquiryRequest(Fields fields) throws SystemException {
		JSONObject json = new JSONObject();
		json = converter.statusEnquiryRequest(fields);
		return json;

	}

	public void updateFields(Fields fields, Transaction transactionResponse) throws SystemException {
		sbiUpiTransformer.updateResponse(fields, transactionResponse);

	}

}