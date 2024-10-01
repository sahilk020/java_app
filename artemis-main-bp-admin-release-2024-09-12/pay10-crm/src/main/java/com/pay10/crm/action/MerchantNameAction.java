package com.pay10.crm.action;

import com.pay10.commons.dao.ReportColumnDetailDao;
import com.pay10.commons.user.*;
import com.pay10.commons.util.*;
import com.pay10.crm.actionBeans.CurrencyMapProvider;
import com.pay10.crm.util.SortingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chandan
 *
 */
public class MerchantNameAction extends AbstractSecureAction {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
     private	MultCurrencyCodeDao currencyCodeDao;
	
	@Autowired
	private CurrencyMapProvider currencyMapProvider;
	

	private static Logger logger = LoggerFactory.getLogger(MerchantNameAction.class.getName());
	private static final long serialVersionUID = -5990800125330748024L;
	
	private List<CompanyProfileUi> companyList = new ArrayList<CompanyProfileUi>();
	
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private Map<String, String> currencyMap = new HashMap<String, String>();
	private User sessionUser = null;
	private List<StatusTypeUI> lst;
	private List<StatusType> statusList;
	private List<TxnType> txnTypelist;
	private HttpServletRequest request;
	
	@Autowired
	private ReportColumnDetailDao reportColumnDetailDao;
	private Map<String, String> filtersColumn=new HashMap<String, String>();
	private Map<String,String> channelType=new HashMap<>();
	
	
	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		try {
			
			String actionName = null;
			
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if(sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN) || sessionUser.getUserType().equals(UserType.SUPERADMIN) || sessionUser.getUserType().equals(UserType.SUBSUPERADMIN)) {

				// Changes By Pritam Ray
				List<Merchants> getMerchantActiveList = userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
				SortingUtil.sortMerchantNames(getMerchantActiveList);
				setMerchantList(getMerchantActiveList);

				currencyMap = getAllCurrency();
				companyList = new CompanyProfileDao().getAllCompanyProfileList();
			}else if(sessionUser.getUserType().equals(UserType.RESELLER)) {
				setMerchantList(userDao.getResellerMerchantList(sessionUser.getResellerId()));
				currencyMap = getAllCurrency();

			}else if(sessionUser.getUserType().equals(UserType.MERCHANT) || sessionUser.getUserType().equals(UserType.SUBUSER)  || sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
				Merchants merchant = new Merchants();
				logger.info("session User Email Id "+sessionUser.getEmailId());
				merchant.setEmailId(sessionUser.getEmailId());
				merchant.setBusinessName(sessionUser.getBusinessName());
				merchant.setPayId(sessionUser.getPayId());
				merchantList.add(merchant);
				if(sessionUser.getUserType().equals(UserType.SUBUSER) || sessionUser.getUserType().equals(UserType.SUBACQUIRER)){
					String parentMerchantPayId = sessionUser.getParentPayId();
					User parentMerchant = userDao.findPayId(parentMerchantPayId);
					merchant.setMerchant(parentMerchant);
					merchantList.add(merchant);
					Object[] obj = merchantList.toArray();
					for(Object sortList : obj){
						if(merchantList.indexOf(sortList) != merchantList.lastIndexOf(sortList)){
							merchantList.remove(merchantList.lastIndexOf(sortList));
						}
					}
				}
 				currencyMap = currencyMapProvider.currencyMap(sessionUser);
			}
			channelType=reportColumnDetailDao.findByTagName("channelType");
			System.out.println("deepchannel "+channelType);
			filtersColumn=reportColumnDetailDao.findByTagName("channelName");
			
			setFiltersColumn(filtersColumn);
			setTxnTypelist(TxnType.gettxnType());
			setLst(StatusTypeUI.getStatusType());
			setStatusList(StatusType.getStatusType());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

		return INPUT;
	}

	public String subUserList() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			logger.info("User PayId "+sessionUser.getPayId());
			if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
				setMerchantList(userDao.getSubUserList(sessionUser
						.getPayId()));
				currencyMap = Currency.getSupportedCurreny(sessionUser);
			} else if (sessionUser.getUserType().equals(UserType.SUBUSER) || sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
				Merchants merchant = new Merchants();
				User user = new User();
				user = userDao.findPayId(sessionUser.getParentPayId());
				merchant.setEmailId(user.getEmailId());
				merchant.setBusinessName(user.getBusinessName());
				merchant.setPayId(user.getPayId());
				merchantList.add(merchant);
				currencyMap = Currency.getSupportedCurreny(sessionUser);
			}

			setLst(StatusTypeUI.getStatusType());
			setTxnTypelist(TxnType.gettxnType());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

		return INPUT;
	}
	
	
	public  Map<String,String> getAllCurrency(){
		Map<String,String> data=new HashMap<String,String>();
		List<MultCurrencyCode> dataList=currencyCodeDao.getCurrencyCode();
		for(MultCurrencyCode code:dataList) {
			data.put(code.getCode(), code.getName());
		}
		
		
		return data;
		
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

	public List<StatusTypeUI> getLst() {
		return lst;
	}

	public void setLst(List<StatusTypeUI> lst) {
		this.lst = lst;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public List<TxnType> getTxnTypelist() {
		return txnTypelist;
	}

	public void setTxnTypelist(List<TxnType> txnTypelist) {
		this.txnTypelist = txnTypelist;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<StatusType> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<StatusType> statusList) {
		this.statusList = statusList;
	}

	public Map<String, String> getFiltersColumn() {
		return filtersColumn;
	}

	public void setFiltersColumn(Map<String, String> filtersColumn) {
		this.filtersColumn = filtersColumn;
	}
	
	public Map<String, String> getChannelType() {
		return channelType;
	}

	public void setChannelType(Map<String, String> channelType) {
		this.channelType = channelType;
	}
	
}
