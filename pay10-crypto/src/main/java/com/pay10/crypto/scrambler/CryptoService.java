package com.pay10.crypto.scrambler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.crypto.util.Validator;

@Service
public class CryptoService {
	@Autowired
	@Qualifier("scramblerService")
	private ScramblerService scramblerService;
	
	@Autowired
	private Validator validator;

	
	public String decrypt(String payId,String data) throws SystemException {
		validator.validateRequest(payId, data);
		Scrambler scrambler = scramblerService.getScrambler(payId);
		return scrambler.decrypt(data);
	}

	public String encrypt(String payId, String data) throws SystemException {
		validator.validateRequest(payId, data);
		Scrambler scrambler = scramblerService.getScrambler(payId);
		return scrambler.encrypt(data);
	}
}