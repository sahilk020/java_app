package com.pay10.crypto.scrambler;

import com.pay10.commons.exception.SystemException;

public interface ScramblerService {

	public Scrambler getScrambler(String payId) throws SystemException;
	public Scrambler getScramblerWithKey(String keyId);
}
