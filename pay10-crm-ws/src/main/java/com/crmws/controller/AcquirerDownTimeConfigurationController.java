package com.crmws.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.ApiResponse;
import com.crmws.service.AcquirerDTConfigurationService;
import com.pay10.commons.user.AcquirerDownTimeConfiguration;

@CrossOrigin
@RestController
@RequestMapping("/acquirer/downtime/configuration")
public class AcquirerDownTimeConfigurationController {
	private static final Logger logger = LoggerFactory
			.getLogger(AcquirerDownTimeConfigurationController.class.getName());

	@Autowired
	AcquirerDTConfigurationService acquirerDTConfigurationService;

	@PostMapping("/add")
	public ApiResponse addAcqDownTimeConfiguration(@RequestBody Map<String, String> acqDownTimeReq) {
		logger.info("Request Received For Add addAcqDownTimeConfiguration RequestInfo={}", acqDownTimeReq);
		ApiResponse apiResponse = new ApiResponse();
		String resp = validateRequest(acqDownTimeReq);
		if (resp != null) {
			apiResponse.setMessage(resp);
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
		boolean checkStatus = acquirerDTConfigurationService.checkDuplicate(acqDownTimeReq);

		if (checkStatus) {
			apiResponse.setMessage("Duplicate Request Received For Acquirer DownTime Configuration");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}

		boolean result = acquirerDTConfigurationService.addAcqDTConfig(acqDownTimeReq);
		if (result) {
			apiResponse.setMessage("Acquirer DownTime Configuration Added Successfully");
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

	@GetMapping("/list")
	public ApiResponse acqDownTimeConfigurationList() {
		logger.info("Request Received For Add acqDownTimeConfigurationList ");
		ApiResponse apiResponse = new ApiResponse();

		List<AcquirerDownTimeConfiguration> acquirerDTList = acquirerDTConfigurationService.fetchAcquirerDTConfigList();

		if (!acquirerDTList.isEmpty()) {
			apiResponse.setMessage("Data Fetch Successfully");
			apiResponse.setData(acquirerDTList);
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("No Data Found");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}

	@PostMapping("/listSearch")
	public ApiResponse acqDownTimeConfigurationSearch(@RequestParam String acquirer, @RequestParam String payment) {
		logger.info("Request Received For Search acqDownTimeConfigurationSearch " + acquirer + payment);

		ApiResponse apiResponse = new ApiResponse();

		List<AcquirerDownTimeConfiguration> acquirerDTList = acquirerDTConfigurationService
				.fetchAcquirerDTConfigListSearch(acquirer, payment);

		if (!acquirerDTList.isEmpty()) {
			apiResponse.setMessage("Data Fetch Successfully");
			apiResponse.setData(acquirerDTList);
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("No Data Found");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}

	@PostMapping("/update")
	public ApiResponse updateAcqDownTimeConfiguration(@RequestBody Map<String, String> acqDownTimeReq) {
		logger.info("Request Received For Add updateAcqDownTimeConfiguration RequestInfo={}", acqDownTimeReq);

		ApiResponse apiResponse = new ApiResponse();
		String resp = validateRequest(acqDownTimeReq);
		if (resp != null) {
			apiResponse.setMessage(resp);
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
		boolean checkStatus = acquirerDTConfigurationService.checkDuplicateById(acqDownTimeReq);

		if (!checkStatus) {
			apiResponse.setMessage("Invalid Request Received For Acquirer DownTime Updataion");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}

		boolean result = acquirerDTConfigurationService.updateAcqDTConfig(acqDownTimeReq);
		if (result) {
			apiResponse.setMessage("Acquirer DownTime Configuration Updated Successfully");
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

	@PostMapping("/findDowntimerule")
	public ApiResponse findAcqDownTimeConfiguration() {

		logger.info("Request Received For Add updateAcqDownTimeConfiguration RequestInfo={}");
		return null;

	}

	@DeleteMapping("/inactive/{acquirerRuleId}")
	public ApiResponse deleteDownTimeConfigurationList(@PathVariable String acquirerRuleId) {
		logger.info("Request Received For Add deleteDownTimeConfigurationList " + acquirerRuleId);
		ApiResponse apiResponse = new ApiResponse();
		if (!isNumeric(acquirerRuleId)) {
			apiResponse.setMessage("AcquirerRuleId should be Numeric value only.");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
		boolean resp = acquirerDTConfigurationService.checkDuplicateRuleId(acquirerRuleId);
		if (!resp) {
			apiResponse.setMessage("AcquirerRuleId not available for deletion operation");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
		boolean result = acquirerDTConfigurationService.inactiveAcqDTConfigRule(acquirerRuleId);
		if (result) {
			apiResponse.setMessage("Acquirer DownTime Configuration Rule Deleted Successfully");
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

	public String validateRequest(Map<String, String> acqDownTimeReq) {
		if (!acqDownTimeReq.containsKey("acquirerName")) {
			return "AcquirerName should be mandatory fields";
		}
		if (acqDownTimeReq.containsKey("acquirerName")
				&& (acqDownTimeReq.get("acquirerName") == null || acqDownTimeReq.get("acquirerName").isEmpty())) {
			return "AcquirerName should not be null & empty";
		}
		if (!acqDownTimeReq.containsKey("paymentType")) {
			return "PaymentType should be mandatory fields";
		}
		if (acqDownTimeReq.containsKey("paymentType")
				&& (acqDownTimeReq.get("paymentType") == null || acqDownTimeReq.get("paymentType").isEmpty())) {
			return "PaymentType should not be null & empty";
		}
		if (!acqDownTimeReq.containsKey("failedCount")) {
			return "FailedCount should be mandatory fields";
		}
		if (acqDownTimeReq.containsKey("failedCount")
				&& (acqDownTimeReq.get("failedCount") == null || acqDownTimeReq.get("failedCount").isEmpty())) {
			return "FailedCount should not be null & empty";
		}
		if (!acqDownTimeReq.containsKey("timeSlab")) {
			return "TimeSlab should be mandatory fields";
		}
		if (acqDownTimeReq.containsKey("timeSlab")
				&& (acqDownTimeReq.get("timeSlab") == null || acqDownTimeReq.get("timeSlab").isEmpty())) {
			return "TimeSlab should not be null & empty";
		}
		return null;
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
