package com.pay10.ipay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AbstractTransactionProcessorFactory;
import com.pay10.pg.core.util.TransactionProcessor;

@Component("iPayTxnProcessor")
public class IPayTransactionProcessorFactory implements AbstractTransactionProcessorFactory {
	
	@Autowired
	private IPaySaleProcessor iPaySaleProcessor;
	
	@Autowired
	private IPayEnquiryProcessor iPayEnquiryProcessor;
	
	@Autowired
	private IPayRefundProcessor iPayRefundProcessor;
	
	
	@Override
	public TransactionProcessor getInstance(Fields fields) throws SystemException {
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		switch (TransactionType.getInstance(txnType)) {
		case SALE:
			return iPaySaleProcessor;
		case ENQUIRY:
			return iPayEnquiryProcessor; 
		case REFUND:
			return iPayRefundProcessor;
		default:
			throw new SystemException(ErrorType.ACQUIRER_ERROR, "Unsupported transaction type for IPay Net Banking Processor");
		}
	} 

}
