package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Proxy;

import com.pay10.commons.util.ModeType;
import com.pay10.commons.util.OrderIdType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.UserStatusType;

@Entity
@Proxy(lazy = false)
@Table(indexes = { @Index(name = "IDX_MYIDX1", columnList = "emailId,payId") })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements Serializable {
	private static final long serialVersionUID = 8476685067435231830L;

	// Personal details
	@Id
	@Column(nullable = false, unique = true)
	private String emailId;
	private String password;
	private String payId;
	private String businessName;
	private String aliasName;// deepak
	private String firstName;
	private String lastName;
	private String companyName;
	private String contactPerson;

	private String merchantType;
	private String resellerId;
	private String productDetail;
	private Date registrationDate;
	private Date updateDate;
	private Date activationDate;
	private String updatedBy;
	@Transient
	private String requestedBy;

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

	private Long tenantId;
	private String tenantNumber;
	// Encryption
	@Column(name = "encKey")
	private String encKey;
	private String google2FASecretkey;
	//private boolean isPo;

	@Column(name="tfa_flag")
	private Boolean tfaFlag=true;

//	public boolean isPo() {
//		return isPo;
//	}
//
//	public void setPo(boolean isPo) {
//		this.isPo = isPo;
//	}

	public String getGoogle2FASecretkey() {
		return google2FASecretkey;
	}

	public void setGoogle2FASecretkey(String google2faSecretkey) {
		google2FASecretkey = google2faSecretkey;
	}

	public String getEncKey() {
		return encKey;
	}

	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}

	// Action
	@Enumerated(EnumType.STRING)
	private ModeType modeType;

	@Enumerated(EnumType.STRING)
	private OrderIdType allowDuplicateOrderId;

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

	// Documents
	private String uploadePhoto;
	private String uploadedPanCard;
	private String uploadedPhotoIdProof;
	private String uploadedContractDocument;

	// Account Activation
	private String accountValidationKey;
	private Boolean emailValidationFlag;

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
	private String merchantGstNo;
	// industry classification
	private String industryCategory;
	private String industrySubCategory;

	// Transaction Emailer
	private String transactionEmailId;
	private String transactionMobileNo;
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
	private String parentPayId;
	private String transactionSms;
	private String paymentMessageSlab;
	private boolean allowSaleDuplicate;
	private boolean allowRefundDuplicate;
	private boolean allowSaleInRefund;
	private boolean allowRefundInSale;
	// saving last activity
	private String lastActionName;
	private String autoMin;
	private boolean autoFlag;
	// Naming Convention
	private String settlementNamingConvention;
	private String refundValidationNamingConvention;
	private Boolean fiatAllowed;
	private Boolean cryptoAllowed;
	private String defaultCrypto;
	private String cryptoAddress;
	private String bankCountry;
	@Column(name = "pg_flag", columnDefinition = "boolean default true")
	private Boolean PGFlag=true;
	@Column(name = "po_flag", columnDefinition = "boolean default false")
	private Boolean POFlag=false;
	// Merchant Onboarding document Blob.
	public void setBankCountry(String bankCountry){
		this.bankCountry = bankCountry;
	}
	public String getBankCountry(){
		return this.bankCountry;
	}

	public Boolean getPGFlag() {
		return PGFlag;
	}

	public void setPGFlag(Boolean pGFlag) {
		PGFlag = pGFlag;
	}

	public Boolean getPOFlag() {
		return POFlag;
	}

	public void setPOFlag(Boolean pOFlag) {
		POFlag = pOFlag;
	}

	public void setDefaultCrypto(String defaultCrypto) {
		this.defaultCrypto = defaultCrypto;
	}


	public Boolean isFiatAllowed(){
		return this.fiatAllowed;
	}

	public Boolean isCryptoAllowed(){
		return this.cryptoAllowed;
	}

	public String getDefaultCrypto(){
		return this.defaultCrypto;
	}

	public String getCryptoAddress(){
		return this.cryptoAddress;
	}


	@Lob
	private byte[] onBoardDocList;
	@Transient
	private String onBoardDocListString;

	// White label URL for merchant hosted.
	private boolean enableWhiteLabelUrl;
	private String whiteLabelUrl;

	private boolean enableAutoRefundPostSettlement;

	// Notification API
	private boolean notificationApiEnableFlag;
	private String notificaionApi;

	// Payment Link
	private String paymentLink;

	// configurable From User Amount
	private float extraRefundLimit;

	// Done By chetan nagaria for change in settlement process to mark transaction as RNS
	@ColumnDefault("1")
	private int settlementDays;
	@ColumnDefault("0")
	private int liabilityHoldPercentage;

	@Column(columnDefinition = "TEXT", length = 500)
	private String callBackUrl;

	@Column(columnDefinition = "boolean default false")
	private boolean callBackFlag = false;

	// default currency
	private String defaultCurrency;

	// Amex field43
	private String amexSellerId;
	private String mCC;

	// payment page default language
	private String defaultLanguage;

	@Enumerated(EnumType.STRING)
	private UserStatusType userStatus;

	@Enumerated(EnumType.STRING)
	private UserType userType;

	// Email Expiry Time
	private Date emailExpiryTime;

	// card save param
	private String cardSaveParam;

	private boolean passwordExpired;

	// For Merchant Segment
	private String segment;

	// For Reseller
	private String cycle;
	@Column(name="SMAId")
	private String smaId;
	@Column(name="MAId")
	private String maId;
	private String AgentId;

	/*
	 * @Enumerated(EnumType.STRING) private BusinessType businessType;
	 */

	@OneToMany(targetEntity = Roles.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Roles> roles = new HashSet<Roles>();

	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@OneToMany(targetEntity = Account.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "")
	private Set<Account> accounts = new HashSet<Account>();

//	@OneToMany(targetEntity=NotificationEmailer.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//	private Set<NotificationEmailer> notificationEmailers = new HashSet<NotificationEmailer>();

//	@OneToMany(targetEntity=RouterRule.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//	private Set<RouterRule> routerRule = new HashSet<RouterRule>();

//	@OneToMany(targetEntity=RuleMap.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//	private Set<RuleMap> rules = new HashSet<RuleMap>();

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "roleId")
	private Role role;

	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "groupId")
	private UserGroup userGroup;

	private boolean bankVerificationStatus;
	private boolean splitPaymentStatus;
	@Column(name = "ekycFlag", columnDefinition = "boolean default false")
	private boolean ekycFlag=false;

	private String payText;
	private String invoiceText;
	private boolean tncStatus;
	private boolean needToShowAcqFieldsInReport;

	//For MerchantS2S Flag
	private boolean merchantS2SFlag;

	//Added By Sweety
	private Boolean payoutIp;
	private String payoutIpList;

	@Column(name = "webStoreApiEnableFlag", columnDefinition = "boolean default false")
	private Boolean webStoreApiEnableFlag=false;
	private String  uuId;
	private String webStoreApiToken;

	//first time activation email send 
	@Column(name = "activeEmailNotificationFlag",columnDefinition = "boolean default false")
	private boolean activeEmailNotificationFlag=false;
	
	public Boolean getPayoutIp() {
		return payoutIp;
	}

	public void setPayoutIp(Boolean payoutIp) {
		this.payoutIp = payoutIp;
	}

	public String getPayoutIpList() {
		return payoutIpList;
	}

	public void setPayoutIpList(String payoutIpList) {
		this.payoutIpList = payoutIpList;
	}

	public void setFiatAllowed(Boolean fiatAllowed) {
		this.fiatAllowed = fiatAllowed;
	}

	public void setCryptoAllowed(Boolean cryptoAllowed) {
		this.cryptoAllowed = cryptoAllowed;
	}

	public void setCryptoAddress(String cryptoAddress) {
		this.cryptoAddress = cryptoAddress;
	}

	public User() {

	}

	public boolean isEkycFlag() {
		return ekycFlag;
	}

	public void setEkycFlag(boolean ekycFlag) {
		this.ekycFlag = ekycFlag;
	}

	public boolean isSplitPaymentStatus() {
		return splitPaymentStatus;
	}

	public void setSplitPaymentStatus(boolean splitPaymentStatus) {
		this.splitPaymentStatus = splitPaymentStatus;
	}

	public boolean isBankVerificationStatus() {
		return bankVerificationStatus;
	}


	public void setBankVerificationStatus(boolean bankVerificationStatus) {
		this.bankVerificationStatus = bankVerificationStatus;
	}

	public Account getAccountUsingAcquirerCode(String acquirer) {
		Set<Account> accounts = getAccounts();
		User acquirerFromDb = new UserDao().findAcquirerByCode(acquirer);

		for (Account account : accounts) {
			if (StringUtils.isEmpty(account.getAcquirerPayId())) {
				continue;
			}
			if (account.getAcquirerPayId().equals(acquirerFromDb.getPayId())) {
				return account;
			}
		}
		return null;
	}

	public String getPrimaryAccount() {
		String acquirerName = "";
		for (Account account : accounts) {
			if (true == account.isPrimaryStatus()) {
				acquirerName = account.getAcquirerName();
				break;
			}
		}
		return acquirerName;
	}

	public String getNetbankingPrimaryAccount() {
		String acquirerName = "";
		for (Account account : accounts) {
			if (true == account.isPrimaryNetbankingStatus()) {
				acquirerName = account.getAcquirerName();
				break;
			}
		}
		return acquirerName;
	}

	public String getTenantNumber() {
		return tenantNumber;
	}

	public void setTenantNumber(String tenantNumber) {
		this.tenantNumber = tenantNumber;
	}

	public void addRole(Roles role) {
		this.roles.add(role);
	}

	public void removeRole(Roles role) {
		this.roles.remove(role);
	}

	public Set<Roles> getRoles() {
		return roles;
	}

	public void setRoles(Set<Roles> roles) {
		this.roles = roles;
	}

	public void addAccount(Account account) {
		this.accounts.add(account);
	}

	public void removeAccount(Account account) {
		this.accounts.remove(account);
	}

	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getUploadePhoto() {
		return uploadePhoto;
	}

	public void setUploadePhoto(String uploadePhoto) {
		this.uploadePhoto = uploadePhoto;
	}

	public String getUploadedPanCard() {
		return uploadedPanCard;
	}

	public void setUploadedPanCard(String uploadedPanCard) {
		this.uploadedPanCard = uploadedPanCard;
	}

	public String getUploadedPhotoIdProof() {
		return uploadedPhotoIdProof;
	}

	public void setUploadedPhotoIdProof(String uploadedPhotoIdProof) {
		this.uploadedPhotoIdProof = uploadedPhotoIdProof;
	}

	public String getUploadedContractDocument() {
		return uploadedContractDocument;
	}

	public void setUploadedContractDocument(String uploadedContractDocument) {
		this.uploadedContractDocument = uploadedContractDocument;
	}

	public String getAccountValidationKey() {
		return accountValidationKey;
	}

	public void setAccountValidationKey(String accountValidationKey) {
		this.accountValidationKey = accountValidationKey;
	}

	public Boolean isEmailValidationFlag() {
		return emailValidationFlag;
	}

	public void setEmailValidationFlag(Boolean emailValidationFlag) {
		this.emailValidationFlag = emailValidationFlag;
	}

	public UserStatusType getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatusType userStatus) {
		this.userStatus = userStatus;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
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

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
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

	public String getParentPayId() {
		return parentPayId;
	}

	public void setParentPayId(String parentPayId) {
		this.parentPayId = parentPayId;
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

	public boolean isNotificationApiEnableFlag() {
		return notificationApiEnableFlag;
	}

	public void setNotificationApiEnableFlag(boolean notificationApiEnableFlag) {
		this.notificationApiEnableFlag = notificationApiEnableFlag;
	}

	public String getNotificaionApi() {
		return notificaionApi;
	}

	public void setNotificaionApi(String notificaionApi) {
		this.notificaionApi = notificaionApi;
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

	public String getLastActionName() {
		return lastActionName;
	}

	public void setLastActionName(String lastActionName) {
		this.lastActionName = lastActionName;
	}

	public String getSettlementNamingConvention() {
		return settlementNamingConvention;
	}

	public void setSettlementNamingConvention(String settlementNamingConvention) {
		this.settlementNamingConvention = settlementNamingConvention;
	}

	public String getRefundValidationNamingConvention() {
		return refundValidationNamingConvention;
	}

	public void setRefundValidationNamingConvention(String refundValidationNamingConvention) {
		this.refundValidationNamingConvention = refundValidationNamingConvention;
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

	public String getmCC() {
		return mCC;
	}

	public void setmCC(String mCC) {
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

	/*
	 * public BusinessType getBusinessType() { return businessType; }
	 *
	 * public void setBusinessType(BusinessType businessType) { this.businessType =
	 * businessType; }
	 */
	public boolean isSurchargeFlag() {
		return surchargeFlag;
	}

	public void setSurchargeFlag(boolean surchargeFlag) {
		this.surchargeFlag = surchargeFlag;
	}

	// Temp Fix for old merchants
	public String getIndustryCategory() {
//		String industryCategoryFromFile = PropertiesManager.propertiesMap.get(industryCategory);
//		if (StringUtils.isBlank(industryCategoryFromFile)) {
//			industryCategory = "OTHER";
//		}
//		if(StringUtils.isBlank(industryCategory)){
//			return "OTHER";
//		}
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

	/*
	 * public Set<NotificationEmailer> getNotificationEmailers() { return
	 * notificationEmailers; }
	 *
	 * public void setNotificationEmailers(Set<NotificationEmailer>
	 * notificationEmailers) { this.notificationEmailers = notificationEmailers; }
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getEmailExpiryTime() {
		return emailExpiryTime;
	}

	public void setEmailExpiryTime(Date emailExpiryTime) {
		this.emailExpiryTime = emailExpiryTime;
	}

	public String getMerchantGstNo() {
		return merchantGstNo;
	}

	public void setMerchantGstNo(String merchantGstNo) {
		this.merchantGstNo = merchantGstNo;
	}

	public String getTransactionSms() {
		return transactionSms;
	}

	public void setTransactionSms(String transactionSms) {
		this.transactionSms = transactionSms;
	}

	public String getTransactionMobileNo() {
		return transactionMobileNo;
	}

	public boolean isSkipOrderIdForRefund() {
		return skipOrderIdForRefund;
	}

	public void setSkipOrderIdForRefund(boolean skipOrderIdForRefund) {
		this.skipOrderIdForRefund = skipOrderIdForRefund;
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

	public byte[] getOnBoardDocList() {
		return onBoardDocList;
	}

	public void setOnBoardDocList(byte[] onBoardDocList) {
		this.onBoardDocList = onBoardDocList;
	}

	public String getOnBoardDocListString() {
		return onBoardDocListString;
	}

	public void setOnBoardDocListString(String onBoardDocListString) {
		this.onBoardDocListString = onBoardDocListString;
	}

	public boolean isEnableWhiteLabelUrl() {
		return enableWhiteLabelUrl;
	}

	public void setEnableWhiteLabelUrl(boolean enableWhiteLabelUrl) {
		this.enableWhiteLabelUrl = enableWhiteLabelUrl;
	}

	public String getWhiteLabelUrl() {
		return whiteLabelUrl;
	}

	public void setWhiteLabelUrl(String whiteLabelUrl) {
		this.whiteLabelUrl = whiteLabelUrl;
	}

	public OrderIdType getAllowDuplicateOrderId() {
		return allowDuplicateOrderId;
	}

	public void setAllowDuplicateOrderId(OrderIdType allowDuplicateOrderId) {
		this.allowDuplicateOrderId = allowDuplicateOrderId;
	}

	public boolean isEnableAutoRefundPostSettlement() {
		return enableAutoRefundPostSettlement;
	}

	public void setEnableAutoRefundPostSettlement(boolean enableAutoRefundPostSettlement) {
		this.enableAutoRefundPostSettlement = enableAutoRefundPostSettlement;
	}

	public boolean isPasswordExpired() {
		return passwordExpired;
	}

	public void setPasswordExpired(boolean passwordExpired) {
		this.passwordExpired = passwordExpired;
	}

	public String getPaymentLink() {
		return paymentLink;
	}

	public void setPaymentLink(String paymentLink) {
		this.paymentLink = paymentLink;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getAutoMin() {
		return autoMin;
	}

	public void setAutoMin(String autoMin) {
		this.autoMin = autoMin;
	}

	public boolean isAutoFlag() {
		return autoFlag;
	}

	public void setAutoFlag(boolean autoFlag) {
		this.autoFlag = autoFlag;
	}

	/* deepak */
	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public int getSettlementDays() {
		return settlementDays;
	}

	public void setSettlementDays(int settlementDays) {
		this.settlementDays = settlementDays;
	}

	public int getLiabilityHoldPercentage() {
		return liabilityHoldPercentage;
	}

	public void setLiabilityHoldPercentage(int liabilityHoldPercentage) {
		this.liabilityHoldPercentage = liabilityHoldPercentage;
	}

	public String getPayText() {
		return payText;
	}

	public void setPayText(String payText) {
		this.payText = payText;
	}

	public String getInvoiceText() {
		return invoiceText;
	}

	public void setInvoiceText(String invoiceText) {
		this.invoiceText = invoiceText;
	}

	public boolean isTncStatus() {
		return tncStatus;
	}

	public void setTncStatus(boolean tncStatus) {
		this.tncStatus = tncStatus;
	}

	public boolean isNeedToShowAcqFieldsInReport() {
		return needToShowAcqFieldsInReport;
	}

	public void setNeedToShowAcqFieldsInReport(boolean needToShowAcqFieldsInReport) {
		this.needToShowAcqFieldsInReport = needToShowAcqFieldsInReport;
	}
	public Boolean isTfaFlag() {
		return tfaFlag;
	}

	public void setTfaFlag(Boolean tfaFlag) {
		this.tfaFlag = tfaFlag;
	}

	public boolean isMerchantS2SFlag() {
		return merchantS2SFlag;
	}

	public void setMerchantS2SFlag(boolean merchantS2SFlag) {
		this.merchantS2SFlag = merchantS2SFlag;
	}

	public Boolean isWebStoreApiEnableFlag() {
		return webStoreApiEnableFlag;
	}

	public void setWebStoreApiEnableFlag(Boolean webStoreApiEnableFlag) {
		this.webStoreApiEnableFlag = webStoreApiEnableFlag;
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public String getWebStoreApiToken() {
		return webStoreApiToken;
	}

	public void setWebStoreApiToken(String webStoreApiToken) {
		this.webStoreApiToken = webStoreApiToken;
	}


	


	public String getSmaId() {
		return smaId;
	}

	public void setSmaId(String smaId) {
		this.smaId = smaId;
	}

	public String getMaId() {
		return maId;
	}

	public void setMaId(String maId) {
		this.maId = maId;
	}

	public String getAgentId() {
		return AgentId;
	}

	public void setAgentId(String agentId) {
		AgentId = agentId;
	}
	
	public boolean isActiveEmailNotificationFlag() {
		return activeEmailNotificationFlag;
	}

	public void setActiveEmailNotificationFlag(boolean activeEmailNotificationFlag) {
		this.activeEmailNotificationFlag = activeEmailNotificationFlag;
	}

	@Override
	public String toString() {
		return "User [emailId=" + emailId + ", password=" + password + ", payId=" + payId + ", businessName="
				+ businessName + ", aliasName=" + aliasName + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", companyName=" + companyName + ", contactPerson=" + contactPerson + ", merchantType=" + merchantType
				+ ", resellerId=" + resellerId + ", productDetail=" + productDetail + ", registrationDate="
				+ registrationDate + ", updateDate=" + updateDate + ", activationDate=" + activationDate
				+ ", updatedBy=" + updatedBy + ", requestedBy=" + requestedBy + ", mobile=" + mobile
				+ ", transactionSmsFlag=" + transactionSmsFlag + ", telephoneNo=" + telephoneNo + ", fax=" + fax
				+ ", address=" + address + ", city=" + city + ", state=" + state + ", country=" + country
				+ ", postalCode=" + postalCode + ", tenantId=" + tenantId + ", tenantNumber=" + tenantNumber
				+ ", encKey=" + encKey + ", modeType=" + modeType + ", allowDuplicateOrderId=" + allowDuplicateOrderId
				+ ", comments=" + comments + ", whiteListIpAddress=" + whiteListIpAddress + ", bankName=" + bankName
				+ ", ifscCode=" + ifscCode + ", accHolderName=" + accHolderName + ", currency=" + currency
				+ ", branchName=" + branchName + ", panCard=" + panCard + ", accountNo=" + accountNo + ", uploadePhoto="
				+ uploadePhoto + ", uploadedPanCard=" + uploadedPanCard + ", uploadedPhotoIdProof="
				+ uploadedPhotoIdProof + ", uploadedContractDocument=" + uploadedContractDocument
				+ ", accountValidationKey=" + accountValidationKey + ", emailValidationFlag=" + emailValidationFlag
				+ ", organisationType=" + organisationType + ", website=" + website + ", multiCurrency=" + multiCurrency
				+ ", businessModel=" + businessModel + ", operationAddress=" + operationAddress + ", operationState="
				+ operationState + ", operationCity=" + operationCity + ", operationPostalCode=" + operationPostalCode
				+ ", dateOfEstablishment=" + dateOfEstablishment + ", cin=" + cin + ", pan=" + pan + ", panName="
				+ panName + ", noOfTransactions=" + noOfTransactions + ", amountOfTransactions=" + amountOfTransactions
				+ ", attemptTrasacation=" + attemptTrasacation + ", merchantGstNo=" + merchantGstNo
				+ ", industryCategory=" + industryCategory + ", industrySubCategory=" + industrySubCategory
				+ ", transactionEmailId=" + transactionEmailId + ", transactionMobileNo=" + transactionMobileNo
				+ ", transactionEmailerFlag=" + transactionEmailerFlag + ", expressPayFlag=" + expressPayFlag
				+ ", merchantHostedFlag=" + merchantHostedFlag + ", iframePaymentFlag=" + iframePaymentFlag
				+ ", transactionAuthenticationEmailFlag=" + transactionAuthenticationEmailFlag
				+ ", transactionCustomerEmailFlag=" + transactionCustomerEmailFlag
				+ ", refundTransactionCustomerEmailFlag=" + refundTransactionCustomerEmailFlag
				+ ", refundTransactionMerchantEmailFlag=" + refundTransactionMerchantEmailFlag
				+ ", transactionFailedAlertFlag=" + transactionFailedAlertFlag + ", retryTransactionCustomeFlag="
				+ retryTransactionCustomeFlag + ", surchargeFlag=" + surchargeFlag + ", skipOrderIdForRefund="
				+ skipOrderIdForRefund + ", parentPayId=" + parentPayId + ", transactionSms=" + transactionSms
				+ ", paymentMessageSlab=" + paymentMessageSlab + ", allowSaleDuplicate=" + allowSaleDuplicate
				+ ", allowRefundDuplicate=" + allowRefundDuplicate + ", allowSaleInRefund=" + allowSaleInRefund
				+ ", allowRefundInSale=" + allowRefundInSale + ", lastActionName=" + lastActionName + ", autoMin="
				+ autoMin + ", autoFlag=" + autoFlag + ", settlementNamingConvention=" + settlementNamingConvention
				+ ", refundValidationNamingConvention=" + refundValidationNamingConvention + ", onBoardDocList="
				+ Arrays.toString(onBoardDocList) + ", onBoardDocListString=" + onBoardDocListString
				+ ", enableWhiteLabelUrl=" + enableWhiteLabelUrl + ", whiteLabelUrl=" + whiteLabelUrl
				+ ", enableAutoRefundPostSettlement=" + enableAutoRefundPostSettlement + ", notificationApiEnableFlag="
				+ notificationApiEnableFlag + ", notificaionApi=" + notificaionApi + ", paymentLink=" + paymentLink
				+ ", extraRefundLimit=" + extraRefundLimit + ", settlementDays=" + settlementDays
				+ ", liabilityHoldPercentage=" + liabilityHoldPercentage + ", callBackUrl=" + callBackUrl
				+ ", callBackFlag=" + callBackFlag + ", defaultCurrency=" + defaultCurrency + ", amexSellerId="
				+ amexSellerId + ", mCC=" + mCC + ", defaultLanguage=" + defaultLanguage + ", userStatus=" + userStatus
				+ ", userType=" + userType + ", emailExpiryTime=" + emailExpiryTime + ", cardSaveParam=" + cardSaveParam
				+ ", passwordExpired=" + passwordExpired + ", segment=" + segment + ", cycle=" + cycle + ", roles="
				+ roles + ", accounts=" + accounts + ", role=" + role + ", userGroup=" + userGroup
				+ ", bankVerificationStatus=" + bankVerificationStatus + ", splitPaymentStatus=" + splitPaymentStatus
				+ ", ekycFlag=" + ekycFlag + ", payText=" + payText + ", invoiceText=" + invoiceText + ", tncStatus="
				+ tncStatus + ", needToShowAcqFieldsInReport=" + needToShowAcqFieldsInReport
				+ ", webStoreApiEnableFlag=" + webStoreApiEnableFlag + ", uuId=" + uuId + ", webStoreApiToken="
				+ webStoreApiToken + ", tfaFlag="+tfaFlag+"]";
	}

//	public String getDbaName() {
//		return dbaName;
//	}
//
//	public void setDbaName(String dbaName) {
//		this.dbaName = dbaName;
//	}
}
