package com.pay10.migs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AbstractTransactionProcessorFactory;
import com.pay10.pg.core.util.TransactionProcessor;
@Component("ezeeClick")
public class EzeeClickTransactionProcessorFactory implements
		AbstractTransactionProcessorFactory {
		@Autowired
		private EzeeClickRefundTransactionProcessor ezeeClickRefundTransactionProcessor;
		
		@Autowired
		private EzeeClickSaleTransactionProcessor ezeeClickSaleTransactionProcessor;
		
		@Autowired
		private EzeeClickStatusTransactionProcessor ezeeClickStatusTransactionProcessor;
		
		
		@Override
		public TransactionProcessor getInstance(Fields fields) throws SystemException{		
			
		
			switch(TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))){
			case REFUND:
				return ezeeClickRefundTransactionProcessor;
			case SALE:
			case ENROLL:
			case AUTHORISE:
				return ezeeClickSaleTransactionProcessor;
			case STATUS:
				return ezeeClickStatusTransactionProcessor;
			default:
				throw new SystemException(ErrorType.ACQUIRER_ERROR, "Unsupported transaction type for ezeeclick");
			}
			
		}
}