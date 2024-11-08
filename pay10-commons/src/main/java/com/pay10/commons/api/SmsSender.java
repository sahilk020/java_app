package com.pay10.commons.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;


@Service
public class SmsSender {
	
	private static Logger logger = LoggerFactory.getLogger(SmsSender.class.getName());

	@SuppressWarnings("unchecked")
	public String sendSMS(String mobile,String message) throws IOException {
		
		String smsUrl = PropertiesManager.propertiesMap.get(Constants.SMS_URL.getValue());
        String senderId=  PropertiesManager.propertiesMap.get(Constants.SENDER_ID.getValue());
        String senderPassword =PropertiesManager.propertiesMap.get(Constants.SENDER_PASSWORD.getValue());
        String senderName =PropertiesManager.propertiesMap.get(Constants.SENDER_NAME.getValue());
        
        StringBuilder xmlString = new StringBuilder();
		xmlString.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		xmlString.append("<!DOCTYPE MESSAGE SYSTEM \"https://api.myvaluefirst.com/psms/dtd/messagev12.dtd\">");
		xmlString.append("<MESSAGE VER=\"1.2\">");
		xmlString.append("<USER USERNAME=\""+senderId+"\" PASSWORD=\""+senderPassword+"\" />");
		xmlString.append("<SMS TEXT=\""+message+"\"  ID=\"1\">");
		xmlString.append("<ADDRESS FROM=\""+senderName+"\" TO=\""+mobile+"\" SEQ=\"1\" />");
		xmlString.append("</SMS>");
		xmlString.append("</MESSAGE>");
        
        
        
        StringBuilder responseBuilder = new StringBuilder();
        try {
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(smsUrl);

			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("data", xmlString.toString()));
		    params.add(new BasicNameValuePair("action", "send"));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				try (InputStream instream = entity.getContent()) {

					Reader in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));

					for (int c; (c = in.read()) >= 0;)
						responseBuilder.append((char) c);
				}
			}
			
			logger.info("SMS Response - "+responseBuilder.toString());
		}
        catch (Exception exception) {
        	logger.error("Exception in sending SMS ",exception);
        	return null;
		}
		return "success";
		
	}

}
