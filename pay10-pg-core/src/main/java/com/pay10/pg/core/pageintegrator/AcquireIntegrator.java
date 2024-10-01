package com.pay10.pg.core.pageintegrator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.ResponseCreator;

/**
 * @author Sunil
 *
 */
public class AcquireIntegrator implements Processor {

	@Autowired
	private AcquirerCustomizerFactory AcquirerCustomizerFactory;
	
	@Autowired
	private ResponseCreator responseCreator;
	
	@Override
	public void preProcess(Fields fields) throws SystemException {
	}

	@Override
	public void process(Fields fields) throws SystemException {
		String responseCode = fields.get(FieldType.RESPONSE_CODE.getName());
		if(!StringUtils.isEmpty(responseCode) && !responseCode.equals(ErrorType.SUCCESS.getCode())){
			if(fields.get(FieldType.STATUS.getName()).equals(StatusType.PENDING.getName())){
				fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
			}
			responseCreator.ResponsePost(fields);
			return;
		}

		Customizer customizer = AcquirerCustomizerFactory.instance(fields);
		if(!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.CITRUS_PAY.getCode())){
			fields.remove(FieldType.ACQUIRER_TYPE.getName());
		}
		customizer.integrate(fields);
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {

	}
}
