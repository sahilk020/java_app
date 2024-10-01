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

public class UpdateMenuDetails extends AbstractSecureAction {

    private static final Logger logger = LoggerFactory.getLogger(UpdateMenuDetails.class.getName());

    private String id;
    private String menuName;
    private String title;
    private Long parentId;
    private String link;
    private Long applicationId;
    private String permission;
    private MenuDto menuDto= new MenuDto();
    @Override
    public String execute() {
        try {
            logger.info("UpdateMenuDetails id....={}",id);
            logger.info(" UpdateMenuDetails menuName....={}",menuName);
            logger.info(" UpdateMenuDetails title....={}",title);
            logger.info(" UpdateMenuDetailsparentId....={}",parentId);
            logger.info("UpdateMenuDetailslink...{}",link);
            logger.info(" UpdateMenuDetailsapplicationId...={}",applicationId);
            logger.info("UpdateMenuDetails permission...={}",permission);

            menuDto.setMenuName(menuName);
            menuDto.setDescription(title);
            menuDto.setParentId(parentId);
            menuDto.setActionName(link);
            menuDto.setApplicationId(applicationId);
            menuDto.setPermission(permission);
            //String url="http://10.0.1.165:80/auth-service/api/v1/menu/"+id;
            String url="https://globaldev.pay10.com/auth-service/api/v1/menu/"+id;

            logger.info("url in update details");
            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            HttpEntity requestEntity = new HttpEntity(menuDto, headers);
            RestTemplate restTemplate = new RestTemplate();

            ParameterizedTypeReference<ResponseEnvelope<MenuDto>> reponse =
                    new ParameterizedTypeReference<ResponseEnvelope<MenuDto>>() {};
            ResponseEntity<ResponseEnvelope<MenuDto>> response = restTemplate.exchange(url, HttpMethod.POST,requestEntity, reponse);
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }


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