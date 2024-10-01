package com.pay10.batch.commons.util;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Service("systemProperties")
public class SystemProperties {
	
	private List<String> transactionFields;
	private List<String> reportingFields;
	private Map<String, String> currencies;
	
	public List<String> getTransactionFields() {
		return transactionFields;
	}

	public List<String> getReportingFields() {
		return reportingFields;
	}

	public void setTransactionFields(List<String> transactionFields) {
		this.transactionFields = transactionFields;
	}

	public void setReportingFields(List<String> reportingFields) {
		this.reportingFields = reportingFields;
	}

	public Map<String, String> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Map<String, String> currencies) {
		this.currencies = currencies;
	}

}
