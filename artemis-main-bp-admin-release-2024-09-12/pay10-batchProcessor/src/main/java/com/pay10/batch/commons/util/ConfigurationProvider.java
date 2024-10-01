package com.pay10.batch.commons.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("recoMongoDbConfig")
public class ConfigurationProvider {
	private String name;
	private String uri;
	private String transactionColl;
	private String reportingColl;
	private String mongoURIprefix;
	private String mongoURIsuffix;
	private String username;
	private String password;
	private String refundAirIrctc;
	private String deltaRefundIrctcUrl;
	private String serverId;
	private String txnStatusColl;
	private String txnExcepColl;
	private String transactionStatusFields;
	private String postSettlementAutoRefundUrl;
	private String encryptedDek;

	
	public String getTransactionColl() {
		return transactionColl;
	}

	public String getReportingColl() {
		return reportingColl;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setTransactionColl(String transactionColl) {
		this.transactionColl = transactionColl;
	}

	public void setReportingColl(String reportingColl) {
		this.reportingColl = reportingColl;
	}
	
	public String getMongoURIprefix() {
		return mongoURIprefix;
	}

	public void setMongoURIprefix(String mongoURIprefix) {
		this.mongoURIprefix = mongoURIprefix;
	}

	public String getMongoURIsuffix() {
		return mongoURIsuffix;
	}

	public void setMongoURIsuffix(String mongoURIsuffix) {
		this.mongoURIsuffix = mongoURIsuffix;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public String getRefundAirIrctc() {
		return refundAirIrctc;
	}

	public void setRefundAirIrctc(String refundAirIrctc) {
		this.refundAirIrctc = refundAirIrctc;
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		return uri;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDeltaRefundIrctcUrl() {
		return deltaRefundIrctcUrl;
	}

	public void setDeltaRefundIrctcUrl(String deltaRefundIrctcUrl) {
		this.deltaRefundIrctcUrl = deltaRefundIrctcUrl;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}


	public String getTxnStatusColl() {
		return txnStatusColl;
	}

	public void setTxnStatusColl(String txnStatusColl) {
		this.txnStatusColl = txnStatusColl;
	}

	public String getTxnExcepColl() {
		return txnExcepColl;
	}

	public void setTxnExcepColl(String txnExcepColl) {
		this.txnExcepColl = txnExcepColl;
	}

	public String getTransactionStatusFields() {
		return transactionStatusFields;
	}

	public void setTransactionStatusFields(String transactionStatusFields) {
		this.transactionStatusFields = transactionStatusFields;
	}

	public String getPostSettlementAutoRefundUrl() {
		return postSettlementAutoRefundUrl;
	}

	public void setPostSettlementAutoRefundUrl(String postSettlementAutoRefundUrl) {
		this.postSettlementAutoRefundUrl = postSettlementAutoRefundUrl;
	}

	public String getEncryptedDek() {
		return encryptedDek;
	}

	public void setEncryptedDek(String encryptedDek) {
		this.encryptedDek = encryptedDek;
	}
	

}
