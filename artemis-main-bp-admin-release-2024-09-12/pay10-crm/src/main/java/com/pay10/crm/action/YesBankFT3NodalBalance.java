package com.pay10.crm.action;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.NodalAccountDetailsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.NodalAccountDetails;
import com.pay10.commons.util.AcquirerTypeNodal;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.SettlementTransactionType;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class YesBankFT3NodalBalance extends AbstractSecureAction{

	private static final long serialVersionUID = 7430521319759081123L;
	private static Logger logger = LoggerFactory.getLogger(YesBankFT3NodalBalance.class.getName());
	
	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;
	
	@Autowired
	NodalAccountDetailsDao nodalAccountDetailsDao;
	
	private String acquirer;
	private String response;
	private String balance;
	private String currencyCode;
	
	public String execute() {
		logger.info("Getting balance..");
		Fields fields = new Fields();
		try {
			fields.put(FieldType.TXNTYPE.getName(), SettlementTransactionType.FUND_CONFIRMATION.getName());
			fields.put(FieldType.NODAL_ACQUIRER.getName(), AcquirerTypeNodal.YESBANKFT3.getCode());
			
			NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(fields.get(FieldType.NODAL_ACQUIRER.getName()));
			if(nodalAccountDetails == null) {
				logger.error("Invalid nodal acquirer type");
				setResponse("failed");
				return SUCCESS;
			}
			
			fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), nodalAccountDetails.getCustId());
			fields.put(FieldType.SRC_ACCOUNT_NO.getName(), nodalAccountDetails.getAccountNumber());
			Map<String, String> responseMap = transactionControllerServiceProvider.nodalSettlementTransact(fields);
			logger.info("Response for nodal fund confirmation : " + responseMap);
			if(responseMap.containsKey("response") && responseMap.get("response").equalsIgnoreCase("success")) {
				setResponse(responseMap.get("response"));
				setBalance(responseMap.get("balance"));
				setCurrencyCode(nodalAccountDetails.getCurrencyCode());
			}
			else {
				setResponse("failed");
			}
			return SUCCESS;
		} catch (SystemException e) {
			logger.error("Exception in get Nodal balance");
			setResponse("failed");
			logger.error(e.getMessage());
		}
		return SUCCESS;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
}
