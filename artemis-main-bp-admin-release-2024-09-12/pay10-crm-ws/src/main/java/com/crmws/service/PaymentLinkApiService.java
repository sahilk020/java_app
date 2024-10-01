package com.crmws.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.pay10.commons.api.ShortUrlProvider;
import com.pay10.commons.dao.PaymentLinkDao;
import com.pay10.commons.user.PaymentLink;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.crmws.dto.MerchantRequest;
import com.crmws.dto.MerchantResponse;
import com.crmws.dto.ResponseEnvelope;

@Service
public class PaymentLinkApiService {
	
	private static Logger logger = LoggerFactory.getLogger(PaymentLinkApiService.class.getName());
	
	@Autowired
	private PaymentLinkDao paymentLinkDao;
	
	@Autowired
	private ShortUrlProvider shortUrlProvider;
	
	@Autowired
	private UserDao userDao;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Calendar cal = Calendar.getInstance();
	
	
	public ResponseEnvelope<MerchantResponse> createPaymentLink(MerchantRequest request)
	{
		logger.info("Saving Payment Link");
		
		List<String> errors = new ArrayList<>();
		MerchantResponse response =null;
		PaymentLink paymentLink =null;
		try {
			
			if(!isValidRequest(request, errors))
			{
				return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST,response,String.join(", ", errors));
			}
			User user =userDao.findPayId(request.getPayId());
			if(ObjectUtils.isEmpty(user))
			{
				return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST,"Invalid PayId");
			}
			if(!user.getUserStatus().getStatus().equalsIgnoreCase("Active"))
			{
				return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST,"Inactive User");
			}
			
			paymentLink =save(request);
			response = new MerchantResponse(paymentLink.getOrderId(),paymentLink.getShortUrl(),paymentLink.getExpiryTime());
			}catch (Exception e) {
				logger.error(String.format("Error creating payment link for order %s", request.getOrderId()));
				return new ResponseEnvelope<>(HttpStatus.INTERNAL_SERVER_ERROR,"Oops!! Error Occoured");
			}
			
			
	        return new ResponseEnvelope<MerchantResponse>(HttpStatus.CREATED,"Payment Link Created Successfully",response);
	}
	
	
	private PaymentLink save(MerchantRequest request) {
		
		PaymentLink paymentLink = new PaymentLink();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		paymentLink.setPaymentLinkId(TransactionManager.getId());
		paymentLink.setInvoiceStatus(InvoiceStatus.UNPAID);
		paymentLink.setOrderId(request.getOrderId());
		paymentLink.setPayId(request.getPayId());
		paymentLink.setName(request.getCustName());
		paymentLink.setEmail(request.getCustEmail());
		paymentLink.setPhone(request.getCustPhone());
		paymentLink.setAmount(request.getAmount());
		paymentLink.setCurrencyCode("356");
		paymentLink.setCreateDate(sdf.format(cal.getTime()));
		paymentLink.setUpdateDate(sdf.format(cal.getTime()));
		if(!StringUtils.isEmpty(request.getAddress())) {
			String invoiceAddress = request.getAddress();
			invoiceAddress = invoiceAddress.replace("\n", " ");
			invoiceAddress = invoiceAddress.replace("\r", " ");
			paymentLink.setAddress(invoiceAddress);
		}

		if (request.getReturnUrl().isEmpty())
		{
			String invoiceReturnUrl = PropertiesManager.propertiesMap.get(CrmFieldConstants.INVOICE_RETURN_URL.getValue());
			paymentLink.setReturnUrl(invoiceReturnUrl);
		}else {
			paymentLink.setReturnUrl(request.getReturnUrl());
		}
		
		String url = PropertiesManager.propertiesMap.get(CrmFieldConstants.PAYMENT_LINK_URL.getValue()) + paymentLink.getPaymentLinkId();
		paymentLink.setInvoiceUrl(url);
		String shortUrl = shortUrlProvider.ShortUrl(url);
		paymentLink.setShortUrl(shortUrl);
		paymentLink.setExpiresDay(StringUtils.isEmpty(request.getExpiresDay())?"0":request.getExpiresDay());
		paymentLink.setExpiresHour(StringUtils.isEmpty(request.getExpiresHour())?"0":request.getExpiresHour());
		cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(paymentLink.getExpiresDay()));
		cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(paymentLink.getExpiresHour()));
		paymentLink.setExpiryTime(sdf.format(cal.getTime()));
		
		save(paymentLink);
		return paymentLink;
	}
	
	public void save(PaymentLink paymentLink) {

		if (!ObjectUtils.isEmpty(paymentLink))
		{
			paymentLinkDao.create(paymentLink);
		}
		
	}
	
	public PaymentLink getPaymentLinkByOrderId(String orderId)
	{
		PaymentLink paymentLink=paymentLinkDao.findByOrderId(orderId);
		return paymentLink;
	}
	
	
	private boolean isValidRequest(MerchantRequest request,List<String> errors)
	{
		if(!ObjectUtils.isEmpty(request))
		{
			
			PaymentLink paymentLink =null;
			if(StringUtils.isEmpty(request.getPayId()))
			{
				errors.add("Pay Id is required");
			}
			if(StringUtils.isEmpty(request.getOrderId()))
			{
				errors.add("Order Id is required");
			}
			paymentLink = getPaymentLinkByOrderId(request.getOrderId());
			if (paymentLink != null) {
			errors.add("Order Id Already Exists");
			}
			if(StringUtils.isEmpty(request.getAmount()))
			{
				errors.add("Amount is required");
			}
			if(StringUtils.isEmpty(request.getCustName()))
			{
				errors.add("Customer Name is required");
			}
			if(StringUtils.isEmpty(request.getCustEmail()))
			{
				errors.add("Customer Email is required");
			}
			if(StringUtils.isEmpty(request.getCustPhone()))
			{
				errors.add("Customer Phone is required");
			}
			if(StringUtils.isEmpty(request.getAddress()))
			{
				errors.add("Address is required");
			}
			if (StringUtils.isEmpty(request.getExpiresDay()) && StringUtils.isEmpty(request.getExpiresHour())) {
				
				errors.add("Expiry Day Or Expiry Hour is required");
			}
			
		}else {
			errors.add("Empty Request");
		}
		
		if(CollectionUtils.isEmpty(errors))
		{
		return true;
		}
		
		return false;
		
	}

}
