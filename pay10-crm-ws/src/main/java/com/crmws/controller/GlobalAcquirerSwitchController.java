package com.crmws.controller;

import com.crmws.dto.AcquirerDTO;
import com.crmws.dto.AcquirerSwitchDto;
import com.crmws.dto.ApiResponse;
import com.crmws.service.GlobalAcquirerSwitchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.user.*;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PaymentTypeUI;
import com.pay10.commons.util.PropertiesManager;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/GlobalAcquirerSwitch")
public class GlobalAcquirerSwitchController {

    @Autowired
    GlobalAcquirerSwitchService globalAcquirerSwitchService;

    @Autowired
    UserDao userDao;

    @Autowired
    AcquirerDowntimeSchedulingDao acquirerDowntimeSchedulingDao;

    @Autowired
    RouterConfigurationDao routerConfigurationDao;

    private static final Logger logger = LoggerFactory.getLogger(GlobalAcquirerSwitchController.class.getName());


    @PostMapping("/getGlobalMappedAcquirer")
    public ApiResponse getAcquirerMasterList(@RequestParam String acquirerType, @RequestParam String paymentType) {
        ApiResponse apiResponse = new ApiResponse();
        List<AcquirerSwitchDto> rulesList = new ArrayList<AcquirerSwitchDto>();

        logger.info("acquirerType..={},PaymentType..={}",acquirerType,paymentType);
        List<RouterConfiguration> list= globalAcquirerSwitchService.getMappedListByAcquirer(acquirerType,paymentType);

        for(RouterConfiguration data:list){
            AcquirerSwitchDto acquirer=new AcquirerSwitchDto();
            acquirer.setMopType( MopType.getInstanceUsingCode(data.getMopType()).getName());
            acquirer.setPayId(data.getMerchant());
            acquirer.setMerchantName(userDao.getBusinessNameByPayId(data.getMerchant()));
            rulesList.add(acquirer);
        }

        if (!list.isEmpty()) {
            apiResponse.setMessage("Data Fetch Successfully");
            apiResponse.setData(rulesList);
            apiResponse.setStatus(true);
            return apiResponse;
        } else {
            if (paymentType.equalsIgnoreCase("ALL")){
                String response = acquirerType+" acquirer is down for "+paymentType+" payment type";
                apiResponse.setMessage(response);
                apiResponse.setData("");
                apiResponse.setStatus(false);
            }else {
                String response = acquirerType+" acquirer is down for "+PaymentType.getInstanceUsingCode(paymentType).getName()+" payment type";
                apiResponse.setMessage(response);
                apiResponse.setData("");
                apiResponse.setStatus(false);
            }

            return apiResponse;
        }
    }


    @PostMapping("/getGlobalPaymentType")
    public ApiResponse getPaymentType(@RequestParam String acquirer) {
        ApiResponse apiResponse = new ApiResponse();
        logger.info("acquirer by payment type={}",acquirer);
        List<String> list= globalAcquirerSwitchService.getPaymentTypeList(acquirer);
        List<AcquirerDTO> data= new ArrayList<AcquirerDTO>();

        for(String payment:list) {
            AcquirerDTO data1=new AcquirerDTO();
            logger.info("Loop PT ::"+payment);

            if (payment.equalsIgnoreCase("ALL")){
                data1.setCode(payment);
                data1.setName("ALL");
                data.add(data1);
            }else {
                data1.setCode(payment);
                data1.setName(PaymentType.getInstanceUsingCode(payment).getName());
                data.add(data1);
            }
        }
        logger.info("PaymentType Data ::"+data);

        if (!list.isEmpty()) {
            apiResponse.setMessage("Data Fetch Successfully");
            apiResponse.setData(data);
            apiResponse.setStatus(true);
            return apiResponse;
        } else {
            apiResponse.setMessage("No Data Found");
            apiResponse.setData("");
            apiResponse.setStatus(false);
            return apiResponse;
        }
    }


    @PostMapping("/changeGlobalStatusInactive")
    public ApiResponse changeStatusForAcquirerMaster(@RequestParam String acquirer, @RequestParam String paymentType,@RequestParam String updateby) {
        ApiResponse apiResponse = new ApiResponse();

        logger.info("acquirerType..={},PaymentType..={}",acquirer,paymentType);
        List<RouterConfiguration> list= globalAcquirerSwitchService.getMappedListByAcquirerForDelete(acquirer,paymentType);
        AcquirerSwitchHistory acquirerSwitchHistory=new AcquirerSwitchHistory();
        acquirerSwitchHistory.setAcquirerName(acquirer);
        acquirerSwitchHistory.setPaymentType(paymentType);
        acquirerSwitchHistory.setUpDateBy(updateby);
        acquirerSwitchHistory.setUpdateOn(getCurrentDate());
        acquirerSwitchHistory.setStatus("INACTIVE");
        acquirerDowntimeSchedulingDao.createAndUpdate(acquirerSwitchHistory);

        list.stream().forEach((k) -> {

            k.setCurrentlyActive(false);
            k.setAcquirerSwitch(true);
            k.setAcUpdateBy(updateby);
            k.setAcUpdateOn(getCurrentDate());

            routerConfigurationDao.deleteAcquirerMaster(k);

        });
        acquirerMasterSwitchNotification( PaymentTypeUI.getpaymentName(paymentType),  acquirer,  updateby, "INACTIVE", getCurrentDate()) ;


        apiResponse.setMessage("Rule is Create SuccessFull");
        apiResponse.setData("");
        apiResponse.setStatus(true);
        return apiResponse;

    }


    @PostMapping("/changeGlobalStatusActive")
    public ApiResponse changeStatusActive(@RequestParam String acquirer, @RequestParam String paymentType,@RequestParam String updateby ) {
        ApiResponse apiResponse = new ApiResponse();

        logger.info("acquirerType..={},PaymentType..={}",acquirer,paymentType);
        List<RouterConfiguration> list= routerConfigurationDao.changeGlobalStatusActive(acquirer,paymentType);
        AcquirerSwitchHistory acquirerSwitchHistory=new AcquirerSwitchHistory();

        acquirerSwitchHistory.setAcquirerName(acquirer);
        acquirerSwitchHistory.setPaymentType(paymentType);
        acquirerSwitchHistory.setUpDateBy(updateby);
        acquirerSwitchHistory.setUpdateOn(getCurrentDate());
        acquirerSwitchHistory.setStatus("ACTIVE");
        acquirerDowntimeSchedulingDao.createAndUpdate(acquirerSwitchHistory);

        if(list.size()>0) {
            list.stream().forEach((k) -> {

                k.setCurrentlyActive(true);
                k.setAcquirerSwitch(false);
                k.setAcUpdateBy(updateby);
                k.setAcUpdateOn(getCurrentDate());
                routerConfigurationDao.deleteAcquirerMaster(k);

            });
            acquirerMasterSwitchNotification( PaymentTypeUI.getpaymentName(paymentType),  acquirer,  updateby, "ACTIVE", getCurrentDate()) ;
            apiResponse.setMessage("Status Change SuccessFull");
            apiResponse.setData(list);
            apiResponse.setStatus(true);
            return apiResponse;

        }else {
            apiResponse.setMessage("No Rule is found ");
            apiResponse.setData(list);
            apiResponse.setStatus(true);
            return apiResponse;
        }
    }


    public String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }


    public void acquirerMasterSwitchNotification(String payment, String acquirer, String updateBy,String status,String updateOn) {

        String responseBody = "";
        Map<String, String> resMap = new HashMap<String, String>();
        try {
            logger.info("send failedTxnsNotification");
            String serviceUrl = PropertiesManager.propertiesMap.get("EmailAcquirerMasterSwitchNotificationURL");

            JSONObject json = new JSONObject();
            json.put("PAYMENTTYPE", payment);
            json.put("ACQUIRER", acquirer);
            json.put("UPDATEBY", updateBy);
            json.put("STATUS", status);
            json.put("UPDATEDON", updateOn);


            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(serviceUrl);
            StringEntity params = new StringEntity(json.toString());
            request.addHeader("Content-type", "application/json");
            request.setEntity(params);
            CloseableHttpResponse resp = httpClient.execute(request);
            responseBody = EntityUtils.toString(resp.getEntity());
            final ObjectMapper mapper = new ObjectMapper();
            final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
            resMap = mapper.readValue(responseBody, type);

        } catch (Exception exception) {
            logger.error("error" + exception);
        }
    }

}
