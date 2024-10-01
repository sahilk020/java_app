package com.pay10.commons.dao;

import com.google.gson.Gson;
import com.pay10.commons.entity.ResetMerchantKey;
import com.pay10.commons.user.MerchantKeySalt;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component("resetMerchantKeyDao")
public class ResetMerchantKeyDao extends HibernateAbstractDao{
    Logger logger= LoggerFactory.getLogger(ResetMerchantKeyDao.class.getName());
    public ResetMerchantKey find(String payId){
          return (ResetMerchantKey) super.find(ResetMerchantKey.class,payId);
    }

    public List<ResetMerchantKey> findListById(String payId){
        try(Session session= HibernateSessionProvider.getSession()){
            Query<ResetMerchantKey> creatQuery= session.createQuery("FROM ResetMerchantKey where payId=:payId order by startDate DESC", ResetMerchantKey.class)
                    .setParameter("payId",payId);
            logger.info("List for Encryption Key::"+new Gson().toJson(creatQuery.getResultList()));
            return creatQuery.getResultList();
        }
    }

    public String saveOrUpdate(ResetMerchantKey resetMerchantKey){
        super.saveOrUpdate(resetMerchantKey);
//        Session session=HibernateSessionProvider.getSession();
//         Transaction tx=session.beginTransaction();
//        try{
//            NativeQuery query=session.createNativeQuery("INSERT INTO reset_merchant_key (payId,encryptionKey,salt,keySalt,startDate,status,createdBy,createdOn) VALUES(:payId,:encryptionKey,:salt,:keySalt,:startDate,:status,:createdBy,:createdOn)")
//                    .setParameter("payId",resetMerchantKey.getPayId())
//                    .setParameter("encryptionKey",resetMerchantKey.getEncryptionKey())
//                    .setParameter("salt",resetMerchantKey.getSalt())
//                    .setParameter("keySalt",resetMerchantKey.getKeySalt())
//                    .setParameter("startDate",resetMerchantKey.getStartDate())
//                    .setParameter("status",resetMerchantKey.getStatus())
//                    .setParameter("createdBy",resetMerchantKey.getCreatedBy())
//                    .setParameter("createdOn",resetMerchantKey.getCreatedOn());
//          int count=query.executeUpdate();
//          if(count>0){
//              logger.info(count+" Row inserted Successfully in Reset Merchant Key");
//          }else{
//              logger.info(count+" Row inserted Successfully in Reset Merchant Key");
//          }
//            tx.commit();
//        }
//        catch (HibernateException e){
//            logger.info("Reset Merchant Key Exception Key Insert Exception: "+e);
//        }
//        finally {
//            autoClose(session);
//        }
        return "SUCCESS";
    }


    public List<ResetMerchantKey> getUpdatingList(Date startTime){
        List<ResetMerchantKey>updatingList=new ArrayList<>();
        try(Session session=HibernateSessionProvider.getSession()) {
            Query getListQuery = session.createQuery("FROM ResetMerchantKey where startDate<:startDate and status='Pending' order by startDate ASC")
                                      .setParameter("startDate", startTime);

            updatingList=getListQuery.getResultList();
            if(updatingList.isEmpty()){
                logger.info("Nothing to Update :::"+new Gson().toJson(updatingList));
            }
            else{
                logger.info("List to Update :::"+new Gson().toJson(updatingList));
            }
        }
        return updatingList;
      }
      public ResetMerchantKey merchantEncryptionKeyExists(ResetMerchantKey resetMerchant){
            ResetMerchantKey resetMerchantKey=null;
            try(Session session=HibernateSessionProvider.getSession()){
                resetMerchantKey=(ResetMerchantKey) session.createQuery("FROM ResetMerchantKey WHERE payId=:payId and encryptionKey=:encyKey and salt=:salt and keySalt=:keySalt")
                        .setParameter("payId",resetMerchant.getPayId())
                        .setParameter("encyKey",resetMerchant.getEncryptionKey())
                        .setParameter("salt",resetMerchant.getSalt())
                        .setParameter("keySalt",resetMerchant.getKeySalt()).getResultList();

                if(resetMerchantKey==null){
                    logger.info("ResetMerchant Done Not Exist:");
                }
                else{
                    logger.info("ResetMerchant Do Exist:");
                    logger.info("List if Merchant Key Exists {}",resetMerchantKey);
                }
                return resetMerchantKey;
            }
            catch (HibernateException h){
                logger.info("Reset Merchant Key Exists Exception: "+h);
            }
        return  resetMerchantKey;
      }


      public void updateStatus(String payId, String oldStatus, String newStatus, String updatedBy){
          Session session=HibernateSessionProvider.getSession();
          Transaction tx= session.beginTransaction();
          Date endDate = null;
          if(newStatus.equalsIgnoreCase("Inactive")){
              endDate = new Date();
          }
          try{
              NativeQuery nativeQuery= session.createNativeQuery("UPDATE reset_merchant_key SET updatedBy=:updatedBy, updatedOn=:updatedOn, status=:newStatus, endDate=:endDate where payId=:payId and status=:oldStatus")
                      .setParameter("updatedBy",updatedBy)
                      .setParameter("updatedOn",new Date())
                      .setParameter("oldStatus",oldStatus)
                      .setParameter("newStatus",newStatus)
                      .setParameter("endDate", endDate)
                      .setParameter("payId",payId);
              int count=nativeQuery.executeUpdate();
              if(count>0){
                  logger.info("Merchant Key Salt updated Successfully for: "+payId+" With Count"+count+" With Encryption Key");
              }
              else{
                  logger.info("Merchant Key Salt updated Successfully for: "+payId+" With Count"+count+" With Encryption Key");
              }
              tx.commit();
          }catch (HibernateException h){
              logger.error("Update Reset Merchant Key Exception: ", h);
          }
          finally {
              autoClose(session);
          }
      }


    public void updateRestMerchantEncryptionKeyStatus(ResetMerchantKey resetMerchantKey, String currentStatus){
        Session session=HibernateSessionProvider.getSession();
        Transaction tx= session.beginTransaction();
        try{
            NativeQuery nativeQuery= session.createNativeQuery("Update reset_merchant_key set updatedBy=:updatedBy, updatedOn=:updatedOn, status=:status where payId=:payId and encryptionKey=:encyKey and salt=:salt and keySalt=:keySalt and status=:currentStatus")
                    .setParameter("encyKey",resetMerchantKey.getEncryptionKey())
                    .setParameter("salt",resetMerchantKey.getSalt())
                    .setParameter("keySalt",resetMerchantKey.getKeySalt())
                    .setParameter("updatedBy",resetMerchantKey.getUpdatedBy())
                    .setParameter("updatedOn",resetMerchantKey.getUpdatedOn())
                    .setParameter("currentStatus",resetMerchantKey.getStatus())
                    .setParameter("status",currentStatus)
                    .setParameter("payId",resetMerchantKey.getPayId());
            int count=nativeQuery.executeUpdate();
            if(count>0)
            {
                logger.info("Merchant Key Salt updated Successfully for: "+resetMerchantKey.getPayId()+" With Count"+count+" With Encryption Key"+resetMerchantKey.getEncryptionKey());
            }
            else{
                logger.info("Merchant Key Salt updated Successfully for: "+resetMerchantKey.getPayId()+" With Count"+count+" With Encryption Key"+resetMerchantKey.getEncryptionKey());
            }
            tx.commit();
        }catch (HibernateException h){
            logger.info("Update Reset Merchant Key Exception: "+h);
        }
        finally {
            autoClose(session);
        }
    }


}