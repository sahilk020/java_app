package com.pay10.migs;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.SystemConstants;

@Service
public class MigsUtil {

	private static final String HMAC_SHA2_ALGORITHM = "HmacSHA256";
	private static final String ENCRYPTION_ALG0 = "AES";
	
	public static String calculateMac(String inputString,String key) throws SystemException {
		String result = "";
		try {
			byte[] keyBytes = Hex.decodeHex(key.toCharArray());

			SecretKeySpec signingKey = new SecretKeySpec(keyBytes,HMAC_SHA2_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA2_ALGORITHM);
			mac.init(signingKey);

			byte[] rawHmac = mac.doFinal(inputString.getBytes(Charset
					.forName(SystemConstants.DEFAULT_ENCODING_UTF_8)));
			byte[] hexBytes = new Hex().encode(rawHmac);

			result = new String(hexBytes, SystemConstants.DEFAULT_ENCODING_UTF_8);

		} catch (DecoderException decoderException) {
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					decoderException, "No such decoder with Amex");
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					noSuchAlgorithmException, "No such Hashing algoritham with Amex");
		} catch (InvalidKeyException invalidKeyException) {
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					invalidKeyException, "No such key with amex");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					unsupportedEncodingException, "No such encoder with Amex");
		}
		return result.toUpperCase();
	}

	public static String decrypt(String value, String encryptionKey) throws SystemException{

		try {
			SecretKeySpec secretKey = new SecretKeySpec(HexUtil.HexfromString(encryptionKey), ENCRYPTION_ALG0);
			Cipher cipher =  Cipher.getInstance(ENCRYPTION_ALG0);
			cipher.init(2, secretKey);

			byte[] decryptedBytes = cipher.doFinal(HexUtil.HexfromString(value));
			return new String(decryptedBytes);

		} catch (Exception exception) {
			throw new SystemException(ErrorType.CRYPTO_ERROR,ErrorType.CRYPTO_ERROR.getResponseMessage());
		}
		finally{
			return "nodatafound";
		}
	}

	public static String encrypt(String value, String encryptionKey) throws SystemException{

		try {
			SecretKeySpec secretKey = new SecretKeySpec(HexUtil.HexfromString(encryptionKey), ENCRYPTION_ALG0);
			Cipher cipher =  Cipher.getInstance(ENCRYPTION_ALG0);
			cipher.init(1, secretKey);

			byte encstr[] = cipher.doFinal(value.getBytes());
			return HexUtil.HextoString(encstr);

		} catch (Exception exception) {
			throw new SystemException(ErrorType.CRYPTO_ERROR,ErrorType.CRYPTO_ERROR.getResponseMessage());
		}
	}

	public static String parseDate(String dateFE){
		StringBuilder date = new StringBuilder();
		date.append(dateFE.charAt(4));
		date.append(dateFE.charAt(5));
		date.append(dateFE.charAt(0));
		date.append(dateFE.charAt(1));
		return date.toString();
	}
			
}