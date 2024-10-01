package com.pay10.nodal.payout.yesBankNodalFT3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

@Service
public class YesBankFT3TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(YesBankFT3TransactionCommunicator.class.getName());

	public String getYesBankFT3Response(String req, String requestType, String serviceUrl) {
		String responseBody = "";
		String beneMaintenanceUsername = PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_BASIC_AUTH_BENE_USERNAME.getValue());
		String beneMaintenancePassword = PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_BASIC_AUTH_BENE_PASSWORD.getValue());
		String username = PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_BASIC_AUTH_USERNAME.getValue());
		String password = PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_BASIC_AUTH_PASSWORD.getValue());
		String clientSecret = PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_CLIENT_SECRET.getValue());
		String clientId = PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_CLIENT_ID.getValue());
		
		try {
			logger.info("Yes Bank FT3 Request : " + req);
			logger.info("Service URL : " + serviceUrl);
			SSLContext sslContext = getSSLContext();
			if(sslContext == null) {
				return null;
			}
			SSLSocketFactory sockFact = sslContext.getSocketFactory();
			URL url = new URL(serviceUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			if(requestType.equalsIgnoreCase("Beneficiary")) {
				username = beneMaintenanceUsername;
				password = beneMaintenancePassword;
			}
			String userpass = username + ":" + password;
			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));

			conn.setRequestProperty("Connection", "close");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("X-IBM-Client-Id", clientId);
			conn.setRequestProperty("X-IBM-Client-Secret", clientSecret);
			conn.setRequestProperty("Authorization", basicAuth);
			conn.setSSLSocketFactory(sockFact);
			OutputStream outputStream = conn.getOutputStream();
			outputStream.write(req.getBytes(StandardCharsets.UTF_8));
			outputStream.close();

			int responseCode = conn.getResponseCode();
			InputStream inputStream;
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inputStream = conn.getInputStream();
			} else {
				inputStream = conn.getErrorStream();
			}
			BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String response = "";
			while ((response = reader.readLine()) != null) {
				responseBody += response;
			}
			inputStream.close();

			logger.info("Yes Bank FT3 Response : " + responseBody);
			return responseBody;
		} catch (Exception e) {
			logger.error("Exception in getting beneficiary maintenance response.");
			logger.error(e.getMessage());
		}
		return null;
	}
	
	private SSLContext getSSLContext() {
		try {
			String pfxPath = PropertiesManager.propertiesMap.get(Constants.ASIANCHECKOUT_PFX_PATH.getValue());
			char[] passphrase = PropertiesManager.propertiesMap.get(Constants.ASIANCHECKOUT_PASSPHRASE.getValue()).toCharArray();
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new FileInputStream(pfxPath), passphrase);
			kmf.init(keyStore, passphrase);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());
			return sslContext;
		} catch (Exception e) {
			logger.error("Error while creating ssl context");
			logger.error(e.getMessage());
			return null;
		}
	}

}
