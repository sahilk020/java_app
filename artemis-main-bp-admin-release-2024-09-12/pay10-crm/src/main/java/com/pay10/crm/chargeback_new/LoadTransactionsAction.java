package com.pay10.crm.chargeback_new;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Chargeback;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.chargeback_new.action.beans.ChargebackDao;
import com.pay10.crm.chargeback_new.util.ChargebackType;

public class LoadTransactionsAction extends AbstractSecureAction {

	private static final long serialVersionUID = -5044276136152533162L;
	
	private String pgRefNum;
	private String acqId;
	private String orderId;
	private String emailId;
	private String cbType;
	private int responsecode;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	ChargebackDao cbDao;
	
	@Autowired
	private MongoInstance mongoInstance;
	
	private static final String prefix = "MONGO_DB_";
	private List<TransactionSearch> aaData;
	

	@Override
	public String execute() {
		List<BasicDBObject> orConditionList1 = new ArrayList<>();
		List<BasicDBObject> orConditionList2 = new ArrayList<>();
		List<BasicDBObject> andConditionList = new ArrayList<>();
		
		if((getPgRefNum() != null) && (!(getPgRefNum().equalsIgnoreCase(""))))
			orConditionList1.add(new BasicDBObject("PG_REF_NUM",getPgRefNum()));
		
		if((getAcqId() != null) && (!(getAcqId().equalsIgnoreCase(""))))
			orConditionList1.add(new BasicDBObject("ACQ_ID", getAcqId()));
		
		if((getOrderId() != null) && (!(getOrderId().equalsIgnoreCase(""))))
			orConditionList1.add(new BasicDBObject("ORDER_ID",getOrderId()));
			
		if((getEmailId() != null) && (!(getEmailId().equalsIgnoreCase(""))))
			orConditionList1.add(new BasicDBObject("CUST_EMAIL", getEmailId()));
		
		orConditionList1.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), "SALE"));
		BasicDBObject statusObj = new BasicDBObject("STATUS","Captured");
		orConditionList2.add(statusObj);
		andConditionList.add(new BasicDBObject("$or", orConditionList2));
		andConditionList.add(new BasicDBObject("$and", orConditionList1));

		BasicDBObject finalQuery = new BasicDBObject("$and", andConditionList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		FindIterable<Document> output = collection.find(finalQuery);
//		output.allowDiskUse(true); 
		MongoCursor<Document> cursor = output.iterator();
		
		statusObj = new BasicDBObject("STATUS","Settled");
		orConditionList2.clear();
		orConditionList2.add(statusObj);
		
		finalQuery = new BasicDBObject("$and", andConditionList);
		FindIterable<Document> output1 = collection.find(finalQuery);
		MongoCursor<Document> cursor1 = output1.iterator();
		Map<String, Document> capturedSet = new HashMap<>();
		Map<String, Document> settledSet = new HashMap<>();
		
		int itr = 0;
		while(cursor.hasNext()) {
			++itr;
			Document doc = cursor.next();
			capturedSet.put(doc.getString(FieldType.ORDER_ID.toString()), doc);
		}
		cursor.close();
		while(cursor1.hasNext()) {
			++itr;
			Document doc = cursor1.next();
			settledSet.put(doc.getString(FieldType.ORDER_ID.toString()), doc);
		}
		cursor1.close();
		
		aaData = new ArrayList<>();
		if(capturedSet.size() > settledSet.size()) {
			for(String key : capturedSet.keySet()) {
				if(settledSet.containsKey(key)) {
					aaData.add(getTransaction(settledSet.get(key)));
				}else {
					aaData.add(getTransaction(capturedSet.get(key)));
				}
			}
		}else {
			for(String key : settledSet.keySet()) {
				if(capturedSet.containsKey(key)) {
					aaData.add(getTransaction(capturedSet.get(key)));
				}else {
					aaData.add(getTransaction(settledSet.get(key)));
				}
			}
		}
		setAaData(aaData);
		responsecode = 200;
		return SUCCESS;
	}
	
	private TransactionSearch getTransaction(Document dbObj) {
		TransactionSearch transReport = new TransactionSearch();
		// Check whether old chargeback exists or not.
		
		if(!StringUtils.isEmpty(getPgRefNum()) || !StringUtils.isEmpty(getAcqId()) || !StringUtils.isEmpty(getOrderId())) {
			String pgRefNum = dbObj.getString(FieldType.PG_REF_NUM.toString());
			List<Chargeback> oldCbList = cbDao.getListByPgRefNum(pgRefNum);
			boolean cbFlag = false;
			boolean preArbFlag = false;
			boolean fraudFlag = false;
			if(oldCbList != null && !oldCbList.isEmpty()) {
				for(Chargeback c : oldCbList) {
					if(c.getChargebackType().equals(ChargebackType.CHARGEBACK.getName())) {
						cbFlag = true;
						String caseId = c.getCaseId();
						if(caseId != null && !caseId.equals("")) {
							transReport.setCaseId(caseId);
						}
					}else if(c.getChargebackType().equals(ChargebackType.PRE_ARBITRATION.getName())) {
						preArbFlag = true;
					}else if(c.getChargebackType().equals(ChargebackType.FRAUD_DISPUTES.getName())) {
						String caseId = c.getCaseId();
						if(caseId != null && !caseId.equals("")) {
							transReport.setCaseId(caseId);
						}
						fraudFlag = true;
					} 
				}
			}
			
			if(cbFlag && preArbFlag && fraudFlag) {
				transReport.setResponseCode("111");
				transReport.setResponseMessage("Chargeback, Pre arbitration and fraud exists.");
			}else if(cbFlag && preArbFlag && !fraudFlag) {
				transReport.setResponseCode("110");
				transReport.setResponseMessage("Chargeback and Pre arbitration exists.");
			}else if(cbFlag && !preArbFlag && fraudFlag) {
				transReport.setResponseCode("101");
				transReport.setResponseMessage("Chargeback and fraud exists.");
			}else if(cbFlag && !preArbFlag && !fraudFlag) {
				transReport.setResponseCode("100");
				transReport.setResponseMessage("Chargeback already exists.");
			}else if(!cbFlag && preArbFlag && fraudFlag) {
				transReport.setResponseCode("011");
				transReport.setResponseMessage("Pre arbitration and fraud exists.");
			}else if(!cbFlag && preArbFlag && !fraudFlag) {
				transReport.setResponseCode("010");
				transReport.setResponseMessage("Pre arbitration already exists.");
			}else if(!cbFlag && !preArbFlag && fraudFlag) {
				transReport.setResponseCode("001");
				transReport.setResponseMessage("Fraud already exists.");
			}else {
				transReport.setResponseCode("000");
				transReport.setResponseMessage("");
			}
		}


		String payId = dbObj.getString(FieldType.PAY_ID.toString());
		transReport.setPayId(payId);
		
		// Get merchant name by payId.
		String merchantName = getMerchantName(payId);
		transReport.setBusinessName(merchantName);
		transReport.setTransactionIdString(dbObj.getString(FieldType.TXN_ID.toString()));
		transReport.setPgRefNum(dbObj.getString(FieldType.PG_REF_NUM.toString()));
		transReport.setOrderId(dbObj.getString(FieldType.ORDER_ID.toString()));
		
		BigDecimal amount = new BigDecimal(dbObj.getString(FieldType.AMOUNT.toString()));
		transReport.setAmount(amount.toString());
		transReport.setCustomerEmail(dbObj.getString(FieldType.CUST_EMAIL.toString()));
		transReport.setCustomerPhone(dbObj.getString(FieldType.CUST_PHONE.toString()));
		
		transReport.setAcqId(dbObj.getString(FieldType.ACQ_ID.toString()));
		transReport.setCreateDate(dbObj.getString(FieldType.CREATE_DATE.toString())); // Transaction create date.
		
		transReport.setCurrency(dbObj.getString(FieldType.CURRENCY_CODE.toString()));
		
		transReport.setPgTdrSc(dbObj.getString(FieldType.PG_TDR_SC.toString()));
		transReport.setAcquirerTdrCalculate(dbObj.getString(FieldType.ACQUIRER_TDR_SC.toString()));
		transReport.setPgGst(dbObj.getString(FieldType.PG_GST.toString()));
		transReport.setAcquirerGst(dbObj.getString(FieldType.ACQUIRER_GST.toString()));
		transReport.setStatus(dbObj.getString(FieldType.STATUS.toString())); // Transaction status.
		
		transReport.setPaymentMethods(dbObj.getString(FieldType.PAYMENT_TYPE.toString()));
		transReport.setMopType(dbObj.getString(FieldType.MOP_TYPE.toString()));
		transReport.setCardMask(dbObj.getString(FieldType.CARD_MASK.toString()));
		transReport.setInternalCardIssusserBank(dbObj.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
		transReport.setInternalCardIssusserCountry(dbObj.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
		transReport.setCustomerCountry(dbObj.getString(FieldType.INTERNAL_CUST_COUNTRY_NAME.toString()));
		transReport.setInternalCustIP(dbObj.getString(FieldType.INTERNAL_CUST_IP.toString()));
		return transReport;
	}

	Map<String, String> merchantMap = new HashMap<>();
	private String getMerchantName(String payId) {
		if(merchantMap.containsKey(payId)) {
			return merchantMap.get(payId);
		}
		User user = userDao.findPayId(payId);
		if(user != null && user.getBusinessName() != null) {
			merchantMap.put(payId, user.getBusinessName());
		}else {
			merchantMap.put(payId, "-");
		}
		
		return merchantMap.get(payId);
	}

	public List<TransactionSearch> getAaData() {
		return aaData;
	}


	public void setAaData(List<TransactionSearch> aaData) {
		this.aaData = aaData;
	}


	public String getPgRefNum() {
		return pgRefNum;
	}


	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}


	public String getAcqId() {
		return acqId;
	}


	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}


	public String getOrderId() {
		return orderId;
	}


	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public int getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(int responsecode) {
		this.responsecode = responsecode;
	}

	public String getCbType() {
		return cbType;
	}

	public void setCbType(String cbType) {
		this.cbType = cbType;
	}

}

