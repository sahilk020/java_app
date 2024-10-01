package com.pay10.paytm;

import org.springframework.stereotype.Service;

@Service("paytmTransaction")
public class Transaction {

	
	// SALE
	private String MID;
	private String WEBSITE;
	private String INDUSTRY_TYPE_ID;
	private String CHANNEL_ID;
	private String ORDER_ID;
	private String CUST_ID;
	private String TXN_AMOUNT;
	private String CALLBACK_URL;
	private String CHECKSUMHASH;
	
	//REFUND AND STATUS
	private String mid;
	private String txnType;
	private String orderId;
	private String txnId;
	private String refId;
	private String refundAmount;
	private String checksum;
	private String signature;
	
	//RESPONSE
	private String RESPMSG;
	private String RESPCODE;
	private String TXNID;
	private String ORDERID;
	private String STATUS;
	private String BANKTXNID;
	private String resultStatus;
	private String resultCode;
	private String resultMsg;
	private String bankTxnId;
	private String acceptRefundStatus;
	private String refundId;
	public String getMID() {
		return MID;
	}
	public void setMID(String mID) {
		MID = mID;
	}
	public String getWEBSITE() {
		return WEBSITE;
	}
	public void setWEBSITE(String wEBSITE) {
		WEBSITE = wEBSITE;
	}
	public String getINDUSTRY_TYPE_ID() {
		return INDUSTRY_TYPE_ID;
	}
	public void setINDUSTRY_TYPE_ID(String iNDUSTRY_TYPE_ID) {
		INDUSTRY_TYPE_ID = iNDUSTRY_TYPE_ID;
	}
	public String getCHANNEL_ID() {
		return CHANNEL_ID;
	}
	public void setCHANNEL_ID(String cHANNEL_ID) {
		CHANNEL_ID = cHANNEL_ID;
	}
	public String getORDER_ID() {
		return ORDER_ID;
	}
	public void setORDER_ID(String oRDER_ID) {
		ORDER_ID = oRDER_ID;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getTXN_AMOUNT() {
		return TXN_AMOUNT;
	}
	public void setTXN_AMOUNT(String tXN_AMOUNT) {
		TXN_AMOUNT = tXN_AMOUNT;
	}
	public String getCALLBACK_URL() {
		return CALLBACK_URL;
	}
	public void setCALLBACK_URL(String cALLBACK_URL) {
		CALLBACK_URL = cALLBACK_URL;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getRESPMSG() {
		return RESPMSG;
	}
	public void setRESPMSG(String rESPMSG) {
		RESPMSG = rESPMSG;
	}
	public String getRESPCODE() {
		return RESPCODE;
	}
	public void setRESPCODE(String rESPCODE) {
		RESPCODE = rESPCODE;
	}
	public String getTXNID() {
		return TXNID;
	}
	public void setTXNID(String tXNID) {
		TXNID = tXNID;
	}
	public String getORDERID() {
		return ORDERID;
	}
	public void setORDERID(String oRDERID) {
		ORDERID = oRDERID;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getBANKTXNID() {
		return BANKTXNID;
	}
	public void setBANKTXNID(String bANKTXNID) {
		BANKTXNID = bANKTXNID;
	}
	public String getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public String getBankTxnId() {
		return bankTxnId;
	}
	public void setBankTxnId(String bankTxnId) {
		this.bankTxnId = bankTxnId;
	}
	public String getAcceptRefundStatus() {
		return acceptRefundStatus;
	}
	public void setAcceptRefundStatus(String acceptRefundStatus) {
		this.acceptRefundStatus = acceptRefundStatus;
	}
	public String getRefundId() {
		return refundId;
	}
	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}
	public String getCHECKSUMHASH() {
		return CHECKSUMHASH;
	}
	public void setCHECKSUMHASH(String cHECKSUMHASH) {
		CHECKSUMHASH = cHECKSUMHASH;
	}
	
	
	
}
