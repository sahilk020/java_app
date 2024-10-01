package com.pay10.pg.core.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.xerces.impl.dv.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class AtomSecureCardData {

	public String encryptData(String sData, String key) throws Exception {
		byte[] bPrivateKey = key.getBytes();
		SecretKeySpec spec = new SecretKeySpec(bPrivateKey, "DES");
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(1, spec);
		byte[] bEncryptedData = cipher.doFinal(sData.getBytes());
		return Base64.encode(bEncryptedData);
	}

	public String decryptData(String sData, String key) throws Exception {
		byte[] bPrivateKey = key.getBytes();
		SecretKeySpec spec = new SecretKeySpec(bPrivateKey, "DES");
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(2, spec);
		byte[] bencryptedData = Base64.decode(sData);
		byte[] bDecryptedData = cipher.doFinal(bencryptedData);
		return new String(bDecryptedData);
	}

}