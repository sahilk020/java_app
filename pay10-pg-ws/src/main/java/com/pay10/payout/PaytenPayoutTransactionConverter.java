package com.pay10.payout;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.CosmosUtil;

@Service("Pay10TransactionConverter")
public class PaytenPayoutTransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(PaytenPayoutTransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;


	@Autowired
	private FieldsDao fieldsDao;
	
	@Autowired
	private UserDao userDao;

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields) throws SystemException {

		String request;

			request = saleRequest(fields);
			
		return request;

	}

	private String saleRequest(Fields fields) throws SystemException {
		String statusCheckApiResponse = null;
		try {
			logger.info(" field in cosmos integrator cosmos"+fields.getFieldsAsString());
		String concatenatedString = null;
		
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
        String checksumKey = fields.get(FieldType.ADF4.getName());
        String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
		if(pgRefNum == null){
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			}
		JSONObject request = new JSONObject();
        request.put("minAmount", "");
        request.put("amount", amount);   
        request.put("extTransactionId", "BHARTIPAY"+fields.get(FieldType.PG_REF_NUM.getName()));
        request.put("channel", "api");
        request.put("remark", "QR SIT testing");
        request.put("source", fields.get(FieldType.MERCHANT_ID.getName()));
        request.put("terminalId", fields.get(FieldType.ADF2.getName()));
        request.put("type", "D");
        request.put("param3", " ");
        request.put("param2", " ");
        request.put("param1", " ");
        request.put("sid", fields.get(FieldType.ADF3.getName()));
        request.put("requestTime", date());
        request.put("reciept", "");
        logger.info("before checksum for cosmos"+request.toString());
        concatenatedString = "" + request.get("minAmount") + request.get("amount") + request.get("extTransactionId") + request.get("channel") + request.get("remark") + request.get("source") + request.get("terminalId") + request.get("type") + request.get("param3") + request.get("param2") + request.get("param1") + request.get("sid") + request.get("requestTime") + request.get("reciept");
        String encryptedCheckSum = CosmosUtil.generateChecksum(concatenatedString, checksumKey);
        request.put("checksum", encryptedCheckSum);
        logger.info("after check checksum  for cosmos"+request.toString());

        String enc = CosmosUtil.encrypt(request.toString(), fields.get(FieldType.TXN_KEY.getName()));
        logger.info("after encryption   for cosmos"+enc);
        String hostUrl = PropertiesManager.propertiesMap.get("cosmosUpiIntentUrl");

        String transactionENCResponse = callRESTApi(hostUrl, enc, "POST", fields.get(FieldType.ADF1.getName()));
         statusCheckApiResponse = CosmosUtil.decrypt(transactionENCResponse, fields.get(FieldType.TXN_KEY.getName()));
        logger.info("response for Qr String  decryption   for cosmos"+statusCheckApiResponse);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return statusCheckApiResponse;
	}
	
	
	public static String callRESTApi(String apiUrl, String request, String method, String cid) {

        StringBuilder serverResponse = new StringBuilder();
        //
        logger.info("COSMOS UPI callRESTApi CID :{}", cid);

        HttpsURLConnection connection = null;

        try {

            URL url = new URL(apiUrl);
            connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("POST");


            //connection.setRequestProperty("Content-Type", "text/plain");
            connection.setRequestProperty("Content-Type", " text/plain");
            connection.setRequestProperty("Content-Length", "" + request.length());
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestProperty("CID", cid);

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Send request
            OutputStream outputStream = connection.getOutputStream();
            DataOutputStream wr = new DataOutputStream(outputStream);
            wr.writeBytes(request.toString());
            wr.close();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;

            int code = ((HttpURLConnection) connection).getResponseCode();//200, 401,404, 500
            int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
            if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
                logger.error("HTTP Response code of txn [COSMOS UPI] :" + code);
                throw new SystemException(ErrorType.ACUIRER_DOWN,
                        "Network Exception with cosmos Upi "
                                + apiUrl);
            }
            while ((line = rd.readLine()) != null) {
                serverResponse.append(line);

            }
            rd.close();

        } catch (Exception e) {
            logger.error("Exception in HTTP call COSMOS UPI :" + apiUrl, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return serverResponse.toString();
    }

	String date() {
		LocalDateTime myDateObj = LocalDateTime.now();  
	    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
	    
	    return myDateObj.format(myFormatObj);
	}
}

	