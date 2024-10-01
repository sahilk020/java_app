package com.pay10.pg.core.util;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.pay10.commons.exception.SystemException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Base64;
public class refundkotaknb {

	
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

	
	
	public static String encrypt(String message, String key)
			throws GeneralSecurityException, UnsupportedEncodingException {
		if (message == null || key == null) {
			throw new IllegalArgumentException("text to be encrypted and key should not be null");
		}
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		byte[] messageArr = message.getBytes();
		SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
		byte[] ivParams = new byte[16];
		byte[] encoded = new byte[messageArr.length + 16];
		System.arraycopy(ivParams, 0, encoded, 0, 16);
		System.arraycopy(messageArr, 0, encoded, 16, messageArr.length);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ivParams));
		byte[] encryptedBytes = cipher.doFinal(encoded);
		encryptedBytes = Base64.getEncoder().encode(encryptedBytes);
		return new String(encryptedBytes);
	}
	
	public static String decrypt(String encryptedStr, String key)
			throws GeneralSecurityException, UnsupportedEncodingException {
		if (encryptedStr == null || key == null) {
			throw new IllegalArgumentException("text to be decrypted and key should not be null");
		}
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
		byte[] encoded = encryptedStr.getBytes();
		encoded = Base64.getDecoder().decode(encoded);
		byte[] decodedEncrypted = new byte[encoded.length - 16];
		System.arraycopy(encoded, 16, decodedEncrypted, 0, encoded.length - 16);
		byte[] ivParams = new byte[16];
		System.arraycopy(encoded, 0, ivParams, 0, ivParams.length);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ivParams));
		byte[] decryptedBytes = cipher.doFinal(decodedEncrypted);
		return new String(decryptedBytes);
	}
	//String checksum = KotakNBUtils.getHMAC256Checksum(paymentStringBuilder.toString(), checksumKey);
	public static String refundPostRequest1() throws SystemException {
		String response = "";

		try {
			String request="wj7670zOWut/obnVtztMO6Wa1lYoW2JujH8qfQIgatb4DxveIM7kLi8YjBbpAQcS/groj9wKbZUHWlbKO74AY/JgZETtAB9xNYKp4+8VVqzdwHSU9H/btwbJJ0MwVKh3oW+qcNJfIIvHFCjg5tA1vbq5wwirjrt2idfKKJ//DB0N+F0W7oWmDDViOTDd60d3/ynF6Wug88Qkp4l401RXWw==";
			String refundurl="https://apigwuat.kotak.com:8443/KBSecPG";
		

			HttpURLConnection connection = null;
			URL url;
			url = new URL(refundurl);
			

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type","text/xml");
			connection.setRequestProperty("Authorization","Bearer fdb8e521-40de-46de-8f3b-f601051a5f41");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
		    connection.setConnectTimeout(60000);
		    connection.setReadTimeout(60000);
			
			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(request);
			wr.flush();
			wr.close();
		
			// Get Response
			InputStream is = connection.getInputStream();
			
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
			String decodedString;
			
			while ((decodedString = bufferedreader.readLine()) != null) {
				
				response = response + decodedString;
				
			}
			
			bufferedreader.close();
			
			System.out.println(" kotak bank NB Response for refund transaction >> " + response);
			
		

		} catch (Exception e) {
		System.out.println("Exception in getting Refund respose for kotak bank NB"+e);
			response = "{\"message\":\"Error in Refund.\",\"refundId\":NA,\"status\":\"FAILED\"}";
			return response;
		}
		return response;
	}
	
	
	
	
	
	

}
