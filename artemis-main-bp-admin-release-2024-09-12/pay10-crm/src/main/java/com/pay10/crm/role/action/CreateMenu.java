package com.pay10.crm.role.action;

import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.ApplicationDto;
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

public class CreateMenu extends AbstractSecureAction {

    private static final Logger logger = LoggerFactory.getLogger(CreateMenu.class.getName());
    private List<ApplicationDto> aaData;



    @Override
    public String execute() {
        try {
            List<ApplicationDto> list = new ArrayList<ApplicationDto>();

            //String url = "http://10.0.1.165:80/auth-service/api/v1/client";
            String url = " https://globaldev.pay10.com/auth-service/api/v1/client";


            RestTemplate restTemplate = new RestTemplate();

            ParameterizedTypeReference<ResponseEnvelope<List<ApplicationDto>>> reponse = new ParameterizedTypeReference<ResponseEnvelope<List<ApplicationDto>>>() {
            };
            ResponseEntity<ResponseEnvelope<List<ApplicationDto>>> response = restTemplate.exchange(url, HttpMethod.GET, null, reponse);
            logger.info("Application list ......={}", response.getBody().getPayLoad());
            list = response.getBody().getPayLoad();
            setAaData(list);
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }
    }


    public List<ApplicationDto> getAaData() {
        return aaData;
    }

    public void setAaData(List<ApplicationDto> aaData) {
        this.aaData = aaData;
    }
}