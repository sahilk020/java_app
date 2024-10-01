package com.pay10.crypto.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;

@Service
public class Validator {

	public void validateRequest(String payId, String data) throws SystemException {
		if(StringUtils.isEmpty(payId) || StringUtils.isEmpty(data)) {
			throw new SystemException(ErrorType.INVALID_REQUEST_FIELD,"Invalid PAY_ID or Data: ");
		}
	}
}
