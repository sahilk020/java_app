package com.pay10.nodal.payout;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.AccountStatus;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.NodalStatusType;
import com.pay10.commons.util.SettlementStatusType;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.YesBankFT3ResultType;

/**
 * @author Rahul
 *
 */
@Service
public class SettlementTransformer {

	private Transaction transaction = null;

	public SettlementTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		String status = "";
		ErrorType errorType = null;
		if (txnType.equals(SettlementTransactionType.FUND_TRANSFER.getName())) {
			if (fields.get(FieldType.NODAL_ACQUIRER.getName()).equalsIgnoreCase(AcquirerType.KOTAK.getCode())) {
				status = getKotakStatus();
				errorType = getKotakResponseCode();
			} else if (fields.get(FieldType.NODAL_ACQUIRER.getName()).equalsIgnoreCase(AcquirerType.YESBANKFT3.getCode())) {
				status = getYesBankFT3Status();
				errorType = getYesBankFT3ResponseCode();
			} else {
				status = getStatus();
				errorType = getResponseCode();
			}
		} else if (txnType.equals(SettlementTransactionType.STATUS.getName())) {
			if (fields.get(FieldType.NODAL_ACQUIRER.getName()).equalsIgnoreCase(AcquirerType.KOTAK.getCode())) {

				status = getKotakStatusEnqStatus();
				errorType = getKotakStatusEnqResponseCode();

			} else if (fields.get(FieldType.NODAL_ACQUIRER.getName()).equalsIgnoreCase(AcquirerType.YESBANKFT3.getCode())) {

				status = getYesBankFT3StatusEnqStatus();
				errorType = getYesBankFT3StatusEnqResponseCode();

			} else {
				status = getStatusEnqStatus();
				errorType = getStatusEnqResponseCode();
			}

		} else if (txnType.equals(SettlementTransactionType.ADD_BENEFICIARY.getName()) 
					|| txnType.equals(SettlementTransactionType.MODIFY_BENEFICIARY.getName())
					|| txnType.equals(SettlementTransactionType.VERIFY_BENEFICIARY.getName())
					|| txnType.equals(SettlementTransactionType.DISABLE_BENEFICIARY.getName())) {
			status = getAddBeneficiaryStatus();
			errorType = getAddBeneficiaryResponseCode();
		} else {
			status = StatusType.REJECTED.getName();
			errorType = ErrorType.REJECTED;
		}
		if (StatusType.REJECTED.getName().equals(status)) {
			// This is applicable when we sent invalid request to server
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
			fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
		} else {
			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
		}
		
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), transaction.getResponeMessage());
		fields.put(FieldType.RRN.getName(), transaction.getUniqueResponseNo());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getStatusCode());
		fields.put(FieldType.PG_DATE_TIME.getName(), transaction.getTransactionDate());

	}

	private ErrorType getYesBankFT3StatusEnqResponseCode() {
		ErrorType errorType = null;
		
		if(StringUtils.isEmpty(transaction.getStatus())) {
			errorType = ErrorType.FAILED;
			transaction.setStatus(AccountStatus.REJECTED.getCode());
		} else if(transaction.getStatus().equalsIgnoreCase(SettlementStatusType.IN_PROCESS.getName())) {
			errorType = ErrorType.PROCESSING;
		} else if(transaction.getStatus().equalsIgnoreCase(SettlementStatusType.SUCCESS.getName())) {
			errorType = ErrorType.SETTLEMENT_SUCCESSFULL;
		} else if(transaction.getStatus().equalsIgnoreCase(SettlementStatusType.SETTLEMENT_REVERSED.getName())) {
			errorType = ErrorType.SETTLEMENT_REVERSED;
		} else {
			// If not case match then mark txn as failed or processing.
			errorType = ErrorType.FAILED;
		}
		return errorType;
	}

	private String getYesBankFT3StatusEnqStatus() {

		String status = "";
		
		if(StringUtils.isEmpty(transaction.getStatus())) {
			status = NodalStatusType.FAILED.getName();
			transaction.setStatus(AccountStatus.FAILED.getCode());
		}else if(transaction.getStatus().equalsIgnoreCase(SettlementStatusType.IN_PROCESS.getName())) {
			status = NodalStatusType.PROCESSING.getName();
		} else if(transaction.getStatus().equalsIgnoreCase(SettlementStatusType.SUCCESS.getName())) {
			status = NodalStatusType.SETTLED.getName();
		} else if(transaction.getStatus().equalsIgnoreCase(SettlementStatusType.SETTLEMENT_REVERSED.getName())) {
			status = NodalStatusType.SETTLEMENT_REVERSED.getName();
		} else {
			status = NodalStatusType.FAILED.getName();
		}
		return status;
	
	}

	private ErrorType getYesBankFT3ResponseCode() {
		ErrorType errorType = null;
		
		if(StringUtils.isEmpty(transaction.getStatus())) {
			errorType = ErrorType.REJECTED;
			transaction.setStatus(AccountStatus.REJECTED.getCode());
		} else if(transaction.getStatus().equalsIgnoreCase(SettlementStatusType.IN_PROCESS.getName())) {
			errorType = ErrorType.PROCESSING;
		} else {
			errorType = ErrorType.FAILED;
		}
		return errorType;
	}

	private String getYesBankFT3Status() {
		String status = "";
		
		if(StringUtils.isEmpty(transaction.getStatus())) {
			status = NodalStatusType.REJECTED.getName();
			transaction.setStatus(AccountStatus.REJECTED.getCode());
		} else if(transaction.getStatus().equalsIgnoreCase(SettlementStatusType.IN_PROCESS.getName())) {
			status = NodalStatusType.PROCESSING.getName();
		} else {
			// Marking other and duplicate txns as failed.
			status = NodalStatusType.FAILED.getName();
		}
		return status;
	}

	public ErrorType getKotakStatusEnqResponseCode() {
		String result = transaction.getStatusCode();
		ErrorType errorType = null;

		if (StringUtils.isBlank(result)) {
			errorType = ErrorType.REJECTED;
			return errorType;
		}
		if (result.equalsIgnoreCase("U")) {
			errorType = ErrorType.SUCCESS;
		} else if (result.equalsIgnoreCase("DF")) {
			errorType = ErrorType.DECLINED;
		} else if (result.equalsIgnoreCase("Error-99") || result.equalsIgnoreCase("C")) {
			errorType = ErrorType.PROCESSING;
		} else if (result.equalsIgnoreCase("CR")) {
			errorType = ErrorType.INVALID_ACTIVITY;
		} else {
			errorType = ErrorType.REJECTED;
		}
		return errorType;
	}

	public String getKotakStatusEnqStatus() {
		String result = transaction.getStatusCode();
		String status = "";

		if (StringUtils.isBlank(result)) {
			status = StatusType.REJECTED.getName();
			return status;
		}

		if (result.equalsIgnoreCase("U")) {
			status = AccountStatus.SETTLED.getName();
		} else if (result.equalsIgnoreCase("DF") || result.equalsIgnoreCase("CF")) {
			status = AccountStatus.DECLINED.getName();
		} else if (result.equalsIgnoreCase("Error-99") || result.equalsIgnoreCase("C")) {
			status = AccountStatus.IN_PROCESS.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}
		return status;
	}

	public ErrorType getResponseCode() {
		String result = transaction.getStatusCode();
		ErrorType errorType = null;
		if (StringUtils.isBlank(result)) {
			errorType = ErrorType.REJECTED;
			return errorType;
		}
		if (result.equals("AS")) {
			errorType = ErrorType.SUCCESS;
		} else if (result.equals("AD")) {
			errorType = ErrorType.DECLINED;
		} else {
			errorType = ErrorType.REJECTED;
		}
		return errorType;
	}

	public ErrorType getKotakResponseCode() {
		String result = transaction.getStatusCode();
		ErrorType errorType = null;
		if (StringUtils.isBlank(result)) {
			errorType = ErrorType.REJECTED;
			return errorType;
		}
		if (result.equals("000")) {
			errorType = ErrorType.SUCCESS;
		} else if (result.equals("002") || result.equals("004")) {
			errorType = ErrorType.DUPLICATE_ORDER_ID;
		} else if (result.equals("003") || result.equals("008")) {
			errorType = ErrorType.INVALID_ACTIVITY;
		} else if (result.equals("001") || result.equals("009") || result.equals("002")) {
			errorType = ErrorType.DENIED;
		} else {
			errorType = ErrorType.REJECTED;
		}
		return errorType;
	}

	public String getStatus() {
		String result = transaction.getStatusCode();
		String status = "";
		if (StringUtils.isBlank(result)) {
			status = StatusType.REJECTED.getName();
			return status;
		}
		if (result.equals("AS")) {
			status = StatusType.SENT_TO_BANK.getName();
		} else if (result.equals("AD")) {
			status = StatusType.INVALID.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}
		return status;
	}

	public String getKotakStatus() {
		String result = transaction.getStatusCode();
		String status = "";
		if (StringUtils.isBlank(result)) {
			status = StatusType.REJECTED.getName();
			return status;
		}
		if (result.equals("000")) {
			status = StatusType.SENT_TO_BANK.getName();
		} else if (result.equals("003") || result.equals("008")) {
			status = StatusType.INVALID.getName();
		} else if (result.equals("002") || result.equals("004")) {
			status = StatusType.DUPLICATE.getName();
		} else if (result.equals("001") || result.equals("009")) {
			status = StatusType.DENIED.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}
		return status;
	}

	public ErrorType getStatusEnqResponseCode() {
		String result = transaction.getStatusCode();
		ErrorType errorType = null;

		if (StringUtils.isBlank(result)) {
			errorType = ErrorType.REJECTED;
			return errorType;
		}
		if (result.equalsIgnoreCase("COMPLETED")) {
			errorType = ErrorType.SUCCESS;
		} else if (result.equalsIgnoreCase("SENT_TO_BENEFICIARY")) {
			errorType = ErrorType.SUCCESS;
		} else if (result.equalsIgnoreCase("IN_PROCESS")) {
			errorType = ErrorType.SUCCESS;
		} else if (result.equalsIgnoreCase("FAILED")) {
			errorType = ErrorType.DECLINED;
		} else {
			errorType = ErrorType.REJECTED;
		}
		return errorType;
	}

	public String getStatusEnqStatus() {
		String result = transaction.getStatusCode();
		String status = "";

		if (StringUtils.isBlank(result)) {
			status = StatusType.REJECTED.getName();
			return status;
		}

		if (result.equalsIgnoreCase("COMPLETED")) {
			status = AccountStatus.SETTLED.getName();
		} else if (result.equalsIgnoreCase("SENT_TO_BENEFICIARY")) {
			status = AccountStatus.SENT_TO_BENEFICIARY.getName();
		} else if (result.equalsIgnoreCase("IN_PROCESS")) {
			status = AccountStatus.IN_PROCESS.getName();
		} else if (result.equalsIgnoreCase("FAILED")) {
			status = AccountStatus.FAILED.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}
		return status;
	}

	public String getAddBeneficiaryStatus() {
		String result = transaction.getStatus();
		String status = "";
		if (StringUtils.isBlank(result)) {
			status = StatusType.REJECTED.getName();
			return status;
		}
		if (result.equalsIgnoreCase("SUCCESS")) {
			status = StatusType.SUCCESS.getName();
		} else if (result.equalsIgnoreCase("FAILURE")) {
			status = StatusType.FAILED.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}
		return status;
	}

	public ErrorType getAddBeneficiaryResponseCode() {
		String result = transaction.getStatus();
		ErrorType errorType = null;

		if (StringUtils.isBlank(result)) {
			errorType = ErrorType.REJECTED;
			return errorType;
		}
		if (result.equalsIgnoreCase("SUCCESS")) {
			errorType = ErrorType.SUCCESS;
		} else if (result.equalsIgnoreCase("FAILURE")) {
			errorType = ErrorType.FAILED;
		} else {
			errorType = ErrorType.REJECTED;
		}
		return errorType;
	}
}
