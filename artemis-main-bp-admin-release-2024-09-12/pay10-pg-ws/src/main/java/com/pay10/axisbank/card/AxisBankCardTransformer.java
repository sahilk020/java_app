package com.pay10.axisbank.card;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;

@Service
public class AxisBankCardTransformer {

	private Transaction transaction = null;

	public AxisBankCardTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;
		
		if ((StringUtils.isNotBlank(transaction.getResponse_Err_Code()))
				&& (StringUtils.isNotBlank(transaction.getResponse_Err_Msg()))
				&& ((transaction.getResponse_Err_Code()).equalsIgnoreCase("00000"))
				&& ((transaction.getResponse_Sts_Flag()).equalsIgnoreCase("SUCCESS")))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else {
			if ((StringUtils.isNotBlank(transaction.getResponse_Err_Code()))
					&& (StringUtils.isNotBlank(transaction.getResponse_Err_Msg()))) {
				
				AxisBankCardResultType resultInstance = AxisBankCardResultType.getInstanceFromName(transaction.getResponse_Err_Code());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					pgTxnMsg = "Transaction failed at acquirer";
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

			}
		}
		 
		  fields.put(FieldType.STATUS.getName(), status);
		  fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		  fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
		  
		  if (StringUtils.isNotBlank(transaction.getResponse_GtwTraceNum())) {
			  fields.put(FieldType.AUTH_CODE.getName(), transaction.getResponse_GtwTraceNum());
		  }
		 
		  else {
			  fields.put(FieldType.AUTH_CODE.getName(), "0");
		  }
		  
		  fields.put(FieldType.RRN.getName(), transaction.getResponse_Ref_Num());
		  fields.put(FieldType.ACQ_ID.getName(), transaction.getResponse_Ref_Num());
		  fields.put(FieldType.PG_RESP_CODE.getName(),transaction.getResponse_Err_Code());
		  fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getResponse_Sts_Flag());
		  fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		 
	}
	
	
}
