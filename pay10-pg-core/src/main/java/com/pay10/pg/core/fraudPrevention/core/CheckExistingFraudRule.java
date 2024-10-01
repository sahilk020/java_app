package com.pay10.pg.core.fraudPrevention.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.operations.Mult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.util.FraudPreventionObj;
import com.pay10.commons.util.FraudRuleType;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Harpreet,Rajendra
 *
 */

@Service
public class CheckExistingFraudRule {

	private static Logger logger = LoggerFactory.getLogger(CheckExistingFraudRule.class.getName());

	private ErrorType responseErrorType; // TODO need to review variable scope

	@Autowired
	private UserDao userDao;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	public ResponseObject exists(FraudPreventionObj fraudPrevention) {
	//	logger.info("CheckExistingFraudRule  "+fraudPrevention.getMerchantCurrency());
		ResponseObject response = new ResponseObject();

		// setting payId according to type of user
		String payId = fraudPrevention.getPayId();
		FraudRuleType fraudType = fraudPrevention.getFraudType();
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> payIdConditionList = new ArrayList<BasicDBObject>();
 		BasicDBObject idQuery= new BasicDBObject("payId", payId);
	 	BasicDBObject ruleQuery = new BasicDBObject();
		BasicDBObject fraudTypeQuery = new BasicDBObject();
		BasicDBObject timeUnitQuery = new BasicDBObject();
	 	BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
		paramConditionLst.add(activeTypeQuery);
		// Check PAY_ID with currency already exists or not
		String currency=fraudPrevention.getCurrency();
		BasicDBObject currencyObj=new BasicDBObject();
		List<String> currencyList=getCurrency(payId,currency);


		if (StringUtils.isNotBlank(fraudPrevention.getId())) {
			paramConditionLst.add(new BasicDBObject("_id", new BasicDBObject("$ne", fraudPrevention.getId())));
		}

		switch (fraudType) {
		case BLOCK_NO_OF_TXNS:
			ruleQuery.append("fraudType", fraudType);
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_TXN_AMOUNT:
			if (fraudPrevention.isFixedAmountFlag()) {
				ruleQuery.append("transactionAmount", fraudPrevention.getTransactionAmount());
			} else {
				ruleQuery.append("maxTransactionAmount", fraudPrevention.getMaxTransactionAmount());
			}
			timeUnitQuery.append("blockTimeUnits", fraudPrevention.getBlockTimeUnits());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_IP_ADDRESS:
			ruleQuery.append("ipAddress", fraudPrevention.getIpAddress());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_EMAIL_ID:
			ruleQuery.append("email", fraudPrevention.getEmail());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_USER_COUNTRY:
			ruleQuery.append("userCountry", new BasicDBObject("$regex", fraudPrevention.getUserCountry()));
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_CARD_ISSUER_COUNTRY:
			ruleQuery.append("issuerCountry", new BasicDBObject("$regex", fraudPrevention.getIssuerCountry()));
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_CARD_BIN:
			ruleQuery.append("negativeBin", fraudPrevention.getNegativeBin());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_CARD_NO:
			ruleQuery.append("negativeCard", fraudPrevention.getNegativeCard());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_CARD_TXN_THRESHOLD:
			fraudTypeQuery.append("fraudType", "BLOCK_CARD_TXN_THRESHOLD");
			ruleQuery.append("negativeCard", fraudPrevention.getNegativeCard());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_PHONE_NUMBER:
			ruleQuery.append("phone", fraudPrevention.getPhone());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_TXN_AMOUNT_VELOCITY:
			ruleQuery.append("userIdentifier", fraudPrevention.getUserIdentifier());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_MACK_ADDRESS:
			ruleQuery.append("mackAddress", fraudPrevention.getMackAddress());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case REPEATED_MOP_TYPES:
			ruleQuery.append("paymentType", fraudPrevention.getPaymentType());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case FIRST_TRANSACTIONS_ALERT:
			fraudTypeQuery.append("fraudType", "FIRST_TRANSACTIONS_ALERT");
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_USER_CITY:
			ruleQuery.append("city", new BasicDBObject("$regex", fraudPrevention.getCity()));
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_USER_STATE:
			ruleQuery.append("stateCode", new BasicDBObject("$regex", fraudPrevention.getStateCode()));
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL:
			fraudTypeQuery.append("fraudType", "BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL");
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_VPA_ADDRESS:
			ruleQuery.append("vpaAddress", fraudPrevention.getVpaAddress());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
 		default:
			logger.error("Something went wrong while checking fraud field values");
			break;
		}
		paramConditionLst.add(idQuery);
		logger.info("Get Currency Object for checkExisting Fraud Rule: "+ getCurrencyObject(payId,currency));
		paramConditionLst.add(getCurrencyObject(payId,currency));
		paramConditionLst.add(ruleQuery);
		paramConditionLst.add(timeUnitQuery);
	 	if (!fraudTypeQuery.isEmpty()) {
			paramConditionLst.add(fraudTypeQuery);
		}

		BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
		logger.info("Final ParamConditionList for exisitng fraud Rule: {}",finalquery);

		if (fraudPreventionMongoService.duplicateChecker(finalquery)) {
			logger.info(" FRAUD_RULE_EXIST   duplicateChecker");
			response.setResponseMessage(responseErrorType.getResponseMessage());
			response.setResponseCode(responseErrorType.getCode());
		} else {
			logger.info(" FRAUD_RULE_NOT_EXIST   duplicateChecker");
			response.setResponseMessage(ErrorType.FRAUD_RULE_NOT_EXIST.getResponseMessage());
			response.setResponseCode(ErrorType.FRAUD_RULE_NOT_EXIST.getCode());
		}
		return response;
	}
	
	public ResponseObject existscurrency(FraudPreventionObj fraudPrevention) {
		logger.info("CheckExistingFraudRule  ");
		ResponseObject response = new ResponseObject();
		// setting payId according to type of user
		String payId = fraudPrevention.getPayId();
		FraudRuleType fraudType = fraudPrevention.getFraudType();
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> payIdConditionList = new ArrayList<BasicDBObject>();
		BasicDBObject idQuery=new BasicDBObject("payId", payId);
		BasicDBObject ruleQuery = new BasicDBObject();
		BasicDBObject fraudTypeQuery = new BasicDBObject();
		BasicDBObject timeUnitQuery = new BasicDBObject();
		// Only verify the Active Rules
		BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
		paramConditionLst.add(activeTypeQuery);
		if (StringUtils.isNotBlank(fraudPrevention.getId())) {
			paramConditionLst.add(new BasicDBObject("_id", new BasicDBObject("$ne", fraudPrevention.getId())));
		}
		switch (fraudType) {
		case BLOCK_NO_OF_TXNS:
			ruleQuery.append("fraudType", fraudType);
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_TXN_AMOUNT:
			if (fraudPrevention.isFixedAmountFlag()) {
				ruleQuery.append("transactionAmount", fraudPrevention.getTransactionAmount());
			} else {
				ruleQuery.append("maxTransactionAmount", fraudPrevention.getMaxTransactionAmount());
			}
			timeUnitQuery.append("blockTimeUnits", fraudPrevention.getBlockTimeUnits());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_IP_ADDRESS:
			ruleQuery.append("ipAddress", fraudPrevention.getIpAddress());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_EMAIL_ID:
			ruleQuery.append("email", fraudPrevention.getEmail());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_USER_COUNTRY:
			ruleQuery.append("userCountry", new BasicDBObject("$regex", fraudPrevention.getUserCountry()));
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_CARD_ISSUER_COUNTRY:
			ruleQuery.append("issuerCountry", new BasicDBObject("$regex", fraudPrevention.getIssuerCountry()));
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_CARD_BIN:
			ruleQuery.append("negativeBin", fraudPrevention.getNegativeBin());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_CARD_NO:
			ruleQuery.append("negativeCard", fraudPrevention.getNegativeCard());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_CARD_TXN_THRESHOLD:
			fraudTypeQuery.append("fraudType", "BLOCK_CARD_TXN_THRESHOLD");
			ruleQuery.append("negativeCard", fraudPrevention.getNegativeCard());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_PHONE_NUMBER:
			ruleQuery.append("phone", fraudPrevention.getPhone());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_TXN_AMOUNT_VELOCITY:
			ruleQuery.append("userIdentifier", fraudPrevention.getUserIdentifier());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_MACK_ADDRESS:
			ruleQuery.append("mackAddress", fraudPrevention.getMackAddress());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case REPEATED_MOP_TYPES:
			ruleQuery.append("paymentType", fraudPrevention.getPaymentType());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case FIRST_TRANSACTIONS_ALERT:
			fraudTypeQuery.append("fraudType", "FIRST_TRANSACTIONS_ALERT");
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_USER_CITY:
			ruleQuery.append("city", new BasicDBObject("$regex", fraudPrevention.getCity()));
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_USER_STATE:
			ruleQuery.append("stateCode", new BasicDBObject("$regex", fraudPrevention.getStateCode()));
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL:
			fraudTypeQuery.append("fraudType", "BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL");
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		case BLOCK_VPA_ADDRESS:
			ruleQuery.append("vpaAddress", fraudPrevention.getVpaAddress());
			responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
			break;
		default:
			logger.error("Something went wrong while checking fraud field values");
			break;
		}
		
		//fraudTypeQuerycurrency.append("currency", fraudPrevention.getCurrency());
		paramConditionLst.add(idQuery);
		paramConditionLst.add(getCurrencyObject(payId, fraudPrevention.getCurrency()));
		//paramConditionLst.add(fraudTypeQuerycurrency);
		paramConditionLst.add(ruleQuery);
		paramConditionLst.add(timeUnitQuery);

		if (!fraudTypeQuery.isEmpty()) {
			paramConditionLst.add(fraudTypeQuery);
		}

		BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
		if (fraudPreventionMongoService.duplicateChecker(finalquery)) {
			logger.info("after CheckExistingFraudRule   duplicateChecker");
			response.setResponseMessage(responseErrorType.getResponseMessage());
			response.setResponseCode(responseErrorType.getCode());
		} else {
			logger.info(" FRAUD_RULE_NOT_EXIST   duplicateChecker");
			response.setResponseMessage(ErrorType.FRAUD_RULE_NOT_EXIST.getResponseMessage());
			response.setResponseCode(ErrorType.FRAUD_RULE_NOT_EXIST.getCode());
		}
		return response;
	}



	public BasicDBObject getCurrencyObject(String id,String currency){
		logger.info(id+ "in currency Object "+currency);
		List<String> currencyList=getCurrency(id,currency);
		if(!currencyList.isEmpty()){
			if(currencyList.size()==1) {
				return new BasicDBObject("currency",currencyList.get(0));
			}
			else{
				return new BasicDBObject("$or", getCurrencyListObj(currencyList));
			}
		}
		else {
			logger.info("!!Currency list is Empty!!");
			return new BasicDBObject();
		}
	}
	public List<String> getCurrency(String payId,String currency){
		String[] currencyReplace = (currency.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "")).split(",");
		List<String> currencies=new ArrayList<>();
		if(payId.equalsIgnoreCase("All")){
			for(String curr: currencyReplace) {
				if(curr.equalsIgnoreCase("All")){
					List<MultCurrencyCode> multCurrencyCodes = multCurrencyCodeDao.getCurrencyCode();
					for (MultCurrencyCode multCurrencyCode : multCurrencyCodes) {
						logger.info("######curr#########"+multCurrencyCode.getCode());
						currencies.add(multCurrencyCode.getCode());
					}
				}
				else{
					logger.info("######curr#########"+curr);

					currencies.add(curr);
				}
			}
		}
		else{
			for(String curr: currencyReplace) {
				if(curr.equalsIgnoreCase("All")){
					List<String> multCurrencyCodes = userDao.findCurrencyByPayId(payId);
					for (String multCurrencyCode : multCurrencyCodes) {
						logger.info("######curr#########"+multCurrencyCode);

						currencies.add(multCurrencyCode);
					}
				}
				else{
					logger.info("######curr#########"+curr);
					currencies.add(curr);
				}
			}
		}
		return currencies;
	}


	public List<BasicDBObject> getCurrencyListObj(List<String> currencyList){
		List<BasicDBObject> finalList=new ArrayList<>();
		if(!currencyList.isEmpty()){
			for(int i=0;i<currencyList.size();i++){
				finalList.add(new BasicDBObject("currency",currencyList.get(i)));
			}
			return finalList;
		}
		return new ArrayList<>();
	}

}
