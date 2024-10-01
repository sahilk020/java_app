package com.pay10.pg.core.sbicard;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;
import java.util.Properties;

public class JksUtil {

    private static Logger logger = LoggerFactory.getLogger(JksUtil.class.getName());
    private static final String fileLocation = System.getenv("BPGATE_PROPS");
    private static Properties properties;

    private void JksUtil(){
    }

    static {
        properties = loadProperties();
    }

    private static Properties loadProperties(){
        logger.info("loadProperties:: folderPath={}", fileLocation);
        String path = StringUtils.join(fileLocation, "sbi-card-config.properties");
        path = path.replace("\\", "/");
        Properties props = new Properties();
        try {
            InputStream inputStream = new FileInputStream(path);
            logger.info("loadProperties:: stream={}, path={}", inputStream, path);
            props.load(inputStream);
            logger.info("loadProperties:: property file loaded");
        } catch (IOException e) {
            logger.warn(e.getLocalizedMessage());
            throw new RuntimeException("Exception occurred reading properties");
        }
        return props;
    }

    public static KeyStore.PrivateKeyEntry getKeyPairEntryForClient(){
        String keystoreFilename = properties.getProperty("CLIENT_JKS_FILE_NAME");
        String keystoreFilePwd = properties.getProperty("CLIENT_JKS_FILE_PWD");
        String aliasName = properties.getProperty("CLIENT_ALIAS_NAME");
        String aliasPwd = properties.getProperty("CLIENT_ALIAS_PWD");
        return loadKeys(keystoreFilename,keystoreFilePwd, aliasName, aliasPwd);
    }

    public static RSAPublicKey getPublicKeyForServer(){
       String keystoreFilename = properties.getProperty("CLIENT_JKS_FILE_NAME");
        String keystoreFilePwd = properties.getProperty("CLIENT_JKS_FILE_PWD");
        String aliasName = properties.getProperty("SERVER_PK_ALIAS_NAME");
        return loadPublicKeys(keystoreFilename,keystoreFilePwd, aliasName);
    }

    private static KeyStore.PrivateKeyEntry loadKeys(String keystoreFilename, String keystoreFilePwd, String aliasName, String aliasPwd) {
        try {
            char[] password = keystoreFilePwd.toCharArray();
            char[] aliasPwdChars = aliasPwd.toCharArray();

            KeyStore keystore;
            try(FileInputStream fIn = new FileInputStream(keystoreFilename)) {
                keystore = KeyStore.getInstance("JKS");
                keystore.load(fIn, password);
            }
            return  (KeyStore.PrivateKeyEntry) keystore.getEntry(Objects.requireNonNull(aliasName), new KeyStore.PasswordProtection(aliasPwdChars));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error Getting Private Key", ex);
            throw new RuntimeException("Unable to load keys from jks : " + ex.getMessage());
        }
    }
	
	private static RSAPublicKey loadPublicKeys(String keystoreFilename, String keystoreFilePwd, String aliasName) {
        try {
            char[] password = keystoreFilePwd.toCharArray();

            KeyStore keystore;
            try(FileInputStream fIn = new FileInputStream(keystoreFilename)) {
                keystore = KeyStore.getInstance("JKS");
                keystore.load(fIn, password);
            }
            return (RSAPublicKey) keystore.getCertificate(aliasName).getPublicKey();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error Getting Server public key", ex);
            throw new RuntimeException("Unable to load keys from jks : " + ex.getMessage());
        }
    }

}
