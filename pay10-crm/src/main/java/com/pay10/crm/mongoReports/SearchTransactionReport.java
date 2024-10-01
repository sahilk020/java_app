package com.pay10.crm.mongoReports;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.SearchTransaction;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;

@Component
public class SearchTransactionReport {

	private static Logger logger = LoggerFactory.getLogger(SearchTransactionReport.class.getName());

	@Autowired
	private UserDao userdao;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;
	
	private static final String prefix = "MONGO_DB_";
	private static Map<String,User> userMap = new HashMap<String, User>();
	public List<SearchTransaction> searchPayment(String orderId, String pgRefNum,String acqId, String customerEmail,String customerPhone,User sessionuser) {
		List<SearchTransaction> transactionList = new ArrayList<SearchTransaction>();
        
		try
		{
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		if (!StringUtils.isBlank(orderId)) {
		obj.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		}
		if (!StringUtils.isBlank(pgRefNum)) {
		obj.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		}
		if (!StringUtils.isBlank(acqId)) {
			obj.add(new BasicDBObject(FieldType.ACQ_ID.getName(), acqId));
			}
		if (!StringUtils.isBlank(customerEmail)) {
		obj.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
		}
		if (!StringUtils.isBlank(customerPhone)) {
			obj.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone));
		}
		andQuery.put("$and", obj);
		
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix+Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).sort(new BasicDBObject(FieldType.CREATE_DATE.getName(),-1)).iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();

			SearchTransaction searchTxn = new SearchTransaction();
			User user = new User();
			if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null){
				user = userMap.get(doc.getString(FieldType.PAY_ID.toString()));
			}
			else {
				user = userdao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
				userMap.put(doc.getString(FieldType.PAY_ID.toString()), user);
			}
			
			
			BigInteger txnID = new BigInteger(doc.getString(FieldType.TXN_ID.toString()));
			searchTxn.setTransactionId((txnID));
			searchTxn.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
			searchTxn.setMerchant(user.getBusinessName());
			searchTxn.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
			searchTxn.settDate(doc.getString(FieldType.CREATE_DATE.toString()));
			
			if (null != doc.getString(FieldType.CUST_EMAIL.toString())) {
				searchTxn.setCustomerEmail(doc.getString(FieldType.CUST_EMAIL.toString()));
			} else {
				searchTxn.setCustomerEmail(CrmFieldConstants.NA.getValue());
			}
			if (null != doc.getString(FieldType.CUST_PHONE.toString())) {
				searchTxn.setCustomerPhone(doc.getString(FieldType.CUST_PHONE.toString()));
			} else {
				searchTxn.setCustomerPhone(CrmFieldConstants.NA.getValue());
			}
			
			if (StringUtils.isBlank(doc.getString(FieldType.PAYMENT_TYPE.toString()))) {
				searchTxn.setPaymentType(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setPaymentType(doc.getString(FieldType.PAYMENT_TYPE.toString()));
			}
			
			if (StringUtils.isBlank(doc.getString(FieldType.MOP_TYPE.toString()))) {
				searchTxn.setMopType(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
			}
			if(doc.getString(FieldType.TXNTYPE.toString()).equals(TransactionType.RECO.getName())) {
				searchTxn.setTxnType(TransactionType.SALE.getName());
			} else if(doc.getString(FieldType.TXNTYPE.toString()).equals(TransactionType.REFUNDRECO.getName())) {
				searchTxn.setTxnType(TransactionType.REFUND.getName());
			} else {
				searchTxn.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
			}
			
			
			
			if (StringUtils.isBlank(doc.getString(FieldType.CARD_MASK.toString()))) {
				searchTxn.setCardNum(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setCardNum(doc.getString(FieldType.CARD_MASK.toString()));
			}
			
			searchTxn.setStatus(doc.getString(FieldType.STATUS.toString()));
			searchTxn.setAmount(doc.getString(FieldType.AMOUNT.toString()));		
			searchTxn.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));	
			if (StringUtils.isBlank(doc.getString(FieldType.CARD_HOLDER_NAME.toString()))) {
				searchTxn.setCustName(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setCustName(doc.getString(FieldType.CARD_HOLDER_NAME.toString()));
			}
				
			
			if (StringUtils.isBlank(doc.getString(FieldType.RRN.toString()))) {
				searchTxn.setRrn(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setRrn(doc.getString(FieldType.RRN.toString()));
			}
			
	
			searchTxn.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
			
			if (StringUtils.isBlank(doc.getString(FieldType.RESPONSE_MESSAGE.toString()))) {
				searchTxn.setIpayResponseMessage(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setIpayResponseMessage(doc.getString(FieldType.RESPONSE_MESSAGE.toString()));
			}
			
			
			if (StringUtils.isBlank(doc.getString(FieldType.PG_TXN_MESSAGE.toString()))) {
				searchTxn.setAcquirerTxnMessage(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setAcquirerTxnMessage(doc.getString(FieldType.PG_TXN_MESSAGE.toString()));
			}
			if (StringUtils.isBlank(doc.getString(FieldType.REFUND_ORDER_ID.toString()))) {
				searchTxn.setRefund_txn_id(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setRefund_txn_id(doc.getString(FieldType.REFUND_ORDER_ID.toString()));
			}
			
			if (StringUtils.isBlank(doc.getString(FieldType.ARN.toString()))) {
				searchTxn.setArn(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setArn(doc.getString(FieldType.ARN.toString()));
			}
			if(sessionuser.getUserType()!=UserType.MERCHANT) {
			
			if (StringUtils.isBlank(doc.getString(FieldType.ACQUIRER_TYPE.toString()))) {
				searchTxn.setAcquirer(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setAcquirer(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
			}
			}
			if (StringUtils.isBlank(doc.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()))) {
				searchTxn.setIssuerBank(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setIssuerBank(doc.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
			}
			
			searchTxn.setResponseCode(doc.getString(FieldType.RESPONSE_CODE.toString()));
			if(StringUtils.isBlank(doc.getString(FieldType.REQUEST_DATE.toString()))) {
				searchTxn.setRequestDate(CrmFieldConstants.NA.toString());	
			} else {
				searchTxn.setRequestDate(doc.getString(FieldType.REQUEST_DATE.toString()));
			}
			
			transactionList.add(searchTxn);
		}
		
		/*Comparator<SearchTransaction> comp = (SearchTransaction a, SearchTransaction b) -> {

			if (a.gettDate().compareTo(b.gettDate()) > 0) {
				return 1;
			} else if (a.gettDate().compareTo(b.gettDate()) < 0) {
				return -1;
			} else {
				return 0;
			}
		};
		
		Collections.sort(transactionList, comp);*/
		return transactionList;
	    }
		catch (Exception e) {
			logger.error("Exception occured Agent Search , Exception = " + e);
			return null;
		}
	}
	
	public List<SearchTransaction> searchPaymentForAgent(String orderId, String pgRefNum,String acqId, String customerEmail,String customerPhone,User sessionuser) {
		List<SearchTransaction> transactionList = new ArrayList<SearchTransaction>();
        
		try
		{
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		if (!StringUtils.isBlank(orderId)) {
		obj.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		}
		if (!StringUtils.isBlank(pgRefNum)) {
		obj.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		}
		if (!StringUtils.isBlank(acqId)) {
			obj.add(new BasicDBObject(FieldType.ACQ_ID.getName(), acqId));
			}
		if (!StringUtils.isBlank(customerEmail)) {
		obj.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
		}
		if (!StringUtils.isBlank(customerPhone)) {
			obj.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone));
		}
		andQuery.put("$and", obj);
		
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix+Constants.COLLECTION_NAME.getValue()));
		
		BasicDBObject match = new BasicDBObject("$match", andQuery);
		BasicDBObject projectElement = new BasicDBObject();
		projectElement.put(FieldType.OID.toString(), 1);
		BasicDBObject project = new BasicDBObject("$project", projectElement);
		List<BasicDBObject> oidPipeline = Arrays.asList(match, project);
		AggregateIterable<Document> oidTotal = coll.aggregate(oidPipeline);
		oidTotal.allowDiskUse(true);
		
		// Removing the Duplicates
		HashSet distinctset=new HashSet();
		MongoCursor<Document> oidTotalCursor = (MongoCursor<Document>) oidTotal.iterator();
		
		while (oidTotalCursor.hasNext()) {
				distinctset.add(oidTotalCursor.next().get("OID"));
		}
		
		Iterator oidCursor =  distinctset.iterator();
		
		//MongoCursor<Document> cursor = coll.find(andQuery).sort(new BasicDBObject(FieldType.CREATE_DATE.getName(),-1)).iterator();
		
		while (oidCursor.hasNext()) {
			
			BasicDBObject oidFinalquery = new BasicDBObject("OID", oidCursor.next().toString());
			BasicDBObject oidMatch = new BasicDBObject("$match", oidFinalquery);
			BasicDBObject oidSort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject oidLimit = new BasicDBObject("$limit", 1);
			List<BasicDBObject> pipeline = Arrays.asList(oidMatch, oidSort, oidLimit);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			
			Document doc = cursor.next();

			SearchTransaction searchTxn = new SearchTransaction();
			User user = new User();
			if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null){
				user = userMap.get(doc.getString(FieldType.PAY_ID.toString()));
			}
			else {
				user = userdao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
				userMap.put(doc.getString(FieldType.PAY_ID.toString()), user);
			}
			
			
			BigInteger txnID = new BigInteger(doc.getString(FieldType.TXN_ID.toString()));
			searchTxn.setTransactionId((txnID));
			searchTxn.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
			searchTxn.setMerchant(user.getBusinessName());
			searchTxn.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
			searchTxn.settDate(doc.getString(FieldType.CREATE_DATE.toString()));
			
			if (null != doc.getString(FieldType.CUST_EMAIL.toString())) {
				searchTxn.setCustomerEmail(doc.getString(FieldType.CUST_EMAIL.toString()));
			} else {
				searchTxn.setCustomerEmail(CrmFieldConstants.NA.getValue());
			}
			if (null != doc.getString(FieldType.CUST_PHONE.toString())) {
				searchTxn.setCustomerPhone(doc.getString(FieldType.CUST_PHONE.toString()));
			} else {
				searchTxn.setCustomerPhone(CrmFieldConstants.NA.getValue());
			}
			
			if (StringUtils.isBlank(doc.getString(FieldType.PAYMENT_TYPE.toString()))) {
				searchTxn.setPaymentType(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setPaymentType(doc.getString(FieldType.PAYMENT_TYPE.toString()));
			}
			
			if (StringUtils.isBlank(doc.getString(FieldType.MOP_TYPE.toString()))) {
				searchTxn.setMopType(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
			}
			if(doc.getString(FieldType.TXNTYPE.toString()).equals(TransactionType.RECO.getName())) {
				searchTxn.setTxnType(TransactionType.SALE.getName());
			} else if(doc.getString(FieldType.TXNTYPE.toString()).equals(TransactionType.REFUNDRECO.getName())) {
				searchTxn.setTxnType(TransactionType.REFUND.getName());
			} else {
				searchTxn.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
			}
			
			
			
			if (StringUtils.isBlank(doc.getString(FieldType.CARD_MASK.toString()))) {
				searchTxn.setCardNum(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setCardNum(doc.getString(FieldType.CARD_MASK.toString()));
			}
			
			searchTxn.setStatus(doc.getString(FieldType.STATUS.toString()));
			searchTxn.setAmount(doc.getString(FieldType.AMOUNT.toString()));		
			searchTxn.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));	
			if (StringUtils.isBlank(doc.getString(FieldType.CARD_HOLDER_NAME.toString()))) {
				searchTxn.setCustName(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setCustName(doc.getString(FieldType.CARD_HOLDER_NAME.toString()));
			}
				
			
			if (StringUtils.isBlank(doc.getString(FieldType.RRN.toString()))) {
				searchTxn.setRrn(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setRrn(doc.getString(FieldType.RRN.toString()));
			}
			
	
			searchTxn.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
			
			if (StringUtils.isBlank(doc.getString(FieldType.RESPONSE_MESSAGE.toString()))) {
				searchTxn.setIpayResponseMessage(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setIpayResponseMessage(doc.getString(FieldType.RESPONSE_MESSAGE.toString()));
			}
			
			
			if (StringUtils.isBlank(doc.getString(FieldType.PG_TXN_MESSAGE.toString()))) {
				searchTxn.setAcquirerTxnMessage(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setAcquirerTxnMessage(doc.getString(FieldType.PG_TXN_MESSAGE.toString()));
			}
			if (StringUtils.isBlank(doc.getString(FieldType.REFUND_ORDER_ID.toString()))) {
				searchTxn.setRefund_txn_id(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setRefund_txn_id(doc.getString(FieldType.REFUND_ORDER_ID.toString()));
			}
			
			if (StringUtils.isBlank(doc.getString(FieldType.ARN.toString()))) {
				searchTxn.setArn(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setArn(doc.getString(FieldType.ARN.toString()));
			}
			if(sessionuser.getUserType()!=UserType.MERCHANT) {
			
			if (StringUtils.isBlank(doc.getString(FieldType.ACQUIRER_TYPE.toString()))) {
				searchTxn.setAcquirer(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setAcquirer(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
			}
			}
			if (StringUtils.isBlank(doc.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()))) {
				searchTxn.setIssuerBank(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setIssuerBank(doc.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
			}
			
			searchTxn.setResponseCode(doc.getString(FieldType.RESPONSE_CODE.toString()));
			if(StringUtils.isBlank(doc.getString(FieldType.REQUEST_DATE.toString()))) {
				searchTxn.setRequestDate(CrmFieldConstants.NA.toString());	
			} else {
				searchTxn.setRequestDate(doc.getString(FieldType.REQUEST_DATE.toString()));
			}
			
			transactionList.add(searchTxn);
			cursor.close();
		}
		
		/*Comparator<SearchTransaction> comp = (SearchTransaction a, SearchTransaction b) -> {

			if (a.gettDate().compareTo(b.gettDate()) > 0) {
				return 1;
			} else if (a.gettDate().compareTo(b.gettDate()) < 0) {
				return -1;
			} else {
				return 0;
			}
		};
		
		Collections.sort(transactionList, comp);*/
		return transactionList;
	    
		}
		catch (Exception e) {
			logger.error("Exception occured Agent Search , Exception = " + e);
			return null;
		}
	}
	
	public List<SearchTransaction> searchPaymentForAgentCustom(String orderId, String pgRefNum,String acqId, String customerEmail,String customerPhone,User sessionuser) {
		List<SearchTransaction> transactionList = new ArrayList<SearchTransaction>();
        
		try
		{
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		if (!StringUtils.isBlank(orderId)) {
		obj.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		}
		if (!StringUtils.isBlank(pgRefNum)) {
		obj.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		}
		if (!StringUtils.isBlank(acqId)) {
			obj.add(new BasicDBObject(FieldType.ACQ_ID.getName(), acqId));
			}
		if (!StringUtils.isBlank(customerEmail)) {
		obj.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
		}
		if (!StringUtils.isBlank(customerPhone)) {
			obj.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone));
		}
		andQuery.put("$and", obj);
		
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix+Constants.COLLECTION_NAME.getValue()));
		
		BasicDBObject match = new BasicDBObject("$match", andQuery);
		BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
		List<BasicDBObject> pipeline = Arrays.asList(match, sort);
		AggregateIterable<Document> itr = coll.aggregate(pipeline);
		itr.allowDiskUse(true);
		
		MongoCursor<Document> cursor = (MongoCursor<Document>) itr.iterator();
		
		while (cursor.hasNext()) {
			
			Document doc = cursor.next();
			SearchTransaction searchTxn = new SearchTransaction();
			
			User user = new User();
			if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null){
				user = userMap.get(doc.getString(FieldType.PAY_ID.toString()));
			}
			else {
				user = userdao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
				userMap.put(doc.getString(FieldType.PAY_ID.toString()), user);
			}
			
			
			BigInteger txnID = new BigInteger(doc.getString(FieldType.TXN_ID.toString()));
			searchTxn.setTransactionId((txnID));
			searchTxn.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
			searchTxn.setMerchant(user.getBusinessName());
			searchTxn.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
			searchTxn.settDate(doc.getString(FieldType.CREATE_DATE.toString()));
			
			if (null != doc.getString(FieldType.CUST_EMAIL.toString())) {
				searchTxn.setCustomerEmail(doc.getString(FieldType.CUST_EMAIL.toString()));
			} else {
				searchTxn.setCustomerEmail(CrmFieldConstants.NA.getValue());
			}
			if (null != doc.getString(FieldType.CUST_PHONE.toString())) {
				searchTxn.setCustomerPhone(doc.getString(FieldType.CUST_PHONE.toString()));
			} else {
				searchTxn.setCustomerPhone(CrmFieldConstants.NA.getValue());
			}
			
			if (StringUtils.isBlank(doc.getString(FieldType.PAYMENT_TYPE.toString()))) {
				searchTxn.setPaymentType(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setPaymentType(doc.getString(FieldType.PAYMENT_TYPE.toString()));
			}
			
			if (StringUtils.isBlank(doc.getString(FieldType.MOP_TYPE.toString()))) {
				searchTxn.setMopType(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
			}
			if(doc.getString(FieldType.TXNTYPE.toString()).equals(TransactionType.RECO.getName())) {
				searchTxn.setTxnType(TransactionType.SALE.getName());
			} else if(doc.getString(FieldType.TXNTYPE.toString()).equals(TransactionType.REFUNDRECO.getName())) {
				searchTxn.setTxnType(TransactionType.REFUND.getName());
			} else {
				searchTxn.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
			}
			
			
			
			if (StringUtils.isBlank(doc.getString(FieldType.CARD_MASK.toString()))) {
				searchTxn.setCardNum(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setCardNum(doc.getString(FieldType.CARD_MASK.toString()));
			}
			
			searchTxn.setStatus(doc.getString(FieldType.STATUS.toString()));
			searchTxn.setAmount(doc.getString(FieldType.AMOUNT.toString()));		
			searchTxn.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));	
			if (StringUtils.isBlank(doc.getString(FieldType.CARD_HOLDER_NAME.toString()))) {
				searchTxn.setCustName(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setCustName(doc.getString(FieldType.CARD_HOLDER_NAME.toString()));
			}
				
			
			if (StringUtils.isBlank(doc.getString(FieldType.RRN.toString()))) {
				searchTxn.setRrn(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setRrn(doc.getString(FieldType.RRN.toString()));
			}
			
	
			searchTxn.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
			
			if (StringUtils.isBlank(doc.getString(FieldType.RESPONSE_MESSAGE.toString()))) {
				searchTxn.setIpayResponseMessage(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setIpayResponseMessage(doc.getString(FieldType.RESPONSE_MESSAGE.toString()));
			}
			
			
			if (StringUtils.isBlank(doc.getString(FieldType.PG_TXN_MESSAGE.toString()))) {
				searchTxn.setAcquirerTxnMessage(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setAcquirerTxnMessage(doc.getString(FieldType.PG_TXN_MESSAGE.toString()));
			}
			if (StringUtils.isBlank(doc.getString(FieldType.REFUND_ORDER_ID.toString()))) {
				searchTxn.setRefund_txn_id(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setRefund_txn_id(doc.getString(FieldType.REFUND_ORDER_ID.toString()));
			}
			
			if (StringUtils.isBlank(doc.getString(FieldType.ARN.toString()))) {
				searchTxn.setArn(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setArn(doc.getString(FieldType.ARN.toString()));
			}
			if(sessionuser.getUserType()!=UserType.MERCHANT) {
			
			if (StringUtils.isBlank(doc.getString(FieldType.ACQUIRER_TYPE.toString()))) {
				searchTxn.setAcquirer(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setAcquirer(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
			}
			}
			if (StringUtils.isBlank(doc.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()))) {
				searchTxn.setIssuerBank(CrmFieldConstants.NA.toString());	
			}
			else {
				searchTxn.setIssuerBank(doc.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
			}
			
			searchTxn.setResponseCode(doc.getString(FieldType.RESPONSE_CODE.toString()));
			if(StringUtils.isBlank(doc.getString(FieldType.REQUEST_DATE.toString()))) {
				searchTxn.setRequestDate(CrmFieldConstants.NA.toString());	
			} else {
				searchTxn.setRequestDate(doc.getString(FieldType.REQUEST_DATE.toString()));
			}
			
			transactionList.add(searchTxn);
			
		}
		cursor.close();
		return transactionList;
	    
		}
		catch (Exception e) {
			logger.error("Exception occured Agent Search custom, Exception = " + e);
			return null;
		}
	}
}
		

