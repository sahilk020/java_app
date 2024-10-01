package com.pay10.commons.dao;

import com.pay10.commons.entity.CurrencyCode;
import com.pay10.commons.user.MultCurrencyCode;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CurrencyCodeDao extends HibernateAbstractDao{
	
	private static Logger logger = LoggerFactory.getLogger(CurrencyCodeDao.class.getName());

    public List<CurrencyCode> findByChannel(String channel){
        String hql = "FROM CurrencyCode WHERE channel = '"+channel+"'";
        List<Object> currencyCodesRaw = findAllBy(hql);
        List<CurrencyCode> currencyCodes = new ArrayList<>();
        for(Object object : currencyCodesRaw){
            currencyCodes.add((CurrencyCode) object);
        }
        return currencyCodes;
    }
    public List<CurrencyCode> findByAll(){
    	 String hql = "FROM CurrencyCode";
         List<Object> currencyCodesRaw = findAllBy(hql);
         List<CurrencyCode> currencyCodes = new ArrayList<>();
         for(Object object : currencyCodesRaw){
             currencyCodes.add((CurrencyCode) object);
         }
    	
    	return currencyCodes;
       
    }
	
	public String getCurrencyNamebyCode(String Code) {
		Session session = HibernateSessionProvider.getSession();
		String multiCurrencyQuery="FROM MultCurrencyCode where code="+Code;
		String CurrencyQuery="FROM CurrencyCode where code="+Code;
		String name="";
		try {
				
		
			MultCurrencyCode multiCurrencyCode= (MultCurrencyCode) session.createQuery(multiCurrencyQuery)
						.setCacheable(true).uniqueResult();
			CurrencyCode currencyCode= (CurrencyCode) session.createQuery(CurrencyQuery)
					.setCacheable(true).uniqueResult();
			if(multiCurrencyCode==null) {
				logger.info("getCurrencyNamebyCode(), if condition invoked");
				name = currencyCode.getName();
			}
			else {
				logger.info("getCurrencyNamebyCode(), else condition invoked");
				name = multiCurrencyCode.getName();
			}
			logger.info("getCurrencyNamebyCode currency name...."+name);
			return name;
			
			} finally {
			autoClose(session);
		}
	}
    
}
