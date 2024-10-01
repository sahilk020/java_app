package com.pay10.migs;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

/**
 * @author Sunil,Puneet
 *
 */
public class Mapper {
	public static PaymentMode  getPaymentMode(Fields fields){
		PaymentMode paymentMode = null;
		
		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		if(paymentType.equals(PaymentType.CREDIT_CARD.getCode())){
			paymentMode = PaymentMode.CREDIT_CARD;
		} else if(paymentType.equals(PaymentType.DEBIT_CARD.getCode())){
			paymentMode = PaymentMode.DEBIT_CARD;
		} else if(paymentType.equals(PaymentType.NET_BANKING.getCode())){
			paymentMode = PaymentMode.NET_BANKING;
		}
		
		return paymentMode;
	}			
	
	public static ErrorType getErrorType(String amexResponseCode){
		ErrorType error = null;
		
		if(amexResponseCode.equals("0")){
			error = ErrorType.SUCCESS;
		} else if(amexResponseCode.equals("03")){
			error = ErrorType.TIMEOUT;
		} else if(amexResponseCode.equals("10")){
			error = ErrorType.SUCCESS;
		} else if(amexResponseCode.equals("141")){
			error = ErrorType.CANCELLED;
		} else if(amexResponseCode.equals("142")){
			error = ErrorType.CANCELLED;
		} else {
			error = ErrorType.DECLINED;
		}
		
		return error;
	}
	
	public static StatusType getStatusType(String amexResponseCode){
		StatusType status = null;
		if(amexResponseCode.equals("0")){
			status = StatusType.CAPTURED;
		} else if(amexResponseCode.equals("01")){
			status = StatusType.CAPTURED;
		} else if(amexResponseCode.equals("03")){
			status = StatusType.TIMEOUT;
		} else if(amexResponseCode.equals("10")){
			status = StatusType.SETTLED_SETTLE;
		} else if(amexResponseCode.equals("141")){
			status = StatusType.CANCELLED;
		} else if(amexResponseCode.equals("142")){
			status = StatusType.CANCELLED;
		} else {
			status = StatusType.DECLINED;
		}
		
		return status;
	}
	
	public static String getTransactionUrl(Fields fields){
		String url="";
		switch(TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))){		
		case AUTHORISE:			
		case CAPTURE:
			if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {
				url = ConfigurationConstants.AXIS_MIGS_SUPPORT_URL.getValue();
			} else {
				url = ConfigurationConstants.AXIS_MIGS_SUPPORT_URL.getValue();
			}
			break;
		case ENQUIRY:
			break;
		case ENROLL:
			if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {
				url = ConfigurationConstants.AXIS_MIGS_TRANSACTION_URL.getValue();
			} else {
				url = ConfigurationConstants.AXIS_MIGS_TRANSACTION_URL.getValue();
			}
			break;
		case REFUND:
			break;		
		default:
			break;
	 }
		return url;
  }
}
