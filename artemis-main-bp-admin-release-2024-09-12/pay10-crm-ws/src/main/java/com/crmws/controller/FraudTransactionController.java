package com.crmws.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.ApiResponse;
import com.crmws.dto.FraudTransactionDTO;
import com.crmws.service.FraudTxnService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/fraud/transaction")
public class FraudTransactionController {
	@Autowired
	FraudTxnService fraudTxnService;

	@GetMapping("/fetch")
	public ResponseEntity<ApiResponse> getFraudTransaction() {
		log.info("Request received for getFraudTransaction :: ");
		Optional<List<FraudTransactionDTO>> getFraudTxn = fraudTxnService.getFraudCapturedTransaction();
		ApiResponse res = new ApiResponse();
		if (getFraudTxn.isPresent()) {
			res.setMessage("Data fetched succesfully");
			res.setData(getFraudTxn.get());
			res.setStatus(true);
		} else {
			res.setMessage("Data not found");
			res.setData(null);
			res.setStatus(false);
		}
		return ResponseEntity.ok(res);
	}
	
	@PostMapping("/action/update")
	public ResponseEntity<ApiResponse> updateFraudTransactionAction(@RequestBody Map<String, String> actionRequest) {
		log.info("Request received for updateFraudTransactionAction :: "+actionRequest);
		boolean resultResp = fraudTxnService.updateFraudTransactionAction(actionRequest);
		if(resultResp && actionRequest.get("actionType").equalsIgnoreCase("BLOCK")) {
			fraudTxnService.updateFraudTransactionRule(actionRequest);
		}
		if(resultResp && actionRequest.get("actionType").equalsIgnoreCase("NOTIFYMERCHANT")) {
			fraudTxnService.notifyToMerchant(actionRequest);
		}
		//boolean resultResp = true; // TODO Call FraudService
		ApiResponse res = new ApiResponse();
		if (resultResp) {
			res.setMessage("Fraud Action Update Succesfully For "+actionRequest.get("actionType"));
			res.setData("");
			res.setStatus(true);
		} else {
			res.setMessage("Failed to update Fraud Action "+actionRequest.get("actionType"));
			res.setData(null);
			res.setStatus(false);
		}
		return ResponseEntity.ok(res);
	}
}
