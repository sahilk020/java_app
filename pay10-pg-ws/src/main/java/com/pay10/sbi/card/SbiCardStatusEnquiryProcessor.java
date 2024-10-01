package com.pay10.sbi.card;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.pg.core.sbicard.DecryptionUtil;
import com.pay10.pg.core.sbicard.EncryptedRequestData;
import com.pay10.pg.core.sbicard.EncryptionUtil;
import com.pay10.pg.core.util.SbiUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SbiCardStatusEnquiryProcessor {

    @Autowired
    @Qualifier("sbiCardTransactionConverter")
    private TransactionConverter converter;

    private SbiCardTransformer sbiTransformer = null;

    @Autowired
    private SbiUtil sbiUtil;

    private static Logger logger = LoggerFactory.getLogger(SbiCardStatusEnquiryProcessor.class.getName());

    public void enquiryProcessor(Fields fields) throws SystemException {
        String newTxnId = TransactionManager.getNewTransactionId();
        fields.put(FieldType.TXN_ID.getName(), newTxnId);
        JSONObject response = null;
        try {
            response = statusEnquiryReq(fields);
        } catch (IOException e) {
            throw new SystemException(ErrorType.ACQUIRER_ERROR, e.getMessage());
        }
        updateFields(fields, response);
    }

    public JSONObject statusEnquiryReq(Fields fields) throws SystemException, IOException {
        JSONObject request = new JSONObject();
        request.put("pgInstanceId", fields.get(FieldType.ADF9.getName()));
        request.put("merchantId", fields.get(FieldType.ADF11.getName()));
        request.put("merchantReferenceNo", fields.get(FieldType.ORDER_ID.getName()));
        request.put("amount", fields.get(FieldType.TOTAL_AMOUNT.getName()));
        request.put("currencyCode", fields.get(FieldType.CURRENCY_CODE.getName()));
        logger.info("doubleVerification and statusEnquiryReq:: request plain req={}", request);
        request = prepareEncReq(fields, request, fields.get(FieldType.ADF1.getName()));
        return new JSONObject(DecryptionUtil.decrypt(executeApi(fields, request)).toJSONString());
    }

    private JSONObject prepareEncReq(Fields fields, JSONObject req, String acqId) {
        EncryptedRequestData encryptedRequestData = EncryptionUtil.encrypt(req.toString());
        JSONObject finalReq = new JSONObject();
        finalReq.put("signedEncRequestPayload", encryptedRequestData.getSignedEncRequestPayload());
        finalReq.put("requestSymmetricEncKey", encryptedRequestData.getRequestSymmetricEncKey());
        finalReq.put("iv", encryptedRequestData.getIv());
        finalReq.put("pgInstanceId", acqId);
        finalReq.put("merchantId", acqId);
        finalReq.put("var1", fields.get(FieldType.PG_REF_NUM.getName()));
        finalReq.put("var2", "");
        finalReq.put("var3", "");
        return finalReq;
    }

    private String executeApi(Fields fields, JSONObject req) throws IOException {
        String pgInstanceId = fields.get(FieldType.ADF9.getName());
        String apiUrl = PropertiesManager.propertiesMap.get("SBI_CARD_STATUS_ENQ_URL");
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("cache-control", "no-cache");
        connection.setRequestProperty("x-api-key", fields.get(FieldType.ADF4.getName()));
        connection.setRequestProperty("pgInstanceId", pgInstanceId);
        logger.info("executeApi:: xApiKey={}, pgInstanceId={}",
                fields.get(FieldType.ADF4.getName()), pgInstanceId);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);

        // Send request
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(req.toString());
        wr.flush();
        wr.close();

        // Get Response
        InputStream is = connection.getInputStream();

        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
        String decodedString;

        String response = "";
        while ((decodedString = bufferedreader.readLine()) != null) {
            response = response + decodedString;
        }
        bufferedreader.close();
        return response;
    }

    public void updateFields(Fields fields, JSONObject response) {
        Transaction transactionResponse = toTransaction(response);
        sbiTransformer = new SbiCardTransformer(transactionResponse);
        sbiTransformer.updateResponse(fields);
    }

    public Transaction toTransaction(JSONObject response) {

        Transaction transaction = new Transaction();

        if (response.getInt("status") == 50020) {
            transaction.setStatus("Success");
        } else {
            transaction.setStatus("Failed");
        }
        transaction.setAcqId(response.get("transactionId").toString());
        if(StringUtils.isNotBlank(response.get("pgErrorDetail").toString())) {
        transaction.setResponseMessage(response.get("pgErrorDetail").toString());
        }
        return transaction;
    }
}
