package com.crmws.service;

import java.util.List;
import java.util.Map;

import com.pay10.commons.user.TPAPTransactionLimit;

public interface TPAPService {

	List<TPAPTransactionLimit> getTPAPTransactionLimitDetails();

	boolean tpapTransactionLimitDetails(Map<String, String> actionRequest);

	boolean addTPAPTransactionLimitInfo(Map<String, String> reqestInfo);

	boolean updateTpapTxnLimit(Map<String, String> actionRequest);

	boolean checkDuplicateTxnLimitId(String txnLimitId);

	boolean inactiveTxnLimitById(String txnLimitId);

}
