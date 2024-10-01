package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pay10.commons.user.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.MerchantAcquirerPropertiesDao;
import com.pay10.commons.entity.CurrencyCode;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.Acquirer;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionType;

public class MerchantMappingAction extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;
	
	
	@Autowired
	private MultCurrencyCodeDao currencyCodeDao;
	
	@Autowired
	private MerchantAcquirerPropertiesDao merchantAcquirerPropertiesDao;

	@Autowired
	private CrmValidator validator;
	
	

	private static Logger logger = LoggerFactory.getLogger(MerchantMappingAction.class.getName());
	private static final long serialVersionUID = 905765909007885886L;

	private int countList;
	public List<PaymentType> paymentList = new ArrayList<PaymentType>();
	public Map<String, Object> mopList = new LinkedHashMap<String, Object>();
	public List<MopType> mopListCC = new ArrayList<MopType>();
	public List<MopType> mopListDC = new ArrayList<MopType>();
	public List<MopType> mopListPC = new ArrayList<MopType>();
	public List<MopType> mopListNB = new ArrayList<MopType>();
	public List<MopType> mopListWL = new ArrayList<MopType>();
	public List<MopType> mopListUPI = new ArrayList<MopType>();
	public List<MopType> mopListEmi = new ArrayList<MopType>();
	public List<MopType> mopListATL = new ArrayList<MopType>();
	public List<TransactionType> transList = new ArrayList<TransactionType>();
	public List<Merchants> listMerchant = new ArrayList<Merchants>();
	public List<Acquirer> listAcquirer = new ArrayList<Acquirer>();
	public List<MopType> mopListQR = new ArrayList<MopType>();

//	public List<MultCurrencyCode> currencyList = new ArrayList<MultCurrencyCode>();

	List<QuomoCurrencyConfiguration> currencyMapping = new ArrayList<>();

	public	Map<String, String> currencyMap = new HashMap<String, String>();

	// private Map<String, String> acquirerList = new TreeMap<String, String>();
	private String merchantEmailId;
	private String acquirer;
	private Map<String, String> currencies;

	private boolean international;
	private boolean domestic;
	private boolean commercial;
	private boolean customer;
	private boolean showSave;
	private User sessionUser = null;

	public HashMap<String, HashMap<String,List<MopType>>> merchantMapping = new HashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		
		logger.info("mopList :"+mopList);
		
		

		try {

			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
					sessionUser.getRole().getId()));
			// setListMerchant(userDao.getMerchantList());
			setListAcquirer(userDao.getAcquirers());
			setAcquirerMerchantProperty(merchantEmailId);
//			currencyList= currencyCodeDao.getCurrencyCode();
			currencyMapping = userDao.findByAcquirerFromQuomoCurrencyConfig(acquirer);

			for (QuomoCurrencyConfiguration currency : currencyMapping) {
				currencyMap.put(currency.getCurrencyCode(),currency.getCurrency());

				merchantMapping.put(currency.getCurrencyCode(),new HashMap<>());
			}
			logger.info("CurrencyMap from QuomoConfig ::::"+currencyMap);
			setCurrencies(currencyMap);

			setShowSave(false);
			if (!(acquirer == null || acquirer.equals(""))) {
				logger.info("------------------------");
				setShowSave(true);
				createList();
				setCountList(PaymentType.values().length);
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		logger.info("mopList2 :"+mopList);
		return INPUT;
	}

	@Override
	public void validate() {
		if ((validator.validateBlankField(getAcquirer()))) {
		} else if (!(validator.validateField(CrmFieldType.ACQUIRER, getAcquirer()))) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}

	public void setAcquirerMerchantProperty(String merchantEmailId) {

		try {

			MerchantAcquirerProperties merchantAcquirerProperties = null;
			String merchantPayId = null;

			if (StringUtils.isNotBlank(merchantEmailId)) {
				merchantPayId = userDao.getPayIdByEmailId(merchantEmailId);
				merchantAcquirerProperties = merchantAcquirerPropertiesDao.getMerchantAcquirerProperties(merchantPayId,
						acquirer);
			}

			if (merchantAcquirerProperties == null) {

				setInternational(false);
				setDomestic(false);
			}

			else if (merchantAcquirerProperties.getPaymentsRegion().equals(AccountCurrencyRegion.ALL)) {
				setInternational(true);
				setDomestic(true);
			} else if (merchantAcquirerProperties.getPaymentsRegion().equals(AccountCurrencyRegion.INTERNATIONAL)) {
				setInternational(true);
				setDomestic(false);
			} else if (merchantAcquirerProperties.getPaymentsRegion().equals(AccountCurrencyRegion.DOMESTIC)) {
				setInternational(false);
				setDomestic(true);
			} else {
				setInternational(false);
				setDomestic(false);
			}

		}

		catch (Exception e) {
			logger.error("Exception in setAcquirerMerchantProperty = " + e);
		}
	}

	/*
	 * public void setAcquirerList() {
	 * 
	 * try {
	 * 
	 * List<Acquirer> acquirersList = new ArrayList<Acquirer>(); Map<String, String>
	 * acquirerMap = new HashMap<String, String>();
	 * 
	 * acquirersList = userDao.getAcquirers();
	 * 
	 * for (Acquirer acquirer : acquirersList) {
	 * 
	 * acquirerMap.put(acquirer.getAcquirerBusinessName(),
	 * acquirer.getAcquirerFirstName()); } acquirerList.putAll(acquirerMap); }
	 * 
	 * catch (Exception e) { logger.error("Exception = " + e); } }
	 */

	private void createList() {

		try {
			logger.info("<<<<<<<<<< acquirer >>>>>>>>>> " + acquirer);
			AcquirerType acqType = AcquirerType.getInstancefromCode(acquirer);
//			List<PaymentType> supportedPaymentTypes = PaymentType.getGetPaymentsFromSystemProp(acquirer);
//			setCurrencies(Currency.getAllCurrency());
//			logger.info("<<<<<<<<<< supportedPaymentTypes >>>>>>>>>> " + supportedPaymentTypes);
			logger.info("<<<<<<<< acqType >>>>>>>>>> " + acqType);

			List<QuomoCurrencyConfiguration> mappingData = userDao.findByAcquirerFromQuomoCurrencyConfig(acquirer);


			for (QuomoCurrencyConfiguration quomoCurrencyConfiguration1 : mappingData){
				HashMap<String,List<MopType>> mopListMap = merchantMapping.get(quomoCurrencyConfiguration1.getCurrencyCode());
				
				mopListMap.forEach((key, mopTypeList) -> {
				    List<MopType> filteredList = mopTypeList.stream()
				                                            .distinct()
				                                            .filter(mopType -> mopType != null)
				                                            .collect(Collectors.toList());
				    mopListMap.put(key, filteredList);
				});

				
			//	mopListMap.put("Net Banking", filteredList);
			//	logger.info("mopListMap : {}"+mopListMap);

				if(mopListMap != null){
					if(!mopListMap.containsKey(PaymentType.getpaymentName(quomoCurrencyConfiguration1.getPaymentType()))){
						mopListMap.put(PaymentType.getpaymentName(quomoCurrencyConfiguration1.getPaymentType()), new ArrayList<>());
					}
					List<MopType> mopTypeList = mopListMap.get(PaymentType.getpaymentName(quomoCurrencyConfiguration1.getPaymentType()));
					mopTypeList.add(MopType.getInstanceUsingCode(quomoCurrencyConfiguration1.getMopType()));

				}
			}
//			logger.info("Merchant Mapping with Currency data from action::::::::::"+merchantMapping);


			if (mopListCC.size() != 0) {
				mopList.put(PaymentType.CREDIT_CARD.getName(), mopListCC);
			}
			if (mopListDC.size() != 0) {
				mopList.put(PaymentType.DEBIT_CARD.getName(), mopListDC);
			}
			if (mopListPC.size() != 0) {
				mopList.put(PaymentType.PREPAID_CARD.getName(), mopListPC);
			}
			if (mopListUPI.size() != 0) {
				mopList.put(PaymentType.UPI.getName(), mopListUPI);
			}
//			if (mopListATL.size() != 0) {
//				mopList.put(PaymentType.AD.getName(), mopListATL);
//			}
			if (mopListQR.size() != 0) {
				mopList.put(PaymentType.QRCODE.getName(), mopListQR);
			}
			if (mopListNB.size() != 0) {
				mopList.put(PaymentType.NET_BANKING.getName(), mopListNB);
			}
			if (CollectionUtils.isNotEmpty(mopListEmi)) {
				mopList.put(PaymentType.EMI.getName(), mopListEmi);
			}


//			if (!(mopListNB.isEmpty())) {
//				Collections.sort(mopListNB);
//				mopList.put(PaymentType.NET_BANKING.getName(), mopListNB);
//			}

			if (mopListWL.size() != 0) {
				mopList.put(PaymentType.WALLET.getName(), mopListWL);
			}
			transList = TransactionType.chargableMopTxn();

		} catch (Exception e) {
			logger.error("Exception occured in MerchantMappingAction , create List , exception = ", e);
		}

	}

	public String display() {
		return NONE;
	}

	public int getCountList() {
		return countList;
	}

	public void setCountList(int countList) {
		this.countList = countList;
	}

	public List<Merchants> getListMerchant() {
		return listMerchant;
	}

	public void setListMerchant(List<Merchants> listMerchant) {
		this.listMerchant = listMerchant;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public Map<String, String> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Map<String, String> currencies) {
		this.currencies = currencies;
	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}

	public List<Acquirer> getListAcquirer() {
		return listAcquirer;
	}

	public void setListAcquirer(List<Acquirer> listAcquirer) {
		this.listAcquirer = listAcquirer;
	}

	public boolean isInternational() {
		return international;
	}

	public void setInternational(boolean international) {
		this.international = international;
	}

	public boolean isDomestic() {
		return domestic;
	}

	public void setDomestic(boolean domestic) {
		this.domestic = domestic;
	}

	public boolean isCommercial() {
		return commercial;
	}

	public void setCommercial(boolean commercial) {
		this.commercial = commercial;
	}

	public boolean isCustomer() {
		return customer;
	}

	public void setCustomer(boolean customer) {
		this.customer = customer;
	}

	public boolean isShowSave() {
		return showSave;
	}

	public void setShowSave(boolean showSave) {
		this.showSave = showSave;
	}
}
