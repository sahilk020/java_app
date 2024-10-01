package com.pay10.pg.core.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AxisBankNBEncDecService {
	
	private static Logger logger = LoggerFactory.getLogger(AxisBankNBEncDecService.class.getName());
	
	//code added by sonu 
	public String encrypt(String salt, String iv, String encKey, String painString) {
		Cipher cipher = null;
		byte[] encrypted = null;
		String encVal = "";

		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKey key = generateKey1(salt, encKey);
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(hex(iv)));
			encrypted = cipher.doFinal(painString.getBytes("UTF-8"));
			encVal = new String(Base64.encodeBase64(encrypted));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception  : "+e);
		}

		return encVal;

	}

	private SecretKey generateKey1(String salt, String passphrase) {
		SecretKey key = null;
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), 10, 256);
			key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception  : "+e);
		}
		return key;

	}

	public static byte[] hex(String str) {
		return DatatypeConverter.parseHexBinary(str);
	}

	public String decrypt(String salt, String iv, String encKey, String cipherValue) {
		Cipher cipher = null;
		byte[] devalue = null;
		String descryptedString = "";
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKey key = generateKey1(salt, encKey);
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(hex(iv)));
			devalue = cipher.doFinal(DatatypeConverter.parseBase64Binary(cipherValue));
			descryptedString = new String(devalue);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return descryptedString;
	}
	
	public String AESEncrypt(String textToEncrypt, String key) {
		
		String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
		SecretKeySpec keySpec;
		byte[] keyBytes = GetKeyAsBytes(key);

		for (int i = 0; i < keyBytes.length; i++) {
			if (keyBytes[i] == -58) {
				keyBytes[i] = -120;
			}
		}

		
		keySpec = new SecretKeySpec(keyBytes, "AES");
		byte[] unencrypted = null;
		try {
			unencrypted = textToEncrypt.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception in Axis NB Encryption",e);
		}

		
		
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception in Axis NB Encryption",e);
		} catch (NoSuchPaddingException e) {
			logger.error("Exception in Axis NB Encryption",e);
		}
		IvParameterSpec iv = new IvParameterSpec(keyBytes);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
		} catch (InvalidKeyException e) {
			logger.error("Exception in Axis NB Encryption",e);
		} catch (InvalidAlgorithmParameterException e) {
			logger.error("Exception in Axis NB Encryption",e);
		}
		
		try {
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			try {
				keySpec = new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), "AES");
			} catch (InvalidKeySpecException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				logger.error("Exception in Axis NB Encryption",e2);
			}
		   
		    try {
				cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
			} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Exception in Axis NB Encryption",e);
			}
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String encryptedPlainText = null;
		try {
			encryptedPlainText = DatatypeConverter.printBase64Binary(cipher.doFinal(unencrypted));
		} catch (IllegalBlockSizeException e) {
			logger.error("Exception in Axis NB Encryption",e);
		} catch (BadPaddingException e) {
			logger.error("Exception in Axis NB Encryption",e);
		}
		return encryptedPlainText;
	}

	public String AESDecrypt(String textToDecrypt, String key) throws Exception, BadPaddingException {

		textToDecrypt = textToDecrypt.replaceAll(" ", "+");
		String decryptedString = "";

		String DECRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
		SecretKeySpec keySpec;
		byte[] keyBytes = GetKeyAsBytes(key);

		keySpec = new SecretKeySpec(keyBytes, "AES");

		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(DECRYPTION_ALGORITHM);

		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception in Axis NB Decryption ",e);
		} catch (NoSuchPaddingException e) {
			logger.error("Exception in Axis NB Decryption ",e);
		}
		IvParameterSpec iv = new IvParameterSpec(keyBytes);
		try {
			cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
		} catch (InvalidKeyException e) {
			logger.error("Exception in Axis NB Decryption ",e);
		} catch (InvalidAlgorithmParameterException e) {
			logger.error("Exception in Axis NB Decryption ",e);
		}
		
		decryptedString = new String(cipher.doFinal(DatatypeConverter.parseBase64Binary(textToDecrypt)));
		return decryptedString;
	}

	public static byte[] GetKeyAsBytes(String key) {
		byte[] keyBytes = new byte[0x10];

		for (int i = 0; i < key.length() && i < keyBytes.length; i++) {
			keyBytes[i] = (byte) key.charAt(i);
		}
		return keyBytes;
	}
	
	// SHA256 starts here
	public String getSha256(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(value.getBytes());
			return bytesToHex(md.digest());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte b : bytes)
			result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}
	
	public static String decrypt(String encryptedData, String keyStr) throws Exception {
		Key key = generateKey(keyStr);
		Cipher c = Cipher.getInstance("AES");
		c.init(2, key);
		byte[] decordedValue = java.util.Base64.getDecoder().decode(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);
		return new String(decValue);
	}
	
	private static Key generateKey(String key) throws Exception {
		byte[] keyValue = key.getBytes();
		return new SecretKeySpec(keyValue, "AES");
	}
	
	public static String encryptStatusEnquiryReq(String inputStr, String sKey) {

		byte[] key = null;
		Cipher cipher = null;
		String encryptedValue = "";
		try {
			key = setKey(sKey);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

		IvParameterSpec ivParameterSpec = new IvParameterSpec(key);

		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			try {
				encryptedValue = DatatypeConverter.printBase64Binary(cipher.doFinal(inputStr.getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			logger.error("Exception In IncryptStatusEnquiryReq : "+e.getMessage());
		}

		return encryptedValue;

	}
	
	public static String decryptStatusEnquiryResp(String textToDecrypt1, String sKey) {

		String decryptedString = "";
		byte[] key = null;

		try {
			key = setKey(sKey);
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			// IvParameterSpec ivParameterSpec = new IvParameterSpec(key);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(key);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
			decryptedString = new String(cipher.doFinal(DatatypeConverter.parseBase64Binary(textToDecrypt1)));
			
			//logger.info("decryptedString>>>>>>>>>>>" + decryptedString);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception In DecryptStatusEnquiryResp : "+e.getMessage());
		}

		return decryptedString;

	}
	
	public static byte[] setKey(String myKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {

		byte[] key;
		MessageDigest sha = null;
		key = myKey.getBytes("UTF-8");
		// System.out.println(key.length);
		sha = MessageDigest.getInstance("SHA-256");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16); // use only first 128 bit

		return key;

	}

}
