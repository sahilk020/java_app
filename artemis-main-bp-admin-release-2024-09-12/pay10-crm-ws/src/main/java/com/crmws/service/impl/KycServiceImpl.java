package com.crmws.service.impl;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.formula.functions.T;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.crmws.dto.EkycOtpRequestDTO;
import com.crmws.dto.EkycOtpResponseDTO;
import com.crmws.dto.EkycOtpVerRequestDTO;
import com.crmws.dto.EkycOtpVerResponseDTO;
import com.crmws.service.KycService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pay10.commons.user.EkycGenDao;
import com.pay10.commons.user.EkycGenTable;
import com.pay10.commons.user.EkycVerDao;
import com.pay10.commons.user.EkycVerTable;
import com.pay10.commons.util.PropertiesManager;
@Service
public class KycServiceImpl implements KycService {
	private static final Logger logger = LoggerFactory.getLogger(KycServiceImpl.class.getName());
	private Gson gson = new Gson();
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private EkycGenDao ekycGenDao;
	@Autowired
	private EkycVerDao ekycVerDao;

	/* Below is code for generating aadhar OTP code start here */
	@Override
	public EkycOtpResponseDTO generateAadharOTP(EkycOtpRequestDTO ekycOtpRequestDTO,HttpServletRequest request) {
		logger.info("KycService generateAadharOTP : " + ekycOtpRequestDTO);
		EkycOtpResponseDTO ekycOtpResponseDTO = new EkycOtpResponseDTO();
		try {
			
			// Store Request Data in table
			EkycGenTable ekycGenTable = new EkycGenTable();
			ekycGenTable.setPayId(ekycOtpRequestDTO.getPayId());
			ekycGenTable.setMobileno(ekycOtpRequestDTO.getMobileno());
			ekycGenTable.setTxnId(ekycOtpRequestDTO.getTxnId());
			ekycGenTable.setAadhar(ekycOtpRequestDTO.getAdhar().replaceAll("\\w(?=\\w{4})", "*"));
			ekycGenTable.setCreateDate(new Date());
			ekycGenTable.setUpdateDate(new Date());
			//Insert generate OTP request detail here
			ekycGenDao.insertEKyc(ekycGenTable);
			//Then create hash basis on customer details
			String hashCode = convertOtpGenerateRequest(ekycOtpRequestDTO);
			logger.info("generateAadharOTP3 : " + hashCode);
			// match with requested hash if match then process request further
			if (hashCode.equals(ekycOtpRequestDTO.getHash())) {
				logger.info("generateAadharOTP2 inside hash matched... : ");
				String jsonFinalData=requestDataAadhar(ekycOtpRequestDTO);
				
				ResponseEntity<String> response = commonRestApiCall(
						PropertiesManager.propertiesMap.get("GENERATE_OTP_API"),jsonFinalData,request.getRemoteAddr());

				// Getting response from API in JSON
				if (response.getStatusCodeValue() == 200) {

					JSONObject jsonObject = new JSONObject(response.getBody());
					String statusCode = jsonObject.getString("statusCode");
					String message = jsonObject.getString("message");

					// Fetch by Pay Id for update response data in same table
					EkycGenTable ekycGenTableFromDB = ekycGenDao.findByPayIdAndTxnId(ekycOtpRequestDTO.getPayId(),ekycOtpRequestDTO.getTxnId());
					ekycGenTableFromDB.setMessage(message);
					ekycGenTableFromDB.setStatusCode(statusCode);

					// Update Response Detail Here Based on Pay Id
					ekycGenDao.updateResponse(ekycGenTableFromDB);

					ekycOtpResponseDTO.setStatusCode(statusCode);
					ekycOtpResponseDTO.setMessage(message);

				}else {
					ekycOtpResponseDTO.setMessage("No response getting from server");
				}

			} else {
				// Hash is not matched
				ekycOtpResponseDTO.setMessage("Requested Hash not matched");
				
			}
		} catch (Exception e) {
			logger.info("generateAadharOTP : " + e);
			ekycOtpResponseDTO.setMessage("Internal server error");
		}
		return ekycOtpResponseDTO;
	}
	
	// Extract ekycOtpRequestDTO field for hashing
	private String convertOtpGenerateRequest(EkycOtpRequestDTO ekycOtpRequestDTO) {
		HashMap<String, Object> tree_map = new LinkedHashMap<String, Object>();

		if (!ObjectUtils.isEmpty(ekycOtpRequestDTO)) {

			// String salt = SaltFactory.getSaltProperty(user);
			String salt = (new PropertiesManager()).getSalt(ekycOtpRequestDTO.getPayId());

			tree_map.put("adhar", ekycOtpRequestDTO.getAdhar());
			tree_map.put("mobileno", ekycOtpRequestDTO.getMobileno());
			tree_map.put("secretKey", salt);
			tree_map.put("txnId", ekycOtpRequestDTO.getTxnId());
			tree_map.put("payId", ekycOtpRequestDTO.getPayId());
		}

		gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(tree_map);
		String generatedHash = generatetHashString(jsonString);
		
		return generatedHash;

	}
	
	// Create hash for actual API CALL
	private String requestDataAadhar(EkycOtpRequestDTO ekycOtpRequestDTO) {
		String hash = "";
		String jsonStringNew = "";
		
		HashMap<String, Object> tree_map = new LinkedHashMap<>();
		try {

			tree_map.put("adhar", ekycOtpRequestDTO.getAdhar());
			tree_map.put("mobileno", ekycOtpRequestDTO.getMobileno());
			tree_map.put("secretKey", PropertiesManager.propertiesMap.get("SECRET_KEY_EKYC"));
			tree_map.put("txnId", ekycOtpRequestDTO.getTxnId());

			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String jsonString = gson.toJson(tree_map);

			hash = generatetHashString(jsonString);

			tree_map.put("hash", hash);
			tree_map.remove("secretKey");
			jsonStringNew = gson.toJson(tree_map);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonStringNew;
	}
	
	/* Below is code for generating aadhar OTP code end here */
	//---------------------------------------------------------------------
	/* Below is code for verify OTP code start here */
	@Override
	public EkycOtpVerResponseDTO verifyAadharOTP(EkycOtpVerRequestDTO ekycOtpVerRequestDTO,HttpServletRequest request) {
		logger.info("KycService verifyAadharOTP : " + ekycOtpVerRequestDTO);
		EkycOtpVerResponseDTO ekycOtpVerResponseDTO=new EkycOtpVerResponseDTO();

		try {
			
			EkycVerTable ekycVerTable = new EkycVerTable();
			ekycVerTable.setPayId(ekycOtpVerRequestDTO.getPayId());
			ekycVerTable.setTxnId(ekycOtpVerRequestDTO.getTxnId());
			ekycVerTable.setCreateDate(new Date());
			ekycVerTable.setUpdateDate(new Date());
			// store request object to DTO
			ekycVerDao.insertEKyc(ekycVerTable);

		String hashCode = convertOtpVerifyRequest(ekycOtpVerRequestDTO);
		// Compare hash here
		if (hashCode.equals(ekycOtpVerRequestDTO.getHash())) {
			
			String jsonFinalData=requestDataVerify(ekycOtpVerRequestDTO);
			
			ResponseEntity<String> response = commonRestApiCall(PropertiesManager.propertiesMap.get("VERIFY_OTP"),jsonFinalData,request.getRemoteAddr());
			if (response.getStatusCodeValue() == 200) {

				JSONObject jsonObject = new JSONObject(response.getBody());
				String statusCode = jsonObject.getString("statusCode");
				String message = jsonObject.getString("message");
				
				//Find exit entry from DB based on payId
				EkycVerTable ekycVerTableDB=ekycVerDao.findByPayIdAndTxnid(ekycOtpVerRequestDTO.getPayId(),ekycOtpVerRequestDTO.getTxnId());
				
				ekycVerTableDB.setFullName(jsonObject.has("full_name")?jsonObject.getString("full_name"):"NA");
				ekycVerTableDB.setDob(jsonObject.has("dob")?jsonObject.getString("dob"):"NA");
				ekycVerTableDB.setGender(jsonObject.has("gender")?jsonObject.getString("gender"):"NA");
				ekycVerTableDB.setAddress(jsonObject.has("address")?jsonObject.getString("address"):"NA");
				ekycVerTableDB.setZip(jsonObject.has("zip")?jsonObject.getString("zip"):"NA");
				ekycVerTableDB.setCare_of(jsonObject.has("care_of")?jsonObject.getString("care_of"):"NA");
				ekycVerTableDB.setMobile_verified(jsonObject.has("mobile_verified")?jsonObject.getBoolean("mobile_verified"):false);
				ekycVerTableDB.setMessage(jsonObject.getString("message"));
				ekycVerTableDB.setTxnId(jsonObject.has("txnId")?jsonObject.getString("txnId"):"NA");
				ekycVerTableDB.setStatusCode(jsonObject.getString("statusCode"));
				
				//Update response here in same table
				ekycVerDao.updateResponse(ekycVerTableDB);
				
				ekycOtpVerResponseDTO.setStatusCode(statusCode);
				ekycOtpVerResponseDTO.setMessage(message);

			}else {
				ekycOtpVerResponseDTO.setMessage("No Response getting from server");
			}

		} else {
			// Hash is not matched
			ekycOtpVerResponseDTO.setMessage("Requested Hash not matched");
		}
		}catch (Exception e) {
			logger.info("generateAadharOTP : " + e);
			ekycOtpVerResponseDTO.setMessage("Internal server error");
		}
		return ekycOtpVerResponseDTO;
	}

	
	// Extract ekycOtpVerRequestDTO field for hashing
	private String convertOtpVerifyRequest(EkycOtpVerRequestDTO ekycOtpVerRequestDTO) {
		HashMap<String, Object> tree_map = new LinkedHashMap<String, Object>();

		if (!ObjectUtils.isEmpty(ekycOtpVerRequestDTO)) {

			String salt = (new PropertiesManager()).getSalt(ekycOtpVerRequestDTO.getPayId());
			
			tree_map.put("otp", ekycOtpVerRequestDTO.getOtp());
			tree_map.put("secretKey", salt);
			tree_map.put("txnId", ekycOtpVerRequestDTO.getTxnId());
			tree_map.put("payId", ekycOtpVerRequestDTO.getPayId());
		}

		gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(tree_map);
		String generatedHash = generatetHashString(jsonString);

		return generatedHash;

	}
	
	// Create hash for actual API CALL
	private String requestDataVerify(EkycOtpVerRequestDTO ekycOtpVerRequestDTO) {
		String hash = "";
		String jsonStringNew = "";

		HashMap<String, Object> tree_map = new LinkedHashMap<>();
		try {

			tree_map.put("otp", ekycOtpVerRequestDTO.getOtp());
			tree_map.put("secretKey", PropertiesManager.propertiesMap.get("SECRET_KEY_EKYC"));
			tree_map.put("txnId", ekycOtpVerRequestDTO.getTxnId());

			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String jsonString = gson.toJson(tree_map);

			hash = generatetHashString(jsonString);

			tree_map.put("hash", hash);
			tree_map.remove("secretKey");
			jsonStringNew = gson.toJson(tree_map);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonStringNew;
	}
	
	/* Below is code for verify OTP code end here */
	//---------------------------------------------------------------------
	/* Below code is used to generate hash string start here */
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
	
	/* Below code is used to generate hash string start here */
	//---------------------------------------------------------------------
	/* Below code is common to call actual rest api start here */	
	private ResponseEntity<String> commonRestApiCall(String url,String jsonData,String ipAddress) {

		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("IPIMEI",ipAddress);
		headers.add("AGENT", PropertiesManager.propertiesMap.get("AGENT"));
		
		
		HttpEntity request = new HttpEntity(jsonData,headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		logger.info("Response from Rest API : " + response);
		return response;
	}
	/* Below code is common to call actual rest api end here */
	
}
