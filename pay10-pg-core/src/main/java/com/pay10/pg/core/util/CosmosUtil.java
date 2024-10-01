package com.pay10.pg.core.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class CosmosUtil {
    // static Key secretKey = null;
    public static String encrypt(String strToEncrypt, String encryptKey) {
        try {
            Key secretKey = setKey(encryptKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
        }
        return null;
    }
    public static String decryptCallBackResponse(String encrypted,String secretKey) {
        try {
            byte[] decodedCiphertext = Base64.getDecoder().decode(encrypted);
            IvParameterSpec ivspec = new IvParameterSpec(decodedCiphertext, 0, 16);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivspec);
            byte[] original = cipher.doFinal(decodedCiphertext, 16, decodedCiphertext.length - 16);

            return new String(original, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    public static Key setKey(String myKey) {
        MessageDigest sha = null;

        try {
            byte[] key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public static String decrypt(String responseString, String encryptKey) {
        try {

            Key secretKey = setKey(encryptKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(responseString)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateChecksum(String concatenatedString, String checksumkey) throws IOException {
        String inputString = concatenatedString + checksumkey;
        StringBuffer sb = null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(inputString.getBytes());
            byte byteData[] = md.digest();
            sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
        }
        return sb.toString();
    }


}
