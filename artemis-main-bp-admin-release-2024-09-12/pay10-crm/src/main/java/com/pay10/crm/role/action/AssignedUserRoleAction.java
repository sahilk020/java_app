package com.pay10.crm.role.action;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.AssignUserDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;



public class AssignedUserRoleAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(AssignedUserRoleAction.class.getName());


	@Autowired
	private UserDao userDao;

	private String payId;
	private Long roleId;

	@Override
	public String execute() {
		try {
           logger.info("payId for Assigned User....={}",payId);
			logger.info("roleId for Assigned User....={}",roleId);
			for(String userId:payId.split("\\s*,\\s*") ){
				User user = userDao.findPayId(userId);
				AssignUserDto assignUserDto = new AssignUserDto();
				assignUserDto.setFullName(user.getBusinessName());
				assignUserDto.setUserId(user.getPayId());
				assignUserDto.setPayId(user.getPayId());
				assignUserDto.setEmailId(user.getEmailId());
				assignUserDto.setRoleId(roleId);
				assignUserDto.setContactNumber(user.getMobile());

				//String url = "http://10.0.1.169:80/auth-service/api/v1/user";
				String url = "https://globaldev.pay10.com/auth-service/api/v1/user";

				HttpHeaders headers = new HttpHeaders();
				headers.set("accept", "application/json");
				HttpEntity requestEntity = new HttpEntity(assignUserDto, headers);
				RestTemplate restTemplate = new RestTemplate();

				ParameterizedTypeReference<AssignUserDto> reponse =
						new ParameterizedTypeReference<AssignUserDto>() {
						};
				ResponseEntity<AssignUserDto> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, reponse);
			}
			return SUCCESS;

		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	public String getPayId() {
		return payId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}
}
