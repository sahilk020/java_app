package com.pay10.crm.invoice.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.SmsControllerServiceProvider;

@Service
public class InvoiceSmsService {
	private static Logger logger = LoggerFactory.getLogger(InvoiceSmsService.class.getName());

	@Autowired
	private SmsControllerServiceProvider smsControllerServiceProvider;

	public void sendSms(String phone, String shortUrl, String url, String businessName, String name) {
		if(StringUtils.isEmpty(shortUrl)) {
			shortUrl = url;
		}
		
		if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(name) || StringUtils.isEmpty(businessName) || StringUtils.isEmpty(shortUrl)) {
			logger.error("Invalid Fields in invoice sms - phone : " + phone + ", name : " + name + ", business name : " + businessName + ", url : " + url + ", short url : " + shortUrl);
		}else {
			logger.error("Sending Invoice SMS - phone : " + phone + ", name : " + name + ", business name : " + businessName + ", url : " + url + ", short url : " + shortUrl);
			smsControllerServiceProvider.sendInvoiceSMS(phone, shortUrl, businessName, name);
		}
		
		
		
	}

	//Added By Sweety for sending sms while creating singgle invoice
	public void sendSmsInvoice(String phone, String shortUrl, String url, String businessName,String amount,String invoiceNo) {
		if(StringUtils.isEmpty(shortUrl)) {
			shortUrl = url;
		}
		
		if(StringUtils.isEmpty(phone) ||  StringUtils.isEmpty(businessName) || StringUtils.isEmpty(shortUrl)||StringUtils.isEmpty(invoiceNo)) {
			logger.error("Invalid Fields in invoice sms - phone : " + phone +", business name : " + businessName + ", url : " + url + ", short url : " + shortUrl+",invoiceNo : " + invoiceNo);
}else {
	logger.error("Sending Invoice SMS - phone : " + phone + ", business name : " + businessName + ", url : " + url + ", short url : " + shortUrl);//smsControllerServiceProvider.sendInvoiceSMS(phone, shortUrl, businessName, name);
			smsControllerServiceProvider.sendSmsKaleyraInvoice(phone, shortUrl, businessName,amount,invoiceNo);
		}		
	}
	
}
