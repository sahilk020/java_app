package com.pay10.batch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.ConfigurationProvider;
@Service("deltaRefundCommunicator")
public class DeltaRefundCommunicator {
	
	private static final Logger logger = LoggerFactory.getLogger(DeltaRefundCommunicator.class);
	
	@Autowired
	private ConfigurationProvider configProvider;

	public String deltaCommunicator(JSONObject refundValue, Fields fields) {

		 String hostUrl = configProvider.getDeltaRefundIrctcUrl();
	
	try {

			URL url = new URL(hostUrl);
			int timeout = 20000;
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(timeout);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			OutputStream os = conn.getOutputStream();
			os.write(refundValue.toString().getBytes());
			os.flush();

			logger.info("Refund Communicator Request : " + refundValue.toString());
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			conn.disconnect();
			String serverResponse = sb.toString();

			// JSONObject response = new JSONObject(serverResponse);
			logger.info("Refund Communicator Response : " + serverResponse);
			return serverResponse;
		} catch (Exception e) {
			// logger.info("Exception in RDS call " + e.getMessage());
		}
		return null;
	}

}
