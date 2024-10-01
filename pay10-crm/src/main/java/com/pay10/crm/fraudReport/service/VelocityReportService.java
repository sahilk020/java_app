package com.pay10.crm.fraudReport.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.user.FraudTransactionDetails;
import com.pay10.commons.util.StatusType;

@Service
public class VelocityReportService extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(VelocityReportService.class.getName());

	public List<FraudTransactionDetails> velocityReportCriteriaBased(String merchant, String dateRange, String status,
			String ruleType) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {

			String startDate = null, endDate = null;
			if (dateRange != null) {
				String arr[] = getDateBetween(dateRange);
				startDate = arr[0];
				endDate = arr[1];
			}
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			Date startDateFormatted=dateFormat1.parse(startDate);
			startDateFormatted.setHours(0);
			startDateFormatted.setMinutes(0);
			startDateFormatted.setSeconds(0);
			Date endDateFormatted=dateFormat1.parse(endDate);
			endDateFormatted.setHours(23);
			endDateFormatted.setMinutes(59);
			endDateFormatted.setSeconds(59);
			Criteria cr = session.createCriteria(FraudTransactionDetails.class);
			cr.add(Restrictions.ge("date", startDateFormatted));
			cr.add(Restrictions.le("date", endDateFormatted));

			if (!status.equalsIgnoreCase("ALL")) {
				if(status.equalsIgnoreCase("Sucsess")) {
				}else {
					cr.add(Restrictions.in("status", StatusType.getInternalStatus(status)));
				}
			}
			if (!ruleType.equalsIgnoreCase("ALL")) {
				cr.add(Restrictions.eq("ruleType", ruleType));
			}
			if (!merchant.equalsIgnoreCase("ALL")) {
				cr.add(Restrictions.eq("merchantName", merchant));
			}
			return cr.list();
		} catch (Exception e) {
			logger.info("Exception Occur in class VelocityReportService in velocityReportCriteriaBased(): "
					+ e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			session.close();
		}

	}

	public String[] getDateBetween(String date_range) {
		// 07/06/2022 - 06/07/2022
		String[] date = new String[2];
		String splitdate[] = date_range.split(" ");
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			date[0] = dateFormat1.format(dateFormat.parse(splitdate[0]));
			date[1] = dateFormat1.format(dateFormat.parse(splitdate[2]));

		} catch (Exception e) {
			logger.info("Exception Occur in class VelocityReportService in getDateBetween(): " + e.getMessage());
			e.printStackTrace();
		}
		return date;
	}
}
