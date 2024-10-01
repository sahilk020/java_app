package com.pay10.crm.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.pay10.crm.util.SortingUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.mongo.PassbookLedgerDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;

public class PassbookPO extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;

	private String dateRange;
	private String payId;

	private SimpleDateFormat sdNew = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdOld = new SimpleDateFormat("MM/dd/yyyy");
	@Autowired
	private UserDao userDao;

	// @Autowired
	// private PassbookPODao passbookPODao;

	@Autowired
	PassbookLedgerDao passbookPODao;
	private List<PassbookPODTO> aaData = new ArrayList<PassbookPODTO>();
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private String merchant;
	
	private User sessionUser = null;

	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}

	public List<PassbookPODTO> getAaData() {
		return aaData;
	}

	public void setAaData(List<PassbookPODTO> aaData) {
		this.aaData = aaData;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	@SuppressWarnings("unchecked")
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		if (!sessionUser.getUserType().name().equalsIgnoreCase("MERCHANT")) {
			// Changes By Pritam Ray
			List<Merchants> getMerchantActiveList = userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
			SortingUtil.sortMerchantNames(getMerchantActiveList);
			setMerchantList(getMerchantActiveList);
		} else {
			Merchants merchants = new Merchants();
			merchants.setMerchant(sessionUser);
			merchantList.clear();
			merchantList.add(merchants);
			setMerchantList(merchantList);
		}
		return SUCCESS;
	}

	public String passbookReport() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		if (dateRange != null&&merchant!=null) {
			User user=userDao.findByEmailId(merchant);
			String from = dateRange.split("-")[0].trim();
			String to = dateRange.split("-")[1].trim();

			try {
				System.out.println(sdNew.format(sdOld.parse(from)));
				System.out.println(sdNew.format(sdOld.parse(to)));
				from = sdNew.format(sdOld.parse(from));
				to = sdNew.format(sdOld.parse(to));
				aaData =passbookPODao.getPassbookDetails(user.getPayId(),from,to);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return SUCCESS;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}
	
	
	
	

}