package com.crmws.loadtest;

import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Hex;

public class CustomHasher {

	public CustomHasher() {
	}

	public static String generateKey(String payId, String salt) throws Exception {
		// String salt = propertiesManager.getSalt(payId);
		if (salt == null) {
			throw new Exception("No such user");
		}
		String generatedKey = (getHash(salt + payId)).substring(0, 32);
		return generatedKey;
	}

	public static String getHash(Map<String, String> fields, String salt) throws Exception {

		// Append salt of merchant
		if (salt == null || salt.isEmpty()) {
			throw new Exception("Null value of salt provided");
		}

		// Sort the request map
		Map<String, String> treeMap = new TreeMap<String, String>(fields);

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

		// Calculate hash at server side
		return getHash(allFields.toString());
	}

	public static String getHash(String input) throws Exception {
		String response = null;

		MessageDigest messageDigest = MessageDigestProvider.provide();
		messageDigest.update(input.getBytes());
		MessageDigestProvider.consume(messageDigest);

		response = new String(Hex.encodeHex(messageDigest.digest()));

		return response.toUpperCase();
	}// getSHA256Hex()

	public static Scrambler getScrambler(String payId, String salt) throws Exception {
		Scrambler scrambler;
		// Generate Key for that pay ID
		String key = CustomHasher.generateKey(payId, salt);
		scrambler = new Scrambler(key);
		// scramblerMap.put(payId, scrambler);
		return scrambler;
	}

	public static String decrypt(String payId, String salt, String data) throws Exception {
		Scrambler scrambler = getScrambler(payId, salt);
		return scrambler.decrypt(data);
	}

	public static String encrypt(String payId, String salt, String data) throws Exception {
		Scrambler scrambler = getScrambler(payId, salt);
		return scrambler.encrypt(data);
	}

}
