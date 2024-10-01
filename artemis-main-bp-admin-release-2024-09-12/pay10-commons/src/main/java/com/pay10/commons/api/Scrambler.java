package com.pay10.commons.api;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class Scrambler {

	public static final String ALGO = "AES";
	private String key = null;
	private Key keyObj = null;

	public Scrambler(String key) {
		this.key = key;
		this.keyObj = new SecretKeySpec(key.getBytes(), ALGO);
	}
	
	public String encryptMain(String data) throws Exception {
		try {
			String ivString = key.substring(0, 16);
			IvParameterSpec iv = new IvParameterSpec(
					ivString.getBytes("UTF-8"));
//			IvParameterSpec iv = new IvParameterSpec(key.getBytes(ConfigurationConstants.DEFAULT_ENCODING_UTF_8.getValue()));
			Cipher cipher = Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, keyObj, iv);

			byte[] encValue = cipher.doFinal(data.getBytes("UTF-8"));

			// Base64.Encoder base64Encoder = Base64.getEncoder().withoutPadding();
			byte[] base64EncodedData = org.apache.commons.codec.binary.Base64.encodeBase64(encValue);// base64Encoder.encodeToString(encValue);
			// return base64EncodedData;
			return new String(base64EncodedData);
		} catch (Exception e) {
			throw new Exception("Error during encryption process");
		}
	}
	
	public Scrambler() {}
	
	public String encrypt(String data){
		
		return  "";
	}
	public String decrypt(String data){
		
		return  "";
	}
}
