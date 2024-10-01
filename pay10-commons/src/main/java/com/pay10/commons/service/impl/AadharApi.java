package com.pay10.commons.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.gson.Gson;
import com.pay10.commons.repository.AadharApiRepository;
import com.pay10.commons.user.AadharEntity;
import com.pay10.commons.util.PropertiesManager;

@Service
public class AadharApi {
	private static Logger logger = LoggerFactory.getLogger(AadharApi.class.getName());
	private SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private RestTemplate restTemplate1=new RestTemplate();

	//private String GENERATE_OTP = PropertiesManager.propertiesMap.get("GENERATE_OTP_URL");
	//private String VERIFY_URL_OTP = PropertiesManager.propertiesMap.get("VERIFY_OTP_URL");

	//private String bearerToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTcxNTMyODgwMywianRpIjoiNGFiMTNhZjctZDMzOS00YWEyLTliZDUtOWVhODFiYjU0Y2ZjIiwidHlwZSI6ImFjY2VzcyIsImlkZW50aXR5IjoiZGV2LnBheUBzdXJlcGFzcy5pbyIsIm5iZiI6MTcxNTMyODgwMywiZXhwIjoxNzE3OTIwODAzLCJlbWFpbCI6InBheUBzdXJlcGFzcy5pbyIsInRlbmFudF9pZCI6Im1haW4iLCJ1c2VyX2NsYWltcyI6eyJzY29wZXMiOlsidXNlciJdfX0.y9IRo3C23yLbAa58w8NmMFj_imK3xVmUp-wwpM8eG_E";

	@Autowired
	private AadharApiRepository aadharApiRepository;

	public Map<String, Object> generateAadharOTP(Map<String, String> joRequest) {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			logger.info("Request For generateAadharOTP : " + joRequest);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", PropertiesManager.propertiesMap.get("BEARERTOKEN"));

			JSONObject jo = new JSONObject();
			jo.put("id_number", joRequest.get("AADHAAR_NO").toString());
			logger.info("Final Request for generate aadhar OTP : " + jo);

			AadharEntity aadharEntity=aadharApiRepository.findByPayId(joRequest.get("PAY_ID").toString());
			
			if(aadharEntity==null) {
				aadharEntity=new AadharEntity();
			}
			
			aadharEntity.setAadharNo(joRequest.get("AADHAAR_NO").toString());
			aadharEntity.setCreateDate(sd.format(new Date()));
			aadharEntity.setMobileNo(joRequest.get("MOBILE_NO").toString());
			aadharEntity.setPayId(joRequest.get("PAY_ID").toString());
			aadharEntity.setTxnId(joRequest.get("TXN_ID").toString());

			aadharApiRepository.saveOrUpdate(aadharEntity);

			HttpEntity<String> entityReq = new HttpEntity<String>(jo.toString(), headers);
			String GENERATE_OTP = PropertiesManager.propertiesMap.get("GENERATE_OTP_URL");
			logger.info("Generate otp url : " + GENERATE_OTP);
			ResponseEntity<String> responseEntity = restTemplate1.exchange(GENERATE_OTP, HttpMethod.POST, entityReq,
					String.class);
			logger.info("Final Response Entity generateOTP : " + responseEntity);
			if (responseEntity.getStatusCodeValue() == 200) {
				logger.info("Final Response generateOTP : " + responseEntity.getBody());
				return saveOrUpdateInDBGenerateOTP(responseEntity.getBody(), aadharEntity);
			} else {
				responseMap.put("STATUS_CODE", "1016");
				responseMap.put("MESSAGE", "Please try after some time");
				//responseMap.put("data", responseEntity.getBody());
				return responseMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseMap.put("STATUS_CODE", "1017");
			responseMap.put("MESSAGE", "System Internal Error");
			return responseMap;
		}
	}

	private Map<String, Object> saveOrUpdateInDBGenerateOTP(String body, AadharEntity aadharEntity) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		JSONObject jo = new JSONObject(body);
		JSONObject jo1 = jo.optJSONObject("data");

		if (aadharEntity != null) {
			aadharEntity.setClientId(jo1.getString("client_id"));
			responseMap.put("STATUS_CODE", "0000");
			responseMap.put("MESSAGE", "OTP send Successfully");
			//responseMap.put("data", aadharApiRepository.saveOrUpdate(aadharEntity));
			 aadharApiRepository.saveOrUpdate(aadharEntity);
			responseMap.put("TXN_ID", aadharEntity.getTxnId());
			return responseMap;
		} else {
			responseMap.put("STATUS_CODE", "1015");
			responseMap.put("MESSAGE", "No Details Found");
			return responseMap;
		}

	}

	@SuppressWarnings("null")
	public Map<String, Object> verifyAadharOTP(Map<String, String> actualRequest) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("Request For verifyAadharOTP : " + actualRequest);
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_JSON);
//			headers.set("Authorization", PropertiesManager.propertiesMap.get("BEARERTOKEN"));

			JSONObject jo1 = new JSONObject(new Gson().toJson(actualRequest));
			logger.info("verifyDATA : " + jo1);
			AadharEntity aadharEntity = aadharApiRepository.findByPayIdAndTxnId(jo1.get("PAY_ID").toString(),
					jo1.get("TXN_ID").toString());
			logger.info("verifyDATA : " + new Gson().toJson(aadharEntity));
			if (aadharEntity == null) {
				responseMap.put("STATUS_CODE", "1015");
				responseMap.put("MESSAGE", "No Details Found");
				//responseMap.put("data", actualRequest);
				return responseMap;
			}

			JSONObject jo = new JSONObject();
			jo.put("client_id", aadharEntity.getClientId());
			jo.put("otp", jo1.get("OTP").toString());

//			HttpEntity<String> entityReq = new HttpEntity<String>(jo.toString(), headers);
//
//			ResponseEntity<String> responseEntity = restTemplate1.exchange(VERIFY_URL_OTP, HttpMethod.POST, entityReq,
//					String.class);
			String VERIFY_URL_OTP = PropertiesManager.propertiesMap.get("VERIFY_OTP_URL");
			logger.info("Verify otp url : " + VERIFY_URL_OTP);
			Map<String, String> resMap = new HashMap<String, String>();
			String responseBody = "";
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(VERIFY_URL_OTP);
			StringEntity params = new StringEntity(jo.toString());
			request.addHeader("content-type", "application/json");
			request.addHeader("Authorization", PropertiesManager.propertiesMap.get("BEARERTOKEN"));
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			logger.info("Response Body : " + responseBody);
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);
			logger.info("Response Body DAta" + resMap);
			if(resMap!=null&&!resMap.isEmpty()) {
			
            	logger.info("Final Response Verify OTP : " + resMap);
				return saveOrUpdateInDBForVerifyOTP(new Gson().toJson(resMap), aadharEntity);
            } else {
                // Handle error
            	responseMap.put("STATUS_CODE", "1016");
				responseMap.put("MESSAGE", "Please try after some time");
				//responseMap.put("data", responseEntity.getBody());
				return responseMap;
            }

		} catch (Exception e) {
			e.printStackTrace();
			responseMap.put("STATUS_CODE", "1017");
			responseMap.put("MESSAGE", "System Internal Error");
			return responseMap;
		}
	}

	private Map<String, Object> saveOrUpdateInDBForVerifyOTP(String body, AadharEntity aadharEntity) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		JSONObject jo = new JSONObject(body);
		JSONObject jo1 = jo.optJSONObject("data");

		if (aadharEntity != null && StringUtils.isNotBlank(body)) {
			String fullName = jo1.getString("full_name");
			String dob = jo1.getString("dob");
			String careOf = jo1.getString("care_of");
			String zip = jo1.getString("zip");
			String gender = jo1.getString("gender");
			String referenceId = jo1.getString("reference_id");

			aadharEntity.setFullName(fullName);
			aadharEntity.setDob(dob);
			aadharEntity.setZip(zip);
			aadharEntity.setGender(gender);
			aadharEntity.setCareOf(careOf);
			aadharEntity.setReferenceId(referenceId);
			aadharEntity.setMessageCode(jo.getString("message_code"));
			aadharEntity.setUpdatedDate(sd.format(new Date()));
			aadharApiRepository.saveOrUpdate(aadharEntity);
			responseMap.put("STATUS_CODE", "0000");
			responseMap.put("MESSAGE", "Aadhar Verify Successfully");
			//responseMap.put("data", aadharApiRepository.saveOrUpdate(aadharEntity));
			responseMap.put("TXN_ID", aadharEntity.getTxnId());
			return responseMap;
		} else {
			logger.info("Not AadharEntity found for payId : " + new Gson().toJson(aadharEntity));
			responseMap.put("STATUS_CODE", "1015");
			responseMap.put("MESSAGE", "No Details Found");
			//responseMap.put("data", aadharEntity);
			return responseMap;
		}

	}

}
