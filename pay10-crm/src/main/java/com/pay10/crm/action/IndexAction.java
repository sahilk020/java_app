package com.pay10.crm.action;

import java.util.*;

import com.pay10.commons.entity.CurrencyCode;
import com.pay10.commons.user.*;
import com.pay10.commons.util.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pay10.crm.actionBeans.CurrencyMapProvider;

@Service
public class IndexAction extends ForwardAction {

	@Autowired
	private CurrencyMapProvider currencyMapProvider;

	@Autowired
	private UserDao userDao;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	@Autowired
	private CrmValidator validator;

	private static final long serialVersionUID = -4616437586910475430L;

	private static Logger logger = LoggerFactory.getLogger(IndexAction.class.getName());
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private List<paymenttypedto> paymentList = new ArrayList<paymenttypedto>();
	private List<MopTypeDto> moplist = new ArrayList<MopTypeDto>();

	
	private Map<String, String> currencyMap = new LinkedHashMap<String, String>();
	private List<MultCurrencyCode> currencyCodeList = new ArrayList<>();
	private String userDefaultCurrency;
	private User user = new User();
	private String permissionString = "";
	private String reselleId;
	private String merchantemail;
	private String payment;

	@SuppressWarnings("unchecked")
	public String authoriseUser() {
		try {
			logger.info("Index Action Home");
			user = (User) sessionMap.get(Constants.USER.getValue());
			setCurrencyMap(currencyMapProvider.currencyMap(user));
			setUserDefaultCurrency(StringUtils.isNotEmpty(user.getDefaultCurrency())?user.getDefaultCurrency(): "356");
			addMultCurrencyCodes();
			logger.info("User type in index action: "+user.getUserType());
			if (user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN)
					|| user.getUserType().equals(UserType.SUPERADMIN)
					|| user.getUserType().equals(UserType.SUBSUPERADMIN)
					) {
			/*	if (user.getUserType().equals(UserType.RESELLER)) {
					merchantList = userDao.getActiveResellerMerchantList(user.getResellerId());
				} else {*/
					logger.info("User is not reseller");
					//merchantList = userDao.getMerchantActiveList();
					merchantList = userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), user.getRole().getId());
				//}
			}
			/*else if (user.getUserType().equals(UserType.MERCHANT)) {
				logger.info("User is merchant "+user.getUserType());
				Merchants merchant = new Merchants();
				user.setOnBoardDocListString(DataEncoder.encodeUserOnBoardDocList(user.getOnBoardDocList()));
				merchant.setMerchant(user);
				merchantList.add(merchant);
				List<String> paymentlist = userDao.getPaymentType(user.getPayId());
				for (String payList : paymentlist)
				{
					logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+payList);
					PaymentTypeUI payvalue =PaymentTypeUI.getInstanceUsingStringValue(payList);
					paymenttypedto paymenttype=new paymenttypedto();
					paymenttype.setCode(payvalue.getCode());
					paymenttype.setName(payvalue.getName());
					paymentList.add(paymenttype);
				}
				paymentList.sort(Comparator.comparing(paymenttypedto::getName));
				logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+paymentList);
			}*/

			if (user.getUserType().equals(UserType.ADMIN)) {
				logger.info("For Admin return "+CrmFieldConstants.ADMIN.getValue());
				return CrmFieldConstants.ADMIN.getValue();
			}
			/*else if(user.getUserType().equals(UserType.RESELLER)){
				return  CrmFieldConstants.USER_RESELLER_TYPE.getValue();
			}*/
           else if (user.getUserType().equals(UserType.SUPERADMIN)) {
				return CrmFieldConstants.SUPERADMIN.getValue();
			} else if (user.getUserType().equals(UserType.SUBSUPERADMIN)) {
				return CrmFieldConstants.SUBSUPERADMIN.getValue();
			}
		   /*
		   else if (user.getUserType().equals(UserType.MERCHANT)
					|| user.getUserType().equals(UserType.POSMERCHANT)) {
				if (user.getUserStatus().equals(UserStatusType.SUSPENDED)) {
					return CrmFieldConstants.NEW_USER.getValue();
				} else {
					setUserDefaultCurrency(StringUtils.isNotEmpty(user.getDefaultCurrency())?user.getDefaultCurrency(): "356");
					return CrmFieldConstants.MERCHANT.getValue();
				}
			} else if (user.getUserType().equals(UserType.SUBUSER)) {
				Set<Roles> roles = user.getRoles();
				
				Set<Permissions> permissions = roles.iterator().next().getPermissions();

				List<String> paymentlist = userDao.getPaymentType(user.getParentPayId());
				for (String payList : paymentlist)
				{
					logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+payList);
					PaymentTypeUI payvalue =PaymentTypeUI.getInstanceUsingStringValue(payList);
					paymenttypedto paymenttype=new paymenttypedto();
					paymenttype.setCode(payvalue.getCode());
					paymenttype.setName(payvalue.getName());
					paymentList.add(paymenttype);
				}
				paymentList.sort(Comparator.comparing(paymenttypedto::getName));


				System.out.println("permissions"  + new Gson().toJson(permissions));
				return CrmFieldConstants.MERCHANT.getValue();
//				if (!permissions.isEmpty()) {
//					Iterator<Permissions> itr = permissions.iterator();
//					while (itr.hasNext()) {
//						PermissionType permissionType = itr.next().getPermissionType();
//						if (permissionType.getPermission().equalsIgnoreCase("View Dashboard")) {
//							Merchants merchant = new Merchants();
//							String parentMerchantPayId = user.getParentPayId();
//							User parentMerchant = userDao.findPayId(parentMerchantPayId);
//							merchant.setMerchant(parentMerchant);
//							merchantList.add(merchant);
//							return CrmFieldConstants.SUBUSER_DASHBOARD.getValue();
//						}
//					}
//					Iterator<Permissions> itr2 = permissions.iterator();
//					while (itr2.hasNext()) {
//						PermissionType permissionType = itr2.next().getPermissionType();
//						if (permissionType.getPermission().equalsIgnoreCase("Search Payment")) {
//							return CrmFieldConstants.SUBUSER.getValue();
//						}
//
//					}
//					Iterator<Permissions> itr3 = permissions.iterator();
//					while (itr3.hasNext()) {
//						PermissionType permissionType = itr3.next().getPermissionType();
//						if (permissionType.getPermission().equalsIgnoreCase("Agent Search")) {
//							return "agentSearchPermission";
//						}
//
//					}
//					Iterator<Permissions> itr4 = permissions.iterator();
//					while (itr4.hasNext()) {
//						PermissionType permissionType = itr4.next().getPermissionType();
//						if (permissionType.getPermission().equalsIgnoreCase("View Transaction Reports")) {
//							return "saleTransactionSearchPermission";
//						} else if (permissionType.getPermission().equalsIgnoreCase("View Invoice")) {
//							return "singleInvoicePagePermission";
//						} else if (permissionType.getPermission().equalsIgnoreCase("View ChargeBack")) {
//							return "viewChargebackPermission";
//						}
//					}
//				}
//				return CrmFieldConstants.SUBUSER.getValue();
			} else if (user.getUserType().equals(UserType.ACQUIRER)) {
				return CrmFieldConstants.ACQUIRER.getValue();
			} else if (user.getUserType().equals(UserType.SUBACQUIRER)) {
				return CrmFieldConstants.ACQUIRER_SUBUSER.getValue();
			} */
			else if (user.getUserType().equals(UserType.SUBADMIN)) {
				String group = user.getUserGroup() != null ? user.getUserGroup().getGroup() : null;
				if (StringUtils.equalsIgnoreCase(group, CrmFieldConstants.OPERATIONS.getValue())) {
					return CrmFieldConstants.OPS_ADMIN.getValue();
				} else if (StringUtils.equalsIgnoreCase(group, CrmFieldConstants.SALES.getValue())) {
					return CrmFieldConstants.SALES_ADMIN.getValue();
				} else if (StringUtils.equalsIgnoreCase(group, CrmFieldConstants.RISK.getValue())) {
					return CrmFieldConstants.RISK_ADMIN.getValue();
				}
				return CrmFieldConstants.SUBADMIN.getValue();
			} /*else if (user.getUserType().equals(UserType.AGENT)) {
				return CrmFieldConstants.AGENT.getValue();
			}*/
			/*else if(user.getUserType().equals(UserType.SMA)){
			    return  CrmFieldConstants.USER_SMA_TYPE.getValue();
			}
			else if(user.getUserType().equals(UserType.MA)){ 
			    return  CrmFieldConstants.USER_MA_TYPE.getValue();
			}
		
				else if(user.getUserType().equals(UserType.Agent)){
				System.out.println(CrmFieldConstants.USER_AGENT_TYPE.getValue());
			    return  CrmFieldConstants.USER_AGENT_TYPE.getValue();				 
			}*/
			logger.info("usertype......={}",user.getUserType());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		logger.info("Unmapped user return from home action to login");
		return LOGIN; // unmapped user
	}

	@Override
	public void validate() {


		logger.info("Index Action Home Validation");
		if ((validator.validateBlankField(getReselleId()))) {
			/*
			 * addFieldError(CrmFieldType.RESELLER_ID.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.RESELLER_ID, getReselleId()))) {
			addFieldError(CrmFieldType.RESELLER_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
	}

	// to provide default State value
	public String getDefaultState() {
		if (StringUtils.isBlank(user.getState())) {
			return States.SELECT_STATE.getName();
		} else {
			return States.getStatesNames().contains(user.getState().toString()) ? user.getState().toString()
					: States.SELECT_STATE.getName();
		}
	}

	// to provide default Operation State value
	public String getDefaultOperationState() {
		if (StringUtils.isBlank(user.getOperationState())) {
			return States.SELECT_STATE.getName();
		} else {
			return States.getStatesNames().contains(user.getOperationState().toString())
					? user.getOperationState().toString()
					: States.SELECT_STATE.getName();
		}
	}

	// to provide default country
	public String getDefaultCountry() {
		if (StringUtils.isBlank(user.getCountry())) {
			return BinCountryMapperType.INDIA.getName();
		} else {
			return user.getCountry();
		}
	}

	public void addMultCurrencyCodes(){
		for(String code : getCurrencyMap().keySet()){
			getCurrencyCodeList().add(multCurrencyCodeDao.findByCode(code));
		}
	}

	@Override
	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	@Override
	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	@Override
	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	@Override
	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPermissionString() {
		return permissionString;
	}

	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
	}

	public String getReselleId() {
		return reselleId;
	}

	public void setReselleId(String reselleId) {
		this.reselleId = reselleId;
	}

	public List<paymenttypedto> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<paymenttypedto> paymentList) {
		this.paymentList = paymentList;
	}

	public String getMerchantemail() {
		return merchantemail;
	}

	public void setMerchantemail(String merchantemail) {
		this.merchantemail = merchantemail;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getUserDefaultCurrency() {
		return this.userDefaultCurrency;
	}
	public void setUserDefaultCurrency(String userDefaultCurrency) {
		this.userDefaultCurrency = userDefaultCurrency;
	}
	public List<MultCurrencyCode> getCurrencyCodeList() {
		return currencyCodeList;
	}
	public void setCurrencyCodeList(List<MultCurrencyCode> currencyCodeList) {
		this.currencyCodeList = currencyCodeList;
	}
}
