package com.crmws.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.service.impl.AadharApi;
import com.pay10.commons.user.MerchantKeySaltDao;
import com.pay10.commons.util.ChecksumUtils;
import com.pay10.commons.util.PropertiesManager;

@Service
public class AadharService {
	private static final Logger logger = LoggerFactory.getLogger(AadharService.class.getName());
	private Gson gson = new Gson();
	@Autowired
	private AadharApi aadharApi;
	@Autowired
	private MerchantKeySaltDao merchantKeySaltDao;
	private String PAYID_REGEX="\\d{16}";
	private String MOBILE_REGEX="\\d{10}";
	private String AADHAR_REGEX="\\d{12}";
	private String OTP_REGEX="\\d{6}";
	private String TXNID_REGEX="^[0-9]+$";
	

	public Map<String, Object> generateAadharOTP(Map<String, String> reqMap) {
		Map<String, String> reqMapActual=new HashMap<>();
		reqMapActual.put("HASH", reqMap.get("HASH"));
		Map<String, Object> responseMap=new HashMap<String, Object>();
		
		if (reqMap != null && reqMap.isEmpty()) {
			responseMap.put("STATUS_CODE", "1001");
			responseMap.put("MESSAGE", "Request parameter cant be empty");
			return responseMap;
		}
		if (!StringUtils.isNotBlank(reqMap.get("PAY_ID"))) {
			responseMap.put("STATUS_CODE", "1002");
			responseMap.put("MESSAGE", "Please provide payId");
			return responseMap;
		}
		if (!reqMap.get("PAY_ID").matches(PAYID_REGEX)) {
			responseMap.put("STATUS_CODE", "1003");
			responseMap.put("MESSAGE", "Please provide valid payId");
			return responseMap;
		}
		if (!StringUtils.isNotBlank(reqMap.get("MOBILE_NO"))) {
			responseMap.put("STATUS_CODE", "1004");
			responseMap.put("MESSAGE", "Please provide mobile number");
			return responseMap;
		}
		if (!reqMap.get("MOBILE_NO").matches(MOBILE_REGEX)) {
			responseMap.put("STATUS_CODE", "1005");
			responseMap.put("MESSAGE", "Please provide valid mobile number");
			return responseMap;
		}
		if (!StringUtils.isNotBlank(reqMap.get("AADHAAR_NO"))) {
			responseMap.put("STATUS_CODE", "1006");
			responseMap.put("MESSAGE", "Please provide aadhaar number");
			return responseMap;
		}
		if (!reqMap.get("AADHAAR_NO").matches(AADHAR_REGEX)) {
			responseMap.put("STATUS_CODE", "1007");
			responseMap.put("MESSAGE", "Please provide valid aadhaar number");
			return responseMap;
		}
		if (!StringUtils.isNotBlank(reqMap.get("HASH"))) {
			responseMap.put("STATUS_CODE", "1008");
			responseMap.put("MESSAGE", "Please provide hash");
			return responseMap;
		}
		
		String generatedHash="";
		try {
			logger.info("Request Data for generate : " + reqMap.remove("HASH"));
			generatedHash=ChecksumUtils.generateCheckSum(reqMap,merchantKeySaltDao.find(reqMap.get("PAY_ID")).getSalt());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		// generate hash for generate OTP
//		Map<String, Object> tree_map = new TreeMap<>();
//		tree_map.put("aadharNo", reqMap.get("aadharNo"));
//		tree_map.put("mobileNo", reqMap.get("mobileNo"));
//		tree_map.put("payId", reqMap.get("payId"));
//		tree_map.put("salt", merchantKeySaltDao.find(reqMap.get("payId")).getSalt());
//
//		gson = new GsonBuilder().disableHtmlEscaping().create();
//		String jsonString = gson.toJson(tree_map);
//		logger.info("JSON String : " + jsonString);
//		String generatedHash = generatetHashString(jsonString);
		logger.info("After generated hash is : " + generatedHash);
		logger.info("After generated hash is : " + reqMapActual.get("HASH"));

		if (!generatedHash.equalsIgnoreCase(reqMapActual.get("HASH"))) {
			responseMap.put("STATUS_CODE", "1009");
			responseMap.put("MESSAGE", "Hash does not match");
			return responseMap;
		}
		reqMap.put("TXN_ID", nextId().toString());
		// call here the actual api for generating
		return aadharApi.generateAadharOTP(reqMap);

	}

	public Map<String, Object> verifyAadharOTP(Map<String, String> reqMap) {
		Map<String, String> reqMapOld=new HashMap<>();
		reqMapOld.put("HASH", reqMap.get("HASH"));
		Map<String, Object> responseMap=new HashMap<String, Object>();
		logger.info("Request Data for verify : " + reqMap);
		if (reqMap != null && reqMap.isEmpty()) {
			responseMap.put("STATUS_CODE", "1001");
			responseMap.put("MESSAGE", "Request parameter cant be empty");
			return responseMap;
		}
		if (!StringUtils.isNotBlank(reqMap.get("PAY_ID"))) {
			responseMap.put("STATUS_CODE", "1002");
			responseMap.put("MESSAGE", "Please provide payId");
			return responseMap;
		}
		if (!reqMap.get("PAY_ID").matches(PAYID_REGEX)) {
			responseMap.put("STATUS_CODE", "1003");
			responseMap.put("MESSAGE", "Please provide valid payId");
			return responseMap;
		}
		if (!StringUtils.isNotBlank(reqMap.get("TXN_ID"))) {
			responseMap.put("STATUS_CODE", "1011");
			responseMap.put("MESSAGE", "Please provide txnId");
			return responseMap;
		}
		if (!reqMap.get("TXN_ID").matches(TXNID_REGEX)) {
			responseMap.put("STATUS_CODE", "1012");
			responseMap.put("MESSAGE", "Please provide valid txnId");
			return responseMap;
		}
		if (!StringUtils.isNotBlank(reqMap.get("OTP"))) {
			responseMap.put("STATUS_CODE", "1013");
			responseMap.put("MESSAGE", "Please provide OTP number");
			return responseMap;
		}
		if (!reqMap.get("OTP").matches(OTP_REGEX)) {
			responseMap.put("STATUS_CODE", "1014");
			responseMap.put("MESSAGE", "Please provide valid OTP number");
			return responseMap;
		}
		if (!StringUtils.isNotBlank(reqMap.get("HASH"))) {
			responseMap.put("STATUS_CODE", "1008");
			responseMap.put("MESSAGE", "Please provide hash");
			return responseMap;
		}

		String generatedHash="";
		try {
			logger.info("Request Data : " + reqMap.remove("HASH"));
			generatedHash=ChecksumUtils.generateCheckSum(reqMap, merchantKeySaltDao.find(reqMap.get("PAY_ID")).getSalt());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		// generate hash for generate OTP
//		Map<String, Object> tree_map = new LinkedHashMap<>();
//		tree_map.put("otp", reqMap.get("otp"));
//		tree_map.put("txnId", reqMap.get("txnId"));
//		tree_map.put("payId", reqMap.get("payId"));
//		tree_map.put("salt", merchantKeySaltDao.find(reqMap.get("payId")).getSalt());
//		logger.info("TREE_MAP : " + tree_map);
//		gson = new GsonBuilder().disableHtmlEscaping().create();
//
//		String jsonString = gson.toJson(tree_map);
//		logger.info("JSON String : " + jsonString);
//		String generatedHash = generatetHashString(jsonString);
		logger.info("After generated hash is : " + generatedHash);
		logger.info("After generated hash is : " + reqMapOld.get("HASH"));

		if (!generatedHash.equalsIgnoreCase(reqMapOld.get("HASH"))) {
			responseMap.put("STATUS_CODE", "1009");
			responseMap.put("MESSAGE", "Hash does not match");
			return responseMap;
		}

		// call here actual api for verifing
		return aadharApi.verifyAadharOTP(reqMap);

	}

	private String callToActualGenerateRequestAadharApi(Map<String, Object> tree_map) {
		tree_map.remove("payId");
		tree_map.remove("salt");
		tree_map.put("secretKey", PropertiesManager.propertiesMap.get("SECRET_KEY_EKYC"));
		if (tree_map.containsKey("txnId") && StringUtils.isNotBlank(tree_map.get("txnId").toString())) {
			// do nothing...
		} else {
			tree_map.put("txnId", nextId());
		}

		gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(tree_map);
		logger.info("callToGenerateAadharApi JSON String : " + jsonString);
		String generatedHash = generatetHashString(jsonString);
		logger.info("callToGenerateAadharApi After generated hash is : " + generatedHash);

		tree_map.put("hash", generatedHash);
		tree_map.remove("secretKey");
		return gson.toJson(tree_map);

	}

	private static AtomicReference<Long> currentTime = new AtomicReference<>(System.currentTimeMillis());

	private static Long nextId() {
		return currentTime.accumulateAndGet(System.currentTimeMillis(), (prev, next) -> next > prev ? next : prev + 1)
				% 10000000000L;
	}

	private String generatetHashString(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			StringBuilder hashtext = new StringBuilder(no.toString(16));
			while (hashtext.length() < 64) {
				hashtext.insert(0, '0');
			}
			return hashtext.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
