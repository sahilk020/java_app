package com.pay10.payout.quomopay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.user.ErrorCodeMappingPayout;
import com.pay10.commons.user.ErrorCodeMappingPayoutDao;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.cosmos.CosmosUpiTransformer;

@Service
public class QuomoPayoutResponseHandler {
	
	private static Logger logger = LoggerFactory.getLogger(QuomoPayoutResponseHandler.class.getName());

	
	@Autowired
	ErrorCodeMappingPayoutDao errorCodeMappingPayoutDao;

	public void getErrorCodeMappingPayout(Fields fields, String acquirerCode) {
		ErrorCodeMappingPayout data=errorCodeMappingPayoutDao.getErrorCodeByAcquirer(fields.get(FieldType.ACQUIRER_TYPE.getName()), acquirerCode);
		
	if(!(data==null)) {
		fields.put(FieldType.STATUS.getName(), data.getPgStatus());
		//fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), data.getPgMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), data.getPgCode());
		fields.put(FieldType.PG_RESP_CODE.getName(), data.getAcqStatuscode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), data.getAcqMessage());
		fields.put(FieldType.PG_TXN_STATUS.getName(), data.getPgStatus());
	}else {
		logger.info("Error Mapping is not find for Acquirer={},Errorcode={}",fields.get(FieldType.ACQUIRER_TYPE.getName()),acquirerCode);
	}
		
	}
	


}
