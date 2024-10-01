package com.pay10.sbi.card;

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

@Service
public class SbiCardTransformer {
	private static Logger logger = LoggerFactory.getLogger(SbiCardTransformer.class.getName());

	private Transaction transaction = null;

	public SbiCardTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}



	public void updateResponse(Fields fields)
	{   ErrorType errorType = null;
		//logger.info("errorMappingService :  "+ errorMappingService);
		logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}","SBICARD",transaction.getStatus());
		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getStatus(), "SBICARD");
		logger.info("Error code mapping  : "+errorMappingDTO);
		
		if(null != errorMappingDTO) {
			fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
			fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getAcqId());
			logger.info("RRN number for sbi card "+transaction.getRef());
			if(StringUtils.isNotBlank(transaction.getRef())){
				fields.put(FieldType.RRN.getName(),transaction.getRef());
			}
		}else {
			logger.info("ErrorCodeMapping not fount for SBI Acquirer");
			
		}
		

	}

	public void updateResponseOld(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;
		if ((StringUtils.isNotBlank(transaction.getStatus()))
				&& ((transaction.getStatus()).equalsIgnoreCase("Success"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
		} else if (((transaction.getStatus()).equalsIgnoreCase("Failure"))){
			//	&& ((transaction.getResponseMessage()).equalsIgnoreCase("Failure. Transaction not attempted"))) {
			status = StatusType.CANCELLED.getName();
			errorType = ErrorType.CANCELLED;
			pgTxnMsg = ErrorType.CANCELLED.getResponseMessage();
		} else {
			if (StringUtils.isNotBlank(transaction.getStatus())) {
				SbiResultType resultInstance = SbiResultType.getInstanceFromName(transaction.getStatus());

				if (resultInstance == null) {

					if (StringUtils.isNotBlank(transaction.getError_code_tag())) {

						if (transaction.getError_code_tag().length() >= 11) {
							String errorCodeTag = transaction.getError_code_tag().substring(0, 11);
							resultInstance = SbiResultType.getInstanceFromName(errorCodeTag);
						}

					}

				}

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getPGCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.DECLINED.getName();
					errorType = ErrorType.getInstanceFromCode("004");
					pgTxnMsg = "Transaction Declined by acquirer";
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

		fields.put(FieldType.ACQ_ID.getName(), transaction.getAcqId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), transaction.getResponseMessage());

	}

	public void updateStatusResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;
		if ((StringUtils.isNotBlank(transaction.getResult()))
				&& ((transaction.getResult()).equalsIgnoreCase("SUCCESS"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else {
			if (StringUtils.isNotBlank(transaction.getResult())) {
				SbiResultType resultInstance = SbiResultType.getInstanceFromName(transaction.getResult());

				if (resultInstance == null) {

					if (StringUtils.isNotBlank(transaction.getError_code_tag())) {

						if (transaction.getError_code_tag().length() >= 11) {
							String errorCodeTag = transaction.getError_code_tag().substring(0, 11);
							resultInstance = SbiResultType.getInstanceFromName(errorCodeTag);
						}

					}

				}

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getPGCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.DECLINED.getName();
					errorType = ErrorType.getInstanceFromCode("004");
					pgTxnMsg = "Transaction Declined by acquirer";
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

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getAuth());
		// fields.put(FieldType.RRN.getName(), transaction.getRef());
		fields.put(FieldType.AVR.getName(), transaction.getAvr());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTranId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getError_code_tag());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getResult());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getPayId());
	}

}
