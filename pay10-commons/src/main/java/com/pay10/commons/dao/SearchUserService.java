package com.pay10.commons.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.SearchUser;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Acquirer;
import com.pay10.commons.util.Agent;
import com.pay10.commons.util.SubAdmin;
import com.pay10.commons.util.SubSuperAdmin;

@Service
public class SearchUserService {
	
	@Autowired
	private UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(SearchUserService.class.getName());
	public List<SearchUser> transactionList = new ArrayList<SearchUser>();

	public SearchUserService() {
	}

	public List<Merchants> getSubUsers(String parentPayId) throws SQLException, ParseException, SystemException {
		List<Merchants> subUser = new ArrayList<Merchants>();
		try {
			subUser = userDao.getSubUsers(parentPayId);
		} catch (Exception exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}

		return subUser;

	}

	public List<Merchants> getAcquirerSubUsers(String parentPayId)
			throws SQLException, ParseException, SystemException {
		List<Merchants> subUser = new ArrayList<Merchants>();
		try {
			subUser = userDao.getAcquirerSubUsers(parentPayId);
		} catch (Exception exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}

		return subUser;
	}	

	public List<SubAdmin> getAgentsList(String sessionPayId) throws SQLException, ParseException, SystemException {
		List<SubAdmin> agentList = new ArrayList<SubAdmin>();
		try {
			agentList = userDao.getUsers(sessionPayId);
		} catch (Exception exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		return agentList;

	}

	public List<SubSuperAdmin> getSubSuperAdminList(String sessionPayId) throws SQLException, ParseException, SystemException {
		List<SubSuperAdmin> agentList = new ArrayList<SubSuperAdmin>();
		try {
			agentList = userDao.getSubSuperAdmin(sessionPayId);
		} catch (Exception exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		return agentList;

	}
	
	public List<Acquirer> getAcquirersList(String sessionPayId) throws SQLException, ParseException, SystemException {
		List<Acquirer> acquirerList = new ArrayList<Acquirer>();
		try {
			acquirerList = userDao.getAcquirers();
		} catch (Exception exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		return acquirerList;

	}
	
	
	public List<Agent> getnewAgentsList() throws SQLException, ParseException, SystemException {
		List<Agent> newagentList = new ArrayList<Agent>();
		try {
			newagentList = userDao.getAgent();
		} catch (Exception exception) {
			logger.error("Database error", exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		return newagentList;

	}
}
