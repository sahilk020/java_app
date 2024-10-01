package com.pay10.TFP;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;

@Service
public class TFPTransformer {

	private static Logger logger = LoggerFactory.getLogger(TFPTransformer.class.getName());

	public void updateResponse(Fields fields, Map<String, String> callBackResponse) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
			logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}", "TFP",
					callBackResponse.get(FieldType.STATUS.getName()));
			ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl()
					.getErrorMappingByAcqCode(callBackResponse.get(FieldType.STATUS.getName()), "TFP");
			logger.info("Error code mapping  : " + errorMappingDTO);

			if (null != errorMappingDTO) {
				fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
				fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
				fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
				fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());

			} else {
				logger.info("ErrorCodeMapping not fount for JAMMUANDKASHMIR Acquirer");

			}

			fields.put(FieldType.RRN.getName(), callBackResponse.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQ_ID.getName(), callBackResponse.get(FieldType.RRN.getName()));

		}
	}

	
	public void updateResponseStatus(Fields fields, JSONObject callBackResponse) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
			logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}", "TFP",
					callBackResponse.getString(FieldType.STATUS.getName()));
			ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl()
					.getErrorMappingByAcqCode(callBackResponse.getString(FieldType.STATUS.getName()), "TFP");
			logger.info("Error code mapping  : " + errorMappingDTO);

			if (null != errorMappingDTO) {
				fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
				fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
				fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
				fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());

			} else {
				logger.info("ErrorCodeMapping not fount for JAMMUANDKASHMIR Acquirer");

			}

			fields.put(FieldType.RRN.getName(), callBackResponse.getString(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQ_ID.getName(), callBackResponse.getString(FieldType.RRN.getName()));

		}
	}

}
