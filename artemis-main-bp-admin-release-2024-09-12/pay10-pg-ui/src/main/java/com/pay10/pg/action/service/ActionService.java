package com.pay10.pg.action.service;

import java.util.Map;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;

public interface ActionService {

	public Fields prepareFields(Map<String, String[]> map) throws SystemException;

	public Fields prepareFieldsMerchantHosted(Map<String, String[]> map) throws SystemException;

}
