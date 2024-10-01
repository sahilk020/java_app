package com.crmws.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crmws.dto.ChargeBackAutoPopulateData;
import com.pay10.commons.repository.DMSEntity;

public class ExcelHelper {
	private static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class.getName());

	@SuppressWarnings("deprecation")
	public static List<DMSEntity> excelUpload(InputStream inputStream) {
		List<DMSEntity> dmsEntity = new ArrayList<DMSEntity>();

		try {

			Workbook workbook = new XSSFWorkbook(inputStream);
			workbook.forEach(sheet -> {
				System.out.println("=> sheet " + sheet.getSheetName());

				Map<Integer, String> map = new HashMap<Integer, String>();
				Cell currentCell = null;
				for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
					DMSEntity entity = new DMSEntity();
					String pgRefNo = null;
					// String cbReason = null;
					String cbReasonCode = null;
					// String cbAmount = null;
					String pgCaseId = null;
//					String cbIntimationDate = null;
					String cbDeadlineDate = null;
					String nemail = null;
					String notificationEmail = null;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					logger.info("new line " + i);
					Row currentRow = sheet.getRow(i);
					int cellsInRow = currentRow != null ? currentRow.getPhysicalNumberOfCells() : 0;
					if (i == 0) {
						for (int j = 0; j < cellsInRow; j++) {
							currentCell = currentRow.getCell(j);
							logger.info("currentCell...." + currentCell);
							if (currentCell != null)
								map.put(currentCell.getColumnIndex(), currentCell.getStringCellValue());
						}

					}

					else if (cellsInRow > 0) {
						for (Integer key : map.keySet()) {
							currentCell = currentRow.getCell(key);
							logger.info("else Condition...." + currentCell);
							logger.info("" + currentCell.getCellTypeEnum().name());
							if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
								if (map.get(key).equalsIgnoreCase("pg_ref_no")) {
									pgRefNo = String.valueOf(currentRow.getCell(key).getNumericCellValue());
									if (pgRefNo.contains("E")) {
										BigDecimal bddouble = new BigDecimal(pgRefNo);
										pgRefNo = "" + bddouble.longValue();
									}
								} else if (map.get(key).equalsIgnoreCase("pg_case_id")) {
									pgCaseId = String.valueOf(currentRow.getCell(key).getNumericCellValue());

								} else if (map.get(key).equalsIgnoreCase("cb_reason_code")) {
									cbReasonCode = String.valueOf((int)currentRow.getCell(key).getNumericCellValue());
//								} else if (map.get(key).equalsIgnoreCase("cb_amount")) {
//									cbAmount = String.valueOf(currentRow.getCell(key).getNumericCellValue());
//									if (cbAmount.contains("E")) {
//										BigDecimal bddouble = new BigDecimal(cbAmount);
//										cbAmount = "" + bddouble.longValue();
//									}
								}
//								else if (map.get(key).equalsIgnoreCase("cb_reason")) {
//									cbReason = String.valueOf(currentRow.getCell(key).getNumericCellValue());
//
//								} else if (map.get(key).equalsIgnoreCase("cb_intimation_date")) {
//
//								    Date iDate = currentRow.getCell(key).getDateCellValue();
//									cbIntimationDate=sdf.format(iDate);
//									logger.info("intimation date in numeric type..." + cbIntimationDate);
//								
//								}
								else if (map.get(key).equalsIgnoreCase("cb_deadline_date")) {

									Date date = currentRow.getCell(key).getDateCellValue();
									cbDeadlineDate = sdf.format(date);
									logger.info("cbDeadlineDate date in numeric type..." + cbDeadlineDate);

								} else if (map.get(key).equalsIgnoreCase("notification_email")) {
									notificationEmail = String.valueOf(currentRow.getCell(key).getNumericCellValue());
									logger.info("cbDeadlineDate date in numeric type..." + notificationEmail);
								}

							} else if (currentCell.getCellTypeEnum() == CellType.STRING) {

								if (map.get(key).equalsIgnoreCase("pg_ref_no")) {
									pgRefNo = String.valueOf(currentRow.getCell(key).getStringCellValue());
									if (pgRefNo.contains("E")) {
										BigDecimal bddouble = new BigDecimal(pgRefNo);
										pgRefNo = "" + bddouble.longValue();
									}
								} else if (map.get(key).equalsIgnoreCase("pg_case_id")) {
									pgCaseId = String.valueOf(currentRow.getCell(key).getStringCellValue());

								} else if (map.get(key).equalsIgnoreCase("cb_reason_code")) {
									cbReasonCode = String.valueOf(currentRow.getCell(key).getStringCellValue());
//								} else if (map.get(key).equalsIgnoreCase("cb_amount")) {
//									cbAmount = String.valueOf(currentRow.getCell(key).getStringCellValue());
//									if (cbAmount.contains("E")) {
//										BigDecimal bddouble = new BigDecimal(cbAmount);
//										cbAmount = "" + bddouble.longValue();
//									}

								}
//								else if (map.get(key).equalsIgnoreCase("cb_reason")) {
//									cbReason = String.valueOf(currentRow.getCell(key).getStringCellValue());
//								}
//								else if (map.get(key).equalsIgnoreCase("cb_intimation_date")) {
//									cbIntimationDate= currentRow.getCell(key).getStringCellValue().toString();
//									logger.info("intimation date in numeric type..." + cbIntimationDate);
//								
//								} 
								else if (map.get(key).equalsIgnoreCase("cb_deadline_date")) {
									cbDeadlineDate = currentRow.getCell(key).getStringCellValue().toString();
									// cbDeadlineDate = sdf.format(date);
									logger.info("cbDeadlineDate date in numeric type..." + cbDeadlineDate);

								} else if (map.get(key).equalsIgnoreCase("notification_email")) {
									notificationEmail = String.valueOf(currentRow.getCell(key).getStringCellValue());
									logger.info("cbDeadlineDate date in numeric type..." + notificationEmail);
								}

							}

						}
						entity.setPgRefNo(pgRefNo);
						entity.setPgCaseId(pgCaseId);
						// entity.setCbReason(cbReason);
						entity.setCbReasonCode(cbReasonCode);
						// entity.setCbAmount(cbAmount);
						entity.setCbDdlineDate(cbDeadlineDate);
//						entity.setCbIntimationDate(cbIntimationDate);
						entity.setNemail(notificationEmail);
						if (StringUtils.isNotBlank(entity.getPgRefNo()) && StringUtils.isNotBlank(entity.getPgCaseId())
								&& StringUtils.isNotBlank(entity.getCbReasonCode())
								&& StringUtils.isNotBlank(entity.getCbDdlineDate()) && StringUtils.isNotBlank(entity.getNemail())) {
							dmsEntity.add(entity);
						}
						

					}

					// logger.info(key+":" + map.get(key));

				}

			});
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return dmsEntity;
	}
}
