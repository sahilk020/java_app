package com.crmws.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.dao.BulkRefundRepository;
import com.crmws.dto.ApiResponse;
import com.crmws.dto.BulkRefundCountDto;
import com.crmws.entity.BulkRefundEntity;
import com.crmws.service.MyFileNotFoundException;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.user.TransactionSearchDownloadObject;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;

@CrossOrigin
@RestController
public class RefundACKFileUploadController {

	private static final Logger logger = LoggerFactory.getLogger(RefundACKFileUploadController.class.getName());

	@Autowired
	BulkRefundRepository bulkRefundRepository;

	@Autowired
	private FieldsDao fieldsDao;

	@PostMapping("/download/refund")
	public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename,
			@RequestParam("filepath") String filepath, HttpServletResponse response) {

		try {
			logger.info("filename :: " + filename);
			logger.info("filepath :: " + filepath);
			response.setHeader("content-Disposition", "inline:filename=\"" + filename + "\"");
			InputStreamResource resource = new InputStreamResource(new FileInputStream(filepath));

			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
		} catch (Exception e) {
			logger.info("exception while downloading={}" + e);

		}
		return null;

	}

	@PostMapping("/refund/upload")
	public String RefundFileUploade(@RequestParam("file") MultipartFile file) {
		logger.info("Request Received For Refund File Upload ");

		long bytes = file.getSize();

		long kilobytes = (bytes / 1024);
		long megabytes = (kilobytes / 1024);
		logger.info("kilobytes " + kilobytes);
		logger.info("megabytes " + megabytes);
		if (megabytes > 5) {
			return "File size exceeds limit!";
		}

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		logger.info("fileName >>>>>>>> " + fileName);
		int index = fileName.lastIndexOf('.');
		String extension = "";

		if (index > 0) {
			extension = fileName.substring(index + 1);
		}
		if (!extension.equalsIgnoreCase("xlsx")) {
			return "Please Select Only xlsx File Format";
		}
		storeupload(file);
		return "File uploaded successfully";

	}

	public String storeupload(MultipartFile file) {
		logger.info("Request Received For Refund File Upload storeupload ");
		// Normalize file name
		String apppath = PropertiesManager.propertiesMap.get("refundAckFileLocation"); //"/home/Properties/refundFiles/upload/";
		String fileName = null;
		Path path = Paths.get(apppath);
		try {
			Files.createDirectories(path);
			fileName = StringUtils.cleanPath(file.getOriginalFilename());
			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = path.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			logger.info("storeFile:: file stored successfully.fileName={}", fileName);

			return fileName;
		} catch (IOException e) {
			logger.info("IOException ", e);
			e.printStackTrace();
			return fileName;
		}
	}

	@GetMapping("/refund/downloadFile")
	public ResponseEntity<Resource> downloadFile(@RequestParam String fileName, HttpServletRequest request) {

		String filePath = PropertiesManager.propertiesMap.get("refundFileLocation")+ fileName; //"/home/Properties/refundFiles/" + fileName;
		// Load file as Resource
		Resource resource = loadFileAsResource(filePath);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
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

	@PostMapping("/refund/upload/bulk")
	public String RefundFileUploadeBulk(@RequestParam("file") MultipartFile file, @RequestParam String email) {
		logger.info("Request Received For RefundFileUploadeBulk ");
		if (file != null) {
			try {
				String filename = file.getOriginalFilename();
				if (fieldsDao.findBulkRefund(filename)) {
					InputStream convFile = file.getInputStream();
					XSSFWorkbook workbook = new XSSFWorkbook(convFile);
					XSSFSheet sheet = workbook.getSheetAt(0);
					Iterator<Row> rowIterator = sheet.iterator();
					while (rowIterator.hasNext()) {
						Row row = rowIterator.next();
						Random random = new Random();
						long number = random.nextLong();
						number = Math.abs(number);
						number = number % 10000000000000000L;
						logger.info("data noumber for row" + row.getRowNum());
						if (row.getRowNum() != 0) {
							Iterator<Cell> cellIterator = row.cellIterator();

							if (org.apache.commons.lang3.StringUtils.isNotBlank(row.getCell(2).toString())) {
								BulkRefundEntity bulkRefundEntity = new BulkRefundEntity();

								bulkRefundEntity.setOrderId(row.getCell(0) != null ? row.getCell(0).toString() : "");
								bulkRefundEntity.setRefund_Flag("R");
								bulkRefundEntity.setAmount(row.getCell(1) != null ? row.getCell(1).toString() : "");
								bulkRefundEntity.setPgRefNO(row.getCell(2) != null ? row.getCell(2).toString() : "");
								bulkRefundEntity.setCurrencyCode("356");
								bulkRefundEntity.setTransactionType(TransactionType.REFUND.getName());
								bulkRefundEntity.setPayId(row.getCell(3) != null ? row.getCell(3).toString() : "");
								bulkRefundEntity.setCreateDate(getCurrentDate());
								bulkRefundEntity.setRefundOrderId(String.valueOf(number));
								bulkRefundEntity.setHash(Hasher.getHash(TransactionManager.getNewTransactionId()));
								bulkRefundEntity.setFileName(filename);
								bulkRefundEntity.setCreateBy(email);
								if (bulkRefundRepository.findBulkRefundCheck( bulkRefundEntity)) {
									if (bulkRefundRepository.findBulkRefundCheckStatus( bulkRefundEntity)) {
										
										bulkRefundEntity.setStatus("Pending");
										bulkRefundEntity.setResponseCode("000");
										bulkRefundEntity.setResponseMessage("Refund is Pending State");

										bulkRefundRepository.saveBulkRefund(bulkRefundEntity);

									}else {
										bulkRefundEntity.setStatus("FAILED");
										bulkRefundEntity.setResponseCode("026");
										bulkRefundEntity.setResponseMessage("Sale amount greater than refund amount");

										bulkRefundRepository.saveBulkRefund(bulkRefundEntity);
		
									}


								}else {
									bulkRefundEntity.setStatus("FAILED");
									bulkRefundEntity.setResponseCode("026");
									bulkRefundEntity.setResponseMessage("Duplicate Request for Refund");

									bulkRefundRepository.saveBulkRefund(bulkRefundEntity);
	
								}
							}
						}
					}
					
					return "file upload successfully";

				}else {
				return "file name is allready there";
				}
			} catch (Exception e) {
				logger.error("exception in file uploading " + e);
				e.printStackTrace();
				return "File Fail to upload";
			}
		}
		return "success";
	}

	@GetMapping("/refund/searchRefund")
	public ApiResponse searchRefund(@RequestParam String FromDate,@RequestParam String toDate ,@RequestParam String fileName,@RequestParam String status) {
		ApiResponse apiResponse = new ApiResponse();
		List<BulkRefundEntity> acquirerDTList = bulkRefundRepository.fetchBulkRefundData(FromDate,toDate,fileName,status);

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
	public String getCurrentDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
	
	@GetMapping("/refund/searchRefundCount")
	public ApiResponse searchRefundCount(@RequestParam String FromDate,@RequestParam String toDate,@RequestParam String filename ) {
		ApiResponse apiResponse = new ApiResponse();
		List<BulkRefundCountDto> acquirerDTList = bulkRefundRepository.fetchBulkRefundDataCount(FromDate,toDate,filename);

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
	
	
	
	@GetMapping("/refund/searchRefundDownload")
	public ApiResponse downloadFile(@RequestParam String FromDate,@RequestParam String toDate ,@RequestParam String fileName,@RequestParam String status, HttpServletRequest request) {
		ApiResponse apiResponse = new ApiResponse();

		try {
	 InputStream fileInputStream;
		 String filename;
		 
	
			List<BulkRefundEntity> acquirerDTList = bulkRefundRepository.fetchBulkRefundData(FromDate,toDate,fileName,status);

			
			
			SXSSFWorkbook wb = new SXSSFWorkbook(100);
			Row row;
			int rownum = 1;
			// Create a blank sheet
			Sheet sheet = wb.createSheet("Transactions Report");
			row = sheet.createRow(0);
		
			List<String> headers = getHeaders();
			for (int i = 0; i < headers.size(); i++) {
				row.createCell(i).setCellValue(headers.get(i));
			}

			if (acquirerDTList.size() < 800000) {

				for (BulkRefundEntity transactionSearch : acquirerDTList) {
					row = sheet.createRow(rownum++);
					List<Object> objArr = myCsvMethodDownloadRefund(transactionSearch);
					for (int cellnum = 0; cellnum < objArr.size(); cellnum++) {
						Cell cell = row.createCell(cellnum);
						if (objArr.get(cellnum) instanceof String)
							cell.setCellValue((String) objArr.get(cellnum));
						else if (objArr.get(cellnum) instanceof Integer)
							cell.setCellValue((Integer) objArr.get(cellnum));
					}
				}
			} else {

				row = sheet.createRow(rownum++);
				// this line creates a cell in the next column of that row
				Cell cell = row.createCell(1);
				cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
			}

				String FILE_EXTENSION = ".xlsx";
				DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
				filename = "Refund_Transactions_" + df.format(new Date()) + FILE_EXTENSION;
				File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
	            
				// this Writes the workbook
				FileOutputStream out;
					out = new FileOutputStream(file);
				
				
				wb.write(out);
				out.flush();
				out.close();
				wb.dispose();
				logger.info("File generated successfully for DownloadPaymentsReportAction"+file.getName());
			
				if (!acquirerDTList.isEmpty()) {
					apiResponse.setMessage(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
					apiResponse.setData(acquirerDTList);
					apiResponse.setStatus(true);
					return apiResponse;
				} else {
					apiResponse.setMessage("download failed");
					apiResponse.setData("");
					apiResponse.setStatus(false);
					return apiResponse;
				}
			
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return null;}
//	
//	orderId,refund_Flag,amount,pgRefNO,currencyCode,
//	transactionType,payId,createDate,status,fileName,
//	RefundOrderId,ResponseCode,ResponseMessage,CreateBy
	
	private List<Object> myCsvMethodDownloadRefund(BulkRefundEntity transactionSearch) {

		List<Object> objArray = new ArrayList<>();
		objArray.add(transactionSearch.getOrderId());
		objArray.add(transactionSearch.getRefund_Flag());
		objArray.add(transactionSearch.getAmount());
		objArray.add(transactionSearch.getPgRefNO());
		objArray.add(transactionSearch.getCurrencyCode());
		objArray.add(transactionSearch.getTransactionType());
			objArray.add(transactionSearch.getPayId());
		objArray.add(transactionSearch.getCreateDate());
		objArray.add(transactionSearch.getStatus());
		objArray.add(transactionSearch.getFileName());
		
		objArray.add(transactionSearch.getRefundOrderId());
		objArray.add(transactionSearch.getResponseCode());
		objArray.add(transactionSearch.getResponseMessage());
		objArray.add(transactionSearch.getCreateBy());
		
		return objArray;
	}

	

	private List<String> getHeaders() {
		List<String> headers = new ArrayList<>();
		headers.add("orderId");
		headers.add("refund_Flag");
		headers.add("amount");
		headers.add("pgRefNO");
		headers.add("currencyCode");
		headers.add("transactionType");
		headers.add("payId");
		headers.add("createDate");
		headers.add("status");
		headers.add("fileName");
		headers.add("RefundOrderId");
		headers.add("ResponseCode");
		headers.add("ResponseMessage");
		headers.add("CreateBy");
return headers;
	}


}
