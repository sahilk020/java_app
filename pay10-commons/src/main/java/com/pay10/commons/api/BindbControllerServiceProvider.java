package com.pay10.commons.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.util.PropertiesManager;

@Service
public class BindbControllerServiceProvider {
	
	public Map<String,String> binfind(String cardnumber) {
		
		Map<String,String> resMap = new HashMap<String,String>();

		try {
			String serviceUrl = PropertiesManager.propertiesMap.get("BindbFindBinURL");
			StringBuilder uri = new StringBuilder();
			uri.append(serviceUrl);
			StringBuilder postData = new StringBuilder();

			if (postData.length() != 0)
				postData.append("&");

			postData.append(URLEncoder.encode(cardnumber, "UTF-8"));
			uri.append(postData);
			URL url = new URL(uri.toString());

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

			StringBuilder res = new StringBuilder();
			for (String c; (c = in.readLine()) != null;) {
				res.append(c);
			}
			
			   final ObjectMapper mapper = new ObjectMapper();
			   final MapType type = mapper.getTypeFactory().constructMapType(
			   Map.class, String.class, Object.class);
			   resMap = mapper.readValue(res.toString(), type);
			
			return resMap;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
