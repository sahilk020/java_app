package com.pay10.commons.api;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Formatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EmitraHasher {
	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

	public static String getHash(String toBeEncryptString, String key) {
		if (toBeEncryptString == null) {
			throw new IllegalArgumentException("To be encrypt string must not be null");
		}
		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(UTF_8),
					HMAC_SHA256_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(secretKeySpec);
			return byteArray2Hex(mac.doFinal(toBeEncryptString.getBytes(UTF_8))).toUpperCase();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return toBeEncryptString;
	}

	public static String byteArray2Hex(byte[] bytes) {
		try (Formatter formatter = new Formatter()) {
			byte[] arrayOfByte = bytes;
			int j = bytes.length;
			for (int i = 0; i < j; i++) {
				byte b = arrayOfByte[i];

				formatter.format("%02x", Byte.valueOf(b));
			}
			return formatter.toString();
		}
	}
}