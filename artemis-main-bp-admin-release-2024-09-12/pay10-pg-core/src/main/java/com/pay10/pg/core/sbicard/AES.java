package com.pay10.pg.core.sbicard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AES {
    private static final Logger logger = Logger.getLogger(AES.class.getName());

    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 12;

    private SecretKey key;
    byte[] IV;

    public static AES init() throws NoSuchAlgorithmException {
        logger.info("Initialize AES");
        AES aes = AES.builder().build();
        try{
            aes.key = generateKey();
            aes.IV = generateRandomValue();
            return aes;
        } catch (NoSuchAlgorithmException e) {
            logger.info("Unable to generate symmetric key: " +e.getLocalizedMessage());
            throw e;
        }
    }

    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        logger.info("Generating symmetric key");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    private static byte[] generateRandomValue() {
        logger.info("Generating initial random value");
        byte[] IV = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        return IV;
    }

    public String getIvValuesAsString(){
        return  Base64.getEncoder().encodeToString(getIV());
    }
}
