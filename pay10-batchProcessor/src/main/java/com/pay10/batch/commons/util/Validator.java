package com.pay10.batch.commons.util;

import org.springframework.stereotype.Service;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.exception.SystemException;

@Service
public interface Validator {
	public void validate(Fields fields) throws SystemException;
}
