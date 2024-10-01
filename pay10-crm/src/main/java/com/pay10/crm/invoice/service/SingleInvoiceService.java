package com.pay10.crm.invoice.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.ShortUrlProvider;
import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.InvoiceType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;


@Service
public class SingleInvoiceService {

	private static Logger logger = LoggerFactory.getLogger(SingleInvoiceService.class.getName());

	@Autowired
	AWSEncryptDecryptService awsEncryptDecryptService;
	
	@Autowired
	private ShortUrlProvider shortUrlProvider;

	@Autowired
	private InvoiceEmailService invoiceEmailService;
	
	@Autowired
	private InvoiceSmsService invoiceSmsService;
	
	@Autowired
	InvoiceDao invoiceDao ;
	
	public String createInvoiceService(Invoice invoice) {
		
		// check for unique invoice number.
		Document doc = invoiceDao.findByInvoiceNumber(invoice.getInvoiceNo());
		if (doc != null) {
			return "INVSIN101";
		}
		String check = "";
		String email = invoice.getEmail();
		String name = invoice.getName();
		String product = invoice.getProductName();
		String phone = invoice.getPhone();
		
		// Use : StringUtils.isEmpty
		if (email.equals(check) || name.equals(check) || product.equals(check) || phone.equals(check)) {
			return "INVSIN100";
		}
		
		// calculate gst amount.
		if(invoice.getGst() == null) {
			invoice.setGst("0");
		}
		// Added by Sweety For merchant consent
		if (invoice.isMerchantConsent()) {
			logger.info("merchant Consent for single invoice..= {}", invoice.isMerchantConsent());
			invoice.setMerchantConsent(invoice.isMerchantConsent());
		}
//		DecimalFormat df = new DecimalFormat("#.##");
		logger.info("Quantity from UI: " + invoice.getQuantity());
		logger.info("Amount from UI: " + invoice.getAmount());
		logger.info("Total Amount from UI : " + invoice.getTotalAmount());
		
		BigDecimal tQuantity = new BigDecimal(invoice.getQuantity());
		BigDecimal tAmount = new BigDecimal(invoice.getAmount());
		BigDecimal tGst = new BigDecimal(invoice.getGst());
		
		logger.info("Big Dec Quantity : " + tQuantity);
		logger.info("Big Dec GST : " + tGst);
		logger.info("Big Dec Amount: " + tAmount);
		
		BigDecimal gstAmount = tQuantity.multiply( (tAmount.multiply(tGst))).divide(new BigDecimal(100));
		BigDecimal calculatedAmount = (tAmount.multiply(tQuantity)).add(gstAmount).setScale(2, RoundingMode.HALF_UP);
		
		BigDecimal totalAmount = new BigDecimal(invoice.getTotalAmount());
		logger.info("Big Dec Total Amount : " + totalAmount);
		logger.info("Big Dec Calculate Amount : " + calculatedAmount);
		
		BigInteger finalCalculatedAmount = calculatedAmount.multiply(new BigDecimal(100)).toBigInteger();
		BigInteger finalTotalAmount = totalAmount.multiply(new BigDecimal(100)).toBigInteger();
		
		logger.info("Final Total Amount : " + finalTotalAmount);
		logger.info("Final calculated amount : " + finalCalculatedAmount);
		
		if(!(finalCalculatedAmount.equals(finalTotalAmount))) {
			return "INVSIN99";
		}
		
		invoice.setGstAmount(String.valueOf(gstAmount));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		invoice.setInvoiceId(TransactionManager.getId());
		invoice.setBusinessName(new UserDao().getBusinessNameByPayId(invoice.getPayId()));
		invoice.setInvoiceStatus(InvoiceStatus.UNPAID);
		invoice.setInvoiceType(InvoiceType.SINGLE_PAYMENT);
		invoice.setCreateDate(sdf.format(cal.getTime()));
		invoice.setUpdateDate(sdf.format(cal.getTime()));
		if(!StringUtils.isEmpty(invoice.getAddress())) {
			String invoiceAddress = invoice.getAddress();
			invoiceAddress = invoiceAddress.replace("\n", " ");
			invoiceAddress = invoiceAddress.replace("\r", " ");
			invoice.setAddress(invoiceAddress);
		}

		cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(invoice.getExpiresDay()));
		cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(invoice.getExpiresHour()));

		invoice.setExpiryTime(sdf.format(cal.getTime()));

//		invoice.setSaltKey(awsEncryptDecryptService.encrypt(invoice.getInvoiceId()));
		if (invoice.getReturnUrl().isEmpty())
		{
			String invoiceReturnUrl = PropertiesManager.propertiesMap.get(CrmFieldConstants.INVOICE_RETURN_URL.getValue());
			invoice.setReturnUrl(invoiceReturnUrl);
		}
		String url = PropertiesManager.propertiesMap.get(CrmFieldConstants.INVOICE_URL.getValue()) + invoice.getInvoiceId();
		invoice.setInvoiceUrl(url);
		//String shortUrl = shortUrlProvider.ShortUrl(url);
		//invoice.setShortUrl(shortUrl);
		
		boolean isInvoiceCreated = invoiceDao.create(invoice);
		
		if(!isInvoiceCreated) {
			invoice.setInvoiceUrl(null);
			invoice.setShortUrl(null);
			invoice.setGstAmount(null);
			return "INVSIN102";
		}
		 
		new Thread(() -> invoiceEmailService.sendEmail(invoice.getEmail(), invoice.getShortUrl(), invoice.getInvoiceUrl(), invoice.getName(), invoice.getBusinessName())).start();
		//new Thread(() -> invoiceSmsService.sendSms(invoice.getPhone(), invoice.getShortUrl(), invoice.getInvoiceUrl(), invoice.getBusinessName(), invoice.getName())).start();
		
		// Added by Sweety For merchant consent and send message
		
		if (invoice.isMerchantConsent()) {
			logger.info("checking condition for message sending while creating single invoice..= {}", invoice.isMerchantConsent());
			new Thread(() -> invoiceSmsService.sendSmsInvoice(invoice.getPhone(), invoice.getShortUrl(), invoice.getInvoiceUrl(), invoice.getBusinessName(), invoice.getAmount(),invoice.getInvoiceNo())).start();

		}
		return "INVSIN200";
	}
}
