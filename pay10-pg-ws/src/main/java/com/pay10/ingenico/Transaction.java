package com.pay10.ingenico;

import org.springframework.stereotype.Service;

@Service("ingenicoTransaction")
public class Transaction {

	private String requestType;
	private String merchantCode;
	private String merchantTxnRefNum;
	private String amount;
	private String currencyCode;
	private String returnUrl;
	private String shoppingCartDet;
	private String txnDate;
	private String bankCode;
	private String webServiceLoc;
	private String mobileNum;
	private String key;
	private String iv;
	private String custName;
	private String timeout;
	private String cardNum;
	private String cardName;
	private String expMM;
	private String expYYYY;
	private String cardCvv;
	
	private String txn_status;
	private String txn_msg;
	private String txn_err_msg;
	private String clnt_txn_ref;
	private String tpsl_bank_cd;
	private String tpsl_txn_id;
	private String tpsl_rfnd_id;
	private String rqst_token;
	
	
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getMerchantCode() {
		return merchantCode;
	}
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
	public String getMerchantTxnRefNum() {
		return merchantTxnRefNum;
	}
	public void setMerchantTxnRefNum(String merchantTxnRefNum) {
		this.merchantTxnRefNum = merchantTxnRefNum;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getShoppingCartDet() {
		return shoppingCartDet;
	}
	public void setShoppingCartDet(String shoppingCartDet) {
		this.shoppingCartDet = shoppingCartDet;
	}
	public String getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getWebServiceLoc() {
		return webServiceLoc;
	}
	public void setWebServiceLoc(String webServiceLoc) {
		this.webServiceLoc = webServiceLoc;
	}
	public String getMobileNum() {
		return mobileNum;
	}
	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getIv() {
		return iv;
	}
	public void setIv(String iv) {
		this.iv = iv;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getExpMM() {
		return expMM;
	}
	public void setExpMM(String expMM) {
		this.expMM = expMM;
	}
	public String getExpYYYY() {
		return expYYYY;
	}
	public void setExpYYYY(String expYYYY) {
		this.expYYYY = expYYYY;
	}
	public String getCardCvv() {
		return cardCvv;
	}
	public void setCardCvv(String cardCvv) {
		this.cardCvv = cardCvv;
	}
	public String getTxn_status() {
		return txn_status;
	}
	public void setTxn_status(String txn_status) {
		this.txn_status = txn_status;
	}
	public String getTxn_msg() {
		return txn_msg;
	}
	public void setTxn_msg(String txn_msg) {
		this.txn_msg = txn_msg;
	}
	public String getTxn_err_msg() {
		return txn_err_msg;
	}
	public void setTxn_err_msg(String txn_err_msg) {
		this.txn_err_msg = txn_err_msg;
	}
	public String getClnt_txn_ref() {
		return clnt_txn_ref;
	}
	public void setClnt_txn_ref(String clnt_txn_ref) {
		this.clnt_txn_ref = clnt_txn_ref;
	}
	public String getTpsl_bank_cd() {
		return tpsl_bank_cd;
	}
	public void setTpsl_bank_cd(String tpsl_bank_cd) {
		this.tpsl_bank_cd = tpsl_bank_cd;
	}
	public String getTpsl_txn_id() {
		return tpsl_txn_id;
	}
	public void setTpsl_txn_id(String tpsl_txn_id) {
		this.tpsl_txn_id = tpsl_txn_id;
	}
	public String getTpsl_rfnd_id() {
		return tpsl_rfnd_id;
	}
	public void setTpsl_rfnd_id(String tpsl_rfnd_id) {
		this.tpsl_rfnd_id = tpsl_rfnd_id;
	}
	public String getRqst_token() {
		return rqst_token;
	}
	public void setRqst_token(String rqst_token) {
		this.rqst_token = rqst_token;
	}
	
	
	
}
