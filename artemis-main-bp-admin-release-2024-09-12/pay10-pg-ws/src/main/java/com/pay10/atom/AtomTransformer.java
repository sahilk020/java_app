package com.pay10.atom;

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
public class AtomTransformer {
	private static Logger logger = LoggerFactory.getLogger(AtomTransformer.class.getName());

	private Transaction transaction = null;

	public AtomTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {
		ErrorType errorType = null;
		String pgTxnMsg = null;

		logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}", "ATOM", transaction.getF_code());

		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getF_code(), "ATOM");
		logger.info("Error code mapping  : " + errorMappingDTO);

		if (null != errorMappingDTO) {
			fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getDesc());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
			fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getMmp_txn());
			fields.put(FieldType.RRN.getName(), transaction.getBank_txn());
		} else {
			logger.info("ErrorCodeMapping not fount for ATOM Acquirer");
		}
	}

	public void updateResponseOld(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getF_code())) && ((transaction.getF_code()).equalsIgnoreCase("Ok")))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())
				&& (StringUtils.isNotBlank(transaction.getF_code()))
				&& ((transaction.getF_code()).equalsIgnoreCase("00")))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())
				&& (StringUtils.isNotBlank(transaction.getF_code()))
				&& ((transaction.getF_code()).equalsIgnoreCase("01")))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else {
			if ((StringUtils.isNotBlank(transaction.getF_code()))) {

				AtomResultType resultInstance = AtomResultType.getInstanceFromName(transaction.getF_code());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
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
		//fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getDesc());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		if (StringUtils.isNotBlank(transaction.getAuth_code())) {
			fields.put(FieldType.AUTH_CODE.getName(), transaction.getAuth_code());
		}

		else {
			fields.put(FieldType.AUTH_CODE.getName(), "0");
		}

		fields.put(FieldType.RRN.getName(), transaction.getBank_txn());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getMmp_txn());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getF_code());

		if (StringUtils.isNotBlank(transaction.getStatusMessage())) {
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatusMessage());
		} else {
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
		}

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

}
