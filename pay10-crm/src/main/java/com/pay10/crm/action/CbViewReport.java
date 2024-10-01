package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.repository.DMSEntity;
import com.pay10.crm.dispute_management.DisplayChargebackList;
import com.pay10.crm.mongoReports.TxnReportForCb;

public class CbViewReport extends AbstractSecureAction {

	private int draw;
	private int length;
	private int start;

	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;

	private String merchant;

	private String pgRefNum;
	private String dateTo;

	private String dateFrom;
	private String cbCaseID;

	@Autowired
	private TxnReportForCb forChargeback;

	HttpServletRequest request = ServletActionContext.getRequest();

	HttpServletResponse response = ServletActionContext.getResponse();

	private List<DMSEntity> aaData = new ArrayList<DMSEntity>();
	private static final long serialVersionUID = -8081930439236006475L;
	private static final Logger logger = LoggerFactory.getLogger(DisplayChargebackList.class.getName());

	@Override
	public String execute() {

		BigInteger bigInt = BigInteger.valueOf(forChargeback.chargeBackReportCount(merchant,pgRefNum,cbCaseID,dateTo,dateFrom));
		setRecordsTotal(bigInt);
		if (getLength() == -1) {
			setLength(getRecordsTotal().intValue());
		}
		aaData=forChargeback.chargeBackReportReport(merchant,pgRefNum,cbCaseID,dateTo,dateFrom,start,length);

		recordsFiltered = recordsTotal;

		return SUCCESS;
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

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getCbCaseID() {
		return cbCaseID;
	}

	public void setCbCaseID(String cbCaseID) {
		this.cbCaseID = cbCaseID;
	}

	public List<DMSEntity> getAaData() {
		return aaData;
	}

	public void setAaData(List<DMSEntity> aaData) {
		this.aaData = aaData;
	}
}
