package com.pay10.commons.dao;

import com.google.gson.Gson;
import com.pay10.commons.entity.WhiteListedIp;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.*;

@Repository
public class WhiteListedIpDao extends HibernateAbstractDao{

    private static Logger logger = LoggerFactory.getLogger(WhiteListedIpDao.class.getName());

    //IP List for PAYOUT & SETTLEMENT
    public List<String> getWhiteListedIpForApi(String payId){
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<String> ipList = new ArrayList<>();

        try {
            logger.info("PayId :"+payId);
            if (!StringUtils.isBlank(payId)) {
                ipList = session.createNativeQuery("Select ip from WhiteListedIp where payId=:payId")
                        .setParameter("payId", payId)
                        .getResultList();
                tx.commit();
                logger.info("IP List :::"+ipList);
               return ipList;
            }
            return ipList;
        }
        catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return ipList;
    }

    //Getting whitelisted IP for PayId
    public Map<String,String> getWhiteListedIpListForUI(String payId){
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<Object[]> ipList = new ArrayList<>();
        Map<String,String> mapData = new HashMap<>();
        try {
            logger.info("PayId :"+payId);
            if (!StringUtils.isBlank(payId)) {
                ipList = session.createNativeQuery("Select id,ip from WhiteListedIp where payId= :payId")
                        .setParameter("payId", payId)
                        .getResultList();
                logger.info("IP List ::"+ new Gson().toJson(ipList));
                for (Object[] objects : ipList) {
                    mapData.put(String.valueOf(objects[0]),String.valueOf(objects[1]));
                }
                tx.commit();
                logger.info("IP Map ::"+mapData);
                return mapData;
            }
        }
        catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return mapData;
    }

    
    // added by manish
    public List<String> getWhiteListedIpListForUICheck(String payId){
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<String> ipList = new ArrayList<>();
        Map<String,String> mapData = new HashMap<>();
        try {
            logger.info("PayId :"+payId);
            if (!StringUtils.isBlank(payId)) {
                ipList = session.createNativeQuery("Select ip from WhiteListedIp where payId= :payId")
                        .setParameter("payId", payId)
                        .getResultList();
                logger.info("IP List ::"+ new Gson().toJson(ipList));
               
                tx.commit();
                logger.info("IP List ::"+ipList);
                return ipList;
            }
        }
        catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return ipList;
    }

    //Saving whitelisted IP for PayId
    public String saveWhiteListedIp(WhiteListedIp whiteListedIp){

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            String query = "INSERT INTO WhiteListedIp (payid, ip, createdBy, createdOn) " +
                    "VALUES ('"+whiteListedIp.getPayId()+"', '"+whiteListedIp.getIp()+"', '"
                    +whiteListedIp.getCreatedBy()+"', '"+whiteListedIp.getCreatedOn()+"')";

            NativeQuery nativeQuery = session.createNativeQuery(query);
            int c = nativeQuery.executeUpdate();
            logger.info(String.format("%d WhiteListedIp Saved", c));
            tx.commit();
            return "IP saved successfully";
        } catch (HibernateException e) {
            handleException(e,tx);
        } finally {
            autoClose(session);
        }
        return "IP saved successfully";

    }


    //Deleting whitelisted IP for PayId
    public String deleteWhiteListedIp(String id){
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        BigInteger bigId = BigInteger.valueOf(Long.parseLong(id));
        logger.info("ID :"+bigId);
        try {
            String query = "Delete from WhiteListedIp where id="+bigId;

            NativeQuery nativeQuery = session.createNativeQuery(query);
            int c = nativeQuery.executeUpdate();
            logger.info(String.format("%d WhiteListedIp delete", c));
            tx.commit();
            return "IP deleted successfully";
        } catch (HibernateException e) {
            handleException(e,tx);
        } finally {
            autoClose(session);
        }
        return "IP deleted successfully";
    }


    //Check IP from Whitelisted IP List
    public boolean checkIpWhitelisted(String payId, String ip){

        List<String> ipList = new ArrayList<>();
        ipList = getWhiteListedIpForApi(payId);
        return ipList.contains(ip);
    }

}
