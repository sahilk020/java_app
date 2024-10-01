package com.pay10.payten;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("paytenFactory")
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
					PropertiesManager.propertiesMap.get(Constants.PAYTEN_RESPONSE_URL));

			transaction.setPayId(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setSaltKey(fields.get(FieldType.TXN_KEY.getName()));
			transaction.setEncKey(fields.get(FieldType.ADF1.getName()));
			transaction.setAmount(fields.get(FieldType.TOTAL_AMOUNT.getName()));

			transaction.setOrderId(fields.get(FieldType.ORDER_ID.getName()));
			transaction.setCurrencyCode(fields.get(FieldType.CURRENCY_CODE.getName()));

			if (StringUtils.isNotBlank(fields.get(FieldType.PAYMENT_TYPE.getName()))
					&& (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
							|| fields.get(FieldType.PAYMENT_TYPE.getName())
									.equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode()))) {

				transaction.setCardNumber(fields.get(FieldType.CARD_NUMBER.getName()));
				transaction.setExpiryDate(fields.get(FieldType.CARD_EXP_DT.getName()));
				transaction.setCavv(fields.get(FieldType.CVV.getName()));
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
				transaction.setCustName(fields.get(FieldType.CUST_NAME.getName()));
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.CARD_HOLDER_NAME.getName()))) {
				transaction.setCardHolderName(fields.get(FieldType.CARD_HOLDER_NAME.getName()));
			} else {
				transaction.setCardHolderName("paytenUser");
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_EMAIL.getName()))) {
				transaction.setCustEmail(fields.get(FieldType.CUST_EMAIL.getName()));
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
				transaction.setCustPhone(fields.get(FieldType.CUST_PHONE.getName()));
			}

			transaction.setPaymentType(fields.get(FieldType.PAYMENT_TYPE.getName()));
			transaction.setMopType(fields.get(FieldType.MOP_TYPE.getName()));

			transaction.setRedirectUrl(returnUrl);

			if (StringUtils.isNotBlank(fields.get(FieldType.PAYMENT_TYPE.getName()))
					&& fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
				transaction.setPayerAddress(fields.get(FieldType.PAYER_ADDRESS.getName()));
			}

			/*
			 * // Generate Hash Fields hashFields = new Fields();
			 * hashFields.put(FieldType.PAY_ID.getName(), transaction.getPayId());
			 * hashFields.put(FieldType.ORDER_ID.getName(), transaction.getOrderId());
			 * 
			 * if (StringUtils.isNotBlank(fields.get(FieldType.PAYMENT_TYPE.getName())) &&
			 * (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.
			 * CREDIT_CARD.getCode()) || fields.get(FieldType.PAYMENT_TYPE.getName())
			 * .equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode()))) {
			 * 
			 * hashFields.put(FieldType.CARD_NUMBER.getName(), transaction.getCardNumber());
			 * hashFields.put(FieldType.CARD_EXP_DT.getName(), transaction.getExpiryDate());
			 * hashFields.put(FieldType.CVV.getName(), transaction.getCavv());
			 * 
			 * }
			 * 
			 * hashFields.put(FieldType.CARD_HOLDER_NAME.getName(),
			 * transaction.getCardHolderName());
			 * 
			 * if (StringUtils.isNotBlank(transaction.getCustPhone())) {
			 * hashFields.put(FieldType.CUST_PHONE.getName(), transaction.getCustPhone()); }
			 * 
			 * if (StringUtils.isNotBlank(transaction.getCustEmail())) {
			 * hashFields.put(FieldType.CUST_EMAIL.getName(), transaction.getCustEmail()); }
			 * 
			 * if (StringUtils.isNotBlank(transaction.getCustName())) {
			 * hashFields.put(FieldType.CUST_NAME.getName(), transaction.getCustName()); }
			 * 
			 * hashFields.put(FieldType.PAYMENT_TYPE.getName(),
			 * transaction.getPaymentType()); hashFields.put(FieldType.MOP_TYPE.getName(),
			 * transaction.getMopType());
			 * 
			 * if (StringUtils.isNotBlank(fields.get(FieldType.PAYMENT_TYPE.getName())) &&
			 * fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI
			 * .getCode())) { hashFields.put(FieldType.PAYER_ADDRESS.getName(),
			 * transaction.getPayerAddress());
			 * 
			 * }
			 * 
			 * hashFields.put(FieldType.AMOUNT.getName(), transaction.getAmount());
			 * hashFields.put(FieldType.CURRENCY_CODE.getName(),
			 * transaction.getCurrencyCode());
			 * hashFields.put(FieldType.RETURN_URL.getName(), transaction.getRedirectUrl());
			 * 
			 * String hash = Hasher.getHashWithSalt(hashFields, transaction.getSaltKey());
			 * transaction.setHaskey(hash);
			 */
			if (StringUtils.isNotBlank(fields.get(FieldType.UDF12.getName()))) {
				transaction.setUdf12(fields.get(FieldType.UDF12.getName()));
			}
			if (StringUtils.isNotBlank(fields.get(FieldType.UDF13.getName()))) {
				transaction.setUdf13(fields.get(FieldType.UDF13.getName()));
			}
		}

		catch (Exception e) {
			logger.error("Exception in preparing request for Payten ", e);
		}

	}

	public void setStatusEnquiry(Fields fields, Transaction transaction) {

		try {
			transaction.setPayId(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setOrderId(fields.get(FieldType.ORDER_ID.getName()));
			transaction.setTxnType("STATUS");
			transaction.setAmount(fields.get(FieldType.TOTAL_AMOUNT.getName()));
			transaction.setCurrencyCode("356");

			transaction.setSaltKey(fields.get(FieldType.TXN_KEY.getName()));

			Fields hashFields = new Fields();

			hashFields.put(FieldType.PAY_ID.getName(), transaction.getPayId());
			hashFields.put(FieldType.ORDER_ID.getName(), transaction.getOrderId());
			hashFields.put(FieldType.AMOUNT.getName(), transaction.getAmount());
			hashFields.put(FieldType.TXNTYPE.getName(), transaction.getTxnType());
			hashFields.put(FieldType.CURRENCY_CODE.getName(), transaction.getCurrencyCode());

			String hash = Hasher.getHashWithSalt(hashFields, transaction.getSaltKey());
			transaction.setHaskey(hash);
		}

		catch (Exception e) {
			logger.error("Exception in preparing Payten Status Enquiry Request", e);
		}

	}

	public void setRefund(Fields fields, Transaction transaction) {

		try {

			transaction.setCurrencyCode("356");
			transaction.setPayId(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setOrderId(fields.get(FieldType.ORDER_ID.getName()));
			transaction.setRefundOrderId(fields.get(FieldType.REFUND_ORDER_ID.getName()));
			transaction.setTxnType("REFUND");
			transaction.setAmount(fields.get(FieldType.TOTAL_AMOUNT.getName()));
			transaction.setCurrencyCode("356");
			transaction.setPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setSaltKey(fields.get(FieldType.TXN_KEY.getName()));
			transaction.setEncKey(fields.get(FieldType.ADF1.getName()));

			Fields hashFields = new Fields();

			hashFields.put(FieldType.CURRENCY_CODE.getName(), "356");
			hashFields.put(FieldType.REFUND_FLAG.getName(), "R");
			hashFields.put(FieldType.PAY_ID.getName(), transaction.getPayId());
			hashFields.put(FieldType.ORDER_ID.getName(), transaction.getOrderId());
			hashFields.put(FieldType.AMOUNT.getName(), transaction.getAmount());
			hashFields.put(FieldType.TXNTYPE.getName(), transaction.getTxnType());
			hashFields.put(FieldType.REFUND_ORDER_ID.getName(), transaction.getRefundOrderId());
			hashFields.put(FieldType.PG_REF_NUM.getName(), transaction.getPgRefNum());

			String hash = Hasher.getHashWithSalt(hashFields, transaction.getSaltKey());
			transaction.setHaskey(hash);

		}

		catch (Exception e) {
			logger.error("Exception in preapring request for Payten ", e);
		}

	}

}
