package com.pay10.paytm;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.paytm.util.CheckSumServiceHelper;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

/**
 * @author Shaiwal
 *
 */
@Service("paytmTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			break;
		case REFUND:
			request = refundRequest(fields, transaction);
			break;
		case SALE:
			request = saleRequest(fields, transaction);
			break;
		}
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		try {

			String requestURL = PropertiesManager.propertiesMap.get("PAYTMSaleUrl");
			TreeMap<String, String> paytmParams = new TreeMap<String, String>();

			paytmParams.put("MID", transaction.getMID());
			paytmParams.put("WEBSITE", transaction.getWEBSITE());
			paytmParams.put("INDUSTRY_TYPE_ID", transaction.getINDUSTRY_TYPE_ID());
			paytmParams.put("CHANNEL_ID", transaction.getCHANNEL_ID());
			paytmParams.put("ORDER_ID", transaction.getORDER_ID());
			paytmParams.put("CUST_ID", transaction.getCUST_ID());
			paytmParams.put("TXN_AMOUNT", transaction.getTXN_AMOUNT());
			paytmParams.put("CALLBACK_URL", transaction.getCALLBACK_URL());

			StringBuilder outputHtml = new StringBuilder();
			outputHtml.append("<html>");
			outputHtml.append("<head>");
			outputHtml.append("<title>Pay10 Merchant Checkout Page</title>");
			outputHtml.append("</head>");
			outputHtml.append("<body>");
			outputHtml.append("<center><h1>Please do not refresh this page...</h1></center>");
			outputHtml.append("<form method='post' action='" + requestURL + "' name='paytm_form'>");

			for (Map.Entry<String, String> entry : paytmParams.entrySet()) {
				outputHtml
						.append("<input type='hidden' name='" + entry.getKey() + "' value='" + entry.getValue() + "'>");
			}

			outputHtml
					.append("<input type='hidden' name='CHECKSUMHASH' value='" + transaction.getCHECKSUMHASH() + "'>");
			outputHtml.append("</form>");
			outputHtml.append("<script type='text/javascript'>");
			outputHtml.append("document.paytm_form.submit();");
			outputHtml.append("</script>");
			outputHtml.append("</body>");
			outputHtml.append("</html>");

			logger.info("Paytm payment request  =  " + outputHtml.toString());

			return outputHtml.toString();
		}

		catch (Exception e) {
			logger.error("Exception in generating Paytm sale request ", e);
		}

		return null;
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		try {
			JSONObject paytmParams = new JSONObject();

			JSONObject body = new JSONObject();
			body.put("mid", transaction.getMid());
			body.put("txnType", Constants.REF_TXN_TYPE);
			body.put("orderId", transaction.getOrderId());
			body.put("txnId", transaction.getTxnId());
			body.put("refId", transaction.getRefId());
			body.put("refundAmount", transaction.getRefundAmount());
			String checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("GmPCBPzl2iRtpgzf",
					body.toString());

			JSONObject head = new JSONObject();
			head.put("clientId", "C11");
			head.put("signature", checksum);

			paytmParams.put("body", body);
			paytmParams.put("head", head);
			String post_data = paytmParams.toString();

			return post_data;
		}

		catch (Exception e) {
			logger.error("Exception in generating paytm refund request", e);
		}
		return null;

	}

	public Transaction toTransaction(String jsonResponse, String txnType) {

		Transaction transaction = new Transaction();

		if (StringUtils.isBlank(jsonResponse)) {

			logger.info("Empty response received for paytm refund");
			return transaction;
		}

		JSONObject respObj = new JSONObject(jsonResponse);

		if (respObj.has("body")) {

			JSONObject respBody = new JSONObject(jsonResponse);
			respBody = (JSONObject) respObj.get("body");

			if (respBody.has("resultInfo")) {

				JSONObject resultInfo = new JSONObject(jsonResponse);
				resultInfo = (JSONObject) respBody.get("resultInfo");

				if (resultInfo.has("resultStatus")) {
						
					String resultStatus = resultInfo.get("resultStatus").toString();
					transaction.setResultStatus(resultStatus);
				}

				if (resultInfo.has("resultCode")) {
					
					String resultCode = resultInfo.get("resultCode").toString();
					transaction.setResultCode(resultCode);
				}

				if (resultInfo.has("resultMsg")) {

					String resultMsg = resultInfo.get("resultMsg").toString();
					transaction.setResultMsg(resultMsg);
				}

			}

			if (respBody.has("txnId")) {

				String txnId = respBody.get("txnId").toString();
				transaction.setTxnId(txnId);
			}

			if (respBody.has("bankTxnId")) {

				String bankTxnId = respBody.get("bankTxnId").toString();
				transaction.setBankTxnId(bankTxnId);
			}

			if (respBody.has("refundId")) {

				String refundId = respBody.get("refundId").toString();
				transaction.setRefundId(refundId);
			}

		}

		return transaction;

	}

	public TransactionConverter() {

	}

}
