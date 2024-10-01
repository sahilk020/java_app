package com.pay10.commons.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.InvoiceType;
import com.pay10.commons.util.PropertiesManager;


@Service
public class InvoiceDao_old extends HibernateAbstractDao {
	
	@Autowired
	private MongoInstance mongoInstance;
	
	private String InvoiceID;
	private String Status;
	private String invType;
	 
	
	private static final String prefix = "MONGO_DB_";

	private static Logger logger = LoggerFactory.getLogger(InvoiceDao_old.class.getName());

	public void create(Invoice invoice) throws DataAccessLayerException {
		super.save(invoice);
	}

	public void delete(Invoice invoice) throws DataAccessLayerException {
		super.delete(invoice);
	}

	public Invoice find(Long id) throws DataAccessLayerException {
		return (Invoice) super.find(Invoice.class, id);
	}

	public Invoice find(String name) throws DataAccessLayerException {
		return (Invoice) super.find(Invoice.class, name);
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoiceList(String fromDate, String toDate, String payId, String invoiceNo, String email,
			String currencyCode, String invoiceType) {

		List<Invoice> invoiceList = new ArrayList<Invoice>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {

			Date dateFrom = null;
			Date dateTo = null;
			try {
				dateTo = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(toDate + " 23:59:59");
				dateFrom = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(fromDate + " 00:00:00");
			} catch (ParseException e) {
				logger.error("Exception in parsingDate ", e);
			}

			StringBuilder sb = new StringBuilder();

			sb.append("from Invoice IV where ");
			sb.append("IV.createDate between :fromDate and :todate");
			if (StringUtils.isNotBlank(payId) && !payId.equalsIgnoreCase("ALL")) {
				sb.append(" and payId = :payId");
			}

			if (StringUtils.isNotBlank(invoiceNo)) {
				sb.append(" and invoiceNo = :invoiceNo");
			}

			if (StringUtils.isNotBlank(email)) {
				sb.append(" and email = :email");
			}

			if (StringUtils.isNotBlank(currencyCode) && !currencyCode.equalsIgnoreCase("ALL")) {
				sb.append(" and currencyCode = :currencyCode");
			}

			if (StringUtils.isNotBlank(invoiceType) && !invoiceType.equalsIgnoreCase("ALL")) {
				sb.append(" and invoiceType = :invoiceType");
			}

			org.hibernate.query.Query query = session.createQuery(sb.toString());

			query.setParameter("fromDate", dateFrom);
			query.setParameter("todate", dateTo);

			if (StringUtils.isNotBlank(payId) && !payId.equalsIgnoreCase("ALL")) {
				query.setParameter("payId", payId);
			}

			if (StringUtils.isNotBlank(invoiceNo)) {
				query.setParameter("invoiceNo", invoiceNo);
			}

			if (StringUtils.isNotBlank(email)) {
				query.setParameter("email", email);
			}

			if (StringUtils.isNotBlank(currencyCode) && !currencyCode.equalsIgnoreCase("ALL")) {
				query.setParameter("currencyCode", currencyCode);
			}

			if (StringUtils.isNotBlank(invoiceType) && !invoiceType.equalsIgnoreCase("ALL")) {
				query.setParameter("invoiceType", invoiceType);
			}

			query.setCacheable(true);
			invoiceList = query.getResultList();
			tx.commit();
			
			 
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject whereQuery = new BasicDBObject();
			BasicDBObject querySort = new BasicDBObject();
			querySort.put("$natural",-1);
			
			for(int i=0;i<invoiceList.size();i++) {
				
				InvoiceID = invoiceList.get(i).getInvoiceId();
				whereQuery.put("INVOICE_ID",InvoiceID);
				FindIterable<Document> cursor = coll.find(whereQuery).limit(1).sort(querySort);
				if (cursor.iterator().hasNext()) {
					
					Document document = cursor.iterator().next();
					 Status = document.getString("STATUS");
					 invType = invoiceList.get(i).getInvoiceType().getName();
					 
					 if(invType.equals(InvoiceType.SINGLE_PAYMENT.getName())) {
						 if(!Status.equals("Captured")) {
							 invoiceList.get(i).setInvoiceStatus(InvoiceStatus.ATTEMPTED);
						 }else {
							 invoiceList.get(i).setInvoiceStatus(InvoiceStatus.PAID);
						 }
						invoiceList.get(i).setCurrencyCode("INR");
						 
					}
					else {
//						invoiceList.get(i).setInvoiceStatus(InvoiceType.PROMOTIONAL_PAYMENT.getName()); // Discuss with sir.
						invoiceList.get(i).setName("NA");
						invoiceList.get(i).setEmail("NA");
						invoiceList.get(i).setCurrencyCode("INR");
						 
					}
					} 
					else if(invoiceList.get(i).getInvoiceType().equals(InvoiceType.SINGLE_PAYMENT.getName())) {
						invoiceList.get(i).setInvoiceStatus(InvoiceStatus.UNPAID);
						invoiceList.get(i).setCurrencyCode("INR");
						 
					}else {
//						invoiceList.get(i).setInvoiceStatus("PROMOTIONAL PAYMENT"); // Discuss with sir
						invoiceList.get(i).setName("NA");
						invoiceList.get(i).setEmail("NA");
						invoiceList.get(i).setCurrencyCode("INR");
						 
					}
				
				}
			
			

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return invoiceList;
	}

}
