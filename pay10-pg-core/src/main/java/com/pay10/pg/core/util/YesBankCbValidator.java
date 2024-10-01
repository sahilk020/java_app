package com.pay10.pg.core.util;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.SystemProperties;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.Validator;

@Service
public class YesBankCbValidator implements Validator {
    private static final String dummyCVV = "111";
    private static final String expDate = "102055";
	@Override
	public void validate(Fields fields) throws SystemException {
		
		switch(TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))){
		case AUTHORISE:
			break;
		case ENROLL:
			validateEnrollment(fields);
			break;
		case REFUND:
			break;
		case SALE:
			break;
		case CAPTURE:
			break;
		case STATUS:
			break;
		default:
			break;
		}
		
	}//validate()
	
	public boolean isValidOrigTransactionType(String origTransactionType){		
		
		boolean valid = false;
		
		if(null != origTransactionType && (origTransactionType.equals(TransactionType.SALE.getName())
				|| origTransactionType.equals(TransactionType.AUTHORISE.getName()))){
			valid = true;
		}
		
		return valid;
	}//isValidOrigTransactionType()

	public void validateEnrollment(Fields fields) throws SystemException{
		Collection<String> mandatoryEnrollmentFields = SystemProperties.getFssenrollmandatoryfields();
		validateMandatoryFields(fields,mandatoryEnrollmentFields);
	}

	public void validateMandatoryFields(Fields fields, Collection<String> mandatoryEnrollmentFields) throws SystemException{
		String mopType = fields.get(FieldType.MOP_TYPE.getName());		
		if(null==mopType){
			throw new SystemException(ErrorType.VALIDATION_FAILED,
					FieldType.MOP_TYPE.getName() + " is a required field");
		}
		
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equals(PaymentType.UPI.getCode())) {
            return;
        }

	}
}
