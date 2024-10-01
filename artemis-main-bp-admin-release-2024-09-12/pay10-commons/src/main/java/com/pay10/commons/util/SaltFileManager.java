package com.pay10.commons.util;

import org.springframework.stereotype.Service;

@Service("saltFileManager")
public class SaltFileManager extends PropertiesManager{

	public boolean insertSalt(String payId,String salt){
		return setProperty(payId,salt,Constants.SALT_FILE_PATH_NAME.getValue());		
	}
	
	public boolean insertKeySalt(String payId,String salt){
		return setProperty(payId,salt,Constants.KEYSALT_FILE_PATH_NAME.getValue());		
	}
}
