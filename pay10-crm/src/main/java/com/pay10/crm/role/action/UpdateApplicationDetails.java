package com.pay10.crm.role.action;

import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.ApplicationDto;
import com.pay10.crm.role.Dto.ResponseEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class UpdateApplicationDetails extends AbstractSecureAction {

    private static final Logger logger = LoggerFactory.getLogger(UpdateApplicationDetails.class.getName());
    private ApplicationDto appDto= new ApplicationDto();
    private String name;
    private String url;
    private String image;
    private String id;


    @Override
    public String execute() {
        appDto.setName(name);
        appDto.setUrl(url);
        appDto.setImage(image);

        //String url="http://10.0.1.165:80/auth-service/api/v1/client/"+id;
        String url="https://globaldev.pay10.com/auth-service/api/v1/client/"+id;

        logger.info("url in update details");
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        HttpEntity requestEntity = new HttpEntity(appDto, headers);
        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<ResponseEnvelope<ApplicationDto>> reponse =
                new ParameterizedTypeReference<ResponseEnvelope<ApplicationDto>>() {};
        ResponseEntity<ResponseEnvelope<ApplicationDto>> response = restTemplate.exchange(url, HttpMethod.POST,requestEntity, reponse);
        return SUCCESS;


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}