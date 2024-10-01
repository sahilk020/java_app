package com.pay10.commons.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswordGenerator {

	public static String KEK ="ZEGs0367ZEGs0367ZEGs0367ZEGs0367";
	public static String IV ="ZEGs0367ZEGs0367";



	public static String getDEK(String plainKey) { //DEK

		return getEncryptedString(IV,KEK,plainKey);

	}

	public static String getEncryptedPassword(String password, String DEK) { //DEK

		String plainDEK = getDeryptedPDEK(DEK);
		System.out.println(" plainDEK " +plainDEK);
		return getEncryptedString(IV,plainDEK,password);

	}



	public static String getEncryptedString(String initVector, String key, String value )
	{
		try
		{

			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(1, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			System.out.println("encrypted string: " + 
					java.util.Base64.getEncoder().encodeToString(encrypted));

			return java.util.Base64.getEncoder().encodeToString(encrypted);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public static String getDeryptedPDEK(String DEK) {

		try
		{

			IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(KEK.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(2, skeySpec, iv);

			byte[] original = cipher.doFinal(java.util.Base64.getDecoder().decode(DEK));
			return new String(original);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;

	}
	public static String getDeryptedPassword(String encPassword, String DEK) {
		
		String plainDEK = getDeryptedPDEK(DEK);
		try
		{

			IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(plainDEK.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(2, skeySpec, iv);

			byte[] original = cipher.doFinal(java.util.Base64.getDecoder().decode(encPassword));
			return new String(original);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;

	}

	

	  public static void main(String[] args) {
	  
	  //One time activity Start ------ to generate password and encrypted DEK for
	  //String plainDEK = "12456789012345678901234567890121";
	  
	  String plainDEK="82725420523831896081492342740741";
	  
	  String encDEK = getDEK(plainDEK);
	  System.out.println("encDEK  >>>>>>>> "+encDEK); //set to property file
	  
	  String encPassword = getEncryptedPassword("Google5wa1",encDEK);
	  
	  System.out.println("encPassword  >>>>>>>> "+encPassword); //set to property
	  
	  //////////////////One time activity ends
	  
	  String decrytedPassword = getDeryptedPassword("WbW9HjfQva2XCHAFFYf4ng==", encDEK);
	  
	  System.out.println("decrytedPassword  >>>>>>>> "+decrytedPassword);
	  
	  }

	 
}
