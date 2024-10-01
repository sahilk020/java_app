package com.pay10.pg.core.util;
import javax.crypto.Mac;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Component
public class IdfcChecksum {
	
	private static Logger logger = LoggerFactory.getLogger(IdfcChecksum.class.getName());

	private final char[] HEX_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	 //secret Key – Key shared between merchant and IDFC
	 //Data String is pipe delimited values
	public String getSecureSHAHash(String secretKey, String dataString) throws Exception { 
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());

		byte[] mac = null;
	SecretKey key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA512"); 
	Mac m = Mac.getInstance("HmacSHA512"); 
	m.init(key); 
	m.update(dataString.getBytes("UTF-8")); 
	mac = m.doFinal(); 
	String hashValue = hex(mac); 
	return hashValue; 
	}
	public String hex(byte[] input) throws Exception 
	{ 
	StringBuffer sb = new StringBuffer(input.length * 2); 
	for (int i = 0; i < input.length; i++) { 
	sb.append(this.HEX_TABLE[(input[i] >> 4 & 0xF)]); 
	sb.append(this.HEX_TABLE[(input[i] & 0xF)]); 
	}
	return sb.toString(); 
	}
	
		private static SecretKeySpec secretKey;
		private static byte[] key;
		public static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
		key = myKey.getBytes("UTF-8");
		sha = MessageDigest.getInstance("SHA-256");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16);
		secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
		}
		}
		public static String encrypt(String strToEncrypt, String secret) {
		try {
			secret = Base64.getEncoder().encodeToString(secret.getBytes());

		setKey(secret);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return
		Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			logger.error("Error while encrypting: " + e.toString());
		}
		return null;
		}
		
		public static String decrypt(String strToDecrypt, String secret) {
		try {
		secret = Base64.getEncoder().encodeToString(secret.getBytes());

		setKey(secret);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
		logger.error("Error while decrypting: " + e.toString());
		}
		return null;
		}
	
}
