package com.pay10.ingenico;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("ingenicoFactory")
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
			setEnrollment(fields, transaction);
			break;
		case REFUND:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			setRefund(fields, transaction);
			break;
		case SALE:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			setEnrollment(fields, transaction);
			break;
		case CAPTURE:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			break;
		case STATUS:
			setStatusEnquiry(fields, transaction);
			break;
		}

		return transaction;
	}

	public void setEnrollment(Fields fields, Transaction transaction) {

		try {
			String pattern = "dd-MM-yyyy";
			String dateInString =new SimpleDateFormat(pattern).format(new Date());

			String ingenicoReturnUrl = returnUrlCustomizer.customizeReturnUrl(fields, PropertiesManager.propertiesMap.get(Constants.INGENICO_RESPONSE_URL));
			String ingenicoSaleUrl = PropertiesManager.propertiesMap.get("INGENICOSaleUrl");
			
			transaction.setMerchantCode(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setAmount(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));
			
			transaction.setCurrencyCode(Constants.CURRENCY_CODE);
			transaction.setRequestType(fields.get(FieldType.ADF1.getName()));
			transaction.setMerchantTxnRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setReturnUrl(ingenicoReturnUrl);
			
			StringBuilder sb =  new StringBuilder();
					
			sb.append(fields.get(FieldType.ADF2.getName()));
			sb.append("_");
			sb.append(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));
			sb.append("_0.0");
			
			transaction.setShoppingCartDet(sb.toString());
			transaction.setTxnDate(dateInString);
			transaction.setBankCode(fields.get(FieldType.ADF3.getName()));
			transaction.setWebServiceLoc(ingenicoSaleUrl);
			
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
				transaction.setMobileNum(fields.get(FieldType.CUST_PHONE.getName()));
			}
			
			transaction.setKey(fields.get(FieldType.TXN_KEY.getName()));
			transaction.setIv(fields.get(FieldType.PASSWORD.getName()));
			
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
				transaction.setCustName(fields.get(FieldType.CUST_NAME.getName()));
			}else {
				transaction.setCustName("Pay10");
			}
			
			transaction.setTimeout(Constants.TIMEOUT);
			
			transaction.setCardNum(fields.get(FieldType.CARD_NUMBER.getName()));
			
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
				transaction.setCardName(fields.get(FieldType.CUST_NAME.getName()));
			}else {
				transaction.setCardName("Pay10");
			}

			transaction.setCardCvv(fields.get(FieldType.CVV.getName()));
			transaction.setExpMM(fields.get(FieldType.CARD_EXP_DT.getName()).substring(0, 2));
			transaction.setExpYYYY(fields.get(FieldType.CARD_EXP_DT.getName()).substring(2, 6));
			
		}

		catch (Exception e) {
			logger.error("Exception in preparing request for ATOM ", e);
		}

	}

	public void setStatusEnquiry(Fields fields, Transaction transaction) {

		try {
		}

		catch (Exception e) {
			logger.error("Exception in preparing Atom Status Enquiry Request", e);
		}

	}

	public void setRefund(Fields fields, Transaction transaction) {

		try {

			String pattern = "dd-MM-yyyy";
			String dateInString =new SimpleDateFormat(pattern).format(new Date());

			String ingenicoSaleUrl = PropertiesManager.propertiesMap.get("INGENICOSaleUrl");
			
			transaction.setMerchantCode(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setAmount(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));
			
			transaction.setCurrencyCode(Constants.CURRENCY_CODE);
			transaction.setRequestType(Constants.REFUND_REQ_CODE);
			transaction.setMerchantTxnRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setTxnDate(dateInString);
			transaction.setBankCode(fields.get(FieldType.ADF3.getName()));
			transaction.setWebServiceLoc(ingenicoSaleUrl);
			transaction.setKey(fields.get(FieldType.TXN_KEY.getName()));
			transaction.setIv(fields.get(FieldType.PASSWORD.getName()));
			transaction.setTpsl_txn_id(fields.get(FieldType.ACQ_ID.getName()));
			transaction.setTimeout(Constants.TIMEOUT);
			
		}

		catch (Exception e) {
			logger.error("Exception in preparing Refund Request for Ingenico ", e);
		}

	}

}
