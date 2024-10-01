package com.pay10.billdesk;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service("billdeskTransaction")
public class Transaction {
	
	private String action;
	private String MerchantID;
	private String CustomerID;
	private String TxnReferenceNo;
	private String BankReferenceNo;
	private String TxnAmount;
	private String BankID;
	private String BankMerchantID;
	private String TxnType;
	private String CurrencyName;
	private String ItemCode;
	private String SecurityType;
	private String SecurityID;
	private String SecurityPassword;
	private String TxnDate;
	private String AuthStatus;
	private String SettlementType;
	private String AdditionalInfo1;
	private String AdditionalInfo2;
	private String AdditionalInfo3;
	private String AdditionalInfo4;
	private String AdditionalInfo5;
	private String AdditionalInfo6;
	private String AdditionalInfo7;
	private String ErrorStatus;
	private String ErrorDescription;
	private String CheckSum;
	private String QueryStatus; 
	private String RefStatus;
	private String RefundId;
	private String ProcessStatus;
	private String ErrorCode;
	
	@SuppressWarnings("incomplete-switch")
	private void setAction(Fields fields) {
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.ENROLL.getName())) {
			txnType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
		}

		String action = null;
		switch (TransactionType.getInstance(txnType)) {
		case REFUND:
			action = "2";
			break;
		}

		setAction(action);
	}
	
	public void setEnrollment(Fields fields) {
		//setCardDetails(fields);
		//setTxnType(fields);
	}
	
	public void setRefund(Fields fields) {
		setAction(fields);
	}
	
	/*
	 * private void setCardDetails(Fields fields) {
	 * setCardNumber(fields.get(FieldType.CARD_NUMBER.getName()));
	 * setCvv(fields.get(FieldType.CVV.getName())); String expDate =
	 * fields.get(FieldType.CARD_EXP_DT.getName()); String expYear =
	 * (expDate.substring(4, 6)); String expMonth = (expDate.substring(0, 2));
	 * setExpiryDate(expMonth.concat(expYear)); }
	 */
	
	/*
	 * private void setTxnType(Fields fields) { String txnType =
	 * fields.get(FieldType.TXNTYPE.getName());
	 * if(txnType.equals(TransactionType.SALE.toString())){ setTxnType("01"); }else
	 * if(txnType.equals(TransactionType.REFUND.toString())){ setTxnType("04");
	 * }else if(txnType.equals(TransactionType.ENQUIRY.toString())){
	 * setTxnType("05"); }else{
	 * 
	 * } }
	 */

	public String getMerchantID() {
		return MerchantID;
	}

	public void setMerchantID(String merchantID) {
		MerchantID = merchantID;
	}

	public String getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(String customerID) {
		CustomerID = customerID;
	}

	public String getTxnReferenceNo() {
		return TxnReferenceNo;
	}

	public void setTxnReferenceNo(String txnReferenceNo) {
		TxnReferenceNo = txnReferenceNo;
	}

	public String getBankReferenceNo() {
		return BankReferenceNo;
	}

	public void setBankReferenceNo(String bankReferenceNo) {
		BankReferenceNo = bankReferenceNo;
	}

	public String getTxnAmount() {
		return TxnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		TxnAmount = txnAmount;
	}

	public String getBankID() {
		return BankID;
	}

	public void setBankID(String bankID) {
		BankID = bankID;
	}

	public String getBankMerchantID() {
		return BankMerchantID;
	}

	public void setBankMerchantID(String bankMerchantID) {
		BankMerchantID = bankMerchantID;
	}

	public String getTxnType() {
		return TxnType;
	}

	public void setTxnType(String txnType) {
		TxnType = txnType;
	}

	public String getCurrencyName() {
		return CurrencyName;
	}

	public void setCurrencyName(String currencyName) {
		CurrencyName = currencyName;
	}

	public String getItemCode() {
		return ItemCode;
	}

	public void setItemCode(String itemCode) {
		ItemCode = itemCode;
	}

	public String getSecurityType() {
		return SecurityType;
	}

	public void setSecurityType(String securityType) {
		SecurityType = securityType;
	}

	public String getSecurityID() {
		return SecurityID;
	}

	public void setSecurityID(String securityID) {
		SecurityID = securityID;
	}

	public String getSecurityPassword() {
		return SecurityPassword;
	}

	public void setSecurityPassword(String securityPassword) {
		SecurityPassword = securityPassword;
	}

	public String getTxnDate() {
		return TxnDate;
	}

	public void setTxnDate(String txnDate) {
		TxnDate = txnDate;
	}

	public String getAuthStatus() {
		return AuthStatus;
	}

	public void setAuthStatus(String authStatus) {
		AuthStatus = authStatus;
	}

	public String getSettlementType() {
		return SettlementType;
	}

	public void setSettlementType(String settlementType) {
		SettlementType = settlementType;
	}

	public String getAdditionalInfo1() {
		return AdditionalInfo1;
	}

	public void setAdditionalInfo1(String additionalInfo1) {
		AdditionalInfo1 = additionalInfo1;
	}

	public String getAdditionalInfo2() {
		return AdditionalInfo2;
	}

	public void setAdditionalInfo2(String additionalInfo2) {
		AdditionalInfo2 = additionalInfo2;
	}

	public String getAdditionalInfo3() {
		return AdditionalInfo3;
	}

	public void setAdditionalInfo3(String additionalInfo3) {
		AdditionalInfo3 = additionalInfo3;
	}

	public String getAdditionalInfo4() {
		return AdditionalInfo4;
	}

	public void setAdditionalInfo4(String additionalInfo4) {
		AdditionalInfo4 = additionalInfo4;
	}

	public String getAdditionalInfo5() {
		return AdditionalInfo5;
	}

	public void setAdditionalInfo5(String additionalInfo5) {
		AdditionalInfo5 = additionalInfo5;
	}

	public String getAdditionalInfo6() {
		return AdditionalInfo6;
	}

	public void setAdditionalInfo6(String additionalInfo6) {
		AdditionalInfo6 = additionalInfo6;
	}

	public String getAdditionalInfo7() {
		return AdditionalInfo7;
	}

	public void setAdditionalInfo7(String additionalInfo7) {
		AdditionalInfo7 = additionalInfo7;
	}

	public String getErrorStatus() {
		return ErrorStatus;
	}

	public void setErrorStatus(String errorStatus) {
		ErrorStatus = errorStatus;
	}

	public String getErrorDescription() {
		return ErrorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		ErrorDescription = errorDescription;
	}

	public String getCheckSum() {
		return CheckSum;
	}

	public void setCheckSum(String checkSum) {
		CheckSum = checkSum;
	}

	public String getQueryStatus() {
		return QueryStatus;
	}

	public void setQueryStatus(String queryStatus) {
		QueryStatus = queryStatus;
	}

	public String getRefStatus() {
		return RefStatus;
	}

	public void setRefStatus(String refStatus) {
		RefStatus = refStatus;
	}

	public String getRefundId() {
		return RefundId;
	}

	public void setRefundId(String refundId) {
		RefundId = refundId;
	}

	public String getProcessStatus() {
		return ProcessStatus;
	}

	public void setProcessStatus(String processStatus) {
		ProcessStatus = processStatus;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}
	

}
