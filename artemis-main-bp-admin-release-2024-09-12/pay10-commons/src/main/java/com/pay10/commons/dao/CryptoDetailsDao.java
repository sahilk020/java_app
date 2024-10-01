package com.pay10.commons.dao;


import com.pay10.commons.dto.CryptoDetailsDTO;
import com.pay10.commons.entity.CryptoDetails;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.util.List;

@Component
public class CryptoDetailsDao  extends  HibernateAbstractDao{

    private static Logger logger = LoggerFactory.getLogger(CryptoDetailsDao.class.getName());

    public List<CryptoDetails> getCryptoDetails(String payId, String currency) {
        try (Session session = HibernateSessionProvider.getSession()) {
            Query query = session.createQuery("FROM CryptoDetails where payId=:payId and currency=:currency")
                    .setParameter("payId",payId)
                    .setParameter("currency",currency);

            List<CryptoDetails> cryptoDetails =  query.getResultList();
            logger.info("List of Crypto Details: " + cryptoDetails);
            return cryptoDetails;
        }
    }
    public String setCryptoDetails(CryptoDetails cryptoDetails)
    {

        super.saveOrUpdate(cryptoDetails);
        return "Success";
    }

    public String  deleteCryptoDetails(long id,String currency){
        CryptoDetails cryptoDetails=new CryptoDetails();
        cryptoDetails.setCurrency(currency);
        cryptoDetails.setId(id);
        super.delete(cryptoDetails);
        return "Deleted Successfully";

    }
    public String checkBankMappingStatus(String payId,List<String> currencyList){
        try(Session session=HibernateSessionProvider.getSession()){
            for(String currency :currencyList){
            Query query= session.createQuery("FROM CryptoDetails where payId=:payId and currency=:currency")
                    .setParameter("payId",payId)
                    .setParameter("currency",currency);
            int count=query.getResultList().size();
            logger.info("Crypto Currency Mapping count: "+count);
            if(count==0){
                return currency;
            }
            }
                 return "SUCCESS";

        }
    }
}