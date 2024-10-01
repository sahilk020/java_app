package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.BSESMonthlyInvoiceReportDetails;
import com.pay10.crm.mongoReports.BSESInvoiceReport;

public class BSESMonthlyInvoiceReportList extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;
	
	private String year;
	private String month;
	private String merchant;
	private int draw;
	private int length;
	private int start;
	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;

	@Autowired
	private BSESInvoiceReport bsesInvoiceReport;
	HttpServletRequest request = ServletActionContext.getRequest();

	HttpServletResponse response = ServletActionContext.getResponse();
	private List<BSESMonthlyInvoiceReportDetails> aaData = new ArrayList<BSESMonthlyInvoiceReportDetails>();

	public String execute() {
		aaData=bsesInvoiceReport.BSESMonthlyInvoiceReportList(merchant, year, month);
		return SUCCESS;
	}

	public List<BSESMonthlyInvoiceReportDetails> getAaData() {
		return aaData;
	}
	public void setAaData(List<BSESMonthlyInvoiceReportDetails> aaData) {
		this.aaData = aaData;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}
	public BigInteger getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(BigInteger recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public BigInteger getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(BigInteger recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

}
