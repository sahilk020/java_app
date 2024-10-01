package com.pay10.pg.core.util;

import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.util.BASE64Decorder;
import com.pay10.commons.util.BASE64Encoder;

public class IciciUtil {

	private static Cipher c;
	private static Key encryptDecryptKey;

	private static Logger logger = LoggerFactory.getLogger(IciciUtil.class.getName());

	public IciciUtil(final String sMasterKey) {

		try {
			String sKey = "";
			final String sAlgorithm = "AES";
			sKey = sMasterKey.trim();
			if (sKey.length() != 16) {
				throw new InvalidKeyException("Master Key should be of 16 Characters");
			}
			final byte[] sKeyBytes = sKey.getBytes();
			IciciUtil.encryptDecryptKey = new SecretKeySpec(sKeyBytes, sAlgorithm);
			IciciUtil.c = Cipher.getInstance(sAlgorithm);
		} catch (Exception e) {
			logger.error("Exceptiom in encrypting ", e);

		}

	}

	public String encrypt(final String stringToEncrypt) {

		try {

			IciciUtil.c.init(1, IciciUtil.encryptDecryptKey);
			final BASE64Encoder base64encoder = new BASE64Encoder();
			String sEncString = base64encoder.encode(IciciUtil.c.doFinal(stringToEncrypt.getBytes()));
			sEncString = sEncString.replaceAll("\n", "");
			sEncString = sEncString.replaceAll("\r", "");
			return sEncString;

		}

		catch (Exception e) {
			logger.error("Exceptiom in encrypting ", e);
			return null;

		}

	}

	public String decrypt(final String stringToDecrypt) {

		try {
			IciciUtil.c.init(2, IciciUtil.encryptDecryptKey);
			final BASE64Decorder base64decoder = new BASE64Decorder();
			byte[] ciphertext;
			final byte[] cleartext = base64decoder.decodeBuffer(this.replaceSpecialChars(stringToDecrypt));
			ciphertext = IciciUtil.c.doFinal(cleartext);
			return this.bytes2String(ciphertext);
		}

		catch (Exception e) {
			logger.error("Exceptiom in encrypting ", e);
			return null;

		}

	}

	private String bytes2String(final byte[] bytes) {

		try {
			final StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < bytes.length; ++i) {
				stringBuilder.append((char) bytes[i]);
			}
			return stringBuilder.toString();
		}

		catch (Exception e) {
			logger.error("Exceptiom in encrypting ", e);
			return null;

		}

	}

	private String replaceSpecialChars(final String str) {

		try {
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < str.length(); ++i) {
				if (str.charAt(i) == ' ') {
					sb.append('+');
				} else {
					sb.append(str.charAt(i));
				}
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("Exceptiom in encrypting ", e);
			return null;

		}

	}

}
