package com.crmws.controller;




import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.dto.ExceptionListWrapper;
import com.crmws.entity.ResponseMessage;
import com.crmws.entity.ResponseMessageExceptionList;
import com.crmws.service.impl.ExceptionListServiceImpl;






@CrossOrigin
@RestController
@RequestMapping("ExceptionList")
public class ExceptionList{

	private static final Logger logger = LoggerFactory.getLogger(ExceptionList.class.getName());
	
	@Autowired
	private ExceptionListServiceImpl exceptionListServiceImpl;
	
	@PostMapping("/RNS/{pgRefNum}")
	public ResponseEntity<ResponseMessageExceptionList> RNS(@PathVariable String pgRefNum) {
		ResponseMessageExceptionList message=new ResponseMessageExceptionList();
		message.setHttpStatus(HttpStatus.OK);
		if (StringUtils.isBlank(pgRefNum)) {
			message.setRespmessage("Please Select PgRefNumber");
		}else {
			message.setRespmessage(exceptionListServiceImpl.RNS(pgRefNum));
		}
		return ResponseEntity.status(message.getHttpStatus()).body(message);
	}
	
	@PostMapping("/rnsBulkUpload")
	public ResponseEntity<ResponseMessageExceptionList> rnsBulkUpload(@RequestBody ExceptionListWrapper pgRefNum) {
		ResponseMessageExceptionList message=new ResponseMessageExceptionList();
		message.setHttpStatus(HttpStatus.OK);
		if (pgRefNum.getPgrefno().size()<1) {
			message.setRespmessage("Please Select PgRefNumber");
		}else {
			message.setRespmessage(exceptionListServiceImpl.rnsBulkUpload(pgRefNum));
		}
		return ResponseEntity.status(message.getHttpStatus()).body(message);
	}
	
	@PostMapping("/Refund/{pgRefNum}")
	public ResponseEntity<ResponseMessageExceptionList> refund(@PathVariable String pgRefNum) {
		
		ResponseMessageExceptionList message=new ResponseMessageExceptionList();
		message.setHttpStatus(HttpStatus.OK);
		if (StringUtils.isBlank(pgRefNum)) {
			message.setRespmessage("Please Select PgRefNumber");
		}else {
			message.setRespmessage(exceptionListServiceImpl.refund(pgRefNum));
		}
		return ResponseEntity.status(message.getHttpStatus()).body(message);
	}
	@PostMapping("/refundbulkUpload")
	public ResponseEntity<ResponseMessageExceptionList> refundbulkUpload(@RequestBody ExceptionListWrapper pgRefNum) {
		
		ResponseMessageExceptionList message=new ResponseMessageExceptionList();
		message.setHttpStatus(HttpStatus.OK);
		if (pgRefNum.getPgrefno().size()<1) {
			message.setRespmessage("Please Select PgRefNumber");
		}else {
			Map<String,String> map=exceptionListServiceImpl.refundbulkUpload(pgRefNum);
			message.setMultipleResponse(map);
		}
		return ResponseEntity.status(message.getHttpStatus()).body(message);
	}
	
	
	@PostMapping("/riskRefund/{pgRefNum}")
	public ResponseEntity<ResponseMessageExceptionList> riskRefund(@PathVariable String pgRefNum) {
		System.out.println("Refund Request for pg_ref number : " +pgRefNum);
		ResponseMessageExceptionList message=new ResponseMessageExceptionList();
		message.setHttpStatus(HttpStatus.OK);
		if (StringUtils.isBlank(pgRefNum)) {
			message.setRespmessage("Please Select PgRefNumber");
		}else {
			message.setRespmessage(exceptionListServiceImpl.refund(pgRefNum));
		}
		return ResponseEntity.status(message.getHttpStatus()).body(message);
	}
	
	@PostMapping("/riskRefundbulkUpload")
	public ResponseEntity<ResponseMessageExceptionList> riskRefundbulkUpload(@RequestBody ExceptionListWrapper pgRefNum) {
		
		ResponseMessageExceptionList message=new ResponseMessageExceptionList();
		message.setHttpStatus(HttpStatus.OK);
		if (pgRefNum.getPgrefno().size()<1) {
			message.setRespmessage("Please Select PgRefNumber");
		}else {
			Map<String,String> map=exceptionListServiceImpl.refundbulkUpload(pgRefNum);
			message.setMultipleResponse(map);
		}
		return ResponseEntity.status(message.getHttpStatus()).body(message);
	}
	
	@PostMapping("/ExceptionListsBulkUpload/{type}")
	public ResponseEntity<ResponseMessage> chargebackStatusBulkUpload(@PathVariable String type,@RequestParam("file") MultipartFile file) {
		logger.info("Bulk Uploading Start method name ExceptionListsBulkUpload()");
		String message = "";

		try {
			if (file.isEmpty() && !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsx")
					&& !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsm")
					&& !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xls")
					) {
				return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage("Please upload valid excel file .xlsx, .xlsm, .xls only.", HttpStatus.OK)));
			}else if(StringUtils.isBlank(type)) {
				return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage("Please Select the type", HttpStatus.OK)));
			} else {
				message = exceptionListServiceImpl.exceptionListBulkUpload(file,type);
			}

		} catch (Exception e) {
			logger.error("Exception Occur in ExceptionListsBulkUpload() : ",e);
			e.printStackTrace();
		}
		logger.info("Bulk Uploading Start method name ExceptionListsBulkUpload()");
		return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(message, HttpStatus.OK)));
	}

}