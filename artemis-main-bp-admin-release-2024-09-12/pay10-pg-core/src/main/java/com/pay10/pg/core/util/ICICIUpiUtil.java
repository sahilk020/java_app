/**
 * 
 */
package com.pay10.pg.core.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("iciciUpiUtil")
public class ICICIUpiUtil {

	private static Logger logger = LoggerFactory.getLogger(ICICIUpiUtil.class.getName());

	public String getEncrypted(String message, String certFile) {
		byte[] messageBytes;
		byte[] tempPub = null;
		String sPub = null;
		byte[] encryptedKey = null;
		byte[] textBytes = null;

		try {
			SecureRandom secureRandom = new SecureRandom();
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			// Loading certificate file

			InputStream inStream = new FileInputStream(certFile);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
			inStream.close();

			messageBytes = message.getBytes();
			RSAPublicKey pubkey = (RSAPublicKey) cert.getPublicKey();
			tempPub = pubkey.getEncoded();
			sPub = new String(tempPub);
			// Initialize the cipher for encryption
			cipher.init(Cipher.ENCRYPT_MODE, pubkey, secureRandom);

			// Encrypt the message
			byte[] ciphertextBytes = cipher.doFinal(messageBytes);
			String encryptedmsg = new String(Base64.getEncoder().encodeToString(ciphertextBytes));

			// LOG.info("Message encrypted with certificate file public key:\n"
			// +encryptedMsg+ "\n");
			return encryptedmsg;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error in getEncryptedMessageByUsingPublicKey with error message:" + e.getMessage());
		}
		return null;
	}

	public String getMessageDiscryption(String msg, String keyFile) {
		// msg="gzhaYfjz2S+vIRIdtEAlPztjy8hAT1CbZiY6261tmBxB/EhUC/tmqC0XB+Bmv+57srP1fMsRoa6NI8sFzDC/L76kDJoJwq9GCYooofR+taT0H8P9K6xGWReEqfe9k8BcrZBKNiEt9c5wtQczuHSvg+Wt5ie3XFEj7fR+SSURYuaeQtTU+cbFDwNz9yG3cD83GD7opsjMPhp1k7z0bmjyE0gjQtHe7wibSnyuQzw9ZuhgVMYlCM6RXYnh66ilkzv30F3qgcBo0TK3pprI+9EZt8Hnd5mxf6neoBKQxoG1WMMolGXpMyO+Pl3mr4MpHIGZRE+F2ensC5cG0+l8eX9eNPbi9cL5/JcqLZ5p4yDdXIbg45QCcMLd4YQvbrsYPvzS8XG2We7g/v9AeOLoWknCtW06zjh+NCLwjalpcCj84Ny300LiGKiJC1lCHSWVhXErm+r9gB4BmVfI7e/Kjt1i07nmYLXKeQAXVHpq1fAK4Mf50xN+hDDCGTZ2C/F98q7Uz9hiKzzBQwMPFMGJniWPI+bL9xJiFM8EdxbF4Mu8IfXbN+AdA2az1FDNlzmEEApIQGj3Qlc3j3asBvEg6SpDRt2hLQoHAaxrKX58DgYDcHNi5Ur0GLhV7xRLFZR651pqvvlwFeSfd/wkz1qrx4QckFNIQ4YZ78SSltu46xFRhwI=";
		// writeLog("encrypted response msg from ICICI: "+msg);
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			SecureRandom secureRandom = new SecureRandom();

			// String keyFile = NH7Constants.CERTS_PATH+"nh7_prod_priv.pem";
			InputStream inStream = new FileInputStream(keyFile);
			byte[] encKey = new byte[inStream.available()];
			inStream.read(encKey);
			inStream.close();

			// writeLog(encryptedKey);
			// writeLog(encryptedData);
			String pvtKey = new String(encKey);
			// writeLog("before replace pvtKey:"+pvtKey);
			pvtKey = pvtKey.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "")
					.replace("-----END PRIVATE KEY-----", "");
			// writeLog("after replace pvtkey:"+pvtKey);
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pvtKey));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			PrivateKey priv = (RSAPrivateKey) keyFactory.generatePrivate(privKeySpec);

			// Initialize the cipher for decryption
			cipher.init(Cipher.DECRYPT_MODE, priv, secureRandom);

			byte[] ciphertextBytes = Base64.getDecoder().decode(msg.getBytes(StandardCharsets.UTF_8));
			// Decrypt the encryptedKey

			String output = new String(cipher.doFinal(ciphertextBytes));
			// String s = new String(textBytes);
			// writeLog("response after decrypting: "+output);
			logger.info("decrypt response----+" + output);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in getMessageDiscryptionByUsingPrivateKey with error message:" + e.getMessage());
		}
		return null;
	}

}
