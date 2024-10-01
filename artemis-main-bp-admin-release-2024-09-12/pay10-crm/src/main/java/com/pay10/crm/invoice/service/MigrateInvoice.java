package com.pay10.crm.invoice.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.InvoiceType;
import com.pay10.crm.action.AbstractSecureAction;

/*
 * @author shubham chauhan
 * 
 */

public class MigrateInvoice extends AbstractSecureAction{

	private static final long serialVersionUID = 1385654677181995714L;
	private static Logger logger = LoggerFactory.getLogger(MigrateInvoice.class.getName());
	@Autowired
	InvoiceDao invoiceDao; 
	
	private String response ;
	
	@Override
	public String execute() {
		List<Invoice> oldInvoiceList = new ArrayList<>();
		logger.info("Migrating Invoice from mysql to mongo");
		String url = ConfigurationConstants.DB_URL.getValue();
		String user = ConfigurationConstants.DB_USER.getValue();
		String password = ConfigurationConstants.DB_PASSWORD.getValue();
		String driver = ConfigurationConstants.DB_DRIVER.getValue();
		
		if(StringUtils.isBlank(url) || StringUtils.isBlank(user) || StringUtils.isBlank(password) || StringUtils.isBlank(driver)) {
			logger.error("Invalid db configurations");
			setResponse("Failure");
			return SUCCESS;
		}
		
		try (Connection con = DriverManager.getConnection(url, user, password);
			 Statement stmt=con.createStatement();  
			 ResultSet rs=stmt.executeQuery("select * from Invoice order by id");)
		{
			
			// Load from Mysql
			while(rs.next()) {
				Invoice invoice = oldToNewInvoice(rs);
				if(invoice != null) {
					oldInvoiceList.add(invoice);
				}else {
					logger.error("Failed to load Invoice");
					setResponse("Failure");
					return SUCCESS;
				}
			}
			
			// Push to mongo
			for(Invoice invoice : oldInvoiceList) {
				if(!invoiceDao.create(invoice)) {
					logger.error("Failed to create invoice : " + invoice.getInvoiceId());
					setResponse("Failure");
					return SUCCESS;
				}
			}
			
		} catch (Exception e) {
			logger.error("Failed while migrating invoice.");
			logger.error(e.getMessage());
			setResponse("Failure");
			return SUCCESS;
		}
		
		setResponse("Success");
		return SUCCESS;
	}

	private Invoice oldToNewInvoice(ResultSet rs) throws SQLException {
		Invoice invoice = new Invoice();
		invoice.setId(Long.parseLong(rs.getString("id")));
		logger.info("Loading Invoice : " + invoice.getId());
		invoice.setAddress(rs.getString("address"));
		invoice.setAmount(rs.getString("amount"));
		invoice.setBusinessName(rs.getString("businessName"));
		invoice.setCity(rs.getString("city"));
		invoice.setCountry(rs.getString("country"));
		invoice.setCurrencyCode(rs.getString("currencyCode"));
		invoice.setEmail(rs.getString("email"));
		invoice.setExpiresDay(rs.getString("expiresDay"));
		invoice.setExpiresHour(rs.getString("expiresHour"));
		invoice.setInvoiceId(rs.getString("invoiceId"));
		invoice.setInvoiceNo(rs.getString("invoiceNo"));
		invoice.setMessageBody(rs.getString("messageBody"));
		invoice.setName(rs.getString("name"));
		invoice.setPayId(rs.getString("payId"));
		invoice.setPhone(rs.getString("phone"));
		invoice.setProductDesc(rs.getString("productDesc"));
		invoice.setProductName(rs.getString("productName"));
		invoice.setQuantity(rs.getString("quantity"));
		invoice.setReturnUrl(rs.getString("returnUrl"));
		invoice.setShortUrl(rs.getString("shortUrl"));
		invoice.setState(rs.getString("state"));
		invoice.setTotalAmount(rs.getString("totalAmount"));
		invoice.setZip(rs.getString("zip"));
		invoice.setGst(rs.getString("gst"));
		invoice.setInvoiceUrl(rs.getString("invoiceUrl"));
		
		
		// Calculate GstAmount
		String gstAmount = rs.getString("gstAmount");
		if(StringUtils.isBlank(gstAmount)) {
			invoice.setGstAmount("0");
		}else {
			invoice.setGstAmount(gstAmount);
		}
		
		
		// Convert from date to string.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(rs.getString("createDate"));
			String createDate = sdf.format(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(invoice.getExpiresDay()));
			cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(invoice.getExpiresHour()));

			invoice.setExpiryTime(sdf.format(cal.getTime()));
			invoice.setCreateDate(createDate);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			return null;
		}
		
		
		// Current time stamp
		invoice.setUpdateDate(sdf.format(new Date()));

		// Convert string to enum
		String oldInvoiceType = rs.getString("invoiceType");
		String oldInvoiceStatus = rs.getString("invoiceStatus");
		
		InvoiceType newInvoiceType = null;
		InvoiceStatus newInvoiceStatus = null;
		
		// If Status = null then set to expired.
		if(StringUtils.isBlank(oldInvoiceStatus)) {
			newInvoiceStatus = InvoiceStatus.EXPIRED;
		}
		else if(oldInvoiceStatus.toLowerCase().contains("unpaid")) {
			newInvoiceStatus = InvoiceStatus.UNPAID;
		}else if(oldInvoiceStatus.toLowerCase().contains("attempted")) {
			newInvoiceStatus = InvoiceStatus.ATTEMPTED;
		}else if(oldInvoiceStatus.toLowerCase().contains("paid")) {
			newInvoiceStatus = InvoiceStatus.PAID;
		}else if(oldInvoiceStatus.toLowerCase().contains("promotion")) {
			newInvoiceStatus = InvoiceStatus.UNPAID;
		}else {
			newInvoiceStatus = InvoiceStatus.EXPIRED;
		}
		
		if(oldInvoiceType.toLowerCase().contains("single") || oldInvoiceType.toLowerCase().contains("invoice")) {
			newInvoiceType = InvoiceType.SINGLE_PAYMENT;
		}else if(oldInvoiceType.toLowerCase().contains("promotional")) {
			newInvoiceType = InvoiceType.PROMOTIONAL_PAYMENT;
		}
		
		invoice.setInvoiceStatus(newInvoiceStatus);
		invoice.setInvoiceType(newInvoiceType);
		
		
		// gstamount is null. what to set. // Set 0
		
		
//		invoice.setServiceCharge(rs.getString("")); // Not req
//		invoice.setSaltKey(rs.getString("")); // Not req
//		invoice.setRecipientMobile(rs.getString("")); // Not req

		return invoice;
	}
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
