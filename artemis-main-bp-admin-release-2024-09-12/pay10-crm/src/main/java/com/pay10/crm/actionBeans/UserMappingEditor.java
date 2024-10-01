package com.pay10.crm.actionBeans;

import com.google.gson.Gson;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

import java.util.Arrays;

/**
 * @author Puneet
 *
 */
@Service
public class UserMappingEditor {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AccountFactory accountFactory;

	private static Logger logger = LoggerFactory.getLogger(UserMappingEditor.class.getName());

	private boolean accountPresent = false;

	public void decideAccountChange(String userEmail, String mappingString,
									String acquirerCode, AccountCurrency[] accountCurrencySetFE, boolean international, boolean domestic) {

		logger.info("decideAccountChange() acquirerCode......"+acquirerCode);
		logger.info("decideAccountChange() accountCurrencySetFE......"+new Gson().toJson(accountCurrencySetFE));
		logger.info("decideAccountChange() international......"+international);
		logger.info("decideAccountChange() domestic......"+domestic);
		logger.info("userEmail :::::::"+userEmail);
		Session session = null;
		try {
			accountPresent = false;
			User user = new User();
			Account account = new Account();
			Account savedAccount = new Account();
			String acquirerPayId;
			User acquirer;
			acquirer = userDao.findAcquirerByCode(acquirerCode);
			acquirerPayId = (acquirer.getPayId());

			logger.info("acquirerPayId ::::"+acquirerPayId);
			user = userDao.find(userEmail);

			logger.info("user.getBusinessName() :::"+user.getBusinessName());

			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
//			session.load(user, userEmail);
			savedAccount = user.getAccountUsingAcquirerCode(acquirerCode);
			if (savedAccount != null) {
				setAccountPresent(true);
			}

			logger.info("Check Account Present condition:::"+accountPresent);
			// create account
			if (!accountPresent) {
//				logger.info("ACCOUNT PRESENT ::::::"+accountPresent);
//				logger.info("Inside IF condition");
//				logger.info("Acquirer Pay ID :::::"+acquirerPayId);
				account.setAcquirerPayId(acquirerPayId);
//				logger.info("Merchant ID :::::"+acquirer.getBusinessName());
				account.setAcquirerName(acquirer.getBusinessName());
//				logger.info("Mapping String ::::::"+mappingString);
//				logger.info("User PAY ID :::::"+user.getPayId());
				account = accountFactory.editAccount(account, mappingString,user.getPayId());
//				logger.info("ACCOUNT :::::::::"+account);
				// add currency
//				logger.info("accountCurrencySetFE ::::::"+ Arrays.toString(accountCurrencySetFE));
				account = accountFactory.addAccountCurrencyForNewTdr(account, accountCurrencySetFE, acquirer, user.getPayId(),international, domestic, mappingString);
				user.addAccount(account);
			} else {
				// edit if the account present
//				logger.info("Inside ELSE condition");
//				logger.info("SavedAccount in ELSE :::"+savedAccount);
//				logger.info("SavedAccount in ELSE :::"+mappingString);
				savedAccount = accountFactory.editAccount(savedAccount,	mappingString, user.getPayId());
				// edit currency
//				logger.info("savedAccount in edit currency :::"+savedAccount);
				savedAccount = accountFactory.removeAccountCurrencyForNewTdrSetting(savedAccount, accountCurrencySetFE,international, domestic, mappingString);
				savedAccount = accountFactory.addAccountCurrencyForNewTdr(savedAccount, accountCurrencySetFE, acquirer, user.getPayId(), international, domestic, mappingString);

			}
//			logger.info("USER INFO ::::::"+user);
			session.update(user);
			tx.commit();
		}
		catch(Exception e) {
			logger.error("Exception in decideAccountChange "+e);
			e.printStackTrace();
		}
		finally {
			HibernateSessionProvider.closeSession(session);
		}
	}

	public boolean isAccountPresent() {
		return accountPresent;
	}

	public void setAccountPresent(boolean accountPresent) {
		this.accountPresent = accountPresent;
	}
}
