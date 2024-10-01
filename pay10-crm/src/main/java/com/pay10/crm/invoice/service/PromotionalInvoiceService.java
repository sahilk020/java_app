package com.pay10.crm.invoice.service;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay10.commons.api.ShortUrlProvider;
import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.InvoiceType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.crm.actionBeans.BatchResponseObject;
import com.pay10.crm.actionBeans.CommanCsvReader;

@Service
public class PromotionalInvoiceService {

	private static Logger logger = LoggerFactory.getLogger(PromotionalInvoiceService.class.getName());

	@Autowired
	private InvoiceEmailService invoiceEmailService;

	@Autowired
	private ShortUrlProvider shortUrlProvider;

	@Autowired
	InvoiceDao invoiceDao;

	@Autowired
	private InvoiceSmsService invoiceSmsService;

	private StringBuilder responseMessage = new StringBuilder();
	private BatchResponseObject batchResponseObject = null;
	public String createInvoiceService(Invoice invoice, File file, String fileName) {

		String check = "";
		String name = invoice.getProductName();
		if (name.equals(check)) {
			return "INVPRO100";
		}
		
		// check for unique invoice number.
		Document doc = invoiceDao.findByInvoiceNumber(invoice.getInvoiceNo());
		if (doc != null) {
			return "INVPRO111";
		}
		
		CommanCsvReader commanCsvReader = new CommanCsvReader();
		batchResponseObject = commanCsvReader.csvReaderForBatchEmailSend(invoice);
		if (batchResponseObject.getInvoiceEmailList().isEmpty()) {
			logger.error("No data found in invoice csv file");
			return "INVPRO101";
		}

		if (invoice.getReturnUrl().isEmpty()) {
			String invoiceReturnUrl = PropertiesManager.propertiesMap
					.get(CrmFieldConstants.INVOICE_RETURN_URL.getValue());
			invoice.setReturnUrl(invoiceReturnUrl);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		invoice.setInvoiceId(TransactionManager.getId());
		invoice.setBusinessName(new UserDao().getBusinessNameByPayId(invoice.getPayId()));
		invoice.setInvoiceStatus(InvoiceStatus.UNPAID);
		invoice.setInvoiceType(InvoiceType.PROMOTIONAL_PAYMENT);
		invoice.setCreateDate(sdf.format(cal.getTime()));
		invoice.setUpdateDate(sdf.format(cal.getTime()));

		//cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(invoice.getExpiresDay()));
		cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt("0"));
		cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(invoice.getExpiresHour()));

		invoice.setExpiryTime(sdf.format(cal.getTime()));
		// Added by Sweety For merchant consent
		if (invoice.isMerchantConsent()) {
			logger.info("merchant Consent for promotional invoice.. {}", invoice.isMerchantConsent());
			invoice.setMerchantConsent(invoice.isMerchantConsent());
		}
		// calculate gst amount.
		if (invoice.getGst() == null) {
			invoice.setGst("0");
		}

		DecimalFormat df = new DecimalFormat("#.##");
		logger.info("Quantity from UI: " + invoice.getQuantity());
		logger.info("Amount from UI: " + invoice.getAmount());
		
		
		BigDecimal tQuantity = new BigDecimal(invoice.getQuantity());
		BigDecimal tAmount = new BigDecimal(invoice.getAmount());
		BigDecimal tGst = new BigDecimal(invoice.getGst());
		
		logger.info("Big Dec Quantity : " + tQuantity);
		logger.info("Big Dec GST : " + tGst);
		logger.info("Big Dec Amount: " + tAmount);
		
		BigDecimal gstAmount = tQuantity.multiply( (tAmount.multiply(tGst))).divide(new BigDecimal(100));
		BigDecimal calculatedAmount = (tAmount.multiply(tQuantity)).add(gstAmount).setScale(2, RoundingMode.HALF_UP);

		invoice.setTotalAmount(String.valueOf(calculatedAmount));
		logger.info("Total Amount from UI : " + invoice.getTotalAmount());
		
		BigDecimal totalAmount = new BigDecimal(invoice.getTotalAmount());
		logger.info("Big Dec Total Amount : " + totalAmount);
		logger.info("Big Dec Calculate Amount : " + calculatedAmount);
		
		BigInteger finalCalculatedAmount = calculatedAmount.multiply(new BigDecimal(100)).toBigInteger();
		BigInteger finalTotalAmount = totalAmount.multiply(new BigDecimal(100)).toBigInteger();
		
		logger.info("Final Total Amount : " + finalTotalAmount);
		logger.info("Final calculated amount : " + finalCalculatedAmount);
		
		if(!(finalCalculatedAmount.equals(finalTotalAmount))) {
			return "INVPRO104";
		}
		invoice.setGstAmount(String.valueOf(gstAmount));

		String url = PropertiesManager.propertiesMap.get(CrmFieldConstants.INVOICE_URL.getValue())
				+ invoice.getInvoiceId();
		invoice.setInvoiceUrl(url);
		//String shortUrl = shortUrlProvider.ShortUrl(url);
		String shortUrl ="";
		invoice.setShortUrl(shortUrl);
		boolean isInvoiceCreated = invoiceDao.create(invoice);
		if (!isInvoiceCreated) {
			invoice.setInvoiceUrl(null);
			invoice.setShortUrl(null);
			invoice.setGstAmount(null);
			return "INVPRO107";
		}

		new Thread(() -> {
			try {
				if(!(StringUtils.isEmpty(file))) {
					batchEmail(invoice, batchResponseObject);
				}
			} catch (Exception e) {
				logger.error("Failed to send batch email via thread");
				logger.error(e.getMessage());
			}
		}).start();
		return "INVPRO200";
	}

	public String batchEmail(Invoice invoice, BatchResponseObject batchResponseObject){
		
		if(batchResponseObject == null) {
			logger.error("Invalid batch response object : " + batchResponseObject);
			return "INVPRO105";
		}
		try {
			//String customer = "Customer";
			String customer = invoice.getName();

			for (Invoice emailphone : batchResponseObject.getInvoiceEmailList()) {
				try {
					invoiceEmailService.sendEmail(emailphone.getEmail(), invoice.getShortUrl(), invoice.getInvoiceUrl(), customer, invoice.getBusinessName());
					invoiceSmsService.sendSmsInvoice(emailphone.getPhone(), invoice.getShortUrl(), invoice.getInvoiceUrl(), invoice.getBusinessName(), invoice.getAmount(),invoice.getInvoiceNo());
					
				} catch (Exception exception) {
					responseMessage.append(ErrorType.EMAIL_ERROR.getResponseMessage());
					responseMessage.append(emailphone.getEmail());
					responseMessage.append(batchResponseObject.getResponseMessage());
					responseMessage.append("\n");
					logger.error("Error!! Unable to send email Emailer fail " + exception);
				}
			}

		} catch (Exception exception) {
			logger.error("sending email via batch file unsuccessfull!" + exception);
			return "INVPRO106";
		}
		return "INVPRO200";
	}

}
