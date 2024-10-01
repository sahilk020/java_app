package com.pay10.crm.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.MerchantDetails;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.DataEncoder;

public class AdminViewListAction extends AbstractSecureAction {

	/**
	 * @author Neeraj
	 */
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DataEncoder dataEncoder;
	
	
	private static final long serialVersionUID = 5558565922924803420L;
	private static Logger logger = LoggerFactory.getLogger(AdminViewListAction.class.getName());
	private List<MerchantDetails> aaData;

	@Override
	public String execute() {
		try {
			aaData = dataEncoder.encodeMerchantDetailsObj(userDao.getAllAdminList());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public List<MerchantDetails> getAaData() {
		return aaData;
	}

	public void setAaData(List<MerchantDetails> aaData) {
		this.aaData = aaData;
	}
}
