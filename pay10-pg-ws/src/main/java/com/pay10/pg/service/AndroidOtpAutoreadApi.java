package com.pay10.pg.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.commons.user.AndroidOtpAutoreadConfig;
import com.pay10.commons.user.AndroidOtpAutoreadConfigDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

/**
 * @author Rohit
 *
 */

@RestController
public class AndroidOtpAutoreadApi {
	private static Logger logger = LoggerFactory.getLogger(AndroidOtpAutoreadApi.class.getName());

	@RequestMapping(method = RequestMethod.GET, value = "/getOtpPageTitleAndId", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getOtpPageTileAndId(HttpServletRequest req) {
		String OtpPageTileAndIdToken = PropertiesManager.propertiesMap.get(Constants.GET_OTP_ID_TILE_API_TOKEN.getValue());
		String reqToken = req.getHeader("Authorization");
		if(OtpPageTileAndIdToken == null || !OtpPageTileAndIdToken.equals(reqToken)) {
			logger.info("Request Token : " + reqToken);
			logger.info("getOtpPageTileAndId Token : " + OtpPageTileAndIdToken);
			logger.error("Token mismatch");
			return Constants.AUTH_FAIL.toString();
		}
		List<AndroidOtpAutoreadConfig> respList = AndroidOtpAutoreadConfigDao.findLastPasswordCreateDate();
		JSONObject json = new JSONObject();
		String resJson = json.valueToString(respList);
		return resJson;
	}

}