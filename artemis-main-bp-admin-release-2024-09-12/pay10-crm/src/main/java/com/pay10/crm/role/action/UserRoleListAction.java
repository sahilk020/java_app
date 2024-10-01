package com.pay10.crm.role.action;

import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.*;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.ApplicationDto;
import com.pay10.crm.role.Dto.ResponseEnvelope;
import com.pay10.crm.role.Dto.UserRoleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class UserRoleListAction extends AbstractSecureAction {

	private static final long serialVersionUID = 3429511096457216583L;
	private static final Logger logger = LoggerFactory.getLogger(UserRoleListAction.class.getName());

	private List<UserGroup> userGroups;
	private User sessionUser = null;
	private List<Merchants> merchantList = new ArrayList<Merchants>();

	private List<ApplicationDto> aaData;

	@Autowired
	UserDao userDao;

	@Override
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {
		if (sessionUser.getUserType().equals(UserType.ADMIN)
				|| sessionUser.getUserType().equals(UserType.SUBADMIN) || sessionUser.getUserType().equals(UserType.SUPERADMIN) || sessionUser.getUserType().equals(UserType.SUBSUPERADMIN)) {
			setMerchantList(userDao.getUserList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
			logger.info("MerchantList....={}",getMerchantList());
		}

			//String url = "http://10.0.1.165:80/auth-service/api/v1/group";
			String url="https://globaldev.pay10.com/auth-service/api/v1/group";
			RestTemplate restTemplate = new RestTemplate();

			ParameterizedTypeReference<ResponseEnvelope<List<UserGroup>>> res =
					new ParameterizedTypeReference<ResponseEnvelope<List<UserGroup>>>() {
					};
			ResponseEntity<ResponseEnvelope<List<UserGroup>>> response = restTemplate.exchange(url, HttpMethod.GET, null, res);
			setUserGroups(response.getBody().getPayLoad());

			//GetApplicationList

			List<ApplicationDto> list = new ArrayList<ApplicationDto>();

			//String applicationUrl = "http://10.0.1.165:80/auth-service/api/v1/client";
			String applicationUrl = "https://globaldev.pay10.com/auth-service/api/v1/client";

			RestTemplate restTemp = new RestTemplate();

			ParameterizedTypeReference<ResponseEnvelope<List<ApplicationDto>>> reponse = new ParameterizedTypeReference<ResponseEnvelope<List<ApplicationDto>>>() {
			};
			ResponseEntity<ResponseEnvelope<List<ApplicationDto>>> resp = restTemplate.exchange(applicationUrl, HttpMethod.GET, null, reponse);
			logger.info("Application list ......={}", resp.getBody().getPayLoad());
			list = resp.getBody().getPayLoad();
			setAaData(list);
			return INPUT;
		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}


	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public List<ApplicationDto> getAaData() {
		return aaData;
	}

	public void setAaData(List<ApplicationDto> aaData) {
		this.aaData = aaData;
	}
}

