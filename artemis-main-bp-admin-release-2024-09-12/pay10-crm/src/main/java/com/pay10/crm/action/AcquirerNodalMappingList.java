package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.AcquirerNodalMappingDao;
import com.pay10.commons.entity.AcqurerNodalMapping;
import com.pay10.commons.util.DataEncoder;

public class AcquirerNodalMappingList extends AbstractSecureAction {

	

	@Autowired
	private DataEncoder encoder;
	
	private static Logger logger = LoggerFactory.getLogger(AcquirerNodalMappingList.class.getName());

	private static final long serialVersionUID = -6919220389124792416L;
	private int draw;
	private int length;
	private int start;
	

	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;



	private List<AcqurerNodalMapping> aaData = new ArrayList<AcqurerNodalMapping>();
	@Autowired
	private AcquirerNodalMappingDao acquirerNodalMappingDao;



	@Override
	public String execute() {

		try {
			BigInteger bigInt = BigInteger.valueOf(acquirerNodalMappingDao.getAllSize());
			
			setRecordsTotal(bigInt);
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			aaData=acquirerNodalMappingDao.getAllAcquirerNodalMapping(length, start);
			recordsFiltered = recordsTotal;
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
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




	public List<AcqurerNodalMapping> getAaData() {
		return aaData;
	}




	public void setAaData(List<AcqurerNodalMapping> aaData) {
		this.aaData = aaData;
	}
	

}
