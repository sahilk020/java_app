package com.pay10.pg.core.sbicard;

import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

public class Client {

    private static RSAPublicKey publicKey;
    private static RSAPrivateKey privateKey;

    static {
        init();
    }

    public static void init(){
        KeyStore.PrivateKeyEntry entry = JksUtil.getKeyPairEntryForClient();
        privateKey = (RSAPrivateKey) entry.getPrivateKey();
        publicKey = (RSAPublicKey) entry.getCertificate().getPublicKey();

    }

    public static RSAPublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return publicKey;
    }

    public static RSAPrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return privateKey;
    }
}
