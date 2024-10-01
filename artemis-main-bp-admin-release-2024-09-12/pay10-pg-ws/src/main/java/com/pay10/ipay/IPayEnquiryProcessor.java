package com.pay10.ipay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.TransactionProcessor;

@Service
public class IPayEnquiryProcessor implements TransactionProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(IPayEnquiryProcessor.class.getName());
	
	@Autowired
	@Qualifier("iPayTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	@Qualifier("iPayTransactionCommunicator")
	private TransactionCommunicator communicator;
	
	@Autowired
	private IPayEnquiryResponseProcessor responseProcessor;

	@Override
	public void transact(Fields fields) throws SystemException {
		logger.info("Generating status enquiry transaction for IPay Net Banking.");
		
		converter.createStatusEnquiryTransaction(fields);
		String encryptedResponse = communicator.transactEnquiry(fields);
		fields.put(FieldType.IPAY_FINAL_ENC_RESPONSE.getName(), encryptedResponse);
		responseProcessor.process(fields);
		// Update the Fields object, scheduler will take care of updating to DB and sending response back to Merchant
	}
	
}
