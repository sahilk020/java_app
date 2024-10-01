package com.pay10.pg.core.sbicard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Base64;

@Slf4j
public class DecryptionUtil {

    public static final int GCM_TAG_LENGTH = 16;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static SecretKey decryptDEK(String encryptedDEK, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new SecretKeySpec(cipher.doFinal(Base64.getDecoder().decode(encryptedDEK)), "AES");
    }

    private static SecretKey unwrapKey(PrivateKey privateKey, byte[] warpedkey)
            throws InvalidKeyException, IllegalBlockSizeException {
        try {
            final Cipher cipher = Cipher
                    .getInstance("RSA");
            cipher.init(Cipher.UNWRAP_MODE, privateKey);
            SecretKey unwrapedkey = (SecretKey) cipher.unwrap(warpedkey, "AES", Cipher.SECRET_KEY);
            return unwrapedkey;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(
                    "Java runtime does not support RSA/ECB/OAEPWithSHA1AndMGF1Padding",
                    e);
        }
    }

    public static String decrypt(String payload, String iv, SecretKey key) throws Exception {
        byte[] cipherText = Base64.getDecoder().decode(payload);
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8,
                Base64.getDecoder().decode(iv));
        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
        // Perform Decryption
        byte[] decryptedText = cipher.doFinal(cipherText);
        return new String(decryptedText);

    }

    public static void verifySignature(String signedResponse, RSAPublicKey publicKey) throws Exception {
        SignedJWT signedJWT2 = SignedJWT.parse(signedResponse);
        JWSVerifier verifier = new RSASSAVerifier(publicKey);
        boolean flag = signedJWT2.verify(verifier);
        if (!flag)
            throw new Exception("Signture not verified");
    }

    public static String getJsonFromJws(String token) throws ParseException, JSONException {
        SignedJWT signedJWT2 = SignedJWT.parse(token);
        return (String) signedJWT2.getJWTClaimsSet().getClaim("message");
    }

    public static JSONObject decrypt(String encrypted) throws SystemException {
        try {
            EncryptedResponseData encryptedResponse = mapper.readValue(encrypted,
                    EncryptedResponseData.class);
            if (encryptedResponse.getStatusCode().equals("PG99200")) {
                SecretKey decryptedSymmetricKey =
                        decryptDEK(encryptedResponse.getResponseSymmetricEncKey(), Client.getPrivateKey());
                String signedResponse = decrypt(encryptedResponse.getSignedEncResponsePayload(),
                        encryptedResponse.getIv(), decryptedSymmetricKey);
                verifySignature(signedResponse, Server.getPublicKey());
                String res = getJsonFromJws(signedResponse);
                log.info("decrypt:: response={}", res);
                JSONParser parser = new JSONParser();
                if (parser.parse(res) instanceof JSONArray) {
                    return (JSONObject) ((JSONArray) parser.parse(res)).get(0);
                }
                return (JSONObject) parser.parse(res);
            }
        } catch (Exception ex) {
            log.error("decrypt:: failed. encReq={}", encrypted, ex);
            throw new SystemException(ErrorType.ACQUIRER_ERROR,
                    ErrorType.ACQUIRER_ERROR.getInternalMessage());
        }
        return null;
    }
}
