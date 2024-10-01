package com.pay10.kotak.nb;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;


@Service
public class KotakTransformernb {
	private static Logger logger = LoggerFactory.getLogger(KotakTransformernb.class.getName());
	
	private Transaction transaction = null;

	public KotakTransformernb(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateRefundResponse(Fields fields) {
		

			String status = "";
			ErrorType errorType = null;
			String pgTxnMsg = null;

			if ((StringUtils.isNotBlank(transaction.getAuthStatus())) && ((transaction.getAuthStatus()).equalsIgnoreCase("Y"))) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

			} else {
				
				status = StatusType.FAILED_AT_ACQUIRER.getName();
				errorType = ErrorType.getInstanceFromCode("022");
				pgTxnMsg = "Transaction failed at acquirer";
				
			}
			

			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.TXN_ID.getName(), transaction.getTransactionId());
			fields.put(FieldType.AUTH_CODE.getName(), transaction.getAuth_code());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getBankReferenceNumber());
	        String amount1 = Amount.formatAmount(transaction.getAmount(), "356");

			fields.put(FieldType.AMOUNT.getName(), amount1);


			if (StringUtils.isNoneBlank(transaction.getStatus())) {
				fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getAuthStatus());
			} else {
				errorType.getResponseCode();
			}

			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
			fields.put(FieldType.RRN.getName(), transaction.getTransactionId());
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getAuthStatus());
			fields.put(FieldType.DATE_TIME.getName(), transaction.getDateAndTime());
			logger.info("kotak response "+fields.toString());
		}

}