package com.pay10.crm.reseller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

public class CheckResellerAlreadyMappedAction extends AbstractSecureAction {

	private static final long serialVersionUID = -2637447578708994732L;
	private static final Logger logger = LoggerFactory.getLogger(CheckResellerAlreadyMappedAction.class.getName());

	@Autowired
	private UserDao userDao;

	private String payId;
	private boolean isMapped;

	@Override
	public String execute() {
		return isMappedReseller();
	}

	private String isMappedReseller() {
		try {
			logger.info("isMappedReseller:: payId={}, isExist={}", getPayId(), isMapped());
			User user = userDao.findByPayId(getPayId());
			setMapped(StringUtils.isNotBlank(user.getResellerId()));
			logger.info("isMappedReseller:: payId={}, isExist={}", getPayId(), isMapped());
			return SUCCESS;
		} catch (Exception ex) {
			logger.error("isMappedReseller:: failed. payId={}", getPayId(), ex);
			return ERROR;
		}
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public boolean isMapped() {
		return isMapped;
	}

	public void setMapped(boolean isMapped) {
		this.isMapped = isMapped;
	}
}
