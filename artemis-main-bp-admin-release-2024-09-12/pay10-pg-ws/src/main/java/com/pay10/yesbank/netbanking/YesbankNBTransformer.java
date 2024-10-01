package com.pay10.yesbank.netbanking;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;

@Service
public class YesbankNBTransformer {

	private Transaction transaction = null;

	public YesbankNBTransformer(Transaction transaction) {
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
		if ((StringUtils.isNotBlank(transaction.getBankRefNo())) && (!(transaction.getBankRefNo()).equals("0"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else {
			if (StringUtils.isNotBlank(transaction.getMessage())) {

				YesbankNBResultType resultInstance = YesbankNBResultType.getInstanceFromName(transaction.getMessage());
				if ((resultInstance != null)) {
					status = resultInstance.getStatusName();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

					if (StringUtils.isNotBlank(transaction.getMessage())) {
						pgTxnMsg = transaction.getMessage();
					} else {
						pgTxnMsg = resultInstance.getMessage();
					}

				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");

					if (StringUtils.isNotBlank(transaction.getMessage())) {
						pgTxnMsg = transaction.getMessage();
					} else {
						pgTxnMsg = "Transaction failed at acquirer";
					}

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;

				if (StringUtils.isNotBlank(transaction.getMessage())) {
					pgTxnMsg = transaction.getMessage();
				} else {
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
		
//		fields.put(FieldType.UDF1.getName(), transaction.getMerchantTimeStamp());
		fields.put(FieldType.AUTH_CODE.getName(), transaction.getBankRefNo());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getBankRefNo());
		fields.put(FieldType.PG_RESP_CODE.getName(), "");
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getBankRefNo());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getMessage());

	}
	
	
	public void updateStatusResponse(Fields fields) {
		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;
		if ((StringUtils.isNotBlank(transaction.getBankRefNo())) && (!(transaction.getBankRefNo()).equals("0"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else {
			if (StringUtils.isNotBlank(transaction.getMessage())) {

				YesbankNBResultType resultInstance = YesbankNBResultType.getInstanceFromName(transaction.getMessage());
				if ((resultInstance != null)) {
					status = resultInstance.getStatusName();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

					if (StringUtils.isNotBlank(transaction.getMessage())) {
						pgTxnMsg = transaction.getMessage();
					} else {
						pgTxnMsg = resultInstance.getMessage();
					}

				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");

					if (StringUtils.isNotBlank(transaction.getMessage())) {
						pgTxnMsg = transaction.getMessage();
					} else {
						pgTxnMsg = "Transaction failed at acquirer";
					}

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;

				if (StringUtils.isNotBlank(transaction.getMessage())) {
					pgTxnMsg = transaction.getMessage();
				} else {
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getBankRefNo());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getBankRefNo());
		fields.put(FieldType.PG_RESP_CODE.getName(), "");
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getBankRefNo());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getMessage());

	}

}
