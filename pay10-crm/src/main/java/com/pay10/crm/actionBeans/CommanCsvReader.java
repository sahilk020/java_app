package com.pay10.crm.actionBeans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.BatchTransactionObj;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.BinRange;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.FraudPreventionObj;
import com.pay10.commons.util.FraudRuleType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.TxnChannel;
import com.pay10.commons.util.Weekdays;
import com.pay10.crm.action.StatusEnquiryParameters;

import au.com.bytecode.opencsv.CSVReader;

@Service
public class CommanCsvReader {

	/*
	 * @Autowired private CrmValidator validator;
	 */

	
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	CrmValidator validator = new CrmValidator();
	private static final long serialVersionUID = 451478670043548529L;
	private static Logger logger = LoggerFactory.getLogger(CommanCsvReader.class.getName());

	// 1. parse batch file for refund
	public BatchResponseObject createRefundList(String fileName) {
		List<BatchTransactionObj> refundList = new LinkedList<BatchTransactionObj>();
		BatchResponseObject batchResponseObject = new BatchResponseObject();
		// Build reader instance
		CSVReader csvReader;
		StringBuilder message = new StringBuilder();
		String line = "";
		try {
			// Start reading from line number 2 (line numbers start from zero)
			csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 1);
			String[] nextLine;
			try {
				while ((nextLine = csvReader.readNext()) != null) {
					try {
						line = Arrays.toString(nextLine).replace("[", "").replace("]", "");
						String[] lineobj = line.split(",");
						// prepare refund fields
						BatchTransactionObj refund = createRefund(lineobj);
						// Verifying the file data here
						if (validateBatchFileRefund(refund)) {
							refundList.add(refund);
						} else {
							message.append(ErrorType.VALIDATION_FAILED.getResponseMessage() + " order id: "
									+ refund.getOrderId());
							continue;
						}
					} catch (Exception exception) {
						logger.error("Exception", exception);
					}
				}
			} catch (IOException exception) {
				logger.error("Exception", exception);
			}
		} catch (FileNotFoundException exception) {
			logger.error("Exception", exception);
		}
		batchResponseObject.setResponseMessage(message.toString());
		batchResponseObject.setBatchTransactionList(refundList);
		return batchResponseObject;
	}

	public boolean validateBatchFileRefund(BatchTransactionObj refund) {

		if ((validator.validateBlankField(refund.getPayId()))) {
		} else if (!validator.validateField(CrmFieldType.PAY_ID, refund.getPayId())) {
			return false;
		}

		if ((validator.validateBlankField(refund.getOrigTxnId()))) {
		} else if (!validator.validateField(CrmFieldType.TXN_ID, refund.getOrigTxnId())) {
			return false;
		}
		if ((validator.validateBlankField(refund.getAmount()))) {
		} else if (!validator.validateField(CrmFieldType.AMOUNT, refund.getAmount())) {
			return false;
		}
		if ((validator.validateBlankField(refund.getTxnType()))) {
		} else if (!validator.validateField(CrmFieldType.TXNTYPE, refund.getTxnType())) {
			return false;
		}
//		if ((validator.validateBlankField(refund.getCurrencyCode()))) {
//		} else if (!validator.validateField(CrmFieldType.CURRENCY, refund.getCurrencyCode())) {
//			return false;
//		}
		if ((validator.validateBlankField(refund.getOrderId()))) {
		} else if (!validator.validateField(CrmFieldType.ORDER_ID, refund.getOrderId())) {
			return false;
		}
		return true;

	}

	public Map<String, String> fieldsRequestMap(BatchTransactionObj obj, User sessionUser) {
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(FieldType.ORIG_TXN_ID.getName(), obj.getOrigTxnId());
		requestMap.put(FieldType.TXNTYPE.getName(), obj.getTxnType());
		requestMap.put(FieldType.AMOUNT.getName(), obj.getAmount());
		requestMap.put(FieldType.CURRENCY_CODE.getName(), obj.getCurrencyCode());
		requestMap.put(FieldType.HASH.getName(), "1234567890123456789012345678901234567890123456789012345678901234");
		requestMap.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
		requestMap.put(FieldType.INTERNAL_TXN_CHANNEL.getName(), TxnChannel.EXTERNAL_BATCH_FILE.getCode());
		requestMap.put(FieldType.INTERNAL_USER_EMAIL.getName(), sessionUser.getEmailId());

		if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
			requestMap.put(FieldType.PAY_ID.getName(), sessionUser.getPayId());
		} else {
			requestMap.put(FieldType.PAY_ID.getName(), obj.getPayId());
		}
		return requestMap;
	}

	private static BatchTransactionObj createRefund(String[] data) {
		return new BatchTransactionObj(data[0], data[1], data[2], data[3], data[4], data[5]);
	}

	// 2. batch file status update
	public LinkedList<StatusEnquiryParameters> prepareStatusUpdateList(String fileName) {

		List<StatusEnquiryParameters> statusEnquiryParametersList = new LinkedList<StatusEnquiryParameters>();
		/* CSVReader csvReader; */
		String line = "";
		try {
			// Start reading from line number 2 (line numbers start from zero)
			CSVReader csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 1);
			String[] nextLine;
			try {
				while ((nextLine = csvReader.readNext()) != null) {
					try {
						line = Arrays.toString(nextLine).replace("[", "").replace("]", "");
						String[] lineobj = line.split(",");
						StatusEnquiryParameters statusEnquiryParameters = statusEnquiryParameters(lineobj);
						statusEnquiryParametersList.add(statusEnquiryParameters);

					} catch (Exception exception) {
						logger.error("Exception", exception);
					}
				}

			} catch (IOException exception) {
				logger.error("Exception", exception);
			}
		} catch (FileNotFoundException exception) {
			logger.error("Exception", exception);
		}
		return (LinkedList<StatusEnquiryParameters>) statusEnquiryParametersList;
	}

	public static StatusEnquiryParameters statusEnquiryParameters(String data[]) {
		return new StatusEnquiryParameters(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7],
				data[8], data[9], data[10], data[11], data[12], data[13], data[14], data[15], data[16], data[17],
				data[18], data[19]);
	}

	public Map<String, String> prepareFields(StatusEnquiryParameters statusEnquiryParameters) {
		Map<String, String> requestMap = new HashMap<String, String>();

		requestMap.put(FieldType.PAY_ID.getName(), statusEnquiryParameters.getPayId());
		requestMap.put(FieldType.TXN_ID.getName(), statusEnquiryParameters.getTxnId());
		requestMap.put(FieldType.TXNTYPE.getName(), TransactionType.STATUS.getName());
		requestMap.put(FieldType.ACQUIRER_TYPE.getName(), statusEnquiryParameters.getAcquirerType());
		requestMap.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), statusEnquiryParameters.getTxnType());
		requestMap.put(FieldType.CURRENCY_CODE.getName(), statusEnquiryParameters.getCurrencyCode());
		requestMap.put(FieldType.OID.getName(), statusEnquiryParameters.getOrderId());
		requestMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), statusEnquiryParameters.getOrigTxnId());
		requestMap.put(FieldType.PG_DATE_TIME.getName(), statusEnquiryParameters.getPgDateTime());
		requestMap.put(FieldType.STATUS.getName(), statusEnquiryParameters.getStatus());
		requestMap.put(FieldType.RESPONSE_CODE.getName(), statusEnquiryParameters.getResponseCode());
		requestMap.put(FieldType.RESPONSE_MESSAGE.getName(), statusEnquiryParameters.getResponseMessage());
		requestMap.put(FieldType.ACQ_ID.getName(), statusEnquiryParameters.getAcqId());
		requestMap.put(FieldType.PG_REF_NUM.getName(), statusEnquiryParameters.getPgRefNum());
		requestMap.put(FieldType.AUTH_CODE.getName(), statusEnquiryParameters.getAuthCode());
		requestMap.put(FieldType.PG_RESP_CODE.getName(), statusEnquiryParameters.getPgRespCode());
		requestMap.put(FieldType.PG_TXN_MESSAGE.getName(), statusEnquiryParameters.getPgTxnMessage());
		requestMap.put(FieldType.PG_DATE_TIME.getName(), statusEnquiryParameters.getPgDateTime());
		requestMap.put(FieldType.CURRENCY_CODE.getName(), statusEnquiryParameters.getCurrencyCode());

		return requestMap;
	}

	// 3. batch file for invoice create
	public BatchResponseObject csvReaderForBatchEmailSend(String fileName) {
		// Build reader instance
		CSVReader csvReader = null;
		List<Invoice> emailPhoneList = new LinkedList<Invoice>();
		BatchResponseObject batchResponseObject = new BatchResponseObject();
		StringBuilder message = new StringBuilder();
		String line = "";

		try {
			// Start reading from line number 2 (line numbers start from zero)
			csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 1);
			String[] nextLine;

			try {
				while ((nextLine = csvReader.readNext()) != null) {
					try {
						line = Arrays.toString(nextLine).replace("[", "").replace("]", "");
						String[] lineobj = line.split(",");

						Invoice batchInvoice = batchInvoiceSend(lineobj);
						if (emailPhoneList.size() > 10000) {
							logger.error("File contains entries beyond limit");
							message.append(ErrorType.VALIDATION_FAILED.getResponseMessage()
									+ " : File contains entries beyond limit");
							break;
						}

						if (batchInvoice != null && validator.validateBatchEmailPhone(batchInvoice)) {
							emailPhoneList.add(batchInvoice);
						} else {
							logger.error("Invalid data in invoice csv file :  " + line);
							message.append(ErrorType.VALIDATION_FAILED.getResponseMessage()
									+ " : Invalid data in invoice csv file :  " + line);
//							csvReader.close();
//							return null;
						}
					} catch (Exception exception) {
						message.append(ErrorType.VALIDATION_FAILED.getResponseMessage() + " Email Id: " + line);
						logger.error("Exception", exception);
					}
				}
			} catch (IOException exception) {
				logger.error("Exception", exception);
			}
		} catch (FileNotFoundException exception) {
			logger.error("Exception", exception);
		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException e) {
					logger.error("Failed to close csv reader");
					e.printStackTrace();
				}
			}

		}
		batchResponseObject.setResponseMessage(message.toString());
		batchResponseObject.setInvoiceEmailList(emailPhoneList);
		return batchResponseObject;
	}

	public BatchResponseObject csvReaderForBatchEmailSend(Invoice invoice) {
		// Build reader instance
		CSVReader csvReader = null;
		List<Invoice> emailPhoneList = new LinkedList<Invoice>();
		BatchResponseObject batchResponseObject = new BatchResponseObject();
		StringBuilder message = new StringBuilder();

		if (validator.validateBatchEmailPhone(invoice)) {
			emailPhoneList.add(invoice);
		} else {
			logger.error("Invalid data in invoice csv file :  " + new Gson().toJson(invoice));
		}
		
		batchResponseObject.setResponseMessage(message.toString());
		batchResponseObject.setInvoiceEmailList(emailPhoneList);
		return batchResponseObject;
	}
	
	public List<Invoice> csvReaderForBatch(String fileName,String businessName,String expiresHour) {
		// Build reader instance
		CSVReader csvReader = null;
		//List<Invoice> emailPhoneList = new LinkedList<Invoice>();
		List<Invoice> invoiceList = new LinkedList<Invoice>();
//		BatchResponseObject batchResponseObject = new BatchResponseObject();
//		StringBuilder message = new StringBuilder();
		String line = "";
		
		
		String finalinvoiceno="";
		
		String[] shortName=businessName.split(" ");
		
		//String shortBusinessName=shortName[0];
		String finalBusinessname = "";
		
		if (shortName.length > 0) {
			finalBusinessname = shortName[0];
		} else if (businessName.length() > 5) {
			finalBusinessname = businessName.substring(0, 5);
		} else {
			finalBusinessname = businessName;
		}
		
		finalinvoiceno="Pro-"+finalBusinessname.trim()+"-Pay10";
		
		try {
			// Start reading from line number 2 (line numbers start from zero)
			csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 1);
			String[] nextLine;

			try {
				while ((nextLine = csvReader.readNext()) != null) {
					try {
						
						line = Arrays.toString(nextLine).replace("[", "").replace("]", "");
						String[] lineobj = line.split(",");
						
						Invoice invoice=new Invoice();
						
						invoice.setInvoiceNo(finalinvoiceno+"-"+System.nanoTime());
						invoice.setName(lineobj[0]);
						invoice.setPhone(lineobj[1]);
						invoice.setEmail(lineobj[2]);
						invoice.setAmount(lineobj[3]);
						invoice.setProductName(lineobj[4]);
						invoice.setQuantity(lineobj[5]);
						invoice.setGst(lineobj[6]);
						invoice.setCurrencyCode(multCurrencyCodeDao.getCurrencyCodeByName(lineobj[7]));
						invoice.setRegion(lineobj[8]);
						
						invoice.setReturnUrl("");
						invoice.setBusinessName(businessName);
						invoice.setExpiresHour(expiresHour);
						
						invoiceList.add(invoice);
												
					} catch (Exception exception) {
						//message.append(ErrorType.VALIDATION_FAILED.getResponseMessage() + " Email Id: " + line);
						
						logger.error("Exception", exception);
					}
				}
			} catch (IOException exception) {
				logger.error("Exception", exception);
			}
		} catch (FileNotFoundException exception) {
			logger.error("Exception", exception);
		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException e) {
					logger.error("Failed to close csv reader");
					e.printStackTrace();
				}
			}

		}
		
		return invoiceList;
	}
	
	private static Invoice batchInvoiceSend(String[] data) {
		if (data.length == 7) {
			//1 position mobile 2 position email
			return new Invoice(data[2], data[1]);
		}
		logger.error("Promotional Invoice csv file data length is incorrect : " + data.length);
		return null;
	}

	public BatchResponseObject csvReaderForBinRange(String fileName) {
		CSVReader csvReader = null;
		List<BinRange> binRangeList = new LinkedList<BinRange>();
		BatchResponseObject batchResponseObject = new BatchResponseObject();
		StringBuilder message = new StringBuilder();
		String line = "";
		try {
			// Start reading from line number 2 (line numbers start from zero)
			csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 1);
			String[] nextLine;

			try {
				while ((nextLine = csvReader.readNext()) != null) {
					try {
						line = Arrays.toString(nextLine).replace("[", "").replace("]", "");
						String lineobj[] = line.split(",");
						if (lineobj.length != 12) {
							continue;
						}
						BinRange binRangeObject = batchBinRange(lineobj);

						if (validator.validateBinRangeFile(binRangeObject)) {
							binRangeList.add(binRangeObject);
						} else {
							message.append(ErrorType.VALIDATION_FAILED.getResponseMessage() + " Id: "
									+ binRangeObject.getId());
							continue;
						}
					} catch (Exception exception) {
						message.append(ErrorType.VALIDATION_FAILED.getResponseMessage() + " Id: " + line);
						logger.error("Exception", exception);
					}
				}
			} catch (IOException exception) {
				logger.error("Exception", exception);
			}
		} catch (FileNotFoundException exception) {
			logger.error("Exception", exception);
		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException e) {
					logger.error("Failed to close csvReader");
					e.printStackTrace();
				}
			}
		}
		batchResponseObject.setResponseMessage(message.toString());
		batchResponseObject.setBinRangeResponseList(binRangeList);
		return batchResponseObject;
	}

	private BinRange batchBinRange(String[] data) {
		return new BinRange(data[0], data[1], data[2], data[3], PaymentType.getInstanceUsingCode(data[4]), data[5],
				data[6], data[7], MopType.getInstanceIgnoreCase(data[8]), data[9], data[10], data[11]);
	}

	// Below method used for Bulk imports or Delete the fraud prevention
	public BatchResponseObject csvReaderForFraudPrevention(User sessionUser, File fileName, String payId,
			String rule,String currency) {

		CSVReader csvReader = null;
		List<FraudPreventionObj> fraudPreventionObjList = new LinkedList<FraudPreventionObj>();
		BatchResponseObject batchResponseObject = new BatchResponseObject();
		StringBuilder message = new StringBuilder();
		String line = "";
		try {
			// Start reading from line number 2 (line numbers start from zero)
			csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 1);
			String[] nextLine;

			try {
				while ((nextLine = csvReader.readNext()) != null) {
					try {
						line = Arrays.toString(nextLine).replace("[", "").replace("]", "").replace("\"", "");
						logger.info("LINE======================================{}", line);
						String lineobj[] = line.split(",");
						if ((lineobj.length == 10 || rule.equalsIgnoreCase(FraudRuleType.REPEATED_MOP_TYPES.name())|| rule.equalsIgnoreCase(FraudRuleType.BLOCK_VPA_ADDRESS.name()))
								&& (sessionUser.getUserType().equals(UserType.ADMIN)
										|| sessionUser.getUserType().equals(UserType.SUBADMIN))) {
							payId = lineobj[0];
							if (StringUtils.isNotBlank(payId)) {
								String[] lineobjAdmin = new String[lineobj.length - 1];
								if (rule.equalsIgnoreCase(FraudRuleType.REPEATED_MOP_TYPES.name())) {
									lineobjAdmin[0] = lineobj[1];
									lineobjAdmin[1] = lineobj[2];
									lineobjAdmin[2] = lineobj[3];
									lineobjAdmin[3] = lineobj[4];
								} 
								if (rule.equalsIgnoreCase(FraudRuleType.BLOCK_VPA_ADDRESS.name())){
									lineobjAdmin[0] = lineobj[1];
									lineobjAdmin[1] = lineobj[2];
									lineobjAdmin[2] = lineobj[3];
									lineobjAdmin[3] = lineobj[4];
									lineobjAdmin[4] = lineobj[5];
									lineobjAdmin[5] = lineobj[6];
									lineobjAdmin[6] = lineobj[7];
									//lineobjAdmin[7] = lineobj[8];
								}
								else {
									lineobjAdmin[0] = lineobj[1];
									lineobjAdmin[1] = lineobj[2];
									lineobjAdmin[2] = lineobj[3];
									lineobjAdmin[3] = lineobj[4];
									lineobjAdmin[4] = lineobj[5];
									lineobjAdmin[5] = lineobj[6];
									lineobjAdmin[6] = lineobj[7];
									lineobjAdmin[7] = lineobj[8];
									lineobjAdmin[8] = lineobj[9];
								}
								FraudPreventionObj fraudPreventionObj = batchRulesforFp(lineobjAdmin, payId, rule,currency);
								if (validator.validateFraudPrevention(fraudPreventionObj)
										&& (rule.equalsIgnoreCase(FraudRuleType.BLOCK_VPA_ADDRESS.name())) || pastDatesValidation(lineobjAdmin, rule)) {
									fraudPreventionObjList.add(fraudPreventionObj);
								} else {
									message.append(ErrorType.VALIDATION_FAILED.getResponseMessage() + " Id: "
											+ fraudPreventionObj.getId());
									continue;
								}
							} else {
								message.append(ErrorType.VALIDATION_FAILED.getResponseMessage()
										+ " payId is not correct: " + payId);
								continue;
							}

						} else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
							FraudPreventionObj fraudPreventionObj = batchRulesforFp(lineobj, payId, rule,currency);

							if (validator.validateFraudPrevention(fraudPreventionObj)
									&& pastDatesValidation(lineobj, rule)) {
								fraudPreventionObjList.add(fraudPreventionObj);
							} else {
								message.append(ErrorType.VALIDATION_FAILED.getResponseMessage() + " Id: "
										+ fraudPreventionObj.getId());
								continue;
							}

						} else {
							payId = lineobj[0];
							if (StringUtils.isNotBlank(payId)) {
								String[] lineobjPerCardTxn = { lineobj[1], lineobj[2], lineobj[3], lineobj[4],
										lineobj[5], lineobj[6], lineobj[7],lineobj[8] };
								FraudPreventionObj fraudPreventionObj = batchRulesforFp(lineobjPerCardTxn, payId, rule,currency);
								if (validator.validateFraudPrevention(fraudPreventionObj)
										|| pastDatesValidationforPerCardTxn(lineobjPerCardTxn, rule)) {
									fraudPreventionObjList.add(fraudPreventionObj);
								} else {
									message.append(ErrorType.VALIDATION_FAILED.getResponseMessage() + " Id: "
											+ fraudPreventionObj.getId());
									continue;
								}
							} else {
								message.append(ErrorType.VALIDATION_FAILED.getResponseMessage()
										+ " payId is not correct: " + payId);
								continue;
							}
						}

					} catch (Exception exception) {
						message.append(ErrorType.VALIDATION_FAILED.getResponseMessage() + " Id: " + line);
						logger.error("Exception", exception);
					}
				}
			} catch (IOException exception) {
				logger.error("Exception", exception);
			}
		} catch (FileNotFoundException exception) {
			logger.error("Exception", exception);
		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException e) {
					logger.error("Failed to close csvReader");
					e.printStackTrace();
				}
			}
		}
		batchResponseObject.setResponseMessage(message.toString());
		batchResponseObject.setFraudPreventionObjList(fraudPreventionObjList);
		return batchResponseObject;
	}

	private boolean pastDatesValidation(String[] data, String rule) throws ParseException {
		if (Arrays.asList(data).contains("TRUE") || rule.equalsIgnoreCase(FraudRuleType.REPEATED_MOP_TYPES.name())) {
			return true;
		} else {
			SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
			Date currentDate = sdformat.parse(sdformat.format(new Date()));
			Date startDate = sdformat.parse(data[2]);
			Date endDate = sdformat.parse(data[3]);
			if ((currentDate.before(startDate) || currentDate.equals(startDate))
					&& (startDate.before(endDate) || startDate.equals(endDate))) {
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean pastDatesValidationforPerCardTxn(String[] data, String rule) throws ParseException {
		if (Arrays.asList(data).contains("TRUE") || rule.equalsIgnoreCase(FraudRuleType.REPEATED_MOP_TYPES.name())) {
			return true;
		} else {
			SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
			Date currentDate = sdformat.parse(sdformat.format(new Date()));
			Date startDate = sdformat.parse(
					StringUtils.containsIgnoreCase(data[2], "/") ? StringUtils.replace(data[2], "/", "-") : data[2]);
			Date endDate = sdformat.parse(
					StringUtils.containsIgnoreCase(data[3], "/") ? StringUtils.replace(data[3], "/", "-") : data[3]);
			if ((currentDate.before(startDate) || currentDate.equals(startDate))
					&& (startDate.before(endDate) || startDate.equals(endDate))) {
				return true;
			} else {
				return false;
			}
		}
	}

	private FraudPreventionObj batchRulesforFp(String[] data, String payId, String rule, String currency) {
		FraudPreventionObj fraudPreventionObj = new FraudPreventionObj();
		FraudRuleType fraudType = FraudRuleType.getInstance(rule);
		fraudPreventionObj.setCurrency(currency);
		switch (fraudType) {
		case BLOCK_NO_OF_TXNS:
			break;
		case BLOCK_TXN_AMOUNT:
			// No need of bulk import
			break;
		case BLOCK_IP_ADDRESS:
			fraudPreventionObj.setPayId(payId);
			fraudPreventionObj.setIpAddress(data[0]);
			fraudPreventionObj.setFraudType(fraudType);
			if (Arrays.asList(data).contains("TRUE")) {
				fraudPreventionObj.setAlwaysOnFlag(true);
				return fraudPreventionObj;
			} else {
				return updateDatesAndDaysforRules(fraudPreventionObj, data[1], data[2], data[3], data[4], data[5]);
			}
		case BLOCK_EMAIL_ID:
			fraudPreventionObj.setPayId(payId);
			fraudPreventionObj.setEmail(data[0]);
			fraudPreventionObj.setFraudType(fraudType);
			if (Arrays.asList(data).contains("TRUE")) {
				fraudPreventionObj.setAlwaysOnFlag(true);
				return fraudPreventionObj;
			} else {
				return updateDatesAndDaysforRules(fraudPreventionObj, data[1], data[2], data[3], data[4], data[5]);
			}
		case BLOCK_PHONE_NUMBER:
			fraudPreventionObj.setPayId(payId);
			fraudPreventionObj.setPhone(data[0].replace("\"", ""));
			fraudPreventionObj.setFraudType(fraudType);
			if (Arrays.asList(data).contains("TRUE")) {
				fraudPreventionObj.setAlwaysOnFlag(true);
				return fraudPreventionObj;
			} else {
				return updateDatesAndDaysforRules(fraudPreventionObj, data[1], data[2], data[3], data[4], data[5]);
			}
		case BLOCK_USER_COUNTRY:
			break;
		case BLOCK_CARD_ISSUER_COUNTRY:
			fraudPreventionObj.setPayId(payId);
			fraudPreventionObj.setIssuerCountry(data[0]);
			fraudPreventionObj.setFraudType(fraudType);
			return fraudPreventionObj;
		case BLOCK_CARD_BIN:
			fraudPreventionObj.setPayId(payId);
			fraudPreventionObj.setNegativeBin(data[0].replace("\"", ""));
			fraudPreventionObj.setFraudType(fraudType);
			if (Arrays.asList(data).contains("TRUE")) {
				fraudPreventionObj.setAlwaysOnFlag(true);
				return fraudPreventionObj;
			} else {
				return updateDatesAndDaysforRules(fraudPreventionObj, data[1], data[2], data[3], data[4], data[5]);
			}
		case BLOCK_CARD_NO:
			fraudPreventionObj.setPayId(payId);
			fraudPreventionObj.setNegativeCard(data[0].replace("\"", ""));
			fraudPreventionObj.setFraudType(fraudType);
			fraudPreventionObj.setBlockTimeUnits(data[8]);
			if (Arrays.asList(data).contains("TRUE")) {
				fraudPreventionObj.setAlwaysOnFlag(true);
				return fraudPreventionObj;
			} else {
				return updateDatesAndDaysforRules(fraudPreventionObj, data[1], data[2], data[3], data[4], data[5]);
			}
		case BLOCK_CARD_TXN_THRESHOLD:
			fraudPreventionObj.setPayId(payId);
			fraudPreventionObj.setNegativeCard(data[0].replace("\"", ""));
			fraudPreventionObj.setPerCardTransactionAllowed(data[1]);
			fraudPreventionObj.setFraudType(fraudType);
			fraudPreventionObj.setBlockTimeUnits(data[8]);
			if (Arrays.asList(data).contains("TRUE")) {
				fraudPreventionObj.setAlwaysOnFlag(true);
				return fraudPreventionObj;
			} else {
				return updateDatesAndDaysforRules(fraudPreventionObj, data[2], data[3], data[4], data[5], data[6]);
			}
		case BLOCK_MACK_ADDRESS:
			fraudPreventionObj.setPayId(payId);
			fraudPreventionObj.setMackAddress(data[0]);
			fraudPreventionObj.setFraudType(fraudType);
			if (Arrays.asList(data).contains("TRUE")) {
				fraudPreventionObj.setAlwaysOnFlag(true);
				return fraudPreventionObj;
			} else {
				return updateDatesAndDaysforRules(fraudPreventionObj, data[1], data[2], data[3], data[4], data[5]);
			}
		case REPEATED_MOP_TYPES:
			fraudPreventionObj.setPayId(payId);
			fraudPreventionObj.setPaymentType(data[0]);
			fraudPreventionObj.setPercentageOfRepeatedMop(Double.valueOf(data[3]));
			fraudPreventionObj.setFraudType(fraudType);
			fraudPreventionObj.setAlwaysOnFlag(true);
			fraudPreventionObj.setBlockTimeUnits(data[2]);
			String emailToNotify = data[1];
			emailToNotify = StringUtils.replace(emailToNotify, ":", ",");
			fraudPreventionObj.setEmailToNotify(emailToNotify);
		case BLOCK_VPA_ADDRESS:
			fraudPreventionObj.setPayId(payId);
			String vpaAddress = data[0];
			//vpaAddress = StringUtils.replace(vpaAddress, ":", ",");
			fraudPreventionObj.setVpaAddress(vpaAddress);
			fraudPreventionObj.setFraudType(fraudType);
			if (Arrays.asList(data).contains("TRUE")) {
				fraudPreventionObj.setAlwaysOnFlag(true);
				return fraudPreventionObj;
			} else {
				return updateDatesAndDaysforRules(fraudPreventionObj, data[1], data[2], data[3], data[4], data[5]);
			}
		default:
			logger.error("Something went wrong while checking fraud field values");
			break;
		}

		return fraudPreventionObj;
	}

	public int fileSizecount(File fileName) throws IOException {

		int count = 0;
		CSVReader csvReader = null;
		csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 1);

		String[] nextLine;

		while ((nextLine = csvReader.readNext()) != null) {
			count++;
		}
		return count;

	}

	private FraudPreventionObj updateDatesAndDaysforRules(FraudPreventionObj fraudPreventionObj, String activeFrom,
			String activeTo, String startTime, String endTime, String days) {

		StringBuilder datefrom = new StringBuilder();
		StringBuilder dateTo = new StringBuilder();
		String formattedStartTime = "";
		String formattedEndTime = "";
		String dayCode = "";

		// Date from and date to format changed
		String dateActiveFrom = activeFrom;
		String dateActiveTo = activeTo;
		datefrom = datefrom.append(dateActiveFrom.substring(6)).append("").append(dateActiveFrom.substring(3, 5))
				.append("").append(dateActiveFrom.substring(0, 2));

		dateTo = dateTo.append(dateActiveTo.substring(6)).append("").append(dateActiveTo.substring(3, 5)).append("")
				.append(dateActiveTo.substring(0, 2));

		// Start time and end time format changes
		formattedStartTime = startTime.replaceAll(":", "");
		formattedEndTime = endTime.replaceAll(":", "");

		// weekdays format changed
		String[] details = days.split(" ");
		StringBuilder dayCodes = new StringBuilder();

		for (String dayname : details) {
			Weekdays dayInstance = Weekdays.getInstanceIgnoreCase(dayname);
			dayCode = dayInstance.getCode();
			dayCodes.append(dayCode);
			dayCodes.append(",");
		}

		dayCode = dayCodes.toString().substring(0, dayCodes.length() - 1);

		fraudPreventionObj.setDateActiveFrom(datefrom.toString());
		fraudPreventionObj.setDateActiveTo(dateTo.toString());
		fraudPreventionObj.setStartTime(formattedStartTime);
		fraudPreventionObj.setEndTime(formattedEndTime);
		fraudPreventionObj.setRepeatDays(dayCode);

		return fraudPreventionObj;
	}
}
