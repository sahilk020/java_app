package com.pay10.agreepay;

public class Constants {
	
	public static final String appId = "api_key";
	public static final String orderId = "order_id"; //This is your (merchant) reference number
	public static final String mode = "mode"; // TEST/LIVE 
	public static final String TxnAmount = "amount";
	public static final String orderCurrency = "currency";
	public static final String description = "description";
	public static final String customerName = "name";
	public static final String customerEmail = "email";
	public static final String customerPhone = "phone";
	
	public static final String addressline1 = "address_line_1";
	public static final String addressline2 = "address_line_2";
	public static final String city = "city";
	public static final String state = "state";
	public static final String country = "country";
	public static final String zipCode = "zip_code";
	public static final String timeoutDuration = "timeout_duration";
	public static final String udf1 = "udf1";
	public static final String udf2 = "udf2";
	public static final String udf3 = "udf3";
	public static final String udf4 = "udf4";
	public static final String udf5 = "udf5";
	public static final String returnUrl = "return_url";
	public static final String returnUrlCancel = "return_url_cancel";
	public static final String percentTdrbyUser = "percent_tdr_by_user";
	public static final String flatfeeTdrbyUser = "flatfee_tdr_by_user";
	public static final String showConvenienceFfee = "show_convenience_fee";
	
	public static final String splitEnforceStrict = "split_enforce_strict";
	public static final String splitInfo = "split_info";
	public static final String paymentOptions = "payment_options";
	public static final String paymentPageDisplayText = "payment_page_display_text";
	public static final String allowedBankCodes = "allowed_bank_codes";
	public static final String allowedEmiTenure = "allowed_emi_tenure";
	public static final String allowedBins = "allowed_bins";
	public static final String offerCode = "offer_code";
	public static final String productDetails = "product_details";
	public static final String enableAutoRefund = "emi_info";
	public static final String emiInfo = "emi_info";
	public static final String signature = "hash";
	
	public static final String paymentOption = "bank_code";
	public static final String card_number = "card_number";
	public static final String card_holder = "card_holder_name";
	public static final String card_expiryDate = "expiry_date";
	public static final String card_cvv = "cvv";
	public static final String paymentCode = "paymentCode";
	public static final String upi_vpa = "payer_virtual_address";
	
	
	
	
	
	public static final String SALE_RETURN_URL = "AGREEPAYReturnUrl";
	
	// response para
	public static final String transactionId = "transaction_id";
	public static final String paymentMode = "payment_mode";
	public static final String PaymentChannel = "Payment_channel";
	public static final String paymentDatetime = "payment_datetime";
	public static final String txStatus = "response_code";
	public static final String errorDesc = "error_desc";
	public static final String txMsg = "response_message";
	public static final String cardmasked = "cardmasked";
	
		
	public static final String orderAmount = "orderAmount";
	
	
	
	public static final String REFUND_REQUEST_URL = "AGREEPAYRefundUrl";
	public static final String STATUS_ENQ_REQUEST_URL = "AGREEPAYStatusEnqUrl";
	
	public static final String paymentOptionUPI = "UPIU";
	public static final String paymentOptionNB = "nb";
	public static final String paymentOptionCard = "card";
	public static final String paymentOptionWallet = "wallet";
	
	public static final String message = "message";
	public static final String refundId = "refund_id";
	public static final String refundRefNo = "refund_reference_no";
	public static final String merchantRefundId = "merchant_refund_id";
	public static final String merchantOrderId= "merchant_order_id";
	public static final String status = "code";
	public static final String orderStatus = "orderStatus";


}
