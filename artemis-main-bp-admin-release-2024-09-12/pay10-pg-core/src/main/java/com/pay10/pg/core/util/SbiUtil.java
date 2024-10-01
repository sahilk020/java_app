package com.pay10.pg.core.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.PropertiesManager;

@Service
public class SbiUtil {

	private static Logger logger = LoggerFactory.getLogger(SbiUtil.class.getName());

	@Autowired
	private PropertiesManager propertiesManager;

	public String encrypt(String data) {

		String path = System.getenv("BPGATE_PROPS") + "" + propertiesManager.propertiesMap.get("SBIEncKeyFileName");
		byte[] key = null;
		try {
			key = returnbyte(path);
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		String encData = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			int blockSize = cipher.getBlockSize();
			byte[] iv = new byte[cipher.getBlockSize()];
			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			int remainder = plaintextLength % blockSize;
			if (remainder != 0) {
				plaintextLength += blockSize - remainder;
			}
			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
			randomSecureRandom.nextBytes(iv);
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
			cipher.init(1, keySpec, parameterSpec);
			byte[] results = cipher.doFinal(plaintext);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(iv);
			outputStream.write(results);
			byte[] encrypteddata = outputStream.toByteArray();
			encData = Base64.encodeBase64String(encrypteddata);
			encData = encData.replace("\n", "").replace("\r", "");
		} catch (Exception exception) {
			logger.error("Exception occured", exception);
		}
		return encData;
	}

	public String decrypt(String encData) {
		String decdata = null;
		String path = System.getenv("BPGATE_PROPS") + "" + propertiesManager.propertiesMap.get("SBIEncKeyFileName");
		byte[] key = null;
		key = returnbyte(path);
		try {
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			byte[] results = Base64.decodeBase64(encData);
			byte[] iv = Arrays.copyOfRange(results, 0, cipher.getBlockSize());
			cipher.init(2, keySpec, new GCMParameterSpec(128, iv));
			byte[] results1 = Arrays.copyOfRange(results, cipher.getBlockSize(), results.length);
			byte[] ciphertext = cipher.doFinal(results1);
			decdata = new String(ciphertext).trim();
		} catch (Exception exception) {
			logger.error("Exception occured", exception);
		}
		return decdata;
	}

	public byte[] returnbyte(String path) {
		FileInputStream fileinputstream;
		byte[] abyte = null;
		try {
			fileinputstream = new FileInputStream(path);
			abyte = new byte[fileinputstream.available()];
			fileinputstream.read(abyte);
			fileinputstream.close();
		} catch (FileNotFoundException e1) {
			logger.error("Exception occured", e1);
		} catch (IOException e) {
			logger.error("Exception occured", e);
		}
		return abyte;
	}
	
	
}
