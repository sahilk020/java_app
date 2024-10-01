package com.crmws.loadtest;

public class RequestDTO {

	private String payId;
	private String keySalt;
	private String salt;
	private String amount;
	private String mopTyep;
	private String paymentType;
	private String returnURL;
	private String cardNo;
	private String expiryDate;
	private String cvv;
	private String custName;
	private String custEmail;
	private String custPhone;
	private String currencyCode;
	private String payerAddress; // PAYER_ADDRESS

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getKeySalt() {
		return keySalt;
	}

	public void setKeySalt(String keySalt) {
		this.keySalt = keySalt;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMopTyep() {
		return mopTyep;
	}

	public void setMopTyep(String mopTyep) {
		this.mopTyep = mopTyep;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getCustPhone() {
		return custPhone;
	}

	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getPayerAddress() {
		return payerAddress;
	}

	public void setPayerAddress(String payerAddress) {
		this.payerAddress = payerAddress;
	}

	@Override
	public String toString() {
		return "RequestDTO [payId=" + payId + ", keySalt=" + keySalt + ", salt=" + salt + ", amount=" + amount
				+ ", mopTyep=" + mopTyep + ", paymentType=" + paymentType + ", returnURL=" + returnURL + ", cardNo="
				+ cardNo + ", expiryDate=" + expiryDate + ", cvv=" + cvv + ", custName=" + custName + ", custEmail="
				+ custEmail + ", custPhone=" + custPhone + ", currencyCode=" + currencyCode + ", payerAddress="
				+ payerAddress + "]";
	}

}
