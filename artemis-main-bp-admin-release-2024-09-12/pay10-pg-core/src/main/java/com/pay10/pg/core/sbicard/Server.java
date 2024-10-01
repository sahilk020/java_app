package com.pay10.pg.core.sbicard;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

public class Server {
    private static RSAPublicKey publicKey;

    static {
        init();
    }

    public static void init(){
        publicKey = JksUtil.getPublicKeyForServer();
    }

    public static RSAPublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return publicKey;
    }
}