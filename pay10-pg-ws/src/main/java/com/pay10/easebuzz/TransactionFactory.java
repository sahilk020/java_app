package com.pay10.easebuzz;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.EasebuzzChecksumUtil;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("easebuzzFactory")
public class TransactionFactory {
	private static final Logger logger = LoggerFactory.getLogger(TransactionFactory.class.getName());
	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	EasebuzzChecksumUtil easebuzzChecksumUtil;

	@SuppressWarnings("incomplete-switch")
	public Transaction getInstance(Fields fields) {

		Transaction transaction = new Transaction();
		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
			break;
		case REFUND:
			
			
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			transaction.setPhone(fields.get(FieldType.CUST_PHONE.getName()));
			break;
		case SALE:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			setEnrollment(fields, transaction);
			break;
		case STATUS:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.STATUS.getName());
			break;
		case ENROLL:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		}

		return transaction;
	}

	public void setEnrollment(Fields fields, Transaction transaction) {

		try {
			
			String easebuzzReturnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
					PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL));
			transaction.setEncryptionKey(fields.get(FieldType.TXN_KEY.getName()));
			transaction.setMerchantKey(fields.get(FieldType.MERCHANT_ID.getName()));
			transaction.setSalt(fields.get(FieldType.ADF1.getName()));
			transaction.setIv(fields.get(FieldType.ADF2.getName()));
			transaction.setAmount(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));

			transaction.setTxnid(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setFurl(easebuzzReturnUrl);
			transaction.setSurl(easebuzzReturnUrl);

			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
				transaction.setFirstname(fields.get(FieldType.CUST_NAME.getName()));
			} else {
				transaction.setFirstname("NA");
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_EMAIL.getName()))) {
				transaction.setEmail(fields.get(FieldType.CUST_EMAIL.getName()));
			} else {
				transaction.setEmail("NA");
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
				transaction.setPhone(fields.get(FieldType.CUST_PHONE.getName()));
			} else {
				transaction.setPhone("NA");
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.PRODUCT_DESC.getName()))) {
				transaction.setProductinfo(fields.get(FieldType.PRODUCT_DESC.getName()));
			} else {
				transaction.setProductinfo("NA");
			}

			// Parameters for Net Banking
			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {

				transaction.setPaymentMode("NB");
				transaction.setBankCode(EasebuzzMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			}

			else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {

				fields.put(FieldType.CARD_MASK.getName(), fields.get(FieldType.PAYER_ADDRESS.getName()));
				transaction.setPaymentMode("UPI");
				transaction.setUpiVA(fields.get(FieldType.PAYER_ADDRESS.getName()));
				// transaction.setMdd("UP|SMSUPI|"+fields.get(FieldType.PAYER_ADDRESS.getName()));
				// transaction.setTtype(Constants.NB_T_TYPE);
				// transaction.setBankid(AtomMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			}

			// Parameters for Cards
			else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
					|| fields.get(FieldType.PAYMENT_TYPE.getName())
							.equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {

				String cardNo = fields.get(FieldType.CARD_NUMBER.getName());
				String cvv = fields.get(FieldType.CVV.getName());
				String cardexp = fields.get(FieldType.CARD_EXP_DT.getName());
				String cardHname = fields.get(FieldType.CARD_HOLDER_NAME.toString());

				transaction.setPaymentMode(fields.get(FieldType.PAYMENT_TYPE.getName()));
				transaction.setCardNo(cardNo);
				transaction.setCvv(cvv);
				transaction.setCardExp(cardexp);
				transaction.setCardHname(cardHname);

			}

		} catch (Exception e) {
			logger.error("Exception in preparing request for ATOM ", e);
		}

	}

}
