package com.pay10.icici.upi;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.ICICIUpiUtil;

/**
 * @author jeetu
 *
 */
@Service
public class ICICIUpiIntegrator {

	private static Logger logger = LoggerFactory.getLogger(ICICIUpiIntegrator.class.getName());

	@Autowired
	@Qualifier("iciciUpiTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("iciciUpiTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("iciciUpiTransformer")
	private ICICIUpiTransformer upiTransformer;

	@Autowired
	@Qualifier("iciciUpiFactory")
	private TransactionFactory transactionFactory;

	@Autowired
	private ICICIUpiUtil iciciUpiUtil;

	public void process(Fields fields) throws SystemException {

		transactionFactory.getInstance(fields);
		String vpaFlag = "Y";// PropertiesManager.propertiesMap.get(Constants.VPA_VAIDATION_FLAG);
		String allowOnFail = PropertiesManager.propertiesMap.get(Constants.ALLOW_COLLECT_ON_VPA_FAIL);

		String transactionType = fields.get(FieldType.TXNTYPE.getName());

		if (vpaFlag.equalsIgnoreCase(Constants.Y_FLAG) && transactionType.equals(TransactionType.SALE.getName())) {
			send(fields);

//			String vpaStatus = vpaValidation(fields);
//			if (StringUtils.isNotBlank(vpaStatus)) {
//
//				if (vpaStatus.contains(Constants.VPA_VAIDATION_FAILED)
//						|| vpaStatus.contains(Constants.VPA_VAIDATION_INVALID)
//						|| vpaStatus.contains(Constants.VPA_VAIDATION_REQ_FAILED)) {
//
//					if (StringUtils.isNotBlank(allowOnFail) && allowOnFail.equalsIgnoreCase("Y")) {
//						send(fields);
//					} else {
//						upiTransformer.updateInvalidVpaResponse(fields, vpaStatus);
//					}
//				}
//
//				else if (vpaStatus.equalsIgnoreCase(Constants.VPA_VAIDATION_SUCCESS)) {
//					send(fields);
//				}
//			} else {
//				send(fields);
//			}
		} else {
			send(fields);
		}
	}

	public String vpaValidation(Fields fields) throws SystemException {

		String merchId = fields.get(FieldType.ADF4.getName());
		String merchChanId = fields.get(FieldType.ADF5.getName());
		String vpa = fields.get(FieldType.PAYER_ADDRESS.getName());
		String vpaStatus = "";
		String checksumString = merchId + merchChanId + vpa;

		try {
			String encryptedCheckSum = "";// checksumGenerator.generateCheckSum(checksumString);

			Map<String, String> requestMap = new HashMap<String, String>();

			requestMap.put("merchId", merchId);
			requestMap.put("merchChanId", merchChanId);
			requestMap.put("customerVpa", vpa);
			requestMap.put("checkSum", encryptedCheckSum);

			JSONObject request = new JSONObject();
//			request.put(Constants.MERCH_ID, requestMap.get(Constants.MERCH_ID));
//			request.put(Constants.MERCH_CHAN_ID, requestMap.get(Constants.MERCH_CHAN_ID));
//			request.put(Constants.CUSTOMER_VPA, requestMap.get(Constants.CUSTOMER_VPA));
//			request.put(Constants.CHECKSUM, requestMap.get(Constants.CHECKSUM));

			// String vpaValidationResponse = communicator.getVpaResponse(request, fields);

//			logger.info("vpa validation API response >>> " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Order Id >>> " 
			// + fields.get(FieldType.ORDER_ID.getName()) + " " + vpaValidationResponse);
//
//			if (StringUtils.isNotBlank(vpaValidationResponse)) {
//
//				JSONObject vpaResponse = new JSONObject(vpaValidationResponse);
//
//				if (StringUtils.isNotBlank(vpaResponse.getString("code"))) {
//
//					if (vpaResponse.getString("code").contains(Constants.VPA_VAIDATION_SUCCESS)) {
//						vpaStatus = Constants.VPA_VAIDATION_SUCCESS;
//					} 
//					
//					else if (vpaResponse.getString("code").contains(Constants.VPA_VAIDATION_INVALID)) {
//						vpaStatus = Constants.VPA_VAIDATION_INVALID;
//					} 
//					else {
//						vpaStatus = Constants.VPA_VAIDATION_FAILED;
//					}
//				}
//			} else {
//				vpaStatus = Constants.VPA_VAIDATION_FAILED;
//
//			}
		}

		catch (Exception e) {
			logger.error("Error in VPA validaiton response parsing for Icici Bank UPI ", e);
		}

		return vpaStatus;
	}

	public void send(Fields fields) throws SystemException {

		Transaction transactionResponse = new Transaction();
		String jsonResponse = null;
		String request = converter.perpareRequest(fields);

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
			//String token
			jsonResponse = communicator.getResponse(request, fields);

//			if (StringUtils.isNotBlank(token)) {
//				JSONObject initialResponse = converter.toTokenTransaction(token);
//				if (initialResponse != null) {
//					if (initialResponse.get(Constants.SUCCESS_REPONSE).toString().equals("true")
//							&& initialResponse.get(Constants.SUCCESS_REPONSE_CODE).toString().equals("92")) {
//						request = converter.collectRequestStatus(fields, initialResponse);
//						jsonResponse = communicator.getTokenStatusResponse(request, fields);
//					}
//				}
//
//			}
		} else if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
			jsonResponse = communicator.getResponse(request, fields);
		}

		else if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.STATUS.getName())) {
			jsonResponse = communicator.getResponse(request, fields);
		}

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case SALE:
			if (StringUtils.isNotBlank(jsonResponse)) {
				transactionResponse = converter.toTransaction(jsonResponse, fields);
				upiTransformer.updateSaleResponse(fields, transactionResponse);
			} else {
				upiTransformer.updateSaleResponse(fields, transactionResponse);
				break;
			}

		case REFUND:
			if (StringUtils.isNotBlank(jsonResponse)) {
				if (jsonResponse.contains(Constants.SUCCESS_REPONSE_CODE)) {
					 transactionResponse = converter.toTransactionRefund(jsonResponse, fields);
					 upiTransformer.updateRefundResponse(fields, transactionResponse);
					break;
				} 
			} else {
				// upiTransformer.updateRefundResponse(fields, transactionResponse);
				break;
			}

		default:
			break;
		}
	}
}
