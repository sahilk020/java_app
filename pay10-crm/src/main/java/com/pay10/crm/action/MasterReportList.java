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
import com.pay10.commons.user.MasterReportDto;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.TDRBifurcationReportDetails;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.crm.mongoReports.MasterReportStatus;
import com.pay10.crm.mongoReports.TransactionStatus;

public class MasterReportList extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;

	@Autowired
	private MasterReportStatus masterReportStatus;

	private int draw;
	private int length;
	private int start;

	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;

	private String type;
	private String date;
	private String yearmonth;
	private String fmmonth;
	private String tmmonth;

	public List<MasterReportDto> aaData=new ArrayList<>();
	
	public String execute() {

		
		if(StringUtils.isNotBlank(date) || StringUtils.isNotBlank(yearmonth) || StringUtils.isNotBlank(fmmonth) || StringUtils.isNotBlank(tmmonth)) {
			BigInteger bigInt = BigInteger
					.valueOf(masterReportStatus.getMasterReportCount(type, date, yearmonth, fmmonth, tmmonth));
			setRecordsTotal(bigInt);
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			recordsFiltered = recordsTotal;
			setAaData( masterReportStatus.getMasterReport(type, date, yearmonth, fmmonth, tmmonth,start,length));
		}else {
			setAaData(new ArrayList<MasterReportDto>());
		}
		

		return SUCCESS;
	}

	
	public List<MasterReportDto> getAaData() {
		return aaData;
	}


	public void setAaData(List<MasterReportDto> aaData) {
		this.aaData = aaData;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getYearmonth() {
		return yearmonth;
	}

	public void setYearmonth(String yearmonth) {
		this.yearmonth = yearmonth;
	}

	public String getFmmonth() {
		return fmmonth;
	}

	public void setFmmonth(String fmmonth) {
		this.fmmonth = fmmonth;
	}

	public String getTmmonth() {
		return tmmonth;
	}

	public void setTmmonth(String tmmonth) {
		this.tmmonth = tmmonth;
	}

}
