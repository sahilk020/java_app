package com.pay10.yesbankcb;

import org.apache.commons.lang3.StringUtils;
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
import com.pay10.pg.core.util.HdfcUpiUtil;

@Service
public class YesBankUpiStatusEnquiryProcessor {

	@Autowired
	@Qualifier("yesBankCbTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("yesBankCbTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("hdfcUpiUtil")
	private HdfcUpiUtil hdfcUpiUtil;

	@Autowired
	@Qualifier("yesBankCbTransformer")
	private YesBankCbTransformer yesBankCbTransformer;

	private static Logger logger = LoggerFactory.getLogger(YesBankUpiStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		JSONObject request = statusEnquiryRequest(fields);
		Transaction statusEnquiryResponse = new Transaction();
		String response = null;
		try {
			response = communicator.getResponse(request, fields);
			statusEnquiryResponse = converter.toTransactionStatusEnquiry(response, fields);
			updateFields(fields, statusEnquiryResponse);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

	}

	public JSONObject statusEnquiryRequest(Fields fields) {

		String merchantId = fields.get(FieldType.ADF5.getName());
		String lastValue = "NA";
		String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());

		StringBuilder request = new StringBuilder();
		request.append(merchantId);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(pgRefNum);
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append(lastValue);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(lastValue);

		logger.info("YesBank Status Enquiry Request  = " + request);

		String key = fields.get(FieldType.ADF1.getName());

		if (StringUtils.isBlank(key)) {
			key = PropertiesManager.propertiesMap.get(Constants.YES_BANKCB_UPI_KEY);
		}

		String encryptedString = null;
		try {
			encryptedString = hdfcUpiUtil.encrypt(request.toString(), key);
			logger.info("Yes Bank upi status enquiry request encryptedString = " + encryptedString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject json = new JSONObject();
		json.put(Constants.REQUEST_MESSAGE, encryptedString);
		json.put(Constants.PG_MERCHANT_ID, merchantId);

		logger.info("Yes Bank upi status enquiry final request encryptedString = " + json.toString());

		return json;

	}

	public void updateFields(Fields fields, Transaction transactionResponse) throws SystemException {
		yesBankCbTransformer.updateResponse(fields, transactionResponse);

	}

}
