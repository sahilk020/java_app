package com.pay10.freecharge;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.commons.util.Fields;

@RestController
public class FreeChargeTransact {

	private static Logger logger = LoggerFactory.getLogger(FreeChargeTransact.class.getName());

	@RequestMapping(method = RequestMethod.POST, value = "freeCharge/refundInitiate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> refundInitiate(@RequestBody Map<String, String> reqmap) {
		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("freeCharge Raw Request:");
			fields.clean();
			Map<String, String> responseMap = new HashMap<String, String>();

			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "freeCharge/transactionStatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> transactionStatus(@RequestBody Map<String, String> reqmap) {
		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("freeCharge Raw Request:");
			fields.clean();
			Map<String, String> responseMap = new HashMap<String, String>();

			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

}
