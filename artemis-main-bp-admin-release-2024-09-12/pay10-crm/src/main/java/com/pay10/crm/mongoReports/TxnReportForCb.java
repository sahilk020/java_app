package com.pay10.crm.mongoReports;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.repository.DMSEntity;

@Component
public class TxnReportForCb {
	

	private static Logger logger = LoggerFactory.getLogger(TxnReportForCb.class.getName());

	public int chargeBackReportCount(String merchant, String pgRefNum, String cbCaseID, String dateTo,
			String dateFrom) {
		try {
			StringBuffer buffer=new StringBuffer();
			
			buffer.append("SELECT COUNT(*) FROM DMSEntity WHERE ");
			if(StringUtils.isNotBlank(merchant)) {
				buffer.append("merchantPayId='"+merchant+"' AND ");
				
			}
			if(StringUtils.isNotBlank(pgRefNum)) {
				buffer.append("pgRefNo='"+pgRefNum+"' AND ");
			}
			
			if(StringUtils.isNotBlank(cbCaseID)) {
				buffer.append("cbCaseId='"+cbCaseID+"' AND ");
			}
			
			if(StringUtils.isNotBlank(dateTo) && StringUtils.isNotBlank(dateFrom)) {
				buffer.append("cbIntimationDate BETWEEN '"+dateFrom+"' AND '"+dateTo+"'");
			}
			
			logger.info("Query for chargeBackReportCount : "+buffer.toString());
			
			Session session = HibernateSessionProvider.getSession();
			Query query=session.createQuery(buffer.toString());
		
			return Integer.parseInt(String.valueOf(query.getResultList().get(0)));
			
		}catch (Exception e) {
			logger.error("Exception Occur in chargeBackReportCount() ",e);
			return 0;
		}
		
	}

	public List<DMSEntity> chargeBackReportReport(String merchant, String pgRefNum, String cbCaseID, String dateTo,
			String dateFrom, int start, int length) {
		try {
			StringBuffer buffer=new StringBuffer();
			
			buffer.append("FROM DMSEntity WHERE ");
			if(StringUtils.isNotBlank(merchant)) {
				buffer.append("merchantPayId='"+merchant+"' AND ");
				
			}
			if(StringUtils.isNotBlank(pgRefNum)) {
				buffer.append("pgRefNo='"+pgRefNum+"' AND ");
			}
			
			if(StringUtils.isNotBlank(cbCaseID)) {
				buffer.append("cbCaseId='"+cbCaseID+"' AND ");
			}
			
			if(StringUtils.isNotBlank(dateTo) && StringUtils.isNotBlank(dateFrom)) {
				buffer.append("cbIntimationDate BETWEEN '"+dateFrom+"' AND '"+dateTo+"'");
			}
			
			logger.info("Query for chargeBackReportCount : "+buffer.toString());
			
			Session session = HibernateSessionProvider.getSession();
			Query query=session.createQuery(buffer.toString());
			if(start!= -1 && length!= -1) {
			query.setFirstResult(start);
			query.setMaxResults(length);
			}
			return (List<DMSEntity>) query.getResultList().stream().sorted(Comparator.comparing(DMSEntity::getDateWithTimeStamp).reversed()).collect(Collectors.toList());	
		}catch (Exception e) {
			logger.error("Exception Occur in chargeBackReportCount() ",e);
			return new ArrayList<DMSEntity>();
		}
		
	}

	
	
}
