package com.pay10.crm.action;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.dto.GRS;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.TDRBifurcationReportDetails;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.crm.mongoReports.TransactionStatus;
import com.pay10.crm.mongoReports.TxnReportForGrs;

public class GRSViewReport extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;
	
	private int draw;
	private int length;
	private int start;

	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;

	private String merchant;
	
	private String status;
	
	private String dateFrom;
	private String dateTo;
	

	@Autowired
	private TxnReportForGrs forGrs;




	HttpServletRequest request = ServletActionContext.getRequest();

	HttpServletResponse response = ServletActionContext.getResponse();
	
	private List<GRS> aaData = new ArrayList<>();
	public String execute() {

		
			BigInteger bigInt = BigInteger.valueOf(forGrs.grsCount(merchant, status,
				 dateFrom, dateTo));
		setRecordsTotal(bigInt);
		if (getLength() == -1) {
			setLength(getRecordsTotal().intValue());
		}
		setAaData(forGrs.grsReport(merchant, status, dateFrom,
				dateTo,start,length));;
		
		recordsFiltered = recordsTotal;

		
		return SUCCESS;
	}
	
	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public List<GRS> getAaData() {
		return aaData;
	}

	public void setAaData(List<GRS> aaData) {
		this.aaData = aaData;
	}

	

	
}
