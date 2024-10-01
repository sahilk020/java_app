package com.pay10.batch.commons;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pay10.batch.RefundCommunicator;
import com.pay10.batch.autorefund.AutoRefund;
import com.pay10.batch.commons.util.DateCreater;
import com.pay10.batch.commons.util.TransactionManager;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.SystemException;
import com.pay10.batch.refundIrctc.RefundRequestCreater;
import com.pay10.commons.util.*;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class Fields {

	private Map<String, Object> sessionMap;
	private Map<String, String> fields = new HashMap<String, String>();
	@Autowired
	private FieldsDao fieldsDao;
	@Autowired
	private RefundCommunicator refundCommunicatior;
	@Autowired
	RefundRequestCreater refundRequestCreater;
	
	private Fields previous = null;
	
	private Fields refundPrevious = null;
	
	@Autowired
	private AutoRefund autoRefund;
	
	private boolean valid = true;
	private static Logger logger = LoggerFactory.getLogger(Fields.class.getName());

	public Fields(Map<String, String> fields) {
		this.fields.putAll(fields);
	}

	public Fields() {
	}
	
	public void insertNewRecoRecord() throws DatabaseException, JSONException, SystemException, com.pay10.commons.exception.SystemException {
		logger.info("Inserting reco record in transaction collection Fields : "+this.getFieldsAsString());
		if(Boolean.parseBoolean(this.get(FieldType.IS_ENROLL_ENROLLED.getName())) == true ||
				Boolean.parseBoolean(this.get(FieldType.IS_SALE_TIMEOUT.getName())) == true ||
				Boolean.parseBoolean(this.get(FieldType.IS_SALE_SENT_TO_BANK.getName())) == true) {
			this.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName());
			this.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			if(this.get(FieldType.RECO_ACQUIRER_TYPE.getName()).equals(AcquirerType.KOTAK.getCode())) {
				this.put(FieldType.RECO_ACQ_ID.getName(),this.get(FieldType.RRN.getName()));
			}
			this.put(FieldType.RECO_PG_TXN_MESSAGE.getName(), StatusType.CAPTURED.name());
			this.put(FieldType.RECO_POST_SETTLED_FLAG.getName(), Constants.Y_FLAG.getValue());
			this.put(FieldType.CREATE_DATE.getName(), DateCreater.addOneSecondInDate(previous.get(FieldType.CREATE_DATE.getName())));
			this.remove(FieldType.IS_ENROLL_ENROLLED.getName());
			this.remove(FieldType.IS_SALE_TIMEOUT.getName());
			this.remove(FieldType.IS_SALE_SENT_TO_BANK.getName());
			
			this.put(FieldType.ACQUIRER_TDR_SC.getName(), previous.get(FieldType.ACQUIRER_TDR_SC.getName()));
			this.put(FieldType.ACQUIRER_GST.getName(), previous.get(FieldType.ACQUIRER_GST.getName()));
			this.put(FieldType.PG_TDR_SC.getName(), previous.get(FieldType.PG_TDR_SC.getName()));
			this.put(FieldType.PG_GST.getName(), previous.get(FieldType.PG_GST.getName()));
		
			if(previous.contains(FieldType.CARD_HOLDER_NAME.getName()) && !StringUtils.isEmpty(previous.get(FieldType.CARD_HOLDER_NAME.getName()))) {
				this.put(FieldType.CARD_HOLDER_NAME.getName(), previous.get(FieldType.CARD_HOLDER_NAME.getName()));
			}

			fieldsDao.insertTransactionWithDate(this);
			this.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
			this.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName());
			// Done By chetan nagaria for change in settlement process to mark transaction as RNS
//			this.put(FieldType.STATUS.getName(), StatusType.SETTLED.getName());
			this.put(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());

			this.put(FieldType.PG_DATE_TIME.getName(), this.get(FieldType.CREATE_DATE.getName()));
			this.put(FieldType.CREATE_DATE.getName(), this.get(FieldType.UPDATE_DATE.getName()));
			
			//added for testing purpose
			this.put(FieldType.CUST_PHONE.getName(), this.get(FieldType.CUST_PHONE.getName()));
			
			fieldsDao.insertTransaction(this);
			autoRefund.callPostSettlementAutoRefund(this);
		} else if(Boolean.parseBoolean(this.get(FieldType.IS_REFUND_TIMEOUT.getName())) == true) {
			//this.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
			this.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName());
			this.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			this.put(FieldType.RECO_PG_TXN_MESSAGE.getName(), StatusType.CAPTURED.name());
			this.put(FieldType.RECO_POST_SETTLED_FLAG.getName(), Constants.Y_FLAG.getValue());
			this.put(FieldType.CREATE_DATE.getName(), previous.get(FieldType.CREATE_DATE.getName()));
			this.put(FieldType.UPDATE_DATE.getName(), DateCreater.formatDateForDb(new Date()));
			this.remove(FieldType.IS_REFUND_TIMEOUT.getName());
			
			this.put(FieldType.ACQUIRER_TDR_SC.getName(), previous.get(FieldType.ACQUIRER_TDR_SC.getName()));
			this.put(FieldType.ACQUIRER_GST.getName(), previous.get(FieldType.ACQUIRER_GST.getName()));
			this.put(FieldType.PG_TDR_SC.getName(), previous.get(FieldType.PG_TDR_SC.getName()));
			this.put(FieldType.PG_GST.getName(), previous.get(FieldType.PG_GST.getName()));
			
			
			fieldsDao.insertTransactionWithDate(this);
			this.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
			this.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUNDRECO.getName());
			// Done By chetan nagaria for change in settlement process to mark transaction as RNS
//			this.put(FieldType.STATUS.getName(), StatusType.SETTLED.getName());
			this.put(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());
			this.put(FieldType.PG_DATE_TIME.getName(), this.get(FieldType.CREATE_DATE.getName()));			
			this.put(FieldType.CREATE_DATE.getName(), this.get(FieldType.UPDATE_DATE.getName()));
			this.put(FieldType.CUST_PHONE.getName(), this.get(FieldType.CUST_PHONE.getName()));
			
			fieldsDao.insertTransaction(this);
		}
		else if(Boolean.parseBoolean(this.get(FieldType.IS_RECO_SETTLED.getName())) == true && 
				Boolean.parseBoolean(this.get(FieldType.IS_REFUND_PENDING.getName())) == true) {
			fieldsDao.insertTransaction(this);			
			JSONObject refundValue = refundRequestCreater.createNewRefundIrctcObject(this.getRefundPrevious());
			String response = refundCommunicatior.communicator(refundValue, this.getRefundPrevious());
			logger.info("refund API  response received from pg ws " + response);
		} else {
			fieldsDao.insertTransaction(this);
		}
	}
	
	public void insertNewRecoReportingRecord() throws DatabaseException {
		logger.info("Inserting reco exception record in reporting collection");
		fieldsDao.insertRecoReportingFields(this);
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
	//removeInternalExt_AcqFields()
		public Fields removeExtAcqFields() {
			Fields internalFields = new Fields();

			for (String key : fields.keySet()) {
				if (key.startsWith(Constants.EXT_ACQ_PREFIX.getValue())) {
					internalFields.put(key, fields.get(key));
				}
			}

			for (String key : internalFields.keySet()) {
				fields.remove(key);
			}

			return internalFields;
		}// removeInternalExt_AcqFields()

	//Remove keys when value/key is null/spaces
	public void clean(){
		Map<String, String> validFields = new HashMap<String, String>();
		for (String key : fields.keySet()) {
			
			String value = fields.get(key);
			if(null == value){
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

	public Fields getRefundPrevious() {
		return refundPrevious;
	}

	public void setRefundPrevious(Fields refundPrevious) {
		this.refundPrevious = refundPrevious;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public int size(){
		return fields.size();
	}
	
	public String getFieldsAsString() {

		StringBuilder allFieldsSum = new StringBuilder();
		allFieldsSum.append("\n");
		for (String key : fields.keySet()) {
			allFieldsSum.append(key);
			allFieldsSum.append("=");
			allFieldsSum.append(fields.get(key));
			allFieldsSum.append("~");
		}
		return allFieldsSum.toString();
	}
	
	public void logAllFields(String message){
				
		//Do not log card details, as this is a security issue
		Map<String, String> secureFields = removeSecureFields();
		
		StringBuilder allFieldsSum = new StringBuilder();
		allFieldsSum.append(message);
		allFieldsSum.append("\n");
		for(String key: fields.keySet()){
			allFieldsSum.append(key);
			allFieldsSum.append(" = ");
			allFieldsSum.append(fields.get(key));
			allFieldsSum.append("~");
		}
		
		//Put secure details back in the collection
		putAll(secureFields);		
		
		MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), this.getCustomMDC());
		logger.info(allFieldsSum.toString());
	}
	
	public Map<String, String> removeSecureFields(){
		
		Map<String, String> secureFields = new HashMap<String, String>();
		
		String cardNumber = remove(FieldType.CARD_NUMBER.getName());
		if(null != cardNumber){
			secureFields.put(FieldType.CARD_NUMBER.getName(), cardNumber);
		}
		
		String cardExpiryDate = remove(FieldType.CARD_EXP_DT.getName());
		if(null != cardExpiryDate){
			secureFields.put(FieldType.CARD_EXP_DT.getName(), cardExpiryDate);
		}
		
		String cvv = remove(FieldType.CVV.getName());
		if(null != cvv){
			secureFields.put(FieldType.CVV.getName(), cvv);
		}
		
		String password = remove(FieldType.PASSWORD.getName());
		if(null != password){
			secureFields.put(FieldType.PASSWORD.getName(), password);
		}
		
		return secureFields;
	}

	public String getCustomMDC(){
		String customMdc = fields.get(FieldType.INTERNAL_CUSTOM_MDC.getName());
		if(null == customMdc){
			StringBuilder mdcBuilder = new StringBuilder();
			mdcBuilder.append(Constants.PG_LOG_PREFIX.getValue());
			mdcBuilder.append(FieldType.RECO_ORDER_ID.getName());
			mdcBuilder.append(Constants.EQUATOR.getValue());
			mdcBuilder.append(fields.get(FieldType.RECO_ORDER_ID.getName()));
			mdcBuilder.append(Constants.SEPARATOR.getValue());
			mdcBuilder.append(FieldType.RECO_PAY_ID.getName());
			mdcBuilder.append(Constants.EQUATOR.getValue());
			mdcBuilder.append(fields.get(FieldType.RECO_PAY_ID.getName()));
			mdcBuilder.append(Constants.SEPARATOR.getValue());
			mdcBuilder.append(FieldType.TXN_ID.getName());
			mdcBuilder.append(Constants.EQUATOR.getValue());
			mdcBuilder.append(fields.get(FieldType.TXN_ID.getName()));

			customMdc = mdcBuilder.toString();
			fields.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), customMdc);
		}
		return customMdc;
	}
	
	//to check a specific key-value exists or not
	public boolean contains(String fieldName){
		return (fields.containsKey(fieldName) ? true : false);
	}

	public Map<String, Object> getSessionMap() {
		return sessionMap;
	}

	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

	public Fields refreshPreviousBasedOnTransactionId() throws DatabaseException {
		String txnId = fields.get(FieldType.ORIG_TXN_ID.getName());
		previous = fieldsDao.getPreviousFields(txnId);
		return previous;
	}

	public List<Fields> getPreviousSaleOrRecoForPgRefNum(String pg_ref_num) throws DatabaseException {
		return this.fieldsDao.getPreviousSaleOrRecoForPgRefNum(pg_ref_num);
	}
	
	public List<Fields> getPreviousRecoForPgRefNum(String pg_ref_num) throws DatabaseException {
		return this.fieldsDao.getPreviousRecoOrPendingForPgRefNum(pg_ref_num);
	}

	public List<Fields> getPreviousRefundOrRecoForPgRefNum(String pg_ref_num) throws DatabaseException {
		return this.fieldsDao.getPreviousRefundOrRecoForPgRefNum(pg_ref_num);
	}
	
	public List<Fields> getPreviousRefundTxnForPgRefNum(String pg_ref_num) throws DatabaseException {
		return this.fieldsDao.getPreviousRefundTxnForPgRefNum(pg_ref_num);
	}
	
	public List<Fields> getPreviousSettlementOrRecoForPgRefNum(String pg_ref_num) throws DatabaseException {
		return this.fieldsDao.getPreviousSettlementOrRecoForPgRefNum(pg_ref_num);
	}
	public List<Fields> getPreviousRefundForPgRefNum(String pg_ref_num) throws DatabaseException {
		return this.fieldsDao.getPreviousRefundForPgRefNum(pg_ref_num);
	}
	
	public List<Fields> getPreviousRefundForRefundOrderId(String refundOrderId, String orderId) throws DatabaseException {
		return this.fieldsDao.getPreviousRefundForRefundOrderId(refundOrderId,orderId);
	}
	
	public List<Fields> getPreviousSettlementOrSaleForPgRefNum(String pg_ref_num) throws DatabaseException {
		return this.fieldsDao.getPreviousSettlementOrSaleForPgRefNum(pg_ref_num);
	}
	
	public List<Fields> getPreviousEnrolledOrSaleForPgRefNum(String pg_ref_num) throws DatabaseException {
		return this.fieldsDao.getPreviousEnrolledOrSaleForPgRefNum(pg_ref_num);
	}
	
	public List<Fields> getPreviousSaleForPgRefNum(String pg_ref_num) throws DatabaseException {
		return this.fieldsDao.getPreviousSaleForPgRefNum(pg_ref_num);
	}
	
	public List<Fields> getPreviousRecoSettledForPgRefNum(String pg_ref_num) throws DatabaseException {
		return this.fieldsDao.getPreviousRecoSettledForPgRefNum(pg_ref_num);
	}
}
