package com.pay10.pg.emitra;

import java.util.Map;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;

public interface EmitraService {

	public Map<String, String> statusInquiry(Fields fields) throws SystemException;

	public Map<String, String> refund(Map<String, String> request);
	
	public Map<String, String> refundStatusInquiry(Fields fields) throws SystemException;
}
