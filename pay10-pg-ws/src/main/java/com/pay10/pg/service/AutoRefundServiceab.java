package com.pay10.pg.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;

@Service("auto")
public class AutoRefundServiceab {
	private static Logger logger = LoggerFactory.getLogger(AutoRefundServiceab.class.getName());

	@Autowired
	private PropertiesManager propertiesManager;

	// http://localhost:8080/crmws/Autorefundcon/AutoRefund
	public String AutoRefundcommunicator(String pgRefNo) {
		JSONObject json = new JSONObject();
		json.put(FieldType.PG_REF_NUM.getName(), pgRefNo);
		String request = json.toString();
		logger.info("refund for Auto refund " + request);
		String response = "";
		final String autorefund = propertiesManager.propertiesMap.get(Constants.AUTO_REFUND.getValue());

		try {

			HttpURLConnection connection = null;
			URL url;
			url = new URL(autorefund + "?" + FieldType.PG_REF_NUM.getName() + "=" + pgRefNo);
			logger.info("url for Autorefund" + url);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			InputStream is = connection.getInputStream();
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
			String decodedString;

			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}

			bufferedreader.close();

			logger.info("  Response for refund transaction >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting Refund respose for Federal bank NB", e);
			response = "{\"message\":\"Error in Refund.\",\"refundId\":NA,\"status\":\"FAILED\"}";
			return response;
		}
		return response;

	}
}
