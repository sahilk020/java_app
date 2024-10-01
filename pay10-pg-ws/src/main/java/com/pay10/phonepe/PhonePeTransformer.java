package com.pay10.phonepe;

import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class PhonePeTransformer {

	private static Logger logger = LoggerFactory.getLogger(PhonePeTransformer.class.getName());

	private Transaction transaction = null;

	public PhonePeTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {

		logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}", "PHONEPE", transaction.getCode());

		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getCode(),
				"PHONEPE");
		logger.info("Error code mapping  : " + errorMappingDTO);

		if (null != errorMappingDTO) {
			fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
			fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
			fields.put(FieldType.RRN.getName(), transaction.getProviderReferenceId());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getProviderReferenceId());
		} else {
			logger.info("ErrorCodeMapping not fount for PHONEPE Acquirer");
		}

	}

	public void updateResponseOld(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if (!(transaction.getHashCodeAcquirer().equals(transaction.getHashCodePg()))) {

			logger.info("CheckSum mismatch for Phone Pe Sale Response");
			status = StatusType.AUTHENTICATION_FAILED.getName();
			errorType = ErrorType.SIGNATURE_MISMATCH;
			pgTxnMsg = ErrorType.SIGNATURE_MISMATCH.getResponseMessage();

		}

		else if ((StringUtils.isNotBlank(transaction.getCode()))
				&& ((transaction.getCode()).equalsIgnoreCase("PAYMENT_SUCCESS"))
				&& fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName()))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
		}

		else {
			if ((StringUtils.isNotBlank(transaction.getCode()))) {

				String respCode = null;
				if (StringUtils.isNotBlank(transaction.getCode())) {
					respCode = transaction.getCode();
				}

				PhonePeResultType resultInstance = PhonePeResultType.getInstanceFromName(respCode);

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.getInstanceFromCode("007");
					pgTxnMsg = "Transaction Declined";

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				pgTxnMsg = "Transaction Rejected";
			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.RRN.getName(), transaction.getProviderReferenceId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getProviderReferenceId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

	public void updateRefundResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		// For Refund
		if (StringUtils.isNotBlank(transaction.getSuccess()) && StringUtils.isNotBlank(transaction.getCode())
				&& transaction.getSuccess().equalsIgnoreCase("true")
				&& transaction.getCode().equalsIgnoreCase("PAYMENT_SUCCESS"))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else {
			if ((StringUtils.isNotBlank(transaction.getCode())) || (StringUtils.isNotBlank(transaction.getCode()))) {

				String respCode = null;
				respCode = transaction.getCode();
				PhonePeResultType resultInstance = null;

				// In case of code PaymentError , check for error result type using
				// PayResponseCode
				if (respCode.equalsIgnoreCase("PAYMENT_ERROR")) {
					if (StringUtils.isNotBlank(transaction.getPayResponseCode())) {
						resultInstance = PhonePeResultType.getInstanceFromName(transaction.getPayResponseCode());
					}

					if (null == resultInstance) {
						resultInstance = PhonePeResultType.getInstanceFromName(respCode);
					}

				} else {
					resultInstance = PhonePeResultType.getInstanceFromName(respCode);
				}

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

					if (StringUtils.isNotBlank(transaction.getMessage())) {
						pgTxnMsg = transaction.getMessage();
					}

					else {
						pgTxnMsg = resultInstance.getMessage();
					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.getInstanceFromCode("007");

					if (StringUtils.isNotBlank(transaction.getMessage())) {
						pgTxnMsg = transaction.getMessage();
					}

					else {
						pgTxnMsg = "Transaction Declined";
					}

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				if (StringUtils.isNotBlank(transaction.getMessage())) {
					pgTxnMsg = transaction.getMessage();
				} else {
					pgTxnMsg = "Transaction Rejected";
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.RRN.getName(), transaction.getProviderReferenceId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getProviderReferenceId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getCode());

		if (StringUtils.isNotBlank(transaction.getPayResponseCode())) {
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getPayResponseCode());
		}

		else if (StringUtils.isNotBlank(transaction.getStatus())) {
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());
		}

		else {
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
		}

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

}
