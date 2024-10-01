package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.entity.BSESMonthlyInvoiceReportEntity;

@Component
public class BSESMonthlyInvoiceReportDAO extends HibernateAbstractDao {
	private static final Logger logger = LoggerFactory.getLogger(BSESMonthlyInvoiceReportEntity.class.getName());

	@SuppressWarnings("rawtypes")
	public boolean getBSESMonthlyInvoiceReportEntity(String payid) {
		try (Session session = HibernateSessionProvider.getSession();) {
			@SuppressWarnings("deprecation")
			Criteria criteria = session.createCriteria(BSESMonthlyInvoiceReportEntity.class);
			criteria.add(Restrictions.eq("payId", Long.valueOf(payid)));
			List list = criteria.list();
			if (list.size() > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	public List<String> getAllDistinctPayIds() {
		try (Session session = HibernateSessionProvider.getSession()) {
			String hql = "SELECT DISTINCT payId FROM BSESMonthlyInvoiceReportEntity";
			Query query = session.createQuery(hql);
			List<String>pay=new ArrayList<>();
			List  list=query.list();
			for (Object object : list) {
				pay.add(String.valueOf(object));
			}
			
			return pay;
		} catch (Exception e) {
			logger.error("Error retrieving distinct payIds:", e);
			return Collections.emptyList();
		}
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	public BSESMonthlyInvoiceReportEntity findByPayIdAndPaymentTypeAndAmount(String payId, String amount,
			String paymentType) {
		try (Session session = HibernateSessionProvider.getSession()) {
			String hql = "FROM BSESMonthlyInvoiceReportEntity WHERE paymentType ='" + paymentType + "' AND  payId ='"
					+ payId + "' AND '" + amount + "' BETWEEN minAmount AND maxAmount ";
			Query query = session.createQuery(hql);
			List list = query.list();
			if (list.size() > 0) {
				return (BSESMonthlyInvoiceReportEntity) list.get(0);
			} else {
				return null;
			}

		} catch (Exception e) {

			logger.error("Error finding data:", e);
			e.printStackTrace();
			return null;
		}

	}

}