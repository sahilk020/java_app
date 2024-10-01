package com.pay10.federalNB;

import org.springframework.stereotype.Service;

@Service("federalBankNBTransaction")
public class Transaction {
	
	private String amount;
	private String order_id;
	private String transaction_id;
	private String payment_mode;
	private String payment_channel;
	private String payment_datetime;
	private String response_code;
	private String response_message;
	private String merchant_refund_id;
	private String merchant_order_id;
	private String refund_status;
	private String refund_reference_no;
	private String refund_id;
	private String code;
	private String message;
	private String error_desc;
	private String bank_transaction_id;
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}
	public String getPayment_channel() {
		return payment_channel;
	}
	public void setPayment_channel(String payment_channel) {
		this.payment_channel = payment_channel;
	}
	public String getPayment_datetime() {
		return payment_datetime;
	}
	public void setPayment_datetime(String payment_datetime) {
		this.payment_datetime = payment_datetime;
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
	public String getMerchant_refund_id() {
		return merchant_refund_id;
	}
	public void setMerchant_refund_id(String merchant_refund_id) {
		this.merchant_refund_id = merchant_refund_id;
	}
	public String getRefund_status() {
		return refund_status;
	}
	public void setRefund_status(String refund_status) {
		this.refund_status = refund_status;
	}
	public String getRefund_reference_no() {
		return refund_reference_no;
	}
	public void setRefund_reference_no(String refund_reference_no) {
		this.refund_reference_no = refund_reference_no;
	}
	public String getRefund_id() {
		return refund_id;
	}
	public void setRefund_id(String refund_id) {
		this.refund_id = refund_id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPayment_mode() {
		return payment_mode;
	}
	public void setPayment_mode(String payment_mode) {
		this.payment_mode = payment_mode;
	}
	public String getError_desc() {
		return error_desc;
	}
	public void setError_desc(String error_desc) {
		this.error_desc = error_desc;
	}
	public String getMerchant_order_id() {
		return merchant_order_id;
	}
	public void setMerchant_order_id(String merchant_order_id) {
		this.merchant_order_id = merchant_order_id;
	}
	public String getBank_transaction_id() {
		return bank_transaction_id;
	}
	public void setBank_transaction_id(String bank_transaction_id) {
		this.bank_transaction_id = bank_transaction_id;
	}
	
	
	
	
	

}
