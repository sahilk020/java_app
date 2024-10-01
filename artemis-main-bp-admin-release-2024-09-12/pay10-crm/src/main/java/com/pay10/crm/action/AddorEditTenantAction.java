package com.pay10.crm.action;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.PermissionType;
import com.pay10.commons.user.Permissions;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.BinCountryMapperType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.States;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.UserStatusType;
import com.pay10.crm.actionBeans.CheckExistingUser;
import com.pay10.crm.actionBeans.CreateSubAdmin;

/**
 * @ Rajendra
 */

public class AddorEditTenantAction  extends AbstractSecureAction {

	@Autowired
	private CrmValidator validator;

	@Autowired
	private CreateSubAdmin createSubAdmin;

	private static final long serialVersionUID = -3762555003309088094L;

	private static Logger logger = LoggerFactory.getLogger(AddorEditTenantAction.class.getName());


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


	private String accountNo;
	
	private String tenantNumber;
	private String companyUrl;
	private String pgUrl;
	
	private String response;
	
	private User user = new User();
	private CompanyProfile companyProfile = new CompanyProfile();
	private ResponseObject responseObject;

	@Autowired
	private CompanyProfileDao companyProfileDao;
	
	@Override
	public String execute() {
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());

			if (getMobile() == null && getEmailId() == null) {

			} else {
				setEmailId(getEmailId().toLowerCase());
				logger.info("Create Company Profile");
				CheckExistingUser checkExistingUser = new CheckExistingUser();
				responseObject = checkExistingUser.checkTenant(getEmailId());
				if(ErrorType.TENANT_UNAVAILABLE.getResponseCode().equals(responseObject.getResponseCode())) {
					responseObject = createSubAdmin.createNewCompanyProfile(getCompanyProfileInstance());				
					if (!ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
						addActionMessage(responseObject.getResponseMessage());
						setResponse(responseObject.getResponseMessage());
					}
					// Sending Email for Email Validation
					logger.info("Create Tenent Successfully");
					//emailControllerServiceProvider.addUser(responseObject, getFirstName());
					addActionMessage(CrmFieldConstants.DETAILS_TENANT_SUCCESSFULLY.getValue());
					setResponse(CrmFieldConstants.DETAILS_TENANT_SUCCESSFULLY.getValue());					
				}else {
					logger.info("Tenent exist with same email ID ");
					//emailControllerServiceProvider.addUser(responseObject, getFirstName());
					addActionMessage(ErrorType.TENANT_AVAILABLE.getInternalMessage());
					setResponse(ErrorType.TENANT_AVAILABLE.getInternalMessage());
				}		

				
			}
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			addActionMessage(CrmFieldConstants.DETAILS_TENANT_FAILED.getValue());
			setResponse(CrmFieldConstants.DETAILS_TENANT_FAILED.getValue());
			return ERROR;
		}
	}

	private CompanyProfile getCompanyProfileInstance() {
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
		companyProfile.setTenantStatus(UserStatusType.PENDING.getStatus());
		// generating the TenantId
		companyProfile.setTenantId(Long.parseLong(TransactionManager.getNewTransactionId()));
		return companyProfile;
	}

	public String editTenant() {

		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());		
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN)) {
				companyProfile = companyProfileDao.getCompanyProfileByEmailId(getEmailId());
				setCompanyProfile(companyProfile);
				getCompanyProfile();
			}
						
			return INPUT;
		} catch (Exception exception) {
			logger.error("Edit Sub super admin Exception", exception);
			return ERROR;
		}
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

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
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
	
	// Bank details getter and setter methods 
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


	public CompanyProfile getCompanyProfile() {
		return companyProfile;
	}

	public void setCompanyProfile(CompanyProfile companyProfile) {
		this.companyProfile = companyProfile;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public ResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(ResponseObject responseObject) {
		this.responseObject = responseObject;
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
	
}
