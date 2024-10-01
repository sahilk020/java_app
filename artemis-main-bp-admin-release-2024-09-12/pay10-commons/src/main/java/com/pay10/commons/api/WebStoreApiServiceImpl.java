package com.pay10.commons.api;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.pay10.commons.dto.WebStoreApiDTO;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.PropertiesManager;

@Service
public class WebStoreApiServiceImpl implements WebStoreApiService {

	@Autowired
	UserDao userDao;
	private RestTemplate restTemplate = new RestTemplate();

	private static Logger logger = LoggerFactory.getLogger(WebStoreApiServiceImpl.class.getName());

	
	@Override
	public String generateUUID(WebStoreApiDTO webStoreApiDTO) {
		String uuid = "";

		String BASE_URL = PropertiesManager.propertiesMap.get("WEB_STORE_URL");

		String url = BASE_URL + "api/generate_uuid";
		logger.info("Url in web store api={}", url);

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		List<MediaType> media = new ArrayList<>();

		media.add(MediaType.APPLICATION_JSON);
		headers.setAccept(media);
		logger.info("before set the value in map SET");
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("pay_id", webStoreApiDTO.getPay_id());
		map.add("salt", webStoreApiDTO.getSalt());
		map.add("merchant_hosted_key", webStoreApiDTO.getMerchant_hosted_key());
		map.add("merchant_name", webStoreApiDTO.getMerchant_name());
		logger.info("After  set the value in map SET" + map);

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		logger.info("Response Getting From API " + response);
		if (response.getStatusCodeValue() == 200) {
			if (response.getBody().contains("msg")) {

				if (StringUtils.isNotBlank(response.getBody())) {
					JSONObject jsonObject = new JSONObject(response.getBody());
					JSONObject mainResponse = jsonObject.getJSONObject("0");
					logger.info("Response Getting From API jsonObject" + mainResponse + "jsonObject.has(\"uuid\")."
							+ mainResponse.getString("uuid"));
					uuid = mainResponse.has("uuid") ? mainResponse.getString("uuid") : "";
					logger.info("Response Getting From API uuid={}" + uuid);

				}
			} else {
				if (StringUtils.isNotBlank(response.getBody())) {
					JSONObject jsonObject = new JSONObject(response.getBody());
					logger.info("Response Getting From API jsonObject" + jsonObject + "jsonObject.has(\"uuid\")."
							+ jsonObject.getString("uuid"));
					uuid = jsonObject.has("uuid") ? jsonObject.getString("uuid") : "";
					logger.info("Response Getting From API uuid={}" + uuid);

				}
			}

		}
		return uuid;
	}

	@Override
	public String generateToken(WebStoreApiDTO webStoreApiDTO) {
		
		String token = "";
		String BASE_URL = PropertiesManager.propertiesMap.get("WEB_STORE_URL");
		logger.info("BASE URL : " + BASE_URL);
		String url = BASE_URL + "api/generate_token/";
		logger.info("Url in web store api={}", url);
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		List<MediaType> media = new ArrayList<>();

		media.add(MediaType.APPLICATION_JSON);
		headers.setAccept(media);
		logger.info("before set the value in map SET");
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("name", webStoreApiDTO.getName());
		map.add("email", webStoreApiDTO.getEmail());
		map.add("password", webStoreApiDTO.getPassword());
		map.add("password_confirmation", webStoreApiDTO.getPassword());
		map.add("uuid", webStoreApiDTO.getUuid());
		logger.info("After  set the value in map SET" + map);

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		logger.info("Response Getting From API token generate " + response);

		JSONObject jsonObject = new JSONObject(response.getBody());
		return jsonObject.has("token") ? jsonObject.getString("token") : null;

	}

	@Override
	public String getByUUID(WebStoreApiDTO webStoreApiDTO) {
	
		System.out.println("INSIDE THE METHOD FETCH ID");
		String BASE_URL = PropertiesManager.propertiesMap.get("WEB_STORE_URL");
		String url = BASE_URL + "api/fetch_user" + "?uuid=" + webStoreApiDTO.getUuid();
		logger.info("UUID :::" + webStoreApiDTO.getUuid());
		logger.info("Url in web store api={}", url);

		logger.info("BEFORE GET THE USER ::::");
		logger.info("UUID IS USED FOR FETCHING THE USER ::" + webStoreApiDTO.getUuid());

		User user = userDao.findByUuid(webStoreApiDTO.getUuid());

		logger.info("AFTER GET THE USER ::::" + user);
		HttpHeaders headers = new HttpHeaders();
		headers.set("accept", "application/json");

		logger.info("TOKEN FROM DB  ::::" + user.getWebStoreApiToken());

		headers.setBearerAuth(user.getWebStoreApiToken());

		logger.info("AFTER SET THE TOEKN :::::::::::");

		HttpEntity requestEntity = new HttpEntity(webStoreApiDTO, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		System.out.println("RESPONSE FROM WEBSTORE" + requestEntity);
		logger.info("Response Getting From API " + responseEntity);

		return responseEntity.getBody();
	}

	@Override
	public String orderDetails(WebStoreApiDTO webStoreApiDTO) {
//4
		System.out.println("INSIDE THE METHOD ORDER DETAILS : " + webStoreApiDTO.getUser_id());

		String BASE_URL = PropertiesManager.propertiesMap.get("WEB_STORE_URL");

		String url = "";
		if (StringUtils.isBlank(webStoreApiDTO.getUser_id())) {
			url = BASE_URL + "api/allorder" + "?" + "uuid=" + webStoreApiDTO.getUuid();
		} else {
			url = BASE_URL + "api/order" + "?user_id=" + webStoreApiDTO.getUser_id() + "&uuid="
					+ webStoreApiDTO.getUuid();
		}
		// url=URLEncoder.encode(url);
		logger.info("Url order details in web store api={}", url);

		HttpHeaders headers = new HttpHeaders();
		headers.set("accept", "application/json");
		User user = userDao.findByUuid(webStoreApiDTO.getUuid());

		headers.setBearerAuth(user.getWebStoreApiToken());

		HttpEntity requestEntity = new HttpEntity(webStoreApiDTO, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		System.out.println("RESPONSE FROM WEBSTORE SERVICE ...ORDER DETAILS:" + requestEntity);
		logger.info("Response Getting From API OF ORDER DETAILS " + responseEntity);

		return responseEntity.getBody();

	}

//		@Override
	public String addProduct(WebStoreApiDTO webStoreApiDTO) {
		String BASE_URL = PropertiesManager.propertiesMap.get("WEB_STORE_URL");

		String url = BASE_URL + "api/add_product";
		logger.info("Url for addProduct in web store api={}", url);

		HttpHeaders headers = new HttpHeaders();

		User user = userDao.findByUuid(webStoreApiDTO.getUuid());

		headers.setBearerAuth(user.getWebStoreApiToken());

		logger.info("AFTER SET THE TOKEN" + user.getWebStoreApiToken());
		// headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		List<MediaType> media = new ArrayList<>();

		media.add(MediaType.APPLICATION_JSON);
		// headers.set("content-type",MediaType.MULTIPART_FORM_DATA_VALUE);
		headers.setAccept(media);

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("image", webStoreApiDTO.getImage());
		map.add("name", webStoreApiDTO.getName());
		map.add("description", webStoreApiDTO.getDescription());
		map.add("price", webStoreApiDTO.getPrice());
		map.add("uuid", webStoreApiDTO.getUuid());
		map.add("discounted_price", webStoreApiDTO.getDiscounted_price());
		map.add("product_status", webStoreApiDTO.isProduct_status() ? 1 : 0);
		logger.info("After  set the value in map SET" + map);

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		logger.info("Response Getting From API " + response);

		return response.getBody();
	}

	@Override
	public String updateProduct(WebStoreApiDTO webStoreApiDTO) {

		String BASE_URL = PropertiesManager.propertiesMap.get("WEB_STORE_URL");

		String url = BASE_URL + "api/update_product";
		logger.info("Url in web store api={}", url);

		HttpHeaders headers = new HttpHeaders();

		headers.set("accept", "application/json");
		User user = userDao.findByUuid(webStoreApiDTO.getUuid());
		headers.setBearerAuth(user.getWebStoreApiToken());

		logger.info("AFTER SET THE TOKEN" + user.getWebStoreApiToken());
		List<MediaType> media = new ArrayList<>();
	

		media.add(MediaType.APPLICATION_JSON);
		headers.setAccept(media);
		logger.info("before set the value in map SET");
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("image", webStoreApiDTO.getImage());
		map.add("id", webStoreApiDTO.getId());
		map.add("name", webStoreApiDTO.getName());
		map.add("description", webStoreApiDTO.getDescription());
		map.add("price", webStoreApiDTO.getPrice());
		map.add("uuid", webStoreApiDTO.getUuid());
		map.add("discounted_price", webStoreApiDTO.getDiscounted_price());
		map.add("product_status", webStoreApiDTO.isProduct_status() ? 1 : 0);
		logger.info("After  set the value in map SET" + map);

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		logger.info("Response Getting From API " + response);

		return response.getBody();

	}

	@Override
	public String fetchProducts(WebStoreApiDTO webStoreApiDTO) {

		String BASE_URL = PropertiesManager.propertiesMap.get("WEB_STORE_URL");

		String url = BASE_URL + "api/view_products" + "?uuid=" + webStoreApiDTO.getUuid();
		logger.info("Url in web store api={}", url);

		HttpHeaders headers = new HttpHeaders();

		headers.set("accept", "application/json");
		User user = userDao.findByUuid(webStoreApiDTO.getUuid());
		logger.info("TOKEN FROM DB " + user.getWebStoreApiToken());

		headers.setBearerAuth(user.getWebStoreApiToken());

		HttpEntity requestEntity = new HttpEntity(webStoreApiDTO, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
				String.class);

		logger.info("Response Getting From API " + responseEntity);

		return responseEntity.getBody();
	}

	public String orderByUuid(WebStoreApiDTO webStoreApiDTO) {

		String BASE_URL = PropertiesManager.propertiesMap.get("WEB_STORE_URL");

		String url = BASE_URL + "api/allorder" + "?uuid=" + webStoreApiDTO.getUuid();
		logger.info("Url in web store api={}", url);

		HttpHeaders headers = new HttpHeaders();

		headers.set("accept", "application/json");
		User user = userDao.findByUuid(webStoreApiDTO.getUuid());
		logger.info("TOKEN FROM DB " + user.getWebStoreApiToken());

		headers.setBearerAuth(user.getWebStoreApiToken());

		HttpEntity requestEntity = new HttpEntity(webStoreApiDTO, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		logger.info("Response Getting From API " + responseEntity);

		return responseEntity.getBody();

	}

	public Map<String, Object> webStoreApi(String type, WebStoreApiDTO webStoreApiDTO) {
		logger.info("INSIDE THE WEBSTORE SERVICE COMMON SERVICE:::::::::::;;" + type);

		Map<String, Object> map = new HashMap<>();

		String TYPE = type.toUpperCase();

		logger.info("WEBSTORE API TYPE::::::::::" + TYPE);
		logger.info("Webstore API Requested data : " + new Gson().toJson(webStoreApiDTO));

		switch (TYPE) {

		case "ADD":
			map.put("response", addProduct(webStoreApiDTO));// fetch
			logger.info("ADD_PRODUCT api INVOKED");
			break;
		case "UPDATE":
			map.put("response", updateProduct(webStoreApiDTO)); // update api call here
			break;
		case "FETCH_USER":
			map.put("response", getByUUID(webStoreApiDTO));// fetch api Here
			break;
		case "ORDER":
			map.put("response", orderDetails(webStoreApiDTO));// call order api here
			break;
		case "VIEW_PRODUCT":
			map.put("response", fetchProducts(webStoreApiDTO));// fetch order api here
			break;
		case "GetBY_UUID":
			map.put("response", orderByUuid(webStoreApiDTO));// fetch
			break;

		default:
			logger.info("No Type found....");

		}
		return map;
	}

}
