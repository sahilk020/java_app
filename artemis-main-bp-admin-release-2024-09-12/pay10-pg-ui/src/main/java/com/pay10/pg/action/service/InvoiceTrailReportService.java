package com.pay10.pg.action.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.InvoiceTrailReport;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;


@Service
public class InvoiceTrailReportService {

	private static Logger logger = LoggerFactory.getLogger(InvoiceTrailReportService.class.getName());
	@Autowired
	UserDao userDao;
	
	@Autowired
	InvoiceDao invoiceDao;

	private InvoiceTrailReport invoiceTrailReport = new InvoiceTrailReport();

	public void updateInvoiceTrailReport(Invoice invoice) {
		logger.info("get invoice object in invoice Trail report service={}",new Gson().toJson(invoice));
		Document doc = invoiceDao.findInvoiceTrailReportNo(invoice.getInvoiceNo());
		logger.info("doc while updating tnc details in invoiceTrailReportService...={}"+ doc.toJson());
		User user = userDao.findPayId1(invoice.getPayId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String PayText = user.getPayText();
		String newString =  PayText.replace("#invoiceNo",invoice.getInvoiceNo());
		logger.info("new string for merchant tnc...={}", newString);
	    
		invoiceTrailReport.setInvoiceId(doc.getString("INVOICE_ID"));
		invoiceTrailReport.setInvoiceNo(doc.getString("INVOICE_NO"));
		invoiceTrailReport.setMerchantTnc(doc.getString("MERCHANT_TNC"));
		invoiceTrailReport.setMerchantTncTimeStamp(doc.getString("MERCHANT_TNC_TIMESTAMP"));
		invoiceTrailReport.setBusinessName(doc.getString("BUSINESS_NAME"));
		invoiceTrailReport.setCustomerTnc(newString);
		invoiceTrailReport.setCustomerTncTimeStamp(sdf.format(cal.getTime()));
		invoiceDao.updateInvoiceTrailReport(invoiceTrailReport);

	}

}
