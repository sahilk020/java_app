package com.pay10.pg.action.service;

import java.util.Map;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;

public interface ActionServiceIRCTC {
	public Fields prepareFieldsIRCTC(Map<String, String[]> map) throws SystemException;
	
}
