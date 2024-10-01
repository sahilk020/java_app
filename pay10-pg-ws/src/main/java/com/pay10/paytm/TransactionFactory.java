package com.pay10.paytm;

import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.paytm.util.CheckSumServiceHelper;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("paytmFactory")
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
			String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields, PropertiesManager.propertiesMap.get(Constants.PAYTM_RESPONSE_URL));
			
			transaction.setMID(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setWEBSITE(fields.get(FieldType.ADF1.getName()));
			transaction.setINDUSTRY_TYPE_ID(fields.get(FieldType.ADF2.getName()));
			transaction.setCHANNEL_ID(fields.get(FieldType.ADF3.getName()));
			
			String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
			transaction.setTXN_AMOUNT(amount);
			transaction.setORDER_ID(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setCUST_ID(TransactionManager.getNewTransactionId());
			transaction.setCALLBACK_URL(returnUrl);
			
			TreeMap<String, String> paytmParams = new TreeMap<String, String>();
			
			paytmParams.put("MID", transaction.getMID());
			paytmParams.put("WEBSITE", transaction.getWEBSITE());
			paytmParams.put("INDUSTRY_TYPE_ID", transaction.getINDUSTRY_TYPE_ID());
			paytmParams.put("CHANNEL_ID", transaction.getCHANNEL_ID());
			paytmParams.put("ORDER_ID", transaction.getORDER_ID());
			paytmParams.put("CUST_ID", transaction.getCUST_ID());
			paytmParams.put("TXN_AMOUNT", transaction.getTXN_AMOUNT());
			paytmParams.put("CALLBACK_URL", transaction.getCALLBACK_URL());
			
			String checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(fields.get(FieldType.TXN_KEY.getName()),
					paytmParams);
			
			transaction.setCHECKSUMHASH(checksum);
			
		}
		
		catch(Exception e) {
			logger.error("Exception in preapring request for Paytm ",e);
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
			logger.error("Exception in preparing Paytm Status Enquiry Request",e);
		}
		
	
	}

	
	
	public void setRefund(Fields fields, Transaction transaction) {
	
		try {
			transaction.setMid(fields.get(FieldType.MERCHANT_ID.getName()));
			
			String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
			transaction.setRefundAmount(amount);
			transaction.setOrderId(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setTxnId(fields.get(FieldType.ACQ_ID.getName()));
			transaction.setRefId(fields.get(FieldType.TXN_ID.getName()));
			
			TreeMap<String, String> paytmParams = new TreeMap<String, String>();
			
			paytmParams.put("mid", transaction.getMid());
			paytmParams.put("txnType", Constants.REF_TXN_TYPE);
			paytmParams.put("orderId", transaction.getOrderId());
			paytmParams.put("txnId", transaction.getTxnId());
			paytmParams.put("refId", transaction.getRefId());
			paytmParams.put("refundAmount", transaction.getRefundAmount());
			
			String checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(fields.get(FieldType.TXN_KEY.getName()),
					paytmParams);
			
			transaction.setChecksum(checksum);
		}
		
		catch(Exception e) {
			logger.error("Exception in preapring request for Paytm ",e);
		}
		
		
	}
	

	
}
