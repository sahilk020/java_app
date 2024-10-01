package com.pay10.pg.core.util;

import org.springframework.stereotype.Service;

@Service
public class CryptoManagerFactory {

	public CryptoManagerFactory() {
	}
	
	public static CryptoManager getCryptoManager(){
		return new DefaultCryptoManager();
	}

}
