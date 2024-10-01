package com.pay10.crm.fraudPrevention.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.crm.action.AbstractSecureAction;

import java.util.ArrayList;
import java.util.List;

public class UpdateFruadConfigurationAction extends AbstractSecureAction {

	private static final long serialVersionUID = -3764837889221585716L;
	private static final Logger logger = LoggerFactory.getLogger(UpdateFruadConfigurationAction.class.getName());

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	private String payId;
	private String key;
	private boolean value;
	private String currency ;

	@Override
	public String execute() {

		try {
			fraudPreventionMongoService.updateConfigDoc(payId,currency, key, value);
			return SUCCESS;
		} catch (Exception ex) {
			logger.error("Exception:", ex);
			return ERROR;
		}
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
