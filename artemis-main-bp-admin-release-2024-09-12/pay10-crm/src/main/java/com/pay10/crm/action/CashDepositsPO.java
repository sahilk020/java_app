package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.dto.CashDepositDTOPO;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.crm.mongoReports.CashDepositPODao;

public class CashDepositsPO extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;
	@Autowired
	private CashDepositPODao cashDepositPODao;
	@Autowired
	private UserDao userDao;
	private List<MultCurrencyCode> currencyList = new ArrayList<>();

	private Map<String, String> payIdList = new HashMap<>();
	private List<CashDepositDTOPO> aaData = new ArrayList<CashDepositDTOPO>();
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	private String currency;
	private User sessionUser = null;

	public String payId;
	public String bank;
	public String amount;
	public String createDate;
	public String txnId;
	public String remark;

	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		if (!sessionUser.getUserType().name().equalsIgnoreCase("MERCHANT")) {
//			setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
//					sessionUser.getRole().getId()));
			List<User> users = userDao.fetchUsersBasedOnPOEnable();
			users.stream().forEach(user -> {
					payIdList.put(user.getPayId(), user.getBusinessName());
			});
			
		} else {
//			Merchants merchants = new Merchants();
//			merchants.setMerchant(sessionUser);
//			merchantList.clear();
//			merchantList.add(merchants);
//			setMerchantList(merchantList);
			List<User> users = userDao.fetchUsersBasedOnPOEnable();
			users.stream().forEach(user -> {
				if(sessionUser.getPayId().equalsIgnoreCase(user.getPayId())) {
					payIdList.put(user.getPayId(), user.getBusinessName());
				}
			});
		}
		
		
//		List<User> users = userDao.fetchUsersBasedOnPOEnable();
//
//		users.stream().forEach(user -> {
//			payIdList.put(user.getPayId(), user.getBusinessName());
//		});

		if (payId != null && txnId != null) {
			CashDepositDTOPO cashDepositDTOPO = new CashDepositDTOPO();
			cashDepositDTOPO.setAmount(amount);
			cashDepositDTOPO.setCreateDate(createDate);
			cashDepositDTOPO.setPayId(payId);
			cashDepositDTOPO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(currency));
			cashDepositDTOPO.setTxnId(txnId);
			cashDepositDTOPO.setRemark(remark);
			cashDepositDTOPO.setStatus("Pending");
			cashDepositDTOPO.setBank("NA");
			cashDepositDTOPO.setApproverRemark("NA");
			cashDepositDTOPO.setRejectRemark("NA");
			cashDepositDTOPO.setUpdateDate("NA");
			cashDepositDTOPO.setUpdatedBy("");

			cashDepositPODao.saveCashDeposit(cashDepositDTOPO);

		}
		List<String> currency1 = userDao.findCurrencyByPayId(sessionUser.getPayId());
		for(String data: currency1){
			MultCurrencyCode currencyData = multCurrencyCodeDao.findByCode(data);
			currencyList.add(currencyData);
		}

		return SUCCESS;
	}

	public String CashDepositReport() {
		sessionUser= (User) sessionMap.get(Constants.USER.getValue());
		if (!sessionUser.getUserType().name().equalsIgnoreCase("MERCHANT")) {
			aaData = cashDepositPODao.getPendingList("ALL");
			System.out.println("Final Report Data : " + new Gson().toJson(aaData));
		}else {
			aaData = cashDepositPODao.getPendingList(sessionUser.getPayId());
			System.out.println("Final Report Data : " + new Gson().toJson(aaData));
		}
		
		return SUCCESS;

	}



	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Map<String, String> getPayIdList() {
		return payIdList;
	}

	public void setPayIdList(Map<String, String> payIdList) {
		this.payIdList = payIdList;
	}

	public List<CashDepositDTOPO> getAaData() {
		return aaData;
	}

	public void setAaData(List<CashDepositDTOPO> aaData) {
		this.aaData = aaData;
	}

	public List<MultCurrencyCode> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<MultCurrencyCode> currencyList) {
		this.currencyList = currencyList;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
