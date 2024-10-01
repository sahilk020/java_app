package com.pay10.nodal.payout;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.pg.history.Historian;

/**
 * @author Amitosh
 *
 */

@Service
public class KotakBankNodalApi {

	private static final Logger logger = LoggerFactory.getLogger(KotakBankNodalApi.class);

	@Autowired
	@Qualifier("kotakBankTransactionConverter")
	private KotakBankTransactionConverter kotakBankTransactionConverter;

	@Autowired
	@Qualifier("kotakBankTransactionCommunicator")
	private KotakBankTransactionCommunicator kotakBankTransactionCommunicator;

	private SettlementTransformer settlementTransformer = null;

	@Autowired
	private Historian historian;

	public void process(Fields fields) throws SystemException, SOAPException {

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		fields.put(FieldType.TXNTYPE.getName(), txnType);
		if (txnType.equalsIgnoreCase(SettlementTransactionType.FUND_TRANSFER.getName())) {
			fundTransferIntegrator(fields);
		} else if (txnType.equalsIgnoreCase(SettlementTransactionType.STATUS.getName())) {
			statusEnqIntegrator(fields);
		}
	}

	public void fundTransferIntegrator(Fields fields) throws SystemException {
		logger.info("Settlement transaction initiated for Kotak Bank");
		Transaction transactionResponse = new Transaction();
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		fields.put(FieldType.OID.getName(), newTxnId);
		logger.info("Transaction Id: " + fields.get(FieldType.TXN_ID.getName()));
		logger.info("Order Id: " + fields.get(FieldType.OID.getName()));
		SOAPMessage request = kotakBankTransactionConverter.fundTransferRequest(fields);
		String response = kotakBankTransactionCommunicator.getFundTransferResponse(fields, request);
		transactionResponse = kotakBankTransactionConverter.toTransaction(response);
		settlementTransformer = new SettlementTransformer(transactionResponse);
		settlementTransformer.updateResponse(fields);
	}

	public void statusEnqIntegrator(Fields fields) {
		logger.info("Status check of Settlement transaction for Kotak Bank initiated");
		Transaction transactionResponse = new Transaction();
		try {
			historian.findPreviousForSettlement(fields);
		} catch (SystemException e) {
			logger.error("Exception caught: " + e);
		}
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		logger.info("Transaction Id: " + fields.get(FieldType.TXN_ID.getName()));
		logger.info("Order Id: " + fields.get(FieldType.OID.getName()));
		SOAPMessage request = KotakBankTransactionConverter.getStatusRequest(fields);
		String response = KotakBankTransactionConverter.getFundTransferResponse(fields, request);
		transactionResponse = KotakBankTransactionConverter.statusToTransaction(response);
		settlementTransformer = new SettlementTransformer(transactionResponse);
		settlementTransformer.updateResponse(fields);
	}
}
