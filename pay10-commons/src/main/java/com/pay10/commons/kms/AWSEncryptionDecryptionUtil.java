
package com.pay10.commons.kms;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.pay10.commons.util.PropertiesManager;

@Service
public class AWSEncryptionDecryptionUtil {

	private final PropertiesManager propertiesManager = new PropertiesManager();
	private String keyArn = propertiesManager.propertiesMap.get("AWSARN");
	private AwsCrypto crypto;
	private KmsMasterKeyProvider prov;
	private String AWSContextName;
	private String AWSContextValue;
	private Map<String, String> context;
	private static Logger logger = LoggerFactory.getLogger(AWSEncryptionDecryptionUtil.class.getName());

	private static boolean isLoaded = false;

	public AWSEncryptionDecryptionUtil() {

		if (!isLoaded) {

			keyArn = PropertiesManager.propertiesMap.get("AWSARN");
			crypto = new AwsCrypto();
			//prov = new KmsMasterKeyProvider(keyArn);
			prov =  KmsMasterKeyProvider.builder().withKeysForEncryption(keyArn).build();
			AWSContextName = PropertiesManager.propertiesMap.get("AWSContextName");
			AWSContextValue = PropertiesManager.propertiesMap.get("AWSContextValue");
			context = Collections.singletonMap(AWSContextName, AWSContextValue);
			isLoaded = true;
		}
	}

	public String encrypt(String data) {

		try {

			final String ciphertext = crypto.encryptString(prov, data, context).getResult();
			return ciphertext;

		}

		catch (Exception e) {
			logger.error("Exception in AWSEncryptionDecryptionUtil encrypt , exception = ", e);
			return null;
		}

	}

	public String decrypt(String data) {

		try {

			final CryptoResult<String, KmsMasterKey> decryptResult = crypto.decryptString(prov, data);

			if (!decryptResult.getMasterKeyIds().get(0).equals(keyArn)) {
				throw new IllegalStateException("Wrong key ID!");
			}

			for (final Map.Entry<String, String> e : context.entrySet()) {
				if (!e.getValue().equals(decryptResult.getEncryptionContext().get(e.getKey()))) {
					throw new IllegalStateException("Wrong Encryption Context!");
				}
			}

			String decryptedString = decryptResult.getResult();
			return decryptedString;

		}

		catch (Exception e) {
			logger.error("Exception in AWSEncryptionDecryptionUtil decrypt , exception = ", e);
			return null;
		}
	}
}
