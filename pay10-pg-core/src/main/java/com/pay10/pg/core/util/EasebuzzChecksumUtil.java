
package com.pay10.pg.core.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EasebuzzChecksumUtil {
	private static final String HASH_ALGORITHM = "HmacSHA256";
	private static Logger logger = LoggerFactory.getLogger(EasebuzzChecksumUtil.class.getName());
	private static Mac sha256_HMAC_Static;

	public String encrypt(String value, String key, String initVector) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private static String openssl_encrypt(String data, String strKey, String strIv) throws Exception {
		Base64 base64 = new Base64();
		Cipher ciper = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		SecretKeySpec key = new SecretKeySpec(strKey.getBytes(), "AES");
		// IvParameterSpec iv = new IvParameterSpec(strIv.getBytes(), 0, 16);
		IvParameterSpec iv = new IvParameterSpec(strIv.getBytes("UTF-8"), 0, 16);

		// Encrypt
		ciper.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] encryptedCiperBytes = ciper.doFinal(data.getBytes());

		System.out.println("encryptedCiperBytes " + new String(encryptedCiperBytes));
		// System.out.println("--------------- @@
		// "+base64.encodeAsString(encryptedCiperBytes));
		String s = new String(encryptedCiperBytes);
		System.out.println("Ciper : " + encryptedCiperBytes.toString());
		return s;
	}

	public static String decrypt(String encrypted, String key, String initVector) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	/*
	 * public String getHash(String hashcode, String Checksumkey) {
	 * 
	 * String result = ""; try { byte[] keyBytes = Checksumkey.getBytes();
	 * SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HASH_ALGORITHM);
	 * 
	 * Mac mac = Mac.getInstance(HASH_ALGORITHM); mac.init(signingKey); byte[]
	 * rawHmac = mac.doFinal(hashcode.getBytes());
	 * 
	 * byte[] hexBytes = new Hex().encode(rawHmac); result = new String(hexBytes,
	 * "UTF-8"); return result.toUpperCase(); } catch (Exception e) {
	 * 
	 * logger.error("Error in getting hash", e); } return result;
	 * 
	 * }
	 */

	public String checksumForInitiateLink(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] messageDigest = md.digest(input.getBytes());

			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);

			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

			return hashtext.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String sha512Hash(String input) {
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

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String decrypt(String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec("53d2b3dee01da699".getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec("ae8f5fcd51fd66de0328bbff87574ed2".getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(input.getBytes(StandardCharsets.UTF_8));
	}

	public static String toHexString(byte[] hash) {
		// Convert byte array into signum representation
		BigInteger number = new BigInteger(1, hash);

		// Convert message digest into hex value
		StringBuilder hexString = new StringBuilder(number.toString(16));

		// Pad with leading zeros
		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}

		return hexString.toString();
	}
	public static String checksumForInitiateLink1(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] messageDigest = md.digest(input.getBytes());

			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);

			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

			return hashtext.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

public static String getfloatamoutforrefund(String input) {
		
		String[] words=input.split("\\.");
		if (words[1].equalsIgnoreCase("00")) {
			words[1]="0";}
		else {
				String amount1=words[1];
				String data=String.valueOf(amount1.charAt(amount1.length()-1));
				String data1=String.valueOf(amount1.charAt(amount1.length()-2));

				
			if(	data.equalsIgnoreCase("0")){
				words[1]=data1;


				
			}
				
			}
			String amount =String.join(".",words[0],words[1]);
	
		
		return amount;
		
	}
	public static void main(String[] args) throws Exception {
		
		
		String merchantKey = "L0O2G1PMIR|E220315PNTXVBL|1.0|1.0|abhishek@paytm.com|9769483293|K6KARMD7UX";
		System.out.println("aaaaaaaaaaaaaaa"+checksumForInitiateLink1(merchantKey));
		
	String amount="1111.001";
	String[] words=amount.split("\\.");
	if (words[1].equalsIgnoreCase("00")) {
		words[1]="0";}
		String abhishek=String.join(".",words[0],words[1]);
	System.out.println("  aaaaaaa"+abhishek);
//	String amount1=words[0]"."words[1];
//	System.out.println("  aaaaaaa"+amount1);	


//		String merchantKey = "L0O2G1PMIR";
//		String merchantSalt = "K6KARMD7UX";
//		//String key = "D4MK4S2MSO";
//		//String salt = "WB3BA4TNEY";
//		System.out.println("Encryption Key : " + sha512Hash(merchantKey).substring(0, 32));
//		System.out.println("Encryption IV : " + sha512Hash(merchantSalt).substring(0, 16));
//
//		String cardNumber = "FvRbWWF1fzwoxDAOzcJ1Ag==";
//		//String var = new String(Base64.decodeBase64("lc4BAsVbmzCPR4bFvBKOCvWG8YyctSB59k2CHo8KP1c".getBytes()));
//		System.out.println("Encrypt String :: " + new EasebuzzChecksumUtil().encrypt("4160210818854351","3a757961cd6d8ef8bc040c204a8fc77b", "3d4fa0db5db10923"));
//		System.out.println(decrypt(cardNumber, "3a757961cd6d8ef8bc040c204a8fc77b", "3d4fa0db5db10923"));
//		//System.out.println("--------- " + openssl_encrypt("lc4BAsVbmzCPR4bFvBKOCvWG8YyctSB59k2CHo8KP1c=", "ae8f5fcd51fd66de0328bbff87574ed2", "53d2b3dee01da699"));
//		
		//System.out.println("--------- " + decrypt(var));
		//String plainTest = "L0O2G1PMIR|1012120121084605|1.00|NA|sonu|sonu@pay10.com|||||||||||K6KARMD7UX";
		//System.out.println("checksum :: "+new EasebuzzChecksumUtil().checksumForInitiateLink(plainTest));
	}

	//Za5L6x9fs6uKbCJ2JVCMTagAyByrgfzciytN4JpSzWU=
	/*
	 * // Driver code public static void main(String args[]) { try {
	 * 
	 * System.out.println("HashCode Generated by SHA-256 for:");
	 * 
	 * String s1 = "2PBP7IABZ2"; System.out.println("\n" + s1 + " : " +
	 * toHexString(getSHA(s1))); String keyhash = toHexString(getSHA(s1));
	 * System.out.println("keyhash" + keyhash);
	 * 
	 * // Encoding.UTF8.GetBytes("Your string with some interesting data").Take(32);
	 * 
	 * } // For specifying wrong message digest algorithms catch
	 * (NoSuchAlgorithmException e) {
	 * System.out.println("Exception thrown for incorrect algorithm: " + e); } }
	 */
	/*
	 * public static void main(String[] args) { StringBuilder result =
	 * getBinary("hi there", 2); System.out.println(result.toString()); }
	 */

	/*
	 * public static StringBuilder getBinary(String str, int
	 * numberOfCharactersWanted) { StringBuilder result = new StringBuilder();
	 * byte[] byt = str.getBytes(); for (int i = 0; i < numberOfCharactersWanted;
	 * i++) { result.append(String.format("%8s",
	 * Integer.toBinaryString(byt[i])).replace(' ', '0')).append(' '); }
	 */

}
