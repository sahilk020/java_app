package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;

@Component
public class MultCurrencyCodeDao extends HibernateAbstractDao {
    private static Logger logger = LoggerFactory.getLogger(MultCurrencyCodeDao.class.getName());


    public List<MultCurrencyCode> getCurrencyCode() {
        Session session = HibernateSessionProvider.getSession();
        String query = "FROM MultCurrencyCode";
        try {


            logger.info(query);
            return session.createQuery(
                            query,
                            MultCurrencyCode.class)
                    .setCacheable(true).getResultList();

        } finally {
            autoClose(session);
        }

    }

    public String getCurrencyNamebyCode(String code) {
        Session session = null;
        String currencyName = null;
        try {
            // Open session
            session = HibernateSessionProvider.getSession();
            // Create the query with a named parameter
            String query = "FROM MultCurrencyCode WHERE code = :code";
            MultCurrencyCode codecurr = (MultCurrencyCode) session.createQuery(query)
                    .setParameter("code", code)
                    .setCacheable(true)
                    .uniqueResult();
            logger.info("Executed query: " + query + " with code: " + code);
            if (codecurr != null) {
                currencyName = codecurr.getName();
            } else {
                logger.warn("No MultCurrencyCode found for code: " + code);
            }
        } catch (Exception exception) {
            logger.error("Exception occurred while fetching currency name", exception);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception e) {
                    logger.error("Exception occurred while closing session", e);
                }
            }
        }

        return currencyName;
    }


    public List<MultCurrencyCode> findAll() {

        return super.findAll(MultCurrencyCode.class);
    }

    public String getSymbolbyCode(String Code) {
        Session session = HibernateSessionProvider.getSession();
        String query = "FROM MultCurrencyCode where code=" + Code;
        try {


            //logger.info(query);
            MultCurrencyCode codecurr = (MultCurrencyCode) session.createQuery(query)
                    .setCacheable(true).uniqueResult();

            return codecurr.getSymbol();

        } finally {
            autoClose(session);
        }
    }

    public MultCurrencyCode findByCode(String code) {
        Session session = HibernateSessionProvider.getSession();
        try {

            return session.createQuery("FROM MultCurrencyCode where code=:code", MultCurrencyCode.class)
                    .setParameter("code", code)
                    .setCacheable(true)
                    .uniqueResult();

        } finally {
            autoClose(session);
        }
    }


    public String getCurrencyCodeByName(String Name) {
        Session session = HibernateSessionProvider.getSession();
        String query = "FROM MultCurrencyCode where name=" + Name;
        try {


            logger.info(query);
            MultCurrencyCode codecurr = (MultCurrencyCode) session.createQuery("FROM MultCurrencyCode where name=:name")
                    .setParameter("name", Name)
                    .setCacheable(true).uniqueResult();

            return codecurr.getCode();

        } finally {
            autoClose(session);
        }
    }

    public List<String> getMerchantCurrency(String payId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<String> list = new ArrayList<String>();
        try {
            Query query = session
                    .createSQLQuery("SELECT distinct currency FROM TdrSetting where  payId=:payId");
            query.setString("payId", payId);
            list = query.list();
            System.out.println("groupList " + list);

            tx.commit();
            logger.info("currency list in dao class" + list);
            return list;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return list;
    }


}
