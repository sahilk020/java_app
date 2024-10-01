package com.pay10.commons.common;

import com.pay10.commons.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Random;

public class EncryptDecrypt {
	private static Logger logger = LoggerFactory.getLogger(EncryptDecrypt.class.getName());
	//import key(KEK) from property file 
	private static final String key = "QpXpikntE1iaxcCK";//"aesEncryptionKey";
	private static final String initVector = "53d2b3dee01da699";//"encryptionIntVec"; //53d2b3dee01da699  //QpXpikntE1iaxcCK

	//Disabled encryption temporarily
	public static String encryptData(String value,String decryptedDEK) {
		return value;
//		try {
//			IvParameterSpec iv = new IvParameterSpec(decryptedDEK.getBytes("UTF-8"));
//			SecretKeySpec skeySpec = new SecretKeySpec(decryptedDEK.getBytes("UTF-8"), "AES");
//
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
//
//			byte[] encrypted = cipher.doFinal(value.getBytes());
//			return Base64.getEncoder().encodeToString(encrypted);
//		} catch (Exception ex) {
//			logger.error("Encryption Error : "+ex.getMessage());
//		}
//		return null;
	}



	//Disabled decryption temporarily
	public static String decryptData(String encrypted,String decryptedDEK) {
		return encrypted;
//		try {
//
//			IvParameterSpec iv = new IvParameterSpec(decryptedDEK.getBytes("UTF-8"));
//			SecretKeySpec skeySpec = new SecretKeySpec(decryptedDEK.getBytes("UTF-8"), "AES");
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
//			return new String(original);
//		} catch (Exception ex) {
//			//ex.printStackTrace();
//			logger.error("Decryption Error : "+ex.getMessage());
//		}
//
//		return null;
	}

	public static String encryptDEK(String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String decryptDEK(String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	public static String getRandomDEKKey(int n) {
		Random rnd = new Random();
		byte[] array = new byte[256];
		rnd.nextBytes(array);
		String randomString = new String(array, Charset.forName("UTF-8"));

		// Create a StringBuffer to store the result
		StringBuffer r = new StringBuffer();

		// Append first 16 alphanumeric characters
		// from the generated random String into the result
		for (int k = 0; k < randomString.length(); k++) {

			char ch = randomString.charAt(k);

			if (((ch >= 'a' && ch <= 'z')
					|| (ch >= 'A' && ch <= 'Z')
					|| (ch >= '0' && ch <= '9'))
					&& (n > 0)) {

				r.append(ch);
				n--;
			}
		}

		// return the resultant string
		return r.toString();

	}



	public static void main(String[] args) {
		//String originalString = "this is pan number 983370009200920000 ";
		//String DEK = getRandomDEKKey(16);
		//System.out.println(" DEK - " + DEK);

		//System.out.println("Original String to encrypt - " + originalString);
		//String encryptedString = encryptData(originalString,DEK);
		//System.out.println("Encrypted String - " + encryptedString);
		//String decryptedString = decryptData(encryptedString,DEK);
		//System.out.println("After decryption - " + decryptedString);
		
		//String encryptedDEK = encryptDEK(DEK);
		//Encrypt the DEK with KEK 
		//System.out.println("encryptedDEK >>> "+encryptedDEK);
		//Store encrypted DEK in database
		
	}

}
