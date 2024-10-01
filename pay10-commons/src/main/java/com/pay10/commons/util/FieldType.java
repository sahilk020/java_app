package com.pay10.commons.util;

import java.util.HashMap;
import java.util.Map;

/*
 * This type serves as the central validation information for different fields which can be submitted in a request
 */
public enum FieldType {

	// Internal use fields

	ACQUIRER_TYPE("ACQUIRER_TYPE", 3, 50, false, FieldFormatType.ALPHANUM, false),
	INTERNAL_ACQUIRER_TYPE("INTERNAL_ACQUIRER_TYPE", 3, 10, false, FieldFormatType.ALPHANUM, false),
	INTERNAL_VALIDATE_HASH_YN("INTERNAL_VALIDATE_HASH_YN", 1, 1, false, FieldFormatType.NONE, false),
	INTERNAL_ORIG_TXN_TYPE("INTERNAL_ORIG_TXN_TYPE", 3, 20, false, FieldFormatType.NONE, false),
	INTERNAL_CUST_IP("INTERNAL_CUST_IP", 7, 15, false, FieldFormatType.NONE, true),
	INTERNAL_CUST_MAC("INTERNAL_CUST_MAC", 7, 20, false, FieldFormatType.NONE, true),
	INTERNAL_CUST_DOMAIN("INTERNAL_CUST_DOMAIN", 7, 30, false, FieldFormatType.NONE, true),
	INTERNAL_CUST_COUNTRY_NAME("INTERNAL_CUST_COUNTRY_NAME", 2, 50, false, FieldFormatType.ALPHANUM, true),
	INTERNAL_CUSTOM_MDC("INTERNAL_CUSTOM_MDC", 1, 256, false, FieldFormatType.NONE, false),
	INTERNAL_REQUEST_FIELDS("INTERNAL_REQUEST_FIELDS", 1, 6000, false, FieldFormatType.NONE, true),
	INTERNAL_INVALID_HASH_YN("INTERNAL_INVALID_HASH_YN", 1, 1, false, FieldFormatType.NONE, false),
	INTERNAL_ORIG_TXN_ID("INTERNAL_ORIG_TXN_ID", 16, 16, false, FieldFormatType.SPECIAL, false),
	INTERNAL_SHOPIFY_YN("INTERNAL_SHOPIFY_YN", 1, 1, false, FieldFormatType.NONE, false),
	INTERNAL_IRCTC_YN("INTERNAL_IRCTC_YN", 1, 1, false, FieldFormatType.NONE, false),
	INTERNAL_PAYMENT_GATEWAY_YN("INTERNAL_PAYMENT_GATEWAY_YN", 1, 1, false, FieldFormatType.NONE, false),
	INTERNAL_USE_WHITE_LABEL_URL("INTERNAL_USE_WHITE_LABEL_URL", 1, 1, false, FieldFormatType.NONE, false),
	OID							("OID", 16, 16, false, FieldFormatType.NUMBER, false),
	VPC_LOCALE	                ("VPC_LOCALE", 2, 5, false, FieldFormatType.ALPHANUM, true),
	INTERNAL_TXN_AUTHENTICATION	("INTERNAL_TXN_AUTHENTICATION", 0, 16, false, FieldFormatType.ALPHA, false),
	CREATE_DATE                 ("CREATE_DATE", 19, 19, false, FieldFormatType.SPECIAL, false),
	UPDATE_DATE                 ("UPDATE_DATE", 19, 19, false, FieldFormatType.SPECIAL, false),
	DATE_INDEX					("DATE_INDEX", 1, 19, false, FieldFormatType.ALPHANUM, false),
	REQUEST_DATE				("REQUEST_DATE", 19, 19, false, FieldFormatType.SPECIAL, false),
	ORIG_TXNTYPE				("ORIG_TXNTYPE", 4, 50, false, FieldFormatType.ALPHA, false),
	VPA_CUST_NAME         ("VPA_CUST_NAME", 0, 300, false, FieldFormatType.SPECIAL, false),
	IS_ENROLLED					("IS_ENROLLED", 1, 50, false, FieldFormatType.ALPHA, false),
	IS_RETRY					("IS_RETRY", 1, 1, false, FieldFormatType.ALPHA, false),	
	PAYMENTEDGE_FINAL_REQUEST("PAYMENTEDGE_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	PAYMENTAGE_FINAL_RESPONSE("PAYMENTAGE_FINAL_RESPONSE", 1, 3000, false, FieldFormatType.SPECIAL, true),

	//Response fields
	DUPLICATE_YN				("DUPLICATE_YN", 1, 1, false, FieldFormatType.ALPHA, false),
	RESPONSE_CODE				("RESPONSE_CODE", 1, 10, false, FieldFormatType.ALPHANUM, false),		
	RESPONSE_MESSAGE			("RESPONSE_MESSAGE", 1, 256,false, FieldFormatType.ALPHANUM, true),
	IDFC_UPI_TXNID			    ("IDFC_UPI_TXNID", 1, 256,false, FieldFormatType.SPECIAL, false),
	ORIG_TXN_ID					("ORIG_TXN_ID", 16, 16, false, FieldFormatType.SPECIAL, false),
	STATUS						("STATUS", 5, 30, false, FieldFormatType.SPECIAL, false),
	ACS_URL						("ACS_URL", 5, 256, false, FieldFormatType.SPECIAL, false),
	ACS_RETURN_URL				("ACS_RETURN_URL", 5, 256, false, FieldFormatType.SPECIAL, false),
	TERM_URL					("TERM_URL", 5, 256, false, FieldFormatType.SPECIAL, false),
	PAREQ						("PAREQ", 1, 1000, false, FieldFormatType.SPECIAL, false),
	PAYMENT_ID					("PAYMENT_ID", 1, 300, false, FieldFormatType.SPECIAL, false),
	ECI							("ECI", 0, 2, false, FieldFormatType.SPECIAL, false),
	AUTH_CODE					("AUTH_CODE", 0, 300, false, FieldFormatType.SPECIAL, false),
	ARN							("ARN", 0, 300, false, FieldFormatType.SPECIAL, false),
	RRN							("RRN", 0, 300, false, FieldFormatType.SPECIAL, false),
	AVR							("AVR", 0, 100, false, FieldFormatType.ALPHANUM, false),
	POST_DATE					("POST_DATE", 0, 20, false, FieldFormatType.SPECIAL, false),
	ACQ_ID						("ACQ_ID", 0, 300, false, FieldFormatType.SPECIAL, false),
	MD							("MD", 0, 10000, false, FieldFormatType.REGEX, false),
	RESPONSE_DATE				("RESPONSE_DATE", 10, 10, false, FieldFormatType.SPECIAL, false),
	RESPONSE_TIME				("RESPONSE_TIME", 6, 10, false, FieldFormatType.SPECIAL, false),
	RESPONSE_DATE_TIME			("RESPONSE_DATE_TIME", 19, 19, false, FieldFormatType.SPECIAL, false),
	IS_STATUS_FINAL				("IS_STATUS_FINAL", 1, 19, false, FieldFormatType.SPECIAL, false),
	LAST_STATUS					("LAST_STATUS", 1, 19, false, FieldFormatType.SPECIAL, false),
	TOTAL_ENQUIRY_COUNT			("TOTAL_ENQUIRY_COUNT", 1, 19, false, FieldFormatType.ALPHANUM, false),
	TOTAL_PROCESSED			    ("TOTAL_PROCESSED", 1, 19, false, FieldFormatType.ALPHANUM, false),
	TOTAL_CAPTURED			    ("TOTAL_CAPTURED", 1, 19, false, FieldFormatType.ALPHANUM, false),
	TOTAL_PENDING			    ("TOTAL_PENDING", 1, 19, false, FieldFormatType.ALPHANUM, false),
	TOTAL_OTHERS			    ("TOTAL_OTHERS", 1, 19, false, FieldFormatType.ALPHANUM, false),
	ALIAS_STATUS				("ALIAS_STATUS", 1, 30, false, FieldFormatType.SPECIAL, false),
	//Reco
	USER_TYPE                   ("USER_TYPE", 1, 256,false, FieldFormatType.ALPHANUM, false),
	CR_DR                       ("CR_DR", 1, 10, false, FieldFormatType.ALPHANUM, false),
	//added by vijay
	TMBNB_FINAL_REQUEST     ("TMBNB_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	TMB_NB_RESPONSE_FIELD		("TMB_NB_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.SPECIAL, true),
	BROWSER_USER_AGENT			("BROWSER_USER_AGENT", 0, 3000, false, FieldFormatType.SPECIAL,
			false),
	BROWSER_ACCEPT_HEADER			("BROWSER_ACCEPT_HEADER", 0, 3000, false, FieldFormatType.SPECIAL,
			false),
	// Response fields
	// Reco
	
	// Card Information
	// Card number minimum length is 13, because FSS UAT contains a test case with
	// card length 13
	CARD_NUMBER("CARD_NUMBER", 13, 19, false, FieldFormatType.NUMBER, false),
	CARD_MASK("CARD_MASK", 2, 50, false, FieldFormatType.SPECIAL, true),
	S_CARD_NUMBER("S_CARD_NUMBER", 28, 28, false, FieldFormatType.SPECIAL, false),
	H_CARD_NUMBER("H_CARD_NUMBER", 64, 64, false, FieldFormatType.ALPHANUM, false),
	CARD_EXP_DT("CARD_EXP_DT", 6, 6, false, FieldFormatType.NUMBER, false),
	S_CARD_EXP_DT("S_CARD_EXP_DT", 28, 28, false, FieldFormatType.SPECIAL, false),
	H_CARD_EXP_DT("H_CARD_EXP_DT", 64, 64, false, FieldFormatType.ALPHANUM, false),
	CVV("CVV", 3, 4, false, FieldFormatType.NUMBER, false),
	PARES("PARES", 1, 10000, false, FieldFormatType.SPECIAL, false),
	ACS_REQ_MAP("ACS_REQ_MAP", 1, 10000, false, FieldFormatType.SPECIAL, false),
	AUTHPARAM("AUTHPARAM", 1, 100000, false, FieldFormatType.SPECIAL, false),
	INTERNAL_CARD_ISSUER_BANK("INTERNAL_CARD_ISSUER_BANK", 1, 256, false, FieldFormatType.SPECIAL, true),
	INTERNAL_CARD_ISSUER_COUNTRY("INTERNAL_CARD_ISSUER_COUNTRY", 2, 100, false, FieldFormatType.ALPHANUM, true),
	CARD_ISSUER_BANK("CARD_ISSUER_BANK", 1, 256, false, FieldFormatType.ALPHANUM, true),
	CARD_ISSUER_COUNTRY("CARD_ISSUER_COUNTRY", 2, 100, false, FieldFormatType.ALPHANUM, true),
	CARD_HOLDER_NAME("CARD_HOLDER_NAME", 2, 45, false, FieldFormatType.SPECIAL, false),
	CARD_SAVE_FLAG("cardsaveflag", 1, 4, false, FieldFormatType.ALPHA, true),
	// Customer billing information
	CUST_NAME("CUST_NAME", 1, 150, false, FieldFormatType.SPECIAL, true),
	CUST_ID("CUST_ID", 5, 256, false, FieldFormatType.SPECIAL, true),
	CUST_FIRST_NAME("CUST_FIRST_NAME", 2, 150, false, FieldFormatType.ALPHA, true),
	CUST_LAST_NAME("CUST_LAST_NAME", 2, 150, false, FieldFormatType.ALPHA, true),
	CUST_PHONE("CUST_PHONE", 8, 15, false, FieldFormatType.PHONE, false),
	CUST_STREET_ADDRESS1("CUST_STREET_ADDRESS1", 2, 250, false, FieldFormatType.ALPHANUM, true),
	CUST_STREET_ADDRESS2("CUST_STREET_ADDRESS2", 2, 250, false, FieldFormatType.ALPHANUM, true),
	CUST_CITY("CUST_CITY", 2, 50, false, FieldFormatType.ALPHANUM, true),
	CUST_STATE("CUST_STATE", 2, 100, false, FieldFormatType.ALPHANUM, true),
	CUST_COUNTRY("CUST_COUNTRY", 2, 100, false, FieldFormatType.ALPHANUM, true),
	CUST_EMAIL("CUST_EMAIL", 6, 120, false, FieldFormatType.EMAIL, true),
	INTERNAL_USER_EMAIL("INTERNAL_USER_EMAIL", 6, 120, false, FieldFormatType.EMAIL, true),
	CUST_ZIP("CUST_ZIP", 6, 9, false, FieldFormatType.ALPHANUM, true),
	REFUNDABLE_AMOUNT("refundableAmount", 3, 12, false, FieldFormatType.NUMBER, false),
	// Customer shipping information
	CUST_SHIP_NAME("CUST_SHIP_NAME", 2, 150, false, FieldFormatType.ALPHA, true),
	CUST_SHIP_FIRST_NAME("CUST_SHIP_FIRST_NAME", 2, 150, false, FieldFormatType.ALPHA, true),
	CUST_SHIP_LAST_NAME("CUST_SHIP_LAST_NAME", 2, 150, false, FieldFormatType.ALPHA, true),
	CUST_SHIP_PHONE("CUST_SHIP_PHONE", 8, 15, false, FieldFormatType.PHONE, true),
	CUST_SHIP_STREET_ADDRESS1("CUST_SHIP_STREET_ADDRESS1", 2, 250, false, FieldFormatType.ALPHANUM, true),
	CUST_SHIP_STREET_ADDRESS2("CUST_SHIP_STREET_ADDRESS2", 2, 250, false, FieldFormatType.ALPHANUM, true),
	CUST_SHIP_CITY("CUST_SHIP_CITY", 2, 50, false, FieldFormatType.ALPHANUM, true),
	CUST_SHIP_STATE("CUST_SHIP_STATE", 2, 100, false, FieldFormatType.ALPHANUM, true),
	CUST_SHIP_COUNTRY("CUST_SHIP_COUNTRY", 2, 100, false, FieldFormatType.ALPHANUM, true),
	CUST_SHIP_EMAIL("CUST_SHIP_EMAIL", 6, 120, false, FieldFormatType.EMAIL, true),
	CUST_SHIP_ZIP("CUST_SHIP_ZIP", 6, 9, false, FieldFormatType.NUMBER, true),

	// Order information
	REQUEST_URL("REQUEST_URL", 5, 1024, false, FieldFormatType.URL, false),
	RETURN_URL("RETURN_URL", 5, 1024, false, FieldFormatType.URL, false),
	CANCEL_URL("CANCEL_URL", 5, 1024, false, FieldFormatType.URL, false),
	CURRENCY_CODE("CURRENCY_CODE", 3, 3, true, FieldFormatType.NUMBER, false),
	KEY_ID("KEY_ID", 1, 2, false, FieldFormatType.NUMBER, false),
	ORDER_ID("ORDER_ID", 1, 50, true, FieldFormatType.ORDER_ID_TYPE, false),
	PAYIN_ORDER_ID("PAYIN_ORDER_ID", 1, 50, false, FieldFormatType.SPECIAL, false),
	AMOUNT("AMOUNT", 3, 12, true, FieldFormatType.AMOUNT, false),
	REFUNDAMOUNT("REFUNDAMOUNT", 3, 12, false, FieldFormatType.NUMBER, false),
	REFUND_DATE_TIME("REFUND_DATE_TIME", 1, 50, false, FieldFormatType.SPECIAL, false),
	TXNTYPE("TXNTYPE", 4, 50, true, FieldFormatType.ALPHA, false),
	MERCHANT_TXN_ID("MERCHANT_TXN_ID", 6, 50, false, FieldFormatType.SPECIAL, false),
	TXN_ID("TXN_ID", 16, 16, false, FieldFormatType.NUMBER, false),
	TXN_SOURCE("TXN_SOURCE", 6, 8, false, FieldFormatType.ALPHA, false),
	APP_CODE("APP_CODE", 2, 8, false, FieldFormatType.ALPHA, false),
	PAY_ID("PAY_ID", 2, 36, true, FieldFormatType.ALPHANUM, false),
	MERCHANT_ID("MERCHANT_ID", 5, 300, false, FieldFormatType.SPECIAL, false),
	TERMINAL_ID("TERMINAL_ID", 2, 30, false, FieldFormatType.SPECIAL, false),
	RESELLER_ID("RESELLER_ID", 1, 30, false, FieldFormatType.NUMBER, false),
	BANK_ID("BANK_ID", 2, 30, false, FieldFormatType.SPECIAL, false),
	PASSWORD("PASSWORD", 6, 2000, false, FieldFormatType.SPECIAL, false),
	ACCT_ID("ACCT_ID", 1, 20, false, FieldFormatType.NUMBER, false),
	PAYMENT_TYPE("PAYMENT_TYPE", 2, 5, false, FieldFormatType.ALPHA, false),
	PG_REF_NUM("PG_REF_NUM", 1, 100, false, FieldFormatType.ALPHANUM, false),
	INVOICE_ID("INVOICE_ID", 16, 16, false, FieldFormatType.NUMBER, false),
	PAYMENT_LINK_ID("PAYMENT_LINK_ID", 16, 16, false, FieldFormatType.NUMBER, false),
	INVOICE_NO("INVOICE_NO", 1, 50, false, FieldFormatType.ORDER_ID_TYPE, false),
	TENANT_ID("TENANT_ID", 16, 16, false, FieldFormatType.NUMBER, false),
	PG_RESP_CODE("PG_RESP_CODE", 1, 10, false, FieldFormatType.ALPHANUM, false),
	PG_TXN_MESSAGE("PG_TXN_MESSAGE", 1, 500, false, FieldFormatType.SPECIAL, false),
	PG_TXN_STATUS("PG_TXN_STATUS", 1, 500, false, FieldFormatType.SPECIAL, false),
	PG_DATE_TIME("PG_DATE_TIME", 1, 50, false, FieldFormatType.SPECIAL, false),
	PG_GATEWAY("PG_GATEWAY", 1, 100, false, FieldFormatType.ALPHANUM, false),
	HASH("HASH", 64, 64, false, FieldFormatType.ALPHANUM, false),
	INTERNAL_BANK_NAME("BANK_NAME", 3, 255, false, FieldFormatType.ALPHA, false),
	INTERNAL_BANK_CODE("BANK_CODE", 3, 6, false, FieldFormatType.NUMBER, false),
	WALLET_NAME("WALLET_NAME", 3, 6, false, FieldFormatType.ALPHA, false),
	PRODUCT_DESC("PRODUCT_DESC", 1, 1024, false, FieldFormatType.SPECIAL, true),
	TXN_KEY("TXN_KEY", 6, 100, false, FieldFormatType.SPECIAL, false),
	TOKEN_ID("TOKEN_ID", 32, 32, false, FieldFormatType.SPECIAL, false),
	NAME_ON_CARD("NAME_ON_CARD", 3, 50, false, FieldFormatType.ALPHA, false),
	IV("IV", 1, 200, false, FieldFormatType.SPECIAL, false),
	RESP_IV("RESP_IV", 1, 200, false, FieldFormatType.SPECIAL, false),
	RESP_TXN_KEY("RESP_TXN_KEY", 6, 100, false, FieldFormatType.SPECIAL, false),
	// Reco
	CANCELLATION_ORDER_ID("CANCELLATION_ORDER_ID", 1, 50, false, FieldFormatType.ORDER_ID_TYPE, false),
	MOP_TYPE("MOP_TYPE", 2, 10, false, FieldFormatType.ALPHANUM, false),
	PG_DATE_TIME_INDEX("PG_DATE_TIME_INDEX", 1, 50, false, FieldFormatType.SPECIAL, false),
	ACQUIRER_TDR_SC("ACQUIRER_TDR_SC", 1, 90, false, FieldFormatType.SPECIAL, false),
	ACQUIRER_GST("ACQUIRER_GST", 1, 90, false, FieldFormatType.SPECIAL, false),
	PG_TDR_SC("PG_TDR_SC", 1, 90, false, FieldFormatType.SPECIAL, false),
	PG_GST("PG_GST", 1, 90, false, FieldFormatType.SPECIAL, false),

	// merchant Captured and Refunded Amount
	TODAY_CAPTURED_AMOUNT("todayCapturedAmount", 2, 12, false, FieldFormatType.NUMBER, false),
	TODAY_REFUND("todayRefundAmount", 2, 12, false, FieldFormatType.NUMBER, false),
	// Txn Information
	IS_MERCHANT_HOSTED("IS_MERCHANT_HOSTED", 1, 1, false, FieldFormatType.ALPHA, false),
	IS_RECURRING("IS_RECURRING", 1, 1, false, FieldFormatType.ALPHA, true),
	IS_INTERNAL_REQUEST("IS_INTERNAL_REQUEST", 1, 1, false, FieldFormatType.ALPHA, false),
	RECURRING_TRANSACTION_INTERVAL("RECURRING_TRANSACTION_INTERVAL", 1, 25, false, FieldFormatType.ALPHA, false),
	RECURRING_TRANSACTION_COUNT("RECURRING_TRANSACTION_COUNT", 1, 2, false, FieldFormatType.NUMBER, false),
	RETRY_FLAG("RETRY_FLAG", 1, 1, false, FieldFormatType.ALPHA, false),
	NUMBER_OF_RETRY("NUMBER_OF_RETRY", 1, 1, false, FieldFormatType.NUMBER, false),
	COUNT("recordsTotal", 0, 100000000, false, FieldFormatType.NUMBER, false),
	RECURRING_TRANSACTION_ID("RECURRING_TRANSACTION_ID", 16, 16, false, FieldFormatType.NUMBER, false),

	SURCHARGE_FLAG("SURCHARGE_FLAG", 1, 1, false, FieldFormatType.ALPHA, false),
	CC_SURCHARGE("CC_SURCHARGE", 3, 12, false, FieldFormatType.AMOUNT, false),
	WL_SURCHARGE("WL_SURCHARGE", 3, 12, false, FieldFormatType.AMOUNT, false),
	PC_SURCHARGE("PC_SURCHARGE", 3, 12, false, FieldFormatType.AMOUNT, false),
	DC_SURCHARGE("DC_SURCHARGE", 3, 12, false, FieldFormatType.AMOUNT, false),
	CC_SURCHARGE_INTERNATIONAL("CC_SURCHARGE_INTERNATIONAL", 3, 12, false, FieldFormatType.AMOUNT, false),
	DC_SURCHARGE_INTERNATIONAL("DC_SURCHARGE_INTERNATIONAL", 3, 12, false, FieldFormatType.AMOUNT, false),
	NB_SURCHARGE("NB_SURCHARGE", 3, 12, false, FieldFormatType.AMOUNT, false),
	AD_SURCHARGE("AD_SURCHARGE", 3, 12, false, FieldFormatType.AMOUNT, false),
	UP_SURCHARGE("UP_SURCHARGE", 3, 12, false, FieldFormatType.AMOUNT, false),
	CC_TOTAL_AMOUNT("CC_TOTAL_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	CC_TOTAL_AMOUNT_INTERNATIONAL("CC_TOTAL_AMOUNT_INTERNATIONAL", 3, 12, false, FieldFormatType.AMOUNT, false),
	DC_TOTAL_AMOUNT("DC_TOTAL_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	DC_TOTAL_AMOUNT_INTERNATIONAL("DC_TOTAL_AMOUNT_INTERNATIONAL", 3, 12, false, FieldFormatType.AMOUNT, false),
	NB_TOTAL_AMOUNT("NB_TOTAL_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	UP_TOTAL_AMOUNT("UP_TOTAL_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	AD_TOTAL_AMOUNT("AD_TOTAL_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	WL_TOTAL_AMOUNT("WL_TOTAL_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	PC_TOTAL_AMOUNT("PC_TOTAL_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	PC_TOTAL_AMOUNT_INTERNATIONAL("PC_TOTAL_AMOUNT_INTERNATIONAL", 3, 12, false, FieldFormatType.AMOUNT, false),
	SURCHARGE_AMOUNT("SURCHARGE_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	TOTAL_AMOUNT("TOTAL_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	SALE_AMOUNT("SALE_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	SALE_TOTAL_AMOUNT("SALE_TOTAL_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	SERVICE_TAX("SERVICE_TAX", 3, 12, false, FieldFormatType.AMOUNT, false),
	REFUND_FLAG("REFUND_FLAG", 1, 1, false, FieldFormatType.ALPHA, false),
	DELTA_REFUND_FLAG("DELTA_REFUND_FLAG", 1, 3, false, FieldFormatType.ALPHA, false),
	REFUND_ORDER_ID("REFUND_ORDER_ID", 1, 50, false, FieldFormatType.SPECIAL, false),
	// txn channel
	INTERNAL_TXN_CHANNEL("INTERNAL_TXN_CHANNEL", 3, 3, false, FieldFormatType.NUMBER, false),
	POST_SETTLED_FLAG("POST_SETTLED_FLAG", 0, 30, false, FieldFormatType.SPECIAL, false),

	// txn Flag
	TRANSACTION_EMAILER_FLAG("transactionEmailerFlag", 1, 1, false, FieldFormatType.NUMBER, false),
	// MIGS Processing
	MIGS_FINAL_REQUEST("MIGS_FINAL_REQUEST", 1, 1000, false, FieldFormatType.SPECIAL, false),

	// Reco Refund
	DB_PG_REF_NUM("DB_PG_REF_NUM", 1, 100, false, FieldFormatType.ALPHANUM, false),
	DB_ORDER_ID("DB_ORDER_ID", 1, 50, false, FieldFormatType.SPECIAL, false),

	// Reco
	RECO_ACQUIRER_TYPE("ACQUIRER_TYPE", 3, 10, false, FieldFormatType.ALPHANUM, false),
	BOOKING_AMOUNT("BOOKING_AMOUNT", 1, 15, false, FieldFormatType.DECIMAL, false),
	DATE_TIME("DATE_TIME", 8, 14, false, FieldFormatType.NUMBER, false),
	REFUND_AMOUNT("REFUND_AMOUNT", 1, 12, false, FieldFormatType.DECIMAL, false),
	SALE_ORDER_ID("SALE_ORDER_ID", 0, 19, false, FieldFormatType.ORDER_ID_TYPE, false),
	SALE_ACQ_ID("SALE_ACQ_ID", 0, 256, false, FieldFormatType.ALPHANUM, false),
	REFUND_ORIG_TXN_ID("REFUND_ORIG_TXN_ID", 16, 16, false, FieldFormatType.SPECIAL, false),
	REFUND_TICKETING_FLAG("REFUND_TICKETING_FLAG", 1, 90, false, FieldFormatType.ALPHANUM, false),
	IS_SALE_CAPTURED("IS_SALE_CAPTURED", 4, 5, false, FieldFormatType.NONE, false),
	IS_DOUBLE_DEBIT("IS_DOUBLE_DEBIT", 4, 5, false, FieldFormatType.NONE, false),
	IS_REFUND_CAPTURED("IS_REFUND_CAPTURED", 4, 5, false, FieldFormatType.NONE, false),
	IS_REFUND_TIMEOUT("IS_REFUND_TIMEOUT", 4, 5, false, FieldFormatType.NONE, false),
	IS_RECO_SETTLED("IS_RECO_SETTLED", 4, 5, false, FieldFormatType.NONE, false),
	IS_RECO_FORCE_CAPRURED("IS_RECO_FORCE_CAPRURED", 4, 5, false, FieldFormatType.NONE, false),
	IS_RECO_RECONCILED("IS_RECO_RECONCILED", 4, 5, false, FieldFormatType.NONE, false),
	IS_RECO_PENDING("IS_RECO_PENDING", 4, 5, false, FieldFormatType.NONE, false),
	IS_REFUND_PENDING("IS_REFUND_PENDING", 4, 5, false, FieldFormatType.NONE, false),
	IS_ENROLL_ENROLLED("IS_ENROLL_ENROLLED", 4, 5, false, FieldFormatType.NONE, false),
	IS_SALE_TIMEOUT("IS_SALE_TIMEOUT", 4, 5, false, FieldFormatType.NONE, false),
	IS_SALE_SENT_TO_BANK("IS_SALE_SENT_TO_BANK", 4, 5, false, FieldFormatType.NONE, false),
	SALE_PG_REF_NUM("SALE_PG_REF_NUM", 1, 100, false, FieldFormatType.ALPHANUM, false),
	REFUND_PG_REF_NUM("REFUND_PG_REF_NUM", 1, 100, false, FieldFormatType.ALPHANUM, false),
	IS_REFUND_RECO_SETTLED("IS_REFUND_RECO_SETTLED", 4, 5, false, FieldFormatType.NONE, false),
	IS_REFUND_RECO_RECONCILED("IS_REFUND_RECO_RECONCILED", 4, 5, false, FieldFormatType.NONE, false),
	NO_REPORTING_REQUIRED("NO_REPORTING_REQUIRED", 4, 5, false, FieldFormatType.NONE, false),
	RECO_TOTAL_AMOUNT("TOTAL_AMOUNT", 1, 12, false, FieldFormatType.DECIMAL, false),
	RECO_ACQ_ID("ACQ_ID", 0, 256, false, FieldFormatType.SPECIAL, false),
	RECO_AMOUNT("AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	RECO_ORDER_ID("ORDER_ID", 1, 50, false, FieldFormatType.SPECIAL, false),
	RECO_CANCELLATION_ORDER_ID("CANCELLATION_ORDER_ID", 1, 50, false, FieldFormatType.SPECIAL, false),
	RECO_PAY_ID("PAY_ID", 2, 36, false, FieldFormatType.ALPHANUM, false),
	RECO_TXNTYPE("TXNTYPE", 4, 50, false, FieldFormatType.ALPHA, false),
	RECO_PAYMENT_TYPE("PAYMENT_TYPE", 2, 5, false, FieldFormatType.ALPHA, false),
	RECO_CARD_HOLDER_TYPE("CARD_HOLDER_TYPE", 1, 90, false, FieldFormatType.ALPHA, false),
	RECO_BOOKING_AMOUNT("BOOKING_AMOUNT", 1, 15, false, FieldFormatType.DECIMAL, false), // were true
	RECO_DATE_TIME("DATE_TIME", 8, 14, false, FieldFormatType.NUMBER, false), // Were true
	RECO_REFUNDAMOUNT("REFUNDAMOUNT", 3, 12, false, FieldFormatType.DECIMAL, false),
	RECO_SALE_ORDER_ID("SALE_ORDER_ID", 0, 19, false, FieldFormatType.ALPHANUM, false),
	RECO_POST_SETTLED_FLAG("POST_SETTLED_FLAG", 1, 90, false, FieldFormatType.ALPHANUM, false),
	RECO_PG_TXN_MESSAGE("PG_TXN_MESSAGE", 1, 255, false, FieldFormatType.SPECIAL, false),
	RECO_PG_TXN_STATUS("PG_TXN_STATUS", 1, 50, false, FieldFormatType.ALPHANUM, false),
	RECO_EXCEPTION_STATUS("EXCEPTION_STATUS", 5, 30, false, FieldFormatType.ALPHA, false),

	// RECO REPORTING PAGE
	DB_TXN_ID("DB_TXN_ID", 16, 16, false, FieldFormatType.NUMBER, false),
	DB_OID("DB_OID", 16, 16, false, FieldFormatType.NUMBER, false),
	DB_ACQ_ID("DB_ACQ_ID", 0, 256, false, FieldFormatType.ALPHANUM, false),
	DB_ORIG_TXNTYPE("DB_ORIG_TXNTYPE", 16, 16, false, FieldFormatType.SPECIAL, false),
	DB_ORIG_TXN_ID("DB_ORIG_TXN_ID", 16, 16, false, FieldFormatType.SPECIAL, false),
	DB_AMOUNT("DB_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	DB_TXNTYPE("DB_TXNTYPE", 4, 50, false, FieldFormatType.ALPHA, false),
	DB_USER_TYPE("DB_USER_TYPE", 4, 50, false, FieldFormatType.ALPHA, false),
	DB_REFUND_FLAG("DB_REFUND_FLAG", 1, 1, false, FieldFormatType.ALPHA, false),
	FILE_LINE_DATA("FILE_LINE_DATA", 20, 500, false, FieldFormatType.NONE, false),
	FILE_LINE_NO("FILE_LINE_NO", 1, 1000, false, FieldFormatType.NONE, false),
	FILE_NAME("FILE_NAME", 20, 80, false, FieldFormatType.NONE, false),

	// Ipay Processing - Net Banking
	IPAY_FINAL_REQUEST("IPAY_FINAL_REQUEST", 1, 1000, false, FieldFormatType.SPECIAL, false),
	IPAY_FINAL_ENC_RESPONSE("IPAY_FINAL_ENC_RESPONSE", 1, 1000, false, FieldFormatType.SPECIAL, false),
	INDUSTRY_ID("INDUSTRY_ID", 2, 350, false, FieldFormatType.ALPHA, false),

	// UPI
	UPI_RESPONSE_MESSAGE("UPI_RESPONSE_MESSAGE", 1, 256, false, FieldFormatType.ALPHANUM, false),
	UPI_RESPONSE_CODE("UPI_RESPONSE_CODE", 1, 10, false, FieldFormatType.ALPHANUM, false),
	UPI_STATUS("UPI_STATUS", 2, 30, false, FieldFormatType.SPECIAL, false),
	UPI_PG_RESPONSE_MESSAGE("UPI_PG_RESPONSE_MESSAGE", 1, 256, false, FieldFormatType.ALPHANUM, false),
	UPI_PG_RESPONSE_CODE("UPI_PG_RESPONSE_CODE", 1, 10, false, FieldFormatType.ALPHANUM, false),
	PAYER_ADDRESS("PAYER_ADDRESS", 1, 255, false, FieldFormatType.UPIADDRESS, false),
	PAYER_PHONE("PAYER_PHONE", 8, 15, false, FieldFormatType.GOOGLEPAYSPECIAL, true),
	PAYER_NAME("PAYER_NAME", 1, 90, false, FieldFormatType.ALPHASPACE, true),
	PAYEE_ADDRESS("PAYEE_ADDRESS", 1, 255, false, FieldFormatType.UPIADDRESS, false),

	// Federal fields
	FEDERAL_CAVV("FEDERAL_CAVV", 1, 100, false, FieldFormatType.SPECIAL, false),
	FEDERAL_XID("FEDERAL_XID", 1, 100, false, FieldFormatType.SPECIAL, false),
	FEDERAL_STATUS("FEDERAL_STATUS", 1, 1, false, FieldFormatType.ALPHA, false),
	FEDERAL_ECI("FEDERAL_ECI", 1, 5, false, FieldFormatType.NUMBER, false),
	FEDERAL_MD("FEDERAL_MD", 16, 20, false, FieldFormatType.NUMBER, false),
	FEDERAL_MPIERROR_CODE("FEDERAL_MPIERROR_CODE", 1, 10, false, FieldFormatType.ALPHANUM, false),
	FEDERAL_RESPONSE_MESSAGE("FEDERAL_RESPONSE_MESSAGE", 1, 256, false, FieldFormatType.ALPHASPACENUM, false),
	FEDERAL_MPI_ID("FEDERAL_MPI_ID", 1, 100, false, FieldFormatType.NUMBER, false),
	FEDERAL_ENROLL_FINAL_REQUEST("FEDERAL_ENROLL_FINAL_REQUEST", 1, 1000, false, FieldFormatType.SPECIAL, false),

	UDF1("UDF1", 1, 255, false, FieldFormatType.UPIADDRESS, false),
	UDF2("UDF2", 1, 90, false, FieldFormatType.ALPHA, false),
	UDF3("UDF3", 1, 255, false, FieldFormatType.UPIADDRESS, false),
	UDF4("UDF4", 1, 90, false, FieldFormatType.SPECIAL, true),
	UDF5("UDF5", 1, 90, false, FieldFormatType.SPECIAL, true),
	UDF6("UDF6", 1, 90, false, FieldFormatType.SPECIAL, true),

	// add by abhishek
	UDF7("UDF7", 1, 90, false, FieldFormatType.ALPHANUM, true),
	UDF8("UDF8", 1, 90, false, FieldFormatType.ALPHANUM, true),
	UDF9("UDF9", 1, 90, false, FieldFormatType.ALPHANUM, true),
	UDF10("UDF10", 1, 90, false, FieldFormatType.ALPHANUM, true),
	UDF11("UDF11", 1, 90, false, FieldFormatType.ALPHANUM, false),
	UDF12("UDF12", 1, 90, false, FieldFormatType.SPECIAL, false),
	UDF13("UDF13", 1, 90, false, FieldFormatType.SPECIAL, false),
	UDF14("UDF14", 1, 90, false, FieldFormatType.SPECIAL, false),

	REFUND_PROCESS("REFUND_PROCESS", 1, 10, false, FieldFormatType.ALPHANUM, false),

	ADF1("ADF1", 1, 250, false, FieldFormatType.SPECIAL, false),
	ADF2("ADF2", 1, 250, false, FieldFormatType.SPECIAL, false),
	ADF3("ADF3", 1, 250, false, FieldFormatType.SPECIAL, false),
	ADF4("ADF4", 1, 250, false, FieldFormatType.SPECIAL, false),
	ADF5("ADF5", 1, 250, false, FieldFormatType.SPECIAL, false),
	ADF6("ADF6", 1, 250, false, FieldFormatType.SPECIAL, false),
	ADF7("ADF7", 1, 250, false, FieldFormatType.SPECIAL, false),
	ADF8("ADF8", 1, 250, false, FieldFormatType.SPECIAL, false),
	ADF9("ADF9", 1, 250, false, FieldFormatType.SPECIAL, false),
	ADF10("ADF10", 1, 250, false, FieldFormatType.SPECIAL, false),
	ADF11("ADF11", 1, 250, false, FieldFormatType.SPECIAL, false),
	ENCDATA("ENCDATA", 1, 20000, false, FieldFormatType.ALPHANUM, false),
	BOB_FINAL_REQUEST("BOB_FINAL_REQUEST", 1, 1000, false, FieldFormatType.SPECIAL, true),
	BOB_RESPONSE_FIELD("BOB_RESPONSE_FIELD", 1, 1000, false, FieldFormatType.BOBSPECIAL, true),
	FSS_FINAL_REQUEST("FSS_FINAL_REQUEST", 1, 1000, false, FieldFormatType.SPECIAL, true),
	FSS_RESPONSE_FIELD("FSS_RESPONSE_FIELD", 1, 1000, false, FieldFormatType.BOBSPECIAL, true),
	ATL_FINAL_REQUEST("ATL_FINAL_REQUEST", 1, 1000, false, FieldFormatType.ALPHANUM, true),
	ATL_RESPONSE_FIELD("ATL_RESPONSE_FIELD", 1, 1000, false, FieldFormatType.ALPHANUM, true),
	KOTAK_FINAL_REQUEST("KOTAK_FINAL_REQUEST", 1, 1000, false, FieldFormatType.SPECIAL, true),
	KOTAK_RESPONSE_FIELD("KOTAK_RESPONSE_FIELD", 1, 1000, false, FieldFormatType.SPECIAL, true),
	IDBI_FINAL_REQUEST("IDBI_FINAL_REQUEST", 1, 1000, false, FieldFormatType.SPECIAL, true),
	IDBI_RESPONSE_FIELD("IDBI_RESPONSE_FIELD", 1, 1000, false, FieldFormatType.SPECIAL, true),
	DIRECPAY_FINAL_REQUEST("DIRECPAY_FINAL_REQUEST", 1, 1000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	DIRECPAY_RESPONSE_FIELD("DIRECPAY_RESPONSE_FIELD", 1, 1000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	ISGPAY_FINAL_REQUEST("ISGPAY_FINAL_REQUEST", 1, 2000, false, FieldFormatType.ISGPAYSPECIAL, true),
	ISGPAY_RESPONSE_FIELD("ISGPAY_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.ISGPAYSPECIAL, true),
	IDFCUPI_RESPONSE_FIELD("IDFCUPI_RESPONSE_FIELD", 1, 1000, false, FieldFormatType.SPECIAL, true),
	MATCH_MOVE_FINAL_REQUEST("MATCH_MOVE_FINAL_REQUEST", 1, 1000, false, FieldFormatType.SPECIAL, true),
	MATCH_MOVE_RESPONSE_FIELD("MATCH_MOVE_RESPONSE_FIELD", 1, 1000, false, FieldFormatType.SPECIAL, true),
	AXISBANK_NB_FINAL_REQUEST("AXISBANK_NB_FINAL_REQUEST", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	AXISBANK_NB_RESPONSE_FIELD("AXISBANK_NB_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	AXISBANK_UPI_RESPONSE_FIELD("AXISBANK_UPI_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	ATOM_FINAL_REQUEST("ATOM_FINAL_REQUEST", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	ATOM_RESPONSE_FIELD("ATOM_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	APBL_FINAL_REQUEST("APBL_FINAL_REQUEST", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	APBL_RESPONSE_FIELD("APBL_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	MOBIKWIK_FINAL_REQUEST("MOBIKWIK_FINAL_REQUEST", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	MOBIKWIK_RESPONSE_FIELD("MOBIKWIK_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	INGENICO_FINAL_REQUEST("INGENICO_FINAL_REQUEST", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	INGENICO_RESPONSE_FIELD("INGENICO_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	PAYTM_RESPONSE_FIELD("PAYTM_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.PAYTMSPECIAL, true),
	PAYTM_FINAL_REQUEST("PAYTM_FINAL_REQUEST", 1, 2000, false, FieldFormatType.PAYTMSPECIAL, true),
	PHONEPE_RESPONSE_FIELD("PHONEPE_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.PAYTMSPECIAL, true),
	PHONEPE_FINAL_REQUEST("PHONEPE_FINAL_REQUEST", 1, 2000, false, FieldFormatType.PAYTMSPECIAL, true),
	PAYU_RESPONSE_FIELD("PAYU_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.PAYTMSPECIAL, true),
	PAYU_FINAL_REQUEST("PAYU_FINAL_REQUEST", 1, 2000, false, FieldFormatType.PAYTMSPECIAL, true),
	BILLDESK_FINAL_REQUEST("BILLDESK_FINAL_REQUEST", 1, 2000, false, FieldFormatType.SPECIAL, true),
	BILLDESK_RESPONSE_FIELD("BILLDESK_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.SPECIAL, true),
	FREECHARGE_FINAL_REQUEST("FREECHARGE_FINAL_REQUEST", 1, 1000, false, FieldFormatType.SPECIAL, false),
	FREECHARGE_RESPONSE_FIELD("FREECHARGE_RESPONSE_FIELD", 1, 1000, false, FieldFormatType.BOBSPECIAL, false),
	SBI_FINAL_REQUEST("SBI_FINAL_REQUEST", 1, 2000, false, FieldFormatType.SPECIAL, false),
	SBI_RESPONSE_FIELD("SBI_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.SPECIAL, false),
	ICICI_NB_FINAL_REQUEST("ICICI_NB_FINAL_REQUEST", 1, 2000, false, FieldFormatType.SPECIAL, false),
	ICICI_NB_RESPONSE_FIELD("ICICI_NB_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.SPECIAL, false),
	EASEBUZZ_FINAL_REQUEST("EASEBUZZ_FINAL_REQUEST", 1, 2000, false, FieldFormatType.SPECIAL, false),
	EASEBUZZ_RESPONSE_FIELD("EASEBUZZ_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.BOBSPECIAL, false),

	SUPPORTED_PAYMENT_TYPE("SUPPORTED_PAYMENT_TYPE", 1, 250, false, FieldFormatType.SPECIAL, false),
	MERCHANT_PAYMENT_TYPE("MERCHANT_PAYMENT_TYPE", 1, 250, false, FieldFormatType.ALPHANUM, false),
	INSERTION_DATE("INSERTION_DATE", 19, 19, false, FieldFormatType.SPECIAL, false),
	PAYMENTS_REGION("PAYMENTS_REGION", 1, 90, false, FieldFormatType.ALPHA, false),
	CARD_HOLDER_TYPE("CARD_HOLDER_TYPE", 1, 90, false, FieldFormatType.ALPHASPACENUM, false),
	EXCEPTION_STATUS("EXCEPTION_STATUS", 5, 30, false, FieldFormatType.SPECIAL, false),
	DB_ACQUIRER_TYPE("DB_ACQUIRER_TYPE", 3, 10, false, FieldFormatType.ALPHANUM, false),
	DB_PAY_ID("DB_PAY_ID", 2, 36, false, FieldFormatType.ALPHANUM, false),

	// Add Beneficiary Parameters
	CUST_ID_BENEFICIARY("CUST_ID_BENEFICIARY", 1, 32, false, FieldFormatType.ALPHANUM, false),
	BENEFICIARY_CD("BENEFICIARY_CD", 1, 32, false, FieldFormatType.ALPHANUM, false),
	BENE_NAME("BENE_NAME", 1, 255, false, FieldFormatType.ALPHASPACE, false),
	BENE_ACCOUNT_NO("BENE_ACCOUNT_NO", 1, 32, false, FieldFormatType.NUMBER, false),
	IFSC_CODE("IFSC_CODE", 11, 11, false, FieldFormatType.ALPHANUM, false),
	PAYMENT_TYPE_BANK("PAYMENT_TYPE_BANK", 1, 250, false, FieldFormatType.SPECIAL, false),
	BENE_TYPE("BENE_TYPE", 1, 1, false, FieldFormatType.ALPHA, false),
	CURRENCY_CD("CURRENCY_CD", 3, 3, false, FieldFormatType.NUMBER, false),
	NODAL_ACQUIRER("NODAL_ACQUIRER", 1, 250, false, FieldFormatType.SPECIAL, false),
	BANK_NAME("BANK_NAME", 1, 250, false, FieldFormatType.SPECIAL, false),
	SRC_ACCOUNT_NO("SRC_ACCOUNT_NO", 1, 32, false, FieldFormatType.NUMBER, false),
	BENE_EXPIRY_DATE("EXPIRY_DATE", 12, 12, false, FieldFormatType.SPECIAL, false,
			CrmSpecialCharacters.BENE_EXPIRY_DATE),
	BENE_TRANSACTION_LIMIT("TRANSACTION_LIMIT", 1, 16, false, FieldFormatType.NUMBER, false),
	BENE_BANK_NAME("BANK_NAME", 1, 128, false, FieldFormatType.ALPHASPACE, false),
	BENE_MOBILE_NUMBER("MOBILE_NUMBER", 10, 16, false, FieldFormatType.PHONE, false),
	BENE_EMAIL_ID("EMAIL_ID", 6, 120, false, FieldFormatType.EMAIL, true),
	BENE_AADHAR_NO("AADHAR_NO", 12, 12, false, FieldFormatType.NUMBER, true),
	BENE_ADDRESS_1("ADDRESS_1", 1, 128, false, FieldFormatType.SPECIAL, false, CrmSpecialCharacters.ADDRESS),
	BENE_ADDRESS_2("ADDRESS_2", 1, 128, false, FieldFormatType.SPECIAL, false, CrmSpecialCharacters.ADDRESS),
	BENE_SWIFT_CODE("SWIFT_CODE", 1, 16, false, FieldFormatType.ALPHA, false),
	BENE_STATUS("STATUS", 1, 100, false, FieldFormatType.ALPHASPACE, false),
	BENE_REQUESTED_BY("REQUESTED_BY", 1, 250, false, FieldFormatType.EMAIL, false),
	BENE_PROCESSED_BY("PROCESSED_BY", 1, 250, false, FieldFormatType.EMAIL, false),
	BENE_ID("BENE_ID", 16, 16, false, FieldFormatType.SPECIAL, false),
	BENE_MERCHANT_PROVIDED_ID("MERCHANT_PROVIDED_ID", 1, 250, false, FieldFormatType.ALPHANUM, false),
	BENE_MERCHANT_PROVIDED_NAME("MERCHANT_PROVIDED_NAME", 1, 250, false, FieldFormatType.ALPHANUM, false),
	BENE_MERCHANT_BUSINESS_NAME("MERCHANT_BUSINESS_NAME", 1, 250, false, FieldFormatType.ALPHANUM, false),
	// Settlement TXN parameters
	ATTEMPT_NO("ATTEMPT_NO", 1, 250, false, FieldFormatType.NUMBER, false),
	APP_ID("APP_ID", 1, 250, false, FieldFormatType.ALPHANUM, false),
	CUSTOMER_ID("CUSTOMER_ID", 1, 250, false, FieldFormatType.ALPHANUM, false),
	PURPOSE_CODE("PURPOSE_CODE", 1, 250, false, FieldFormatType.ALPHANUM, false),
	REQUESTED_BY("REQUESTED_BY", 1, 250, false, FieldFormatType.SPECIAL, false),
	CAPTURED_DATE("CAPTURED_DATE", 1, 250, false, FieldFormatType.SPECIAL, false),
	SETTLEMENT_DATE("SETTLEMENT_DATE", 1, 250, false, FieldFormatType.SPECIAL, false),
	SETTLEMENT_DATE_INDEX("SETTLEMENT_DATE_INDEX", 1, 12, false, FieldFormatType.ALPHANUM, false),
	SETTLEMENT_FLAG("SETTLEMENT_FLAG", 1, 2, false, FieldFormatType.ALPHANUM, false),
	NODAL_CREDIT_DATE("NODAL_CREDIT_DATE", 19, 19, false, FieldFormatType.SPECIAL, false),
	NODAL_PAYOUT_INITIATED_DATE("NODAL_PAYOUT_INITIATED_DATE", 1, 250, false, FieldFormatType.SPECIAL, false),
	NODAL_PAYOUT_DATE("NODAL_PAYOUT_DATE", 1, 250, false, FieldFormatType.SPECIAL, false),
	// LYRA

	LYRA_FINAL_REQUEST("LYRA_FINAL_REQUEST", 1, 4000, false, FieldFormatType.SPECIAL, false),
	LYRA_RESPONSE_FIELD("LYRA_RESPONSE_FIELD", 1, 4000, false, FieldFormatType.SPECIAL, false),
TFP_FINAL_REQUEST     ("TFP_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	TFP_FINAL_RESPONSE		("TFP_RESPONSE", 1, 2000, false, FieldFormatType.SPECIAL, true),
	// Settlement Report parameters
	SURCHARGE_ACQ("SURCHARGE_ACQ", 3, 12, false, FieldFormatType.AMOUNT, false),
	GST_ACQ("GST_ACQ", 3, 12, false, FieldFormatType.AMOUNT, false),
	SURCHARGE_IPAY("SURCHARGE_IPAY", 3, 12, false, FieldFormatType.AMOUNT, false),
	GST_IPAY("GST_IPAY", 3, 12, false, FieldFormatType.AMOUNT, false),
	SURCHARGE_MMAD("SURCHARGE_MMAD", 3, 12, false, FieldFormatType.AMOUNT, false),
	GST_MMAD("GST_MMAD", 3, 12, false, FieldFormatType.AMOUNT, false),

	// Dashboard Fields
	DASHBOARD_SALE_AMOUNT("SALE_AMOUNT_DASHBOARD", 1, 16, false, FieldFormatType.AMOUNT, false),
	DASHBOARD_SALE_TOTAL_AMOUNT("SALE_TOTAL_AMOUNT_DASHBOARD", 1, 16, false, FieldFormatType.AMOUNT, false),
	DASHBOARD_REFUND_AMOUNT("REFUND_AMOUNT", 1, 16, false, FieldFormatType.AMOUNT, false),
	DASHBOARD_REFUND_TOTAL_AMOUNT("REFUND_TOTAL_AMOUNT", 1, 16, false, FieldFormatType.AMOUNT, false),
	DASHBOARD_SALE_COUNT("DASHBOARD_SALE_COUNT", 1, 16, false, FieldFormatType.ALPHANUM, false),
	DASHBOARD_REFUND_COUNT("DASHBOARD_REFUND_COUNT", 1, 16, false, FieldFormatType.ALPHANUM, false),
	DASHBOARD_FAILED_COUNT("DASHBOARD_FAILED_COUNT", 1, 16, false, FieldFormatType.ALPHANUM, false),

	BIN_RANGE_HIGH("binRangeHigh", 1, 24, false, FieldFormatType.NUMBER, false),
	BIN_RANGE_LOW("binRangeLow", 1, 24, false, FieldFormatType.NUMBER, false),

	// Yes bank notification API
	CUSTOMER_CODE("CUSTOMER_CODE", 6, 6, false, FieldFormatType.ALPHA, false),
	BENE_ACCOUNT_NUMBER("BENE_ACCOUNT_NUMBER", 3, 100, false, FieldFormatType.ALPHANUM, false),
	BENE_IFSC("BENE_IFSC", 11, 11, false, FieldFormatType.ALPHANUM, false),
	YBN_CURRENCY_CODE("YBN_CURRENCY_CODE", 3, 3, false, FieldFormatType.ALPHA, false),
	TRANSFER_TYPE("TRANSFER_TYPE", 3, 4, false, FieldFormatType.ALPHA, false),
	BANK_TRANSACTION_ID("BANK_TRANSACTION_ID", 3, 100, false, FieldFormatType.ALPHANUM, false),
	TIME_STAMP("TIME_STAMP", 3, 50, false, FieldFormatType.SPECIAL, false),
	REMI_ACCOUNT_NUMBER("REMI_ACCOUNT_NUMBER", 3, 100, false, FieldFormatType.ALPHANUM, false),
	REMI_ACCOUNT_IFSC("REMI_ACCOUNT_IFSC", 11, 11, false, FieldFormatType.ALPHANUM, false),
	REMI_ACCOUNT_TYPE("REMI_ACCOUNT_TYPE", 3, 50, false, FieldFormatType.ALPHASPACE, false),
	REMI_NAME("REMI_NAME", 3, 50, false, FieldFormatType.ALPHASPACE, false),
	REMI_ADDRESS("REMI_ADDRESS", 3, 50, false, FieldFormatType.SPECIAL, false),
	REMI_COMMENTS("REMI_COMMENTS", 3, 250, false, FieldFormatType.SPECIAL, false),
	YBN_STATUS("YBN_STATUS", 3, 15, false, FieldFormatType.ALPHA, false),
	YBN_AMOUNT("YBN_AMOUNT", 3, 13, false, FieldFormatType.DECIMAL_AMOUNT, false),
	CREDIT_ACCT_NO("CREDIT_ACCT_NO", 3, 100, false, FieldFormatType.ALPHANUM, false),
	RETURNED_AT("RETURNED_AT", 3, 50, false, FieldFormatType.SPECIAL, false),
	INTERNAL_RESPONSE_FIELDS("INTERNAL_RESPONSE_FIELDS", 1, 6000, false, FieldFormatType.NONE, true),
	TOTAL_TDR_SC("TOTAL_TDR_SC", 1, 90, false, FieldFormatType.SPECIAL, false),
	BENE_ACCOUNT_TYPE("BENE_ACCOUNT_TYPE", 3, 50, false, FieldFormatType.ALPHASPACENUM, false),
	BENE_USER_TYPE("BENE_USER_TYPE", 1, 2, false, FieldFormatType.ALPHA, false),
	PROCESSED_BY("PROCESSED_BY", 1, 250, false, FieldFormatType.SPECIAL, false),
	REMI_ACCOUNT_NO("REMI_ACCOUNT_NO", 3, 100, false, FieldFormatType.ALPHANUM, false),
	REMI_USER_TYPE("REMI_USER_TYPE", 1, 2, false, FieldFormatType.ALPHA, false),
	COMMENTS("COMMENTS", 1, 1024, false, FieldFormatType.SPECIAL, true),
	NODAL_ACCOUNT_NO("NODAL_ACCOUNT_NO", 1, 32, false, FieldFormatType.NUMBER, false),
	ACCOUNT_ENTRY_TYPE("ACCOUNT_ENTRY_TYPE", 1, 100, false, FieldFormatType.ALPHASPACENUM, false),
	REQUEST_CHANNEL("REQUEST_CHANNEL", 1, 250, false, FieldFormatType.SPECIAL, false),
	INTERNAL_HEADER_USER_AGENT("INTERNAL_HEADER_USER_AGENT", 1, 200, false, FieldFormatType.SPECIAL, false),

	CASHFREE_RESPONSE_FIELD("CASHFREE_RESPONSE_FIELD", 1, 3000, false, FieldFormatType.SPECIAL, true),
	CASHFREE_FINAL_REQUEST("CASHFREE_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),

	// added for routerConfiguration id for smart routing
	ROUTER_CONFIGURATION_ID("ROUTER_CONFIGURATION_ID", 1, 30, false, FieldFormatType.SPECIAL, false),
	// add by abhishek

	AGREEPAY_RESPONSE_FIELD("AGREEPAY_RESPONSE_FIELD", 1, 3000, false, FieldFormatType.SPECIAL, true),
	AGREEPAY_FINAL_REQUEST("AGREEPAY_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	FEDERALBANK_NB_RESPONSE_FIELD("FEDERALBANK_NB_RESPONSE_FIELD", 1, 3000, false, FieldFormatType.SPECIAL, true),
	FEDERALBANK_NB_REQUEST("FEDERALBANK_NB_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	YESBANKNB_FINAL_REQUEST("YESBANKNB_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	YESBANKNB_RESPONSE_FIELD("YESBANKNB_RESPONSE_FIELD", 1, 3000, false, FieldFormatType.SPECIAL, true),
	PINELABS_RESPONSE_FIELD("PINELABS_RESPONSE_FIELD", 1, 3000, false, FieldFormatType.SPECIAL, true),
	PINELABS_FINAL_REQUEST("PINELABS_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	HDFC_DUAL_VERFICATION("HDFC_DUAL_VERFICATION", 1, 3000, false, FieldFormatType.SPECIAL, true),
	HDFC_ACQUIRER_CODE("HDFC_ACQUIRER_CODE", 1, 3000, false, FieldFormatType.SPECIAL, true),

	IS_EMITRA("IS_EMITRA", 1, 10, false, FieldFormatType.SPECIAL, true),

	// intent flow 1 = UPI Intent Flow
	UPI_INTENT("UPI_INTENT", 1, 1, false, FieldFormatType.NUMBER, false),
	CUST_CONSENT("CUST_CONSENT", 1, 3000, false, FieldFormatType.SPECIAL, true),
	MERCHANT_CONSENT("MERCHANT_CONSENT", 1, 3000, false, FieldFormatType.SPECIAL, true),
	CAMSPAY_RESPONSE_FIELD("CAMSPAY_RESPONSE_FIELD", 1, 3000, false, FieldFormatType.SPECIAL, true),
	CAMSPAY_FINAL_REQUEST("CAMSPAY_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	UTR_NO("UTR_NO", 12, 12, false, FieldFormatType.ALPHANUM, false),
	COSMOS_UPI_RESPONSE_FIELD("COSMOS_UPI_RESPONSE_FIELD", 1, 3000, false, FieldFormatType.SPECIAL, true),
	COSMOS_UPI_FINAL_REQUEST("COSMOS_UPI_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	QR_TOTAL_AMOUNT("QR_TOTAL_AMOUNT", 3, 12, false, FieldFormatType.AMOUNT, false),
	QR_SURCHARGE("QR_SURCHARGE", 3, 12, false, FieldFormatType.AMOUNT, false),
	CUST_IP("CUST_IP", 1, 3000, false, FieldFormatType.SPECIAL, true),
	IDFC_FINAL_REQUEST("IDFC_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	IDFC_FINAL_RESPONSE("IDFC_FINAL_RESPONSE", 1, 3000, false, FieldFormatType.SPECIAL, true),
	JAMMU_AND_KASHMIR_FINAL_REQUEST("JAMMU_AND_KASHMIR_FINAL_REQUEST", 1, 1000, false, FieldFormatType.SPECIAL, true),
	JAMMU_AND_KASHMIR_RESPONSE_FIELD("JAMMU_AND_KASHMIR_RESPONSE_FIELD", 1, 1000, false, FieldFormatType.SPECIAL, true),
	CANARANB_FINAL_REQUEST("CANARANB_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	CANARABANK_NB_RESPONSE_FIELD("CANARABANK_NB_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
	SHIVALIK_NB_FINAL_REQUEST("SHIVALIK_NB_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	SHIVALIK_NB_RESPONSE_FIELD("SHIVALIK_NB_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.SPECIAL, true),
	DEMO_RESPONSE_FIELD("DEMO_RESPONSE_FIELD", 1, 3000, false, FieldFormatType.SPECIAL, true),
	DEMO_FINAL_REQUEST("DEMO_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	DB_TRANSACTION_STATUS("DB_TXN_STATUS", 5, 30, false, FieldFormatType.ALPHA, false),
	HOLD_RELEASE("HOLD_RELEASE", 1, 1, false, FieldFormatType.NUMBER, false),
	HOLD_DATE("HOLD_DATE", 19, 19, false, FieldFormatType.SPECIAL, false),
	RELEASE_DATE("RELEASE_DATE", 19, 19, false, FieldFormatType.SPECIAL, false),
	ERROR_MESSAGE("ERROR_MESSAGE", 19, 19, false, FieldFormatType.SPECIAL, false),
	LIABILITY_TYPE("LIABILITY_TYPE", 19, 19, false, FieldFormatType.SPECIAL, false),
	CB_CASE_ID("CB_CASE_ID", 1, 3000, false, FieldFormatType.SPECIAL, true),
	FAVOUR("FAVOUR", 1, 3000, false, FieldFormatType.SPECIAL, true),
	DATE("DATE", 1, 3000, false, FieldFormatType.SPECIAL, true),
	LIABILITYHOLDREMARKS("LIABILITY_HOLD_REMARKS", 19, 19, false, FieldFormatType.SPECIAL, false),
	LIABILITYRELEASEREMARKS("LIABILITY_RELEASE_REMARKS", 19, 19, false, FieldFormatType.SPECIAL, false),
	MERCHANT_CATEGORY("MERCHANT_CATEGORY", 0, 3000, false, FieldFormatType.SPECIAL, false),

	MERCHANT_BUSS_NAME("MERCHANT_BUSS_NAME", 0, 3000, false, FieldFormatType.SPECIAL, false),
	BROWSER_LANG("BROWSER_LANG", 0, 3000, false, FieldFormatType.SPECIAL, false),

	BROWSER_SCREEN_HEIGHT("BROWSER_SCREEN_HEIGHT", 0, 3000, false, FieldFormatType.SPECIAL, false),
	BROWSER_SCREEN_WIDTH("BROWSER_SCREEN_WIDTH", 0, 3000, false, FieldFormatType.SPECIAL, false),
	BROWSER_TZ("BROWSER_TZ", 0, 3000, false, FieldFormatType.SPECIAL, false),

	BROWSER_JAVA_ENABLED("BROWSER_JAVA_ENABLED", 0, 3000, false, FieldFormatType.SPECIAL, false),
	BROWSER_DEVICE("BROWSER_DEVICE", 0, 3000, false, FieldFormatType.SPECIAL, false),

	BROWSER_COLOR_DEPTH("BROWSER_COLOR_DEPTH", 0, 3000, false, FieldFormatType.SPECIAL, false),
	SBI_CARD_PA_RES("SBI_CARD_PA_RES", 0, 3000, false, FieldFormatType.SPECIAL, false),

	SBI_AUTH_RESPONSE("SBI_AUTH_RESPONSE", 0, 3000, false, FieldFormatType.SPECIAL, false),
	SBI_CARD_FINAL_RES("SBI_CARD_FINAL_RES", 0, 3000, false, FieldFormatType.SPECIAL, false),
	PSPNAME("PSPNAME", 1, 255, false, FieldFormatType.SPECIAL, false),
	UPDATEDBY("UPDATED_BY", 19, 19, false, FieldFormatType.SPECIAL, false),
	UPDATEDAT("UPDATED_AT", 19, 19, false, FieldFormatType.SPECIAL, false),
	EMI_BANK_ID("EMI_BANK_ID", 1, 3000, false, FieldFormatType.SPECIAL, false),
	TENURE_LENGTH("TENURE_LENGTH", 1, 10, false, FieldFormatType.SPECIAL, false),
	SBI_OTP_PAGE("SBI_OTP_PAGE", 1, 10, false, FieldFormatType.SPECIAL, false),

	// Added By Sweety
	IS_MERCHANT_S2S("IS_MERCHANT_S2S", 1, 1, false, FieldFormatType.ALPHA, false),
	GRSID("GRSID", 19, 19, false, FieldFormatType.SPECIAL, false),
	CITY_UNION_BANK_FINAL_REQUEST("CITY_UNION_BANK_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	CITY_UNION_BANK_RESPONSE_FIELD("CITY_UNION_BANK_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
	PATH("PATH", 1, 3000, false, FieldFormatType.SPECIAL, false),	
	
	
		IRCTC_REFUND_TYPE("IRCTC_REFUND_TYPE", 1, 100, false, FieldFormatType.SPECIAL, true),
		IRCTC_REFUND_CANCELLED_DATE("IRCTC_REFUND_CANCELLED_DATE", 1, 100, false, FieldFormatType.SPECIAL, true),
		IRCTC_REFUND_CANCELLED_ID("IRCTC_REFUND_CANCELLED_ID", 1, 100, false, FieldFormatType.SPECIAL, true),
		IRCTC_REFUND_FILE_TYPE("IRCTC_REFUND_FILE_TYPE", 1, 100, false, FieldFormatType.SPECIAL, true),
		IRCTC_REFUND_FILE_DATE("IRCTC_REFUND_FILE_DATE", 1, 100, false, FieldFormatType.SPECIAL, true),
		IRCTC_REFUND("IRCTC_REFUND", 1, 100, false, FieldFormatType.SPECIAL, true),
		
		QUOMO_FINAL_REQUEST("QUOMO_FINAL_REQUEST", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
		QUOMO_RESPONSE_FIELD("QUOMO_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.DIRECPAYSPECIAL, true),
		HTPAY_FINAL_REQUEST("HTPAY_FINAL_REQUEST", 1, 2000, false, FieldFormatType.SPECIAL, true),
		HTPAY_RESPONSE_FIELD("HTPAY_RESPONSE_FIELD", 1, 2000, false, FieldFormatType.SPECIAL, true),
		CHANNEL("CHANNEL",1,50,false,FieldFormatType.ALPHA,false),
		
		
		
		ACC_NO("ACC_NO", 1, 3000, false, FieldFormatType.SPECIAL, false),
		CLIENT_IP("CLIENT_IP", 1, 3000, false, FieldFormatType.SPECIAL, false),
		PAY_TYPE("PAY_TYPE", 1, 3000, false, FieldFormatType.SPECIAL, false),
		ACC_TYPE("ACC_TYPE", 1, 3000, false, FieldFormatType.SPECIAL, false),
		ACC_PROVINCE("ACC_PROVINCE", 1, 3000, false, FieldFormatType.SPECIAL, false),
		ACC_CITY_NAME("ACC_CITY_NAME", 1, 3000, false, FieldFormatType.SPECIAL, false),
		ACC_NAME("ACC_NAME", 1, 3000, false, FieldFormatType.SPECIAL, false),
		BANK_BRANCH("BANK_BRANCH", 1, 3000, false, FieldFormatType.SPECIAL, false),
		BANK_CODE("BANK_CODE", 1, 3000, false, FieldFormatType.SPECIAL, false),
		IFSC("IFSC", 1, 3000, false, FieldFormatType.SPECIAL, false),
		TXN_TYPE("PATH", 1, 3000, false, FieldFormatType.SPECIAL, false),
		MERCHANT_NAME("MERCHANT_NAME", 1, 3000, false, FieldFormatType.SPECIAL, false),
		PAY10_FINAL_REQUEST("PAY10_FINAL_REQUEST", 1, 3000, false, FieldFormatType.SPECIAL, true),
		PAY10_FINAL_RESPONSE("PAY10_FINAL_RESPONSE", 1, 3000, false, FieldFormatType.SPECIAL, true),

		REMARKS("REMARKS", 1, 3000, false, FieldFormatType.SPECIAL, false),
	APPROVER_REMARK("APPROVER_REMARK", 1, 50, false, FieldFormatType.ALPHA, false),
	CREATED_BY("CREATED_BY", 1, 50, false, FieldFormatType.ALPHA, false),
	REJECT_REMARK("REJECT_REMARK", 1, 50, false, FieldFormatType.ALPHA, false),

	// Added for Payout
	COMMISSION("COMMISSION", 1, 50, false, FieldFormatType.ALPHA, false),
	COMMISSION_TYPE("COMM_TYPE", 1, 50, false, FieldFormatType.ALPHA, false),
	COMMISSION_VALUE("COMM_VALUE", 1, 50, false, FieldFormatType.NUMBER, false),
	REMARK("REMARK", 1, 50, false, FieldFormatType.ALPHA, false),
	CURRENCY_NAME("CURRENCY_NAME", 1, 3000, false, FieldFormatType.SPECIAL, false),
	MIN_TICKET_SIZE("MIN_TICKET_SIZE", 1, 3000, false, FieldFormatType.SPECIAL, false),
	MAX_TICKET_SIZE("MAX_TICKET_SIZE", 1, 3000, false, FieldFormatType.SPECIAL, false),
	DAILY_LIMIT("DAILY_LIMIT", 1, 3000, false, FieldFormatType.SPECIAL, false),
	WEEKLY_LIMIT("WEEKLY_LIMIT", 1, 3000, false, FieldFormatType.SPECIAL, false),
	MONTHLY_LIMIT("MONTHLY_LIMIT", 1, 3000, false, FieldFormatType.SPECIAL, false),
	DAILY_VOLUME("DAILY_VOLUME", 1, 3000, false, FieldFormatType.SPECIAL, false),
	WEEKLY_VOLUME("WEEKLY_VOLUME", 1, 3000, false, FieldFormatType.SPECIAL, false),
	MONTHLY_VOLUME("MONTHLY_VOLUME", 1, 3000, false, FieldFormatType.SPECIAL, false),
	OPENING_BALANCE("OPENING_BALANCE", 1, 3000, false, FieldFormatType.SPECIAL, false),
	CLOSING_BALANCE("CLOSING_BALANCE", 1, 3000, false, FieldFormatType.SPECIAL, false),
	PAYMENT_REDIRECT_ID("PAYMENT_REDIRECT_ID", 1, 100, false, FieldFormatType.SPECIAL, false),

	WEBHOOK_FAILED_COUNT("WEBHOOK_FAILED_COUNT", 1, 3, false, FieldFormatType.SPECIAL, false),
	WEBHOOK_POST_FLAG("WEBHOOK_POST_FLAG", 1, 3, false, FieldFormatType.SPECIAL, false),
	POSTING_METHOD_FLAG("POSTING_METHOD_FLAG", 1, 3, false, FieldFormatType.SPECIAL, false);

	private final String name;
	private final int minLength;
	private final int maxLength;
	private final boolean required;
	private final FieldFormatType type;
	private final String responseMessage;
	private final boolean isSpecialCharReplacementAllowed;
	private final CrmSpecialCharacters specialChars;

	private FieldType(String name, int minLength, int maxLength, boolean required, FieldFormatType type,
			boolean isSpecialCharReplacementAllowed, CrmSpecialCharacters specialChars) {
		this.name = name;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.required = required;
		this.responseMessage = "Invalid " + name;
		this.type = type;
		this.isSpecialCharReplacementAllowed = isSpecialCharReplacementAllowed;
		this.specialChars = specialChars;
	}

	private FieldType(String name, int minLength, int maxLength, boolean required, String responseMessage,
			FieldFormatType type, boolean isSpecialCharReplacementAllowed) {
		this.name = name;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.required = required;
		this.responseMessage = responseMessage;
		this.type = type;
		this.isSpecialCharReplacementAllowed = isSpecialCharReplacementAllowed;
		this.specialChars = null;
	}

	private FieldType(String name, int minLength, int maxLength, boolean required, FieldFormatType type,
			boolean isSpecialCharReplacementAllowed) {
		this.name = name;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.required = required;
		this.responseMessage = "Invalid " + name;
		this.type = type;
		this.isSpecialCharReplacementAllowed = isSpecialCharReplacementAllowed;
		this.specialChars = null;
	}

	public static Map<String, FieldType> getFieldsMap() {
		Map<String, FieldType> fields = new HashMap<String, FieldType>();

		FieldType[] fieldTypes = FieldType.values();
		for (FieldType fieldType : fieldTypes) {
			fields.put(fieldType.getName(), fieldType);
		}

		return fields;
	}

	public static Map<String, FieldType> getMandatoryRequestFields() {
		Map<String, FieldType> fields = new HashMap<String, FieldType>();

		FieldType[] fieldTypes = FieldType.values();
		for (FieldType fieldType : fieldTypes) {
			if (fieldType.isRequired()) {
				fields.put(fieldType.getName(), fieldType);
			}
		}

		return fields;
	}

	public static Map<String, FieldType> getMandatorSupportFields() {
		Map<String, FieldType> fields = new HashMap<String, FieldType>();

		fields.put(FieldType.ORIG_TXN_ID.getName(), FieldType.ORIG_TXN_ID);
		fields.put(FieldType.TXNTYPE.getName(), FieldType.TXNTYPE);
		fields.put(FieldType.AMOUNT.getName(), FieldType.AMOUNT);
		fields.put(FieldType.PAY_ID.getName(), FieldType.PAY_ID);
		fields.put(FieldType.HASH.getName(), FieldType.HASH);

		return fields;
	}

	public static Map<String, FieldType> getMandatoryStatusRequestFields() {
		Map<String, FieldType> fields = new HashMap<String, FieldType>();

		fields.put(FieldType.ORDER_ID.getName(), FieldType.ORDER_ID);
		fields.put(FieldType.TXNTYPE.getName(), FieldType.TXNTYPE);
		//fields.put(FieldType.AMOUNT.getName(), FieldType.AMOUNT);
		fields.put(FieldType.PAY_ID.getName(), FieldType.PAY_ID);
		//fields.put(FieldType.CURRENCY_CODE.getName(), FieldType.CURRENCY_CODE);
		fields.put(FieldType.HASH.getName(), FieldType.HASH);

		return fields;
	}

	public static Map<String, FieldType> getMandatoryRecoRequestFields() {
		Map<String, FieldType> fields = new HashMap<String, FieldType>();

		fields.put(FieldType.ORIG_TXN_ID.getName(), FieldType.ORIG_TXN_ID);
		fields.put(FieldType.ORDER_ID.getName(), FieldType.ORDER_ID);
		fields.put(FieldType.TXNTYPE.getName(), FieldType.TXNTYPE);
		fields.put(FieldType.AMOUNT.getName(), FieldType.AMOUNT);
		fields.put(FieldType.PAY_ID.getName(), FieldType.PAY_ID);
		fields.put(FieldType.HASH.getName(), FieldType.HASH);

		return fields;
	}

	public String getName() {
		return name;
	}

	public int getMinLength() {
		return minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public boolean isRequired() {
		return required;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public FieldFormatType getType() {
		return type;
	}

	public boolean isSpecialCharReplacementAllowed() {
		return isSpecialCharReplacementAllowed;
	}
}