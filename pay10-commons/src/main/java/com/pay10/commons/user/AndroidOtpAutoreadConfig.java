package com.pay10.commons.user;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * @author Rohit
 *
 */

@Entity
@Table(name = "AndroidOtpAutoreadConfig")
public class AndroidOtpAutoreadConfig implements Serializable {

	private static final long serialVersionUID = -7105526894458312216L;

	// Empty Constructor
	public AndroidOtpAutoreadConfig() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String otpId;
	private String pageTitle;
	private String bankName;
	private String cardType;
	
	public String getOtpId() {
		return otpId;
	}

	public void setOtpId(String otpId) {
		this.otpId = otpId;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

}
