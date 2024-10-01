package com.pay10.commons.util;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Token;
import com.pay10.commons.user.UserDao;

/**
 * @author Sunil
 *
 */
@Service
public class TokenFactory {
	
	@Autowired
	private UserDao userDao;
	
	private static UserDao userDaoRef;
	
	@PostConstruct
	void init()
	{
		userDaoRef = this.userDao;
	}

	public static Token instanceDelete(Fields fields) throws SystemException{
		Token token = new Token();
		
		token.setCardSaveParam(fields.get(userDaoRef.findPayId(fields.get(FieldType.PAY_ID.getName())).getCardSaveParam()));
		token.setPayId(fields.get(FieldType.PAY_ID.getName()));
		token.setId(fields.get(FieldType.TOKEN_ID.getName()));
		
		return token;
	}
	
	public static Map<String, String> dcrypt(Fields fields, Map<String, String> requestMap) throws SystemException{
		
		//String key = keyProvider.getKey(fields); TODO
		//Scrambler scrambler = new Scrambler(key);
		//String pan = scrambler.decrypt(requestMap.get(FieldType.CARD_NUMBER.getName()));
		//String expDate = scrambler.decrypt(requestMap.get(FieldType.CARD_EXP_DT.getName()));
		//requestMap.put(FieldType.CARD_NUMBER.getName(),pan);
		//requestMap.put(FieldType.CARD_EXP_DT.getName(),expDate);
		requestMap.put(FieldType.MOP_TYPE.getName(),requestMap.get(FieldType.MOP_TYPE.getName()));
		requestMap.put(FieldType.PAYMENT_TYPE.getName(),requestMap.get(FieldType.PAYMENT_TYPE.getName()));
		return requestMap;
	}
	
	
public static Map<String, String> dcryptToken(String tokenId, Map<String, String> requestMap) throws SystemException{
		
		//String key = keyProvider.getKey(fields); TODO
		//Scrambler scrambler = new Scrambler(key);
		//String pan = scrambler.decrypt(requestMap.get(FieldType.CARD_NUMBER.getName()));
		//String expDate = scrambler.decrypt(requestMap.get(FieldType.CARD_EXP_DT.getName()));
		//requestMap.put(FieldType.CARD_NUMBER.getName(),pan);
		//requestMap.put(FieldType.CARD_EXP_DT.getName(),expDate);
		requestMap.put(FieldType.MOP_TYPE.getName(),requestMap.get(FieldType.MOP_TYPE.getName()));
		requestMap.put(FieldType.PAYMENT_TYPE.getName(),requestMap.get(FieldType.PAYMENT_TYPE.getName()));
		return requestMap;
	}
	
}
