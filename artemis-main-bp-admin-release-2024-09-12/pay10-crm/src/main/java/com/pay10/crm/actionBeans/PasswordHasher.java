package com.pay10.crm.actionBeans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.MerchantKeySaltService;

/**
 * @author Puneet
 *
 */
public class PasswordHasher {
	private static Logger logger = LoggerFactory.getLogger(PasswordHasher.class.getName());
	
	@Autowired 
	MerchantKeySaltService merchantKeySaltService;
	
	public static String hashPassword(String password,String payId) throws SystemException{
	
		//String salt = (new PropertiesManager()).getSalt(payId);	
		String salt = new MerchantKeySaltService().getSalt(payId);
		logger.info("PasswordHasher For PayId={},Salt={} ",payId,salt);
		if(null==salt){
			throw new SystemException(ErrorType.AUTHENTICATION_UNAVAILABLE, ErrorType.AUTHENTICATION_UNAVAILABLE.getResponseCode());
		}
		
		String hashedPassword = Hasher.getHash(password.concat(salt));
		logger.info("hashedPassword For PayId={},hashedPassword={} ",payId,hashedPassword);
		return hashedPassword;		
	}
//	public static void main(String[] args)  {
//	
//	String payid="1003510720020615";
//	String password="szWOd8dyXXEUbl8lMQ@!";
//	
//			
//			String hashedPassword;
//			try {
//				hashedPassword = hashPassword(password,payid);
//				System.out.println("Original String to    anisha - " + hashedPassword);
//
//			} catch (SystemException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				System.out.println("fail");
//			}
//			
//		}
//		
	
}

	
