package com.pay10.crm.action;

import java.util.*;

import com.pay10.commons.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.BinCountryMapperType;
import com.pay10.commons.util.BusinessType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.InvoiceType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.States;
import com.pay10.crm.actionBeans.CurrencyMapProvider;

import static com.pay10.commons.common.EncryptDecrypt.decryptData;
import static com.pay10.commons.common.EncryptDecrypt.main;

/**
 * @author Puneet , Rajendra 
 * 
 */

public class ForwardAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(ForwardAction.class.getName());
	String mainKey = PropertiesManager.propertiesMap.get("MAIN_KEY_FOR_ALL_MERCHANT");

	@Autowired
	private UserDao userDao;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	private static final long serialVersionUID = -6411665872667971425L;
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private List<CompanyProfileUi> companyList = new ArrayList<CompanyProfileUi>();
	private Map<String, String> currencyMap = new LinkedHashMap<String, String>();
	private Map<String, String> industryTypes = new TreeMap<String, String>();
	private Map<String ,String> currencyList=new HashMap<>();
	private Map<String, String>currencyByPayID=new HashMap<>();
	private String  merchantPayId;
	private User sessionUser = null;
	private List<User> userActivelist = new ArrayList<User>();
	private List<InvoiceType> invoiceSearchPromotionalPaymentType;

	private String decryptedMobileNumber;
	private String decryptedCINNumber;
	private String selectedCurrency;
	private List<MultCurrencyCode>currencyListDao;

	Map<String,String> defultCurrencyMapList=new HashMap<>();


	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		CurrencyMapProvider currencyMapProvider = new CurrencyMapProvider();
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());

			if (sessionUser == null) {
				return INPUT;
			}
			
			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBSUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.RESELLER)
					|| sessionUser.getUserType().equals(UserType.AGENT)
					|| sessionUser.getUserType().equals(UserType.ACQUIRER)) {
				if (sessionUser.getUserType().equals(UserType.RESELLER)) {
					merchantList = new UserDao()
							.getActiveResellerMerchants(sessionUser
									.getResellerId());
				} else if (sessionUser.getUserType().equals(UserType.ACQUIRER)) {
					String merchantPayId = sessionUser.getPayId();
					List<User> userlist = new ArrayList<User>();
					userlist = new UserDao().getUserActiveList();
					for (User user : userlist) {
						Merchants merchant = new Merchants();
						merchant.setEmailId(user.getEmailId());
						merchant.setPayId(user.getPayId());
						merchant.setBusinessName(user.getBusinessName());
						userActivelist.add(user);
						Set<Account> accountSet = user.getAccounts();
						Iterator<Account> accountDetails = accountSet
								.iterator();
						while (accountDetails.hasNext()) {
							Account account = accountDetails.next();
							if (merchantPayId
									.equals(account.getAcquirerPayId())) {
								merchantList.add(merchant);
							}

						}
					}
				} else {
					
					List<InvoiceType> merchantPaymentList = new ArrayList<InvoiceType>();
					merchantPaymentList.add(InvoiceType.SINGLE_PAYMENT);
					merchantPaymentList.add(InvoiceType.PROMOTIONAL_PAYMENT);
					
					setInvoiceSearchPromotionalPaymentType(merchantPaymentList);
					
					//merchantList = new UserDao().getMerchantActiveList();
					merchantList = new UserDao().getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
					setIndustryTypes(BusinessType.getIndustryCategoryList());
				}
				currencyMap = currencyMapProvider.currencyMap(sessionUser);

			} else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
				
				List<InvoiceType> merchantPaymentList = new ArrayList<InvoiceType>();
				merchantPaymentList.add(InvoiceType.SINGLE_PAYMENT);
				merchantPaymentList.add(InvoiceType.PROMOTIONAL_PAYMENT);
				
				setInvoiceSearchPromotionalPaymentType(merchantPaymentList);
				String merchantKey = decryptData(sessionUser.getEncKey(), mainKey);
				logger.info("Before Decrypted Number: "+sessionUser.getMobile());
				setDecryptedMobileNumber(decryptData(sessionUser.getMobile(),merchantKey));
				logger.info("Decrypted Number "+decryptData(sessionUser.getMobile(),merchantKey));
				setDecryptedCINNumber(decryptData(sessionUser.getCin(),merchantKey));
				logger.info("Decrypted CIN: "+decryptData(sessionUser.getCin(),merchantKey));
				//sessionPut("contactNumber",decryptData(sessionUser.getMobile(),mainKey));
				//logger.info("Number " +decryptData(sessionUser.getMobile(),mainKey));
				currencyMap = currencyMapProvider.currencyMap(sessionUser);
				Merchants merchant = new Merchants();
				merchant.setEmailId(sessionUser.getEmailId());
				merchant.setPayId(sessionUser.getPayId());
				merchant.setBusinessName(sessionUser.getBusinessName());
				sessionUser.setPaymentLink(PropertiesManager.propertiesMap.get("merchantPaymentLinkFormURL")+""+sessionUser.getPayId());
				merchantList.add(merchant);
				if (currencyMap.isEmpty()) {
					addFieldError(CrmFieldType.DEFAULT_CURRENCY.getName(),
							ErrorType.UNMAPPED_CURRENCY_ERROR
									.getResponseMessage());
					addActionMessage("No currency mapped!!");
					return INPUT;
				}

			} else if (sessionUser.getUserType().equals(UserType.SUBUSER)) {
				
				List<InvoiceType> merchantPaymentList = new ArrayList<InvoiceType>();
				merchantPaymentList.add(InvoiceType.SINGLE_PAYMENT);
				merchantPaymentList.add(InvoiceType.PROMOTIONAL_PAYMENT);
				setInvoiceSearchPromotionalPaymentType(merchantPaymentList);
				
				Merchants merchant = new Merchants();
				String parentMerchantPayId = sessionUser.getParentPayId();
				User parentMerchant = userDao.findPayId(parentMerchantPayId);
				merchant.setMerchant(parentMerchant);
				merchantList.add(merchant);
				currencyMap = currencyMapProvider.currencyMap(sessionUser);
				if (currencyMap.isEmpty()) {
					addFieldError(CrmFieldType.DEFAULT_CURRENCY.getName(),
							ErrorType.UNMAPPED_CURRENCY_ERROR
									.getResponseMessage());
					addActionMessage("No currency mapped!!");
					return INPUT;
				}

			}
			return INPUT;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@SuppressWarnings("unchecked")
	public String monthlyInvoiceError() {
		CurrencyMapProvider currencyMapProvider = new CurrencyMapProvider();
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBSUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {

				List<InvoiceType> merchantPaymentList = new ArrayList<InvoiceType>();
				merchantPaymentList.add(InvoiceType.SINGLE_PAYMENT);
				merchantPaymentList.add(InvoiceType.PROMOTIONAL_PAYMENT);
				setInvoiceSearchPromotionalPaymentType(merchantPaymentList);
				//merchantList = new UserDao().getMerchantActiveList();
				merchantList = new UserDao().getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
				setIndustryTypes(BusinessType.getIndustryCategoryList());
				currencyMap = currencyMapProvider.currencyMap(sessionUser);
			}
			logger.info("Inside Monthly invoice Error Class");
			addActionMessage("Unable to Download File (Or) File Does Not Exist on Server!!");
			return INPUT;

		} catch (Exception e) {
			logger.error("Failed to Generate the Monthly Invoice Report, Exception is: " + e);
			addActionMessage("Unable to download the file");
			return INPUT;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String superAdminCompny() {
		
		CurrencyMapProvider currencyMapProvider = new CurrencyMapProvider();
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN) || sessionUser.getUserType().equals(UserType.SUBSUPERADMIN)) {

				//merchantList = new UserDao().getMerchantActiveList();
				merchantList = new UserDao().getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
				companyList = new CompanyProfileDao().getAllCompanyProfileList();
			}
			logger.info("Inside the Admin sign up");
			return INPUT;

		} catch (Exception e) {
			logger.error("Failed to load the Admin sign up, Exception is: " + e);
			addActionMessage("Failed to load the Admin sign up");
			return INPUT;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public String allMerchants() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.SUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBSUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.ACQUIRER)
					) {

					merchantList = new UserDao().getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
				    // set currencies
				    currencyMap = Currency.getAllCurrency();
			    } 
			 else if (sessionUser.getUserType().equals(UserType.RESELLER)) {
				Merchants merchant = new Merchants();
				User user = new User();
				UserDao userDao = new UserDao();
				user = userDao.findPayId(sessionUser.getResellerId());
				merchant.setMerchant(user);
				merchantList.add(merchant);
				// set currencies
				currencyMap = Currency.getAllCurrency();
				
			} else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
				Merchants merchant = new Merchants();
				merchant.setMerchant(sessionUser);
				merchantList.add(merchant);
				// set currencies
				currencyMap = Currency.getSupportedCurreny(sessionUser);
			} else if (sessionUser.getUserType().equals(UserType.SUBUSER)) {
				Merchants merchant = new Merchants();
				User user = new User();
				UserDao userDao = new UserDao();
				user = userDao.findPayId(sessionUser.getParentPayId());
				merchant.setMerchant(user);
				merchantList.add(merchant);
				// set currencies
				currencyMap = Currency.getSupportedCurreny(sessionUser);
			} else if (sessionUser.getUserType().equals(UserType.AGENT)) {
				Merchants merchant = new Merchants();
				User user = new User();
				UserDao userDao = new UserDao();
				user = userDao.findPayId(sessionUser.getPayId());
				merchant.setMerchant(user);
				merchantList.add(merchant);
				// set currencies
				currencyMap = Currency.getSupportedCurreny(sessionUser);
			}

			return INPUT;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}
	//to provide default country on Invoice page
	public String getDefaultCountry(){
			return BinCountryMapperType.INDIA.getName();
	}
	
	//to provide default Company Name
	public String getDefaultCompanyName(){
		if(companyList.size() != 0) {
			for (CompanyProfileUi cpu : companyList) {
					return cpu.getCompanyName().toString();										
			}
		}
		return "ALL";
	}

	public String getDefaultCurrency(){
		currencyListDao=multCurrencyCodeDao.findAll();

	  	for(MultCurrencyCode currency: currencyListDao)
		{
			defultCurrencyMapList.put(currency.getCode(),currency.getName());
 		}
		setDefultCurrencyMapList(defultCurrencyMapList);
		return SUCCESS;
	}

	public String getCurrencyByID()
	{
		List<String> currencyCodeList=new ArrayList<>();
		currencyCodeList=userDao.findCurrencyByPayId(getMerchantPayId());
		for(String currencyCode: currencyCodeList)
		{
			logger.info("Currency code"+currencyCode+"for:"+getMerchantPayId());
			String currencyName = multCurrencyCodeDao.getCurrencyNamebyCode(currencyCode);
			currencyByPayID.put(currencyCode,currencyName);

		}
		setCurrencyByPayID(currencyByPayID);
		return SUCCESS;

	}
	//to provide default State value
	public String getDefaultState(){
		return States.SELECT_STATE.getName();
	}
	
	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public List<CompanyProfileUi> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<CompanyProfileUi> companyList) {
		this.companyList = companyList;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public Map<String, String> getIndustryTypes() {
		return industryTypes;
	}

	public void setIndustryTypes(Map<String, String> industryTypes) {
		this.industryTypes = industryTypes;
	}

	public List<InvoiceType> getInvoiceSearchPromotionalPaymentType() {
		return invoiceSearchPromotionalPaymentType;
	}

	public void setInvoiceSearchPromotionalPaymentType(List<InvoiceType> invoiceSearchPromotionalPaymentType) {
		this.invoiceSearchPromotionalPaymentType = invoiceSearchPromotionalPaymentType;
	}

	public Map<String, String> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(Map<String, String> currencyList) {
		this.currencyList = currencyList;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

	public Map<String, String> getCurrencyByPayID() {
		return currencyByPayID;
	}

	public void setCurrencyByPayID(Map<String, String> currencyByPayID) {
		this.currencyByPayID = currencyByPayID;
	}

	public String getSelectedCurrency() {
		return selectedCurrency;
	}

	public void setSelectedCurrency(String selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}

	public Map<String, String> getDefultCurrencyMapList() {
		return defultCurrencyMapList;
	}

	public void setDefultCurrencyMapList(Map<String, String> defultCurrencyMapList) {
		this.defultCurrencyMapList = defultCurrencyMapList;
	}

	public String getDecryptedMobileNumber() {
		return decryptedMobileNumber;
	}

	public void setDecryptedMobileNumber(String decryptedMobileNumber) {
		this.decryptedMobileNumber = decryptedMobileNumber;
	}

	public String getDecryptedCINNumber() {
		return decryptedCINNumber;
	}

	public void setDecryptedCINNumber(String decryptedCINNumber) {
		this.decryptedCINNumber = decryptedCINNumber;
	}
}
