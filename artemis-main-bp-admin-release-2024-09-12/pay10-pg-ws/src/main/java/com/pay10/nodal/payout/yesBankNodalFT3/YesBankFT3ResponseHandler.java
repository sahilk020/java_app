package com.pay10.nodal.payout.yesBankNodalFT3;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.YesBankFT3ResultType;

@Service
public class YesBankFT3ResponseHandler {

	private static final String FAULT_KEY = "Fault";
	private static final String SUCCESS_KEY = "maintainBeneResponse";
	private static final String REQUEST_STATUS_KEY = "RequestStatus";
	private static final String RRN_OPEN_KEY = "ReqRefNo";
	private static final String ERROR_KEY = "Error";
	private static final String RESULT_KEY = "Result";

	private static Logger logger = LoggerFactory.getLogger(YesBankFT3ResponseHandler.class.getName());

	@SuppressWarnings("unchecked")
	public Map<String, String> handleAddBeneResponse(String res) {
		JSONObject innerMap = null;
		Map<String, String> responseMap = new HashMap<>();
		try {
			JSONObject response = new JSONObject(res);
			if (response.has(FAULT_KEY)) {
				logger.error("Received failure in response");
				innerMap = (JSONObject) response.get(FAULT_KEY);
				responseMap.put(RESULT_KEY, "FAULT");
				if (!innerMap.has("Code")) {
					logger.error("Unknown request status");
					responseMap.put(RESULT_KEY, "UNKNOWN");
					return responseMap;
				}

				JSONObject codeMap = (JSONObject) innerMap.get("Code");

				if (!codeMap.has("Subcode")) {
					logger.error("Unknown request status");
					responseMap.put(RESULT_KEY, "UNKNOWN");
					return responseMap;
				}

				JSONObject subCodeMap = (JSONObject) codeMap.get("Subcode");

				responseMap.put("ErrorCode", subCodeMap.get("Value").toString());
//				responseMap.put("Reason", ((JSONObject) innerMap.get("Detail")).get("MessageList").toString());
				responseMap.put("Reason", ((JSONObject) innerMap.get("Reason")).get("Text").toString());

			}

			else if (response.has(SUCCESS_KEY)) {
				logger.info("Received valid response from yes bank");
				innerMap = (JSONObject) response.get(SUCCESS_KEY);

				if (innerMap.has(REQUEST_STATUS_KEY) && innerMap.get(REQUEST_STATUS_KEY).equals("SUCCESS")) {
					logger.info("Found success key in response");
					if (innerMap.has(RRN_OPEN_KEY)) {
						responseMap.put(RRN_OPEN_KEY, innerMap.get(RRN_OPEN_KEY).toString());
					}
					responseMap.put(REQUEST_STATUS_KEY, innerMap.get(REQUEST_STATUS_KEY).toString());
					responseMap.put(RESULT_KEY, "SUCCESS");
				} else if (innerMap.has(REQUEST_STATUS_KEY) && innerMap.get(REQUEST_STATUS_KEY).equals("FAILURE")) {
					logger.info("Found Failure key in response");
					responseMap.put(REQUEST_STATUS_KEY, innerMap.get(REQUEST_STATUS_KEY).toString());
					JSONObject errorMap = (JSONObject) innerMap.get(ERROR_KEY);
					JSONArray errorArray = new JSONArray(errorMap.get(ERROR_KEY).toString());
					JSONObject errorObject = errorArray.getJSONObject(0);
					responseMap.put("ErrorSubCode", errorObject.get("ErrorSubCode").toString());
					if(errorObject.has("GeneralMsg")) {
						responseMap.put("GeneralMsg", errorObject.get("GeneralMsg").toString());
					}else {
						responseMap.put("GeneralMsg", errorObject.get("Reason").toString());
					}

					responseMap.put(RESULT_KEY, "FAILURE");
					logger.error(innerMap.get(ERROR_KEY).toString());
				} else {
					logger.error("unknown request status");
					responseMap.put(RESULT_KEY, "UNKNOWN");
				}
			} else {
				logger.error("Unknown response");
				// TODO Add proper response;
				responseMap.put(RESULT_KEY, "UNKNOWN");
			}

			return responseMap;
		} catch (Exception e) {
			logger.error("Exception while handling bene maintenance response");
			logger.error(e.getMessage());
			responseMap.put(RESULT_KEY, "UNKNOWN");
		}
		return responseMap;

	}

	@SuppressWarnings("unchecked")
	public Map<String, String> handleStatusEnquiryResponse(String res) {
		JSONObject innerMap = null;
		Map<String, String> responseMap = new HashMap<>();
		try {
//			 res = "{\"Data\":{\"ConsentId\":\"1057462\",\"TransactionIdentification\":\"861786d823fc11eb95d10a0028fc0000\",\"Status\":\"FAILED\",\"CreationDateTime\":\"2020-11-11T14:31:38.000+05:30\",\"StatusUpdateDateTime\":\"2020-11-11T22:32:05.000+05:30\",\"Initiation\":{\"InstructionIdentification\":\"PA1010401112152338\",\"EndToEndIdentification\":\"N316200009875795\",\"InstructedAmount\":{\"Amount\":6.543E+3,\"Currency\":\"INR\"},\"DebtorAccount\":{\"Identification\":\"016190100002195\",\"SecondaryIdentification\":\"1057462\"},\"CreditorAccount\":{\"SchemeName\":\"SBIN0004441\",\"Identification\":\"404\",\"Name\":\"1166601020134439\",\"BeneficiaryCode\":\"1166601020134439\",\"RemittanceInformation\":{\"Unstructured\":{\"CreditorReferenceInformation\":\"sdfg\"}},\"ClearingSystemIdentification\":\"NEFT\"}}},\"Risk\":{\"PaymentContextCode\":\"NODAL\",\"DeliveryAddress\":{\"AddressLine\":null}},\"Links\":{\"Self\":\"https:\\/\\/uatesbtrans.yesbank.com:7085\\/api-banking\\/v2.0\\/domestic-payments\\/payment-details\"},\"Meta\":{\"ErrorCode\":\"sfms:E70\",\"ErrorSeverity\":null,\"ActionCode\":\"AC_TXN_FAILED\",\"ActionDescription\":\"Transaction Failed. Please check error description and action\"}}";
			JSONObject response = new JSONObject(res);
			
			if (response.has("Data")) {
				innerMap = (JSONObject) response.get("Data");
				if (innerMap.has("Status")) {
					responseMap.put(RESULT_KEY, "SUCCESS");
					String status = innerMap.get("Status").toString();
					responseMap.put("Status", status);
					if(innerMap.has("StatusUpdateDateTime")) {
						responseMap.put("Timestamp", innerMap.get("StatusUpdateDateTime").toString());
					}
					if(innerMap.has("TransactionIdentification")) {
						responseMap.put("acqId", innerMap.get("TransactionIdentification").toString());
					}
					if(innerMap.has("Initiation")) {
						Object utr = ((JSONObject) innerMap.get("Initiation")).get("EndToEndIdentification");
						if(utr == null) {
							responseMap.put("utr", "");
						}else {
							responseMap.put("utr", utr.toString());
						}
					}
					
					if(status.equalsIgnoreCase("Failed")) {
						if (response.has("Meta")) {
							logger.info("Recieved Failed Status in status enquiry.");
							innerMap = (JSONObject) response.get("Meta");

							YesBankFT3ResultType yesBankFT3ResultType = YesBankFT3ResultType
									.getInstanceFromCode(innerMap.get("ErrorCode").toString());
							if (yesBankFT3ResultType == null) {
								yesBankFT3ResultType = YesBankFT3ResultType.UNKNOWN;
							}
							responseMap.put("ErrorCode", yesBankFT3ResultType.getBankCode());
							responseMap.put("Reason", yesBankFT3ResultType.getMessage());
							responseMap.put("Status", yesBankFT3ResultType.getStatusName());
							responseMap.put(RESULT_KEY, "SUCCESS");
							return responseMap;
						}
					}
					return responseMap;
				}
			}
			
			if (response.has("Meta")) {
				logger.info("Error in transaction while doing status enquiry");
				innerMap = (JSONObject) response.get("Meta");

				YesBankFT3ResultType yesBankFT3ResultType = YesBankFT3ResultType
						.getInstanceFromCode(innerMap.get("ErrorCode").toString());
				if (yesBankFT3ResultType == null) {
					yesBankFT3ResultType = YesBankFT3ResultType.UNKNOWN;
				}
				responseMap.put("ErrorCode", yesBankFT3ResultType.getBankCode());
				responseMap.put("Reason", yesBankFT3ResultType.getMessage());
				responseMap.put("Status", yesBankFT3ResultType.getStatusName());
				responseMap.put("Timestamp", "");
				responseMap.put(RESULT_KEY, "SUCCESS");
				return responseMap;
			}

			if (response.has("Code")) {
				logger.info("Error in transaction while doing status enquiry");
				YesBankFT3ResultType yesBankFT3ResultType = YesBankFT3ResultType
						.getInstanceFromCode(response.get("Code").toString());
				if (yesBankFT3ResultType == null) {
					yesBankFT3ResultType = YesBankFT3ResultType.UNKNOWN;
				}
				// TODO Review code and check reason is needed or not
				responseMap.put("ErrorCode", yesBankFT3ResultType.getBankCode());
				responseMap.put("Reason", yesBankFT3ResultType.getMessage());
				responseMap.put("Status", yesBankFT3ResultType.getStatusName());
				responseMap.put("Timestamp", "");
				responseMap.put(RESULT_KEY, "SUCCESS");
				return responseMap;
			}
			
			if(response.has("error")) {
				logger.info("Bank Server not responding.");
				innerMap = response.getJSONObject("error");
				if(innerMap.get("code").equals(404)) {
					responseMap.put(RESULT_KEY, "SUCCESS");
					responseMap.put("Status", YesBankFT3ResultType.PENDING.getStatusName());
					responseMap.put("Timestamp", "");
					return responseMap;
				}
			}
			logger.info("Unknown response while doing status enquiry.");
			responseMap.put("ErrorCode", YesBankFT3ResultType.UNKNOWN.getBankCode());
			responseMap.put("Reason", YesBankFT3ResultType.UNKNOWN.getStatusName());
			responseMap.put("Timestamp", "");
			responseMap.put(RESULT_KEY, "fault");
			return responseMap;
		} catch (Exception e) {
			logger.error("Exception while handling status enquiry response");
			logger.error(e.getMessage());
			responseMap.put(RESULT_KEY, "UNKNOWN");
			responseMap.put("Timestamp", "");
		}
		return responseMap;
	}

	public Map<String, String> handleFundConfirmationResponse(String res) {
		Map<String, String> responseMap = new HashMap<>();
		try {
			JSONObject response = new JSONObject(res);
			if (response.has("Code")) {
				logger.info("Error in getting nodal funds.");
				YesBankFT3ResultType yesBankFT3ResultType = YesBankFT3ResultType
						.getInstanceFromCode(response.get("Code").toString());
				if (yesBankFT3ResultType == null) {
					yesBankFT3ResultType = YesBankFT3ResultType.UNKNOWN;
				}
				responseMap.put("ErrorCode", yesBankFT3ResultType.getBankCode());
				responseMap.put("Reason", yesBankFT3ResultType.getStatusName());
				responseMap.put(RESULT_KEY, "FAILURE");
				return responseMap;
			}

			if (!response.has("Data")) {
				logger.info("Response doesn't have key data");
				responseMap.put("ErrorCode", YesBankFT3ResultType.UNKNOWN.getBankCode());
				responseMap.put("Reason", YesBankFT3ResultType.UNKNOWN.getStatusName());
				responseMap.put(RESULT_KEY, "FAILURE");
				return responseMap;
			}

			@SuppressWarnings("unchecked")
			String balance = ((JSONObject) ((JSONObject) response.get("Data")).get("FundsAvailableResult"))
					.get("BalanceAmount").toString();
			responseMap.put("balance", balance);
			responseMap.put(RESULT_KEY, "SUCCESS");
			return responseMap;
		} catch (Exception e) {
			logger.error("Exception while handling Fund confirmation response");
			logger.error(e.getMessage());
			responseMap.put(RESULT_KEY, "UNKNOWN");
		}
		return responseMap;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> handleFundTransferResponse(String response) {
		Map<String, String> responseMap = new HashMap<>();
		JSONObject innerMap = null;
		try {
			JSONObject res = new JSONObject(response);
			if (res.has("Code")) {
				logger.info("Error in transaction while doing fund transfer");

				YesBankFT3ResultType yesBankFT3ResultType = YesBankFT3ResultType
						.getInstanceFromCode(res.get("Code").toString());
				if (yesBankFT3ResultType == null) {
					yesBankFT3ResultType = YesBankFT3ResultType.UNKNOWN;
				}
				responseMap.put("ErrorCode", yesBankFT3ResultType.getBankCode());
				responseMap.put("Reason", yesBankFT3ResultType.getStatusName());
				responseMap.put(RESULT_KEY, "fault");
				responseMap.put("Timestamp", "");
				return responseMap;
			}

			if (res.has("Meta")) {
				logger.info("Error in transaction while doing status enquiry");
				innerMap = (JSONObject) res.get("Meta");

				YesBankFT3ResultType yesBankFT3ResultType = YesBankFT3ResultType
						.getInstanceFromCode(innerMap.get("ErrorCode").toString());
				if (yesBankFT3ResultType == null) {
					yesBankFT3ResultType = YesBankFT3ResultType.UNKNOWN;
				}
				responseMap.put("ErrorCode", yesBankFT3ResultType.getBankCode());
				responseMap.put("Reason", yesBankFT3ResultType.getStatusName());
				responseMap.put("Timestamp", "");
				responseMap.put(RESULT_KEY, "fault");
				return responseMap;
			}

			if (!res.has("Data")) {
				logger.info("Response doesn't have key data");
				responseMap.put("ErrorCode", YesBankFT3ResultType.UNKNOWN.getBankCode());
				responseMap.put("Reason", YesBankFT3ResultType.UNKNOWN.getStatusName());
				responseMap.put("Timestamp", "");
				responseMap.put(RESULT_KEY, "fault");
				return responseMap;
			}

			JSONObject dataObj = res.getJSONObject("Data");
			responseMap.put(RESULT_KEY, "SUCCESS");
			responseMap.put("Status", dataObj.getString("Status"));
			responseMap.put("Timestamp", dataObj.getString("StatusUpdateDateTime"));
			return responseMap;
		} catch (Exception e) {
			logger.error("Exception while handling Fund transfer response");
			logger.error(e.getMessage());
			responseMap.put(RESULT_KEY, "UNKNOWN");
			responseMap.put("Timestamp", "");
		}
		return responseMap;
	}

}
