package com.pay10.notification.sms.smsCreater;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.SmsSender;
import com.pay10.commons.user.AnalyticsData;
import com.pay10.commons.user.MerchantDailySMSObject;
import com.pay10.commons.user.TransactionCountSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;

@Service
public class SendPerformanceSmsServiceSMS {

	private static Logger logger = LoggerFactory.getLogger(SendPerformanceSmsServiceSMS.class.getName());

	@Autowired
	private AnalyticsDataServiceSMS analyticsDataService;

	@Autowired
	private TransactionSummaryCountServiceSMS transactionSummaryCountService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private SmsSender smsSender;

	public String sendSms(String merchantEmailId, String smsParam) {

		PropertiesManager propertiesManager = new PropertiesManager();

		Date dateToday = new Date();
		Date yesterdayDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(dateToday);
		String modifiedDateYesterday = new SimpleDateFormat("yyyy-MM-dd").format(yesterdayDate);

		String dateTodayFrom = modifiedDate + " 00:00:00";
		String dateTodayTo = modifiedDate + " 23:59:59";

		String dateYesterDayFrom = modifiedDateYesterday + " 00:00:00";
		String dateYesterDayTo = modifiedDateYesterday + " 23:59:59";

		String dateInitial = "2018-10-16 00:00:00";
		String paymentMethods = "ALL";
		String acquirer = "ALL";

		logger.info("Inside SendPerformanceSmsServiceSMS sendSms");
		try {

			if (StringUtils.isBlank(acquirer)) {
				acquirer = "ALL";
			}

			if (StringUtils.isBlank(paymentMethods)) {
				paymentMethods = "ALL";
			}

			StringBuilder smsSuccesslist = new StringBuilder();
			StringBuilder smsFailedlist = new StringBuilder();

			String merchantPayId = null;
			User merchant = new User();
			if (!merchantEmailId.equalsIgnoreCase("All")) {
				merchant = userDao.findPayIdByEmail(merchantEmailId);
				merchantPayId = merchant.getPayId();
			} else {
				merchantPayId = merchantEmailId;
			}

			if (StringUtils.isBlank(merchant.getTransactionSms())) {
				logger.info("No mobile number configured for this merchant to send SMS !");
				return null;
			}

			if (StringUtils.isNotBlank(smsParam) && smsParam.equalsIgnoreCase("capturedData")) {
				logger.info("Preparing capturedData SMS ");
				AnalyticsData analyticsData = analyticsDataService.getTransactionCount(dateYesterDayFrom,
						dateYesterDayTo, merchantPayId, paymentMethods, acquirer, null, smsParam);

				// Capture Data variables
				String date = dateYesterDayFrom;
				String merchantName = merchant.getBusinessName();
				String totTransCount = analyticsData.getSuccessTxnCount();
				String totTransAmount = analyticsData.getTotalCapturedTxnAmount();
				String ccPercentShare = analyticsData.getCCTxnPercent();
				String dcPercentShare = analyticsData.getDCTxnPercent();
				String upiPercentShare = analyticsData.getUPTxnPercent();
				String ppiPercentShare = analyticsData.getWLTxnPercent();
				String nbPercentShare = analyticsData.getNBTxnPercent();
				String iPayAvgTransAmt = analyticsData.getAvgTkt();
				//String rejectionRate = analyticsData.getTotalRejectedTxnPercent();

				logger.info("SMS Data Prepared ");
				// Create SMS body

				StringBuilder capturedSMSBody = new StringBuilder();

				SimpleDateFormat outFormat = new SimpleDateFormat("dd-MMM-yyyy");

				Date dateCapFrom = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(dateYesterDayFrom);
				Date dateCapTo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(dateYesterDayTo);

				String dateCapFromString = outFormat.format(dateCapFrom);
				String dateCapToString = outFormat.format(dateCapTo);

				capturedSMSBody.append("Day End Captured Data for merchant " + merchantName + " &#10 ");
				capturedSMSBody.append(" &#10 ");
				if (dateCapFromString.equalsIgnoreCase(dateCapToString)) {
					capturedSMSBody.append("Capture Date: " + dateCapFromString + " &#10 ");
				} else {
					capturedSMSBody
							.append("Capture Date: From " + dateCapFromString + " to " + dateCapToString + " &#10 ");
				}

				capturedSMSBody.append("Tot. Trans Count: " + totTransCount + " &#10 ");
				capturedSMSBody.append("Tot. Trans Amt: "
						+ (format(String.format("%.0f", Double.valueOf(totTransAmount)))) + ".00" + " &#10 ");
				capturedSMSBody.append("CC Share: " + ccPercentShare + "% &#10 ");
				capturedSMSBody.append("DC Share: " + dcPercentShare + "% &#10 ");
				capturedSMSBody.append("UP Share: " + upiPercentShare + "% &#10 ");
				capturedSMSBody.append("WL Share: " + ppiPercentShare + "% &#10 ");
				capturedSMSBody.append("NB Share: " + nbPercentShare + "% &#10 ");
				capturedSMSBody.append("iPay Avg. Trans Amt: "
						+ ((format(String.format("%.0f", Double.valueOf(iPayAvgTransAmt)))) + ".00" + " &#10 "));

				logger.info("SMS Data Sending =  " + capturedSMSBody.toString());
				
				String smsSendingList = merchant.getTransactionSms();
				String[] smsSendingListArray = smsSendingList.split(",");
				
				for (String mobileNo : smsSendingListArray) {
					
					try
					{
						logger.info("Start 2 sec delay in before sending next SMS");
					    Thread.sleep(2000);
					}
					catch(InterruptedException ex)
					{
					    Thread.currentThread().interrupt();
					}
					
					logger.info("End 2 sec delay in before sending next SMS");
					
					smsSender.sendSMS(mobileNo, capturedSMSBody.toString());
					logger.info("SMS sent to mobile  " + mobileNo);

				}

			}

			else if (StringUtils.isNotBlank(smsParam) && smsParam.equalsIgnoreCase("settledData")) {
				logger.info("Preparing settledData SMS ");

				TransactionCountSearch transactionCountSearch = transactionSummaryCountService.getTransactionCount(
						dateTodayFrom, dateTodayTo, merchantPayId, paymentMethods, acquirer, null, 1, 1, "ALL", "ALL",
						"ALL", TransactionType.SALE.getName());

				TransactionCountSearch transactionCountSearchCumulative = transactionSummaryCountService
						.getTransactionCount(dateInitial, dateTodayTo, merchantPayId, paymentMethods, acquirer, null, 1,
								1, "ALL", "ALL", "ALL", TransactionType.SALE.getName());

				String dateSettled = dateTodayFrom;
				String merchantName = merchant.getBusinessName();
				String cumBookAmt = transactionCountSearchCumulative.getActualSettlementAmount();
				String cumProfit = transactionCountSearchCumulative.getTotalProfit();

				double totalSettledCumultive = Double.valueOf(transactionCountSearchCumulative.getSaleSettledCount());
				double postSettledTxnCountCumulative = Double
						.valueOf(transactionCountSearchCumulative.getPostSettledTransactionCount());
				double totalBookedCumulative = totalSettledCumultive - postSettledTxnCountCumulative;

				double postSettledTxnCount = Double.valueOf(transactionCountSearch.getPostSettledTransactionCount());

				double totalSettled = Double.valueOf(transactionCountSearch.getSaleSettledCount());
				double totalBooked = totalSettled - postSettledTxnCount;

				double successPer = 0.00;
				if (totalSettled > 0) {
					successPer = (totalBooked / totalSettled) * 100;
				}

				String totTransSettledCount = (String.format("%.0f", totalSettled));
				String transAmtSettled = transactionCountSearch.getSaleSettledAmount();
				String tktBookCount = (String.format("%.0f", totalBooked));
				String bookAmt = transactionCountSearch.getActualSettlementAmount();
				String iPayProfit = transactionCountSearch.getTotalProfit();

				String ccPercentShareSettled = transactionCountSearch.getCcSettledPercentage();
				String dcPercentShareSettled = transactionCountSearch.getDcSettledPercentage();
				String upPercentShareSettled = transactionCountSearch.getUpSettledPercentage();
				String successPercent = (String.format("%.2f", successPer));
				String avgBookingAmtSettled = transactionCountSearch.getAvgSettlementAmount();

				logger.info("SMS data prepared");
				// Create SMS body

				StringBuilder settledSMSBody = new StringBuilder();

				settledSMSBody.append("Day End Settled Data \n");
				settledSMSBody.append("Merchant: " + merchantName + " \n");
				settledSMSBody.append("Date: " + dateSettled + " \n");
				settledSMSBody.append("Cum. Book: " + totalBookedCumulative + " \n");
				settledSMSBody.append("Cum. Book Amt: " + cumBookAmt + "\n");
				settledSMSBody.append("Cum. Profit: " + cumProfit + " \n");
				settledSMSBody.append("Tot. Trans: " + totTransSettledCount + " \n");
				settledSMSBody.append("Trans Amt: " + transAmtSettled + " \n");
				settledSMSBody.append("Tkt Book: " + tktBookCount + " \n");
				settledSMSBody.append("Book Amt: " + bookAmt + " \n");
				settledSMSBody.append("iPay Profit: " + iPayProfit + " \n");
				settledSMSBody.append("CC % Share: " + ccPercentShareSettled + " \n");
				settledSMSBody.append("DC % Share: " + dcPercentShareSettled + " \n");
				settledSMSBody.append("UPI % Share: " + upPercentShareSettled + " \n");
				settledSMSBody.append("Success %: " + successPercent + " \n");
				settledSMSBody.append("Avg. Booking Amt: " + avgBookingAmtSettled + " \n");

				logger.info("SMS Prepared  " + settledSMSBody.toString());

				String smsSendingList = merchant.getTransactionSms();

				String[] smsSendingListArray = smsSendingList.split(",");

				for (String mobileNo : smsSendingListArray) {
					smsSender.sendSMS(mobileNo, settledSMSBody.toString());
					logger.info("SMS sent to mobile  " + mobileNo);

				}

				StringBuilder finalMessage = new StringBuilder();

				if (StringUtils.isNotBlank(smsSuccesslist.toString())) {
					finalMessage.append("SMS sent successfully to " + smsSuccesslist.toString() + "\n");
				}

				if (StringUtils.isNotBlank(smsFailedlist.toString())) {
					finalMessage.append("  Failed to sent sms on  " + smsFailedlist.toString() + "\n");
				}

			}

			else if (StringUtils.isNotBlank(smsParam) && smsParam.equalsIgnoreCase("iPaycapturedData")) {

				logger.info("Preparing iPaycapturedData SMS ");

				AnalyticsData analyticsData = analyticsDataService.getTransactionTotalProfitCount(dateYesterDayFrom, dateYesterDayTo,
						merchantPayId, paymentMethods, acquirer, null , smsParam);
				
				logger.info("Preparing iPaycapturedData SMS Capture analyticsData variables1  -> " + analyticsData);
				
				AnalyticsData analyticsDataCumm = analyticsDataService
						.getTransactionTotalProfitCount(dateInitial, dateYesterDayTo, merchantPayId, paymentMethods,
								acquirer, null, smsParam);
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
				BigDecimal calValueGstCumm = profitIncGstCumm.divide(totalTransCountCumm, 2,
						RoundingMode.HALF_DOWN);
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
				String date = dateYesterDayFrom;
				String merchantName = merchant.getBusinessName();
				String totTransCount = analyticsData.getSuccessTxnCount();
				String totTransAmount = analyticsData.getTotalCapturedTxnAmount();
				String ccPercentShare = analyticsData.getCCTxnPercent();
				String dcPercentShare = analyticsData.getDCTxnPercent();
				String upiPercentShare = analyticsData.getUPTxnPercent();
				String walletPercentShare = analyticsData.getWLTxnPercent();
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

				Date dateCapFrom = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(dateYesterDayFrom);
				Date dateCapTo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(dateYesterDayTo);

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

			}
			
			else if (StringUtils.isNotBlank(smsParam) && smsParam.equalsIgnoreCase("merchantData")) {
				logger.info("Preparing Data for Merchant SMS New");
			
				MerchantDailySMSObject merchantSMSObject = transactionSummaryCountService.getMerchantSMSDailyData(
						dateYesterDayFrom, dateYesterDayTo, merchantPayId, paymentMethods, acquirer, null, 1, 1, "ALL",
						"ALL", "ALL", TransactionType.SALE.getName());


				String merchantName = merchant.getBusinessName();
				logger.info("Mechant SMS data prepared");	
				// Create SMS body

				StringBuilder capturedSMSBody = new StringBuilder();

				capturedSMSBody.append("Day End Captured Data for merchant: " + merchantName + " \\n");
				capturedSMSBody.append(" \\n");
				capturedSMSBody.append("Captured Date: " + dateYesterDayFrom + "\\n");
				capturedSMSBody.append("Total Booking: " + merchantSMSObject.getTotalBooking() + "\\n");
				capturedSMSBody.append("Total Amount: " + merchantSMSObject.getTotalAmount() + "\\n");
				capturedSMSBody.append("Total Comm. (inc.GST): " + merchantSMSObject.getTotalCommWithGST()+ "\\n");
				capturedSMSBody.append(" \\n");
				capturedSMSBody.append("Bank Comm. (inc.GST): " + merchantSMSObject.getBankCommWIthGST()+ "\\n");
				capturedSMSBody.append("iPay Comm. (inc.GST): " + merchantSMSObject.getiPayCommWithGST()+ " \\n");
				capturedSMSBody.append(" \\n");
				
				capturedSMSBody.append("CC % Share in No. of Ticket: " + merchantSMSObject.getCcPercentTicket() + " %\\n");
				capturedSMSBody.append("CC iPay Comm. (inc.GST):" + merchantSMSObject.getCcIpayCommWithGST() + "\\n");
				capturedSMSBody.append("CC Bank Comm. (inc.GST):" + merchantSMSObject.getCcBankCommWithGST()+ "\\n");
				capturedSMSBody.append("DC % Share in No. of Ticket: " + merchantSMSObject.getDcPercentTicket() + " %\\n");
				capturedSMSBody.append("DC iPay Comm. (inc.GST):" + merchantSMSObject.getDcIpayCommWithGST()+ "\\n");
				capturedSMSBody.append("DC Bank Comm. (inc.GST):" + merchantSMSObject.getDcBankCommWithGST()+ "\\n");
				capturedSMSBody.append("UPI % Share in No. of Ticket: " + merchantSMSObject.getUpPercentTicket() + " %\\n");
				capturedSMSBody.append("UPI iPay Comm. (inc.GST):" + merchantSMSObject.getUpIpayCommWithGST()+ "\\n");
				capturedSMSBody.append("UPI Bank Comm. (inc.GST):" + merchantSMSObject.getUpBankCommWithGST()+ "\\n");
				capturedSMSBody.append("WALLET % Share in No. of Ticket: " + merchantSMSObject.getWlPercentTicket() + " %\\n");
				capturedSMSBody.append("WALLET iPay Comm. (inc.GST):" + merchantSMSObject.getWlIpayCommWithGST()+ "\\n");
				capturedSMSBody.append("WALLET Bank Comm. (inc.GST):" + merchantSMSObject.getWlBankCommWithGST()+ "\\n");
				logger.info("Mechant SMS Prepared  " + capturedSMSBody.toString());
				String smsSendingList = merchant.getTransactionSms();

				String[] smsSendingListArray = smsSendingList.split(",");

				for (String mobileNo : smsSendingListArray) {
					
					try
					{
						logger.info("Start 2 sec delay in before sending next SMS");
					    Thread.sleep(2000);
					}
					catch(InterruptedException ex)
					{
					    Thread.currentThread().interrupt();
					}
					
					logger.info("End 2 sec delay in before sending next SMS");
					
					smsSender.sendSMS(mobileNo, capturedSMSBody.toString());
					logger.info("SMS sent to mobile  " + mobileNo);
				}

			}
			

		} catch (Exception e) {
			logger.error("Exception in getting transaction summary count data for SMS " + e);
		}

		return null;
	}

	public void checkLastTxn(String payId, String duration) {

		try {

			Date dateBefore = new Date(System.currentTimeMillis() - (Integer.valueOf(duration) * 60) * 1000);
			Date dateNow = new Date();

			String formattedDateBefore = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dateBefore);
			String formattedDateNow = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dateNow);

			boolean isTxnDone = analyticsDataService.isTxnCapturedForMerchant(payId, formattedDateBefore,
					formattedDateNow);
			if (!isTxnDone) {

				SimpleDateFormat outFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

				Date dateCapFrom = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
						.parse(formattedDateBefore);
				Date dateCapTo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(formattedDateNow);

				String dateCapFromString = outFormat.format(dateCapFrom);
				String dateCapToString = outFormat.format(dateCapTo);

				User user = userDao.findPayId(payId);
				StringBuilder smsBody = new StringBuilder();
				smsBody.append("Alert ! No Successful Transaction Done in last " + duration + " minutes for Merchant "
						+ user.getBusinessName() + "\\n");
				smsBody.append("From : " + dateCapFromString + "\\n");
				smsBody.append("To : " + dateCapToString + "\\n");

				logger.info("SMS Data Sending =  " + smsBody.toString());
				String smsSendingList = user.getTransactionSms();
				String[] smsSendingListArray = smsSendingList.split(",");

				for (String mobileNo : smsSendingListArray) {
					smsSender.sendSMS(mobileNo, smsBody.toString());
					logger.info("SMS sent to mobile  " + mobileNo);

				}
			}

		} catch (Exception e) {
			logger.error("Exception occured in sending hourly SMS   " + e);
		}
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

}
