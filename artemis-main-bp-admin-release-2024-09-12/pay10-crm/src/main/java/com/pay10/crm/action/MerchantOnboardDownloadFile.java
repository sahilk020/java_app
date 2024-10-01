package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.Payment;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.BinCountryMapperType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.MerchantKeySaltService;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.SaltFactory;
import com.pay10.commons.util.States;
import com.pay10.crm.actionBeans.CurrencyMapProvider;
import com.pay10.crm.actionBeans.IndustryTypeCategoryProvider;
import com.pay10.pg.core.fraudPrevention.util.AccountPasswordScrambler;

public class MerchantOnboardDownloadFile extends AbstractSecureAction implements ModelDriven<User>{

	private static final long serialVersionUID = 1726928933385353789L;
	private static Logger logger = LoggerFactory.getLogger(MerchantOnboardDownloadFile.class.getName());

	@Autowired 
	MerchantKeySaltService merchantKeySaltService;
	
	private String payId;
	private String fileName;
	private String responseMsg;
	
	private InputStream fileInputStream;
	
	public InputStream getFileInputStream() {
		return fileInputStream;
	}
	
	private User user = new User();
	private String salt;
	private Boolean isFlag;
	private Map<String, String> currencyMap = new LinkedHashMap<>();
	private Map<String, String> industryTypesList = new TreeMap<>();

	
	@Autowired
	private IndustryTypeCategoryProvider industryTypeCategoryProvider;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private CurrencyMapProvider currencyMapProvider;
	
	@Autowired
	private AccountPasswordScrambler accPwdScrambler;
	
	@Override
	public String execute() {
	    try {
	    	String filePath = PropertiesManager.propertiesMap.get(Constants.MERCHANT_ONBOARD_DOC_UPLOAD_PATH.getValue());
			// get old chargeback...
			
	    	filePath += getPayId() + "/docs/";
			File file = new File(filePath + getFileName());
			if(!file.exists()) {
				logger.error("File doesn't exists : " + file.getAbsolutePath());
				addActionMessage("File does not exist");
				setData();
				return INPUT;
			}
			
			fileInputStream = new FileInputStream(file);
			// Close the stream.
		} catch (FileNotFoundException e) {
			logger.error("Error in Chargeback File Download");
			logger.error(e.getMessage());
			addActionMessage("Unable to download file");
			setData();
			return INPUT;
		}
	    return SUCCESS;
	} 
	
	public void setData() {
		setUser(userDao.findPayId(getPayId()));
		
		// Convert document blob to string.
		user.setOnBoardDocListString(DataEncoder.encodeUserOnBoardDocList(user.getOnBoardDocList()));
		
		//setUser(accPwdScrambler.retrieveAndDecryptPass(user)); //Decrypt password to display at front end
		//setSalt(SaltFactory.getSaltProperty(user));
		setSalt(merchantKeySaltService.getMerchantKeySalt(user.getPayId()).getSalt());
		// set currencies
		currencyMap = currencyMapProvider.currencyMap(user);
		addPaymentMapped(user,currencyMap);
		//set IndustryTypes 
		 Map<String,String>	industryCategoryLinkedMap = industryTypeCategoryProvider.industryTypes(user);
		 
		 if(industryCategoryLinkedMap.containsKey("Default")) {
			industryCategoryLinkedMap.remove("Default");
			}
			
		 industryTypesList.putAll(industryCategoryLinkedMap);
		 if(currencyMap.isEmpty()){
			addFieldError(CrmFieldType.DEFAULT_CURRENCY.getName(),ErrorType.UNMAPPED_CURRENCY_ERROR.getResponseMessage());
		}
		
	}
	
	public User addPaymentMapped(User user , Map<String,String> currencyMap) {
		
		try {
			
			Set<Account> accountSet = null;
			Set<Account> accountSetUpdated = new HashSet <>();
			accountSet = user.getAccounts();
			
			for (Account account : accountSet ) {

				Set<Payment> paymentSet  = account.getPayments();
				AccountCurrency accountCurrency = account.getAccountCurrency(CrmFieldConstants.INR.getValue());
				Set<AccountCurrency> accountCurrencySetUpdated = new HashSet <>();
				
				
				StringBuilder paymentTypeString = new StringBuilder() ;
				
				for (Payment payment : paymentSet) {
					paymentTypeString.append(payment.getPaymentType().getName());
					paymentTypeString.append(" | ");
				}
				accountCurrency.setMappedPaymentTypes(paymentTypeString.toString());
				accountCurrency.setCurrencyName(CrmFieldConstants.INR.toString());
				accountCurrencySetUpdated.add(accountCurrency);
				account.setAccountCurrencySet(accountCurrencySetUpdated);
				accountSetUpdated.add(account);
				
			}
			
			user.setAccounts(accountSetUpdated);
			return user;
		}
		
		catch(Exception e) {
			logger.error("Exception "+e);
		}
		return user;
		
		
		
	}
	
	public void validator() {
	if ((validator.validateBlankField(getSalt()))) {
	} else if (!(validator.validateField(CrmFieldType.SALT_KEY,
			getSalt()))) {
		addFieldError(CrmFieldType.SALT_KEY.getName(), validator
				.getResonseObject().getResponseMessage());
	}
	}
	//to provide default country
	public String getDefaultCountry(){
		if(StringUtils.isBlank(user.getCountry())){
			return BinCountryMapperType.INDIA.getName();
		}else{
			return user.getCountry();
		}
	}

	//to provide default State value
	public String getDefaultState(){
		if(StringUtils.isBlank(user.getState())){
			return States.SELECT_STATE.getName();
		}else{
			return States.getStatesNames().contains(user.getState()) ? user.getState() : States.SELECT_STATE.getName();
		}
	}
	
	//to provide default Operation State value
	public String getDefaultOperationState(){
		if(StringUtils.isBlank(user.getOperationState())){
			return States.SELECT_STATE.getName();
		}else{
			return States.getStatesNames().contains(user.getOperationState()) ? user.getOperationState() : States.SELECT_STATE.getName();
		}
	}
	
	public String  validatePrimary(){
		Set<Account> accounts = null;
		accounts = user.getAccounts();
		for (Account account : accounts) {
			if(account.isPrimaryStatus()) {
				setIsFlag(true);
				return SUCCESS;
			}
		}
		setIsFlag(false);
		return SUCCESS;
	}

	@Override
	public void  validate(){
		if(!validator.validateField(CrmFieldType.PAY_ID, getPayId())){
			addFieldError(CrmFieldType.PAY_ID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	@Override
	public User getModel() {
		return null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Boolean getIsFlag() {
		return isFlag;
	}

	public void setIsFlag(Boolean isFlag) {
		this.isFlag = isFlag;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public Map<String, String> getIndustryTypesList() {
		return industryTypesList;
	}

	public void setIndustryTypesList(Map<String, String> industryTypesList) {
		this.industryTypesList = industryTypesList;
	}

}
