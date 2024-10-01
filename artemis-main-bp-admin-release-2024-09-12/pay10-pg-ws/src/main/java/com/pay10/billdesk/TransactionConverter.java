package com.pay10.billdesk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.BillDeskChecksumUtil;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;


@Service("billdeskTransactionConverter")
public class TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	private BillDeskChecksumUtil billDeskChecksumUtil;

	@Autowired
	private FieldsDao fieldsDao;

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			break;
		case REFUND:
			request = refundRequest(fields, transaction);
			StringBuilder req = new StringBuilder();
			req.append(Constants.REQUEST_MSG);
			req.append(request);
			request = req.toString();
			break;
		case SALE:
			String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
			
			if (paymentType.equalsIgnoreCase(PaymentType.UPI.getCode())) {
				request = upiSaleRequest(fields, transaction);
			} else {
				request = saleRequest(fields, transaction);
			}

			break;
		case CAPTURE:
			break;
		case STATUS:
			request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request;

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
				PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL));

		StringBuilder request = new StringBuilder();
		prepareSaleRequest(request, fields, returnUrl, amount);

		String hashCode = billDeskChecksumUtil.getHash(request.toString(), fields.get(FieldType.TXN_KEY.getName()));

		request.append("|");
		request.append(hashCode);

		logRequest(request.toString(), fields);

		return request.toString();

	}
	
	public String upiSaleRequest(Fields fields, Transaction transaction) throws SystemException {

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
				PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL));

		StringBuilder request = new StringBuilder();
		prepareUpiSaleRequest(request, fields, returnUrl, amount);

		String hashCode = billDeskChecksumUtil.getHash(request.toString(), fields.get(FieldType.TXN_KEY.getName()));

		request.append("|");
		request.append(hashCode);

		logRequest(request.toString(), fields);

		return request.toString();

	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		try {

			Map<String, String> orderMap = fieldsDao.getCaptureTxn(fields.get(FieldType.ORDER_ID.getName()));
			String createDate = orderMap.get(FieldType.CREATE_DATE.getName());
			logger.info("Create Date is " + createDate);
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			DateFormat currentDate = new SimpleDateFormat("yyyyMMddHHmmss");
			Calendar calobj = Calendar.getInstance();
			String timeStamp = currentDate.format(calobj.getTime());

			String saleTxnDate = createDate;
			DateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date oldDate = null;
			try {
				oldDate = (Date) oldFormatter.parse(saleTxnDate);
			} catch (Exception e) {
				logger.error("exception while parsing date ", e);
			}
			String txnDate = formatter.format(oldDate);

			StringBuilder request = new StringBuilder();
			request.append(Constants.RefundCode);
			request.append(Constants.PIPE);
			request.append(fields.get(FieldType.MERCHANT_ID.getName()));
			request.append(Constants.PIPE);
			request.append(fields.get(FieldType.ACQ_ID.getName()));
			request.append(Constants.PIPE);
			request.append(txnDate);
			request.append(Constants.PIPE);
			request.append(fields.get(FieldType.ORIG_TXN_ID.getName()));
			request.append(Constants.PIPE);
			request.append(Amount.toDecimal(fields.get(FieldType.SALE_TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));
			request.append(Constants.PIPE);
			request.append(amount);
			request.append(Constants.PIPE);
			request.append(timeStamp);
			request.append(Constants.PIPE);
			request.append(fields.get(FieldType.PG_REF_NUM.getName()));
			request.append(Constants.PIPE);
			request.append(Constants.NA);
			request.append(Constants.PIPE);
			request.append(Constants.NA);
			request.append(Constants.PIPE);
			request.append(Constants.NA);

			String checkSum = billDeskChecksumUtil.getHash(request.toString(), fields.get(FieldType.TXN_KEY.getName()));
			request.append(Constants.PIPE);
			request.append(checkSum);

			logger.info("Refund Request message to BillDesk: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + request);
			return request.toString();
		}

		catch (Exception e) {
			logger.error("Exception", e);
			return null;
		}

	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) throws SystemException {

		StringBuilder request = new StringBuilder();

		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		String strDate = dateFormat.format(date);

		request.append(Constants.StatusEnqCode);
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.MERCHANT_ID.getName()));
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.PG_REF_NUM.getName()));
		request.append(Constants.SEPARATOR);
		request.append(strDate);

		String hash = billDeskChecksumUtil.getHash(request.toString(), fields.get(FieldType.TXN_KEY.getName()));
		request.append(Constants.SEPARATOR);
		request.append(hash);

		return request.toString();

	}

	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();

		String[] keyValueParams = response.split("\\|");

		if (keyValueParams[14] != null) {
			transaction.setAuthStatus(keyValueParams[14]);
		}

		if (keyValueParams[1] != null) {
			transaction.setCustomerID(keyValueParams[1]);
		}

		if (keyValueParams[2] != null) {
			transaction.setTxnReferenceNo(keyValueParams[2]);
		}

		if (keyValueParams[3] != null) {
			transaction.setBankReferenceNo(keyValueParams[3]);
		}

		if (keyValueParams[23] != null) {
			transaction.setErrorStatus(keyValueParams[23]);
		}

		if (keyValueParams[24] != null) {
			transaction.setErrorDescription(keyValueParams[24]);
		}

		return transaction;
	}

	
	public Transaction toUpiTransaction(String response) {

		JSONObject respJson = new JSONObject(response);
		String msg = respJson.getString("msg").toString();
		
		Transaction transaction = new Transaction();

		String[] keyValueParams = msg.split("\\|");

		if (keyValueParams[14] != null) {
			transaction.setAuthStatus(keyValueParams[14]);
		}

		if (keyValueParams[1] != null) {
			transaction.setCustomerID(keyValueParams[1]);
		}

		if (keyValueParams[2] != null) {
			transaction.setTxnReferenceNo(keyValueParams[2]);
		}

		if (keyValueParams[3] != null) {
			transaction.setBankReferenceNo(keyValueParams[3]);
		}

		if (keyValueParams[23] != null) {
			transaction.setErrorStatus(keyValueParams[23]);
		}

		if (keyValueParams[24] != null) {
			transaction.setErrorDescription(keyValueParams[24]);
		}

		return transaction;
	}
	
	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to Billdesk  : Url= " + requestMessage, fields);
	}

	private void log(String message, Fields fields) {
		message = Pattern.compile("(<CardNumber>)([\\s\\S]*?)(</card>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<ExpiryDate>)([\\s\\S]*?)(</pan>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<CardSecurityCode>)([\\s\\S]*?)(</expmonth>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<TerminalId>)([\\s\\S]*?)(</expyear>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<PassCode>)([\\s\\S]*?)(</cvv2>)").matcher(message).replaceAll("$1$3");
		// message =
		// Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(message).replaceAll("$1$3");
		MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
		logger.info(message);
	}

	private void prepareSaleRequest(StringBuilder request, Fields fields, String returnUrl, String amount) {

		request.append(fields.get(FieldType.MERCHANT_ID.getName()));
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.PG_REF_NUM.getName()));
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append(amount);
		request.append(Constants.SEPARATOR);
		request.append(BilldeskMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("INR");
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.ADF1.getName()));
		request.append(Constants.SEPARATOR);
		request.append("R");
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.PASSWORD.getName()));
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("F");
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.ORDER_ID.getName()));
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.CUST_EMAIL.getName()));
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.CUST_PHONE.getName()));
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append(returnUrl);

	}

	private void prepareUpiSaleRequest(StringBuilder request, Fields fields, String returnUrl, String amount) {

		request.append(fields.get(FieldType.MERCHANT_ID.getName()));
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.PG_REF_NUM.getName()));
		request.append(Constants.SEPARATOR);
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
			request.append("UPIC:" + fields.get(FieldType.PAYER_ADDRESS.getName()).toString() + ":NA:NA:NA");
		} else {
			request.append("NA");
		}

		request.append(Constants.SEPARATOR);
		request.append(amount);
		request.append(Constants.SEPARATOR);
		request.append("IC4");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("INR");
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.ADF1.getName()));
		request.append(Constants.SEPARATOR);
		request.append("R");
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.PASSWORD.getName()));
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("F");
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.CUST_EMAIL.getName()));
		request.append(Constants.SEPARATOR);
		request.append(fields.get(FieldType.CUST_PHONE.getName()));
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append("NA");
		request.append(Constants.SEPARATOR);
		request.append(returnUrl);

	}

	public Transaction toTransactionStatus(String response) {

		Transaction transaction = new Transaction();
		if (StringUtils.isBlank(response)) {
			return transaction;
		}

		String[] keyValueParams = response.split("\\|");

		if (keyValueParams[15] != null) {
			transaction.setAuthStatus(keyValueParams[15]);
		}

		if (keyValueParams[2] != null) {
			transaction.setCustomerID(keyValueParams[2]);
		}

		if (keyValueParams[3] != null) {
			transaction.setTxnReferenceNo(keyValueParams[3]);
		}

		if (keyValueParams[4] != null) {
			transaction.setBankReferenceNo(keyValueParams[4]);
		}

		if (keyValueParams[24] != null) {
			transaction.setErrorStatus(keyValueParams[24]);
		}

		if (keyValueParams[25] != null) {
			transaction.setErrorDescription(keyValueParams[25]);
		}

		if (keyValueParams[31] != null) {
			transaction.setQueryStatus(keyValueParams[31]);
		}
		return transaction;
	}

	public Transaction toTransactionRefund(String response) {

		Transaction transaction = new Transaction();

		String[] keyValueParams = response.split("\\|");

		if (keyValueParams[8] != null) {
			transaction.setRefStatus(keyValueParams[8]);
		}

		if (keyValueParams[4] != null) {
			transaction.setCustomerID(keyValueParams[4]);
		}

		if (keyValueParams[2] != null) {
			transaction.setTxnReferenceNo(keyValueParams[2]);
		}

		if (keyValueParams[3] != null) {
			transaction.setRefStatus(keyValueParams[8]);
		}

		if (keyValueParams[10] != null) {
			transaction.setErrorStatus(keyValueParams[10]);
		}

		if (keyValueParams[11] != null) {
			transaction.setErrorDescription(keyValueParams[11]);
		}

		if (keyValueParams[12] != null) {
			transaction.setProcessStatus(keyValueParams[12]);
		}

		return transaction;
	}
	
	public String getTextBetweenTags(String text, String tag1, String tag2) {

		int leftIndex = text.indexOf(tag1);
		if (leftIndex == -1) {
			return null;
		}

		int rightIndex = text.indexOf(tag2);
		if (rightIndex != -1) {
			leftIndex = leftIndex + tag1.length();
			return text.substring(leftIndex, rightIndex);
		}

		return null;
	}
}
