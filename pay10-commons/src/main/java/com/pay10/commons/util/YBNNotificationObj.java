package com.pay10.commons.util;

import java.util.HashMap;
import java.util.Map;

public class YBNNotificationObj {

	private String customer_code;
	
	private String bene_account_no;
	private String bene_account_ifsc;
	private String bene_full_name;
	
	private String transfer_amt;
	private String transfer_ccy;
	private String transfer_type;
	
	
	private String transfer_unique_no;
	private String transfer_timestamp;
	
	private String rmtr_account_no;
	private String rmtr_account_ifsc;
	private String rmtr_account_type;
	private String rmtr_full_name;
	private String rmtr_address;
	private String rmtr_to_bene_note;
	private String attempt_no;
	private String status;
	private String credit_acct_no;
	private String returned_at;
	
	public static Map<String,String> creatRequestMap(YBNNotificationObj yBNNotificationObj){
		Map<String,String> requestMap= new HashMap<String,String>();
		requestMap.put(FieldType.ATTEMPT_NO.getName(), yBNNotificationObj.getAttempt_no());
		requestMap.put(FieldType.BENE_IFSC.getName(),yBNNotificationObj.getBene_account_ifsc());
		requestMap.put(FieldType.BENE_ACCOUNT_NUMBER.getName(),yBNNotificationObj.getBene_account_no());
		requestMap.put(FieldType.BENE_NAME.getName(),yBNNotificationObj.getBene_full_name());
		requestMap.put(FieldType.CREDIT_ACCT_NO.getName(),yBNNotificationObj.getCredit_acct_no());
		requestMap.put(FieldType.CUSTOMER_CODE.getName(),yBNNotificationObj.getCustomer_code());
		requestMap.put(FieldType.RETURNED_AT.getName(),yBNNotificationObj.getReturned_at());
		requestMap.put(FieldType.REMI_ACCOUNT_IFSC.getName(),yBNNotificationObj.getRmtr_account_ifsc());
		requestMap.put(FieldType.REMI_ACCOUNT_NUMBER.getName(),yBNNotificationObj.getRmtr_account_no());
		requestMap.put(FieldType.REMI_ACCOUNT_TYPE.getName(),yBNNotificationObj.getRmtr_account_type());
		requestMap.put(FieldType.REMI_ADDRESS.getName(),yBNNotificationObj.getRmtr_address());
		requestMap.put(FieldType.REMI_NAME.getName(),yBNNotificationObj.getRmtr_full_name());
		requestMap.put(FieldType.REMI_COMMENTS.getName(),yBNNotificationObj.getRmtr_to_bene_note());
		requestMap.put(FieldType.YBN_STATUS.getName(),yBNNotificationObj.getStatus());
		requestMap.put(FieldType.YBN_AMOUNT.getName(),yBNNotificationObj.getTransfer_amt());
		requestMap.put(FieldType.YBN_CURRENCY_CODE.getName(),yBNNotificationObj.getTransfer_ccy());
		requestMap.put(FieldType.TIME_STAMP.getName(),yBNNotificationObj.getTransfer_timestamp());
		requestMap.put(FieldType.TRANSFER_TYPE.getName(),yBNNotificationObj.getTransfer_type());
		requestMap.put(FieldType.BANK_TRANSACTION_ID.getName(),yBNNotificationObj.getTransfer_unique_no());
		
		return requestMap;
		
	}
	public String getCustomer_code() {
		return customer_code;
	}
	public void setCustomer_code(String customer_code) {
		this.customer_code = customer_code;
	}
	public String getBene_account_no() {
		return bene_account_no;
	}
	public void setBene_account_no(String bene_account_no) {
		this.bene_account_no = bene_account_no;
	}
	public String getBene_account_ifsc() {
		return bene_account_ifsc;
	}
	public void setBene_account_ifsc(String bene_account_ifsc) {
		this.bene_account_ifsc = bene_account_ifsc;
	}
	public String getTransfer_amt() {
		return transfer_amt;
	}
	public void setTransfer_amt(String transfer_amt) {
		this.transfer_amt = transfer_amt;
	}
	public String getTransfer_ccy() {
		return transfer_ccy;
	}
	public void setTransfer_ccy(String transfer_ccy) {
		this.transfer_ccy = transfer_ccy;
	}
	public String getTransfer_type() {
		return transfer_type;
	}
	public void setTransfer_type(String transfer_type) {
		this.transfer_type = transfer_type;
	}
	public String getTransfer_unique_no() {
		return transfer_unique_no;
	}
	public void setTransfer_unique_no(String transfer_unique_no) {
		this.transfer_unique_no = transfer_unique_no;
	}
	public String getTransfer_timestamp() {
		return transfer_timestamp;
	}
	public void setTransfer_timestamp(String transfer_timestamp) {
		this.transfer_timestamp = transfer_timestamp;
	}
	public String getRmtr_account_no() {
		return rmtr_account_no;
	}
	public void setRmtr_account_no(String rmtr_account_no) {
		this.rmtr_account_no = rmtr_account_no;
	}
	public String getRmtr_account_ifsc() {
		return rmtr_account_ifsc;
	}
	public void setRmtr_account_ifsc(String rmtr_account_ifsc) {
		this.rmtr_account_ifsc = rmtr_account_ifsc;
	}
	public String getRmtr_account_type() {
		return rmtr_account_type;
	}
	public void setRmtr_account_type(String rmtr_account_type) {
		this.rmtr_account_type = rmtr_account_type;
	}
	public String getRmtr_full_name() {
		return rmtr_full_name;
	}
	public void setRmtr_full_name(String rmtr_full_name) {
		this.rmtr_full_name = rmtr_full_name;
	}
	public String getRmtr_address() {
		return rmtr_address;
	}
	public void setRmtr_address(String rmtr_address) {
		this.rmtr_address = rmtr_address;
	}
	public String getRmtr_to_bene_note() {
		return rmtr_to_bene_note;
	}
	public void setRmtr_to_bene_note(String rmtr_to_bene_note) {
		this.rmtr_to_bene_note = rmtr_to_bene_note;
	}
	public String getAttempt_no() {
		return attempt_no;
	}
	public void setAttempt_no(String attempt_no) {
		this.attempt_no = attempt_no;
	}
	public String getBene_full_name() {
		return bene_full_name;
	}
	public void setBene_full_name(String bene_full_name) {
		this.bene_full_name = bene_full_name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCredit_acct_no() {
		return credit_acct_no;
	}
	public void setCredit_acct_no(String credit_acct_no) {
		this.credit_acct_no = credit_acct_no;
	}
	public String getReturned_at() {
		return returned_at;
	}
	public void setReturned_at(String returned_at) {
		this.returned_at = returned_at;
	}
	
}
