package com.pay10.pg.core.camspay.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Formatter;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public class CamsPayHasher {
	public static String encryptMessage(String message, String passKey, String iv) throws Exception {
		return encryptDecryptCAMSPayload("ENCRYPT", message, passKey, iv);
	}

	public static String decryptMessage(String cryptString, String passKey, String iv) throws Exception {
		return encryptDecryptCAMSPayload("DECRYPT", cryptString, passKey, iv);
	}

	private static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static String ALGORITHM = "AES";
	private static String DIGEST = "SHA-256";

	private static Cipher cipher;
	private static SecretKey password;
	private static IvParameterSpec IVParamSpec;

	@SuppressWarnings("resource")
	public static String encryptDecryptCAMSPayload(String Mode, String cryptText, String passKey, String iv)
			throws Exception {
		byte[] decryptedVal = null;
		String retText = "";
		try {

			// Encode digest
			MessageDigest digest = MessageDigest.getInstance(DIGEST);
			byte[] bytehash = digest.digest(passKey.getBytes(StandardCharsets.UTF_8));
			String strHash = "";
			Formatter formatter = new Formatter();

			for (byte myByte : bytehash) {
				strHash = "";
				strHash = String.format("%02x", myByte);
				formatter.format(StringUtils.leftPad(strHash, 2, "0"));
			}
			strHash = formatter.toString();

			if (strHash.length() > 32) {
				strHash = strHash.substring(0, 32);
			}

			byte[] bytekeytemp = strHash.getBytes(StandardCharsets.UTF_8);

			byte[] bytekey = new byte[32];
			System.arraycopy(bytekeytemp, 0, bytekey, 0, 32);

			password = new SecretKeySpec(bytekey, ALGORITHM);

			// Initialize objects
			cipher = Cipher.getInstance(TRANSFORMATION);
			IVParamSpec = new IvParameterSpec(iv.getBytes());

			if (Mode.equals("ENCRYPT")) {
				cipher.init(Cipher.ENCRYPT_MODE, password, IVParamSpec);
				byte[] ciphertext = cipher.doFinal(cryptText.getBytes(StandardCharsets.UTF_8));

				byte[] encodedValue = Base64.getEncoder().encode(ciphertext);
				String encryptedVal = new String(encodedValue);
				encryptedVal = encryptedVal.replace("+", "-");
				encryptedVal = encryptedVal.replace("/", "_");
				retText = encryptedVal;
			} else {
				cipher.init(Cipher.DECRYPT_MODE, password, IVParamSpec);

				cryptText = cryptText.replace("-", "+");
				cryptText = cryptText.replace("_", "/");

				byte[] decodedValue = Base64.getDecoder().decode(cryptText.getBytes(StandardCharsets.UTF_8));
				decryptedVal = cipher.doFinal(decodedValue);
				retText = new String(decryptedVal);
			}

		} catch (NoSuchAlgorithmException e) {
			throw new Exception("CAMS Encryption error, No such algorithm ->" + ALGORITHM);
		} catch (NoSuchPaddingException e) {
			throw new Exception("CAMS Encryption error, No such padding PKCS7");
		} catch (Exception e) {
			throw new Exception("CAMS Encryption error:" + exceptionToString(e));
		}
		return retText;
	}

	private static String exceptionToString(Exception ex) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		return writer.toString();
	}

	public static String generateCheckSum(String request, String encKey) {
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec skeySpec = new SecretKeySpec(encKey.getBytes("UTF-8"), "HmacSHA256");
			sha256_HMAC.init(skeySpec);
			return Hex.encodeHexString(sha256_HMAC.doFinal(request.getBytes("UTF-8")));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
