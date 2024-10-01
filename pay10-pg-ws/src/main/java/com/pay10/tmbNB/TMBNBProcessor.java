package com.pay10.tmbNB;


import com.infosys.feba.tools.shoppingmallencryption.ShoppingMallSymmetricCipherHelper;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.*;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;
import com.pay10.pg.core.util.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service("TMBNBProcessor")
public class TMBNBProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(TMBNBProcessor.class.getName());

    @Override
    public void preProcess(Fields fields) throws SystemException {

    }

    @Override
    public void process(Fields fields) throws SystemException {
        //logger.info(" process TMB NetBanking fields=======================================: {}", fields.getFieldsAsString());

        if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
                .equals(TransactionType.REFUNDRECO.getName()))) {
            return;
        }

        if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.TMBNB.getCode())) {
            return;
        }
        saleRequest(fields);
        logger.info(" process TMB NetBanking fields:++++++++++++++++++++++++++++++++++++++++ {}", fields.getFieldsAsString());

    }


    @Override
    public void postProcess(Fields fields) throws SystemException {

    }


    public String saleRequest(Fields fields) throws SystemException {
        if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
            fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
        }
        String transactionUrl = PropertiesManager.propertiesMap.get("TMBNBSaleURL");
        String TMB_RETURN_URL = PropertiesManager.propertiesMap.get("TMB_RETURN_URL");
        String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
        StringBuilder request = new StringBuilder();
        request.append("FORMSGROUP_ID__");
        request.append("=");
        request.append("AuthenticationFG");
        request.append("&");
        request.append("__START_TRAN_FLAG__");
        request.append("=");
        request.append("Y");
        request.append("&");
        request.append("FG_BUTTONS__");
        request.append("=");
        request.append("LOAD");
        request.append("&");
        request.append("ACTION.LOAD");
        request.append("=");
        request.append("Y");
        request.append("&");
        request.append("AuthenticationFG.LOGIN_FLAG");
        request.append("=1");
        request.append("&");
        request.append("BANK_ID");
        request.append("=");
        request.append("TMB");
        request.append("&");
        request.append("AuthenticationFG.USER_TYPE");
        request.append("=");
        request.append("1");
        request.append("&");
        request.append("AuthenticationFG.MENU_ID");
        request.append("=");
        request.append("CIMSHP");
        request.append("&");
        request.append("AuthenticationFG.CALL_MODE");
        request.append("=");
        request.append("2");
        request.append("&");
        request.append("RU");
        request.append("=");
        request.append(TMB_RETURN_URL);
        request.append("&");
        request.append("CATEGORY_ID");
        request.append("=");
        request.append(fields.get(FieldType.ADF1.getName()));//PAYTEN
        request.append("&");

        StringBuilder qs = new StringBuilder();
        qs.append("ShoppingMallTranFG.TRAN_CRN");
        qs.append("~INR");
        qs.append("|");
        qs.append("ShoppingMallTranFG.TXN_AMT");
        qs.append("~");
        String amount1 = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356");
        qs.append(amount1);
        qs.append("|");
        qs.append("ShoppingMallTranFG.PID");
        qs.append("~");
        qs.append(fields.get(FieldType.MERCHANT_ID.getName()));
        qs.append("|");
        qs.append("ShoppingMallTranFG.PRN");
        qs.append("~");
        if (pgRefNo == null) {
            fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
        }

        qs.append(fields.get(FieldType.PG_REF_NUM.getName()));
        qs.append("|");
        qs.append("ShoppingMallTranFG.ITC");
        qs.append("~");
        qs.append("M-" + fields.get(FieldType.ADF5.getName()));//M–PAY10
        logger.info("TMB NB Sale QS request :{}", qs.toString());

        String encodedUrl = null;
        String encryptedQS = null;
        try {
            encryptedQS = ShoppingMallSymmetricCipherHelper.encrypt(qs.toString(), fields.get(FieldType.TXN_KEY.getName()), "AES");//KEY = "P@y10@)tmb"
            logger.info("TMB NB Sale encryptedQS:{}", encryptedQS.toString());
            encodedUrl = URLEncoder.encode(encryptedQS, "UTF-8");
        } catch (Exception exception) {
            logger.error("Error while encrypting TMB NetBanking request: {}", exception.getMessage());
        }
        request.append("QS");
        request.append("=");
        request.append(encodedUrl);

        String finalRequest = transactionUrl + "?" + request;
        logger.info("TMB NB final SALE Request:{}", finalRequest.toString());
        updateSaleResponse(fields, finalRequest.toString());
        return finalRequest.toString();

    }


    public String statusEnquiry(Fields fields, boolean isDual) {
        String statusUrl = PropertiesManager.propertiesMap.get("TMBNBstatusURL");
        StringBuilder request = new StringBuilder();
        String TMB_RETURN_URL = PropertiesManager.propertiesMap.get("TMB_RETURN_URL");
        String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
        if (pgRefNo == null)
            fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
        request.append("Action.ShoppingMall.Login.Init");
        request.append("=");
        request.append("Y");
        request.append("&");
        request.append("BankId");
        request.append("=");
        request.append("TMB");
        request.append("&");
        request.append("MD");
        request.append("=");
        request.append("V");
        request.append("&");
        request.append("PID");
        request.append("=");
        request.append(fields.get(FieldType.MERCHANT_ID.getName()));
        request.append("&");
        // if(FieldType.TXNTYPE.getName().equalsIgnoreCase(TransactionType.SALE.getName())){
        request.append("BID");
        request.append("=");
        request.append(fields.get(FieldType.RRN.getName()));
        request.append("&");//}
        request.append("AMT");
        request.append("=");
        String amount1 = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356");
        request.append(amount1);
        request.append("&");
        request.append("ITC");
        request.append("=");
        request.append(fields.get(FieldType.ADF5.getName()));
        request.append("&");
        request.append("CRN");
        request.append("=");
        request.append("INR");
        request.append("&");
        request.append("PRN");
        request.append("=");
        request.append(pgRefNo);
        request.append("&");
        request.append("CG");
        request.append("=");
        request.append("Y");
        request.append("&");
        request.append("USER_LANG_ID");
        request.append("=");
        request.append("001");
        request.append("&");
        request.append("UserType");
        request.append("=");
        request.append("1");
        request.append("&");
        request.append("AppType");
        request.append("=");
        request.append("corporate");
        request.append("&");
        request.append("RU");
        request.append("=");
        request.append(TMB_RETURN_URL);//+"?"+"MID="+fields.get(FieldType.MERCHANT_ID.getName()));
        if (isDual) {
            logger.info("TMB NB dual verification Plain request:{},pgRefNo:{}", request.toString(),pgRefNo);
        } else {
            logger.info("TMB NB Status check Plain request:{},pgRefNo:{}", request.toString(),pgRefNo);
        }
        //request.append(TMB_RETURN_URL+"?"+"PG_REF="+fields.get(FieldType.PG_REF_NUM.getName())+"&"+"MID="+fields.get(FieldType.MERCHANT_ID.getName()));

        String finalStatusURL = statusUrl + "?" + request.toString();
        logger.info("TMB NB Status Enquiry Request finalStatusURL:{}", finalStatusURL);
        String response = callRESTApi(statusUrl, request.toString(), "GET");
        logger.info("TMB NB Status Enquiry Response :{}", response);
        return response.toString();

    }

    public void enquiryStatus(Fields fields) {
        String statusUrl = PropertiesManager.propertiesMap.get("TMBNBstatusURL");
        StringBuilder request = new StringBuilder();
        String TMB_RETURN_URL = PropertiesManager.propertiesMap.get("TMB_RETURN_URL");
        String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());

        request.append("Action.ShoppingMall.Login.Init");
        request.append("=");
        request.append("Y");
        request.append("&");
        request.append("BankId");
        request.append("=");
        request.append("TMB");
        request.append("&");
        request.append("MD");
        request.append("=");
        request.append("V");
        request.append("&");
        request.append("PID");
        request.append("=");
        request.append(fields.get(FieldType.MERCHANT_ID.getName()));
        request.append("&");
        request.append("BID");
        request.append("=");
        request.append("");
        request.append("&");
        request.append("AMT");
        request.append("=");
        String amount1 = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356");
        request.append(amount1);
        request.append("&");
        request.append("ITC");
        request.append("=");
        request.append("M-" + fields.get(FieldType.ADF5.getName()));
        request.append("&");
        request.append("CRN");
        request.append("=");
        request.append("INR");
        request.append("&");
        request.append("PRN");
        request.append("=");
        request.append(pgRefNo);
        request.append("&");
        request.append("CG");
        request.append("=");
        request.append("Y");
        request.append("&");
        request.append("USER_LANG_ID");
        request.append("=");
        request.append("001");
        request.append("&");
        request.append("UserType");
        request.append("=");
        request.append("1");
        request.append("&");
        request.append("AppType");
        request.append("=");
        request.append("corporate");
        request.append("&");
        request.append("RU");
        request.append("=");
        request.append(TMB_RETURN_URL);//+"?"+"MID="+fields.get(FieldType.MERCHANT_ID.getName()));
        logger.info("TMB NB enquiryStatus request:{}", request.toString());

        //request.append(TMB_RETURN_URL+"?"+"PG_REF="+fields.get(FieldType.PG_REF_NUM.getName())+"&"+"MID="+fields.get(FieldType.MERCHANT_ID.getName()));

        String finalStatusURL = statusUrl + "?" + request.toString();
        logger.info("TMB NB enquiryStatus finalStatusURL:{}", finalStatusURL);
        String response = callRESTApi(statusUrl, request.toString(), "GET");
        logger.info("TMB NB enquiryStatus Response :{}", response);
        String str = response.replaceAll("<.*?>", " ");

        Map<String, String> newmap = splitString(str);
        logger.info("dual verification newmap: " + newmap);
        String bId = "";
        if (newmap.containsKey("BID")) {
            bId = newmap.get("BID");
        }
        logger.info("dual verification Bid: " + bId);

        ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(newmap.get("STATUS"), "TMBNB");
        if (errorMappingDTO != null) {
            fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
            fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
            fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
            fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
            fields.put(FieldType.ACQ_ID.getName(), bId);
            //fields.put(FieldType.PG_TXN_STATUS.getName(), ErrorType.ACQUIRER_ERROR.getCode());
        }


    }

    public static String callRESTApi(String apiUrl, String request, String method) {

        StringBuilder serverResponse = new StringBuilder();
        //
        HttpsURLConnection connection = null;
        logger.info("TMBNB callRESTApi request :{}", request.toString());
        try {

            URL url = new URL(apiUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setDoOutput(true);
            // connection.setDoInput(true);
            OutputStream os = connection.getOutputStream();
            os.write(request.getBytes());
            os.flush();
            os.close();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;

            int code = ((HttpURLConnection) connection).getResponseCode();//200, 401,404, 500
            int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
            if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
                logger.error("HTTP Response code of txn [TMB NB] :" + code);
                throw new SystemException(ErrorType.ACUIRER_DOWN,
                        "Network Exception with TMB NB"
                                + apiUrl);
            }
            while ((line = rd.readLine()) != null) {
                serverResponse.append(line);

            }
            rd.close();

        } catch (Exception e) {
            logger.error("Exception in HTTP call TMB NB :" + apiUrl, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return serverResponse.toString();
    }

    public void updateSaleResponse(Fields fields, String request) {

        fields.put(FieldType.TMBNB_FINAL_REQUEST.getName(), request);
        fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
        fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
        fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

    }

    public static Map<String, String> splitString(String s) {
        String[] split = s.split("[= ? &]+");
        // String data = split[2];
        int length = split.length;
        Map<String, String> maps = new HashMap<>();

        for (int i = 0; i < length; i += 2) {
            maps.put(split[i], split[i + 1]);
        }

        return maps;

    }

}
