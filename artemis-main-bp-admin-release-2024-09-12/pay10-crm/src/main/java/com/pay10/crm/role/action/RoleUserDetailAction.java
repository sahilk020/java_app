package com.pay10.crm.role.action;

import com.pay10.commons.user.Role;
import com.pay10.commons.user.UserGroup;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.UserRoleDto;
import com.pay10.crm.role.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import com.pay10.crm.role.Dto.ResponseEnvelope;

/**
 * @author Sweety
 *
 */
public class RoleUserDetailAction extends AbstractSecureAction {

	private static final long serialVersionUID = 3255958300818815691L;
	private static Logger logger = LoggerFactory.getLogger(RoleUserDetailAction.class.getName());

	@Autowired
	private RoleService roleService;

	private List<UserRoleDto> aaData;

	@Override
	public String execute() {
		try {
			List<UserRoleDto> list= new ArrayList<UserRoleDto>();

			//String url="http://10.0.1.169:80/auth-service/api/v1/role";
			String url="https://globaldev.pay10.com/auth-service/api/v1/role";
			RestTemplate restTemplate = new RestTemplate();

			ParameterizedTypeReference<ResponseEnvelope<List<UserRoleDto>>> reponse = new ParameterizedTypeReference<ResponseEnvelope<List<UserRoleDto>>>() {};
			ResponseEntity<ResponseEnvelope<List<UserRoleDto>>> response = restTemplate.exchange(url, HttpMethod.GET,null, reponse);
           logger.info("role list ......={}",response.getBody().getPayLoad());
            list=response.getBody().getPayLoad();
			setAaData(list);
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public List<UserRoleDto> getAaData() {
		return aaData;
	}

	public void setAaData(List<UserRoleDto> aaData) {
		this.aaData = aaData;
	}
}
