package com.crmws.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.ApiResponse;
import com.crmws.dto.TPAPTxnLimitDTO;
import com.crmws.service.TPAPService;
import com.pay10.commons.user.TPAPTransactionLimit;

@CrossOrigin
@RestController
@RequestMapping("/tpap/transaction")
public class TPAPController {
	private static final Logger logger = LoggerFactory.getLogger(TPAPController.class.getName());

	@Autowired
	TPAPService tpapService;

	@PostMapping("/add")
	public ApiResponse addTPAPTransactionLimitInfo(@RequestBody Map<String, String> reqestInfo) {
		logger.info("Request Received For Add TPAPTransactionLimitInfo RequestInfo={}", reqestInfo);
		ApiResponse apiResponse = new ApiResponse();

		boolean result = tpapService.addTPAPTransactionLimitInfo(reqestInfo);
		if (result) {
			apiResponse.setMessage("TPAP Transaction Limit Details Added Successfully");
			apiResponse.setData("");
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("Something goes wrong, Please try after some time!!");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}

	@GetMapping("/limit/fetch")
	public ResponseEntity<ApiResponse> getTPAPTransactionLimitDetails() {
		logger.info("Request received for getTPAPTransactionLimitDetails :: ");
		List<TPAPTransactionLimit> getTxnLimitInfo = tpapService.getTPAPTransactionLimitDetails();
		ApiResponse res = new ApiResponse();
		if (getTxnLimitInfo != null) {
			res.setMessage("Data fetched succesfully");
			res.setData(getTxnLimitInfo);
			res.setStatus(true);
		} else {
			res.setMessage("Data not found");
			res.setData(null);
			res.setStatus(false);
		}
		return ResponseEntity.ok(res);
	}

	@PostMapping("/limit/update")
	public ResponseEntity<ApiResponse> updateTPAPTransactionLimitDetails(
			@RequestBody Map<String, String> actionRequest) {
		logger.info("Request received for updateFraudTransactionAction :: " + actionRequest);
		boolean resultResp = tpapService.updateTpapTxnLimit(actionRequest);
		ApiResponse res = new ApiResponse();
		if (resultResp) {
			res.setMessage("TPAP Transaction Limit Update Succesfully");
			res.setData("");
			res.setStatus(true);
		} else {
			res.setMessage("Failed to update TPAP Transaction Limit");
			res.setData(null);
			res.setStatus(false);
		}
		return ResponseEntity.ok(res);
	}
	
	
	@DeleteMapping("/limit/inactive/{txnLimitId}")
	public ApiResponse deleteTPAPTransactionLimit(@PathVariable String txnLimitId) {
		logger.info("Request Received For Delete TPAPTransactionLimit " + txnLimitId);
		ApiResponse apiResponse = new ApiResponse();
		if (!isNumeric(txnLimitId)) {
			apiResponse.setMessage("TPAP TransactionLimitId should be Numeric value only.");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
		boolean resp = tpapService.checkDuplicateTxnLimitId(txnLimitId);
		if (!resp) {
			apiResponse.setMessage("TPAP TransactionLimitId not available for deletion operation");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
		boolean result = tpapService.inactiveTxnLimitById(txnLimitId);
		if (result) {
			apiResponse.setMessage("TPAP Transaction Limit Rule Deleted Successfully");
			apiResponse.setData("");
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("Something goes wrong, Please try after some time!!");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}
	
	public static boolean isNumeric(String strNum) {
		if (strNum == null || strNum.isEmpty()) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
