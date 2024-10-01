package com.pay10.pg.core.util;

import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;

@Service
public interface CryptoManager {
	public void secure(Fields fields) throws SystemException;

	public void removeSecureFields(Fields fields);

	public void hashCardDetails(Fields fields) throws SystemException;
	
	public void encryptCardDetails(Fields fields);
	
	public String maskCardNumber(String cardNumber);
}
