package com.crmws.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.dao.BulkRefundRepository;
import com.crmws.dto.ApiResponse;
import com.crmws.entity.BulkRefundEntity;
import com.crmws.entity.IrctcRefundEntity;
import com.crmws.service.IrctcReundService;
import com.crmws.service.MyFileNotFoundException;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;

@CrossOrigin
@RestController
@RequestMapping("/irctc")
public class IrctcRefundController {
	private static final Logger logger = LoggerFactory.getLogger(IrctcRefundController.class.getName());

	@Autowired
	BulkRefundRepository bulkRefundRepository;

	@Autowired
	IrctcReundService irctcReundService;

	@Autowired
	private FieldsDao fieldsDao;

	@GetMapping("/refund")
	public String IrctcRefundqqq() {
		logger.info("Request Received For IrctcRefundFileUploadeBulk");

		return "success";
	}

	@PostMapping("/refund/search")
	public ApiResponse refundSearch(@RequestParam String dateFrom, @RequestParam String FileName,
			@RequestParam String refundType, @RequestParam String canorderId, @RequestParam String orderId,
			@RequestParam String pgRefNum) {

		ApiResponse apiResponse = new ApiResponse();
		
		List<Map> data = irctcReundService.refundSearch(dateFrom, FileName, refundType, canorderId, orderId, pgRefNum,"R");

		if (!data.isEmpty()) {
			apiResponse.setMessage("Data Fetch Successfully");
			apiResponse.setData(data);
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("No Data Found");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}
	
	@PostMapping("/nget/refund/search")
	public ApiResponse NgetRefundSearch(@RequestParam String dateFrom, @RequestParam String FileName,
			@RequestParam String refundType, @RequestParam String canorderId, @RequestParam String orderId,
			@RequestParam String pgRefNum) {

		ApiResponse apiResponse = new ApiResponse();

		List<Map> data = irctcReundService.refundSearch(dateFrom, FileName, refundType, canorderId, orderId, pgRefNum,"N");

		if (!data.isEmpty()) {
			apiResponse.setMessage("Data Fetch Successfully");
			apiResponse.setData(data);
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("No Data Found");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}

	@PostMapping("/refund/upload/bulk")
	public String IrctcRefundFileUploadeBulk(@RequestParam("file") MultipartFile file, @RequestParam String email) {
		logger.info("Request Received For IrctcRefundFileUploadeBulk");
		try {
			StringBuffer respMsg = new StringBuffer();
			StringBuffer fileDate = new StringBuffer();
			boolean validationResp = irctcReundService.validateIrctcRefundFile(file, email, respMsg, "refund_", fileDate);
			logger.info(validationResp + " " + respMsg);
			if (validationResp) {
				return respMsg.toString();
			}

			String filename = file.getOriginalFilename();
			String irctcRefundFileLocation = "/home/radhey/RR/refundFiles/irctc/";
			boolean fileUpload = irctcReundService.fileStore(file, filename, irctcRefundFileLocation);

			// String fileResponse = irctcReundService.readFile();
			try (BufferedReader in = new BufferedReader(new FileReader(irctcRefundFileLocation + filename))) {
				String line;
				int cnt = 0;
				while ((line = in.readLine()) != null) {

					String[] pair = line.split("\\|", -1);
					System.out.println("Line Number " + cnt);

					System.out.println("pair " + pair[0]);
					System.out.println("pair " + pair[1]);
					System.out.println("pair " + pair[2]);
					System.out.println("pair " + pair[3]);
					System.out.println("pair " + pair[4]);
					System.out.println("pair " + pair[5]);
					System.out.println("pair " + pair[6]);

					IrctcRefundEntity irctcRefundEntity = new IrctcRefundEntity();

					irctcRefundEntity.setOrderId(pair[0]);
					irctcRefundEntity.setRefundFlag(pair[1]);
					irctcRefundEntity.setAmount(pair[2]);
					irctcRefundEntity.setPgRefNO(pair[3]);
					irctcRefundEntity.setBankRefNum(pair[3]);
					irctcRefundEntity.setTotalAmount(pair[5]);
					irctcRefundEntity.setCurrencyCode("356");
					irctcRefundEntity.setFileDate(fileDate.toString());
					irctcRefundEntity.setTransactionType(TransactionType.REFUND.getName());
					// irctcRefundEntity.setPayId(row.getCell(3) != null ? row.getCell(3).toString()
					// : "");
					irctcRefundEntity.setRefundFileType("R");
					irctcRefundEntity.setIrctcRefundCancelledDate(pair[4]);
					irctcRefundEntity.setIrctcRefundCancelledId(pair[6]);
					irctcRefundEntity.setCreateDate(getCurrentDate());
					irctcRefundEntity
							.setRefundOrderId(String.valueOf(Math.abs(new Random().nextLong()) % 10000000000000000L));
					irctcRefundEntity.setHash(Hasher.getHash(TransactionManager.getNewTransactionId()));
					irctcRefundEntity.setFileName(filename);
					irctcRefundEntity.setCreateBy(email);

					bulkRefundRepository.saveIrctcBulkRefund(irctcRefundEntity);
					cnt++;
				}
			} catch (Exception e) {
				System.out.println("Exception " + e);
				return "";
			}

			return "file upload successfully";

		} catch (Exception e) {
			logger.error("exception in file uploading " + e);
			e.printStackTrace();
			return "File Fail to upload";
		}

	}

	@PostMapping("/nget/refund/upload/bulk")
	public String IrctcNgetRefundFileUploadeBulk(@RequestParam("file") MultipartFile file, @RequestParam String email) {
		logger.info("Request Received For IrctcNgetRefundFileUploadeBulk");
		try {
			StringBuffer respMsg = new StringBuffer();
			StringBuffer fileDate = new StringBuffer();
			boolean validationResp = irctcReundService.validateIrctcRefundFile(file, email, respMsg, "ngetdeltarefund_", fileDate);
			logger.info(validationResp + " " + respMsg);
			if (validationResp) {
				return respMsg.toString();
			}

			String filename = file.getOriginalFilename();
			String irctcRefundFileLocation = "/home/radhey/RR/refundFiles/irctc/";
			boolean fileUpload = irctcReundService.fileStore(file, filename, irctcRefundFileLocation);

			// String fileResponse = irctcReundService.readFile();
			try (BufferedReader in = new BufferedReader(new FileReader(irctcRefundFileLocation + filename))) {
				String line;
				int cnt = 0;
				while ((line = in.readLine()) != null) {

					String[] pair = line.split("\\|", -1);
					System.out.println("Line Number " + cnt);

					logger.info("pair " + pair[0]);
					logger.info("pair " + pair[1]);
					logger.info("pair " + pair[2]);
					logger.info("pair " + pair[3]);
					logger.info("pair " + pair[4]);
					logger.info("pair " + pair[5]);
					logger.info("pair " + pair[6]);

					IrctcRefundEntity irctcRefundEntity = new IrctcRefundEntity();

					irctcRefundEntity.setOrderId(pair[0]);
					irctcRefundEntity.setRefundFlag(pair[1]);
					irctcRefundEntity.setAmount(pair[2]);
					irctcRefundEntity.setPgRefNO(pair[3]);
					irctcRefundEntity.setBankRefNum(pair[3]);
					irctcRefundEntity.setTotalAmount(pair[5]);
					irctcRefundEntity.setCurrencyCode("356");
					irctcRefundEntity.setTransactionType(TransactionType.REFUND.getName());
					// irctcRefundEntity.setPayId(row.getCell(3) != null ? row.getCell(3).toString()
					// : "");
					irctcRefundEntity.setRefundFileType("N");
					irctcRefundEntity.setIrctcRefundCancelledDate(pair[4]);
					irctcRefundEntity.setIrctcRefundCancelledId(pair[6]);
					irctcRefundEntity.setCreateDate(getCurrentDate());
					irctcRefundEntity
							.setRefundOrderId(String.valueOf(Math.abs(new Random().nextLong()) % 10000000000000000L));
					irctcRefundEntity.setHash(Hasher.getHash(TransactionManager.getNewTransactionId()));
					irctcRefundEntity.setFileName(filename);
					irctcRefundEntity.setCreateBy(email);

					bulkRefundRepository.saveIrctcBulkRefund(irctcRefundEntity);
					cnt++;
				}
			} catch (Exception e) {
				logger.info("Exception " + e);
				return "";
			}

			return "file upload successfully";

		} catch (Exception e) {
			logger.info("exception in file uploading " + e);
			e.printStackTrace();
			return "File Fail to upload";
		}

	}

	
	@GetMapping("/refund/downloadFile")
	public ResponseEntity<Resource> irctcRefundDownload(@RequestParam String fileName, HttpServletRequest request) {
		logger.info("Request Received for download irctc refund validate file, fileName = " + fileName);
		String filePath = "/home/radhey/RR/refundFiles/irctc/download/" + fileName; //"/home/Properties/refundFiles/" + fileName;
		
		Resource resource = loadFileAsResource(filePath);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	@GetMapping("/nget/refund/downloadFile")
	public ResponseEntity<Resource> downloadFile(@RequestParam String fileName, HttpServletRequest request) {
		logger.info("Request Received for download irctc refund validate file, fileName = " + fileName);
		String filePath = "/home/radhey/RR/refundFiles/irctc/download/" + fileName; //"/home/Properties/refundFiles/" + fileName;
		
		Resource resource = loadFileAsResource(filePath);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	private Resource loadFileAsResource(String fileName) {
		logger.info("Download File Path : " + fileName);
		Path filePath = Paths.get(fileName);
		Resource resource = null;
		try {
			resource = new UrlResource(filePath.toUri());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (resource.exists()) {
			return resource;
		} else {
			throw new MyFileNotFoundException("File not found " + fileName);
		}
	}
	
	public String getCurrentDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

}
