package com.pay10.batch.commons.util;

import java.util.Map;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.exception.ErrorType;
import com.pay10.batch.exception.SystemException;
import com.pay10.commons.util.FieldType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class GeneralValidator implements Validator {
	
	private static Logger logger = Logger.getLogger(GeneralValidator.class
			.getName());
	public final char REPLACEMENT_CHAR = ' ';
	public static final Map<String, FieldType> fieldTypeMap = FieldType.getFieldsMap();

	public GeneralValidator() {
	}

	private void validateFieldType(FieldType fieldType, String key, Fields fields, StringBuilder value)
			throws SystemException {
		
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

		case URL:
			// TODO: Put validations for urls
			break;
			
		case NONE:
			//Ignore
			break;
		case AMOUNT:
			validateAmount(value, fieldType, fields);
			break;
		case DECIMAL:
			validateDecimal(value, fieldType, fields);
			break;
		}
	}
	
	public void validateRecoRefund(Fields fields) throws SystemException {

		for (String key : fields.keySet()) {
			recoRefundIrctc(fieldTypeMap.get(key), key, fields);
		}
	}

	private void recoRefundIrctc(FieldType fieldType, String key, Fields fields) throws SystemException {

		String valueKey = fields.get(key);
		StringBuilder value = new StringBuilder(valueKey);
		
		if (null == fieldType) {
		// Check if validations are set for the requested field
			throw new SystemException(ErrorType.VALIDATION_FAILED, "No such field defined, field = " + key);
		} else if (value.length() < fieldType.getMinLength()) {

			throw new SystemException(ErrorType.VALIDATION_FAILED,
					"Minimum length of '" + key + "' is " + fieldType.getMinLength());
		} else if (value.length() > fieldType.getMaxLength()) {

			throw new SystemException(ErrorType.VALIDATION_FAILED,
					"Maximum length of '" + key + "' is " + fieldType.getMaxLength());
		}

		// Validate type
		validateFieldType(fieldType, key, fields, value);
	}

	private void validateAlpha(StringBuilder request, FieldType fieldType,
			Fields fields) throws SystemException {

		final int CAPITAL_ALPHA_START = (int) 'A';
		final int CAPITAL_ALPHA_END = (int) 'Z';
		final int ALPHA_START = (int) 'a';
		final int ALPHA_END = (int) 'z';

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = (int) ch;

			if (ascii >= CAPITAL_ALPHA_START && ascii <= CAPITAL_ALPHA_END) {
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
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName());
				}
			}
		}

		fields.put(fieldType.getName(), value.toString());
	}

	private void validateAlphaNum(StringBuilder request, FieldType fieldType,
			Fields fields) throws SystemException {

		final int NUMBER_START = (int) '0';
		final int NUMBER_END = (int) '9';
		final int CAPITAL_ALPHA_START = (int) 'A';
		final int CAPITAL_ALPHA_END = (int) 'Z';
		final int ALPHA_START = (int) 'a';
		final int ALPHA_END = (int) 'z';

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = (int) ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				continue;
			} else if (ascii >= CAPITAL_ALPHA_START
					&& ascii <= CAPITAL_ALPHA_END) {
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
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName() + " : " + value);
				}
			}
		}

		fields.put(fieldType.getName(), value.toString());
	}

	private void validateNumber(StringBuilder request, FieldType fieldType,
			Fields fields) throws SystemException {

		final int NUMBER_START = (int) '0';
		final int NUMBER_END = (int) '9';
		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = (int) ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				continue;
			} else {
				if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName());
				}
			}
		}

		fields.put(fieldType.getName(), value.toString());
	}

	private void validateAmount(StringBuilder request, FieldType fieldType,
			Fields fields) throws SystemException {
		if(StringUtils.isBlank(request.toString().replaceAll("0",""))){
			throw new SystemException(ErrorType.VALIDATION_FAILED,
					"Invalid " + fieldType.getName());
		}
		validateNumber(request,fieldType,fields);
		
	}
	
	private void validateSpecialChar(StringBuilder request, FieldType fieldType,
			Fields fields) throws SystemException {

		final int NUMBER_START = (int) '0';
		final int NUMBER_END = (int) '9';
		final int CAPITAL_ALPHA_START = (int) 'A';
		final int CAPITAL_ALPHA_END = (int) 'Z';
		final int ALPHA_START = (int) 'a';
		final int ALPHA_END = (int) 'z';
		final char[] permittedSpecialChars = { ' ', '@', ',', '-', '_', '+',
				'/', '=', '*', '.', ':','\n', '\r' };

		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = (int) ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				// allow numbers
				continue;
			} else if (ascii >= CAPITAL_ALPHA_START
					&& ascii <= CAPITAL_ALPHA_END) {
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
	
	private void validateDecimal(StringBuilder request, FieldType fieldType,
			Fields fields) throws SystemException {

		final int NUMBER_START = (int) '0';
		final int NUMBER_END = (int) '9';
		final int DOT = (int) '.';
		StringBuilder value = new StringBuilder(request);
		int length = request.length();
		int dotCount = 0;
		for (int index = 0; index < length; ++index) {
			char ch = request.charAt(index);
			int ascii = (int) ch;

			if (ascii >= NUMBER_START && ascii <= NUMBER_END) {
				continue;
			}else if(ascii == DOT && index != 0 && dotCount < 1) {
				dotCount++;
				continue;
			}else {
				if (fieldType.isSpecialCharReplacementAllowed()) {
					// If special char replacement is allowed
					value.setCharAt(index, REPLACEMENT_CHAR);
				} else {
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							"Invalid " + fieldType.getName());
				}
			}
		}

		fields.put(fieldType.getName(), value.toString());
	}

	@Override
	public void validate(Fields fields) throws SystemException {
		// TODO Auto-generated method stub
		
	}
}
