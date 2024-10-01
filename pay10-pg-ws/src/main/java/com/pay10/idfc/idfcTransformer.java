package com.pay10.idfc;

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
public class idfcTransformer {
	private static Logger logger = LoggerFactory.getLogger(idfcTransformer.class.getName());

	private Transaction transaction = null;

	public idfcTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	public void updateResponse(Fields fields){

		logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}","IDFC",transaction.getResponseCode());
		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getResponseCode(), "IDFC");
		logger.info("Error code mapping  : " + errorMappingDTO);
		
		if(null != errorMappingDTO) {
			fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
			fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
			fields.put(FieldType.AUTH_CODE.getName(), transaction.getResponseCode());
			fields.put(FieldType.RRN.getName(), transaction.getBID());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getBID());
		}else {
			logger.info("ErrorCodeMapping not fount for IDFC Acquirer");
		}
		




	}
	
}
