package com.pay10.commons.util;

public enum CrmFieldType {

	// Personal details
		PASSWORD				("password",8,32,FieldFormatType.SPECIAL,CrmSpecialCharacters.PASSWORD),
		PAY_ID					("payId",10,20,FieldFormatType.NUMBER),
		OTP					    ("otp",2,20,FieldFormatType.NUMBER),
		FIRSTNAME				("firstName", 2, 32, FieldFormatType.ALPHASPACE),
		LASTNAME 				("lastName", 2, 32, FieldFormatType.ALPHASPACE),
		COMPANY_NAME			("companyName", 2, 256,FieldFormatType.SPECIAL,CrmSpecialCharacters.COMPANY),
		WEBSITE					("website", 10, 256, FieldFormatType.SPECIAL,CrmSpecialCharacters.WEBSITE),
		CONTACT_PERSON			("contactPerson", 5,35,FieldFormatType.ALPHASPACE),
		EMAILID 				("emailId", 5, 100, FieldFormatType.EMAIL),
		INTERNAL_USER_EMAIL		("internalUserEmail", 5, 100, FieldFormatType.EMAIL),
		BUSINESS_TYPE			("businessType", 2, 250, FieldFormatType.SPECIAL,CrmSpecialCharacters.INDUSTRYCATEGORY),
		MERCHANT_TYPE			("merchantType",4,34,FieldFormatType.ALPHASPACE),
		MERCHANT			    ("merchant",2,150,FieldFormatType.ALPHASPACE),
		NO_OF_TRANSACTIONS		("noOfTransactions", 1, 32, FieldFormatType.NUMBER), 
		AMOUNT_OF_TRANSACTIONS	("amountOfTransactions", 3, 12, FieldFormatType.NUMBER),
		RESELLER_ID				("resellerId", 10,20,FieldFormatType.NUMBER),
		PRODUCT_DETAIL			("productDetail",3,23,FieldFormatType.ALPHA),
		REGISTRATION_DATE		("registrationDate", 2, 32, FieldFormatType.DATE),
		BUSINESS_NAME			("businessName",2,256,FieldFormatType.SPECIAL,CrmSpecialCharacters.BUSINESS_NAME),
	    USERSTATUS				("userStatus",4,30,FieldFormatType.ALPHA),
	    ORGANIZATIONTYPE		("organisationType",1,30,FieldFormatType.ALPHASPACENUM),
	    ACTIVATION_DATE		    ("activationDate",4,30,FieldFormatType.DATE),
	    ATTEMPT_TRASACATION		("attemptTrasacation",0,5,FieldFormatType.NUMBER),
	    POST_SETTLED_FLAG		("postSettledFlag",0,20,FieldFormatType.ALPHASPACE),

	    MULTICURRENCY		    ("multiCurrency",2,30,FieldFormatType.ALPHA),
	    BUSINESSMODEL		    ("businessModel",2,100,FieldFormatType.SPECIAL,CrmSpecialCharacters.BUSINESSMODEL),
	    OPERATIONADDRESS	    ("operationAddress",3, 250, FieldFormatType.SPECIAL,CrmSpecialCharacters.ADDRESS),
	    OPERATION_CITY	        ("operationCity",3, 150, FieldFormatType.SPECIAL,CrmSpecialCharacters.ADDRESS),
	    PPERATION_STATE	        ("operationState",3, 150, FieldFormatType.SPECIAL,CrmSpecialCharacters.ADDRESS),
	    CIN		                ("cin",2,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.BUSINESSMODEL),
	    TAN		                ("TAN",2,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.BUSINESSMODEL),
	    PAN		                ("pan",2,30,FieldFormatType.ALPHANUM),
	    PANNAME		            ("panName",2,30,FieldFormatType.ALPHASPACENUM),
	    DATE_OF_ESTABLISHMENT   ("dateOfEstablishment",6,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.DATE),
	    STATUS				    ("status",4,30,FieldFormatType.ALPHA),
	    INDUSTRY_CATEGORY		("industryCategory",2,350,FieldFormatType.SPECIAL,CrmSpecialCharacters.INDUSTRYCATEGORY),
	    INDUSTRY_SUB_CATEGORY	("industrySubCategory",2,350,FieldFormatType.SPECIAL,CrmSpecialCharacters.INDUSTRYSUBCATEGORY),
	    ID						("id",1,20,FieldFormatType.ALPHANUM),
		// Contact Details	
		MOBILE 					("mobile", 10, 15, FieldFormatType.NUMBER ),
		TELEPHONE_NO 			("telephoneNo", 8, 15, FieldFormatType.SPECIAL,CrmSpecialCharacters.TELEPHONE ),
		FAX 					("fax", 8, 15, FieldFormatType.NUMBER),
		ADDRESS					("address", 3, 150, FieldFormatType.SPECIAL,CrmSpecialCharacters.ADDRESS),
		CITY					("city", 2, 32, FieldFormatType.ALPHASPACE),
		STATE					("state", 2, 32, FieldFormatType.ALPHASPACE),
		COUNTRY					("country", 2, 32, FieldFormatType.ALPHASPACE),
		POSTALCODE				("postalCode", 4, 20, FieldFormatType.NUMBER),
		OPERATION_POSTAL_CODE 	("operationPostalCode", 4, 20, FieldFormatType.NUMBER),
		
		// Action	
		COMMENTS				("comments", 1, 200, FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		WHITE_LIST_IPADDRES 	("whiteListIpAddress", 6, 15, FieldFormatType.NUMBER),
		
		// Bank Details
		BANK_NAME			("bankName", 3, 200, FieldFormatType.ALPHASPACENUM),
		IFSC_CODE			("ifscCode", 11, 11, FieldFormatType.ALPHANUM),
		ACC_HOLDER_NAME		("accHolderName", 4, 200, FieldFormatType.ALPHASPACENUM),
		CURRENCY			("currency", 3, 3, FieldFormatType.ALPHANUM),       
		BRANCH_NAME			("branchName", 3, 200, FieldFormatType.SPECIAL,CrmSpecialCharacters.SPACE),
		PANCARD				("pancard", 6, 10, FieldFormatType.ALPHANUM),
		ACCOUNT_NO			("accountNo", 6, 200, FieldFormatType.NUMBER),
		
	    PG_URL    			("pgUrl", 8, 300, FieldFormatType.URL),
	    COMPANY_URL    		("companyUrl", 8, 300, FieldFormatType.URL),
		TENANT_NUMBER		("tenantNumber", 9, 11, FieldFormatType.ALPHANUM),
		
		//Naming Convention
		SETTLEMENT_NAMING_CONVENTION		("settlementNamingConvention",1,50,FieldFormatType.SPECIAL,CrmSpecialCharacters.SETTLEMENT_NAMING_CONVENTION),
		REFUNDVALIDATION_NAMING_CONVENTION	("refundValidationNamingConvention",1,50,FieldFormatType.SPECIAL,CrmSpecialCharacters.REFUNDVALIDATION_NAMING_CONVENTION),
		
		// Account class
		RESPONSE		       ("response", 4, 200, FieldFormatType.ALPHASPACE),
		MERCHANT_EMAILID 	   ("merchantEmailId", 5, 100, FieldFormatType.EMAIL),
		NUMBER			       ("number", 6, 200, FieldFormatType.NUMBER),
		MERCHANT_ID		       ("MERCHANT_ID", 6, 200, FieldFormatType.NUMBER),
		ACQUIRER_PAY_ID	       ("ACQUIRER_PAY_ID", 6, 200, FieldFormatType.NUMBER),
		ACCOUNT_VALIDATION_KEY ("accountValidationKey", 6, 200, FieldFormatType.NUMBER),
		
		//Invoice Fields
		
		INVOICE_ADDRESS					("ADDRESS",3, 250, FieldFormatType.SPECIAL,CrmSpecialCharacters.ADDRESS),
		INVOICE_AMOUNT        			("AMOUNT", 1, 10, FieldFormatType.AMOUNT),
		INVOICE_BUSINESS_NAME 			("BUSINESS_NAME",2,256,FieldFormatType.SPECIAL,CrmSpecialCharacters.BUSINESS_NAME),
		INVOICE_CITY          			("CITY", 2, 100, FieldFormatType.ALPHASPACE),
		INVOICE_COUNTRY       			("COUNTRY", 2, 100, FieldFormatType.ALPHASPACE),
		INVOICE_CREATE_DATE				("CREATE_DATE",2,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.DATE),
	    INVOICE_CURRENCY_CODE 			("CURRENCY_CODE", 3, 3, FieldFormatType.ALPHANUM),
		INVOICE_REGION 					("PAYMENT_REGION", 1, 90, FieldFormatType.ALPHA),
		INVOICE_EMAIL         			("EMAIL", 8, 100, FieldFormatType.EMAIL),
		INVOICE_EXPIRES_DAY   			("EXPIRES_DAY", 1, 2, FieldFormatType.NUMBER),
		INVOICE_MONTH		   			("month", 0, 13, FieldFormatType.NUMBER),
		INVOICE_YEAR		   			("year", 0, 2500, FieldFormatType.NUMBER),
	    INVOICE_EXPIRES_HOUR  			("EXPIRES_HOUR", 1, 2, FieldFormatType.NUMBER),
	    INVOICE_ID            			("INVOICE_ID", 2, 20, FieldFormatType.NUMBER),
	    PAYMENT_LINK_ID            		("PAYMENT_LINK_ID", 2, 20, FieldFormatType.NUMBER),
		INVOICE_NUMBER        			("INVOICE_NO", 1, 45, FieldFormatType.SPECIAL,CrmSpecialCharacters.INVOICE_NUMBER),
		INVOICE_TYPE					("INVOICE_TYPE",1,50,FieldFormatType.ALPHASPACE),
		INVOICE_MESSAGE_BODY			("MESSAGE_BODY",1, 500, FieldFormatType.SPECIAL, CrmSpecialCharacters.CHARGEBACK_CHAT_SPECIAL_CHAR),
		INVOICE_NAME          			("NAME", 2, 32, FieldFormatType.ALPHASPACE),
		INVOICE_PAY_ID		  			("PAY_ID",10,20,FieldFormatType.NUMBER),
		INVOICE_PHONE         			("PHONE", 8, 13, FieldFormatType.NUMBER),
		INVOICE_PRODUCT_DESCRIPTION  	("PRODUCT_DESC", 2, 250, FieldFormatType.ALPHASPACENUM),
		INVOICE_PRODUCT_NAME          	("PRODUCT_NAME", 1, 50, FieldFormatType.ALPHASPACENUM),
			
		INVOICE_QUANTITY			  	("QUANTITY", 1, 6, FieldFormatType.NUMBER),
	    INVOICE_RECIPIENT_MOBILE		("RECIPIENT_MOBILE", 10, 15, FieldFormatType.NUMBER ),
	    INVOICE_RETURN_URL    			("RETURN_URL", 8, 300, FieldFormatType.URL),
	    INVOICE_SHORT_URL           	("SHORT_URL", 8, 200, FieldFormatType.URL),
	    INVOICE_STATE         			("STATE", 2, 100, FieldFormatType.ALPHASPACE),
	    INVOICE_TOTAL_AMOUNT			("TOTAL_AMOUNT", 1, 10, FieldFormatType.AMOUNT),
	    INVOICE_UPDATE_DATE				("UPDATE_DATE",2,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.DATE),
	    INVOICE_EXPIRY_TIME				("EXPIRY_TIME",2,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.DATE),
	    INVOICE_ZIP           			("ZIP", 6, 10, FieldFormatType.ALPHANUM),
	    INVOICE_GST						("GST",1,3, FieldFormatType.AMOUNT),
	    INVOICE_GST_AMOUNT				("GST_AMOUNT", 1, 10, FieldFormatType.AMOUNT),
	    INVOICE_STATUS					("INVOICE_STATUS",1, 100, FieldFormatType.ALPHASPACE),
	    INVOICE_URL           			("INVOICE_URL", 8, 200, FieldFormatType.URL),
	    INVOICE_SALT_KEY                ("SALT_KEY", 2, 200, FieldFormatType.ALPHANUM),
	    INVOICE_SERVICE_CHARGE        	("SERVICE_CHARGE", 1, 5, FieldFormatType.AMOUNT),
	    // Invoice fields : end
	    
		SERVICE_CHARGE        ("serviceCharge", 1, 5, FieldFormatType.AMOUNT),
		SALT_KEY              ("saltKey", 2, 200, FieldFormatType.ALPHANUM),
		TRANSACTION_EMAIL_ID  ("transactionEmailId", 5, 100, FieldFormatType.EMAIL),
		PARENT_PAY_ID		  ("parentPayId",10,20,FieldFormatType.NUMBER),
		PRODUCT_NAME          ("productName", 1, 200, FieldFormatType.SPECIAL,CrmSpecialCharacters.ADDRESS),
		//Login Details
		IP					  ("IPADDRESS",5,20,FieldFormatType.SPECIAL, CrmSpecialCharacters.IP),
		INTERNAL_CUST_IP 	  ("internalCustIp",5,20,FieldFormatType.SPECIAL, CrmSpecialCharacters.IP),
		OPERATINGSYSTEM		  ("OPERATINGSYSTEM",4,20,FieldFormatType.SPECIAL,CrmSpecialCharacters.OPERATINGSYSTEM),
		BROWSER				  ("BROWSER",2,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.BROWSER),
		//Remittance Field
		UTR 			      ("utr", 2, 20, FieldFormatType.ALPHANUM),
		CAPTCHA			  	  ("captcha", 8, 8, FieldFormatType.ALPHANUM),
		NET_AMOUNT         	  ("netAmount", 1, 32, FieldFormatType.AMOUNT),
		REMITTED_DATE		  ("remittedDate", 1, 32, FieldFormatType.AMOUNT),
		
		// Account 
		MERCHANTID		      ("merchantId", 1, 200, FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		ACQUIRER_PAYID	      ("acquirerPayId", 1, 200, FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		ACQ_PAYID	          ("acqPayId", 1, 200, FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		ACCOUNT_PASSWORD      ("password", 1, 500, FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		TXN_KEY		          ("txnKey", 1, 200, FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		AQCQUIRER_NAME	      ("acquirerName", 1, 200, FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		//Transaction reporting/search
		MAP_STRING            ("mapString", 2, 4000, FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		MERCHANT_EMAIL_ID     ("merchantEmailId",5,100,FieldFormatType.EMAIL),
		CUSTOMER_EMAIL_ID     ("customerEmail",5,100,FieldFormatType.EMAIL),
		DATE_FROM             ("dateFrom",2,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.DATE),
		DATE_TO               ("dateTo",2,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.DATE),
		ACQUIRER              ("acquirer",2,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.COMMA),
		PAYMENT_TYPE          ("paymentType",2,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.COMMA),
		SUCCESS_MESSAGE       ("successMessage",2,100,FieldFormatType.ALPHASPACE),
		ORDER_ID              ("orderId",1,100,FieldFormatType.SPECIAL,CrmSpecialCharacters.ORDER_ID),
		CARD_NUMBER_MASK      ("cardNumber",2,100,FieldFormatType.SPECIAL,CrmSpecialCharacters.CARD_MASK),
		TXN_STATUS            ("status",2,30,FieldFormatType.ALPHASPACE),
		ALIAS_STATUS          ("status",2,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.COMMA),
		TRANSACTION_ID        ("transactionId",10,20,FieldFormatType.NUMBER),
		TENANT_ID        	  ("tenantId",10,20,FieldFormatType.NUMBER),
		AMOUNT                ("amount",1,20,FieldFormatType.AMOUNT),
		GENERAL_STRING        ("messageString",1,6000,FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		UPLOADE_PHOTO         ("uploadePhoto",1,6000,FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		UPLOADE_PAN_CARD	  ("uploadedPanCard",1,6000,FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		UPLOADE_PHOTOID_PROOF      ("uploadedPhotoIdProof",1,6000,FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		UPLOADE_CONTRACT_DOCUMENT  ("uploadedContractDocument",1,6000,FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		//Extra Refund Amount
		EXTRA_REFUND_LIMIT		   ("extraRefundLimit",1,32,FieldFormatType.AMOUNT),
		DEFAULT_CURRENCY		   ("defaultCurrency",3,3,FieldFormatType.ALPHANUM),
		//Amex field43
		AMEX_SELLER_ID			   ("amexSellerId",1,40,FieldFormatType.ALPHANUM),
		MCC		   			       ("mCC",1,10,FieldFormatType.ALPHANUM),
		
		//Fraud Prevention System
		FRAUD_IP_ADDRESS					("IP address",7,45, FieldFormatType.SPECIAL, CrmSpecialCharacters.FRAUD_IP),
		FRAUD_SUBNET_MASK					("Subnet mask",7,15, FieldFormatType.SPECIAL, CrmSpecialCharacters.FRAUD_IP),
		FRAUD_DOMAIN_NAME					("Domain name", 10, 256,  FieldFormatType.SPECIAL, CrmSpecialCharacters.WEBSITE),
		FRAUD_NEGATIVE_BIN					("Card bin", 6, 6,  FieldFormatType.NUMBER),	
		FRAUD_NEGATIVE_CARD					("Card no.", 15, 19,  FieldFormatType.SPECIAL, CrmSpecialCharacters.CARD_MASK),
		FRAUD_MIN_TRANSACTION_AMOUNT		("Min. txn amount", 1, 32,  FieldFormatType.AMOUNT),
		FRAUD_MAX_TRANSACTION_AMOUNT		("Max. txn amount", 1, 32,  FieldFormatType.AMOUNT),
		FRAUD_ISSUER_COUNTRY				("Card issuer country", 2, 32,  FieldFormatType.ALPHASPACE),
		FRAUD_USER_COUNTRY					("User country", 2, 32,  FieldFormatType.ALPHASPACE),
		FRAUD_PER_CARD_TRANSACTION_ALLOWED	("Per card allowed txns", 1, 32,FieldFormatType.NUMBER),
		FRAUD_MINUTE_TXN_LIMIT				("Minute txn limit", 1, 32, FieldFormatType.NUMBER),
		FRAUD_CURRENCY						("Currency", 3, 3, FieldFormatType.ALPHANUM), 
		FRAUD_EMAIL							("EMAIL ID", 5, 100, FieldFormatType.EMAIL),
		FRAUD_MACK_ADDRESS					("Mack address",7,45, FieldFormatType.SPECIAL, CrmSpecialCharacters.WEBSITE),
		
		//Ticketing System
		TICKET_ID							("ticketId",10,20,FieldFormatType.NUMBER),
		TICKET_MESSAGE						("messageBody",3,300,FieldFormatType.SPECIAL ,CrmSpecialCharacters.SPEICIAL_CHARACTERS_FOR_TICKET),
		TICKET_ASSIGNED_TO					("ticketAsssignedTo",2, 32,  FieldFormatType.ALPHA),
		TICKET_STATUS						("ticketStauts",4,30,FieldFormatType.ALPHA),
		TICKET_TYPE							("ticketType",4,30,FieldFormatType.ALPHA),
		TICKET_SUBJECT						("subject",3,60,FieldFormatType.SPECIAL ,CrmSpecialCharacters.SPEICIAL_CHARACTERS_FOR_TICKET),
		TICKET_MOBILE						("mobileNo",10, 15, FieldFormatType.NUMBER ),
		
		//Response fields
		AUTH_CODE							("authCode", 0, 6, FieldFormatType.ALPHANUM),
		RRN									("rrn", 0, 50, FieldFormatType.ALPHANUM),
		RESPONSE_CODE						("responseCode", 1, 10,  FieldFormatType.ALPHANUM),		
		RESPONSE_MESSAGE					("responseMessage", 1, 256, FieldFormatType.ALPHANUM),
		
		//ORDER INFO
		PG_TXN_MESSAGE						("pg TxnMessage", 1, 255, FieldFormatType.ALPHANUM),
		PG_RESP_CODE						("pg RespCode", 1, 10, FieldFormatType.ALPHANUM),
		PG_REF_NUM							("pg RefNum", 1, 100, FieldFormatType.ALPHANUM),
		PG_DATE_TIME						("pg DateTime", 1, 50, FieldFormatType.SPECIAL),
		TXN_ID								("txn Id", 16, 16,FieldFormatType.NUMBER),
		TXNTYPE								("txn Type", 4, 50, FieldFormatType.ALPHA),
		
		//BIN RANGE
		BIN_CODE							("bin code",6,9,FieldFormatType.NUMBER),
		BIN_RANGE_HIGH						("bin range high",1,24,FieldFormatType.NUMBER),
		BIN_RANGE_LOW						("bin range low",1,24,FieldFormatType.NUMBER),
		CARD_TYPE							("card type",2,30,FieldFormatType.SPECIAL ,CrmSpecialCharacters.CARD_TYPE),
		GROUP_CODE							("group code",1,50,FieldFormatType.ALPHASPACENUM),
		ISSUER_BANK							("issuer bank",1,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.SPEICIAL_CHARACTERS_FOR_TICKET),
		ISSUER_COUNTRY						("issuer country",1,50,FieldFormatType.ALPHASPACE),
		MOP_TYPE							("mop type",2,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.COMMA),
		
		
		//by VJ for additional validations
		
		USER_TYPE                           ("userType",1,200,FieldFormatType.ALPHASPACENUM),
		OPERATION                           ("operation",1,200,FieldFormatType.ALPHASPACENUM),
		FILE_NAME                           ("fileName",1,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		DEST_PATH                           ("destPath",1,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		FILE_NAME_STRUTS                    ("fileNameStruts",1,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
		FILE_FORMAT                         ("fileFormat",1,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
        ACCOUNT_CURRENCY_SET                ("accountCurrencySet",1,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
        RESELLER                            ("reseller",1,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
        SUBJECT								("subject",1,200,FieldFormatType.SPECIAL ,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
        MESSAGE								("Mmessage",1,1500,FieldFormatType.SPECIAL ,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),
        ACQ_ID								("acqId", 1, 100, FieldFormatType.ALPHANUM),
        RFU1								("rfu1", 1, 100, FieldFormatType.ALPHASPACENUM),
        RFU2								("rfu2", 1, 100, FieldFormatType.ALPHASPACENUM),
        MERCHANT_SERVICE_TAX                ("merchantServiceTax", 1, 32, FieldFormatType.AMOUNT),
        CARD_HOLDER_TYPE					("CARD_HOLDER_TYPE", 1, 100, FieldFormatType.ALPHASPACENUM),
        PAYMENTS_REGION						("PAYMENTS_REGION", 1, 100, FieldFormatType.ALPHASPACENUM),
        
		DEFAULT_LANGUAGE                    ("defaultLanguage",1,100,FieldFormatType.ALPHASPACENUM),
		EMAIL_EXPIRY_TIME                   ("emailExpiryTime",2,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.DATE),
		LAST_ACTION_NAME                    ("lastActionName",1,100,FieldFormatType.ALPHASPACENUM),
		MERCHANT_GST_NUMBER                 ("merchantGstNo",1,100,FieldFormatType.ALPHASPACENUM),
		COMPANY_GST_NUMBER                 ("companyGstNo",1,100,FieldFormatType.ALPHASPACENUM),
		UPDATE_DATE                         ("updateDate",2,30,FieldFormatType.SPECIAL,CrmSpecialCharacters.DATE),
		UPDATED_BY			                ("updatedBy",1,100,FieldFormatType.ALPHASPACENUM),
		
		//Validation for Agent Search
		 ACQUIRER_ID						("acquirerId",1,100,FieldFormatType.SPECIAL,CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS),

		 // Charge back @author shubhamchauhan
			CHARGEBACK_ID("chargebackId", 10, 20, FieldFormatType.NUMBER),
			CREATE_DATE("createDate", 2, 30, FieldFormatType.SPECIAL, CrmSpecialCharacters.DATE),
			TARGET_DATE("targetDate", 2, 30, FieldFormatType.SPECIAL, CrmSpecialCharacters.DATE),
			CHARGEBACK_TYPE("chargebackType", 2, 200, FieldFormatType.SPECIAL, CrmSpecialCharacters.SPACE),
			CHARGEBACK_CHAT("chargebackChat", 2, 2147483647, FieldFormatType.SPECIAL, CrmSpecialCharacters.CHARGEBACK_CHAT_SPECIAL_CHAR),
			TXN_DATE("transactionDate", 2, 30, FieldFormatType.SPECIAL, CrmSpecialCharacters.DATE),
			CUST_EMAIL_ID("customerEmail", 5, 100, FieldFormatType.EMAIL),
			CUST_PHONE("customerPhone", 8, 13, FieldFormatType.NUMBER),
			CASE_ID("caseId", 1, 100, FieldFormatType.SPECIAL, CrmSpecialCharacters.ORDER_ID), // Special characters are same as ORDER_ID,
		// Chargeback fields : end
			
			// Fields missing in UserDao.java responseUser map
			TRANSACTION_SMS 			("transactionSms",1, 500, FieldFormatType.SPECIAL, CrmSpecialCharacters.COMMA),
			PAYMENT_MESSAGE_SLAB 		("paymentMessageSlab",1,2, FieldFormatType.ALPHANUM),
			CARD_SAVE_PARAM				("cardSaveParam",1, 50, FieldFormatType.SPECIAL, CrmSpecialCharacters.UNDERSCORE),
			WHITE_LABEL_URL		("whiteLabelUrl", 1, 250, FieldFormatType.SPECIAL, CrmSpecialCharacters.WHITE_LABEL_URL),
			
			//Validation for Beneficiary
			BENE_MER_ID 				("BENE_MER_ID", 1, 50, FieldFormatType.ALPHANUM),
			BENEFICIARY_CD              ("BENEFICIARY_CD", 1, 32, FieldFormatType.ALPHANUM),
			BENE_TYPE					("BENE_TYPE", 1, 1, FieldFormatType.ALPHA),//enum
			BENE_NAME					("BENE_NAME", 1, 255,  FieldFormatType.ALPHASPACE),
			BENE_EXPIRY_DATE			("EXPIRY_DATE", 1, 50,  FieldFormatType.SPECIAL,CrmSpecialCharacters.BENE_EXPIRY_DATE),//DOUBT
			CURRENCY_CD 				("CURRENCY_CD",3,3, FieldFormatType.ALPHA),
			BENE_TRANSACTION_LIMIT		("TRANSACTION_LIMIT",1,8, FieldFormatType.NUMBER),
			BENE_BANK_NAME				("BANK_NAME", 1, 128, FieldFormatType.SPECIAL,CrmSpecialCharacters.BENE_BANK_NAME),//DOUBT
			BENE_NODAL_ACQUIRER			("NODAL_ACQUIRER", 1, 250, FieldFormatType.ALPHA),
			BENE_ADDRESS_1				("ADDRESS_1", 1, 128, FieldFormatType.SPECIAL,  CrmSpecialCharacters.BENE_ADDRESS),
			BENE_ADDRESS_2				("ADDRESS_2", 1, 128, FieldFormatType.SPECIAL, CrmSpecialCharacters.BENE_ADDRESS),
			BENE_MOBILE_NUMBER			("MOBILE_NUMBER", 10, 16, FieldFormatType.NUMBER),
			BENE_EMAIL_ID				("EMAIL_ID", 3, 128, FieldFormatType.EMAIL),
			BENE_ACCOUNT_NO				("BENE_ACCOUNT_NO", 1, 32, FieldFormatType.NUMBER),
			BENE_COMMENT				("BENE_COMMENT", 2, 150, FieldFormatType.SPECIAL,CrmSpecialCharacters.BENE_COMMENTS),
			BENE_AMOUNT					("AMOUNT", 1, 10, FieldFormatType.AMOUNT),
			BENE_STATUS					("STATUS_TYPE", 1, 10, FieldFormatType.ALPHA),
			OID							("OID", 16, 16, FieldFormatType.NUMBER),
			BENE_AADHAR_NO				("AADHAR_NO", 12, 12, FieldFormatType.NUMBER),
			BENE_MERCHANT_PROVIDED_ID	("MERCHANT_PROVIDED_ID", 1, 32,FieldFormatType.ALPHANUM),
			BENE_MERCHANT_PROVIDED_NAME	("MERCHANT_PROVIDED_NAME", 1, 32,FieldFormatType.ALPHANUM),
			BENE_MERCHANT_BUSINESS_NAME	("MERCHANT_BUSINESS_NAME", 1, 32, FieldFormatType.ALPHANUM),
			MENU_ID						("id", 1, 10, FieldFormatType.NUMBER),
			MENU_NAME					("menuName", 1, 200, FieldFormatType.ALPHANUM),
			PARENT_ID					("id", 1, 10, FieldFormatType.NUMBER),
			ROLE_NAME					("roleName", 1, 200, FieldFormatType.ALPHANUM),
				
		
	//added by  Sweety on resellerdailyupdate
	IS_DELETED					("IsDeleted", 1, 10, FieldFormatType.NUMBER),
	TOTAL_SUCCESS_TRANSACTION               ("TotatlSuccessTransaction", 1, 200, FieldFormatType.NUMBER),
	TOTAL_REFUND               ("TotalRefund", 1, 200, FieldFormatType.AMOUNT),
	TOTAL_CHARGEBACK               ("TotalChargeback", 1, 200, FieldFormatType.AMOUNT),
	SETTLEMENT_STATUS               ("SettlementStatus", 1, 200, FieldFormatType.AMOUNT),
	SALE_AMOUNT               ("saleamount", 1, 200, FieldFormatType.AMOUNT),
	RESELLER_COMMISION               ("resellercommision", 1, 200, FieldFormatType.AMOUNT),
	SMA_COMMISSION                   ("smacommission", 1, 200, FieldFormatType.AMOUNT),
	MA_COMMISSION                   ("macommission", 1, 200, FieldFormatType.AMOUNT),
	AGENT_COMMISSION                   ("agentcommission", 1, 200, FieldFormatType.AMOUNT),
	SMA_ID				("smaId", 10,20,FieldFormatType.NUMBER),
	MA_ID				("maId", 10,20,FieldFormatType.NUMBER),
	AGENT_ID				("agentId", 10,20,FieldFormatType.NUMBER)
	
;	
		
		private final String name;
		private final int minLength;
		private final int maxLength;
		private final FieldFormatType type;
		private final CrmSpecialCharacters specialChars;
		
		
		private CrmFieldType(String name,int minLength,int maxLength, FieldFormatType type, CrmSpecialCharacters specialChars)
		{
			this.name = name;
			this.minLength = minLength;
			this.maxLength = maxLength;
			this.type = type;
			this.specialChars = specialChars;
		}
		
		private CrmFieldType(String name, int minLength, int maxLength, FieldFormatType type)
		{
			this.name = name;
			this.minLength = minLength;
			this.maxLength = maxLength;
			this.type = type;
			this.specialChars=null;
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
		public FieldFormatType getType(){
			return type;
		}

		public CrmSpecialCharacters getSpecialChars() {
			return specialChars;
		}
	}
