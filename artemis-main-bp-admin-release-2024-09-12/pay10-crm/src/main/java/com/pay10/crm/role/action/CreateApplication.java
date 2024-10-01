package com.pay10.crm.role.action;

import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.ResponseEnvelope;
import com.pay10.crm.role.Dto.UserRoleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class CreateApplication  extends AbstractSecureAction {

    private static Logger logger = LoggerFactory.getLogger(CreateApplication.class.getName());

    public String execute() {

            return SUCCESS;

    }

}
