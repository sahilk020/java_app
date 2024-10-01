package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import com.pay10.commons.dao.QuomoCurrencyConfigurationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.AcquirerMasterDB;
import com.pay10.commons.user.AcquirerMasterDBDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.crm.po.service.WalletPOService;

@SuppressWarnings("serial")
public class WalletPOAction extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AcquirerMasterDBDao acquirerMasterDBDao;

	@Autowired
	private MultCurrencyCodeDao multiCurrencyCodeDao;
	@Autowired
	private WalletPOService walletPOService;

	@Autowired
	private QuomoCurrencyConfigurationDao quomoCurrencyConfigurationDao;

	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private List<AcquirerMasterDB> acquirerList = new ArrayList<AcquirerMasterDB>();
	private List<MultCurrencyCode> currencyList = new ArrayList<>();

	private List<String> acquirerListReBalance = new ArrayList<String>();
	private List<String> currencyListReBalance = new ArrayList<>();

	private List<String> currencyListReBalance1 = new ArrayList<>();
	private User sessionUser = null;

	private String amount;
	private String currency;
	private String acquirerFrom;
	private String acquirerTo;
	private String merchant;

	private String balance;

	private String selectedAcquirerReBalance;

	private String selectedAcquirerReBalance1;

	private List<Object> aaData = new ArrayList<Object>();

	@Override
	public String execute() {
		return SUCCESS;
	}

	private static Logger logger = LoggerFactory.getLogger(WalletPOAction.class.getName());


	public String acquirerFund() {
		System.out.println(acquirerFrom);
		String balance = walletPOService.findFundByAcquire(acquirerFrom);
		System.out.println("Balance : " + balance);
		setBalance(balance);

		return SUCCESS;
	}

	public String acquirerWallet() {
		logger.info("acquirerWallet");
		setAcquirerListReBalance(quomoCurrencyConfigurationDao.getAcquirerList());
		return SUCCESS;
	}

	public String acquirerWalletAction() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setAaData(walletPOService.acquirerFunds(acquirerFrom));
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String merchantWallet() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		if (!sessionUser.getUserType().name().equalsIgnoreCase("MERCHANT")) {
			setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
					sessionUser.getRole().getId()));
		} else {
			Merchants merchants = new Merchants();
			merchants.setMerchant(sessionUser);
			merchantList.clear();
			merchantList.add(merchants);
			setMerchantList(merchantList);
		}
		setAcquirerList(acquirerMasterDBDao.getAcquirerMasterDBsWithAliasName());
		return SUCCESS;
	}

	public String merchantWalletAction() {
		logger.info("here the Merchant Wallet Action function is getting called ");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		logger.info("What is the Merchant :- {} , Acquirer From :- {} , Currency :- {}", merchant, acquirerFrom, currency);
		String payID = "";
		if (!merchant.equalsIgnoreCase("ALL")) {
			payID = userDao.findByEmailId(merchant).getPayId();
		}
		String selectedCurrency = "";
		if (!currency.equalsIgnoreCase("All Currency")) {
			selectedCurrency = currency;
		}
		setAaData(walletPOService.getCurrencyWiseDetails(payID, selectedCurrency));
		return SUCCESS;
	}

	public String rebalanceAcquirerAction() {
		logger.info("rebalanceAcquirerAction");
		setAcquirerListReBalance(quomoCurrencyConfigurationDao.getAcquirerList());
		return SUCCESS;
	}

	public String rebalanceAcquirerFunds() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		aaData = walletPOService.rebalanceAcquirerFunds(acquirerFrom, acquirerTo, currency, amount);
		return SUCCESS;
	}

	public String acquirerFundAdd() {
		logger.info("acquirerFundAdd");
		setAcquirerListReBalance(quomoCurrencyConfigurationDao.getAcquirerList());
		return SUCCESS;
	}

	public String acquirerFundStatus() {
		logger.info("acquirerFundStatus");
		setAcquirerListReBalance(quomoCurrencyConfigurationDao.getAcquirerList());
		return SUCCESS;
	}

	public String getCurrencyForAcquirerReBalance() {
		logger.info("getCurrencyForAcquirerReBalance :"+selectedAcquirerReBalance);
		setCurrencyListReBalance(quomoCurrencyConfigurationDao.getCurrencyListByAcquirer(selectedAcquirerReBalance));
		return SUCCESS;
	}

	public String getCurrencyForAcquirerReBalanceOne() {
		logger.info("getCurrencyForAcquirerReBalanceOne :"+selectedAcquirerReBalance1);
		setCurrencyListReBalance1(quomoCurrencyConfigurationDao.getCurrencyListByAcquirer(selectedAcquirerReBalance1));
		return SUCCESS;
	}





	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public List<AcquirerMasterDB> getAcquirerList() {
		return acquirerList;
	}

	public void setAcquirerList(List<AcquirerMasterDB> acquirerList) {
		this.acquirerList = acquirerList;
	}

	public List<MultCurrencyCode> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<MultCurrencyCode> currencyList) {
		this.currencyList = currencyList;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAcquirerFrom() {
		return acquirerFrom;
	}

	public void setAcquirerFrom(String acquirerFrom) {
		this.acquirerFrom = acquirerFrom;
	}

	public String getAcquirerTo() {
		return acquirerTo;
	}

	public void setAcquirerTo(String acquirerTo) {
		this.acquirerTo = acquirerTo;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public List<Object> getAaData() {
		return aaData;
	}

	public void setAaData(List<Object> aaData) {
		this.aaData = aaData;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public List<String> getAcquirerListReBalance() {
		return acquirerListReBalance;
	}

	public void setAcquirerListReBalance(List<String> acquirerListReBalance) {
		this.acquirerListReBalance = acquirerListReBalance;
	}

	public List<String> getCurrencyListReBalance() {
		return currencyListReBalance;
	}

	public void setCurrencyListReBalance(List<String> currencyListReBalance) {
		this.currencyListReBalance = currencyListReBalance;
	}

	public String getSelectedAcquirerReBalance() {
		return selectedAcquirerReBalance;
	}

	public void setSelectedAcquirerReBalance(String selectedAcquirerReBalance) {
		this.selectedAcquirerReBalance = selectedAcquirerReBalance;
	}

	public List<String> getCurrencyListReBalance1() {
		return currencyListReBalance1;
	}

	public void setCurrencyListReBalance1(List<String> currencyListReBalance1) {
		this.currencyListReBalance1 = currencyListReBalance1;
	}

	public String getSelectedAcquirerReBalance1() {
		return selectedAcquirerReBalance1;
	}

	public void setSelectedAcquirerReBalance1(String selectedAcquirerReBalance1) {
		this.selectedAcquirerReBalance1 = selectedAcquirerReBalance1;
	}
}
