package com.pay10.crm.chargeback;

import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.mongo.MongoInstance;

public class TransactionDetailFactory {
	@Autowired
	MongoInstance mongoInstance;
	
	
	public  static TransactionDetailProvider getTransactionDetail(){
		return new DefaultTransactionDetailProvider();
	}
}
