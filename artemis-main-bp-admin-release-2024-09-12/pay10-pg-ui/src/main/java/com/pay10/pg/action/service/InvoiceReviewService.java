package com.pay10.pg.action.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

/**
 * @author Puneet
 *
 */
@Service
public class InvoiceReviewService {

	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	private InvoiceDao invoiceDao;

	private static final String prefix = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(InvoiceReviewService.class.getName());

	public ErrorType validateSingleInvoiceStatus(Invoice invoice) {
		switch (invoice.getInvoiceStatus()) {
		case ATTEMPTED:
		case IN_PROCESS:
		case UNPAID:
			if (updateExpiredInvoice(invoice)) {
				return ErrorType.INVOICE_EXPIRED;
			} else {
				InvoiceStatus status = getSingleInvoicePaymentStatus(invoice);
				// if paid
				if (status.equals(InvoiceStatus.PAID)) {
					// update DB
					invoice.setInvoiceStatus(InvoiceStatus.PAID);
					invoiceDao.update(invoice);
					return ErrorType.INVOICE_PAID;
				} // if in process
				else if (status.equals(InvoiceStatus.IN_PROCESS)) {
					// update DB
					invoiceDao.update(invoice);
					return ErrorType.INVOICE_IN_PROCESS;
				} // if failed payments
				else if (status.equals(InvoiceStatus.UNPAID)) {
					invoice.setInvoiceStatus(InvoiceStatus.IN_PROCESS);
					// update DB
					invoiceDao.update(invoice);
					return ErrorType.SUCCESS;
				} else {
					// non reachable code
					return ErrorType.SUCCESS;
				}
			}
		case PAID:
			// return paid
			return ErrorType.INVOICE_PAID;

		case EXPIRED:
			// Return expired
			return ErrorType.INVOICE_EXPIRED;

		default:
			return ErrorType.INVOICE_EXPIRED;
		}
	}

	public ErrorType validatePromotionalInvoiceStatus(Invoice invoice) {
		InvoiceStatus status = invoice.getInvoiceStatus();
		if (status.equals(InvoiceStatus.EXPIRED)) {
			return ErrorType.INVOICE_EXPIRED;
		} else if (updateExpiredInvoice(invoice)) {
			return ErrorType.INVOICE_EXPIRED;
		} else {
			return ErrorType.SUCCESS;
		}

	}

	public InvoiceStatus getSingleInvoicePaymentStatus(Invoice invoice) {

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			BasicDBObject invoiceIdQuery = new BasicDBObject(FieldType.INVOICE_ID.getName(), invoice.getInvoiceId());
			BasicDBObject txnTypeQuery = new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName());

			List<BasicDBObject> conditionsList = new ArrayList<BasicDBObject>();

			conditionsList.add(txnTypeQuery);
			conditionsList.add(invoiceIdQuery);

			BasicDBObject finalQuery = new BasicDBObject("$and", conditionsList);

			BasicDBObject match = new BasicDBObject("$match", finalQuery);

			// Sort in descending order to get latest Entry
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));

			// Project element to bring only required data
			BasicDBObject projectElement = new BasicDBObject();
			projectElement.put(FieldType.STATUS.toString(), 1);

			BasicDBObject project = new BasicDBObject("$project", projectElement);
			List<BasicDBObject> pipeline = Arrays.asList(match, sort, project);

			AggregateIterable<Document> dbRequest = coll.aggregate(pipeline);
			dbRequest.allowDiskUse(true);

			MongoCursor<Document> cursor = (MongoCursor<Document>) dbRequest.iterator();

			// Query didn't return any data
			if (!cursor.hasNext()) {
				return InvoiceStatus.UNPAID;
			}

			boolean isFirst = true;

			while (cursor.hasNext()) {

				Document dbobj = cursor.next();
				String status = dbobj.getString(FieldType.STATUS.getName());

				// Check first Result to verify if transaction is in progress
				if (isFirst && (status.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())
						|| status.equalsIgnoreCase(StatusType.ENROLLED.getName())
						|| status.equalsIgnoreCase(StatusType.PENDING.getName()))) {
					return InvoiceStatus.IN_PROCESS;
				}
				isFirst = false;

				// If any of the records show captured , send CAPTURED
				if (status.equals(StatusType.CAPTURED.getName())) {
					return InvoiceStatus.PAID;
				}

			}
			cursor.close();

			// For all other results
			return InvoiceStatus.UNPAID;
		}

		catch (Exception e) {
			// Collect payment again
			logger.error("Exception in getting invoice data", e);
			return InvoiceStatus.UNPAID;
		}
	}

	public boolean updateExpiredInvoice(Invoice invoice) {
		if (checkExpiryDate(invoice)) {
			invoice.setInvoiceStatus(InvoiceStatus.EXPIRED);
			// update DB
			invoiceDao.update(invoice);
			return true;
		} else {
			return false;
		}
	}

	public boolean checkExpiryDate(Invoice invoice) {
		try {
			String expiryDateTime = invoice.getExpiryTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date expiryDate = sdf.parse(expiryDateTime);
			Date currentDate = new Date();

			if (expiryDate.compareTo(currentDate) > 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception exception) {
			logger.error("Unable to parse date in invoice validation", exception);
			return false;
		}
	}
}
