package com.pay10.crm.invoice.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.EmailControllerServiceProvider;

@Service
public class InvoiceEmailService {

	private static Logger logger = LoggerFactory.getLogger(InvoiceEmailService.class.getName());
	
	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;

	public void sendEmail(String email, String shortUrl, String url, String name, String businessName) {
		
		if(StringUtils.isEmpty(shortUrl)) {
			shortUrl = url;
		}
		
		if(StringUtils.isEmpty(email) || StringUtils.isEmpty(name) || StringUtils.isEmpty(businessName) || StringUtils.isEmpty(shortUrl)) {
			logger.error("Invalid Fields in invoice email - email : " + email + ", name : " + name + ", business name : " + businessName + ", url : " + url + ", short url : " + shortUrl);
		}else {
			logger.info("Sending invoice E-Mail at - email : " + email + ", name : " + name + ", business name : " + businessName + ", url : " + url + ", short url : " + shortUrl);
			emailControllerServiceProvider.invoiceLink(shortUrl, email, name, businessName);
		}
		
		
	}

}
