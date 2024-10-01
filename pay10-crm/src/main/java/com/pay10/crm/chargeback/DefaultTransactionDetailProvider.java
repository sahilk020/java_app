package com.pay10.crm.chargeback;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.TransactionDetailsService;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.TransactionHistory;
@Component
public class DefaultTransactionDetailProvider implements TransactionDetailProvider{
	@Autowired
	MongoInstance mongoInstance;

	public DefaultTransactionDetailProvider(){
		
	}

	@Override
	public List<TransactionHistory> getAllTransactionsFromDb(String txnId) throws SystemException {
		
		TransactionDetailsService transactionService = new TransactionDetailsService();
		return transactionService.getTransaction(txnId);		
	}
	
	@Override
	public void getCapturedTransactionsFromDb(String ordereId, String txnId) {
		
		
		
	}
}
