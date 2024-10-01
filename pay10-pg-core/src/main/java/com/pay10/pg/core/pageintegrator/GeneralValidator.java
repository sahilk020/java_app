package com.pay10.pg.core.pageintegrator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldFormatType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.UserAccountServices;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessorValidatorFactory;

@Service
public class GeneralValidator implements Validator {

	@Autowired
	private ProcessorValidatorFactory processorValidatorFactory;

	@Autowired
	private Hasher hasher;
	
	@Autowired
	private UserAccountServices userAccountServices;

	private static Logger logger = LoggerFactory.getLogger(GeneralValidator.class.getName());
    public static final String emailRegex = "^[_A-Za-z0-9+-.]+(\\.[_A-Za-z0-9+-.]+)*@[_A-Za-z0-9+-]+(\\.[A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)";
	public static final String upiAddressRegex = "^[a-zA-Z0-9-.@]*$";
	public static final Pattern numberPattern = Pattern.compile(".*\\D.*");
	public static final Pattern alphaNumPattern = Pattern.compile("^[[:alnum:]]*$");
	public static final String alphaWithWhiteSpace = "([a-zA-Z]+\\s+)*[a-zA-Z]+";
	public static final String urlRegex = "^(https?|http?|www.?)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	public static final String DATE_FORMAT = "yyyyMMdd";
	public static final int MAX_YEAR = 100;
	public static final int MAX_MONTH = 12;
	public static final int MIN_MONTH = 1;
	public final char REPLACEMENT_CHAR = ' ';
	public final char NUMBER_REPLACEMENT_CHAR = '0';
	public static final Map<String, FieldType> fieldTypeMap = FieldType.getFieldsMap();
	public static final Map<String, FieldType> mandatorySupportFields = FieldType.getMandatorSupportFields();
	public static final Map<String, FieldType> mandatoryRequestFields = FieldType.getMandatoryRequestFields();
	public static final Map<String, FieldType> mandatoryStatusRequestFields = FieldType.getMandatoryStatusRequestFields();
	public static final Map<String, FieldType> mandatoryRecoRequestFields = FieldType.getMandatoryRecoRequestFields();
	public static final Map<String, FieldType> currencyMap = new HashMap<>();

	public GeneralValidator() {
	}

	@Override
	public void validate(Fields fields) throws SystemException {
		// from field definitions to request fields
		backwardValidations(fields);

		// from request fields to field definitions
		forwardValidations(fields);

		customValidations(fields);

		// processorValidations(fields);
	}

	public void processorValidations(Fields fields) throws SystemException {

		// Do not do processor specific validations for new order transactions
		if (fields.get(FieldType.TXNTYPE.getName()).equals( // TODO... discuss
															// about the need
															// for acquirer
															// specific
															// validations for
															// refund and
															// capture
				TransactionType.NEWORDER.getName())
				|| fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.REFUND.getName())
				|| fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.CAPTURE.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.REFUNDRECO.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())){
			return;
		}
		// fields.put(FieldType.ACQUIRER_TYPE.getName(),
		// AcquirerType.getDefault(fields).getCode());
		Validator processorValidator = processorValidatorFactory.getInstance(fields);
		processorValidator.validate(fields);
	}

	public void backwardValidations(Fields fields) throws SystemException {
		String origTxnId = fields.get(FieldType.ORIG_TXN_ID.getName());
		if (null == origTxnId) {
			String txntype = fields.get(FieldType.TXNTYPE.getName());
			if (txntype.equals(TransactionType.STATUS.getName())) {
				// validate non support transactions
				validateMandatoryFields(fields, mandatoryStatusRequestFields);
				
			} else if (txntype.equals(TransactionType.RECO.getName()) || txntype.equals(TransactionType.REFUNDRECO.getName())){
				
				validateMandatoryFields(fields, mandatoryRecoRequestFields);
			}
			else {
				validateMandatoryFields(fields, mandatoryRequestFields);
			}
		} else {
			// Validate support transactions
			validateMandatoryFields(fields, mandatorySupportFields);
		}
	}

	public void validateMandatoryFields(Fields fields, Map<String, FieldType> fieldTypes) throws SystemException {
		for (FieldType fieldType : fieldTypes.values()) {
			String key = fieldType.getName();
			if (StringUtils.isBlank(fields.get(key))) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, fieldType.getName() + " is a required field");
			}
			validateField(fieldTypeMap.get(key), key, fields);
		}
	}

	public void customValidations(Fields fields) throws SystemException {

		validateTransactionType(fields);

		validateMopType(fields);

		// comment by puneet as customValidationfunction is called by every acq
		// processor
		// validateCardNumber(fields);

		validateExpiryDate(fields);

		boolean emitra = fields.contains("IS_EMITRA");
		if (emitra) {
			fields.remove("IS_EMITRA");
			validateRequeryHash(fields);
			if (fields.contains(FieldType.AMOUNT.getName())) {
				long amount = fields.get(FieldType.AMOUNT.getName()) == null ? 0
						: Math.round(Double.valueOf(fields.get(FieldType.AMOUNT.getName())) * 100);
				fields.put(FieldType.AMOUNT.getName(), String.valueOf(amount));
				fields.put("IS_EMITRA", "true");
			}
		} else {
			validateHash(fields);
		}

		validateCurrency(fields);
	}

	public void validateCurrency(Fields fields) throws SystemException {
		Currency.validateCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
	}

	public void validateExpiryDate(Fields fields) throws SystemException {
		String requestedDate = fields.get(FieldType.CARD_EXP_DT.getName());
		
		if (null == requestedDate) {
			return;
		}
		
		final Date date = new Date();
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
		String today = simpleDateFormat.format(date);
		int thisYear = Integer.parseInt(today.substring(0, 4));
		int thisMonth = Integer.parseInt(today.substring(4, 6));
		int requestedYear = Integer.parseInt(requestedDate.substring(2, 6));
		int requestedMonth = Integer.parseInt(requestedDate.substring(0, 2));

		// if requested year is less than present year
		if (requestedYear < thisYear) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CARD_EXP_DT.getName() + " = '"
					+ fields.get(FieldType.CARD_EXP_DT.getName()) + ", is not a valid value");
		} else if (requestedYear == thisYear && requestedMonth < thisMonth) {
			// if requested year is present year, but requested month is expired
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CARD_EXP_DT.getName() + " = '"
					+ fields.get(FieldType.CARD_EXP_DT.getName()) + ", is not a valid value");
		} else if (requestedYear > (thisYear + MAX_YEAR)) {
			// if requested year is too far in future
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CARD_EXP_DT.getName() + " = '"
					+ fields.get(FieldType.CARD_EXP_DT.getName()) + ", is not a valid value");
		} else if (requestedMonth < MIN_MONTH || requestedMonth > MAX_MONTH) {
			// If month range is invalid
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CARD_EXP_DT.getName() + " = '"
					+ fields.get(FieldType.CARD_EXP_DT.getName()) + ", is not a valid value");
		}
	}

	public void validateCardNumber(Fields fields) throws SystemException {
		
		String cardNumber = fields.get(FieldType.CARD_NUMBER.getName());
		
		// Validate card number length.
		
		boolean isValid = false;
		if (StringUtils.isNotBlank(cardNumber)) {
			validateNumber(new StringBuilder(cardNumber), FieldType.CARD_NUMBER, fields);
			int sum = 0;
			boolean alternate = false;
			for (int i = cardNumber.length() - 1; i >= 0; i--) {
				int n = Integer.parseInt(cardNumber.substring(i, i + 1));
				if (alternate) {
					n *= 2;
					if (n > 9) {
						n = (n % 10) + 1;
					}
				}
				sum += n;
				alternate = !alternate;
			}
			isValid = (sum % 10 == 0);
		}
		if (!isValid) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CARD_NUMBER.getName() + " = '"
					+ fields.get(FieldType.CARD_NUMBER.getName()) + ", is not a valid value");
		}
	}

	public void validateMopType(Fields fields) throws SystemException {

		if (null == fields.get(FieldType.MOP_TYPE.getName())) {
			return;
		}

		MopType[] mopTypes = MopType.values();
		boolean flag = true;
		for (MopType mopType : mopTypes) {
			if (fields.get(FieldType.MOP_TYPE.getName()).equals(mopType.getCode())) {
				flag = false;
				break;
			}
		}
		if (flag) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.MOP_TYPE.getName() + " = '"
					+ fields.get(FieldType.MOP_TYPE.getName()) + ", is not a valid value");
		}
	}

	public void validateTransactionType(Fields fields) throws SystemException {
		String requestTransactionType = fields.get(FieldType.TXNTYPE.getName());
		if (null == requestTransactionType) {
			return;
		}

		TransactionType[] transactionTypes = TransactionType.values();
		boolean flag = true;
		for (TransactionType transactionType : transactionTypes) {
			if (requestTransactionType.equals(transactionType.getName()) ) {
				flag = false;
				break;
			}
		}

		if (flag) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.TXNTYPE.getName() + " = '"
					+ fields.get(FieldType.TXNTYPE.getName()) + ", is not a valid value");
		}
	}

	public void forwardValidations(Fields fields) throws SystemException {

			Iterator<Map.Entry<String, String>> iterator =  fields.getFields().entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String,String> entry = iterator.next();
				validateField(fieldTypeMap.get(entry.getKey()), entry.getKey(), fields);
			}
			
		}
	

	public void validateHash(Fields fields) throws SystemException {
		logger.info("HASH CHECK Field "+ fields.getFieldsAsString());
		// Do not do hash validation, it has already been done
		String validateFlag = fields.get(FieldType.INTERNAL_VALIDATE_HASH_YN.getName());
		if (null != validateFlag && validateFlag.equals("N")) {
			return;
		}

		// Hash sent by merchant in request
		String merchantHash = fields.remove(FieldType.HASH.getName());
		logger.info("HASH remove "+ merchantHash);
		if (StringUtils.isEmpty(merchantHash)) {
			handleInvalidHash(fields);
		}

		String calculatedHash = Hasher.getHash(fields);
		if (!calculatedHash.equals(merchantHash)) {
			StringBuilder hashMessage = new StringBuilder("Merchant hash =");
			hashMessage.append(merchantHash);
			hashMessage.append(", Calculated Hash=");
			hashMessage.append(calculatedHash);
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
			logger.error(hashMessage.toString());
			if (fields.get(FieldType.TXNTYPE.getName()) != null
					&& (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.STATUS.getName()) || fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.PO_STATUS.getName()))) {
				fields.put(FieldType.RESPONSE_CODE.getName(),ErrorType.INVALID_HASH.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.INVALID_HASH.getInternalMessage());
			}else {
				handleInvalidHash(fields);
			}
		}
	}

	/**
	 * This method is added for rectify the special hash logic of e-mitra.
	 */
	public void validateHash(Fields fields, boolean customFieldsHash) throws SystemException {

		// Do not do hash validation, it has already been done
		String validateFlag = fields.get(FieldType.INTERNAL_VALIDATE_HASH_YN.getName());
		if (null != validateFlag && validateFlag.equals("N")) {
			return;
		}
		// Hash sent by merchant in request
		String merchantHash = fields.remove(FieldType.HASH.getName());
		if (StringUtils.isEmpty(merchantHash)) {
			handleInvalidHash(fields);
		}
		
		Map<String, String> customFields = new HashMap<>();
		String[] configuredFields = PropertiesManager.propertiesMap.get("CONFIGURABLE_FIELDS").split(",");
		logger.info("configuredFields..={}", configuredFields.toString());
		
		for (String configField :configuredFields) {
			customFields.put(configField, fields.get(configField));
		}
		String payId = fields.get(FieldType.PAY_ID.getName());
		String key = userAccountServices.generateMerchantHostedEncryptionKey(payId);
		String calculatedHash = Hasher.getHash(customFields, key);
		if (!calculatedHash.equals(merchantHash)) {
			StringBuilder hashMessage = new StringBuilder("Merchant hash =");
			hashMessage.append(merchantHash);
			hashMessage.append(", Calculated Hash=");
			hashMessage.append(calculatedHash);
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
			logger.error(hashMessage.toString());
			handleInvalidHash(fields);
		}
	}
	
	/**
	 * This method is added for rectify the special hash logic of e-mitra for re-query API.
	 */
	public void validateRequeryHash(Fields fields) throws SystemException {

		// Do not do hash validation, it has already been done
		String validateFlag = fields.get(FieldType.INTERNAL_VALIDATE_HASH_YN.getName());
		if (null != validateFlag && validateFlag.equals("N")) {
			return;
		}
		// Hash sent by merchant in request
		String merchantHash = fields.remove(FieldType.HASH.getName());
		if (StringUtils.isEmpty(merchantHash)) {
			handleInvalidHash(fields);
		}
		
		String payId = fields.get(FieldType.PAY_ID.getName());
		String key = userAccountServices.generateMerchantHostedEncryptionKey(payId);
		String calculatedHash = Hasher.getHash(fields.getFields(), key);
		if (!calculatedHash.equals(merchantHash)) {
			StringBuilder hashMessage = new StringBuilder("Merchant hash =");
			hashMessage.append(merchantHash);
			hashMessage.append(", Calculated Hash=");
			hashMessage.append(calculatedHash);
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
			logger.error(hashMessage.toString());
			handleInvalidHash(fields);
		}
	}
	
	public void handleInvalidHash(Fields fields) throws SystemException {
		if (ConfigurationConstants.IS_DEBUG.getValue().equals(Constants.TRUE.getValue())
				&& ConfigurationConstants.ALLOW_FAILED_HASH.getValue().equals(Constants.TRUE.getValue())) {
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
			logger.warn("Hash failed, continuing in debug mode!");
			return;
		}

		fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "Y");

		throw new SystemException(ErrorType.INVALID_HASH, "Invalid " + FieldType.HASH.getName());
	}

	public boolean isValidEmailId(String email) {
		if (email.matches(emailRegex)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isValidUpiAddress(String upiAddress) {
		if (upiAddress.matches(upiAddressRegex)) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("incomplete-switch")
	public void validateField(FieldType fieldType, String key, Fields fields) throws SystemException {

		String valueKey = fields.get(key);

		StringBuilder value = new StringBuilder(valueKey);

		if (null == fieldType) {
			// Check if validations are set for the requested field
			throw new SystemException(ErrorType.VALIDATION_FAILED, "No such field defined, field = " + key);
		} 

		// Validate type
		switch (fieldType.getType()) {
		case ALPHA:
			validateAlpha(value, fieldType, fields);
			break;

		case NUMBER:
			validateNumber(value, fieldType, fields);
			break;

		case ALPHANUM:
			validateAlphaNum(value, fieldType, fields);
			break;

		case SPECIAL:
			validateSpecialChar(value, fieldType, fields);
			break;
			
		case ORDER_ID_TYPE:
			validateOrderId(value, fieldType, fields);
			break;
			
		case BOBSPECIAL:
			validateBobSpecialChar(value, fieldType, fields);
			break;	
		case DIRECPAYSPECIAL:
			validateDirecpaySpecialChar(value, fieldType, fields);
			break;	
		case ISGPAYSPECIAL:
			validateISGPAYSpecialChar(value, fieldType, fields);
			break;
		case GOOGLEPAYSPECIAL:
			validateGooglePaySpecialChar(value, fieldType, fields);
			break;
		case PAYTMSPECIAL:
			validatePaytmSpecialChar(value, fieldType, fields);
			break;	
			
		case URL:
			// TODO: Put validations for urls
			break;

		case NONE:
			// Ignore
			break;
		case AMOUNT:
			validateAmount(value, fieldType, fields);
			break;
		case EMAIL:
			if (!isValidEmailId(value.toString())) {
				if (fieldType.isSpecialCharReplacementAllowed()) {
					fields.remove(fieldType.getName());
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + key);
				}
			}
			break;
		
		case PHONE:
			if (!isValidPhoneNumber(valueKey, fieldType)) {
				logger.error("Invalid CUST_PHONE : " + valueKey);
				fields.remove(fieldType.getName());
			}
			return;
			
		case UPIADDRESS:
			if (!isValidUpiAddress(value.toString())) {
				if (fieldType.isSpecialCharReplacementAllowed()) {
					fields.remove(fieldType.getName());
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + key);
				}
			}
			break;	
		}
		
		if(!fields.contains(key) && !fieldType.isRequired()) {
			return;
		}
		value = new StringBuilder(fields.get(key));
		if (value.length() < fieldType.getMinLength()) {

			// Tolerate if field optional
			if (!fieldType.isRequired()) {
				return;
			}

			if (fieldType.isSpecialCharReplacementAllowed()) {
				appendDefaultValue(value, fieldType);
			} else {
				throw new SystemException(ErrorType.VALIDATION_FAILED,
						"Minimum length of '" + key + "' is " + fieldType.getMinLength());
			}
		} else if (value.length() > fieldType.getMaxLength()) {

			if (fieldType.isSpecialCharReplacementAllowed()) {
				value.setLength(fieldType.getMaxLength());
				fields.put(fieldType.getName(), value.toString());
			} else {
				throw new SystemException(ErrorType.VALIDATION_FAILED,
						"Maximum length of '" + key + "' is " + fieldType.getMaxLength());
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	public void validateField(FieldType fieldType, String key, String value) throws SystemException {
		if(StringUtils.isBlank(value)) {
			return;
		}
		StringBuilder request = new StringBuilder(value);
		if (null == fieldType) {
			// Check if validations are set for the requested field
			throw new SystemException(ErrorType.VALIDATION_FAILED, "No such field defined, field = " + key);
		}

		// Validate type
		switch (fieldType.getType()) {
		case ALPHA:
			validateAlpha(value, fieldType);
			break;

		case NUMBER:
			validateNumber(value, fieldType);
			break;

		case ALPHANUM:
			validateAlphaNum(value, fieldType);
			break;

		case SPECIAL:
			validateSpecialChar(value, fieldType);
			break;
			
		case URL:
			// TODO: Put validations for urls
			break;

		case NONE:
			// Ignore
			break;
		case AMOUNT:
			validateAmount(value, fieldType,null);
			break;
		case EMAIL:
			if (!isValidEmailId(value.toString())) {
				if (fieldType.isSpecialCharReplacementAllowed()) {
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + key);
				}
			}
			break;
		
		case PHONE:
			if (!isValidPhoneNumber(value, fieldType)) {
				logger.error("Invalid CUST_PHONE : " + value);
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + key);
			}
			return;
		
		case DECIMAL_AMOUNT:
			validateDecimalAmount(value, fieldType,null);
		break;
		}
		if (value.length() < fieldType.getMinLength()) {

			// Tolerate if field optional
			if (!fieldType.isRequired()) {
				return;
			}

			if (fieldType.isSpecialCharReplacementAllowed()) {
				appendDefaultValue(request, fieldType);
			} else {
				throw new SystemException(ErrorType.VALIDATION_FAILED,
						"Minimum length of '" + key + "' is " + fieldType.getMinLength());
			}
		} else if (value.length() > fieldType.getMaxLength()) {

			if (fieldType.isSpecialCharReplacementAllowed()) {
				request.setLength(fieldType.getMaxLength());
			} else {
				throw new SystemException(ErrorType.VALIDATION_FAILED,
						"Maximum length of '" + key + "' is " + fieldType.getMaxLength());
			}
		}
	}
	private boolean isValidPhoneNumber(String custPhone, FieldType fieldType) {
		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		int length = custPhone.length();
		int sum = 0;
		for (int index = 0; index < length; ++index) {
			char ch = custPhone.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				sum += Integer.parseInt(Character.toString(ch));
			} else if (ascii == '+' && index == 0) {
				
			}
			else {
				if (!fieldType.isSpecialCharReplacementAllowed()) {
					return false;
				}
			}
		}
		
		if(sum == 0) {
			return false;
		}
		
		if (custPhone.length() < fieldType.getMinLength()) {
			return false;
		} else if (custPhone.length() > fieldType.getMaxLength()) {
			return false;
		}
		
		return true;
	}

	public void appendDefaultValue(StringBuilder value, FieldType fieldType) {
		int lengthDiff = fieldType.getMinLength() - value.length();
		FieldFormatType fieldFormatType = fieldType.getType();
		switch (fieldFormatType) {
		case NUMBER:
			append(value, NUMBER_REPLACEMENT_CHAR, lengthDiff);
			break;
		default:
			append(value, REPLACEMENT_CHAR, lengthDiff);
		}
	}

	public void append(StringBuilder value, char inputChar, int length) {
		for (int index = 0; index < length; ++index) {
			value.append(inputChar);
		}
	}

	public void validateAlpha(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {
		StringBuilder value = new StringBuilder(request);
		String result = validateAlpha(value.toString(), fieldType);
		fields.put(fieldType.getName(), result);
	}

	public String validateAlpha(String request, FieldType fieldType) throws SystemException {
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final int UNDERSCORE = '_';

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
				// allow capital alphabets
				continue;
			} else if (ascii >= ALPHA_START && ascii <= ALPHA_END) {
				// allow small char alphabets
				continue;
			} else if (ascii == UNDERSCORE) {
				// allow small char alphabets
				continue;
			} else {
				if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + fieldType.getName());
				}
			}
		}
		return value.toString().replace(" ", "");
	}

	public void validateAlphaNum(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {
		StringBuilder value = new StringBuilder(request);
		String result = validateAlphaNum(request.toString(), fieldType);
		fields.put(fieldType.getName(), result);
	}
	public String validateAlphaNum(String request, FieldType fieldType) throws SystemException {
		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				continue;
			} else if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
				// allow capital alphabets
				continue;
			} else if (ascii >= ALPHA_START && ascii <= ALPHA_END) {
				// allow small char alphabets
				continue;
			} else {
				if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + fieldType.getName());
				}
			}
		}
		return value.toString().replace(" ", "");
	}

	public void validateNumber(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {
		StringBuilder value = new StringBuilder(request);
		String result = validateNumber(request.toString(), fieldType);
		fields.put(fieldType.getName(), result);
	}// validateNumber()

	public String validateNumber(String request, FieldType fieldType) throws SystemException {

		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				continue;
			} else {
				if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + fieldType.getName());
				}
			}
		}
		return value.toString().replace(" ", "");
	}
	public void validateAmount(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {
		validateAmount(request.toString(), fieldType,fields);
		// To remove leading zeros
		BigDecimal amountInPaisa = new BigDecimal(fields.get(FieldType.AMOUNT.getName()));
		fields.put(FieldType.AMOUNT.getName(), amountInPaisa.toString()); 
				

	}
	public void validateAmount(String request, FieldType fieldType,Fields fields) throws SystemException {
		if (StringUtils.isBlank(request.toString().replace("0", ""))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + fieldType.getName());
		}
		validateDecimalAmount(request, fieldType,fields);
	}
	
	public void validateDecimalAmount(String request, FieldType fieldType,Fields fields) throws SystemException {
		//TODO add dynamic currency code
		String amount = Amount.removeDecimalAmount(request, fields.get(FieldType.CURRENCY_CODE.getName()));
		if (StringUtils.isBlank(amount.toString().replace("0", ""))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + fieldType.getName());
		}
		validateNumber(amount, fieldType);
	}
	public void validateSpecialChar(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {

		validateSpecialChar(request.toString(), fieldType);
		fields.put(fieldType.getName(), request.toString());
	}// validateSpecialChar()

	public void validateSpecialChar(String request, FieldType fieldType) throws SystemException {
		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final char[] permittedSpecialChars = {' ','{','}','&',';','@', ',', '-', '_', '+', '/', '=', '*', '.', ':', '\n', '\r', '?','\'', '(', ')','|', '#', '$'};

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				// allow numbers
				continue;
			} else if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
				// allow capital alphabets
				continue;
			} else if (ascii >= ALPHA_START && ascii <= ALPHA_END) {
				// allow small char alphabets
				continue;
			} else {
				boolean foundFlag = false;
				// allow permitted special chars
				for (char specialChar : permittedSpecialChars) {
					if (specialChar == ch) {
						foundFlag = true;
						break;
					}
				}

				if (foundFlag) {
					continue;
				} else if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
					break;
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName() + ", Invalid char found = '" + ch + "'");
				}

			}
		}
	}
	
	public void validateOrderId(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {

		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final char[] permittedSpecialChars = {' ','-', '_', '+', '.'};

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				// allow numbers
				continue;
			} else if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
				// allow capital alphabets
				continue;
			} else if (ascii >= ALPHA_START && ascii <= ALPHA_END) {
				// allow small char alphabets
				continue;
			} else {
				boolean foundFlag = false;
				// allow permitted special chars
				for (char specialChar : permittedSpecialChars) {
					if (specialChar == ch) {
						foundFlag = true;
						break;
					}
				}

				if (foundFlag) {
					continue;
				} else if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
					break;
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName() + ", Invalid char found = '" + ch + "'");
				}

			}
		}

		fields.put(fieldType.getName(), value.toString());
	}
	
	
	public void validateDirecpaySpecialChar(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {

		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final char[] permittedSpecialChars = {' ','~','$','{','}','(',')','&',';','@', ',', '-', '_', '+', '/', '=', '*', '.', ':','|', '\n', '\r', '?','\''};

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				// allow numbers
				continue;
			} else if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
				// allow capital alphabets
				continue;
			} else if (ascii >= ALPHA_START && ascii <= ALPHA_END) {
				// allow small char alphabets
				continue;
			} else {
				boolean foundFlag = false;
				// allow permitted special chars
				for (char specialChar : permittedSpecialChars) {
					if (specialChar == ch) {
						foundFlag = true;
						break;
					}
				}

				if (foundFlag) {
					continue;
				} else if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
					break;
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName() + ", Invalid char found = '" + ch + "'");
				}

			}
		}

		fields.put(fieldType.getName(), value.toString());
	}
	
	public void validatePaytmSpecialChar(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {

		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final char[] permittedSpecialChars = {'[',']',' ','~','$','{','}','&',';','@', ',', '-', '_', '+', '/', '=', '*', '.', ':','|', '\n', '\r', '?','\'','#'};

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				// allow numbers
				continue;
			} else if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
				// allow capital alphabets
				continue;
			} else if (ascii >= ALPHA_START && ascii <= ALPHA_END) {
				// allow small char alphabets
				continue;
			} else {
				boolean foundFlag = false;
				// allow permitted special chars
				for (char specialChar : permittedSpecialChars) {
					if (specialChar == ch) {
						foundFlag = true;
						break;
					}
				}

				if (foundFlag) {
					continue;
				} else if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
					break;
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName() + ", Invalid char found = '" + ch + "'");
				}

			}
		}

		fields.put(fieldType.getName(), value.toString());
	}
	public void validateISGPAYSpecialChar(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {

		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final char[] permittedSpecialChars = {' ', '(', ')','{','}','&',';','@', ',', '-', '_', '+', '/', '=', '*', '.', ':','|', '\n', '\r', '?','\''};

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				// allow numbers
				continue;
			} else if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
				// allow capital alphabets
				continue;
			} else if (ascii >= ALPHA_START && ascii <= ALPHA_END) {
				// allow small char alphabets
				continue;
			} else {
				boolean foundFlag = false;
				// allow permitted special chars
				for (char specialChar : permittedSpecialChars) {
					if (specialChar == ch) {
						foundFlag = true;
						break;
					}
				}

				if (foundFlag) {
					continue;
				} else if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
					break;
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName() + ", Invalid char found = '" + ch + "'");
				}

			}
		}

		fields.put(fieldType.getName(), value.toString());
	}
	
	public void validateBobSpecialChar(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {

		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final char[] permittedSpecialChars = { ' ', '@', ',', '-', '_', '+', '/', '=', '*', '.', ':', '\n', '\r', '?', '<', '>'};

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				// allow numbers
				continue;
			} else if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
				// allow capital alphabets
				continue;
			} else if (ascii >= ALPHA_START && ascii <= ALPHA_END) {
				// allow small char alphabets
				continue;
			} else {
				boolean foundFlag = false;
				// allow permitted special chars
				for (char specialChar : permittedSpecialChars) {
					if (specialChar == ch) {
						foundFlag = true;
						break;
					}
				}

				if (foundFlag) {
					continue;
				} else if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
					break;
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName() + ", Invalid char found = '" + ch + "'");
				}

			}
		}

		fields.put(fieldType.getName(), value.toString());
	}// validateSpecialChar()
	public void validateGooglePaySpecialChar(StringBuilder request, FieldType fieldType, Fields fields) throws SystemException {

		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final char[] permittedSpecialChars = { '+'};

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				// allow numbers
				continue;
			} else {
				boolean foundFlag = false;
				// allow permitted special chars
				for (char specialChar : permittedSpecialChars) {
					if (specialChar == ch) {
						foundFlag = true;
						break;
					}
				}

				if (foundFlag) {
					continue;
				} else if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
					break;
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName() + ", Invalid char found = '" + ch + "'");
				}

			}
		}

		fields.put(fieldType.getName(), value.toString());
	}// validateSpecialChar()
	
	public void validateReturnUrl(Fields fields) throws SystemException {
		if (!isValidUrl(fields.get(FieldType.RETURN_URL.getName()))) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RETURN_URL.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RETURN_URL.getResponseMessage());
			fields.setValid(false);
			throw new SystemException(ErrorType.INVALID_RETURN_URL, "Invalid return url");
		}
	}

	public boolean isValidUrl(String url) {
		if (StringUtils.isEmpty(url) || url.trim().equals("") || !url.matches(urlRegex)) {
			return false;
		} else {
			return true;
		}
	}

	public void validatePaymentType(Fields fields) throws SystemException {
		String paymentTypeCode = fields.get(FieldType.PAYMENT_TYPE.getName());
		String mopTypeCode = fields.get(FieldType.MOP_TYPE.getName());
		PaymentType paymentType = PaymentType.getInstanceUsingCode(paymentTypeCode);
		MopType mopType = MopType.getmop(mopTypeCode);
		if (null == paymentType) {
			fields.setValid(false);
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(),"Invalid Request");	
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + FieldType.PAYMENT_TYPE.getName());
		} else if (null == mopType) {
			fields.setValid(false);
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "Invalid Request");

			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + FieldType.MOP_TYPE.getName());
		}
	}
	
	public void validateCardDetails(Fields fields) throws SystemException {
		String invalidCardNumberMsg = "Invalid Card Number";
		String invalidCardExpiryDateMsg = "Invalid Card Expiry Date";
		String invalidCardCvvMsg = "Invalid Card CVV";
		
		
		// Validate card number
		// Validate card Length
		String cardNumber = fields.get(FieldType.CARD_NUMBER.getName());
		if (StringUtils.isBlank(cardNumber) || cardNumber.length() < FieldType.CARD_NUMBER.getMinLength() || cardNumber.length() > FieldType.CARD_NUMBER.getMaxLength()) {
			logger.error(invalidCardNumberMsg);
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CARD_NUMBER.getName() + " = '"
					+ fields.get(FieldType.CARD_NUMBER.getName()) + ", is not a valid value");
		}
		validateCardNumber(fields);
		
		// Validate card Expiry date
		String requestedDate = fields.get(FieldType.CARD_EXP_DT.getName());
		
		if (StringUtils.isBlank(requestedDate) || requestedDate.length() < FieldType.CARD_EXP_DT.getMinLength() || requestedDate.length() > FieldType.CARD_EXP_DT.getMaxLength()) {
			logger.error(invalidCardExpiryDateMsg);
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CARD_EXP_DT.getName() + " = '"
					+ fields.get(FieldType.CARD_EXP_DT.getName()) + ", is not a valid value");
			
		}
		
		// Validate whether field contains only number.
		validateNumber(new StringBuilder(requestedDate), FieldType.CARD_EXP_DT, fields);
		validateExpiryDate(fields);
		
		// Validate card CVV
		String cvv = fields.get(FieldType.CVV.getName());
		
		if (StringUtils.isBlank(cvv) || cvv.length() < FieldType.CVV.getMinLength() || cvv.length() > FieldType.CVV.getMaxLength()) {
			logger.error(invalidCardCvvMsg);
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CVV.getName() + " = '"
					+ fields.get(FieldType.CVV.getName()) + ", is not a valid value");
		}
		validateNumber(new StringBuilder(cvv), FieldType.CVV, fields);
	}

	public void validateRequestMap(Map<String, String> requestMap) throws SystemException {
		for (Entry<String, String> entry : requestMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			validateField(fieldTypeMap.get(key), key, value);
		}
	}

	public static String getEmailregex() {
		return emailRegex;
	}

	public static Pattern getNumberpattern() {
		return numberPattern;
	}

	public static Pattern getAlphanumpattern() {
		return alphaNumPattern;
	}

	public static String getAlphawithwhitespace() {
		return alphaWithWhiteSpace;
	}

}
