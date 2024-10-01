package com.pay10.crypto.key;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.MerchantKeySalt;
import com.pay10.commons.util.MerchantKeySaltService;
import com.pay10.commons.util.PropertiesManager;

@Service
public class DefaultKeyProvider implements KeyProvider {

	private static Logger logger = LoggerFactory.getLogger(DefaultKeyProvider.class.getName());

	@Autowired
	private PropertiesManager propertiesManager;
	
	@Autowired 
	MerchantKeySaltService merchantKeySaltService;
	
	/*
	 * @Override public String generateKey(String payId) throws SystemException {
	 * String salt = propertiesManager.getKeySalt(payId);
	 * logger.info("Creating new key for PAY_ID: " + payId); if(salt==null) { throw
	 * new SystemException(ErrorType.USER_NOT_FOUND,"No such user"); } String
	 * generatedKey = (Hasher.getHash(salt+payId)).substring(0,32); return
	 * generatedKey; }
	 */
	
	@Override
	public String generateKey(String payId) throws SystemException {
		MerchantKeySalt merchantKeySalt = merchantKeySaltService.getMerchantKeySalt(payId);
		logger.info("Creating new key for PAY_ID:: " + payId);
		if(merchantKeySalt == null || merchantKeySalt.getKeySalt() == null||  merchantKeySalt.getKeySalt().isEmpty()) {
			throw new SystemException(ErrorType.USER_NOT_FOUND,"No such user");
		}
		String generatedKey = (Hasher.getHash(merchantKeySalt.getKeySalt()+payId)).substring(0,32);
		return generatedKey;
	}
}

