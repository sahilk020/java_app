package com.pay10.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionPayload {
	
	@JsonProperty("PG_REF_NUM")
	private String pgRefNumber;
	
	@JsonProperty("TXN_ID")
	private String transactionId;
	
	@JsonProperty("ORDER_ID")
	private String orderId;
	
	@JsonProperty("TXNTYPE")
	private String transactionType;
	
	@JsonProperty("CUST_NAME")
	private String customerName;
	
	@JsonProperty("CUST_EMAIL")
	private String customerEmail;
	
	@JsonProperty("STATUS")
	private String status;
	
	@JsonProperty("PAYMENT_TYPE")
	private String paymentType;
	
	@JsonProperty("MOP_TYPE")
	private String mopType;
	
	@JsonProperty("AMOUNT")
	private String amount;
	
	@JsonProperty("TOTAL_AMOUNT")
	private String totalAmount;
	
	@JsonProperty("CREATE_DATE")
	private String createdOn;
	
	@JsonProperty("UPDATE_DATE")
	private String updatedOn;


}
