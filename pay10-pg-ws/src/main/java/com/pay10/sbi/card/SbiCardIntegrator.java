package com.pay10.sbi.card;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SbiCardIntegrator {

    private static final Logger logger = LoggerFactory.getLogger(SbiCardIntegrator.class.getName());
    @Autowired
    @Qualifier("sbiCardTransactionConverter")
    private TransactionConverter converter;

    @Autowired
    @Qualifier("sbiCardTransactionCommunicator")
    private TransactionCommunicator communicator;

    @Autowired
    private TransactionFactory TransactionFactory;

    private SbiCardTransformer sbiTransformer = null;

    @Autowired
    @Qualifier("updateProcessor")
    private Processor updateProcessor;

    public void process(Fields fields) throws SystemException {

        send(fields);

    }// process

    public void send(Fields fields) throws SystemException {

        Transaction transactionRequest = new Transaction();
        Transaction transactionResponse = new Transaction();

        logger.info("Request Received at SbiCardIntegrator :: " + fields.getFieldsAsString());
        if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
            fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
        }

        transactionRequest = TransactionFactory.getInstance(fields);

        String request = converter.perpareRequest(fields, transactionRequest);

        String txnType = fields.get(FieldType.TXNTYPE.getName());
        if (txnType.equals(TransactionType.SALE.getName())) {
        	if(fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.RUPAY.getCode())) {
        		
        	}else {
        		
        	
            communicator.updateSaleResponse(fields, request);
        	}
        } else {
        	if(fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.RUPAY.getCode())) {
        		 transactionResponse = converter.toTransactionRupay(new JSONObject(request));
                 sbiTransformer = new SbiCardTransformer(transactionResponse);
                 sbiTransformer.updateResponse(fields);	
        	}else {
            transactionResponse = converter.toTransaction(new JSONObject(request));
            sbiTransformer = new SbiCardTransformer(transactionResponse);
            sbiTransformer.updateResponse(fields);
        	}
        }

    }

    public Map<String, String> processFinalReq(Fields fields) throws SystemException {
        String res = converter.preparePrqFReq(fields);
        JSONObject response = new JSONObject(res);
        converter.prepareAuthorizationReq(fields, response);
        return fields.getFields();
    }
}
