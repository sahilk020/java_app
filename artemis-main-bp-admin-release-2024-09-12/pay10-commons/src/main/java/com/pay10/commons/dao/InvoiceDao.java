package com.pay10.commons.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.InvoiceTrailReport;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.InvoiceUtilities;
import com.pay10.commons.util.PropertiesManager;

/**
 * 
 * @author shubhamchauhan
 */

@Component
public class InvoiceDao {

	@Autowired
	private MongoInstance mongoInstance;
	private static final String PREFIX = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(InvoiceDao.class.getName());

	public boolean create(Invoice invoice) {

		if (invoice == null) {
			logger.info("Invoice is null");
			return false;
		}
		try {
			Document doc = InvoiceUtilities.getDocFromInvoice(invoice);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(PREFIX + Constants.INVOICE_COLLECTION_NAME.getValue()));
			collection.insertOne(doc);
		} catch (Exception e) {
			logger.error("Failed to create invoice.");
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public Invoice update(Invoice invoice) {
		String oldUpdateDate = invoice.getUpdateDate();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap.get(PREFIX + Constants.INVOICE_COLLECTION_NAME.getValue()));
			
			invoice.setUpdateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		    invoice.setMerchantConsent(invoice.isMerchantConsent());
			
			Document doc = InvoiceUtilities.getDocFromInvoice(invoice);
			BasicDBObject findQuery = new BasicDBObject();
			findQuery.put(CrmFieldType.INVOICE_ID.getName(), invoice.getInvoiceId());
			collection.replaceOne(findQuery, doc);
			//Added By Sweety
			logger.info("invoice update method call to update the status of invoice",invoice.isMerchantConsent());
			logger.info("Invoice updated successfully : " + invoice.getInvoiceId());
		} catch (Exception e) {
			logger.info("Failed to update invoice : " + invoice.getInvoiceId());
			logger.error(e.getMessage());
			invoice.setUpdateDate(oldUpdateDate);
		}
		return invoice;
	}

	// This method will return single invoice only. For getting list modify the code.
	public Document findByInvoiceNumber(String invoiceNumber) {
		if(StringUtils.isEmpty(invoiceNumber)) {
			logger.error("Empty invoice number");
			return null;
		}
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(PREFIX + Constants.INVOICE_COLLECTION_NAME.getValue()));

//		List<BasicDBObject> andQueryList = new ArrayList<>();
//		andQueryList.add(new BasicDBObject(CrmFieldType.INVOICE_NUMBER.getName(), invoiceNumber));
//		andQueryList.add(new BasicDBObject(CrmFieldType.INVOICE_STATUS.getName(), InvoiceStatus.UNPAID.getName()));
		BasicDBObject findQuery = new BasicDBObject(CrmFieldType.INVOICE_NUMBER.getName(), invoiceNumber);
//		findQuery.put("$and", andQueryList);
		logger.info("Query for find invoice by number : " + findQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();

		Document doc = null;
		if (cursor.hasNext()) {
			doc = cursor.next();
		}
		cursor.close();
		return doc;
	}

	public Invoice findByInvoiceId(String invoiceId) {
		Invoice invoice = null;
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(PREFIX + Constants.INVOICE_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put(CrmFieldType.INVOICE_ID.getName(), invoiceId);
		logger.info("Query for find invoice by Id : " + findQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();

		Document doc = null;
		if (cursor.hasNext()) {
			doc = cursor.next();
		}
		cursor.close();
		if (doc != null && doc.getString(CrmFieldType.INVOICE_ID.getName()).equals(invoiceId)) {
			invoice = InvoiceUtilities.getInvoiceFromDoc(doc);
		}
		return invoice;
	}

	public Map<String, Object> getInvoiceList(String fromDate, String toDate, String merchantPayId, String invoiceNo,
			String customerEmail, String currency, String paymentType, int start, int length) throws SystemException {
		Map<String, Object> map = new HashMap<>();
		List<Invoice> invoiceList = new ArrayList<>();
		try {
			List<BasicDBObject> queryList = new ArrayList<>();

			if (StringUtils.isNotBlank(invoiceNo)) {
				queryList.add(new BasicDBObject(CrmFieldType.INVOICE_NUMBER.getName(), invoiceNo));
			}

			if (StringUtils.isNotBlank(customerEmail)) {
				queryList.add(new BasicDBObject(CrmFieldType.INVOICE_EMAIL.getName(), customerEmail));
			}
			
			// If invoice number or customer email is present. Bypass all other fields.
			if(StringUtils.isNotBlank(invoiceNo) || StringUtils.isNotBlank(customerEmail)){
				BasicDBObject findQuery = new BasicDBObject("$or", queryList);
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> collection = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(PREFIX + Constants.INVOICE_COLLECTION_NAME.getValue()));
				logger.info("Find query for invoice List : " + findQuery);
				
				BasicDBObject match = new BasicDBObject("$match", findQuery);
				BasicDBObject skip  = new BasicDBObject("$skip", start);
				BasicDBObject limit = new BasicDBObject("$limit", length);
				List<BasicDBObject> pipeline = Arrays.asList(match, skip, limit);
				
				logger.info("pipeLine : " + pipeline);
				
				Long totalRecords = collection.count(findQuery);
				AggregateIterable<Document> output = collection.aggregate(pipeline);
				MongoCursor<Document> cursor = output.iterator();
				output.allowDiskUse(true);
				Document doc = null;
				while (cursor.hasNext()) {
					doc = cursor.next();
					invoiceList.add(InvoiceUtilities.getInvoiceFromDoc(doc));
				}
				cursor.close();
				map.put("list", invoiceList);
				map.put("count", totalRecords);
				return map;
			}

			String dateFrom = null;
			String dateTo = null;
			
			// Convert UI date time format to MongoDb date time format.
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date fDate = sdf.parse(fromDate);
			Date tDate = sdf.parse(toDate);
			
			fromDate = new SimpleDateFormat("yyyy-MM-dd").format(fDate);
			toDate = new SimpleDateFormat("yyyy-MM-dd").format(tDate);

			dateTo = toDate + " 23:59:59";
			dateFrom = fromDate + " 00:00:00";
			BasicDBObject dateFromObject = new BasicDBObject(CrmFieldType.INVOICE_CREATE_DATE.getName(),new BasicDBObject("$gte", dateFrom));
			BasicDBObject dateToObject = new BasicDBObject(CrmFieldType.INVOICE_CREATE_DATE.getName(),new BasicDBObject("$lte", dateTo));
			
			queryList.add(dateFromObject);
			queryList.add(dateToObject);
			
			if (StringUtils.isNotBlank(merchantPayId) && !merchantPayId.equalsIgnoreCase("ALL")) {
				queryList.add(new BasicDBObject(CrmFieldType.INVOICE_PAY_ID.getName(), merchantPayId));
			}
			
			if (StringUtils.isNotBlank(currency) && !currency.equalsIgnoreCase("ALL")) {
				queryList.add(new BasicDBObject(CrmFieldType.INVOICE_CURRENCY_CODE.getName(), currency));
			}

			if (StringUtils.isNotBlank(paymentType) && !paymentType.equalsIgnoreCase("ALL")) {
				queryList.add(new BasicDBObject(CrmFieldType.INVOICE_TYPE.getName(), paymentType));
			}
			BasicDBObject findQuery = new BasicDBObject("$and", queryList);
			logger.info("Find query for invoice list : " + findQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap.get(PREFIX + Constants.INVOICE_COLLECTION_NAME.getValue()));

			BasicDBObject match = new BasicDBObject("$match", findQuery);
			BasicDBObject skip  = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			
			Long totalRecords = collection.count(findQuery);
			List<BasicDBObject> pipeline = Arrays.asList(match, skip, limit);
			logger.info("pipeLine : " + pipeline);
			AggregateIterable<Document> output = collection.aggregate(pipeline);
			MongoCursor<Document> cursor = output.iterator();
			output.allowDiskUse(true);
			Document doc = null;
			while (cursor.hasNext()) {
				doc = cursor.next();
				invoiceList.add(InvoiceUtilities.getInvoiceFromDoc(doc));
			}
			cursor.close();
			map.put("list", invoiceList);
			map.put("count", totalRecords);
		} catch (Exception exception) {
			logger.error("Exception in getting invoice List ", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		
		return map;
	}

	//Added By Sweety
	public boolean createInvoiceTrailReport(InvoiceTrailReport invoiceTrailReport) {
		if (invoiceTrailReport == null) {
			logger.info("invoiceTrailReport is null");
			return false;
		}
		try {
			Document doc = InvoiceUtilities.getDocFromInvoiceTrailReport(invoiceTrailReport);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("InvoiceTrailReport");
			collection.insertOne(doc);
		} catch (Exception e) {
			logger.error("Failed to create invoiceTrailReport.");
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	public InvoiceTrailReport updateInvoiceTrailReport(InvoiceTrailReport invoiceTrailReport) {
		//String oldUpdateDate = invoice.getUpdateDate();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("InvoiceTrailReport");
			
			Document doc = InvoiceUtilities.getDocFromInvoiceTrailReport(invoiceTrailReport);
			logger.info("doc while updating tnc details...={}"+ doc.toJson());
			BasicDBObject findQuery = new BasicDBObject();
			findQuery.put("INVOICE_ID", invoiceTrailReport.getInvoiceId());
			collection.replaceOne(findQuery, doc);
			//Added By Sweety
			logger.info("invoiceTrailReport updated successfully : " + invoiceTrailReport.getInvoiceId());
		} catch (Exception e) {
			logger.info("Failed to update invoiceTrail Report : " + invoiceTrailReport.getInvoiceId());
			logger.error(e.getMessage());
		}
		return invoiceTrailReport;
		
	}
	
	// This method will return single invoice only. For getting list modify the code.
	public Document findInvoiceTrailReportNo(String invoiceNumber) {
		if(StringUtils.isEmpty(invoiceNumber)) {
			logger.error("Empty invoice number");
			return null;
		}
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("InvoiceTrailReport");

		BasicDBObject findQuery = new BasicDBObject(CrmFieldType.INVOICE_NUMBER.getName(), invoiceNumber);
		logger.info("Query for find invoice by number : " + findQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();

		Document doc = null;
		if (cursor.hasNext()) {
			doc = cursor.next();
		}
		cursor.close();
		return doc;
	}
}
