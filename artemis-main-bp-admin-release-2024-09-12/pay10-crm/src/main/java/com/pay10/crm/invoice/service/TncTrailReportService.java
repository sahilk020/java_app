package com.pay10.crm.invoice.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.InvoiceTrailReport;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.crm.invoice.MessageCondition;

@Service
public class TncTrailReportService {
	private static Logger logger = LoggerFactory.getLogger(TncTrailReportService.class.getName());


	@Autowired
	InvoiceDao invoiceDao;
	
	@Autowired
	UserDao userDao;
	
	private InvoiceTrailReport invoiceTrailReport = new InvoiceTrailReport();
	
	
	public void createTrailReport(Invoice invoice) {
	
		Document doc = invoiceDao.findByInvoiceNumber(invoice.getInvoiceNo());
		User user=userDao.findByPayId(invoice.getPayId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		invoiceTrailReport.setInvoiceId(invoice.getInvoiceId());
		invoiceTrailReport.setInvoiceNo(invoice.getInvoiceNo());
		invoiceTrailReport.setBusinessName(invoice.getBusinessName());
		//invoiceTrailReport.setMerchantTnc(user.getInvoiceText());
		String merchantText=user.getInvoiceText();
		  String newString= merchantText.replace("#invoiceNo",invoice.getInvoiceNo());
		  logger.info("new string for merchant tnc...={}",newString);
		invoiceTrailReport.setMerchantTnc(newString);
		invoiceTrailReport.setMerchantTncTimeStamp(sdf.format(cal.getTime()));

       boolean isInvoiceTrailReportCreated=invoiceDao.createInvoiceTrailReport(invoiceTrailReport);
		logger.info("isInvoiceTrailReportCreated",isInvoiceTrailReportCreated);

	}

}
