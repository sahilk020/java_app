package com.pay10.commons.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;

@Service
public class Fields {

	private Map<String, Object> sessionMap;
	private Map<String, String> fields = new HashMap<String, String>();

	private Fields previous = null;
	private boolean valid = true;
	private static Logger logger = LoggerFactory.getLogger(Fields.class.getName());

	@Autowired
	private FieldsDao fieldsDao;

	public Fields(Map<String, String> fields) {
		this.fields.putAll(fields);
	}

	public Fields() {
	}

	public void updateNewOrderDetails() throws SystemException {
		fieldsDao.updateNewOrderDetails(this);
	}

	public void updateNewOrderDetails(Fields fields) throws SystemException {
		fieldsDao.updateNewOrderDetails(this);
	}

	public void updateStatus() throws SystemException {
		fieldsDao.updateStatus(this);
	}

	public void checkDuplicate() throws SystemException {
		fieldsDao.getDuplicate(this);
	}

	public void insert() throws SystemException {
		fieldsDao.insert(this);
	}

	public void insert(Fields fields) throws SystemException {
		fieldsDao.insert(fields);
	}
	public void insertPO(Fields fields) throws SystemException {
		fieldsDao.insertPO(fields);
	}
	public void insertPOUpdate(Fields fields) throws SystemException {
		fieldsDao.insertPOUpdate(fields);
	}

	public void insertNewOrder() throws SystemException {
		fieldsDao.insertNewOrder(this);
	}

	public void updateCurrentTransaction() throws SystemException {
		fieldsDao.updateCurrentTransaction(this);
	}

	public boolean createAllForYesUpiEnquiry(String pgRefNum) throws SystemException {
		return fieldsDao.createAllForYesUpiEnquiry(pgRefNum);
	}

	public Fields refreshPrevious() throws SystemException {
		String txnId = fields.get(FieldType.ORIG_TXN_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		previous = fieldsDao.getFields(txnId, payId);
		return previous;
	}

	public Fields refreshPrevious(Fields fields) throws SystemException {
		String txnId = fields.get(FieldType.ORIG_TXN_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String txnType = TransactionType.SALE.getName();
		String status = StatusType.CAPTURED.getName();
		fields.previous = fieldsDao.getFieldsForRefund(txnId, payId, txnType, status);
		return previous;
	}
	
	
	public Fields refreshPreviousPayout(Fields fields) throws SystemException {
		String txnId = fields.get(FieldType.ORIG_TXN_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String txnType = TransactionType.SALE.getName();
		String status = StatusTypePayout.REQUEST_ACCEPTED.getName();
		fields.previous = fieldsDao.getFieldsForRefundPayout(txnId, payId, txnType, status);
		return previous;
	}

	public Fields refreshPreviousForFedUpi(Fields fields) throws SystemException {
		String refId = fields.get(FieldType.UDF5.getName());
		String txnType = TransactionType.SALE.getName();
		fields.previous = fieldsDao.getFieldsForFedUpi(refId, txnType);
		return previous;
	}

	public Fields refreshPreviousForFedUpiRefund(Fields fields) throws SystemException {
		String refId = fields.get(FieldType.UDF5.getName());
		String txnType = TransactionType.REFUND.getName();
		fields.previous = fieldsDao.getFieldsForFedUpi(refId, txnType);
		return previous;
	}

	public Fields refreshPreviousForHdfcUpi(Fields fields) throws SystemException {
		String refId = fields.get(FieldType.PG_REF_NUM.getName());
		String txnType = TransactionType.SALE.getName();
		fields.previous = fieldsDao.getFieldsForHdfcUpi(refId, txnType);
		return previous;
	}

	public Fields refreshPreviousForStatus(Fields fields) throws SystemException {
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		//String amount = fields.get(FieldType.AMOUNT.getName());
		//amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
		previous = fieldsDao.getFieldsForStatus(orderId, payId, "");

		if (!(null != previous && previous.size() > 0)) {
			previous = fieldsDao.getFieldsForRefundStatus(orderId, payId, "");
			if (!(null != previous && previous.size() > 0)) {
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.STATUS.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseMessage());
			} else {
				fields.previous = previous;
			}
		} else {
			fields.previous = previous;
		}

		return previous;
	}
	
	
	public Fields refreshPreviousForStatusPayout(Fields fields) throws SystemException {
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String amount = fields.get(FieldType.AMOUNT.getName());
		amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
		previous = fieldsDao.getFieldsForStatusPayout(orderId, payId, amount);

		if (!(null != previous && previous.size() > 0)) {
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.STATUS.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseMessage());
		} else {
			fields.previous = previous;
		}

		return previous;
	}
	
	public Fields refreshPreviousForRefundStatus(Fields fields) throws SystemException {
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String amount = fields.get(FieldType.AMOUNT.getName());
		amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
		previous = fieldsDao.getFieldsForRefundStatus(orderId, payId, amount);
		fields.previous = previous;
		return previous;
	}

	public void validateDupicateOrderId(Fields fields) throws SystemException {
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		Long recordsCount = fieldsDao.validateDuplicateOrderId(fields, orderId, payId, currencyCode);
		if (recordsCount > 0) {
			throw new SystemException(ErrorType.DUPLICATE_ORDER_ID, "");
		}
	}

	public void validateDupicateSaleOrderId(Fields fields) throws SystemException {
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		Long recordsCount = fieldsDao.validateDuplicateSaleOrderId(orderId, payId, currencyCode);
		if (recordsCount > 0) {
			throw new SystemException(ErrorType.DUPLICATE_ORDER_ID, "");
		}
	}
	
	public void validateDupicateSaleOrderIdPayout(Fields fields) throws SystemException {
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		Long recordsCount = fieldsDao.validateDuplicateSaleOrderIdPayout(orderId, payId, currencyCode);
		if (recordsCount > 0) {
			throw new SystemException(ErrorType.DUPLICATE_ORDER_ID, "");
		}
	}

	public void validateDupicateSubmit(Fields fields) throws SystemException {

		String orderId = fields.get(FieldType.ORDER_ID.getName());
		Long recordsCount = fieldsDao.validateDuplicateSubmit(orderId);
		if (recordsCount > 0) {
			throw new SystemException(ErrorType.DUPLICATE_SUBMISSION, "");
		}
	}

	public void validateDupicateSaleOrderIdInRefund(Fields fields) throws SystemException {
		String refundOrderId = fields.get(FieldType.REFUND_ORDER_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		Long recordsCount = fieldsDao.validateDuplicateSaleOrderIdInRefund(refundOrderId, payId, currencyCode);
		if (recordsCount > 0) {
			throw new SystemException(ErrorType.DUPLICATE_ORDER_ID, "");
		}
	}

	public void validateDupicateRefundOrderIdInSale(Fields fields) throws SystemException {
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		Long recordsCount = fieldsDao.validateDuplicateRefundOrderIdInSale(orderId, payId, currencyCode);
		if (recordsCount > 0) {
			throw new SystemException(ErrorType.DUPLICATE_ORDER_ID, "");
		}
	}

	public void validateDupicateRefundOrderId(Fields fields) throws SystemException {
		String refundOrderId = fields.get(FieldType.REFUND_ORDER_ID.getName());
		String origTxnType = fields.get(FieldType.ORIG_TXNTYPE.getName());
		// TODO : Null check on txnType and OrigTxnType
		if (StringUtils.isEmpty(origTxnType) || StringUtils.isEmpty(refundOrderId)) {
			return;
		}
		String payId = fields.get(FieldType.PAY_ID.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String status = StatusType.CAPTURED.getName();
		Long recordsCount = fieldsDao.validateDuplicateRefundOrderId(refundOrderId, payId, currencyCode, status);
		if (recordsCount > 0) {
			throw new SystemException(ErrorType.DUPLICATE_REFUND_ORDER_ID, "");
		}
	}

	public Fields refreshPreviousForVerify(Fields fields) throws SystemException {
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String amount = fields.get(FieldType.AMOUNT.getName());
		amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
		String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
		String status = StatusType.CAPTURED.getName();
		previous = fieldsDao.getFieldsForVerify(orderId, payId, amount, pgRefNum, status);

		if (!(null != previous && previous.size() > 0)) {
			previous = fieldsDao.getFieldsForRefundStatus(orderId, payId, amount);
			if (!(null != previous && previous.size() > 0)) {
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.STATUS.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseMessage());
			}
		} else {
			fields.previous = previous;
		}

		return previous;
	}

	  public Fields refreshPreviousForAcquirerUpi(Fields fields) throws SystemException {
	        String refId = fields.get(FieldType.PG_REF_NUM.getName());
	        String txnType = TransactionType.SALE.getName();
	        fields.previous = fieldsDao.getFieldsForAcquirerUpi(refId, txnType);
	        return previous;
	    }

	public Fields refreshPreviousForsettlement(Fields fields) throws SystemException {
		String custID = fields.get(FieldType.CUSTOMER_ID.getName());
		String oId = fields.get(FieldType.OID.getName());
		String status = StatusType.SENT_TO_BANK.getName();
		previous = fieldsDao.getFieldsForSettlement(custID, oId, status);

		if (!(null != previous && previous.size() > 0)) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseMessage());
		} else {
			fields.previous = previous;
		}
		return previous;
	}

	public Fields refreshPreviousForReco(Fields fields) throws SystemException {
		String pgrefNo = fields.get(FieldType.ORIG_TXN_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String amount = fields.get(FieldType.AMOUNT.getName());
		amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String txnType = fields.get(FieldType.TXNTYPE.getName());

		previous = fieldsDao.getFieldsForReco(pgrefNo, payId, amount, orderId, txnType);
		if (!(null != previous && previous.size() > 0)) {
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), txnType);
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseMessage());
		} else {
			fields.previous = previous;
		}
		return previous;
	}

	public void put(Fields fields) {
		this.fields.putAll(fields.getFields());
	}

	public Fields(Fields fields) {
		this.fields.putAll(fields.getFields());
	}

	public void putAll(Map<String, String> fields_) {
		this.fields.putAll(fields_);
	}

	public Map<String, String> getFields() {
		return new HashMap<String, String>(fields);
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public void put(String key, String value) {
		fields.put(key, value);
	}

	public String get(String key) {
		return fields.get(key);
	}

	public Set<String> keySet() {
		return fields.keySet();
	}

	public String remove(String key) {
		return fields.remove(key);
	}

	// Remove keys when value/key is null/spaces
	public void clean() {
		Map<String, String> validFields = new HashMap<String, String>();
		for (String key : fields.keySet()) {

			String value = fields.get(key);
			if (null == value) {
				continue;
			}
			key.trim();
			value.trim();

			if (!key.isEmpty() && !value.isEmpty()) {
				validFields.put(key, value);
			}
		}

		fields.clear();
		fields.putAll(validFields);
	}

	public void clear() {
		fields.clear();
	}

	public void removeExtraFields() {

		// All the fields which are not configured to be allowed are ignored
		Collection<String> allowedRequestFields = SystemProperties.getRequestfields();
		Map<String, String> validFields = new HashMap<String, String>();
		for (String key : fields.keySet()) {
			if (allowedRequestFields.contains(key)) {
				validFields.put(key, fields.get(key));
			}
		}

		fields.clear();
		fields.putAll(validFields);
	}// removeExtraFields()

	public void removeExtraInternalFields() {

		// All the fields which are not configured to be allowed are ignored
		Collection<String> allowedRequestFields = SystemProperties.getInternalRequestFields();
		Map<String, String> validFields = new HashMap<String, String>();
		for (String key : fields.keySet()) {
			if (allowedRequestFields.contains(key)) {
				validFields.put(key, fields.get(key));
			}
		}

		fields.clear();
		fields.putAll(validFields);
	}// removeExtraFields()

	public void trimAndToUpper() {
		Map<String, String> validFields = new HashMap<String, String>();
		for (String key : fields.keySet()) {
			String value = fields.get(key);
			key.trim();
			value.trim();

			if (!key.isEmpty() && !value.isEmpty()) {
				validFields.put(key.toUpperCase(), value.toUpperCase());
			}
		}

		fields.clear();
		fields.putAll(validFields);
	}// trimAndToUpper()

	public void removeSecureFieldsSubmitted() {
		for (String secureField : SystemProperties.getSecureRequestFields()) {
			fields.remove(secureField);
		}
	}

	public FieldsDao getFieldsDao() {
		return fieldsDao;
	}

	public void setFieldsDao(FieldsDao fieldsDao) {
		this.fieldsDao = fieldsDao;
	}

	public Fields getPrevious() {
		return previous;
	}

	public void setPrevious(Fields previous) {
		this.previous = previous;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public int size() {
		return fields.size();
	}

	public Map<String, String> removeSecureFields() {

		Map<String, String> secureFields = new HashMap<String, String>();

		String cardNumber = remove(FieldType.CARD_NUMBER.getName());
		if (null != cardNumber) {
			secureFields.put(FieldType.CARD_NUMBER.getName(), cardNumber);
		}

		String cardExpiryDate = remove(FieldType.CARD_EXP_DT.getName());
		if (null != cardExpiryDate) {
			secureFields.put(FieldType.CARD_EXP_DT.getName(), cardExpiryDate);
		}

		String cvv = remove(FieldType.CVV.getName());
		if (null != cvv) {
			secureFields.put(FieldType.CVV.getName(), cvv);
		}

		String password = remove(FieldType.PASSWORD.getName());
		if (null != password) {
			secureFields.put(FieldType.PASSWORD.getName(), password);
		}

		String txnKey = remove(FieldType.TXN_KEY.getName());
		if (null != txnKey) {
			secureFields.put(FieldType.TXN_KEY.getName(), txnKey);
		}

		String adf1 = remove(FieldType.ADF1.getName());
		if (null != adf1) {
			secureFields.put(FieldType.ADF1.getName(), adf1);
		}

		String adf2 = remove(FieldType.ADF2.getName());
		if (null != adf2) {
			secureFields.put(FieldType.ADF2.getName(), adf2);
		}

		String adf3 = remove(FieldType.ADF3.getName());
		if (null != adf3) {
			secureFields.put(FieldType.ADF3.getName(), adf3);
		}

		String adf4 = remove(FieldType.ADF4.getName());
		if (null != adf4) {
			secureFields.put(FieldType.ADF4.getName(), adf4);
		}

		String adf5 = remove(FieldType.ADF5.getName());
		if (null != adf5) {
			secureFields.put(FieldType.ADF5.getName(), adf5);
		}

		String adf6 = remove(FieldType.ADF6.getName());
		if (null != adf6) {
			secureFields.put(FieldType.ADF6.getName(), adf6);
		}

		String adf7 = remove(FieldType.ADF7.getName());
		if (null != adf7) {
			secureFields.put(FieldType.ADF7.getName(), adf7);
		}

		String adf8 = remove(FieldType.ADF8.getName());
		if (null != adf8) {
			secureFields.put(FieldType.ADF8.getName(), adf8);
		}

		String adf9 = remove(FieldType.ADF9.getName());
		if (null != adf9) {
			secureFields.put(FieldType.ADF9.getName(), adf9);
		}

		String adf10 = remove(FieldType.ADF10.getName());
		if (null != adf10) {
			secureFields.put(FieldType.ADF10.getName(), adf10);
		}

		String adf11 = remove(FieldType.ADF11.getName());
		if (null != adf11) {
			secureFields.put(FieldType.ADF11.getName(), adf11);
		}

		String bobcardno = remove(Constants.BOB_CARD_NUMBER.getValue());
		if (null != bobcardno) {
			secureFields.put(Constants.BOB_CARD_NUMBER.getValue(), bobcardno);
		}

		String bobExpMonth = remove(Constants.BOB_EXP_MONTH.getValue());
		if (null != bobExpMonth) {
			secureFields.put(Constants.BOB_EXP_MONTH.getValue(), bobExpMonth);
		}

		String bobExpYear = remove(Constants.BOB_EXP_YEAR.getValue());
		if (null != bobExpYear) {
			secureFields.put(Constants.BOB_EXP_YEAR.getValue(), bobExpYear);
		}

		String bobcvv = remove(Constants.BOB_CVV.getValue());
		if (null != bobcvv) {
			secureFields.put(Constants.BOB_CVV.getValue(), bobcvv);
		}

		String fedcard = remove(Constants.FED_CARD_NUMBER.getValue());
		if (null != fedcard) {
			secureFields.put(Constants.FED_CARD_NUMBER.getValue(), fedcard);
		}

		String fedExpMonthYear = remove(Constants.FED_EXP_MONTH_YEAR.getValue());
		if (null != fedExpMonthYear) {
			secureFields.put(Constants.FED_EXP_MONTH_YEAR.getValue(), fedExpMonthYear);
		}

		String fedExpMonth = remove(Constants.FED_EXP_MONTH.getValue());
		if (null != fedExpMonth) {
			secureFields.put(Constants.FED_EXP_MONTH.getValue(), fedExpMonth);
		}

		String fedExpYear = remove(Constants.FED_EXP_YEAR.getValue());
		if (null != fedExpYear) {
			secureFields.put(Constants.FED_EXP_YEAR.getValue(), fedExpYear);
		}

		String fedcvv = remove(Constants.FED_CVV.getValue());
		if (null != fedcvv) {
			secureFields.put(Constants.FED_CVV.getValue(), fedcvv);
		}

		String migsCard = remove(Constants.MIGS_CARD_NUMBER.getValue());
		if (null != migsCard) {
			secureFields.put(Constants.MIGS_CARD_NUMBER.getValue(), migsCard);
		}

		String migsExp = remove(Constants.MIGS_EXP.getValue());
		if (null != migsExp) {
			secureFields.put(Constants.MIGS_EXP.getValue(), migsExp);
		}

		String migsCvv = remove(Constants.MIGS_CVV.getValue());
		if (null != migsCvv) {
			secureFields.put(Constants.MIGS_CVV.getValue(), migsCvv);
		}

		String firstDataCard = remove(Constants.FIRST_DATA_CARD.getValue());
		if (null != firstDataCard) {
			secureFields.put(Constants.FIRST_DATA_CARD.getValue(), firstDataCard);
		}

		String firstDataExpMonth = remove(Constants.FIRST_DATA_EXP_MONTH.getValue());
		if (null != firstDataExpMonth) {
			secureFields.put(Constants.FIRST_DATA_EXP_MONTH.getValue(), firstDataExpMonth);
		}

		String firstDataExpYear = remove(Constants.FIRST_DATA_EXP_YEAR.getValue());
		if (null != firstDataExpYear) {
			secureFields.put(Constants.FIRST_DATA_EXP_YEAR.getValue(), firstDataExpYear);
		}

		String firstDataCvv = remove(Constants.FIRST_DATA_CVV.getValue());
		if (null != firstDataCvv) {
			secureFields.put(Constants.FIRST_DATA_CVV.getValue(), firstDataCvv);
		}

		return secureFields;
	}

	public String getFieldsAsString() {

		// current content commented to avoid card details being sent to log files
		// StringBuilder allFieldsSum = new StringBuilder();
		// allFieldsSum.append("\n");
		/*
		 * for (String key : fields.keySet()) { allFieldsSum.append(key);
		 * allFieldsSum.append("="); allFieldsSum.append(fields.get(key));
		 * allFieldsSum.append("~"); }
		 */
		// return allFieldsSum.toString();
		return getFieldsAsBlobString();
	}

	public String getFieldsAsBlobString() {

		StringBuilder allFieldsSum = new StringBuilder();
		allFieldsSum.append("\n");
		for (String key : fields.keySet()) {
			if (key.equals(FieldType.CARD_NUMBER.getName())) {
				continue;
			} else if (key.equals(FieldType.CARD_EXP_DT.getName())) {
				continue;
			} else if (key.equals(FieldType.CVV.getName())) {
				continue;
			}
			allFieldsSum.append(key);
			allFieldsSum.append("=");
			allFieldsSum.append(fields.get(key));
			allFieldsSum.append("~");
			// logger.info("allFieldsSum"+allFieldsSum);

		}

		return allFieldsSum.toString();
	}

	public void logAllFields(String message) {

		// Do not log card details, as this is a security issue
		Map<String, String> secureFields = removeSecureFields();

		StringBuilder allFieldsSum = new StringBuilder();
		allFieldsSum.append(message);
		allFieldsSum.append("\n");
		for (String key : fields.keySet()) {
			allFieldsSum.append(key);
			allFieldsSum.append(" = ");
			allFieldsSum.append(fields.get(key));
			allFieldsSum.append("~");
		}
		// Put secure details back in the collection
		putAll(secureFields);

		// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), this.getCustomMDC());
		logger.info(removeCardDetailsForLogger(allFieldsSum.toString()));
	}
	
	// added by sonu for remove to cardNumber from _FINAL_REQUEST
	public void logAllFieldsUsingMasking(String message) {

		Map<String, String> validFields = new HashMap<String, String>();
		for (String key : fields.keySet()) {
			String value = fields.get(key);
			key.trim();
			if(!StringUtils.isBlank(value)) {
			value.trim();
			}
			String finalRequest = null;
			if (key.endsWith("_FINAL_REQUEST")) {
				finalRequest = value.trim();

				if (finalRequest.startsWith("{")) {

					JSONObject jsonObj = new JSONObject(finalRequest);
					if (jsonObj.has("card_number")) {
						jsonObj = maskJsonObj(jsonObj, "card_number", jsonObj.getString("card_number"));
					}
					if (jsonObj.has("Card_number")) {
						jsonObj = maskJsonObj(jsonObj, "Card_number", jsonObj.getString("Card_number"));
					}
					if (jsonObj.has("card_Number")) {
						jsonObj = maskJsonObj(jsonObj, "card_Number", jsonObj.getString("card_Number"));
					}
					if (jsonObj.has("Card_Number")) {
						jsonObj = maskJsonObj(jsonObj, "Card_Number", jsonObj.getString("Card_Number"));
					}
					if (jsonObj.has("CardNumber")) {
						jsonObj = maskJsonObj(jsonObj, "CardNumber", jsonObj.getString("CardNumber"));
					}
					if (jsonObj.has("Cardnumber")) {
						jsonObj = maskJsonObj(jsonObj, "Cardnumber", jsonObj.getString("Cardnumber"));
					}
					if (jsonObj.has("cardNumber")) {
						jsonObj = maskJsonObj(jsonObj, "cardNumber", jsonObj.getString("cardNumber"));
					}
					if (jsonObj.has("cardnumber")) {
						jsonObj = maskJsonObj(jsonObj, "cardnumber", jsonObj.getString("cardnumber"));
					}
					if (jsonObj.has("card_expiryMonth")) {
						jsonObj = maskJsonObj(jsonObj, "card_expiryMonth", jsonObj.getString("card_expiryMonth"));
					}
					if (jsonObj.has("card_expiryYear")) {
						jsonObj = maskJsonObj(jsonObj, "card_expiryYear", jsonObj.getString("card_expiryYear"));
					}
					if (jsonObj.has("card_cvv")) {
						jsonObj = maskJsonObj(jsonObj, "card_cvv", jsonObj.getString("card_cvv"));
					}
					validFields.put(key, jsonObj.toString());
					continue;
				}

			}
			
			if(key.equalsIgnoreCase("CARD_NUMBER") && value != null && !value.isEmpty()) {
				value = maskifyCardInfo(value);
			}
			if(key.equalsIgnoreCase("CVV") && value != null && !value.isEmpty()) {
				value = maskifyCardInfo(value);
			}
			if(key.equalsIgnoreCase("CARD_EXP_DT") && value != null && !value.isEmpty()) {
				value = maskifyCardInfo(value);
			}
			
			
			if (!key.isEmpty() && value != null && !value.isEmpty()) {
				validFields.put(key.toUpperCase(), value.toUpperCase());
			}
		}
		logger.info(message + " : " + validFields);
	}

	public static String maskify(String str) {
		StringBuffer buf = new StringBuffer(str);
		return new String(buf.replace(4, 12, "********"));
	}
	
	public static String maskifyCardInfo(String str) {
		StringBuffer buf = new StringBuffer(str);
		if (buf.length() >= 16) {
			return new String(buf.replace(4, 12, "********"));
		} else if (buf.length() == 3) {
			return new String(buf.replace(0, 3, "***"));
		} else if (buf.length() == 4) {
			return new String(buf.replace(0, 4, "****"));
		} else if (buf.length() == 2) {
			return new String(buf.replace(0, 2, "**"));
		}else if (buf.length() == 6) {
			return new String(buf.replace(0, 6, "******"));
		}else {
			return new String(buf.replace(0, 1, "*"));
		}
		
	}

	public static JSONObject maskJsonObj(JSONObject jsonObj, String paramName, String paramValue) {
		if (paramValue.length() >= 16) {
			jsonObj.put(paramName, maskify(paramValue));
		} else if (paramValue.length() == 3) {
			jsonObj.put(paramName, maskifyCVV(paramValue));
		} else if (paramValue.length() == 4) {
			jsonObj.put(paramName, maskifyExpireYear(paramValue));
		} else if (paramValue.length() == 2) {
			jsonObj.put(paramName, maskifyExpireMonth(paramValue));
		}
		
		return jsonObj;
	}

	
	private static String maskifyCVV(String paramValue) {
		StringBuffer buf = new StringBuffer(paramValue);
		return new String(buf.replace(0, 3, "***"));
	}

	private static String maskifyExpireYear(String paramValue) {
		StringBuffer buf = new StringBuffer(paramValue);
		return new String(buf.replace(0, 4, "****"));
	}
	private static String maskifyExpireMonth(String paramValue) {
		StringBuffer buf = new StringBuffer(paramValue);
		return new String(buf.replace(0, 2, "**"));
	}

	// added by sonu for remove to cardNumber from _FINAL_REQUEST
	public void logAllFieldsUsingMaskingOld(String message) {

		Map<String, String> validFields = new HashMap<String, String>();
		for (String key : fields.keySet()) {
			String value = fields.get(key);
			key.trim();
			value.trim();
			String finalRequest = null;
			if (key.endsWith("_FINAL_REQUEST")) {
				finalRequest = value.trim();

				JSONObject jsonObj = new JSONObject(finalRequest);
				int i = 0;
				if (jsonObj.has("card_number")) {

					String cardNumber = jsonObj.getString("card_number");
					if (cardNumber.length() >= 16) {
						i++;
						String markCardNumber = maskify(jsonObj.getString("card_number"));
						jsonObj.put("card_number", markCardNumber);
					}
				}

				if (i > 0) {
					validFields.put(key, jsonObj.toString());
					continue;
				}
			}

			if (!key.isEmpty() && !value.isEmpty()) {
				validFields.put(key.toUpperCase(), value.toUpperCase());
			}
		}
		logger.info(message + " : " + validFields);
	}

	public String removeCardDetailsForLogger(String allFieldsString) {

		allFieldsString = Pattern.compile("(name=\'ccnum\' value=)([\\s\\S]*?)(>)").matcher(allFieldsString)
				.replaceAll("$1***$3");
		allFieldsString = Pattern.compile("(name=\'ccvv\' value=)([\\s\\S]*?)(>)").matcher(allFieldsString)
				.replaceAll("$1***$3");
		allFieldsString = Pattern.compile("(name=\'ccexpyr\' value=)([\\s\\S]*?)(>)").matcher(allFieldsString)
				.replaceAll("$1***$3");
		allFieldsString = Pattern.compile("(name=\'ccexpmon\' value=)([\\s\\S]*?)(>)").matcher(allFieldsString)
				.replaceAll("$1***$3");
		allFieldsString = Pattern.compile("(name=\"pan\" value=\")([\\s\\S]*?)(\\\">)").matcher(allFieldsString)
				.replaceAll("$1$3");
		allFieldsString = Pattern.compile("(name=\"expiry\" value=\")([\\s\\S]*?)(\\\">)").matcher(allFieldsString)
				.replaceAll("$1$3");
		allFieldsString = Pattern.compile("(<card>)([\\s\\S]*?)(</card>)").matcher(allFieldsString).replaceAll("$1$3");
		allFieldsString = Pattern.compile("(<pan>)([\\s\\S]*?)(</pan>)").matcher(allFieldsString).replaceAll("$1$3");
		allFieldsString = Pattern.compile("(<expmonth>)([\\s\\S]*?)(</expmonth>)").matcher(allFieldsString)
				.replaceAll("$1$3");
		allFieldsString = Pattern.compile("(<expyear>)([\\s\\S]*?)(</expyear>)").matcher(allFieldsString)
				.replaceAll("$1$3");
		allFieldsString = Pattern.compile("(<cvv2>)([\\s\\S]*?)(</cvv2>)").matcher(allFieldsString).replaceAll("$1$3");
		allFieldsString = Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(allFieldsString)
				.replaceAll("$1$3");

		return allFieldsString;
	}

	public void addDefaultFields() throws SystemException {
		fields.put(FieldType.STATUS.getName(), StatusType.PENDING.getName());

		String transactionId = fields.get(FieldType.TXN_ID.getName());
		if (StringUtils.isEmpty(transactionId)) {
			fields.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		}

		fields.put(FieldType.KEY_ID.getName(), SystemConstants.DEFAULT_KEY_ID);
		// Changes by @Puneet removed acquirer addtion code as it is done later
		// in addAcquirerFields method
	}
	
	
	public void addPayoutDefaultFields() throws SystemException {
		fields.put(FieldType.STATUS.getName(), StatusTypePayout.REQUEST_ACCEPTED.getName());

		String transactionId = fields.get(FieldType.TXN_ID.getName());
		if (StringUtils.isEmpty(transactionId)) {
			fields.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		}

		fields.put(FieldType.KEY_ID.getName(), SystemConstants.DEFAULT_KEY_ID);
		
	}

	public Fields removeInternalFields() {
		Fields internalFields = new Fields();

		//logger.info("key Set : "+fields.keySet());
		for (String key : fields.keySet()) {
			if (key.startsWith(SystemConstants.FIELDS_PREFIX)) {
				internalFields.put(key, fields.get(key));
			}
		}
		//logger.info("internalFields 1 : "+internalFields.getFieldsAsString());
		for (String key : internalFields.keySet()) {
			fields.remove(key);
		}
		//logger.info("internalFields 2 : "+internalFields.getFieldsAsString());
		//logger.info("field final : "+fields);
		return internalFields;
	}// removeInternalFields()
	public Fields removeVpcFields() {
		Fields internalFields = new Fields();

		for (String key : fields.keySet()) {
			if (key.startsWith(SystemConstants.VPC_PREFIX)) {
				internalFields.put(key, fields.get(key));
			}
		}

		for (String key : internalFields.keySet()) {
			fields.remove(key);
		}

		return internalFields;
	}// removeInternalVPCFields()

	public void updateForAuthorization() throws SystemException {
		fieldsDao.insertTransaction(this);
		fieldsDao.updateNewOrder(this);
	}

	public void updateTransactionDetails() throws SystemException {
		fieldsDao.updateForAuthorization(this);
	}

	public String getCustomMDC() {
		String customMdc = fields.get(FieldType.INTERNAL_CUSTOM_MDC.getName());
		if (null == customMdc) {
			StringBuilder mdcBuilder = new StringBuilder();
			mdcBuilder.append(Constants.PG_LOG_PREFIX.getValue());
			mdcBuilder.append(FieldType.ORDER_ID.getName());
			mdcBuilder.append(Constants.EQUATOR.getValue());
			mdcBuilder.append(fields.get(FieldType.ORDER_ID.getName()));
			mdcBuilder.append(Constants.SEPARATOR.getValue());
			mdcBuilder.append(FieldType.PAY_ID.getName());
			mdcBuilder.append(Constants.EQUATOR.getValue());
			mdcBuilder.append(fields.get(FieldType.PAY_ID.getName()));
			mdcBuilder.append(Constants.SEPARATOR.getValue());
			mdcBuilder.append(FieldType.TXN_ID.getName());
			mdcBuilder.append(Constants.EQUATOR.getValue());
			mdcBuilder.append(fields.get(FieldType.TXN_ID.getName()));

			customMdc = mdcBuilder.toString();
			fields.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), customMdc);
		}
		return customMdc;
	}

	// to check a specific key-value exists or not
	public boolean contains(String fieldName) {
		return (fields.containsKey(fieldName) ? true : false);
	}

	public Map<String, Object> getSessionMap() {
		return sessionMap;
	}

	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

	public Fields refreshPreviousForsettlementKotak(Fields fields) throws SystemException {

		String oId = fields.get(FieldType.OID.getName());
		String status = StatusType.SENT_TO_BANK.getName();
		previous = fieldsDao.getFieldsForSettlement(oId, status);

		if (!(null != previous && previous.size() > 0)) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseMessage());
		} else {
			fields.previous = previous;
		}
		return previous;
	}

	public Fields refreshPreviousForSale(Fields fields) throws SystemException {
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String amount = fields.get(FieldType.AMOUNT.getName());
		amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		String status = StatusType.SENT_TO_BANK.getName();
		String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
		previous = fieldsDao.getFieldsForSaleTxn(orderId, payId, amount, txnType, status, pgRefNum);

		if (!(null != previous && previous.size() > 0)) {

			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.STATUS.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.NO_SUCH_TRANSACTION.getResponseMessage());

		} else {
			fields.previous = previous;
		}

		return previous;
	}

	public Fields refreshPreviousForLyraUpiSale(Fields fields) throws SystemException {
		String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
		String txnType = TransactionType.SALE.getName();
		String status = StatusType.SENT_TO_BANK.getName();
		fields.previous = fieldsDao.getFieldsForLyraUpi(pgRefNum, txnType, status);
		return previous;
	}

	/*
	 * public static String maskify1(String str) { StringBuffer buf = new
	 * StringBuffer(str); return new String(buf.replace(4, 12, "********")); }
	 */

	
}
