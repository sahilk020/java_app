package com.pay10.axisbank.netbanking;

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
public class AxisBankNBTransformer {
	private static Logger logger = LoggerFactory.getLogger(AxisBankNBTransformer.class.getName());

	private Transaction transaction = null;

	public AxisBankNBTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	public void updateResponse(Fields fields){

		logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}","AXISBANKNB",transaction.getStatFlg());
		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getStatFlg(), "AXISBANKNB");
		logger.info("Error code mapping  : " + errorMappingDTO);
		
		if(null != errorMappingDTO) {
			fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
			fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
			fields.put(FieldType.AUTH_CODE.getName(), transaction.getItc());
			fields.put(FieldType.RRN.getName(), transaction.getBid());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getBid());
		}else {
			logger.info("ErrorCodeMapping not fount for AXISBANKNB Acquirer");
		}
		




	}
	public void updateResponseOld(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

			
			if ( fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.STATUS.getName()) && (StringUtils.isNotBlank(transaction.getStatFlg()))
					&& ((transaction.getStatFlg()).equalsIgnoreCase("S")))
			{
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

			}
		
		else if ((StringUtils.isNotBlank(transaction.getPaid()))
				&& (StringUtils.isNotBlank(transaction.getStatFlg()))
				&& ((transaction.getPaid()).equalsIgnoreCase("Y"))
				&& ((transaction.getStatFlg()).equalsIgnoreCase("S"))
				&& !fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.STATUS.getName()))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else {
			if ((StringUtils.isNotBlank(transaction.getStatFlg()))) {

				AxisBankNBResultType resultInstance = AxisBankNBResultType
						.getInstanceFromName(transaction.getStatFlg());

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
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getItc());

		fields.put(FieldType.RRN.getName(), transaction.getBid());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getBid());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getStatFlg());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getPaid());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

}
