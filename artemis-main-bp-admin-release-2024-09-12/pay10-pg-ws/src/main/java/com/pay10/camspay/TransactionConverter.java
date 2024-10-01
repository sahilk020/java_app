package com.pay10.camspay;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.CurrencyTypes;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.camspay.dto.CamsPayId;
import com.pay10.pg.core.camspay.dto.CamsPayInput;
import com.pay10.pg.core.camspay.dto.CamsPayInput.SchemeDetails;
import com.pay10.pg.core.camspay.dto.CamsPayInput.Schemes;
import com.pay10.pg.core.camspay.util.CamsPayHasher;
import com.pay10.pg.core.camspay.util.Constants;

import bsh.This;

/**
 * @author Jay
 *
 */
@Service("camsPayTransactionConverter")
public class TransactionConverter {

	private static final Logger logger = LoggerFactory.getLogger(This.class.getName());
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final DecimalFormat df = new DecimalFormat("#.00");
	private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public TransactionConverter() {

	}

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			break;
		case REFUND:
			request = fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode()) ? refundRequest(fields) : "";
			break;
		case SALE:
			request = saleRequest(fields);
			break;
		}
		return request.toString();

	}

	public String refundRequest(Fields fields) {
		
		logger.info("refundRequest ::"+fields.getFieldsAsString());
		String request = null;
		String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
		String subBillerId = fields.get(FieldType.ADF8.getName());
		String encKey = "01WERdzfsdg$#$"; //fields.get(FieldType.ADF6.getName());
		String encIV = "camspayaesvector"; //fields.get(FieldType.ADF7.getName());
		String txnEncKey = fields.get(FieldType.ADF3.getName());
		
		JSONObject json = new JSONObject();
		json.put("merchantid", merchantId);
		json.put("subbillerid", subBillerId);
		json.put("trxnid", fields.get(FieldType.ORIG_TXN_ID.getName()));
		json.put("camspayrefno", fields.get(FieldType.RRN.getName()));
		json.put("amount", fields.get(FieldType.AMOUNT.getName()));
		json.put("enckey", txnEncKey);
		
		logger.info("Refund Request for Camspay UPI = "+json.toString());
		
		logger.info("Refund Request for Camspay encKey = "+encKey+ ", encIV "+encIV);
		try {
			request = CamsPayHasher.encryptMessage(json.toString(), encKey, encIV);
			logger.info("Refund Encrypted Request for Camspay trxnid = "+fields.get(FieldType.ORIG_TXN_ID.getName())+ ", request ="+request);
		} catch (Exception e) {
			logger.error("saleRequest:: failed.", e);
			return null;
		}
		
		return request;
	}

	public String saleRequest(Fields fields) throws SystemException {

		try {
			String requestURL = PropertiesManager.propertiesMap.get(Constants.CAMSPAY_SALE_REQUEST_URL);
			logger.info("saleRequest:: fields={}, requestURL={}", fields.getFieldsAsString(), requestURL);

			StringBuilder outputHtml = new StringBuilder();
			outputHtml.append("<html>");
			outputHtml.append("<head>");
			outputHtml.append("<title>Pay10 Merchant Checkout Page</title>");
			outputHtml.append("</head>");
			outputHtml.append("<body onload='document.forms[0].submit()'>");
			outputHtml.append("<center><h1>Please do not refresh this page...</h1></center>");
			outputHtml.append("<form method='POST' action='" + requestURL + "' name='PostForm'>");

			outputHtml.append("<input type='hidden' name='id' value='" + getId(fields) + "'>");
			String request = mapper.writeValueAsString(getReq(fields));
			String reqEncKey = fields.get(FieldType.ADF3.getName());
			String reqIv = fields.get(FieldType.ADF4.getName());
			String checksumKey = fields.get(FieldType.ADF5.getName());
			outputHtml.append("<input type='hidden' name='req' value='"
					+ CamsPayHasher.encryptMessage(request, reqEncKey, reqIv) + "'>");
			outputHtml.append("<input type='hidden' name='checksum' value='"
					+ CamsPayHasher.generateCheckSum(request, checksumKey) + "'>");
			outputHtml.append("</form>");
			outputHtml.append("</body>");
			outputHtml.append("</html>");
			logger.info("saleRequest:: CAMSPAY:- request={}", outputHtml.toString());
			return outputHtml.toString();
		} catch (Exception e) {
			logger.error("saleRequest:: failed.", e);
			return null;
		}
	}

	public Transaction toTransaction(String jsonResponse, String txnType) {

		Transaction transaction = new Transaction();

		logger.info("toTransaction:: CAMSPAY: response received response={}, txnType={}", jsonResponse, txnType);
		if (StringUtils.isBlank(jsonResponse)) {
			logger.info("toTransaction:: CAMSPAY: empty response received");
			return transaction;
		}

		JSONObject respObj = new JSONObject(jsonResponse);
		logger.info("toTransaction:: CAMSPAY: respObj={}", respObj);
		return transaction;

	}

	private String getId(Fields fields) {
		CamsPayId id = new CamsPayId();
		id.setMerchantid(fields.get(FieldType.MERCHANT_ID.getName()));
		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		paymentType = StringUtils.equalsIgnoreCase(paymentType, PaymentType.UPI.getCode())
				? PaymentType.getpaymentName(paymentType)
				: paymentType;
		id.setPaytype(paymentType);
		id.setSubbillerid(fields.get(FieldType.ADF8.getName()));
		try {
			String encKeyId = fields.get(FieldType.ADF1.getName());
			String ivId = fields.get(FieldType.ADF2.getName());
			return CamsPayHasher.encryptMessage(mapper.writeValueAsString(id), encKeyId, ivId);
		} catch (Exception ex) {
			logger.error("getId:: failed.", ex);
			return null;
		}
	}

	private CamsPayInput getReq(Fields fields) {
		CamsPayInput input = new CamsPayInput();
		input.setCustid(fields.get(FieldType.CUST_EMAIL.getName()));
		input.setTrxnid(fields.get(FieldType.PG_REF_NUM.getName()));
		String amount = df.format(Double.valueOf(fields.get(FieldType.TOTAL_AMOUNT.getName())) / 100);
		input.setAmount(amount);
		input.setCurrency(CurrencyTypes.getInstancefromName(fields.get(FieldType.CURRENCY_CODE.getName())).getCode());
		input.setSuccessurl(PropertiesManager.propertiesMap.get(Constants.CAMSPAY_RESPONSE_URL));
		input.setFailureurl(PropertiesManager.propertiesMap.get(Constants.CAMSPAY_RESPONSE_URL));
		input.setReqdt(format.format(new Date()));
		input.setDevicetype(Constants.DEVICE_TYPE);
		String iptAllowed = fields.get(FieldType.PAYMENT_TYPE.getName());
		iptAllowed = StringUtils.equalsIgnoreCase(iptAllowed, PaymentType.UPI.getCode())
				? PaymentType.getpaymentName(iptAllowed)
				: iptAllowed;
		input.setIptallowed(iptAllowed);
		input.setApplicationname(Constants.PAY10);

		Schemes schemes = new Schemes();
		schemes.setScount(Constants.SCOUNT);
		schemes.setTt(Constants.TT);
		List<SchemeDetails> list = new ArrayList<CamsPayInput.SchemeDetails>();
		SchemeDetails details = new SchemeDetails();
		details.setAmt(amount);
		details.setSc(Constants.SC);
		details.setAc(Constants.AC);
		schemes.setSdtl(list);
		input.setSchemes(schemes);

		PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
		MopType mopType = MopType.getmop(fields.get(FieldType.MOP_TYPE.getName()));
		CamsPayMopType camsPayMopType = CamsPayMopType.getmop(mopType.getCode());
		switch (paymentType) {
		case NET_BANKING:
			input.setBankcode(camsPayMopType.getBankCode());
			input.setCurrcode(fields.get(FieldType.CURRENCY_CODE.getName()));
			break;
		case UPI:
			input.setVpa(fields.get(FieldType.PAYER_ADDRESS.getName()));
			break;
		default:
			break;
		}
		return input;
	}
}
