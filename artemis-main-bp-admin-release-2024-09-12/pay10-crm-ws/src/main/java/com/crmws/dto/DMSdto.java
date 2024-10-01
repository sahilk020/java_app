package com.crmws.dto;

import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pay10.commons.user.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class DMSdto {

	private long id;

	@JsonProperty("merchant_pay_id")
	private String merchantPayId;

	@JsonProperty("cb_case_id")
	private String cbCaseId;

	@JsonProperty(value = "date_of_txn")
	private String dtOfTxn;

	@JsonProperty("txn_amount")
	private String txnAmount;

	@JsonProperty("pg_case_id")
	private String pgCaseId;

	@JsonProperty("merchant_txn_id")
	private String merchantTxnId;

	@JsonProperty("order_id")
	private String orderId;

	@JsonProperty("pgRefNo")
	private String pgRefNo;

	@JsonProperty("bank_txn_id")
	private String bankTxnId;

	@JsonProperty("cb_amount")
	private String cbAmount;

	@JsonProperty("cb_reason")
	private String cbReason;

	@JsonProperty("cb_reason_code")
	private String cbReasonCode;

//@JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy-MM-dd")
	@JsonProperty("cb_intimation_date")
	private String cbIntimationDate;

//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("cb_deadline_date")
	private String cbDdlineDate;

	@JsonProperty("mode_of_payment")
	private String modeOfPayment;

	@JsonProperty("acq_name")
	private String acqName;

	@JsonProperty(value = "settlement_date")
	private String settlemtDate;

	@JsonProperty("customer_name")
	private String customerName;

	@JsonProperty("customer_phone")
	private String customerPhone;

	@JsonProperty("email")
	private String email;
	
	@JsonProperty("nemail")
	private String nemail;

	private String filePaths;
	
	private Status status;
	@Transient
	private String userType;
	@Transient
	private String emailId;
	private String createdBy;
	private String updatedBy;
	private String cbClosedInFavorBank;
	private String cbClosedInFavorMerchant;//Bank/Acquire or merchant
	private String cbPodDate;
	private String cbInitiatedDate;
	private String cbAcceptDate;
	private String cbCloseDate;
	private String cbFavourDate;
	private String merchantName;
	private String currencyName;
	private String dateWithTimeStamp;


}