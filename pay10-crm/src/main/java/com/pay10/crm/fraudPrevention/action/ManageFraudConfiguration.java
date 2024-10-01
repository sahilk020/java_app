package com.pay10.crm.fraudPrevention.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.util.FraudPreventationConfiguration;
import com.pay10.crm.action.AbstractSecureAction;

import java.util.ArrayList;
import java.util.List;

public class ManageFraudConfiguration extends AbstractSecureAction {

	private static final Logger logger = LoggerFactory.getLogger(ManageFraudConfiguration.class.getName());
	private static final long serialVersionUID = 3106916902847739022L;

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	private String payId;
	private String currency;
	private FraudPreventationConfiguration configuration;

	@Override
	public String execute() {
		try {
			configuration = fraudPreventionMongoService.getConfigByPayId(getPayId(),getCurrency());
			if (configuration == null) {
				configuration = new FraudPreventationConfiguration();
				configuration.setId(payId);
				fraudPreventionMongoService.createConfigDoc(configuration,getCurrency());
			}

		} catch (Exception ex) {
			logger.error("Exception : ", ex);
		}
		return SUCCESS;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}


	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public FraudPreventationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(FraudPreventationConfiguration configuration) {
		this.configuration = configuration;
	}

}
