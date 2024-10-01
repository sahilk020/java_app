package com.pay10.crm.actionBeans;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionStatusBean {
	private String _id;

	
    private double AMOUNT;

    private String ORIG_TXN_ID;

    private String ORIG_TXNTYPE;

    private String PG_REF_NUM;

    private String ACCT_ID;

    private String ACQ_ID;

    private String OID;

    private String CARD_MASK;

    private String TXN_ID;

    private String TXNTYPE;

    private String CUST_NAME;

    private String ORDER_ID;

    private String PAY_ID;

    private String MOP_TYPE;

    private String CURRENCY_CODE;

    private String STATUS;

    private String RESPONSE_CODE;

    private String RESPONSE_MESSAGE;

    private String CUST_EMAIL;

    private String PAYMENT_TYPE;

    private String ACQUIRER_TYPE;

    private String PRODUCT_DESC;

    private String AUTH_CODE;

    private String PG_DATE_TIME;

    private String PG_RESP_CODE;

    private String PG_TXN_MESSAGE;

    private String INTERNAL_CUST_IP;

    private String INTERNAL_CARD_ISSUER_BANK;

    private String INTERNAL_CARD_ISSUER_COUNTRY;

    private String INTERNAL_USER_EMAIL;

    private String INTERNAL_CUST_COUNTRY_NAME;

    private String RRN;

    private String INTERNAL_REQUEST_FIELDS;

    private String SURCHARGE_FLAG;

    private String CREATE_DATE;

    private String UPDATE_DATE;

    private String REFUND_FLAG;

    private String REFUND_ORDER_ID;

    private INSERTION_DATE INSERTION_DATE;

    private String REQUEST_DATE;

    private String SRC_ACCOUNT_NO;

    private String BENE_ACCOUNT_NO;

    private String BENE_NAME;

    private String BENEFICIARY_CD;

    private String DATE_INDEX;

    private double PG_TDR_SC;

    private double ACQUIRER_TDR_SC;

    private double PG_GST;

    private double ACQUIRER_GST;

    private String CUST_ID;

    private String CARD_HOLDER_NAME;

    private String INVOICE_ID;

    private String RESELLER_ID;

    private double RESELLER_CHARGES;

    private double RESELLER_GST;

    private String ALIAS_STATUS;

    private String CARD_HOLDER_TYPE;

    private String PAYMENTS_REGION;

    private String SETTLEMENT_DATE;

    private String SETTLEMENT_DATE_INDEX;

    private String SETTLEMENT_FLAG;

    private double SURCHARGE_AMOUNT;

    private double TOTAL_AMOUNT;

    private String UDF1;

    private String UDF2;

    private String UDF3;

    private String UDF4;

    private String UDF5;

    private String UDF6;
    
    private String netSettleAmount;
    
    private String businessName;
    
    

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getNetSettleAmount() {
		return netSettleAmount;
	}

	public void setNetSettleAmount(String netSettleAmount) {
		this.netSettleAmount = netSettleAmount;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public double getAMOUNT() {
		return AMOUNT;
	}

	public void setAMOUNT(double aMOUNT) {
		AMOUNT = aMOUNT;
	}

	public String getORIG_TXN_ID() {
		return ORIG_TXN_ID;
	}

	public void setORIG_TXN_ID(String oRIG_TXN_ID) {
		ORIG_TXN_ID = oRIG_TXN_ID;
	}

	public String getORIG_TXNTYPE() {
		return ORIG_TXNTYPE;
	}

	public void setORIG_TXNTYPE(String oRIG_TXNTYPE) {
		ORIG_TXNTYPE = oRIG_TXNTYPE;
	}

	public String getPG_REF_NUM() {
		return PG_REF_NUM;
	}

	public void setPG_REF_NUM(String pG_REF_NUM) {
		PG_REF_NUM = pG_REF_NUM;
	}

	public String getACCT_ID() {
		return ACCT_ID;
	}

	public void setACCT_ID(String aCCT_ID) {
		ACCT_ID = aCCT_ID;
	}

	public String getACQ_ID() {
		return ACQ_ID;
	}

	public void setACQ_ID(String aCQ_ID) {
		ACQ_ID = aCQ_ID;
	}

	public String getOID() {
		return OID;
	}

	public void setOID(String oID) {
		OID = oID;
	}

	public String getCARD_MASK() {
		return CARD_MASK;
	}

	public void setCARD_MASK(String cARD_MASK) {
		CARD_MASK = cARD_MASK;
	}

	public String getTXN_ID() {
		return TXN_ID;
	}

	public void setTXN_ID(String tXN_ID) {
		TXN_ID = tXN_ID;
	}

	public String getTXNTYPE() {
		return TXNTYPE;
	}

	public void setTXNTYPE(String tXNTYPE) {
		TXNTYPE = tXNTYPE;
	}

	public String getCUST_NAME() {
		return CUST_NAME;
	}

	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public String getORDER_ID() {
		return ORDER_ID;
	}

	public void setORDER_ID(String oRDER_ID) {
		ORDER_ID = oRDER_ID;
	}

	public String getPAY_ID() {
		return PAY_ID;
	}

	public void setPAY_ID(String pAY_ID) {
		PAY_ID = pAY_ID;
	}

	public String getMOP_TYPE() {
		return MOP_TYPE;
	}

	public void setMOP_TYPE(String mOP_TYPE) {
		MOP_TYPE = mOP_TYPE;
	}

	public String getCURRENCY_CODE() {
		return CURRENCY_CODE;
	}

	public void setCURRENCY_CODE(String cURRENCY_CODE) {
		CURRENCY_CODE = cURRENCY_CODE;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getRESPONSE_CODE() {
		return RESPONSE_CODE;
	}

	public void setRESPONSE_CODE(String rESPONSE_CODE) {
		RESPONSE_CODE = rESPONSE_CODE;
	}

	public String getRESPONSE_MESSAGE() {
		return RESPONSE_MESSAGE;
	}

	public void setRESPONSE_MESSAGE(String rESPONSE_MESSAGE) {
		RESPONSE_MESSAGE = rESPONSE_MESSAGE;
	}

	public String getCUST_EMAIL() {
		return CUST_EMAIL;
	}

	public void setCUST_EMAIL(String cUST_EMAIL) {
		CUST_EMAIL = cUST_EMAIL;
	}

	public String getPAYMENT_TYPE() {
		return PAYMENT_TYPE;
	}

	public void setPAYMENT_TYPE(String pAYMENT_TYPE) {
		PAYMENT_TYPE = pAYMENT_TYPE;
	}

	public String getACQUIRER_TYPE() {
		return ACQUIRER_TYPE;
	}

	public void setACQUIRER_TYPE(String aCQUIRER_TYPE) {
		ACQUIRER_TYPE = aCQUIRER_TYPE;
	}

	public String getPRODUCT_DESC() {
		return PRODUCT_DESC;
	}

	public void setPRODUCT_DESC(String pRODUCT_DESC) {
		PRODUCT_DESC = pRODUCT_DESC;
	}

	public String getAUTH_CODE() {
		return AUTH_CODE;
	}

	public void setAUTH_CODE(String aUTH_CODE) {
		AUTH_CODE = aUTH_CODE;
	}

	public String getPG_DATE_TIME() {
		return PG_DATE_TIME;
	}

	public void setPG_DATE_TIME(String pG_DATE_TIME) {
		PG_DATE_TIME = pG_DATE_TIME;
	}

	public String getPG_RESP_CODE() {
		return PG_RESP_CODE;
	}

	public void setPG_RESP_CODE(String pG_RESP_CODE) {
		PG_RESP_CODE = pG_RESP_CODE;
	}

	public String getPG_TXN_MESSAGE() {
		return PG_TXN_MESSAGE;
	}

	public void setPG_TXN_MESSAGE(String pG_TXN_MESSAGE) {
		PG_TXN_MESSAGE = pG_TXN_MESSAGE;
	}

	public String getINTERNAL_CUST_IP() {
		return INTERNAL_CUST_IP;
	}

	public void setINTERNAL_CUST_IP(String iNTERNAL_CUST_IP) {
		INTERNAL_CUST_IP = iNTERNAL_CUST_IP;
	}

	public String getINTERNAL_CARD_ISSUER_BANK() {
		return INTERNAL_CARD_ISSUER_BANK;
	}

	public void setINTERNAL_CARD_ISSUER_BANK(String iNTERNAL_CARD_ISSUER_BANK) {
		INTERNAL_CARD_ISSUER_BANK = iNTERNAL_CARD_ISSUER_BANK;
	}

	public String getINTERNAL_CARD_ISSUER_COUNTRY() {
		return INTERNAL_CARD_ISSUER_COUNTRY;
	}

	public void setINTERNAL_CARD_ISSUER_COUNTRY(String iNTERNAL_CARD_ISSUER_COUNTRY) {
		INTERNAL_CARD_ISSUER_COUNTRY = iNTERNAL_CARD_ISSUER_COUNTRY;
	}

	public String getINTERNAL_USER_EMAIL() {
		return INTERNAL_USER_EMAIL;
	}

	public void setINTERNAL_USER_EMAIL(String iNTERNAL_USER_EMAIL) {
		INTERNAL_USER_EMAIL = iNTERNAL_USER_EMAIL;
	}

	public String getINTERNAL_CUST_COUNTRY_NAME() {
		return INTERNAL_CUST_COUNTRY_NAME;
	}

	public void setINTERNAL_CUST_COUNTRY_NAME(String iNTERNAL_CUST_COUNTRY_NAME) {
		INTERNAL_CUST_COUNTRY_NAME = iNTERNAL_CUST_COUNTRY_NAME;
	}

	public String getRRN() {
		return RRN;
	}

	public void setRRN(String rRN) {
		RRN = rRN;
	}

	public String getINTERNAL_REQUEST_FIELDS() {
		return INTERNAL_REQUEST_FIELDS;
	}

	public void setINTERNAL_REQUEST_FIELDS(String iNTERNAL_REQUEST_FIELDS) {
		INTERNAL_REQUEST_FIELDS = iNTERNAL_REQUEST_FIELDS;
	}

	public String getSURCHARGE_FLAG() {
		return SURCHARGE_FLAG;
	}

	public void setSURCHARGE_FLAG(String sURCHARGE_FLAG) {
		SURCHARGE_FLAG = sURCHARGE_FLAG;
	}

	public String getCREATE_DATE() {
		return CREATE_DATE;
	}

	public void setCREATE_DATE(String cREATE_DATE) {
		CREATE_DATE = cREATE_DATE;
	}

	public String getUPDATE_DATE() {
		return UPDATE_DATE;
	}

	public void setUPDATE_DATE(String uPDATE_DATE) {
		UPDATE_DATE = uPDATE_DATE;
	}

	public String getREFUND_FLAG() {
		return REFUND_FLAG;
	}

	public void setREFUND_FLAG(String rEFUND_FLAG) {
		REFUND_FLAG = rEFUND_FLAG;
	}

	public String getREFUND_ORDER_ID() {
		return REFUND_ORDER_ID;
	}

	public void setREFUND_ORDER_ID(String rEFUND_ORDER_ID) {
		REFUND_ORDER_ID = rEFUND_ORDER_ID;
	}

	public INSERTION_DATE getINSERTION_DATE() {
		return INSERTION_DATE;
	}

	public void setINSERTION_DATE(INSERTION_DATE iNSERTION_DATE) {
		INSERTION_DATE = iNSERTION_DATE;
	}

	public String getREQUEST_DATE() {
		return REQUEST_DATE;
	}

	public void setREQUEST_DATE(String rEQUEST_DATE) {
		REQUEST_DATE = rEQUEST_DATE;
	}

	public String getSRC_ACCOUNT_NO() {
		return SRC_ACCOUNT_NO;
	}

	public void setSRC_ACCOUNT_NO(String sRC_ACCOUNT_NO) {
		SRC_ACCOUNT_NO = sRC_ACCOUNT_NO;
	}

	public String getBENE_ACCOUNT_NO() {
		return BENE_ACCOUNT_NO;
	}

	public void setBENE_ACCOUNT_NO(String bENE_ACCOUNT_NO) {
		BENE_ACCOUNT_NO = bENE_ACCOUNT_NO;
	}

	public String getBENE_NAME() {
		return BENE_NAME;
	}

	public void setBENE_NAME(String bENE_NAME) {
		BENE_NAME = bENE_NAME;
	}

	public String getBENEFICIARY_CD() {
		return BENEFICIARY_CD;
	}

	public void setBENEFICIARY_CD(String bENEFICIARY_CD) {
		BENEFICIARY_CD = bENEFICIARY_CD;
	}

	public String getDATE_INDEX() {
		return DATE_INDEX;
	}

	public void setDATE_INDEX(String dATE_INDEX) {
		DATE_INDEX = dATE_INDEX;
	}

	public double getPG_TDR_SC() {
		return PG_TDR_SC;
	}

	public void setPG_TDR_SC(double pG_TDR_SC) {
		PG_TDR_SC = pG_TDR_SC;
	}

	public double getACQUIRER_TDR_SC() {
		return ACQUIRER_TDR_SC;
	}

	public void setACQUIRER_TDR_SC(double aCQUIRER_TDR_SC) {
		ACQUIRER_TDR_SC = aCQUIRER_TDR_SC;
	}

	public double getPG_GST() {
		return PG_GST;
	}

	public void setPG_GST(double pG_GST) {
		PG_GST = pG_GST;
	}

	public double getACQUIRER_GST() {
		return ACQUIRER_GST;
	}

	public void setACQUIRER_GST(double aCQUIRER_GST) {
		ACQUIRER_GST = aCQUIRER_GST;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getCARD_HOLDER_NAME() {
		return CARD_HOLDER_NAME;
	}

	public void setCARD_HOLDER_NAME(String cARD_HOLDER_NAME) {
		CARD_HOLDER_NAME = cARD_HOLDER_NAME;
	}

	public String getINVOICE_ID() {
		return INVOICE_ID;
	}

	public void setINVOICE_ID(String iNVOICE_ID) {
		INVOICE_ID = iNVOICE_ID;
	}

	public String getRESELLER_ID() {
		return RESELLER_ID;
	}

	public void setRESELLER_ID(String rESELLER_ID) {
		RESELLER_ID = rESELLER_ID;
	}

	public double getRESELLER_CHARGES() {
		return RESELLER_CHARGES;
	}

	public void setRESELLER_CHARGES(double rESELLER_CHARGES) {
		RESELLER_CHARGES = rESELLER_CHARGES;
	}

	public double getRESELLER_GST() {
		return RESELLER_GST;
	}

	public void setRESELLER_GST(double rESELLER_GST) {
		RESELLER_GST = rESELLER_GST;
	}

	public String getALIAS_STATUS() {
		return ALIAS_STATUS;
	}

	public void setALIAS_STATUS(String aLIAS_STATUS) {
		ALIAS_STATUS = aLIAS_STATUS;
	}

	public String getCARD_HOLDER_TYPE() {
		return CARD_HOLDER_TYPE;
	}

	public void setCARD_HOLDER_TYPE(String cARD_HOLDER_TYPE) {
		CARD_HOLDER_TYPE = cARD_HOLDER_TYPE;
	}

	public String getPAYMENTS_REGION() {
		return PAYMENTS_REGION;
	}

	public void setPAYMENTS_REGION(String pAYMENTS_REGION) {
		PAYMENTS_REGION = pAYMENTS_REGION;
	}

	public String getSETTLEMENT_DATE() {
		return SETTLEMENT_DATE;
	}

	public void setSETTLEMENT_DATE(String sETTLEMENT_DATE) {
		SETTLEMENT_DATE = sETTLEMENT_DATE;
	}

	public String getSETTLEMENT_DATE_INDEX() {
		return SETTLEMENT_DATE_INDEX;
	}

	public void setSETTLEMENT_DATE_INDEX(String sETTLEMENT_DATE_INDEX) {
		SETTLEMENT_DATE_INDEX = sETTLEMENT_DATE_INDEX;
	}

	public String getSETTLEMENT_FLAG() {
		return SETTLEMENT_FLAG;
	}

	public void setSETTLEMENT_FLAG(String sETTLEMENT_FLAG) {
		SETTLEMENT_FLAG = sETTLEMENT_FLAG;
	}

	public double getSURCHARGE_AMOUNT() {
		return SURCHARGE_AMOUNT;
	}

	public void setSURCHARGE_AMOUNT(double sURCHARGE_AMOUNT) {
		SURCHARGE_AMOUNT = sURCHARGE_AMOUNT;
	}

	public double getTOTAL_AMOUNT() {
		return TOTAL_AMOUNT;
	}

	public void setTOTAL_AMOUNT(double tOTAL_AMOUNT) {
		TOTAL_AMOUNT = tOTAL_AMOUNT;
	}

	public String getUDF1() {
		return UDF1;
	}

	public void setUDF1(String uDF1) {
		UDF1 = uDF1;
	}

	public String getUDF2() {
		return UDF2;
	}

	public void setUDF2(String uDF2) {
		UDF2 = uDF2;
	}

	public String getUDF3() {
		return UDF3;
	}

	public void setUDF3(String uDF3) {
		UDF3 = uDF3;
	}

	public String getUDF4() {
		return UDF4;
	}

	public void setUDF4(String uDF4) {
		UDF4 = uDF4;
	}

	public String getUDF5() {
		return UDF5;
	}

	public void setUDF5(String uDF5) {
		UDF5 = uDF5;
	}

	public String getUDF6() {
		return UDF6;
	}

	public void setUDF6(String uDF6) {
		UDF6 = uDF6;
	}

    
		
}
