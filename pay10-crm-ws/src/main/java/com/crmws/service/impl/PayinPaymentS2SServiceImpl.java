package com.crmws.service.impl;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.crmws.dto.PayinPaymentS2SRequest;
import com.crmws.dto.PayinPaymentS2SResponse;
import com.crmws.dto.ResponseEnvelope;
import com.crmws.service.PayinPaymentS2SService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.api.ShortUrlProvider;
import com.pay10.commons.dao.PayinPaymentS2SDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.PayinPaymentS2S;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PayinPaymentS2SStatus;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;

@Service
public class PayinPaymentS2SServiceImpl implements PayinPaymentS2SService {

	private static Logger logger = LoggerFactory.getLogger(PayinPaymentS2SServiceImpl.class.getName());

	@Autowired
	private PayinPaymentS2SDao payinPaymentS2SDao;

	@Autowired
	private ShortUrlProvider shortUrlProvider;

	@Autowired
	private UserDao userDao;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Calendar cal = Calendar.getInstance();

	public Map<String, String> createPaymentLink(PayinPaymentS2SRequest request) {
		logger.info("Saving Intent Payment Link");
		Map<String, String> resp = new HashMap<String, String>();
		List<String> errors = new ArrayList<>();
		PayinPaymentS2SResponse response = null;
		PayinPaymentS2S paymentLink = null;
		try {

			if (!isValidRequest(request, errors)) {
				resp.put("ORDER_ID", String.valueOf(request.getOrderId()));
				resp.put("PAY_ID", String.valueOf(request.getPayId()));
				resp.put("STATUS", "Invalid Request");
				resp.put("RESPONSE_CODE", "300");
				resp.put("RESPONSE_MESSAGE", String.join(", ", errors));
				return resp;
			}
			User user = userDao.findPayId(request.getPayId());
			if (ObjectUtils.isEmpty(user)) {
				resp.put("ORDER_ID", String.valueOf(request.getOrderId()));
				resp.put("PAY_ID", String.valueOf(request.getPayId()));
				resp.put("STATUS", "Invalid Request");
				resp.put("RESPONSE_CODE", "300");
				resp.put("RESPONSE_MESSAGE", "Invalid PayId");
				return resp;
			}
			if (!user.getUserStatus().getStatus().equalsIgnoreCase("Active")) {
				resp.put("ORDER_ID", String.valueOf(request.getOrderId()));
				resp.put("PAY_ID", String.valueOf(request.getPayId()));
				resp.put("STATUS", "Invalid Request");
				resp.put("RESPONSE_CODE", "102");
				resp.put("RESPONSE_MESSAGE", "Inactive User");
				return resp;
			}

			paymentLink = save(request);
			//response = new PayinPaymentS2SResponse(paymentLink.getOrderId(), paymentLink.getPaymentRedirectUrl(), "Payment created successfully", "000");
		} catch (Exception e) {
			logger.error(String.format("Error creating intend payment link for order %s", request.getOrderId()));
			resp.put("ORDER_ID", String.valueOf(request.getOrderId()));
			resp.put("PAY_ID", String.valueOf(request.getPayId()));
			resp.put("STATUS", "Invalid Request");
			resp.put("RESPONSE_CODE", "300");
			resp.put("RESPONSE_MESSAGE", "something went wrong, please try again!");
			return resp;
		}
		
		resp.put("ORDER_ID", String.valueOf(request.getOrderId()));
		resp.put("PAY_ID", String.valueOf(request.getPayId()));
		resp.put("STATUS", "Request Accepted");
		resp.put("RESPONSE_CODE", "000");
		resp.put("RESPONSE_MESSAGE", "Request accepted successfully!");
		resp.put("PAY_URL", paymentLink.getPaymentRedirectUrl());
		return resp;
	}

	private PayinPaymentS2S save(PayinPaymentS2SRequest request) {

		PayinPaymentS2S paymentLink = new PayinPaymentS2S();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String PaymentRedirectId = String.valueOf(UUID.randomUUID());
		paymentLink.setPayId(request.getPayId());
		paymentLink.setPayType(request.getPayType());
		paymentLink.setCustName(request.getCustName());
		paymentLink.setCustFirstName(request.getCustFirstName());
		paymentLink.setCustLastName(request.getCustLastName());
		paymentLink.setCustStreeAddress1(request.getCustStreeAddress1());
		paymentLink.setCustCity(request.getCustCity());
		paymentLink.setCustState(request.getCustState());
		paymentLink.setCustCountry(request.getCustCountry());
		paymentLink.setCustZip(request.getCustZip());
		paymentLink.setCustPhone(request.getCustPhone());
		paymentLink.setCustEmail(request.getCustEmail());
		paymentLink.setAmount(Amount.toDecimal(request.getAmount(), request.getCurrencyCode()));
		paymentLink.setTxntype(request.getTxntype());
		paymentLink.setCurrencyCode(request.getCurrencyCode());
		paymentLink.setProductDesc(request.getProductDesc());
		paymentLink.setOrderId(request.getOrderId());
		paymentLink.setReturnUrl(request.getReturn_url());
		paymentLink.setHash(request.getHash());
		paymentLink.setPaymentType(request.getPaymentType());
		paymentLink.setPaymentStatus(PayinPaymentS2SStatus.UNPAID);
		paymentLink.setCreateDate(sdf.format(cal.getTime()));
		paymentLink.setUpdateDate(sdf.format(cal.getTime()));
		paymentLink.setPaymentRedirectId(PaymentRedirectId);
		paymentLink.setPaymentRedirectUrl(PropertiesManager.propertiesMap.get("payinPaymentRedirectUrl")+PaymentRedirectId);
		
		save(paymentLink);
		return paymentLink;
	}

	public void save(PayinPaymentS2S paymentLink) {

		if (!ObjectUtils.isEmpty(paymentLink)) {
			payinPaymentS2SDao.create(paymentLink);
		}

	}

	public PayinPaymentS2S getPaymentLinkByOrderId(String orderId) {
		PayinPaymentS2S paymentLink = payinPaymentS2SDao.findByOrderId(orderId);
		return paymentLink;
	}

	private boolean isValidRequest(PayinPaymentS2SRequest request, List<String> errors) {
		
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +  //part before @
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; 
		
		String urlRegex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		if (!ObjectUtils.isEmpty(request)) {

			PayinPaymentS2S paymentLink = null;
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
			
			if (StringUtils.isEmpty(request.getHash())) {
				errors.add("Hash is required");
			}
			
			ObjectMapper objectMapper = new ObjectMapper();
		    Map<String, String> map = objectMapper.convertValue(request, new TypeReference<Map<String, String>>() {});
		    Fields fields = new Fields(map);
		    if(validateHash(fields)) {
		    	errors.add("Invalid Hash");
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
				if (Double.valueOf(amount.replace(".", "")) <= 0) {
					errors.add("Amount should be greater than 0");
				}
			}
			if (StringUtils.isEmpty(request.getCustName())) {
				errors.add("Customer Name is required");
			}
			if (request.getCustName() != null && !(request.getCustName().length() >= 1 && request.getCustName().length() <= 150)) {
				errors.add("Invalid Customer Name");
			}
			if (StringUtils.isEmpty(request.getCustEmail())) {
				errors.add("Customer Email is required");
			}
			if (request.getCustEmail() != null && !Pattern.compile(emailRegex).matcher(request.getCustEmail()).matches()) {
				errors.add("Valid Customer Email is required");
			}
			if (request.getCustEmail() != null && !(request.getCustEmail().length() >= 6 && request.getCustEmail().length() <= 120)) {
				errors.add("Invalid Customer Email");
			}
			if (StringUtils.isEmpty(request.getCustPhone())) {
				errors.add("Customer Phone is required");
			}
			if (request.getCustPhone() != null &&  !(request.getCustPhone().length() >= 8 && request.getCustPhone().length() <= 15)) {
				errors.add("Customer Phone must be between 8 to 15 digit only");
			}
			if (request.getCustPhone() != null && !request.getCustPhone().matches("[0-9]+")) {
				errors.add("Customer phone should be numeric only");
			}

			//
			if (StringUtils.isEmpty(request.getCurrencyCode())) {
				errors.add("Currency code is required");
			}

			String allCurrency = PropertiesManager.propertiesMap.get("ALLOWED_CURRENCY_CODE");
			List<String> currencyList = Arrays.asList(allCurrency.split(","));
			if (null != request.getCurrencyCode() &&  !currencyList.contains(request.getCurrencyCode())) {
				errors.add("Invalid currency code");
			}
			
			String all_vals = PropertiesManager.propertiesMap.get("ALLOWED_PAYMENT_TYPE");
			List<String> list = Arrays.asList(all_vals.split(","));
			if (StringUtils.isNotBlank(request.getPaymentType()) && null != request.getPaymentType() &&  !list.contains(request.getPaymentType())) {
				errors.add("Invalid paymentType");
			}
			
			if (StringUtils.isEmpty(request.getPayType())) {
				errors.add("Paytype is required");
			}
			
			if (null != request.getPayType() && !request.getPayType().equals("FIAT")) {
				errors.add("Invalid Paytype");
			}
			
			if (StringUtils.isEmpty(request.getReturn_url())) {
				errors.add("Return-url is required");
			}
			
			if (request.getReturn_url() != null &&  !(request.getReturn_url().length() >= 5 && request.getReturn_url().length() <= 1024)) {
				errors.add("Invalid length of return-url");
			}
			if (null != request.getReturn_url() && !Pattern.compile(urlRegex).matcher(request.getReturn_url()).matches()) {
				errors.add("Invalid return-url");
			}
			
			if (StringUtils.isEmpty(request.getTxntype())) {
				errors.add("Txntype is required");
			}
			
			if (null != request.getTxntype() && !request.getTxntype().equals("SALE")) {
				errors.add("Invalid Txntype");
			}
			// add extra validation
			if (null != request.getPayId() && !(request.getPayId().length() == 16)) {
				errors.add("Payid should be 16 digit");
			}
			
			if (null != request.getPayId() && !request.getPayId().matches("[0-9]+")) {
				errors.add("Payid should be numeric only");
			}
			
			if (null != request.getOrderId() && !( request.getOrderId().length() >= 1 && request.getOrderId().length() < 50)) {
				errors.add("Order id should be less than equal to 50");
			}
			
			if (null != request.getAmount() && !(request.getAmount().length() >= 3 && request.getAmount().length() <= 12)) {
				errors.add("Invaild Amount");
			}
			
			if (null != request.getAmount() && !request.getAmount().matches("[0-9]+")) {
				errors.add("Amount should be numeric only");
			}
			
		} else {
			errors.add("Empty Request");
		}

		if (CollectionUtils.isEmpty(errors)) {
			return true;
		}

		return false;

	}
	
	
	public boolean validateHash(Fields fields) {
		logger.info("HASH CHECK Field "+ fields.getFieldsAsString());
		
		// Hash sent by merchant in request
		String merchantHash = fields.remove(FieldType.HASH.getName());
		logger.info("HASH remove "+ merchantHash);
		
		String calculatedHash = null;
		try {
			calculatedHash = Hasher.getHash(fields);
		} catch (SystemException e) {
			return false;
		}
		logger.info("merchantHash hash:"+merchantHash+",,,, calculatedHash:"+calculatedHash);
		if (!calculatedHash.equals(merchantHash)) {
			StringBuilder hashMessage = new StringBuilder("Merchant hash =");
			hashMessage.append(merchantHash);
			hashMessage.append(", Calculated Hash=");
			hashMessage.append(calculatedHash);
			logger.info(hashMessage.toString());
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String amount = "0000000000000000";
		/*
		 * if (Double.valueOf(amount.replace(".", "")) <= 0) {
		 * System.out.println("---------"); } else { System.out.println("-----++----");
		 * }
		 */
		
		String paymentType =  "UP";
		String all_vals = "UP,NB,WL,CARD,QR,EMI";
		List<String> list = Arrays.asList(all_vals.split(","));
		if (StringUtils.isNotBlank(paymentType) && null != paymentType &&  !list.contains(paymentType)) {
			System.out.println("-----");
		}else {
			System.out.println("--++---");
		}

	}
}
