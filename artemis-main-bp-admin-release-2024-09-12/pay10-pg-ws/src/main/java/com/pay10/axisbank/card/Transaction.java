package com.pay10.axisbank.card;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;

@Service("axisBankCardTransaction")
public class Transaction {

	// Merchant Details

	private String merchantId;
	private String collaboratorId;
	private String encryptionKey;
	
	// Transaction Details Data

	private String merchantOrderNumber;
	private String amount;
	private String successURL;
	private String failureURL;
	private String transactionMode;
	private String payMode;
	private String transactionType;
	private String currency;
	private String fee;
	private String settlementAmount;
	private String settlementDate;
	// Billing Details Data

	private String billToFirstName;
	private String billToLastName;
	private String billToStreet1;
	private String billToStreet2;
	private String billToCity;
	private String billToState;
	private String billToPostalCode;
	private String billToCountry;
	private String billToEmailId;
	private String billToMobileNumber;
	private String billToPhoneNumber1;
	private String billToPhoneNumber2;
	private String billToPhoneNumber3;

	// Shipping Details Data

	private String shipToFirstName;
	private String shipToLastName;
	private String shipToStreet1;
	private String shipToStreet2;
	private String shipToCity;
	private String shipToState;
	private String shipToPostalCode;
	private String shipToCountry;
	private String shipToEmailId;
	private String shipToMobileNumber;
	private String shipToPhoneNumber1;
	private String shipToPhoneNumber2;
	private String shipToPhoneNumber3;

	// Payment Details Data

	private String cardNumber;
	private String expMonth;
	private String expYear;
	private String cvv;
	private String cardHolderName;
	private String cardType;
	private String mopType;
	private String customerMobileNumber;
	private String paymentId;
	private String otp;
	private String gatewayId;
	private String cardTokenNumber;
	private String vpa;

	// TODO : Not Required For Now
	// Merchant Details Data
	// Other Details Data
	// DCC Details Data
	
	//Packet Bit Map
	private String response_PBM;
	
	private String response_TxnData_FBM;
	private String response_Mer_Ord_Num;
	private String response_Curr;
	private String response_Amt;
	private String response_PayMode;
	private String response_TxnType;
	
	private String response_TxnResponse_FBM;
	private String response_Ref_Num;
	private String response_Txn_Date;
	private String response_GtwTraceNum;
	private String response_GtwIdentifier;
	
	
	private String response_TxnStatus_FBM;
	private String response_Sts_Comment;
	private String response_Sts_Flag;
	private String response_Err_Code;
	private String response_Err_Msg;
	
	//Status Enq params
	
	//private String status_referenceId;
	private String country;
	private String status_statusOfTxn;
	private String status_refundReqId;
	private String status_refundAmt;
	private String status_refundAmtAvl;
	
	public void setEnrollment(Fields fields) {
		setMerchantInformation(fields);
		setTxnDataDetails(fields);
		setBillingDataDetails(fields);
		setShippingDataDetails(fields);
		setPaymentDataDetails(fields);
	}

	private void setMerchantInformation(Fields fields) {
		setMerchantId(fields.get(FieldType.MERCHANT_ID.getName()));
		setCollaboratorId(fields.get(FieldType.ADF1.getName()));
		setEncryptionKey(fields.get(FieldType.TXN_KEY.getName()));
	}

	private void setTxnDataDetails(Fields fields) {

		setMerchantOrderNumber(fields.get(FieldType.PG_REF_NUM.getName()));
		setAmount(fields.get(FieldType.TOTAL_AMOUNT.getName()));
		setSuccessURL(PropertiesManager.propertiesMap.get(Constants.DIRECPAY_RESPONSE_URL));
		setFailureURL(PropertiesManager.propertiesMap.get(Constants.DIRECPAY_RESPONSE_URL));
		setTransactionMode(Constants.DIRECPAY_TXN_MODE);
		setPayMode(fields.get(FieldType.PAYMENT_TYPE.getName()));
		setTransactionType(Constants.DIRECPAY_TXN_TYPE_SALE);
		setCurrency(Constants.DIRECPAY_CURRENCY_CODE);
	}

	private void setBillingDataDetails(Fields fields) {

		setBillToFirstName("Pay10");
		setBillToLastName("Pay10");
		setBillToStreet1("Floor3");
		setBillToStreet2("Sector54");
		setBillToCity("Gurgaon");
		setBillToState("Haryana");
		setBillToPostalCode("122002");
		setBillToCountry("IN");
		setBillToEmailId("support@pay10.com");
		setBillToMobileNumber("99199999199");
		setBillToPhoneNumber1("22332222");
		setBillToPhoneNumber2("22332223");
		setBillToPhoneNumber3("22332224");
	}

	private void setShippingDataDetails(Fields fields) {

		setShipToFirstName("Pay10");
		setShipToLastName("Pay10");
		setShipToStreet1("Floor3");
		setShipToStreet2("Sector54");
		setShipToCity("Gurgaon");
		setShipToState("Haryana");
		setShipToPostalCode("122002");
		setShipToCountry("IN");
		setShipToMobileNumber("99199999199");
		setShipToPhoneNumber1("22332222");
		setShipToPhoneNumber2("22332223");
		setShipToPhoneNumber3("22332224");

	}

	private void setPaymentDataDetails(Fields fields) {

		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode()) ||
				fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {
			
			setCardNumber(fields.get(FieldType.CARD_NUMBER.getName()));
			setExpMonth(fields.get(FieldType.CARD_EXP_DT.getName()).substring(0, 2));
			setExpYear("20"+fields.get(FieldType.CARD_EXP_DT.getName()).substring(2, 4));
			setCvv(fields.get(FieldType.CVV.getName()));
			setCardHolderName(fields.get(FieldType.CARD_HOLDER_NAME.getName()));
			setGatewayId(fields.get(FieldType.MOP_TYPE.getName()));

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {
			setGatewayId(fields.get(FieldType.MOP_TYPE.getName()));
		}

	}

	private void setMerchantDataDetails(Fields fields) {
	}

	private void setOtherDataDetails(Fields fields) {
	}

	private void setDCCDataDetails(Fields fields) {
	}

	public void setStatusEnquiry(Fields fields) {
	}

	public void setRefund(Fields fields) {
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getCollaboratorId() {
		return collaboratorId;
	}

	public void setCollaboratorId(String collaboratorId) {
		this.collaboratorId = collaboratorId;
	}

	public String getMerchantOrderNumber() {
		return merchantOrderNumber;
	}

	public void setMerchantOrderNumber(String merchantOrderNumber) {
		this.merchantOrderNumber = merchantOrderNumber;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getSuccessURL() {
		return successURL;
	}

	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}

	public String getFailureURL() {
		return failureURL;
	}

	public void setFailureURL(String failureURL) {
		this.failureURL = failureURL;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBillToFirstName() {
		return billToFirstName;
	}

	public void setBillToFirstName(String billToFirstName) {
		this.billToFirstName = billToFirstName;
	}

	public String getBillToLastName() {
		return billToLastName;
	}

	public void setBillToLastName(String billToLastName) {
		this.billToLastName = billToLastName;
	}

	public String getBillToStreet1() {
		return billToStreet1;
	}

	public void setBillToStreet1(String billToStreet1) {
		this.billToStreet1 = billToStreet1;
	}

	public String getBillToStreet2() {
		return billToStreet2;
	}

	public void setBillToStreet2(String billToStreet2) {
		this.billToStreet2 = billToStreet2;
	}

	public String getBillToCity() {
		return billToCity;
	}

	public void setBillToCity(String billToCity) {
		this.billToCity = billToCity;
	}

	public String getBillToState() {
		return billToState;
	}

	public void setBillToState(String billToState) {
		this.billToState = billToState;
	}

	public String getBillToPostalCode() {
		return billToPostalCode;
	}

	public void setBillToPostalCode(String billToPostalCode) {
		this.billToPostalCode = billToPostalCode;
	}

	public String getBillToCountry() {
		return billToCountry;
	}

	public void setBillToCountry(String billToCountry) {
		this.billToCountry = billToCountry;
	}

	public String getBillToEmailId() {
		return billToEmailId;
	}

	public void setBillToEmailId(String billToEmailId) {
		this.billToEmailId = billToEmailId;
	}

	public String getBillToMobileNumber() {
		return billToMobileNumber;
	}

	public void setBillToMobileNumber(String billToMobileNumber) {
		this.billToMobileNumber = billToMobileNumber;
	}

	public String getBillToPhoneNumber1() {
		return billToPhoneNumber1;
	}

	public void setBillToPhoneNumber1(String billToPhoneNumber1) {
		this.billToPhoneNumber1 = billToPhoneNumber1;
	}

	public String getBillToPhoneNumber2() {
		return billToPhoneNumber2;
	}

	public void setBillToPhoneNumber2(String billToPhoneNumber2) {
		this.billToPhoneNumber2 = billToPhoneNumber2;
	}

	public String getBillToPhoneNumber3() {
		return billToPhoneNumber3;
	}

	public void setBillToPhoneNumber3(String billToPhoneNumber3) {
		this.billToPhoneNumber3 = billToPhoneNumber3;
	}

	public String getShipToFirstName() {
		return shipToFirstName;
	}

	public void setShipToFirstName(String shipToFirstName) {
		this.shipToFirstName = shipToFirstName;
	}

	public String getShipToLastName() {
		return shipToLastName;
	}

	public void setShipToLastName(String shipToLastName) {
		this.shipToLastName = shipToLastName;
	}

	public String getShipToStreet1() {
		return shipToStreet1;
	}

	public void setShipToStreet1(String shipToStreet1) {
		this.shipToStreet1 = shipToStreet1;
	}

	public String getShipToStreet2() {
		return shipToStreet2;
	}

	public void setShipToStreet2(String shipToStreet2) {
		this.shipToStreet2 = shipToStreet2;
	}

	public String getShipToCity() {
		return shipToCity;
	}

	public void setShipToCity(String shipToCity) {
		this.shipToCity = shipToCity;
	}

	public String getShipToState() {
		return shipToState;
	}

	public void setShipToState(String shipToState) {
		this.shipToState = shipToState;
	}

	public String getShipToPostalCode() {
		return shipToPostalCode;
	}

	public void setShipToPostalCode(String shipToPostalCode) {
		this.shipToPostalCode = shipToPostalCode;
	}

	public String getShipToCountry() {
		return shipToCountry;
	}

	public void setShipToCountry(String shipToCountry) {
		this.shipToCountry = shipToCountry;
	}

	public String getShipToEmailId() {
		return shipToEmailId;
	}

	public void setShipToEmailId(String shipToEmailId) {
		this.shipToEmailId = shipToEmailId;
	}

	public String getShipToMobileNumber() {
		return shipToMobileNumber;
	}

	public void setShipToMobileNumber(String shipToMobileNumber) {
		this.shipToMobileNumber = shipToMobileNumber;
	}

	public String getShipToPhoneNumber1() {
		return shipToPhoneNumber1;
	}

	public void setShipToPhoneNumber1(String shipToPhoneNumber1) {
		this.shipToPhoneNumber1 = shipToPhoneNumber1;
	}

	public String getShipToPhoneNumber2() {
		return shipToPhoneNumber2;
	}

	public void setShipToPhoneNumber2(String shipToPhoneNumber2) {
		this.shipToPhoneNumber2 = shipToPhoneNumber2;
	}

	public String getShipToPhoneNumber3() {
		return shipToPhoneNumber3;
	}

	public void setShipToPhoneNumber3(String shipToPhoneNumber3) {
		this.shipToPhoneNumber3 = shipToPhoneNumber3;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getExpMonth() {
		return expMonth;
	}

	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}

	public String getExpYear() {
		return expYear;
	}

	public void setExpYear(String expYear) {
		this.expYear = expYear;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCustomerMobileNumber() {
		return customerMobileNumber;
	}

	public void setCustomerMobileNumber(String customerMobileNumber) {
		this.customerMobileNumber = customerMobileNumber;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getCardTokenNumber() {
		return cardTokenNumber;
	}

	public void setCardTokenNumber(String cardTokenNumber) {
		this.cardTokenNumber = cardTokenNumber;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String getResponse_PBM() {
		return response_PBM;
	}

	public void setResponse_PBM(String response_PBM) {
		this.response_PBM = response_PBM;
	}

	public String getResponse_TxnData_FBM() {
		return response_TxnData_FBM;
	}

	public void setResponse_TxnData_FBM(String response_TxnData_FBM) {
		this.response_TxnData_FBM = response_TxnData_FBM;
	}

	public String getResponse_Mer_Ord_Num() {
		return response_Mer_Ord_Num;
	}

	public void setResponse_Mer_Ord_Num(String response_Mer_Ord_Num) {
		this.response_Mer_Ord_Num = response_Mer_Ord_Num;
	}

	public String getResponse_Curr() {
		return response_Curr;
	}

	public void setResponse_Curr(String response_Curr) {
		this.response_Curr = response_Curr;
	}

	public String getResponse_Amt() {
		return response_Amt;
	}

	public void setResponse_Amt(String response_Amt) {
		this.response_Amt = response_Amt;
	}

	public String getResponse_PayMode() {
		return response_PayMode;
	}

	public void setResponse_PayMode(String response_PayMode) {
		this.response_PayMode = response_PayMode;
	}

	public String getResponse_TxnType() {
		return response_TxnType;
	}

	public void setResponse_TxnType(String response_TxnType) {
		this.response_TxnType = response_TxnType;
	}

	public String getResponse_TxnResponse_FBM() {
		return response_TxnResponse_FBM;
	}

	public void setResponse_TxnResponse_FBM(String response_TxnResponse_FBM) {
		this.response_TxnResponse_FBM = response_TxnResponse_FBM;
	}

	public String getResponse_Ref_Num() {
		return response_Ref_Num;
	}

	public void setResponse_Ref_Num(String response_Ref_Num) {
		this.response_Ref_Num = response_Ref_Num;
	}

	public String getResponse_Txn_Date() {
		return response_Txn_Date;
	}

	public void setResponse_Txn_Date(String response_Txn_Date) {
		this.response_Txn_Date = response_Txn_Date;
	}

	public String getResponse_GtwIdentifier() {
		return response_GtwIdentifier;
	}

	public void setResponse_GtwIdentifier(String response_GtwIdentifier) {
		this.response_GtwIdentifier = response_GtwIdentifier;
	}

	public String getResponse_TxnStatus_FBM() {
		return response_TxnStatus_FBM;
	}

	public void setResponse_TxnStatus_FBM(String response_TxnStatus_FBM) {
		this.response_TxnStatus_FBM = response_TxnStatus_FBM;
	}

	public String getResponse_Sts_Flag() {
		return response_Sts_Flag;
	}

	public void setResponse_Sts_Flag(String response_Sts_Flag) {
		this.response_Sts_Flag = response_Sts_Flag;
	}

	public String getResponse_Err_Code() {
		return response_Err_Code;
	}

	public void setResponse_Err_Code(String response_Err_Code) {
		this.response_Err_Code = response_Err_Code;
	}

	public String getResponse_Err_Msg() {
		return response_Err_Msg;
	}

	public void setResponse_Err_Msg(String response_Err_Msg) {
		this.response_Err_Msg = response_Err_Msg;
	}

	public String getResponse_GtwTraceNum() {
		return response_GtwTraceNum;
	}

	public void setResponse_GtwTraceNum(String response_GtwTraceNum) {
		this.response_GtwTraceNum = response_GtwTraceNum;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStatus_statusOfTxn() {
		return status_statusOfTxn;
	}

	public void setStatus_statusOfTxn(String status_statusOfTxn) {
		this.status_statusOfTxn = status_statusOfTxn;
	}


	public String getStatus_refundReqId() {
		return status_refundReqId;
	}

	public void setStatus_refundReqId(String status_refundReqId) {
		this.status_refundReqId = status_refundReqId;
	}

	public String getStatus_refundAmt() {
		return status_refundAmt;
	}

	public void setStatus_refundAmt(String status_refundAmt) {
		this.status_refundAmt = status_refundAmt;
	}

	public String getStatus_refundAmtAvl() {
		return status_refundAmtAvl;
	}

	public void setStatus_refundAmtAvl(String status_refundAmtAvl) {
		this.status_refundAmtAvl = status_refundAmtAvl;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(String settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getResponse_Sts_Comment() {
		return response_Sts_Comment;
	}

	public void setResponse_Sts_Comment(String response_Sts_Comment) {
		this.response_Sts_Comment = response_Sts_Comment;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	
}
