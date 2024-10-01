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
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.TDRBifurcationReportDetails;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.crm.mongoReports.TransactionStatus;

public class TDRBifurcationReportList extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;
	List<Merchants> merchantList = new ArrayList<>();
	List<String> statuss = new ArrayList<>();
	List<String> ruleTypee = new ArrayList<>();
	private int draw;
	private int length;
	private int start;

	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;

	private String merchant;
	private String dateRange;
	private String status;
	private String ruleType;
	private String acquirer;
	private String dateFrom;
	private String dateTo;
	private String settlementDateFrom;
	private String settlementDateTo;

	

	@Autowired
	private DataEncoder encoder;

	@Autowired
	private UserDao userDao;
	@Autowired
	private TransactionStatus transactionStatus;

	HttpServletRequest request = ServletActionContext.getRequest();

	HttpServletResponse response = ServletActionContext.getResponse();
	private List<TDRBifurcationReportDetails> tdrBifurcationReportDetails = new ArrayList<TDRBifurcationReportDetails>();
	
	private List<TDRBifurcationReportDetails> aaData = new ArrayList<TDRBifurcationReportDetails>();
	public String execute() {

		if ((!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) || (!StringUtils.isBlank(settlementDateFrom) && !StringUtils.isBlank(settlementDateTo))) {
			BigInteger bigInt = BigInteger.valueOf(transactionStatus.getTDRBurificationReportCount(merchant, status,
				acquirer, dateFrom, dateTo, settlementDateFrom, settlementDateTo));// ,ipAddress,totalAmount));
		setRecordsTotal(bigInt);
		if (getLength() == -1) {
			setLength(getRecordsTotal().intValue());
		}

		tdrBifurcationReportDetails = new ArrayList<TDRBifurcationReportDetails>();

		aaData = encoder.tdrBifurcationreport(transactionStatus.getTDRBurificationReport(merchant, status, acquirer, dateFrom,
				dateTo, settlementDateFrom, settlementDateTo,start,length));
		
		recordsFiltered = recordsTotal;

		}else {
			BigInteger bigInt = BigInteger.valueOf(transactionStatus.getTDRBurificationReportCount("All", "All",
					"All", new SimpleDateFormat("yyyyMMdd").format(new Date()), new SimpleDateFormat("yyyyMMdd").format(new Date()), settlementDateFrom, settlementDateTo));// ,ipAddress,totalAmount));
			setRecordsTotal(bigInt);
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}

			tdrBifurcationReportDetails = new ArrayList<TDRBifurcationReportDetails>();

			aaData = encoder.tdrBifurcationreport(transactionStatus.getTDRBurificationReport("All", "All",
					"All", new SimpleDateFormat("yyyyMMdd").format(new Date()), new SimpleDateFormat("yyyyMMdd").format(new Date()), settlementDateFrom, settlementDateTo,start,length));
			
			recordsFiltered = recordsTotal;
		}	

		return SUCCESS;
	}

	public List<TDRBifurcationReportDetails> getTdrBifurcationReportDetails() {
		return tdrBifurcationReportDetails;
	}

	public void setTdrBifurcationReportDetails(List<TDRBifurcationReportDetails> tdrBifurcationReportDetails) {
		this.tdrBifurcationReportDetails = tdrBifurcationReportDetails;
	}

	public List<String> getRuleTypee() {
		return ruleTypee;
	}

	public void setRuleTypee(List<String> ruleTypee) {
		this.ruleTypee = ruleTypee;
	}

	public List<String> getStatuss() {
		return statuss;
	}

	public void setStatuss(List<String> statuss) {
		this.statuss = statuss;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public String getSettlementDateFrom() {
		return settlementDateFrom;
	}

	public void setSettlementDateFrom(String settlementDateFrom) {
		this.settlementDateFrom = settlementDateFrom;
	}

	public String getSettlementDateTo() {
		return settlementDateTo;
	}

	public void setSettlementDateTo(String settlementDateTo) {
		this.settlementDateTo = settlementDateTo;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
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

	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
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

	public List<TDRBifurcationReportDetails> getAaData() {
		return aaData;
	}

	public void setAaData(List<TDRBifurcationReportDetails> aaData) {
		this.aaData = aaData;
	}

	
}
