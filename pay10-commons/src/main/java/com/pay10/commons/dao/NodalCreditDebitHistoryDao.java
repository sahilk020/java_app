package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.NodalCreditDebitHistory;

@Service
public class NodalCreditDebitHistoryDao extends HibernateAbstractDao {

	public void create(NodalCreditDebitHistory NodalCreditDebitHistory) throws DataAccessLayerException {
		super.save(NodalCreditDebitHistory);
	}

	@SuppressWarnings("unchecked")
	public List<NodalCreditDebitHistory> findHistory(String payId, String acquirer, String paymentType,
			Date nodalDate) {
		List<NodalCreditDebitHistory> nodalCreditDebitHistoryList = new ArrayList<NodalCreditDebitHistory>();
		nodalCreditDebitHistoryList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			nodalCreditDebitHistoryList = session.createQuery(
					"from NodalCreditDebitHistory ST where ST.payId = :payId and ST.acquirer = :acquirer and ST.paymentMethod = :paymentType and ST.nodalDate = :nodalDate")
					.setParameter("payId", payId)
					.setParameter("acquirer", acquirer)
					.setParameter("paymentType", paymentType)
					.setParameter("nodalDate", nodalDate)
					.setCacheable(true).getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}

		if (nodalCreditDebitHistoryList.size() < 2) {
			return nodalCreditDebitHistoryList;
		}

		Comparator<NodalCreditDebitHistory> comp = (NodalCreditDebitHistory a, NodalCreditDebitHistory b) -> {

			if (a.getCreatedDate().compareTo(b.getCreatedDate()) > 0) {
				return -1;
			} else if (a.getCreatedDate().compareTo(b.getCreatedDate()) < 0) {
				return 1;
			} else {
				return 0;
			}
		};

		Collections.sort(nodalCreditDebitHistoryList, comp);
		return nodalCreditDebitHistoryList;
	}

}
