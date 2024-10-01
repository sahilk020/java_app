package com.pay10.commons.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Invoice;

@Service
public class SearchInvoiceService {

	@Autowired
	private InvoiceDao_old invoiceDao;

	private static Logger logger = LoggerFactory.getLogger(SearchInvoiceService.class.getName());

	public SearchInvoiceService() {
	}

	public List<Invoice> getInvoiceList(String fromDate, String toDate, String merchantPayId, String userType,
			String invoiceNo, String customerEmail, String currency, String paymentType)
			throws SQLException, ParseException, SystemException {

		List<Invoice> invoiceList = new ArrayList<Invoice>();

		try {
			invoiceList = invoiceDao.getInvoiceList(fromDate, toDate, merchantPayId, invoiceNo, customerEmail,
					currency, paymentType);
		} catch (Exception exception) {
			logger.error("Exception in getting invoide List ", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		return invoiceList;
	}
}
