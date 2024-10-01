package com.pay10.crm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.UserCommision;
import com.pay10.commons.user.UserCommissionDao;

public class UserCommissionDetailUpdate extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(UserCommissionDetailUpdate.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	@Autowired
	UserCommissionDao userCommissionDao;

	private boolean commissiontype;
	private String sma_commission_percent;
	private String sma_commission_amount;
	private String ma_commission_percent;
	private String ma_commission_amount;
	private String agent_commission_percent;
	private String agent_commission_amount;

	private String response;
	private String added_by;

	private long id;
	// List<Resellercommision> resellercommisiondata = new
	// ArrayList<Resellercommision>();

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {

			logger.info("commissiontype......" + commissiontype);
			logger.info("sma_commission_percent....." + sma_commission_percent);
			logger.info("sma_commission_amount......" + sma_commission_amount);
			logger.info("ma_commission_percent....." + ma_commission_percent);
			logger.info("ma_commission_amount......" + ma_commission_amount);
			logger.info("agent_commission_percent....." + agent_commission_percent);
			logger.info("agent_commission_amount......" + agent_commission_amount);
			logger.info("id....." + id);

			UserCommision usercommision = userCommissionDao.findByPayId(id);
			logger.info("usercommision......" + usercommision.toString());

			usercommision.setCommissiontype(commissiontype);
			usercommision.setSma_commission_amount(sma_commission_amount);
			usercommision.setSma_commission_percent(sma_commission_percent);
			usercommision.setMa_commission_amount(ma_commission_amount);
			usercommision.setMa_commission_percent(ma_commission_percent);
			usercommision.setAgent_commission_amount(agent_commission_amount);
			usercommision.setAgent_commission_percent(agent_commission_percent);
			usercommision.setAdded_by(added_by);
			usercommision.setId(id);

			userCommissionDao.saveandUpdate1(usercommision);

		} catch (Exception exception) {
			setResponse(ErrorType.CHARGINGDETAIL_NOT_SAVED.getResponseMessage());
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	public boolean isCommissiontype() {
		return commissiontype;
	}

	public String getSma_commission_percent() {
		return sma_commission_percent;
	}

	public void setSma_commission_percent(String sma_commission_percent) {
		this.sma_commission_percent = sma_commission_percent;
	}

	public String getSma_commission_amount() {
		return sma_commission_amount;
	}

	public void setSma_commission_amount(String sma_commission_amount) {
		this.sma_commission_amount = sma_commission_amount;
	}

	public String getMa_commission_percent() {
		return ma_commission_percent;
	}

	public void setMa_commission_percent(String ma_commission_percent) {
		this.ma_commission_percent = ma_commission_percent;
	}

	public String getMa_commission_amount() {
		return ma_commission_amount;
	}

	public void setMa_commission_amount(String ma_commission_amount) {
		this.ma_commission_amount = ma_commission_amount;
	}

	public String getAgent_commission_percent() {
		return agent_commission_percent;
	}

	public void setAgent_commission_percent(String agent_commission_percent) {
		this.agent_commission_percent = agent_commission_percent;
	}

	public String getAgent_commission_amount() {
		return agent_commission_amount;
	}

	public void setAgent_commission_amount(String agent_commission_amount) {
		this.agent_commission_amount = agent_commission_amount;
	}

	public void setCommissiontype(boolean commissiontype) {
		this.commissiontype = commissiontype;
	}

	public String getAdded_by() {
		return added_by;
	}

	public void setAdded_by(String added_by) {
		this.added_by = added_by;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
