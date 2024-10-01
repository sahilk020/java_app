package com.pay10.crm.web_store_api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;

public class WebstoreOrderAction extends AbstractSecureAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8002428434641535658L;

	private static Logger logger = LoggerFactory.getLogger(WebstoreOrderAction.class.getName());

	@Autowired
	private UserDao userDao;

	private List<Merchants> merchants;
	private long id;
	private String name;
	private String description;
	private String price;
	private String discountprice;
	private String payId1;
	private boolean productStatus;
	private String image;

	@Override
	public String execute() {
		logger.info("WebStoreOrder get Merchant details... "+"image...."+image);
		
		// sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		User user = (User) sessionMap.get(Constants.USER.getValue());
		Merchants merchant = new Merchants();
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "Merchant")) {
			merchant.setBusinessName(user.getBusinessName());
			merchant.setPayId(user.getPayId());
			merchant.setEmailId(user.getEmailId());
			merchant.setUuId(user.getUuId());
			merchant.setIsActive(true);
			List<Merchants> merchants = new ArrayList<>();
			merchants.add(merchant);
			setMerchants(merchants);

		} else if(StringUtils.isNotEmpty(payId1)) {
				logger.info("UUID in else condition in web storeorderAction={}",payId1);
				User u=(userDao.findByUuid(payId1));
				Merchants setMerchant = new Merchants();
				setMerchant.setBusinessName(u.getBusinessName());
				setMerchant.setEmailId(u.getEmailId());
				setMerchant.setPayId(u.getPayId());
				setMerchant.setUuId(u.getUuId());
				List<Merchants> merchants = new ArrayList<>();
				merchants.add(setMerchant);
				setMerchants(merchants);

			}
			else {
				setMerchants(userDao.getWebStoreEnableMerchantList());
			}
		
		
		return SUCCESS;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isProductStatus() {
		return productStatus;
	}

	public void setProductStatus(boolean productStatus) {
		this.productStatus = productStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDiscountprice() {
		return discountprice;
	}

	public void setDiscountprice(String discountprice) {
		this.discountprice = discountprice;
	}

	public String getPayId1() {
		return payId1;
	}

	public void setPayId1(String payId1) {
		this.payId1 = payId1;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Merchants> getMerchants() {
		return merchants;
	}

	public void setMerchants(List<Merchants> merchants) {
		this.merchants = merchants;
	}

}
