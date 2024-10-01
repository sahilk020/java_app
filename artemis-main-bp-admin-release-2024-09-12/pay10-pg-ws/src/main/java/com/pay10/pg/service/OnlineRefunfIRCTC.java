package com.pay10.pg.service;

public class OnlineRefunfIRCTC {
	 	private String terminalID;
	    private String terminalpwd;
	    private int currencyType;
	    private String refundId;
	    private String reservationId;
	    private String bankTxnId;
	    private double txnAmount;
	    private String checkSum;
	    private String refundType;
	    private String merchantCode;
	    private double refundAmount;
	    private String canDate;
	    
	   
	    public String getCanDate() {
			return canDate;
		}
		public void setCanDate(String canDate) {
			this.canDate = canDate;
		}
		public double getRefundAmount() {
			return refundAmount;
		}
		public void setRefundAmount(double refundAmount) {
			this.refundAmount = refundAmount;
		}
		public String getMerchantCode() {
			return merchantCode;
		}
		public void setMerchantCode(String merchantCode) {
			this.merchantCode = merchantCode;
		}
		public String getRefundType() {
	    	return refundType;
	    }
	    public void setRefundType(String refundType) {
	    	this.refundType=refundType;
	    }
		public String getTerminalID() {
			return terminalID;
		}
		public void setTerminalID(String terminalID) {
			this.terminalID = terminalID;
		}
		public String getTerminalpwd() {
			return terminalpwd;
		}
		public void setTerminalpwd(String terminalpwd) {
			this.terminalpwd = terminalpwd;
		}
		public int getCurrencyType() {
			return currencyType;
		}
		public void setCurrencyType(int currencyType) {
			this.currencyType = currencyType;
		}
		public String getRefundId() {
			return refundId;
		}
		public void setRefundId(String refundId) {
			this.refundId = refundId;
		}
		public String getReservationId() {
			return reservationId;
		}
		public void setReservationId(String reservationId) {
			this.reservationId = reservationId;
		}
		public String getBankTxnId() {
			return bankTxnId;
		}
		public void setBankTxnId(String bankTxnId) {
			this.bankTxnId = bankTxnId;
		}
		public double getTxnAmount() {
			return txnAmount;
		}
		public void setTxnAmount(double txnAmount) {
			this.txnAmount = txnAmount;
		}
		public String getCheckSum() {
			return checkSum;
		}
		public void setCheckSum(String checkSum) {
			this.checkSum = checkSum;
		}
	    
	    
}
