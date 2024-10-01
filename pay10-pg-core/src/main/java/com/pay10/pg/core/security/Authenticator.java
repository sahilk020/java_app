package com.pay10.pg.core.security;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Fields;

public interface Authenticator {

	public ErrorType checkLogin(String userId, String password);
	public void authenticate(Fields fields) throws SystemException;
	public User getUserFromPayId(Fields fields) throws SystemException;
	public User getUser(Fields fields);
	public void setUser(User user);
	public void setUpdatedUser(User user);
	public void validatePaymentOptions(Fields fields) throws SystemException;
	//public List<ChargingDetails> getSupportedChargingDetailsList();
	public void isUserExists(Fields fields) throws SystemException;
}
