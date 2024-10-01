package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.user.Account;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TDRStatus;

public class AccountSummary extends AbstractSecureAction {
	/**
	 * 
	 */
	private static Logger logger = LoggerFactory.getLogger(AccountSummary.class.getName());
	private static final long serialVersionUID = 959533707653092826L;
	private User user = new User();
    private  List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
	@Override
	public String execute() {

		try {
			Set<Account> accountList=new HashSet<Account>();
			user = (User) sessionMap.get(Constants.USER.getValue());
			accountList = user.getAccounts();
			 for (Account account : accountList) {
			if (null != accountList) {
				Set<ChargingDetails> data = account.getChargingDetails();
				for (PaymentType paymentType : PaymentType.values()) {
					String paymentName = paymentType.getName();
				for (ChargingDetails chargingDetails : data) {
					if (chargingDetails.getStatus().equals(TDRStatus.ACTIVE) && chargingDetails.getPaymentType().getName().equals(paymentName)) {
						chargingDetailsList.add(chargingDetails);
					}
				}
				}
			}
			 }
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return null;

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<ChargingDetails> getChargingDetailsList() {
		return chargingDetailsList;
	}

	public void setChargingDetailsList(List<ChargingDetails> chargingDetailsList) {
		this.chargingDetailsList = chargingDetailsList;
	}

}
