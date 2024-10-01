package com.crmws.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChargeBackAutoPopulateData {
	

	@JsonProperty("merchant_pay_id")
	private String payId;
	
	@JsonProperty("cb_case_id")
	private String cbCaseId;
	
	@JsonProperty("pgRefNo")
	private String pgRefNo;
	
	@JsonProperty("txn_amount")
	private String txnAmount;

	@JsonProperty(value="date_of_txn")
	private String dateOfTxn;
	
	@JsonProperty("merchant_txn_id")
	private String merchantTxnId;
	
	@JsonProperty("order_id")
	private String orderId;
	
	@JsonProperty("bank_txn_id")
	private String bankTxnId;
	
	
	@JsonProperty("mode_of_payment")
	private String modeOfPayment;
	
	@JsonProperty("acq_name")
	private String acquirerName;
	
	@JsonProperty(value="settlement_date")
	private String settlementDate;
	
	@JsonProperty("customer_name")
	private String merchantName;
	
	@JsonProperty("customer_phone")
	private String mobile;
	
	@JsonProperty("email")
	private String email;
	
	private String currencyName;
	private String nemail;
		
	
}
