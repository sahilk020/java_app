package com.pay10.pg.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.SmsSender;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.dto.MerchantWalletPODTO;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.VpaMasterDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.AcquirerTypeServicePayout;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.POhistory.PayoutHistorian;
import com.pay10.pg.core.charging.PayoutChargingDetailHelper;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.security.Authenticator;
import com.pay10.pg.core.util.Processor;

@Service("PayoutSecurityProcessor")
public class PayoutSecurityProcessor implements Processor {

	@Autowired
	private VpaMasterDao vapDao;

	@Autowired
	private Authenticator authenticator;

	@Autowired
	private PayoutHistorian historian;

	@Autowired
	private GeneralValidator generalValidator;

	@Autowired
	private AWSEncryptDecryptService awsEncryptDecryptService;

	@Autowired
	private AcquirerTypeServicePayout acquirerTypeService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private SurchargeDao surchargeDao;
	
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	@Autowired
	private PayoutChargingDetailHelper payoutChargingDetailHelper;

	@Autowired
	private SmsSender smsSender;
	@Autowired
	private Fields field;

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private MerchantWalletPODao merchantWalletPODao;

	@Autowired
	private StaticDataProvider staticDataProvider;
	@Autowired
	private PayoutChargingDetailHelper chargingDetailHelper;

	private static int smsCount = 0;

	private static Logger logger = LoggerFactory.getLogger(PayoutSecurityProcessor.class.getName());

	@Override
	public void preProcess(Fields fields) throws SystemException {

	}

	@Override
	public void process(Fields fields) throws SystemException {

		validate(fields);
		logger.info("Enter Payout Security Processor Process : {}", fields.getFieldsAsString());

		if (fields.get(FieldType.TXNTYPE.getName()) != null && fields.get(FieldType.RESPONSE_CODE.getName()) != null
				&& fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.STATUS.getName())
				&& fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.INVALID_HASH.getCode())) {
			fields.put(FieldType.STATUS.getName(), "Error");
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
			return;
		}

		fields.addDefaultFields();
		fields.logAllFields("Refined Request:");
		// Process authenticity
		authenticate(fields);
		addPreviousFieldsPayout(fields);

//		fields.put(FieldType.PG_REF_NUM.getName(), TransactionManager.getNewTransactionId());
		fields.put(FieldType.MERCHANT_NAME.getName(),
				userDao.findByPayId(fields.get(FieldType.PAY_ID.getName())).getBusinessName());

//		validateDupicateOrderIdPayout(fields);
		addAcquirerFields(fields);

		chargingDetailHelper.addTransactionDataFieldsPayout(fields);

		logger.info("Before processorValidations () ");
		generalValidator.processorValidations(fields);
		logger.info("After processorValidations () ");
		logger.info("fields in secuity processor in payout {} {}", fields.get(FieldType.TXNTYPE.getName())
				, fields.get(FieldType.STATUS.getName()));

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())
				&& (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.PENDING.getName()) || fields
						.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SENT_TO_BANK.getName()))) {
			MerchantWalletDetail(fields);
		}

		logger.info("Exit Security : {}", fields.getFieldsAsString());
	}

	private void MerchantWalletDetail(Fields fields) throws SystemException {
		logger.info("fields in secuity processor in payout");
		MerchantWalletPODTO merchantWalletPODTO = new MerchantWalletPODTO();
		merchantWalletPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));
		merchantWalletPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName())));

		MerchantWalletPODTO merchantWalletPODTO1 = merchantWalletPODao.findByPayId(merchantWalletPODTO);
		if (merchantWalletPODTO1 == null) {
			throw new SystemException(ErrorType.PO_WALLET_IS_NOT_FOUND,
					",Merchant wallet is not found for  PayId=" + fields.get(FieldType.PAY_ID.getName()));

		}
		logger.info("fields in secuity processor in payout" + merchantWalletPODTO1.toString());

		double finalAmount = Double.valueOf(merchantWalletPODTO1.getFinalBalance());
		double TotalTdrAmount = Double.valueOf(fields.get(FieldType.ACQUIRER_TDR_SC.getName()))
				+ Double.valueOf(fields.get(FieldType.PG_TDR_SC.getName()));
		double transactionAmount = Double.valueOf(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
				fields.get(FieldType.CURRENCY_CODE.getName())));
		double totalamount = TotalTdrAmount + transactionAmount;
		double finalAmt = finalAmount - TotalTdrAmount;
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime myDateObj = LocalDateTime.now();
		String formattedDate = myDateObj.format(myFormatObj);
		logger.info("fields in secuity processor in payout finalAmount" + finalAmount);
		logger.info("fields in secuity processor in payout TotalTdrAmount" + TotalTdrAmount);
		logger.info("fields in secuity processor in payout totalamount" + totalamount);

		logger.info("fields in secuity processor in payout" + merchantWalletPODTO1.toString());
		logger.info("fields in secuity processor in payout" + finalAmt);

		merchantWalletPODTO1.setFinalBalance(String.valueOf(finalAmt));


		double totalBalance = Double.valueOf(merchantWalletPODTO1.getTotalBalance());
		double totalBalanceAmt = totalBalance - TotalTdrAmount;
		logger.info("fields in secuity processor in payout totalBalance" + totalBalance);
		logger.info("fields in secuity processor in payout" + totalBalanceAmt);

		merchantWalletPODTO1.setTotalBalance(String.valueOf(totalBalanceAmt));



		PassbookPODTO passbookPODTO = new PassbookPODTO();
		passbookPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));
		passbookPODTO.setType("DEBIT");
		passbookPODTO.setNarration("PAYOUT CHARGES");
		passbookPODTO.setCreateDate(formattedDate);
		passbookPODTO.setTxnId(TransactionManager.getNewTransactionId());
		passbookPODTO.setDebitAmt(String.valueOf(TotalTdrAmount));
		passbookPODTO.setAmount(String.valueOf(TotalTdrAmount));
		passbookPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(String.valueOf(fields.get(FieldType.CURRENCY_CODE.getName()))));
		passbookPODTO.setRespTxn(fields.get(FieldType.ORDER_ID.getName()));
		logger.info("Save the Transaction History in passbook"+passbookPODTO.toString());
		merchantWalletPODao.savePassbookDetailsByPassbook(passbookPODTO);

		merchantWalletPODao.SaveAndUpdteWallet(merchantWalletPODTO1);

		if(finalAmt<0){
			throw new SystemException(ErrorType.INSUFFICIENT_BALANCE, "Insufficient balance in merchant wallet");
		}

	}

	public void validate(Fields fields) throws SystemException {
		Processor validationProcessor = new ValidationProcessor();
		validationProcessor.preProcess(fields);
		validationProcessor.process(fields);
		validationProcessor.postProcess(fields);
	}

	public void addPreviousFieldsPayout(Fields fields) throws SystemException {
		// Ideally previous fields are responsibility of history processor,
		// but we are putting it here for smart router

		String txntype = fields.get(FieldType.TXNTYPE.getName());

		if (txntype.equals(TransactionType.PO_STATUS.getName())) {
			historian.findPreviousForStatusPayout(fields);

		} else {
			historian.findPreviousPayout(fields);

		}
		historian.populateFieldsFromPreviousPayout(fields);

	}

	public void checkDuplicateSubmitPayout(Fields fields) throws SystemException {

		if (StringUtils.isNotBlank(fields.get(FieldType.TXNTYPE.getName()))
				&& StringUtils.isBlank(fields.get(FieldType.ACQ_ID.getName()))) {

			if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())
					|| fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
				historian.validateDuplicateSubmit(fields);
			}

		}

	}

	public void validateDupicateOrderIdPayout(Fields fields) throws SystemException {

		String origTxnType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
		if (StringUtils.isEmpty(origTxnType)) {
			origTxnType = "";
		}

		if (((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName())))
				|| ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.REFUND.getName()))
						&& (!origTxnType.equalsIgnoreCase(TransactionType.STATUS.getName())))) {
			historian.validateDuplicateOrderId(fields);
		}

		if ((fields.contains(FieldType.IS_MERCHANT_HOSTED.getName()))
				&& !(StringUtils.isEmpty(fields.get(FieldType.IS_MERCHANT_HOSTED.getName())))
				&& (fields.get(FieldType.IS_MERCHANT_HOSTED.getName()).equals("Y"))) {
			historian.validateDuplicateOrderId(fields);
		}
	}

	public void addAcquirerFields(Fields fields) throws SystemException {

		try {
			logger.info("111111111111111111111111111" + fields.getFieldsAsString());
			if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName()))) {
				return;
			}
			String internalOrigTxnId = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
			if (internalOrigTxnId != null) {

				if (fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
						|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
						|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
								.equals(TransactionType.REFUNDRECO.getName())) {
					return;
				}
			}
			User user = authenticator.getUser(fields);

			String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
			if (StringUtils.isEmpty(acquirer)) {
				acquirer = fields.get(FieldType.INTERNAL_ACQUIRER_TYPE.getName());
				if (StringUtils.isEmpty(acquirer)) {
					// authenticator.validatePaymentOptions(fields);
					AcquirerType acquirerType = acquirerTypeService.getDefault(fields, user);

					acquirer = acquirerType.getCode();
					logger.info("Current Acquirer for Order Id " + fields.get(FieldType.ORDER_ID.getName()) + " is "
							+ acquirer);
					fields.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
					fields.put(FieldType.INTERNAL_ACQUIRER_TYPE.getName(), acquirer);
				} else {
					fields.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
				}
			}

			if (user == null) {
				logger.info("User is null for txn ID = " + fields.get(FieldType.TXN_ID.getName()));
			} else {
				logger.info(
						"User payID = " + user.getPayId() + " Txn payID = " + fields.get(FieldType.PAY_ID.getName()));
			}

//		

			String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());

			smsCount = smsCount + 1;

			String payId = fields.get(FieldType.PAY_ID.getName());

			if (smsCount % 3 == 0) {

				StringBuilder smsBody = new StringBuilder();
				smsBody.append("Alert !");
				smsBody.append("Transaction not processed for merchant  with payId = " + payId);
				if (null != user.getBusinessName()) {
					smsBody.append(" and name  = " + user.getBusinessName());
				}
				smsBody.append(" due to missing account for merchant .");
				smsBody.append("Transaction Id = " + fields.get(FieldType.TXN_ID.getName()));
				if (StringUtils.isNotBlank(fields.get(FieldType.ORDER_ID.getName()))) {
					smsBody.append(" , Order Id = " + fields.get(FieldType.ORDER_ID.getName()));
				}

				String smsSenderList = PropertiesManager.propertiesMap.get("smsAlertList");

				for (String mobile : smsSenderList.split(",")) {
					try {
						smsSender.sendSMS(mobile, smsBody.toString());
					} catch (IOException e) {
						logger.error("SMS not sent for transaction failure when account is null");
					}
				}

			}
		} catch (Exception e) {
			logger.error("Error In Security Processor", e);
			throw new SystemException(ErrorType.NOT_APPROVED_FROM_ACQUIRER,
					"User is not approved from acquirer, PayId=" + fields.get(FieldType.PAY_ID.getName())
							+ " , Acquirer = " + fields.get(FieldType.ACQUIRER_TYPE.getName()));
		}

	}

	public void authenticate(Fields fields) throws SystemException {
		User user = null;

		if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
				&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
						.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
			user = staticDataProvider.getUserData(fields.get(FieldType.PAY_ID.getName()));
		}

		else {
			user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		}

		authenticator.setUser(user);
		authenticator.authenticate(fields);
	}

	@Override
	public void postProcess(Fields fields) {
		fields.removeSecureFieldsSubmitted();
		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.REFUND.getName()))) {
			if (StringUtils.isBlank(fields.get(FieldType.REFUND_FLAG.getName()))) {
				fields.put(FieldType.REFUND_FLAG.getName(), "C");
			}
		}
	}
}
