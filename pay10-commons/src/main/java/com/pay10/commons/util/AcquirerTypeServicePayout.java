package com.pay10.commons.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.ChannelMaster;
import com.pay10.commons.user.ChannelMasterDao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.RouterConfigurationPayout;
import com.pay10.commons.user.RouterConfigurationPayoutDao;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.TdrSettingPayout;
import com.pay10.commons.user.TdrSettingPayoutDao;
import com.pay10.commons.user.User;

@Service
public class AcquirerTypeServicePayout {

	@Autowired
	private RouterConfigurationPayoutDao routerConfigurationPayoutDao;

	@Autowired
	ChannelMasterDao channelMasterDao;

	@Autowired
	private StaticDataProvider staticDataProvider;
	
	@Autowired
	MultCurrencyCodeDao multCurrencyCodeDao;

	@Autowired
	private PropertiesManager propertiesManager;

	
	@Autowired
	private TdrSettingPayoutDao payoutDao;
	private static Logger logger = LoggerFactory.getLogger(AcquirerTypeServicePayout.class.getName());

	public AcquirerType getDefault(Fields fields, User user) throws SystemException {
		return getAcquirer(fields, user);
	}

	private AcquirerType getAcquirer(Fields fields, User user) throws SystemException {
		logger.info("Finding acquirer");
		String acquirerName = "";

		ChannelMaster channel = channelMasterDao.getMatchingRule(fields.get(FieldType.PAY_TYPE.getName()));
		logger.info("channel"+channel.toString());
		if (channel == null) {
			throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED,
					ErrorType.PAYMENT_OPTION_NOT_SUPPORTED.getResponseCode());
		}

		String currency = multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		logger.info("currency"+currency);

		String channelcode = channel.getName();
		String payId = user.getPayId();
		String transactionType = user.getModeType().toString();
		logger.info("currency"+currency);

		String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
		String cardHolderType = fields.get(FieldType.CARD_HOLDER_TYPE.getName());
		logger.info("currency"+currency);

		String slabId = "00";

		logger.info("Transaction details found for txn id " + fields.get(FieldType.TXN_ID.getName())
				+ "  Looking for payment slab now");
		logger.info("Transaction details = " + channelcode + " " + " " + currency + " " 
				+ " " + payId + " " + transactionType + " " + paymentsRegion + " " + cardHolderType);

		

		String identifier = payId +channel.getName()+ currency +  transactionType + paymentsRegion + cardHolderType
				+ slabId;
		String mopType = null;
		switch (channelcode) {

		case "FIAT":
			logger.info("Acqurier identifier = " + identifier);

			List<RouterConfigurationPayout> rulesListFait = new ArrayList<RouterConfigurationPayout>();
			rulesListFait = routerConfigurationPayoutDao.findActiveAcquirersByIdentifier(identifier);
			logger.info("Acqurier rulesListFait = " + rulesListFait.size());

			if (rulesListFait.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesListFait.size() > 1) {

				if (null != rulesListFait.get(0).getRoutingType()
						&& rulesListFait.get(0).getRoutingType().equalsIgnoreCase("NormalBase")) {

					logger.info("RuleList For QRCODE Transaction : " + rulesListFait);
					for (RouterConfigurationPayout routerConfigurationPayout : rulesListFait) {

						double maxAmount = routerConfigurationPayout.getMaxAmount();
						logger.info("Merchant MaxAmount Available Is : " + maxAmount);
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);

						boolean lastFiveFailedTxnsFalg = routerConfigurationPayoutDao.lastFiveTransaction(
								routerConfigurationPayout.getAcquirer(), routerConfigurationPayout.getChannel(),
								routerConfigurationPayout.getCurrency(), routerConfigurationPayout.getFailedCount(), payId);

						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ", lastFiveFailedTxnsFalg,
								routerConfigurationPayout.isDown());

						boolean lastFailedTxnCntFlag = false;
						// boolean lastFiveFailedTxnsFalg = true;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							// TODO Initiated mail to support team for Failed count limit exceed
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if (routerConfigurationPayout.isDown()) {
								logger.info("Acquirer Down Condition");
								if (routerConfigurationPayout.getFailureCount() >= routerConfigurationPayout.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationPayoutDao.disableRCForFailedCountExceededById_again(
											Long.valueOf(routerConfigurationPayout.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
												routerConfigurationPayout.getChannel(), routerConfigurationPayout.getCurrency());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationPayoutDao.increaseFailedCntForRouterConfiguration(
											Long.valueOf(routerConfigurationPayout.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationPayoutDao
										.disableRCForFailedCountExceededById(Long.valueOf(routerConfigurationPayout.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
											routerConfigurationPayout.getChannel(), routerConfigurationPayout.getCurrency());
								}
							}

						}

						if (maxAmount >= txnAmount && lastFailedTxnCntFlag) {
							logger.info("updated id " + routerConfigurationPayout.getId() + " max amount "
									+ (maxAmount - txnAmount));
							acquirerName = AcquirerType.getAcquirerName(routerConfigurationPayout.getAcquirer());
							fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
									String.valueOf(routerConfigurationPayout.getId()));
							break;
						}
					}

					if (StringUtils.isEmpty(acquirerName)) {
						throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED,
								ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
					}

				} else if (null != rulesListFait.get(0).getRoutingType()
						&& rulesListFait.get(0).getRoutingType().equalsIgnoreCase("AmountBase")) {

					boolean amountBaseFlag = false;
					logger.info("RuleList For QRCODE Transaction : " + rulesListFait);
					for (RouterConfigurationPayout routerConfigurationPayout : rulesListFait) {

						double routerMinAmount = Double.parseDouble(routerConfigurationPayout.getRouterMinAmount());
						double routerMaxAmount = Double.parseDouble(routerConfigurationPayout.getRouterMixAmount());

						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);

						boolean lastFiveFailedTxnsFalg = routerConfigurationPayoutDao.lastFiveTransaction(
								routerConfigurationPayout.getAcquirer(), routerConfigurationPayout.getChannel(),
								routerConfigurationPayout.getCurrency(), routerConfigurationPayout.getFailedCount(), payId);

						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ", lastFiveFailedTxnsFalg,
								routerConfigurationPayout.isDown());

						boolean lastFailedTxnCntFlag = false;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if (routerConfigurationPayout.isDown()) {
								logger.info("Acquirer Down Condition");
								if (routerConfigurationPayout.getFailureCount() >= routerConfigurationPayout.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationPayoutDao.disableRCForFailedCountExceededById_again(
											Long.valueOf(routerConfigurationPayout.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
												routerConfigurationPayout.getChannel(), routerConfigurationPayout.getCurrency());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationPayoutDao.increaseFailedCntForRouterConfiguration(
											Long.valueOf(routerConfigurationPayout.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationPayoutDao
										.disableRCForFailedCountExceededById(Long.valueOf(routerConfigurationPayout.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
											routerConfigurationPayout.getChannel(), routerConfigurationPayout.getCurrency());
								}
							}

						}

						if (txnAmount >= routerMinAmount && txnAmount <= routerMaxAmount && lastFailedTxnCntFlag) {
							double maxAmount = routerConfigurationPayout.getMaxAmount();
							logger.info("Merchant MaxAmount Available Is : " + maxAmount);

							if (maxAmount >= txnAmount) {
								logger.info("updated id " + routerConfigurationPayout.getId() + " max amount "
										+ (maxAmount - txnAmount));
								acquirerName = AcquirerType.getAcquirerName(routerConfigurationPayout.getAcquirer());
								fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
										String.valueOf(routerConfigurationPayout.getId()));
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

					List<String> acquirerNames = new ArrayList<>();
					rulesListFait.stream().forEach(rule -> acquirerNames.add(rule.getAcquirer()));
					List<String> documents = routerConfigurationPayoutDao.findLastTransaction(acquirerNames, channelcode,
							currency, payId);
					if (documents.size() > 0) {
						JSONArray ja = new JSONArray(documents.toString());
						int totalAcquirers = acquirerNames.size();
						String acqName = ja.getJSONObject(0).getString("ACQUIRER_TYPE");
						int LastTxnAcqPosition = acquirerNames.indexOf(acqName);
						LastTxnAcqPosition += 1;
						int currentPosition = 0;
						boolean acqFalg = false;
						for (int j = 0; j < rulesListFait.size(); j++) {
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
									+ rulesListFait.get(currentPosition));
							double maxAmount = rulesListFait.get(currentPosition).getMaxAmount();
							double txnAmount = Double
									.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
											fields.get(FieldType.CURRENCY_CODE.getName())));
							logger.info("Customer Transaction Amount Is : " + txnAmount);
							RouterConfigurationPayout routerConfigurationPayout = rulesListFait.get(currentPosition);
							logger.info(
									"Last five transaction : " + " ACQUIRE NAME " + routerConfigurationPayout.getAcquirer()
											+ " PAYMENTTYPENAME " + routerConfigurationPayout.getChannel()
											+ " MOPTYPE " + routerConfigurationPayout.getCurrency());

							if (maxAmount >= txnAmount) {
								logger.info("################  maxAmount >= txnAmount (true) #####################");
								txnAmtFlag = true;
							} else {
								logger.info("################  maxAmount >= txnAmount (false) #####################");
								txnAmtFlag = false;
							}

							boolean lastFiveFailedTxnsFalg = routerConfigurationPayoutDao.lastFiveTransaction(
									routerConfigurationPayout.getAcquirer(), routerConfigurationPayout.getChannel(),
									routerConfigurationPayout.getCurrency(), routerConfigurationPayout.getFailedCount(), payId);

							logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ", lastFiveFailedTxnsFalg,
									routerConfigurationPayout.isDown());
							if (lastFiveFailedTxnsFalg) {
								logger.info("################  lastFailedTxnCntFlag (true) #####################");
								lastFailedTxnCntFlag = true;
							} else {
								logger.info("################  lastFailedTxnCntFlag (false) #####################");
								if (routerConfigurationPayout.isDown()) {
									logger.info("Acquirer Down Condition");
									if (routerConfigurationPayout.getFailureCount() >= routerConfigurationPayout.getFailedCount()) {
										logger.info("Acquirer Down Condition & Failure Count");
										lastFailedTxnCntFlag = false;
										int result = routerConfigurationPayoutDao.disableRCForFailedCountExceededById_again(
												Long.valueOf(routerConfigurationPayout.getId()));
										if (result == 1) {
											// TODO Mail initiated to business team.
											EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
											ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
													routerConfigurationPayout.getChannel(),
													routerConfigurationPayout.getCurrency());
										}
									} else {
										lastFailedTxnCntFlag = true;
										logger.info("Acquirer Down Condition & Else ");
										routerConfigurationPayoutDao.increaseFailedCntForRouterConfiguration(
												Long.valueOf(routerConfigurationPayout.getId()));
									}
								} else {
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationPayoutDao.disableRCForFailedCountExceededById(
											Long.valueOf(routerConfigurationPayout.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
												routerConfigurationPayout.getChannel(), routerConfigurationPayout.getCurrency());
									}
								}

							}

							if (txnAmtFlag && lastFailedTxnCntFlag) {
								// if (txnAmtFlag) {
								logger.info("$$ updated id " + rulesListFait.get(currentPosition).getId()
										+ " max amount " + (maxAmount - txnAmount));
								acquirerName = AcquirerType
										.getAcquirerName(rulesListFait.get(currentPosition).getAcquirer());
								fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
										String.valueOf(rulesListFait.get(currentPosition).getId()));
								acqFalg = true;
								break;
							}

						}

						if (!acqFalg) {
							logger.info("############ All smart routing role failed for identifier ############# "
									+ identifier);
						}

					} else {
						acquirerName = AcquirerType.getAcquirerName(rulesListFait.get(0).getAcquirer());
					}
				}
			} else {
				for (RouterConfigurationPayout routerConfigurationPayout : rulesListFait) {
					acquirerName = AcquirerType.getAcquirerName(routerConfigurationPayout.getAcquirer());
				}
			}

			logger.info("AcquirerName For R :: " + acquirerName);

			break;
		case "CRYPTO":

			List<RouterConfigurationPayout> rulesListCRYPTO = new ArrayList<RouterConfigurationPayout>();
			rulesListCRYPTO = routerConfigurationPayoutDao.findActiveAcquirersByIdentifier(identifier);
			if (rulesListCRYPTO.size() == 0) {
				logger.info("No acquirer found for identifier = " + identifier + " Order id "
						+ fields.get(FieldType.ORDER_ID.getName()));
			} else if (rulesListCRYPTO.size() > 1) {

				if (null != rulesListCRYPTO.get(0).getRoutingType()
						&& rulesListCRYPTO.get(0).getRoutingType().equalsIgnoreCase("NormalBase")) {

					logger.info("RuleList For QRCODE Transaction : " + rulesListCRYPTO);
					for (RouterConfigurationPayout routerConfigurationPayout : rulesListCRYPTO) {

						double maxAmount = routerConfigurationPayout.getMaxAmount();
						logger.info("Merchant MaxAmount Available Is : " + maxAmount);
						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);

						boolean lastFiveFailedTxnsFalg = routerConfigurationPayoutDao.lastFiveTransaction(
								routerConfigurationPayout.getAcquirer(), routerConfigurationPayout.getChannel(),
								routerConfigurationPayout.getCurrency(), routerConfigurationPayout.getFailedCount(), payId);

						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ", lastFiveFailedTxnsFalg,
								routerConfigurationPayout.isDown());

						boolean lastFailedTxnCntFlag = false;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if (routerConfigurationPayout.isDown()) {
								logger.info("Acquirer Down Condition");
								if (routerConfigurationPayout.getFailureCount() >= routerConfigurationPayout.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationPayoutDao.disableRCForFailedCountExceededById_again(
											Long.valueOf(routerConfigurationPayout.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
												routerConfigurationPayout.getChannel(), routerConfigurationPayout.getCurrency());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationPayoutDao.increaseFailedCntForRouterConfiguration(
											Long.valueOf(routerConfigurationPayout.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationPayoutDao
										.disableRCForFailedCountExceededById(Long.valueOf(routerConfigurationPayout.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
											routerConfigurationPayout.getChannel(), routerConfigurationPayout.getCurrency());
								}
							}

						}

						if (maxAmount >= txnAmount && lastFailedTxnCntFlag) {
							logger.info("updated id " + routerConfigurationPayout.getId() + " max amount "
									+ (maxAmount - txnAmount));
							acquirerName = AcquirerType.getAcquirerName(routerConfigurationPayout.getAcquirer());
							fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
									String.valueOf(routerConfigurationPayout.getId()));
							break;
						}
					}

					if (StringUtils.isEmpty(acquirerName)) {
						throw new SystemException(ErrorType.DECLINED_BY_LIMIT_EXCEEDED,
								ErrorType.DECLINED_BY_LIMIT_EXCEEDED.getResponseCode());
					}

				} else if (null != rulesListCRYPTO.get(0).getRoutingType()
						&& rulesListCRYPTO.get(0).getRoutingType().equalsIgnoreCase("AmountBase")) {

					boolean amountBaseFlag = false;
					logger.info("RuleList For rulesListCRYPTO Transaction : " + rulesListCRYPTO);
					for (RouterConfigurationPayout routerConfigurationPayout : rulesListCRYPTO) {

						double routerMinAmount = Double.parseDouble(routerConfigurationPayout.getRouterMinAmount());
						double routerMaxAmount = Double.parseDouble(routerConfigurationPayout.getRouterMixAmount());

						double txnAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
								fields.get(FieldType.CURRENCY_CODE.getName())));
						logger.info("Customer Transaction Amount Is : " + txnAmount);

						boolean lastFiveFailedTxnsFalg = routerConfigurationPayoutDao.lastFiveTransaction(
								routerConfigurationPayout.getAcquirer(), routerConfigurationPayout.getChannel(),
								routerConfigurationPayout.getCurrency(), routerConfigurationPayout.getFailedCount(), payId);

						logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ", lastFiveFailedTxnsFalg,
								routerConfigurationPayout.isDown());

						boolean lastFailedTxnCntFlag = false;
						if (lastFiveFailedTxnsFalg) {
							logger.info("################  lastFailedTxnCntFlag (true) #####################");
							lastFailedTxnCntFlag = true;
						} else {
							logger.info("################  lastFailedTxnCntFlag (false) #####################");
							if (routerConfigurationPayout.isDown()) {
								logger.info("Acquirer Down Condition");
								if (routerConfigurationPayout.getFailureCount() >= routerConfigurationPayout.getFailedCount()) {
									logger.info("Acquirer Down Condition & Failure Count");
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationPayoutDao.disableRCForFailedCountExceededById_again(
											Long.valueOf(routerConfigurationPayout.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
												routerConfigurationPayout.getChannel(), routerConfigurationPayout.getCurrency());
									}
								} else {
									lastFailedTxnCntFlag = true;
									logger.info("Acquirer Down Condition & Else ");
									routerConfigurationPayoutDao.increaseFailedCntForRouterConfiguration(
											Long.valueOf(routerConfigurationPayout.getId()));
								}
							} else {
								lastFailedTxnCntFlag = false;
								int result = routerConfigurationPayoutDao
										.disableRCForFailedCountExceededById(Long.valueOf(routerConfigurationPayout.getId()));
								if (result == 1) {
									// TODO Mail initiated to business team.
									EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
									ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
											routerConfigurationPayout.getChannel(), routerConfigurationPayout.getCurrency());
								}
							}

						}

						if (txnAmount >= routerMinAmount && txnAmount <= routerMaxAmount && lastFailedTxnCntFlag) {
							double maxAmount = routerConfigurationPayout.getMaxAmount();
							logger.info("Merchant MaxAmount Available Is : " + maxAmount);

							if (maxAmount >= txnAmount) {
								logger.info("updated id " + routerConfigurationPayout.getId() + " max amount "
										+ (maxAmount - txnAmount));
								acquirerName = AcquirerType.getAcquirerName(routerConfigurationPayout.getAcquirer());
								fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
										String.valueOf(routerConfigurationPayout.getId()));
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

					List<String> acquirerNames = new ArrayList<>();
					rulesListCRYPTO.stream().forEach(rule -> acquirerNames.add(rule.getAcquirer()));
					List<String> documents = routerConfigurationPayoutDao.findLastTransaction(acquirerNames, channelcode,
							currency, payId);
					if (documents.size() > 0) {
						JSONArray ja = new JSONArray(documents.toString());
						int totalAcquirers = acquirerNames.size();
						String acqName = ja.getJSONObject(0).getString("ACQUIRER_TYPE");
						int LastTxnAcqPosition = acquirerNames.indexOf(acqName);
						LastTxnAcqPosition += 1;
						int currentPosition = 0;
						boolean acqFalg = false;
						for (int j = 0; j < rulesListCRYPTO.size(); j++) {
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
									+ rulesListCRYPTO.get(currentPosition));
							double maxAmount = rulesListCRYPTO.get(currentPosition).getMaxAmount();
							double txnAmount = Double
									.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
											fields.get(FieldType.CURRENCY_CODE.getName())));
							logger.info("Customer Transaction Amount Is : " + txnAmount);
							RouterConfigurationPayout routerConfigurationPayout = rulesListCRYPTO.get(currentPosition);
							// Fetch last five failed transaction based on Acquirer
							logger.info(
									"Last five transaction : " + " ACQUIRE NAME " + routerConfigurationPayout.getAcquirer()
											+ " PAYMENTTYPENAME " + routerConfigurationPayout.getChannel()
											+ " MOPTYPE " + routerConfigurationPayout.getCurrency());

							if (maxAmount >= txnAmount) {
								logger.info("################  maxAmount >= txnAmount (true) #####################");
								txnAmtFlag = true;
							} else {
								logger.info("################  maxAmount >= txnAmount (false) #####################");
								txnAmtFlag = false;
							}

							boolean lastFiveFailedTxnsFalg = routerConfigurationPayoutDao.lastFiveTransaction(
									routerConfigurationPayout.getAcquirer(), routerConfigurationPayout.getChannel(),
									routerConfigurationPayout.getCurrency(), routerConfigurationPayout.getFailedCount(), payId);

							logger.info("lastFiveFailedTxnsFalg = {}, IsDown= {} ", lastFiveFailedTxnsFalg,
									routerConfigurationPayout.isDown());
							if (lastFiveFailedTxnsFalg) {
								logger.info("################  lastFailedTxnCntFlag (true) #####################");
								lastFailedTxnCntFlag = true;
							} else {
								logger.info("################  lastFailedTxnCntFlag (false) #####################");
								if (routerConfigurationPayout.isDown()) {
									logger.info("Acquirer Down Condition");
									if (routerConfigurationPayout.getFailureCount() >= routerConfigurationPayout.getFailedCount()) {
										logger.info("Acquirer Down Condition & Failure Count");
										lastFailedTxnCntFlag = false;
										int result = routerConfigurationPayoutDao.disableRCForFailedCountExceededById_again(
												Long.valueOf(routerConfigurationPayout.getId()));
										if (result == 1) {
											EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
											ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
													routerConfigurationPayout.getChannel(),
													routerConfigurationPayout.getCurrency());
										}
									} else {
										lastFailedTxnCntFlag = true;
										logger.info("Acquirer Down Condition & Else ");
										routerConfigurationPayoutDao.increaseFailedCntForRouterConfiguration(
												Long.valueOf(routerConfigurationPayout.getId()));
									}
								} else {
									lastFailedTxnCntFlag = false;
									int result = routerConfigurationPayoutDao.disableRCForFailedCountExceededById(
											Long.valueOf(routerConfigurationPayout.getId()));
									if (result == 1) {
										// TODO Mail initiated to business team.
										EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
										ecsp.failedCntLimitExceedEmail(routerConfigurationPayout.getAcquirer(),
												routerConfigurationPayout.getChannel(), routerConfigurationPayout.getCurrency());
									}
								}

							}

							if (txnAmtFlag && lastFailedTxnCntFlag) {
								// if (txnAmtFlag) {
								logger.info("$$ updated id " + rulesListCRYPTO.get(currentPosition).getId()
										+ " max amount " + (maxAmount - txnAmount));
								acquirerName = AcquirerType
										.getAcquirerName(rulesListCRYPTO.get(currentPosition).getAcquirer());
								fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
										String.valueOf(rulesListCRYPTO.get(currentPosition).getId()));
								acqFalg = true;
								break;
							}

						}

						if (!acqFalg) {
							logger.info("############ All smart routing role failed for identifier ############# "
									+ identifier);
						}

					} else {
						acquirerName = AcquirerType.getAcquirerName(rulesListCRYPTO.get(0).getAcquirer());
					}
				}
			} else {
				for (RouterConfigurationPayout routerConfigurationPayout : rulesListCRYPTO) {
					acquirerName = AcquirerType.getAcquirerName(routerConfigurationPayout.getAcquirer());
				}
			}

			logger.info("AcquirerName For R :: " + acquirerName);

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
				+channelcode + "\t acquirerName : " + acquirerName
				+ "\t MopType: " +currency + "Transaction Type : SALE" + "\t currency :"
				+ currency + "\t amount : " + amount + "\t Date : "
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "\t paymentsRegion : " + paymentsRegion
				+ "\t cardHolderType : " + cardHolderType);
		TdrSettingPayout setting = payoutDao.getTdrAndSurcharge(payId, channel.getName(),
				acquirerName, "SALE", currency, amount,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date), paymentsRegion, cardHolderType);
		if (setting == null) {
			throw new SystemException(ErrorType.TDR_NOT_SET_FOR_THIS_AMOUNT,
					ErrorType.TDR_NOT_SET_FOR_THIS_AMOUNT.getResponseCode());
		}
		return AcquirerType.getInstancefromName(acquirerName);
	}

	

	private static int getRandomNumber() {
		Random rnd = new Random();
		int randomNumber = (rnd.nextInt(100)) + 1;
		return randomNumber;
	}

}
