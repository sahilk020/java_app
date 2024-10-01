package com.pay10.pg.core.util;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.SystemProperties;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.Validator;

@Service
public class AmexValidator implements Validator {
	
	@Override
	public void validate(Fields fields) throws SystemException {
		switch(TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))){
		case AUTHORISE:
		case SALE:
			break;
		case ENROLL:
			validateEnrollment(fields);
			break;
		case REFUND:
			break;
		case CAPTURE:
			break;
		case STATUS:
			break;
		default:
			break;
		}
	}
	
	public void validateEnrollment(Fields fields) throws SystemException{
		Collection<String> mandatoryEnrollmentFields = SystemProperties.getFssenrollmandatoryfields();
		validateMandatoryFields(fields,mandatoryEnrollmentFields);
	}
	
	public void validateMandatoryFields(Fields fields, Collection<String> mandatoryEnrollmentFields) throws SystemException{
		for(String key : mandatoryEnrollmentFields){
			if(null == fields.get(key)){				
				throw new SystemException(ErrorType.VALIDATION_FAILED,
						key + " is a required field");
			}else if(FieldType.CVV.getName().equals(key)){
				if(!(fields.get(key).length()==(FieldType.CVV.getMaxLength()))){
					throw new SystemException(ErrorType.VALIDATION_FAILED,
							key + " is invalid field");
				}
			}//if
		}//for
	}
}
