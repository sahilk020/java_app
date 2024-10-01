package com.pay10.commons.util;

import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;

@Service
public interface Validator {
	public void validate(Fields fields) throws SystemException;
}
