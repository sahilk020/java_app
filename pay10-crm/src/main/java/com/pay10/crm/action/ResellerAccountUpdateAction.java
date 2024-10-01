package com.pay10.crm.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.user.CycleDao;
import com.pay10.commons.user.CycleMaster;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.MerchantKeySaltService;
import com.pay10.commons.util.UserStatusType;
import com.pay10.crm.actionBeans.CurrencyMapProvider;
import com.pay10.crm.actionBeans.MerchantRecordUpdater;

public class ResellerAccountUpdateAction extends AbstractSecureAction implements
		ServletRequestAware, ModelDriven<User> {
	
	
	@Autowired
	private CrmValidator crmValidator;
	
	@Autowired
	private CurrencyMapProvider currencyMapProvider;
	
	@Autowired
	private MerchantRecordUpdater merchantRecordUpdater;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AuditTrailService auditTrailService;
	
	@Autowired
	private CycleDao cycledao;
	
	@Autowired 
	MerchantKeySaltService merchantKeySaltService;


	private String rDate = "";

	private String aDate = "";

	private List<CycleMaster> listCycle = new ArrayList<CycleMaster>();

	private static Logger logger = LoggerFactory
			.getLogger(ResellerAccountUpdateAction.class.getName());
	private static final long serialVersionUID = -7165881905141203999L;
	private User user = new User();
	private String salt;
	private HttpServletRequest request;
	private String defaultCurrency;
	private Map<String, String> currencyMap = new LinkedHashMap<String, String>();
	private Boolean tfaFlag;
	
	@SuppressWarnings("unchecked")
	public String saveResellerAction() {
		

		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			logger.info("settlement cycle......" + user.getCycle());
			setListCycle(cycledao.findAll());
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			//setSalt(SaltFactory.getSaltProperty(user));
			setSalt(merchantKeySaltService.getMerchantKeySalt(user.getPayId()).getSalt());
			user.setUserType(UserType.RESELLER);
			logger.info("Tfa Flag: "+ user.isTfaFlag());
//			user.setTfaFlag(getTfaFlag());
			String prevReseller = mapper.writeValueAsString(userDao.findByEmailId(user.getEmailId()));
			if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)) {

				if (user.getUserStatus().toString()
						.equals(UserStatusType.ACTIVE.getStatus().toString())) {
					user.setActivationDate(date);
				} else if (user
						.getUserStatus()
						.toString()
						.equals(UserStatusType.SUSPENDED.getStatus().toString())
						|| user.getUserStatus()
								.toString()
								.equals(UserStatusType.TRANSACTION_BLOCKED
										.getStatus().toString())) {
					user.setActivationDate(null);
					logger.info("Merchant EmailId" + ":" + user.getEmailId()
							+ "," + "Merchant Status" + ":"
							+ user.getUserStatus().getStatus() + ","
							+ "Ip Address" + ":" + request.getRemoteAddr());
				}

				setUser(merchantRecordUpdater.updateResellerDetails(user));
				addActionMessage(CrmFieldConstants.USER_DETAILS_UPDATED
						.getValue());
				currencyMap = currencyMapProvider.currencyMap(user);
				
				Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
						.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
				AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(user), prevReseller,
						actionMessagesByAction.get("resellerSaveAction"));
				auditTrailService.saveAudit(request, auditTrail);

				if(user.getActivationDate()!=null) {
					setaDate(dateFormat.format(user.getActivationDate()));
				}
				if(user.getRegistrationDate()!=null) {
					setrDate(dateFormat.format(user.getRegistrationDate()));
				}
				return INPUT;
			} else {

				setUser(merchantRecordUpdater.updateUserProfile(user));
				sessionMap.put(Constants.USER.getValue(), user);
				
				Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
						.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
				AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(user), prevReseller,
						actionMessagesByAction.get("resellerSaveAction"));
				auditTrailService.saveAudit(request, auditTrail);



				if(user.getActivationDate()!=null) {
					setaDate(dateFormat.format(user.getActivationDate()));
				}
				if(user.getRegistrationDate()!=null) {
					setrDate(dateFormat.format(user.getRegistrationDate()));
				}
				return INPUT;
			}
			
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	


	@Override
  public void validate() {
//		User userDB = new User();
//		userDB = getUser();
//		if ((crmValidator.validateBlankField(user.getFirstName()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.FIRSTNAME,
//				user.getFirstName()))) {
//			addFieldError(CrmFieldType.FIRSTNAME.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getLastName()))) {
//
//		} else if (!(crmValidator.validateField(CrmFieldType.LASTNAME,
//				user.getLastName()))) {
//			addFieldError(CrmFieldType.LASTNAME.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getCompanyName()))) {
//
//		} else if (!(crmValidator.validateField(CrmFieldType.COMPANY_NAME,
//				user.getCompanyName()))) {
//			addFieldError(CrmFieldType.COMPANY_NAME.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		/*if ((crmValidator.validateBlankField(user.getBusinessType()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.BUSINESS_TYPE,
//				user.getBusinessType()))) {
//			addFieldError(CrmFieldType.BUSINESS_TYPE.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}*/
//		if ((crmValidator.validateBlankField(user.getTelephoneNo()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.TELEPHONE_NO,
//				user.getTelephoneNo()))) {
//			addFieldError(CrmFieldType.TELEPHONE_NO.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getAddress()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.ADDRESS,
//				user.getAddress()))) {
//			addFieldError(CrmFieldType.ADDRESS.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getCity()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.CITY,
//				user.getCity()))) {
//			addFieldError(CrmFieldType.CITY.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getState()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.STATE,
//				user.getState()))) {
//			addFieldError(CrmFieldType.STATE.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getCountry()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.COUNTRY,
//				user.getCountry()))) {
//			addFieldError(CrmFieldType.COUNTRY.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getPostalCode()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.POSTALCODE,
//				user.getPostalCode()))) {
//			addFieldError(CrmFieldType.POSTALCODE.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getBankName()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.BANK_NAME,
//				user.getBankName()))) {
//			addFieldError(CrmFieldType.BANK_NAME.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getIfscCode()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.IFSC_CODE,
//				user.getIfscCode()))) {
//			addFieldError(CrmFieldType.IFSC_CODE.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getAccHolderName()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.ACC_HOLDER_NAME,
//				user.getAccHolderName()))) {
//			addFieldError(CrmFieldType.ACC_HOLDER_NAME.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getCurrency()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.CURRENCY,
//				user.getCurrency()))) {
//			addFieldError(CrmFieldType.CURRENCY.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getBranchName()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.BRANCH_NAME,
//				user.getBranchName()))) {
//			addFieldError(CrmFieldType.BRANCH_NAME.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getBusinessName()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.BUSINESS_NAME,
//				user.getBusinessName()))) {
//			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getComments()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.COMMENTS,
//				user.getComments()))) {
//			addFieldError(CrmFieldType.COMMENTS.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getPanCard()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.PANCARD,
//				user.getPanCard()))) {
//			addFieldError(CrmFieldType.PANCARD.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getAccountNo()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.ACCOUNT_NO,
//				user.getAccountNo()))) {
//			addFieldError(CrmFieldType.ACCOUNT_NO.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getWebsite()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.WEBSITE,
//				user.getWebsite()))) {
//			addFieldError(CrmFieldType.WEBSITE.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getOrganisationType()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.ORGANIZATIONTYPE,
//				user.getOrganisationType()))) {
//			addFieldError(CrmFieldType.ORGANIZATIONTYPE.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getMultiCurrency()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.MULTICURRENCY,
//				user.getMultiCurrency()))) {
//			addFieldError(CrmFieldType.MULTICURRENCY.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getBusinessModel()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.BUSINESSMODEL,
//				user.getBusinessModel()))) {
//			addFieldError(CrmFieldType.BUSINESSMODEL.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getOperationAddress()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.OPERATIONADDRESS,
//				user.getOperationAddress()))) {
//			addFieldError(CrmFieldType.OPERATIONADDRESS.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getOperationCity()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.CITY,
//				user.getOperationCity()))) {
//			addFieldError(CrmFieldType.CITY.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getOperationState()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.STATE,
//				user.getOperationState()))) {
//			addFieldError(CrmFieldType.STATE.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getOperationPostalCode()))) {
//		} else if (!(crmValidator.validateField(
//				CrmFieldType.OPERATION_POSTAL_CODE,
//				user.getOperationPostalCode()))) {
//			addFieldError(CrmFieldType.OPERATION_POSTAL_CODE.getName(),
//					crmValidator.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getCin()))) {
//		} else if (!(crmValidator
//				.validateField(CrmFieldType.CIN, user.getCin()))) {
//			addFieldError(CrmFieldType.CIN.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getPan()))) {
//		} else if (!(crmValidator
//				.validateField(CrmFieldType.PAN, user.getPan()))) {
//			addFieldError(CrmFieldType.PAN.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getPanName()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.PANNAME,
//				user.getPanName()))) {
//			addFieldError(CrmFieldType.PANNAME.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getNoOfTransactions()))) {
//		} else if (!(crmValidator.validateField(
//				CrmFieldType.NO_OF_TRANSACTIONS, user.getNoOfTransactions()))) {
//			addFieldError(CrmFieldType.NO_OF_TRANSACTIONS.getName(),
//					crmValidator.getResonseObject().getResponseMessage());
//		}
//
//		if ((crmValidator.validateBlankField(user.getAmountOfTransactions()))) {
//		} else if (!(crmValidator.validateField(
//				CrmFieldType.AMOUNT_OF_TRANSACTIONS,
//				user.getAmountOfTransactions()))) {
//			addFieldError(CrmFieldType.AMOUNT_OF_TRANSACTIONS.getName(),
//					crmValidator.getResonseObject().getResponseMessage());
//		}
//
//		if ((crmValidator.validateBlankField(user.getDateOfEstablishment()))) {
//		} else if (!(crmValidator.validateField(
//				CrmFieldType.DATE_OF_ESTABLISHMENT,
//				user.getDateOfEstablishment()))) {
//			addFieldError(CrmFieldType.DATE_OF_ESTABLISHMENT.getName(),
//					crmValidator.getResonseObject().getResponseMessage());
//		}
//
//		if ((crmValidator.validateBlankField(user.getAccountValidationKey()))) {
//		} else if (!(crmValidator.validateField(
//				CrmFieldType.ACCOUNT_VALIDATION_KEY,
//				user.getAccountValidationKey()))) {
//			addFieldError(CrmFieldType.ACCOUNT_VALIDATION_KEY.getName(),
//					crmValidator.getResonseObject().getResponseMessage());
//		}
//
//		if ((crmValidator.validateBlankField(user.getUploadePhoto()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.UPLOADE_PHOTO,
//				user.getUploadePhoto()))) {
//			addFieldError(CrmFieldType.UPLOADE_PHOTO.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getUploadedPanCard()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.UPLOADE_PAN_CARD,
//				user.getUploadedPanCard()))) {
//			addFieldError(CrmFieldType.UPLOADE_PAN_CARD.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getUploadedPhotoIdProof()))) {
//		} else if (!(crmValidator.validateField(
//				CrmFieldType.UPLOADE_PHOTOID_PROOF,
//				user.getUploadedPhotoIdProof()))) {
//			addFieldError(CrmFieldType.UPLOADE_PHOTOID_PROOF.getName(),
//					crmValidator.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator
//				.validateBlankField(user.getUploadedContractDocument()))) {
//		} else if (!(crmValidator.validateField(
//				CrmFieldType.UPLOADE_CONTRACT_DOCUMENT,
//				user.getUploadedContractDocument()))) {
//			addFieldError(CrmFieldType.UPLOADE_CONTRACT_DOCUMENT.getName(),
//					crmValidator.getResonseObject().getResponseMessage());
//		}
//		if (crmValidator.validateBlankField(user.getTransactionEmailId())) {
//		} else if (!(crmValidator.isValidEmailId(user.getTransactionEmailId()))) {
//			addFieldError(CrmFieldType.TRANSACTION_EMAIL_ID.getName(),
//					crmValidator.getResonseObject().getResponseMessage());
//		}
//
//		if (crmValidator.validateBlankField(user.getEmailId())) {
//			addFieldError(CrmFieldType.EMAILID.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		} else if (!(crmValidator.isValidEmailId(user.getEmailId()))) {
//			addFieldError(CrmFieldType.EMAILID.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//
//		if ((crmValidator.validateBlankField(user.getContactPerson()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.CONTACT_PERSON,
//				user.getContactPerson()))) {
//			addFieldError(CrmFieldType.CONTACT_PERSON.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getPayId()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.PAY_ID,
//				user.getPayId()))) {
//			addFieldError(CrmFieldType.PAY_ID.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getPassword()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.PASSWORD,
//				user.getPassword()))) {
//			addFieldError(CrmFieldType.PASSWORD.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//
//		if ((crmValidator.validateBlankField(user.getParentPayId()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.PARENT_PAY_ID,
//				user.getParentPayId()))) {
//			addFieldError(CrmFieldType.PARENT_PAY_ID.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//
//		if ((crmValidator.validateBlankField(user.getWhiteListIpAddress()))) {
//		} else if (!(crmValidator.validateField(
//				CrmFieldType.WHITE_LIST_IPADDRES, user.getWhiteListIpAddress()))) {
//			addFieldError(CrmFieldType.WHITE_LIST_IPADDRES.getName(),
//					crmValidator.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getFax()))) {
//		} else if (!(crmValidator
//				.validateField(CrmFieldType.FAX, user.getFax()))) {
//			addFieldError(CrmFieldType.FAX.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getMobile()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.MOBILE,
//				user.getMobile()))) {
//			addFieldError(CrmFieldType.MOBILE.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getProductDetail()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.PRODUCT_DETAIL,
//				user.getProductDetail()))) {
//			addFieldError(CrmFieldType.PRODUCT_DETAIL.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getResellerId()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.RESELLER_ID,
//				user.getResellerId()))) {
//			addFieldError(CrmFieldType.RESELLER_ID.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getMerchantType()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.MERCHANT_TYPE,
//				user.getMerchantType()))) {
//			addFieldError(CrmFieldType.MERCHANT_TYPE.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if ((crmValidator.validateBlankField(user.getBranchName()))) {
//		} else if (!(crmValidator.validateField(CrmFieldType.BRANCH_NAME,
//				user.getBranchName()))) {
//			addFieldError(CrmFieldType.BRANCH_NAME.getName(), crmValidator
//					.getResonseObject().getResponseMessage());
//		}
//		if (!getFieldErrors().isEmpty()) {
//			user.setUserType(UserType.MERCHANT);
//			currencyMap = currencyMapProvider.currencyMap(user);
//			
//		}
//	
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public User getModel() {
		return user;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}
	public String getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public CycleDao getCycledao() {
		return cycledao;
	}

	public void setCycledao(CycleDao cycledao) {
		this.cycledao = cycledao;
	}

	public List<CycleMaster> getListCycle() {
		return listCycle;
	}

	public Boolean getTfaFlag() {
		return tfaFlag;
	}

	public void setTfaFlag(Boolean tfaFlag) {
		this.tfaFlag = tfaFlag;
	}

	public void setListCycle(List<CycleMaster> listCycle) {
		this.listCycle = listCycle;
	}


	public String getrDate() {
		return rDate;
	}

	public void setrDate(String rDate) {
		this.rDate = rDate;
	}

	public String getaDate() {
		return aDate;
	}

	public void setaDate(String aDate) {
		this.aDate = aDate;
	}
}