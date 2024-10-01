package com.pay10.payout.payten;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaytenTransactionRequest {

	@JsonProperty("beneficiary")
	public Beneficiary getBeneficiary() {
		return this.beneficiary;
	}

	public void setBeneficiary(Beneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}

	Beneficiary beneficiary;

	@JsonProperty("code")
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	String code;

	@JsonProperty("moneyRemittance")
	public MoneyRemittance getMoneyRemittance() {
		return this.moneyRemittance;
	}

	public void setMoneyRemittance(MoneyRemittance moneyRemittance) {
		this.moneyRemittance = moneyRemittance;
	}

	MoneyRemittance moneyRemittance;

	@JsonProperty("merchantTransId")
	public String getMerchantTransId() {
		return this.merchantTransId;
	}

	public void setMerchantTransId(String merchantTransId) {
		this.merchantTransId = merchantTransId;
	}

	String merchantTransId;

	@JsonProperty("message")
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	String message;

	@JsonProperty("bankResponse")
	public String getBankResponse() {
		return this.bankResponse;
	}

	public void setBankResponse(String bankResponse) {
		this.bankResponse = bankResponse;
	}

	String bankResponse;

	@JsonProperty("response")
	public String getResponse() {
		return this.response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	String response;

	@JsonProperty("hash")
	public String getHash() {
		return this.hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	String hash;

	private String responseCode;
	private String status;
	private String responseMessage;

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public class Beneficiary {
		@JsonProperty("accountNo")
		public String getAccountNo() {
			return this.accountNo;
		}

		public void setAccountNo(String accountNo) {
			this.accountNo = accountNo;
		}

		String accountNo;

		@JsonProperty("beneficiaryName")
		public String getBeneficiaryName() {
			return this.beneficiaryName;
		}

		public void setBeneficiaryName(String beneficiaryName) {
			this.beneficiaryName = beneficiaryName;
		}

		String beneficiaryName;

		@JsonProperty("ifscCode")
		public String getIfscCode() {
			return this.ifscCode;
		}

		public void setIfscCode(String ifscCode) {
			this.ifscCode = ifscCode;
		}

		String ifscCode;
	}

	public class MoneyRemittance {
		@JsonProperty("amount")
		public double getAmount() {
			return this.amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

		double amount;

		@JsonProperty("charges")
		public double getCharges() {
			return this.charges;
		}

		public void setCharges(double charges) {
			this.charges = charges;
		}

		double charges;

		@JsonProperty("paymentId")
		public String getPaymentId() {
			return this.paymentId;
		}

		public void setPaymentId(String paymentId) {
			this.paymentId = paymentId;
		}

		String paymentId;

		@JsonProperty("transDate")
		public String getTransDate() {
			return this.transDate;
		}

		public void setTransDate(String transDate) {
			this.transDate = transDate;
		}

		String transDate;

		@JsonProperty("transferStatus")
		public String getTransferStatus() {
			return this.transferStatus;
		}

		public void setTransferStatus(String transferStatus) {
			this.transferStatus = transferStatus;
		}

		String transferStatus;

		@JsonProperty("transferType")
		public String getTransferType() {
			return this.transferType;
		}

		public void setTransferType(String transferType) {
			this.transferType = transferType;
		}

		String transferType;

		@JsonProperty("transId")
		public String getTransId() {
			return this.transId;
		}

		public void setTransId(String transId) {
			this.transId = transId;
		}

		String transId;
	}

}
