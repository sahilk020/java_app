package com.pay10.pg.core.fraudPrevention.core;

import java.io.File;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.dao.FraudTransactionDetailsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.FraudTransactionDetails;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.FraudPreventationConfiguration;
import com.pay10.commons.util.FraudPreventionObj;
import com.pay10.commons.util.FraudRuleType;
import com.pay10.commons.util.Helper;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.SystemConstants;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.Weekdays;
import com.pay10.pg.core.fraudPrevention.model.FraudTxnDao;
import com.pay10.pg.core.fraudPrevention.model.FraudTxnExcelModel;
import com.pay10.pg.core.fraudPrevention.model.FraudTxnExcelWriter;
import com.pay10.pg.core.fraudPrevention.model.TxnFilterRequest;
import com.pay10.pg.core.fraudPrevention.util.FraudPreventionUtil;

/**
 * @author Harpreet, Rahul , Rajendra, Jay
 *
 */
@Service
public class FraudRuleImplementor {

	private static Logger logger = LoggerFactory.getLogger(FraudRuleImplementor.class.getName());
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/*
	 * @Autowired private FraudPreventionDao fraudPreventionDao;
	 */
	@Autowired
	private FraudTxnDao fraudTxnDao;

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;

	@Autowired
	private UserDao userDao;

	@Autowired
	private GeolocationApiService geolocationApiService;

	@Autowired
	private FraudTransactionDetailsDao fraudTransactionDetailsDao;

	public void applyRule(Fields fields) throws SystemException {
		List<FraudPreventionObj> fraudPreventionRuleList = new ArrayList<FraudPreventionObj>();
		String payId2 = fields.get(FieldType.PAY_ID.getName());
		String currency= (fields.get(FieldType.CURRENCY_CODE.getName()));
		logger.info("Currency selected "+currency+" and payId "+payId2);
		if(currency == null || currency.isEmpty()) {
			return;
		}
		fraudPreventionRuleList = fraudPreventionMongoService.fraudPreventionListByPayId(payId2,currency);
	 	logger.info("**********************"+new Gson().toJson(fraudPreventionRuleList));
		logger.info("Currency selected "+currency+" and payId "+payId2);

		FraudPreventationConfiguration configuration = fraudPreventionMongoService.getConfigByPayId(payId2,currency);
		configuration = configuration == null ? new FraudPreventationConfiguration() : configuration;
		logger.info("Configuration {}",configuration);
		if (fraudPreventionRuleList != null) {
			final String payId = fields.get(FieldType.PAY_ID.getName());
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			boolean isMerchantHosted = false;
			String transactionStatus = fields.get(FieldType.STATUS.getName());
			if (null != fields.get(FieldType.IS_MERCHANT_HOSTED.getName())
					&& fields.get(FieldType.IS_MERCHANT_HOSTED.getName()).equalsIgnoreCase("Y")) {
				isMerchantHosted = true;
			}
			String ipAddress = fields.get(FieldType.INTERNAL_CUST_IP.getName());
			
			for (FraudPreventionObj fraudPrevention : fraudPreventionRuleList) {
				final FraudRuleType fraudType = fraudPrevention.getFraudType();
				logger.info("FraudPreventionRoleList fraudType "+ fraudType);

				boolean result = true;
				switch (fraudType) {
				case BLOCK_TXN_AMOUNT:
					//logger.info("**********************"+new Gson().toJson(fraudPrevention));
					logger.info("Block Transaction Amount "+fraudType);
					if ((!isMerchantHosted && !(txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnLimitTxnAmtBlocking()) {
						logger.info("Block Transaction Amount not applied due to Transaction Type NEW ORDER"+txnType.equals(TransactionType.NEWORDER.getName())+" OR ENROLL"+txnType.equals(TransactionType.ENROLL.getName())
								+" limit blocking Enable "+configuration.isOnLimitTxnAmtBlocking());
						break;
					}
					String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
					logger.info("Currency "+currencyCode);
					if (currencyCode.equalsIgnoreCase(fraudPrevention.getCurrency())) {
						String tempRequestAmount = fields.get(FieldType.AMOUNT.getName());
						Double requestAmount = Double.valueOf(tempRequestAmount) / 100;
						requestAmount = requestAmount == null ? 0 : requestAmount;
						if (fraudPrevention.isFixedAmountFlag()) {
							LocalDateTime currentDateTime = DateCreater.now();
							String startDate = getStartDateByTimeUnits(currentDateTime,
									fraudPrevention.getBlockTimeUnits());
							String endDate = currentDateTime.format(DateCreater.dateTimeFormat);
							TxnFilterRequest request = new TxnFilterRequest(payId, startDate, endDate, currencyCode,
									null, null, null, null, null, 0, null, null);
							Map<String, Object> txnAmountDetails = fraudTxnDao.getTxnAmountAndCountByFilter(request);
							double totalAmt = (double) txnAmountDetails.get("Amount");
							int totalTxn = (int) txnAmountDetails.get("Count");
						//	logger.info("**********************"+new Gson().toJson(fraudPrevention));
						//	logger.info("**********************"+totalAmt);
							logger.info("**********************"+totalAmt);


							if (totalAmt >= fraudPrevention.getTransactionAmount()
									|| totalTxn >= fraudPrevention.getRepetationCount()) {
								setFraudError(fields, fraudType);
							}
						} else if (!((requestAmount >= Double.parseDouble(fraudPrevention.getMinTransactionAmount())
								&& (requestAmount <= Double.parseDouble(fraudPrevention.getMaxTransactionAmount()))))) {
							setFraudError(fields, fraudType);
						}
					}
					break;
				case BLOCK_TXN_AMOUNT_VELOCITY:
					if ((!isMerchantHosted && !(txnType.equals(TransactionType.NEWORDER.getName())
							|| txnType.equals(TransactionType.ENROLL.getName())
							|| txnType.equals(TransactionType.SALE.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnTxnAmountVelocityBlocking()) {
						break;
					}
					validateVelocity(payId, fields, fraudPrevention);
					break;
				case BLOCK_IP_ADDRESS:
					if ((!isMerchantHosted && !(txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnIpBlocking()) {
						logger.info("IP ADDRESS NOT BLOCKED due to TRANSACTION TYPE  NEW ORDER"+txnType.equals(TransactionType.NEWORDER.getName())+" ENROLL "+txnType.equals(TransactionType.ENROLL.getName())+" configuration blocking "+configuration.isOnIpBlocking());
						break;
					}
					Collection<String> ipAddressList = Helper.parseFields(fraudPrevention.getIpAddress());
					logger.info("IP Address List{} ",ipAddressList);
					for (String blockIpAddress : ipAddressList) {
						validateRequestField(fields, fraudPrevention, blockIpAddress);
					}
					break;
				case BLOCK_EMAIL_ID:
					logger.info("Email id Case");
					if ((!isMerchantHosted && !(txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnEmailBlocking()) {

						logger.info("Email id on blocking configuration "+configuration.isOnEmailBlocking()+" or enroll "+txnType.equals(TransactionType.ENROLL.getName())+" or new order"+txnType.equals(TransactionType.NEWORDER.getName()));
						break;
					}
					logger.info("BLOCK BY EMAIL ID CASE");
					Collection<String> blockEmailIdList = Helper.parseFields(fraudPrevention.getEmail());
					logger.info("Block Email id List {} ",blockEmailIdList);
					for (String blockEmail : blockEmailIdList) {

						validateRequestField(fields, fraudPrevention, blockEmail);
					}
					break;
				case BLOCK_PHONE_NUMBER:
					if ((!isMerchantHosted && !(txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnPhoneNoBlocking()) {
						logger.info("Phone Number Blocking no new order "+txnType.equals(TransactionType.NEWORDER.getName())+" ENROLL "+txnType.equals(TransactionType.NEWORDER.getName())+" configuration "+configuration.isOnPhoneNoBlocking());
						break;
					}
					logger.info("BLOCK BY PHONE NUMBER CASE");
					Collection<String> blockPhoneList = Helper.parseFields(fraudPrevention.getPhone());
					logger.info("Phone Number {}", blockPhoneList);
					for (String blockPhone : blockPhoneList) {
						validateRequestField(fields, fraudPrevention, blockPhone);
					}
					break;
				case BLOCK_CARD_ISSUER_COUNTRY:
					if ((!isMerchantHosted && (txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnIssuerCountriesBlocking()) {
						break;
					}
					logger.info("BLOCK BY CARD ISSUER COUNTRY CASE");
					Collection<String> blockCardIssuerList = Helper.parseFields(fraudPrevention.getIssuerCountry());
					logger.info("CARD ISSUER COUNTRY{} ",blockCardIssuerList);
					for (String blockCardIssuer : blockCardIssuerList) {
						validateRequestField(fields, fraudPrevention, blockCardIssuer);
					}
					break;
				case BLOCK_USER_COUNTRY:
					if ((!isMerchantHosted && !(txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnUserCountriesBlocking()) {
						logger.info("block user country new order "+txnType.equals(TransactionType.NEWORDER.getName())+" ENROLL "+txnType.equals(TransactionType.ENROLL.getName())+" blocking enabled "+configuration.isOnUserCountriesBlocking());
						break;
					}
					logger.info("BLOCK BY USER COUNTRY ");
					String country = geolocationApiService.getCountryByIp(ipAddress);
					logger.info("User Country "+country);
					country = StringUtils.isBlank(country) ? fields.get(fraudType.getFieldName()) : country;
					if (StringUtils.equalsAnyIgnoreCase(country,
							StringUtils.split(fraudPrevention.getUserCountry(), ","))) {
						setFraudError(fields, fraudType);
					}
					break;
				case BLOCK_USER_STATE:
					if ((!isMerchantHosted && !(txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnStateBlocking()) {
						logger.info("block user state new order "+txnType.equals(TransactionType.NEWORDER.getName())+" ENROLL "+txnType.equals(TransactionType.ENROLL.getName())+" blocking enabled "+configuration.isOnUserCountriesBlocking());

						break;
					}
					logger.info("BLOCK BY USER STATE CASE");
					String state = geolocationApiService.getStateByIp(ipAddress);
					logger.info("State List "+state);
					if (StringUtils.equalsAnyIgnoreCase(state,
							StringUtils.split(fraudPrevention.getStateCode(), ","))) {
						setFraudError(fields, fraudType);
					}
					break;
				case BLOCK_USER_CITY:
					if ((!isMerchantHosted && !(txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnCityBlocking()) {
						logger.info("block user city new order "+txnType.equals(TransactionType.NEWORDER.getName())+" ENROLL "+txnType.equals(TransactionType.ENROLL.getName())+" blocking enabled "+configuration.isOnCityBlocking());

						break;
					}
					logger.info("BLOCK BY USER CITY CASE");
					String city = geolocationApiService.getCityByIp(ipAddress);
					logger.info("City List "+city);
					if (StringUtils.equalsAnyIgnoreCase(city, StringUtils.split(fraudPrevention.getCity(), ","))) {
						setFraudError(fields, fraudType);
					}
					break;
				case BLOCK_CARD_NO:
					if ((!isMerchantHosted && (txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnCardMaskBlocking()) {
						break;
					}
					logger.info("BLOCK BY CARD NO CASE");
					Collection<String> cardNoList = Helper.parseFields(fraudPrevention.getNegativeCard());

					if (fields.contains(fraudType.getFieldName())) {
						String cardMask = FraudPreventionUtil.makeCardMask1(fields);
						LocalDateTime currentDateTime = DateCreater.now();
						String startDate = getStartDateByTimeUnits(currentDateTime,
								fraudPrevention.getBlockTimeUnits());
						String endDate = currentDateTime.format(DateCreater.dateTimeFormat);
//						long count = fraudTxnDao.getPerCardTransactions(payId, cardMask, startDate, endDate);
						for (String cardNo : cardNoList) {
							if (cardMask.equals(cardNo)) {
								boolean alwaysOnflag = fraudPrevention.isAlwaysOnFlag();
								if (alwaysOnflag == false) {
									result = validateTime(fraudPrevention);
								} else {
									result = true;
								}
//								if (result
//										&& count >= Integer.parseInt(fraudPrevention.getPerCardTransactionAllowed())) {
								if(result){
									setFraudError(fields, fraudType);
								}
							}
						}
					}

					break;
				case BLOCK_CARD_BIN:
					if ((!isMerchantHosted && (txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnCardRangeBlocking()) {
						break;
					}
					logger.info("BLOCK CARD BIN CASE");
					Collection<String> cardBinList = Helper.parseFields(fraudPrevention.getNegativeBin());
					if (fields.contains(fraudType.getFieldName())) {
						String reqCardNo = fields.get(FieldType.CARD_NUMBER.getName());
						String reqBin = reqCardNo.substring(0, SystemConstants.CARD_BIN_LENGTH);
						for (String cardBin : cardBinList) {
							if (reqBin.equals(cardBin)) {
								boolean alwaysOnflag = fraudPrevention.isAlwaysOnFlag();
								if (alwaysOnflag == false) {
									result = validateTime(fraudPrevention);
								} else {
									setFraudError(fields, fraudType);
								}
								if (result == true) {
									setFraudError(fields, fraudType);
								}
							}
						}
					}
					break;
				case BLOCK_CARD_TXN_THRESHOLD:
					if ((!isMerchantHosted && (txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnLimitCardTxnBlocking()) {
						break;
					}
					if (fields.contains(fraudType.getFieldName())) {
						String cardMask2 = FraudPreventionUtil.makeCardMask(fields);

						LocalDateTime currentDateTime = DateCreater.now();
						String startDate = getStartDateByTimeUnits(currentDateTime,
								fraudPrevention.getBlockTimeUnits());
						String endDate = currentDateTime.format(DateCreater.dateTimeFormat);
						logger.info("BLOCK CARD TXN THRESHOLD CASE");
						long noOfTransctions = 0;
						try {
							noOfTransctions = fraudTxnDao.getPerCardTransactions(fields.get(FieldType.PAY_ID.getName()),
									cardMask2, startDate, endDate);
						} catch (Exception e) {
							logger.error("Database Error while fetching no of txns :" + e);
						}
						long perCardTxnAllowed = Long.parseLong(fraudPrevention.getPerCardTransactionAllowed());

						boolean alwaysOnflag = fraudPrevention.isAlwaysOnFlag();
						if (alwaysOnflag == false) {
							result = validateTime(fraudPrevention);
						}

						if (!(noOfTransctions < perCardTxnAllowed) && (alwaysOnflag || result)) {
							setFraudError(fields, fraudType);
						}

					}
					break;
				case FIRST_TRANSACTIONS_ALERT:
//					String status = fields.get(FieldType.STATUS.getName());
//					if (!StringUtils.equalsAnyIgnoreCase(status, StatusType.getInternalStatus("Captured").split(","))) {
//						logger.info("applyRule:: skipped as failed or pending transaction");
//						break;
//					}
					String tempRequestAmount = fields.get(FieldType.AMOUNT.getName());
					Double requestAmount = Double.valueOf(tempRequestAmount);
					List<Double> amounts = fraudTxnDao.getAmountByMerchant(payId).stream().filter(amount -> amount >= fraudPrevention.getTransactionAmount())
							.collect(Collectors.toList());
					logger.info("Amounts: {}", amounts);
					if (amounts.isEmpty()
							&& requestAmount >= fraudPrevention.getTransactionAmount()) {
						sendFirstTransactionEmail(payId, fraudPrevention);
					}
					break;
				case REPEATED_MOP_TYPES:
					if ((!isMerchantHosted && (txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnNotifyRepeatedMopType()) {
						break;
					}
					if (!StringUtils.equalsIgnoreCase(fraudPrevention.getPaymentType(), fields.get("PAYMENT_TYPE"))) {
						break;
					}
					LocalDateTime currentDateTime = DateCreater.now();
					String startDate = getStartDateByTimeUnits(currentDateTime, fraudPrevention.getBlockTimeUnits());
					String endDate = currentDateTime.format(DateCreater.dateTimeFormat);
					double percentage = fraudTxnDao.getPercentageByMopType(payId, fraudPrevention.getPaymentType(),
							startDate, endDate);
					if (percentage < fraudPrevention.getPercentageOfRepeatedMop()) {
						break;
					}
					Map<String, Double> percentageByPaymentType = new HashMap<>();
					percentageByPaymentType.put(fraudPrevention.getPaymentType(), percentage);
					String merchantName = userDao.getBusinessNameByPayId(payId);
					emailControllerServiceProvider.repeatedMopTypeEmail(
							Arrays.asList(fraudPrevention.getEmailToNotify().split(",")), percentageByPaymentType,
							merchantName);
					break;

				case BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL:
					if ((!isMerchantHosted && (txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnBlockRepeatedMopTypeForSameDetails()) {
						break;
					}
					validateRepeatedMopTypeForSameDetails(payId, fields, fraudPrevention);
					break;
				case BLOCK_MACK_ADDRESS:
					if ((!isMerchantHosted && !(txnType.equals(TransactionType.NEWORDER.getName())))
							|| (isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
							|| !configuration.isOnMacBlocking()) {
						break;
					}
					Collection<String> mackAddressList = Helper.parseFields(fraudPrevention.getMackAddress());
					for (String blockMackAddress : mackAddressList) {
						validateRequestField(fields, fraudPrevention, blockMackAddress);
					}
					break;
					
				case BLOCK_TICKET_LIMIT:
					if ((!isMerchantHosted && (txnType.equals(TransactionType.SALE.getName())))
							&& (transactionStatus.equalsIgnoreCase(StatusType.PENDING.getName()))) {
						//logger.info("<<<<<<<<<<< BLOCK_TICKET_LIMIT >>>>>>>>>>>> ");
						//logger.info("BLOCK_TICKET_LIMIT, FraudPrevention = {} ", fraudPrevention);
						//logger.info("BLOCK_TICKET_LIMIT, TxnType = {} ", txnType);

						if (StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block")) {
							setFraudError(fields, fraudType);
						}
						
						String tempTxnAmount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName()));
						Double txnAmount = Double.valueOf(tempTxnAmount);
						logger.info("BLOCK_TICKET_LIMIT, TxnAmount = {} ", txnAmount);
						if (txnAmount < Double.valueOf(fraudPrevention.getMinTransactionAmount())
								|| txnAmount > Double.valueOf(fraudPrevention.getMaxTransactionAmount())) {
							logger.info("Bridge fraud rule For BLOCK_TICKET_LIMIT , payID = {}",
									fields.get(FieldType.PAY_ID.getName()));
							fields.put(Constants.PG_FRAUD_TYPE.getValue(), fraudType.getValue());
							 fields.put(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName());
							addFraudEntryInDb(fields);
						}

					} else {
						//logger.info("BLOCK_TICKET_LIMIT BREAK");
						break;
					}

					break;
				case BLOCK_TRANSACTION_LIMIT:

					if ((!isMerchantHosted && (txnType.equals(TransactionType.SALE.getName())))
							&& (transactionStatus.equalsIgnoreCase(StatusType.PENDING.getName()))) {
						//logger.info("<<<<<<<<<< BLOCK_TRANSACTION_LIMIT >>>>>>>>>>>>");

						//logger.info("BLOCK_TRANSACTION_LIMIT, TxnType = {} ", txnType);
						if (StringUtils.equalsIgnoreCase(fraudPrevention.getBlockDaily(), "Daily")) {
							
							if (StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block") 
									&& StringUtils.containsIgnoreCase(fraudPrevention.getBlockDailyType(), "Y")) {
								setFraudError(fields, fraudType);
							}
							
							String tempRequestAmount1 = fields.get(FieldType.AMOUNT.getName());
							Double requestAmount1 = Double.valueOf(tempRequestAmount1) / 100;
							requestAmount1 = requestAmount1 == null ? 0 : requestAmount1;
							LocalDateTime currentDateTime1 = DateCreater.now();
							String startDate1 = getStartDateByTimeUnits(currentDateTime1, "Daily");
							String endDate1 = currentDateTime1.format(DateCreater.dateTimeFormat);
							TxnFilterRequest request = new TxnFilterRequest(payId, startDate1, endDate1,
									fields.get(FieldType.CURRENCY_CODE.getName()), null, null, null, null, null, 0,
									null, null);
							Map<String, Object> txnAmountDetails = fraudTxnDao.getTxnAmountAndCountByFilter(request);
							double totalAmt = (double) txnAmountDetails.get("Amount");
							logger.info("BLOCK_TRANSACTION_LIMIT Daily TotalAmt = {}, requestAmount1 = {} ", totalAmt,
									requestAmount1);
							if (totalAmt >= Double.valueOf(fraudPrevention.getDailyAmount())) {
								logger.info("Bridge fraud rule For BLOCK_TRANSACTION_LIMIT Daily, payID = {}",
										fields.get(FieldType.PAY_ID.getName()));
								fields.put(Constants.PG_FRAUD_TYPE.getValue(), fraudType.getValue());
								 fields.put(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName());
								fields.put(Constants.PG_BLOCK_TYPE.getValue(), fraudPrevention.getBlockDaily());
								addFraudEntryInDb(fields);
							}

						}
						if (StringUtils.equalsIgnoreCase(fraudPrevention.getBlockWeekly(), "Weekly")) {
							
							if (StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block") 
									&& StringUtils.containsIgnoreCase(fraudPrevention.getBlockWeeklyType(), "Y")) {
								setFraudError(fields, fraudType);
							}
							
							String tempRequestAmount1 = fields.get(FieldType.AMOUNT.getName());
							Double requestAmount1 = Double.valueOf(tempRequestAmount1) / 100;
							requestAmount1 = requestAmount1 == null ? 0 : requestAmount1;
							LocalDateTime currentDateTime1 = DateCreater.now();
							String startDate1 = getStartDateByTimeUnits(currentDateTime1, "Weekly");
							String endDate1 = currentDateTime1.format(DateCreater.dateTimeFormat);
							TxnFilterRequest request = new TxnFilterRequest(payId, startDate1, endDate1,
									fields.get(FieldType.CURRENCY_CODE.getName()), null, null, null, null, null, 0,
									null, null);
							Map<String, Object> txnAmountDetails = fraudTxnDao.getTxnAmountAndCountByFilter(request);
							double totalAmt = (double) txnAmountDetails.get("Amount");
							logger.info("BLOCK_TRANSACTION_LIMIT Weekly TotalAmt = {}, requestAmount1 = {} ", totalAmt,
									requestAmount1);
							if (totalAmt >= Double.valueOf(fraudPrevention.getWeeklyAmount())) {
								logger.info("Bridge fraud rule For BLOCK_TXN_AMOUNT Weekly, payID = {}",
										fields.get(FieldType.PAY_ID.getName()));
								fields.put(Constants.PG_FRAUD_TYPE.getValue(), fraudType.getValue());
								 fields.put(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName());
								fields.put(Constants.PG_BLOCK_TYPE.getValue(), fraudPrevention.getBlockWeekly());
								addFraudEntryInDb(fields);
							}
						}
						if (StringUtils.equalsIgnoreCase(fraudPrevention.getBlockMonthly(), "Monthly")) {
							
							if (StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block") 
									&& StringUtils.containsIgnoreCase(fraudPrevention.getBlockMonthlyType(), "Y")) {
								setFraudError(fields, fraudType);
							}
							
							String tempRequestAmount1 = fields.get(FieldType.AMOUNT.getName());
							Double requestAmount1 = Double.valueOf(tempRequestAmount1) / 100;
							requestAmount1 = requestAmount1 == null ? 0 : requestAmount1;
							LocalDateTime currentDateTime1 = DateCreater.now();
							String startDate1 = getStartDateByTimeUnits(currentDateTime1, "Monthly");
							String endDate1 = currentDateTime1.format(DateCreater.dateTimeFormat);
							TxnFilterRequest request = new TxnFilterRequest(payId, startDate1, endDate1,
									fields.get(FieldType.CURRENCY_CODE.getName()), null, null, null, null, null, 0,
									null, null);
							Map<String, Object> txnAmountDetails = fraudTxnDao.getTxnAmountAndCountByFilter(request);
							double totalAmt = (double) txnAmountDetails.get("Amount");
							logger.info("BLOCK_TRANSACTION_LIMIT Monthly TotalAmt = {}, requestAmount1 = {} ", totalAmt,
									requestAmount1);
							if (totalAmt >= Double.valueOf(fraudPrevention.getMonthlyAmount())) {
								logger.info("Bridge fraud rule For BLOCK_TXN_AMOUNT Monthly, payID = {}",
										fields.get(FieldType.PAY_ID.getName()));
								fields.put(Constants.PG_FRAUD_TYPE.getValue(), fraudType.getValue());
								 fields.put(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName());
								fields.put(Constants.PG_BLOCK_TYPE.getValue(), fraudPrevention.getBlockMonthly());
								addFraudEntryInDb(fields);
							}
						}

					} else {
						//logger.info("BLOCK_TRANSACTION_LIMIT BREAK");
						break;
					}

					break;
				
				case BLOCK_MOP_LIMIT:
					if ((!isMerchantHosted && (txnType.equals(TransactionType.SALE.getName())))
							&& (transactionStatus.equalsIgnoreCase(StatusType.PENDING.getName()))) {
						//logger.info("<<<<<<<<<<< BLOCK_TICKET_LIMIT >>>>>>>>>>>>>>>");

						if (!StringUtils.equalsIgnoreCase(fraudPrevention.getPaymentType(),
								fields.get("PAYMENT_TYPE"))) {
							break;
						}

						//logger.info("BLOCK_MOP_LIMIT, FraudPrevention = {} ", fraudPrevention);
						//logger.info("BLOCK_MOP_LIMIT, TxnType = {},PaymentType = {} ", txnType,	fields.get("PAYMENT_TYPE"));

						if (StringUtils.equalsIgnoreCase(fraudPrevention.getBlockDaily(), "Daily")) {
							
							if (StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block") 
									&& StringUtils.containsIgnoreCase(fraudPrevention.getBlockDailyType(), "Y")) {
								setFraudError(fields, fraudType);
							}
							
							String tempRequestAmount1 = fields.get(FieldType.AMOUNT.getName());
							Double requestAmount1 = Double.valueOf(tempRequestAmount1) / 100;
							requestAmount1 = requestAmount1 == null ? 0 : requestAmount1;
							LocalDateTime currentDateTime1 = DateCreater.now();
							String startDate1 = getStartDateByTimeUnits(currentDateTime1, "Daily");
							String endDate1 = currentDateTime1.format(DateCreater.dateTimeFormat);
							TxnFilterRequest request = new TxnFilterRequest(payId, startDate1, endDate1,
									fields.get(FieldType.CURRENCY_CODE.getName()), null, null, null, null, null, 0,
									null, fields.get(FieldType.PAYMENT_TYPE.getName()));
							Map<String, Object> txnAmountDetails = fraudTxnDao.getTxnAmountAndCountByFilter(request);
							double totalAmt = (double) txnAmountDetails.get("Amount");
							logger.info("BLOCK_MOP_LIMIT Daily TotalAmt = {}, requestAmount1 = {} ", totalAmt,
									requestAmount1);
							if (totalAmt >= Double.valueOf(fraudPrevention.getDailyAmount())) {
								logger.info("Bridge fraud rule For BLOCK_TICKET_LIMIT Daily, PaymentType = {}",
										fields.get("PAYMENT_TYPE"));
								fields.put(Constants.PG_FRAUD_TYPE.getValue(), fraudType.getValue());
								 fields.put(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName());
								fields.put(Constants.PG_BLOCK_TYPE.getValue(), fraudPrevention.getBlockDaily());
								addFraudEntryInDb(fields);
							}

						}
						if (StringUtils.equalsIgnoreCase(fraudPrevention.getBlockWeekly(), "Weekly")) {
							
							if (StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block") 
									&& StringUtils.containsIgnoreCase(fraudPrevention.getBlockWeeklyType(), "Y")) {
								setFraudError(fields, fraudType);
							}
							
							
							String tempRequestAmount1 = fields.get(FieldType.AMOUNT.getName());
							Double requestAmount1 = Double.valueOf(tempRequestAmount1) / 100;
							requestAmount1 = requestAmount1 == null ? 0 : requestAmount1;
							LocalDateTime currentDateTime1 = DateCreater.now();
							String startDate1 = getStartDateByTimeUnits(currentDateTime1, "Weekly");
							String endDate1 = currentDateTime1.format(DateCreater.dateTimeFormat);
							TxnFilterRequest request = new TxnFilterRequest(payId, startDate1, endDate1,
									fields.get(FieldType.CURRENCY_CODE.getName()), null, null, null, null, null, 0,
									null, fields.get(FieldType.PAYMENT_TYPE.getName()));
							Map<String, Object> txnAmountDetails = fraudTxnDao.getTxnAmountAndCountByFilter(request);
							double totalAmt = (double) txnAmountDetails.get("Amount");
							logger.info("BLOCK_MOP_LIMIT Weekly TotalAmt = {}, requestAmount1 = {} ", totalAmt,
									requestAmount1);
							if (totalAmt >= Double.valueOf(fraudPrevention.getWeeklyAmount())) {
								logger.info("Bridge fraud rule For BLOCK_TICKET_LIMIT Weekly, PaymentType = {}",
										fields.get("PAYMENT_TYPE"));
								fields.put(Constants.PG_FRAUD_TYPE.getValue(), fraudType.getValue());
								fields.put(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName());
								fields.put(Constants.PG_BLOCK_TYPE.getValue(), fraudPrevention.getBlockWeekly());
								addFraudEntryInDb(fields);
							}
						}
						if (StringUtils.equalsIgnoreCase(fraudPrevention.getBlockMonthly(), "Monthly")) {
							
							if (StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block") 
									&& StringUtils.containsIgnoreCase(fraudPrevention.getBlockMonthlyType(), "Y")) {
								setFraudError(fields, fraudType);
							}
							
							String tempRequestAmount1 = fields.get(FieldType.AMOUNT.getName());
							Double requestAmount1 = Double.valueOf(tempRequestAmount1) / 100;
							requestAmount1 = requestAmount1 == null ? 0 : requestAmount1;
							LocalDateTime currentDateTime1 = DateCreater.now();
							String startDate1 = getStartDateByTimeUnits(currentDateTime1, "Monthly");
							String endDate1 = currentDateTime1.format(DateCreater.dateTimeFormat);
							TxnFilterRequest request = new TxnFilterRequest(payId, startDate1, endDate1,
									fields.get(FieldType.CURRENCY_CODE.getName()), null, null, null, null, null, 0,
									null, fields.get(FieldType.PAYMENT_TYPE.getName()));
							Map<String, Object> txnAmountDetails = fraudTxnDao.getTxnAmountAndCountByFilter(request);
							double totalAmt = (double) txnAmountDetails.get("Amount");
							logger.info("BLOCK_MOP_LIMIT Monthly TotalAmt = {}, requestAmount1 = {} ", totalAmt,
									requestAmount1);
							if (totalAmt >= Double.valueOf(fraudPrevention.getMonthlyAmount())) {
								logger.info("Bridge fraud rule For BLOCK_TICKET_LIMIT Monthly, PaymentType = {}",
										fields.get("PAYMENT_TYPE"));
								fields.put(Constants.PG_FRAUD_TYPE.getValue(), fraudType.getValue());
								 fields.put(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName());
								fields.put(Constants.PG_BLOCK_TYPE.getValue(), fraudPrevention.getBlockMonthly());
								addFraudEntryInDb(fields);
							}
						}

					} else {
						//logger.info("BLOCK_TICKET_LIMIT BREAK");
						break;
					}

					break;
				case BLOCK_VPA_ADDRESS:
					logger.info("BLOCK_VPA_ADDRESS CASE...");
					if(StringUtils.isNotBlank(fields.get("PAYMENT_TYPE")) && fields.get("PAYMENT_TYPE").equalsIgnoreCase("UP")) {
						if ((!isMerchantHosted && (txnType.equals(TransactionType.NEWORDER.getName())))
								//(isMerchantHosted && !(txnType.equals(TransactionType.ENROLL.getName())))
								|| !configuration.isOnVpaAddressBlocking() ||(!(txnType.equals(TransactionType.SALE.getName())) && !(txnType.equals(TransactionType.ENROLL.getName()))) ) {
							//logger.info("if condition for VPA invoked");
							break;
						}
				
					Collection<String> vpaAddressList = Helper.parseFields(fraudPrevention.getVpaAddress());
					for (String blockVpaAddress : vpaAddressList) {
						//logger.info("vpaAddress..={}",blockVpaAddress);
						String getVpa=  fields.get(FieldType.PAYER_ADDRESS.getName());
						if(getVpa!=null) {
						//logger.info("getVpa..={}",getVpa);
						String vpaArr[]= getVpa.split("@");
						String splittedVpa=vpaArr[0];
						
						//logger.info("splittedVpa..={}",splittedVpa);
						
						
						String a[]=blockVpaAddress.split("@");
						//condition: get prefix of vpa address  when vpa address contains @all as suffix
						//,need to block based on the prefix value
			
						if (a[1].equalsIgnoreCase("all")) {
							if(a[0].equalsIgnoreCase(splittedVpa)) {
								validateRequestField(fields, fraudPrevention, getVpa);	
								
							}
						}else {
								validateRequestField(fields, fraudPrevention, blockVpaAddress);
							
						}
					
					}
				}
				}
					break;
				default: // logically not reachable
					logger.error("Something went wrong while checking fraud field values");
					break;
				}
				logger.info("out of switch case");

			}
		} else {
			logger.info("Fraud prevention list null");
			return;
		}
	}

	private void setFraudError(Fields fields, final FraudRuleType fraudType) throws SystemException {
		fields.put(Constants.PG_FRAUD_TYPE.getValue(), fraudType.getValue());
		fields.put(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName());
//		fields.put(FieldType.RESPONSE_CODE.getName(),ErrorType.DENIED_BY_FRAUD.getResponseCode());
		fields.put("monitorType", "Block");
		logger.info("Set Fraud Error throws Exception");
		addFraudEntryInDb(fields);
		throw new SystemException(ErrorType.DENIED_BY_FRAUD,
				"Fraud transaction with PAY_ID=" + fields.get(CrmFieldType.PAY_ID.getName()) + " detected and blocked"
						+ "with FRAUD_TYPE: " + fraudType.getValue());
	}

	private void validateVelocity(String payId, Fields fields, FraudPreventionObj fraudPrevention)
			throws SystemException {
		LocalDateTime currentDateTime = DateCreater.now();
		String startDate = getStartDateByTimeUnits(currentDateTime, fraudPrevention.getBlockTimeUnits());
		String endDate = currentDateTime.format(DateCreater.dateTimeFormat);
		String ipAddress = fields.get(FieldType.INTERNAL_CUST_IP.getName());
		String emailId = fields.get(FieldType.CUST_EMAIL.getName());
		String mobileNo = fields.get(FieldType.CUST_PHONE.getName());
		String currencyCod = fields.get(FieldType.CURRENCY_CODE.getName());
		String vpa = StringUtils.equalsIgnoreCase(fields.get(FieldType.PAYMENT_TYPE.getName()), "UP")
				? fields.get(FieldType.CARD_MASK.getName())
				: "";

		if (StringUtils.isBlank(vpa) && StringUtils.equalsIgnoreCase(fraudPrevention.getUserIdentifier(), "VPA")) {
			logger.info("validateVelocity:: skipped as VPA is blank");
			return;
		}
		
		if (StringUtils.isBlank(ipAddress) && StringUtils.equalsIgnoreCase(fraudPrevention.getUserIdentifier(), "ipAddress")) {
			logger.info("validateVelocity:: skipped as IP ADDRESS is blank");
			return;
		}

		if (StringUtils.equalsIgnoreCase(fraudPrevention.getUserIdentifier(), "Card")) {
			validateCard(payId, fraudPrevention, startDate, endDate, fields);
			return;
		}

		if (!StringUtils.equalsIgnoreCase(fraudPrevention.getUserIdentifier(), "VPA")) {
			vpa = null;
		}

		if (!StringUtils.equalsIgnoreCase("Email", fraudPrevention.getUserIdentifier())) {
			emailId = null;
		}

		if (!StringUtils.equalsIgnoreCase("Phone", fraudPrevention.getUserIdentifier())) {
			mobileNo = null;
		}

		if (!StringUtils.equalsIgnoreCase("ipAddress", fraudPrevention.getUserIdentifier())) {
			ipAddress = null;
		} 

		FraudRuleType fraudType = fraudPrevention.getFraudType();
		List<FraudTxnExcelModel> transactions = new ArrayList<>();
		try {
			String payIdForQuery = StringUtils.isBlank(fraudPrevention.getParentId()) ? fraudPrevention.getPayId()
					: "ALL";
			TxnFilterRequest request = new TxnFilterRequest(payIdForQuery, startDate, endDate, currencyCod, ipAddress,
					emailId, mobileNo, null, vpa, 0, null, null);
			transactions = fraudTxnDao.getTxnByFilter(request);
		} catch (Exception ex) {
			logger.error("validateVelocity:: failed. payId={}", payId, ex);
		}
		double totalTxnAmount = transactions.stream()
				.filter(doc -> StringUtils.equalsIgnoreCase(doc.getStatus(), "Captured"))
				.collect(Collectors.summingDouble(doc -> doc.getBaseAmount()));
		long totalTxn = transactions.size();
		if (!StringUtils.equalsAnyIgnoreCase(fraudPrevention.getStatusVelocity(), "ALL")) {
			totalTxn = transactions.stream()
					.filter(doc -> StringUtils.equalsAnyIgnoreCase(doc.getStatus(),
							getDbStatus(fraudPrevention.getStatusVelocity()).split(",")))
					.collect(Collectors.counting());
		}

		transactions = transactions.stream()
				.filter(doc -> Double.valueOf(fraudPrevention.getMaxTransactionAmount()) > 0
						&& totalTxnAmount >= Double.valueOf(fraudPrevention.getMaxTransactionAmount())
								? StringUtils.equalsIgnoreCase(doc.getStatus(), "Captured")
								: StringUtils.equalsAnyIgnoreCase(doc.getStatus(),
										getDbStatus(fraudPrevention.getStatusVelocity()).split(",")))
				.collect(Collectors.toList());

		if ((Double.valueOf(fraudPrevention.getMaxTransactionAmount()) > 0
				&& totalTxnAmount >= Double.valueOf(fraudPrevention.getMaxTransactionAmount()))
				|| totalTxn >= fraudPrevention.getRepetationCount()) {
			sendVelocityEmail(fraudPrevention.getPayId(), fraudPrevention, transactions);
			if (StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block")) {
				setFraudError(fields, fraudType);
			} else {
				fields.put("monitorType", "Notify");
				addFraudEntryInDb(fields);
			}
		}
	}

	private String getDbStatus(String velocityStatus) {
		if (StringUtils.equalsIgnoreCase(velocityStatus, "Success")) {
			return "Captured";
		}
		if (StringUtils.equalsIgnoreCase(velocityStatus, "Failed")) {
			return "Failed,Error,Cancelled By User,Failed at Acquirer,Rejected,Denied due to fraud";
		}
		return "Invalid,Sent to Bank,Pending";
	}

	private void validateCard(String payId, FraudPreventionObj fraudPrevention, String startDate, String endDate,
			Fields fields) throws SystemException {
		List<FraudTxnExcelModel> transactions = new ArrayList<>();
		String cardMask = fields.get(FieldType.CARD_MASK.getName());
		if (StringUtils.isBlank(cardMask)) {
			logger.info("validateCard:: skipped as card mask is not available");
			return;
		}
		try {
			String payIdForQuery = StringUtils.isBlank(fraudPrevention.getParentId()) ? fraudPrevention.getPayId()
					: "ALL";
			TxnFilterRequest request = new TxnFilterRequest(payIdForQuery, startDate, endDate, null, null, null, null,
					null, null, 0, null, null);
			transactions = fraudTxnDao.getTxnByFilter(request);
			transactions = transactions.stream()
					.filter(doc -> StringUtils.equalsIgnoreCase(doc.getCardMask(), cardMask))
					.collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error("validateCard:: failed. payId={}", payId, ex);
		}
		double totalTxnAmount = transactions.stream()
				.filter(doc -> StringUtils.equalsIgnoreCase(doc.getStatus(), "Captured"))
				.collect(Collectors.summingDouble(doc -> doc.getBaseAmount()));
		long totalTxn = transactions.size();
		if (!StringUtils.equalsAnyIgnoreCase(fraudPrevention.getStatusVelocity(), "ALL")) {
			totalTxn = transactions.stream()
					.filter(doc -> StringUtils.equalsAnyIgnoreCase(doc.getStatus(),
							getDbStatus(fraudPrevention.getStatusVelocity()).split(",")))
					.collect(Collectors.counting());
		}

		transactions = transactions.stream()
				.filter(doc -> Double.valueOf(fraudPrevention.getMaxTransactionAmount()) > 0
						&& totalTxnAmount >= Double.valueOf(fraudPrevention.getMaxTransactionAmount())
								? StringUtils.equalsIgnoreCase(doc.getStatus(), "Captured")
								: StringUtils.equalsAnyIgnoreCase(doc.getStatus(),
										getDbStatus(fraudPrevention.getStatusVelocity()).split(",")))
				.collect(Collectors.toList());

		if ((Double.valueOf(fraudPrevention.getMaxTransactionAmount()) > 0
				&& totalTxnAmount >= Double.valueOf(fraudPrevention.getMaxTransactionAmount()))
				|| totalTxn >= fraudPrevention.getRepetationCount()) {
			sendVelocityEmail(fraudPrevention.getPayId(), fraudPrevention, transactions);
			if (StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block")) {
				setFraudError(fields, fraudPrevention.getFraudType());
			} else {
				fields.put("monitorType", "Notify");
				addFraudEntryInDb(fields);
			}
		}
	}

	private void validateRepeatedMopTypeForSameDetails(String payId, Fields fields, FraudPreventionObj fraudPrevention)
			throws SystemException {
		LocalDateTime currentDateTime = DateCreater.now();
		String startDate = getStartDateByTimeUnits(currentDateTime, fraudPrevention.getBlockTimeUnits());
		String endDate = currentDateTime.format(DateCreater.dateTimeFormat);
		String ipAddress = fields.get(FieldType.INTERNAL_CUST_IP.getName());
		String emailId = fields.get(FieldType.CUST_EMAIL.getName());
		String mobileNo = fields.get(FieldType.CUST_PHONE.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String tempRequestAmount = fields.get(FieldType.AMOUNT.getName());
		Double requestAmount = Double.parseDouble(Amount.toDecimal(tempRequestAmount, currencyCode));
		String mopType = fields.get(FieldType.MOP_TYPE.getName());
		String cardMask = fields.get(FieldType.CARD_MASK.getName());
		logger.info(
				"validateRepeatedMopTypeForSameDetails:: startDate={},endDate={}, ipAddress={}, emailId={}, mobileNo={}, currencyCode={}, requestAmount={}, mopType={}, cardMask={}",
				startDate, endDate, ipAddress, emailId, mobileNo, currencyCode, requestAmount, mopType, cardMask);
		if (StringUtils.isBlank(mopType)) {
			logger.info("validateRepeatedMopTypeForSameDetails:: skipped as mop type not found");
			return;
		}
		requestAmount = requestAmount == null ? 0 : requestAmount;
		TxnFilterRequest request = new TxnFilterRequest(fields.get(FieldType.PAY_ID.getName()), startDate, endDate,
				currencyCode, ipAddress, emailId, mobileNo, null, cardMask, requestAmount, mopType, null);
		List<FraudTxnExcelModel> transactions = new ArrayList<>();
		try {
			transactions = fraudTxnDao.getTxnByFilter(request);
		} catch (ParseException ex) {
			logger.error("validateRepeatedMopTypeForSameDetails:: failed. payId={}", payId, ex);
		}

		if (transactions.size() >= fraudPrevention.getRepetationCount()) {
			fraudPrevention.setUserIdentifier("Repeated Amount");
			sendVelocityEmail(payId, fraudPrevention, transactions);
			if (StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block")) {
				setFraudError(fields, fraudPrevention.getFraudType());
			} else {
				fields.put("monitorType", "Notify");
				addFraudEntryInDb(fields);
			}
		}
	}

	private void sendFirstTransactionEmail(String payId, FraudPreventionObj fraudPrevention) {
		String merchantName = userDao.getBusinessNameByPayId(payId);
		emailControllerServiceProvider.sendFirstTransactionEmail(fraudPrevention.getEmailToNotify(), merchantName);
	}

	private void sendVelocityEmail(String payId, FraudPreventionObj fraudPrevention,
			List<FraudTxnExcelModel> transactions) {
		if (fraudPrevention.isNotified()) {
			logger.info("sendVelocityEmail:: skipped as already notified payId={}", fraudPrevention.getPayId());
			return;
		}
		List<String> emailIds = Arrays.asList(StringUtils.split(fraudPrevention.getEmailToNotify(), ","));
		transactions.forEach(transaction -> transaction
				.setMerchantName(StringUtils.equalsIgnoreCase(transaction.getMerchantName(), "ALL") ? "ALL"
						: userDao.getBusinessNameByPayId(transaction.getMerchantName())));
		addFraudEntryInDb(transactions);
		logger.info("Excel to be sent as attachment: {}",transactions);
		File excel = FraudTxnExcelWriter.writeExcel(transactions);
		String merchantNames = transactions.stream().map(FraudTxnExcelModel::getMerchantName)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining(", "));
		boolean isBlock = StringUtils.containsIgnoreCase(fraudPrevention.getMonitoringType(), "Block");
		emailControllerServiceProvider.velociTyBlockingEmail(emailIds, merchantNames, excel, isBlock,
				fraudPrevention.getUserIdentifier());
		markAsNotified(fraudPrevention);
	}

	private void addFraudEntryInDb(Fields field) {
		try {
			String store = field.get("NEED_TO_STORE_FRAUD");
			if (StringUtils.isBlank(store)) {
				FraudTransactionDetails model = entityToModel(field);
				if (StringUtils.isBlank(model.getPgRefNo())
						|| !fraudTransactionDetailsDao.isExist(model.getPgRefNo())) {
					logger.info("addFraudEntryInDb:: intialized to store data in db. pgRefNo={}", model.getPgRefNo());
					fraudTransactionDetailsDao.save(model);
				}
			}
		} catch (Exception ex) {
			logger.error("addFraudEntryInDb:: failed", ex);
		}
	}

	private void addFraudEntryInDb(List<FraudTxnExcelModel> transactions) {
		transactions.forEach(transaction -> {
			try {
				if (!fraudTransactionDetailsDao.isExist(transaction.getPgRefNo())) {
					fraudTransactionDetailsDao.save(entityToModel(transaction));
				}
			} catch (Exception ex) {
				logger.error("addFraudEntryInDb:: failed, pgRefNo={}", transaction.getPgRefNo(), ex);
			}
		});
	}

	private FraudTransactionDetails entityToModel(FraudTxnExcelModel transaction) {
		FraudTransactionDetails detail = new FraudTransactionDetails();
		detail.setTxnId(transaction.getTxnId());
		detail.setPgRefNo(transaction.getPgRefNo());
		detail.setDate(transaction.getDate());
		detail.setMerchantName(transaction.getMerchantName());
		detail.setOrderId(transaction.getOrderId());
		detail.setRefundOrderId(transaction.getRefundOrderId());
		detail.setMopType(transaction.getMopType());
		detail.setPaymentType(transaction.getPaymentType());
		detail.setTxnType("SALE");
		detail.setStatus(transaction.getStatus());
		detail.setBaseAmount(transaction.getBaseAmount());
		detail.setCustomerEmail(transaction.getCustomerEmail());
		detail.setCustomerPhone(transaction.getCustomerPhone());
		detail.setIpAddress(transaction.getIpAddress());
		detail.setCardMask(transaction.getCardMask());
		return detail;
	}

	private FraudTransactionDetails entityToModel(Fields fields) {
		FraudTransactionDetails detail = new FraudTransactionDetails();
		detail.setTxnId(fields.get(FieldType.TXN_ID.getName()));
		detail.setPgRefNo(fields.get(FieldType.PG_REF_NUM.getName()));
		detail.setDate(dateFormat.format(new Date()));
		detail.setOrderId(fields.get("ORDER_ID"));
		detail.setRefundOrderId(fields.get("REFUND_ORDER_ID"));
		String mopType = StringUtils.isNotBlank(fields.get("MOP_TYPE")) ? MopType.getmopName(fields.get("MOP_TYPE"))
				: "NA";
		detail.setMopType(mopType);
		String paymentType = StringUtils.isNotBlank(fields.get("PAYMENT_TYPE"))
				? PaymentType.getpaymentName(fields.get("PAYMENT_TYPE"))
				: "NA";
		detail.setPaymentType(paymentType);
		detail.setMerchantName(userDao.getBusinessNameByPayId(fields.get(FieldType.PAY_ID.getName())));
		detail.setTxnType("SALE");
		detail.setStatus(fields.get("STATUS"));
		DecimalFormat df = new DecimalFormat("#.00");
		String amount = df.format(Double.valueOf(fields.get("AMOUNT")) / 100);
		detail.setBaseAmount(Double.valueOf(amount));
		detail.setCustomerEmail(fields.get("CUST_EMAIL"));
		detail.setCustomerPhone(fields.get("CUST_PHONE"));
		detail.setIpAddress(fields.get("INTERNAL_CUST_IP"));
		detail.setCardMask(fields.get("CARD_MASK"));
		detail.setRuleType(fields.get("monitorType"));
		detail.setFraudType(fields.get(Constants.PG_FRAUD_TYPE.getValue()));
		detail.setBlockType(fields.get(Constants.PG_BLOCK_TYPE.getValue()));
		detail.setIsBlock("N");
		detail.setIsIgnore("N");
		detail.setIsNotify("N");

		return detail;
	}

	private void markAsNotified(FraudPreventionObj fraudPrevention) {
		fraudPrevention.setNotified(true);
		fraudPreventionMongoService.updateAdminFraudRule(fraudPrevention);
		logger.info("markAsNotified:: completed. id={}, payId={}", fraudPrevention.getId(), fraudPrevention.getPayId());
	}

	// to validate fields of same category
	private void validateRequestField(Fields fields, final FraudPreventionObj fraudPrevention,
			final String fraudFieldValue) throws SystemException {

		// checking other fields
		logger.info("@@@ Validate Request Field : " +fields.getFieldsAsString());
		boolean result = true;

		FraudRuleType fraudType = fraudPrevention.getFraudType();

		logger.info("@@@ validateRequestField FraudRuleType : {} ",fraudType);

		if (fields.contains(fraudType.getFieldName())) {
			logger.info("Fields contains field type ");
			if (fields.get(fraudType.getFieldName()).equalsIgnoreCase(fraudFieldValue)
					|| (fraudPrevention.getFraudType().getValue()
							.equalsIgnoreCase(FraudRuleType.BLOCK_CARD_ISSUER_COUNTRY.getValue())
							&& fields.get(fraudType.getFieldName()).toUpperCase().contains(fraudFieldValue))
					|| (fraudPrevention.getFraudType().getValue()
							.equalsIgnoreCase(FraudRuleType.BLOCK_USER_COUNTRY.getValue())
							&& fraudFieldValue.toUpperCase().contains(fields.get(fraudType.getFieldName())))) {
				logger.info("Validate Request Field validation"+fraudPrevention.getFraudType().getValue());
				logger.info("Validate Request Field validation"+fraudFieldValue.toUpperCase());
				logger.info("Validate Request Field validation"+fields.get(fraudType.getFieldName()));

				boolean alwaysOnflag = fraudPrevention.isAlwaysOnFlag();
				if (alwaysOnflag == false) {
					result = validateTime(fraudPrevention);
					logger.info("Result Flag: "+result);
				} else {
					setFraudError(fields, fraudType);
				}
				if (result == true) {
					logger.info("Set Fraud Error");
					setFraudError(fields, fraudType);
				}
			}
		}
	}

	private String getStartDateByTimeUnits(LocalDateTime currentDateTime, String timeUnit) {
		if (StringUtils.equals(timeUnit, "Hourly")) {
			return DateCreater.subtractHours(currentDateTime, 1);
		}

		if (StringUtils.equals(timeUnit, "Daily")) {
			return DateCreater.getCurrentDateWithSTime();
		}

		if (StringUtils.equals(timeUnit, "Weekly")) {
			return DateCreater.getCurrentWeekStartDate();
		}

		if (StringUtils.equals(timeUnit, "Monthly")) {
			return DateCreater.getCurrentMonthStartDate();
		}
		return DateCreater.getCurrentYearStartDate();
	}

	private boolean validateTime(FraudPreventionObj fraudPrevention) {
		String dateActiveFrom = fraudPrevention.getDateActiveFrom();
		String dateActiveTo = fraudPrevention.getDateActiveTo();

		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyyMMdd");
		String todayDate = formatter.format(date);
		logger.info("Validate Today Time: "+todayDate);

		int compareDateFrom = dateActiveFrom.compareTo(todayDate);
		logger.info("Validate Time From Date: "+compareDateFrom);
		int compareDateTo = dateActiveTo.compareTo(todayDate);
		logger.info("Validate Time From Date: "+compareDateTo);

		if ((compareDateFrom == 0 || compareDateFrom < 0) && ((compareDateTo == 0 || compareDateTo > 0))) {

			String startTime = fraudPrevention.getStartTime();
			String endTime = fraudPrevention.getEndTime();
			logger.info("After start date: "+startTime);
			logger.info("After end date: "+endTime);

			SimpleDateFormat sdfTime = new SimpleDateFormat("HHmmss");
			Date now = new Date();
			String currentTime = sdfTime.format(now);

			int compareStartTime = startTime.compareTo(currentTime);
			logger.info("After Compare start Time: "+compareStartTime);
			int compareEndTime = endTime.compareTo(currentTime);
			logger.info("After Compare end Time: "+compareEndTime);

			if ((compareStartTime == 0 || compareStartTime < 0) && ((compareEndTime == 0 || compareEndTime > 0))) {

				String repeatDays = fraudPrevention.getRepeatDays();
				logger.info("Repeated Days: "+repeatDays);
				Collection<String> daysList = Helper.parseFields(repeatDays);
				logger.info("Days List{}",daysList);
				DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
				logger.info("Day of Week "+ dayOfWeek);
				Weekdays dayInstance = Weekdays.getDayInstance(dayOfWeek.toString());
				logger.info("Day Instance: "+dayInstance);
				String dayCode = dayInstance.getCode();
				logger.info("Day Code "+dayCode);

				for (String day : daysList) {
					if (dayCode.equals(day)) {
						logger.info("Day Code Matched return true: "+dayCode);
						return true;

					}
				}
			}
		}
		logger.info("Return False for day compare");
		return false;
	}
}