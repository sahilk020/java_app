package com.pay10.pg.core.sbicard;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
public class EncryptionUtil {

    public static final int GCM_TAG_LENGTH = 16;

    public static String digitalSignWithRSA(String message, RSAPrivateKey privateKey) throws JOSEException {
        JWSSigner signer = new RSASSASigner(privateKey);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("RTH").claim("message", message)
                .issuer("https://rth.com")
                .expirationTime(new Date(new Date().getTime() + 60 * 1000)).build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
        signedJWT.sign(signer);
        String token = signedJWT.serialize();
        return token;
    }

    public static String encryptDEK(SecretKey key, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(key.getEncoded()));
    }

    public static byte[] wrapKey(PublicKey pubKey, SecretKey symKey)
            throws InvalidKeyException, IllegalBlockSizeException {
        try {
            final Cipher cipher = Cipher
                    .getInstance("RSA");

            cipher.init(Cipher.WRAP_MODE, pubKey);

            final byte[] wrapped = cipher.wrap(symKey);
            return wrapped;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(
                    "Java runtime does not support RSA/ECB/OAEPWithSHA1AndMGF1Padding",
                    e);
        }
    }

    public static String encrypt(String plaintextStr, AES aes) throws Exception {
        byte[] plaintext = plaintextStr.getBytes();
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(aes.getKey().getEncoded(), "AES");
        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, aes.getIV());
        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
        // Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static EncryptedRequestData encrypt(String request) {
        EncryptedRequestData encryptedReq = null;
        try {
            String signedRequest = EncryptionUtil.digitalSignWithRSA(request,
                    Client.getPrivateKey());
            AES aes = AES.init();
            String encryptedRequest = EncryptionUtil.encrypt(signedRequest, aes);
            String encSymmetricKey = EncryptionUtil.encryptDEK(aes.getKey(), Server.getPublicKey());
            encryptedReq = EncryptedRequestData.buildRequest(encryptedRequest, encSymmetricKey,
                    aes);
        } catch (Exception ex) {
            log.error("encrypt:: failed. request={}", request, ex);
        }
        return encryptedReq;
    }
}
