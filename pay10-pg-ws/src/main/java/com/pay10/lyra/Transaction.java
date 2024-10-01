package com.pay10.lyra;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Service("lyraTransaction")
public class Transaction {

	private String action;
	private String amount;
	private String expMonth;
	private String expYear;
	private String id;
	private String card;
	private String cvv;
	private String type;
	private String member;
	private String password;
	private String result;
	private String auth;
	private String ref;
	private String avr;
	private String tranId;
	private String payId;
	private String paymentType;
	private String cardIssuerCountry;
	private String cardType;
	private String transactionType;
	private String currencyCode;
	private String acsUrl;
	private String pareq;
	private String status;
	private String merchantId;
	private String submitUrl;
	private String pgDateTime;
	private String uuid;
	private String errorCode;
	private String errorMessage;
	private String rrn;
	private String chargeFlag;
	
	

	public void setEnrollment(Fields fields) {
		setCardDetails(fields);

	}

	public void setSale(Fields fields) {
		setCard(fields.get(FieldType.PAYER_ADDRESS.getName()));
		setMember(fields.get(FieldType.PAYER_NAME.getName()));
		fields.put(FieldType.CUST_NAME.getName(), fields.get(FieldType.PAYER_NAME.getName()));
	}

	private void setCardDetails(Fields fields) {
		setCard(fields.get(FieldType.CARD_NUMBER.getName()));
		setCvv(fields.get(FieldType.CVV.getName()));
		String expDate = fields.get(FieldType.CARD_EXP_DT.getName());
		setExpYear(expDate.substring(4, 6));
		setExpMonth(expDate.substring(0, 2));
		setMember(fields.get(FieldType.CUST_NAME.getName()));
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getExpMonth() {
		return expMonth;
	}

	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}

	public String getExpYear() {
		return expYear;
	}

	public void setExpYear(String expYear) {
		this.expYear = expYear;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getAvr() {
		return avr;
	}

	public void setAvr(String avr) {
		this.avr = avr;
	}

	public String getTranId() {
		return tranId;
	}

	public void setTranId(String tranId) {
		this.tranId = tranId;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getCardIssuerCountry() {
		return cardIssuerCountry;
	}

	public void setCardIssuerCountry(String cardIssuerCountry) {
		this.cardIssuerCountry = cardIssuerCountry;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getAcsUrl() {
		return acsUrl;
	}

	public void setAcsUrl(String acsUrl) {
		this.acsUrl = acsUrl;
	}

	public String getPareq() {
		return pareq;
	}

	public void setPareq(String pareq) {
		this.pareq = pareq;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getSubmitUrl() {
		return submitUrl;
	}

	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl;
	}

	public String getPgDateTime() {
		return pgDateTime;
	}

	public void setPgDateTime(String pgDateTime) {
		this.pgDateTime = pgDateTime;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getChargeFlag() {
		return chargeFlag;
	}

	public void setChargeFlag(String chargeFlag) {
		this.chargeFlag = chargeFlag;
	}
	

}
