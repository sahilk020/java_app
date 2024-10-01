package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;

import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
@Embeddable
public class Currency {
	private String code;
	private int places;
	private static final String alphabaticFileName= "alphabatic-currencycode.properties";
	private static Map<String, Integer> currencies = loadCurrencies();
	private static final String currencyPrefix= "CURRENCY_";
	private static Logger logger = LoggerFactory.getLogger(Currency.class.getName());

	static Map<String,String> allCurrencyMap = null;

	public Currency() {

	}

	public Currency(String code, int places) {
		this.code = code;
		this.places = places;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getPlaces() {
		return places;
	}

	public void setPlaces(int places) {
		this.places = places;
	}

	public static void validateCurrency(String currencyCode) throws SystemException {

		if (null == currencyCode) {
			return;
		}

		if (!currencies.containsKey(currencyCode)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid "
					+ FieldType.CURRENCY_CODE.getName());
		}
	}

	public static Map<String, Integer> loadCurrencies() {
		Map<String, String> allCurrencies = (new PropertiesManager())
				.getAllProperties(PropertiesManager.getCurrencyfile());
		Map<String, Integer> currenciesMap = new HashMap<String, Integer>();

		for (String currencyCode : allCurrencies.keySet()) {
			int numberOfPlaces = Integer.parseInt(allCurrencies
					.get(currencyCode));
			currenciesMap.put(currencyCode, numberOfPlaces);
		}

		return currenciesMap;
	}// loadCurrencies()

	public static int getNumberOfPlaces(String currencyCode) {
		//System.out.println("deep : "+currencyCode);
		return Integer.parseInt(CurrencyNumber.getDecimalfromCode(currencyCode));
	}
	public static void main(String[] args) {
		System.out.println(Currency.getNumberOfPlaces("458"));
	}
	public static Currency getDefaultCurrency() {
		String currCode = ConfigurationConstants.DEFAULT_CURRENCY.getValue();
		return new Currency(currCode, getNumberOfPlaces(currCode));
	}
	
	public static String getAlphabaticCode(String numericCurrencyCode) {
		return PropertiesManager.propertiesMap.get(currencyPrefix+numericCurrencyCode);
	}

	public static String getNumericCode(String alphabaticCurrencyCode) {
		return PropertiesManager.propertiesMap.get(alphabaticCurrencyCode);
	}
	 
//	public static Map<String,String> getSupportedCurreny(User user){
//		Map<String,String> currencyMap = new HashMap<String,String>();
//		
//		for(Account account: user.getAccounts()){
//			Set<AccountCurrency> AccountCurrencySet = account.getAccountCurrencySet();
//			for(AccountCurrency accountCurrency:AccountCurrencySet){
//				String currencyCode = accountCurrency.getCurrencyCode();
//				currencyMap.put(currencyCode, Currency.getAlphabaticCode(currencyCode));
//			}
//		}
//		return currencyMap;
//	}
	
	public static Map<String,String> getSupportedCurreny(User user){
	Map<String,String> currencyMap = new HashMap<String,String>();
	MultCurrencyCodeDao CurrencyCodeDao =new MultCurrencyCodeDao(); 
	for(Account account: user.getAccounts()){
		Set<AccountCurrency> AccountCurrencySet = account.getAccountCurrencySet();
		for(AccountCurrency accountCurrency:AccountCurrencySet){
			String currencyCode = accountCurrency.getCurrencyCode();
			currencyMap.put(currencyCode, CurrencyCodeDao.getCurrencyNamebyCode(currencyCode)
					);
		}
	}
	return currencyMap;
}
	
	
	public static Map<String,String> getAllCurrency(){
		PropertiesManager propertiesManager= new PropertiesManager();
//		allCurrencyMap = propertiesManager.getAllProperties(alphabaticFileName);
		//logger.info("1111111111111111111111111111111");
		MultCurrencyCodeDao CurrencyCodeDao =new MultCurrencyCodeDao(); 
		List<MultCurrencyCode> currencyList = new ArrayList<MultCurrencyCode>();
		//logger.info("1111111111111111111111111111111");

		currencyList= CurrencyCodeDao.getCurrencyCode();
		Map<String, String> currencyMap = new HashMap<String, String>();
		//logger.info("1111111111111111111111111111111");


		for (MultCurrencyCode currency : currencyList) {
			currencyMap.put(currency.getCode(),currency.getName());
		}
		//logger.info("1111111111111111111111111111111"+currencyMap);

		setAllCurrencyMap(currencyMap);
		
		return allCurrencyMap;
	}

	public static Map<String, String> getAllCurrencyMap() {
		return allCurrencyMap;
	}

	public static void setAllCurrencyMap(Map<String, String> allCurrencyMap) {
		Currency.allCurrencyMap = allCurrencyMap;
	}

	

	
	
//	public static Map<String,String> getAllCurrency(){
//		PropertiesManager propertiesManager= new PropertiesManager();
//		Map<String,String> allCurrencyMap;
//		allCurrencyMap = propertiesManager.getAllProperties(alphabaticFileName);
//		return allCurrencyMap;
//	}
	
	
}
