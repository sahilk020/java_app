package com.pay10.crm.action;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.BinCountryMapperType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.States;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.UserStatusType;
import com.pay10.crm.chargeback_new.util.ChargebackUtilities;

/**
 * @ Rajendra
 */

public class EditTenantSaveAction  extends AbstractSecureAction {

	@Autowired
	private CrmValidator validator;

	private static final long serialVersionUID = 1776920255101097001L;
	private static Logger logger = LoggerFactory.getLogger(EditTenantSaveAction.class.getName());
	
	private User user = new User();
	
	private String emailId;	
	private String companyName;
	private String tanNumber;
	private String mobile;
	private String telephoneNo;
	private String fax;
	private String address;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String hsnSacCode;
	private String cin;
	private String companyGstNo;
	
	// Company Bank details
	private String bankName;
	private String ifscCode;
	private String accHolderName;
	private String currency;
	private String branchName;
	private String panCard;
	private String panName;

	private String tenantStatus;
	private String accountNo;
	private String tenantNumber;
	private String companyUrl;
	private String pgUrl;
	
	// for Acquirer details saving 
	private String acquirerString;
	
	// Director details saving
	private String directorname;
	private String directorAadharNumber;
	private String directorpan;
	private String directorMobileNumber;
	private String directorEmail;
	
	private String response;
	
	private CompanyProfile companyProfile = new CompanyProfile();
	
	private boolean fileuploadcomplete = false;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	private CompanyProfileDao companyProfileDao;
	
	private String responseMsg;
	@Override
	public String execute() {
		try {
	
		user = (User) sessionMap.get(Constants.USER.getValue());
		
		companyProfile = companyProfileDao.getCompanyProfileByEmailId(getEmailId());
		
		companyProfile = getCompanyProfileInstance(companyProfile);
		companyProfileDao.update(companyProfile);
		
		addActionMessage(CrmFieldConstants.DETAILS_TENANT_UPDATED_SUCCESSFULLY.getValue());
		setResponse(CrmFieldConstants.DETAILS_TENANT_UPDATED_SUCCESSFULLY.getValue());
		
		}catch(Exception e) {
			addActionMessage(CrmFieldConstants.DETAILS_TENANT_FAILED.getValue());
			setResponse(CrmFieldConstants.DETAILS_TENANT_FAILED.getValue());
		}
		return SUCCESS;
	}
	
	private CompanyProfile getCompanyProfileInstance(CompanyProfile companyProfile) {
		
		companyProfile.setEmailId(getEmailId());
		companyProfile.setCompanyName(getCompanyName());
		companyProfile.setMobile(getMobile());
		companyProfile.setTelephoneNo(getTelephoneNo());
		companyProfile.setFax(getFax());
		companyProfile.setAddress(getAddress());
		companyProfile.setCity(getCity());
		companyProfile.setState(getState());
		companyProfile.setCountry(getCountry());
		companyProfile.setPostalCode(getPostalCode());
		companyProfile.setHsnSacCode(getHsnSacCode());
		companyProfile.setCin(getCin());
		companyProfile.setCompanyGstNo(getCompanyGstNo());
		companyProfile.setBankName(getBankName());
		companyProfile.setIfscCode(getIfscCode());
		companyProfile.setAccHolderName(getAccHolderName());
		companyProfile.setCurrency(getCurrency());
		companyProfile.setBranchName(getBranchName());
		companyProfile.setPanCard(getPanCard());
		companyProfile.setPanName(getPanName());
		companyProfile.setAccountNo(getAccountNo());
		companyProfile.setTanNumber(getTanNumber());		
		companyProfile.setPgUrl(getPgUrl());
		companyProfile.setCompanyUrl(getCompanyUrl());
		companyProfile.setTenantNumber(getTenantNumber());
		companyProfile.setTenantStatus(getTenantStatus());
		
		companyProfile.setAcquirerString(getAcquirerString());
		// director details
		companyProfile.setDirectorname(getDirectorname());
		companyProfile.setDirectorAadharNumber(getDirectorAadharNumber());
		companyProfile.setDirectorpan(getDirectorpan());
		companyProfile.setDirectorMobileNumber(getDirectorMobileNumber());
		companyProfile.setDirectorEmail(getDirectorEmail());
		
		return companyProfile;
	}
	
	//to provide default country
	public String getDefaultCountry(){
		if(StringUtils.isBlank(companyProfile.getCountry())){
			return BinCountryMapperType.INDIA.getName();
		}else{
			return companyProfile.getCountry();
		}
	}

	//to provide default State value
	public String getDefaultState(){
		if(StringUtils.isBlank(companyProfile.getState())){
			return States.SELECT_STATE.getName();
		}else{
			return States.getStatesNames().contains(companyProfile.getState().toString()) ? companyProfile.getState().toString() : States.SELECT_STATE.getName();
		}
	}


	@Override
	public void validate() {

		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		
		if (getMobile() == null ) {

		}

		else {
			if ((validator.validateBlankField(getCompanyName()))) {
				addFieldError(CrmFieldType.COMPANY_NAME.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.COMPANY_NAME, getCompanyName()))) {
				addFieldError(CrmFieldType.COMPANY_NAME.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getTanNumber()))) {
				addFieldError(CrmFieldType.TAN.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.TAN, getTanNumber()))) {
				addFieldError(CrmFieldType.TAN.getName(), validator.getResonseObject().getResponseMessage());
			}
			
			if (validator.validateBlankField(getMobile())) {
				addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.MOBILE, getMobile()))) {
				addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
			}
			if (validator.validateBlankField(getEmailId())) {
				addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.isValidEmailId(getEmailId()))) {
				addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			}
			if ((validator.validateBlankField(getTelephoneNo()))) {

			} else if (!(validator.validateField(CrmFieldType.TELEPHONE_NO, getTelephoneNo()))) {
				addFieldError(CrmFieldType.TELEPHONE_NO.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getAddress()))) {
				addFieldError(CrmFieldType.ADDRESS.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.ADDRESS, getAddress()))) {
				addFieldError(CrmFieldType.ADDRESS.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getCity()))) {
				addFieldError(CrmFieldType.CITY.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.CITY, getCity()))) {
				addFieldError(CrmFieldType.CITY.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getState()))) {
				addFieldError(CrmFieldType.STATE.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.STATE, getState()))) {
				addFieldError(CrmFieldType.STATE.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getCountry()))) {
			} else if (!(validator.validateField(CrmFieldType.COUNTRY, getCountry()))) {
				addFieldError(CrmFieldType.COUNTRY.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getPostalCode()))) {
				addFieldError(CrmFieldType.POSTALCODE.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.POSTALCODE, getPostalCode()))) {
				addFieldError(CrmFieldType.POSTALCODE.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getBankName()))) {
				addFieldError(CrmFieldType.BANK_NAME.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.BANK_NAME, getBankName()))) {
				addFieldError(CrmFieldType.BANK_NAME.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getIfscCode()))) {
				addFieldError(CrmFieldType.IFSC_CODE.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.IFSC_CODE, getIfscCode()))) {
				addFieldError(CrmFieldType.IFSC_CODE.getName(), ErrorType.IFSC_CODE.getInternalMessage());
			}
			if ((validator.validateBlankField(getAccHolderName()))) {
				addFieldError(CrmFieldType.ACC_HOLDER_NAME.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.ACC_HOLDER_NAME, getAccHolderName()))) {
				addFieldError(CrmFieldType.ACC_HOLDER_NAME.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getCurrency()))) {

			} else if (!(validator.validateField(CrmFieldType.CURRENCY, getCurrency()))) {
				addFieldError(CrmFieldType.CURRENCY.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getBranchName()))) {
				addFieldError(CrmFieldType.BRANCH_NAME.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.BRANCH_NAME, getBranchName()))) {
				addFieldError(CrmFieldType.BRANCH_NAME.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getPanCard()))) {
				addFieldError(CrmFieldType.PANCARD.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.PANCARD, getPanCard()))) {
				addFieldError(CrmFieldType.PANCARD.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getAccountNo()))) {
				addFieldError(CrmFieldType.ACCOUNT_NO.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.ACCOUNT_NO, getAccountNo()))) {
				addFieldError(CrmFieldType.ACCOUNT_NO.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getCompanyGstNo()))) {
				addFieldError(CrmFieldType.COMPANY_GST_NUMBER.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.COMPANY_GST_NUMBER, getCity()))) {
				addFieldError(CrmFieldType.COMPANY_GST_NUMBER.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getState()))) {
			} else if (!(validator.validateField(CrmFieldType.STATE, getState()))) {
				addFieldError(CrmFieldType.STATE.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getCin()))) {
				addFieldError(CrmFieldType.CIN.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.CIN, getCin()))) {
				addFieldError(CrmFieldType.CIN.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getPanName()))) {
			} else if (!(validator.validateField(CrmFieldType.PANNAME, getPanName()))) {
				addFieldError(CrmFieldType.PANNAME.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(getFax()))) {
			} else if (!(validator.validateField(CrmFieldType.FAX, getFax()))) {
				addFieldError(CrmFieldType.FAX.getName(), validator.getResonseObject().getResponseMessage());
			}

			if ((validator.validateBlankField(getTenantNumber()))) {
			} else if (!(validator.validateField(CrmFieldType.TENANT_NUMBER, getTenantNumber()))) {
				addFieldError(CrmFieldType.TENANT_NUMBER.getName(), validator.getResonseObject().getResponseMessage());
			}
			
			if ((validator.validateBlankField(getPgUrl()))) {
			} else if (!(validator.validateField(CrmFieldType.PG_URL, getPgUrl()))) {
				addFieldError(CrmFieldType.PG_URL.getName(), validator.getResonseObject().getResponseMessage());
			}
			
			if ((validator.validateBlankField(getCompanyUrl()))) {
			} else if (!(validator.validateField(CrmFieldType.COMPANY_URL, getCompanyUrl()))) {
				addFieldError(CrmFieldType.COMPANY_URL.getName(), validator.getResonseObject().getResponseMessage());
			}
			
		}

	}
	
	
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTanNumber() {
		return tanNumber;
	}

	public void setTanNumber(String tanNumber) {
		this.tanNumber = tanNumber;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelephoneNo() {
		return telephoneNo;
	}

	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getHsnSacCode() {
		return hsnSacCode;
	}

	public void setHsnSacCode(String hsnSacCode) {
		this.hsnSacCode = hsnSacCode;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public String getCompanyGstNo() {
		return companyGstNo;
	}

	public void setCompanyGstNo(String companyGstNo) {
		this.companyGstNo = companyGstNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getAccHolderName() {
		return accHolderName;
	}

	public void setAccHolderName(String accHolderName) {
		this.accHolderName = accHolderName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getPanCard() {
		return panCard;
	}

	public void setPanCard(String panCard) {
		this.panCard = panCard;
	}

	public String getPanName() {
		return panName;
	}

	public void setPanName(String panName) {
		this.panName = panName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getTenantNumber() {
		return tenantNumber;
	}

	public void setTenantNumber(String tenantNumber) {
		this.tenantNumber = tenantNumber;
	}

	public String getCompanyUrl() {
		return companyUrl;
	}

	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}

	public String getPgUrl() {
		return pgUrl;
	}

	public void setPgUrl(String pgUrl) {
		this.pgUrl = pgUrl;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public CompanyProfile getCompanyProfile() {
		return companyProfile;
	}

	public void setCompanyProfile(CompanyProfile companyProfile) {
		this.companyProfile = companyProfile;
	}

	public CompanyProfileDao getCompanyProfileDao() {
		return companyProfileDao;
	}

	public void setCompanyProfileDao(CompanyProfileDao companyProfileDao) {
		this.companyProfileDao = companyProfileDao;
	}

	public String getTenantStatus() {
		return tenantStatus;
	}

	public void setTenantStatus(String tenantStatus) {
		this.tenantStatus = tenantStatus;
	}

	public String getAcquirerString() {
		return acquirerString;
	}

	public void setAcquirerString(String acquirerString) {
		this.acquirerString = acquirerString;
	}

	public String getDirectorname() {
		return directorname;
	}

	public void setDirectorname(String directorname) {
		this.directorname = directorname;
	}

	public String getDirectorAadharNumber() {
		return directorAadharNumber;
	}

	public void setDirectorAadharNumber(String directorAadharNumber) {
		this.directorAadharNumber = directorAadharNumber;
	}

	public String getDirectorpan() {
		return directorpan;
	}

	public void setDirectorpan(String directorpan) {
		this.directorpan = directorpan;
	}

	public String getDirectorMobileNumber() {
		return directorMobileNumber;
	}

	public void setDirectorMobileNumber(String directorMobileNumber) {
		this.directorMobileNumber = directorMobileNumber;
	}

	public String getDirectorEmail() {
		return directorEmail;
	}

	public void setDirectorEmail(String directorEmail) {
		this.directorEmail = directorEmail;
	}

	
}
