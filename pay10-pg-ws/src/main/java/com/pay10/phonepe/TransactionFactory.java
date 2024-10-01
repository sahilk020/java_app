package com.pay10.phonepe;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("phonepeFactory")
public class TransactionFactory {
	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

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
			String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields, PropertiesManager.propertiesMap.get(Constants.PHONEPE_RESPONSE_URL));
			
			transaction.setMerchantId(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setSaltKey(fields.get(FieldType.TXN_KEY.getName()));
			transaction.setSaltIndex(fields.get(FieldType.ADF1.getName()));
			transaction.setAmount(fields.get(FieldType.TOTAL_AMOUNT.getName()));
			
			if( fields.contains(FieldType.CUST_ID.getName()) && StringUtils.isNotBlank(fields.get(FieldType.CUST_ID.getName()))) {
				String custId = fields.get(FieldType.CUST_ID.getName());
				if(custId.length() > 64) {
					custId = custId.substring(0,64);
				}
				transaction.setMerchantUserId(custId);
			} else {
				transaction.setMerchantUserId("");
			}

			transaction.setTransactionId(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setRedirectUrl(returnUrl);
			transaction.setRedirectMode(Constants.REDIRECT_MODE);
			transaction.setSubMerchant(fields.get(FieldType.ADF3.getName()));
		}
		
		catch(Exception e) {
			logger.error("Exception in preapring request for Phone Pe ",e);
		}
		
		
	}

	public void setStatusEnquiry(Fields fields , Transaction transaction) {
	
	try {
		}
		
		catch(Exception e) {
			logger.error("Exception in preparing phone pe Status Enquiry Request",e);
		}
		
	
	}

	
	
	public void setRefund(Fields fields, Transaction transaction) {
	
		try {
			transaction.setMerchantId(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setSaltKey(fields.get(FieldType.TXN_KEY.getName()));
			transaction.setSaltIndex(fields.get(FieldType.ADF1.getName()));
			transaction.setMerchantId(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setAmount(fields.get(FieldType.TOTAL_AMOUNT.getName()));
			transaction.setTransactionId(fields.get(FieldType.TXN_ID.getName()));
			
			transaction.setProviderReferenceId(fields.get(FieldType.ACQ_ID.getName()));
			transaction.setMerchantOrderId(fields.get(FieldType.ORDER_ID.getName()));
			transaction.setSubMerchant(fields.get(FieldType.ADF3.getName()));
			transaction.setMessage("Refund processing for "+fields.get(FieldType.ORDER_ID.getName()));
			
		}
		
		catch(Exception e) {
			logger.error("Exception in preapring request for phone pe ",e);
		}
		
		
	}
	

	
}
