package com.pay10.commons.api;

import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

import com.pay10.commons.user.MerchantKeySaltDao;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.MerchantKeySalt;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MerchantKeySaltService;
import com.pay10.commons.util.PropertiesManager;

@Component
public class Hasher {

	@Autowired
	private PropertiesManager propertiesManager;

	public Hasher() {
	}
	private static Logger logger = LoggerFactory.getLogger(Hasher.class.getName());

	public static String getHash(String input) throws SystemException {
		String response = null;

		MessageDigest messageDigest = MessageDigestProvider.provide();
		messageDigest.update(input.getBytes());
		MessageDigestProvider.consume(messageDigest);

		response = new String(Hex.encodeHex(messageDigest.digest()));

		return response.toUpperCase();
	}// getSHA256Hex()


	public static String getHash(Fields fields) throws SystemException {
		MerchantKeySalt  merchantKeySalt = MerchantKeySaltService.merchantKeySaltMap.get(fields.get(FieldType.PAY_ID.getName()));
		String salt = null;
		if(null != merchantKeySalt) {
			salt = merchantKeySalt.getSalt();
		}
		if (StringUtils.isBlank(salt)) {
			salt = (new MerchantKeySaltService()).getSalt(fields.get(FieldType.PAY_ID.getName()));
			if (salt != null) {
				logger.info("Salt found from propertiesManager for payId = " + fields.get(FieldType.PAY_ID.getName()));
			}
		}

		if (null == salt) {
			logger.info("Inside Hasher , salt = null , fields = " + fields.getFieldsAsBlobString());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_PAYID_ATTEMPT.getCode());
			throw new SystemException(ErrorType.INVALID_PAYID_ATTEMPT, "Invalid " + FieldType.PAY_ID.getName());
		}

		logger.info("Salt found from static map in propertiesManager for payId = " + fields.get(FieldType.PAY_ID.getName()));
		// Sort the request map
		Fields internalFields = fields.removeInternalFields();

		Map<String, String> treeMap = new TreeMap<String, String>(fields.getFields());

		fields.put(internalFields);
		//logger.info("hash check..={}", fields.getFieldsAsString());
		// Calculate the hash string
		StringBuilder allFields = new StringBuilder();
		for (String key : treeMap.keySet()) {
			allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
			allFields.append(key);
			allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
			allFields.append(fields.get(key));
		}

		allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
		allFields.append(salt);
		logger.info(" ############# allFields ############# " + allFields.toString());

		// Calculate hash at server side
		return getHash(allFields.toString());
	}

	public static String getHash(Map<String, String> input, String hashKey) throws SystemException {

		// Append salt of merchant
		String salt = null;
		salt = PropertiesManager.saltStore.get(input.get(FieldType.PAY_ID.getName()));
		if (StringUtils.isBlank(salt)) {
			salt = (new PropertiesManager()).getSalt(input.get(FieldType.PAY_ID.getName()));
			if (salt != null) {
				logger.info("Salt found from propertiesManager for payId = "+input.get(FieldType.PAY_ID.getName()));
			}else{
				salt = (new MerchantKeySaltDao()).find(input.get(FieldType.PAY_ID.getName())).getSalt();
			}

		}
		else{
			logger.info("Salt found from static map in propertiesManager for payId = "+input.get(FieldType.PAY_ID.getName()));
		}

		if (null == salt) {
			throw new SystemException(ErrorType.INVALID_PAYID_ATTEMPT,
					"Invalid " + FieldType.PAY_ID.getName());
		}
		logger.info("hash check..={}",input);
		// Calculate the hash string
		StringBuilder allFields = new StringBuilder();
		Map<String, String> treeMap = new TreeMap<String, String>(
				input);
		for (String key : treeMap.keySet()) {
			allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
			allFields.append(key);
			allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
			allFields.append(treeMap.get(key));
		}

		allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
		allFields.append(salt);
		logger.info(" ############# allFields ############# "+allFields.toString());

		// Calculate hash at server side
		return EmitraHasher.getHash(allFields.toString(), hashKey);
	}

	public static String getHashWithSalt(Fields fields , String salt) throws SystemException {

		Fields internalFields = fields.removeInternalFields();
		Map<String, String> treeMap = new TreeMap<String, String>(
				fields.getFields());
		fields.put(internalFields);

		// Calculate the hash string
		StringBuilder allFields = new StringBuilder();
		for (String key : treeMap.keySet()) {
			allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
			allFields.append(key);
			allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
			allFields.append(fields.get(key));
		}

		allFields.deleteCharAt(0);
		allFields.append(salt);

		// Calculate hash at server side
		return getHash(allFields.toString());
	}
}
