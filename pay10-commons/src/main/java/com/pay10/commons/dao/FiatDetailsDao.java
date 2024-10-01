package com.pay10.commons.dao;

import com.pay10.commons.dto.FiatDetailsDTO;
import com.pay10.commons.entity.FiatDetails;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FiatDetailsDao extends HibernateAbstractDao{

    Logger logger= LoggerFactory.getLogger(FiatDetails.class.getName());

    public void saveOrUpdate(FiatDetails fiatDetails){
        super.saveOrUpdate(fiatDetails);
    }
    public List<FiatDetailsDTO> getFiatList(String payId,String currency){
        try(Session session=HibernateSessionProvider.getSession()){
            return session.createQuery("FROM FiatDetails where payId=:payId and currency=:currency")
                    .setParameter("payId",payId)
                    .setParameter("currency",currency).getResultList();
         }
    }
    public void delete(FiatDetails fiatDetails){
        super.delete(fiatDetails);
    }

    public String checkCurrencyAccountMapping(String payId,List<String> currencyList){

        try(Session session=HibernateSessionProvider.getSession()){
            for(String currency :currencyList) {
                Query query = session.createQuery("FROM FiatDetails where payId=:payId and currency=:currency")
                        .setParameter("payId", payId)
                        .setParameter("currency", currency);
                int count = query.getResultList().size();
                logger.info("Fiat Check Currency mapping count: " + count);
                if(count==0){
                    return currency;
                }
            }
            return "SUCCESS";
        }
    }
}
