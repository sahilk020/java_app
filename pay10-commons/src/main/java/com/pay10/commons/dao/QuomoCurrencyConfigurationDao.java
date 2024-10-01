package com.pay10.commons.dao;

import com.google.gson.Gson;
import com.pay10.commons.user.QuomoCurrencyConfiguration;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.PaymentType;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class QuomoCurrencyConfigurationDao extends HibernateAbstractDao{

    private static Logger logger = LoggerFactory.getLogger(QuomoCurrencyConfigurationDao.class.getName());


        public List<QuomoCurrencyConfiguration> findByAcquirerCurrencyPaymentType (String acquirer, String
        currency, String paymentType){
            try (Session session = HibernateSessionProvider.getSession()) {
                List<QuomoCurrencyConfiguration> mopList = new ArrayList<>();
                if (currency.equalsIgnoreCase("ALL") && paymentType.equalsIgnoreCase("ALL")) {

                    Query preQuery1 = session.createQuery("FROM QuomoCurrencyConfiguration WHERE acquirer=:acquirer AND status='Active'").setParameter("acquirer", acquirer);
                    mopList = preQuery1.getResultList();

                } else {
                    Query preQuery2 = session.createQuery("FROM QuomoCurrencyConfiguration WHERE acquirer=:acquirer AND currency=:currency AND paymentType=:paymentType AND status='Active'").setParameter("acquirer", acquirer).setParameter("currency", currency).setParameter("paymentType", paymentType);
                    mopList = preQuery2.getResultList();
                }
                logger.info("GET QuomoCurrencyConfiguration :::" + mopList);
                return mopList;
            }
        }

        public String editByIdBankMopTypeBankName(String id, String bankMopType, String bankName) {
            try (Session session = HibernateSessionProvider.getSession()) {



                return "Success";
            }
        }

        public String saveNewMopConfig(String acquirer,String currency, String currencyCode, String paymentType, String mopType, String bankName, String bankId, String emailId){
            Date date = new Date();
            try (Session session = HibernateSessionProvider.getSession()) {

                NativeQuery query = session.createNativeQuery("INSERT INTO QuomoCurrencyConfiguration VALUES(id,':acquirer',':currency',':currencyCode',':bankId','55',':paymentType',':mopType',':bankName','1','100000',':emailId',':date','ACTIVE');")
                                            .setParameter("acquirer","TESTING").setParameter("currency","INR")
                                            .setParameter("currencyCode","999").setParameter("paymentType","NB")
                                            .setParameter("emailId", "abc@xyz.com").setParameter("mopType","SBI")
                                            .setParameter("bankName",bankName).setParameter("bankId",bankId)
                                            .setParameter("date",date);
                logger.info("");
                return "Success";
            }
        }

    public List<String> getAcquirerList() {
        List<String> acquirerList = new ArrayList<>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            acquirerList = session.createQuery("SELECT distinct acquirer FROM QuomoCurrencyConfiguration").getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        logger.info("Acquirer List :"+acquirerList);
        return acquirerList;
    }


    public List<String> getCurrencyListByAcquirer(String acquirer) {
        logger.info("Acquirer  :"+acquirer);
        List<String> currencyList = new ArrayList<>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            currencyList = session.createQuery("SELECT distinct currency FROM QuomoCurrencyConfiguration where acquirer=:acquirer")
                    .setParameter("acquirer", acquirer).getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        logger.info("Currency List :"+currencyList);
        return currencyList;
    }

}
