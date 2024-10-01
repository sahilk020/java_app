package com.pay10.mobikwik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.MobikwikEncDecUtil;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("mobikwikFactory")
public class TransactionFactory {
	
	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	private MobikwikEncDecUtil mobikwikEncDecUtil;
	
	private static Logger logger = LoggerFactory.getLogger(TransactionFactory.class.getName());
	
	@SuppressWarnings("incomplete-switch")
	public Transaction getInstance(Fields fields) {

		Transaction transaction = new Transaction();
		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
			break;
		case ENROLL:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			setEnrollment(fields,transaction);
			break;
		case REFUND:
			
			fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			setRefund(fields,transaction);
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			
			break;
		case SALE:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			setEnrollment(fields,transaction);
			break;
		case CAPTURE:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			break;
		case STATUS:
			setStatusEnquiry(fields,transaction);
			break;
		}

		return transaction;
	}
	
	public void setEnrollment(Fields fields, Transaction transaction) {
		
		
		try {
			
			String mobikwikReturnUrl = returnUrlCustomizer.customizeReturnUrl(fields, PropertiesManager.propertiesMap.get(Constants.MOBIKWIK_RESPONSE_URL));
			String buyerEmail = PropertiesManager.propertiesMap.get("MobikwikEmail");
			String mode = PropertiesManager.propertiesMap.get("MobikwikMode");
			String mobiTxnType = PropertiesManager.propertiesMap.get("MobikwikTxnType");
			String zpPayOption = PropertiesManager.propertiesMap.get("MobikwikPayOpt");
			
			
			
			transaction.setAmount(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
			transaction.setBuyerEmail(buyerEmail);
			transaction.setCurrency("INR");
			transaction.setMerchantIdentifier((fields.get(FieldType.MERCHANT_ID.getName())));
			transaction.setMode(mode);
			transaction.setOrderId(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setTxnType(mobiTxnType);
			transaction.setReturnUrl(mobikwikReturnUrl);
			transaction.setZpPayOption(zpPayOption);
			
			
			StringBuilder hashString =  new StringBuilder(); 
			
			hashString.append("'"+transaction.getAmount()+"'");
			hashString.append("'"+transaction.getOrderId()+"'");
			hashString.append("'"+transaction.getReturnUrl()+"'");
			hashString.append("'"+transaction.getMerchantIdentifier()+"'");

			String checksum = mobikwikEncDecUtil.calculateChecksum(fields.get(FieldType.TXN_KEY.getName()) ,hashString.toString());
			
			transaction.setChecksum(checksum);
			
		}
		
		catch(Exception e) {
			logger.error("Exception in preapring request for Mobikwik ");
		}
		
		
	}

	public void setStatusEnquiry(Fields fields , Transaction transaction) {
	
	try {
			
			/*
			 * String login = fields.get(FieldType.MERCHANT_ID.getName()); String dateString
			 * = null; String date = null; dateString =
			 * fieldsDao.getCaptureDate(fields.get(FieldType.PG_REF_NUM.getName()));
			 * 
			 * if (StringUtils.isNotBlank(dateString)) { date = dateString.substring(0,10);
			 * transaction.setDate(date); }
			 * transaction.setAmt(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
			 * fields.get(FieldType.CURRENCY_CODE.getName()))); transaction.setLogin(login);
			 */
		}
		
		catch(Exception e) {
			logger.error("Exception in preparing Apbl Status Enquiry Request",e);
		}
		
	
	}

	
	
	public void setRefund(Fields fields, Transaction transaction) {
	
		try {
			
			transaction.setAmount(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
			transaction.setMerchantIdentifier(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setTxid(fields.get(FieldType.PG_REF_NUM.getName()));
			
			StringBuilder sb =  new StringBuilder();
			
			sb.append("'"+transaction.getMerchantIdentifier()+"'");
			sb.append("'"+transaction.getTxid()+"'");
			sb.append("'"+transaction.getAmount()+"'");
			
			String checksum = mobikwikEncDecUtil.calculateChecksum(fields.get(FieldType.TXN_KEY.getName()) ,sb.toString());
			
			transaction.setChecksum(checksum);
			
		}
		
		catch(Exception e) {
			logger.error("Exception in preapring refund request for Mobikwik ",e);
		}
		
		
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
	}// getTextBetweenTags()
	
}
