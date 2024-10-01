package com.pay10.payu;

import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
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
import com.pay10.pg.core.payu.util.Constants;
import com.pay10.pg.core.payu.util.PayuHasher;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;

@Service("payuFactory")
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
			fields.put(FieldType.CUST_NAME.getName(), transaction.getFirstName());
			break;
		case REFUND:

			fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			setRefund(fields, transaction);
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));

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
			String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
					PropertiesManager.propertiesMap.get(Constants.PAYU_RESPONSE_URL));
			String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			String firstName = fields.get(FieldType.CUST_NAME.getName());
			String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
			String mopType = fields.get(FieldType.MOP_TYPE.getName());
			String bankCode = fields.get(FieldType.MOP_TYPE.getName());
			if(paymentType.equals(Constants.WL)) {
				paymentType= Constants.CASH;
			}
			if(paymentType.equals(Constants.NB)||paymentType.equals(Constants.CASH)) {
				bankCode = PayuNBMopType.getBankCode(mopType);
			}else {
				transaction.setCcnum(fields.get(FieldType.CARD_NUMBER.getName()));
				transaction.setCcname(fields.get(FieldType.CARD_HOLDER_NAME.getName())); 
				transaction.setCcvv(fields.get(FieldType.CVV.getName()));
				transaction.setCcExpYr(fields.get(FieldType.CARD_EXP_DT.getName()));
				transaction.setCcExpMon(fields.get(FieldType.CARD_EXP_DT.getName()));
			}
			
			//transaction.setKey(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setKey(fields.get(FieldType.TXN_KEY.getName()));
			transaction.setTxnId(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setAmount(amount);
			transaction.setProductInfo(Constants.PAY10_PRODUCT);
			 
			if(StringUtils.isEmpty(firstName)) {
				fields.put(FieldType.CUST_NAME.getName(), Constants.PAY10);
			} else if (firstName.length() > 20){
				firstName = firstName.substring(0, 20);
				fields.put(FieldType.CUST_NAME.getName(), firstName); 
			}
			
			fields.put(FieldType.PASSWORD.getName(), fields.get(FieldType.ADF2.getName()));

			transaction.setFirstName(fields.get(FieldType.CUST_NAME.getName()));
			transaction.setEmail(Constants.PAY10_PAYU_EMAIL);
			transaction.setSurl(returnUrl);
			transaction.setFurl(returnUrl);
			transaction.setPg(paymentType);
			transaction.setBankCode(bankCode);
			transaction.setConsentShared("0");
			//transaction.setSalt(fields.get(FieldType.PASSWORD.getName()));
			transaction.setSalt(fields.get(FieldType.ADF2.getName()));

			TreeMap<String, String> payuParams = new TreeMap<String, String>();

			payuParams.put(Constants.KEY, transaction.getKey());
			payuParams.put(Constants.TXNID, transaction.getTxnId());
			payuParams.put(Constants.AMOUNT, transaction.getAmount());
			payuParams.put(Constants.PRODUCT_INFO, transaction.getProductInfo());
			payuParams.put(Constants.FIRSTNAME, transaction.getFirstName());
			payuParams.put(Constants.EMAIL, transaction.getEmail());
			payuParams.put(Constants.SALT, transaction.getSalt());
			String hash = PayuHasher.payuSaleRequestHash(payuParams);
			transaction.setHash(hash);

		}

		catch (Exception e) {
			logger.error("Exception in preapring request for Payu ", e);
		}

	}

	public void setStatusEnquiry(Fields fields, Transaction transaction) {

		try {

			transaction.setKey(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setTxnId(fields.get(FieldType.ORDER_ID.getName()));
			transaction.setSalt(fields.get(FieldType.PASSWORD.getName()));

			TreeMap<String, String> payuParams = new TreeMap<String, String>();

			payuParams.put(Constants.KEY, transaction.getKey());
			payuParams.put(Constants.COMMAND, Constants.VERIFY_PAYMENT);
			payuParams.put(Constants.VAR1, transaction.getTxnId());
			payuParams.put(Constants.SALT, transaction.getSalt());

			String hash = PayuHasher.payuRefundAndStatusEnqHash(payuParams);
			transaction.setHash(hash);

		}

		catch (Exception e) {
			logger.error("Exception in preparing Payu Status Enquiry Request", e);
		}

	}

	public void setRefund(Fields fields, Transaction transaction) {

		try {
			transaction.setKey(fields.get(FieldType.TXN_KEY.getName()));
			transaction.setMihPayuId(fields.get(FieldType.ACQ_ID.getName()));
			transaction.setSalt(fields.get(FieldType.ADF2.getName()));
			transaction.setRefundToken(fields.get(FieldType.REFUND_ORDER_ID.getName()));
			String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			transaction.setRefundAmount(amount);

			TreeMap<String, String> payuParams = new TreeMap<String, String>();

			payuParams.put(Constants.KEY, transaction.getKey());
			payuParams.put(Constants.COMMAND, Constants.CANCEL_REFUND_TRANSACTION);
			payuParams.put(Constants.VAR1, transaction.getMihPayuId());
			payuParams.put(Constants.VAR2, transaction.getRefundToken());
			payuParams.put(Constants.VAR3, transaction.getRefundAmount());
			payuParams.put(Constants.SALT, transaction.getSalt());
logger.info("checksum request for payu "+payuParams.toString());
			String hash = PayuHasher.payuRefundAndStatusEnqHash(payuParams);
			logger.info("checksum complete for payu "+hash);
			transaction.setHash(hash);

		}

		catch (Exception e) {
			logger.error("Exception in preapring request for Paytu ", e);
		}

	}

}
