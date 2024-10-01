package com.pay10.scheduler.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.dao.AcquirerDTConfigurationDao;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.dao.OfflineRefundDao;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.entity.IRCTCRefundFile;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.AcquirerDownTimeConfiguration;
import com.pay10.commons.user.OfflineRefund;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.scheduler.commons.BulkRefundFileFormat;
import com.pay10.scheduler.commons.ConfigurationProvider;
import com.pay10.scheduler.commons.TransactionDataProvider;
import com.pay10.scheduler.core.ServiceControllerProvider;
import com.pay10.scheduler.dto.RefundAckDTO;
import com.pay10.scheduler.irctc.IRCTCRefundFileService;
import com.pay10.commons.api.VelocityEmailer;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.StatusTypeCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RefundTransactionScheduler {
	private static final Logger logger = LoggerFactory.getLogger(RefundTransactionScheduler.class);

	@Autowired
	private ConfigurationProvider configurationProvider;

	@Autowired
	private TransactionDataProvider transactionDataProvider;

	@Autowired
	private ServiceControllerProvider serviceControllerProvider;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	@Autowired
	AcquirerDTConfigurationDao acquirerDTConfigurationDao;

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;

	@Autowired
	OfflineRefundDao offlineRefundDao;

	@Autowired
	BulkRefundFileFormat bulkRefundFileFormat;

	@Autowired
	VelocityEmailer emailer;

	@Autowired
	IRCTCRefundFileService irctcRefundFileService;

	private static final int MERCHANT_ID_COLUMN_INDEX = 0;
	private static final int REFUND_REQ_NO_COLUMN_INDEX = 1;
	private static final int TRANS_REF_NO_COLUMN_INDEX = 2;
	private static final int CUST_REF_NO_COLUMN_INDEX = 3;
	private static final int ORDER_NO_COLUMN_INDEX = 4;
	private static final int REFUND_REQ_AMT_COLUMN_INDEX = 5;
	private static final int REFUND_REMARK_COLUMN_INDEX = 6;
	private static final int REFUND_STATUS_COLUMN_INDEX = 7;
	private static final int BANK_REFUND_TXN_ID_INDEX = 8;
	private static final int REFUND_DATE_COLUMN_INDEX = 9;
	private static final int BANK_REMARK_COLUMN_INDEX = 10;

	//@Scheduled(cron = "0 */10 * * * *") // 0 */2 * * * * 0 30 1 * * *
	private void fetchRefundTransactionData() {

		try {

			logger.info(
					"------------------- Started fetching refund transaction data --------------------------------");
			List<Document> refundData = transactionDataProvider.fetchOnlineRefundTransactionData();
			String bankRefundUrl = configurationProvider.getRefundServiceApiUrl();

			for (Document doc : refundData) {
				logger.info("Sending refund request , URL = " + bankRefundUrl + " 	pgRef == "
						+ doc.getString(FieldType.PG_REF_NUM.getName()));
				JSONObject data = new JSONObject();
				data.put(FieldType.PG_REF_NUM.getName(), doc.getString(FieldType.PG_REF_NUM.getName()));
				data.put(FieldType.ORDER_ID.getName(), doc.getString(FieldType.ORDER_ID.getName()));
				data.put(FieldType.REFUND_ORDER_ID.getName(), doc.getString(FieldType.REFUND_ORDER_ID.getName()));
				data.put(FieldType.AMOUNT.getName(),
						Amount.formatAmount(doc.getString(FieldType.AMOUNT.getName()), "356"));
				data.put(FieldType.CURRENCY_CODE.getName(), doc.getString(FieldType.CURRENCY_CODE.getName()));
				data.put(FieldType.TXNTYPE.getName(), doc.getString(FieldType.TXNTYPE.getName()));
				data.put(FieldType.PAY_ID.getName(), doc.getString(FieldType.PAY_ID.getName()));
				data.put(FieldType.REFUND_FLAG.getName(), "R");

				logger.info("Refund Request = {}", data);

				serviceControllerProvider.bankRefundService(data, bankRefundUrl);
			}

		}

		catch (Exception e) {
			logger.error("Exception in bank Refund from scheduler", e);
		}

	}

	//@Scheduled(cron = "0 0 1 * * *") // @Scheduled(cron = "0 */5 * * * *")
	private void fetchOfflineRefundDataForRefundFile_New() {
		try {
			logger.info(
					"############ Started fetching Sent to Bank transaction for making refund file for Acquirer########");
			List<String> acqurierList = transactionDataProvider.fetchOfflineRefundTransactionData();
			String refundFilePath = configurationProvider.getRefundFileLocation();
			logger.info("Acquirer List For Refund Utility : " + acqurierList);
			acqurierList.forEach(acquirer -> {
				logger.info("Fetch Refund Data For Acquirer = {} ", acquirer);
				List<Document> refundRecord = transactionDataProvider.fetchOfflineRefundTransactionDataNew(acquirer);
				try {
					if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.CANARANBBANK.getCode())) {
						bulkRefundFileFormat.canaraRefundFile(refundRecord, refundFilePath, acquirer);
					} else if ((!StringUtils.isEmpty(acquirer))
							&& acquirer.equals(AcquirerType.JAMMU_AND_KASHMIR.getCode())) {
						bulkRefundFileFormat.JammuAndKashmirRefundFile(refundRecord, refundFilePath, acquirer);
					} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.AXISBANK.getCode())) {
						bulkRefundFileFormat.axisBankNBRefundFile(refundRecord, refundFilePath, acquirer);
					} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBINB.getCode())) {
						bulkRefundFileFormat.sbiNBRefundFile(refundRecord, refundFilePath, acquirer);
					} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBICARD.getCode())) {
						bulkRefundFileFormat.sbiCardRefundFile(refundRecord, refundFilePath, acquirer);
					} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBI.getCode())) {
						bulkRefundFileFormat.sbiUPIRefundFile(refundRecord, refundFilePath, acquirer);
					} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.KOTAK.getCode())) {
						bulkRefundFileFormat.kotakNBRefundFile(refundRecord, refundFilePath, acquirer);
					} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.COSMOS.getCode())) {
						bulkRefundFileFormat.cosmosUPIRefundFile(refundRecord, refundFilePath, acquirer);
					} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKNB.getCode())) {
						bulkRefundFileFormat.yesbankNBRefundFile(refundRecord, refundFilePath, acquirer);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			});
			logger.info(
					"############ Completed fetching Sent to Bank transaction for making refund file for Acquirer########");
		}

		catch (Exception e) {
			logger.error("Exception in while creating SBI UPI Refund File from scheduler", e);
		}

	}

	//@Scheduled(cron = "0 */2 * * * *") // every minute
	private void processRefundAckFileForSbiUPI() {
		logger.info("Start Processing SBI Refund Ack Files ::");
		File folder = new File(configurationProvider.getRefundAckFileLocation()); // "D:\\Sbi_Refund_Ack_file\\"
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {
				logger.info(file.getName());
				List<RefundAckDTO> fileData = readExcelFile(
						configurationProvider.getRefundAckFileLocation() + file.getName());

				fileData.forEach(respFileData -> {
					logger.info("respFileData " + respFileData);

					if (fieldsDao.checkRefundStatus(respFileData.getTransRefNo(), respFileData.getRefundReqNo())) {
						logger.info("Already Refund Transaction Captured For PG_REF_NUM = {}, REFUND_ORDER_ID = {} ",
								respFileData.getTransRefNo(), respFileData.getRefundReqNo());
						return;
					}

					Fields fields = fieldsDao.getPreviousRefundPendingForPgRefNum(respFileData.getTransRefNo(),
							respFileData.getRefundReqNo());
					logger.info("Fields Object = {} ", fields.getFieldsAsBlobString());
					if (StringUtils.isEmpty(fields.get(FieldType.PG_REF_NUM.getName()))) {
						logger.info("Fields Object Empty For : " + respFileData.toString());
						return;
					}

					String newTxnId = TransactionManager.getNewTransactionId();

					String amountString = fields.get(FieldType.AMOUNT.getName());
					String currencyString = fields.get(FieldType.CURRENCY_CODE.getName());
					String totalAmountString = fields.get(FieldType.TOTAL_AMOUNT.getName());

					String amount = "0";
					if (!StringUtils.isEmpty(amountString) && !StringUtils.isEmpty(currencyString)) {
						amount = Amount.formatAmount(amountString, currencyString);
						fields.put(FieldType.AMOUNT.getName(), amount);
					}

					String totalAmount = "0";
					if (!StringUtils.isEmpty(totalAmountString) && !StringUtils.isEmpty(currencyString)) {
						totalAmount = Amount.formatAmount(totalAmountString, currencyString);
						fields.put(FieldType.TOTAL_AMOUNT.getName(), totalAmount);
					}

					if (refundAckFileValidate(respFileData, fields)) {
						logger.info("update refund transaction for success");
						fields.put(FieldType.STATUS.getName(), "Captured");
						fields.put(FieldType.RESPONSE_MESSAGE.getName(), "SUCCESS");
						fields.put(FieldType.RESPONSE_CODE.getName(), "000");
						fields.put(FieldType.ALIAS_STATUS.getName(), "Captured");
						fields.put(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName());
						fields.put(FieldType.AUTH_CODE.getName(), respFileData.getBankRefundTxnId());
						fields.put(FieldType.TXN_ID.getName(), newTxnId);
						fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
						if (null != fields.get(FieldType.REFUND_PROCESS.getName())) {
							fields.put(FieldType.REFUND_PROCESS.getName(), "S");
						}
						try {
							logger.info("Refund Ack File Process Captured transaction. fields={}"
									+ fields.getFieldsAsString());
							fieldsDao.insertTransaction(fields);
						} catch (SystemException e) {
							logger.error("processRefundAckFileForSbiUPI:: Captured transaction. fields={}",
									fields.getFieldsAsString(), e);
						}
					} else {

						logger.info("update refund transaction for Failed");
						fields.put(FieldType.STATUS.getName(), "Failed");
						fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Failed by acquirer");
						fields.put(FieldType.RESPONSE_CODE.getName(), "022");
						fields.put(FieldType.ALIAS_STATUS.getName(), "Failed");
						fields.put(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName());
						fields.put(FieldType.AUTH_CODE.getName(), respFileData.getBankRefundTxnId());
						fields.put(FieldType.TXN_ID.getName(), newTxnId);
						fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));

						try {
							logger.info("Refund Ack File Process Failed transaction. fields={}"
									+ fields.getFieldsAsString());
							fieldsDao.insertTransaction(fields);
						} catch (SystemException e) {
							logger.error("processRefundAckFileForSbiUPI:: Failed transaction. fields={}",
									fields.getFieldsAsString(), e);
						}
					}

				});

				Path temp = null;
				try {
					temp = Files.move(Paths.get(configurationProvider.getRefundAckFileLocation() + file.getName()),
							Paths.get(configurationProvider.getRefundSuccessFileLocation() + file.getName()));
				} catch (IOException e) {
					logger.error("FAILED IN FILE MOVING FROM ONE LOCATION TO ANOTHER. ", e);
					e.printStackTrace();
				}

				if (temp != null) {
					logger.info("File renamed and moved successfully");
				} else {
					logger.info("Failed to move the file");
				}
			}
		}
		logger.info("Completed Processing SBI Refund Ack Files");
	}

	public List<RefundAckDTO> readExcelFile(String fileName) {
		List<RefundAckDTO> result = new ArrayList<>();
		try (InputStream fileInputStream = new FileInputStream(fileName)) {

			// Read InputStream into Workbook
			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
			// Read the first Sheet
			Sheet sheet = workbook.getSheetAt(0);
			// Get row Iterator
			Iterator<Row> rowIterator = sheet.rowIterator();

			// Skip the first row because it is the header row
			if (rowIterator.hasNext()) {
				rowIterator.next();
			}
			// Read all data rows
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				// Get cell Iterator
				Iterator<Cell> cellIterator = row.cellIterator();
				RefundAckDTO refundAckFile = new RefundAckDTO();
				// Read cell data
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					CellType cellType = cell.getCellType().equals(CellType.FORMULA) ? cell.getCachedFormulaResultType()
							: cell.getCellType();
					switch (columnIndex) {
					case MERCHANT_ID_COLUMN_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setMerchantId(cell.getStringCellValue());
							break;
						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setMerchantId(String.valueOf(cell.getNumericCellValue()));
							break;
						}

					case REFUND_REQ_NO_COLUMN_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setRefundReqNo(cell.getStringCellValue());
							break;
						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setRefundReqNo(String.valueOf(cell.getNumericCellValue()));
							break;

						}
					case TRANS_REF_NO_COLUMN_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setTransRefNo(cell.getStringCellValue());
							break;
						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setTransRefNo(String.valueOf(cell.getNumericCellValue()));
							break;
						}
					case CUST_REF_NO_COLUMN_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setCustRefNo(cell.getStringCellValue());
							break;
						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setCustRefNo(String.valueOf(cell.getNumericCellValue()));
							break;
						}
					case ORDER_NO_COLUMN_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setOrderNo(cell.getStringCellValue());
							break;

						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setOrderNo(String.valueOf(cell.getNumericCellValue()));
							break;
						}
					case REFUND_REQ_AMT_COLUMN_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setRefundReqAmt(cell.getStringCellValue());
							break;
						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setRefundReqAmt(String.valueOf(cell.getNumericCellValue()));
							break;
						}
					case REFUND_REMARK_COLUMN_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setRefundRemark(cell.getStringCellValue());
							break;
						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setRefundRemark(String.valueOf(cell.getNumericCellValue()));
							break;
						}
					case REFUND_STATUS_COLUMN_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setRefundStatus(cell.getStringCellValue());
							break;
						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setRefundStatus(String.valueOf(cell.getNumericCellValue()));
							break;
						}
					case BANK_REFUND_TXN_ID_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setBankRefundTxnId(cell.getStringCellValue());
							break;
						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setBankRefundTxnId(String.valueOf(cell.getNumericCellValue()));
							break;
						}
					case REFUND_DATE_COLUMN_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setRefundDate(cell.getStringCellValue());
							break;
						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setRefundDate(String.valueOf(cell.getNumericCellValue()));
							break;
						}
					case BANK_REMARK_COLUMN_INDEX:
						if (cellType.equals(CellType.STRING)) {
							refundAckFile.setBankRemark(cell.getStringCellValue());
							break;
						}
						if (cellType.equals(CellType.NUMERIC)) {
							refundAckFile.setBankRemark(String.valueOf(cell.getNumericCellValue()));
							break;
						}
					}
				}

				result.add(refundAckFile);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.info("exception " + e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("exception " + e);
		}

		logger.info("result " + result);
		return result;
	}

	private boolean refundAckFileValidate(RefundAckDTO respFileData, Fields fields) {
		if (!respFileData.getTransRefNo().equalsIgnoreCase(fields.get(FieldType.PG_REF_NUM.getName()))) {
			return false;
		} else if (!respFileData.getBankRemark().equalsIgnoreCase("Refund Success")) {
			return false;
		} else if (!respFileData.getRefundStatus().equalsIgnoreCase("1.0")) {
			return false;
		} else if (!respFileData.getRefundRemark().equalsIgnoreCase("R")) {
			return false;
		} else if (!respFileData.getOrderNo().equalsIgnoreCase(fields.get(FieldType.ORDER_ID.getName()))) {
			return false;
		} else if (!respFileData.getRefundReqNo().equalsIgnoreCase(fields.get(FieldType.REFUND_ORDER_ID.getName()))) {
			return false;
		}
		return true;
	}

	public Object[] myCsvMethodDownloadPaymentsforSaleCaptured(Document redundDocument) {
		Object[] objectArray = new Object[23];

		// objectArray[0] = merchantId;
		objectArray[0] = getMerchantKey(redundDocument.get(FieldType.PAY_ID.getName()).toString(),
				redundDocument.get(FieldType.REFUND_ORDER_ID.getName()).toString(),
				redundDocument.get(FieldType.ACQUIRER_TYPE.getName()).toString());
		objectArray[1] = redundDocument.get(FieldType.REFUND_ORDER_ID.getName()).toString();
		objectArray[2] = redundDocument.get(FieldType.PG_REF_NUM.getName()).toString();
		objectArray[3] = redundDocument.get(FieldType.ORIG_TXN_ID.getName()).toString();
		objectArray[4] = redundDocument.get(FieldType.ORDER_ID.getName()).toString();
		objectArray[5] = redundDocument.get(FieldType.AMOUNT.getName()).toString();
		objectArray[6] = "Customer refund";

		return objectArray;
	}

	public String getMerchantKey(String payId, String orderId, String acquirerName) {
		logger.info("getMerchantKey, payId={}, orderId={}, acquirerName={}", payId, orderId, acquirerName);
		User user = userDao.findPayId(payId);
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + payId + " and ORDER ID = " + orderId);
		} else {
			for (Account accountThis : accounts) {
				logger.info("accountThis " + accountThis);
				if (accountThis.getAcquirerName().equalsIgnoreCase(acquirerName)) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = null;
		try {
			accountCurrency = account.getAccountCurrency("356");
		} catch (SystemException e) {
			logger.info("Exception while generating refund file ", e);
		}

		String merchantName = accountCurrency.getMerchantId();
		logger.info("merchantName");
		return merchantName;

	}

	@Scheduled(cron = "0 0 0 * * *") // Every day at 12:00 A.M (0 0 0 * * *), (0 */30 * * * * - every 30 min for
										// testing purpose).
	private void resetDefaultMaxAmountForRouterConfiguration() {
		try {
			logger.info("Start Scheduler For Reset MaxAmount For RouterConfiguration" + new Date());
			routerConfigurationDao.resetRouterConfigurationMaxAmountForAll();
			logger.info("Completed Scheduler For Reset MaxAmount For RouterConfiguration" + new Date());
		} catch (Exception e) {
			logger.error("Exception in Reset Default MaxAmount For RouterConfiguration from scheduler", e);
		}

	}

	//@Scheduled(cron = "0 */30 * * * *")
	private void checkAcquirerDown_Or_Not() {
		try {
			logger.info("Start Scheduler For To Check Acquirer Down or Not :: " + new Date());
			List<AcquirerDownTimeConfiguration> acquirerDTInfo = acquirerDTConfigurationDao.getAllAcquirerDTConfig();
			logger.info("AcquirerDownTimeConfiguration :: " + acquirerDTInfo.toString());
			String respCodeList = configurationProvider.getResponseCodeAcqNotification();
			List<String> responseCode = Arrays.asList(respCodeList.split(","));
			logger.info("checkAcquirerDown_Or_Not, responseCode :: " + responseCode);

			acquirerDTInfo.forEach(action -> {
				logger.info("Action " + action.getAcquirerName());
				logger.info("Action " + action.getFailedCount());
				logger.info("Action " + action.getPaymentType());

				Map<String, String> response = fieldsDao.checkAcquirerDownStatus(action.getAcquirerName(),
						action.getPaymentType(), Integer.valueOf(action.getFailedCount()), action.getTimeSlab(),
						responseCode);
				if (response.get("status").equalsIgnoreCase("Y")) {
					// TODO Initiate Mail TO Business Team as "Acquirer Down".
					logger.info("Acquirer Down For AcquirerName = {}, PaymentType = {}, FailedCnt = {}",
							action.getAcquirerName(), action.getPaymentType(), action.getFailedCount());
					emailControllerServiceProvider.notifyToBusinessTeamAsAcquirerDown(action, response.get("downTime"));
				}
			});
			logger.info("Completed Scheduler For To Check Acquirer Down or Not :: " + new Date());
		} catch (Exception e) {
			logger.error("Exception in Check Acquirer Down or Not, from scheduler", e);
		}

	}

	@Scheduled(cron = "0 */30 * * * *")
	private void reActivateSmartRouterConfiguration() {
		try {
			logger.info("####### Start reActivateSmartRouterConfiguration ###### " + new Date());
			routerConfigurationDao.reActivateSmartRouterConfiguration();
			logger.info("####### Completed reActivateSmartRouterConfiguration ######### " + new Date());
		} catch (Exception e) {
			logger.error("Exception in reActivateSmartRouterConfiguration from scheduler", e);
		}

	}

	@Scheduled(cron = "0 */10 * * * *")
	private void reActivateSRConfiguration_UpiFailedCnt() {
		try {
			logger.info("Start reActivateSRConfiguration_UpiFailedCnt :: " + new Date());
			routerConfigurationDao.reActivateSmartRouterConfiguration();
			logger.info("Completed reActivateSRConfiguration_UpiFailedCnt :: " + new Date());
		} catch (Exception e) {
			logger.error("Exception in reActivateSRConfiguration_UpiFailedCnt from scheduler", e);
		}

	}

	
	//@Scheduled(cron = "0 */30 * * * *")
	private void irctcSettlementProcess() {
		try {
			logger.info("********************* scheduler start for irctcSettlementProcess *********************");
			String payID = configurationProvider.getIrctcPayId(); //"8019130508123423";
			List<Document> documents = transactionDataProvider.fetchIrctcSettlementData(payID);
			DateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMdd");
			File folder = new File("/home/radhey/RR/refundFiles/irctc/settlement");
			String fileNameFormat = "settlement_%s_BRDS_%s_V%d.txt";
			File output;
			int version = 1;
			while (true) {
				String fileName = String.format(fileNameFormat, "PAYTMMUPI", fileNameDateFormat.format(new Date()),
						version);
				output = new File(folder, fileName);
				if (output.exists()) {
					version++;
				} else {
					break;
				}
			}
			output.createNewFile();
			try (FileWriter writer = new FileWriter(output)) {
				for (Document document : documents) {
					String pgRefNum = String.valueOf(document.get("PG_REF_NUM"));
					String totalAmount = String.valueOf(document.get("TOTAL_AMOUNT"));
					String transactionDate = String.valueOf(document.get("CREATE_DATE"));
					String orderId = String.valueOf(document.get("ORDER_ID"));
					String mode = PaymentType.getpaymentName(String.valueOf(document.get("PAYMENT_TYPE")));
					writer.write(
							String.format("%s|%s|%s|%s|%s%n", pgRefNum, totalAmount, transactionDate, orderId, mode));
				}
				writer.flush();
			}catch (Exception e) {
				logger.info("Exception in irctcSettlementProcess :: " + e);
			}
			if (output.length() > 0) {
				String toMail = "sonu@pay10.com";
				sendMailWithAttachment(toMail, output);
			}
		} catch (Exception e) {
			logger.info("Exception in irctcSettlementProcess " + e);
		}
	}
	
	 	//@Scheduled(cron = "0 */10****")
	 /*private void irctcSettlement() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -2);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		String fromDate = dateFormat.format(calendar.getTime());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		String toDate = dateFormat.format(calendar.getTime());
		Query query = new Query();
		List<String> payIDs = new ArrayList<>();
		payIDs.add("1009130823112518");
		payIDs.add("1009130725144455");
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(Criteria.where("PAY_ID").in(payIDs));
		criteriaList.add(Criteria.where("TXNTYPE").is("SALE"));
		criteriaList
				.add(Criteria.where("STATUS").in(StatusTypeCustom.CAPTURED.getName(), StatusTypeCustom.RNS.getName()));
		criteriaList.add(Criteria.where("CREATE_DATE").gte(fromDate).lte(toDate));

		query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		List<Map> documents = mongoTemplate.find(query, Map.class, "transactionStatus");
		logger.error(String.valueOf(documents.size()));
		File folder = new File("D:\\irctc-reports");
		String fileNameFormat = "settlement_%s_BRDS_%s_V%d.txt";
		File output;
		int version = 1;
		while (true) {
			String fileName = String.format(fileNameFormat, "PAYTMMUPI", fileNameDateFormat.format(new Date()),
					version);
			output = new File(folder, fileName);
			if (output.exists()) {
				version++;
			} else {
				break;
			}
		}
		output.createNewFile();
		try (FileWriter writer = new FileWriter(output)) {
			for (Map document : documents) {
				String pgRefNum = String.valueOf(document.get("PG_REF_NUM"));
				String totalAmount = String.valueOf(document.get("TOTAL_AMOUNT"));
				String transactionDate = String.valueOf(document.get("CREATE_DATE"));
				String orderId = String.valueOf(document.get("ORDER_ID"));
				String mode = PaymentType.getpaymentName(String.valueOf(document.get("PAYMENT_TYPE")));
				writer.write(String.format("%s|%s|%s|%s|%s%n", pgRefNum, totalAmount, transactionDate, orderId, mode));
			}
			writer.flush();
		}
		if (output.length() > 0) {
			String toMail = "chinmay.kadam@pay10.com";
			sendMailWithAttachment(toMail, output);
		}
	}*/

	private void sendMailWithAttachment(String to, File file) {
		emailer.sendEmailWithTextAndAttachment("Settlement File", "IRCTC Settlement File", to, "", false, file.getName(), file);
	}

	
	//@Scheduled(cron = "0 */30 * * * *")
	private void irctcRefundFileGenerationProcess() throws Exception {
		try {
			logger.info("********** scheduler start for irctcRefundFileGenerationProcess *****************");
			String payID = configurationProvider.getIrctcPayId(); //"8019130508123423";
			List<Document> documents = transactionDataProvider.fetchirctcRefundFileGenerationData(payID, "R");
			DateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMdd");
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			logger.error(String.valueOf(documents.size()));
			File folder = new File("/home/radhey/RR/refundFiles/irctc/download");
			String fileNameFormat = "refundvalidation_%s_BRDS_%s_V%d.txt";
			File output;
			int version = 1;
			while (true) {
				String fileName = String.format(fileNameFormat, "PAY10UPI", fileNameDateFormat.format(new Date()),
						version);
				output = new File(folder, fileName);
				if (output.exists()) {
					version++;
				} else {
					break;
				}
			}
			try (FileWriter writer = new FileWriter(output)) {
				for (Map document : documents) {
					String orderId = String.valueOf(document.get("ORDER_ID"));
					String refundType = "NA";
					String amount = String.valueOf(document.get("AMOUNT"));
					String origTxnId = String.valueOf(document.get("RRN"));
					String status = String.valueOf(document.get("STATUS"));
					String createDate = String.valueOf(document.get("CREATE_DATE"));
					String authCode = String.valueOf(document.get("AUTH_CODE"));
					String refundCancelledDate = "NA";
					String totalAmount = String.valueOf(document.get("TOTAL_AMOUNT"));
					String refundCancelledId = "NA";
					String remarks = "NA";
					writer.write(String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s%n", orderId, refundType, amount,
							origTxnId, status, createDate, authCode, refundCancelledDate, totalAmount,
							refundCancelledId, remarks));
				}
				writer.flush();
				if (output.length() > 0) {
					IRCTCRefundFile irctcRefundFile = new IRCTCRefundFile();
					irctcRefundFile.setFileName(output.getName());
					irctcRefundFile.setFilePath(output.getAbsolutePath());
					irctcRefundFile.setPayId(payID);
					irctcRefundFile.setDate(df1.format(new Date()));
					irctcRefundFileService.save(irctcRefundFile);
				}
			}
			
		}catch (Exception e) {
			logger.info("Exception in irctcRefundFileGenerationProcess " + e);
		}
	}
	
	//@Scheduled(cron = "0 */30 * * * *")
	private void irctcNgetRefundFileGenerationProcess() throws Exception {
		try {
			logger.info("********** scheduler start for irctcNgetRefundFileGenerationProcess *****************");
			String payID = configurationProvider.getIrctcPayId(); //"8019130508123423";
			List<Document> documents = transactionDataProvider.fetchirctcRefundFileGenerationData(payID, "N");
			DateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMdd");
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			logger.error(String.valueOf(documents.size()));
			File folder = new File("/home/radhey/RR/refundFiles/irctc/download");
			String fileNameFormat = "ngetdeltaconfirm_%s_BRDS_%s_V%d.txt";
			File output;
			int version = 1;
			while (true) {
				String fileName = String.format(fileNameFormat, "PAY10UPI00000", fileNameDateFormat.format(new Date()),
						version);
				output = new File(folder, fileName);
				if (output.exists()) {
					version++;
				} else {
					break;
				}
			}
			try (FileWriter writer = new FileWriter(output)) {
				for (Map document : documents) {
					String orderId = String.valueOf(document.get("ORDER_ID"));
					String refundType = "NA";
					String amount = String.valueOf(document.get("AMOUNT"));
					String origTxnId = String.valueOf(document.get("RRN"));
					String status = String.valueOf(document.get("STATUS"));
					String createDate = String.valueOf(document.get("CREATE_DATE"));
					String authCode = String.valueOf(document.get("AUTH_CODE"));
					String refundCancelledDate = "NA";
					String totalAmount = String.valueOf(document.get("TOTAL_AMOUNT"));
					String refundCancelledId = "NA";
					String remarks = document.get("STATUS") !=null && document.get("STATUS").equals("Captured") ? "Success" : "Failed";
					writer.write(String.format("%s|%s|%s|%s|%s|%s|%s|%s%n", orderId, createDate, authCode,
							amount, status, remarks, createDate, origTxnId));
				}
				writer.flush();
				if (output.length() > 0) {
					IRCTCRefundFile irctcRefundFile = new IRCTCRefundFile();
					irctcRefundFile.setFileName(output.getName());
					irctcRefundFile.setFilePath(output.getAbsolutePath());
					irctcRefundFile.setPayId(payID);
					irctcRefundFile.setDate(df1.format(new Date()));
					irctcRefundFileService.save(irctcRefundFile);
				}
			}
			
		}catch (Exception e) {
			logger.info("Exception in irctcRefundFileGenerationProcess " + e);
		}
	}
	
	//@Scheduled(cron = "0 */10 * * * *")
	/*private void irctcRefundFileGeneration() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -2);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		String fromDate = dateFormat.format(calendar.getTime());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		String toDate = dateFormat.format(calendar.getTime());
		Query query = new Query();
		List<String> payIDs = new ArrayList<>();
		payIDs.add("1009130823112518");
		payIDs.add("1009130725144455");
		for (String payID : payIDs) {
			List<Criteria> criteriaList = new ArrayList<>();
			criteriaList.add(Criteria.where("PAY_ID").is(payID));
			criteriaList.add(Criteria.where("TXNTYPE").is("REFUND"));
			criteriaList.add(
					Criteria.where("STATUS").in(StatusTypeCustom.CAPTURED.getName(), StatusTypeCustom.RNS.getName()));
			criteriaList.add(Criteria.where("CREATE_DATE").gte(fromDate).lte(toDate));
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
			List<Map> documents = mongoTemplate.find(query, Map.class, "transactionStatus");
			logger.error(String.valueOf(documents.size()));
			File folder = new File("D:\\irctc-reports");
			String fileNameFormat = "refundvalidation_%s_BRDS_%s_V%d.txt";
			File output;
			int version = 1;
			while (true) {
				String fileName = String.format(fileNameFormat, "PAYTMMUPI", fileNameDateFormat.format(new Date()),
						version);
				output = new File(folder, fileName);
				if (output.exists()) {
					version++;
				} else {
					break;
				}
			}
			try (FileWriter writer = new FileWriter(output)) {
				for (Map document : documents) {
					String orderId = String.valueOf(document.get("ORDER_ID"));
					String refundType = "NA";
					String amount = String.valueOf(document.get("AMOUNT"));
					String origTxnId = String.valueOf(document.get("RRN"));
					String status = String.valueOf(document.get("STATUS"));
					String createDate = String.valueOf(document.get("CREATE_DATE"));
					String authCode = String.valueOf(document.get("AUTH_CODE"));
					String refundCancelledDate = "NA";
					String totalAmount = String.valueOf(document.get("TOTAL_AMOUNT"));
					String refundCancelledId = "NA";
					String remarks = "NA";
					writer.write(String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s%n", orderId, refundType, amount,
							origTxnId, status, createDate, authCode, refundCancelledDate, totalAmount,
							refundCancelledId, remarks));
				}
				writer.flush();
				if (output.length() > 0) {
					IRCTCRefundFile irctcRefundFile = new IRCTCRefundFile();
					irctcRefundFile.setFileName(output.getName());
					irctcRefundFile.setFilePath(output.getAbsolutePath());
					irctcRefundFile.setPayId(payID);
					irctcRefundFileService.save(irctcRefundFile);
				}
			}
		}
	}*/
}
