package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dto.CashDepositDTOPO;
import com.pay10.commons.dto.MerchantWalletPODTO;
import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.mongoReports.ApproverListPODao;
import com.pay10.crm.mongoReports.CashDepositPODao;

public class MerchantWalletPO extends AbstractSecureAction {

	private static final long serialVersionUID = 690514249678307821L;
	private static final Logger logger = LoggerFactory.getLogger(MerchantWalletPO.class.getName());

	@Autowired
	private UserDao userDao;

	@Autowired
	private ApproverListPODao approverListPODao;

	@Autowired
	private CashDepositPODao cashDepositPODao;
	
	@Autowired
	private MerchantWalletPODao merchantWalletPODao;
	
	public List<MerchantWalletPODTO> getAaData() {
		return aaData;
	}

	public void setAaData(List<MerchantWalletPODTO> aaData) {
		this.aaData = aaData;
	}

	public MerchantWalletPODTO getMerchantWalletPODTO() {
		return merchantWalletPODTO;
	}

	public void setMerchantWalletPODTO(MerchantWalletPODTO merchantWalletPODTO) {
		this.merchantWalletPODTO = merchantWalletPODTO;
	}

	private List<MerchantWalletPODTO> aaData = new ArrayList<MerchantWalletPODTO>();
	private MerchantWalletPODTO merchantWalletPODTO = new MerchantWalletPODTO();

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		
		 return SUCCESS;
	}

	public String callWalletApi() {
		aaData = merchantWalletPODao.getAllData();
		return SUCCESS;
	}
	
	
}
