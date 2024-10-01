package com.pay10.commons.kms;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AWSEncryptDecryptService {

	private static Logger logger = LoggerFactory.getLogger(AWSEncryptDecryptService.class.getName());
	private static Map<String,String> decryptedDataMap = new HashMap<String,String>();
	
	@Autowired
	AWSEncryptionDecryptionUtil awsEncryptionDecryptionUtil;
	
	public  String decrypt( String data) {
		
		try {
			String decryptedDataFromMap = decryptedDataMap.get(data);
			
			if (StringUtils.isBlank(decryptedDataFromMap)) {
				String decryptedData = awsEncryptionDecryptionUtil.decrypt(data);
				
				decryptedDataMap.put(data,decryptedData);
				return decryptedData;
			}
			
			return decryptedDataFromMap;
		}
		
		catch(Exception e) {
			
			logger.error("Exception occured in AWSEncryptDecryptService , decrypt( String data) , exception =   "+e);
			return null;
		}
		
	}

	public String encrypt(String data) {
		
		try {

			String encryptedData = awsEncryptionDecryptionUtil.encrypt(data);
			return encryptedData;
			
		}
		
		catch(Exception e) {
			logger.error("Exception occured in AWSEncryptDecryptService , encrypt( String data) , exception =   "+e);
			return null;
		}
		
		

	}
}
