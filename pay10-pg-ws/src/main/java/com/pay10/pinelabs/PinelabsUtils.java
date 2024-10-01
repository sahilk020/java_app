package com.pay10.pinelabs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class PinelabsUtils {

	/**
	 * This method will generate key hash
	 * 
	 * @param input
	 * @param strSecretKey
	 * @return
	 */

	private static Logger logger = LoggerFactory.getLogger(PinelabsUtils.class.getName());

	public String GenerateHash(String input, String strSecretKey) {
		String strHash = "";
		try {
			if (!isValidString(input) || !isValidString(strSecretKey)) {
				return strHash;
			}
			byte[] convertedHashKey = new byte[strSecretKey.length() / 2];
			for (int i = 0; i < strSecretKey.length() / 2; i++) {
				convertedHashKey[i] = (byte) Integer.parseInt(strSecretKey.substring(i * 2, (i * 2) + 2), 16); // hexNumber
																												// radix
			}
			strHash = hmacDigest(input.toString(), convertedHashKey, "HmacSHA256");
		} catch (Exception ex) {
			strHash = "";
		}
		return strHash.toUpperCase();
	}

	private String hmacDigest(String msg, byte[] keyString, String algo) {
		String digest = null;
		try {
			SecretKeySpec key = new SecretKeySpec(keyString, algo);
			Mac mac = Mac.getInstance(algo);
			mac.init(key);
			byte[] bytes = mac.doFinal(msg.getBytes("UTF-8"));
			StringBuffer hash = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					hash.append('0');
				}
				hash.append(hex);
			}
			digest = hash.toString();
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception occured in hashing the pine payment gateway request" + e);
		} catch (InvalidKeyException e) {
			logger.error("Exception occured in hashing the pine payment gateway request" + e);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception occured in hashing the pine payment gateway request" + e);
		}
		return digest;
	}

	public boolean isValidString(String str) {
		if (str != null && !"".equals(str.trim())) {
			return true;
		}
		return false;
	}
}
