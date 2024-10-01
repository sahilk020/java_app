package com.pay10.crm.action;



import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.AcquirerNodalMappingDao;
import com.pay10.commons.entity.AcqurerNodalMapping;



public class AcquirerNodalMapped extends AbstractSecureAction {

	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(AcquirerNodalMapped.class.getName());
	
	@Autowired
	private AcquirerNodalMappingDao acquirerNodalMappingDao;
	
	private String acquirer;
	private String nodal;
	private String userType;
	private String token;
	private String response;
	
	@Override
	public String execute() {
		logger.info("AcquirerNodalMapped Page Executed");
		try {
			Date date=new Date();
			AcqurerNodalMapping acqurerNodalMapping=new AcqurerNodalMapping();
			acqurerNodalMapping.setAcquirer(acquirer);
			acqurerNodalMapping.setCreateAt(date);
			acqurerNodalMapping.setStartAt(date);
			acqurerNodalMapping.setCreateBy(userType);
			acqurerNodalMapping.setNodal(nodal);
			acqurerNodalMapping.setActiveFlag(true);
			setResponse(acquirerNodalMappingDao.saveAcqurerNodalMapping(acqurerNodalMapping));
		} catch (Exception e) {
			logger.error("Exception Occur in execute :",e);
			
		}
		return SUCCESS;
	}
	
	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getNodal() {
		return nodal;
	}

	public void setNodal(String nodal) {
		this.nodal = nodal;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	
	
	
}
