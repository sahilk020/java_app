package com.pay10.crm.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gson.GsonBuilder;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.util.BusinessType;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.TDRStatus;

public class ServiceTaxPlatformAction extends AbstractSecureAction {

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(ServiceTaxPlatformAction.class.getName());
	private static final long serialVersionUID = -6879974923614009981L;

	private Map<String, List<ServiceTax>> serviceTaxData = new HashMap<String, List<ServiceTax>>();
	private Map<String, String> industryCategoryList = new TreeMap<String, String>();
	private String businessType;

	@Override
	public String execute() {

		logger.info("CR ServiceTaxPlatformAction BusinessType : "+businessType);

		try {

			setIndustryCategoryList(BusinessType.getIndustryCategoryList());
			List<ServiceTax> serviceTaxList = new ArrayList<ServiceTax>();
			Map<String, String> businessTypeList = BusinessType.getIndustryCategoryList();
			ServiceTax serviceTax = new ServiceTax();
			if (businessType != "All") {

				logger.info("IF CR ServiceTaxPlatformAction BusinessType : "+businessType);

				serviceTax = serviceTaxDao.findServiceTax(businessType);

				logger.info("IF CR ServiceTaxPlatformAction ServiceTax : "+serviceTax);

				if (serviceTax != null) {
					serviceTaxList.add(serviceTax);
				} else {
					ServiceTax serviceTaxUndefined = new ServiceTax();
					serviceTaxUndefined.setServiceTax(BigDecimal.ZERO);
					serviceTaxUndefined.setBusinessType(businessType);
					serviceTaxUndefined.setStatus(TDRStatus.INACTIVE);
					serviceTaxList.add(serviceTaxUndefined);
				}
			}
			else {

				logger.info("Else CR ServiceTaxPlatformAction BusinessType : "+new GsonBuilder().disableHtmlEscaping().create().toJson(businessTypeList));

				for (Entry<String, String> businessType : businessTypeList.entrySet()) {

					serviceTax = serviceTaxDao.findServiceTax(businessType.getValue());

					if (serviceTax != null) {
						serviceTaxList.add(serviceTax);
					} else {
						ServiceTax serviceTaxUndefined = new ServiceTax();
						serviceTaxUndefined.setServiceTax(BigDecimal.ZERO);
						serviceTaxUndefined.setBusinessType(businessType.getValue());
						serviceTaxUndefined.setStatus(TDRStatus.INACTIVE);
						serviceTaxList.add(serviceTaxUndefined);
					}

				}
			}

			serviceTaxData.put("Local Tax Rate", serviceTaxList);
			if (serviceTaxData.size() == 0) {
				addActionMessage(ErrorType.LOCAL_TAX_RATES_NOT_AVAILABLE.getResponseMessage());
				addActionMessage(ErrorType.LOCAL_TAX_RATES_NOT_AVAILABLE.getResponseMessage());
				addActionMessage(ErrorType.UNKNOWN.getResponseMessage());
				return INPUT;

			}
		}

		catch (Exception e) {
			logger.error("Service tax load failed " + e);
			addActionMessage(ErrorType.LOCAL_TAX_RATES_NOT_AVAILABLE.getResponseMessage());
			addActionMessage(ErrorType.UNKNOWN.getResponseMessage());
			return ERROR;
		}
		
		return INPUT;
	}




	@Override
	public void validate() {
		if (validator.validateBlankField(getBusinessType())) {
			//addFieldError(CrmFieldType.BUSINESS_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.BUSINESS_NAME, getBusinessType()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}
		 Map<String,String>	industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();
		 industryCategoryList.putAll(industryCategoryLinkedMap);
	}

	public String display() {
		return INPUT;
	}

	public Map<String, List<ServiceTax>> getServiceTaxData() {
		return serviceTaxData;
	}

	public void setServiceTaxData(Map<String, List<ServiceTax>> serviceTaxData) {
		this.serviceTaxData = serviceTaxData;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Map<String, String> getIndustryCategoryList() {
		return industryCategoryList;
	}

	public void setIndustryCategoryList(Map<String, String> industryCategoryList) {
		this.industryCategoryList = industryCategoryList;
	}

}
