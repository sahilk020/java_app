package com.pay10.pinelabs;

public class Constants {
	
	
	public static final String EQUAL_SEPARATOR = "=";
	
	public static final String TxnTypeCapture = "9";
	public static final String TxnTypeEnquiry = "3";
	public static final String TxnTypeRefund = "10";
	public static final String upi_option_upi = "UPI";
	public static final String upi_option_gpay = "GPAY";
	
	// Body param
	public static final String merchant_data = "merchant_data";
	public static final String payment_data = "payment_data";
	public static final String txn_data = "txn_data";
	public static final String customer_data = "customer_data";
	public static final String udf_data = "udf_data";
	//merchant_data	
	public static final String merchantId = "merchant_id";
	public static final String mAccessCode = "merchant_access_code";
	public static final String orderId = "unique_merchant_txn_id"; //This is your (merchant) reference number
	public static final String returnUrl = "merchant_return_url";
	public static final String TxnAmount = "amount_in_paisa";
	//txn_data
	public static final String navigation_mode = "navigation_mode"; ///Navigation mode 	2 for Redirect	7 for Seamless
	public static final String navigation_mode_val = "7"; // for seamless
	public static final String paymentMode = "payment_mode";
	public static final String transactionType = "transaction_type";
	public static final String transactionType_val = "1"; //1 for Purchase,8 for PreAuth 
	public static final String time_stamp = "time_stamp";
	//customer_data
	public static final String customerFName = "first_name";
	public static final String customerLName = "last_name";
	public static final String customerEmail = "email_id";
	public static final String customer_id = "customer_id";
	public static final String customerPhone = "mobile_no";
	public static final String billing_data = "billing_data";
	public static final String shipping_data = "shipping_data";
	//udf_data
	public static final String udf_field_1 = "udf_field_1";
	public static final String udf_field_2 = "udf_field_2";
	public static final String udf_field_3 = "udf_field_3";
	public static final String udf_field_4 = "udf_field_4";
	public static final String udf_field_5 = "udf_field_5";
	//billing_data
	public static final String baddress1 = "address1";
	public static final String baddress2 = "address2";
	public static final String baddress3 = "address3";
	public static final String bcity = "city";
	public static final String bstate = "state";
	public static final String bcountry = "country";
	public static final String bzipCode = "pincode";
	public static final String bcustomerFName = "first_name";
	public static final String bcustomerLName = "last_name";
	public static final String bcustomerPhone = "mobile_no";
	//shipping_data
	public static final String saddress1 = "address1";
	public static final String saddress2 = "address2";
	public static final String saddress3 = "address3";
	public static final String scity = "city";
	public static final String sstate = "state";
	public static final String scountry = "country";
	public static final String szipCode = "pincode";
	public static final String scustomerFName = "first_name";
	public static final String scustomerLName = "last_name";
	public static final String scustomerPhone = "mobile_no";
	//response param
	public static final String token = "token";
	public static final String response_code = "response_code";
	public static final String responseMsg = "response_message";
	public static final String redirect_url = "redirect_url";
	public static final String api_url = "api_url";
	public static final String reason_code = "reason_code";
	public static final String reason_message = "reason_message";
	
	
	//payment body param
	public static final String emi_data = "emi_data";
	public static final String paymentOptionNB = "netbanking_data";
	public static final String paymentOptionCard = "card_data";
	public static final String paymentOptionWallet = "wallet_data";
	public static final String additional_data = "additional_data";
	public static final String paymentOptionUpi = "upi_data";
	
	//card data
	public static final String card_number = "card_number";
	public static final String card_holder = "card_holder_name";
	public static final String card_expiry_year = "card_expiry_year";
	public static final String card_cvv = "cvv";
	public static final String card_expiry_month = "card_expiry_month";
	//netbanking
	public static final String paymentCode = "pay_code";
	//wallet data
	public static final String wallet_code = "wallet_code";
	public static final String w_mobile_number = "mobile_number";
	//UPI data
	public static final String upi_vpa = "vpa";
	public static final String upi_mobile_no = "mobile_no";
	public static final String upi_upi_option = "upi_option";
	
	
	
	//response 
	public static final String orderCurrency = "ppc_CurrencyCode"; // Integer--Use-356 for INR
	public static final String description = "ppc_ImeiProductDetails";
	public static final String responseAmt = "captured_amount_in_paisa";
	public static final String pinePGTxn_id = "pine_pg_transaction_id";
	public static final String responseRefundAmt = "refund_amount_in_paisa";
	public static final String parentTxnStatus = "parent_txn_status";
	public static final String pineTxnStatus = "pine_pg_txn_status";
	public static final String txnCompeletionDT = "pine_pg_txn_status";
	public static final String acquirerName = "pine_pg_txn_status";
	public static final String txnResponseMsg = "txn_response_msg";
	public static final String parentTxnMsg = "parent_txn_response_message";
	public static final String parentTxnCode = "parent_txn_response_code";
		
	
	///refund and Enquiry
	public static final String pccMerchantId = "ppc_MerchantID";
	public static final String pccAmt = "ppc_Amount";
	public static final String pccDiaSecretType = "ppc_DIA_SECRET_TYPE";
	public static final String pccDiaSecret = "ppc_DIA_SECRET";
	public static final String pccUniqueMid = "ppc_UniqueMerchantTxnID";
	public static final String pccMerchantAccessCode = "ppc_MerchantAccessCode";
	public static final String transactionId = "ppc_PinePGTransactionID";
	public static final String pccTxnType = "ppc_TransactionType";
	public static final String pccCurrencyCode = "ppc_CurrencyCode";
	public static final String pccImeiProductDetails = "ppc_ImeiProductDetails ";
	public static final String diaSecretType = "SHA256";
	
	
	
	//response for refund/enquiry
	public static final String pccPinePGTxnStatus = "ppc_PinePGTxnStatus";
	public static final String pccTransactionCompletionDateTime = "ppc_TransactionCompletionDateTime";
	public static final String pccTxnResponseCode = "ppc_TxnResponseCode";
	public static final String pccTxnResponseMessage = "ppc_TxnResponseMessage";
	public static final String pccAcquirerName  = "ppc_AcquirerName";
	public static final String pccPinePGTransactionID = "ppc_PinePGTransactionID";
	public static final String pccMerchantReturnURL = "ppc_MerchantReturnURL";
	public static final String pccEMITenureMonth = "ppc_EMITenureMonth";
	public static final String pccEMIInterestRatePercent = "ppc_EMIInterestRatePercent";
	public static final String pccEMIProcessingFee = "ppc_EMIProcessingFee";
	public static final String pccEMIPrincipalAmount = "ppc_EMIPrincipalAmount";
	public static final String pccEMIAmountPayableEachMonth = "ppc_EMIAmountPayableEachMonth";
	public static final String pccProductCode = "ppc_ProductCode";
	public static final String pccProductDisplayName = "ppc_ProductDisplayName";
	public static final String pcc_Is_BankEMITransaction = "ppc_Is_BankEMITransaction";
	public static final String pcc_Is_BrandEMITransaction = "ppc_Is_BrandEMITransaction";
	public static final String pcc_CapturedAmount = "ppc_CapturedAmount";
	public static final String pcc_RefundedAmount = "ppc_RefundedAmount";
	public static final String pcc_EMICashBackType = "ppc_EMICashBackType";
	public static final String pcc_EMIIssuerDiscCashBackPercent = "ppc_EMIIssuerDiscCashBackPercent";
	public static final String pcc_EMIIssuerDiscCashBackFixedAmt = "ppc_EMIIssuerDiscCashBackFixedAmt";		
	public static final String pcc_EMIMerchantDiscCashBackPercent = "ppc_EMIMerchantDiscCashBackPercent";
	public static final String pcc_EMIMerchantCashBackFixedAmt = "ppc_EMIMerchantCashBackFixedAmt"	;	
	public static final String pcc_EMITotalDiscCashBackPercent = "ppc_EMITotalDiscCashBackPercent"	;	
	public static final String pcc_EMITotalDiscCashBackPercentFixedAmt = "ppc_EMITotalDiscCashBackPercentFixedAmt";
	public static final String pcc_EMITotalDiscCashBackAmt = "ppc_EMITotalDiscCashBackAmt";
	public static final String pcc_EMIAdditionalCashBack = "ppc_EMIAdditionalCashBack";
	public static final String pcc_EMIAdditionalRewardPoints = "ppc_EMIAdditionalRewardPoints";
	public static final String pcc_PaymentMode = "ppc_PaymentMode";
	public static final String pcc_ADDOnInstantDiscAmt = "ppc_ADDOnInstantDiscAmt";
	public static final String pcc_OriginalTxnAmt = "ppc_OriginalTxnAmt";
	public static final String pcc_ADDOnInstantDiscPercentage = "ppc_ADDOnInstantDiscPercentage";
	public static final String pcc_ADDOnRewardPointMultiplier = "ppc_ADDOnRewardPointMultiplier";
	public static final String pcc_ADDOnRewardPointsAwarded = "ppc_ADDOnRewardPointsAwarded";
	public static final String pcc_Parent_TxnStatus = "ppc_Parent_TxnStatus";
	public static final String pcc_ParentTxnResponseCode = "ppc_ParentTxnResponseCode";
	public static final String pcc_ParentTxnResponseMessage = "ppc_ParentTxnResponseMessage";
	public static final String pcc_ProgramType = "ppc_ProgramType";
	public static final String pcc_MaskedCardNumber = "ppc_MaskedCardNumber";
	public static final String pcc_RwdCustomerMobileNumber = "ppc_RwdCustomerMobileNumber";
	public static final String pcc_ISEZEClick = "ppc_ISEZEClick";
	
	
	
	
	
	public static final String timeoutDuration = "timeout_duration";
	
	public static final String returnUrlCancel = "return_url_cancel";
	public static final String percentTdrbyUser = "percent_tdr_by_user";
	public static final String flatfeeTdrbyUser = "flatfee_tdr_by_user";
	public static final String showConvenienceFfee = "show_convenience_fee";
	
	public static final String signature = "hash";
	
	public static final String paymentOption = "bank_code";
	
	
	
	
	
	
	
	public static final String REFUND_REQUEST_URL = "PINELABSRefundUrl";
	public static final String STATUS_ENQ_REQUEST_URL = "PINELABSStatusEnqUrl";
	public static final String SALE_RETURN_URL = "PINELABSReturnUrl";
	public static final String TOKEN_RETURN_URL = "PINELABSTokenUrl";
	
	// response para
	
	
	
	
	
	
	
	
	

}
