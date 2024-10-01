package com.crmws.dto;

public class FraudTransactionDTO {
	private String id;
	private String fraudType;
	private String merchantName;
	private String orderId;
	private String subject;
	private String txnId;
	private String paymentType;
	private String blockType;
	private String mopType;
	private double baseAmount;
	private String txnType;
	private String date;
	private FraudAction action;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFraudType() {
		return fraudType;
	}

	public void setFraudType(String fraudType) {
		this.fraudType = fraudType;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public FraudAction getAction() {
		return action;
	}

	public void setAction(FraudAction action) {
		this.action = action;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getBlockType() {
		return blockType;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public double getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(double baseAmount) {
		this.baseAmount = baseAmount;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public static class FraudAction {
		boolean ignore = false;
		boolean block = false;
		boolean notifyMerchant = false;

		public boolean isIgnore() {
			return ignore;
		}

		public void setIgnore(boolean ignore) {
			this.ignore = ignore;
		}

		public boolean isBlock() {
			return block;
		}

		public void setBlock(boolean block) {
			this.block = block;
		}

		public boolean isNotifyMerchant() {
			return notifyMerchant;
		}

		public void setNotifyMerchant(boolean notifyMerchant) {
			this.notifyMerchant = notifyMerchant;
		}

		@Override
		public String toString() {
			return "FraudAction [ignore=" + ignore + ", block=" + block + ", notifyMerchant=" + notifyMerchant + "]";
		}

	}

}
