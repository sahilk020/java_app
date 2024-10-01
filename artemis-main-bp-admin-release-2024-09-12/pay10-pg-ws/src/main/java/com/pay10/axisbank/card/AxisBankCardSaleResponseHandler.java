package com.pay10.axisbank.card;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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
public class AxisBankCardSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(AxisBankCardSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	public static final String PIPE = "|";
	public static final String DOUBLE_PIPE = "||";

	public Map<String, String> process(Fields fields) throws SystemException {
		
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String pipedResponse = fields.get(FieldType.DIRECPAY_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(pipedResponse);

		AxisBankCardTransformer axisBankCardTransformer = new AxisBankCardTransformer(transactionResponse);
		axisBankCardTransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.DIRECPAY_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String pipedResponse) {

		Transaction transaction = new Transaction();
		String responseparamsArray [] = pipedResponse.split(Pattern.quote("||"));
		
		if (responseparamsArray.length < 4) {
			return transaction;
		}
		
		String response_PBM = "";
		String response_TxnData = "";
		String response_RespData = "";
		String response_StatusData = "";
		
		
		
		if (StringUtils.isNotBlank(responseparamsArray[0])) {
			response_PBM = responseparamsArray[0];
		}
		
		if (StringUtils.isNotBlank(responseparamsArray[1])) {
			response_TxnData = responseparamsArray[1];
		}
		
		if (StringUtils.isNotBlank(responseparamsArray[2])) {
			response_RespData = responseparamsArray[2];
		}
		
		if (StringUtils.isNotBlank(responseparamsArray[3])) {
			response_StatusData = responseparamsArray[3];
		}
		
		
		if (response_PBM.equalsIgnoreCase("1010000")) {
		
			transaction.setResponse_Sts_Flag("FAILURE");
			transaction.setResponse_Err_Code("10025");
			transaction.setResponse_Err_Msg("Invalid Gateway ID");
		}
		
		
		String response_TxnDataArray [] = response_TxnData.split(Pattern.quote("|"));
		// Handled For Net Baking
		if (response_TxnDataArray [0].equalsIgnoreCase("111101")) {
			transaction.setResponse_Mer_Ord_Num(response_TxnDataArray[1]);
			transaction.setResponse_Curr(response_TxnDataArray[2]);
			transaction.setResponse_Amt(response_TxnDataArray[3]);
			transaction.setResponse_PayMode(response_TxnDataArray[4]);
			transaction.setResponse_TxnType(response_TxnDataArray[5]);
		}
	
		
		String response_RespDataArray [] = response_RespData.split(Pattern.quote("|"));
		// Net Banking Success response
		if (response_RespDataArray [0].equalsIgnoreCase("1100110")) {
			transaction.setResponse_Ref_Num(response_RespDataArray[1]);
			transaction.setResponse_Txn_Date(response_RespDataArray[2]);
			transaction.setResponse_GtwTraceNum(response_RespDataArray[3]);
			transaction.setResponse_GtwIdentifier(response_RespDataArray[4]);
		}
		
		// Net Banking Failure response
		if (response_RespDataArray [0].equalsIgnoreCase("1100010")) {
			
			transaction.setResponse_Ref_Num(response_RespDataArray[1]);
			transaction.setResponse_Txn_Date(response_RespDataArray[2]);
			transaction.setResponse_GtwIdentifier(response_RespDataArray[3]);
		}
		
		// Handled For Net Baking
		String response_StatusDataArray [] = response_StatusData.split(Pattern.quote("|"));
		
		if (response_StatusDataArray[0].equalsIgnoreCase("111")) {
			
			transaction.setResponse_Sts_Flag(response_StatusDataArray[1]);
			transaction.setResponse_Err_Code(response_StatusDataArray[2]);
			transaction.setResponse_Err_Msg(response_StatusDataArray[3]);
		}
		
		return transaction;
	}// toTransaction()


}
