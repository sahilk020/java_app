package com.pay10.commons.util;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.BinCardInfo;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.User;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AcquirerTypeService {

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	@Autowired
	private StaticDataProvider staticDataProvider;

	@Autowired
	private PropertiesManager propertiesManager;
	@Autowired
	private TdrSettingDao dao;
	private static Logger logger = LoggerFactory.getLogger(AcquirerTypeService.class.getName());
	// private static Map<String,List<RouterConfiguration>> routerConfigurationMap =
	// new HashMap<String,List<RouterConfiguration>>();

	public AcquirerType getDefault(Fields fields, User user) throws SystemException {
		return getAcquirer(fields, user);
	}

	/*
	 * public AcquirerType getDefaultOld(Fields fields, User user) throws
	 * SystemException { return getAcquirer(fields.getFields(), user); }
	 */

	private AcquirerType getAcquirer(Fields fields, User user) throws SystemException {
		logger.info("Finding acquirer");
		String acquirerName = "";
		PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
		String mopType = fields.get(FieldType.MOP_TYPE.getName());
		String currency = fields.get(FieldType.CURRENCY_CODE.getName());
		String paymentTypeCode = fields.get(FieldType.PAYMENT_TYPE.getName());
		String payId = user.getPayId();
		String transactionType = user.getModeType().toString();

		String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
		String cardHolderType = fields.get(FieldType.CARD_HOLDER_TYPE.getName());

		// change for smart routing logic
		// String slabId = "00";
		String slabId = "00";

		logger.info("Transaction details found for txn id " + fields.get(FieldType.TXN_ID.getName())
				+ "  Looking for payment slab now");
		logger.info("Transaction details = " + paymentType.toString() + " " + mopType + " " + currency + " "
				+ paymentTypeCode + " " + payId + " " + transactionType + " " + paymentsRegion + " " + cardHolderType);

		// code added by sonu for tdr logic (----------- Start -------------)
//		Map<String, String> tdrMap = new HashMap<String, String>();
//		user.getAccounts().forEach(accounts -> {
//			accounts.getTdrSetting().forEach(action -> {
//
//				/*
//				 * logger.info("Id " + action.getId() + " Status : " + action.getStatus() +
//				 * " TDR Status : " + action.getTdrStatus() + " mopType " + action.getMopType()
//				 * + " paymentType : " + action.getPaymentType());
//				 */
//
//				String status = action.getStatus();
//				String tdrStatus = action.getTdrStatus();
//				if (paymentType.toString().equals(action.getPaymentType())
//						&& mopType.equals(MopType.getCodeusingInstance(action.getMopType()))) {
//					if (!tdrStatus.equals(TDRStatus.ACTIVE.toString())) {
//						if (status.equals(TDRStatus.ACTIVE.toString())) {
//							tdrMap.put("flag", "SUCCESS");
//						}
//
//					}
//				}
//
//			});
//		});
//
//		logger.info("tdrMap " + tdrMap.toString());
//
//		if (!tdrMap.isEmpty()) {
//			throw new SystemException(ErrorType.TDR_SETTING_PENDING, ErrorType.TDR_SETTING_PENDING.getResponseCode());
//		}
		// (------------- Complete -------------)

		if (StringUtils.isEmpty(fields.get(FieldType.PAYMENT_TYPE.getName())) || StringUtils.isEmpty(mopType)
				|| StringUtils.isEmpty(currency)) {
			return null;
		}

		String identifier = payId + currency + paymentTypeCode + mopType + transactionType + paymentsRegion
				+ cardHolderType + slabId;
		logger.info("Acqurier identifier = " + identifier);
		switch (paymentType) {

		case CREDIT_CARD:
		case DEBIT_CARD:
		case PREPAID_CARD:

			List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

			rulesList = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
			if (rulesList.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesList.size() > 1) {

				boolean binBased = true;
				String binBankName = fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
				logger.info("GetAcquirer, CardBankName= " + binBankName + " for orderId= "+ fields.get(FieldType.ORDER_ID.getName()));
				List<String> binAcquirerNames = new ArrayList<>();
				rulesList.stream().forEach(rule -> binAcquirerNames.add(rule.getAcquirer()));
				logger.info("binAcquirerNames " + binAcquirerNames);
				BinCardInfo acquirerNameFromDB = routerConfigurationDao.getBankName(binBankName);
				logger.info("acquirerNameFromDB :::: " + acquirerNameFromDB);
				if(null != acquirerNameFromDB) {
					for (int i = 0; i < binAcquirerNames.size(); i++) {
						if (binAcquirerNames.get(i).equalsIgnoreCase(acquirerNameFromDB.getAcquirerName())) {

							double maxAmount = rulesList.get(i).getMaxAmount();
							logger.info("BinBased, Merchant MaxAmount Available Is : " + maxAmount);
							double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
									fields.get(FieldType.CURRENCY_CODE.getName())));
							logger.info("BinBased, Customer Transaction Amount Is : " + txnAmount);

							if (maxAmount >= txnAmount) {
								logger.info("BinBased, updated id " + rulesList.get(i).getId() + " max amount " + (maxAmount - txnAmount));
								acquirerName = AcquirerType.getAcquirerName(rulesList.get(i).getAcquirer());
								fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
										String.valueOf(rulesList.get(i).getId()));
								binBased = false;
								break;
							}
						}
					}
				}
				

				logger.info("binBased = "+binBased+ " , OrderId= "+fields.get(FieldType.ORDER_ID.getName()));
				if (binBased) {

					if (null != rulesList.get(0).getRoutingType()
							&& rulesList.get(0).getRoutingType().equalsIgnoreCase("NormalBase")) {

						logger.info("RuleList For Card Transaction : " + rulesList);
						for (RouterConfiguration routerConfiguration : rulesList) {

							double maxAmount = routerConfiguration.getMaxAmount();
							logger.info("Merchant MaxAmount Available Is : " + maxAmount);
							double txnAmount = Double
									.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
											fields.get(FieldType.CURRENCY_CODE.getName())));
							logger.info("Customer Transaction Amount Is : " + txnAmount);

							boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
									routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
									routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);

							logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ", lastFiveFailedTxnsFalg,
									routerConfiguration.isDown());

							boolean lastFailedTxnCntFlag = false;
							// boolean lastFiveFailedTxnsFalg = true;
							if (lastFiveFailedTxnsFalg) {
								logger.info("################  lastFailedTxnCntFlag (true) #####################");
								lastFailedTxnCntFlag = true;
							} else {
								// TODO Initiated mail to support team for Failed count limit exceed
								logger.info("################  lastFailedTxnCntFlag (false) #####################");
								if (routerConfiguration.isDown()) {
									logger.info("Acquirer Down Condition");
									if (routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
										logger.info("Acquirer Down Condition & Failure Count");
										lastFailedTxnCntFlag = false;
										int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(
												Long.valueOf(routerConfiguration.getId()));
										if (result == 1) {
											// TODO Mail initiated to business team.
											EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
											ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
													routerConfiguration.getPaymentType(),
													routerConfiguration.getMopType());
										}
									} else {
										lastFailedTxnCntFlag = true;
										logger.info("Acquirer Down Condition & Else ");
										routerConfigurationDao.increaseFailedCntForRouterConfiguration(
												Long.valueOf(routerConfiguration.getId()));
									}
								} else {
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById(
											Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								}

							}
							if (maxAmount >= txnAmount && lastFailedTxnCntFlag) {
								logger.info("updated id " + routerConfiguration.getId() + " max amount "
										+ (maxAmount - txnAmount));
								acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
								fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
										String.valueOf(routerConfiguration.getId()));
								break;
							}
						}

						if (StringUtils.isEmpty(acquirerName)) {
							throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED,
									ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
						}

					} else if (null != rulesList.get(0).getRoutingType()
							&& rulesList.get(0).getRoutingType().equalsIgnoreCase("AmountBase")) {

						boolean amountBaseFlag = false;
						logger.info("RuleList For Card Transaction : " + rulesList);
						for (RouterConfiguration routerConfiguration : rulesList) {

							double routerMinAmount = Double.parseDouble(routerConfiguration.getRouterMinAmount());
							double routerMaxAmount = Double.parseDouble(routerConfiguration.getRouterMixAmount());

							double txnAmount = Double
									.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
											fields.get(FieldType.CURRENCY_CODE.getName())));
							logger.info("Customer Transaction Amount Is : " + txnAmount);

							boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
									routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
									routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);

							logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ", lastFiveFailedTxnsFalg,
									routerConfiguration.isDown());

							boolean lastFailedTxnCntFlag = false;
							// boolean lastFiveFailedTxnsFalg = true;
							if (lastFiveFailedTxnsFalg) {
								logger.info("################  lastFailedTxnCntFlag (true) #####################");
								lastFailedTxnCntFlag = true;
							} else {
								// TODO Initiated mail to support team for Failed count limit exceed
								logger.info("################  lastFailedTxnCntFlag (false) #####################");
								if (routerConfiguration.isDown()) {
									logger.info("Acquirer Down Condition");
									if (routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
										logger.info("Acquirer Down Condition & Failure Count");
										lastFailedTxnCntFlag = false;
										int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(
												Long.valueOf(routerConfiguration.getId()));
										if (result == 1) {
											// TODO Mail initiated to business team.
											EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
											ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
													routerConfiguration.getPaymentType(),
													routerConfiguration.getMopType());
										}
									} else {
										lastFailedTxnCntFlag = true;
										logger.info("Acquirer Down Condition & Else ");
										routerConfigurationDao.increaseFailedCntForRouterConfiguration(
												Long.valueOf(routerConfiguration.getId()));
									}
								} else {
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById(
											Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								}

							}

							if (txnAmount >= routerMinAmount && txnAmount <= routerMaxAmount && lastFailedTxnCntFlag) {
								double maxAmount = routerConfiguration.getMaxAmount();
								logger.info("Merchant MaxAmount Available Is : " + maxAmount);

								if (maxAmount >= txnAmount) {
									logger.info("updated id " + routerConfiguration.getId() + " max amount "
											+ (maxAmount - txnAmount));
									acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
									fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
											String.valueOf(routerConfiguration.getId()));
									break;
								}
							} else {
								amountBaseFlag = true;
							}

						}

						if (amountBaseFlag) {
							throw new SystemException(ErrorType.DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE,
									ErrorType.DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE.getResponseCode());
						}
						if (StringUtils.isEmpty(acquirerName)) {
							throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED,
									ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
						}

					} else {

						String cardBankName = fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
						logger.info("cardBankName : " + cardBankName);

						List<String> acquirerNames = new ArrayList<>();
						rulesList.stream().forEach(rule -> acquirerNames.add(rule.getAcquirer()));
						// Find last transaction details:
						logger.info("##@@@@@@@@@@@@@@@@@@@@@@@@@Acquirers Name : " + acquirerNames);
						List<String> documents = routerConfigurationDao.findLastTransaction(acquirerNames,
								paymentTypeCode, mopType, payId);
						logger.info("RuleList For Cards Transaction : " + rulesList);
						logger.info("documents size : " + documents.size());
						if (documents.size() > 0) {
							JSONArray ja = new JSONArray(documents.toString());
							int totalAcquirers = acquirerNames.size();
							logger.info("totalAcquirers : " + totalAcquirers);
							logger.info("ja.getJSONObject(0).getString(\"ACQUIRER_TYPE\") : "
									+ ja.getJSONObject(0).getString("ACQUIRER_TYPE"));
							// logger.info("rulesList.get(i).getAcquirer() :
							// "+rulesList.get(i).getAcquirer());

							String acqName = ja.getJSONObject(0).getString("ACQUIRER_TYPE");
							logger.info("Last Transaction Acquirer Name :  " + acqName);
							logger.info("Smart Routing Acaquirer List :: " + acquirerNames);
							logger.info("Acquirer position in   " + acquirerNames.indexOf(acqName));
							logger.info("Smart Routing Acaquirer :: " + totalAcquirers);

							int LastTxnAcqPosition = acquirerNames.indexOf(acqName);
							LastTxnAcqPosition += 1;
							logger.info("Last Transaction acquirer position : " + LastTxnAcqPosition);

							int currentPosition = 0;
							boolean acqFalg = false;
							for (int j = 0; j < rulesList.size(); j++) {
								boolean txnAmtFlag = false;
								boolean lastFailedTxnCntFlag = false;

								if (totalAcquirers == LastTxnAcqPosition) {
									currentPosition = 0;
									LastTxnAcqPosition = 1;
								} else if (totalAcquirers > LastTxnAcqPosition) {
									currentPosition = LastTxnAcqPosition;
									LastTxnAcqPosition = LastTxnAcqPosition + 1;
								}

								logger.info("Acquirer Postion : " + currentPosition + " values : "
										+ rulesList.get(currentPosition));
								double maxAmount = rulesList.get(currentPosition).getMaxAmount();
								double txnAmount = Double
										.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
												fields.get(FieldType.CURRENCY_CODE.getName())));
								logger.info("Customer Transaction Amount Is : " + txnAmount);
								RouterConfiguration routerConfiguration = rulesList.get(currentPosition);
								// Fetch last five failed transaction based on Acquirer
								logger.info("Last five transaction : " + " ACQUIRE NAME "
										+ routerConfiguration.getAcquirer() + " PAYMENTTYPENAME "
										+ routerConfiguration.getPaymentTypeName() + " MOPTYPE "
										+ routerConfiguration.getMopType());

								if (maxAmount >= txnAmount) {
									logger.info(
											"################  maxAmount >= txnAmount (true) #####################");
									txnAmtFlag = true;
								} else {
									// TODO Initiated mail to support team limit exceed
									logger.info(
											"################  maxAmount >= txnAmount (false) #####################");
									txnAmtFlag = false;
								}

								boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
										routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
										routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);

								logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ", lastFiveFailedTxnsFalg,
										routerConfiguration.isDown());
								// boolean lastFiveFailedTxnsFalg = true;
								if (lastFiveFailedTxnsFalg) {
									logger.info("################  lastFailedTxnCntFlag (true) #####################");
									lastFailedTxnCntFlag = true;
								} else {
									// TODO Initiated mail to support team for Failed count limit exceed
									logger.info("################  lastFailedTxnCntFlag (false) #####################");
									if (routerConfiguration.isDown()) {
										logger.info("Acquirer Down Condition");
										if (routerConfiguration.getFailureCount() >= routerConfiguration
												.getFailedCount()) {
											logger.info("Acquirer Down Condition & Failure Count");
											lastFailedTxnCntFlag = false;
											int result = routerConfigurationDao
													.disableRCForFailedCountExceededById_again(
															Long.valueOf(routerConfiguration.getId()));
											if (result == 1) {
												// TODO Mail initiated to business team.
												EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
												ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
														routerConfiguration.getPaymentType(),
														routerConfiguration.getMopType());
											}
										} else {
											lastFailedTxnCntFlag = true;
											logger.info("Acquirer Down Condition & Else ");
											routerConfigurationDao.increaseFailedCntForRouterConfiguration(
													Long.valueOf(routerConfiguration.getId()));
										}
									} else {
										lastFailedTxnCntFlag = false;
										int result = routerConfigurationDao.disableRCForFailedCountExceededById(
												Long.valueOf(routerConfiguration.getId()));
										if (result == 1) {
											// TODO Mail initiated to business team.
											EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
											ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
													routerConfiguration.getPaymentType(),
													routerConfiguration.getMopType());
										}
									}

								}

								if (txnAmtFlag && lastFailedTxnCntFlag) {
									// if (txnAmtFlag) {
									logger.info("$$ updated id " + rulesList.get(currentPosition).getId()
											+ " max amount " + (maxAmount - txnAmount));
									acquirerName = AcquirerType
											.getAcquirerName(rulesList.get(currentPosition).getAcquirer());
									fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
											String.valueOf(rulesList.get(currentPosition).getId()));
									acqFalg = true;
									break;
								}

							}

							if (!acqFalg) {
								logger.info("############ All smart routing role failed for identifier ############# "
										+ identifier);
							}

						} else {
							acquirerName = AcquirerType.getAcquirerName(rulesList.get(0).getAcquirer());
						}
					}
				} 
			} else {
				for (RouterConfiguration routerConfiguration : rulesList) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
				}
			}

			logger.info("AcquirerName For Cards :: " + acquirerName);
			break;
		
		case QRCODE:

			List<RouterConfiguration> rulesListQR = new ArrayList<RouterConfiguration>();
			rulesListQR = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
			if (rulesListQR.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesListQR.size() > 1) {
				
				if(null != rulesListQR.get(0).getRoutingType() && rulesListQR.get(0).getRoutingType().equalsIgnoreCase("NormalBase")) {
					
					logger.info("RuleList For QRCODE Transaction : " + rulesListQR);
					for (RouterConfiguration routerConfiguration : rulesListQR) {

						double maxAmount = routerConfiguration.getMaxAmount();
						logger.info("Merchant MaxAmount Available Is : " + maxAmount);
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);
						
						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());

						boolean lastFailedTxnCntFlag = false;
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}
						
						if (maxAmount >= txnAmount && lastFailedTxnCntFlag) {
							logger.info("updated id " + routerConfiguration.getId() + " max amount " + (maxAmount - txnAmount));
							acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
							fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(), String.valueOf(routerConfiguration.getId()));
							break;
						}
					}

					if (StringUtils.isEmpty(acquirerName)) {
						throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED, ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
					}
					
				} else if(null != rulesListQR.get(0).getRoutingType() && rulesListQR.get(0).getRoutingType().equalsIgnoreCase("AmountBase")) {
					
					boolean amountBaseFlag = false;
					logger.info("RuleList For QRCODE Transaction : " + rulesListQR);
					for (RouterConfiguration routerConfiguration : rulesListQR) {

						double routerMinAmount = Double.parseDouble(routerConfiguration.getRouterMinAmount());
						double routerMaxAmount = Double.parseDouble(routerConfiguration.getRouterMixAmount());
						
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);
						
						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());

						boolean lastFailedTxnCntFlag = false;
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}
						
						if(txnAmount >= routerMinAmount && txnAmount <= routerMaxAmount && lastFailedTxnCntFlag) {
							double maxAmount = routerConfiguration.getMaxAmount();
							logger.info("Merchant MaxAmount Available Is : " + maxAmount);
							
							
							if (maxAmount >= txnAmount) {
								logger.info("updated id " + routerConfiguration.getId() + " max amount " + (maxAmount - txnAmount));
								acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
								fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(), String.valueOf(routerConfiguration.getId()));
								break;
							}
						}else {
							amountBaseFlag = true;
						}
						
					}

					if(amountBaseFlag) {
						throw new SystemException(ErrorType.DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE, ErrorType.DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE.getResponseCode());
					}
					if (StringUtils.isEmpty(acquirerName)) {
						throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED, ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
					}
					
					
				} else {
					
				List<String> acquirerNames = new ArrayList<>();
				rulesListQR.stream().forEach(rule -> acquirerNames.add(rule.getAcquirer()));
				List<String> documents = routerConfigurationDao.findLastTransaction(acquirerNames, paymentTypeCode,
						mopType, payId);
				if (documents.size() > 0) {
					JSONArray ja = new JSONArray(documents.toString());
					int totalAcquirers = acquirerNames.size();
					String acqName = ja.getJSONObject(0).getString("ACQUIRER_TYPE");
					int LastTxnAcqPosition = acquirerNames.indexOf(acqName);
					LastTxnAcqPosition += 1;
					int currentPosition = 0;
					boolean acqFalg = false;
					for (int j = 0; j < rulesListQR.size(); j++) {
						boolean txnAmtFlag = false;
						boolean lastFailedTxnCntFlag = false;
						if (totalAcquirers == LastTxnAcqPosition) {
							currentPosition = 0;
							LastTxnAcqPosition = 1;
						} else if (totalAcquirers > LastTxnAcqPosition) {
							currentPosition = LastTxnAcqPosition;
							LastTxnAcqPosition = LastTxnAcqPosition + 1;
						}

						logger.info("Acquirer Postion : " + currentPosition + " values : "
								+ rulesListQR.get(currentPosition));
						double maxAmount = rulesListQR.get(currentPosition).getMaxAmount();
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						RouterConfiguration routerConfiguration = rulesListQR.get(currentPosition);
						// Fetch last five failed transaction based on Acquirer
						logger.info("Last five transaction : " + " ACQUIRE NAME " + routerConfiguration.getAcquirer()
								+ " PAYMENTTYPENAME " + routerConfiguration.getPaymentTypeName() + " MOPTYPE "
								+ routerConfiguration.getMopType());

						if (maxAmount >= txnAmount) {
							logger.info("################  maxAmount >= txnAmount (true) #####################");
							txnAmtFlag = true;
						} else {
							// TODO Initiated mail to support team limit exceed
							logger.info("################  maxAmount >= txnAmount (false) #####################");
							txnAmtFlag = false;
						}

						/*
						 * boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
						 * routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
						 * routerConfiguration.getMopType(),
						 * routerConfiguration.getFailedCount(),payId);
						 * 
						 * //boolean lastFiveFailedTxnsFalg = true; if (lastFiveFailedTxnsFalg) {
						 * logger.
						 * info("################  lastFailedTxnCntFlag (true) #####################");
						 * lastFailedTxnCntFlag = true; } else { // TODO Initiated mail to support team
						 * for Failed count limit exceed logger.
						 * info("################  lastFailedTxnCntFlag (false) #####################");
						 * lastFailedTxnCntFlag = false; int result =
						 * routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(
						 * routerConfiguration.getId())); if(result == 1) { //TODO Mail initiated to
						 * business team. EmailControllerServiceProvider ecsp = new
						 * EmailControllerServiceProvider();
						 * ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
						 * routerConfiguration.getPaymentType(), routerConfiguration.getMopType()); } }
						 */

						//if (txnAmtFlag && lastFailedTxnCntFlag) {
						/*
						 * if (txnAmtFlag) { logger.info("$$ updated id " +
						 * rulesListQR.get(currentPosition).getId() + " max amount " + (maxAmount -
						 * txnAmount)); acquirerName =
						 * AcquirerType.getAcquirerName(rulesListQR.get(currentPosition).getAcquirer());
						 * fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
						 * String.valueOf(rulesListQR.get(currentPosition).getId())); acqFalg = true;
						 * break; }
						 */
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);

						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}

						if (txnAmtFlag && lastFailedTxnCntFlag) {
						//if (txnAmtFlag) {
							logger.info("$$ updated id " + rulesListQR.get(currentPosition).getId() + " max amount "
									+ (maxAmount - txnAmount));
							acquirerName = AcquirerType
									.getAcquirerName(rulesListQR.get(currentPosition).getAcquirer());
							fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
									String.valueOf(rulesListQR.get(currentPosition).getId()));
							acqFalg = true;
							break;
						}

					}

					if (!acqFalg) {
						logger.info("############ All smart routing role failed for identifier ############# "
								+ identifier);
					}

				} else {
					acquirerName = AcquirerType.getAcquirerName(rulesListQR.get(0).getAcquirer());
				}}
			} else {
				for (RouterConfiguration routerConfiguration : rulesListQR) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
				}
			}

			logger.info("AcquirerName For R :: " + acquirerName);

			break;
		case NET_BANKING:

			List<RouterConfiguration> rulesListNB = new ArrayList<RouterConfiguration>();
			rulesListNB = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
			if (rulesListNB.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesListNB.size() > 1) {
				
				if(null != rulesListNB.get(0).getRoutingType() && rulesListNB.get(0).getRoutingType().equalsIgnoreCase("NormalBase")) {
					
					logger.info("RuleList For NB Transaction : " + rulesListNB);
					for (RouterConfiguration routerConfiguration : rulesListNB) {

						double maxAmount = routerConfiguration.getMaxAmount();
						logger.info("Merchant MaxAmount Available Is : " + maxAmount);
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);
						
						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());

						boolean lastFailedTxnCntFlag = false;
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}
						
						if (maxAmount >= txnAmount && lastFailedTxnCntFlag) {
							logger.info("updated id " + routerConfiguration.getId() + " max amount " + (maxAmount - txnAmount));
							acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
							fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(), String.valueOf(routerConfiguration.getId()));
							break;
						}
					}

					if (StringUtils.isEmpty(acquirerName)) {
						throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED, ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
					}
					
				} else if(null != rulesListNB.get(0).getRoutingType() && rulesListNB.get(0).getRoutingType().equalsIgnoreCase("AmountBase")) {
					
					boolean amountBaseFlag = false;
					logger.info("RuleList For NB Transaction : " + rulesListNB);
					for (RouterConfiguration routerConfiguration : rulesListNB) {

						double routerMinAmount = Double.parseDouble(routerConfiguration.getRouterMinAmount());
						double routerMaxAmount = Double.parseDouble(routerConfiguration.getRouterMixAmount());
						
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);
						
						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());

						boolean lastFailedTxnCntFlag = false;
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}
						
						if(txnAmount >= routerMinAmount && txnAmount <= routerMaxAmount && lastFailedTxnCntFlag) {
							double maxAmount = routerConfiguration.getMaxAmount();
							logger.info("Merchant MaxAmount Available Is : " + maxAmount);
							
							
							if (maxAmount >= txnAmount) {
								logger.info("updated id " + routerConfiguration.getId() + " max amount " + (maxAmount - txnAmount));
								acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
								fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(), String.valueOf(routerConfiguration.getId()));
								break;
							}
						}else {
							amountBaseFlag = true;
						}
						
					}

					if(amountBaseFlag) {
						throw new SystemException(ErrorType.DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE, ErrorType.DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE.getResponseCode());
					}
					if (StringUtils.isEmpty(acquirerName)) {
						throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED, ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
					}
					
					
				} else {
				List<String> acquirerNames = new ArrayList<>();
				rulesListNB.stream().forEach(rule -> acquirerNames.add(rule.getAcquirer()));
				logger.info("Smart Routing Acquirer List for NetBanking " + acquirerNames);
				List<String> documents = routerConfigurationDao.findLastTransaction(acquirerNames, paymentTypeCode,
						mopType, payId);

				if (documents.size() > 0) {
					JSONArray ja = new JSONArray(documents.toString());
					int totalAcquirers = acquirerNames.size();
					String acqName = ja.getJSONObject(0).getString("ACQUIRER_TYPE");
					logger.info("Last Acquirer Txn : " + acquirerNames);
					int LastTxnAcqPosition = acquirerNames.indexOf(acqName);
					LastTxnAcqPosition += 1;
					int currentPosition = 0;
					boolean acqFalg = false;
					for (int j = 0; j < rulesListNB.size(); j++) {
						boolean txnAmtFlag = false;
						boolean lastFailedTxnCntFlag = false;
						if (totalAcquirers == LastTxnAcqPosition) {
							currentPosition = 0;
							LastTxnAcqPosition = 1;
						} else if (totalAcquirers > LastTxnAcqPosition) {
							currentPosition = LastTxnAcqPosition;
							LastTxnAcqPosition = LastTxnAcqPosition + 1;
						}

						logger.info("Acquirer Postion : " + currentPosition + " values : "
								+ rulesListNB.get(currentPosition));
						double maxAmount = rulesListNB.get(currentPosition).getMaxAmount();
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						RouterConfiguration routerConfiguration = rulesListNB.get(currentPosition);
						// Fetch last five failed transaction based on Acquirer
						logger.info("routerConfiguration : " + routerConfiguration.toString());
						logger.info("Last five transaction : " + " ACQUIRE NAME " + routerConfiguration.getAcquirer()
								+ " PAYMENTTYPENAME " + routerConfiguration.getPaymentTypeName() + " MOPTYPE "
								+ routerConfiguration.getMopType());

						if (maxAmount >= txnAmount) {
							logger.info("################  maxAmount >= txnAmount (true) #####################");
							txnAmtFlag = true;
						} else {
							// TODO Initiated mail to support team limit exceed
							logger.info("################  maxAmount >= txnAmount (false) #####################");
							txnAmtFlag = false;
						}

						/*
						 * List<String> lastfivefailedtxns = routerConfigurationDao.lastFiveTransaction(
						 * routerConfiguration.getAcquirer(), routerConfiguration.getPaymentTypeName(),
						 * routerConfiguration.getMopType(), routerConfiguration.getFailureCount());
						 */

						/*
						 * boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
						 * routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
						 * routerConfiguration.getMopType(),
						 * routerConfiguration.getFailedCount(),payId);
						 * 
						 * logger.info("lastFiveFailedTxnsFalg : "+lastFiveFailedTxnsFalg); if
						 * (lastFiveFailedTxnsFalg) { logger.
						 * info("################  lastFailedTxnCntFlag (true) #####################");
						 * lastFailedTxnCntFlag = true; } else { // TODO Initiated mail to support team
						 * for Failed count limit exceed logger.
						 * info("################  lastFailedTxnCntFlag (false) #####################");
						 * lastFailedTxnCntFlag = false; int result =
						 * routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(
						 * routerConfiguration.getId())); if(result == 1) { //TODO Mail initiated to
						 * business team. EmailControllerServiceProvider ecsp = new
						 * EmailControllerServiceProvider();
						 * ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
						 * routerConfiguration.getPaymentType(), routerConfiguration.getMopType()); } }
						 */

						//if (txnAmtFlag && lastFailedTxnCntFlag) {
						/*
						 * if (txnAmtFlag) { logger.info("$$ updated id " +
						 * rulesListNB.get(currentPosition).getId() + " max amount " + (maxAmount -
						 * txnAmount)); acquirerName =
						 * AcquirerType.getAcquirerName(rulesListNB.get(currentPosition).getAcquirer());
						 * fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
						 * String.valueOf(rulesListNB.get(currentPosition).getId())); acqFalg = true;
						 * break; }
						 */
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);

						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}

						if (txnAmtFlag && lastFailedTxnCntFlag) {
						//if (txnAmtFlag) {
							logger.info("$$ updated id " + rulesListNB.get(currentPosition).getId() + " max amount "
									+ (maxAmount - txnAmount));
							acquirerName = AcquirerType
									.getAcquirerName(rulesListNB.get(currentPosition).getAcquirer());
							fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
									String.valueOf(rulesListNB.get(currentPosition).getId()));
							acqFalg = true;
							break;
						}

					}

					if (!acqFalg) {
						logger.info("############ All smart routing role failed for identifier ############# "
								+ identifier);
					}

					logger.info("%%%%%%%%%%% acquirerName %%%%%%%%%% : " + acquirerName);

				} else {
					acquirerName = AcquirerType.getAcquirerName(rulesListNB.get(0).getAcquirer());
				} }
			} else {
				for (RouterConfiguration routerConfiguration : rulesListNB) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
				}
			}

			logger.info("AcquirerName For NetBanking :: " + acquirerName);
			break;

		/*
		 * List<RouterConfiguration> rulesListNB = new ArrayList<RouterConfiguration>();
		 * 
		 * rulesListNB =
		 * routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
		 * 
		 * if (rulesListNB.size() == 0) {
		 * logger.info("No acquirer found for identifier = " + identifier + " Order id "
		 * + fields.get(FieldType.ORDER_ID.getName())); } else if (rulesListNB.size() >
		 * 1) {
		 * 
		 * logger.info("RuleList For NetBanking Transaction : " + rulesListNB);
		 * 
		 * for (RouterConfiguration routerConfiguration : rulesListNB) {
		 * 
		 * double maxAmount = routerConfiguration.getMaxAmount();
		 * logger.info("Merchant MaxAmount Available Is : " + maxAmount); double
		 * txnAmount =
		 * Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
		 * fields.get(FieldType.CURRENCY_CODE.getName())));
		 * logger.info("Customer Transaction Amount Is : " + txnAmount); if (maxAmount
		 * >= txnAmount) { logger.info("updated id " + routerConfiguration.getId() +
		 * " max amount " + (maxAmount - txnAmount));
		 * 
		 * //routerConfiguration.setMaxAmount(maxAmount - txnAmount);
		 * //routerConfigurationDao.freezeRouterConfigurationMaxAmountById(
		 * routerConfiguration, routerConfiguration.getId()); acquirerName =
		 * AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
		 * fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
		 * String.valueOf(routerConfiguration.getId())); break; } }
		 * 
		 * if (StringUtils.isEmpty(acquirerName)) { // TODO mail initiate to business
		 * team (msg - Merchant Limit Exceeded) throw new
		 * SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED,
		 * ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode()); } } else {
		 * 
		 * for (RouterConfiguration routerConfiguration : rulesListNB) { acquirerName =
		 * AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
		 * 
		 * } } break;
		 */

		case WALLET:

			List<RouterConfiguration> rulesListWL = new ArrayList<RouterConfiguration>();
			rulesListWL = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
			if (rulesListWL.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesListWL.size() > 1) {
				
				if(null != rulesListWL.get(0).getRoutingType() && rulesListWL.get(0).getRoutingType().equalsIgnoreCase("NormalBase")) {
					
					logger.info("RuleList For Wallet Transaction : " + rulesListWL);
					for (RouterConfiguration routerConfiguration : rulesListWL) {

						double maxAmount = routerConfiguration.getMaxAmount();
						logger.info("Merchant MaxAmount Available Is : " + maxAmount);
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);
						
						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());

						boolean lastFailedTxnCntFlag = false;
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}
						
						if (maxAmount >= txnAmount && lastFailedTxnCntFlag) {
							logger.info("updated id " + routerConfiguration.getId() + " max amount " + (maxAmount - txnAmount));
							acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
							fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(), String.valueOf(routerConfiguration.getId()));
							break;
						}
					}

					if (StringUtils.isEmpty(acquirerName)) {
						throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED, ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
					}
					
				} else if(null != rulesListWL.get(0).getRoutingType() && rulesListWL.get(0).getRoutingType().equalsIgnoreCase("AmountBase")) {
					
					boolean amountBaseFlag = false;
					logger.info("RuleList For Wallet Transaction : " + rulesListWL);
					for (RouterConfiguration routerConfiguration : rulesListWL) {

						double routerMinAmount = Double.parseDouble(routerConfiguration.getRouterMinAmount());
						double routerMaxAmount = Double.parseDouble(routerConfiguration.getRouterMixAmount());
						
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);
						
						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());

						boolean lastFailedTxnCntFlag = false;
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}
						
						if(txnAmount >= routerMinAmount && txnAmount <= routerMaxAmount && lastFailedTxnCntFlag) {
							double maxAmount = routerConfiguration.getMaxAmount();
							logger.info("Merchant MaxAmount Available Is : " + maxAmount);
							
							
							if (maxAmount >= txnAmount) {
								logger.info("updated id " + routerConfiguration.getId() + " max amount " + (maxAmount - txnAmount));
								acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
								fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(), String.valueOf(routerConfiguration.getId()));
								break;
							}
						}else {
							amountBaseFlag = true;
						}
						
					}

					if(amountBaseFlag) {
						throw new SystemException(ErrorType.DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE, ErrorType.DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE.getResponseCode());
					}
					if (StringUtils.isEmpty(acquirerName)) {
						throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED, ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
					}
					
					
				} else {
				List<String> acquirerNames = new ArrayList<>();
				rulesListWL.stream().forEach(rule -> acquirerNames.add(rule.getAcquirer()));
				List<String> documents = routerConfigurationDao.findLastTransaction(acquirerNames, paymentTypeCode,
						mopType, payId);
				if (documents.size() > 0) {
					JSONArray ja = new JSONArray(documents.toString());
					int totalAcquirers = acquirerNames.size();
					String acqName = ja.getJSONObject(0).getString("ACQUIRER_TYPE");
					int LastTxnAcqPosition = acquirerNames.indexOf(acqName);
					LastTxnAcqPosition += 1;
					int currentPosition = 0;
					boolean acqFalg = false;
					for (int j = 0; j < rulesListWL.size(); j++) {
						boolean txnAmtFlag = false;
						boolean lastFailedTxnCntFlag = false;
						if (totalAcquirers == LastTxnAcqPosition) {
							currentPosition = 0;
							LastTxnAcqPosition = 1;
						} else if (totalAcquirers > LastTxnAcqPosition) {
							currentPosition = LastTxnAcqPosition;
							LastTxnAcqPosition = LastTxnAcqPosition + 1;
						}

						logger.info("Acquirer Postion : " + currentPosition + " values : "
								+ rulesListWL.get(currentPosition));
						double maxAmount = rulesListWL.get(currentPosition).getMaxAmount();
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						RouterConfiguration routerConfiguration = rulesListWL.get(currentPosition);
						// Fetch last five failed transaction based on Acquirer
						logger.info("Last five transaction : " + " ACQUIRE NAME " + routerConfiguration.getAcquirer()
								+ " PAYMENTTYPENAME " + routerConfiguration.getPaymentTypeName() + " MOPTYPE "
								+ routerConfiguration.getMopType());

						if (maxAmount >= txnAmount) {
							logger.info("################  maxAmount >= txnAmount (true) #####################");
							txnAmtFlag = true;
						} else {
							// TODO Initiated mail to support team limit exceed
							logger.info("################  maxAmount >= txnAmount (false) #####################");
							txnAmtFlag = false;
						}

						/*
						 * boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
						 * routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
						 * routerConfiguration.getMopType(),
						 * routerConfiguration.getFailedCount(),payId);
						 * 
						 * //boolean lastFiveFailedTxnsFalg = true; if (lastFiveFailedTxnsFalg) {
						 * logger.
						 * info("################  lastFailedTxnCntFlag (true) #####################");
						 * lastFailedTxnCntFlag = true; } else { // TODO Initiated mail to support team
						 * for Failed count limit exceed logger.
						 * info("################  lastFailedTxnCntFlag (false) #####################");
						 * lastFailedTxnCntFlag = false; int result =
						 * routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(
						 * routerConfiguration.getId())); if(result == 1) { //TODO Mail initiated to
						 * business team. EmailControllerServiceProvider ecsp = new
						 * EmailControllerServiceProvider();
						 * ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
						 * routerConfiguration.getPaymentType(), routerConfiguration.getMopType()); } }
						 */

						//if (txnAmtFlag && lastFailedTxnCntFlag) {
						/*
						 * if (txnAmtFlag) { logger.info("$$ updated id " +
						 * rulesListWL.get(currentPosition).getId() + " max amount " + (maxAmount -
						 * txnAmount)); acquirerName =
						 * AcquirerType.getAcquirerName(rulesListWL.get(currentPosition).getAcquirer());
						 * fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
						 * String.valueOf(rulesListWL.get(currentPosition).getId())); acqFalg = true;
						 * break; }
						 */
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);

						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}

						if (txnAmtFlag && lastFailedTxnCntFlag) {
						//if (txnAmtFlag) {
							logger.info("$$ updated id " + rulesListWL.get(currentPosition).getId() + " max amount "
									+ (maxAmount - txnAmount));
							acquirerName = AcquirerType
									.getAcquirerName(rulesListWL.get(currentPosition).getAcquirer());
							fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
									String.valueOf(rulesListWL.get(currentPosition).getId()));
							acqFalg = true;
							break;
						}

					}

					if (!acqFalg) {
						logger.info("############ All smart routing role failed for identifier ############# "
								+ identifier);
					}

					logger.info("%%%%%%%%%%% acquirerName %%%%%%%%%% : " + acquirerName);

				} else {
					acquirerName = AcquirerType.getAcquirerName(rulesListWL.get(0).getAcquirer());
				}}
			} else {
				for (RouterConfiguration routerConfiguration : rulesListWL) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
				}
			}

			logger.info("AcquirerName For Wallet :: " + acquirerName);
			break;

		/*
		 * List<RouterConfiguration> rulesListWL = new ArrayList<RouterConfiguration>();
		 * 
		 * rulesListWL =
		 * routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
		 * 
		 * if (rulesListWL.size() == 0) {
		 * logger.info("No acquirer found for identifier = " + identifier +
		 * " Order id "+ fields.get(FieldType.ORDER_ID.getName())); } else if
		 * (rulesListWL.size() > 1) {
		 * 
		 * logger.info("RuleList For Wallet Transaction : " + rulesListWL);
		 * 
		 * for (RouterConfiguration routerConfiguration : rulesListWL) {
		 * 
		 * double maxAmount = routerConfiguration.getMaxAmount();
		 * logger.info("Merchant MaxAmount Available Is : " + maxAmount); double
		 * txnAmount =
		 * Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
		 * fields.get(FieldType.CURRENCY_CODE.getName())));
		 * logger.info("Customer Transaction Amount Is : " + txnAmount); if (maxAmount
		 * >= txnAmount) { logger.info("updated id " + routerConfiguration.getId() +
		 * " max amount " + (maxAmount - txnAmount));
		 * 
		 * //routerConfiguration.setMaxAmount(maxAmount - txnAmount);
		 * //routerConfigurationDao.freezeRouterConfigurationMaxAmountById(
		 * routerConfiguration, routerConfiguration.getId()); acquirerName =
		 * AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
		 * fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
		 * String.valueOf(routerConfiguration.getId())); break; } } } else {
		 * 
		 * for (RouterConfiguration routerConfiguration : rulesListWL) { acquirerName =
		 * AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
		 * 
		 * } } break;
		 */
//		case EMI:
//			acquirerName = AcquirerType.CITRUS_PAY.getName();
//			break;
//		case RECURRING_PAYMENT:
//			acquirerName = AcquirerType.CITRUS_PAY.getName();
//			break;
		case UPI:

			List<RouterConfiguration> rulesListUpi = new ArrayList<RouterConfiguration>();
			rulesListUpi = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
			if (rulesListUpi.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesListUpi.size() > 1) {
				if(null != rulesListUpi.get(0).getRoutingType() && rulesListUpi.get(0).getRoutingType().equalsIgnoreCase("NormalBase")) {
					
					logger.info("RuleList For UPI Transaction : " + rulesListUpi);
					for (RouterConfiguration routerConfiguration : rulesListUpi) {

						double maxAmount = routerConfiguration.getMaxAmount();
						logger.info("Merchant MaxAmount Available Is : " + maxAmount);
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);
						
						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());

						boolean lastFailedTxnCntFlag = false;
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}
						
						if (maxAmount >= txnAmount && lastFailedTxnCntFlag) {
							logger.info("updated id " + routerConfiguration.getId() + " max amount " + (maxAmount - txnAmount));
							acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
							fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(), String.valueOf(routerConfiguration.getId()));
							break;
						}
					}

					if (StringUtils.isEmpty(acquirerName)) {
						throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED, ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
					}
					
				} else if(null != rulesListUpi.get(0).getRoutingType() && rulesListUpi.get(0).getRoutingType().equalsIgnoreCase("AmountBase")) {
					
					boolean amountBaseFlag = false;
					logger.info("RuleList For UPI Transaction : " + rulesListUpi);
					for (RouterConfiguration routerConfiguration : rulesListUpi) {

						double routerMinAmount = Double.parseDouble(routerConfiguration.getRouterMinAmount());
						double routerMaxAmount = Double.parseDouble(routerConfiguration.getRouterMixAmount());
						
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);
						
						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());

						boolean lastFailedTxnCntFlag = false;
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}
						
						if(txnAmount >= routerMinAmount && txnAmount <= routerMaxAmount && lastFailedTxnCntFlag) {
							double maxAmount = routerConfiguration.getMaxAmount();
							logger.info("Merchant MaxAmount Available Is : " + maxAmount);
							
							
							if (maxAmount >= txnAmount) {
								logger.info("updated id " + routerConfiguration.getId() + " max amount " + (maxAmount - txnAmount));
								acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
								fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(), String.valueOf(routerConfiguration.getId()));
								break;
							}
						}else {
							amountBaseFlag = true;
						}
						
					}

					if(amountBaseFlag) {
						throw new SystemException(ErrorType.DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE, ErrorType.DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE.getResponseCode());
					}
					if (StringUtils.isEmpty(acquirerName)) {
						throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED, ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
					}
					
					
				} else {
				List<String> acquirerNames = new ArrayList<>();
				rulesListUpi.stream().forEach(rule -> acquirerNames.add(rule.getAcquirer()));
				List<String> documents = routerConfigurationDao.findLastTransaction(acquirerNames, paymentTypeCode,
						mopType, payId);
				if (documents.size() > 0) {
					JSONArray ja = new JSONArray(documents.toString());
					int totalAcquirers = acquirerNames.size();
					String acqName = ja.getJSONObject(0).getString("ACQUIRER_TYPE");
					int LastTxnAcqPosition = acquirerNames.indexOf(acqName);
					LastTxnAcqPosition += 1;
					int currentPosition = 0;
					boolean acqFalg = false;
					for (int j = 0; j < rulesListUpi.size(); j++) {
						boolean txnAmtFlag = false;
						boolean lastFailedTxnCntFlag = false;
						if (totalAcquirers == LastTxnAcqPosition) {
							currentPosition = 0;
							LastTxnAcqPosition = 1;
						} else if (totalAcquirers > LastTxnAcqPosition) {
							currentPosition = LastTxnAcqPosition;
							LastTxnAcqPosition = LastTxnAcqPosition + 1;
						}

						logger.info("Acquirer Postion : " + currentPosition + " values : "
								+ rulesListUpi.get(currentPosition));
						double maxAmount = rulesListUpi.get(currentPosition).getMaxAmount();
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);
						RouterConfiguration routerConfiguration = rulesListUpi.get(currentPosition);
						// Fetch last five failed transaction based on Acquirer
						logger.info("Last five transaction : " + " ACQUIRE NAME " + routerConfiguration.getAcquirer()
								+ " PAYMENTTYPENAME " + routerConfiguration.getPaymentTypeName() + " MOPTYPE "
								+ routerConfiguration.getMopType());

						if (maxAmount >= txnAmount) {
							logger.info("################  maxAmount >= txnAmount (true) #####################");
							txnAmtFlag = true;
						} else {
							// TODO Initiated mail to support team limit exceed
							logger.info("################  maxAmount >= txnAmount (false) #####################");
							txnAmtFlag = false;
						}

						
						boolean lastFiveFailedTxnsFalg = routerConfigurationDao.lastFiveTransaction(
								routerConfiguration.getAcquirer(), routerConfiguration.getPaymentType(),
								routerConfiguration.getMopType(), routerConfiguration.getFailedCount(), payId);

						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ",lastFiveFailedTxnsFalg,routerConfiguration.isDown());
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if(routerConfiguration.isDown()) {
								logger.info("Acquirer Down Condition");
								if(routerConfiguration.getFailureCount() >= routerConfiguration.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationDao.disableRCForFailedCountExceededById_again(Long.valueOf(routerConfiguration.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
												routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationDao.increaseFailedCntForRouterConfiguration(Long.valueOf(routerConfiguration.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationDao.disableRCForFailedCountExceededById(Long.valueOf(routerConfiguration.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfiguration.getAcquirer(),
											routerConfiguration.getPaymentType(), routerConfiguration.getMopType());
								}
							}
							
						}

						if (txnAmtFlag && lastFailedTxnCntFlag) {
						//if (txnAmtFlag) {
							logger.info("$$ updated id " + rulesListUpi.get(currentPosition).getId() + " max amount "
									+ (maxAmount - txnAmount));
							acquirerName = AcquirerType
									.getAcquirerName(rulesListUpi.get(currentPosition).getAcquirer());
							fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
									String.valueOf(rulesListUpi.get(currentPosition).getId()));
							acqFalg = true;
							break;
						}

					}

					if (!acqFalg) {
						logger.info("############ All smart routing role failed for identifier ############# "
								+ identifier);
					}

				} else {
					acquirerName = AcquirerType.getAcquirerName(rulesListUpi.get(0).getAcquirer());
					fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(), String.valueOf(rulesListUpi.get(0).getId()));
				}}
			} else {
				for (RouterConfiguration routerConfiguration : rulesListUpi) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
					fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),	String.valueOf(routerConfiguration.getId()));
				}
			}

			logger.info("AcquirerName For UPI :: " + acquirerName);

			break;

		/*
		 * List<RouterConfiguration> rulesListUpi = new
		 * ArrayList<RouterConfiguration>();
		 * 
		 * rulesListUpi =
		 * routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
		 * 
		 * if (rulesListUpi.size() == 0) {
		 * logger.info("No acquirer found for identifier = " + identifier + " Order id "
		 * + fields.get(FieldType.ORDER_ID.getName())); } else if (rulesListUpi.size() >
		 * 1) {
		 * 
		 * logger.info("RuleList For UPI Transaction : " + rulesListUpi); for
		 * (RouterConfiguration routerConfiguration : rulesListUpi) {
		 * 
		 * double maxAmount = routerConfiguration.getMaxAmount();
		 * logger.info("Merchant MaxAmount Available Is : " + maxAmount); double
		 * txnAmount =
		 * Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
		 * fields.get(FieldType.CURRENCY_CODE.getName())));
		 * logger.info("Customer Transaction Amount Is : " + txnAmount);
		 * 
		 * if (maxAmount >= txnAmount) { logger.info( "updated id " +
		 * routerConfiguration.getId() + " max amount " + (maxAmount - txnAmount));
		 * 
		 * //routerConfiguration.setMaxAmount(maxAmount - txnAmount);
		 * //routerConfigurationDao.freezeRouterConfigurationMaxAmountById(
		 * routerConfiguration, routerConfiguration.getId()); acquirerName =
		 * AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
		 * fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
		 * String.valueOf(routerConfiguration.getId())); break; } } } else {
		 * 
		 * for (RouterConfiguration routerConfiguration : rulesListUpi) { acquirerName =
		 * AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
		 * 
		 * } }
		 * 
		 * break;
		 */

//		case AD:
//			RouterRuleDao ruleDaoAtl = new RouterRuleDao();
//			RouterRule atlRules = ruleDaoAtl.findRuleByFieldsByPayId(payId, paymentTypeCode, mopType, currency,
//					transactionType);
//			if (atlRules == null) {
//				String allPayId = "ALL MERCHANTS";
//				atlRules = ruleDaoAtl.findRuleByFieldsByPayId(allPayId, paymentTypeCode, mopType, currency,
//						transactionType);
//			}
//			if (atlRules == null) {
//				throw new SystemException(ErrorType.ROUTER_RULE_NOT_FOUND, ErrorType.ROUTER_RULE_NOT_FOUND.getResponseCode());
//			}
//			String atlAcquirerList = atlRules.getAcquirerMap();
//			Collection<String> atlAcqList = Helper.parseFields(atlAcquirerList);
//			Map<String, String> atlAcquirerMap = new LinkedHashMap<String, String>();
//			for (String atlAcquirer : atlAcqList) {
//				String[] acquirerPreference = atlAcquirer.split("-");
//				atlAcquirerMap.put(acquirerPreference[0], acquirerPreference[1]);
//			}
//
//			String atlPrimaryAcquirer = atlAcquirerMap.get("1");
//			// String primaryAcquirer ="FIRSTDATA";
//			acquirerName = AcquirerType.getInstancefromCode(atlPrimaryAcquirer).getName();
//			break;
		case EMI:
			logger.info("EMI INVOKED. identifier = {}", identifier);
			List<RouterConfiguration> rulesListEmi = routerConfigurationDao.
					findActiveAcquirersByIdentifier(identifier);
			if (rulesListEmi.isEmpty()) {
				logger.info("No acquirer found for identifier = {}, Order id ={}", identifier,
						fields.get(FieldType.ORDER_ID.getName()));
			} else {
				for (RouterConfiguration routerConfiguration : rulesListEmi) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
					fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
							String.valueOf(routerConfiguration.getId()));
				}
			}

			logger.info("AcquirerName For EMI :: {}", acquirerName);

			break;
		default:
			break;
		}
		if (StringUtils.isEmpty(acquirerName)) {
			throw new SystemException(ErrorType.ACQUIRER_NOT_FOUND, ErrorType.ACQUIRER_NOT_FOUND.getResponseCode());
		}
		fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.getInstancefromName(acquirerName).getCode());
		logger.info("Acquirer for identifier = " + identifier + " Order id " + fields.get(FieldType.ORDER_ID.getName())
				+ " Pay id " + fields.get(FieldType.PAY_ID.getName()) + " Acquirer = " + acquirerName);

		String amount = String.valueOf((Double.valueOf(fields.get(FieldType.AMOUNT.getName())) / 100));
		Date date = new Date();
		logger.info("Before Transaction Check TDR : payId : " + payId + "\t PaymentType : "
				+ PaymentType.getInstanceUsingCode(paymentTypeCode) + "\t acquirerName : " + acquirerName
				+ "\t MopType: " + MopType.getInstanceUsingCode(mopType) + "Transaction Type : SALE" + "\t currency :"
				+ currency + "\t amount : " + amount + "\t Date : "
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "\t paymentsRegion : " + paymentsRegion
				+ "\t cardHolderType : " + cardHolderType);
		TdrSetting setting = dao.getTdrAndSurcharge(payId, PaymentType.getInstanceUsingCode(paymentTypeCode).toString(),
				acquirerName, MopType.getInstanceUsingCode(mopType).toString(), "SALE", currency, amount,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date), paymentsRegion, cardHolderType);
		if (setting == null) {
			throw new SystemException(ErrorType.TDR_NOT_SET_FOR_THIS_AMOUNT,
					ErrorType.TDR_NOT_SET_FOR_THIS_AMOUNT.getResponseCode());
		}
		return AcquirerType.getInstancefromName(acquirerName);
	}

	private AcquirerType getAcquirerOld(Map<String, String> fields, User user) throws SystemException {
		logger.info("Finding acquirer");
		String acquirerName = "";
		PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
		String mopType = fields.get(FieldType.MOP_TYPE.getName());
		String currency = fields.get(FieldType.CURRENCY_CODE.getName());
		String paymentTypeCode = fields.get(FieldType.PAYMENT_TYPE.getName());
		String payId = user.getPayId();
		String transactionType = user.getModeType().toString();

		String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
		String cardHolderType = fields.get(FieldType.CARD_HOLDER_TYPE.getName());

		String slabId = "";

		boolean paymentTypeFound = false;
		logger.info("Transaction details found for txn id " + fields.get(FieldType.TXN_ID.getName())
				+ "  Looking for payment slab now");
		logger.info("Transaction details = " + paymentType.toString() + " " + mopType + " " + currency + " "
				+ paymentTypeCode + " " + payId + " " + transactionType + " " + paymentsRegion + " " + cardHolderType);

		// code added by sonu for tdr logic (----------- Start -------------)
//		Map<String, String> tdrMap = new HashMap<String, String>();
//		user.getAccounts().forEach(accounts -> {
//			accounts.getTdrSetting().forEach(action -> {
//
//				/*
//				 * logger.info("Id " + action.getId() + " Status : " + action.getStatus() +
//				 * " TDR Status : " + action.getTdrStatus() + " mopType " + action.getMopType()
//				 * + " paymentType : " + action.getPaymentType());
//				 */
//
//				String status = action.getStatus();
//				String tdrStatus = action.getTdrStatus();
//				if (paymentType.toString().equals(action.getPaymentType())
//						&& mopType.equals(action.getMopType())) {
//					if (!tdrStatus.equals(TDRStatus.ACTIVE.toString())) {
//						if (status.equals(TDRStatus.ACTIVE.toString())) {
//							tdrMap.put("flag", "SUCCESS");
//						}
//
//					}
//				}
//
//			});
//		});
//		user.getAccounts().forEach(accounts -> {
//			accounts.getTdrSetting().forEach(action -> {
//
//				/*
//				 * logger.info("Id " + action.getId() + " Status : " + action.getStatus() +
//				 * " TDR Status : " + action.getTdrStatus() + " mopType " + action.getMopType()
//				 * + " paymentType : " + action.getPaymentType());
//				 */
//
//				String status = action.getStatus();
//				String tdrStatus = action.getTdrStatus();
//				if (paymentType.toString().equals(action.getPaymentType())
//						&& mopType.equals(MopType.getCodeusingInstance(action.getMopType()))) {
//					if (!tdrStatus.equals(TDRStatus.ACTIVE.toString())) {
//						if (status.equals(TDRStatus.ACTIVE.toString())) {
//							tdrMap.put("flag", "SUCCESS");
//						}
//
//					}
//				}
//
//			});
//		});
//
//		logger.info("tdrMap " + tdrMap.toString());
//
//		if (!tdrMap.isEmpty()) {
//			throw new SystemException(ErrorType.TDR_SETTING_PENDING, ErrorType.TDR_SETTING_PENDING.getResponseCode());
//		}
		// (------------- Complete -------------)

		String checkAmountString = propertiesManager.propertiesMap.get(Constants.SWITCH_ACQUIRER_AMOUNT.getValue());

		if (!checkAmountString.contains(payId)) {
			paymentTypeFound = false;
		} else {
			String[] checkAmountArray = checkAmountString.split(",");
			BigDecimal transactionAmount = new BigDecimal(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));

			for (String amountSlab : checkAmountArray) {

				if (!amountSlab.contains(payId)) {
					continue;
				}

				String[] amountSlabArray = amountSlab.split("-");

				BigDecimal minTxnAmount = new BigDecimal(amountSlabArray[1]);
				BigDecimal maxTxnAmount = new BigDecimal(amountSlabArray[2]);

				if (!StringUtils.isBlank(amountSlabArray[4])) {

					if (amountSlabArray[4].equalsIgnoreCase(paymentType.getCode())) {

						minTxnAmount = new BigDecimal(amountSlabArray[1]);
						maxTxnAmount = new BigDecimal(amountSlabArray[2]);

						if (transactionAmount.compareTo(minTxnAmount) >= 0
								&& transactionAmount.compareTo(maxTxnAmount) < 1) {
							slabId = amountSlabArray[0];
							paymentTypeFound = true;
							break;
						} else {
							paymentTypeFound = false;
						}

					} else {
						paymentTypeFound = false;
					}
				}

			}

		}

		if (!paymentTypeFound) {
			logger.info("Slab not found for txn Id  = " + fields.get(FieldType.TXN_ID.getName()));
			slabId = "00";
		}

		if (StringUtils.isEmpty(fields.get(FieldType.PAYMENT_TYPE.getName())) || StringUtils.isEmpty(mopType)
				|| StringUtils.isEmpty(currency)) {
			return null;
		}

		String identifier = payId + currency + paymentTypeCode + mopType + transactionType + paymentsRegion
				+ cardHolderType + slabId;
		logger.info("Acqurier identifier = " + identifier);
		switch (paymentType) {

		case CREDIT_CARD:
		case DEBIT_CARD:
		case PREPAID_CARD:
			List<RouterConfiguration> rulesList = null;

			if (propertiesManager.propertiesMap.get("useStaticData") != null
					&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {
				rulesList = staticDataProvider.getRouterConfigData(identifier);

			} else {
				rulesList = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
			}

			int randomNumber = getRandomNumber();
			if (rulesList == null || rulesList.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesList.size() > 1) {

				int min = 1;
				int max = 0;
				for (RouterConfiguration routerConfiguration : rulesList) {
					int loadPercentage = routerConfiguration.getLoadPercentage();
					min = 1 + max;
					max = max + loadPercentage;
					if (randomNumber >= min && randomNumber < max) {
						acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
						break;
					}
				}
			} else {

				for (RouterConfiguration routerConfiguration : rulesList) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());

				}
			}
			break;

		case NET_BANKING:

			List<RouterConfiguration> rulesListNB = new ArrayList<RouterConfiguration>();
			if (propertiesManager.propertiesMap.get("useStaticData") != null
					&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {
				rulesListNB = staticDataProvider.getRouterConfigData(identifier);

			} else {
				rulesListNB = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
			}

			if (rulesListNB.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesListNB.size() > 1) {
				int randomNumberUpi = getRandomNumber();
				int min = 1;
				int max = 0;
				for (RouterConfiguration routerConfiguration : rulesListNB) {
					int loadPercentage = routerConfiguration.getLoadPercentage();
					min = 1 + max;
					max = max + loadPercentage;
					if (randomNumberUpi >= min && randomNumberUpi < max) {
						acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
						break;
					}
				}
			} else {

				for (RouterConfiguration routerConfiguration : rulesListNB) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());

				}
			}
			break;

		case WALLET:
			List<RouterConfiguration> rulesListWL = new ArrayList<RouterConfiguration>();

			if (propertiesManager.propertiesMap.get("useStaticData") != null
					&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {
				rulesListWL = staticDataProvider.getRouterConfigData(identifier);

			} else {
				rulesListWL = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
			}

			if (rulesListWL.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesListWL.size() > 1) {
				int randomNumberUpi = getRandomNumber();
				int min = 1;
				int max = 0;
				for (RouterConfiguration routerConfiguration : rulesListWL) {
					int loadPercentage = routerConfiguration.getLoadPercentage();
					min = 1 + max;
					max = max + loadPercentage;
					if (randomNumberUpi >= min && randomNumberUpi < max) {
						acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
						break;
					}
				}
			} else {

				for (RouterConfiguration routerConfiguration : rulesListWL) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());

				}
			}
			break;
//		case EMI:
//			acquirerName = AcquirerType.CITRUS_PAY.getName();
//			break;
//		case RECURRING_PAYMENT:
//			acquirerName = AcquirerType.CITRUS_PAY.getName();
//			break;
		case UPI:

			List<RouterConfiguration> rulesListUpi = new ArrayList<RouterConfiguration>();

			if (propertiesManager.propertiesMap.get("useStaticData") != null
					&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {
				rulesListUpi = staticDataProvider.getRouterConfigData(identifier);

			} else {
				rulesListUpi = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
			}

			if (rulesListUpi.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesListUpi.size() > 1) {
				int randomNumberUpi = getRandomNumber();
				int min = 1;
				int max = 0;
				for (RouterConfiguration routerConfiguration : rulesListUpi) {
					int loadPercentage = routerConfiguration.getLoadPercentage();
					min = 1 + max;
					max = max + loadPercentage;
					if (randomNumberUpi >= min && randomNumberUpi < max) {
						acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
						break;
					}
				}
			} else {

				for (RouterConfiguration routerConfiguration : rulesListUpi) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());

				}
			}

			if (rulesListUpi.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesListUpi.size() > 1) {
				int randomNumberUpi = getRandomNumber();
				int min = 1;
				int max = 0;
				for (RouterConfiguration routerConfiguration : rulesListUpi) {
					int loadPercentage = routerConfiguration.getLoadPercentage();
					min = 1 + max;
					max = max + loadPercentage;
					if (randomNumberUpi >= min && randomNumberUpi < max) {
						acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
						break;
					}
				}
			} else {

				for (RouterConfiguration routerConfiguration : rulesListUpi) {
					acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());

				}
			}
			break;

//		case AD:
//			RouterRuleDao ruleDaoAtl = new RouterRuleDao();
//			RouterRule atlRules = ruleDaoAtl.findRuleByFieldsByPayId(payId, paymentTypeCode, mopType, currency,
//					transactionType);
//			if (atlRules == null) {
//				String allPayId = "ALL MERCHANTS";
//				atlRules = ruleDaoAtl.findRuleByFieldsByPayId(allPayId, paymentTypeCode, mopType, currency,
//						transactionType);
//			}
//			if (atlRules == null) {
//				throw new SystemException(ErrorType.ROUTER_RULE_NOT_FOUND,
//						ErrorType.ROUTER_RULE_NOT_FOUND.getResponseCode());
//			}
//			String atlAcquirerList = atlRules.getAcquirerMap();
//			Collection<String> atlAcqList = Helper.parseFields(atlAcquirerList);
//			Map<String, String> atlAcquirerMap = new LinkedHashMap<String, String>();
//			for (String atlAcquirer : atlAcqList) {
//				String[] acquirerPreference = atlAcquirer.split("-");
//				atlAcquirerMap.put(acquirerPreference[0], acquirerPreference[1]);
//			}
//
//			String atlPrimaryAcquirer = atlAcquirerMap.get("1");
//			// String primaryAcquirer ="FIRSTDATA";
//			acquirerName = AcquirerType.getInstancefromCode(atlPrimaryAcquirer).getName();
//			break;
		case EMI:
			logger.info("EMI INVOKED. identifier = {}", identifier);
			List<RouterConfiguration> rulesListEmi = routerConfigurationDao.
						findActiveAcquirersByIdentifier(identifier);
			if (rulesListEmi.isEmpty()) {
				logger.info("No acquirer found for identifier = {}, Order id ={}", identifier,
						fields.get(FieldType.ORDER_ID.getName()));
			} else {
				for (RouterConfiguration routerConfiguration : rulesListEmi) {
						acquirerName = AcquirerType.getAcquirerName(routerConfiguration.getAcquirer());
					}
				}

			logger.info("AcquirerName For EMI :: {}", acquirerName);

			break;
		default:
			break;
		}
		if (StringUtils.isEmpty(acquirerName)) {
			throw new SystemException(ErrorType.ACQUIRER_NOT_FOUND, ErrorType.ACQUIRER_NOT_FOUND.getResponseCode());
		}
		fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.getInstancefromName(acquirerName).getCode());
		logger.info("Acquirer for identifier = " + identifier + " Order id " + fields.get(FieldType.ORDER_ID.getName())
				+ " Pay id " + fields.get(FieldType.PAY_ID.getName()) + " Acquirer = " + acquirerName);
		return AcquirerType.getInstancefromName(acquirerName);
	}

	private static int getRandomNumber() {
		Random rnd = new Random();
		int randomNumber = (rnd.nextInt(100)) + 1;
		return randomNumber;
	}

}
