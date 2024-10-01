package com.crmws.controller;

import com.crmws.service.MerchantService;
import com.pay10.commons.exception.SystemException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class MerchantController {

    private static final Logger logger = LoggerFactory.getLogger(MerchantController.class.getName());

    @Autowired
    MerchantService merchantService;
    @PostMapping("/createMerchant")
    public String createMerchant(@RequestBody Map<String,Object> getData) throws ParseException {
            logger.info("MerchantController getData.....={}",getData);
        try {
			merchantService.createMerchant(getData);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "Created Successfully";
    }
    
    @PostMapping("/createMerchantMdr")
    public String createMerchantMdrDetails(@RequestBody Map<String,Object> getData) throws ParseException {
            logger.info("MerchantController getMdrData.....={}",getData);
       String response= merchantService.createMerchantMdrDetails(getData);
        return response;
    }
    
}
