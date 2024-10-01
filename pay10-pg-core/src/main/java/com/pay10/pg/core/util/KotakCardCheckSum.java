package com.pay10.pg.core.util;

import java.security.Key;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import com.google.gson.internal.LinkedTreeMap;

@Component
public class KotakCardCheckSum {
	
public String getHashValue(Map<Object, Object> fields, String sSalt) throws Exception {
		
		String sHashString = null;

		try {

			while (fields.values().remove(null))
				;

			Map<Object, Object> myMap = new LinkedHashMap<>();

			for (Object str : fields.keySet()) {

				if (fields.get(str).getClass() == LinkedTreeMap.class) {

					for (Object str2 : ((LinkedTreeMap) fields.get(str)).keySet()) {

						if (((LinkedTreeMap) fields.get(str)).get(str2).getClass() == LinkedTreeMap.class) {

							for (Object str3 : ((LinkedTreeMap) ((LinkedTreeMap) fields.get(str)).get(str2)).keySet()) {
								myMap.put(str3.toString(), ((LinkedTreeMap) ((LinkedTreeMap) fields.get(str)).get(str2))
										.get(str3).toString());
							}

						} else {
							myMap.put(str2.toString(), ((LinkedTreeMap) fields.get(str)).get(str2).toString());
						}
					}

				} else {
					myMap.put(str, fields.get(str).toString());
				}

			}

			List fieldNames = new ArrayList(myMap.keySet());
			Collections.sort(fieldNames);

			System.out.println("fieldNames for hashing -> " + fieldNames);

			StringBuffer buf = new StringBuffer();
			buf.append(sSalt);

			Iterator itr = fieldNames.iterator();
			while (itr.hasNext()) {
				Object fieldName = (Object) itr.next();
				Object fieldValue = (Object) myMap.get(fieldName);
				if ((fieldValue != null) && !fieldValue.equals("")) {
					buf.append(fieldValue);
				}
			}

			System.out.println("buffer -> " + buf);

			StringBuffer hexString = new StringBuffer();

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(buf.toString().getBytes("UTF-8"));
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			sHashString = hexString.toString();
		} catch (Exception e) {

			System.out.println("Exception in getHashValue -> [" + e + "]");
			e.printStackTrace();

			throw e;

		}

		return sHashString;
	}
public  String decrypt(String encryptedData, String keySet) throws Exception {
	byte[] keyByte = keySet.getBytes();
	Key key = generateKey(keyByte);
	Cipher c = Cipher.getInstance("AES");
	c.init(Cipher.DECRYPT_MODE, key);
	byte[] decryptedByteValue = new Base64().decode(encryptedData.getBytes());
	byte[] decValue = c.doFinal(decryptedByteValue);
	String decryptedValue = new String(decValue);
	return decryptedValue;
}

public static String encrypt(String Data, String keySet) throws Exception {
	byte[] keyByte = keySet.getBytes();
	Key key = generateKey(keyByte);
	Cipher c = Cipher.getInstance("AES");
	c.init(Cipher.ENCRYPT_MODE, key);
	byte[] encVal = c.doFinal(Data.getBytes());
	byte[] encryptedByteValue = new Base64().encode(encVal);
	String encryptedValue = new String(encryptedByteValue);
	return encryptedValue;
}

public static String decryptsir(String encryptedData, String keySet) throws Exception {
	byte[] keyByte = keySet.getBytes();
	Key key = generateKey(keyByte);
	Cipher c = Cipher.getInstance("AES");
	c.init(Cipher.DECRYPT_MODE, key);
	byte[] decryptedByteValue = new Base64().decode(encryptedData.getBytes());
	byte[] c1 = new byte[decryptedByteValue.length + 11];
	byte[] padded = {'0','0','0','0','0','0','0','0','0','0','0'};
	System.arraycopy(padded, 0, c1, 0, padded.length);
	System.arraycopy(decryptedByteValue, 0, c1, padded.length, decryptedByteValue.length);
	System.out.println(c1.length);
	System.out.println(new String(c1));
	byte[] decValue = c.doFinal(c1);
	String decryptedValue = new String(decValue);
	return decryptedValue;
}

private static Key generateKey(byte[] keyByte) throws Exception {
	Key key = new SecretKeySpec(keyByte, "AES");
	return key;
}
public static void main(String[] args) throws Exception {

String response="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";
//System.out.println(new String(new Base64().decode(response.getBytes())));
String key ="3a5c1c4e81d7eb133a5c1c4e81d7eb13";
//System.out.println(decrypt(response,key));
	
}
}
