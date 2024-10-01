package com.pay10.pg.action.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.dao.IntentPaymentLinkDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.PaymentLink;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.ResponseCreator;

@Service
public class IntentPaymentLinkService {
	private static final String prefix = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(IntentPaymentLinkService.class.getName());

	@Autowired
	private ResponseCreator responseCreater;

	@Autowired
	private IntentPaymentLinkDao intentPaymentLinkDao;

	@Autowired
	private MongoInstance mongoInstance;

	public String prepareFields(PaymentLink paymentLink) throws SystemException {
		logger.info("Intent paymentLink : "+paymentLink.toString());
		Random random = new Random();
		String requestUrl = ConfigurationConstants.INVOICE_PAYMENT_LINK.getValue();
		Fields fields = new Fields();
		fields.put(FieldType.ORDER_ID.getName(), paymentLink.getOrderId());
		fields.put(FieldType.PAYMENT_LINK_ID.getName(), paymentLink.getPaymentLinkId());
		fields.put(FieldType.RETURN_URL.getName(), paymentLink.getReturnUrl());
		fields.put(FieldType.CURRENCY_CODE.getName(), paymentLink.getCurrencyCode());
		fields.put(FieldType.PAY_ID.getName(), paymentLink.getPayId());
		fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(paymentLink.getAmount(), paymentLink.getCurrencyCode()));
		fields.put(FieldType.UPI_INTENT.getName(), "1");
		

		// Optional fields
		if (StringUtils.isNotBlank(paymentLink.getAddress())) {
			fields.put(FieldType.CUST_STREET_ADDRESS1.getName(), paymentLink.getAddress());
		}

		if (StringUtils.isNotBlank(paymentLink.getEmail())) {
			fields.put(FieldType.CUST_EMAIL.getName(), paymentLink.getEmail());
		}
		if (StringUtils.isNotBlank(paymentLink.getName())) {
			fields.put(FieldType.CUST_NAME.getName(), paymentLink.getName());
		}
		if (StringUtils.isNotBlank(paymentLink.getPhone())) {
			fields.put(FieldType.CUST_PHONE.getName(), paymentLink.getPhone());
		}

		String hash = Hasher.getHash(fields);
		fields.put(FieldType.HASH.getName(), hash);

		return responseCreater.createInvoiceRequest(fields, requestUrl);
	}

	public PaymentLink getPaymentLink(String paymentLinkId) {
		PaymentLink paymentLink = intentPaymentLinkDao.findByPaymentLinkId(paymentLinkId);
		return paymentLink;
	}

	public PaymentLink getPaymentLinkByOrderId(String orderId) {
		PaymentLink paymentLink = intentPaymentLinkDao.findByOrderId(orderId);
		return paymentLink;
	}

	public void save(PaymentLink paymentLink) {

		if (!ObjectUtils.isEmpty(paymentLink)) {
			intentPaymentLinkDao.create(paymentLink);
		}

	}

	public ErrorType validatePaymentLinkStatus(PaymentLink paymentLink) {
		switch (paymentLink.getInvoiceStatus()) {
		case ATTEMPTED:
		case IN_PROCESS:
		case UNPAID:
			if (updateExpiredInvoice(paymentLink)) {
				return ErrorType.INVOICE_EXPIRED;
			} else {
				InvoiceStatus status = getPaymentLinkPaymentStatus(paymentLink);
				// if paid
				if (status.equals(InvoiceStatus.PAID)) {
					// update DB
					paymentLink.setInvoiceStatus(InvoiceStatus.PAID);
					intentPaymentLinkDao.update(paymentLink);
					return ErrorType.INVOICE_PAID;
				} // if in process
				else if (status.equals(InvoiceStatus.IN_PROCESS)) {
					// update DB
					intentPaymentLinkDao.update(paymentLink);
					return ErrorType.INVOICE_IN_PROCESS;
				} // if failed payments
				else if (status.equals(InvoiceStatus.UNPAID)) {
					paymentLink.setInvoiceStatus(InvoiceStatus.IN_PROCESS);
					// update DB
					intentPaymentLinkDao.update(paymentLink);
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

	public boolean updateExpiredInvoice(PaymentLink paymentLink) {
		if (checkExpiryDate(paymentLink)) {
			paymentLink.setInvoiceStatus(InvoiceStatus.EXPIRED);
			// update DB
			intentPaymentLinkDao.update(paymentLink);
			return true;
		} else {
			return false;
		}
	}

	public boolean checkExpiryDate(PaymentLink paymentLink) {
		try {
			String expiryDateTime = paymentLink.getExpiryTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date expiryDate = sdf.parse(expiryDateTime);
			Date currentDate = new Date();

			if (expiryDate.compareTo(currentDate) > 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception exception) {
			logger.error("Unable to parse date in payment link validation", exception);
			return false;
		}
	}

	public InvoiceStatus getPaymentLinkPaymentStatus(PaymentLink paymentLink) {

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			BasicDBObject invoiceIdQuery = new BasicDBObject(FieldType.PAYMENT_LINK_ID.getName(),
					paymentLink.getPaymentLinkId());
			BasicDBObject txnTypeQuery = new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(),
					TransactionType.SALE.getName());

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

}
