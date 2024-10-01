package com.pay10.crm.role.action;

import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.ApplicationDto;
import com.pay10.crm.role.Dto.MenuDto;
import com.pay10.crm.role.Dto.ResponseEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class UpdateMenu extends AbstractSecureAction {

    private static final Logger logger = LoggerFactory.getLogger(UpdateMenu.class.getName());

    private String id;
    private String menuName;
    private String title;
    private Long parentId;
    private String link;
    private Long applicationId;
    private String permission;
    private List<ApplicationDto> aaData;

    public List<ApplicationDto> getAaData() {
        return aaData;
    }

    public void setAaData(List<ApplicationDto> aaData) {
        this.aaData = aaData;
    }

    @Override
    public String execute() {
        logger.info("UpdateMenu permission...={}",permission);
        ApplicationDto list = new ApplicationDto();
        List<ApplicationDto> setList= new ArrayList<>();
       // String url = "http://10.0.1.165:80/auth-service/api/v1/client/"+applicationId;
        String url = "https://globaldev.pay10.com/auth-service/api/v1/client/"+applicationId;

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<ResponseEnvelope<ApplicationDto>> reponse = new ParameterizedTypeReference<ResponseEnvelope<ApplicationDto>>() {
        };
        ResponseEntity<ResponseEnvelope<ApplicationDto>> response = restTemplate.exchange(url, HttpMethod.GET, null, reponse);
        logger.info("Application list ......={}", response.getBody().getPayLoad());
        list = response.getBody().getPayLoad();
        setList.add(list);
        setAaData(setList);
        return SUCCESS;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenu() {
        return menuName;
    }

    public void setMenu(String menuName) {
        this.menuName = menuName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }


}