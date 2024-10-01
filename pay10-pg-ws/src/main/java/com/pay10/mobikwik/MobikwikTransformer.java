package com.pay10.mobikwik;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;

@Service
public class MobikwikTransformer {

	private Transaction transaction = null;

	public MobikwikTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getStatuscode()))
				&& ((transaction.getStatuscode()).equalsIgnoreCase("0")))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;

			if (StringUtils.isNotBlank(transaction.getStatus())) {
				pgTxnMsg = transaction.getStatus();
			} else {
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			}

		}


		else {
			if ((StringUtils.isNotBlank(transaction.getStatuscode()))) {

				MobikwikResultType resultInstance = MobikwikResultType.getInstanceFromName(transaction.getStatuscode());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

					if (StringUtils.isNotBlank(transaction.getStatus())) {
						pgTxnMsg = transaction.getStatus();
					}

					else {
						pgTxnMsg = resultInstance.getMessage();
					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.getInstanceFromCode("007");

					if (StringUtils.isNotBlank(transaction.getStatus())) {
						pgTxnMsg = transaction.getStatus();
					}

					else {
						pgTxnMsg = "Transaction Declined";
					}

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				if (StringUtils.isNotBlank(transaction.getStatus())) {
					pgTxnMsg = transaction.getStatus();
				} else {
					pgTxnMsg = "Transaction Rejected";
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.ACQ_ID.getName(), transaction.getRefId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getStatuscode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}
	
	
	public void updateSaleResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getResponseCode()))
				&& ((transaction.getResponseCode()).equalsIgnoreCase("0")))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;

			if (StringUtils.isNotBlank(transaction.getResponseDescription())) {
				pgTxnMsg = transaction.getResponseDescription();
			} else {
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			}

		}


		else {
			if ((StringUtils.isNotBlank(transaction.getResponseCode()))) {

				MobikwikResultType resultInstance = MobikwikResultType.getInstanceFromName(transaction.getResponseCode());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

					if (StringUtils.isNotBlank(transaction.getResponseDescription())) {
						pgTxnMsg = transaction.getResponseDescription();
					}

					else {
						pgTxnMsg = resultInstance.getMessage();
					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.getInstanceFromCode("007");

					if (StringUtils.isNotBlank(transaction.getResponseDescription())) {
						pgTxnMsg = transaction.getResponseDescription();
					}

					else {
						pgTxnMsg = "Transaction Declined";
					}

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				if (StringUtils.isNotBlank(transaction.getResponseDescription())) {
					pgTxnMsg = transaction.getResponseDescription();
				} else {
					pgTxnMsg = "Transaction Rejected";
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.ACQ_ID.getName(), transaction.getPgTransId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getStatuscode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

}
