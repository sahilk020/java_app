package com.pay10.pg.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.commons.util.Fields;
import com.pay10.nodal.payout.NodalRequestHandler;

@RestController
public class SettlementService {

	private static Logger logger = LoggerFactory.getLogger(SettlementService.class.getName());

	@Autowired
	private NodalRequestHandler nodalRequestHandler;
	
	//settle amount directly to current account (Merchant or PG)
		@PostMapping(value = "/nodalSettlement", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
		public @ResponseBody Map<String, String> nodalSettlement(@RequestBody Map<String, String> reqmap) {
			try {
				Fields fields = new Fields(reqmap);
				fields.logAllFields("Nodal Settlement Raw Request:");
				return nodalRequestHandler.settlementProcess(fields);

			} catch (Exception exception) {
				// Ideally this should be a non-reachable code
				logger.error("Failed to process request : ", exception);
				return null;
			}
		}
}
