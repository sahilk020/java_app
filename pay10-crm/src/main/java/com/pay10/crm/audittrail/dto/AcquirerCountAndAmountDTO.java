package com.pay10.crm.audittrail.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public class AcquirerCountAndAmountDTO {
	private String acquirerName;
	private BigDecimal totalAmount;
	private BigInteger totalCount;

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigInteger getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(BigInteger totalCount) {
		this.totalCount = totalCount;
	}
	
	public Object[] myCsvMethod() {
		  Object[] objectArray = new Object[3];
		  objectArray[0] = acquirerName;
		  objectArray[1] = totalAmount;
		  objectArray[2] = totalCount;
		  return objectArray;
	}

}
