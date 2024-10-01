package com.pay10.crypto.key;

import com.pay10.commons.exception.SystemException;

public interface KeyProvider {

	public String generateKey(String payId) throws SystemException;
	
}
