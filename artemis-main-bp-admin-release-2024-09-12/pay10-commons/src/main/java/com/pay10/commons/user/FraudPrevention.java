package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.UpdateTimestamp;

import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FraudRuleType;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Sunil,Harpreet, Rahul
 *
 */

@Entity
@Proxy(lazy= false)@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FraudPrevention implements Serializable,Comparable<FraudPrevention>{

	private static final long serialVersionUID = 3331114640639616608L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String payId;
	private String email;  
	private String ipAddress;  
	private String subnetMask;
	private String domainName;
	private String negativeBin;
	private String negativeCard;   
	private String minutesTxnLimit; 
	private String currency;
	private String minTransactionAmount;
	private String maxTransactionAmount;
	private String issuerCountry; 
	private String userCountry;
	private String whiteListIpAddress;
	@CreationTimestamp
	private Date createDate;
	@UpdateTimestamp
	private Date updateDate;
	@Enumerated(EnumType.STRING)
	private TDRStatus status;
	@Enumerated(EnumType.STRING)
	private FraudRuleType fraudType;
	private String perCardTransactionAllowed;
	private String noOfTransactionAllowed;
	@Transient
	private String ruleGroupId;
	@Transient
	private String currencyName;
	
	private String dateActiveFrom;
	private String dateActiveTo;
	private String startTime;
	private String endTime;
	private String repeatDays;
	private boolean alwaysOnFlag;

	@Override
	public int compareTo(FraudPrevention fraudPrevention) {
		return Integer.parseInt(this.getRuleGroupId()) < Integer.parseInt(fraudPrevention.getRuleGroupId()) ? -1:
			Integer.parseInt(this.getRuleGroupId()) == Integer.parseInt(fraudPrevention.getRuleGroupId()) ? 0 : 1;
	}

	public String getPayId() {
		return payId;
	}
	
	public void setPayId(String payId) {
		this.payId = payId;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getNegativeBin() {
		return negativeBin;
	}
	
	public void setNegativeBin(String negativeBin) {
		this.negativeBin = negativeBin;
	}
	
	public String getNegativeCard() {
		return negativeCard;
	}
	
	public void setNegativeCard(String negativeCard) {
		this.negativeCard = negativeCard;
	}
	
	public String getMinTransactionAmount() {
		return minTransactionAmount;
	}
	
	public void setMinTransactionAmount(String minTransactionAmount) {
		this.minTransactionAmount = minTransactionAmount;
	}
	
	public String getMaxTransactionAmount() {
		return maxTransactionAmount;
	}
	
	public void setMaxTransactionAmount(String maxTransactionAmount) {
		this.maxTransactionAmount = maxTransactionAmount;
	}
	
	public String getSubnetMask() {
		return subnetMask;
	}
	
	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}
	
	public String getUserCountry() {
		return userCountry;
	}
	
	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}
	
	public String getIssuerCountry() {
		return issuerCountry;
	}

	public FraudRuleType getFraudType() {
		return fraudType;
	}

	public void setFraudType(FraudRuleType fraudType) {
		this.fraudType = fraudType;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getPerCardTransactionAllowed() {
		return perCardTransactionAllowed;
	}

	public void setPerCardTransactionAllowed(String perCardTransactionAllowed) {
		this.perCardTransactionAllowed = perCardTransactionAllowed;
	}

	public void setIssuerCountry(String issuerCountry) {
		this.issuerCountry = issuerCountry;
	}
	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	//returning ruleGroupId, from FraudRuleType Enum
	public String getRuleGroupId() {
		return fraudType.getCode();
	}
	
	//to return alpha code of currency for front-end
	public String getCurrencyName(){
		return Currency.getAlphabaticCode(this.currency);
	}

	public String getWhiteListIpAddress() {
		return whiteListIpAddress;
	}

	public void setWhiteListIpAddress(String whiteListIpAddress) {
		this.whiteListIpAddress = whiteListIpAddress;
	}

	public TDRStatus getStatus() {
		return status;
	}

	public void setStatus(TDRStatus status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRepeatDays() {
		return repeatDays;
	}

	public void setRepeatDays(String repeatDays) {
		this.repeatDays = repeatDays;
	}

	public String getDateActiveFrom() {
		return dateActiveFrom;
	}

	public void setDateActiveFrom(String dateActiveFrom) {
		this.dateActiveFrom = dateActiveFrom;
	}

	public String getDateActiveTo() {
		return dateActiveTo;
	}

	public void setDateActiveTo(String dateActiveTo) {
		this.dateActiveTo = dateActiveTo;
	}

	public boolean isAlwaysOnFlag() {
		return alwaysOnFlag;
	}

	public String getMinutesTxnLimit() {
		return minutesTxnLimit;
	}

	public void setMinutesTxnLimit(String minutesTxnLimit) {
		this.minutesTxnLimit = minutesTxnLimit;
	}

	public void setAlwaysOnFlag(boolean alwaysOnFlag) {
		this.alwaysOnFlag = alwaysOnFlag;
	}

	public String getNoOfTransactionAllowed() {
		return noOfTransactionAllowed;
	}

	public void setNoOfTransactionAllowed(String noOfTransactionAllowed) {
		this.noOfTransactionAllowed = noOfTransactionAllowed;
	}


	
}