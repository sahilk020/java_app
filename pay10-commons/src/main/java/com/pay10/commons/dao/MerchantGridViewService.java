package com.pay10.commons.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.MerchantDetails;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.UserStatusType;

@Service
public class MerchantGridViewService {
	
	@Autowired
	private UserDao userDao;
	
	private static Logger logger = LoggerFactory.getLogger(MerchantGridViewService.class.getName());
	private final static String query = "select payId, businessName, emailId, userStatus,Mobile,registrationDate,userType from User where (userType='MERCHANT') order by payId ";
	private final static String querySubUser = "select p.payId,p.emailId, p.userStatus,p.Mobile,p.registrationDate,p.userType,u.businessName from User p inner join User u ON (p.parentPayId = u.payId) where (p.userType='SUBUSER') order by p.payId ";
	private final static String querySubUserList = "select payId, emailId, userStatus,Mobile,registrationDate,userType, (select businessName from User  where (userType='MERCHANT' and payId =?)) As 'businessName' from User where (userType='SUBUSER' and parentPayId =?) order BY parentPayId ";
	//private final static String businesTypeQuery = "select payId, businessName, emailId, userStatus,Mobile,registrationDate,userType from User where (userType='MERCHANT' and industryCategory =? and status) order by payId ";
	private final static String businesTypeAdminQuery = "select payId, businessName, emailId, userStatus,Mobile,registrationDate,userType from User where (userType='MERCHANT' and industryCategory =? ) order by payId ";
	private final static String businesTypeSubAdminQuery = "select payId, businessName, emailId, userStatus,Mobile,registrationDate,userType from User where (userType='MERCHANT' and industryCategory =? and segment =? ) order by payId ";
	
	private final static String rsellerListQuery = "select payId, businessName, emailId, userStatus,Mobile,registrationDate,userType from User where (userType='Reseller') order by payId ";
	private final static String reselerQuery = "select payId,resellerId, businessName, emailId, userStatus,Mobile,registrationDate,userType from User where userType='MERCHANT' and resellerId=? ";

	public MerchantGridViewService() {
	}

	private Connection getConnection() throws SQLException {
		return DataAccessObject.getBasicConnection();
	}

	/*
	 * public List<MerchantDetails> getAllMerchantDetails(String userRoleType)
	 * throws SystemException {
	 * 
	 * return userDao.getMerchantDetails(userRoleType); }
	 */
	
	public List<MerchantDetails> getAllMerchantDetails(String userRoleType, String segmentName, long roleId) throws SystemException {
		logger.info("Role Type :"+userRoleType+" Segment Name :"+segmentName+" Role Id :"+roleId);
		return userDao.getMerchantDetails(userRoleType, segmentName, roleId);
	}
	
	public List<MerchantDetails> getAllMerchants() throws SystemException {
		List<MerchantDetails> merchants = new ArrayList<MerchantDetails>();
		try (Connection connection = getConnection()) {
			try (PreparedStatement prepStmt = connection.prepareStatement(query)) {
				try (ResultSet rs = prepStmt.executeQuery()) {
					while (rs.next()) {

						MerchantDetails merchant = new MerchantDetails();
						merchant.setPayId(rs.getString("payId"));
						merchant.setBusinessName(rs.getString("businessName"));
						merchant.setEmailId(rs.getString("emailId"));
						merchant.setMobile(rs.getString("Mobile"));
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String regDate = sdf.format(rs.getString("registrationDate"));
						merchant.setRegistrationDate(regDate);
						merchant.setUserType(rs.getString("userType"));
						String status = rs.getString("userStatus");

						if (status != null) {
							UserStatusType userStatus = UserStatusType.valueOf(status);
							merchant.setStatus(userStatus);
						}
						merchants.add(merchant);
					}
				}
			}
		} catch (SQLException exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		return merchants;
	}

	public List<MerchantDetails> getAllReselerMerchants(String resellerId) throws SystemException {
		List<MerchantDetails> merchants = new ArrayList<MerchantDetails>();
		try (Connection connection = getConnection()) {
			try (PreparedStatement prepStmt = connection.prepareStatement(reselerQuery)) {
				prepStmt.setString(1, resellerId);
				try (ResultSet rs = prepStmt.executeQuery()) {
					while (rs.next()) {
						MerchantDetails merchant = new MerchantDetails();
						merchant.setPayId(rs.getString("payId"));
						merchant.setResellerId(rs.getString("resellerId"));
						merchant.setBusinessName(rs.getString("businessName"));
						merchant.setEmailId(rs.getString("emailId"));
						merchant.setMobile(rs.getString("Mobile"));
						merchant.setRegistrationDate(rs.getString("registrationDate"));
						merchant.setUserType(rs.getString("userType"));
						String status = rs.getString("userStatus");

						if (status != null) {
							UserStatusType userStatus = UserStatusType.valueOf(status);
							merchant.setStatus(userStatus);
						}
						merchants.add(merchant);
					}
				}
			}
		} catch (SQLException exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		return merchants;
	}

	public List<MerchantDetails> getAllReseller() throws SystemException {
		
		
		
		List<MerchantDetails> merchants = new ArrayList<MerchantDetails>();
		List<Merchants> merchList = userDao.getAllReseller1();
		
		
		for (Merchants merch : merchList) {
			
			MerchantDetails merchant = new MerchantDetails();
			merchant.setPayId(merch.getPayId());
			merchant.setBusinessName(merch.getBusinessName());
			merchant.setEmailId(merch.getEmailId());
			merchant.setMobile(merch.getMobile());
			merchant.setRegistrationDate(merch.getRegistrationDate());
			merchant.setUserType(merch.getUserType());
			String status = merch.getStatus();

			if (status != null) {
				UserStatusType userStatus = UserStatusType.valueOf(status);
				merchant.setStatus(userStatus);
			}
			merchants.add(merchant);
			
		}
		
		/*
		 * try (Connection connection = getConnection()) { try (PreparedStatement
		 * prepStmt = connection.prepareStatement(rsellerListQuery)) { try (ResultSet rs
		 * = prepStmt.executeQuery()) { while (rs.next()) {
		 * 
		 * 
		 * } } } } catch (SQLException exception) { logger.error("Database error",
		 * exception); throw new SystemException(ErrorType.DATABASE_ERROR,
		 * ErrorType.DATABASE_ERROR.getResponseMessage()); }
		 */
		return merchants;
	}

	/*
	 * public List<MerchantDetails> getAllMerchants(String businessType, String
	 * roleType) throws SystemException { List<MerchantDetails> merchants = new
	 * ArrayList<MerchantDetails>();
	 * 
	 * try (Connection connection = getConnection()) { try (PreparedStatement
	 * prepStmt = connection.prepareStatement(businesTypeQuery)) {
	 * prepStmt.setString(1, businessType); if (StringUtils.isNotBlank(roleType)) {
	 * prepStmt.setString(2, roleType); } try (ResultSet rs =
	 * prepStmt.executeQuery()) { while (rs.next()) {
	 * 
	 * MerchantDetails merchant = new MerchantDetails();
	 * merchant.setPayId(rs.getString("payId"));
	 * merchant.setBusinessName(rs.getString("businessName"));
	 * merchant.setEmailId(rs.getString("emailId"));
	 * merchant.setMobile(rs.getString("Mobile"));
	 * merchant.setRegistrationDate(rs.getString("registrationDate"));
	 * merchant.setUserType(rs.getString("userType")); String status =
	 * rs.getString("userStatus");
	 * 
	 * if (status != null) { UserStatusType userStatus =
	 * UserStatusType.valueOf(status); merchant.setStatus(userStatus); }
	 * merchants.add(merchant); } } } } catch (SQLException exception) {
	 * logger.error("Database error", exception); throw new
	 * SystemException(ErrorType.DATABASE_ERROR,
	 * ErrorType.DATABASE_ERROR.getResponseMessage()); }
	 * 
	 * return UserDao.filterByRoleType(roleType, merchants); }
	 */
	
	public List<MerchantDetails> getAllMerchants(String businessType, String roleType, String segmentName, long roleId) throws SystemException {
		logger.info("Business Type :"+businessType+" Role Type :"+roleType+" Segment Name :"+roleId);
		List<MerchantDetails> merchants = new ArrayList<MerchantDetails>();

		try {
			Connection connection = getConnection();
			PreparedStatement prepStmt = null;
			if (roleId == 1) {
				prepStmt = connection.prepareStatement(businesTypeAdminQuery);
				prepStmt.setString(1, businessType);
			} else {
				prepStmt = connection.prepareStatement(businesTypeSubAdminQuery);
				prepStmt.setString(1, businessType);
				prepStmt.setString(2, segmentName);
			}

			if (StringUtils.isNotBlank(roleType)) {
				prepStmt.setString(2, roleType);
			}
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {

				logger.info("Getting Merchants Data");
				MerchantDetails merchant = new MerchantDetails();
				merchant.setPayId(rs.getString("payId"));
				merchant.setBusinessName(rs.getString("businessName"));
				merchant.setEmailId(rs.getString("emailId"));
				merchant.setMobile(rs.getString("Mobile"));
				merchant.setRegistrationDate(rs.getString("registrationDate"));
				merchant.setUserType(rs.getString("userType"));
				String status = rs.getString("userStatus");

				if (status != null) {
					logger.info("Status :"+status);
					UserStatusType userStatus = UserStatusType.valueOf(status);
					merchant.setStatus(userStatus);
				}
				logger.info("Merchant :"+merchant);
				merchants.add(merchant);
			}

		} catch (SQLException exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}

		return UserDao.filterByRoleType(roleType, merchants);
	}

	public List<MerchantDetails> getAllMerchantSubUser() throws SystemException {
		List<MerchantDetails> merchants = new ArrayList<MerchantDetails>();
		try (Connection connection = getConnection()) {
			try (PreparedStatement prepStmt = connection.prepareStatement(querySubUser)) {
				try (ResultSet rs = prepStmt.executeQuery()) {
					while (rs.next()) {

						MerchantDetails merchant = new MerchantDetails();
						merchant.setPayId(rs.getString("payId"));
						merchant.setEmailId(rs.getString("emailId"));
						merchant.setBusinessName(rs.getString("businessName"));
						merchant.setMobile(rs.getString("Mobile"));
						merchant.setRegistrationDate(rs.getString("registrationDate"));
						merchant.setUserType(rs.getString("userType"));
						String status = rs.getString("userStatus");

						if (status != null) {
							UserStatusType userStatus = UserStatusType.valueOf(status);
							merchant.setStatus(userStatus);
						}
						merchants.add(merchant);
					}
				}
			}
		} catch (SQLException exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		return merchants;
	}

	public List<MerchantDetails> getAllMerchantSubUserList(String emailId) throws SystemException {
		List<MerchantDetails> merchants = new ArrayList<MerchantDetails>();
		User user = userDao.findPayIdByEmail(emailId);
		String parentPayId = user.getPayId();
		try (Connection connection = getConnection()) {
			try (PreparedStatement prepStmt = connection.prepareStatement(querySubUserList)) {
				prepStmt.setString(1, parentPayId);
				prepStmt.setString(2, parentPayId);
				try (ResultSet rs = prepStmt.executeQuery()) {
					while (rs.next()) {

						MerchantDetails merchant = new MerchantDetails();
						merchant.setPayId(rs.getString("payId"));
						merchant.setEmailId(rs.getString("emailId"));
						merchant.setBusinessName(rs.getString("businessName"));
						merchant.setMobile(rs.getString("Mobile"));
						merchant.setRegistrationDate(rs.getString("registrationDate"));
						merchant.setUserType(rs.getString("userType"));
						String status = rs.getString("userStatus");

						if (status != null) {
							UserStatusType userStatus = UserStatusType.valueOf(status);
							merchant.setStatus(userStatus);
						}
						merchants.add(merchant);
					}
				}
			}
		} catch (SQLException exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		return merchants;
	}
}