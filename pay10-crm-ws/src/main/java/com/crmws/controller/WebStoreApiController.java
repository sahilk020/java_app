package com.crmws.controller;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pay10.commons.api.WebStoreApiService;
import com.pay10.commons.dto.WebStoreApiDTO;

@CrossOrigin(origins = "*")
@RestController
public class WebStoreApiController {
	
	private static Logger logger = LoggerFactory.getLogger(WebStoreApiController.class.getName());
	
	@Autowired
	private WebStoreApiService webService;
	
//	@PostMapping("/generateUUId")
//	public String generateUUId(@RequestBody WebStoreApiDTO webStoreApiDTO) {
//		return webService.generateUUID(webStoreApiDTO);
//	}
	
	
	
	@PostMapping("/webStoreApi")
	public Map<String,Object> webStoreApi(@RequestParam String type,@RequestBody WebStoreApiDTO webStoreApiDTO)
	{
	//@RequestParam("file") MultipartFile multipartFile ,
	logger.info("INSIDE THE WEBSTORE CONTROLLER COMMON SERVICE::::::={}",type);
	return webService.webStoreApi(type, webStoreApiDTO);
	}
	@PostMapping("/product")
	public String addProducts( @RequestBody WebStoreApiDTO webStoreApiDTO)
	{
	       //webStoreApiDTO.setImage(null);
		logger.info("webStoreApiDTO in add product api in crmws....={}",webStoreApiDTO);
	return webService.addProduct(webStoreApiDTO);
	}

}
