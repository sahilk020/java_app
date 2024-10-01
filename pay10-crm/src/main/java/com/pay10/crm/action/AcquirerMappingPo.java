package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.dto.CommissionPODTO;
import com.pay10.commons.mongo.CommissionPODao;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;

public class AcquirerMappingPo extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;
	@Autowired
	private UserDao userDao;
	private Map<String, String> payIdList = new HashMap<>();
	private List<MultCurrencyCode> currencyList = new ArrayList<>();
	private CommissionPODTO commissionPOMapping;
	private String payId;
	private String acquirer;
	private String currency;
	private String response;
	private String type;
	private String value;
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	@Autowired
	private CommissionPODao commissionPODao;
	private int draw;
	private int length;
	private int start;
	

	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;
	private List<CommissionPODTO> aaData = new ArrayList<CommissionPODTO>();

	public String execute() {
		List<User> users = userDao.fetchUsersBasedOnPOEnable();

		users.stream().forEach(user -> {
			payIdList.put(user.getPayId(), user.getBusinessName());
		});

		currencyList = multCurrencyCodeDao.getCurrencyCode();

		return SUCCESS;
	}

	public String acquirerMappingSave() {
		System.out.println("PAYID : " + payId);
		System.out.println("ACQUIRER : " + acquirer);
		System.out.println("CURRENCY : " + currency);
		System.out.println("TYPE : " + type);
		System.out.println("VALUE : " + value);

		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		response = commissionPODao.saveAcquirerMapping(payId, acquirer, currency, type, value,
				sessionUser.getEmailId());
		return SUCCESS;
	}

	public String getCommissionReport() {
		
		BigInteger bigInt = BigInteger.valueOf(commissionPODao.getActiveDetailsCount());//,ipAddress,totalAmount));
		setRecordsTotal(bigInt);
		if (getLength() == -1) {
			setLength(getRecordsTotal().intValue());
		}
		
		recordsFiltered = recordsTotal;
		aaData = commissionPODao.getActiveDetails(start, length);
		return SUCCESS;
	}

	public Map<String, String> getPayIdList() {
		return payIdList;
	}

	public void setPayIdList(Map<String, String> payIdList) {
		this.payIdList = payIdList;
	}

	public List<MultCurrencyCode> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<MultCurrencyCode> currencyList) {
		this.currencyList = currencyList;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public CommissionPODTO getCommissionPOMapping() {
		return commissionPOMapping;
	}

	public void setCommissionPOMapping(CommissionPODTO commissionPOMapping) {
		this.commissionPOMapping = commissionPOMapping;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public List<CommissionPODTO> getAaData() {
		return aaData;
	}

	public void setAaData(List<CommissionPODTO> aaData) {
		this.aaData = aaData;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public BigInteger getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(BigInteger recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public BigInteger getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(BigInteger recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	
}