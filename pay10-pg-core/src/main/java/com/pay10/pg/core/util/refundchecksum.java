package com.pay10.pg.core.util;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;




public class refundchecksum {
	@Autowired
	RestTemplate restTemplate;
	
	public static String getHMAC256Checksum(String msg, String checkSumKey) {
		Mac sha512_HMAC = null;
		String result = null;
		try {
			byte[] byteKey = checkSumKey.getBytes("UTF-8");
			final String HMAC_SHA256 = "HmacSHA256";
			sha512_HMAC = Mac.getInstance(HMAC_SHA256);
			SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA256);
			sha512_HMAC.init(keySpec);
			byte[] mac_data = sha512_HMAC.doFinal((msg).getBytes("UTF-8"));
			// result = Base64.toBase64String(mac_data);
			result = bytesToHex(mac_data);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("Done");
		}
		return result;

	}

	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = "0123456789abcdef".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j <bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	
public static void main(String[] args){
	

	String checkdata="0050|6603202211|OSENAM|1009120307111447|1009120307111447|1.00||";
//	String checkdata="0500|1585051995990|OSKOTAK|1585051995990|1|RAZORPAY|||";
	String key="KMBANK";
	String checksum = getHMAC256Checksum(checkdata, key);
System.out.println("cehck sun data        "+checksum);
}

}
