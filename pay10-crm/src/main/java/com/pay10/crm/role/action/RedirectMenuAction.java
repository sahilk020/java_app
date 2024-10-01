package com.pay10.crm.role.action;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.MenuDto;
import com.pay10.crm.role.Dto.ResponseEnvelope;
import org.apache.struts.action.ActionForward;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class RedirectMenuAction extends AbstractSecureAction {

    private static final Logger logger = LoggerFactory.getLogger(RedirectMenuAction.class.getName());

    private String menuId;
    private String redirectUrl;


    @Override
    public String execute() {
        try {
           // MenuDto menuDto=new MenuDto();
            User user = (User) sessionMap.get(Constants.USER.getValue());

            logger.info("RedirectMenuAction user...={}",user.getPayId());
           // String url="http://10.0.1.169:80/auth-service/api/v1/menu/"+menuId;
            String url="https://globaldev.pay10.com/auth-service/api/v1/menu/"+menuId;

            logger.info("url in get menu details by Id");
            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
           // HttpEntity requestEntity = new HttpEntity(menuDto, headers);
            RestTemplate restTemplate = new RestTemplate();

            ParameterizedTypeReference<ResponseEnvelope<MenuDto>> reponse =
                    new ParameterizedTypeReference<ResponseEnvelope<MenuDto>>() {};
            ResponseEntity<ResponseEnvelope<MenuDto>> response = restTemplate.exchange(url, HttpMethod.GET,null, reponse);
            MenuDto menuDto=response.getBody().getPayLoad();
            logger.info("RedirectMenuAction......={}",response.getBody().getPayLoad());
            logger.info("RedirectMenuAction actionName......={}",menuDto.getActionName());
            String request = menuDto.getActionName();
            HttpServletResponse res=ServletActionContext.getResponse();
            //res.sendRedirect("http://localhost:4200/#"+request+"?username="+user.getPayId());
            //res.sendRedirect("http://localhost:4200/#"+request+"?username="+user.getEmailId());
            //res.sendRedirect("https://globaldev.pay10.com/#"+request+"?username="+user.getPayId());
            res.sendRedirect("https://globaldev.pay10.com/#"+request+"?username="+user.getEmailId());

            return "redirecturl";
        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }


    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }


    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

}