package com.pay10.billdesk;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class BilldeskTransformer {

	private Transaction transaction = null;

	public BilldeskTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {
		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;

			if ((StringUtils.isNotBlank(transaction.getAuthStatus()))
					&& ((transaction.getAuthStatus()).equals("0300"))) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

			} else {
				if (StringUtils.isNotBlank(transaction.getAuthStatus())) {
					
						BilldeskResultType resultInstance = BilldeskResultType
								.getInstanceFromName(transaction.getAuthStatus());
						if ((resultInstance != null)) {
							status = resultInstance.getStatusName();
							errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
							pgTxnMsg = resultInstance.getMessage();
						} else {
							status = StatusType.FAILED_AT_ACQUIRER.getName();
							errorType = ErrorType.getInstanceFromCode("022");
							pgTxnMsg = "Transaction failed at acquirer";
						}
					

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

				}
			}
		 

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getAuthStatus());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnReferenceNo());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getAuthStatus());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getBankReferenceNo());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getErrorStatus());

	}
	
	
	public void updateStatusResponse(Fields fields) {
		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;

			if ((StringUtils.isNotBlank(transaction.getAuthStatus()))
					&& ((transaction.getAuthStatus()).equals("0300"))) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

			} else {
				if (StringUtils.isNotBlank(transaction.getAuthStatus())) {
					
						BilldeskResultType resultInstance = BilldeskResultType
								.getInstanceFromName(transaction.getAuthStatus());
						if ((resultInstance != null)) {
							status = resultInstance.getStatusName();
							errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
							pgTxnMsg = resultInstance.getMessage();
						} else {
							
							status = StatusType.FAILED_AT_ACQUIRER.getName();
							errorType = ErrorType.getInstanceFromCode("022");
							pgTxnMsg = "Transaction failed at acquirer";
							
							
							if (StringUtils.isNoneBlank(transaction.getErrorDescription()) ) {
								pgTxnMsg = transaction.getErrorDescription();
							}
						}
					

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
					if (StringUtils.isNoneBlank(transaction.getErrorDescription()) ) {
						pgTxnMsg = transaction.getErrorDescription();
					}

				}
			}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getAuthStatus());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnReferenceNo());
		
		if (StringUtils.isNoneBlank(transaction.getErrorStatus()) ) {
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getAuthStatus());
		}
		else {
			errorType.getResponseCode();
		}
		
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getBankReferenceNo());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getErrorStatus());

	}
	
	
	public void updateRefundResponse(Fields fields) {
		
		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;

			if ((StringUtils.isNotBlank(transaction.getRefStatus()))
					&& (((transaction.getRefStatus()).equals("0699")) || (transaction.getRefStatus()).equals("0799"))) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

			} else {
				if (StringUtils.isNotBlank(transaction.getRefStatus())) {
					
						BilldeskResultType resultInstance = BilldeskResultType
								.getInstanceFromName(transaction.getRefStatus());
						if ((resultInstance != null)) {
							status = resultInstance.getStatusName();
							errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
							pgTxnMsg = resultInstance.getMessage();
							if (StringUtils.isNoneBlank(transaction.getErrorDescription()) ) {
								pgTxnMsg = transaction.getErrorDescription();
							}
						} else {
							status = StatusType.FAILED_AT_ACQUIRER.getName();
							errorType = ErrorType.getInstanceFromCode("022");
							pgTxnMsg = "Transaction failed at acquirer";
							if (StringUtils.isNoneBlank(transaction.getErrorDescription()) ) {
								pgTxnMsg = transaction.getErrorDescription();
							}
						}
					

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
					if (StringUtils.isNoneBlank(transaction.getErrorDescription()) ) {
						pgTxnMsg = transaction.getErrorDescription();
					}

				}
			}
			
			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

			fields.put(FieldType.AUTH_CODE.getName(), transaction.getAuthStatus());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getBankReferenceNo());
			
			if (StringUtils.isNoneBlank(transaction.getErrorCode()) ) {
				fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getErrorCode());
			}
			else {
				errorType.getResponseCode();
			}
			
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
			fields.put(FieldType.RRN.getName(), transaction.getBankReferenceNo());
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getErrorDescription());
		}
	
	public void updateUpiResponse(Fields fields, Transaction transaction) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getAuthStatus())) && (StringUtils.isNotBlank(transaction.getErrorStatus())) && (StringUtils.isNotBlank(transaction.getErrorDescription()))
				&& ((transaction.getAuthStatus()).equalsIgnoreCase("0002"))	&& ((transaction.getErrorStatus()).equalsIgnoreCase("NA")) && ((transaction.getErrorDescription()).equalsIgnoreCase("0002")))

		{

			if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName())) {
				status = StatusType.SENT_TO_BANK.getName();
			}

			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else {
			status = StatusType.REJECTED.getName();
			errorType = ErrorType.REJECTED;
			
			if (StringUtils.isNotBlank(transaction.getErrorStatus())) {
				pgTxnMsg = transaction.getErrorStatus();
			}
			else {
				pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
			}
			
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		if (StringUtils.isNotBlank(transaction.getTxnReferenceNo())) {
			fields.put(FieldType.AUTH_CODE.getName(), transaction.getTxnReferenceNo());
		}

		else {
			fields.put(FieldType.AUTH_CODE.getName(), "0");
		}

		fields.put(FieldType.RRN.getName(), transaction.getBankReferenceNo());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnReferenceNo());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getAuthStatus());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getAuthStatus());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}
	
}
