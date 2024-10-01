package com.pay10.commons.util;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.IPAddress;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.NodalTransactions;
import com.pay10.commons.user.ResponseObject;

/**
 * @author Puneet
 * 
 */

@Service
public class CrmValidator {

	public static final String batchEmailRegex = "^[_A-Za-z0-9+-.]+(\\.[_A-Za-z0-9+-.]+)*@[_A-Za-z0-9+-]+(\\.[A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)+([,.][_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+))*$";
	public static final String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
// 	public static final String emailRegex = "^[_A-Za-z0-9+-.]+(\\.[_A-Za-z0-9+-.]+)*@[_A-Za-z0-9+-]+([A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)";
	public static final String emailOriginalRegex = "/^([A-Za-z0-9_\\-\\.\\+])+\\@([A-Za-z0-9_\\-\\+])+\\.([A-Za-z]{2,4})$/";
	public static final String passwordRegex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@,_+/=]).{8,32}$";
	public static final String amountRegex = "[0-9]+([,.][0-9]{1,2})?";
	public static final String phoneRegex = "/^([0]|\\+91)?[- ]?[56789]\\d{9}$/";
	public static final String negativeAmountRegex = "-?[0-9]+([,.][0-9]{1,2})?";
	public static final String urlRegex = "^(https?|http?|www.?)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	// Define the regex pattern for valid business names
	private static final String BUSINESS_NAME_PATTERN = "^[a-zA-Z0-9&'.,\\-! ]*$";

	private boolean isValid;

	private ResponseObject resonseObject = new ResponseObject();


	// Added By Pritam Ray
	public  boolean validateBusinessName(String businessName) {
		return Pattern.matches(BUSINESS_NAME_PATTERN, businessName.trim());
	}

	// Below method used for Validating the IPV4 Address
	public boolean validatedIpv4(String ip) {
		try {
			if (ip == null || ip.isEmpty()) {
				return false;
			}

			String[] parts = ip.split("\\.");
			if (parts.length != 4) {
				return false;
			}

			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			if (ip.endsWith(".")) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public boolean isValidEmailId(String email) {
		if (email.matches(emailRegex)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isValidfpEmailId(String email) {
		if (email.matches(emailOriginalRegex)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isValidIpAddress(String ipAddress) {
		if (IPAddress.isValidIPv4(ipAddress) || IPAddress.isValidIPv6(ipAddress)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isValidMackAddress(String mackAddress) {
		String regex = "^([0-9A-Fa-f]{2}[:-])" + "{5}([0-9A-Fa-f]{2})|" + "([0-9a-fA-F]{4}\\." + "[0-9a-fA-F]{4}\\."
				+ "[0-9a-fA-F]{4})$";
		Pattern mackPattern = Pattern.compile(regex);
		return mackPattern.matcher(mackAddress).matches();
	}

	public boolean isValidBatchEmailId(String email) {
		if (email.matches(batchEmailRegex)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isValidURL(String url) {
		if (url.matches(urlRegex)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isValidPasword(String password) {
		if (password.matches(passwordRegex) && ((password.length() >= CrmFieldType.PASSWORD.getMinLength()))
				&& (password.length() <= CrmFieldType.PASSWORD.getMaxLength())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateBlankField(String field) {
		if (StringUtils.isBlank(field) || field.equals(Constants.FALSE.getValue())) {
			resonseObject.setResponseMessage(ErrorType.EMPTY_FIELD.getResponseMessage());
			return true;
		}
		return false;
	}

	public boolean validateBlankField(Long field) {
		if (null == field || field == 0) {
			resonseObject.setResponseMessage(ErrorType.EMPTY_FIELD.getResponseMessage());
			return true;
		}
		return false;
	}

	public boolean validateBlankField(int field) {
		if (field == 0) {
			resonseObject.setResponseMessage(ErrorType.EMPTY_FIELD.getResponseMessage());
			return true;
		}
		return false;
	}
	
	public boolean validateBlankField(Double field) {
		if (null == field || field == 0) {
			resonseObject.setResponseMessage(ErrorType.EMPTY_FIELD.getResponseMessage());
			return true;
		}
		return false;
	}

	public boolean validateBlankFields(String field) {
		if (StringUtils.isEmpty(field) || field.equals(Constants.FALSE.getValue())) {
			resonseObject.setResponseMessage(ErrorType.EMPTY_FIELDS.getResponseMessage());
			return true;
		}
		return false;
	}

	public boolean validateBlankField(Date cbRaiseDate) {
		return true;
	}

	public boolean validateField(CrmFieldType fieldType, String field) {

		StringBuilder value = new StringBuilder(field);

		if (null == fieldType) {
			resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			return false;
		} else if (value.length() < fieldType.getMinLength()) {
			resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			return false;
		} else if (value.length() > fieldType.getMaxLength()) {
			resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			return false;
		}
		// validate type

		switch (fieldType.getType()) {
		case ALPHA:
			validateAlpha(value, fieldType);
			break;

		case ALPHASPACE:
			validateAlphaSpace(value, fieldType);
			break;

		case ALPHANUM:
			validateAlphaNum(value, fieldType);
			break;

		case EMAIL:
			validateEmail(value, fieldType);
			break;

		case NUMBER:
			validateNumber(value, fieldType);
			break;

		case SPECIAL:
			validateSpecialChar(value, fieldType);
			break;
		case ALPHASPACENUM:
			validateAlphaSpaceNumeric(value, fieldType);
			break;
		case AMOUNT:
			validateAmount(value, fieldType);
			break;
		case URL:
			validateUrl(value, fieldType);
			break;
		default:
			break;
		}
		return getIsValid();
	}

	public void append(StringBuilder value, char inputChar, int length) {
		for (int index = 0; index < length; ++index) {
			value.append(inputChar);
		}
	}

	public void validateAlphaSpaceNumeric(StringBuilder request, CrmFieldType fieldType) {
		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final int SPACE = ' ';
		final int DOT = '.';
		int length = request.length();
		setIsValid(true);
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
			} else if (SPACE == ascii) {
				// allow space in between string
				continue;
			} else if (DOT == ascii) {
				// allow fullstop in between string
				continue;
			} else {
				resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
				setIsValid(false);
			}
		}
	}

	public void validateAlphaSpace(StringBuilder request, CrmFieldType fieldType) {

		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final int SPACE = ' ';
		final int DOT = '.';

		int length = request.length();
		setIsValid(true);
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
				// allow capital alphabets
				continue;
			} else if (ascii >= ALPHA_START && ascii <= ALPHA_END) {
				// allow small char alphabets
				continue;
			} else if (SPACE == ascii) {
				// allow space in between string
				continue;
			} else if (DOT == ascii) {
				// allow fullstop in between string
				continue;
			} else {
				resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
				setIsValid(false);
			}
		}
	} // validate alpha

	public void validateAlpha(StringBuilder request, CrmFieldType fieldType) {

		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';

		int length = request.length();
		setIsValid(true);
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
				// allow capital alphabets
				continue;
			} else if (ascii >= ALPHA_START && ascii <= ALPHA_END) {
				// allow small char alphabets
				continue;
			} else {
				resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
				setIsValid(false);
			}
		}
	} // validate alpha

	public void validateAlphaNum(StringBuilder request, CrmFieldType fieldType) {

		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';

		setIsValid(true);
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
				resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
				setIsValid(false);
			}
		}
	}// validate alphanum

	public void validateNumber(StringBuilder request, CrmFieldType fieldType) {

		final int NUMBER_START = '0';
		final int NUMBER_END = '9';

		setIsValid(true);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				continue;
			} else {
				resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
				setIsValid(false);
			}
		}
	}// validateNumber()

	public boolean isValidPhoneNumber(String custPhone, CrmFieldType fieldType) {
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

			} else {
				return false;
			}
			/*
			 * else { if (!fieldType.isSpecialCharReplacementAllowed()) { return false; } }
			 */
		}

		if (sum == 0) {
			return false;
		}

		if (custPhone.length() < fieldType.getMinLength()) {
			return false;
		} else if (custPhone.length() > fieldType.getMaxLength()) {
			return false;
		}

		return true;
	}

	private boolean validateCardBin(String cardBin) {
		final int[] Card_Start = { '2', '3', '4', '5', '6' };
		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		int length = cardBin.length();
		int sum = 0;
		for (int index = 0; index < Card_Start.length; ++index) {
			char ch = cardBin.charAt(0);
			int ascii = ch;
			if (ascii == Card_Start[index]) {
				++sum;
			}
		}
		if (sum == 0) {
			return false;
		} else {
			for (int index = 0; index < length; ++index) {
				char ch = cardBin.charAt(index);
				int ascii = ch;

				if (ascii >= NUMBER_START && ascii <= NUMBER_END) {

				} else {
					return false;
				}
			}
		}

		return true;
	}

	private boolean validateCardNo(String cardNo) {
		final int[] card_Start = { '2', '3', '4', '5', '6' };
		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		int length = cardNo.length();
		int sum = 0;
		for (int index = 0; index < card_Start.length; ++index) {
			char ch = cardNo.charAt(0);
			int ascii = ch;
			if (ascii == card_Start[index]) {
				++sum;
			}
		}
		if (sum == 0) {
			return false;
		} else {
			for (int index = 0; index < length; ++index) {
				char ch = cardNo.charAt(index);
				int ascii = ch;

				if (ascii >= NUMBER_START && ascii <= NUMBER_END && (index < 6 || index > 11)) {

				} else if (index >= 6 && index <= 11) {
					if (ascii == '*') {

					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}

		return true;
	}

	public void validateSpecialChar(StringBuilder request, CrmFieldType fieldType) {

		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final char[] permittedSpecialChars = fieldType.getSpecialChars().getPermittedSpecialChars();
		request = new StringBuilder(request.toString().replaceAll("\\n", ""));

		setIsValid(true);
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
				} else {
					resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
					setIsValid(false);
				}
			}
		}
	}// validateSpecialChar()

	public void validateAmount(StringBuilder request, CrmFieldType fieldType) {
		setIsValid(false);
		if (request.toString().matches(amountRegex)) {
			setIsValid(true);
		}
	}
	// validateAmount()

	public boolean validateNegativeAmount(String request) {
		boolean result = false;
		if (request.matches(negativeAmountRegex)) {
			result = true;
		}
		return result;
	}// validateNegativeAmount() for settlement

	public void validateUrl(StringBuilder request, CrmFieldType fieldType) {
		setIsValid(false);
		if (request.toString().matches(urlRegex)) {
			setIsValid(true);
		}
	}// validateUrl()

	public void validateEmail(StringBuilder request, CrmFieldType fieldType) {
		setIsValid(false);
		if (isValidEmailId(request.toString())) {
			setIsValid(true);
		}
	}// validareEmail

	public String validateRequestParameter(String request) {
		final int NUMBER_START = '0';
		final int NUMBER_END = '9';
		final int CAPITAL_ALPHA_START = 'A';
		final int CAPITAL_ALPHA_END = 'Z';
		final int ALPHA_START = 'a';
		final int ALPHA_END = 'z';
		final char[] permittedSpecialChars = CrmSpecialCharacters.ALL_SPEICIAL_CHARACTERS.getPermittedSpecialChars();
		final char REPLACEMENT_CHAR = ' ';

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
				} else {
					// Replace invalid characters
					value.setCharAt(index, REPLACEMENT_CHAR);
					break;
				}
			}
		}
		return value.toString();
	}// validateRequestParameters()

	public boolean validateChargingDetailsValues(ChargingDetails chargingDetails) {
		// if(chargingDetails.getMerchantTDR() != (chargingDetails.getPgTDR() +
		// chargingDetails.getBankTDR())){
		if ((chargingDetails.getMerchantTDR() == 0.0) || (chargingDetails.getMerchantServiceTax() == 0.0)) {
			return false;
		}
		if (!BigDecimal.valueOf(chargingDetails.getMerchantTDR()).equals(
				BigDecimal.valueOf(chargingDetails.getPgTDR()).add(BigDecimal.valueOf(chargingDetails.getBankTDR())))) {
			return false;
		}
		if (!BigDecimal.valueOf(chargingDetails.getMerchantTDRAFC()).equals(BigDecimal
				.valueOf(chargingDetails.getPgTDRAFC()).add(BigDecimal.valueOf(chargingDetails.getBankTDRAFC())))) {
			return false;
		}
		return true;
	}

	public boolean validateBatchEmailPhone(Invoice invoice) {
		CrmValidator validator = new CrmValidator();
		if ((validator.validateBlankField(invoice.getPhone())) || (invoice.getPhone().equalsIgnoreCase(null))) {
		} else if (!validator.validateField(CrmFieldType.MOBILE, invoice.getPhone())) {
			return false;
		}
		if ((validator.validateBlankField(invoice.getEmail()) || invoice.getEmail().equalsIgnoreCase(null))) {
		} else if (!validator.validateField(CrmFieldType.EMAILID, invoice.getEmail())) {
			return false;
		}

		return true;
	}

	public boolean validateFraudPrevention(FraudPreventionObj fraudPreventionObj) {

		CrmValidator validator = new CrmValidator();

		if ((validator.validateBlankField(fraudPreventionObj.getPayId()))) {
		}
		// custom validation for payId
		else if (!fraudPreventionObj.getPayId().equalsIgnoreCase(CrmFieldConstants.ALL.getValue())
				&& !validator.validateField(CrmFieldType.PAY_ID, fraudPreventionObj.getPayId())) {
			return false;
		}
		if (validator.validateBlankField(fraudPreventionObj.getFraudType().toString())) {
		}

		FraudRuleType fraudType = FraudRuleType.getInstance(fraudPreventionObj.getFraudType().toString());
		switch (fraudType) {
		case BLOCK_IP_ADDRESS:
			if (validator.validateBlankField(fraudPreventionObj.getIpAddress())) {
			}
			Collection<String> ipAddressList = Helper.parseFields(fraudPreventionObj.getIpAddress());
			for (String oneIpAddress : ipAddressList) {
				if (!validator.isValidIpAddress(oneIpAddress)) {
					return false;
				}
			}
			return true;
		case BLOCK_USER_COUNTRY:
			if (validator.validateBlankField(fraudPreventionObj.getUserCountry())) {
			}
			Collection<String> userCountryList = Helper.parseFields(fraudPreventionObj.getUserCountry());
			for (String oneUserCountry : userCountryList) {
				if (!validator.validateField(CrmFieldType.FRAUD_USER_COUNTRY, oneUserCountry)) {
					return false;
				}
			}
			return true;
		case BLOCK_CARD_BIN:
			if (validator.validateBlankField(fraudPreventionObj.getNegativeBin())) {

			}
			Collection<String> blockCardBinList = Helper.parseFields(fraudPreventionObj.getNegativeBin());
			for (String oneCardBin : blockCardBinList) {
				if (!validator.validateField(CrmFieldType.FRAUD_NEGATIVE_BIN, oneCardBin)) {
					return false;
				}
				if (!validator.validateCardBin(oneCardBin)) {
					return false;
				}
			}

			return true;
		case BLOCK_CARD_ISSUER_COUNTRY:
			if (validator.validateBlankField(fraudPreventionObj.getIssuerCountry())) {

			}
			Collection<String> blockCardIssuerList = Helper.parseFields(fraudPreventionObj.getIssuerCountry());
			for (String oneCardIssuer : blockCardIssuerList) {
				if (!validator.validateField(CrmFieldType.FRAUD_ISSUER_COUNTRY, oneCardIssuer)) {
					return false;
				}
			}

			return true;
		case BLOCK_CARD_NO:
			if (validator.validateBlankField(fraudPreventionObj.getNegativeCard())) {
			}
			Collection<String> blockCardNoList = Helper.parseFields(fraudPreventionObj.getNegativeCard());
			for (String oneblockCardNo : blockCardNoList) {
				if (oneblockCardNo.length() == 16) {
					if (!validator.validateCardNo(oneblockCardNo)) {
						return false;
					}
				} else {
					return false;
				}
			}

			return true;
		case BLOCK_EMAIL_ID:
			if (validator.validateBlankField(fraudPreventionObj.getEmail())) {

			}
			Collection<String> blockEmailIDList = Helper.parseFields(fraudPreventionObj.getEmail());
			for (String oneEmailId : blockEmailIDList) {
				if (!validator.isValidEmailId(oneEmailId)) {
					return false;
				}
			}

			return true;
		case BLOCK_PHONE_NUMBER:
			if (validator.validateBlankField(fraudPreventionObj.getPhone())) {

			}
			Collection<String> blockPhoneList = Helper.parseFields(fraudPreventionObj.getPhone());
			for (String onePhone : blockPhoneList) {
				if (!validator.isValidPhoneNumber(onePhone, CrmFieldType.CUST_PHONE)) {
					return false;
				}
			}

			return true;
		case BLOCK_CARD_TXN_THRESHOLD:
			if (validator.validateBlankField(fraudPreventionObj.getPerCardTransactionAllowed())) {

			}
			if (!validator.validateField(CrmFieldType.FRAUD_MIN_TRANSACTION_AMOUNT,
					fraudPreventionObj.getPerCardTransactionAllowed())) {
				return false;
			}

			if (validator.validateBlankField(fraudPreventionObj.getNegativeCard())) {
			}
			Collection<String> blockCardNoLst = Helper.parseFields(fraudPreventionObj.getNegativeCard());
			for (String oneblockCardNo : blockCardNoLst) {
				if (oneblockCardNo.length() == 16) {
					if (!validator.validateCardNo(oneblockCardNo)) {
						return false;
					}
				} else {
					return false;
				}
			}
			return true;
		case BLOCK_NO_OF_TXNS:
			if (!validator.validateField(CrmFieldType.FRAUD_MINUTE_TXN_LIMIT,
					fraudPreventionObj.getMinutesTxnLimit())) {
				return false;
			}
			return true;
		case BLOCK_TXN_AMOUNT:
			if (!validator.validateField(CrmFieldType.FRAUD_CURRENCY, fraudPreventionObj.getCurrency())) {
				return false;
			}
			if (!validator.validateField(CrmFieldType.FRAUD_MIN_TRANSACTION_AMOUNT,
					fraudPreventionObj.getMinTransactionAmount())) {
				return false;
			}
			if (!validator.validateField(CrmFieldType.FRAUD_MAX_TRANSACTION_AMOUNT,
					fraudPreventionObj.getMaxTransactionAmount())) {
				return false;
			}
			if (!(Float.parseFloat(fraudPreventionObj.getMaxTransactionAmount()) >= Float
					.parseFloat(fraudPreventionObj.getMinTransactionAmount()))) {
				return false;
			}
			return true;
		case BLOCK_TXN_AMOUNT_VELOCITY:
			if (!validator.validateField(CrmFieldType.FRAUD_MAX_TRANSACTION_AMOUNT,
					fraudPreventionObj.getMaxTransactionAmount())) {
				return false;
			}
			return true;
		case BLOCK_MACK_ADDRESS:
			if (validator.validateBlankField(fraudPreventionObj.getMackAddress())) {
				return false;
			}
			Collection<String> mackAddressList = Helper.parseFields(fraudPreventionObj.getMackAddress());
			for (String mackAddress : mackAddressList) {
				if (!validator.isValidMackAddress(mackAddress)) {
					return false;
				}
			}
			return true;
		case BLOCK_VPA_ADDRESS:
			if (validator.validateBlankField(fraudPreventionObj.getVpaAddress())) {
			}
			Collection<String> vpaAddressList = Helper.parseFields(fraudPreventionObj.getVpaAddress());
			for (String oneVpaAddress : vpaAddressList) {
				return true;
			}
			return true;
		}
		

		return true;
	}

	public boolean validateBinRangeFile(BinRange binRange) {
		CrmValidator validator = new CrmValidator();
		if (validator.validateBlankFields(binRange.getBinCodeLow())
				|| binRange.getBinCodeLow().equalsIgnoreCase("NA")) {

		} else if (!validator.validateField(CrmFieldType.BIN_CODE, binRange.getBinCodeLow())) {
			return false;
		}
		if (validator.validateBlankFields(binRange.getBinCodeHigh())
				|| binRange.getBinCodeHigh().equalsIgnoreCase("NA")) {

		} else if (!validator.validateField(CrmFieldType.BIN_CODE, binRange.getBinCodeHigh())) {
			return false;
		}
		if (validator.validateBlankFields(binRange.getCardType().toString())
				|| binRange.getCardType().toString().equalsIgnoreCase("NA")) {

		} else if (!validator.validateField(CrmFieldType.CARD_TYPE, binRange.getCardType().toString())) {
			return false;
		}
		if (validator.validateBlankFields(binRange.getGroupCode()) || binRange.getGroupCode().equalsIgnoreCase("NA")) {

		} else if (!validator.validateField(CrmFieldType.GROUP_CODE, binRange.getGroupCode())) {
			return false;
		}
		if (validator.validateBlankFields(binRange.getIssuerBankName())
				|| binRange.getIssuerBankName().equalsIgnoreCase("NA")) {

		} else if (!validator.validateField(CrmFieldType.ISSUER_BANK, binRange.getIssuerBankName())) {
			return false;
		}
		if (validator.validateBlankFields(binRange.getIssuerCountry())
				|| binRange.getIssuerCountry().equalsIgnoreCase("NA")) {

		} else if (!validator.validateField(CrmFieldType.ISSUER_COUNTRY, binRange.getIssuerCountry())) {
			return false;
		}
		if (validator.validateBlankFields(binRange.getMopType().toString())
				|| binRange.getMopType().toString().equalsIgnoreCase("NA")) {

		} else if (!validator.validateField(CrmFieldType.MOP_TYPE, binRange.getMopType().toString())) {
			return false;
		}
		if (validator.validateBlankFields(binRange.getProductName())
				|| binRange.getProductName().equalsIgnoreCase("NA")) {

		} else if (!validator.validateField(CrmFieldType.PRODUCT_NAME, binRange.getProductName())) {
			return false;
		}

		if (validator.validateBlankFields(binRange.getBinRangeHigh())
				|| binRange.getBinRangeHigh().equalsIgnoreCase("NA")) {

		} else if (!validator.validateField(CrmFieldType.BIN_RANGE_HIGH, binRange.getBinRangeHigh())) {
			return false;
		}
		if (validator.validateBlankFields(binRange.getBinRangeLow())
				|| binRange.getBinRangeLow().equalsIgnoreCase("NA")) {

		} else if (!validator.validateField(CrmFieldType.BIN_RANGE_LOW, binRange.getBinRangeLow())) {
			return false;
		}
		return true;
	}

	public boolean validateBeneficiaryAccounts(BeneficiaryAccounts beneficiaryAccounts) {
		beneficiaryAccounts.getBeneficiaryCd();
		CrmValidator validator = new CrmValidator();

		if (validator.validateBlankFields(beneficiaryAccounts.getBeneficiaryCd())) {
			beneficiaryAccounts.setResponseMessage("BeneficiaryCode is mandatory field");
//			return false;
		} else if (!validator.validateField(CrmFieldType.BENEFICIARY_CD, beneficiaryAccounts.getBeneficiaryCd())) {
			beneficiaryAccounts.setResponseMessage("Invalid Beneficiary Code");
//			return false;
		}
		
		if (beneficiaryAccounts.getCurrencyCd() == null || (validator.validateBlankField(beneficiaryAccounts.getCurrencyCd().getCode()))) {
			beneficiaryAccounts.setResponseMessage("Invalid Currency Code");
//			return false;
		} else if (CurrencyTypes.getInstancefromName(beneficiaryAccounts.getCurrencyCd().getName()) == null) {
			beneficiaryAccounts.setResponseMessage("Invalid Currency Code");
//			return false;
		}

		if (validator.validateBlankFields(beneficiaryAccounts.getBeneName())) {
			beneficiaryAccounts.setResponseMessage("Beneficiary Name is mandatory");
//			return false;
		} else if (!validator.validateField(CrmFieldType.BENE_NAME, beneficiaryAccounts.getBeneName())) {
			beneficiaryAccounts.setResponseMessage("Invalid Beneficiary Name");
//			return false;
		}

		if (validator.validateBlankFields(beneficiaryAccounts.getBankName())) {
			beneficiaryAccounts.setResponseMessage("Bank Name is mandatory");
//			return false;
		} else if (!validator.validateField(CrmFieldType.BENE_BANK_NAME, beneficiaryAccounts.getBankName())) {
			beneficiaryAccounts.setResponseMessage("Invalid Bank Name");
//			return false;
		}

		if (validator.validateBlankFields(beneficiaryAccounts.getIfscCode())) {
			beneficiaryAccounts.setResponseMessage("IFSC code is mandatory");
//			return false;
		} else if (!validator.validateField(CrmFieldType.IFSC_CODE, beneficiaryAccounts.getIfscCode())) {
			beneficiaryAccounts.setResponseMessage("Invalid IFSC code");
//			return false;
		}

		if (validator.validateBlankFields(beneficiaryAccounts.getBeneAccountNo())) {
			beneficiaryAccounts.setResponseMessage("Beneficiary Account Number is mandatory");
//			return false;
		} else if (!validator.validateField(CrmFieldType.BENE_ACCOUNT_NO, beneficiaryAccounts.getBeneAccountNo())) {
			beneficiaryAccounts.setResponseMessage("Invalid Beneficary Account Number");
//			return false;
		}

		if (validator.validateBlankFields(beneficiaryAccounts.getBeneExpiryDate())) {
		} else {
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
			sdformat.setLenient(false);
			ParsePosition position = new ParsePosition(0);
			try {
				Date d1 = sdformat.parse(beneficiaryAccounts.getBeneExpiryDate(), position);
				if (d1 == null || position.getIndex() != beneficiaryAccounts.getBeneExpiryDate().length()) {
					beneficiaryAccounts.setResponseMessage("Invalid Expiry Date");
				}
			} catch (Exception e) {
				beneficiaryAccounts.setResponseMessage("Invalid Expiry Date");
			}
		}

		if (validator.validateBlankFields(beneficiaryAccounts.getMobileNo())) {
		} else if (!validator.validateField(CrmFieldType.BENE_MOBILE_NUMBER, beneficiaryAccounts.getMobileNo())) {
			beneficiaryAccounts.setResponseMessage("Invalid Mobile Number");
//			return false;
		}

		if (validator.validateBlankFields(beneficiaryAccounts.getEmailId())) {
		} else if (!validator.validateField(CrmFieldType.BENE_EMAIL_ID, beneficiaryAccounts.getEmailId())) {
			beneficiaryAccounts.setResponseMessage("Invalid Email Id");
//			return false;
		}

		if (validator.validateBlankFields(beneficiaryAccounts.getAddress1())) {
		} else if (!validator.validateField(CrmFieldType.BENE_ADDRESS_1, beneficiaryAccounts.getAddress1())) {
			beneficiaryAccounts.setResponseMessage("Invalid Address 1");
//			return false;
		}

		if (validator.validateBlankFields(beneficiaryAccounts.getAddress2())) {
		} else if (!validator.validateField(CrmFieldType.BENE_ADDRESS_2, beneficiaryAccounts.getAddress2())) {
			beneficiaryAccounts.setResponseMessage("Invalid Address 2");
//			return false;
		}

		if (validator.validateBlankFields(beneficiaryAccounts.getAadharNo())) {
		} else if (!validator.validateField(CrmFieldType.BENE_AADHAR_NO, beneficiaryAccounts.getAadharNo())) {
			beneficiaryAccounts.setResponseMessage("Invalid Aadhar Card number");
//			return false;
		}

		return true;
	}
	
	public Boolean validateAmountByYesBankNodalPaymentType(String paymentType, String amt) {
		NodalPaymentTypesYesBankFT3 instance = NodalPaymentTypesYesBankFT3.getInstancefromCode(paymentType);
		if(instance == null) {
			resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			return false;
		}
		
		BigDecimal minimumAmount = new BigDecimal(instance.getMinimumAmount());
		BigDecimal maximumAmount = new BigDecimal(instance.getMaximumAmount());
		BigDecimal amount = new BigDecimal(amt);
//		-1, 0, or 1 as this BigDecimal is numerically less than, equal to, or greater than val
		if(amount.compareTo(minimumAmount) < 0 || amount.compareTo(maximumAmount) > 0 ) {
			resonseObject.setResponseMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			return false;
		}
		
		return true; 
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public ResponseObject getResonseObject() {
		return resonseObject;
	}

	public void setResonseObject(ResponseObject resonseObject) {
		this.resonseObject = resonseObject;
	}
	
	public boolean validateNodalTransactions(NodalTransactions nodalTransactions) {
		CrmValidator validator = new CrmValidator();
		if (validator.validateBlankField(nodalTransactions.getBeneficiaryCode())) {
			nodalTransactions.setResponseMessage("Field beneficiary code is mandatory");
		} else if (!(validator.validateField(CrmFieldType.BENE_MERCHANT_PROVIDED_ID, nodalTransactions.getBeneficiaryCode()))) {
			nodalTransactions.setResponseMessage("Invalid beneficiary code");
		}

		if (validator.validateBlankFields(nodalTransactions.getPaymentType())) {
			nodalTransactions.setResponseMessage("Field payment type is mandatory");
		} else if ((NodalPaymentTypes.getInstancefromName(nodalTransactions.getPaymentType()) == null)) {
			nodalTransactions.setResponseMessage("Invalid payment type");
		}
		if(StringUtils.isBlank(nodalTransactions.getResponseMessage())) {
			nodalTransactions.setPaymentType(NodalPaymentTypes.getInstancefromName(nodalTransactions.getPaymentType()).getCode());
		}

		if (validator.validateBlankFields(nodalTransactions.getComments())) {
			nodalTransactions.setResponseMessage("Field comment is mandatory");
		} else if (!(validator.validateField(CrmFieldType.BENE_COMMENT, nodalTransactions.getComments()))) {
			nodalTransactions.setResponseMessage("Invalid comments");
		} else if(!nodalTransactions.getComments().matches(".*[a-zA-Z0-9]+.*")) {
			nodalTransactions.setResponseMessage("Invalid comments");
		}
		

		if(validator.validateBlankFields(nodalTransactions.getAmount())) {
			nodalTransactions.setResponseMessage("Field amount is mandatory");
		} else if(!validator.validateField(CrmFieldType.BENE_AMOUNT, nodalTransactions.getAmount())) {
			nodalTransactions.setResponseMessage("Invalid Amount");
		} else if(!(validator.validateAmountByYesBankNodalPaymentType(nodalTransactions.getPaymentType(), nodalTransactions.getAmount()))) {
			nodalTransactions.setResponseMessage("Invalid Amount");
		}
		return true;
	}
	
	public boolean allZerosCharacters(String value) {
		for(char ch : value.toCharArray()) {
			if(ch != '0') {
				return false;
			}
		}
		return true;
	}
}
