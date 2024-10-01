package com.pay10.pg.core.util;

import com.pay10.commons.dto.MerchantWalletPODTO;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.util.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.nimbusds.jose.shaded.gson.Gson;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.security.Authenticator;
import com.pay10.pg.core.security.AuthenticatorFactory;
import com.pay10.pg.core.security.AuthenticatorImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service("PayoutUpdateProcessor")
public class PayoutUpdateProcessor implements Processor {

	@Autowired
	private CryptoManager cryptoManager;

	@Autowired
	private GeneralValidator generalValidator;

	@Autowired
	FieldsDao fieldsDao;

	@Autowired
	private Fields field;

	@Autowired
	private RouterConfigurationService routerConfigurationService;

	@Autowired
	AuthenticatorImpl authenticator;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	@Autowired
	private MerchantWalletPODao merchantWalletPODao;

	private static Logger logger = LoggerFactory.getLogger(PayoutUpdateProcessor.class.getName());
	final String bsesPayid = PropertiesManager.propertiesMap.get(Constants.BSESPAYID.getValue());

	@Override
	public void preProcess(Fields fields) throws SystemException {
		// cryptoManager.secure(fields);
	}

	@Override
	public void process(Fields fields) throws SystemException {

		if (StringUtils.isNoneBlank(fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))
				&& fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equalsIgnoreCase("PO_STATUS")) {
			return;
		}
		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName())) 
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.PENDING.getName())) {
			logger.info("Payout Update Processor, PG_REF_NUM={},status={}",fields.get(FieldType.PG_REF_NUM.getName()),fields.get(FieldType.STATUS.getName()));
			field.insertPO(fields);
		}
		
		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName())) 
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())) {
			logger.info("Payout Update Processor, PG_REF_NUM={},status={}",fields.get(FieldType.PG_REF_NUM.getName()),fields.get(FieldType.STATUS.getName()));
			field.insertPOUpdate(fields);
		}
		
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName())) &&
				!fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName()) &&
				!fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.PENDING.getName()) &&
				!fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SENT_TO_BANK.getName()) &&
				!fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.REQUEST_ACCEPTED.getName())){
			logger.info("Payout Update Processor, PG_REF_NUM={},status={}",fields.get(FieldType.PG_REF_NUM.getName()),fields.get(FieldType.STATUS.getName()));
			updateMerchantWallet(fields);
			field.insertPOUpdate(fields);
		}


	}

	public void prepareInvalidTransactionForStorage(Fields fields) throws SystemException {
		logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + fields.getFieldsAsString());
		String payId = fields.get(FieldType.PAY_ID.getName());
		if (!StringUtils.isEmpty(payId)) {

//			 Authenticator authenticator = AuthenticatorFactory.getAuthenticator();
			authenticator.isUserExists(fields);

			String txnId = fields.get(FieldType.TXN_ID.getName());
			if (StringUtils.isEmpty(txnId)) {
				txnId = TransactionManager.getNewTransactionId();
			}
			String responseCode = fields.get(FieldType.RESPONSE_CODE.getName());
			String responseMessage = fields.get(FieldType.RESPONSE_MESSAGE.getName());
			String detailMessage = fields.get(FieldType.PG_TXN_MESSAGE.getName());
			String transactionType = fields.get(FieldType.TXNTYPE.getName());
			String returnUrl = fields.get(FieldType.RETURN_URL.getName());
			String orderId = fields.get(FieldType.ORDER_ID.getName());
			Fields internalFields = fields.removeInternalFields();
			String oid = fields.get(FieldType.OID.getName());
			String resellerId = fields.get(FieldType.RESELLER_ID.getName());
			if (StringUtils.isEmpty(oid)) {
				oid = txnId;
			}
			// Validate and insert fields
			if (StringUtils.isNotBlank(transactionType)) {
				if (transactionType.equals(TxnType.REFUND.getName())) {
					fields.put(FieldType.ORIG_TXNTYPE.getName(), TxnType.REFUND.getName());
				} else {
					fields.put(FieldType.ORIG_TXNTYPE.getName(), TxnType.SALE.getName());
					fields.put(FieldType.TXNTYPE.getName(), TxnType.SALE.getName());
				}
			} else {
				fields.put(FieldType.ORIG_TXNTYPE.getName(), TxnType.SALE.getName());
				fields.put(FieldType.TXNTYPE.getName(), TxnType.SALE.getName());
			}
			String txnType = fields.get(FieldType.TXNTYPE.getName());
//			fields.clear();
			fields.put(internalFields);
			fields.put(FieldType.TXNTYPE.getName(), txnType);
			fields.put(FieldType.TXN_ID.getName(), txnId);
			fields.put(FieldType.OID.getName(), oid);
			fields.put(FieldType.RESPONSE_CODE.getName(), responseCode);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), responseMessage);
			logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + fields.getFieldsAsString());
			if (fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.EKYC_VERIFICATION.getCode())) {
				logger.info(
						"Ekyc Response Message and Response Code : " + fields.get(FieldType.RESPONSE_MESSAGE.getName())
								+ "\t" + fields.get(FieldType.RESPONSE_CODE.getName()));
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.EKYC_VERIFICATION.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_VERIFICATION.getResponseCode());
			} else if (fields.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.EKYC_DETAILS_NOT_FOUND.getCode())) {
				logger.info(
						"Ekyc Response Message and Response Code : " + fields.get(FieldType.RESPONSE_MESSAGE.getName())
								+ "\t" + fields.get(FieldType.RESPONSE_CODE.getName()));
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.EKYC_DETAILS_NOT_FOUND.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_DETAILS_NOT_FOUND.getResponseCode());
			} else if (fields.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.EKYC_SERVICE_DOWN.getCode())) {
				logger.info(
						"Ekyc Response Message and Response Code : " + fields.get(FieldType.RESPONSE_MESSAGE.getName())
								+ "\t" + fields.get(FieldType.RESPONSE_CODE.getName()));
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.EKYC_SERVICE_DOWN.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_SERVICE_DOWN.getResponseCode());
			} else {
				fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			}

			logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + fields.getFieldsAsString());

			fields.put(FieldType.PAY_ID.getName(), payId);
			fields.put(FieldType.RETURN_URL.getName(), returnUrl);
			fields.put(FieldType.ORDER_ID.getName(), orderId);

			if (StringUtils.isNotBlank(resellerId)) {
				fields.put(FieldType.RESELLER_ID.getName(), resellerId);
			}

			try {
				generalValidator.validateField(FieldType.ORDER_ID, FieldType.ORDER_ID.getName(), fields);
			} catch (SystemException systemException) {
				fields.put(FieldType.ORDER_ID.getName(), Constants.SUCCESS_CODE.getValue());
			}

			fields.put(FieldType.PG_TXN_MESSAGE.getName(), detailMessage);

			String pgrefNum = fields.get(FieldType.PG_REF_NUM.getName());
			if (pgrefNum == null) {
				fields.put(FieldType.PG_REF_NUM.getName(), txnId);
			}

			logger.info("CHECKING ABC: " + fields.getFieldsAsString());
			field.insertPO(fields);
		}
	}

	public void addOid(Fields fields) {
		String oid = fields.get(FieldType.OID.getName());
		// Oid is already present, return
		if (!StringUtils.isEmpty(oid)) {
			return;
		}

		String transactionType = fields.get(FieldType.TXNTYPE.getName());
		if (null != transactionType && transactionType.equals(TransactionType.NEWORDER.getName())) {
			oid = fields.get(FieldType.TXN_ID.getName());
		} else {
			String origOid = fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName());
			if (!StringUtils.isEmpty(origOid)) {
				oid = origOid;
			} else {
				// to add OID for transactions that are through webservice or invalid
				oid = fields.get(FieldType.TXN_ID.getName());
			}
		}
		if (StringUtils.isEmpty(oid)) {
			logger.warn("Unable to add OID, this may cause some issues!");
		} else {
			fields.put(FieldType.OID.getName(), oid);
		}
	}

	@Override
	public void postProcess(Fields fields) {
		/*
		 * String transactionType = fields.get(FieldType.TXNTYPE.getName());
		 * if((transactionType.equals(TransactionType.SALE.getName())) ||
		 * (transactionType.equals(TransactionType.REFUND.getName()))) {
		 * fields.put(FieldType.TXN_ID.getName(),
		 * fields.get(FieldType.PG_REF_NUM.getName())); }
		 */

	}

	private void updateMerchantWallet(Fields fields) throws SystemException{
		logger.info("Inside updateMerchantWallet : " + fields.getFieldsAsString());
			MerchantWalletPODTO merchantWalletPODTO = new MerchantWalletPODTO();
			merchantWalletPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));
			merchantWalletPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName())));

			MerchantWalletPODTO merchantWalletPODTO1 = merchantWalletPODao.findByPayId(merchantWalletPODTO);
			if(merchantWalletPODTO1 == null) {
				return;
			}

			double finalAmount = Double.parseDouble(merchantWalletPODTO1.getFinalBalance());
			double acquirerTdr = Double.parseDouble(fields.getFields().getOrDefault(FieldType.ACQUIRER_TDR_SC.getName(),"0"));
			double pgTdr = Double.parseDouble(fields.getFields().getOrDefault(FieldType.PG_TDR_SC.getName(),"0"));
			double totalTdrAmount = acquirerTdr + pgTdr;
			double totalAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));
			double finalAmt = finalAmount + totalTdrAmount + totalAmount;

			logger.info("fields in update processor in payout finalAmount abc{} ", finalAmount);
			logger.info("fields in update processor in payout totalTdrAmount abc{}", totalTdrAmount);
			logger.info("fields in update processor in payout totalAmount abc{}", totalAmount);

			logger.info("fields in update processor in payout abc{}", merchantWalletPODTO1);
			logger.info("fields in update processor in payout abc{}", finalAmt);

			merchantWalletPODTO1.setFinalBalance(String.valueOf(finalAmount+totalTdrAmount));


			double totalBalance = Double.parseDouble(merchantWalletPODTO1.getTotalBalance());
			double totalBalanceAmt = totalBalance + totalTdrAmount + totalAmount;
			logger.info("fields in update processor in payout totalBalance abc{}", totalBalance);
			logger.info("fields in update processor in payout abc {}", totalBalanceAmt);

			merchantWalletPODTO1.setTotalBalance(String.valueOf(totalBalance+totalTdrAmount));

		LocalDateTime myDateObj = LocalDateTime.now();
		
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		String formattedDate = myDateObj.format(myFormatObj);
		logger.info("Formatted Date : " + formattedDate);
		
//		PassbookPODTO passbookPODTO = new PassbookPODTO();
//		passbookPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));
//		passbookPODTO.setType("CREDIT");
//		passbookPODTO.setNarration("PAYOUT CHARGES REVERSAL");
//		passbookPODTO.setCreateDate(formattedDate);
//		passbookPODTO.setTxnId(TransactionManager.getNewTransactionId());
//		passbookPODTO.setAmount(String.valueOf(totalTdrAmount));
//		passbookPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(String.valueOf(fields.get(FieldType.CURRENCY_CODE.getName()))));
//		passbookPODTO.setRespTxn(fields.get(FieldType.ORDER_ID.getName()));
//		logger.info("Save the Transaction History in passbook abc{}", passbookPODTO);
//		if(totalTdrAmount!=0) {
//			merchantWalletPODao.savePassbookDetailsByPassbook(passbookPODTO);
//		}
//		passbookPODTO.setTxnId(TransactionManager.getNewTransactionId());
//		passbookPODTO.setNarration("PAYOUT REVERSAL");
//		passbookPODTO.setAmount(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
//		logger.info("Save the Transaction History in passbook abc {}",passbookPODTO);
//
//		merchantWalletPODao.savePassbookDetailsByPassbook(passbookPODTO);
//
//
//		//merchantWalletPODao.SaveAndUpdteWallet(merchantWalletPODTO1);
//		merchantWalletPODao.SaveAndUpdteAvailableBalanceWallet(merchantWalletPODTO1);
		
		
		PassbookPODTO passbookPODTO = new PassbookPODTO();
        passbookPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));
        passbookPODTO.setType("CREDIT");
        passbookPODTO.setNarration("PAYOUT CHARGES REVERSAL");
        passbookPODTO.setCreateDate(formattedDate);
        passbookPODTO.setTxnId(TransactionManager.getNewTransactionId());
        passbookPODTO.setAmount(String.valueOf(totalTdrAmount));
        passbookPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(String.valueOf(fields.get(FieldType.CURRENCY_CODE.getName()))));
        passbookPODTO.setRespTxn(fields.get(FieldType.ORDER_ID.getName()));
        logger.info("Save the Transaction History in passbook {}", passbookPODTO);
        
        merchantWalletPODao.savePassbookDetailsByPassbook(passbookPODTO);
        logger.info("Before Wallet Update PAYOUT CHARGES REVERSAL : " + new Gson().toJson(merchantWalletPODTO1));
        merchantWalletPODao.SaveAndUpdteWallet(merchantWalletPODTO1);
        
        //another service call here
        
        PassbookPODTO passbookPODTORev = new PassbookPODTO();
        passbookPODTORev.setPayId(fields.get(FieldType.PAY_ID.getName()));
        passbookPODTORev.setType("CREDIT");
        passbookPODTORev.setNarration("PAYOUT REVERSAL");
        passbookPODTORev.setCreateDate(formattedDate);
        passbookPODTORev.setTxnId(TransactionManager.getNewTransactionId());
        passbookPODTORev.setAmount(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
        passbookPODTORev.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(String.valueOf(fields.get(FieldType.CURRENCY_CODE.getName()))));
        passbookPODTORev.setRespTxn(fields.get(FieldType.ORDER_ID.getName()));
        
        logger.info("Save the Transaction History in passbook {}",passbookPODTORev);
        MerchantWalletPODTO merchantWalletPODTORev = merchantWalletPODao.findByPayId(merchantWalletPODTO);
        logger.info("MerchantWalletPODTORev : " + new Gson().toJson(merchantWalletPODTORev));
        merchantWalletPODao.savePassbookDetailsByPassbook(passbookPODTORev);
        
        double MerfinalBal=Double.parseDouble(merchantWalletPODTORev.getFinalBalance());
        String amt=Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
        double totalFinal=Double.parseDouble(amt)+MerfinalBal;
        merchantWalletPODTORev.setFinalBalance(String.valueOf(totalFinal));
        
        //merchantWalletPODTORev.setFinalBalance(String.valueOf(Double.parseDouble(merchantWalletPODTORev.getFinalBalance())+Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()))));
        logger.info("Before Wallet Update PAYOUT REVERSAL : " + new Gson().toJson(merchantWalletPODTORev));
        merchantWalletPODao.SaveAndUpdteWallet(merchantWalletPODTORev);




	}
}
