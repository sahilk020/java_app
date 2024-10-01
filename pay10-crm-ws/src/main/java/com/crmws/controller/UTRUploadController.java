package com.crmws.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.crmws.entity.ResponseMessage;
import com.crmws.exception.DateValidationException;
import com.crmws.service.DMSService;
import com.crmws.service.UTRUploadService;

@CrossOrigin
@RestController
public class UTRUploadController {

	private static final Logger logger = LoggerFactory.getLogger(UTRUploadController.class.getName());

	@Autowired
	private UTRUploadService utrUploadService;
	private String message = "";
	private boolean dateFormatStatus = true;

	@PostMapping("/bulkUploadUTR")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {

		try {
			message = "";
			dateFormatStatus = true;
			validateDateInFile(file);
			if (dateFormatStatus) {
				if (utrUploadService.save(file).equalsIgnoreCase("Sucesss")) {
					message = "UTR file uploaded successfully: " + file.getOriginalFilename();
				} else {
					message = "UTR file not uploaded successfully : " + file.getOriginalFilename();
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(message, HttpStatus.OK)));
		} catch (Exception e) {
			logger.info("exception while uploading={}" + e);
			e.printStackTrace();
			message = "Could not upload the file error: " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(new ResponseMessage("UTR file not uploaded successfully : " + file.getOriginalFilename(),
							HttpStatus.EXPECTATION_FAILED));
		}

	}
	
	@PostMapping("/bulkChargebackUtr/{type}")
	public ResponseEntity<ResponseMessage> bulkChargebackUtr(@RequestParam("file") MultipartFile file ,@PathVariable String type) {

		try {
			message = "";
			dateFormatStatus = true;
			validateDateInFile(file);
			if (dateFormatStatus) {
				if (utrUploadService.saveChargeback(file,type).equalsIgnoreCase("Sucesss")) {
					message = "UTR file uploaded successfully: " + file.getOriginalFilename();
				} else {
					message = "UTR file not uploaded successfully : " + file.getOriginalFilename();
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(message, HttpStatus.OK)));
		} catch (Exception e) {
			logger.info("exception while uploading={}" + e);
			e.printStackTrace();
			message = "Could not upload the file error: " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(new ResponseMessage("UTR file not uploaded successfully : " + file.getOriginalFilename(),
							HttpStatus.EXPECTATION_FAILED));
		}

	}

	private boolean validateDateInFile(MultipartFile file) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			workbook.forEach(sheet -> {
				System.out.println("=> sheet " + sheet.getSheetName());
				Map<Integer, String> map = new HashMap<Integer, String>();
				Cell currentCell = null;
				for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
					Row currentRow = sheet.getRow(i);
					int cellsInRow = currentRow != null ? currentRow.getPhysicalNumberOfCells() : 0;
					if (i == 0) {
						for (int j = 0; j < cellsInRow; j++) {
							currentCell = currentRow.getCell(j);
							if (currentCell != null)
								map.put(currentCell.getColumnIndex(), currentCell.getStringCellValue());
						}
					} else if (cellsInRow > 0) {
						for (Integer key : map.keySet()) {
							currentCell = currentRow.getCell(key);
							if (currentCell != null) {
								if (map.get(key).equalsIgnoreCase("UTR_Date")) {
									DataFormatter dataFormatter = new DataFormatter();
									Cell cellValue = currentRow.getCell(currentCell.getColumnIndex());
									String formattedCellStr = dataFormatter.formatCellValue(cellValue);
									if ((formattedCellStr != null && formattedCellStr.length() > 0)
											&& !dateValidator(formattedCellStr)) {
										message = "Date Format Not Valid (Date Format Should be in yyyy-MM-dd HH:MM:SS example: 2023-09-26 12:23:23)";
										dateFormatStatus = false;
									}
								}
							}
						}
					}
				}

			});
		} catch (Exception e) {
			dateFormatStatus = false;
		}
		return dateFormatStatus;
	}


	private boolean dateValidator(String UTRDate) {
		Date date = new Date();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setLenient(false);
		try {
			date = sdf.parse(UTRDate);
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

}