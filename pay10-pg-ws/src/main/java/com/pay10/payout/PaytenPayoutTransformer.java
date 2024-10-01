package com.pay10.payout;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;
import com.pay10.sbi.card.SbiCardTransformer;

@Service
public class PaytenPayoutTransformer {
	private static Logger logger = LoggerFactory.getLogger(PaytenPayoutTransformer.class.getName());

	private Transaction transaction = null;

	public PaytenPayoutTransformer(Transaction transaction) {
		this.transaction = transaction;
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
		
		
		
//		if ((StringUtils.isNotBlank(transaction.getStatus())) && ((transaction.getStatus()).equals("SUCCESS"))) {
//			status = StatusType.CAPTURED.getName();
//			errorType = ErrorType.SUCCESS;
//			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
//
//	
//				
//				} else {
//					status = StatusType.FAILED_AT_ACQUIRER.getName();
//					errorType = ErrorType.getInstanceFromCode("022");
//
//					if (StringUtils.isNotBlank(transaction.getStatus())) {
//						pgTxnMsg = transaction.getStatus();
//					} else {
//						pgTxnMsg = "Transaction failed at acquirer";
//					}
//	
//		}
			//logger.info("errorMappingService :  "+ errorMappingService);
			logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}","COSMOS",transaction.getStatus());
			ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getStatus(), "COSMOS");
			logger.info("Error code mapping  : "+errorMappingDTO);
			
			if(null != errorMappingDTO) {
				fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
				//fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
				fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
				fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
				if ( StringUtils.isNotEmpty( transaction.getAuth_code())) {
					fields.put(FieldType.AUTH_CODE.getName(), transaction.getAuth_code());
					}
					fields.put(FieldType.RRN.getName(), transaction.getRrn());
					if (StringUtils.isNotBlank(transaction.getAcq_id())) {

						fields.put(FieldType.ACQ_ID.getName(), transaction.getAcq_id());
						}
					
					fields.put(FieldType.CARD_MASK.getName(), transaction.getMerchantVPA());
			}else {
				logger.info("ErrorCodeMapping not fount for COSMOS Acquirer");
				
			}

		


	}
	
	
	public void updateUpiIntentStatus(Fields feilds,String response) {
            JSONObject statusResponseJson = new JSONObject(response);
			  JSONArray data = statusResponseJson.getJSONArray("data");

            String status = "";
    		ErrorType errorType = null;
    		String pgTxnMsg = null;
                if (org.apache.commons.lang.StringUtils.isNotBlank(data.getJSONObject(0).getString("respMessge"))&&data.getJSONObject(0).getString("respMessge").equalsIgnoreCase("SUCCESS")&&data.getJSONObject(0).getString("respCode").equalsIgnoreCase("0")) {
		status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

	
				
				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");

					if (StringUtils.isNotBlank(data.getJSONObject(0).getString("respMessge"))) {
						pgTxnMsg = data.getJSONObject(0).getString("respMessge");
					} else {
						pgTxnMsg = "Transaction failed at acquirer";
					}
	
		}
                
                if (!data.getJSONObject(0).getString("upiId").isEmpty()) {

            		feilds.put(FieldType.CARD_MASK.getName(), data.getJSONObject(0).getString("upiId"));

            		}
                
            		if (!data.getJSONObject(0).getString("respMessge").isEmpty()) {

            			feilds.put(FieldType.PG_RESP_CODE.getName(), data.getJSONObject(0).getString("upiId"));
                		
                		feilds.put(FieldType.PG_TXN_STATUS.getName(), data.getJSONObject(0).getString("upiId")); 
            		}
            		
            		if (!data.getJSONObject(0).getString("respCode").toString().equalsIgnoreCase("null")
            				&& !data.getJSONObject(0).getString("respCode").toString().isEmpty()) {

            			feilds.put(FieldType.AUTH_CODE.getName(),data.getJSONObject(0).getString("respCode") );
            		}
            		if (!data.getJSONObject(0).getString("custRefNo").isEmpty()) {

            			feilds.put(FieldType.RRN.getName(), data.getJSONObject(0).getString("custRefNo"));
                		}
            		
            		if (!data.getJSONObject(0).getString("upiTxnId").isEmpty()) {

                		feilds.put(FieldType.ACQ_ID.getName(), data.getJSONObject(0).getString("upiTxnId"));  
                		}
            		
            		feilds.put(FieldType.STATUS.getName(), status);
            		//feilds.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
            		feilds.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getResponseMessage());
            		feilds.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
            		

            		
            		feilds.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
	}
public void updateUpiStatus(Fields feilds,String response) {
	JSONObject statusResponseJson = new JSONObject(response);
    JSONObject data = statusResponseJson.getJSONObject("data");

    String status = "";
	ErrorType errorType = null;
	String pgTxnMsg = null;
        if (org.apache.commons.lang.StringUtils.isNotBlank(data.getString("respMessge"))&&data.getString("respMessge").equalsIgnoreCase("SUCCESS")) {
status = StatusType.CAPTURED.getName();
	errorType = ErrorType.SUCCESS;
	pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();


		
		} else {
			status = StatusType.FAILED_AT_ACQUIRER.getName();
			errorType = ErrorType.getInstanceFromCode("022");

			if (StringUtils.isNotBlank(data.getString("respMessge"))) {
				pgTxnMsg = data.getString("respMessge");
			} else {
				pgTxnMsg = "Transaction failed at acquirer";
			}

}
        
        if (!data.getString("upiId").isEmpty()) {

    		feilds.put(FieldType.CARD_MASK.getName(), data.getString("upiId"));

    		}
        
    		if (!data.getString("respMessge").isEmpty()) {

    			feilds.put(FieldType.PG_RESP_CODE.getName(), data.getString("upiId"));
        		
        		feilds.put(FieldType.PG_TXN_STATUS.getName(), data.getString("upiId")); 
    		}
    		
    		if (!data.get("respCode").toString().equalsIgnoreCase("null")
    				&& !data.get("respCode").toString().isEmpty()) {

    			feilds.put(FieldType.AUTH_CODE.getName(),data.getString("respCode") );
    		}
    		if (!data.getString("custRefNo").isEmpty()) {

    			feilds.put(FieldType.RRN.getName(), data.getString("custRefNo"));
        		feilds.put(FieldType.ACQ_ID.getName(), data.getString("custRefNo"));           
        		}
    		
    		feilds.put(FieldType.STATUS.getName(), status);
    		//feilds.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
    		feilds.put(FieldType.RESPONSE_MESSAGE.getName(),transaction.getResponseMessage());
    		feilds.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
    		

    		
    		feilds.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
	}


public void updateResponseRefund(Fields fields, JSONObject statusResponseJson) {

	String status = "";
	ErrorType errorType = null;
	String pgTxnMsg = null;
	
	
	

		logger.info("Looking For Acquirer {} ErrorCode Mapping refund {}","COSMOS",statusResponseJson.getString("statusCode"));
		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(statusResponseJson.getString("statusCode"), "COSMOS");
		logger.info("Error code mapping  : "+errorMappingDTO);
		
		if(null != errorMappingDTO) {
			fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
			//fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), statusResponseJson.getString("statusMsg"));
			fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
			fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
			
				if (StringUtils.isNotBlank(statusResponseJson.getString("orgRrn"))) {

					fields.put(FieldType.ACQ_ID.getName(), statusResponseJson.getString("orgRrn"));
					}
				
		}else {
			logger.info("ErrorCodeMapping not fount for COSMOS Acquirer");
			
		}

	


}



public void updateResponseupi(Fields fields) {


logger.info("Feidl data in cosmos"+fields.getFieldsAsString() );
logger.info("FieldType.STATUS.getName().........................", fields.get(FieldType.RESPONSE_MESSAGE.getName()));

logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}","COSMOS",fields.get(FieldType.RESPONSE_MESSAGE.getName()));
ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(fields.get(FieldType.RESPONSE_MESSAGE.getName()), "COSMOS");
logger.info("Error code mapping  : "+errorMappingDTO);

if(null != errorMappingDTO) {
	fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
	//fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
	fields.put(FieldType.RESPONSE_MESSAGE.getName(),transaction.getResponseMessage());
	fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
	fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
	fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
	fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
	if(StringUtils.isBlank(fields.get(FieldType.ACQ_ID.getName()))) {
		fields.put(FieldType.ACQ_ID.getName(), fields.get(FieldType.RRN.getName()));

	}
}else {
	logger.info("ErrorCodeMapping not fount for COSMOS Acquirer");
	
}
}

}