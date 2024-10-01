package com.pay10.crm.chargeback;

import java.util.List;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.TransactionHistory;

public interface TransactionDetailProvider {

	public  List<TransactionHistory> getAllTransactionsFromDb(String txnId)  throws SystemException;
	public void getCapturedTransactionsFromDb(String orderId, String txnId);
}
