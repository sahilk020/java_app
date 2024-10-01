package com.pay10.apbl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.ApblEncDecUtil;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;

@Service("apblFactory")
public class TransactionFactory {
	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	private ApblEncDecUtil apblEncDecUtil;
	
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
			
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			setRefund(fields,transaction);
			
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
			String pattern = "ddMMyyyyHHmmss";
			DateFormat df = new SimpleDateFormat(pattern);
			Date dateToday = Calendar.getInstance().getTime();  
			String dateNow = df.format(dateToday);
			
			String apblReturnUrl = returnUrlCustomizer.customizeReturnUrl(fields, PropertiesManager.propertiesMap.get(Constants.APBL_RESPONSE_URL));
			
			transaction.setMID(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setTXN_REF_NO(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setSU(apblReturnUrl);
			transaction.setFU(apblReturnUrl);
			
			String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
			transaction.setAMT(amount);
			transaction.setDATE(dateNow);
			transaction.setCUR(Constants.CUR);
			
			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.WALLET.getCode())) {
				transaction.setService("WT");
			}
			else {
				transaction.setService("NB");
			}
			
			StringBuilder hashString =  new StringBuilder(); 
			
			hashString.append(transaction.getMID());
			hashString.append("#");
			hashString.append(transaction.getTXN_REF_NO());
			hashString.append("#");
			hashString.append(transaction.getAMT());
			hashString.append("#");
			hashString.append(transaction.getDATE());
			hashString.append("#");
			hashString.append(transaction.getService());
			hashString.append("#");
			hashString.append(fields.get(FieldType.TXN_KEY.getName()));
			

			String hash = apblEncDecUtil.getHash(hashString.toString());
			
			transaction.setHASH(hash);
			
		}
		
		catch(Exception e) {
			logger.error("Exception in preapring request for APBL ");
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
			String pattern = "ddMMyyyyHHmmss";
			DateFormat df = new SimpleDateFormat(pattern);
			Date dateToday = Calendar.getInstance().getTime();  
			String dateNow = df.format(dateToday);
			
			
			transaction.setMID(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setFeSessionId(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setTxnId(fields.get(FieldType.ACQ_ID.getName()));
			transaction.setDATE(dateNow);
			transaction.setRequest(Constants.ECOMM_REVERSAL);
			
			String amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
			transaction.setAMT(amount);
			
			StringBuilder hashString =  new StringBuilder(); 
			
			hashString.append(transaction.getMID());
			hashString.append("#");
			hashString.append(transaction.getTxnId());
			hashString.append("#");
			hashString.append(transaction.getAMT());
			hashString.append("#");
			hashString.append(transaction.getDATE());
			hashString.append("#");
			hashString.append(fields.get(FieldType.TXN_KEY.getName()));
			
			String hash = apblEncDecUtil.getHash(hashString.toString());
			
			transaction.setHASH(hash);
			
		}
		
		catch(Exception e) {
			logger.error("Exception in preapring request for APBL ",e);
		}
		
		
	}
	

	
}
