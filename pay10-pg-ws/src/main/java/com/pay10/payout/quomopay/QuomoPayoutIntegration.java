package com.pay10.payout.quomopay;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.user.AccountCurrencyPayout;
import com.pay10.commons.user.AccountCurrencyPayoutDao;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Service
public class QuomoPayoutIntegration {
	private static Logger logger = LoggerFactory.getLogger(QuomoPayoutIntegration.class.getName());

	public static String MerchantId = "35540466";

	private static String BASE_URL = "https://www.quomo.digital";
	private static String TRANSFER_URL = "/Payapi_Index_TransdfUser.html";
	private static String BALANACE_ENQUIRY_URL = "/Payapi_Index_PBalance.html";
	private static String BANK_LIST_URL = "/getBankList";
	@Autowired
	QuomoPayoutTransactionConverter quomoPayoutTransactionConverter;

	@Autowired
	AccountCurrencyPayoutDao accountCurrencyPayoutDao;

	@Autowired
	QuomoPayoutResponseHandler quomoPayoutResponseHandler;

	public void process(Fields fields) {
		try {
			AccountCurrencyPayout accountCurrencyRequest = accountCurrencyPayoutDao.getAccountCurrencyPayoutDetail(
					fields.get(FieldType.ACQUIRER_TYPE.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));

			logger.info("Fields inQuomoPayoutIntegration " + fields.getFieldsAsString());
			JSONObject request = quomoPayoutTransactionConverter.getTransferRequest(fields, accountCurrencyRequest);
			logger.info("Request for Transactmoney" + request);
			String Response = quomoPayoutTransactionConverter.getRestApi(request, fields,
					accountCurrencyRequest.getAdf2());
			logger.info("Response for Transactmoney" + request);
			JSONObject finalResponse = new JSONObject(Response);

			quomoPayoutResponseHandler.getErrorCodeMappingPayout(fields,
					(String) finalResponse.get("responseCode").toString());
			logger.info("Fields inQuomoPayoutIntegration " + fields.getFieldsAsString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void processStatus(Fields fields) {
		try {
			logger.info("Fields inQuomoPayoutIntegration for blancecheck " + fields.getFieldsAsString());
			AccountCurrencyPayout accountCurrencyRequest = accountCurrencyPayoutDao.getAccountCurrencyPayoutDetail(
					fields.get(FieldType.ACQUIRER_TYPE.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));

			JSONObject request = quomoPayoutTransactionConverter.checkBlance(fields, accountCurrencyRequest);
			logger.info("Request for blanceCheck" + request);
			String response;

			response = quomoPayoutTransactionConverter.getRestApi(request, fields, accountCurrencyRequest.getAdf2());
			logger.info("Request for blanceCheck" + response);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
