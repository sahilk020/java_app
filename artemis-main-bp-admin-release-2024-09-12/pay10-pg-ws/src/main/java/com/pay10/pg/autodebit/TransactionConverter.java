package com.pay10.pg.autodebit;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.ConstantsIrctc;
import com.pay10.pg.core.util.MerchantIPayUtil;

/**
 * @author Rahul
 *
 */
@Service("atlTransactionConverter")
public class TransactionConverter {
	
	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());
	
	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;
	
	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			break;
		case REFUND:
			//request = refundRequest(fields, transaction);
			break;
		case SALE:
			request = saleRequest(fields, transaction);
			break;
		case CAPTURE:
			break;
		case STATUS:
			//request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request.toString();

	}
	
	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {
		
		StringBuilder request = new StringBuilder();
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String appCode = PropertiesManager.propertiesMap.get(Constants.ATL_APP_CODE);
		String securityId = PropertiesManager.propertiesMap.get(Constants.ATL_SECURITY_ID);
		String returnUrl = PropertiesManager.propertiesMap.get(Constants.ATL_RETURN_URL);
		String paymentMode = PropertiesManager.propertiesMap.get(Constants.ATL_PAYMENT_MODE);
		String key = fields.get(FieldType.TXN_KEY.getName());
		String iv = fields.get(FieldType.PASSWORD .getName());
		String custId = fields.get(FieldType.CUST_ID.getName());
		
		request.append(Constants.MERCHANT_CODE).append(Constants.EQUATOR)
		.append(fields.get(FieldType.MERCHANT_ID.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.RESERVATION_ID).append(Constants.EQUATOR)
		.append(fields.get(FieldType.ORDER_ID.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.TXN_AMOUNT).append(Constants.EQUATOR)
		.append(amount);
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.CURRENCY_TYPE).append(Constants.EQUATOR)
		.append(transaction.getCurrency());
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.APP_CODE).append(Constants.EQUATOR)
		.append(appCode);
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.PAYMENT_MODE).append(Constants.EQUATOR)
		.append(paymentMode);
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.TXN_DATE).append(Constants.EQUATOR)
		.append(transaction.getTxnDate());
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.SECURITY_ID).append(Constants.EQUATOR)
		.append(securityId);
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.RETURN_URL).append(Constants.EQUATOR)
		.append(returnUrl);
		if(!StringUtils.isBlank(custId)) {
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.USER_ID).append(Constants.EQUATOR)
		.append(custId);
		}
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.GATEWAY_TRANSSACTION_ID).append(Constants.EQUATOR)
		.append(fields.get(FieldType.PG_REF_NUM.getName()));
		
		try {
			request = mapChecksum(request);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}
		
		String encryptedString = null;
		try {
			encryptedString = MerchantIPayUtil.encryptIRCTC(request.toString(), key, iv).toUpperCase();
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}
		return encryptedString;
	}
	
	public StringBuilder mapChecksum(StringBuilder request) throws SystemException {
		String checksum = Hasher.getHash(request.toString());

		request.append(ConstantsIrctc.IPAY_SEPARATOR);
		request.append(ConstantsIrctc.CHECKSUM).append(ConstantsIrctc.EQUATOR).append(checksum);

		return request;
	}
	
	public void toTransaction(JSONObject response, Fields fields) {

		Map<String, String> requestMap = new HashMap<String, String>();
		for (Object key : response.keySet()) {
			String key1 = key.toString();
			String value = response.get(key.toString()).toString();
			requestMap.put(key1, value);
		}
		
		fields.put(FieldType.PG_RESP_CODE.getName(), requestMap.get(Constants.RESPONSE_CODE));
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), requestMap.get(Constants.RESPONSE_MESSAGE));
		fields.put(FieldType.PG_TXN_STATUS.getName(), requestMap.get(Constants.RDS_STATUS));
	}
}
