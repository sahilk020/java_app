package com.pay10.pg.service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.log4j.Logger;



public class Utils {
	//static Logger log = Logger.getLogger(Utils.class.getName());

	private static final String PROP_FILE_NAME = "/home/IR/irctc.properties"; //UAT	
	private static final String NUMERIC_STRING = "0123456789";
	public static String getSHA(String input) 
	{ 

		try { 
			MessageDigest md = MessageDigest.getInstance("SHA-256"); 
			byte[] messageDigest = md.digest(input.getBytes()); 
			BigInteger no = new BigInteger(1, messageDigest); 
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) { 
				hashtext = "0" + hashtext; 
			} 
			return hashtext; 
		} 

		catch (NoSuchAlgorithmException e) { 
			//log.info("Exception thrown" + " for incorrect algorithm: " + e); 

			System.out.println("Exception thrown" + " for incorrect algorithm: " + e.getMessage());
			return null; 
		} 
	} 

	
	public static String getEncData(String sData, String encKey)
	{		
		try
		{
			if(encKey.length() == 8)
				encKey = encKey+encKey;
			
			String ALGO = "AES";
			byte[] keyByte = encKey.getBytes();
			Key key = new SecretKeySpec(keyByte, ALGO);

			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encVal = c.doFinal(sData.getBytes());
			byte[] encryptedByteValue = java.util.Base64.getEncoder().encode(encVal);
			String encValue = new String(encryptedByteValue);
			
			return encValue;
		}
		catch (Exception e)
		{
			//log.info("PGUtils.java ::: getDecData() :: Error occurred while Encryption : "+e);
			System.out.println("PGUtils.java ::: getDecData() :: Error occurred while Encryption : "+e);
		}
		
		return null;
	}
	
	public static String getDecData(String sData, String decKey)
	{		
		try
		{
			if(decKey.length() == 8)
				decKey = decKey+decKey;
			
			String ALGO = "AES";
			byte[] keyByte = decKey.getBytes();
			Key key = new SecretKeySpec(keyByte, ALGO);
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedByteValue = java.util.Base64.getDecoder().decode(sData.getBytes());

			byte[] decValue = c.doFinal(decryptedByteValue);
			String decryptedValue = new String(decValue);

			return decryptedValue;
		}
		catch (Exception e)
		{
			//log.info("PGUtils.java ::: getDecData() :: Error occurred while Decryption : "+e);
			System.out.println("PGUtils.java ::: getDecData() :: Error occurred while Encryption : "+e);
		}
		return null;
	}
	
	public static String getPropertyValue(String variableName) throws IOException
	{
		FileInputStream inputStream = null;
		String variableValue = null;

		try
		{			 
			File file = new File(PROP_FILE_NAME);
			if(file != null && file.exists())
			{
				inputStream = new FileInputStream(file);		

				if (inputStream != null)
				{
					Properties prop = new Properties();
					prop.load(inputStream);
					variableValue = prop.getProperty(variableName);

					//log.info("PGUtils.java ::: getPropertyValue() :: Key : " + variableName + " ::: Value : " + variableValue);
					System.out.println("PGUtils.java ::: getPropertyValue() :: Key : " + variableName + " ::: Value : " + variableValue);
				} 
			}
			else
			{
				//log.info("PGUtils.java ::: getPropertyValue() :: Property file '" + PROP_FILE_NAME + "' not found in the Classpath");
				System.out.println("PGUtils.java ::: getPropertyValue() :: Key : " + variableName + " ::: Value : " + variableValue);
			}
		}
		catch (Exception e)
		{
			//log.info("PGUtils.java ::: getPropertyValue() :: Exception : "+e.getMessage());
			System.out.println("PGUtils.java ::: getPropertyValue() :: Exception : "+e.getMessage());
		} 
		finally
		{
			if(inputStream != null)
				inputStream.close();
		}
		return variableValue;
	}
	
	public String getPassword()
	{
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32).substring(0,8);
	}
	
	public String getSecretCode()
	  {
	    SecureRandom random = new SecureRandom();
	    return new BigInteger(130, random).toString().substring(2, 8);
	  }
	
	
	
	public static byte[] hex2ByteArray(String sHexData)
	{
		byte rawData[] = new byte[sHexData.length() / 2];
		for(int i = 0; i < rawData.length; i++)
		{
			int index = i * 2;
			int v = Integer.parseInt(sHexData.substring(index, index + 2), 16);
			rawData[i] = (byte)v;
		}

		return rawData;
	}

	public static synchronized String byteToHex(byte byData[])
	{
		StringBuffer sb = new StringBuffer(byData.length * 2);
		for (int i = 0; i < byData.length; i++)
		{
			int v = byData[i] & 0xff;
			if (v < 16)
			{
				sb.append('0');
			}

			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}
	
	public static String randomNumeric(int count) 
	{
		StringBuilder builder = new StringBuilder();
		while (count-- != 0)
		{
			int character = (int)(Math.random()*NUMERIC_STRING.length());
			builder.append(NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}
	
	public static void main(String[] args) {
		String s="merchantCode=1009120911030610|reservationId=1644920623232750|bankTxnId=IG01439494|txnAmount=2000.00";
		System.out.println((getSHA(s)));
	}

}
