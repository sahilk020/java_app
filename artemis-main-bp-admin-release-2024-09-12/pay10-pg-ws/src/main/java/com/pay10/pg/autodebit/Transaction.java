package com.pay10.pg.autodebit;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

public class Transaction {

	private String currency;
	private String txnDate;

	public void setEnrollment(Fields fields) {
		setMerchantInformation(fields);
		setDateTime(fields);
	}

	private void setMerchantInformation(Fields fields) {
		String currencyCode = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		setCurrency(currencyCode);
	}

	private void setDateTime(Fields fields) {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("YYYYMMdd");
		Date currentDate = new Date();
		String date = inputDateFormat.format(currentDate);
		setTxnDate(date);
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}

}
