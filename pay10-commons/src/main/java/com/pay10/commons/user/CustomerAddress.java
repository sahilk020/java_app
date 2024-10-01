package com.pay10.commons.user;

import java.io.Serializable;



public class CustomerAddress implements Serializable {

	private static final long serialVersionUID = -213386584633098489L;

	private String custName;
	private String cardHolderName;
	private String custPhone;
	private String custStreetAddress1;
	private String custStreetAddress2;
	private String custCity;
	private String custState;
	private String custCountry;
	private String custZip;
	private String custShipName;
	private String custShipStreetAddress1;
	private String custShipStreetAddress2;
	private String custShipCity;
	private String custShipPhone;
	private String custShipState;
	private String custShipCountry;
	private String custShipZip;
	private String internalTxnAuthentication;
	
	private String cardMask;
	private String acquirerType;
	private String pgTdr;
	private String pgGst;
	private String acquirerTdr;
	private String acquirerGst;
	private String issuer;
	private String pgTxnMsg;
	private String mopType;
	private String udf6;
	
	public CustomerAddress() {

	}
	
	public String getUdf6() {
		return udf6;
	}

	public void setUdf6(String udf6) {
		this.udf6 = udf6;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getCustPhone() {
		return custPhone;
	}

	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}

	public String getCustStreetAddress1() {
		return custStreetAddress1;
	}

	public void setCustStreetAddress1(String custStreetAddress1) {
		this.custStreetAddress1 = custStreetAddress1;
	}

	public String getCustStreetAddress2() {
		return custStreetAddress2;
	}

	public void setCustStreetAddress2(String custStreetAddress2) {
		this.custStreetAddress2 = custStreetAddress2;
	}

	public String getCustCity() {
		return custCity;
	}

	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}

	public String getCustState() {
		return custState;
	}

	public void setCustState(String custState) {
		this.custState = custState;
	}

	public String getCustCountry() {
		return custCountry;
	}

	public void setCustCountry(String custCountry) {
		this.custCountry = custCountry;
	}

	public String getCustZip() {
		return custZip;
	}

	public void setCustZip(String custZip) {
		this.custZip = custZip;
	}

	public String getCustShipStreetAddress1() {
		return custShipStreetAddress1;
	}

	public void setCustShipStreetAddress1(String custShipStreetAddress1) {
		this.custShipStreetAddress1 = custShipStreetAddress1;
	}

	public String getCustShipStreetAddress2() {
		return custShipStreetAddress2;
	}

	public void setCustShipStreetAddress2(String custShipStreetAddress2) {
		this.custShipStreetAddress2 = custShipStreetAddress2;
	}

	public String getCustShipCity() {
		return custShipCity;
	}

	public void setCustShipCity(String custShipCity) {
		this.custShipCity = custShipCity;
	}
	
	public String getCustShipPhone() {
		return custShipPhone;
	}

	public void setCustShipPhone(String custShipPhone) {
		this.custShipPhone = custShipPhone;
	}
	

	public String getCustShipState() {
		return custShipState;
	}

	public void setCustShipState(String custShipState) {
		this.custShipState = custShipState;
	}

	public String getCustShipCountry() {
		return custShipCountry;
	}

	public void setCustShipCountry(String custShipCountry) {
		this.custShipCountry = custShipCountry;
	}

	public String getCustShipZip() {
		return custShipZip;
	}

	public void setCustShipZip(String custShipZip) {
		this.custShipZip = custShipZip;
	}

	public String getInternalTxnAuthentication() {
		return internalTxnAuthentication;
	}

	public void setInternalTxnAuthentication(String internalTxnAuthentication) {
		this.internalTxnAuthentication = internalTxnAuthentication;
	}

	public String getCustShipName() {
		return custShipName;
	}

	public void setCustShipName(String custShipName) {
		this.custShipName = custShipName;
	}

	public String getCardMask() {
		return cardMask;
	}

	public void setCardMask(String cardMask) {
		this.cardMask = cardMask;
	}

	public String getAcquirerType() {
		return acquirerType;
	}

	public void setAcquirerType(String acquirerType) {
		this.acquirerType = acquirerType;
	}

	public String getPgTdr() {
		return pgTdr;
	}

	public void setPgTdr(String pgTdr) {
		this.pgTdr = pgTdr;
	}

	public String getPgGst() {
		return pgGst;
	}

	public void setPgGst(String pgGst) {
		this.pgGst = pgGst;
	}

	public String getAcquirerTdr() {
		return acquirerTdr;
	}

	public void setAcquirerTdr(String acquirerTdr) {
		this.acquirerTdr = acquirerTdr;
	}

	public String getAcquirerGst() {
		return acquirerGst;
	}

	public void setAcquirerGst(String acquirerGst) {
		this.acquirerGst = acquirerGst;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getPgTxnMsg() {
		return pgTxnMsg;
	}

	public void setPgTxnMsg(String pgTxnMsg) {
		this.pgTxnMsg = pgTxnMsg;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	@Override
	public String toString() {
		return "CustomerAddress [custName=" + custName + ", cardHolderName=" + cardHolderName + ", custPhone="
				+ custPhone + ", custStreetAddress1=" + custStreetAddress1 + ", custStreetAddress2="
				+ custStreetAddress2 + ", custCity=" + custCity + ", custState=" + custState + ", custCountry="
				+ custCountry + ", custZip=" + custZip + ", custShipName=" + custShipName + ", custShipStreetAddress1="
				+ custShipStreetAddress1 + ", custShipStreetAddress2=" + custShipStreetAddress2 + ", custShipCity="
				+ custShipCity + ", custShipPhone=" + custShipPhone + ", custShipState=" + custShipState
				+ ", custShipCountry=" + custShipCountry + ", custShipZip=" + custShipZip
				+ ", internalTxnAuthentication=" + internalTxnAuthentication + ", cardMask=" + cardMask
				+ ", acquirerType=" + acquirerType + ", pgTdr=" + pgTdr + ", pgGst=" + pgGst + ", acquirerTdr="
				+ acquirerTdr + ", acquirerGst=" + acquirerGst + ", issuer=" + issuer + ", pgTxnMsg=" + pgTxnMsg
				+ ", mopType=" + mopType + "]";
	}

	
	
}
