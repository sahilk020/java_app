package com.pay10.commons.repository;

import java.util.List;
import java.util.Objects;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.entity.ChargebackReasonEntity;
import com.pay10.commons.exception.DataAccessLayerException;

@Repository
public class ChargebackReasonRepository extends HibernateAbstractDao {
    private static final Logger logger = LoggerFactory.getLogger(ChargebackReasonRepository.class.getName());

    public void save(ChargebackReasonEntity chargebackReasonEntity) throws DataAccessLayerException {
        super.save(chargebackReasonEntity);
    }

    public List<ChargebackReasonEntity> getAllChargebackReasons() {
        List<ChargebackReasonEntity> results = null;
        Session session = HibernateSessionProvider.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ChargebackReasonEntity> cr = cb.createQuery(ChargebackReasonEntity.class);
        Root<ChargebackReasonEntity> root = cr.from(ChargebackReasonEntity.class);

        // Create a predicate to filter by deleted = false
        Predicate deletedPredicate = cb.equal(root.get("deleted"), false);
        cr.where(deletedPredicate);

        cr.select(root);

        Query<ChargebackReasonEntity> query = session.createQuery(cr);
        results = query.getResultList();

        return results;
    }

    public boolean deleteChargebackReasons(long id) {
        logger.info("@@@@@ call deleteChargebackReasons By id : {} ", id);
        Transaction trx = null;
        boolean deleted = false;
        try {
            Session session = HibernateSessionProvider.getSession();
            trx = session.beginTransaction();
            ChargebackReasonEntity chargebackReason = session.get(ChargebackReasonEntity.class, id);
            logger.info("@@@@@ call deleteChargebackReasons chargebackReason  : {} ", chargebackReason);

            if (!Objects.isNull(chargebackReason)) {
                chargebackReason.setDeleted(true);
                session.update(chargebackReason);
            }

            trx.commit();

            deleted = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("@@@@@ Exception can be occur when call deleteChargebackReasons By id : {} ", id);
            if (trx != null) {
                trx.rollback();
            }
        }

        return deleted;
    }

    public ChargebackReasonEntity getcbReasonDescriptionFromcbReasonCode(String cbReasonCode) {
        Session session = HibernateSessionProvider.getSession();
//		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//		CriteriaQuery<ChargebackReasonEntity> criteriaQuery = criteriaBuilder.createQuery(ChargebackReasonEntity.class);
//		Root<ChargebackReasonEntity> studentRoot = criteriaQuery.from(ChargebackReasonEntity.class);
//		criteriaQuery.select(studentRoot);
//		criteriaQuery.where(criteriaBuilder.equal(studentRoot.get("cbReasonCode"), cbReasonCode));
//		TypedQuery<ChargebackReasonEntity> typedQuery = session.createQuery(criteriaQuery);
//		List<ChargebackReasonEntity> chargebackReasonEntity = typedQuery.getResultList();
//		if (chargebackReasonEntity.size() > 0) {
//			return chargebackReasonEntity.get(0);
//		} else {
//			return null;
//		}
        String query = "FROM ChargebackReasonEntity WHERE cbReasonCode='" + cbReasonCode + "'";
        logger.info("Get getcbReasonDescriptionFromcbReasonCode Query: " + query);
        Query query2 = session.createQuery(query);
        List list = query2.getResultList();
        if (list.size() > 0) {
            return (ChargebackReasonEntity) list.get(0);
        } else {
            return null;
        }

    }
}
