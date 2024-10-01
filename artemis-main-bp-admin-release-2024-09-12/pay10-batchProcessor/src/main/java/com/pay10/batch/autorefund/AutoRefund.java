package com.pay10.batch.autorefund;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.ConfigurationProvider;
import com.pay10.commons.util.FieldType;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutoRefund {

	private static final Logger logger = LoggerFactory.getLogger(AutoRefund.class);

	@Autowired
	private ConfigurationProvider configProvider;
	
	
	public void callPostSettlementAutoRefund(Fields fields) {
		
		try {
			JSONObject refundObject = new JSONObject();
			refundObject.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
			String hostUrl = configProvider.getPostSettlementAutoRefundUrl();
			URL url = new URL(hostUrl);
			int timeout = 20000;
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(timeout);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");

			OutputStream os = conn.getOutputStream();
			os.write(refundObject.toString().getBytes());
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			conn.disconnect();
		} catch (Exception exception) {
			logger.info("Exception in Refund WS call : " + exception.getMessage());
		}
	}
}
