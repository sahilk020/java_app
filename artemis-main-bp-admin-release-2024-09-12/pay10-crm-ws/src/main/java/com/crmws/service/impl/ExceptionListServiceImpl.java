package com.crmws.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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

import com.crmws.dto.ExceptionListWrapper;
import com.crmws.entity.ResponseMessage;
import com.crmws.service.ExceptionListService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.repository.ExceptionListRepository;
import com.pay10.commons.user.User;

@Service
public class ExceptionListServiceImpl implements ExceptionListService {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionListServiceImpl.class.getName());
	
	@Autowired
	private ExceptionListRepository exceptionListRepository;
	
	@Override
	public String RNS(String pgRefNum) {
		String status="Failed";
		try {
			status=updateRns(pgRefNum);
		} catch (Exception e) {
			status="Something Went Wrong Please try After Sometime";
			logger.error("Exception occur in RNS() : ",e);
			e.printStackTrace();
		}
		logger.info("Return Response : "+status);
		return status;
	}

	@Override
	public String refund(String pgRefNum) {
		String status="Failed";
		logger.info("Refund for pgrefNum "+pgRefNum);
		try {
			status=refundProcess(pgRefNum);
		} catch (Exception e) {
			status="Something Went Wrong Please try After Sometime";
			logger.error("Exception occur in refund() : ",e);
			e.printStackTrace();
		}
		logger.info("Return Response : "+status);
		return status;
	}

	@Override
	public Map<String,String> refundbulkUpload(ExceptionListWrapper pgRefNum) {
		Map<String,String>map=new HashMap<>();
		logger.info("list of pgRefNumber size "+pgRefNum.getPgrefno().size());
		try {
			for (String iterable_element : pgRefNum.getPgrefno()) {
				logger.info("Refund for pgrefNum "+pgRefNum);
				String response=refundProcess(iterable_element);
				map.put(iterable_element, response);
			}
		} catch (Exception e) {
			map.put("Error", "Something Went Wrong Please try After Sometime");
			logger.error("Exception occur in refundbulkUpload() : ",e);
			e.printStackTrace();
		}
		logger.info("Return Response : "+map);
		return map;
	}

	@Override
	public String rnsBulkUpload(ExceptionListWrapper pgRefNum) {
		String status="Total ";
		int count=0;
		try {
			for (String iterable_element : pgRefNum.getPgrefno()) {
				String response=updateRns(iterable_element);
				if (response.equalsIgnoreCase("Successfully RNS Marked")) {
					count++;
				}
			}
		
			status=status+" "+count+" RNS Marked";
		
		} catch (Exception e) {
			status="Something Went Wrong Please try After Sometime";
			logger.error("Exception occur in rnsBulkUpload() : ",e);
			e.printStackTrace();
		}
		logger.info("Return Response : "+status);
		return status;
	}
	

	public String updateRns(String pgRefNum) {
		return exceptionListRepository.updateRns(pgRefNum);
	}

	public String refundProcess(String pgRefNum) throws SystemException, JsonProcessingException {
		return exceptionListRepository.refundProcess(pgRefNum);
	}
	
	@Override
	public String exceptionListBulkUpload(MultipartFile formData,String type) {
		String statusBack = "Successfully Updated";
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(formData.getInputStream());
			logger.info("=> sheet reading file");
			List<String>csu=new ArrayList<>();
			workbook.forEach(sheet -> {
				System.out.println("=> sheet " + sheet.getSheetName());
				Map<Integer, String> map = new HashMap<Integer, String>();
				Cell currentCell = null;
				for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
					String txnId = null;
					String utrNo = null;
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
								if (map.get(key).equalsIgnoreCase("PG_REF_NUM")) {
									if (currentCell.getCellTypeEnum() == CellType.STRING) {
										txnId = String.valueOf(currentRow.getCell(key).getStringCellValue());
										logger.info("Update TXN details :" + txnId);
										
										if (txnId != null  && txnId.trim().length() > 0) {
											csu.add(txnId);
										}
									} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
										txnId = String.valueOf(currentRow.getCell(key).getNumericCellValue() + "");
										if (txnId != null && txnId.length() > 0 && txnId.contains("E")) {
											BigDecimal bddouble = new BigDecimal(txnId);
											txnId = "" + bddouble.longValue();
											logger.info("Update TXN details :" + txnId);
											
											if (txnId != null && txnId.trim().length() > 0) {
												csu.add(txnId);
											}
										}
									}
								}

							}
						}
						
					}
				}
			});
			
			
			if (csu.size()>0) {
				if (type.equalsIgnoreCase("RNS")) {
					int size=exceptionListRepository.checkPgRefNumberRNS(csu);
					if (size==csu.size()) {
						ExceptionListWrapper exceptionListWrapper=new ExceptionListWrapper();
						exceptionListWrapper.setPgrefno(csu);
						statusBack =rnsBulkUpload(exceptionListWrapper);
					}else {
						statusBack="Could Not find All PG_REF_NUM in our system";
					}
				}else {
					if(type.equalsIgnoreCase("REFUND")){
						int size=exceptionListRepository.checkPgRefNumberRefund(csu);
						if(size==csu.size()) {
							ExceptionListWrapper exceptionListWrapper=new ExceptionListWrapper();
							exceptionListWrapper.setPgrefno(csu);
							statusBack =rnsBulkUpload(exceptionListWrapper);
						}
					}else {
						statusBack="Please Select Type";
					}
				}
				
				
			}else {
				statusBack="Excel Sheet is Empty";
			}
			
			workbook.close();
		} catch (Exception e) {
			statusBack = "Failed To Upload";
			logger.error("Exception Occur in chargebackStatusBulkUpload : ", e);
			e.printStackTrace();
		}
		return statusBack;
	}
}
