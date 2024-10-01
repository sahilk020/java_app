package com.pay10.commons.api;

import java.io.File;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pay10.commons.dto.WebStoreApiDTO;

public interface WebStoreApiService{

	String generateUUID(WebStoreApiDTO webStoreApiDTO);

	String generateToken(WebStoreApiDTO webStoreApiDTO);

	String getByUUID(WebStoreApiDTO webStoreApiDTO);

	String orderDetails(WebStoreApiDTO webStoreApiDTO);
	
	String addProduct(WebStoreApiDTO webStoreApiDTO);
	
	String updateProduct(WebStoreApiDTO webStoreApiDTO);
	
	String fetchProducts(WebStoreApiDTO webStoreApiDTO);
	String orderByUuid(WebStoreApiDTO webStoreApiDTO);
	
	Map<String,Object> webStoreApi(String type,WebStoreApiDTO webStoreApiDTO);
	

}
