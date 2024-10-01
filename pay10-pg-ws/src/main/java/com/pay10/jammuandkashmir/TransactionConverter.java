package com.pay10.jammuandkashmir;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pay10.axisbank.netbanking.Constants;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.jammuandKishmir.ShoppingMallBase64EncoderDecoder;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

@Service("jammuandkashmirNBTransactionConverter")

public class TransactionConverter {
	
	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	
	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;
	
	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		
		case SALE:
			request = saleRequest(fields, transaction);
			break;
		case ENQUIRY:
		//request = statusEnquiryRequest1(fields, transaction);
			break;
		}
		return request;

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		try {
			if(StringUtils.isBlank(fields.get(FieldType.PG_REF_NUM.getName())) ){
				fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));

				
			}
			String returnUrl = PropertiesManager.propertiesMap.get("JAMMUANDKASHMIR_RETURNURL");
			logger.info("feild for jammu and kishmir"+fields.getFieldsAsString());
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			logger.info("feild for jammu and kishmir"+fields.getFieldsAsString()+amount);
String currency=Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
			String payeeId = fields.get(FieldType.MERCHANT_ID.getName());
			String category = fields.get(FieldType.ADF1.getName());

			String encryptKey = fields.get(FieldType.TXN_KEY.getName());
			
			StringBuilder salerequest = new StringBuilder();

			salerequest.append("ShoppingMallTranFG.TRAN_CRN~");
			salerequest.append(currency);

			salerequest.append("|");
			salerequest.append("ShoppingMallTranFG.TXN_AMT~");
			salerequest.append(amount);

			salerequest.append("|");
			salerequest.append("ShoppingMallTranFG.PID~");
			salerequest.append(payeeId);

			salerequest.append("|");
			salerequest.append("ShoppingMallTranFG.PRN~");
			salerequest.append(fields.get(FieldType.PG_REF_NUM.getName()));

			salerequest.append("|");
			salerequest.append("ShoppingMallTranFG.ITC~");
			salerequest.append("PAY10");
logger.info(" raw request jammu and kismhir bank"+salerequest.toString());
		
String encryptyString=ShoppingMallBase64EncoderDecoder.jammuandKismhmirenc(salerequest.toString(),encryptKey);
logger.info(" encrypt request jammu and kismhir bank"+encryptyString);
StringBuilder mainsalerequest = new StringBuilder();
mainsalerequest.append("CATEGORY_ID=");
mainsalerequest.append(category)	;
mainsalerequest.append("&");	
mainsalerequest.append("RU=");
mainsalerequest.append(returnUrl);	
mainsalerequest.append("&");	
mainsalerequest.append("QS=");
mainsalerequest.append(encryptyString);	

logger.info(" main request jammu and kismhir bank"+mainsalerequest);
return mainsalerequest.toString();

		}

		catch (Exception e) {
			logger.error("Exception in generating JAMMMU AND KISHMIR request ", e);
		}

		return null;
	}
	
	 public void updateSaleResponse(Fields fields, String request) {
		logger.info("Encrypted Request to JAMMU AND KISHMIR bank: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + request);
		fields.put(FieldType.JAMMU_AND_KASHMIR_FINAL_REQUEST.getName(), request);
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
	}

	public Transaction toTransaction(String response, String string) {
		Transaction transaction = new Transaction();
		//PID=1600671|PRN=1059130113163538|ITC=PAY10|AMT=10.00|CRN=INR|BID=23520792|TXN_STATUS=SUSPECT|
		String responseArray [] = response.split("\\|");
		
		for (String data : responseArray) {
			
			if (data.contains("PID")) {
				String dataArray [] = data.split("=");
				transaction.setPaid(dataArray[1]);
			}
			
			else if (data.contains("PRN")) {
				String dataArray [] = data.split("=");
				transaction.setPrn(dataArray[1]);
			}
			
			else if (data.contains("BID")) {
				String dataArray [] = data.split("=");
				transaction.setBid(dataArray[1]);
			}
			
			else if (data.contains("ITC")) {
				String dataArray [] = data.split("=");
				transaction.setItc(dataArray[1]);
			}
			
			else if (data.contains("AMT")) {
				String dataArray [] = data.split("=");
				transaction.setAmt(dataArray[1]);
			}
			
			else if (data.contains("CRN")) {
				String dataArray [] = data.split("=");
				transaction.setCrn(dataArray[1]);
			}
			
			else if (data.contains("TXN_STATUS")) {
				String dataArray [] = data.split("=");
				transaction.setStatus(dataArray[1]);
			}
			else {
				continue;
			}
			
		}
		logger.info("log transaction "+transaction.toString());
		return transaction;
	
	}

}
