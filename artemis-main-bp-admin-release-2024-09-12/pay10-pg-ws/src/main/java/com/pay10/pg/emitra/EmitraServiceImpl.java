package com.pay10.pg.emitra;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.util.TransactionResponser;
import com.pay10.pg.core.util.emitra.EmitraScrambler;
import com.pay10.pg.history.Historian;
import com.pay10.requestrouter.RequestRouter;

import bsh.This;

@Service
public class EmitraServiceImpl implements EmitraService {

	private static final Logger logger = LoggerFactory.getLogger(This.class.getName());

	@Autowired
	private GeneralValidator validator;

	@Autowired
	private Historian historian;

	@Autowired
	private TransactionResponser transactionResponser;

	@Autowired
	private EmitraScrambler scrambler;

	@Autowired
	private RequestRouter router;

	@Override
	public Map<String, String> statusInquiry(Fields fields) throws SystemException {

		fields.put("IS_EMITRA", "true");
		validator.validate(fields);

		String fieldsAsString = fields.getFieldsAsBlobString();
		fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
		fields.logAllFields("statusInquiry:: Initial Refine Request:");

		historian.findPreviousForStatus(fields);

		historian.populateFieldsFromPrevious(fields);

		transactionResponser.getResponse(fields, true);

		return fields.getFields();
	}

	@Override
	public Map<String, String> refund(Map<String, String> request) {
		Map<String, String> response = new HashMap<String, String>();

		try {

			logger.info("refund:: request initialized. request={}", request.toString());
			Map<String, String> decryptedReq = decrypt(request);
			logger.info("refund:: decrypted request={}", decryptedReq.toString());

			Fields fields = new Fields(decryptedReq);
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();

			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			if (fields.contains(FieldType.UDF6.getName())) {
				if (!fields.get(FieldType.UDF6.getName()).isEmpty()) {
					fields.put(FieldType.UDF6.getName(), Constants.Y_FLAG.getValue());
				}
			}
			fields.logAllFields("refund:: Refine Request:");
			fields.put("IS_EMITRA", "true");
			Map<String, String> responseMap = router.route(fields);
			responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
			logger.info("refund:: response received from acquirer response={}", responseMap.toString());
			responseMap.remove("CUST_PHONE");
			responseMap.remove("HASH");
			responseMap.remove("ORIG_TXN_ID");
			responseMap.remove("CARD_MASK");
			responseMap.remove("CURRENCY_CODE");
			responseMap.remove("CUST_EMAIL");
			responseMap.remove("AUTH_CODE");
			responseMap.remove("TXN_ID");
			responseMap.remove("RRN");
			responseMap.remove("CARD_HOLDER_NAME");
//			StringBuilder allFields = new StringBuilder();
//			for (Entry<String, String> entry : responseMap.entrySet()) {
//				allFields.append("&");
//				allFields.append(entry.getKey());
//				allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
//				allFields.append(entry.getValue());
//			}
//			allFields.deleteCharAt(0);
//			response.put("PAY_ID", responseMap.get("PAY_ID"));
//			response.put("ENCDATA", scrambler.encrypt(responseMap.get("PAY_ID"), allFields.toString()));
			return responseMap;
		} catch (Exception ex) {
			logger.error("refund:: failed. request={}", request, ex);
			response.put("PAY_ID", request.get("PAY_ID"));
			String encryptedString = "Something goes wrong!";
			response.put("STATUS", "FAILED");
			response.put("MESSAGE", encryptedString);
			return response;
		}
	}

	private Map<String, String> decrypt(Map<String, String> request) throws SystemException {
		String decrypt = scrambler.decrypt(request.get("PAY_ID"), request.get("ENCDATA"));
		String[] fieldArray = decrypt.split("~");
		Map<String, String> requestMap = new HashMap<String, String>();
		for (String entry : fieldArray) {
			String[] namValuePair = entry.split("=", 2);
			if (namValuePair.length == 2) {
				requestMap.put(namValuePair[0], namValuePair[1]);
			} else {
				requestMap.put(namValuePair[0], "");
			}
		}
		return requestMap;
	}

	@Override
	public Map<String, String> refundStatusInquiry(Fields fields) throws SystemException {
		fields.put("IS_EMITRA", "true");
		validator.validate(fields);

		String fieldsAsString = fields.getFieldsAsBlobString();
		fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
		fields.logAllFields("refundStatusInquiry:: Initial Refine Request:");

		historian.findPreviousForStatusOfRefund(fields);

		historian.populateFieldsFromPrevious(fields);

		transactionResponser.getResponse(fields, true);

		return fields.getFields();
	}

}
