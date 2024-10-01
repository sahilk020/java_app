package com.pay10.pg.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.entity.CurrencyCode;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MerchantPaymentLink;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.MerchantKeySaltService;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

/**
 * @author Rajendra
 *
 */

public class MerchantPaymentLinkAction extends AbstractSecureAction {

	@Autowired
	private CrmValidator validator;
	
	@Autowired 
	MerchantKeySaltService merchantKeySaltService;

	private MerchantPaymentLink mpl = new MerchantPaymentLink();
	private static final long serialVersionUID = 6995933586262716101L;
	private static Logger logger = LoggerFactory.getLogger(MerchantPaymentLinkAction.class.getName());
	private String svalue;
	private String totalamount ="0.00";
	private String enablePay;
	private String currencyName;
	private String invoiceId;
	
	private Map<String, String> currencies;

	public List<MultCurrencyCode> currencyList = new ArrayList<MultCurrencyCode>();
	public	Map<String, String> currencyMap = new HashMap<String, String>();

	private String currencyCode = "356";
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private MultCurrencyCodeDao currencyCodeDao;

	
	@Override
	public String execute() {

		try {
			if(!validate(svalue)) {
				return "invalidRequest";
			}
			User user = userDao.findPayId(svalue);
			
			mpl = getMerchantPaymentLinkInstance();
			//mpl.setSaltKey(SaltFactory.getSaltProperty(user));
			mpl.setSaltKey(merchantKeySaltService.getMerchantKeySalt(user.getPayId()).getSalt());
			if (null == mpl) {
				return "invalidRequest";
			}
			ErrorType errorType = ErrorType.SUCCESS;
			currencyList= currencyCodeDao.getCurrencyCode();
			for (MultCurrencyCode currency : currencyList) {
				currencyMap.put(currency.getName(),currency.getCode());
			}
//			setCurrencies(currencyMap);
			logger.info(new Gson().toJson(currencyMap));
			mpl.setCurrencies(currencyMap);

			
			String merchantPaymentLinkPage = "paymentLink";
			setCurrencyName(Currency.getAlphabaticCode(mpl.getCurrencyCode()));
			
			logger.info("currency : "+getCurrencyName());
			addActionMessage(errorType.getResponseMessage());
			sessionMap.put(Constants.STATUS.getValue(), errorType);
			
			if(user.getUserStatus().getStatus().equalsIgnoreCase("Active")) {
				setEnablePay("TRUE");
			}else {
				setEnablePay("FALSE");
			}
			
			getMpl();
			return merchantPaymentLinkPage;
			
		} catch (Exception exception) {
			MDC.put(Constants.CRM_LOG_USER_PREFIX.getValue(), mpl.getPaymentNumber() + svalue);
			logger.error("Error validating Invoice", exception);
			return ERROR;
		}

	}


	private MerchantPaymentLink getMerchantPaymentLinkInstance() {
		mpl.setCurrencyCode(getCurrencyCode());
		mpl.setPayId(getSvalue());
		String paymentLinkretrunUrl = PropertiesManager.propertiesMap.get(CrmFieldConstants.MERCHANT_RETURN_URL.getValue());
		mpl.setReturnUrl(paymentLinkretrunUrl);
		mpl.setOrderId("Cash"+TransactionManager.getNewTransactionId());
		String paymentLinkUrl = PropertiesManager.propertiesMap.get(CrmFieldConstants.MERCHANT_PAYMENT_LINK.getValue());
		mpl.setPaymentActionUrl(paymentLinkUrl);
		mpl.setCurrencyName(Currency.getAlphabaticCode(mpl.getCurrencyCode()));
		return mpl;
	}


	public boolean validate(String sValue) {

		if (validator.validateBlankField(getSvalue())) {
			return false;
		} else if (!validator.validateField(CrmFieldType.PAY_ID, getSvalue())) {
			return false;
		}else {
			return true;
		}
	}


	public Map<String, String> getCurrencies() {
		return currencies;
	}


	public void setCurrencies(Map<String, String> currencies) {
		this.currencies = currencies;
	}

	public MerchantPaymentLink getMpl() {
		return mpl;
	}

	public void setMpl(MerchantPaymentLink mpl) {
		this.mpl = mpl;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getSvalue() {
		return svalue;
	}

	public void setSvalue(String svalue) {
		this.svalue = svalue;
	}

	public String getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(String totalamount) {
		this.totalamount = totalamount;
	}

	public String getEnablePay() {
		return enablePay;
	}

	public void setEnablePay(String enablePay) {
		this.enablePay = enablePay;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
}
