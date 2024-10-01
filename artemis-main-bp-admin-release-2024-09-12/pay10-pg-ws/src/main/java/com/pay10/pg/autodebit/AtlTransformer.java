package com.pay10.pg.autodebit;

import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;

@Service
public class AtlTransformer {


	public void updateResponse(Fields fields) {

		String status = getStatus(fields);
		ErrorType errorType = getResponseCode(fields);

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

	}

	public ErrorType getResponseCode(Fields fields) {
		String result = fields.get(FieldType.PG_TXN_STATUS.getName());
		ErrorType errorType = null;

		if(result.equalsIgnoreCase(Constants.SUCCESS)) {
			errorType = ErrorType.SUCCESS;
		} else {	
			errorType = ErrorType.DECLINED;
		}
		
		return errorType;
	}

	public String getStatus(Fields fields) {
		String result = fields.get(FieldType.PG_TXN_STATUS.getName());
		String status = "";

		if(result.equalsIgnoreCase(Constants.SUCCESS)) {
			status = StatusType.CAPTURED.getName();
		}else {	
			status = StatusType.FAILED.getName();
		}

		return status;
	}

}
