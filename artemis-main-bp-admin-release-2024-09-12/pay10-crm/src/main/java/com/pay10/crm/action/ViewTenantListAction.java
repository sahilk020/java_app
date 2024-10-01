package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.SearchUserService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.SubAdmin;
import com.pay10.commons.util.Tenant;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReports;

/**
 * @ Rajendra
 */

public class ViewTenantListAction   extends AbstractSecureAction {

	private static final long serialVersionUID = 5929300136391392637L;
	private static Logger logger = LoggerFactory.getLogger(ViewTenantListAction.class.getName());
	private List<Tenant> aaData = new ArrayList<Tenant>();
	private String emailId;
	private String phoneNo;

	@Autowired
	private SearchUserService searchUserService;
	
	@Autowired
	private CompanyProfileDao companyProfileDao;
	
	@Override
	public String execute() {
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		DataEncoder encoder = new DataEncoder();
		try {

			if ((sessionUser.getUserType().equals(UserType.SUPERADMIN)) || (sessionUser.getUserType().equals(UserType.SUBSUPERADMIN))) {
				setAaData(encoder.encodeTenantsObj(companyProfileDao.getAllTenants()));

			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return SUCCESS;
	}

	@Override
	public void validate() {
		// No need validations for front end side. We are not taking any front values 

	}

	public List<Tenant> getAaData() {
		return aaData;
	}

	public void setAaData(List<Tenant> aaData) {
		this.aaData = aaData;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	
}
