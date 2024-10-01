package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

import com.pay10.commons.util.ModeType;
import com.pay10.commons.util.OrderIdType;
import com.pay10.commons.util.UserStatusType;

/**
 * @author Rahul
 *
 */
@Entity
@Proxy(lazy = false)
@Table(indexes = { @Index(name = "IDX_MYIDX1", columnList = "emailId,payId") })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PendingUserApproval implements Serializable {

	private static final long serialVersionUID = 6428257431260324977L;
	// Personal details
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, unique = true)
	private long id;

	private String emailId;
	private String payId;
	private String businessName;
	private String firstName;
	private String lastName;
	private String companyName;
	private String contactPerson;
	private String transactionSms;
	private String merchantType;
	private String resellerId;
	private String productDetail;
	private Date registrationDate;
	private Date updateDate;
	private String requestedBy;
	private String processedBy;
	private String requestStatus;

	// Contact Details
	private String mobile;
	private boolean transactionSmsFlag;
	private String telephoneNo;
	private String fax;
	private String address;
	private String city;
	private String state;	
	private String country;
	private String postalCode;
	// Action
	@Enumerated(EnumType.STRING)
	private ModeType modeType;

	private String comments;
	private String whiteListIpAddress;

	// Bank Details
	private String bankName;
	private String ifscCode;
	private String accHolderName;
	private String currency;
	private String branchName;
	private String panCard;
	private String accountNo;

	// Account Activation
	private boolean emailValidationFlag;

	// business details
	private String organisationType;
	private String website;
	private String multiCurrency;
	private String businessModel;
	private String operationAddress;
	private String operationState;
	private String operationCity;
	private String operationPostalCode;
	private String dateOfEstablishment;
	private String cin;
	private String pan;
	private String panName;
	private String noOfTransactions;
	private String amountOfTransactions;
	private String attemptTrasacation;
	// industry classification
	private String industryCategory;
	private String industrySubCategory;

	// Transaction Emailer
	private String transactionEmailId;
	private String transactionMobileNO;
	private boolean transactionEmailerFlag;

	// Payment Flag
	private boolean expressPayFlag;
	private boolean merchantHostedFlag;
	private boolean iframePaymentFlag;
	private boolean transactionAuthenticationEmailFlag;
	private boolean transactionCustomerEmailFlag;
	private boolean refundTransactionCustomerEmailFlag;
	private boolean refundTransactionMerchantEmailFlag;
	private boolean transactionFailedAlertFlag;
	private boolean retryTransactionCustomeFlag;
	private boolean surchargeFlag;
	private boolean skipOrderIdForRefund;
	private String paymentMessageSlab;
	private OrderIdType allowDuplicateOrderId;
	private String cardSaveParam;
	private boolean allowSaleDuplicate;
	private boolean allowRefundDuplicate;
	private boolean allowSaleInRefund;
	private boolean allowRefundInSale;
	
	// Notification API 
	private boolean notificationApiEnableFlag;
	private String notificaionApi;
	
	@Column(columnDefinition = "TEXT", length = 500)
	private String callBackUrl;
	
	@Column(columnDefinition = "boolean default false")
	private boolean callBackFlag = false;
	
	// Payment Link
	private String paymentLink;
	
	public boolean isNotificationApiEnableFlag() {
		return notificationApiEnableFlag;
	}

	public void setNotificationApiEnableFlag(
			boolean notificationApiEnableFlag) {
		this.notificationApiEnableFlag = notificationApiEnableFlag;
	}
	
	
	public String getNotificaionApi() {
		return notificaionApi;
	}

	public void setNotificaionApi(String notificaionApi) {
		this.notificaionApi = notificaionApi;
	}
	
	public String getPaymentLink() {
		return paymentLink;
	}

	public void setPaymentLink(String paymentLink) {
		this.paymentLink = paymentLink;
	}
	
	// configurable From User Amount
	public boolean isSkipOrderIdForRefund() {
		return skipOrderIdForRefund;
	}

	public void setSkipOrderIdForRefund(boolean skipOrderIdForRefund) {
		this.skipOrderIdForRefund = skipOrderIdForRefund;
	}


	public OrderIdType getAllowDuplicateOrderId() {
		return allowDuplicateOrderId;
	}

	public void setAllowDuplicateOrderId(OrderIdType allowDuplicateOrderId) {
		this.allowDuplicateOrderId = allowDuplicateOrderId;
	}


	private float extraRefundLimit;

	// default currency
	private String defaultCurrency;

	// Amex field43
	private String amexSellerId;
	private String mCC;

	// payment page default language
	private String defaultLanguage;

	@Enumerated(EnumType.STRING)
	private UserStatusType userStatus;

	public PendingUserApproval() {

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getMerchantType() {
		return merchantType;
	}

	public void setMerchantType(String merchantType) {
		this.merchantType = merchantType;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public boolean isCallBackFlag() {
		return callBackFlag;
	}

	public void setCallBackFlag(boolean callBackFlag) {
		this.callBackFlag = callBackFlag;
	}

	public String getNoOfTransactions() {
		return noOfTransactions;
	}

	public void setNoOfTransactions(String noOfTransactions) {
		this.noOfTransactions = noOfTransactions;
	}

	public String getAmountOfTransactions() {
		return amountOfTransactions;
	}

	public void setAmountOfTransactions(String amountOfTransactions) {
		this.amountOfTransactions = amountOfTransactions;
	}

	public String getResellerId() {
		return resellerId;
	}

	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}

	public String getProductDetail() {
		return productDetail;
	}

	public void setProductDetail(String productDetail) {
		this.productDetail = productDetail;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public boolean isTransactionSmsFlag() {
		return transactionSmsFlag;
	}

	public void setTransactionSmsFlag(boolean transactionSmsFlag) {
		this.transactionSmsFlag = transactionSmsFlag;
	}

	public String getTelephoneNo() {
		return telephoneNo;
	}

	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public ModeType getModeType() {
		return modeType;
	}

	public void setModeType(ModeType modeType) {
		this.modeType = modeType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getWhiteListIpAddress() {
		return whiteListIpAddress;
	}

	public void setWhiteListIpAddress(String whiteListIpAddress) {
		this.whiteListIpAddress = whiteListIpAddress;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getAccHolderName() {
		return accHolderName;
	}

	public void setAccHolderName(String accHolderName) {
		this.accHolderName = accHolderName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getPanCard() {
		return panCard;
	}

	public void setPanCard(String panCard) {
		this.panCard = panCard;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public boolean isEmailValidationFlag() {
		return emailValidationFlag;
	}

	public void setEmailValidationFlag(boolean emailValidationFlag) {
		this.emailValidationFlag = emailValidationFlag;
	}

	public UserStatusType getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatusType userStatus) {
		this.userStatus = userStatus;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getMultiCurrency() {
		return multiCurrency;
	}

	public void setMultiCurrency(String multiCurrency) {
		this.multiCurrency = multiCurrency;
	}

	public String getBusinessModel() {
		return businessModel;
	}

	public void setBusinessModel(String businessModel) {
		this.businessModel = businessModel;
	}

	public String getOperationAddress() {
		return operationAddress;
	}

	public void setOperationAddress(String operationAddress) {
		this.operationAddress = operationAddress;
	}

	public String getOperationState() {
		return operationState;
	}

	public void setOperationState(String operationState) {
		this.operationState = operationState;
	}

	public String getOperationCity() {
		return operationCity;
	}

	public void setOperationCity(String operationCity) {
		this.operationCity = operationCity;
	}

	public String getOperationPostalCode() {
		return operationPostalCode;
	}

	public void setOperationPostalCode(String operationPostalCode) {
		this.operationPostalCode = operationPostalCode;
	}

	public String getDateOfEstablishment() {
		return dateOfEstablishment;
	}

	public void setDateOfEstablishment(String dateOfEstablishment) {
		this.dateOfEstablishment = dateOfEstablishment;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getPanName() {
		return panName;
	}

	public void setPanName(String panName) {
		this.panName = panName;
	}

	public String getOrganisationType() {
		return organisationType;
	}

	public void setOrganisationType(String organisationType) {
		this.organisationType = organisationType;
	}

	public String getTransactionEmailId() {
		return transactionEmailId;
	}

	public void setTransactionEmailId(String transactionEmailId) {
		this.transactionEmailId = transactionEmailId;
	}

	public boolean isTransactionEmailerFlag() {
		return transactionEmailerFlag;
	}

	public void setTransactionEmailerFlag(boolean transactionEmailerFlag) {
		this.transactionEmailerFlag = transactionEmailerFlag;
	}

	public boolean isExpressPayFlag() {
		return expressPayFlag;
	}

	public boolean isTransactionCustomerEmailFlag() {
		return transactionCustomerEmailFlag;
	}

	public void setTransactionCustomerEmailFlag(boolean transactionCustomerEmailFlag) {
		this.transactionCustomerEmailFlag = transactionCustomerEmailFlag;
	}

	public boolean isRefundTransactionCustomerEmailFlag() {
		return refundTransactionCustomerEmailFlag;
	}

	public void setRefundTransactionCustomerEmailFlag(boolean refundTransactionCustomerEmailFlag) {
		this.refundTransactionCustomerEmailFlag = refundTransactionCustomerEmailFlag;
	}

	public boolean isRefundTransactionMerchantEmailFlag() {
		return refundTransactionMerchantEmailFlag;
	}

	public void setRefundTransactionMerchantEmailFlag(boolean refundTransactionMerchantEmailFlag) {
		this.refundTransactionMerchantEmailFlag = refundTransactionMerchantEmailFlag;
	}

	public boolean isTransactionFailedAlertFlag() {
		return transactionFailedAlertFlag;
	}

	public void setTransactionFailedAlertFlag(boolean transactionFailedAlertFlag) {
		this.transactionFailedAlertFlag = transactionFailedAlertFlag;
	}
	
	public void setExpressPayFlag(boolean expressPayFlag) {
		this.expressPayFlag = expressPayFlag;
	}

	public boolean isMerchantHostedFlag() {
		return merchantHostedFlag;
	}

	public void setMerchantHostedFlag(boolean merchantHostedFlag) {
		this.merchantHostedFlag = merchantHostedFlag;
	}

	public boolean isIframePaymentFlag() {
		return iframePaymentFlag;
	}

	public void setIframePaymentFlag(boolean iframePaymentFlag) {
		this.iframePaymentFlag = iframePaymentFlag;
	}

	public boolean isTransactionAuthenticationEmailFlag() {
		return transactionAuthenticationEmailFlag;
	}

	public void setTransactionAuthenticationEmailFlag(boolean transactionAuthenticationEmailFlag) {
		this.transactionAuthenticationEmailFlag = transactionAuthenticationEmailFlag;
	}

	public boolean isRetryTransactionCustomeFlag() {
		return retryTransactionCustomeFlag;
	}

	public void setRetryTransactionCustomeFlag(boolean retryTransactionCustomeFlag) {
		this.retryTransactionCustomeFlag = retryTransactionCustomeFlag;
	}

	public String getAttemptTrasacation() {
		return attemptTrasacation;
	}

	public void setAttemptTrasacation(String attemptTrasacation) {
		this.attemptTrasacation = attemptTrasacation;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public float getExtraRefundLimit() {
		return extraRefundLimit;
	}

	public void setExtraRefundLimit(float extraRefundLimit) {
		this.extraRefundLimit = extraRefundLimit;
	}

	public String getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public String getMCC() {
		return mCC;
	}

	public void setMCC(String mCC) {
		this.mCC = mCC;
	}

	public String getAmexSellerId() {
		return amexSellerId;
	}

	public void setAmexSellerId(String amexSellerId) {
		this.amexSellerId = amexSellerId;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public boolean isSurchargeFlag() {
		return surchargeFlag;
	}

	public void setSurchargeFlag(boolean surchargeFlag) {
		this.surchargeFlag = surchargeFlag;
	}

	public String getIndustryCategory() {
		return industryCategory;
	}

	public void setIndustryCategory(String industryCategory) {
		this.industryCategory = industryCategory;
	}

	public String getIndustrySubCategory() {
		return industrySubCategory;
	}

	public void setIndustrySubCategory(String industrySubCategory) {
		this.industrySubCategory = industrySubCategory;
	}

	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getmCC() {
		return mCC;
	}

	public void setmCC(String mCC) {
		this.mCC = mCC;
	}

	public String getProcessedBy() {
		return processedBy;
	}

	public void setProcessedBy(String processedBy) {
		this.processedBy = processedBy;
	}

	public String getTransactionSms() {
		return transactionSms;
	}

	public void setTransactionSms(String transactionSms) {
		this.transactionSms = transactionSms;
	}

	public String getPaymentMessageSlab() {
		return paymentMessageSlab;
	}

	public void setPaymentMessageSlab(String paymentMessageSlab) {
		this.paymentMessageSlab = paymentMessageSlab;
	}

	public String getCardSaveParam() {
		return cardSaveParam;
	}

	public void setCardSaveParam(String cardSaveParam) {
		this.cardSaveParam = cardSaveParam;
	}

	public boolean isAllowSaleDuplicate() {
		return allowSaleDuplicate;
	}

	public void setAllowSaleDuplicate(boolean allowSaleDuplicate) {
		this.allowSaleDuplicate = allowSaleDuplicate;
	}

	public boolean isAllowRefundDuplicate() {
		return allowRefundDuplicate;
	}

	public void setAllowRefundDuplicate(boolean allowRefundDuplicate) {
		this.allowRefundDuplicate = allowRefundDuplicate;
	}

	public boolean isAllowSaleInRefund() {
		return allowSaleInRefund;
	}

	public void setAllowSaleInRefund(boolean allowSaleInRefund) {
		this.allowSaleInRefund = allowSaleInRefund;
	}

	public boolean isAllowRefundInSale() {
		return allowRefundInSale;
	}

	public void setAllowRefundInSale(boolean allowRefundInSale) {
		this.allowRefundInSale = allowRefundInSale;
	}
	

}
