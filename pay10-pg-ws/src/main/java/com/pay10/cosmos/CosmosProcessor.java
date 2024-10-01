package com.pay10.cosmos;


import static com.pay10.pg.core.util.CosmosUtil.generateChecksum;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.CosmosUtil;
import com.pay10.pg.core.util.Processor;

@Service("cosmosProcessor")
public class CosmosProcessor implements Processor {
    private static Logger logger = LoggerFactory.getLogger(CosmosProcessor.class.getName());
    static Key secretKey = null;

    @Autowired
	private CosmosUpIntegrator cosmosUpIntegrator;
    
    @Autowired
	private CosmosUpiTransformer CosmosUpiTransformer;
    
    @Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;



    
    @Override
    public void preProcess(Fields fields) throws SystemException {


    }

    @Override
    public void process(Fields fields) throws SystemException {
        String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
        logger.info("COSMOS UPI pgRefNo++process+++++++++++++++++++++++++++++++++++++++++++" + pgRefNo);

        logger.info("process:: txnType={}", fields.get(FieldType.TXNTYPE.getName()));
        if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
                .equals(TransactionType.REFUNDRECO.getName()))) {
            logger.info("process:: return without initializing integrator.");
            return;
        }

        if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.COSMOS.getCode())) {
            return;
        }

        if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
            fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
        }
        
        if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.COSMOS.getCode())&&((StringUtils.isNotEmpty(fields.get(FieldType.UPI_INTENT.getName())) && fields.get(FieldType.UPI_INTENT.getName()).equals("1"))||fields.get(FieldType.PAYMENT_TYPE.getName()).equals(PaymentType.QRCODE.getCode()))) {
   		cosmosUpIntegrator.process(fields);

       }else {
        boolean vpaValidateStatus = vpaValidation(fields);
        if (vpaValidateStatus) {
            collectAPI(fields);
            logger.info("collectAPI :: " + fields.getFieldsAsString());
        } else {
        	fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
            logger.info("vpaValidateStatus invalid");
        }
       }
    }

    @Override
    public void postProcess(Fields fields) throws SystemException {

    }

    public boolean vpaValidation(Fields fields) throws SystemException {
        String concatenatedString = null;
        try {//
            String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
            if (pgRefNo == null)
                fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
            logger.info("COSMOS UPI TXN_ID +++++++++++++++++++++++++++++++++++++++++++++" + fields.get(FieldType.TXN_ID.getName()));
            logger.info("COSMOS UPI pgRefNo+++++++++++++++++++++++++++++++++++++++++++++" + fields.get(FieldType.PG_REF_NUM.getName()));

            logger.info("COSMOS UPI +++++++++++++++++++++++{}", fields.get(FieldType.PAYER_ADDRESS.getName()));
            JSONObject request = new JSONObject();
            request.put("source", fields.get(FieldType.MERCHANT_ID.getName()));
            request.put("channel", "api");
            request.put("extTransactionId", "BHARTIPAY"+fields.get(FieldType.PG_REF_NUM.getName()));
            request.put("upiId", fields.get(FieldType.PAYER_ADDRESS.getName()));
            request.put("terminalId", fields.get(FieldType.ADF2.getName()));
            request.put("sid", fields.get(FieldType.ADF3.getName()));
            String checksumKey = fields.get(FieldType.ADF4.getName());
            concatenatedString = "" + request.get("source") + request.get("channel") + request.get("extTransactionId") + request.get("upiId") + request.get("terminalId") + request.get("sid");
            logger.info("COSMOS UPI concatenatedString+++++++++++++++++++++++++++++++++++++++++++++" + concatenatedString);

            String encryptedCheckSum = generateChecksum(concatenatedString, checksumKey);
            logger.info("COSMOS UPI encryptedCheckSum+++++++++++++++++++++++++++++++++++++++++++++" + encryptedCheckSum);

            request.put("checksum", encryptedCheckSum);
            String hostUrl = PropertiesManager.propertiesMap.get("cosmosupiValidetorUrl");
            logger.info("COSMOS UPI VPA validation request :{}, {}", fields.get(FieldType.ORDER_ID.getName()), request.toString());

            String enc = CosmosUtil.encrypt(request.toString(), fields.get(FieldType.TXN_KEY.getName()));
            logger.info("COSMOS UPI VPA validation request enc :{}", enc);
            logger.info("COSMOS UPI VPA validation request CID :{}", fields.get(FieldType.ADF1.getName()));

            String vpaValidationEncResponse = callRESTApi(hostUrl, enc, "POST", fields.get(FieldType.ADF1.getName()));
            logger.info("COSMOS UPI vpaValidation API Response Encrypted :{}", vpaValidationEncResponse);
            String vpaValidationResponse = CosmosUtil.decrypt(vpaValidationEncResponse, fields.get(FieldType.TXN_KEY.getName()));
            logger.info("COSMOS UPI VPA validation Response : {},{}", fields.get(FieldType.ORDER_ID.getName()), vpaValidationResponse);
            if (StringUtils.isNotBlank(vpaValidationResponse)) {
            	
                JSONObject vpaResponse = new JSONObject(vpaValidationResponse);
                if (StringUtils.isNotBlank(vpaResponse.getString("status"))) {
                	
                	JSONArray dataArray = vpaResponse.getJSONArray("data");
                    JSONObject data = dataArray.getJSONObject(0);
                    logger.info("data " + data);
                    if(data.has("customerName")) {
                    fields.put(FieldType.CARD_HOLDER_NAME.getName(),data.getString("customerName"));
                    }

                    String CosmosTimeOut = PropertiesManager.propertiesMap.get("CosmosTimeOut");

                    if(StringUtils.isNotBlank(CosmosTimeOut)&&CosmosTimeOut.equalsIgnoreCase("Y")) {
                    	
                    if (data.getString("respMessge").equalsIgnoreCase("SUCCESS")) {
                        return true;
                    } else
                        return false;
                }else {
                	
                	 if (data.getString("status").equalsIgnoreCase("SUCCESS")) {
                         return true;
                     } else
                         return false;
                	
                }
                    
                }
            } else {
                return false;

            }
        } catch (SystemException e) {
        	throw new SystemException(e.getErrorType(), e.getMessage());
		}  catch (Exception e) {
            logger.error("Error in VPA validaiton response parsing for COSMOS UPI", e);
        }

        return false;

    }

    public void collectAPI(Fields fields) throws SystemException {

        //removed hardcoding
        String customerName = StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))?fields.get(FieldType.CUST_NAME.getName()):"";
        String concatenatedString = null;
        try {
            //String amount= fields.get(FieldType.AMOUNT.getName());
    		String amount = acquirerTxnAmountProvider.amountProvider(fields);

            logger.info("COSMOS UPI Collect API amount=================== :{}",amount);

            JSONObject request = new JSONObject();
            request.put("source", fields.get(FieldType.MERCHANT_ID.getName()));
            request.put("channel", "api");
            request.put("extTransactionId", "BHARTIPAY"+fields.get(FieldType.PG_REF_NUM.getName()));
            request.put("upiId", fields.get(FieldType.PAYER_ADDRESS.getName()));
            request.put("terminalId", fields.get(FieldType.ADF2.getName()));
            request.put("amount", amount);
            request.put("customerName", fields.get(FieldType.CARD_HOLDER_NAME.getName()));
            request.put("infoKYC", " ");
            request.put("statusKYC", "");
            request.put("remark", "");
            request.put("requestTime", "");
            request.put("sid", fields.get(FieldType.ADF3.getName()));
            //request.put("param_1",fields.get(FieldType.CARD_HOLDER_NAME.getName()));
            String checksumKey = fields.get(FieldType.ADF4.getName());
            concatenatedString = "" + request.get("source") + request.get("channel") + request.get("extTransactionId") + request.get("upiId") + request.get("terminalId") + request.get("amount") + request.get("customerName") + request.get("infoKYC") + request.get("statusKYC") + request.get("remark") + request.get("requestTime") + request.get("sid");
logger.info(concatenatedString);
            String encryptedCheckSum = CosmosUtil.generateChecksum(concatenatedString, checksumKey);
            request.put("checksum", encryptedCheckSum);
            String hostUrl = PropertiesManager.propertiesMap.get("cosmosupiCollectAPIUrl");


            logger.info("COSMOS UPI Collect API request :{}, {}", fields.get(FieldType.ORDER_ID.getName()), request.toString());
            String enc = CosmosUtil.encrypt(request.toString(), fields.get(FieldType.TXN_KEY.getName()));
            logger.info("COSMOS UPI Collect API request enc :{}", enc);
            logger.info("COSMOS UPI Collect request CID :{}", fields.get(FieldType.ADF1.getName()));

            String vpaValidationEncResponse = callRESTApi(hostUrl, enc, "POST", fields.get(FieldType.ADF1.getName()));
            String collectAPIResponse = CosmosUtil.decrypt(vpaValidationEncResponse, fields.get(FieldType.TXN_KEY.getName()));

            logger.info("COSMOS UPI Collect API ENC Response : {},{}", fields.get(FieldType.ORDER_ID.getName()), vpaValidationEncResponse);
            logger.info("COSMOS UPI Collect API Response :: {},{}", fields.get(FieldType.ORDER_ID.getName()), collectAPIResponse);

            if (StringUtils.isNotBlank(collectAPIResponse)) {
                JSONObject vpaResponse = new JSONObject(collectAPIResponse);
                if (StringUtils.isNotBlank(vpaResponse.getString("status"))) {
                    if (vpaResponse.getString("status").equalsIgnoreCase("SUCCESS")) {
                        JSONArray dataArray = vpaResponse.getJSONArray("data");
                        JSONObject data = dataArray.getJSONObject(0);
                        logger.info("data " + data);
                        logger.info("data respCode " + data.getString("respCode"));
                        if ("0".equalsIgnoreCase(data.getString("respCode"))) {
                            //success
                            logger.info("Step 01");
                            fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
                            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
                            fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

                        } else {
                            logger.info("Step 02");
                            fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
                            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getResponseCode());
                            fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());


                        }
                    } else {
                        logger.info("Step 03");
                        fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
                        fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getResponseCode());
                        fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
                    }
                } else {
                    logger.info("Step 04");
                    fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
                    fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getResponseCode());
                    fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
                }
            }
        } catch (SystemException e) {
        	throw new SystemException(e.getErrorType(), e.getMessage());
		} catch (Exception e) {
            logger.error("Error in Collect api response parsing for COSMOS ", e);
        }


    }

    public void statusCheckApi(Fields fields) throws SystemException {
        String concatenatedString = null;
        try {
        	logger.info("feidls in cosmos upi"+fields.getFieldsAsString());

            JSONObject request = new JSONObject();
            request.put("source", fields.get(FieldType.MERCHANT_ID.getName()));
            request.put("channel", "api");
            request.put("extTransactionId", "BHARTIPAY"+fields.get(FieldType.PG_REF_NUM.getName()));
            request.put("terminalId", fields.get(FieldType.ADF2.getName()));
            String checksumKey = fields.get(FieldType.ADF4.getName());
           
            concatenatedString = "" +request.get("source")+ request.get("channel") + request.get("terminalId") +request.get("extTransactionId");//+ request.get("terminalId");

            String encryptedCheckSum = generateChecksum(concatenatedString, checksumKey);
            
            request.put("checksum", encryptedCheckSum);
            String hostUrl;
            String internalRequestFields=fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
			String[] paramaters = internalRequestFields.split("~");
			Map<String, String> paramMap = new HashMap<String, String>();
			for (String param : paramaters) {
				String[] parameterPair = param.split("=");
				if (parameterPair.length > 1) {
					paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
				}
			}
            if ((StringUtils.isNotEmpty(fields.get(FieldType.UPI_INTENT.getName())) && fields.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1"))||fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.QRCODE.getCode())) {            	
            	
                hostUrl = PropertiesManager.propertiesMap.get("cosmosupiStatusintentAPIUrl");

            }else {
                 hostUrl = PropertiesManager.propertiesMap.get("cosmosupiStatusAPIUrl");

            }

            logger.info("COSMOS UPI status API request :{}, {}", fields.get(FieldType.ORDER_ID.getName()), request.toString());
            String enc = CosmosUtil.encrypt(request.toString(), fields.get(FieldType.TXN_KEY.getName()));
            logger.info("COSMOS UPI status API request enc :{}", enc);
            logger.info("COSMOS UPI status API request CID :{}", fields.get(FieldType.ADF1.getName()));
logger.info("host url"+hostUrl);
            String statusCheckApiEncResponse = callRESTApi(hostUrl, enc, "POST", fields.get(FieldType.ADF1.getName()));
            if (StringUtils.isNotBlank(statusCheckApiEncResponse)){
            String statusCheckApiResponse = CosmosUtil.decrypt(statusCheckApiEncResponse, fields.get(FieldType.TXN_KEY.getName()));
            logger.info("COSMOS UPI status API ENC Response : {},{}", fields.get(FieldType.ORDER_ID.getName()), statusCheckApiEncResponse);

            logger.info("COSMOS UPI status API Response : {},{}", fields.get(FieldType.ORDER_ID.getName()), statusCheckApiResponse);
            JSONObject statusResponseJson = new JSONObject(statusCheckApiResponse);

            JSONArray data = statusResponseJson.getJSONArray("data");
	        String amount = data.getJSONObject(0).getString("amount");
        	String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
            if (StringUtils.isNotBlank(statusCheckApiResponse)&& toamount.equalsIgnoreCase(amount)) {
//            	if (StringUtils.isNotEmpty(paramMap.get(FieldType.UPI_INTENT.getName())) && paramMap.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1")) {
//            		CosmosUpiTransformer.updateUpiIntentStatus(fields,statusCheckApiResponse);
//                }else {
//             		CosmosUpiTransformer.updateUpiStatus(fields,statusCheckApiResponse);
//
//                }
            	
            	if (org.apache.commons.lang.StringUtils.isNotBlank(data.getJSONObject(0).getString("respMessge"))&&data.getJSONObject(0).getString("respMessge").equalsIgnoreCase("PENDING")&&data.getJSONObject(0).getString("respCode").equalsIgnoreCase("1")) {
                     
                 	logger.info("the status is not update because this it is pending status for this pgref no"+fields.get(FieldType.PG_REF_NUM.getName()));
                 }else {
          
		CosmosUpiTransformer.updateUpiIntentStatus(fields,statusCheckApiResponse);
                 }
            }} 
        } catch (Exception e) {
            logger.error("Error in statusCheck validaiton response parsing for COSMOS ", e);
        }


    }

    public JSONObject transactionStatus(Fields fields) {
        try {
            String concatenatedString = null;
            JSONObject request = new JSONObject();

            logger.info("source======================"+fields.get(FieldType.MERCHANT_ID.getName()));
            request.put("source", fields.get(FieldType.MERCHANT_ID.getName()));
            request.put("channel", "api");
            request.put("extTransactionId", "BHARTIPAY"+fields.get(FieldType.PG_REF_NUM.getName()));
            logger.info("terminalId==============="+fields.get(FieldType.ADF2.getName()));
           request.put("terminalId", fields.get(FieldType.ADF2.getName()));
            logger.info("transactionStatus  or dual verification  request===cosmos======================="+request);
            String checksumKey = fields.get(FieldType.ADF4.getName());
            concatenatedString = "" +request.get("source")+ request.get("channel") + request.get("terminalId") +request.get("extTransactionId");//+ request.get("terminalId");
            logger.info("concatenatedString==================="+concatenatedString);

            String encryptedCheckSum = generateChecksum(concatenatedString, checksumKey);

            logger.info("encryptedCheckSum==================="+encryptedCheckSum);
            request.put("checksum", encryptedCheckSum);
            logger.info("cosmos dual verification request==================="+request.toString());
            String hostUrl;
            if ((StringUtils.isNotEmpty(fields.get(FieldType.UPI_INTENT.getName())) && fields.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1"))||fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.QRCODE.getCode())) {            	
            	
                hostUrl = PropertiesManager.propertiesMap.get("cosmosupiStatusintentAPIUrl");

            }else {
                 hostUrl = PropertiesManager.propertiesMap.get("cosmosupiStatusAPIUrl");

            }
            logger.info("COSMOS UPI transaction API request :{}, {}", fields.get(FieldType.ORDER_ID.getName()), request.toString());
            String enc = CosmosUtil.encrypt(request.toString(), fields.get(FieldType.TXN_KEY.getName()));
            logger.info("COSMOS UPI transaction API request enc :{}", enc);
            String transactionENCResponse = callRESTApi(hostUrl, enc, "POST", fields.get(FieldType.ADF1.getName()));
            logger.info("COSMOS UPI transactionStatus  or dual verification  enc :{}", enc);
            String transactionStatusResponse = CosmosUtil.decrypt(transactionENCResponse, fields.get(FieldType.TXN_KEY.getName()));
            logger.info("COSMOS UPI transactionStatus  or dual verification Response : {},{}", fields.get(FieldType.ORDER_ID.getName()), transactionENCResponse);

            logger.info("COSMOS UPI transactionStatus  or dual verification  Response : {},{}", fields.get(FieldType.ORDER_ID.getName()), transactionStatusResponse);
            if (org.apache.commons.lang.StringUtils.isNotBlank(transactionStatusResponse)) {
                JSONObject statusResponseJson = new JSONObject(transactionStatusResponse);
                return statusResponseJson;
              /*  if (org.apache.commons.lang.StringUtils.isNotBlank(statusResponseJson.getString("status"))) {
                    if (statusResponseJson.getString("status").equalsIgnoreCase("SUCCESS")) {
                        JSONObject data = statusResponseJson.getJSONObject("data");
                        if ("0".equalsIgnoreCase(data.getString("resCode"))) {

                        } else {

                        }
                    } else
                        return false;
                }
            } else {
                return false;

            } */
            }
        } catch (Exception e) {
            logger.error("Error in transaction status response parsing for COSMOS ", e);
        }

        return null;

    }

    public static String callRESTApi(String apiUrl, String request, String method, String cid) throws SystemException {

        StringBuilder serverResponse = new StringBuilder();
        //
        logger.info("COSMOS UPI callRESTApi CID :{}", cid);

        HttpsURLConnection connection = null;

        try {

            URL url = new URL(apiUrl);
            disableSSLCertificateChecking();
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
            //Code override for testing purpose.
            code = 503;
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
            logger.error("Exception in HTTP call COSMOS UPI ::" + apiUrl, e);
            logger.error("HTTP Response code of txn [COSMOS UPI] :::");
            throw new SystemException(ErrorType.ACUIRER_DOWN, "Network Exception with cosmos Upi "+ apiUrl);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return serverResponse.toString();
    }


    public static void main(String[] args) {
        Fields fields = new Fields();

        String concatenatedString = null;
        try {

            JSONObject request = new JSONObject();
            request.put("source", "Merchant123");
            request.put("channel", "api");
            request.put("extTransactionId", "XYZ211001adkhff123233a");
            request.put("upiId", "vijay@123");
            request.put("terminalId", "merchant123-001");
            request.put("sid", "submerchant-001");
            String checksumKey = "khff123233a)";

            concatenatedString = "" + request.get("source") + request.get("channel") + request.get("extTransactionId") + request.get("upiId") + request.get("terminalId") + request.get("sid");
            logger.info("COSMOS UPI VPA validation concatenatedString : {}", concatenatedString);

            String encryptedCheckSum = generateChecksum(concatenatedString, checksumKey);
            request.put("checksum", encryptedCheckSum);
            String hostUrl = " https://merchantuat.timepayonline.com/evok/cm/v2/verifyVPA"; //PropertiesManager.propertiesMap.get("cosmosupiValidetorUrl");
            logger.info("COSMOS UPI VPA validation request :{}, {}", fields.get(FieldType.ORDER_ID.getName()), request.toString());

            String enc = CosmosUtil.encrypt(request.toString(), "6b61d0cbbf7c4e239148bd78c3f5a0b0");
            logger.info("enc==" + enc);
            String vpaValidationResponse = callRESTApi(hostUrl, request.toString(), "POST", "");


            logger.info("COSMOS UPI VPA validation Response : {},{}", fields.get(FieldType.ORDER_ID.getName()), vpaValidationResponse);
            if (StringUtils.isNotBlank(vpaValidationResponse)) {

                JSONObject vpaResponse = new JSONObject(vpaValidationResponse);

                if (StringUtils.isNotBlank(vpaResponse.getString("status"))) {

                    if (vpaResponse.getString("status").equalsIgnoreCase("SUCCESS")) {
                        return;
                    } else
                        return;
                }
            } else {
                return;

            }
        } catch (Exception e) {
            logger.error("Error in VPA validaiton response parsing for COSMOS ", e);
        }

    }

	private static void disableSSLCertificateChecking() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// Not implemented
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// Not implemented
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("TLS");

			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}