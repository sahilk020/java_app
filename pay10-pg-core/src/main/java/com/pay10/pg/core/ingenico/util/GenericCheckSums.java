package com.pay10.pg.core.ingenico.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.util.BASE64Decorder;
import com.pay10.commons.util.BASE64Encoder;

public final class GenericCheckSums

{

	private static Logger logger = LoggerFactory.getLogger(GenericCheckSums.class.getName());

	public static String ToHexStr(final byte[] dataBuf) {
		String hxstr = null;
		final Hex hx = new Hex();
		hxstr = new String(Hex.encodeHex(dataBuf));
		return hxstr;
	}

	public static byte[] FromHexStr(final String hxStr) {
		final char[] charArray = hxStr.toCharArray();
		byte[] bufstr = null;
		final Hex hxx = new Hex();
		try {
			bufstr = Hex.decodeHex(charArray);
		} catch (Exception e) {
			logger.error("Caught exception ", e);
		}
		return bufstr;
	}

	public static String ToBase64Str(final byte[] buff) {
		String rslt = null;
		final BASE64Encoder encoder = new BASE64Encoder();
		rslt = encoder.encode(buff);
		return rslt;
	}

	public static byte[] FromBase64Str(final String base64Str) {
		byte[] rslt = null;
		final BASE64Decorder decoder = new BASE64Decorder();
		try {
			rslt = decoder.decodeBuffer(base64Str);
		} catch (Exception e) {
			logger.error("Caught exception ", e);
		}
		return rslt;
	}

	public static byte[] getMD5DigestString(final String Parameterstring) {
		byte[] ParameterBuffer = null;
		try {
			ParameterBuffer = Parameterstring.getBytes("UTF-8");
		} catch (Exception e) {
			logger.error("Unsupported Encoding Exception ", e);
		}
		final Digest digest = (Digest) new MD5Digest();
		digest.update(ParameterBuffer, 0, ParameterBuffer.length);
		final byte[] digestValue = new byte[digest.getDigestSize()];
		digest.doFinal(digestValue, 0);
		return digestValue;
	}

	public static byte[] getSHA256DigestString(final String Parameterstring) {
		byte[] ParameterBuffer = null;
		try {
			ParameterBuffer = Parameterstring.getBytes("UTF-8");
		} catch (Exception e) {
			logger.error("Unsupported Encoding Exception ", e);
		}
		final Digest digest = (Digest) new SHA256Digest();
		digest.update(ParameterBuffer, 0, ParameterBuffer.length);
		final byte[] digestValue = new byte[digest.getDigestSize()];
		digest.doFinal(digestValue, 0);
		return digestValue;
	}

	public static byte[] getSHA1DigestString(final String Parameterstring) {
		byte[] ParameterBuffer = null;
		try {
			ParameterBuffer = Parameterstring.getBytes("UTF-8");
		} catch (Exception e) {
			logger.error("Unsupported Encoding Exception ", e);
		}
		final Digest digest = (Digest) new SHA1Digest();
		digest.update(ParameterBuffer, 0, ParameterBuffer.length);
		final byte[] digestValue = new byte[digest.getDigestSize()];
		digest.doFinal(digestValue, 0);
		return digestValue;
	}

	public static String encryptHx(final String text, final byte[] iv, final byte[] key) throws Exception {
		final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		final IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(1, keySpec, ivSpec);
		final byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
		final String hxencstr = ToHexStr(results);
		return hxencstr;
	}

	public static String decryptHx(final String hxstrr, final byte[] iv, final byte[] key) throws Exception {
		final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		final IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(2, keySpec, ivSpec);
		final byte[] decbytes = FromHexStr(hxstrr);
		final byte[] results = cipher.doFinal(decbytes);
		return new String(results, "UTF-8");
	}

	public static String decryptHxT(final String hxstrr, final byte[] iv, final byte[] key) throws Exception {
		final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		final IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(2, keySpec, ivSpec);
		final byte[] decbytes = FromHexStr(hxstrr);
		final byte[] results = cipher.doFinal(decbytes);
		return new String(results, "UTF-8").trim();
	}

	public static byte[] computeHMacSha1(final byte[] databuf, final String keyBytes) {
		final Digest dgst = (Digest) new SHA1Digest();
		final HMac hmc = new HMac(dgst);
		final int macsize = hmc.getMacSize();
		final byte[] outMac = new byte[macsize];
		byte[] SharedSecretkey = null;
		try {
			SharedSecretkey = keyBytes.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
		}
		final KeyParameter skpara = new KeyParameter(SharedSecretkey);
		hmc.init((CipherParameters) skpara);
		hmc.update(databuf, 0, databuf.length);
		hmc.doFinal(outMac, 0);
		return outMac;
	}

	public static boolean verifyHMacSha1(final byte[] databuf, final String keyBytes, final byte[] MacToCompare) {
		boolean result = false;
		final Digest dgst = (Digest) new SHA1Digest();
		final HMac hmc = new HMac(dgst);
		final int macsize = hmc.getMacSize();
		byte[] oMac = new byte[macsize];
		oMac = computeHMacSha1(databuf, keyBytes);
		result = Arrays.equals(oMac, MacToCompare);
		return result;
	}

	public static byte[] computeHMacSha256(final byte[] databuf, final String keyBytes) {
		final Digest dgst = (Digest) new SHA256Digest();
		final HMac hmc = new HMac(dgst);
		final int macsize = hmc.getMacSize();
		final byte[] outMac = new byte[macsize];
		byte[] SharedSecretkey = null;
		try {
			SharedSecretkey = keyBytes.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
		}
		final KeyParameter skpara = new KeyParameter(SharedSecretkey);
		hmc.init((CipherParameters) skpara);
		hmc.update(databuf, 0, databuf.length);
		hmc.doFinal(outMac, 0);
		return outMac;
	}

	public static boolean verifyHMacSha256(final byte[] databuf, final String keyBytes, final byte[] MacToCompare) {
		boolean result = false;
		final Digest dgst = (Digest) new SHA256Digest();
		final HMac hmc = new HMac(dgst);
		final int macsize = hmc.getMacSize();
		byte[] oMac = new byte[macsize];
		oMac = computeHMacSha256(databuf, keyBytes);
		result = Arrays.equals(oMac, MacToCompare);
		return result;
	}
}
