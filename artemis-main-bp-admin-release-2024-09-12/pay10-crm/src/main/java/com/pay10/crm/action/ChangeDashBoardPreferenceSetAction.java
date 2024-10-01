package com.pay10.crm.action;

import java.util.Date;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.DashboardPreferenceSet;
import com.pay10.commons.user.DashboardPreferenceSetDao;
import com.pay10.commons.user.InvoiceNumberCounter;
import com.pay10.commons.user.InvoiceNumberCounterDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.TransactionManager;
import com.pay10.crm.actionBeans.ChargingDetailsMaintainer;

/**
 * @author Rajendra 
 *
 */

public class ChangeDashBoardPreferenceSetAction  extends AbstractSecureAction {
		
	@Autowired
	private CrmValidator validator;
	
	private static Logger logger = LoggerFactory.getLogger(ChangeDashBoardPreferenceSetAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;
	
	private String emailId;
	private String preferenceSetConstant;
	
	@Autowired
	private static DashboardPreferenceSetDao dashboardPreferenceSetDao;
	
	private static DashboardPreferenceSetDao dpsDao = new DashboardPreferenceSetDao();
	
	@Override
	public String execute() {
		try {
			
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN) || sessionUser.getUserType().equals(UserType.ADMIN)
					 || sessionUser.getUserType().equals(UserType.RESELLER)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.ASSOCIATE)
					|| sessionUser.getUserType().equals(UserType.MERCHANT)) {
				
				String userEmailId = sessionUser.getEmailId().toString();								
				creatingDashboardPreferenceSet(userEmailId,preferenceSetConstant);
			
			}
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}


	synchronized public static void creatingDashboardPreferenceSet(String emailId,String preferenceSetConst) {
		Date currentDate = new Date();
		DashboardPreferenceSet dps=new DashboardPreferenceSet();
		dps = dpsDao.getDashboardPreferenceSetByEmailId(emailId);
		if(dps == null) {
			DashboardPreferenceSet dps2=new DashboardPreferenceSet();
			dps2.setId(Long.parseLong(TransactionManager.getNewTransactionId()));
			dps2.setEmailId(emailId);
			dps2.setPreferenceSetConstant(preferenceSetConst);
			dps2.setCreatedDate(currentDate);
			dpsDao.create(dps2);
		}else {
			Session session = null;
			Long id = dps.getId();
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			session.load(dps, dps.getId());
			try {
				DashboardPreferenceSet dps3 = session.get(DashboardPreferenceSet.class, id);
				dps3.setEmailId(emailId);
				dps3.setPreferenceSetConstant(preferenceSetConst);
				dps3.setCreatedDate(currentDate);
				session.update(dps3);
				tx.commit();
			} catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}
		}

	}
	

	@Override
	public void validate() {
		
		// Present we are taking the email Id from Session User object		
		/*
		 * if(validator.validateBlankField(getEmailId())){ } else
		 * if(!validator.validateField(CrmFieldType.EMAILID, getEmailId())){
		 * addFieldError(CrmFieldType.EMAILID.getName(),
		 * ErrorType.INVALID_FIELD.getResponseMessage()); }
		 */

	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	public String getPreferenceSetConstant() {
		return preferenceSetConstant;
	}

	public void setPreferenceSetConstant(String preferenceSetConstant) {
		this.preferenceSetConstant = preferenceSetConstant;
	}
}
