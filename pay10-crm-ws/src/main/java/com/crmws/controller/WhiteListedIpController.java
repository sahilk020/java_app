package com.crmws.controller;

import com.pay10.commons.dao.WhiteListedIpDao;
import com.pay10.commons.entity.WhiteListedIp;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WhiteListedIpController {
    private static Logger logger = LoggerFactory.getLogger(WhiteListedIpController.class.getName());

    @Autowired
    UserDao userDao;
    @Autowired
    WhiteListedIpDao whiteListedIpDao;

    @GetMapping("/getWhiteListedIpForUi")
    public Map<String, String> getWhiteListedIpForUi(@RequestParam String emailId){
        logger.info("EmailId :"+emailId);
        String payId = userDao.getPayIdByEmailId(emailId);
        logger.info("getWhiteListedIpForUi :"+payId);
        return whiteListedIpDao.getWhiteListedIpListForUI(payId);
    }

    @PostMapping("/saveWhiteListedIp")
    public String saveWhiteListedIp(@RequestParam String emailId, @RequestParam String ip, @RequestParam String createdBy) {
        logger.info("EmailId :" + emailId + " IP :" + ip + " createdBy :" + createdBy);
        Date date = new Date();
        WhiteListedIp whiteListedIp = new WhiteListedIp();
        whiteListedIp.setPayId(userDao.getPayIdByEmailId(emailId));
        whiteListedIp.setIp(ip);
        whiteListedIp.setCreatedBy(userDao.getEmailIdByPayId(createdBy));
        whiteListedIp.setCreatedOn(String.valueOf(date));
        
        // Check if the IP address already exists
        Map<String, String> whiteListedIpMap = whiteListedIpDao.getWhiteListedIpListForUI(userDao.getPayIdByEmailId(emailId));
        for (String ipFromDB : whiteListedIpMap.values()) {
            if (ipFromDB.equalsIgnoreCase(ip)) {
                logger.info("IP already exists in the database");
                return "IP already existed";
            }
        }

        // If IP is not duplicate, proceed to save
        logger.info("Controller: IP saved successfully");
        return whiteListedIpDao.saveWhiteListedIp(whiteListedIp);
    }


    //Added by Anubha
    
    @GetMapping("/checkIpExists")
    @ResponseBody
    public Map<String, Boolean> checkIpExists(@RequestParam String ip, @RequestParam String payId) {
        logger.info("checkIpExists-PayId = " + payId + " ip = " + ip);
        User user=userDao.findByEmailId(payId);
        List<String> whiteListedIpList = whiteListedIpDao.getWhiteListedIpListForUICheck(user.getPayId());
        logger.info("Whitelisted IP List for PayId " + payId + ": " + whiteListedIpList);
        boolean exists = whiteListedIpList.contains(ip);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }


    @DeleteMapping("/deleteWhiteListedIp")
    public String deleteWhiteListedIp(@RequestParam String id){
        logger.info("Controller : IP deleted successfully :"+id);
        return whiteListedIpDao.deleteWhiteListedIp(id);
    }

    @GetMapping("/getWhiteListedIpForApi")
    public List<String> getWhiteListedIpForApi(@RequestParam String payId){
        logger.info("getWhiteListedIpForApi :"+payId);
        return whiteListedIpDao.getWhiteListedIpForApi(payId);
    }


}
