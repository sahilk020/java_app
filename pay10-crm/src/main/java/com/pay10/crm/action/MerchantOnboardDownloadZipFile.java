package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
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

public class MerchantOnboardDownloadZipFile extends AbstractSecureAction implements ModelDriven<User> {

	private static final long serialVersionUID = -2105553344789768823L;
	private static Logger logger = LoggerFactory.getLogger(MerchantOnboardDownloadZipFile.class.getName());

	@Autowired 
	MerchantKeySaltService merchantKeySaltService;
	
	private String payId;
	private String fileName;
	private String orgKeyName;

	@Autowired
	UserDao userDao;
	
	private String salt;
	private Boolean isFlag;
	private Map<String, String> currencyMap = new LinkedHashMap<>();
	private Map<String, String> industryTypesList = new TreeMap<>();

	
	@Autowired
	private IndustryTypeCategoryProvider industryTypeCategoryProvider;
	
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private CurrencyMapProvider currencyMapProvider;
	
	@Autowired
	private AccountPasswordScrambler accPwdScrambler;


	private FileInputStream fileInputStream;
	private long contentLength;

	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}

	private User user = new User();

	@Override
	public String execute() {
		setUser(userDao.findPayId(getPayId()));
		String filePath = PropertiesManager.propertiesMap.get(Constants.MERCHANT_ONBOARD_DOC_UPLOAD_PATH.getValue());
		String zipFileName = filePath + getPayId() + "/docs/" + getUser().getBusinessName() + (new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date())) + ".zip";
		
		String oldDocList = "{}";
		if (user.getOnBoardDocList() != null) {
			oldDocList = new String(user.getOnBoardDocList());
		}
		user.setOnBoardDocListString(DataEncoder.encodeUserOnBoardDocList(user.getOnBoardDocList()));

		// create byte buffer
		byte[] buffer = new byte[1024];

		JSONObject jsObj = new JSONObject(oldDocList);
		JSONObject orgValue = jsObj.getJSONObject(orgKeyName);
		try (FileOutputStream fos = new FileOutputStream(zipFileName);
			 ZipOutputStream zos = new ZipOutputStream(fos);) {
			orgValue.keySet().forEach(keyStr -> {
				JSONObject keyValue = orgValue.getJSONObject(keyStr);
				String fileName = keyValue.getString("completefilename");
				String filePaths = PropertiesManager.propertiesMap.get(Constants.MERCHANT_ONBOARD_DOC_UPLOAD_PATH.getValue()) + getPayId() + "/docs/";

				File srcFile = new File(filePaths + fileName);
				try {
					FileInputStream fis = new FileInputStream(srcFile);
					zos.putNextEntry(new ZipEntry(srcFile.getName()));
					int length;

					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					zos.closeEntry();
					fis.close();
				} catch (Exception e) {
					logger.error("Failed to create zip or file doesn't exists");
					addActionMessage("Unable to download file");
					logger.error(e.getMessage());
				}
			});
		}
		catch (Exception e) {
			logger.error("Failed to create zip");
			addActionMessage("Unable to download file");
			logger.error(e.getMessage());
			setData();
			return INPUT;
		}

		File zipFile = new File(zipFileName);
		try {
			fileInputStream = new FileInputStream(zipFile);
			fileName = zipFile.getName();
			contentLength = zipFile.length();
			zipFile.deleteOnExit();
		} catch (FileNotFoundException e) {
			logger.error("Error while downloading file");
			addActionMessage("Unable to download file");
			logger.error(e.getMessage());
			setData();
			return INPUT;
		}
		return SUCCESS;
	}
	
	public void setData() {
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
			Set<Account> accountSetUpdated = new HashSet<>();
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

	public String getOrgKeyName() {
		return orgKeyName;
	}

	public void setOrgKeyName(String orgKeyName) {
		this.orgKeyName = orgKeyName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
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

	@Override
	public User getModel() {
		return null;
	}

}
