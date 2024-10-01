package com.pay10.pg.core.util;

import java.security.MessageDigest;

import org.springframework.stereotype.Service;

@Service
public class ApblEncDecUtil

{
	
	public String getHash(String text) throws Exception {
		
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(text.getBytes());

		byte byteData[] = md.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {

			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		
		return hexString.toString();
		
		
	}
	
}
