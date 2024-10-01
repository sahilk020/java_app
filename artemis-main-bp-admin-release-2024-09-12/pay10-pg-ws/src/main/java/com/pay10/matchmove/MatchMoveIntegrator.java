package com.pay10.matchmove;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;


@Service
public class MatchMoveIntegrator {
	
	private static Logger logger = LoggerFactory.getLogger(MatchMoveIntegrator.class.getName());
	
	@Autowired
	@Qualifier("matchMoveTransactionConverter")
	private TransactionConverter converter;

	
	@Autowired
	@Qualifier("matchMoveTransactionCommunicator")
	private TransactionCommunicator communicator;
	

	@Autowired
	@Qualifier("matchMoveFactory")	
	private TransactionFactory TransactionFactory;
	
	private MatchMoveTransformer iMudraTransformer = null;
	
	public void process(Fields fields) throws SystemException {

		send(fields);

	} // process
	
	public void send(Fields fields) throws SystemException {
   
		logger.info("inside MatchMoveIntegrator class, send function");
		
		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		transactionRequest = TransactionFactory.getInstance(fields);
		String request = converter.perpareRequest(fields, transactionRequest);
        	
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			String initalResponse = communicator.getResponse(request, fields);
			transactionResponse = converter.toInitialTransaction(fields,initalResponse);
			if(StringUtils.isNotBlank(transactionResponse.getResponse_code())&&(transactionResponse.getResponse_code().equals("CP0200")))
			{
			communicator.updateSaleResponse(fields, transactionResponse.getRedirect_url());
			}
			else {
			iMudraTransformer = new MatchMoveTransformer(transactionResponse);
			iMudraTransformer.updateInitalResponse(fields);
			}
		} else if (txnType.equals(TransactionType.REFUND.getName())) {
			String response = communicator.getResponse(request, fields);
			transactionResponse = converter.toTransaction(fields,response);
			iMudraTransformer = new MatchMoveTransformer(transactionResponse);
			iMudraTransformer.updateResponse(fields);
		} else {
			String response = communicator.getResponse(request, fields);
//			transactionResponse = converter.toStatusTransaction(fields,response);
//			idbiTransformer = new IdbiTransformer(transactionResponse);
//			idbiTransformer.updateResponse(fields);
		}

	}
}
