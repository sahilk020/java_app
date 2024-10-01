package com.pay10.crm.po.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pay10.commons.mongo.WalletHistoryRepository;
import com.pay10.commons.user.AcquirerMasterDBDao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.crm.po.dto.AcquirerBalanceDTO;
import com.pay10.crm.po.dto.AcquirerMerchantWiseDetailDTO;
import com.pay10.crm.po.dto.RebalanceDTO;

@Service
public class WalletPOService {

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	@Autowired
	private AcquirerMasterDBDao acquirerMasterDBDao;
	@Autowired
	private WalletHistoryRepository walletHistoryRepository;

	private static Logger logger = LoggerFactory.getLogger(WalletPOService.class.getName());

	public List<Object> rebalanceAcquirerFunds(String acquirerFrom, String acquirerTo, String currency, String amount) {
		List<Object> rebalanceDTOs = new ArrayList<>();

		for (int i = 1; i <= 5; i++) {
			RebalanceDTO rebalanceDTO = new RebalanceDTO();
			rebalanceDTO.setsNo(i);
			rebalanceDTO.setAcquireFrom(acquirerMasterDBDao.getAcquirerNameByCode(acquirerFrom));
			rebalanceDTO.setAcquireTo(acquirerMasterDBDao.getAcquirerNameByCode(acquirerTo));
			rebalanceDTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(currency));
			rebalanceDTO.setAmount(amount + i);
			rebalanceDTO.setDate(new Date().toString());
			rebalanceDTOs.add(rebalanceDTO);
		}
		return rebalanceDTOs;

	}

	public List<Object> acquirerFunds(String acquirer) {
		List<Object> acquirerBalanceDTOs = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			AcquirerBalanceDTO acquirerBalanceDTO = new AcquirerBalanceDTO();
			acquirerBalanceDTO.setsNo(i);
			acquirerBalanceDTO.setAcquirerName(acquirerMasterDBDao.getAcquirerNameByCode(acquirer));
			acquirerBalanceDTO.setCurrencyCode("356");
			acquirerBalanceDTO.setCurrencyName("INR");
			acquirerBalanceDTO.setAvailableBalance(String.valueOf((Integer.parseInt("1000") + i)));
			acquirerBalanceDTOs.add(acquirerBalanceDTO);
		}

		return acquirerBalanceDTOs;

	}

	public List<Object> getCurrencyWiseDetails(String merchant, String currency) {
		logger.info("Here the get current wise detail method is getting called :- {}", merchant);
		List<Object> list = walletHistoryRepository.findMerchantFundByPayIdV2(merchant, currency);
		System.out.println("CURRENCY : " + new Gson().toJson(list));
		return list;
	}

	public List<Object> getCurrencyAndAcquireWiseDetail(String merchant, String acquirerFrom) {
		List<Object> curreObjects = new ArrayList<Object>();
		for (int i = 1; i <= 5; i++) {
			AcquirerMerchantWiseDetailDTO acquirerMerchantWiseDetailDTO = new AcquirerMerchantWiseDetailDTO();
			acquirerMerchantWiseDetailDTO.setsNo(i);
			acquirerMerchantWiseDetailDTO.setCurrency("INR" + i);
			acquirerMerchantWiseDetailDTO.setTotalBalance("800");
			acquirerMerchantWiseDetailDTO.setAvailableBalance("1000");
			acquirerMerchantWiseDetailDTO.setAcquirer("ACQ" + i);
			curreObjects.add(acquirerMerchantWiseDetailDTO);
		}
		return curreObjects;
	}

	public String findFundByAcquire(String acquirerFrom) {
		if (acquirerFrom.equalsIgnoreCase("BOB")) {
			return "10000.00";
		} else {
			return "1000.00";
		}

	}

}
