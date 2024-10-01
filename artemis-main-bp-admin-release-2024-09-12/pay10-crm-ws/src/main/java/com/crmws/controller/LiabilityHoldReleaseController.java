package com.crmws.controller;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.dto.ChargeBackAutoPopulateData;
import com.crmws.dto.DMSdto;
import com.crmws.dto.LiabilityHoldReleaseWrapper;
import com.crmws.dto.LiabilityHoldWrapper;
import com.crmws.entity.ResponseMessage;
import com.crmws.exception.DateValidationException;
import com.crmws.service.DMSService;
import com.crmws.service.LiabilityHoldReleaseService;
import com.crmws.service.UTRUploadService;
import com.ibm.icu.math.BigDecimal;

@CrossOrigin
@RestController
public class LiabilityHoldReleaseController {

	private static final Logger logger = LoggerFactory.getLogger(LiabilityHoldReleaseController.class.getName());

	@Autowired
	private LiabilityHoldReleaseService liabilityHoldReleaseService;

	@GetMapping("/testLiabilityHoldReleaseController")
	public ResponseEntity<ResponseMessage> test() {

		return ResponseEntity.status(HttpStatus.OK)
				.body((new ResponseMessage("LiabilityHoldReleaseController woking!!!!!", HttpStatus.OK)));
	}

	@PostMapping("/holdTransactions/{remarks}/{user}")
	public ResponseEntity<ResponseMessage> hold(@RequestBody LiabilityHoldWrapper pgrefNum ,@PathVariable String remarks,@PathVariable String user) {

		String message = "";

		try {
			pgrefNum.getPgrefno().stream().forEach(i -> {
				System.out.print("Transaction hold sucessfully PGREF number:" + i);
			});
			liabilityHoldReleaseService.hold(pgrefNum.getPgrefno(),remarks,user);

			message = " Transaction hold sucessfully ";
			return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(message, HttpStatus.OK)));
		} catch (Exception e) {
			logger.info("exception while uploading={}" + e);
			e.printStackTrace();
			message = "Could not upload the file error";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(new ResponseMessage(e.getMessage(), HttpStatus.EXPECTATION_FAILED));
		}

	}

	@PostMapping("/releaseTransactions/{remarks}/{user}")
	public ResponseEntity<ResponseMessage> release(@RequestBody LiabilityHoldReleaseWrapper pgrefNum,@PathVariable String remarks,@PathVariable String user) {

		String message = "";

		try {
			logger.info("inside rest api calll releaseTransactions");
			pgrefNum.getPgrefno().stream().forEach(i -> {
				System.out.print("Transaction released sucessfully PGREF number:" + i);
			});
			;
			liabilityHoldReleaseService.release(pgrefNum.getPgrefno(),remarks,user);
			message = " Transaction release sucessfully ";
			return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(message, HttpStatus.OK)));
		} catch (Exception e) {
			logger.info("exception while uploading={}" + e);
			e.printStackTrace();
			message = "Could not upload the file error";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(new ResponseMessage(e.getMessage(), HttpStatus.EXPECTATION_FAILED));
		}

	}

	@PostMapping("/bulkUploadLiablityRelease/{user}")
	public ResponseEntity<ResponseMessage> bulkUploadLiablityHoldAndRelease(@RequestParam("file") MultipartFile file,@PathVariable String user) {

		String message = "";

		try {
			if (file.isEmpty() && !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsx")
					&& !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsm")
					&& !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xls")
					) {
				return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage("Please upload valid excel file .xlsx, .xlsm, .xls only.", HttpStatus.OK)));
			} else {
				message = liabilityHoldReleaseService.bulkUploadLiablityHoldAndRelease(file, "RELEASE",user);
			}

		} catch (Exception e) {

		}
		return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(message, HttpStatus.OK)));
	}

	@PostMapping("/bulkUploadLiablityHold")
	public ResponseEntity<ResponseMessage> bulkUploadLiablityHold(@RequestParam("file") MultipartFile file) {

		String message = "";

		try {
			if (file.isEmpty() && !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsx")
					&& !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsm")
					&& !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xls")
					) {
				return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage("Please upload valid excel file .xlsx, .xlsm, .xls only.", HttpStatus.OK)));
			} else {
				message = liabilityHoldReleaseService.bulkUploadLiablityHoldAndRelease(file, "HOLD","user");
			}

		} catch (Exception e) {

		}
		return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(message, HttpStatus.OK)));
	}

}