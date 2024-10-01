package com.pay10.notification.txnpush.pushtxnemailbuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.DataAccessObject;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.CompanyProfileDao;

@Component("notificationDao")
public class NotificationDao extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(NotificationDao.class.getName());
	
	public NotificationDao() {
		super();
	}
	
	private static final String getNotificationWithEmailIdQuery = "from CompanyProfile U where U.emailId = :emailId";
	private static final String getNotificationTableWithEmailId = "select new map (emailId as emailId, accHolderName as accHolderName, "
			+ "accountNo as accountNo, companyGstNo as companyGstNo,"
			+ "address as address, bankName as bankName, branchName as branchName, cin as cin, comments as comments, companyName as companyName, "
			+ "mobile as mobile, telephoneNo as telephoneNo, fax as fax, address as address,"
			+ "city as city, state as state, country as country, postalCode as postalCode, ifscCode as ifscCode, currency as currency, panCard as panCard, "
			+ "panName as panName)"
			+ "from CompanyProfile U where U.emailId = :emailId1";

	private Connection getConnection() throws SQLException {
		return DataAccessObject.getBasicConnection();
	}
	public void create(NotificationDetail nd) throws DataAccessLayerException {
		super.save(nd);
	}
	public void delete(NotificationDetail nd) throws DataAccessLayerException {
		super.delete(nd);
	}

	public NotificationDetail find(Long tenantId) throws DataAccessLayerException {
		return (NotificationDetail) super.find(NotificationDetail.class, tenantId);
	}

	public NotificationDetail find(String name) throws DataAccessLayerException {
		return (NotificationDetail) super.find(NotificationDetail.class, name);
	}
	
	@SuppressWarnings("rawtypes")
	public List findAll() throws DataAccessLayerException {
		return super.findAll(NotificationDetail.class);
	}

	public void update(NotificationDetail nd) throws DataAccessLayerException {
		super.saveOrUpdate(nd);
	}
	
}
