package com.pay10.pg.emitra;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

import bsh.This;

@RestController
public class EmitraApiController {
	private static Logger logger = LoggerFactory.getLogger(This.class.getName());

	@Autowired
	private EmitraService emitraService;

	@PostMapping(value = "emitra/requery", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> requery(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("requery:: initialize. request={}", reqmap);
			Fields fields = new Fields(reqmap);
			fields.logAllFields("requery:: Initial Raw Request:");
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			Map<String, String> responseMap = emitraService.statusInquiry(fields);
			responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
			return responseMap;
		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("requery:: failed. request={}", reqmap, exception);
			return null;
		}

	}

	@PostMapping(value = "emitra/refund", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> refund(@RequestBody Map<String, String> reqmap) {
		return emitraService.refund(reqmap);
	}

	@PostMapping(value = "emitra/refund/requery", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> requeryRefund(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("requeryRefund:: initialize. request={}", reqmap);
			Fields fields = new Fields(reqmap);
			fields.logAllFields("requeryRefund:: Initial Raw Request:");
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			Map<String, String> responseMap = emitraService.refundStatusInquiry(fields);
			responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
			return responseMap;
		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("requeryRefund:: failed. request={}", reqmap, exception);
			return null;
		}

	}
}
