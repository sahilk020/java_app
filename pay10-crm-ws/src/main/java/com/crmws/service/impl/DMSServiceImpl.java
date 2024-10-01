package com.crmws.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.dto.ChargeBackAutoPopulateData;
import com.crmws.dto.DMSdto;
import com.crmws.entity.ResponseMessage;
import com.crmws.exception.DateValidationException;
import com.crmws.service.DMSService;
import com.crmws.util.ExcelHelper;
import com.google.gson.Gson;
import com.pay10.commons.api.VelocityEmailer;
import com.pay10.commons.entity.ChargebackReasonEntity;
import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.repository.ChargebackReasonRepository;
import com.pay10.commons.repository.DMSEntity;
import com.pay10.commons.repository.DMSFileData;
import com.pay10.commons.repository.DMSRepository;
import com.pay10.commons.repository.DMSRepositoryForFileUpload;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.Status;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

@Service
public class DMSServiceImpl implements DMSService {

	private static final Logger logger = LoggerFactory.getLogger(DMSServiceImpl.class.getName());

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	Calendar calendar = Calendar.getInstance();

	@Autowired
	private UserDao userDao;

	@Autowired
	private MongoTransactionDetails txnDetails;

	@Autowired
	private DMSRepository dmsrepo;

	@Autowired
	private DMSRepositoryForFileUpload dmsRepositoryForFileUpload;

	@Autowired
	private ChargebackReasonRepository chargebackReasonRepository;

	@Autowired
	private MongoTransactionDetails details;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private ChargebackBulkMailServiceImpl chargebackBulkMailServiceImpl;

	@Autowired
	private VelocityEmailer emailer;
	final String mailToForChargeback = PropertiesManager.propertiesMap.get(Constants.MAILTOFORCHARGEBACK.getValue());

	private String message;
	
	private Gson gson=new Gson();
	@Autowired
	private MerchantWalletPODao merchantWalletPODao;
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	@Override
	public ChargeBackAutoPopulateData getByPgRefNo(String pgRefNo) {
		ChargeBackAutoPopulateData data = txnDetails.getByPgRefNo(pgRefNo);
		if (data != null) {
			logger.info("payId in service class..." + data.getPayId());
			User merchantDetails = userDao.findPayId(data.getPayId());
			data.setPayId(merchantDetails.getPayId());
			data.setNemail(merchantDetails.getEmailId());
			data.setPgRefNo(pgRefNo);
			data.setCbCaseId(TransactionManager.getNewTransactionId());
		} else {
			return null;
		}

		return data;
	}

	@Override
	public ResponseMessage validation(DMSdto dto) {
		Map<String, List<String>> validationResult = new HashMap<>();
		validationResult.put("error", new ArrayList<>());
		/*
		 * if (dto.getPgCaseId().equals(0.00)) {
		 * validationResult.get("error").add("pg caseId can not be empty"); } if
		 * (dto.getMerchantPayId().equals(0.00)) {
		 * validationResult.get("error").add("Merchant payId can not be empty"); } if
		 * (dto.getOrderId().equals(0.00)) {
		 * validationResult.get("error").add("Order ID can not be empty"); } if
		 * (dto.getCbAmount() <= 0 || dto.getCbAmount() < 0) {
		 * validationResult.get("error").add("cb amount can not be Zero"); } if
		 * (dto.getCbReason().equals(0.00)) {
		 * validationResult.get("error").add("CB Reason can not be empty"); } if
		 * (dto.getCbReasonCode().equals(0.00)) {
		 * validationResult.get("error").add("CB Reason code can not be empty"); } if
		 * (dto.getCustomerPhone().equals(0.00)) {
		 * validationResult.get("error").add("Check no of digit of mobile number "); }
		 * if (dto.getEmail() == null || dto.getEmail().length() == 0) {
		 * validationResult.get("error").add("email can not be empty"); }
		 * 
		 * if (dto.getEmail() == "^(.+)@(.+)$" && dto.getEmail() != null &&
		 * dto.getEmail().length() != 0) {
		 * validationResult.get("error").add("please enter a valid email"); }
		 * 
		 * if (dto.getCbAmount() > dto.getTxnAmount()) { return new
		 * ResponseMessage("chargeback amount can not be more than txn amount",
		 * HttpStatus.BAD_REQUEST, validationResult); }
		 */
		if (validationResult.get("error").size() > 0) {
			return new ResponseMessage("error", HttpStatus.BAD_REQUEST, validationResult);
		}
		return new ResponseMessage("error", HttpStatus.BAD_REQUEST, validationResult);
	}

	@Override
	public List<DMSdto> listAll(String payId) {
		logger.info("PayId : " + payId);
		if(StringUtils.isNotBlank(payId)) {
			User user=userDao.findByPayId(payId);
			if(user.getUserType().name().equalsIgnoreCase("SUBUSER")) {
				payId=user.getParentPayId();
			}
		}
		List<DMSEntity> olddms = StringUtils.isNotBlank(payId) ? dmsrepo.findByPayId(payId) : dmsrepo.findAll();
		ArrayList<DMSdto> dto = new ArrayList<>();
		for (DMSEntity dmsEntity : olddms) {
			DMSdto dto1 = new DMSdto();
			BeanUtils.copyProperties(dmsEntity, dto1);
			dto1.setMerchantName(dmsEntity.getBusinessName());
			dto1.setDateWithTimeStamp(StringUtils.isNotBlank(dto1.getDateWithTimeStamp())?dto1.getDateWithTimeStamp():dateFormat.format(new Date()));
			dto.add(dto1);

		}
		dto.sort(Comparator.comparing(DMSdto::getDateWithTimeStamp).reversed());
		return dto;
	}

	public List<DMSdto> listChargebackStatus(String payId) {
		List<DMSEntity> olddms = StringUtils.isNotBlank(payId) ? dmsrepo.listChargebackStatus(payId)
				: dmsrepo.listChargebackStatus("");
		ArrayList<DMSdto> dto = new ArrayList<>();
		for (DMSEntity dmsEntity : olddms) {
			DMSdto dto1 = new DMSdto();
			BeanUtils.copyProperties(dmsEntity, dto1);
			dto1.setMerchantName(dmsEntity.getBusinessName());
			dto.add(dto1);
		}
		return dto;
	}

	@Override
	public DMSdto getDMSdtoById(long id) {
		DMSdto data = new DMSdto();
		DMSEntity dms = dmsrepo.findById(id);
		List<DMSFileData> dmsFileEntitylist = dmsRepositoryForFileUpload.findAllByCaseId(dms.getCbCaseId());
		String files = null;
		System.out.println("File Data :   " + dmsFileEntitylist.size());
		if (dmsFileEntitylist.size() > 0) {
			for (DMSFileData dmsFileData : dmsFileEntitylist) {
				System.out.println(dmsFileData.toString());
				if (files == null) {
					files = dmsFileData.getFilePaths();
				} else {
					files = files + "," + dmsFileData.getFilePaths();
				}
			}
		}
		System.out.println("Files :   " + files);
		data.setFilePaths(files);
		data.setId(dms.getId());

		BeanUtils.copyProperties(dms, data);
		System.out.println("File 1  :  " + data.getFilePaths());
		data.setFilePaths(files);
		System.out.println("File 2 :  " + data.getFilePaths());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {

			if (StringUtils.isNotBlank(data.getDtOfTxn())) {
				data.setDtOfTxn(sdf.format(sdf.parse(data.getDtOfTxn())));
			}
			if (StringUtils.isNotBlank(data.getSettlemtDate())) {
				data.setSettlemtDate(sdf.format(sdf.parse(data.getSettlemtDate())));
			}
		} catch (ParseException e) {
			logger.error("Failed to parse", e);
		}
		return data;
	}

	@Override
	public ResponseMessage accept(DMSdto dto, long id) {

		logger.info("Status of chargeback..." + dto.getStatus());
		DMSEntity dms = new DMSEntity();
		DMSEntity olddms = dmsrepo.findById(id);
		String ret_msg = "";

		if (!olddms.isMerchantDocUploadFlag()
				&& dto.getStatus().toString().equalsIgnoreCase(Status.ACCEPTED.toString())) {
			ret_msg = "Document not uploaded please upload document first!";
		} else if (!dto.getUserType().equalsIgnoreCase("Merchant")) {
			ret_msg = "You do not have permission to accept the chargeback!";
		} else {
			logger.info("Status of chargeback..." + dms.getStatus());
			dto.setUpdatedBy(dto.getEmailId());
			dto.setCbAcceptDate(simpleDateFormat.format(new Date()));
			dto.setStatus(Status.ACCEPTED);
			BeanUtils.copyProperties(dto, dms);
			dms.setId(olddms.getId());
			dms.setFilePaths(olddms.getFilePaths());
			dms.setUpdatedBy(dto.getEmailId());
			dmsrepo.update(dms);
			ret_msg = "Data saved successfully";
		}
		return new ResponseMessage(ret_msg, HttpStatus.OK);

	}

	@Override
	public ResponseMessage update(DMSdto dto, long id) {

		logger.info("Status of chargeback..." + dto.getStatus());
		DMSEntity dms = new DMSEntity();
		DMSEntity olddms = dmsrepo.findByCbCaseId(dto.getCbCaseId()).get(0);

		String ret_msg = "";

		if (dto.getUserType().equalsIgnoreCase("ADMIN")||dto.getUserType().equalsIgnoreCase("SUB ADMIN")) {
			ret_msg = "You do not have permission to update the chargeback";
		}
		else if(dto.getUserType().equalsIgnoreCase("MERCHANT")||dto.getUserType().equalsIgnoreCase("SUB USER")){
			logger.info("Status of chargeback..." + dto.getStatus() + " - " + dto.getUpdatedBy());
			User user=userDao.findByEmailId(dto.getUpdatedBy());
			//BeanUtils.copyProperties(dto, dms);
			olddms.setCbAcceptDate(simpleDateFormat.format(new Date()));;
			olddms.setStatus(dto.getStatus());
			if(user.getUserType().name().equalsIgnoreCase("SUBUSER")) {
				User userParent=userDao.findByPayId(user.getParentPayId());
				olddms.setUpdatedBy(userParent.getEmailId());
			}else {
				olddms.setUpdatedBy(dto.getEmailId());
			}
			dmsrepo.update(olddms);
			new Thread() {
				public void run() {
					chargebackBulkMailServiceImpl.sendChargeBackMail(olddms);
				}
			}.start();
			ret_msg = "Data saved successfully";
		}
		return new ResponseMessage(ret_msg, HttpStatus.OK);

	}

	@Override
	public ResponseMessage updateCbFavourMerchant(long id) {
		String ret_msg = "";
		List<DMSEntity> list=dmsrepo.findByCbCaseId(String.valueOf(id));
		if(!CollectionUtils.isEmpty(list)&&list.size()>0) {
			DMSEntity dms = list.get(0);
			if(StringUtils.equalsAnyIgnoreCase((Status.CLOSED.toString()),(dms.getStatus().toString()))) {
				dms.setCbClosedInFavorMerchant(Status.MERCHANT_FAVOUR.toString());
				dms.setCbFavourDate(simpleDateFormat.format(new Date()));
				if ((txnDetails.creatNewEntryInTransactionStatusCBReversal(dms.getPgRefNo(), (java.util.Date) new Date()))
						.equalsIgnoreCase("Sucesss")) {
					dmsrepo.update(dms);
				}
				ret_msg = "Data saved successfully";
			}else {
				ret_msg = "Chargeback is not closed yet case id:" + String.valueOf(id);
			}
		}else {
			ret_msg = "Chargeback not found with case id : "+ String.valueOf(id);
		}
		
		
		return new ResponseMessage(ret_msg, HttpStatus.OK);

	}

	@Override
	public ResponseMessage updateCbFavourBankAcquirer(long id) {
		String ret_msg = "";
		List<DMSEntity> list=dmsrepo.findByCbCaseId(String.valueOf(id));
		if(!CollectionUtils.isEmpty(list)&&list.size()>0) {
			DMSEntity dms = list.get(0);
			if(StringUtils.equalsAnyIgnoreCase((Status.CLOSED.toString()),(dms.getStatus().toString()))) {
				dms.setCbClosedInFavorBank(Status.BANK_ACQ_FAVOUR.toString());;
				dms.setCbFavourDate(simpleDateFormat.format(new Date()));
//				if ((txnDetails.creatNewEntryInTransactionStatusCBReversal(dms.getPgRefNo(), (java.util.Date) new Date()))
//						.equalsIgnoreCase("Sucesss")) {
					dmsrepo.update(dms);
//				}
				ret_msg = "Data saved successfully";
			}else {
				ret_msg = "Chargeback is not closed yet case id:" + String.valueOf(id);
			}
		}else {
			ret_msg = "Chargeback not found with case id : "+ String.valueOf(id);
		}
		return new ResponseMessage(ret_msg, HttpStatus.OK);

	}

	@Override
	public ResponseMessage close(DMSdto dto, long id) {

		logger.info("Close of chargeback..." + gson.toJson(dto));
		DMSEntity olddms = dmsrepo.findObjectByCbCaseId(dto.getCbCaseId());

		String ret_msg = "";

		if (dto.getUserType().equalsIgnoreCase("Merchant")) {
			ret_msg = "You do not have permission to close the chargeback";
		} else {
			
			if(dto.getCbClosedInFavorMerchant().equalsIgnoreCase("MERCHANT_FAVOUR")) {
				//Charge reversal method called here
				dto.setTxnAmount(olddms.getTxnAmount());
				dto.setMerchantPayId(olddms.getMerchantPayId());
				dto.setCurrencyName(StringUtils.isNotBlank(olddms.getCurrencyName())?olddms.getCurrencyName():"INR");
				dto.setOrderId(olddms.getOrderId());
				chargeBackRaised(dto, false);
			}
			
			olddms.setCbClosedInFavorBank(dto.getCbClosedInFavorBank());
			olddms.setCbClosedInFavorMerchant(dto.getCbClosedInFavorMerchant());
			olddms.setCbCloseDate(simpleDateFormat.format(new Date()));
			olddms.setCbFavourDate(simpleDateFormat.format(new Date()));
			olddms.setStatus(Status.CLOSED);

			dmsrepo.update(olddms);
			new Thread() {
				public void run() {
					chargebackBulkMailServiceImpl.sendChargeBackMail(olddms);
				}
			}.start();
			ret_msg = "Data saved successfully";
		}
		return new ResponseMessage(ret_msg, HttpStatus.OK);

	}

	@Override
	public List<DMSEntity> getAllEntity() {
		return dmsrepo.findAll();
	}

	@Override
	public List<String> getPgRefNosByPayId(String payId) {
		logger.info("merchantPayId...." + payId);
		return txnDetails.getPgRefNosByPayId(payId);
	}

//for excel upload
	@Override
	public String save(MultipartFile file) throws DateValidationException {
		int count = 0;
		try {

			List<DMSEntity> entity = ExcelHelper.excelUpload(file.getInputStream());
			logger.info("List of excel data" + entity.toString());

			if (fileValidate(entity)) {
				for (DMSEntity dms : entity) {
					ChargeBackAutoPopulateData data = null;
					String pgRef = dms.getPgRefNo();
					data = getByPgRefNo(pgRef);
					if (data != null) {
						Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String tdate = sdf.format(date);
//					validateIntimationDate(dms.getCbIntimationDate()) &&
						if (validateDeadlineDate(dms.getCbDdlineDate())) {
							count++;
							dms.setMerchantPayId(data.getPayId());
							dms.setCustomerName(data.getMerchantName());
							dms.setCustomerPhone(data.getMobile());
							dms.setCbReason( chargebackReasonRepository
							.getcbReasonDescriptionFromcbReasonCode(dms.getCbReasonCode()).getCbReasonDescription());
							dms.setEmail(data.getEmail());
							dms.setPgRefNo(pgRef);
							dms.setCbCaseId(data.getCbCaseId());
							dms.setAcqName(data.getAcquirerName());
							dms.setBankTxnId(data.getBankTxnId());
							dms.setDtOfTxn(data.getDateOfTxn());
							dms.setMerchantTxnId(data.getMerchantTxnId());
							dms.setModeOfPayment(data.getModeOfPayment());
							dms.setOrderId(data.getOrderId());
							if (StringUtils.isNotBlank(data.getSettlementDate()))
								dms.setSettlemtDate(data.getSettlementDate());
							dms.setTxnAmount(data.getTxnAmount());
							dms.setCbAmount(data.getTxnAmount());
							logger.info("SUNIL DATE : "+tdate);
							dms.setCbInitiatedDate(tdate);
							dms.setCbIntimationDate(tdate);
							dms.setStatus(Status.INITIATED);
							dmsrepo.save(dms);
							createNewTransactionEntry(dms.getPgRefNo(), dms.getDtOfTxn());
						}else {
							message = "Cb Deadline Date should be more than today date";
							return message;
						}
					}

				}
				message = " file uploaded successfully , " + count + " Chargeback Raised and File name : "
						+ file.getOriginalFilename();
				return message;
			} else {
				return getMessage();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error while uploading excel data");
		}

	}

	public boolean fileValidate(List<DMSEntity> entity) {
		for (DMSEntity t : entity) {
			String cbDeadLineDate = t.getCbDdlineDate();
			String pgrefNo = t.getPgRefNo();
			String pgCaseId = t.getPgCaseId();
			String cbReasonCode = t.getCbReasonCode();

			boolean checkblank = false;

			if (StringUtils.isBlank(cbDeadLineDate)) {
				checkblank = false;

				setMessage("Cb Deadline Should not be empty");
				return false;
			} else {
				checkblank = true;
			}

			if (StringUtils.isBlank(pgrefNo)) {
				checkblank = false;

				setMessage("Pg Ref No Should not be empty");
				return false;
			} else {
				checkblank = true;
			}

			if (StringUtils.isBlank(pgCaseId)) {
				checkblank = false;

				setMessage("PG Case ID Should not be empty");
				return false;
			} else {
				checkblank = true;
			}

			if (StringUtils.isBlank(cbReasonCode)) {
				checkblank = false;

				setMessage("CB Reason Code Should not be empty");
				return false;
			} else {
				checkblank = true;
			}

			if (checkblank) {
				if (dateValidator(cbDeadLineDate)) {
					ChargebackReasonEntity chargebackReasonEntity = chargebackReasonRepository
							.getcbReasonDescriptionFromcbReasonCode(cbReasonCode.trim());
					if (chargebackReasonEntity != null) {
						Document document = details.getTxnByPgRefNo(pgrefNo);
						if (document != null) {
							return true;
						} else {
							setMessage("PG REF NUMBER is not found in our database");
							return false;
						}
					} else {
						setMessage("Could Not found Cb Reason Code Please Register the cb reason code");
						return false;
					}
				} else {
					setMessage("CB DeadlineDate Format Should be yyyy-MM-dd like (2023-06-20)");
					return false;
				}
			}

		}
		setMessage("File Data Is Not Correct");
		return false;
	}

	private boolean dateValidator(String UTRDate) {
		logger.info("Deadline Date : "+UTRDate);
		Date date = new Date();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);
		try {
			date = sdf.parse(UTRDate);
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ResponseMessage save(DMSdto dto) {
		String response="Transaction could not performed";
		try {
			DMSEntity dms = new DMSEntity();

			logger.info("Status of chargeback..." + dto.getStatus());
			BeanUtils.copyProperties(dto, dms);
			dms.setCbInitiatedDate(simpleDateFormat.format(new Date()));
			dms.setStatus(Status.INITIATED);
			dms.setCreatedBy(dto.getEmailId());
			logger.info("get date of transaction..." + dto.getDtOfTxn());
			logger.info("get settlement date of transaction..." + dto.getSettlemtDate());
			dms.setBusinessName(userDao.getBusinessNameByPayId(dms.getMerchantPayId()));
			
			List<DMSEntity> listEntities= dmsrepo.findByPgRefNo(dto.getPgRefNo());
			logger.info("PGREF NUMBER : " + dto.getPgRefNo() + " DATA : " + listEntities.size());
			if(listEntities!=null&&listEntities.size()>0) {
				return new ResponseMessage("Chargeback for this PG REF NO is already raised.", HttpStatus.OK);
			}
			
			logger.info("Request DTO : " + gson.toJson(dto));
			String resp=chargeBackRaised(dto,true);
			//response=resp.equalsIgnoreCase("Success")?"Chargeback Created Successfully":(String) response;
			if (resp.equalsIgnoreCase("Success")) {
				dms.setDateWithTimeStamp(dateFormat.format(new Date()));
				dmsrepo.save(dms);
				createNewTransactionEntry(dms.getPgRefNo(), dto.getDtOfTxn());
				response = "Chargeback Created Successfully";
				new Thread() {
					public void run() {
						chargebackBulkMailServiceImpl.sendChargeBackMail(dms);
					}
				}.start();
				return new ResponseMessage(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return new ResponseMessage(response, HttpStatus.OK);
		}

		return new ResponseMessage(response, HttpStatus.OK);
	}

	private String chargeBackRaised(DMSdto dto,boolean chargeBackType) {
		logger.info("chargeBackRaised : " + gson.toJson(dto));
		Fields fields=new Fields();
		fields.put(FieldType.PAY_ID.getName(), dto.getMerchantPayId());
		fields.put(FieldType.CURRENCY_CODE.getName(), multCurrencyCodeDao.getCurrencyCodeByName(dto.getCurrencyName()));
		String amt = dto.getTxnAmount();
		String str[]=amt.split("\\.");
		if(str[1].length()==1)
			str[1]=str[1]+"0";
		amt = str[0]+str[1];
		fields.put(FieldType.AMOUNT.getName(),amt );
		fields.put(FieldType.ORDER_ID.getName(),dto.getOrderId());
		logger.info("Request from chargeBackRaised " + fields.getFieldsAsString() + " " + chargeBackType);
		return merchantWalletPODao.updateMerchantWalletForChargeBack(fields, chargeBackType);
	}
	
	private ResponseMessage createNewTransactionEntry(String pgRefString, String date) {

		try {

			if (txnDetails.creatNewEntryInTransactionStatusCBInitiated(pgRefString, date).equalsIgnoreCase("Sucesss"))
				return new ResponseMessage("data saved successfully", HttpStatus.OK);
			else
				return new ResponseMessage("Unable to save data in transaction table", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return new ResponseMessage("Unable to save data in transaction table", HttpStatus.BAD_REQUEST);

		}

	}

	private boolean validateIntimationDate(String intimationDate) throws ParseException {
		Date intDate = sdf.parse(intimationDate);
		calendar.setTime(new Date());
		return intDate.before(calendar.getTime());
	}

	private boolean validateDeadlineDate(String deadlineDate) throws ParseException {
		Date intDate = sdf.parse(deadlineDate);
		calendar.setTime(new Date()); // Using today's date
		return intDate.after(calendar.getTime());
	}

	@Override
	public String chargebackStatusBulkUpload(MultipartFile formData) {
		String statusBack = "Successfully Updated";
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(formData.getInputStream());
			logger.info("=> sheet reading file");
			Map<String, String> csu = new HashMap();
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
								if (map.get(key).equalsIgnoreCase("CB Case Id")) {
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
								if (map.get(key).equalsIgnoreCase("Favour")) {
									if (currentCell.getCellTypeEnum() == CellType.STRING) {
										utrNo = String.valueOf(currentRow.getCell(key).getStringCellValue());
									} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
										utrNo = String.valueOf(currentRow.getCell(key).getNumericCellValue() + "");
										if (utrNo != null && utrNo.length() > 0 && utrNo.contains("E")) {
											BigDecimal bddouble = new BigDecimal(utrNo);
											utrNo = "" + bddouble.longValue();
										}
									}
								}

							}
						}
						logger.info("Update TXN details :" + txnId + utrNo);
						logger.info("Calling mongo method now");
						if (txnId != null && utrNo != null && txnId.trim().length() > 0 && utrNo.trim().length() > 0) {
							csu.put(txnId, utrNo);
						}
					}
				}
			});

			// insertion in mongodb
			Map<String, String> responseFromDb = dmsrepo.insertChargeBackStatusUpdate(csu);

			for (Map.Entry<String, String> entry : responseFromDb.entrySet()) {
				String cbCaseId = entry.getKey();
				String favour = entry.getValue();

				if (favour.equalsIgnoreCase("merchant")) {
					ResponseMessage responseMessage = updateCbFavourMerchant(Long.parseLong(cbCaseId));
					if (responseMessage.getRespmessage().equalsIgnoreCase("Data saved successfully")) {
						long status = dmsrepo.updateChargeBackStatusUpdate(cbCaseId,responseMessage.getRespmessage());
						logger.info("Number Of Document Update in chargebackStatusUpdate :" + status);
					} else {
						long status = dmsrepo.updateChargeBackStatusUpdate(cbCaseId,responseMessage.getRespmessage());
						logger.info("Response From updateCbFavourMerchant " + responseMessage.getRespmessage());
					}
				} else {
					ResponseMessage responseMessage = updateCbFavourBankAcquirer(Long.parseLong(cbCaseId));
					if (responseMessage.getRespmessage().equalsIgnoreCase("Data saved successfully")) {
						long status = dmsrepo.updateChargeBackStatusUpdate(cbCaseId,responseMessage.getRespmessage());
						logger.info("Number Of Document Update in chargebackStatusUpdate :" + status);
					} else {
						long status = dmsrepo.updateChargeBackStatusUpdate(cbCaseId,responseMessage.getRespmessage());
						logger.info("Response From updateCbFavourMerchant " + responseMessage.getRespmessage());
					}
				}

			}
			workbook.close();
		} catch (Exception e) {
			statusBack = "Failed To Upload";
			logger.error("Exception Occur in chargebackStatusBulkUpload : ", e);
			e.printStackTrace();
		}
		return statusBack;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
