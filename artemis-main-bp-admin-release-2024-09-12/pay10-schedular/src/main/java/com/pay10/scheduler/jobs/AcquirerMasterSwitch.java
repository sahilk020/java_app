package com.pay10.scheduler.jobs;

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

import com.pay10.commons.user.AcquirerDowntimeSchedulingDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.scheduler.commons.ConfigurationProvider;

@Service
public class AcquirerMasterSwitch {

    private static final Logger logger = LoggerFactory.getLogger(AcquirerMasterSwitch.class);


	@Autowired
	private ConfigurationProvider configurationProvider;
	  
	  
	  public void getAcquirerMasterSwitch() {
		  
			String response="";

				try {

					HttpURLConnection connection = null;
					URL url;
					url = new URL(configurationProvider.getAcquirerMasterSwitch());
					logger.info("url for Autorefund" + url);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");

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
				}

			}
	       
 
	  
	  
	  
}
