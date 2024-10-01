package com.pay10.pg.core.ingenico.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import com.pay10.commons.util.BASE64Decorder;
import com.pay10.commons.util.BASE64Encoder;


public final class AESFinalNew

{
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
        System.out.println("Decrypted buff len = " + decbytes.length);
        final byte[] results = cipher.doFinal(decbytes);
        return new String(results, "UTF-8");
    }
    
    public static String encrypt(final String text, final byte[] iv, final byte[] key) throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        final IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(1, keySpec, ivSpec);
        final byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
        final BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(results);
    }
    
    public static String decrypt(final String text, final byte[] iv, final byte[] key) throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        final IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(2, keySpec, ivSpec);
        final BASE64Decorder decoder = new BASE64Decorder();
        final byte[] results = cipher.doFinal(decoder.decodeBuffer(text));
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bufstr;
    }
    
    public static String url_decoder(final String str, final String encoding) {
        String rslt = null;
        try {
            rslt = URLDecoder.decode(str, encoding);
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return rslt;
    }
    
    public static String url_encoder(final String str, final String encoding) {
        String rslt = null;
        try {
            rslt = URLEncoder.encode(str, encoding);
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return rslt;
    }
}
