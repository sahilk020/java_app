package com.pay10.pg.core.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class AtomEncDecUtil

{
	private static int pswdIterations = 65536;
	private static int keySize = 256;
	private final byte[] ivBytes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

	/*
	 * public String encrypt(String plainText, String key, String merchantTxnId)
	 * throws Exception { this.password = key; this.salt = merchantTxnId; return
	 * encrypt(plainText,key,merchantTxnId); }
	 */

	public String encrypt(String plainText,String password,String salt) throws Exception {
		byte[] saltBytes = salt.getBytes("UTF-8");
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, pswdIterations, keySize);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
		IvParameterSpec localIvParameterSpec = new IvParameterSpec(this.ivBytes);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(1, secret, localIvParameterSpec);
		byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
		return byteToHex(encryptedTextBytes);
	}

	/*
	 * public String decrypt(String encryptedText, String key, String merchantTxnId)
	 * throws Exception { this.password = key; this.salt = merchantTxnId; return
	 * decrypt(encryptedText); }
	 */

	public String decrypt(String encryptedText,String password,String salt) throws Exception {
		byte[] saltBytes = salt.getBytes("UTF-8");
		byte[] encryptedTextBytes = hex2ByteArray(encryptedText);

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, pswdIterations, keySize);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
		IvParameterSpec localIvParameterSpec = new IvParameterSpec(this.ivBytes);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(2, secret, localIvParameterSpec);
		byte[] decryptedTextBytes = null;
		decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
		return new String(decryptedTextBytes);
	}

	private String byteToHex(byte[] byData) {
		StringBuffer sb = new StringBuffer(byData.length * 2);
		for (int i = 0; i < byData.length; ++i) {
			int v = byData[i] & 0xFF;
			if (v < 16)
				sb.append('0');
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	private byte[] hex2ByteArray(String sHexData) {
		byte[] rawData = new byte[sHexData.length() / 2];
		for (int i = 0; i < rawData.length; ++i) {
			int index = i * 2;
			int v = Integer.parseInt(sHexData.substring(index, index + 2), 16);
			rawData[i] = (byte) v;
		}
		return rawData;
	}
	
	public  String getEncodedValueWithSha2(String hashKey, String... param) {
		String resp = null;
		StringBuilder sb = new StringBuilder();
		for (String s : param) {
			sb.append(s);
		}
		try {
			System.out.println("[getEncodedValueWithSha2]String to Encode =" + sb.toString());
			resp = byteToHexString(encodeWithHMACSHA2(sb.toString(), hashKey));
		} catch (Exception e) {
			System.out.println("[getEncodedValueWithSha2]Unable to encocd value with key :" + hashKey + " and input :"
					+ sb.toString());
			e.printStackTrace();
		}
		return resp;
	}
	
	public  byte[] encodeWithHMACSHA2(String text, String keyString) {

		try {
			java.security.Key sk = new javax.crypto.spec.SecretKeySpec(keyString.getBytes("UTF-8"), "HMACSHA512");
			javax.crypto.Mac mac = javax.crypto.Mac.getInstance(sk.getAlgorithm());
			mac.init(sk);
			byte[] hmac = mac.doFinal(text.getBytes("UTF-8"));
			return hmac;
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public  String byteToHexString(byte byData[]) {
		StringBuilder sb = new StringBuilder(byData.length * 2);
		for (int i = 0; i < byData.length; i++) {
			int v = byData[i] & 0xff;
			if (v < 16)
				sb.append('0');
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}
}
