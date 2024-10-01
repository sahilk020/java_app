package com.pay10.crm.action;

import java.util.Map;
import java.util.TreeMap;

import com.pay10.commons.util.BusinessType;

/**
 * @author Rahul
 *
 */
public class DisplayServiceTaxPage extends AbstractSecureAction{

	private static final long serialVersionUID = -3054395368649186158L;
	private Map<String, String> industryCategoryList = new TreeMap<String, String>();

	@Override
	public String execute(){
		Map<String,String>	industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();
		 industryCategoryList.putAll(industryCategoryLinkedMap);
		return INPUT;
		
	}
	

	public Map<String, String> getIndustryCategoryList() {
		return industryCategoryList;
	}

	public void setIndustryCategoryList(Map<String, String> industryCategoryList) {
		this.industryCategoryList = industryCategoryList;
	}
}