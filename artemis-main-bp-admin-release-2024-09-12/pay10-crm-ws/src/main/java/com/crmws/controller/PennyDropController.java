package com.crmws.controller;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.pay10.commons.user.PennyDto;
import com.pay10.commons.util.PropertiesManager;
/** 
@author Deepak Bind*/
@RestController
public class PennyDropController {
	
	//LoggerFactory logger
	@PostMapping("/pennyDrop")
	@Consumes(MediaType.APPLICATION_JSON)
    public String pennyDropValidation(@RequestBody PennyDto dto  ) throws ParseException, IOException{
	    String  ipimei="0.0.0.0";
	    System.out.println("data get from form"+dto.toString());
	    Gson gson = new Gson();
	    String result = "";
	    String serviceUrl = PropertiesManager.propertiesMap.get("PennyServiceURL");
		String url = serviceUrl;
	  // HttpPost post = new HttpPost("https://payout.bhartipay.com/BhartiPayService/rest/MudraManagerBK/verifyUserAccount");
	   // HttpPost post = new HttpPost("");
		  HttpPost post = new HttpPost(url);
	    post.addHeader("Accept", "application/json");
		post.addHeader("IPIMEI", ipimei);
		post.addHeader("content-type", "application/json");
		String jsonRequest = gson.toJson(dto);
		System.out.println("result sender Registration :" + jsonRequest);
		post.setEntity(new StringEntity(jsonRequest));
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {
			result = EntityUtils.toString(response.getEntity());
		}
		System.out.println("result sender Registration :" + result);
		
		System.out.println("Final Response Sender Reg " + gson.toJson(result));
		
       return result;
		//return  gson.toJson(result);
    }

	

	

}
