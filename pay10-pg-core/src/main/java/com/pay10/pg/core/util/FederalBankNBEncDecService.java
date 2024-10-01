/**
 * 
 */
package com.pay10.pg.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.regex.Matcher;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class FederalBankNBEncDecService {
	 
    private static SecretKeySpec secretKey;
    private static byte[] key;

    private static Logger logger = LoggerFactory.getLogger(FederalBankNBEncDecService.class.getName());
 
    public static void setKey(String myKey) 
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            secretKey = new SecretKeySpec(key, "AES");
        } 
         
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
 
    public static String encrypt(String strToEncrypt, String secret) 
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } 
        catch (Exception e) 
        {
            String message = "Federal bank Netbanking - Error occurred while encrypting the data.";
            logger.error(message, e);
        }
        return null;
    }
 
    public static String decrypt(String strToDecrypt, String secret) 
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
            String message = "Federal bank Netbanking - Error occurred while decrypting the data.";
            logger.error(message, e);
        }

        return null;
    }
}



