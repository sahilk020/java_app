package com.pay10.batch.commons.util;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.Constants;


@Service
public class Amount {
	public static final char DECIMAL = '.';

	@Autowired
	private SystemProperties systemProperties;
	
	public String toDecimal(String amount, String currencyCode){				
		StringBuilder decimalAmount = new StringBuilder();
		int places = Integer.parseInt(systemProperties.getCurrencies().get(currencyCode));
		decimalAmount.append(amount.substring(0, amount.length() - places));
		decimalAmount.append(DECIMAL);
		decimalAmount.append(amount.substring(amount.length() - places));
		
		return decimalAmount.toString();
	}
	
	public String convertToDecimal(String amount, String currencyCode){
		int places = Integer.parseInt(systemProperties.getCurrencies().get(currencyCode));
		String str = String.format ("%." + places + "f", Float.parseFloat(amount.toString()));
		return str;
	}
	
	public String formatAmount(String amount, String currencyCode) {
	
		final int places = Integer.parseInt(systemProperties.getCurrencies().get(currencyCode));		
		BigDecimal amountValue = new BigDecimal(amount).setScale(places, BigDecimal.ROUND_HALF_UP);
		amount = amountValue.toString();
		amount = amount.replaceAll("\\.", Constants.BLANK_REPLACEMENT_STRING.getValue());
		return amount;	
	}	
}
