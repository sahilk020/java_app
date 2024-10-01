package com.pay10.crm.invoice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.BinCountryMapperType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.Currency;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.actionBeans.CurrencyMapProvider;

public class SingleInvoice extends AbstractSecureAction implements ModelDriven<Invoice> {

	/**
	 * @author shubhamchauhan
	 */
	private static final long serialVersionUID = 8597581873868173117L;
	private Invoice invoice = null;
	private List<Merchants> merchantList = new ArrayList<>();
	private Map<String, String> currencyMap = new LinkedHashMap<String, String>();
	private User sessionUser = null;
	
	@Autowired
	private UserDao userDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		setInvoice(new Invoice());
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		CurrencyMapProvider currencyMapProvider = new CurrencyMapProvider();

		if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)
				|| sessionUser.getUserType().equals(UserType.RESELLER)
				|| sessionUser.getUserType().equals(UserType.SUPERADMIN)) {
			//merchantList = new UserDao().getMerchantActiveList();
			merchantList = new UserDao().getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
			currencyMap = currencyMapProvider.currencyMap(sessionUser);
		} else {
			currencyMap = Currency.getSupportedCurreny(sessionUser);
			if (sessionUser.getUserType().equals(UserType.SUBUSER)) {
				System.out.println("Sub user");
				Merchants merchant = new Merchants();
				String parentPayId = sessionUser.getParentPayId();
				User parentUser = userDao.findPayId(parentPayId);
				merchant.setMerchant(parentUser);
				merchantList.add(merchant);
				currencyMap = Currency.getSupportedCurreny(parentUser);
				invoice.setPayId(sessionUser.getParentPayId());
				invoice.setBusinessName(userDao.getMerchantByPayId(sessionUser.getParentPayId()));

			} else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
				invoice.setPayId(sessionUser.getPayId());
				invoice.setBusinessName(userDao.getMerchantByPayId(sessionUser.getPayId()));
				Merchants merchant = new Merchants();
				merchant.setEmailId(sessionUser.getEmailId());
				merchant.setPayId(sessionUser.getPayId());
				merchant.setBusinessName(sessionUser.getBusinessName());
				merchantList.add(merchant);

			}
		}
		
		setDefault();
		return SUCCESS;
	}

	private void setDefault() {
		invoice.setExpiresDay("0");
		invoice.setExpiresHour("0");
		invoice.setQuantity("1");
	}

	@Override
	public Invoice getModel() {
		return invoice;
	}

	// to provide default country
	public String getDefaultCountry() {
		if (StringUtils.isBlank(invoice.getCountry())) {
			return BinCountryMapperType.INDIA.getName();
		} else {
			return invoice.getCountry();
		}
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

}
