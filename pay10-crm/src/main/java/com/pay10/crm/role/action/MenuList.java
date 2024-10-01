package com.pay10.crm.role.action;

import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.ApplicationDto;
import com.pay10.crm.role.Dto.MenuDto;
import com.pay10.crm.role.Dto.ResponseEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuList extends AbstractSecureAction {

    private static Logger logger = LoggerFactory.getLogger(MenuList.class.getName());

    private List<MenuDto> aaData;

    @Override
    public String execute() {
        return SUCCESS;
    }

    public String getList(){
        try {
            List<MenuDto> list= new ArrayList<MenuDto>();
            ApplicationDto applicationList= new ApplicationDto();

            List<MenuDto> setList= new ArrayList<>();
            //String url="http://10.0.1.165:80/auth-service/api/v1/menu";
            String url="https://globaldev.pay10.com/auth-service/api/v1/menu";

            RestTemplate restTemplate = new RestTemplate();

            ParameterizedTypeReference<ResponseEnvelope<List<MenuDto>>> reponse = new ParameterizedTypeReference<ResponseEnvelope<List<MenuDto>>>() {};
            ResponseEntity<ResponseEnvelope<List<MenuDto>>> response = restTemplate.exchange(url, HttpMethod.GET,null, reponse);
            list=response.getBody().getPayLoad();
           // setAaData(list);
            for(MenuDto menuDto:list) {
                MenuDto menu= new MenuDto();
                menu.setId(menuDto.getId());
                menu.setMenuName(menuDto.getMenuName());
                menu.setActionName(menuDto.getActionName());
                menu.setParentId(menuDto.getParentId());
                menu.setDescription(menuDto.getDescription());
                menu.setPermission(menuDto.getPermission());
                menu.setApplicationId(menuDto.getApplicationId());

                logger.info("menu list ,getAadata...={}", getAaData());

                if(menuDto.getApplicationId()>0) {
                    //String applicationUrl = "http://10.0.1.165:80/auth-service/api/v1/client/" + menuDto.getApplicationId();
                    String applicationUrl = "https://globaldev.pay10.com/auth-service/api/v1/client/" + menuDto.getApplicationId();

                    RestTemplate restTemp = new RestTemplate();

                    ParameterizedTypeReference<ResponseEnvelope<ApplicationDto>> res = new ParameterizedTypeReference<ResponseEnvelope<ApplicationDto>>() {
                    };
                    ResponseEntity<ResponseEnvelope<ApplicationDto>> appResponse = restTemp.exchange(applicationUrl, HttpMethod.GET, null, res);
                    applicationList = appResponse.getBody().getPayLoad();

                     menu.setApplicationName(applicationList.getName());


                    }
                setList.add(menu);
                setAaData(setList);
                logger.info("menu list ,getAppdata...={}", getAaData());
            }


            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }

    }

    public List<MenuDto> getAaData() {
        return aaData;
    }

    public void setAaData(List<MenuDto> aaData) {
        this.aaData = aaData;
    }


}
