package com.pay10.pg.action.service;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
@Service
public class CardBinProcessor {
	
	public String getCardBin(Fields fields) {
		String cardNumber = fields.get(FieldType.CARD_NUMBER.getName());
		String cardBin = cardNumber.replace(" ", "").replace(",", "").substring(0, 6);
		
		return cardBin;
	}
	

}
