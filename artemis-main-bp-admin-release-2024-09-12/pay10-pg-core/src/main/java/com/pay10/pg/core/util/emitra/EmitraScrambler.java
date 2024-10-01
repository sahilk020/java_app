package com.pay10.pg.core.util.emitra;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.SystemConstants;

import bsh.This;

@Component
public class EmitraScrambler {
	private static Logger logger = LoggerFactory.getLogger(This.class.getName());

	@Autowired
	private PropertiesManager propertiesManager;

	public static final String ALGO = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

	public String getKey(String payId) throws SystemException {
		String salt = propertiesManager.getKeySalt(payId);
		logger.info("Creating new key for PAY_ID: " + payId);
		if (salt == null) {
			throw new SystemException(ErrorType.USER_NOT_FOUND, "No such user");
		}
		String generatedKey = (Hasher.getHash(salt + payId)).substring(0, 32);
		return generatedKey;
	}

	public String encrypt(String payId, String data) throws SystemException {
		try {
			logger.info("encrypt:: payId={}", payId);
			String key = getKey(payId);
			logger.info("encrypt:: key={}", key);
			SecretKeySpec keyObj = new SecretKeySpec(
					DigestUtils.sha256(key.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8)), ALGO);
			;
			IvParameterSpec iv = new IvParameterSpec(
					DigestUtils.md5(key.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8)));
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, keyObj, iv);

			byte[] encValue = cipher.doFinal(data.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8));

			String base64EncodedData = Base64.encodeBase64String(encValue);
			return base64EncodedData;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException
				| IllegalBlockSizeException | BadPaddingException
				| InvalidAlgorithmParameterException scramblerExceptionException) {
			throw new SystemException(ErrorType.CRYPTO_ERROR, scramblerExceptionException,
					"Error during encryption process");
		}
	}

	public String decrypt(String payId, String data) throws SystemException {
		try {
			String key = getKey(payId);
			SecretKeySpec keyObj = new SecretKeySpec(
					DigestUtils.sha256(key.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8)), ALGO);
			;
			IvParameterSpec iv = new IvParameterSpec(
					DigestUtils.md5(key.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8)));
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, keyObj, iv);

			byte[] decodedData = Base64.decodeBase64(data);
			byte[] decValue = cipher.doFinal(decodedData);
			return new String(decValue);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException
				| IllegalBlockSizeException | BadPaddingException
				| InvalidAlgorithmParameterException scramblerExceptionException) {
			throw new SystemException(ErrorType.CRYPTO_ERROR, scramblerExceptionException,
					"Error during decryption process");

		}
	}
}
