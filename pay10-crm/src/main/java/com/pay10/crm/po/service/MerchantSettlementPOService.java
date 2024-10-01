package com.pay10.crm.po.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pay10.commons.dto.SettlementDTO;
import com.pay10.commons.mongo.SettlementPORepository;
import com.pay10.commons.mongo.WalletHistoryRepository;
import com.pay10.commons.user.User;

@Service
public class MerchantSettlementPOService {

	// private SimpleDateFormat dmy = new SimpleDateFormat("dd-MM-yyyy");
	@Autowired
	private SettlementPORepository settlementPORepository;
	@Autowired
	private WalletHistoryRepository walletHistoryRepository;

	public List<Object> getMerchantSettlementDetails(User user, String currency) {
		return settlementPORepository.getMerchantSettlementDetails(user, currency);
	}

	public String createPayout(SettlementDTO settlementDTO,HttpServletRequest request) {
		return settlementPORepository.createSettlementFromPanelAPI(settlementDTO,request);
	}
	
	public String createSettlementFromPanel(SettlementDTO settlementDTO,HttpServletRequest request) {
		return settlementPORepository.createSettlementFromPanel(settlementDTO,request);
	}
	
	

	public String getMerchantAvailableBalance(String merchant,String currencyName) {
		Map<String, Object> walletMap = walletHistoryRepository.findMerchantFundByPayId(merchant,currencyName);
		if (walletMap != null && walletMap.size() > 0) {
			return walletMap.get("finalBalance").toString();
		} else {
			return "0.0";
		}
	}

	public List<Object> getAdminSettlementDetailsPO(User user, String currency) {
		return settlementPORepository.getMerchantPendingSettlementDetailsForPO(user, currency);
	}
	
	public List<Object> getAdminSettlementDetails(User user, String currency) {
		return settlementPORepository.getMerchantPendingSettlementDetails(user, currency);
	}
	

	public String approveSettlementByTxnId(String txnId,String payId,String pgRefNum,String updatedBy) {
		// update settlement status based on TxnId
		return settlementPORepository.approveRequest(txnId,payId,pgRefNum,updatedBy);
	}
	
	public String approveSettlementByTxnIdDetail(String txnId,String payId,String currency,String updatedBy, String uid) {
		// update settlement status based on TxnId
		return settlementPORepository.approveRequestDetail(txnId,payId,currency,updatedBy,uid);
	}

//	public String approveSettlementByTxnIdAndFile(String uid, String txnId, File file) {
//		// update settlement status based on file data
//		return settlementPORepository.approveRequest(uid, txnId);
//	}

	public String rejectSettlementByUid(String payId,String pgRefNum,String updatedBy,String amount,String currency) {
		return settlementPORepository.rejectRequest(payId,pgRefNum,updatedBy,amount,currency);
	}
	
	public String rejectSettlementByUidDetail(String payId,String currency,String updatedBy,String uid) {
		return settlementPORepository.rejectRequestDetail(payId,currency,updatedBy,uid);
	}

}
