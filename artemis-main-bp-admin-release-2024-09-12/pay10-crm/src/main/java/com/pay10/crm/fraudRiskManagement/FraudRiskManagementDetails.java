package com.pay10.crm.fraudRiskManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.entity.FraudManagementDto;

public class FraudRiskManagementDetails extends AbstractSecureAction {

	/**
	 * Sweety
	 */
	private static final long serialVersionUID = 5754823519804798546L;
	private static Logger logger = LoggerFactory.getLogger(FraudRiskManagementDetails.class.getName());
	private List<FraudManagementDto> aaData = new ArrayList<FraudManagementDto>();

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;
	private String dateFrom;
	private String dateTo;

	public String execute() {
		try {
			setAaData(fraudPreventionMongoService.prepareData(getDateFrom(), getDateTo()));
			// sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			logger.info("getDetails of frm aaData..={}", new Gson().toJson(getAaData()));

			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public List<FraudManagementDto> getAaData() {
		return aaData;
	}

	public void setAaData(List<FraudManagementDto> aaData) {
		this.aaData = aaData;
	}

}
