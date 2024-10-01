package com.pay10.sbi.upi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.SbiTokenDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.SbiToken;
import com.pay10.commons.util.TransactionManager;

@Service("wssbiUpiUtils")
public class SbiUtils {
	private static Logger logger = LoggerFactory.getLogger(SbiUtils.class.getName());

	public SbiToken sbiToken = new SbiToken();

	public SbiTokenDao sbiTokenDao = new SbiTokenDao();

	public String getClientId() throws SystemException {
		String client_id = "";
		client_id = TransactionManager.getId();

		return client_id;
	}

	public Transaction getClientSecret(String acquirerName) {
		Transaction transaction = new Transaction();
		sbiToken = sbiTokenDao.getTokenByName(acquirerName);

		transaction.setAcquireName(sbiToken.getAcquirerName());
		transaction.setClientId(sbiToken.getClientId());
		transaction.setClientSecret(sbiToken.getClientSecret());
		transaction.setAccessToken(sbiToken.getAccessToken());
		transaction.setAccessTokenExpire(sbiToken.getAccessTokentExpire());
		transaction.setRefreshToken(sbiToken.getRefreshToken());
		transaction.setRefreshTokenExpire(sbiToken.getRefreshTokenExpire());
		return transaction;
	}

	public boolean validRefreshToken(String tokenTime, String validTill) {
		boolean validToken = false;

		return false;
	}

	public void saveToken(Transaction transaction) {

		sbiToken.setAcquirerName(transaction.getAcquireName());
		sbiToken.setClientId(transaction.getClientId());
		sbiToken.setClientSecret(transaction.getClientSecret());
		sbiToken.setAccessToken(transaction.getAccessToken());
		sbiToken.setAccessTokentExpire(transaction.getAccessTokenExpire());
		sbiToken.setRefreshToken(transaction.getRefreshToken());
		sbiToken.setRefreshTokenExpire(transaction.getAccessTokenExpire());
		sbiTokenDao.save(sbiToken);

	}

	public void updateToken(Transaction transaction) {

		sbiToken.setAcquirerName(transaction.getAcquireName());
		sbiToken.setClientId(transaction.getClientId());
		sbiToken.setClientSecret(transaction.getClientSecret());
		sbiToken.setAccessToken(transaction.getAccessToken());
		sbiToken.setAccessTokentExpire(transaction.getAccessTokenExpire());
		sbiToken.setRefreshToken(transaction.getRefreshToken());
		sbiToken.setRefreshTokenExpire(transaction.getAccessTokenExpire());

		sbiTokenDao.update(sbiToken);

	}

}
