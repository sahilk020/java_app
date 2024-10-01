package com.pay10.matchmove;

import java.math.BigDecimal;
import java.security.MessageDigest;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

@Service("matchMoveTransactionConverter")
public class TransactionConverter {
	
	
	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;


	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;
        String txnType=fields.get(FieldType.ORIG_TXNTYPE.getName());
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
		case CAPTURE:
			break;
		case STATUS:
			break;
		}
		return request;

	}
	
	
	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String totalAamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),fields.get(FieldType.CURRENCY_CODE.getName()));

		BigDecimal amount1=new BigDecimal(amount);
		BigDecimal totalAamount1=new BigDecimal(totalAamount);
		BigDecimal convenience_fee=totalAamount1.subtract(amount1);
		String convenience_fees=convenience_fee.toString();
		
		JSONObject json=new JSONObject();
		String hashValue=GenerateHash(fields, transaction);
		String returnUrl = PropertiesManager.propertiesMap.get(Constant.MATCHMOVECALLBACKURL);
		try
		{  
		json.put(Constant.CALLBACKURL, returnUrl);
		json.put(Constant.CONVENIENCE_FEE,convenience_fees);
		json.put(Constant.PRODUCT_DESC, Constant.PRODUCT_DESC_VALUE);
		json.put(Constant.PURCHASE_AMOUNT, amount);
		json.put(Constant.PURCHASE_CURRENCY,transaction.getPurchase_currency());
		json.put(Constant.TXN_REF_ID, fields.get(FieldType.PG_REF_NUM.getName()));
		json.put("hash", hashValue);
		logger.info("Sale Request for matchmove"+json.toString());
		
		}
		catch (Exception ex) {
			logger.error("Exception in TransactionConverter converter, saleRequest", ex);
		}
		return json.toString();
}

	
	
	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
	    String refundFlag=fields.get(FieldType.REFUND_FLAG.getName());
	    String refundType="";
	    if(refundFlag.equals("R"))
	    {
	    	refundType=Constant.REVERSAL;
	    }
	    else
	    {
	    	refundType=Constant.REFUND;
	    }
		JSONObject json=new JSONObject();
		String hashValue=GenerateRefundHash(fields, transaction,refundType,amount);
		try
		{ 
		json.put(Constant.TXN_REF_ID, fields.get(FieldType.ORIG_TXN_ID.getName()));
		json.put(Constant.REF_ID, fields.get(FieldType.PG_REF_NUM.getName()));
		json.put(Constant.CHECKOUT_REF_ID,fields.get(FieldType.ACQ_ID.getName()));
		json.put(Constant.REFUND_AMOUNT,amount);
		json.put(Constant.REFUND_TYPE, refundType);
		json.put(Constant.REASON, refundType);
		json.put("hash", hashValue);
		logger.info("Refund Request for matchmove"+json.toString());
		
		}
		catch (Exception ex) {
			logger.error("Exception in TransactionConverter converter, saleRequest", ex);
		}
		return json.toString();
}
	
	
	
	
	
	public  String GenerateHash(Fields fields, Transaction transaction) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String securityKey = fields.get(FieldType.TXN_KEY.getName());
		String returnUrl = PropertiesManager.propertiesMap.get(Constant.MATCHMOVECALLBACKURL);	
		String totalAamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),fields.get(FieldType.CURRENCY_CODE.getName()));
		BigDecimal amount1=new BigDecimal(amount);
		BigDecimal totalAamount1=new BigDecimal(totalAamount);
		BigDecimal convenience_fee=totalAamount1.subtract(amount1);
		String convenience_fees=convenience_fee.toString();
		StringBuilder builder=new StringBuilder();
		builder.append(Constant.CALLBACKURL);
		builder.append(Constant.EQUATOR);
		builder.append(returnUrl);
		builder.append(Constant.TILDE);
		builder.append(Constant.CONVENIENCE_FEE);
		builder.append(Constant.EQUATOR);
		builder.append(convenience_fees);
		builder.append(Constant.TILDE);
		builder.append(Constant.PRODUCT_DESC);
		builder.append(Constant.EQUATOR);
	    builder.append(Constant.PRODUCT_DESC_VALUE);
	    builder.append(Constant.TILDE);
	    builder.append(Constant.PURCHASE_AMOUNT);
	    builder.append(Constant.EQUATOR);
	    builder.append(amount);
	    builder.append(Constant.TILDE);
	    builder.append(Constant.PURCHASE_CURRENCY);
	    builder.append(Constant.EQUATOR);
	    builder.append(transaction.getPurchase_currency());
		builder.append(Constant.TILDE);
	    builder.append(Constant.TXN_REF_ID);
	    builder.append(Constant.EQUATOR);
	    builder.append(fields.get(FieldType.PG_REF_NUM.getName()));
	    builder.append(securityKey);
	    String hashValue=sha256(builder.toString());
	    String genratedHash=hashValue.toUpperCase();
	    logger.info("Generate hash value of mathcmvoe saleRequest");
	    
		return genratedHash;
}
	
	
	
	public  String GenerateRefundHash(Fields fields, Transaction transaction,String refundType,String amount) throws SystemException {
		String securityKey = fields.get(FieldType.TXN_KEY.getName());

		StringBuilder builder=new StringBuilder();
		
	
		
		builder.append(Constant.REFUND_AMOUNT);
		builder.append(Constant.EQUATOR);
		builder.append(amount);
		builder.append(Constant.TILDE);  

		builder.append(Constant.CHECKOUT_REF_ID);
		builder.append(Constant.EQUATOR);
		builder.append(fields.get(FieldType.ACQ_ID.getName()));
		builder.append(Constant.TILDE);
		
		builder.append(Constant.REASON);
		builder.append(Constant.EQUATOR);
	    builder.append(refundType);
	    builder.append(Constant.TILDE);
	    
	    builder.append(Constant.REF_ID);
		builder.append(Constant.EQUATOR);
		builder.append(fields.get(FieldType.PG_REF_NUM.getName()));
		builder.append(Constant.TILDE);
		
		builder.append(Constant.TXN_REF_ID);
		builder.append(Constant.EQUATOR);
		builder.append(fields.get(FieldType.ORIG_TXN_ID.getName()));
		builder.append(Constant.TILDE);
		
		builder.append(Constant.REFUND_TYPE);
		builder.append(Constant.EQUATOR);
		builder.append(refundType);
	    builder.append(securityKey);
	
	    String hashValue=sha256(builder.toString());
	    String genratedHash=hashValue.toUpperCase();
	 
		return genratedHash;
}
	
	
	public Transaction toTransaction(Fields fields, String refundResponse1) {
		logger.info("Refund Response from Matchmove toTransaction method: " + refundResponse1);
		Transaction transaction = new Transaction();
		try {
            if(StringUtils.isNotBlank(refundResponse1))
            {
			JSONObject jsonObject = new JSONObject(refundResponse1);
			String responseCode = jsonObject.get("response_code").toString();
			String responseMessage = jsonObject.getString("response_message").toString();
			String acqId = jsonObject.getString("checkout_ref_id");
			transaction.setResponse_code(responseCode);
			transaction.setResponse_message(responseMessage);
			transaction.setCheckout_ref_id(acqId);
            }

		} catch (Exception ex) {
			logger.info("get error on matchmove response: " + ex);
		}
		return transaction;
	}
	
	
	
	
	
	public Transaction toInitialTransaction(Fields fields, String refundResponse1) {
		logger.info("initial Response from Matchmove toInitialTransaction method :" + refundResponse1);
		Transaction transaction = new Transaction();
		try {
			if(StringUtils.isNotBlank(refundResponse1))
			{
			JSONObject jsonObject = new JSONObject(refundResponse1);
			String responseCode = jsonObject.get("response_code").toString();
			String responseMessage = jsonObject.getString("response_message").toString();
			String redirectUrl = jsonObject.getString("redirect_url");
			String acqId = jsonObject.getString("checkout_ref_id");
			String txn_ref_id = jsonObject.getString("txn_ref_id");
			transaction.setResponse_code(responseCode);
			transaction.setResponse_message(responseMessage);
			transaction.setRedirect_url(redirectUrl);
			transaction.setCheckout_ref_id(acqId);
			transaction.setTxn_ref_id(txn_ref_id);
			}
		} catch (Exception ex) {
			logger.info("get error on matchmove response: " + ex);
		}
		return transaction;
	}
	
	
	
	
	
	public static String sha256(String base) 
	{ 
	try 
	{ 
	MessageDigest digest = MessageDigest.getInstance("SHA-256"); 
	byte[] hash = digest.digest(base.getBytes("UTF-8")); 
	StringBuffer hexString = new StringBuffer(); 
	for (int i = 0; i < hash.length; i++) 
	{ 
	String hex = Integer.toHexString(0xff & hash[i]); 
	if(hex.length() == 1) hexString.append('0'); 
	hexString.append(hex); 
	} 

	return hexString.toString(); 
	} 
	catch(Exception ex) 
	{ 
	throw new RuntimeException(ex); 
	} 
	} 
	
}
