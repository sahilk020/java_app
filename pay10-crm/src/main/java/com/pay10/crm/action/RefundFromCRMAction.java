package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.CashfreeRefundDTO;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.crm.audittrail.dto.RefundPayload;

public class RefundFromCRMAction extends AbstractSecureAction {

	private static final long serialVersionUID = 149102749485831816L;

	private static Logger logger = LoggerFactory.getLogger(RefundFromCRMAction.class.getName());

	private String orderId;
	private String response;
	private String refundAmount;
	private String saleAmount;

	public String getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(String saleAmount) {
		this.saleAmount = saleAmount;
	}

	private String pgRefNum;
	private String refundOrderId;
	private String currencyCode;
	private String refundFlag;
	private String payId;
	private String refundDate;

	private User sessionUser = new User();

	@Autowired
	private CrmValidator validator;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private AuditTrailService auditTrailService;

	private static final String prefix = "MONGO_DB_";

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {

		try {

			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			String validationResponse = checkRefund();
			//String pendingValidationResponse = checkPendingRefund();
			
			/*
			 * if (pendingValidationResponse.equalsIgnoreCase("ERROR")) { return SUCCESS; }
			 */
			
			if (validationResponse.equalsIgnoreCase("ERROR")) {
				return SUCCESS;
			}
			refund();

			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
			AuditTrail auditTrail = new AuditTrail(getPayload(), null, actionMessagesByAction.get("refundFromCrm"));
			auditTrailService.saveAudit(request, auditTrail);

			return SUCCESS;

		} catch (Exception exception) {
			logger.error("Exception in RefundFromCRMAction Class, execute method ", exception);
		}

		return SUCCESS;
	}

	private String checkPendingRefund() {
		
		MongoCursor<Document> cursor = null;
		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
			findQuery.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), "CASHFREE"));
			findQuery.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			findQuery.add(new BasicDBObject(FieldType.STATUS.getName(), "Pending"));
			findQuery.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			BasicDBObject fetchQuery = new BasicDBObject("$and", findQuery);

			logger.info("Query for find to check refund status by Id : " + findQuery);
			FindIterable<Document> output = collection.find(fetchQuery);
			cursor = output.iterator();

			if (cursor.hasNext()) {
				Document documentObj = (Document) cursor.next();
				logger.info("documentObj "+documentObj.toString());
				if (null != documentObj) {
					setResponse("Refund already initiated, Now is in pending state !!");
					return "ERROR";
				}
			}
			return SUCCESS;
		} catch (Exception exception) {
			String message = "Error while refund from crm portal ";
			logger.error(message, exception);
		} finally {
			cursor.close();
		}
		return SUCCESS;
	}

	private String getPayload() throws JsonProcessingException {
		RefundPayload payload = new RefundPayload();
		payload.setOrderId(getOrderId());
		payload.setResponse(getResponse());
		payload.setRefundAmount(getRefundAmount());
		payload.setSaleAmount(getSaleAmount());
		payload.setPgRefNum(getPgRefNum());
		payload.setRefundOrderId(getRefundOrderId());
		payload.setCurrencyCode(getCurrencyCode());
		payload.setRefundFlag(getRefundFlag());
		payload.setPayId(getPayId());
		payload.setRefundDate(getRefundDate());
		return mapper.writeValueAsString(payload);
	}

	public String refund() {
		try {

			Fields fields = new Fields();
			fields.put(FieldType.ORDER_ID.getName(), getOrderId());
			fields.put(FieldType.REFUND_FLAG.getName(), getRefundFlag());
			fields.put(FieldType.AMOUNT.getName(),
					(Amount.formatAmount(getRefundAmount(), Currency.getNumericCode(getCurrencyCode()))));
			fields.put(FieldType.PG_REF_NUM.getName(), getPgRefNum());
			fields.put(FieldType.REFUND_ORDER_ID.getName(), getRefundOrderId());
			fields.put(FieldType.CURRENCY_CODE.getName(), Currency.getNumericCode(getCurrencyCode()));
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName());
			fields.put(FieldType.PAY_ID.getName(), getPayId());
			if (sessionUser.getUserType().equals(UserType.RESELLER)) {
				fields.put(FieldType.RESELLER_ID.getName(), sessionUser.getPayId());
			}
			fields.put(FieldType.HASH.getName(), Hasher.getHash(TransactionManager.getNewTransactionId()));

			logger.info("Crm Refund Request : " + fields.getFieldsAsString() + " By User " + sessionUser.getEmailId());

			String refundurl = Constants.TXN_WS_InterNalRefund.getValue();
			Map<String, String> response = transactionControllerServiceProvider.transact(fields, refundurl);
			logger.info("Crm Refund Response " + response);
			if (response.isEmpty()) {
				setResponse("Refund not initiated !!");
			} else {
				setResponse("Response Status is :" + response.get(FieldType.STATUS.getName()));
			}

		} catch (JSONException exception) {
			logger.error("Exception in RefundRejectionReportAction Class, refund function ", exception);
		} catch (SystemException exception) {
			setResponse("unsettled amount is less then refund amount !!");

			logger.error("Exception in RefundRejectionReportAction Class, refund method ", exception);
		}
		return SUCCESS;
	}

	public String checkRefund() {

		logger.info("##################### checkRefund ####################"+getOrderId());
		String holdReleaseResp = checkRefundReleaseFlag();
		logger.info("checkRefund, holdReleaseResp= {}, OrderId={}",holdReleaseResp,getOrderId());
		if(null != holdReleaseResp && holdReleaseResp.equalsIgnoreCase("1")) {
			setResponse("Refund not initiated due to transaction in hold state");
			return "ERROR";
		}
		
		Double saleAmount = 0.0;
		Double refundAmount = Double.valueOf(getRefundAmount());

		// If user has not provided the refund order , add auto-gen
		if (!StringUtils.isNotBlank(getRefundOrderId())) {
			setRefundOrderId(TransactionManager.getNewTransactionId());
		}

		// Query to Check for sale and refund transactions
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

		List<String> statusList = new ArrayList<>();
		statusList.add(StatusType.CAPTURED.getName());
		statusList.add(StatusType.FORCE_CAPTURED.getName());
		statusList.add(StatusType.SETTLED_RECONCILLED.getName());
		
		BasicDBObject currencyQuery = new BasicDBObject(FieldType.CURRENCY_CODE.getName(),
				Currency.getNumericCode(getCurrencyCode()));
		BasicDBObject orderIdQuery = new BasicDBObject(FieldType.ORDER_ID.getName(), getOrderId());
		//BasicDBObject statusQuery = new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		BasicDBObject responseCodeQuery = new BasicDBObject(FieldType.RESPONSE_CODE.getName(),
				ErrorType.SUCCESS.getCode());

		paramConditionLst.add(currencyQuery);
		paramConditionLst.add(orderIdQuery);
		//paramConditionLst.add(statusQuery);
		paramConditionLst.add(new BasicDBObject("STATUS", new BasicDBObject("$in", statusList)));
		paramConditionLst.add(responseCodeQuery);

		// initialize Mongo collection and DB instance
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		// If user has not provided the refund order , add auto-gen
		if (!StringUtils.isNotBlank(getRefundOrderId())) {
			setRefundOrderId(TransactionManager.getNewTransactionId());
		} else {

			// Query to Check if Refund Order Id Already Present
			List<BasicDBObject> refundConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject refundOrderIdQuery = new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), getOrderId());
			refundConditionLst.add(currencyQuery);
			refundConditionLst.add(refundOrderIdQuery);
			//refundConditionLst.add(statusQuery);
			refundConditionLst.add(new BasicDBObject("STATUS", new BasicDBObject("$in", statusList)));
			refundConditionLst.add(responseCodeQuery);

			BasicDBObject finalRefundQuery = new BasicDBObject("$and", refundConditionLst);

			Long count = coll.count(finalRefundQuery);
			if (count > 0) {
				setResponse("Refund Order Id already present!");
				return "ERROR";
			}
		}

		BasicDBObject finalQuery = new BasicDBObject("$and", paramConditionLst);

		Long count = coll.count(finalQuery);
		if (count < 1) {
			setResponse("No Transaction Found");
			return "ERROR";
		}

		else {

			FindIterable<Document> iterator = coll.find(finalQuery);
			MongoCursor<Document> cursor = iterator.iterator();
			
			boolean saleCaptured = true;
			while (cursor.hasNext()) {

				Document doc = cursor.next();

				/*
				 * if
				 * (doc.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.
				 * SALE.getName())) { saleAmount = saleAmount +
				 * Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName())); }
				 */
				if (saleCaptured  && doc.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
					saleAmount = saleAmount + Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
					saleCaptured = false;
				}
				
				if (saleCaptured  && doc.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.RECO.getName())
						&& doc.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName())) {
					saleAmount = saleAmount + Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
					saleCaptured = false;
				}
				
				if (saleCaptured  && doc.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.RECO.getName())
						&& doc.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.FORCE_CAPTURED.getName())) {
					saleAmount = saleAmount + Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
					saleCaptured = false;
				}
				
				if (saleCaptured  && doc.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.RECO.getName())
						&& doc.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())) {
					saleAmount = saleAmount + Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
					saleCaptured = false;
				}

				if (doc.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
					refundAmount = refundAmount + Double.valueOf(doc.getString(FieldType.AMOUNT.getName()));
				}

				setPayId(doc.getString(FieldType.PAY_ID.getName()));
			}

			if (refundAmount > saleAmount) {
				setResponse("Refund Amount more than Sale Amount");
				return "ERROR";
			}

			if (saleAmount.compareTo(refundAmount) == 0) {
				setRefundFlag("R");
			} else {
				setRefundFlag("C");
			}

			return "SUCCESS";
		}

	}

	public String checkRefundReleaseFlag() {

		logger.info("check Refund Hold-Release Flag, OrderId={}", getOrderId());
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

		BasicDBObject orderIdQuery = new BasicDBObject(FieldType.ORDER_ID.getName(), getOrderId());
		paramConditionLst.add(orderIdQuery);

		// initialize Mongo collection and DB instance
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		BasicDBObject finalQuery = new BasicDBObject("$and", paramConditionLst);

		FindIterable<Document> iterator = coll.find(finalQuery);
		MongoCursor<Document> cursor = iterator.iterator();

		String holdReleaseFlag = null;

		while (cursor.hasNext()) {
			Document doc = cursor.next();
			if (null != doc.getString(FieldType.HOLD_RELEASE.getName())
					&& doc.getString(FieldType.HOLD_RELEASE.getName()).equalsIgnoreCase("1")) {
				holdReleaseFlag = doc.getString(FieldType.HOLD_RELEASE.getName());
			}

		}
		return holdReleaseFlag;
	}
	
	@Override
	public void validate() {
		if (validator.validateBlankField(getOrderId())) {
		} else if (!validator.validateField(CrmFieldType.ORDER_ID, getOrderId())) {
			addFieldError(CrmFieldType.ORDER_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
			setResponse("Invalid Order Id");
		}

		if (validator.validateBlankField(getPgRefNum())) {
		} else if (!validator.validateField(CrmFieldType.PG_REF_NUM, getPgRefNum())) {
			addFieldError(CrmFieldType.PG_REF_NUM.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
			setResponse("Invalid Pg Ref Num");
		}

		if (validator.validateBlankField(getRefundAmount())) {
		} else if (!validator.validateField(CrmFieldType.AMOUNT, getRefundAmount())) {
			addFieldError(CrmFieldType.AMOUNT.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
			setResponse("Invalid Amount");
		}

		if (validator.validateBlankField(getCurrencyCode())) {
		} else if (!validator.validateField(CrmFieldType.CURRENCY, getCurrencyCode())) {
			addFieldError(CrmFieldType.CURRENCY.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
			setResponse("Invalid Currency");
		}

		if (StringUtils.isNotBlank(getRefundOrderId())) {
			if (!validator.validateField(CrmFieldType.ORDER_ID, getRefundOrderId())) {
				addFieldError(CrmFieldType.ORDER_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
				setResponse("Invalid Refund Order Id");
			}
		}
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getRefundOrderId() {
		return refundOrderId;
	}

	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getRefundFlag() {
		return refundFlag;
	}

	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(String refundDate) {
		this.refundDate = refundDate;
	}

}
