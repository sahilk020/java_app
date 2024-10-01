package com.pay10.icici.mpgs;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Service("iciciMpgsTransaction")
public class Transaction {
	
	private String acsURL;
	private String expYear;
	private String expMonth;
	private String paReq;
	private String gatewayRecommendation;
	private String xid;
	private String veResEnrolled;
	private String acquirerCode;
	private String gatewayCode;
	private String rrn; 
	private String acq; 
	private String status;
	
	public void setCardDetails(Fields fields) {
		
		String expDate = fields.get(FieldType.CARD_EXP_DT.getName());
		setExpYear(expDate.substring(4, 6));
		setExpMonth(expDate.substring(0, 2));
	}



	public String getAcsURL() {
		return acsURL;
	}

	public void setAcsURL(String acsURL) {
		this.acsURL = acsURL;
	}

	public String getExpYear() {
		return expYear;
	}

	public void setExpYear(String expYear) {
		this.expYear = expYear;
	}



	public String getExpMonth() {
		return expMonth;
	}



	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}



	public String getPaReq() {
		return paReq;
	}



	public void setPaReq(String paReq) {
		this.paReq = paReq;
	}



	public String getGatewayRecommendation() {
		return gatewayRecommendation;
	}



	public void setGatewayRecommendation(String gatewayRecommendation) {
		this.gatewayRecommendation = gatewayRecommendation;
	}



	public String getXid() {
		return xid;
	}



	public void setXid(String xid) {
		this.xid = xid;
	}



	public String getVeResEnrolled() {
		return veResEnrolled;
	}



	public void setVeResEnrolled(String veResEnrolled) {
		this.veResEnrolled = veResEnrolled;
	}



	public String getAcquirerCode() {
		return acquirerCode;
	}



	public void setAcquirerCode(String acquirerCode) {
		this.acquirerCode = acquirerCode;
	}



	public String getGatewayCode() {
		return gatewayCode;
	}



	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}



	public String getRrn() {
		return rrn;
	}



	public void setRrn(String rrn) {
		this.rrn = rrn;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getAcq() {
		return acq;
	}



	public void setAcq(String acq) {
		this.acq = acq;
	}

}
