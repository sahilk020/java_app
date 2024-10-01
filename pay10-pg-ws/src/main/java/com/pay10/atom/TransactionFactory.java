package com.pay10.atom;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.util.*;
import com.pay10.pg.core.util.AtomSecureCardData;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service("atomFactory")
public class TransactionFactory {

	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private AtomSecureCardData atomSecureCardData;

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
			String pattern = "dd/MM/yyyy%20HH:mm:ss";
			DateFormat df = new SimpleDateFormat(pattern);
			Date dateToday = Calendar.getInstance().getTime();
			String dateNow = df.format(dateToday);

			String atomReturnUrl = returnUrlCustomizer.customizeReturnUrl(fields, PropertiesManager.propertiesMap.get(Constants.ATOM_RESPONSE_URL));
			transaction.setProdid(fields.get(FieldType.ADF2.getName()));
			transaction.setAmt(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));
			transaction.setTxncurr(Constants.TXN_CURR);
			// txnscamt is 0 since no value needs to be added by acquired over the total
			// amount
			transaction.setTxnscamt("0");
			transaction.setClientcode(Constants.CLIENT_CODE);
			transaction.setTxnid(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setDate(dateNow);
			transaction.setRu(atomReturnUrl);

			// New ID for Each Transaction
			transaction.setCustacc(TransactionManager.getNewTransactionId());
			
			// Parameters for Net Banking
			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {

				transaction.setMdd("NB");
				transaction.setTtype(Constants.NB_T_TYPE);
				transaction.setBankid(AtomMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			} if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.WALLET.getCode())) {

				//transaction.setMdd("WL");
				transaction.setTtype(Constants.WL_T_TYPE);
				transaction.setMdd("MW");
				transaction.setBankid(AtomMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			} 
			
			else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {

				fields.put(FieldType.CARD_MASK.getName(),fields.get(FieldType.PAYER_ADDRESS.getName()));
				transaction.setMdd("UP|SMSUPI|"+fields.get(FieldType.PAYER_ADDRESS.getName()));
				transaction.setTtype(Constants.NB_T_TYPE);
				//transaction.setBankid(AtomMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(
					PaymentType.EMI.getCode())) {
				transaction.setMdd("EMI");
				transaction.setTtype(Constants.NB_T_TYPE);
				transaction.setUdf5(fields.get(FieldType.EMI_BANK_ID.getName()));
				transaction.setUdf6(fields.get(FieldType.TENURE_LENGTH.getName()));
			}
			
			// Parameters for Cards
			else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
					|| fields.get(FieldType.PAYMENT_TYPE.getName())
							.equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {

				String cardEncKey = fields.get(FieldType.ADF10.getName());
				transaction.setTtype(Constants.CCDC_T_TYPE);

				String cardNo = fields.get(FieldType.CARD_NUMBER.getName());
				String cvv = fields.get(FieldType.CVV.getName());
				String expMonth = fields.get(FieldType.CARD_EXP_DT.getName()).substring(0, 2);
				String expYear = fields.get(FieldType.CARD_EXP_DT.getName()).substring(2, 6);

				StringBuilder data = new StringBuilder();

				data.append(cardNo);
				data.append("|");
				data.append(cvv);
				data.append("|");
				data.append(expYear);
				data.append("|");
				data.append(expMonth);

				String encCardData = atomSecureCardData.encryptData(data.toString(), cardEncKey);
				String cardHname = fields.get(FieldType.CARD_HOLDER_NAME.toString());
				String cardType = fields.get(FieldType.PAYMENT_TYPE.toString());
				String cardAssociate = "";

				if (fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.VISA.getCode())) {
					cardAssociate = "VISA";
				} else if (fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.MASTERCARD.getCode())) {
					cardAssociate = "MASTER";
				} else if (fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.RUPAY.getCode())) {
					cardAssociate = "RUPAY";
				} else if (fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.AMEX.getCode())) {
					cardAssociate = "AMEX";
				}

				String mdd = "channelid=INT|carddata=" + encCardData + "|cardhname=" + cardHname + "|cardtype="
						+ cardType + "|card=" + cardAssociate;
				transaction.setMdd(mdd);

			}

			// Optional echo back fields by ATOM not added to transaction for now
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
				transaction.setUdf1(fields.get(FieldType.CUST_NAME.getName()));
			}
			else {
				transaction.setUdf1("Pay10Customer");
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_EMAIL.getName()))) {
				transaction.setUdf2(fields.get(FieldType.CUST_EMAIL.getName()));
			}
			else {
				transaction.setUdf2("customer@pay10.com");
			}
			
			
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
				transaction.setUdf3(fields.get(FieldType.CUST_PHONE.getName()));
			}
			else {
				transaction.setUdf3("9911000000");
			}
			
			String udf4 = "";
			String udf5 = "";
			String udf6 = "";
			String udf9 = "";

		}

		catch (Exception e) {
			logger.error("Exception in preparing request for ATOM ", e);
		}

	}

	public void setStatusEnquiry(Fields fields, Transaction transaction) {

		try {

			String login = fields.get(FieldType.MERCHANT_ID.getName());
			String dateString = null;
			String date = null;
			dateString = fieldsDao.getCaptureDate(fields.get(FieldType.PG_REF_NUM.getName()));

			if (StringUtils.isNotBlank(dateString)) {
				date = dateString.substring(0, 10);
				transaction.setDate(date);
			}
			transaction.setAmt(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));
			transaction.setLogin(login);
		}

		catch (Exception e) {
			logger.error("Exception in preparing Atom Status Enquiry Request", e);
		}

	}

	public void setRefund(Fields fields, Transaction transaction) {

		try {

			String login = fields.get(FieldType.MERCHANT_ID.getName());
			//String pass = fields.get(FieldType.PASSWORD.getName());ADF11
			String pass = fields.get(FieldType.ADF11.getName());
			byte[] bytesEncoded = Base64.encodeBase64(pass.getBytes());
			String baseEncodedPass = new String(bytesEncoded);

			String dateString = null;
			String date = null;
			dateString = fieldsDao.getCaptureDateByOrderId(fields.get(FieldType.ORDER_ID.getName()));

			if (StringUtils.isNotBlank(dateString)) {
				date = dateString.substring(0, 10);
				transaction.setDate(date);
			}
			transaction.setAmt(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));
			transaction.setTxnid(fields.get(FieldType.ACQ_ID.getName()));
			transaction.setLogin(login);
			transaction.setPass(baseEncodedPass);
			transaction.setMerefundref(fields.get(FieldType.TXN_ID.getName()));
		}

		catch (Exception e) {
			logger.error("Exception in preparing Refund Request", e);
		}

	}

}
