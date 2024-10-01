package com.pay10.crm.action;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import com.pay10.commons.util.BusinessType;

/**
 * @author Puneet
 *
 */
public class MerchantListRedirectAction extends AbstractSecureAction {
	private static Logger logger = LoggerFactory.getLogger(MerchantListRedirectAction.class.getName());
	private static final long serialVersionUID = -2541609533197706532L;
	private Map<String, String> industryTypes = new TreeMap<String, String>();

	@Override
	public String execute() {
		try {
			Map<String,String>	industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();
			industryTypes.putAll(industryCategoryLinkedMap);
			return INPUT;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	}

	public Map<String, String> getIndustryTypes() {
		return industryTypes;
	}

	public void setIndustryTypes(Map<String, String> industryTypes) {
		this.industryTypes = industryTypes;
	}

}
