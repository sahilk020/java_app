package com.crmws.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.AcquirerRebalanceDTO;
import com.crmws.dto.AcquirerRebalanceTxnDTO;
import com.crmws.dto.ApiResponse;
import com.crmws.service.AcquirerRebalanceService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/acquirerRebalace")
@Slf4j
public class AcquirerRebalanceController {
	
	@Autowired
	AcquirerRebalanceService acquirerRebalanceService;

	@GetMapping("/action/acquirerList")
	public ResponseEntity<ApiResponse> getAcquirerList() {
		log.info("Request received for getFraudTransaction :: ");
		Optional<List<AcquirerRebalanceDTO>> getAcquirerList = acquirerRebalanceService.getAcquirerRebalanceList();
		ApiResponse res = new ApiResponse();
		if (getAcquirerList.isPresent()) {
			res.setMessage("Data fetched succesfully");
			res.setData(getAcquirerList.get());
			res.setStatus(true);
		} else {
			res.setMessage("Data not found");
			res.setData(null);
			res.setStatus(false);
		}
		return ResponseEntity.ok(res);
	}
	
	@GetMapping("/action/acquirerList/{acquirerName}")
	public ResponseEntity<ApiResponse> getAcquirerList(@PathVariable String acquirerName) {
		log.info("Request received for getFraudTransaction :: ");
		Optional<List<AcquirerRebalanceDTO>> getAcquirerList = acquirerRebalanceService.getAcquirerListByAcquirerName(acquirerName);
		ApiResponse res = new ApiResponse();
		if (getAcquirerList.isPresent()) {
			res.setMessage("Data fetched succesfully");
			res.setData(getAcquirerList.get());
			res.setStatus(true);
		} else {
			res.setMessage("Data not found");
			res.setData(null);
			res.setStatus(false);
		}
		return ResponseEntity.ok(res);
	}
	
	@PostMapping("/action/fund/transfer")
	public ResponseEntity<ApiResponse> updateAcquirerBalance(@RequestBody Map<String, String> actionRequest) {
		log.info("Request received for fund transfer :: "+actionRequest);
		String resultResp = acquirerRebalanceService.acquirerFundTransfer(actionRequest);
		ApiResponse res = new ApiResponse();
		if (StringUtils.isNotBlank(resultResp) && resultResp.equalsIgnoreCase("success")) {
			res.setMessage("Fund transfered request process succesfully of amount  "+actionRequest.get("amount"));
			res.setData("");
			res.setStatus(true);
		} else if(StringUtils.isNotBlank(resultResp)) {
			res.setMessage(resultResp);
			res.setData("");
			res.setStatus(false);
		} else {
			res.setMessage("Fund transfer failed, Please try again");
			res.setData(null);
			res.setStatus(false);
		}
		return ResponseEntity.ok(res);
	}
	
	@PostMapping("/action/fund/add")
	public ResponseEntity<ApiResponse> addAcquirerBalance(@RequestBody Map<String, String> actionRequest) {
		log.info("Request received for fund transfer : "+actionRequest);
		String resultResp = acquirerRebalanceService.addAcquirerFund(actionRequest);
		ApiResponse res = new ApiResponse();
		if (StringUtils.isNotBlank(resultResp) && resultResp.equalsIgnoreCase("success")) {
			res.setMessage("Add fund request initiated succesfully of amount  "+actionRequest.get("amount"));
			res.setData("");
			res.setStatus(true);
		} else {
			res.setMessage("Add fund request initiation failed, Please try again");
			res.setData(null);
			res.setStatus(false);
		}
		return ResponseEntity.ok(res);
	}
	
	@PostMapping("/action/fund/{actionStatus}")
	public ResponseEntity<ApiResponse> actionAcquirerBalanceStatus(@PathVariable String actionStatus, @RequestBody Map<String, String> actionRequest) {
		log.info("Request received for actionAcquirerBalanceStatus : actionStatus:"+actionStatus + ", actionRequest:"+actionRequest);
		
		boolean resultResp = false;
		if(StringUtils.equalsIgnoreCase(actionStatus, "REJECTED") | StringUtils.equalsIgnoreCase(actionStatus, "APPROVED")) {
			resultResp = acquirerRebalanceService.actionAcquirerBalanceStatus(actionRequest, actionStatus);
		}
		
		ApiResponse res = new ApiResponse();
		if (resultResp) {
			res.setMessage("Fund Request Successfully "+actionStatus.substring(0, 1).toUpperCase()+ actionStatus.substring(1).toLowerCase());
			res.setData("");
			res.setStatus(true);
		} else {
			res.setMessage("Fund Request Failed, please try again");
			res.setData(null);
			res.setStatus(false);
		}
		return ResponseEntity.ok(res);
	}
	
	
	@GetMapping("/action/transactionList/{type}")
	public ResponseEntity<ApiResponse> getAcquirerTransactions(@PathVariable String type) {
		log.info("Request received for getFraudTransaction : ");
		List<AcquirerRebalanceTxnDTO> getTxnsList = acquirerRebalanceService.getAcquirerTransactions(type);
		ApiResponse res = new ApiResponse();
		if (getTxnsList != null) {
			res.setMessage("Data fetched succesfully");
			res.setData(getTxnsList);
			res.setStatus(true);
		} else {
			res.setMessage("Data not found");
			res.setData(null);
			res.setStatus(false);
		}
		return ResponseEntity.ok(res);
	}
		
}
