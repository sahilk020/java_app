package com.pay10.icici.netbanking;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class IciciNBSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(IciciNBSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private IciciNBTransformer iciciNBTransformer;

	public static final String PIPE = "|";
	public static final String DOUBLE_PIPE = "||";

	public Map<String, String> process(Fields fields) throws SystemException {
		
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
	//	generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String pipedResponse = fields.get(FieldType.ICICI_NB_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(pipedResponse);

		iciciNBTransformer = new IciciNBTransformer(transactionResponse);
		iciciNBTransformer.updateSaleResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.ICICI_NB_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String pipedResponse) {

		Transaction transaction = new Transaction();
		
		String responseArray [] = pipedResponse.split("&");
		
		for (String data : responseArray) {
			
			if (data.contains(Constants.PAID)) {
				String dataArray [] = data.split("=");
				transaction.setPaid(dataArray[1]);
			}
			
			else if (data.contains(Constants.PRN)) {
				String dataArray [] = data.split("=");
				transaction.setPrn(dataArray[1]);
			}
			
			else if (data.contains(Constants.BID)) {
				String dataArray [] = data.split("=");
				transaction.setBid(dataArray[1]);
			}
			
			else if (data.contains(Constants.ITC)) {
				String dataArray [] = data.split("=");
				transaction.setItc(dataArray[1]);
			}
			
			else if (data.contains(Constants.AMT)) {
				String dataArray [] = data.split("=");
				transaction.setAmt(dataArray[1]);
			}
			
			else if (data.contains(Constants.CRN)) {
				String dataArray [] = data.split("=");
				transaction.setCrn(dataArray[1]);
			}
			
			else if (data.contains(Constants.STATFLG)) {
				String dataArray [] = data.split("=");
				transaction.setStatFlg(dataArray[1]);
			}
			else if (data.contains("FEDID")) {
				String dataArray [] = data.split("=");
				transaction.setFedId(dataArray[1]);
			}
			else {
				continue;
			}
			
		}
		
		return transaction;
	}// toTransaction()


}
