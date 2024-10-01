package com.crmws.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.crmws.dto.MerchantRequest;
import com.crmws.dto.MerchantResponse;
import com.crmws.dto.ResponseEnvelope;
import com.crmws.service.IntentPaymentLinkService;
import com.pay10.commons.api.ShortUrlProvider;
import com.pay10.commons.dao.IntentPaymentLinkDao;
import com.pay10.commons.user.PaymentLink;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

@Service
public class IntentPaymentLinkServiceImpl implements IntentPaymentLinkService {

	private static Logger logger = LoggerFactory.getLogger(IntentPaymentLinkServiceImpl.class.getName());

	@Autowired
	private IntentPaymentLinkDao intentPaymentLinkDao;

	@Autowired
	private ShortUrlProvider shortUrlProvider;

	@Autowired
	private UserDao userDao;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Calendar cal = Calendar.getInstance();

	public ResponseEnvelope<MerchantResponse> createPaymentLink(MerchantRequest request) {
		logger.info("Saving Intent Payment Link");

		List<String> errors = new ArrayList<>();
		MerchantResponse response = null;
		PaymentLink paymentLink = null;
		try {

			if (!isValidRequest(request, errors)) {
				return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST, response, String.join(", ", errors));
			}
			User user = userDao.findPayId(request.getPayId());
			if (ObjectUtils.isEmpty(user)) {
				return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST, "Invalid PayId");
			}
			if (!user.getUserStatus().getStatus().equalsIgnoreCase("Active")) {
				return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST, "Inactive User");
			}

			paymentLink = save(request);
			response = new MerchantResponse(paymentLink.getOrderId(), paymentLink.getShortUrl(), paymentLink.getExpiryTime());
		} catch (Exception e) {
			logger.error(String.format("Error creating intend payment link for order %s", request.getOrderId()));
			return new ResponseEnvelope<>(HttpStatus.INTERNAL_SERVER_ERROR, "Oops!! Error Occoured");
		}

		return new ResponseEnvelope<MerchantResponse>(HttpStatus.CREATED, "Intend Payment Link Created Successfully",
				response);
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
		if (!StringUtils.isEmpty(request.getAddress())) {
			String invoiceAddress = request.getAddress();
			invoiceAddress = invoiceAddress.replace("\n", " ");
			invoiceAddress = invoiceAddress.replace("\r", " ");
			paymentLink.setAddress(invoiceAddress);
		}

		if (request.getReturnUrl().isEmpty()) {
			String invoiceReturnUrl = PropertiesManager.propertiesMap
					.get(CrmFieldConstants.INVOICE_RETURN_URL.getValue());
			paymentLink.setReturnUrl(invoiceReturnUrl);
		} else {
			paymentLink.setReturnUrl(request.getReturnUrl());
		}

		logger.info("INTEND_PAYMENT_LINK_URL : " + CrmFieldConstants.INTENT_PAYMENT_LINK_URL.getValue());
		String url = PropertiesManager.propertiesMap.get(CrmFieldConstants.INTENT_PAYMENT_LINK_URL.getValue())
				+ paymentLink.getPaymentLinkId();
		logger.info("url " + url);
		paymentLink.setInvoiceUrl(url);
		//String shortUrl = shortUrlProvider.ShortUrl(url);
		//paymentLink.setShortUrl(shortUrl);
		paymentLink.setShortUrl(url);
		paymentLink.setExpiresDay(StringUtils.isEmpty(request.getExpiresDay()) ? "0" : request.getExpiresDay());
		paymentLink.setExpiresHour(StringUtils.isEmpty(request.getExpiresHour()) ? "0" : request.getExpiresHour());
		cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(paymentLink.getExpiresDay()));
		cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(paymentLink.getExpiresHour()));
		paymentLink.setExpiryTime(sdf.format(cal.getTime()));

		save(paymentLink);
		return paymentLink;
	}

	public void save(PaymentLink paymentLink) {

		if (!ObjectUtils.isEmpty(paymentLink)) {
			intentPaymentLinkDao.create(paymentLink);
		}

	}

	public PaymentLink getPaymentLinkByOrderId(String orderId) {
		PaymentLink paymentLink = intentPaymentLinkDao.findByOrderId(orderId);
		return paymentLink;
	}

	private boolean isValidRequest(MerchantRequest request, List<String> errors) {
		
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +  //part before @
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; 
		if (!ObjectUtils.isEmpty(request)) {

			PaymentLink paymentLink = null;
			if (StringUtils.isEmpty(request.getPayId())) {
				errors.add("Pay Id is required");
			}
			if (StringUtils.isEmpty(request.getOrderId())) {
				errors.add("Order Id is required");
			}
			if (request.getOrderId() != null && Pattern.compile("[^a-z_0-9 ]", Pattern.CASE_INSENSITIVE).matcher(request.getOrderId()).find()) {
				errors.add("Order Id not accept special character");
			}
			if (request.getOrderId() != null && Pattern.compile(" ").matcher(request.getOrderId()).find()) {
				errors.add("Order Id not accept blank space");
			}
			
			paymentLink = getPaymentLinkByOrderId(request.getOrderId());
			
			if (paymentLink != null) {
				errors.add("Order Id Already Exists");
			}
			if (StringUtils.isEmpty(request.getAmount())) {
				errors.add("Amount is required");
			}
			if (request.getAmount() != null) {
				String amount = request.getAmount();
				if (Integer.valueOf(amount.replace(".", "")) <= 0) {
					errors.add("Amount should be greater than 0");
				}
			}
			if (StringUtils.isEmpty(request.getCustName())) {
				errors.add("Customer Name is required");
			}
			if (StringUtils.isEmpty(request.getCustEmail())) {
				errors.add("Customer Email is required");
			}
			if (request.getCustEmail() != null && !Pattern.compile(emailRegex).matcher(request.getCustEmail()).matches()) {
				errors.add("Valid Customer Email is required");
			}
			if (StringUtils.isEmpty(request.getCustPhone())) {
				errors.add("Customer Phone is required");
			}
			if (request.getCustPhone() != null &&  !(request.getCustPhone().length() == 10)) {
				errors.add("Customer Phone must only 10 digit");
			}
			if (request.getCustPhone() != null && !request.getCustPhone().matches("[0-9]+")) {
				errors.add("Customer Phone not accept special character");
			}
			if (StringUtils.isEmpty(request.getAddress())) {
				errors.add("Address is required");
			}
			if (StringUtils.isEmpty(request.getExpiresDay()) && StringUtils.isEmpty(request.getExpiresHour())) {

				errors.add("Expiry Day Or Expiry Hour is required");
			}
			if (request.getExpiresDay() !=null && Integer.valueOf(request.getExpiresDay()) < 0) {

				errors.add("Expiry Day Not Accept minus values");
			}
			if (request.getExpiresHour() !=null && Integer.valueOf(request.getExpiresHour()) < 0) {

				errors.add("Expiry Hour Not Accept minus values");
			}
			if (request.getExpiresDay() !=null && Integer.valueOf(request.getExpiresDay()) == 0 && 
					request.getExpiresHour() !=null && Integer.valueOf(request.getExpiresHour()) == 0 ) {

				errors.add("Expiry Day Or Expiry Hour must be greater than 0");
			}
			if (request.getExpiresDay() !=null && Integer.valueOf(request.getExpiresDay()) > 365) {

				errors.add("Expiry Day accept only 365 day");
			}
			if (request.getExpiresHour() !=null && Integer.valueOf(request.getExpiresHour()) > 24) {

				errors.add("Expiry Hour accept only 24 hr");
			}

		} else {
			errors.add("Empty Request");
		}

		if (CollectionUtils.isEmpty(errors)) {
			return true;
		}

		return false;

	}
	
	public static void main(String[] args) {
		String orderId = "Sonu_1234";
		//System.out.println(Pattern.compile("[^a-z_0-9 ]", Pattern.CASE_INSENSITIVE).matcher(orderId).find());
		
		/*
		 * Pattern pattern = Pattern.compile(" ").matcher(orderId); Matcher matcher =
		 * pattern.matcher(orderId); int spaceCount = 0; while (matcher.find()) {
		 * spaceCount++; }
		 */
	    
	   boolean cnt =  Pattern.compile(" ").matcher(orderId).find();
	   System.out.println("cnt "+cnt);
	}
	
}
