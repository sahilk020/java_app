package com.pay10.pg.core.tokenization;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.dto.TokenCardResponseDto;
import com.pay10.commons.dto.TokenResponseDto;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.payu.util.Constants;
import com.pay10.pg.core.payu.util.PayuHasher;

@Service
public class TokenService {

	private static Logger logger = LoggerFactory.getLogger(TokenService.class.getName());

	@Autowired
	private ObjectMapper objectMapper;

	private static final String TOKEN_TXN_KEY = PropertiesManager.propertiesMap.getOrDefault("PAYUTOKENTXNKEY", null);
	private static final String TOKEN_ADF2 = PropertiesManager.propertiesMap.getOrDefault("PAYUTOKENADF2", null);

	public TokenResponseDto createToken(Fields fields) {
		String response = "";
		TokenResponseDto responseDto = null;
		if (StringUtils.isNoneBlank(TOKEN_TXN_KEY, TOKEN_ADF2)) {
			fields.put(FieldType.TXN_KEY.getName(), TOKEN_TXN_KEY);
			fields.put(FieldType.ADF2.getName(), TOKEN_ADF2);

			StringBuilder request = new StringBuilder();
			try {

				request.append("key=");

				request.append(fields.get(FieldType.TXN_KEY.getName()));

				request.append("&");
				request.append("command=");
				request.append("save_payment_instrument");
				request.append("&");
				request.append("hash=");
				request.append(PayuHasher.payuTokenCreateHash(fields));
				request.append("&");
				request.append("var1=");

				request.append(
						fields.get(FieldType.TXN_KEY.getName()) + ":" + fields.get(FieldType.CUST_EMAIL.getName()));

				request.append("&");
				request.append("var2=");
				request.append(fields.get(FieldType.CARD_HOLDER_NAME.getName()));
				request.append("&");
				request.append("var3=");
				request.append(fields.get(FieldType.PAYMENT_TYPE.getName()));
				request.append("&");
				request.append("var4=");
				request.append(fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()).toUpperCase());
				request.append("&");
				request.append("var5=");
				request.append(fields.get(FieldType.CARD_HOLDER_NAME.getName()));
				request.append("&");
				request.append("var6=");
				request.append(fields.get(FieldType.CARD_NUMBER.getName()));
				request.append("&");
				request.append("var7=");
				request.append(fields.get(Constants.CCEXPMON));
				request.append("&");
				request.append("var8=");
				request.append(fields.get(Constants.CCEXPYR));

				HttpURLConnection connection = null;
				URL url;
				url = new URL(PropertiesManager.propertiesMap.get(Constants.PAYU_TOKEN_URL));
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(60000);
				connection.setReadTimeout(60000);

				logger.info("createToken:: request={}", request.toString());
				
				// Send request
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(request.toString());
				wr.flush();
				wr.close();

				// Get Response
				InputStream is = connection.getInputStream();

				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
				String decodedString;

				while ((decodedString = bufferedreader.readLine()) != null) {
					response = response + decodedString;
				}

				bufferedreader.close();

				logger.info("Response for create token >> " + response);
				responseDto = objectMapper.readValue(response, new TypeReference<TokenResponseDto>() {
				});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.error("Error Creating Token create token ,No account found");
		}
		return responseDto;
	}

	public TokenCardResponseDto getUserCards(Fields fields) {
		String response = "";
		TokenCardResponseDto responseDto = null;
		if (StringUtils.isNoneBlank(TOKEN_TXN_KEY, TOKEN_ADF2)) {
			fields.put(FieldType.TXN_KEY.getName(), TOKEN_TXN_KEY);
			fields.put(FieldType.ADF2.getName(), TOKEN_ADF2);
			StringBuilder request = new StringBuilder();
			try {
				request.append("key=");
				request.append(fields.get(FieldType.TXN_KEY.getName()));

				request.append("&");
				request.append("command=");
				request.append("get_payment_instrument");
				request.append("&");
				request.append("hash=");
				request.append(PayuHasher.payuTokenDetailsHash(fields));
				request.append("&");
				request.append("var1=");

				request.append(
						fields.get(FieldType.TXN_KEY.getName()) + ":" + fields.get(FieldType.CUST_EMAIL.getName()));

				HttpURLConnection connection = null;
				URL url;
				url = new URL(PropertiesManager.propertiesMap.get(Constants.PAYU_TOKEN_URL));
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(60000);
				connection.setReadTimeout(60000);

				// Send request
				logger.info("Request for get token >> " + request.toString());
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(request.toString());
				wr.flush();
				wr.close();

				// Get Response
				InputStream is = connection.getInputStream();

				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
				String decodedString;

				while ((decodedString = bufferedreader.readLine()) != null) {
					response = response + decodedString;
				}

				bufferedreader.close();

				logger.info("Response for get token >> " + response);
				responseDto = objectMapper.readValue(response, new TypeReference<TokenCardResponseDto>() {
				});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.error("Error Getting Token ,No account found");
		}
		return responseDto;

	}

	public TokenResponseDto deleteToken(Fields fields) {
		String response = "";
		TokenResponseDto responseDto = null;
		if (StringUtils.isNoneBlank(TOKEN_TXN_KEY, TOKEN_ADF2)) {
			fields.put(FieldType.TXN_KEY.getName(), TOKEN_TXN_KEY);
			fields.put(FieldType.ADF2.getName(), TOKEN_ADF2);
			StringBuilder request = new StringBuilder();
			try {
				request.append("key=");
				request.append(fields.get(FieldType.TXN_KEY.getName()));
				request.append("&");
				request.append("command=");
				request.append("delete_payment_instrument");
				request.append("&");
				request.append("hash=");
				request.append(PayuHasher.payuTokenDeleteHash(fields));
				request.append("&");
				request.append("var1=");
				request.append(
						fields.get(FieldType.TXN_KEY.getName()) + ":" + fields.get(FieldType.CUST_EMAIL.getName()));
				request.append("&");
				request.append("var2=");
				request.append(fields.get(FieldType.TOKEN_ID.getName()));

				HttpURLConnection connection = null;
				URL url;
				url = new URL(PropertiesManager.propertiesMap.get(Constants.PAYU_TOKEN_URL));
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(60000);
				connection.setReadTimeout(60000);

				// Send request
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(request.toString());
				wr.flush();
				wr.close();

				// Get Response
				InputStream is = connection.getInputStream();

				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
				String decodedString;

				while ((decodedString = bufferedreader.readLine()) != null) {
					response = response + decodedString;
				}

				bufferedreader.close();

				logger.info("Response for delete token >> " + response);
				responseDto = objectMapper.readValue(response, new TypeReference<TokenResponseDto>() {
				});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.error("Error Deleting Token ,No account found");
		}
		return responseDto;
	}
}
