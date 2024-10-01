package com.pay10.crm.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.SmsSender;
import com.pay10.commons.user.AnalyticsData;
import com.pay10.commons.user.MerchantDailySMSObject;
import com.pay10.commons.user.MerchantSMSObject;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.crm.actionBeans.AnalyticsDataService;
import com.pay10.crm.actionBeans.TransactionSummaryCountService;

public class SendPerformanceSmsAction extends AbstractSecureAction {

	private static final long serialVersionUID = -6323553936458486881L;

	private static Logger logger = LoggerFactory.getLogger(SendPerformanceSmsAction.class.getName());

	private String dateFrom;
	private String dateInitial;
	private String dateTo;
	public String paymentMethods;
	public String acquirer;
	private String merchantEmailId;
	private String smsParam;
	private String response;

	private String transactionType;
	private String mopType;
	
	@Autowired
	private AnalyticsDataService analyticsDataService;

	@Autowired
	private TransactionSummaryCountService transactionSummaryCountService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private SmsSender smsSender;

	PropertiesManager propertiesManager = new PropertiesManager();

	@Override
	public String execute() {

		logger.info("Inside SendPerformanceSmsAction execute");

		Map<String, List<ServiceTax>> serviceTaxMap = new HashMap<String, List<ServiceTax>>();
		try {

			if (StringUtils.isBlank(acquirer)) {
				acquirer = "ALL";
			}

			if (StringUtils.isBlank(paymentMethods)) {
				paymentMethods = "ALL";
			}
			
			if (StringUtils.isBlank(transactionType)) {
				transactionType = "ALL";
			}

			if (StringUtils.isBlank(mopType)) {
				mopType = "ALL";
			}

			dateInitial = "2018-10-16 00:00:00";
			dateFrom = DateCreater.toDateTimeformatCreater(dateFrom);
			dateTo = DateCreater.formDateTimeformatCreater(dateTo);
			StringBuilder smsSuccesslist = new StringBuilder();
			StringBuilder smsFailedlist = new StringBuilder();

			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {

				String merchantPayId = null;
				User merchant = new User();
				serviceTaxMap.clear();
				if (!merchantEmailId.equalsIgnoreCase("All")) {
					merchant = userDao.findPayIdByEmail(merchantEmailId);
					merchantPayId = merchant.getPayId();
				} else {
					merchantPayId = merchantEmailId;
				}
				if (StringUtils.isNotBlank(smsParam) && smsParam.equalsIgnoreCase("iPaycapturedData")) {

					String mobile = propertiesManager.getIpaySmsPropertiesFile("iPaySms");

				} else if (StringUtils.isBlank(merchant.getTransactionSms())) {
					setResponse("No mobile number configured for this merchant to send SMS !");
					return SUCCESS;
				}

				if (StringUtils.isNotBlank(smsParam) && smsParam.equalsIgnoreCase("capturedData")) {

					logger.info("Preparing capturedData SMS ");
					AnalyticsData analyticsData = analyticsDataService.getTransactionCount(dateFrom, dateTo,
							merchantPayId, paymentMethods, acquirer, sessionUser, smsParam,transactionType,mopType, "ALL");

					// Capture Data variables
					String date = dateFrom;
					String merchantName = merchant.getBusinessName();
					String totTransCount = analyticsData.getSuccessTxnCount();
					String totTransAmount = analyticsData.getTotalCapturedTxnAmount();
					String ccPercentShare = analyticsData.getCCTxnPercent();
					String dcPercentShare = analyticsData.getDCTxnPercent();
					String upiPercentShare = analyticsData.getUPTxnPercent();
					String ppiPercentShare = analyticsData.getWLTxnPercent();
					String nbPercentShare = analyticsData.getNBTxnPercent();
					String iPayAvgTransAmt = analyticsData.getAvgTkt();
					String looktoBook = analyticsData.getSuccessTxnPercent();
					String rejectionRate = analyticsData.getTotalRejectedTxnPercent();

					logger.info("SMS Data Prepared ");
					// Create SMS body

					StringBuilder capturedSMSBody = new StringBuilder();

					SimpleDateFormat outFormat = new SimpleDateFormat("dd-MMM-yyyy");

					Date dateCapFrom = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(dateFrom);
					Date dateCapTo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(dateTo);

					String dateCapFromString = outFormat.format(dateCapFrom);
					String dateCapToString = outFormat.format(dateCapTo);

					capturedSMSBody.append("Day End Captured Data for merchant " + merchantName + "  \\n");
					capturedSMSBody.append(" \\n");
					if (dateCapFromString.equalsIgnoreCase(dateCapToString)) {
						capturedSMSBody.append("Capture Date: " + dateCapFromString + " \\n");
					} else {
						capturedSMSBody
								.append("Capture Date: From " + dateCapFromString + " to " + dateCapToString + "\\n");
					}

					capturedSMSBody.append("Tot. Trans Count: " + totTransCount + "\\n");
					capturedSMSBody.append("Tot. Trans Amt: "
							+ (format(String.format("%.0f", Double.valueOf(totTransAmount)))) + ".00" + "\\n");
					capturedSMSBody.append("CC Share: " + ccPercentShare + "% \\n");
					capturedSMSBody.append("DC Share: " + dcPercentShare + "% \\n");
					capturedSMSBody.append("UP Share: " + upiPercentShare + "% \\n");
					capturedSMSBody.append("PPI Share: " + ppiPercentShare + "% \\n");
					capturedSMSBody.append("NB Share: " + nbPercentShare + "% \\n");
					capturedSMSBody.append("iPay Avg. Trans Amt: "
							+ ((format(String.format("%.0f", Double.valueOf(iPayAvgTransAmt)))) + ".00"));

					logger.info("SMS Data Sending =  " + capturedSMSBody.toString());
					String smsSendingList = merchant.getTransactionSms();
					String[] smsSendingListArray = smsSendingList.split(",");

					for (String mobileNo : smsSendingListArray) {

						logger.info("SMS sent to mobile  " + mobileNo);
						String response = smsSender.sendSMS(mobileNo, capturedSMSBody.toString());

						if (response.contains("success")) {
							smsSuccesslist.append(mobileNo);
							smsSuccesslist.append(" , ");
						} else {
							smsFailedlist.append(mobileNo);
							smsFailedlist.append(" , ");
						}
					}

					StringBuilder finalMessage = new StringBuilder();

					if (StringUtils.isNotBlank(smsSuccesslist.toString())) {
						finalMessage.append("SMS sent successfully to " + smsSuccesslist.toString() + "\n");
					}

					if (StringUtils.isNotBlank(smsFailedlist.toString())) {
						finalMessage.append("  Failed to sent sms on  " + smsFailedlist.toString() + "\n");
					}

					setResponse(finalMessage.toString());
				} else if (StringUtils.isNotBlank(smsParam) && smsParam.equalsIgnoreCase("iPaycapturedData")) {

					logger.info("Preparing iPaycapturedData SMS ");

					AnalyticsData analyticsData = analyticsDataService.getTransactionTotalProfitCount(dateFrom, dateTo,
							merchantPayId, paymentMethods, acquirer, sessionUser, smsParam);
					
					logger.info("Preparing iPaycapturedData SMS Capture analyticsData variables1  -> " + analyticsData);
					
					AnalyticsData analyticsDataCumm = analyticsDataService
							.getTransactionTotalProfitCount(dateInitial, dateTo, merchantPayId, paymentMethods,
									acquirer, sessionUser, smsParam);
					logger.info("Preparing iPaycapturedData SMS Capture CUMM analyticsDataCumm variables2 -> " + analyticsDataCumm);
					// Capture Cumm Data variables
					String totTransCountCumm = analyticsDataCumm.getSuccessTxnCount();
					String totTransAmountCumm = analyticsDataCumm.getTotalCapturedTxnAmount();
					String ipayProfitCumm = analyticsDataCumm.getIpayProfitInclGstCumm();
					String ipayProfitInclGstCumm = analyticsDataCumm.getIpayProfitInclGstCumm();
					logger.info("Preparing iPaycapturedData SMS Capture Cumm Data variables" + totTransCountCumm);
					BigDecimal totalTransCountCumm = new BigDecimal(totTransCountCumm.replace(",", ""));

					// calculation for AvgipayProfit IncGstCumm
					BigDecimal profitIncGstCumm = new BigDecimal(ipayProfitInclGstCumm.replace(",", ""));
					BigDecimal calValueGstCumm = profitIncGstCumm.divide(totalTransCountCumm, 2,RoundingMode.HALF_DOWN);
					String avgipayProfitInclGstCumm = calValueGstCumm.toString();
					logger.info("Preparing iPaycapturedData SMS calculation for AvgipayProfit IncGstCumms"
							+ avgipayProfitInclGstCumm);
					// calculation for AvgipayProfit ExcGstCumm
					String stCumm = analyticsDataCumm.getGst();
					String st = analyticsData.getGst();

					logger.info("Preparing iPaycapturedData SMS calculation for AvgipayProfit ExcGstCumm" + st);
					BigDecimal perc = new BigDecimal(stCumm);
					BigDecimal amt = new BigDecimal(ipayProfitInclGstCumm.replace(",", ""));
					BigDecimal ONE_HUNDRED = new BigDecimal(100);
					BigDecimal calValue = amt.multiply(perc).divide(ONE_HUNDRED);
					BigDecimal finalAmt = amt.subtract(calValue);
					BigDecimal ipayProfitExcGstCumm = finalAmt.divide(totalTransCountCumm, 2, RoundingMode.HALF_DOWN);
					String avgIpayProfitExcGstCumm = ipayProfitExcGstCumm.toString();

					// Capture Data variables
					String date = dateFrom;
					String merchantName = merchant.getBusinessName();
					String totTransCount = analyticsData.getSuccessTxnCount();
					String totTransAmount = analyticsData.getTotalCapturedTxnAmount();
					String ccPercentShare = analyticsData.getCCTxnPercent();
					String dcPercentShare = analyticsData.getDCTxnPercent();
					String upiPercentShare = analyticsData.getUPTxnPercent();
					String walletPercentShare = analyticsData.getWLTxnPercent();
					String netBankingPercentShare = analyticsData.getNBTxnPercent();
					String ipayProfit = analyticsData.getIpayProfitInclGstCumm();
					String ipayProfitInclGst = analyticsData.getIpayProfitInclGstCumm();

					BigDecimal totalTransCount = new BigDecimal(totTransCount.replace(",", ""));

					// calculation for AvgipayProfit IncGst
					BigDecimal profitIncGst = new BigDecimal(ipayProfitInclGst.replace(",", ""));
					BigDecimal calValueGst = profitIncGst.divide(totalTransCount, 2, RoundingMode.HALF_DOWN);
					String avgipayProfitInclGst = calValueGst.toString();

					// calculation for ipayProfit ExcGst
					BigDecimal perc2 = new BigDecimal(st);
					BigDecimal amt2 = new BigDecimal(ipayProfitInclGst.replace(",", ""));
					BigDecimal ONE_HUNDRED2 = new BigDecimal(100);
					BigDecimal calValue2 = amt2.multiply(perc2).divide(ONE_HUNDRED2);
					BigDecimal finalAmt2 = amt2.subtract(calValue2);
					BigDecimal ipayProfitExcGst = finalAmt2.divide(totalTransCount, 2, RoundingMode.HALF_DOWN);
					String avgIpayProfitExcGst = ipayProfitExcGst.toString();

					logger.info("SMS Data Prepared ");
					// Create SMS body

					StringBuilder capturedSMSBody = new StringBuilder();

					SimpleDateFormat outFormat = new SimpleDateFormat("dd-MMM-yyyy");

					Date dateCapFrom = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(dateFrom);
					Date dateCapTo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(dateTo);

					String dateCapFromString = outFormat.format(dateCapFrom);
					String dateCapToString = outFormat.format(dateCapTo);

					capturedSMSBody.append("Day End Captured Data for merchant " + merchantName + "  \\n");
					capturedSMSBody.append(" \\n");
					capturedSMSBody.append("Cumm Txn Count: " + totTransCountCumm + "\\n");

					capturedSMSBody.append("Cumm Txn Amt: INR  "
							+ (formatDecimal(
									String.format("%.2f", Double.valueOf(totTransAmountCumm.replace(",", "")))))
							+ "\\n");

					capturedSMSBody.append("Cumm iPay Profit: "
							+ (formatDecimal(String.format("%.2f", Double.valueOf(ipayProfitCumm.replace(",", "")))))
							+ "\\n");
					capturedSMSBody.append("Cumm iPay Profit/Txn(Inc.GST): "
							+ (formatDecimal(
									String.format("%.2f", Double.valueOf(avgipayProfitInclGstCumm.replace(",", "")))))
							+ " \\n");
					capturedSMSBody.append("Cumm iPay Profit/Txn(Exc.GST): "
							+ (formatDecimal(
									String.format("%.2f", Double.valueOf(avgIpayProfitExcGstCumm.replace(",", "")))))
							+ " \\n");

					capturedSMSBody.append(" \\n");
					capturedSMSBody.append(" \\n");

					if (dateCapFromString.equalsIgnoreCase(dateCapToString)) {
						capturedSMSBody.append("Capture Date: " + dateCapFromString + " \\n");
					} else {
						capturedSMSBody
								.append("Capture Date: From " + dateCapFromString + " to " + dateCapToString + "\\n");
					}
					capturedSMSBody.append(" \\n");
					capturedSMSBody.append("Tot. Txn Count: " + totTransCount + "\\n");
					capturedSMSBody.append("Tot. Txn Amt: INR  "
							+ (formatDecimal(String.format("%.2f", Double.valueOf(totTransAmount.replace(",", "")))))
							+ "\\n");
					capturedSMSBody.append("CC Share: " + ccPercentShare + "% \\n");
					capturedSMSBody.append("DC Share: " + dcPercentShare + "% \\n");
					capturedSMSBody.append("UP Share: " + upiPercentShare + "% \\n");
					capturedSMSBody.append("PPI Share: " + walletPercentShare + "% \\n");
					capturedSMSBody.append("NB Share: " + netBankingPercentShare + "% \\n");
					capturedSMSBody.append("iPay Profit: "
							+ (formatDecimal(String.format("%.2f", Double.valueOf(ipayProfit.replace(",", "")))))
							+ "\\n");
					capturedSMSBody.append("iPay Profit/Txn(Inc.GST): "
							+ (formatDecimal(
									String.format("%.2f", Double.valueOf(avgipayProfitInclGst.replace(",", "")))))
							+ " \\n");
					capturedSMSBody.append("iPay Profit/Txn(Exc.GST): "
							+ (formatDecimal(
									String.format("%.2f", Double.valueOf(avgIpayProfitExcGst.replace(",", "")))))
							+ " \\n");

					logger.info("SMS Data Sending =  " + capturedSMSBody.toString());
					String smsSendingList = propertiesManager.getIpaySmsPropertiesFile("iPaySms");
					String[] smsSendingListArray = smsSendingList.split(",");

					for (String mobileNo : smsSendingListArray) {

						logger.info("SMS sent to mobile  " + mobileNo);
						String response = smsSender.sendSMS(mobileNo, capturedSMSBody.toString());

						if (response.contains("success")) {
							smsSuccesslist.append(mobileNo);
							smsSuccesslist.append(" , ");
						} else {
							smsFailedlist.append(mobileNo);
							smsFailedlist.append(" , ");
						}
					}

					StringBuilder finalMessage = new StringBuilder();

					if (StringUtils.isNotBlank(smsSuccesslist.toString())) {
						finalMessage.append("SMS sent successfully to " + smsSuccesslist.toString() + "\n");
					}

					if (StringUtils.isNotBlank(smsFailedlist.toString())) {
						finalMessage.append("  Failed to sent sms on  " + smsFailedlist.toString() + "\n");
					}

					setResponse(finalMessage.toString());
				}

				else if (StringUtils.isNotBlank(smsParam) && smsParam.equalsIgnoreCase("merchantData")) {

					logger.info("Preparing Data for Merchant SMS New");

					MerchantDailySMSObject merchantSMSObject = transactionSummaryCountService.getMerchantSMSDailyData(
							dateFrom, dateTo, merchantPayId, paymentMethods, acquirer, sessionUser, 1, 1, "ALL", "ALL",
							"ALL", TransactionType.SALE.getName());

					String merchantName = merchant.getBusinessName();
					logger.info("Mechant SMS data prepared");
					// Create SMS body

					StringBuilder capturedSMSBody = new StringBuilder();

					capturedSMSBody.append("Day End Captured Data for merchant: " + merchantName + " \\n");
					capturedSMSBody.append(" \\n");
					capturedSMSBody.append("Captured Date: " + dateFrom + "\\n");
					capturedSMSBody.append("Total Booking: " + merchantSMSObject.getTotalBooking() + "\\n");
					capturedSMSBody.append("Total Amount: " + merchantSMSObject.getTotalAmount() + "\\n");
					capturedSMSBody.append("Total Comm. (inc.GST): " + merchantSMSObject.getTotalCommWithGST() + "\\n");
					capturedSMSBody.append(" \\n");
					capturedSMSBody.append("Bank Comm. (inc.GST): " + merchantSMSObject.getBankCommWIthGST() + "\\n");
					capturedSMSBody.append("iPay Comm. (inc.GST): " + merchantSMSObject.getiPayCommWithGST() + " \\n");
					capturedSMSBody.append(" \\n");

					capturedSMSBody
							.append("CC % Share in No. of Ticket: " + merchantSMSObject.getCcPercentTicket() + " %\\n");
					capturedSMSBody
							.append("CC iPay Comm. (inc.GST):" + merchantSMSObject.getCcIpayCommWithGST() + "\\n");
					capturedSMSBody
							.append("CC Bank Comm. (inc.GST):" + merchantSMSObject.getCcBankCommWithGST() + "\\n");
					capturedSMSBody
							.append("DC % Share in No. of Ticket: " + merchantSMSObject.getDcPercentTicket() + " %\\n");
					capturedSMSBody
							.append("DC iPay Comm. (inc.GST):" + merchantSMSObject.getDcIpayCommWithGST() + "\\n");
					capturedSMSBody
							.append("DC Bank Comm. (inc.GST):" + merchantSMSObject.getDcBankCommWithGST() + "\\n");
					capturedSMSBody.append(
							"UPI % Share in No. of Ticket: " + merchantSMSObject.getUpPercentTicket() + " %\\n");
					capturedSMSBody
							.append("UPI iPay Comm. (inc.GST):" + merchantSMSObject.getUpIpayCommWithGST() + "\\n");
					capturedSMSBody
							.append("UPI Bank Comm. (inc.GST):" + merchantSMSObject.getUpBankCommWithGST() + "\\n");
					capturedSMSBody.append(
							"PPI % Share in No. of Ticket: " + merchantSMSObject.getWlPercentTicket() + " %\\n");
					capturedSMSBody
							.append("PPI iPay Comm. (inc.GST):" + merchantSMSObject.getWlIpayCommWithGST() + "\\n");
					capturedSMSBody
							.append("PPI Bank comm. (inc.GST):"+ merchantSMSObject.getWlBankCommWithGST()+"\\n");
					capturedSMSBody
					.append("NB % Share in No. of Ticket: " + merchantSMSObject.getNbPercentTicket() + " %\\n");
			capturedSMSBody
					.append("NB iPay Comm. (inc.GST):" + merchantSMSObject.getNbIpayCommWithGST() + "\\n");
			capturedSMSBody
					.append("NB Bank Comm. (inc.GST):" + merchantSMSObject.getNbBankCommWithGST() + "\\n");
					
					
					logger.info("Mechant SMS Prepared  " + capturedSMSBody.toString());

					String smsSendingList = merchant.getTransactionSms();

					String[] smsSendingListArray = smsSendingList.split(",");

					for (String mobileNo : smsSendingListArray) {
						logger.info("SMS sent to mobile  " + mobileNo);
						String response = smsSender.sendSMS(mobileNo, capturedSMSBody.toString());

						if (response.contains("success")) {
							smsSuccesslist.append(mobileNo);
							smsSuccesslist.append(" , ");
						} else {
							smsFailedlist.append(mobileNo);
							smsFailedlist.append(" , ");
						}
					}

					StringBuilder finalMessage = new StringBuilder();

					if (StringUtils.isNotBlank(smsSuccesslist.toString())) {
						finalMessage.append("SMS sent successfully to " + smsSuccesslist.toString() + "\n");
					}

					if (StringUtils.isNotBlank(smsFailedlist.toString())) {
						finalMessage.append("  Failed to sent sms on  " + smsFailedlist.toString() + "\n");
					}

					setResponse(finalMessage.toString());

				}

				else if (StringUtils.isNotBlank(smsParam) && smsParam.equalsIgnoreCase("settledData")) {

					logger.info("Preparing Settled Data SMS ");

					MerchantSMSObject merchantSMSObject = transactionSummaryCountService.getMerchantSMSData(dateFrom,
							dateTo, merchantPayId, paymentMethods, acquirer, sessionUser, 1, 1, "ALL", "ALL", "ALL",
							TransactionType.SALE.getName());

					// Settled Data variables

					String merchantName = merchant.getBusinessName();
					logger.info("Settled Data  SMS data prepared");
					// Create SMS body

					StringBuilder settledSMSBody = new StringBuilder();

					settledSMSBody.append("Day End Settlement Data for merchant " + merchantName + " \\n");
					settledSMSBody.append(" \\n");
					settledSMSBody.append("Capture Date: " + merchantSMSObject.getDateCaptured() + "\\n");
					settledSMSBody.append("Settlement Date: " + merchantSMSObject.getDateSettled() + "\\n");
					settledSMSBody.append(" \\n");
					settledSMSBody.append("Total Sale Amount: " + merchantSMSObject.getTotalSettledAmount() + "\\n");
					settledSMSBody.append("CC  Sale Amount: " + merchantSMSObject.getCcSettledAmt() + "\\n");
					settledSMSBody.append("DC  Sale Amount: " + merchantSMSObject.getDcSettledAmt() + "\\n");
					settledSMSBody.append("UPI Sale Amount: " + merchantSMSObject.getUpSettledAmt() + "\\n");
					settledSMSBody.append(" \\n");
					settledSMSBody.append("Total Settled Txn " + merchantSMSObject.getTotalTxnCount() + " \\n");
					settledSMSBody.append("CC  TXN: " + merchantSMSObject.getCcTxnPer() + " %\\n");
					settledSMSBody.append("DC  TXN: " + merchantSMSObject.getDcTxnPer() + " %\\n");
					settledSMSBody.append("UPI TXN: " + merchantSMSObject.getUpTxnPer() + " %\\n");
					settledSMSBody.append(" \\n");
					settledSMSBody.append("Total Settled Amount (Sale settled - Refund settled): "
							+ merchantSMSObject.getSettledAmount() + "\\n");
					logger.info("Mechant SMS Prepared  " + settledSMSBody.toString());

					String smsSendingList = merchant.getTransactionSms();

					String[] smsSendingListArray = smsSendingList.split(",");

					for (String mobileNo : smsSendingListArray) {
						logger.info("SMS sent to mobile  " + mobileNo);
						String response = smsSender.sendSMS(mobileNo, settledSMSBody.toString());

						if (response.contains("success")) {
							smsSuccesslist.append(mobileNo);
							smsSuccesslist.append(" , ");
						} else {
							smsFailedlist.append(mobileNo);
							smsFailedlist.append(" , ");
						}
					}

					StringBuilder finalMessage = new StringBuilder();

					if (StringUtils.isNotBlank(smsSuccesslist.toString())) {
						finalMessage.append("SMS sent successfully to " + smsSuccesslist.toString() + "\n");
					}

					if (StringUtils.isNotBlank(smsFailedlist.toString())) {
						finalMessage.append("  Failed to sent sms on  " + smsFailedlist.toString() + "\n");
					}

					setResponse(finalMessage.toString());

				}

			}

		} catch (Exception e) {
			logger.error("Exception in getting transaction summary count data for SMS " + e);
		}

		return SUCCESS;
	}

	public String format(String amount) {
		StringBuilder stringBuilder = new StringBuilder();
		char amountArray[] = amount.toCharArray();
		int a = 0, b = 0;
		for (int i = amountArray.length - 1; i >= 0; i--) {
			if (a < 3) {
				stringBuilder.append(amountArray[i]);
				a++;
			} else if (b < 2) {
				if (b == 0) {
					stringBuilder.append(",");
					stringBuilder.append(amountArray[i]);
					b++;
				} else {
					stringBuilder.append(amountArray[i]);
					b = 0;
				}
			}
		}
		return stringBuilder.reverse().toString();
	}

	public String formatDecimal(String amount) {
		StringBuilder stringBuilder = new StringBuilder();
		char amountArray[] = amount.toCharArray();
		int a = 0, b = 0;
		for (int i = amountArray.length - 1; i >= 0; i--) {
			if (a < 6) {
				stringBuilder.append(amountArray[i]);
				a++;
			} else if (b < 2) {
				if (b == 0) {
					stringBuilder.append(",");
					stringBuilder.append(amountArray[i]);
					b++;
				} else {
					stringBuilder.append(amountArray[i]);
					b = 0;
				}
			}
		}
		return stringBuilder.reverse().toString();
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}

	public String getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public String getDateInitial() {
		return dateInitial;
	}

	public void setDateInitial(String dateInitial) {
		this.dateInitial = dateInitial;
	}

	public String getSmsParam() {
		return smsParam;
	}

	public void setSmsParam(String smsParam) {
		this.smsParam = smsParam;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	
}
