package com.pay10.crm.actionBeans;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.Currency;

/**
 * @author shashi
 *
 */

@Service
public class CurrencyMapProvider{
	
	
	@Autowired
	private UserDao userDao;
	
	private static Logger logger = LoggerFactory
			.getLogger(CurrencyMapProvider.class.getName());

	public Map<String, String> currencyMap(User user) {
		
		Map<String, String> tempMap;
		String currencyKey = user.getDefaultCurrency();
		Map<String, String> currencyMap = new LinkedHashMap<String, String>();
		if (user.getUserType().equals(UserType.ADMIN)
				|| user.getUserType().equals(UserType.RESELLER) || user.getUserType().equals(UserType.ACQUIRER)
				|| user.getUserType().equals(UserType.SUPERADMIN)||user.getUserType().equals(UserType.SUBADMIN) ||user.getUserType().equals(UserType.AGENT)||  
				user.getUserType().equals(UserType.SMA) ||  user.getUserType().equals(UserType.MA) || user.getUserType().equals(UserType.Agent)) {
			logger.info("1111111111111111111111111111111");

			tempMap = Currency.getAllCurrency();
			logger.info("1111111111111111111111111111111");

			
			logger.info("currencyKeycurrencyKeycurrencyKey"+currencyKey);
			// set currencies
			String strKey = tempMap.get(CrmFieldConstants.INR.getValue());
			if (StringUtils.isBlank(currencyKey)) {
				if (!StringUtils.isBlank(strKey)) {
					tempMap.remove(CrmFieldConstants.INR.getValue());
					currencyMap.put(CrmFieldConstants.INR.getValue(), strKey);
					for (Entry<String, String> entry : tempMap.entrySet()) {
						try {
							currencyMap.put(entry.getKey(), entry.getValue());
						} catch (ClassCastException classCastException) {
							logger.error("Exception", classCastException);
						}
					}
					return currencyMap;
				}
			}
		}else if(user.getUserType().equals(UserType.SUBUSER) || user.getUserType().equals(UserType.SUBACQUIRER)){
		//	User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			String parentPayId = user.getParentPayId();
			User parentUser = new UserDao().findPayId(parentPayId);
			tempMap = Currency.getSupportedCurreny(parentUser);
			if (StringUtils.isBlank(currencyKey)) {
				return tempMap;
			}
		} 
		else {
			
			logger.info("currencyKeycurrencyKeycurrencyKey"+user.toString());

			tempMap = Currency.getSupportedCurreny(user);
			logger.info("currencyKeycurrencyKeycurrencyKey"+tempMap);

			if (StringUtils.isBlank(currencyKey)) {
				return tempMap;
			}
		}
		tempMap.remove(currencyKey);
		currencyMap.put(currencyKey, Currency.getAlphabaticCode(currencyKey));
		for (Entry<String, String> entry : tempMap.entrySet()) {
			try {
				currencyMap.put(entry.getKey(), entry.getValue());
			} catch (ClassCastException classCastException) {
				logger.error("Exception", classCastException);
			}
		}

		return currencyMap;
	}
}
