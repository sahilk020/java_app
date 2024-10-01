package com.pay10.crm.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.BeneficiaryAccountsDao;
import com.pay10.commons.dao.NodalTxnReports;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.user.NodalTransactions;
import com.pay10.commons.util.AccountStatus;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class MerchantFetchNodalTransferHistoryAction extends AbstractSecureAction {


	private static final long serialVersionUID = -7131628278175215188L;

	private static Logger logger = LoggerFactory.getLogger(MerchantFetchNodalTransferHistoryAction.class.getName());

	@Autowired
	private NodalTxnReports txnReports;

	@Autowired
	BeneficiaryAccountsDao beneficiaryAccountsDao;

	private String txnId;
	private String response;
	private String merchantBusinessName;
	private String merchantProvidedName;
	private String merchantProvidedId;
//	private String acquirer;
	private String mobileNo;
	private String emailId;
	private String txnDate;
	private String beneAccountNo;
	private String ifscCode;
	private String amount;
	private String utr;
	private String paymentType;
	private String status;
	private String bankName;
	private String orderId;
	private String beneType;

	@Override
	public String execute() {
		// Check whether user type is merchant
		try {
			NodalTransactions txn = txnReports.getTransactionByOrderIdAndStatus(txnId);
			if (txn == null) {
				logger.info("Transaction not found.");
				setResponse("Transaction not found.");
				return SUCCESS;
			}
			setMerchantBusinessName(txn.getMerchantBusinessName());
//			setAcquirer(txn.getAcquirer());
			setMobileNo(txn.getMobile());
			setEmailId(txn.getEmail());
			setTxnDate(txn.getCreatedDate());
			setAmount(txn.getAmount());
			setUtr(txn.getRrn());
			setPaymentType(txn.getPaymentType());
			setStatus(txn.getStatus());
			setOrderId(txn.getOrderId());
			setBankName(bankName);
			if(txn.getBeneType() != null) {
				setBeneType(txn.getBeneType().getCode());
			} else {
				setBeneType("NA");
			}
			List<BeneficiaryAccounts> beneficiaryAccountsList = beneficiaryAccountsDao
					.findByBeneficiaryCdAndStatusAndPaymentTypeAndPayId(txn.getBeneficiaryCode(), txn.getPayId(),
							paymentType, "ALL");
			BeneficiaryAccounts beneAccount = null;
			for (BeneficiaryAccounts bene : beneficiaryAccountsList) {
				if (bene.getStatus().equals(AccountStatus.ACTIVE) || bene.getStatus().equals(AccountStatus.EXPIRE)) {
					beneAccount = bene;
					break;
				}
			}

			if (beneAccount == null) {
				setMerchantProvidedId(txn.getBeneAccNo());
				setMerchantProvidedName(txn.getBeneficiaryName());
				setBeneAccountNo("NA");
				setIfscCode("NA");
				setBankName("NA");
			} else {
				setMerchantProvidedId(beneAccount.getMerchantProvidedId());
				setMerchantProvidedName(beneAccount.getMerchantProvidedName());
				setBeneAccountNo(beneAccount.getBeneAccountNo());
				setIfscCode(beneAccount.getIfscCode());
				setBankName(beneAccount.getBankName());
			}
			setResponse("");
		} catch (Exception e) {
			logger.error("Exception while searching txn.");
			logger.error(e.getMessage());
			setResponse("Transaction not found.");
		}
		return SUCCESS;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getMerchantBusinessName() {
		return merchantBusinessName;
	}

	public void setMerchantBusinessName(String merchantBusinessName) {
		this.merchantBusinessName = merchantBusinessName;
	}

	public String getMerchantProvidedName() {
		return merchantProvidedName;
	}

	public void setMerchantProvidedName(String merchantProvidedName) {
		this.merchantProvidedName = merchantProvidedName;
	}

	public String getMerchantProvidedId() {
		return merchantProvidedId;
	}

	public void setMerchantProvidedId(String merchantProvidedId) {
		this.merchantProvidedId = merchantProvidedId;
	}

//	public String getAcquirer() {
//		return acquirer;
//	}
//
//	public void setAcquirer(String acquirer) {
//		this.acquirer = acquirer;
//	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}

	public String getBeneAccountNo() {
		return beneAccountNo;
	}

	public void setBeneAccountNo(String beneAccountNo) {
		this.beneAccountNo = beneAccountNo;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getBeneType() {
		return beneType;
	}

	public void setBeneType(String beneType) {
		this.beneType = beneType;
	}
}
