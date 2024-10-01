package com.pay10.crm.role.action;

import com.pay10.crm.action.AbstractSecureAction;
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

public class AddMenu extends AbstractSecureAction {

    private static final Logger logger = LoggerFactory.getLogger(AddMenu.class.getName());
    private MenuDto menuDto= new MenuDto();

    private String menu;
    private String title;
    private Long parentId;
    private String link;
    private Long applicationId;
    private String permission;


    @Override
    public String execute() {
        logger.info("menu....={}"+menu);
        logger.info("title....={}"+title);
        logger.info("parentId...={}"+parentId);
        logger.info("link.....={}"+link);
        logger.info("applicationId....={}"+applicationId);
        logger.info("permission...={}"+permission);


        menuDto.setMenuName(menu);
        menuDto.setDescription(title);
        menuDto.setParentId(parentId);
        menuDto.setActionName(link);
        menuDto.setApplicationId(applicationId);
        menuDto.setPermission(permission);

        //String url="http://10.0.1.165:80/auth-service/api/v1/menu";
        String url="https://globaldev.pay10.com/auth-service/api/v1/menu";

        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        HttpEntity requestEntity = new HttpEntity(menuDto, headers);
        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<ResponseEnvelope<MenuDto>> reponse =
                new ParameterizedTypeReference<ResponseEnvelope<MenuDto>>() {};
        ResponseEntity<ResponseEnvelope<MenuDto>> response = restTemplate.exchange(url, HttpMethod.POST,requestEntity, reponse);
        return SUCCESS;

    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }




}