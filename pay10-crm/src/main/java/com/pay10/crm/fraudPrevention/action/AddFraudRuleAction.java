package com.pay10.crm.fraudPrevention.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.FraudPreventionObj;
import com.pay10.commons.util.FraudRuleType;
import com.pay10.commons.util.Helper;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.fraudPrevention.actionBeans.FraudRuleCreator;
import com.pay10.pg.core.fraudPrevention.model.FraudRuleModel;

/**
 * @author Harpreet, Rahul , Rajendra
 *
 */
public class AddFraudRuleAction extends AbstractSecureAction implements ModelDriven<FraudRuleModel> {

	@Autowired
	private FraudRuleCreator ruleCreator;

	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(AddFraudRuleAction.class.getName());

	private static final long serialVersionUID = 1676422870817349417L;
	private FraudRuleModel fraudRuleModel = new FraudRuleModel();

	@Autowired
	private ResponseObject responseObject;

	@Autowired
	private AuditTrailService auditTrailService;

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {

		try {
			//logger.info("************************************************");

			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			String createdBy = sessionUser.getEmailId().toString();

			logger.info("@addFraudRule Currency:{} " , fraudRuleModel.getCurrency());
			//JSONObject obj=new JSONObject(fraudRuleModel.getMerchantCurrency());
		//	logger.info("After Parse{} ",obj);
		//	JSONArray array=obj.toJSONArray(obj);

			logger.info("************************************************");
		 	//Object obj= new JSONParser().parse(fraudRuleModel.getMerchantCurrency());
			// logger.info(String.valueOf(fraudRuleModel.getMerchantCurrency().charAt(0)));
			responseObject = ruleCreator.createFraudRule(fraudRuleModel, createdBy, sessionUser);
			logger.info("enter validation" + "IpAddress");

			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(fraudRuleModel), null,
					actionMessagesByAction.get("addFraudRule"));
			auditTrailService.saveAudit(request, auditTrail);
			fraudRuleModel.setResponseCode(responseObject.getResponseCode());
			fraudRuleModel.setResponseMsg(responseObject.getResponseMessage());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Fraud Prevention System - Exception :" + exception);
			return ERROR;
		}
	}

	@Override
	public FraudRuleModel getModel() {
		return fraudRuleModel;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void validate() {
		logger.info("************************************************");

		logger.info("enter validation" + "IpAddress");

		if ((validator.validateBlankField(fraudRuleModel.getPayId()))) {
			addFieldError(CrmFieldType.PAY_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
		// custom validation for payId
		else if (!fraudRuleModel.getPayId().equalsIgnoreCase(CrmFieldConstants.ALL.getValue())
				&& !validator.validateField(CrmFieldType.PAY_ID, fraudRuleModel.getPayId())) {
			addFieldError(CrmFieldType.PAY_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
		if (validator.validateBlankField(fraudRuleModel.getFraudType())) {
			addFieldError(null, ErrorType.COMMON_ERROR.getResponseMessage());
		}
		//if(validator.validateBlankField(fraudRuleModel.getMerchantCurrency())){
		//	addFieldError(CrmFieldType.CURRENCY.getName(), validator.getResonseObject().getResponseMessage());
		//}
		logger.info("************************************************");


		FraudRuleType fraudType = FraudRuleType.getInstance(fraudRuleModel.getFraudType());
		String[] currencyReplace = (fraudRuleModel.getCurrency().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "")).split(",");

		switch (fraudType) {
		case BLOCK_IP_ADDRESS:
			if (validator.validateBlankField(fraudRuleModel.getIpAddress())) {
				addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
				break;
			}
			Collection<String> ipAddressList = Helper.parseFields(fraudRuleModel.getIpAddress());
			for (String oneIpAddress : ipAddressList) {
				if (!validator.validateField(CrmFieldType.FRAUD_IP_ADDRESS, oneIpAddress)) {
					addFieldError(CrmFieldType.FRAUD_IP_ADDRESS.getName(),
							validator.getResonseObject().getResponseMessage());
					logger.info("LIST ITERATOR FOR IpAddress" + validator.getResonseObject().getResponseMessage());
					break;
				}
			}

			break;
		case BLOCK_USER_COUNTRY:
			if (validator.validateBlankField(fraudRuleModel.getUserCountry())) {
				addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
				break;
			}
			Collection<String> userCountryList = Helper.parseFields(fraudRuleModel.getUserCountry());
			for (String oneUserCountry : userCountryList) {
				if (!validator.validateField(CrmFieldType.FRAUD_USER_COUNTRY, oneUserCountry)) {
					addFieldError(CrmFieldType.FRAUD_USER_COUNTRY.getName(),
							validator.getResonseObject().getResponseMessage());
					break;
				}
			}
			break;
		case BLOCK_CARD_BIN:
			if (validator.validateBlankField(fraudRuleModel.getNegativeBin())) {
				addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
				break;
			}
			Collection<String> blockCardBinList = Helper.parseFields(fraudRuleModel.getNegativeBin());
			for (String oneCardBin : blockCardBinList) {
				if (!validator.validateField(CrmFieldType.FRAUD_NEGATIVE_BIN, oneCardBin)) {
					addFieldError(CrmFieldType.FRAUD_NEGATIVE_BIN.getName(),
							validator.getResonseObject().getResponseMessage());
					break;
				}
			}

			break;
		case BLOCK_CARD_ISSUER_COUNTRY:
			if (validator.validateBlankField(fraudRuleModel.getIssuerCountry())) {
				addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
				break;
			}
			Collection<String> blockCardIssuerList = Helper.parseFields(fraudRuleModel.getIssuerCountry());
			for (String oneCardIssuer : blockCardIssuerList) {
				if (!validator.validateField(CrmFieldType.FRAUD_ISSUER_COUNTRY, oneCardIssuer)) {
					addFieldError(CrmFieldType.FRAUD_ISSUER_COUNTRY.getName(),
							validator.getResonseObject().getResponseMessage());
					break;
				}
			}

			break;
		case BLOCK_CARD_NO:
			if (validator.validateBlankField(fraudRuleModel.getNegativeCard())) {
				addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
				break;
			}
			Collection<String> blockCardNoList = Helper.parseFields(fraudRuleModel.getNegativeCard());
			for (String oneCardIssuer : blockCardNoList) {
				if (oneCardIssuer.length() == 16) {
					break;
				} else {
					addFieldError(CrmFieldType.FRAUD_NEGATIVE_CARD.getName(),
							validator.getResonseObject().getResponseMessage());
					break;
				}
			}

			break;
		case BLOCK_EMAIL_ID:
			if (validator.validateBlankField(fraudRuleModel.getEmail())) {
				addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
				break;
			}
			Collection<String> blockEmailIDList = Helper.parseFields(fraudRuleModel.getEmail());
			for (String oneEmailId : blockEmailIDList) {
				if (!validator.isValidEmailId(oneEmailId)) {
					addFieldError(CrmFieldType.FRAUD_EMAIL.getName(),
							validator.getResonseObject().getResponseMessage());
					break;
				}
			}

			break;
		case BLOCK_PHONE_NUMBER:
			if (validator.validateBlankField(fraudRuleModel.getPhone())) {
				addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
				break;
			}
			Collection<String> blockPhoneList = Helper.parseFields(fraudRuleModel.getPhone());
			for (String onePhone : blockPhoneList) {
				if (!validator.isValidPhoneNumber(onePhone, CrmFieldType.CUST_PHONE)) {
					addFieldError(CrmFieldType.CUST_PHONE.getName(), validator.getResonseObject().getResponseMessage());
					break;
				}
			}

			break;
		case BLOCK_TXN_AMOUNT_VELOCITY:
			for(String currency: currencyReplace) {
				if (!validator.validateField(CrmFieldType.FRAUD_CURRENCY, currency)) {
					addFieldError(CrmFieldType.FRAUD_CURRENCY.getName(), validator.getResonseObject().getResponseMessage());
				}
			}
				if (validator.validateBlankField(fraudRuleModel.getRepetationCount())) {
					addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
					break;
				}
				break;

		case BLOCK_CARD_TXN_THRESHOLD:
			if (validator.validateBlankField(fraudRuleModel.getPerCardTransactionAllowed())) {
				addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
				break;
			}
			if (!validator.validateField(CrmFieldType.FRAUD_MIN_TRANSACTION_AMOUNT,
					fraudRuleModel.getPerCardTransactionAllowed())) {
				addFieldError(CrmFieldType.FRAUD_MIN_TRANSACTION_AMOUNT.getName(),
						validator.getResonseObject().getResponseMessage());
			}

			break;
		case BLOCK_NO_OF_TXNS:

			if (!validator.validateField(CrmFieldType.FRAUD_MINUTE_TXN_LIMIT, fraudRuleModel.getMinutesTxnLimit())) {
				addFieldError(CrmFieldType.FRAUD_MINUTE_TXN_LIMIT.getName(),
						validator.getResonseObject().getResponseMessage());
			}
			break;
		case BLOCK_TXN_AMOUNT:
		 	for(String currency: currencyReplace) {
				if (!validator.validateField(CrmFieldType.FRAUD_CURRENCY, currency)) {
					addFieldError(CrmFieldType.FRAUD_CURRENCY.getName(), validator.getResonseObject().getResponseMessage());
				}

			if (!fraudRuleModel.isFixedAmountFlag()) {
				if (!(validator.validateField(CrmFieldType.FRAUD_MIN_TRANSACTION_AMOUNT,
						fraudRuleModel.getMinTransactionAmount()) && !fraudRuleModel.isFixedAmountFlag())) {
					addFieldError(CrmFieldType.FRAUD_MIN_TRANSACTION_AMOUNT.getName(),
							validator.getResonseObject().getResponseMessage());
				}
				if (!(validator.validateField(CrmFieldType.FRAUD_MAX_TRANSACTION_AMOUNT,
						fraudRuleModel.getMaxTransactionAmount()) && !fraudRuleModel.isFixedAmountFlag())) {
					addFieldError(CrmFieldType.FRAUD_MAX_TRANSACTION_AMOUNT.getName(),
							validator.getResonseObject().getResponseMessage());
				}
				if (!(Float.parseFloat(fraudRuleModel.getMaxTransactionAmount()) >= Float
						.parseFloat(fraudRuleModel.getMinTransactionAmount()))) {
					addFieldError(
							CrmFieldType.FRAUD_MIN_TRANSACTION_AMOUNT.getName() + Constants.COMMA.getValue()
									+ CrmFieldType.FRAUD_MAX_TRANSACTION_AMOUNT.getName(),
							validator.getResonseObject().getResponseMessage());
				}
				List<FraudPreventionObj> fRules = fraudPreventionMongoService
						.fraudPreventionListbyRulecurrency(fraudRuleModel.getPayId(), fraudType.getValue(),currency);
				long count = fRules.stream().filter(rule -> StringUtils.isNotBlank(rule.getMaxTransactionAmount()))
						.count();
				if (count > 0) {
					addFieldError(
							CrmFieldType.FRAUD_MIN_TRANSACTION_AMOUNT.getName() + Constants.COMMA.getValue()
									+ CrmFieldType.FRAUD_MAX_TRANSACTION_AMOUNT.getName(),
							ErrorType.FRAUD_RULE_ALREADY_EXISTS.getResponseMessage());
				}
			} else {
				List<FraudPreventionObj> fRules = fraudPreventionMongoService
						.fraudPreventionListbyRulecurrency(fraudRuleModel.getPayId(), fraudType.getValue(),currency);
				long count = fRules.stream()
						.filter(rule -> (!StringUtils.equalsIgnoreCase(rule.getId(), fraudRuleModel.getId()))
								&& rule.getTransactionAmount() > 0 && rule.isFixedAmountFlag()
								&& StringUtils.equals(rule.getBlockTimeUnits(), fraudRuleModel.getBlockTimeUnits()))
						.count();
				if (count > 0) {
					addFieldError(
							CrmFieldType.FRAUD_MIN_TRANSACTION_AMOUNT.getName() + Constants.COMMA.getValue()
									+ CrmFieldType.FRAUD_MAX_TRANSACTION_AMOUNT.getName(),
							ErrorType.FRAUD_RULE_ALREADY_EXISTS.getResponseMessage());
					}
				}
			 }
			break;
		case BLOCK_MACK_ADDRESS:

			if (!validator.validateField(CrmFieldType.FRAUD_MACK_ADDRESS, fraudRuleModel.getMackAddress())) {
				addFieldError(CrmFieldType.FRAUD_MACK_ADDRESS.getName(),
						validator.getResonseObject().getResponseMessage());
			}
			if (!validator.isValidMackAddress(fraudRuleModel.getMackAddress())) {
				addFieldError(CrmFieldType.FRAUD_MACK_ADDRESS.getName(), "Please provide valid mack address.");
			}
			break;
		case FIRST_TRANSACTIONS_ALERT:
			if (validator.validateBlankField(fraudRuleModel.getTransactionAmount())) {
				addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
			}
			if (validator.validateBlankField(fraudRuleModel.getEmailToNotify())) {
				addFieldError(null, ErrorType.INVALID_FIELD.getResponseMessage());
			}
			break;
		}
		logger.info("************************************************");

	}
}