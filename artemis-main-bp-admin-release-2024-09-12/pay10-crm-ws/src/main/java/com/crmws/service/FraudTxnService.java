package com.crmws.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.crmws.dto.FraudTransactionDTO;

public interface FraudTxnService {

	Optional<List<FraudTransactionDTO>> getFraudCapturedTransaction();

	boolean updateFraudTransactionAction(Map<String, String> actionRequest);

	void notifyToMerchant(Map<String, String> actionRequest);

	void updateFraudTransactionRule(Map<String, String> actionRequest);

}
