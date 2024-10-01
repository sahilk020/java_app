package com.pay10.pg.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class YesBankNBEncDecService {

    private static Logger logger = LoggerFactory.getLogger(YesBankNBEncDecService.class.getName());

    private static SecretKeySpec keyEncDec = null;
    private static IvParameterSpec IVParameterSpec = null;

    public String encrypt(String p_plain_text, String p_key) throws Exception {
        System.out.println(p_plain_text + "->>-" + p_key);
        try {
            generateKey(p_key);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyEncDec, IVParameterSpec);

            String l_encVal = new String(Base64.encodeBase64(cipher.doFinal(p_plain_text.getBytes("UTF-8")), false));
            return l_encVal;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Yesbank NB While making encryption --" + e);
        }

        return null;
    }

    private static void generateKey(String p_key) throws Exception {
        MessageDigest sha = null;
        try {
            byte[] key = p_key.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);

            byte[] temp_key = new byte[16];
            System.arraycopy(key, 0, temp_key, 0, 16);
            key = temp_key;
            keyEncDec = new SecretKeySpec(key, "AES");
            IVParameterSpec = new IvParameterSpec(key);
        } catch (NoSuchAlgorithmException e) {
            logger.info("Yesbank NB While generating key for encryption --" + e);
        } catch (UnsupportedEncodingException e) {
            logger.info("Yesbank NB While generating key for encryption --" + e);
        }
    }

    public static byte[] hex(String str) {
        return DatatypeConverter.parseHexBinary(str);
    }

    public String decrypt(String p_encrypted_text, String p_key) {
        String descryptedString = "";
        Cipher cipher = null;
        try {
            generateKey(p_key);
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keyEncDec, IVParameterSpec);
            byte[] l_decodedvalue = Base64.decodeBase64(p_encrypted_text.getBytes("UTF-8"));
            byte[] decValue = cipher.doFinal(l_decodedvalue);

            descryptedString = new String(decValue);
        } catch (Exception e) {
            logger.info("Yesbank NB While making decryption --" + e);
        }

        return descryptedString;
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
}
