package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pay10.commons.user.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.MopTypeUI;
import com.pay10.commons.util.PaymentTypeUI;
import com.pay10.commons.util.TransactionType;

public class AcquirerRouterRedirectPayout extends AbstractSecureAction {
	private static Logger logger = LoggerFactory.getLogger(AcquirerRouterRedirectPayout.class.getName());
	private static final long serialVersionUID = -3118366599992623506L;
	private List<TransactionType> transactionTypeList = new ArrayList<TransactionType>();
//	private Map<String, String> currencyMap = Currency.getAllCurrency();
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private List<String> moptypeList = new ArrayList<String>();
	private Map<String, String> currencyMap = new HashMap<String, String>();

	private List<String> acquirerList = new ArrayList<String>();
	private List<String> acquirerList1 = new ArrayList<String>();
	private List<String> acquirerList2 = new ArrayList<String>();
	private List<String> mopbanklist = new ArrayList<String>();
	private List<MopTypeDto> moplist = new ArrayList<MopTypeDto>();
	public List<MultCurrencyCode> currencyList = new ArrayList<MultCurrencyCode>();
	public Map<String, String> currencyMap1 = new HashMap<String, String>();

	private List<String> acquirerListEdit = new ArrayList<>();

	private static final int FETCH_ACQR_TYPE = 1;

	private static final int FETCH_PAYMENT_TYPE = 3;
	private static final int FETCH_MOP_TYPE = 4;

	@Autowired
	private TdrSettingPayoutDao tdrSettingPayoutDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RouterRuleDaoPayout routerRuleDaoPayout;

	private String payId;
	@Autowired
	private MultCurrencyCodeDao currencyCodeDao;

	private int type;
	private String Acquirer1;
	private String paytype;
	private User sessionUser = null;
	private String merchantemail;
	private String payment;

	private String selectedCurrency;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {

			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			transactionTypeList.add(TransactionType.AUTHORISE);
			transactionTypeList.add(TransactionType.SALE);
			// merchantList = userDao.getMerchantActiveList();
			merchantList = userDao.fetchUsersBasedOnPOEnable().stream().map(user -> {

				Merchants merchants = new Merchants();

				merchants.setMerchant(user);

				return merchants;

			}).collect(Collectors.toList());

			{
				switch (type) {

				case FETCH_ACQR_TYPE:
					logger.info("Fetch ACQR data type= {}, payId={}", type, payId);
					List<String> acqlist = tdrSettingPayoutDao.getAcquirerTypeList(payId);
					for (String acqList : acqlist) {
						String contoCode = AcquirerType.getInstancefromName(acqList).getCode();
						acquirerList.add(contoCode);

					}
					setAcquirerList(acquirerList);

					List<String> acquirerListEditDemo = routerRuleDaoPayout.getAcquirerListEdit(payId);
					for (String acqList1 : acquirerListEditDemo) {
						String finalAcquirer = acqList1.substring(2);
						acquirerListEdit.add(finalAcquirer);
					}
					setAcquirerListEdit(acquirerListEdit);
					logger.info("Acquirer List : {}", acquirerList);
					logger.info("Acquirer List Edit : {}", acquirerListEdit);
					break;

				case FETCH_PAYMENT_TYPE:
					logger.info("Fetch Payment type list data  type= {}, payId={}", type, payId);
					List<String> paymentLsit = tdrSettingPayoutDao.getPaymentType(payId, Acquirer1);
					setAcquirerList1(paymentLsit);
					logger.info("Payment List : {}", paymentLsit);

					break;
				case FETCH_MOP_TYPE:
					logger.info("Fetch MOP data type= {}, payId={}", type, payId);
					List<String> mopList = tdrSettingPayoutDao.getMopTypelist(payId, Acquirer1, paytype);
					for (String moplList : mopList) {

						moptypeList.add(moplList);

					}

					setAcquirerList2(moptypeList);
					logger.info("Mop List : {}", mopList);
					break;

				default:
					logger.info("null value in type");
				}
			}

			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	}

	public String GetAllMoptypeBypayment() {
		try {
			MopTypeUI[] mopdata = MopTypeUI.values();
			for (MopTypeUI mopList : mopdata) {
				moplist.add(new MopTypeDto(mopList.getCode(), mopList.getName(), mopList.getName()));

			}
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	}

	public String GetMoptypeBypayment() {
		try {
			String payId = StringUtils.isBlank(merchantemail) || StringUtils.equalsIgnoreCase(merchantemail, "ALL")
					? null
					: userDao.getPayIdByEmailId(merchantemail);
			String payvalue = PaymentTypeUI.getInstanceUsingCode1(payment);
			List<String> mopdata = userDao.getMOPtypelist(payId, payvalue);
			for (String mopList : mopdata) {
				MopType mopvalue = MopType.getInstanceUsingStringValue1(mopList);
				if (mopvalue != null) {
					moplist.add(new MopTypeDto(mopvalue.getCode(), mopvalue.getName(), mopvalue.getName()));
				}
			}
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}

	}

	public List<MultCurrencyCode> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<MultCurrencyCode> currencyList) {
		this.currencyList = currencyList;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public List<TransactionType> getTransactionTypeList() {
		return transactionTypeList;
	}

	public void setTransactionTypeList(List<TransactionType> transactionTypeList) {
		this.transactionTypeList = transactionTypeList;

	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getAcquirerList() {
		return acquirerList;
	}

	public void setAcquirerList(List<String> acquirerList) {
		this.acquirerList = acquirerList;
	}

	public String getAcquirer1() {
		return Acquirer1;
	}

	public void setAcquirer1(String acquirer1) {
		Acquirer1 = acquirer1;
	}

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}

	public List<String> getMopbanklist() {
		return mopbanklist;
	}

	public void setMopbanklist(List<String> mopbanklist) {
		this.mopbanklist = mopbanklist;
	}

	public List<String> getAcquirerList1() {
		return acquirerList1;
	}

	public void setAcquirerList1(List<String> acquirerList1) {
		this.acquirerList1 = acquirerList1;
	}

	public List<String> getAcquirerList2() {
		return acquirerList2;
	}

	public void setAcquirerList2(List<String> acquirerList2) {
		this.acquirerList2 = acquirerList2;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
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

	public List<String> getMoptypeList() {
		return moptypeList;
	}

	public void setMoptypeList(List<String> moptypeList) {
		this.moptypeList = moptypeList;
	}

	public List<MopTypeDto> getMoplist() {
		return moplist;
	}

	public void setMoplist(List<MopTypeDto> moplist) {
		this.moplist = moplist;
	}

	public String getSelectedCurrency() {
		return selectedCurrency;
	}

	public void setSelectedCurrency(String selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}

	public List<String> getAcquirerListEdit() {
		return acquirerListEdit;
	}

	public void setAcquirerListEdit(List<String> acquirerListEdit) {
		this.acquirerListEdit = acquirerListEdit;
	}
}