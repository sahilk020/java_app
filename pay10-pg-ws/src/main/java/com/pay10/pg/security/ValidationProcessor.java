package com.pay10.pg.security;

import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.Processor;
@Service
public class ValidationProcessor implements Processor {

	@Override
	public void preProcess(Fields fields) {
	}

	@Override
	public void process(Fields fields) throws SystemException {
		Validator validator = ValidatorFactory.getValidator();
		validator.validate(fields);
	}

	@Override
	public void postProcess(Fields fields) {
	}
}
