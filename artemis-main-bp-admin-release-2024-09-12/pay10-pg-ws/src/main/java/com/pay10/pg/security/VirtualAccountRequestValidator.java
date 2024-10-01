package com.pay10.pg.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.YBNNotificationObj;
import com.pay10.pg.core.pageintegrator.GeneralValidator;

/**
 * @author Puneet
 *
 */
@Service
public class VirtualAccountRequestValidator {

	@Autowired
	private GeneralValidator generalValidator;
	
	public void validateBasicDetails(YBNNotificationObj yBNNotificationObj) throws SystemException {
		Map<String,String> requestMap= YBNNotificationObj.creatRequestMap(yBNNotificationObj);
		generalValidator.validateRequestMap(requestMap);
	}
}
