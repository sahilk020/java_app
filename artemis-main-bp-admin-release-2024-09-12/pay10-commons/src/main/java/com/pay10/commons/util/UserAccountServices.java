package com.pay10.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.MerchantKeySalt;

@Service
public class UserAccountServices {
	
	private static Logger logger = LoggerFactory.getLogger(UserAccountServices.class.getName());
	
	@Autowired
	private PropertiesManager propertiesManager;
	
	@Autowired 
	MerchantKeySaltService merchantKeySaltService;
	
	/*
	 * public String generateMerchantHostedEncryptionKey(String payId) { String
	 * merchantSalt = propertiesManager.getKeySalt(payId); String generatedKey = "";
	 * if(merchantSalt == null) { logger.
	 * error("Exception while generating merchant hosted encryption key for payid : "
	 * + payId); logger.error("Salt not found."); return generatedKey; } try {
	 * generatedKey = (Hasher.getHash(merchantSalt + payId)).substring(0,32); }
	 * catch (SystemException e) { logger.
	 * error("Exception while generating merchant hosted encryption key for payid : "
	 * + payId); } return generatedKey; }
	 */

	public String generateMerchantHostedEncryptionKey(String payId) {
		MerchantKeySalt merchantKeySalt = merchantKeySaltService.getMerchantKeySalt(payId);
		String generatedKey = "";
		if(merchantKeySalt.getKeySalt() == null ||  merchantKeySalt.getKeySalt().isEmpty()) {
			logger.error("Exception while generating merchant hosted encryption key for payid : " + payId);
			logger.error("Salt not found.");
			return generatedKey;
		}
		try {
			generatedKey = (Hasher.getHash(merchantKeySalt.getKeySalt() + payId)).substring(0,32);
		} catch (SystemException e) {
			logger.error("Exception while generating merchant hosted encryption key for payid : " + payId);
		}
		return generatedKey;
	}
	
}
