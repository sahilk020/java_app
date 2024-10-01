package com.pay10.commons.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportingCollection {
		
	   private String _id;
	    @JsonProperty("TXN_ID") 
	    private String tXN_ID;
	    @JsonProperty("RECO_TXNTYPE") 
	    private String rECO_TXNTYPE;
	    @JsonProperty("FILE_NAME") 
	    private String fILE_NAME;
	    @JsonProperty("FILE_LINE_NO") 
	    private String fILE_LINE_NO;
	    @JsonProperty("FILE_LINE_DATA") 
	    private String fILE_LINE_DATA;
	    @JsonProperty("DB_TXN_ID") 
	    private String dB_TXN_ID;
	    @JsonProperty("DB_TXNTYPE") 
	    private String dB_TXNTYPE;
	    @JsonProperty("DB_OID") 
	    private String dB_OID;
	    @JsonProperty("DB_ACQ_ID") 
	    private String dB_ACQ_ID;
	    @JsonProperty("DB_ORIG_TXN_ID") 
	    private String dB_ORIG_TXN_ID;
	    @JsonProperty("DB_ORIG_TXNTYPE") 
	    private String dB_ORIG_TXNTYPE;
	    @JsonProperty("DB_AMOUNT") 
	    private String dB_AMOUNT;
	    @JsonProperty("DB_PG_REF_NUM") 
	    private String dB_PG_REF_NUM;
	    @JsonProperty("DB_ORDER_ID") 
	    private String dB_ORDER_ID;
	    @JsonProperty("DB_PAY_ID") 
	    private String dB_PAY_ID;
	    @JsonProperty("DB_ACQUIRER_TYPE") 
	    private String dB_ACQUIRER_TYPE;
	    @JsonProperty("CREATE_DATE") 
	    private String cREATE_DATE;
	    @JsonProperty("UPDATE_DATE") 
	    private String uPDATE_DATE;
	    @JsonProperty("RESPONSE_CODE") 
	    private String rESPONSE_CODE;
	    @JsonProperty("RESPONSE_MESSAGE") 
	    private String rESPONSE_MESSAGE;
	    @JsonProperty("DB_USER_TYPE") 
	    private String dB_USER_TYPE;
	    @JsonProperty("RECO_EXCEPTION_STATUS") 
	    private String rECO_EXCEPTION_STATUS;
	    private String srNo;
		public String get_id() {
			return _id;
		}
		public void set_id(String _id) {
			this._id = _id;
		}
		public String gettXN_ID() {
			return tXN_ID;
		}
		public void settXN_ID(String tXN_ID) {
			this.tXN_ID = tXN_ID;
		}
		public String getrECO_TXNTYPE() {
			return rECO_TXNTYPE;
		}
		public void setrECO_TXNTYPE(String rECO_TXNTYPE) {
			this.rECO_TXNTYPE = rECO_TXNTYPE;
		}
		public String getfILE_NAME() {
			return fILE_NAME;
		}
		public void setfILE_NAME(String fILE_NAME) {
			this.fILE_NAME = fILE_NAME;
		}
		public String getfILE_LINE_NO() {
			return fILE_LINE_NO;
		}
		public void setfILE_LINE_NO(String fILE_LINE_NO) {
			this.fILE_LINE_NO = fILE_LINE_NO;
		}
		public String getfILE_LINE_DATA() {
			return fILE_LINE_DATA;
		}
		public void setfILE_LINE_DATA(String fILE_LINE_DATA) {
			this.fILE_LINE_DATA = fILE_LINE_DATA;
		}
		public String getdB_TXN_ID() {
			return dB_TXN_ID;
		}
		public void setdB_TXN_ID(String dB_TXN_ID) {
			this.dB_TXN_ID = dB_TXN_ID;
		}
		public String getdB_TXNTYPE() {
			return dB_TXNTYPE;
		}
		public void setdB_TXNTYPE(String dB_TXNTYPE) {
			this.dB_TXNTYPE = dB_TXNTYPE;
		}
		public String getdB_OID() {
			return dB_OID;
		}
		public void setdB_OID(String dB_OID) {
			this.dB_OID = dB_OID;
		}
		public String getdB_ACQ_ID() {
			return dB_ACQ_ID;
		}
		public void setdB_ACQ_ID(String dB_ACQ_ID) {
			this.dB_ACQ_ID = dB_ACQ_ID;
		}
		public String getdB_ORIG_TXN_ID() {
			return dB_ORIG_TXN_ID;
		}
		public void setdB_ORIG_TXN_ID(String dB_ORIG_TXN_ID) {
			this.dB_ORIG_TXN_ID = dB_ORIG_TXN_ID;
		}
		public String getdB_ORIG_TXNTYPE() {
			return dB_ORIG_TXNTYPE;
		}
		public void setdB_ORIG_TXNTYPE(String dB_ORIG_TXNTYPE) {
			this.dB_ORIG_TXNTYPE = dB_ORIG_TXNTYPE;
		}
		public String getdB_AMOUNT() {
			return dB_AMOUNT;
		}
		public void setdB_AMOUNT(String dB_AMOUNT) {
			this.dB_AMOUNT = dB_AMOUNT;
		}
		public String getdB_PG_REF_NUM() {
			return dB_PG_REF_NUM;
		}
		public void setdB_PG_REF_NUM(String dB_PG_REF_NUM) {
			this.dB_PG_REF_NUM = dB_PG_REF_NUM;
		}
		public String getdB_ORDER_ID() {
			return dB_ORDER_ID;
		}
		public void setdB_ORDER_ID(String dB_ORDER_ID) {
			this.dB_ORDER_ID = dB_ORDER_ID;
		}
		public String getdB_PAY_ID() {
			return dB_PAY_ID;
		}
		public void setdB_PAY_ID(String dB_PAY_ID) {
			this.dB_PAY_ID = dB_PAY_ID;
		}
		public String getdB_ACQUIRER_TYPE() {
			return dB_ACQUIRER_TYPE;
		}
		public void setdB_ACQUIRER_TYPE(String dB_ACQUIRER_TYPE) {
			this.dB_ACQUIRER_TYPE = dB_ACQUIRER_TYPE;
		}
		public String getcREATE_DATE() {
			return cREATE_DATE;
		}
		public void setcREATE_DATE(String cREATE_DATE) {
			this.cREATE_DATE = cREATE_DATE;
		}
		public String getuPDATE_DATE() {
			return uPDATE_DATE;
		}
		public void setuPDATE_DATE(String uPDATE_DATE) {
			this.uPDATE_DATE = uPDATE_DATE;
		}
		public String getrESPONSE_CODE() {
			return rESPONSE_CODE;
		}
		public void setrESPONSE_CODE(String rESPONSE_CODE) {
			this.rESPONSE_CODE = rESPONSE_CODE;
		}
		public String getrESPONSE_MESSAGE() {
			return rESPONSE_MESSAGE;
		}
		public void setrESPONSE_MESSAGE(String rESPONSE_MESSAGE) {
			this.rESPONSE_MESSAGE = rESPONSE_MESSAGE;
		}
		public String getdB_USER_TYPE() {
			return dB_USER_TYPE;
		}
		public void setdB_USER_TYPE(String dB_USER_TYPE) {
			this.dB_USER_TYPE = dB_USER_TYPE;
		}
		public String getrECO_EXCEPTION_STATUS() {
			return rECO_EXCEPTION_STATUS;
		}
		public void setrECO_EXCEPTION_STATUS(String rECO_EXCEPTION_STATUS) {
			this.rECO_EXCEPTION_STATUS = rECO_EXCEPTION_STATUS;
		}
	    
		public String getSrNo() {
			return srNo;
		}
		public void setSrNo(String srNo) {
			this.srNo = srNo;
		}
		
		public Object[] myCsvMethod() {
			  Object[] objectArray = new Object[23];
			  
			 
			  objectArray[0] = srNo;
			  objectArray[1] = tXN_ID;
			  objectArray[2] = rECO_TXNTYPE;
			  objectArray[3] = fILE_NAME;
			  objectArray[4] = fILE_LINE_NO;
			  objectArray[5] = fILE_LINE_DATA;
			  objectArray[6] = dB_TXN_ID;
			  objectArray[7] = dB_TXNTYPE;
			  objectArray[8] = dB_OID;
			  objectArray[9] = dB_ACQ_ID;
			  objectArray[10] = dB_ORIG_TXN_ID;
			  objectArray[11] = dB_ORIG_TXNTYPE;
			  objectArray[12] = dB_AMOUNT;
			  objectArray[13] = dB_PG_REF_NUM;
			  objectArray[14] = dB_ORDER_ID;
			  objectArray[15] = dB_PAY_ID;
			  objectArray[16] = dB_ACQUIRER_TYPE;
			  objectArray[17] = cREATE_DATE;
			  objectArray[18] = uPDATE_DATE;
			  objectArray[19] = rESPONSE_CODE;
			  objectArray[20] = rESPONSE_MESSAGE;
			  objectArray[21] = dB_USER_TYPE;
			  objectArray[22] = rECO_EXCEPTION_STATUS;
			 
			
			  return objectArray;
			}
}
