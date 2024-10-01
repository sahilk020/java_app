package com.pay10.crm.action;

import java.util.Set;

import com.pay10.commons.dao.PendingMappingRequestDao;
import com.pay10.commons.user.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.MerchantAcquirerPropertiesDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

/**
 * @author Puneet
 *
 */
public class MerchantMappingDisplay extends AbstractSecureAction {

	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserDao userDao;

	@Autowired
	AWSEncryptDecryptService awsEncryptDecryptService;

	@Autowired
	private MerchantAcquirerPropertiesDao merchantAcquirerPropertiesDao;
	
	@Autowired
	private resellercommisiondao resellercommisiondao;

	@Autowired
	private PendingMappingRequestDao pendingMappingRequestDao;


	private static Logger logger = LoggerFactory.getLogger(MerchantMappingDisplay.class.getName());
	private static final long serialVersionUID = 8733557567586189516L;
	private String merchantEmailId;
	private String acquirer;
	private String region;
	private String response;
	private String mappedString;
	private Object currencyString;

//	private boolean international;
//	private boolean domestic;
	private boolean commercial;
	private boolean customer;
	private boolean resellerMapped;

	@Override
	public String execute() {
		try {
			if (acquirer != null && merchantEmailId != null) {
				getMapping();
				setAcquirerMerchantProperty(merchantEmailId);
				return SUCCESS;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			setResponse(ErrorType.MAPPING_NOT_FETCHED.getResponseMessage());
			return SUCCESS;
		}
		return SUCCESS;
	}

	public void setAcquirerMerchantProperty(String merchantEmailId) {

		try {

			String merchantPayId = userDao.getPayIdByEmailId(merchantEmailId);

			MerchantAcquirerProperties merchantAcquirerProperties = merchantAcquirerPropertiesDao
					.getMerchantAcquirerProperties(merchantPayId, acquirer);

			/*
			 * if (merchantAcquirerProperties == null) {
			 * 
			 * setCommercial(false); setCustomer(false); }
			 * 
			 * else if
			 * (merchantAcquirerProperties.getCardHolderType().equals(CardHolderType.ALL)) {
			 * setCommercial(true); setCustomer(true); } else if
			 * (merchantAcquirerProperties.getCardHolderType().equals(CardHolderType.
			 * COMMERCIAL)){ setCommercial(true); setCustomer(false); } else if
			 * (merchantAcquirerProperties.getCardHolderType().equals(CardHolderType.
			 * CONSUMER)){ setCommercial(false); setCustomer(true); } else {
			 * setCommercial(false); setCustomer(false); }
			 */

//			if (merchantAcquirerProperties == null) {
//
//				setInternational(false);
//				setDomestic(false);
//			}
//
//			else if (merchantAcquirerProperties.getPaymentsRegion().equals(AccountCurrencyRegion.ALL)) {
//				setInternational(true);
//				setDomestic(true);
//			} else if (merchantAcquirerProperties.getPaymentsRegion().equals(AccountCurrencyRegion.INTERNATIONAL)) {
//				setInternational(true);
//				setDomestic(false);
//			} else if (merchantAcquirerProperties.getPaymentsRegion().equals(AccountCurrencyRegion.DOMESTIC)) {
//				setInternational(false);
//				setDomestic(true);
//			} else {
//				setInternational(false);
//				setDomestic(false);
//			}

		}

		catch (Exception e) {
			logger.error("Exception in setAcquirerMerchantProperty = " + e);
		}
	}

	private void getMapping() throws SystemException {
		User user = userDao.find(merchantEmailId);
		user.getAccounts();
//		logger.info("user.getAccounts() :::::"+user.getAccounts());
		Account account = user.getAccountUsingAcquirerCode(acquirer);
		if (account == null) {
			setMappedString("");
			return;
		}
		Session session = null;
		try {
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			session.load(account, account.getId());
			PendingMappingRequest pendingMappingRequest = pendingMappingRequestDao.findActiveMappingRequest(merchantEmailId, acquirer);
			setMappedString(pendingMappingRequest.getMapString());
			if (StringUtils.isNotBlank(getMappedString())) {
				setResellerMapped(resellercommisiondao.isMappedReseller(user.getPayId()));
			}
			Set<AccountCurrency> accountCurrencySet = account.getAccountCurrencySet();
//			logger.info("accountCurrencySet in MerchantMappingDisplay :::::: "+accountCurrencySet);
			setCurrencyString(accountCurrencySet);
			tx.commit();
			for (AccountCurrency accountCurrency : accountCurrencySet) {
				if (!StringUtils.isAnyEmpty(accountCurrency.getPassword())) {
					String decryptedPassword = awsEncryptDecryptService.decrypt(accountCurrency.getPassword());
					accountCurrency.setPassword(decryptedPassword);
				}

				/*
				 * if(!StringUtils.isAnyEmpty(accountCurrency.getAdf1())){ String adf1 =
				 * awsEncryptDecryptService.decrypt(accountCurrency.getAdf1());
				 * accountCurrency.setAdf1(adf1); }
				 * 
				 * if(!StringUtils.isAnyEmpty(accountCurrency.getAdf2())){
				 * 
				 * String adf2 = awsEncryptDecryptService.decrypt(accountCurrency.getAdf2());
				 * accountCurrency.setAdf2(adf2);
				 * 
				 * }
				 * 
				 * if(!StringUtils.isAnyEmpty(accountCurrency.getAdf3())){
				 * 
				 * String adf3 = awsEncryptDecryptService.decrypt(accountCurrency.getAdf3());
				 * accountCurrency.setAdf3(adf3);
				 * 
				 * }
				 * 
				 * if(!StringUtils.isAnyEmpty(accountCurrency.getAdf4())){
				 * 
				 * String adf4 = awsEncryptDecryptService.decrypt(accountCurrency.getAdf4());
				 * accountCurrency.setAdf4(adf4);
				 * 
				 * }
				 * 
				 * if(!StringUtils.isAnyEmpty(accountCurrency.getAdf5())){
				 * 
				 * String adf5 = awsEncryptDecryptService.decrypt(accountCurrency.getAdf5());
				 * accountCurrency.setAdf5(adf5);
				 * 
				 * }
				 */

				boolean camsPay = StringUtils.equalsIgnoreCase(getAcquirer(), AcquirerType.CAMSPAY.getCode());
				if (StringUtils.isNotBlank(accountCurrency.getAdf6())) {

					String adf6 = !camsPay ? awsEncryptDecryptService.decrypt(accountCurrency.getAdf6())
							: accountCurrency.getAdf6();
					accountCurrency.setAdf6(adf6);

				}

				if (StringUtils.isNotBlank(accountCurrency.getAdf7())) {

					String adf7 = !camsPay ? awsEncryptDecryptService.decrypt(accountCurrency.getAdf7())
							: accountCurrency.getAdf7();
					accountCurrency.setAdf7(adf7);

				}

				/*
				 * if(!StringUtils.isAnyEmpty(accountCurrency.getAdf8())){
				 * 
				 * String adf8 = awsEncryptDecryptService.decrypt(accountCurrency.getAdf8());
				 * accountCurrency.setAdf8(adf8);
				 * 
				 * }
				 * 
				 * if(!StringUtils.isAnyEmpty(accountCurrency.getAdf9())){
				 * 
				 * String adf9 = awsEncryptDecryptService.decrypt(accountCurrency.getAdf9());
				 * accountCurrency.setAdf9(adf9);
				 * 
				 * }
				 * 
				 * if(!StringUtils.isAnyEmpty(accountCurrency.getAdf10())){
				 * 
				 * String adf10 = awsEncryptDecryptService.decrypt(accountCurrency.getAdf10());
				 * accountCurrency.setAdf10(adf10);
				 * 
				 * }
				 * 
				 * if(!StringUtils.isAnyEmpty(accountCurrency.getAdf11())){
				 * 
				 * String adf11 = awsEncryptDecryptService.decrypt(accountCurrency.getAdf11());
				 * accountCurrency.setAdf11(adf11);
				 * 
				 * }
				 */

			}

		} finally {
			HibernateSessionProvider.closeSession(session);
		}
	}

	@Override
	public void validate() {

		if ((validator.validateBlankField(getAcquirer()))) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.ACQUIRER, getAcquirer()))) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		if ((validator.validateBlankField(getMerchantEmailId()))) {
			addFieldError(CrmFieldType.MERCHANT_EMAIL_ID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.MERCHANT_EMAIL_ID, getMerchantEmailId()))) {
			addFieldError(CrmFieldType.MERCHANT_EMAIL_ID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		if ((validator.validateBlankField(getResponse()))) {
		} else if (!(validator.validateField(CrmFieldType.RESPONSE, getResponse()))) {
			addFieldError(CrmFieldType.RESPONSE.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}

	public String display() {
		return NONE;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getMappedString() {
		return mappedString;
	}

	public void setMappedString(String mappedString) {
		this.mappedString = mappedString;
	}

	public Object getCurrencyString() {
		return currencyString;
	}

	public void setCurrencyString(Object currencyString) {
		this.currencyString = currencyString;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

//	public boolean isInternational() {
//		return international;
//	}
//
//	public void setInternational(boolean international) {
//		this.international = international;
//	}
//
//	public boolean isDomestic() {
//		return domestic;
//	}
//
//	public void setDomestic(boolean domestic) {
//		this.domestic = domestic;
//	}

	public boolean isCommercial() {
		return commercial;
	}

	public void setCommercial(boolean commercial) {
		this.commercial = commercial;
	}

	public boolean isCustomer() {
		return customer;
	}

	public void setCustomer(boolean customer) {
		this.customer = customer;
	}

	public boolean isResellerMapped() {
		return resellerMapped;
	}

	public void setResellerMapped(boolean resellerMapped) {
		this.resellerMapped = resellerMapped;
	}
}
