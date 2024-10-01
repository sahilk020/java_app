package com.pay10.pg.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.KeySpec;
import java.util.Base64;
//import java.util.Base64;
import java.util.Formatter;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("kotakNBUtils")
public class KotakNBUtils {

	private static Logger logger = LoggerFactory.getLogger(KotakUpiUtils.class.getName());

	private static String AESKey = "boooooooooom!!!!";
	  private static String salt = "asdasdaa2qe21sd12wx2";
	  
	  public String encrypt(String strToEncrypt, String key)
	  {
		  String encryptedStr="PGERRENC002";
	    try
	    {
	      	
	      byte[] ivParams = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	      IvParameterSpec ivspec = new IvParameterSpec(ivParams);
	      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	      byte[] messageArr = strToEncrypt.getBytes();
	      byte[] keyparam = key.getBytes();
	      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	      KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
	      SecretKey tmp = factory.generateSecret(spec);
	      SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
	      
	      byte[] encoded = new byte[messageArr.length + 16];
	      System.arraycopy(ivParams, 0, encoded, 0, 16);
	      System.arraycopy(messageArr, 0, encoded, 16, messageArr.length);
	      
	      cipher.init(1, secretKey, ivspec);
	      
	      byte[] encryptedBytes = cipher.doFinal(encoded);
	      encryptedStr = new String(Base64.getEncoder().encode(encryptedBytes));
	      //encryptedStr = new String(Base64.encodeBase64(encryptedBytes));
	      System.out.println(encryptedStr);
	      return new String(URLEncoder.encode(encryptedStr, "UTF-8"));
	    }
	    catch (Exception e)
	    {
	      encryptedStr="PGERRENC002";
	      System.out.println("Error while encrypting: " + e.toString());
	    }
	    return encryptedStr;
	  }
	  
	  public String decrypt(String strToDecrypt, String key)
	  {
		  String decryptedStr="PGERRDCR001";
	    try
	    
	    {
	      byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	      IvParameterSpec ivspec = new IvParameterSpec(iv);
	      
	      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	      KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
	      SecretKey tmp = factory.generateSecret(spec);
	      SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
	      
	      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	      cipher.init(2, secretKey, ivspec);
	      //System.out.println("strToDecrypt "+strToDecrypt);
	      // decryptedStr=URLDecoder.decode(strToDecrypt, "UTF-8");
	      //System.out.println("decryptedStr "+decryptedStr);
	      //System.out.println("Base64 ::  "+Base64.getDecoder().decode(decryptedStr));
			if (strToDecrypt.contains("%2")) {
				strToDecrypt = URLDecoder.decode(strToDecrypt, "UTF-8");
			}
	      decryptedStr=new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt))).trim();
	      //decryptedStr=new String(cipher.doFinal(Base64.decodeBase64(decryptedStr))).trim();
	      
	      
	      return decryptedStr;
	    }
	    catch (Exception e)
	    {
	      decryptedStr="PGERRDCR001";	
	      System.out.println("Error while decrypting: " + e.toString());
	      e.printStackTrace();
	    }
	    return decryptedStr;
	  }
	  
	  
	  public  String encodeWithHMACSHA2(String text, String keyString) {

			try {
				java.security.Key sk = new javax.crypto.spec.SecretKeySpec(keyString.getBytes("UTF-8"), "HMACSHA512");
				javax.crypto.Mac mac = javax.crypto.Mac.getInstance(sk.getAlgorithm());
				mac.init(sk);
				byte[] hmac = mac.doFinal(text.getBytes("UTF-8"));
				return hmac.toString();
			}

			catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	  
	  private static final String HMAC_SHA512 = "HmacSHA512";

	  private static String toHexString(byte[] bytes) {
	      Formatter formatter = new Formatter();
	      for (byte b : bytes) {
	          formatter.format("%02x", b);
	      }
	      return formatter.toString();
	  }

	  public String calculateHMAC(String data, String key)
	      throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
	  {
	      SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
	      Mac mac = Mac.getInstance(HMAC_SHA512);
	      mac.init(secretKeySpec);
	      return toHexString(mac.doFinal(data.getBytes()));
	  }

	  public byte[] calcHmacSha256(byte[] secretKey, byte[] message) {
		    byte[] hmacSha256 = null;
		    try {
		      Mac mac = Mac.getInstance("HmacSHA256");
		      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
		      mac.init(secretKeySpec);
		      hmacSha256 = mac.doFinal(message);
		    } catch (Exception e) {
		      throw new RuntimeException("Failed to calculate hmac-sha256", e);
		    }
		    return hmacSha256;
		  }
	  
	  


	  public String getHMAC256Checksum(String msg, String checkSumKey) {
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
			final char[] hexArray = "0123456789ABCDEF".toCharArray();
			char[] hexChars = new char[bytes.length * 2];
			for (int j = 0; j <bytes.length; j++) {
				int v = bytes[j] & 0xFF;
				hexChars[j * 2] = hexArray[v >>> 4];
				hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			}
			return new String(hexChars);
		}


	 

}
