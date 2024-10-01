package com.pay10.jammuandkashmir;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;

@Service
public class JammuandKashmirBankNBTransformer {
	
	private static Logger logger = LoggerFactory.getLogger(JammuandKashmirBankNBTransformer.class.getName());

	private Transaction transaction = null;
	public JammuandKashmirBankNBTransformer(Transaction transaction) {
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

			if(fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
		logger.info("log for tarsnaction "+transaction.toString());
				logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}","JAMMUANDKASHMIR",transaction.getPaid());
				ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getPaid(), "JAMMUANDKASHMIR");
				logger.info("Error code mapping  : "+errorMappingDTO);
				
				if(null != errorMappingDTO) {
					fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
					fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
					fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
					fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
					fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
					fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
					

				}else {
					logger.info("ErrorCodeMapping not fount for JAMMUANDKASHMIR Acquirer");
					
				}

			

	}
		
			
			if ( fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.STATUS.getName()))
					
			{
				if ((StringUtils.isNotBlank(transaction.getStatus()))) {

					

					if (transaction.getStatus().equalsIgnoreCase("SUCCESS")) {
						status = StatusType.CAPTURED.getName();
						errorType = ErrorType.getInstanceFromCode("000");
						pgTxnMsg = transaction.getStatus();
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
				fields.put(FieldType.STATUS.getName(), status);
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

				fields.put(FieldType.AUTH_CODE.getName(),transaction.getStatus());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

		}
			
	

		fields.put(FieldType.RRN.getName(), transaction.getBid());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getBid());
		
	}
	
	
	
	
}
