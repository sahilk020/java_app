package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.ResellerPayout;

@Component("resellerDao")
public class ResellerDao extends HibernateAbstractDao {

    private final Logger logger = LoggerFactory.getLogger(ResellerDao.class);

    public void create(ResellerPayout resellerPayout) throws DataAccessLayerException {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
			Query query = session.createNativeQuery("DELETE FROM ResellerPayout WHERE resellerId=:resellerId AND fromDate=:fromDate AND toDate=:toDate AND currency=:currency AND status='PENDING'")
					.setParameter("resellerId", resellerPayout.getResellerId())
					.setParameter("fromDate", resellerPayout.getFromDate())
					.setParameter("toDate", resellerPayout.getToDate())
                    .setParameter("currency", resellerPayout.getCurrency());
			query.executeUpdate();
			tx.commit();
        } finally {
            autoClose(session);
        }
        super.save(resellerPayout);
    }

    public List<ResellerPayout> getResellerPayoutDetails(String userType, String resellerId, String currency,
                                                         String fromDate, String toDate) {
        Session session = HibernateSessionProvider.getSession();
        List<ResellerPayout> resellerData = new ArrayList<ResellerPayout>();
        List<ResellerPayout> setResellerData = new ArrayList<ResellerPayout>();
        try {
//			if (StringUtils.isBlank(resellerId)) {
//
//				return session
//						.createQuery("FROM ResellerPayout  where fromDate>= :fromDate and toDate<= :toDate",
//								ResellerPayout.class)
//						.setParameter("fromDate", fromDate).setParameter("toDate", toDate).setCacheable(true)
//						.getResultList();
//			} else {

            resellerData = session.createQuery(
                            "FROM ResellerPayout  where resellerId = :resellerId and currency=:currency and fromDate>= :fromDate and toDate<= :toDate")
                    .setParameter("resellerId", resellerId).setParameter("currency", currency).setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate).setCacheable(true).getResultList();

            for (ResellerPayout data : resellerData) {
                ResellerPayout payout = new ResellerPayout();
                if (userType.equalsIgnoreCase("SMA")) {

                    payout.setTotalCommission(data.getTotalSMACommission());
                }
                if (userType.equalsIgnoreCase("MA")) {
                    payout.setTotalCommission(data.getTotalMACommission());
                }
                if (userType.equals("Agent")) {
                    payout.setTotalCommission(data.getTotalAgentCommission());
                }
                payout.setPayoutId(data.getPayoutId());
                payout.setResellerName(data.getResellerName());
                payout.setTotalamount(data.getTotalamount());
                payout.setStatus(data.getStatus());
                payout.setTds(data.getTds());
                payout.setUtrNo(data.getUtrNo());
                payout.setSettlementDate(data.getSettlementDate());
                payout.setId(data.getId());
                payout.setCreationDate(data.getCreationDate());
                payout.setResellerId(data.getResellerId());
                setResellerData.add(payout);
            }
            // }
            return setResellerData;
        } finally {
            autoClose(session);
        }
    }

    public void updateResellerPayoutDetails(String resellerId, String batchNo, String utrNo, String settlementDate, String tds) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
//			session.createQuery(
//					"update ResellerPayout R set R.utrNo = :utrNo,settlementDate=:settlementDate,tds=:tds  where R.resellerId = :resellerId")
//					.setParameter("resellerId", resellerId).setParameter("utrNo", utrNo)
//					.setParameter("settlementDate", settlementDate).setParameter("tds", tds).executeUpdate();
            logger.info("resellerId : {}", resellerId);
            logger.info("utrNo : {}", utrNo);
            logger.info("settlementDate : {}", settlementDate);
            logger.info("tds : {}", tds);
            logger.info("payoutId : {}", batchNo);
            session.createQuery(
                            "update ResellerPayout R set R.utrNo = :utrNo,settlementDate=:settlementDate,tds=:tds,status=:status  where R.resellerId = :resellerId AND payoutId=:payoutId")
                    .setParameter("resellerId", resellerId)
                    .setParameter("utrNo", utrNo)
                    .setParameter("settlementDate", settlementDate)
                    .setParameter("tds", tds)
					.setParameter("payoutId", batchNo)
                    .setParameter("status", "Settled").executeUpdate();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            logger.error("Error", objectNotFound);
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            logger.error("Error", hibernateException);
            handleException(hibernateException, tx);
        }catch (Exception e){
            logger.error("Error", e);
        } finally {
            autoClose(session);
        }
    }
}
