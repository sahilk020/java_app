package com.pay10.htpay;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;

@Service
public class Htpaytransformer {
	private static Logger logger = LoggerFactory.getLogger(Htpaytransformer.class.getName());
	private Transaction transaction = null;

	public Htpaytransformer(Transaction transactionResponse) {
		this.transaction = transactionResponse;
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
		
		
	
			logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}","HTPAY",transaction.getStatus());
			ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getStatus(),"HTPAY");
			logger.info("Error code mapping  : "+errorMappingDTO);
			
			if(null != errorMappingDTO) {
				fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
				//fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
				fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
				fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
				fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
					
					fields.put(FieldType.RRN.getName(), transaction.getPforderno());

						fields.put(FieldType.ACQ_ID.getName(), transaction.getPforderno());
						
					
			}else {
				logger.info("ErrorCodeMapping not fount for COSMOS Acquirer");
				
			}

		


	}
	
	

}
