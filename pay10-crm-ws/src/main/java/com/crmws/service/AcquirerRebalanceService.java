package com.crmws.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.crmws.dto.AcquirerRebalanceDTO;
import com.crmws.dto.AcquirerRebalanceTxnDTO;

public interface AcquirerRebalanceService {

	Optional<List<AcquirerRebalanceDTO>> getAcquirerRebalanceList();

	String acquirerFundTransfer(Map<String, String> actionRequest);

	List<AcquirerRebalanceTxnDTO> getAcquirerTransactions(String type);

	Optional<List<AcquirerRebalanceDTO>> getAcquirerListByAcquirerName(String acquirerName);

	String addAcquirerFund(Map<String, String> actionRequest);

	boolean actionAcquirerBalanceStatus(Map<String, String> actionRequest, String actionStatus);

}
