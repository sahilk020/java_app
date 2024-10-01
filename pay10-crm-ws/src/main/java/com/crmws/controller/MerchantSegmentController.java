package com.crmws.controller;

import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.commons.dao.RoleDao;

@RestController
@RequestMapping("/segment")
public class MerchantSegmentController {
	private static Logger logger = LoggerFactory.getLogger(MerchantSegmentController.class.getName()); 
	@Autowired
	private RoleDao roleDao;
	
	@SuppressWarnings("unchecked")
	@PostMapping("/updateSegment")
    public ResponseEntity<String> updateSegment(@RequestBody Map<String, String> data) {

        String payId = data.get("payId");
        String emailId = data.get("emailId");
        String segmentName = data.get("segmentName");
        logger.info("payId : "+payId+", emailId : "+emailId+", segmentName : "+segmentName);
        int result = roleDao.updateSegments(segmentName, emailId, payId);
        
        if(result == 1) {
        	return ResponseEntity.ok(new JSONObject().put("message", "Segment Updated Successfully !!!").toString());
        }else {
        	return ResponseEntity.ok(new JSONObject().put("message", "Segment Updated Successfully !!!").toString());
        }
        
    }

}
