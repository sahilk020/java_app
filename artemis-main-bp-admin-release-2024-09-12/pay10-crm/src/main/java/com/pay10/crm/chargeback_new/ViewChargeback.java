package com.pay10.crm.chargeback_new;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Chargeback;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.chargeback_new.action.beans.ChargebackDao;

public class ViewChargeback extends AbstractSecureAction {

	@Autowired
	private ChargebackDao chargebackDao;
	
	private static final long serialVersionUID = 4208045338885337001L;
	private static Logger logger = LoggerFactory.getLogger(ViewChargeback.class.getName());
	private Chargeback chargeback = new Chargeback();
	private List<Chargeback> aaData;
	private String payId;
	
	private String chargebackType;
	private String chargebackStatus;
	private String dateTo;
	private String dateFrom;
	
	@Override
	public String execute(){
		try{
			User user = (User) sessionMap.get(Constants.USER);
			setDateFrom(DateCreater.formatDateforChargeback(dateFrom));
			setDateTo(DateCreater.formatDateforChargeback(dateTo));
			
			if (user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN) || user.getUserType().equals(UserType.SUPERADMIN)) {
				setAaData(chargebackDao.findAllChargeback(getDateFrom(),getDateTo(),getPayId(),getChargebackType(),getChargebackStatus()));
			}
			else if (user.getUserType().equals(UserType.MERCHANT)) {
				setAaData(chargebackDao.findChargebackByPayid(getDateFrom(),getDateTo(),user.getPayId(),getChargebackType(),getChargebackStatus()));
			}
			else if (user.getUserType().equals(UserType.SUBUSER)) {
				setAaData(chargebackDao.findChargebackByPayid(getDateFrom(),getDateTo(),getPayId(),getChargebackType(),getChargebackStatus()));
			}
			
			
			return SUCCESS;
			
		}
		catch(Exception exception){
			logger.error("Exception", exception);
			return ERROR;
			
		}
		
		
	}


	public Chargeback getChargeback() {
		return chargeback;
	}


	public void setChargeback(Chargeback chargeback) {
		this.chargeback = chargeback;
	}


	public List<Chargeback> getAaData() {
		return aaData;
	}


	public void setAaData(List<Chargeback> aaData) {
		this.aaData = aaData;
	}


	public String getPayId() {
		return payId;
	}


	public void setPayId(String payId) {
		this.payId = payId;
	}



	public String getChargebackType() {
		return chargebackType;
	}


	public void setChargebackType(String chargebackType) {
		this.chargebackType = chargebackType;
	}


	public String getChargebackStatus() {
		return chargebackStatus;
	}


	public void setChargebackStatus(String chargebackStatus) {
		this.chargebackStatus = chargebackStatus;
	}


	public String getDateTo() {
		return dateTo;
	}


	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}


	public String getDateFrom() {
		return dateFrom;
	}


	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
}
