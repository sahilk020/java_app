package com.pay10.scheduler.commons;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("paymentgateway.demo")
public class ConfigurationProvider {

	// Mongo
	private String MONGO_DB_dbName;
	private String MONGO_DB_collectionName;
	private String MONGO_DB_host;
	private String MONGO_DB_port;
	private String MONGO_DB_mongoURIprefix;
	private String MONGO_DB_mongoURIsuffix;
	private String MONGO_DB_username;
	private String MONGO_DB_password;
	private String MONGO_DB_reportingCollectionName;
	private String MONGO_DB_settlementCollectionName;
	private String MONGO_DB_statusEnquiryCollectionName;
	private String MONGO_DB_summaryTransactionsCollectionName;
	private String payuStatusEnquiryApiUrl;
	private String transactionStatusEnquiry;
	private String acquirerName;
	private String acquirerNamePayout;

	private String enquiryApiUrl;
	private String salePayoutUrl;
	private String enquiryApiUrlPayout;
	private String pendingTransactionStatusUpdateCron;
	private String merchantPayId;
	private String AcquirerMasterSwitch;

	private String hoursBefore;
	private String hoursInterval;
	private String minutesBefore;
	private String minutesInterval;
	private String minutesBeforePayout;
	private String minutesIntervalPayout;
	private String minutesBeforePayoutEnquiry;
	private String minutesIntervalPayoutEnquiry;
	private String payoutStatusEnquiryAPi;
	private String transactionBankStatusEnquiryCron;

	// URL
	private String smsApiUrl;
	private String statusEnquiryApiUrl;
	private String refundApiUrl;
	private String orderConfirmApi;
	// Add by abhi
	private String AlertTimdiff;
	private String SlackToken;
	private String Slackchannel;
	// Added By Sweety
	private String commissionSchedulerApiUrl;
	private String resellerMonthlyPayoutSchedulerApiUrl;
	private String resellerQuaterlyPayoutSchedulerApiUrl;

	private String refundServiceApiUrl;
	private String refundFileLocation;
	private String refundAckFileLocation;
	private String refundSuccessFileLocation;
	private String responseCodeAcqNotification;

	private String bulkRefundUrl;
	private String bulkRefundConfig;

	private String chargebackApiUrl;

	private String bsesEmailApiUrl;

	private String chargebackAutoCloseUrl;
	private String irctcPayId;
	
	private String webhookFailedCount;
	private String webhookPayInResponseFields;
	private String webhookPayOutResponseFields;
	private String webhookEnable;

	public String getEnquiryApiUrlPayout() {
		return enquiryApiUrlPayout;
	}

	public void setEnquiryApiUrlPayout(String enquiryApiUrlPayout) {
		this.enquiryApiUrlPayout = enquiryApiUrlPayout;
	}

	public String getAcquirerNamePayout() {
		return acquirerNamePayout;
	}

	public void setAcquirerNamePayout(String acquirerNamePayout) {
		this.acquirerNamePayout = acquirerNamePayout;
	}

	public String getPayoutStatusEnquiryAPi() {
		return payoutStatusEnquiryAPi;
	}

	public void setPayoutStatusEnquiryAPi(String payoutStatusEnquiryAPi) {
		this.payoutStatusEnquiryAPi = payoutStatusEnquiryAPi;
	}

	public String getMinutesBeforePayoutEnquiry() {
		return minutesBeforePayoutEnquiry;
	}

	public void setMinutesBeforePayoutEnquiry(String minutesBeforePayoutEnquiry) {
		this.minutesBeforePayoutEnquiry = minutesBeforePayoutEnquiry;
	}

	public String getMinutesIntervalPayoutEnquiry() {
		return minutesIntervalPayoutEnquiry;
	}

	public void setMinutesIntervalPayoutEnquiry(String minutesIntervalPayoutEnquiry) {
		this.minutesIntervalPayoutEnquiry = minutesIntervalPayoutEnquiry;
	}

	public String getSalePayoutUrl() {
		return salePayoutUrl;
	}

	public void setSalePayoutUrl(String salePayoutUrl) {
		this.salePayoutUrl = salePayoutUrl;
	}

	public String getBsesEmailApiUrl() {
		return bsesEmailApiUrl;
	}

	public void setBsesEmailApiUrl(String bsesEmailApiUrl) {
		this.bsesEmailApiUrl = bsesEmailApiUrl;
	}

	public String getChargebackApiUrl() {
		return chargebackApiUrl;
	}

	public void setChargebackApiUrl(String chargebackApiUrl) {
		this.chargebackApiUrl = chargebackApiUrl;
	}

	public String getMONGO_DB_dbName() {
		return MONGO_DB_dbName;
	}

	public void setMONGO_DB_dbName(String mONGO_DB_dbName) {
		MONGO_DB_dbName = mONGO_DB_dbName;
	}

	public String getMONGO_DB_collectionName() {
		return MONGO_DB_collectionName;
	}

	public void setMONGO_DB_collectionName(String mONGO_DB_collectionName) {
		MONGO_DB_collectionName = mONGO_DB_collectionName;
	}

	public String getMONGO_DB_host() {
		return MONGO_DB_host;
	}

	public void setMONGO_DB_host(String mONGO_DB_host) {
		MONGO_DB_host = mONGO_DB_host;
	}

	public String getMONGO_DB_port() {
		return MONGO_DB_port;
	}

	public void setMONGO_DB_port(String mONGO_DB_port) {
		MONGO_DB_port = mONGO_DB_port;
	}

	public String getMONGO_DB_mongoURIprefix() {
		return MONGO_DB_mongoURIprefix;
	}

	public void setMONGO_DB_mongoURIprefix(String mONGO_DB_mongoURIprefix) {
		MONGO_DB_mongoURIprefix = mONGO_DB_mongoURIprefix;
	}

	public String getMONGO_DB_mongoURIsuffix() {
		return MONGO_DB_mongoURIsuffix;
	}

	public void setMONGO_DB_mongoURIsuffix(String mONGO_DB_mongoURIsuffix) {
		MONGO_DB_mongoURIsuffix = mONGO_DB_mongoURIsuffix;
	}

	public String getMONGO_DB_username() {
		return MONGO_DB_username;
	}

	public void setMONGO_DB_username(String mONGO_DB_username) {
		MONGO_DB_username = mONGO_DB_username;
	}

	public String getMONGO_DB_password() {
		return MONGO_DB_password;
	}

	public void setMONGO_DB_password(String mONGO_DB_password) {
		MONGO_DB_password = mONGO_DB_password;
	}

	public String getMONGO_DB_reportingCollectionName() {
		return MONGO_DB_reportingCollectionName;
	}

	public void setMONGO_DB_reportingCollectionName(String mONGO_DB_reportingCollectionName) {
		MONGO_DB_reportingCollectionName = mONGO_DB_reportingCollectionName;
	}

	public String getMONGO_DB_settlementCollectionName() {
		return MONGO_DB_settlementCollectionName;
	}

	public void setMONGO_DB_settlementCollectionName(String mONGO_DB_settlementCollectionName) {
		MONGO_DB_settlementCollectionName = mONGO_DB_settlementCollectionName;
	}

	public String getMONGO_DB_statusEnquiryCollectionName() {
		return MONGO_DB_statusEnquiryCollectionName;
	}

	public void setMONGO_DB_statusEnquiryCollectionName(String mONGO_DB_statusEnquiryCollectionName) {
		MONGO_DB_statusEnquiryCollectionName = mONGO_DB_statusEnquiryCollectionName;
	}

	public String getMONGO_DB_summaryTransactionsCollectionName() {
		return MONGO_DB_summaryTransactionsCollectionName;
	}

	public void setMONGO_DB_summaryTransactionsCollectionName(String mONGO_DB_summaryTransactionsCollectionName) {
		MONGO_DB_summaryTransactionsCollectionName = mONGO_DB_summaryTransactionsCollectionName;
	}

	public String getTransactionStatusEnquiry() {
		return transactionStatusEnquiry;
	}

	public void setTransactionStatusEnquiry(String transactionStatusEnquiry) {
		this.transactionStatusEnquiry = transactionStatusEnquiry;
	}

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public String getEnquiryApiUrl() {
		return enquiryApiUrl;
	}

	public String getCommissionSchedulerApiUrl() {
		return commissionSchedulerApiUrl;
	}

	public void setCommissionSchedulerApiUrl(String commissionSchedulerApiUrl) {
		this.commissionSchedulerApiUrl = commissionSchedulerApiUrl;
	}

	public String getResellerMonthlyPayoutSchedulerApiUrl() {
		return resellerMonthlyPayoutSchedulerApiUrl;
	}

	public void setResellerMonthlyPayoutSchedulerApiUrl(String resellerMonthlyPayoutSchedulerApiUrl) {
		this.resellerMonthlyPayoutSchedulerApiUrl = resellerMonthlyPayoutSchedulerApiUrl;
	}

	public String getResellerQuaterlyPayoutSchedulerApiUrl() {
		return resellerQuaterlyPayoutSchedulerApiUrl;
	}

	public void setResellerQuaterlyPayoutSchedulerApiUrl(String resellerQuaterlyPayoutSchedulerApiUrl) {
		this.resellerQuaterlyPayoutSchedulerApiUrl = resellerQuaterlyPayoutSchedulerApiUrl;
	}

	public void setEnquiryApiUrl(String enquiryApiUrl) {
		this.enquiryApiUrl = enquiryApiUrl;
	}

	public String getPendingTransactionStatusUpdateCron() {
		return pendingTransactionStatusUpdateCron;
	}

	public void setPendingTransactionStatusUpdateCron(String pendingTransactionStatusUpdateCron) {
		this.pendingTransactionStatusUpdateCron = pendingTransactionStatusUpdateCron;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

	public String getHoursBefore() {
		return hoursBefore;
	}

	public void setHoursBefore(String hoursBefore) {
		this.hoursBefore = hoursBefore;
	}

	public String getHoursInterval() {
		return hoursInterval;
	}

	public void setHoursInterval(String hoursInterval) {
		this.hoursInterval = hoursInterval;
	}

	public String getMinutesBefore() {
		return minutesBefore;
	}

	public String getMinutesBeforePayout() {
		return minutesBeforePayout;
	}

	public void setMinutesBeforePayout(String minutesBeforePayout) {
		this.minutesBeforePayout = minutesBeforePayout;
	}

	public String getMinutesIntervalPayout() {
		return minutesIntervalPayout;
	}

	public void setMinutesIntervalPayout(String minutesIntervalPayout) {
		this.minutesIntervalPayout = minutesIntervalPayout;
	}

	public String getAcquirerMasterSwitch() {
		return AcquirerMasterSwitch;
	}

	public void setAcquirerMasterSwitch(String acquirerMasterSwitch) {
		AcquirerMasterSwitch = acquirerMasterSwitch;
	}

	public void setMinutesBefore(String minutesBefore) {
		this.minutesBefore = minutesBefore;
	}

	public String getMinutesInterval() {
		return minutesInterval;
	}

	public void setMinutesInterval(String minutesInterval) {
		this.minutesInterval = minutesInterval;
	}

	public String getTransactionBankStatusEnquiryCron() {
		return transactionBankStatusEnquiryCron;
	}

	public void setTransactionBankStatusEnquiryCron(String transactionBankStatusEnquiryCron) {
		this.transactionBankStatusEnquiryCron = transactionBankStatusEnquiryCron;
	}

	public String getSmsApiUrl() {
		return smsApiUrl;
	}

	public void setSmsApiUrl(String smsApiUrl) {
		this.smsApiUrl = smsApiUrl;
	}

	public String getStatusEnquiryApiUrl() {
		return statusEnquiryApiUrl;
	}

	public void setStatusEnquiryApiUrl(String statusEnquiryApiUrl) {
		this.statusEnquiryApiUrl = statusEnquiryApiUrl;
	}

	public String getRefundApiUrl() {
		return refundApiUrl;
	}

	public void setRefundApiUrl(String refundApiUrl) {
		this.refundApiUrl = refundApiUrl;
	}

	public String getOrderConfirmApi() {
		return orderConfirmApi;
	}

	public void setOrderConfirmApi(String orderConfirmApi) {
		this.orderConfirmApi = orderConfirmApi;
	}

	public String getAlertTimdiff() {
		return AlertTimdiff;
	}

	public void setAlertTimdiff(String alertTimdiff) {
		AlertTimdiff = alertTimdiff;
	}

	public String getSlackToken() {
		return SlackToken;
	}

	public void setSlackToken(String slackToken) {
		SlackToken = slackToken;
	}

	public String getSlackchannel() {
		return Slackchannel;
	}

	public void setSlackchannel(String slackchannel) {
		Slackchannel = slackchannel;
	}

	public String getRefundServiceApiUrl() {
		return refundServiceApiUrl;
	}

	public void setRefundServiceApiUrl(String refundServiceApiUrl) {
		this.refundServiceApiUrl = refundServiceApiUrl;
	}

	public String getRefundFileLocation() {
		return refundFileLocation;
	}

	public void setRefundFileLocation(String refundFileLocation) {
		this.refundFileLocation = refundFileLocation;
	}

	public String getRefundAckFileLocation() {
		return refundAckFileLocation;
	}

	public void setRefundAckFileLocation(String refundAckFileLocation) {
		this.refundAckFileLocation = refundAckFileLocation;
	}

	public String getRefundSuccessFileLocation() {
		return refundSuccessFileLocation;
	}

	public void setRefundSuccessFileLocation(String refundSuccessFileLocation) {
		this.refundSuccessFileLocation = refundSuccessFileLocation;
	}

	public String getBulkRefundUrl() {
		return bulkRefundUrl;
	}

	public void setBulkRefundUrl(String bulkRefundUrl) {
		this.bulkRefundUrl = bulkRefundUrl;
	}

	public String getBulkRefundConfig() {
		return bulkRefundConfig;
	}

	public void setBulkRefundConfig(String bulkRefundConfig) {
		this.bulkRefundConfig = bulkRefundConfig;
	}

	public String getResponseCodeAcqNotification() {
		return responseCodeAcqNotification;
	}

	public void setResponseCodeAcqNotification(String responseCodeAcqNotification) {
		this.responseCodeAcqNotification = responseCodeAcqNotification;
	}

	public String getPayuStatusEnquiryApiUrl() {
		return payuStatusEnquiryApiUrl;
	}

	public void setPayuStatusEnquiryApiUrl(String payuStatusEnquiryApiUrl) {
		this.payuStatusEnquiryApiUrl = payuStatusEnquiryApiUrl;
	}

	public String getChargebackAutoCloseUrl() {
		return chargebackAutoCloseUrl;
	}

	public void setChargebackAutoCloseUrl(String chargebackAutoClose) {
		this.chargebackAutoCloseUrl = chargebackAutoCloseUrl;
	}

	public String getIrctcPayId() {
		return irctcPayId;
	}

	public void setIrctcPayId(String irctcPayId) {
		this.irctcPayId = irctcPayId;
	}

	public String getWebhookFailedCount() {
		return webhookFailedCount;
	}

	public void setWebhookFailedCount(String webhookFailedCount) {
		this.webhookFailedCount = webhookFailedCount;
	}

	public String getWebhookPayInResponseFields() {
		return webhookPayInResponseFields;
	}

	public void setWebhookPayInResponseFields(String webhookPayInResponseFields) {
		this.webhookPayInResponseFields = webhookPayInResponseFields;
	}

	public String getWebhookPayOutResponseFields() {
		return webhookPayOutResponseFields;
	}

	public void setWebhookPayOutResponseFields(String webhookPayOutResponseFields) {
		this.webhookPayOutResponseFields = webhookPayOutResponseFields;
	}

	public String getWebhookEnable() {
		return webhookEnable;
	}

	public void setWebhookEnable(String webhookEnable) {
		this.webhookEnable = webhookEnable;
	}
	

}