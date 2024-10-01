package com.pay10.matchmove;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Service("matchMoveTransaction")
public class Transaction implements Serializable {
	

	private static final long serialVersionUID = -2959431086258801106L;
	
	private String convenience_fee;
	private String mobile;
	private String mobile_country_code;
	private String order_id;
	private String product_desc;
	private String purchase_amount;
	private String purchase_currency;
	private String txn_ref_id;
	private String email;
	private String merchant_name;
	private String response_code;
	private String response_message;
	private String checkout_ref_id;
	private String transaction_status;
	private String refundAmount;
	private String transaction_response_message;
	private String redirect_url;
	
	public String getConvenience_fee() {
		return convenience_fee;
	}
	public void setConvenience_fee(String convenience_fee) {
		this.convenience_fee = convenience_fee;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMobile_country_code() {
		return mobile_country_code;
	}
	public void setMobile_country_code(String mobile_country_code) {
		this.mobile_country_code = mobile_country_code;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getProduct_desc() {
		return product_desc;
	}
	public void setProduct_desc(String product_desc) {
		this.product_desc = product_desc;
	}
	public String getPurchase_amount() {
		return purchase_amount;
	}
	public void setPurchase_amount(String purchase_amount) {
		this.purchase_amount = purchase_amount;
	}
	public String getPurchase_currency() {
		return purchase_currency;
	}
	public void setPurchase_currency(String purchase_currency) {
		this.purchase_currency = purchase_currency;
	}
	public String getTxn_ref_id() {
		return txn_ref_id;
	}
	public void setTxn_ref_id(String txn_ref_id) {
		this.txn_ref_id = txn_ref_id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMerchant_name() {
		return merchant_name;
	}
	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}
	public String getResponse_code() {
		return response_code;
	}
	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}
	public String getResponse_message() {
		return response_message;
	}
	public void setResponse_message(String response_message) {
		this.response_message = response_message;
	}
	
	 public void setEnrollment(Fields fields) {
			setCurrency(fields);	
		}
	 
	
	public String getTransaction_response_message() {
		return transaction_response_message;
	}
	public void setTransaction_response_message(String transaction_response_message) {
		this.transaction_response_message = transaction_response_message;
	}
	public String getTransaction_status() {
		return transaction_status;
	}
	public void setTransaction_status(String transaction_status) {
		this.transaction_status = transaction_status;
	}
	public String getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getCheckout_ref_id() {
		return checkout_ref_id;
	}
	public void setCheckout_ref_id(String checkout_ref_id) {
		this.checkout_ref_id = checkout_ref_id;
	}
	
	
	
	
	
	public String getRedirect_url() {
		return redirect_url;
	}
	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
	}
	public void setCurrency(Fields fields) {
		String currencyCode =fields.get(FieldType.CURRENCY_CODE.getName());
		String currency = Currency.getAlphabaticCode(currencyCode);
		setPurchase_currency(currency);	
	}
	


}
