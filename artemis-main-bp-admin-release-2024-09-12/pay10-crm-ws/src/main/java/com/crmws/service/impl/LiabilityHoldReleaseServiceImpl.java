
package com.crmws.service.impl;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.service.LiabilityHoldReleaseService;
import com.google.gson.Gson;
import com.pay10.commons.api.VelocityEmailer;
import com.pay10.commons.dto.BulkUploadLiabilityHoldAndRelease;
import com.pay10.commons.entity.LiabilityPgRefNumbersEntity;
import com.pay10.commons.repository.LiabilityHoldAndRelease;
import com.pay10.commons.repository.LiabilityPgRefNumbers;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;


@Service
public class LiabilityHoldReleaseServiceImpl implements LiabilityHoldReleaseService {
	@Autowired
	private PropertiesManager propertiesManager;
	private static final Logger logger = LoggerFactory.getLogger(LiabilityHoldReleaseServiceImpl.class.getName());
	final String liabilityMailTo = PropertiesManager.propertiesMap.get(Constants.LIABILITYMAILTO.getValue());
	@Autowired
	private MongoTransactionDetails txnDetails;

	@Autowired
	private LiabilityPgRefNumbers liabilityPgRefNumbers;

	@Autowired
	private VelocityEmailer velocityEmailer;
	
	@Autowired
	private MongoTransactionDetails mongoTransactionDetails;

	@Autowired
	LiabilityHoldAndRelease liabilityHoldAndRelease;
	
	@SuppressWarnings("deprecation")
	@Override
	public void hold(List<?> pgrefNum,String remarks,String user) {
		try {
			
			List<String>pgRefnum=new ArrayList<>();
			logger.info("inside service holdTransactions");

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			List<BulkUploadLiabilityHoldAndRelease>bulkUploadLiabilityHoldAndReleases=new ArrayList<>();
			pgrefNum.stream().forEach(i -> {
				pgRefnum.add(i.toString());
				BulkUploadLiabilityHoldAndRelease holdAndRelease = new BulkUploadLiabilityHoldAndRelease();
				holdAndRelease.setPgRefNum(i.toString());
				holdAndRelease.setStatus("0");
				holdAndRelease.setErrorMsg(null);
				holdAndRelease.setLiabilityType("HOLD");
				holdAndRelease.setLiablityDateIndex(formatter.format(date));
				holdAndRelease.setDateIndex(formatter1.format(date));
				holdAndRelease.setRemarks(remarks);
				holdAndRelease.setUpdatedat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
				holdAndRelease.setUpdatedby(user);
				
			


				

				bulkUploadLiabilityHoldAndReleases.add(holdAndRelease);
				
			});

			bulkUploadLiabilityHoldAndReleases.stream().forEach(t -> {
				LiabilityPgRefNumbersEntity entity = new LiabilityPgRefNumbersEntity();
				entity.setLiabilityType(t.getLiabilityType());
				entity.setPgRefnum(t.getPgRefNum());
				insertLibilityPgRefNumberInMySqlDb(entity);
			});

			bulkUploadLiabilityHoldAndReleases.stream().forEach(t -> {
				insertLibilityPgRefNumberInMyMongoDb(t);
			});

			liabilityHoldAndRelease.getAllStatusZero().stream().forEach(t -> {
				pgRefnum.add(t.getPgRefNum());
				UpdateLibiltiyInTransaction(t, t.getLiabilityType(),user);
			});
			
			double amount=liabilityHoldAndRelease.getTotalAmountFromPgRefNUM(pgRefnum);
			String body="Transactions are holed which are amounting to rupees"+amount;
			
			
			String subject="Transactions are holed";
			String []mailTo=liabilityMailTo.split(",");
			velocityEmailer.sendEmailWithTextAndAttachmentLiability(body,  subject,  mailTo,"Release");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error while holding transaction");
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void release(List<?> pgrefNum,String remarks,String user) {
		try {
			List<String>pgRefnum=new ArrayList<>();
			logger.info("inside service releaseTransactions");

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			List<BulkUploadLiabilityHoldAndRelease>bulkUploadLiabilityHoldAndReleases=new ArrayList<>();
			pgrefNum.stream().forEach(i -> {
				pgRefnum.add(i.toString());
				BulkUploadLiabilityHoldAndRelease holdAndRelease = new BulkUploadLiabilityHoldAndRelease();
				holdAndRelease.setPgRefNum(i.toString());
				holdAndRelease.setStatus("0");
				holdAndRelease.setErrorMsg(null);
				holdAndRelease.setLiabilityType("RELEASE");
				holdAndRelease.setLiablityDateIndex(formatter.format(date));
				holdAndRelease.setDateIndex(formatter1.format(date));
				holdAndRelease.setRemarks(remarks);
				holdAndRelease.setUpdatedby(user);
				holdAndRelease.setUpdatedat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
				
				



				
				bulkUploadLiabilityHoldAndReleases.add(holdAndRelease);
			});
			
			bulkUploadLiabilityHoldAndReleases.stream().forEach(t -> {
				LiabilityPgRefNumbersEntity entity = new LiabilityPgRefNumbersEntity();
				entity.setLiabilityType(t.getLiabilityType());
				entity.setPgRefnum(t.getPgRefNum());
				insertLibilityPgRefNumberInMySqlDb(entity);
			});

			bulkUploadLiabilityHoldAndReleases.stream().forEach(t -> {
				insertLibilityPgRefNumberInMyMongoDb(t);
			});

			liabilityHoldAndRelease.getAllStatusZero().stream().forEach(t -> {
				pgRefnum.add(t.getPgRefNum());
				UpdateLibiltiyInTransaction(t, t.getLiabilityType(),user);
			});
			
			
			
			
			double amount=liabilityHoldAndRelease.getTotalAmountFromPgRefNUM(pgRefnum);
			String body="Transactions are holed which are amounting to rupees"+amount;
			String subject="Transactions are released";
			
			String []mailTo=liabilityMailTo.split(",");
			velocityEmailer.sendEmailWithTextAndAttachmentLiability(body,  subject,  mailTo,"Release");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error while releasing transaction");
		}
	}

	@Override
	public String bulkUploadLiablityHoldAndRelease(MultipartFile formData, String type,String user) {
		String status = "Successfully Uploaded File";
		try {
			
			List<String>pgRefnum=new ArrayList<>();
			List<BulkUploadLiabilityHoldAndRelease> bulkUploadLiabilityHoldAndReleases = new ArrayList<>();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();

			XSSFWorkbook workbook = new XSSFWorkbook(formData.getInputStream());
			logger.info("=> sheet reading file");

			workbook.forEach(sheet -> {
				System.out.println("=> sheet " + sheet.getSheetName());
				Map<Integer, String> map = new HashMap<Integer, String>();
				Cell currentCell = null;
				for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
					String txnId = null;
					String remarks = null;
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
								if (map.get(key).equalsIgnoreCase("PG REF NUM")) {
									if (currentCell.getCellTypeEnum() == CellType.STRING) {
										txnId = String.valueOf(currentRow.getCell(key).getStringCellValue());
									} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
										txnId = String.valueOf(currentRow.getCell(key).getNumericCellValue() + "");
										if (txnId != null && txnId.length() > 0 && txnId.contains("E")) {
											BigDecimal bddouble = new BigDecimal(txnId);
											txnId = "" + bddouble.longValue();
										}
									}
								}
								if (map.get(key).equalsIgnoreCase("REMARKS")) {
									if (currentCell.getCellTypeEnum() == CellType.STRING) {
										remarks = String.valueOf(currentRow.getCell(key).getStringCellValue());
									} 
								}

							}
						}
						logger.info("Update PG Ref Num details :" + txnId);
						if (txnId != null && txnId.trim().length() > 0 && remarks != null && remarks.trim().length() > 0) {
							pgRefnum.add(txnId);
							BulkUploadLiabilityHoldAndRelease holdAndRelease = new BulkUploadLiabilityHoldAndRelease();
							holdAndRelease.setPgRefNum(txnId);
							holdAndRelease.setStatus(null);
							holdAndRelease.setErrorMsg(null);
							holdAndRelease.setLiabilityType(type);
							holdAndRelease.setLiablityDateIndex(formatter.format(date));
							holdAndRelease.setDateIndex(formatter1.format(date));
							holdAndRelease.setRemarks(remarks);
							holdAndRelease.setUpdatedby(user);
							holdAndRelease.setUpdatedat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()));
							


							
							bulkUploadLiabilityHoldAndReleases.add(holdAndRelease);
						}
					}
				}
			});

			bulkUploadLiabilityHoldAndReleases.stream().forEach(t -> {
				LiabilityPgRefNumbersEntity entity = new LiabilityPgRefNumbersEntity();
				entity.setLiabilityType(t.getLiabilityType());
				entity.setPgRefnum(t.getPgRefNum());
				insertLibilityPgRefNumberInMySqlDb(entity);
			});

			bulkUploadLiabilityHoldAndReleases.stream().forEach(t -> {
				insertLibilityPgRefNumberInMyMongoDb(t);
			});

			liabilityHoldAndRelease.getAllStatusZero().stream().forEach(t -> {
				pgRefnum.add(t.getPgRefNum());
				UpdateLibiltiyInTransaction(t, t.getLiabilityType(),user);
			});

			double amount=liabilityHoldAndRelease.getTotalAmountFromPgRefNUM(pgRefnum);
			String body="Transactions are "+type.toLowerCase()+" which are amounting to rupees "+amount;
			String subject="Transactions are "+type.toLowerCase();
			String []mailTo=liabilityMailTo.split(",");
			
			velocityEmailer.sendEmailWithTextAndAttachmentLiability(body,  subject,  mailTo,type);
		} catch (Exception e) {
			logger.info("Exception Occur in : ", e);
			e.printStackTrace();
			status = "Failed To Upload File";
		}
		return status;
	}

	public void insertLibilityPgRefNumberInMySqlDb(LiabilityPgRefNumbersEntity liabilityPgRefNumbersEntity) {
		logger.info("Inside insertLibilityPgRefNumberInMySqlDb() insertion is inprocess and parameters are "
				+ new Gson().toJson(liabilityPgRefNumbersEntity));

		liabilityPgRefNumbers.save(liabilityPgRefNumbersEntity);
		logger.info("Inside insertLibilityPgRefNumberInMySqlDb() insertion is Completed");
	}

	public void insertLibilityPgRefNumberInMyMongoDb(
			BulkUploadLiabilityHoldAndRelease bulkUploadLiabilityHoldAndReleases) {
		logger.info("Inside  insertLibilityPgRefNumberInMyMongoDb() and parameter are : "
				+ new Gson().toJson(bulkUploadLiabilityHoldAndReleases));
		String status = liabilityHoldAndRelease.insert(bulkUploadLiabilityHoldAndReleases);
		logger.info("Inside  insertLibilityPgRefNumberInMyMongoDb() : " + status);
	}

	public void UpdateLibiltiyInTransaction(BulkUploadLiabilityHoldAndRelease bulkUploadLiabilityHoldAndReleases,
			String type,String user) {

		logger.info(
				"Inside UpdateLibiltiyInTransaction updatation in transaction and transaction Satus is inprocess and parameter are "
						+ new Gson().toJson(bulkUploadLiabilityHoldAndReleases) + "\ttype :" + type);
		String response = mongoTransactionDetails.holdReleaseTransaction(
				bulkUploadLiabilityHoldAndReleases.getPgRefNum(),
				bulkUploadLiabilityHoldAndReleases.getLiabilityType(),bulkUploadLiabilityHoldAndReleases.getRemarks(),user);
		logger.info("Inside UpdateLibiltiyInTransaction updatation is inprocess");
		long status = liabilityHoldAndRelease.update(bulkUploadLiabilityHoldAndReleases.getPgRefNum(), response, type,user);

		logger.info("Inside UpdateLibiltiyInTransaction updatation is completed : " + status);

	}
	
	

}
