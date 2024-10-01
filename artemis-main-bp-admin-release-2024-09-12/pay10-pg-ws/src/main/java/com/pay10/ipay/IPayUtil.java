package com.pay10.ipay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.SystemConstants;

@Component
public class IPayUtil {
	
	public static final String ALGO = "AES";

	public static String encrypt(String data, String key, String iv) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGO);
			IvParameterSpec ivKey = new IvParameterSpec(iv.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8));
			Cipher cipher = Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivKey);

			byte[] encValue = cipher.doFinal(data.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8));

			Base64.Encoder base64Encoder = Base64.getEncoder().withoutPadding();
			String base64EncodedData = base64Encoder.encodeToString(encValue);
			System.out.println("encdata = " + base64EncodedData );
			base64EncodedData = URLEncoder.encode(base64EncodedData, SystemConstants.DEFAULT_ENCODING_UTF_8);
			return base64EncodedData;
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {

			noSuchAlgorithmException.printStackTrace();
		} catch (NoSuchPaddingException noSuchPaddingException) {

			noSuchPaddingException.printStackTrace();
		} catch (InvalidKeyException invalidKeyException) {

			invalidKeyException.printStackTrace();
		} catch (IllegalBlockSizeException illegalBlockSizeException) {

			illegalBlockSizeException.printStackTrace();
		} catch (BadPaddingException badPaddingException) {

			badPaddingException.printStackTrace();
		} catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {

			invalidAlgorithmParameterException.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	} 

	public static String decrypt(String data, String key, String iv) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGO);
			IvParameterSpec ivKey = new IvParameterSpec(iv.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8));
			Cipher cipher = Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivKey);
			byte[] decodedData = Base64.getDecoder().decode(data);
			byte[] decValue = cipher.doFinal(decodedData);

			return new String(decValue);

		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {

			noSuchAlgorithmException.printStackTrace();
		} catch (NoSuchPaddingException noSuchPaddingException) {

			noSuchPaddingException.printStackTrace();
		} catch (InvalidKeyException invalidKeyException) {

			invalidKeyException.printStackTrace();
		} catch (IOException ioException) {

			ioException.printStackTrace();
		} catch (IllegalBlockSizeException illegalBlockSizeException) {

			illegalBlockSizeException.printStackTrace();
		} catch (BadPaddingException badPaddingException) {

			badPaddingException.printStackTrace();
		} catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {

			invalidAlgorithmParameterException.printStackTrace();
		}

		return null;
	}
	
	public static void main(String[] args) {
		String value= "merchantCode=362379|reservationId=1050180108163751|txnAmount=10.00|currencyType=INR|appCode=ET|pymtMode=Internet|txnDate=20180108|securityId=CRIS|RU=http://localhost:8080/pgui/jsp/iPayResponse";
		//String[] splitted = value.split("\\|");
		try {
			String checksum = Hasher.getHash(value);
			System.out.println(checksum);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data = "merchantCode=361179|reservationId=1050180108163751|txnAmount=10.00|currencyType=INR|appCode=ET|pymtMode=Internet|txnDate=20180108|securityId=CRIS|RU=http://localhost:8080/pgui/jsp/iPayResponse|checkSum=3A2A6AEFCC25952E31818B62817AF805EB9CE289698911EE247F7ABB172CE5F8";
		String key= "ACME-1234ACME-12ACME-1234ACME-12";
		String iv="4e5Wa71fYoT7MFEX";
		String encrypt = encrypt(data, key, iv);
		System.out.println("Encrypted String= " + encrypt);
		try {
			String hexa = Hex.encodeHexString(encrypt.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8));
			System.out.println("Hex= " + hexa);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String dvalue = "lgPA6uRWJlLAlHOdJ5orxoxBwlyLteZ9v22W7IuN2bQ/ZPT5P6QCpoVaf4KAAyfyoi+XgtfaVnG3unXuADOGb3uRNPM/pwLbI8mNGH4DuBkmQwxa8d+2cPpRtrdbP2uNIHzJBmglNrpONtoEsxg6Ln/sx7bn9Gq+c7iZikI/g/MRYyKM5zioXRVfsZQ4GVgGVaqgrgUlnZbZFuDAYurJtOG7kR+QDK0jpQAwx3Gg8lA=lgPA6uRWJlLAlHOdJ5orxoxBwlyLteZ9v22W7IuN2bQ/ZPT5P6QCpoVaf4KAAyfyoi+XgtfaVnG3unXuADOGb3uRNPM/pwLbI8mNGH4DuBkmQwxa8d+2cPpRtrdbP2uNIHzJBmglNrpONtoEsxg6Ln/sx7bn9Gq+c7iZikI/g/MRYyKM5zioXRVfsZQ4GVgGVaqgrgUlnZbZFuDAYurJtOG7kR+QDK0jpQAwx3Gg8lA=";
		String decode = null;
		try {
			//decode = URLDecoder.decode(dvalue, SystemConstants.DEFAULT_ENCODING_UTF_8);
			decode = URLEncoder.encode(dvalue, SystemConstants.DEFAULT_ENCODING_UTF_8);
			
			System.out.println("encoded String= " + decode);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String decrypt = decrypt(decode, key, iv);
		System.out.println("decrypted String= " + decrypt);
	}

}
