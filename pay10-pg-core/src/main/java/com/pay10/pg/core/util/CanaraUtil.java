package com.pay10.pg.core.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

import static com.pay10.pg.core.util.HexUtil.asHex;

public class CanaraUtil {
    public static String encryptString(
            String textToEncrypt
            , String key
            , String ivkey) {
        byte[] l_encrypted = null;
        Cipher l_encrcipher = null;
        String l_encryptedString = null;
        byte[] keyBytes = new byte[16];
        byte[] pwdBytes = new byte[32];
        try {
            pwdBytes = key.getBytes("UTF-8");
            keyBytes = ivkey.getBytes("UTF-8");
            l_encrcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skeySpec = new SecretKeySpec(pwdBytes, "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(keyBytes);
            l_encrcipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParamSpec);
            l_encrypted = l_encrcipher.doFinal(textToEncrypt.getBytes("UTF-8"));
            l_encryptedString = new String(Hex.encodeHex(l_encrypted));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l_encryptedString.toUpperCase();
    }

    public static String decryptString(
            String textToDecrypt
            , String key
            , String ivkey
    ) {
        byte[] l_decodeBytes = null,
                l_decryptedBytes = null;
        Cipher l_decrcipher = null;
        String l_decryptedText = null;
        byte[] keyBytes = new byte[16];
        byte[] pwdBytes = new byte[32];
        try {
            pwdBytes = key.getBytes("UTF-8");

            keyBytes = ivkey.getBytes("UTF-8");
            l_decrcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skeySpec = new SecretKeySpec(pwdBytes, "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(keyBytes);
            textToDecrypt = textToDecrypt.toLowerCase();
            l_decrcipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParamSpec);
            l_decodeBytes = Hex.decodeHex(textToDecrypt.toCharArray());
            l_decryptedBytes = l_decrcipher.doFinal(l_decodeBytes);
            l_decryptedText = new String(l_decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l_decryptedText;
    }


    public static String ChecksumCal(String p_param_string) {
        byte[] l_SHAHashBytes = null;
        MessageDigest l_messageDigest = null;
        try {
            l_messageDigest = MessageDigest.getInstance("SHA-256");
            l_messageDigest.update(p_param_string.getBytes("UTF-8"), 0,
                    p_param_string.length());
            l_SHAHashBytes = l_messageDigest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return asHex(l_SHAHashBytes).toUpperCase();
    }
}
