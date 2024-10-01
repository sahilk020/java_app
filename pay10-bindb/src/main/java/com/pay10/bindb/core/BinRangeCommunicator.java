package com.pay10.bindb.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.pay10.bindb.service.BinRangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BinRangeCommunicator {
	private final static String USER_AGENT = "Mozilla/5.0";
	private static Logger logger = LoggerFactory.getLogger(BinRangeCommunicator.class.getName());

	public StringBuilder getCommunicator(String requestUrl, String cardBin) throws IOException {
		String requestString = requestUrl.concat(cardBin);
		URL url = new URL(requestString);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

		httpURLConnection.setRequestMethod("GET");

		// add request header
		httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

		String inputLine;
		StringBuilder response = new StringBuilder();

		while ((inputLine = bufferedReader.readLine()) != null) {
			response.append(inputLine);
		}
		bufferedReader.close();
		return response;
	}

	public Map<String, String> getCommunicator(String requestUrl) throws IOException {
		StringBuilder uri = new StringBuilder();
		Map<String, String> resMap = new HashMap<String, String>();
		uri.append(requestUrl);
		URL url = new URL(uri.toString());

		logger.info("request to bin db api " + uri);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// add request header
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestMethod("GET");
		// conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

		StringBuilder res = new StringBuilder();
		for (String c; (c = in.readLine()) != null;) {
			res.append(c);
		}
		logger.info("response from bin db api " + res);
		final ObjectMapper mapper = new ObjectMapper();
		final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
		resMap = mapper.readValue(res.toString(), type);

		return resMap;

	}

}
