package com.pay10.crm.action;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReports;

public class GlobalTransactionSearchActionAdmin extends AbstractSecureAction {



	@Autowired
	private DataEncoder encoder;

	@Autowired
	private TxnReports txnReports;
	
	
	private static Logger logger = LoggerFactory.getLogger(GlobalTransactionSearchActionAdmin.class.getName());

	private static final long serialVersionUID = -6919220389124792416L;

	private String fieldType;
	private String fieldTypeValue;
	
	private String dateFrom;
	private String dateTo;

	private int draw;
	private int length;
	private int start;
	
	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;

	

	public DataEncoder getEncoder() {
		return encoder;
	}



	public void setEncoder(DataEncoder encoder) {
		this.encoder = encoder;
	}



	public String getFieldType() {
		return fieldType;
	}



	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}



	public String getFieldTypeValue() {
		return fieldTypeValue;
	}



	public void setFieldTypeValue(String fieldTypeValue) {
		this.fieldTypeValue = fieldTypeValue;
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



	public List<TransactionSearchNew> getAaData() {
		return aaData;
	}



	public void setAaData(List<TransactionSearchNew> aaData) {
		this.aaData = aaData;
	}



	private List<TransactionSearchNew> aaData = new ArrayList<TransactionSearchNew>();
	


	@Override
	public String execute() {	
		try {
			
			if(!StringUtils.isBlank(getFieldTypeValue())) {
			BigInteger bigInt = BigInteger.valueOf(txnReports.globalSearchTransactionCount(getFieldType(),getFieldTypeValue(),dateFrom, dateTo));
			logger.info("Inside TransactionSearchActionAdmin , bigInt=========== = " + bigInt);

			setRecordsTotal(bigInt);
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			aaData = encoder.encodeTransactionSearchObjNew(txnReports.globalSearchTransaction(getFieldType(),getFieldTypeValue(),dateFrom, dateTo));

			aaData = aaData.stream().sorted(new Comparator<TransactionSearchNew>() {

				@Override
				public int compare(TransactionSearchNew o1, TransactionSearchNew o2) {
					String date1 = StringUtils.isBlank(o1.getCreateDate()) ? o1.getDateFrom() : o1.getCreateDate();
					String date2 = StringUtils.isBlank(o2.getCreateDate()) ? o2.getDateFrom() : o2.getCreateDate();
					return date2.compareTo(date1);
				}
			}).collect(Collectors.toList());
			recordsFiltered = recordsTotal;
			
			}else {
				List<TransactionSearchNew>empty=new ArrayList<>();
				aaData = encoder.encodeTransactionSearchObjNew(empty);	
			}	

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		logger.info("return value : "+new Gson().toJson(aaData));
		return SUCCESS;

	}

	



	

	
}
