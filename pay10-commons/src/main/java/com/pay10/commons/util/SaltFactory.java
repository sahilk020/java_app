package com.pay10.commons.util;

import java.util.UUID;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.MerchantKeySaltDao;
import com.pay10.commons.user.User;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Puneet
 *
 */


public class SaltFactory {


	public static final String dash = "-";

	//Generate new random salt
	public static String generateRandomSalt(){
		UUID uuId = UUID.randomUUID();
		String salt = uuId.toString().replaceAll(dash,Constants.BLANK_REPLACEMENT_STRING.getValue()).substring(0, Integer.parseInt(Constants.SALT_LENGTH.getValue()));
		return salt;
	}

	//get salt
	public static String getSaltProperty(User user){

		String salt = (new PropertiesManager()).getSalt(user.getPayId());

		if(salt==null){
			salt = (new MerchantKeySaltDao()).find(user.getPayId()).getSalt();
		}
		return salt;
	}
	
	public static String getKeySaltProperty(User user){
		String keySalt = null;
		try {
		(new PropertiesManager()).getKeySalt(user.getPayId());

		if(keySalt==null){
			keySalt = (new MerchantKeySaltDao()).find(user.getPayId()).getKeySalt();
		}
		
			return (Hasher.getHash(keySalt+user.getPayId())).substring(0,32);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return keySalt;
		}
	}
}
