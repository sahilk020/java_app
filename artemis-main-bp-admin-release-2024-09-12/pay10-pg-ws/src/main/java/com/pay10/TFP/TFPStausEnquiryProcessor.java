package com.pay10.TFP;

import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;

@Service
public class TFPStausEnquiryProcessor {
	private static Logger logger = LoggerFactory.getLogger(TFPStausEnquiryProcessor.class.getName());

	@Autowired
	TFPProcessor tfpProcessor;

	public void enquiryProcessor(Fields fields) {
		String response = statusEnquiry(fields);
		 JSONArray dataArray = new JSONArray(response);
         JSONObject responseJson = dataArray.getJSONObject(0);
     
		TFPTransformer tFPTransformer = new TFPTransformer();
		tFPTransformer.updateResponseStatus(fields, responseJson);

	}

	public String statusEnquiry(Fields fields) {
		String salt_key = fields.get(FieldType.ADF5.getName());
		String Url = PropertiesManager.propertiesMap.get("TFPstatusEnquiryURL");

		JSONObject saleRequest = new JSONObject();

		saleRequest.put("APP_ID", fields.get(FieldType.ADF3.getName()));
		saleRequest.put("ORDER_ID", fields.get(FieldType.PG_REF_NUM.getName()));
		saleRequest.put("CURRENCY_CODE", fields.get(FieldType.CURRENCY_CODE.getName()));
		saleRequest.put("AMOUNT", fields.get(FieldType.TOTAL_AMOUNT.getName()));
		saleRequest.put("TXN_ID", fields.get(FieldType.RRN.getName()));
		logger.info("Dual Verfication and Status Enquiry " + saleRequest);
		TreeMap<String, String> treeMap = new TreeMap<String, String>();

		for (String key : saleRequest.keySet()) {

			treeMap.put(key, (String) saleRequest.getString(key));
		}

		StringBuilder allFields = new StringBuilder();
		for (String key : treeMap.keySet()) {
			allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
			allFields.append(key);
			allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
			allFields.append(treeMap.get(key));
		}
		allFields.deleteCharAt(0);

		allFields.append(salt_key);
		logger.info("HASH DATA for CAPTURED TRANSACTION" + allFields);

		saleRequest.put(FieldType.HASH.getName(), tfpProcessor.generateSHA512(allFields.toString()));

		String response = tfpProcessor.callRESTApi(Url, saleRequest.toString(), "POST");

		return response.toString();
	}
	
	
}
