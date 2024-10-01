package com.pay10.canaraNBbank;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.*;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;
import com.pay10.hdfc.upi.Constants;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.CanaraUtil;
import com.pay10.pg.core.util.Processor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service("CanaraNBProcessor")
public class CanaraNBProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(CanaraNBProcessor.class.getName());
    @Autowired
    private AcquirerTxnAmountProvider acquirerTxnAmountProvider;
    @Autowired
    private UserDao userDao;
    @Override
    public void preProcess(Fields fields) throws SystemException {

    }

    @Override
    public void process(Fields fields) throws SystemException {
        logger.info(" process CANARA NetBanking fields=======================================: {}", fields.getFieldsAsString());

        if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
                || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
                .equals(TransactionType.REFUNDRECO.getName()))) {
            return;
        }

        if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.CANARANBBANK.getCode())) {
            return;
        }
        saleRequest(fields);
        logger.info(" process CANARA NetBanking fields:++++++++++++++++++++++++++++++++++++++++ {}", fields.getFieldsAsString());

    }


    @Override
    public void postProcess(Fields fields) throws SystemException {

    }


    public String saleRequest(Fields fields) throws SystemException {
        if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
            fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
        }
        String transactionUrl = PropertiesManager.propertiesMap.get("CANARANBSaleURL");
        String encryptedResponse = "";
        String responseUrl = "";
        String amount = acquirerTxnAmountProvider.amountProvider(fields);
        String merchant = userDao.getBusinessNameByPayId(fields.get(FieldType.PAY_ID.getName())); 
        String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
        if (pgRefNo == null)
            fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
        logger.info(" saleRequest CANARA NetBanking url: {}", transactionUrl);
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy+HH:mm:ss");
        String date = myDateObj.format(myFormatObj);

        StringBuilder request = new StringBuilder();
        request.append("fldTxnId");
        request.append("=");
        request.append("PER");
        request.append("&");
        request.append("fldClientCode");
        request.append("=");
        request.append("CLIENTCODE");
        request.append("&");
        request.append("fldClientAccount");
        request.append("=");
        request.append("");
        request.append("&");
        request.append("fldMerchCode");
        request.append("=");
        request.append(fields.get(FieldType.MERCHANT_ID.getName()));
        request.append("&");
        request.append("fldTxnCurr");
        request.append("=INR");
        request.append("&");
        request.append("fldTxnAmt");
        request.append("=");
       // String amount1 = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356");
       // logger.info("amount:::::::::::::::::::::::::" + amount1);
        request.append(amount);
        request.append("&");
        request.append("fldTxnScAmt");
        request.append("=");
        request.append("0");
        request.append("&");
        request.append("fldMerchRefNbr");
        request.append("=");
        request.append(fields.get(FieldType.PG_REF_NUM.getName()));
        request.append("&");
        request.append("fldSucStatFlg");
        request.append("=");
        request.append("N");
        request.append("&");
        request.append("fldFailStatFlg");
        request.append("=");
        request.append("N");
        request.append("&");
        request.append("fldDatTimeTxn");
        request.append("=");
        request.append(date);
        request.append("&");
        request.append("fldRef1");
        request.append("=");
        request.append(fields.get(FieldType.PAY_ID.getName()));
        request.append("&");
        request.append("fldRef2");
        request.append("=");
        request.append(merchant);
        request.append("&");
        request.append("fldRef3");
        request.append("=");
        request.append("");
        request.append("&");
        request.append("fldRef4");
        request.append("=");
        request.append("");
        request.append("&");
        request.append("fldRef5");
        request.append("=");
        request.append("");
        request.append("&");
        request.append("fldRef6");
        request.append("=");
        request.append("");
        request.append("&");
        request.append("fldRef7");
        request.append("=");
        request.append("");
        request.append("&");
        request.append("fldRef8");
        request.append("=");
        request.append("");
        request.append("&");
        request.append("fldRef9");
        request.append("=");
        request.append("");
        logger.info("Before checksum request: {}", request.toString());
        String checksum = CanaraUtil.ChecksumCal(request.toString());
        logger.info("after checksum : {}", checksum);
        request.append("&");
        request.append("checksum");
        request.append("=");
        request.append(checksum);
        String encryptedRequest = null;
        logger.info("canara NB Palin Text:{}", request.toString());
        try {
            encryptedRequest = CanaraUtil.encryptString(request.toString(), fields.get(FieldType.ADF1.getName()), fields.get(FieldType.ADF2.getName()));
            logger.info("canara NB encryptedRequest:{}", encryptedRequest.toString());
        } catch (Exception exception) {
            logger.error("Error while encrypting CANARA NetBanking request: {}", exception.getMessage());
        }
        String finalRequest = transactionUrl + "?encdata=" + encryptedRequest;
        logger.info("canara NB finalRequest:{}", finalRequest.toString());
        updateSaleResponse(fields, finalRequest.toString());
        return finalRequest.toString();


    }

    public void enquiryProcessor(Fields fields) {

        try {
            String decryptedResponse = null;

            String Response = statusEnquiry(fields,false);
            String encryptedDualResp = getTagValue(Response, "Output");
            //doubleVerifResponse = parseStatusEnquiry(dualVerificationResponse);
            logger.info("Canara Bank NB Status EnquiryProcessor fields:{}" + fields.getFieldsAsString());
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(fields.get(FieldType.ADF1.getName())) && org.apache.commons.lang3.StringUtils.isNotEmpty(fields.get(FieldType.ADF2.getName()))) {
                decryptedResponse = CanaraUtil.decryptString(encryptedDualResp, fields.get(FieldType.ADF1.getName()), fields.get(FieldType.ADF2.getName()));
                logger.info("Canara NB Status EnquiryProcessor Response :{}", decryptedResponse);
            }
            String[] paramatersResp = decryptedResponse.split("~");
            Map<String, String> doubleVerifResponseMap = new HashMap<String, String>();
            //db
            for (String param : paramatersResp) {
                String[] parameterPair = param.split("=");
                if (parameterPair.length > 1) {
                    doubleVerifResponseMap.put(parameterPair[0], parameterPair[1]);
                }
            }


            logger.info("Canara NB Status EnquiryProcessor in MAP:" + doubleVerifResponseMap);
            String amont = doubleVerifResponseMap.get("TxnAmt");
            String PG_refNO = doubleVerifResponseMap.get("MerchRefNbr");
            String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));

            if (amont.equalsIgnoreCase(amount) && PG_refNO.equalsIgnoreCase(fields.get(FieldType.PG_REF_NUM.getName()))) {
            	String statuCode = doubleVerifResponseMap.get("ReturnCode")+doubleVerifResponseMap.get("VerifyStatus");
            	if(null != statuCode && !statuCode.equalsIgnoreCase("00")) {
            		statuCode = "11";
            	}
            	logger.info("Canara status enquiry final statuCode for errorMapping, statuCode = {}, PGREFNUM = {} ",statuCode,fields.get(FieldType.PG_REF_NUM.getName()));
                ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(statuCode, "CANARANBBANK");
                logger.info("Error code mapping CANARA BANK NB:{}", errorMappingDTO);

                fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
                fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
                fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
                fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
                fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
                fields.put(FieldType.ACQ_ID.getName(), doubleVerifResponseMap.get("BankRefNo"));
                //fields.put(FieldType.PG_TXN_STATUS.getName(), ErrorType.ACQUIRER_ERROR.getCode());
                fields.put(FieldType.RRN.getName(), doubleVerifResponseMap.get("BankRefNo"));

            } else {
                fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
                fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
                fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());

            }
            logger.info("Fields::"+fields.getFieldsAsString());
        } catch (SystemException e) {
            e.printStackTrace();
        }


    }


    public String statusEnquiry(Fields fields,boolean isDual) throws SystemException {
        String statusUrl = PropertiesManager.propertiesMap.get("CANARANBstatusURL");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String DateTime = now.format(format);
        // String statusUrl="https://testunnat.canarabank.in/entry/merchantverifyencr";
        logger.info("fields=" + fields.getFieldsAsString());

        String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
        //fields.put(fields.get(FieldType.TOTAL_AMOUNT.getName()), amount);
        // String amount = acquirerTxnAmountProvider.amountProvider(fields);
        //  String amount1 = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), "356");
        StringBuilder request = new StringBuilder();
        request.append("fldTxnId");
        request.append("=");
        request.append("VRF");
        request.append("&");
        request.append("fldClientCode");
        request.append("=");
        request.append("CLIENTCODE");
        request.append("&");
        request.append("fldClientAccount");
        request.append("=");
        request.append("");
        request.append("&");
        request.append("fldMerchCode");
        request.append("=");
        request.append(fields.get(FieldType.MERCHANT_ID.getName()));
        request.append("&");
        request.append("fldTxnCurr");
        request.append("=INR");
        request.append("&");
        request.append("fldTxnAmt");
        request.append("=");
        request.append(amount);
        request.append("&");
        request.append("fldTxnScAmt");
        request.append("=");
        request.append("0");
        request.append("&");
        request.append("fldMerchRefNbr");
        request.append("=");
        request.append(fields.get(FieldType.PG_REF_NUM.getName()));
        request.append("&");
        request.append("fldSucStatFlg");
        request.append("=");
        request.append("N");
        request.append("&");
        request.append("fldFailStatFlg");
        request.append("=");
        request.append("N");
        request.append("&");
        request.append("fldDatTimeTxn");
        request.append("=");
        request.append(DateTime);
        request.append("&");
        request.append("fldOrgDatTimeTxn");
        request.append("=");
        request.append(DateTime);
        request.append("&");
        request.append("fldBankRefNo");
        request.append("=");
        String rrn = fields.get(FieldType.RRN.getName());
        if (StringUtils.isBlank(rrn)) {
            request.append("");
        } else {
            request.append(fields.get(FieldType.RRN.getName()));
        }
        logger.info("Before checksum Status request: {}", request.toString());
        String checksum = CanaraUtil.ChecksumCal(request.toString());
        logger.info("after status checksum : {}", checksum);
        request.append("&");
        request.append("checkSum");
        request.append("=");
        request.append(checksum);
        String encryptedRequest = null;

        String KEY = fields.get(FieldType.ADF1.getName());
        String ivKEY = fields.get(FieldType.ADF2.getName());

        try {
            encryptedRequest = CanaraUtil.encryptString(request.toString(), KEY, ivKEY);
            logger.info("canara NB status encrypted Request:{}", encryptedRequest.toString());
        } catch (Exception exception) {
            logger.error("Error while encrypting CANARA NetBanking status request: {}", exception.getMessage());
        }
        if(isDual){
            logger.info("canara NB dual verification Plain request:{}", request.toString());
            logger.info("canara NB dual verification encrypted Request:{}", encryptedRequest.toString());
        }
        else {
            logger.info("canara NB Status check Plain request:{}", request.toString());
            logger.info("canara NB status encrypted Request:{}", encryptedRequest.toString());
        }
        StringBuilder encSB = new StringBuilder();
        encSB.append("fldMerchCode=")
                .append(fields.get(FieldType.MERCHANT_ID.getName()))
                .append("&encdata=")
                .append(encryptedRequest.toString());
        String finalStatusURL = statusUrl + "?" + encSB.toString();

        logger.info("finalStatusURL Canara NB bank URL:{}", finalStatusURL);


        String response = callRESTApi(statusUrl, encSB.toString(), "GET");
        logger.info("statusEnquiryResponse Canara NB bank :{}", response);

        return response.toString();


    }
   /* public String dualVerfication(Fields fields) throws SystemException {
        String statusUrl = PropertiesManager.propertiesMap.get("CANARANBstatusURL");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String DateTime = now.format(format);
        // String amount = acquirerTxnAmountProvider.amountProvider(fields);
        //  String amount1 = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), "356");
        StringBuilder request = new StringBuilder();
        request.append("fldTxnId");
        request.append("=");
        request.append("VRF");
        request.append("&");
        request.append("fldClientCode");
        request.append("=");
        request.append("CLIENTCODE");
        request.append("&");
        request.append("fldClientAccount");
        request.append("=");
        request.append("");
        request.append("&");
        request.append("fldMerchCode");
        request.append("=");
        request.append(fields.get(FieldType.MERCHANT_ID.getName()));
        request.append("&");
        request.append("fldTxnCurr");
        request.append("=INR");
        request.append("&");
        request.append("fldTxnAmt");
        request.append("=");
      // String amount1 = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), "356");
       //logger.info("amount1:::::::::::::::::::::::::"+amount1);
        request.append(fields.get(FieldType.AMOUNT.getName()));
        request.append("&");
        request.append("fldTxnScAmt");
        request.append("=");
        request.append("0");
        request.append("&");
        request.append("fldMerchRefNbr");
        request.append("=");
        request.append(fields.get(FieldType.PG_REF_NUM.getName()));
        request.append("&");
        request.append("fldSucStatFlg");
        request.append("=");
        request.append("N");
        request.append("&");
        request.append("fldFailStatFlg");
        request.append("=");
        request.append("N");
        request.append("&");
        request.append("fldDatTimeTxn");
        request.append("=");
        request.append(DateTime);
        request.append("&");
        request.append("fldOrgDatTimeTxn");
        request.append("=");
        request.append(DateTime);
        request.append("&");
        request.append("fldBankRefNo");
        request.append("=");
        request.append(fields.get(FieldType.RRN.getName()));
        String finalStatusURL = statusUrl + "?" + request.toString();
        String response = callRESTApi(statusUrl, request.toString(), "GET");

        logger.info("statusEnquiryRequest Canara NB bank URL:{}", finalStatusURL);
        return response;


    } */


    public void updateSaleResponse(Fields fields, String request) {

        fields.put(FieldType.CANARANB_FINAL_REQUEST.getName(), request);
        fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
        fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
        fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

    }

    public void StatusEnquirey(String xmlResponse, Fields fields) {

        String VerifyOutput = getTagValue(xmlResponse, "VerifyOutput");
        String VerifyStatus = getTagValue(xmlResponse, "VerifyStatus");


        String ClientAccount = getTagValue(xmlResponse, "ClientAccount");
        String MerchRefNbr = getTagValue(xmlResponse, "MerchRefNbr");
        String OrgDatTimeTxn = getTagValue(xmlResponse, "OrgDattimeTxn");
        String BankRefNo = getTagValue(xmlResponse, "BankRefNo");
        String TxnAmt = getTagValue(xmlResponse, "TxnAmt");
        String ReturnCode = getTagValue(xmlResponse, "ReturnCode");
        String MerchantCode = getTagValue(xmlResponse, "MerchantCode");
        String status = null;
        ErrorType errorType = null;

        if (ReturnCode.equals("0") && VerifyStatus.equals("0")) {
            status = StatusType.CAPTURED.getName();
            errorType = ErrorType.SUCCESS;

        } else {
            status = StatusType.FAILED.getName();
            errorType = ErrorType.REJECTED;

        }
        String txnType = fields.get(FieldType.TXNTYPE.getName());

        if (txnType.equals(TransactionType.STATUS.getName())) {
            fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.CANARANBBANK.getCode());
            fields.put(FieldType.PG_REF_NUM.getName(), MerchRefNbr);
            fields.put(FieldType.STATUS.getName(), status);
            fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
            fields.put(FieldType.PG_RESP_CODE.getName(), ReturnCode);
            fields.put(FieldType.PG_TXN_STATUS.getName(), VerifyStatus);
            fields.put(FieldType.PG_TXN_MESSAGE.getName(), VerifyStatus);
            fields.put(FieldType.ACQ_ID.getName(), BankRefNo);
            fields.put(FieldType.RRN.getName(), BankRefNo);
            fields.put(FieldType.AUTH_CODE.getName(), ReturnCode);

        }
    }


    public static String callRESTApi(String apiUrl, String params, String method) {

        StringBuilder serverResponse = new StringBuilder();
        //
        HttpsURLConnection connection = null;

        try {

            URL url = new URL(apiUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            //  connection.setRequestProperty("Content-Type", " text/plain");
            //   connection.setRequestProperty("Content-Length", "" + request.length());
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setDoOutput(true);
            // connection.setDoInput(true);
            OutputStream os = connection.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;

            int code = ((HttpURLConnection) connection).getResponseCode();//200, 401,404, 500
            int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
            if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
                logger.error("HTTP Response code of txn [CANARA NB] :" + code);
                throw new SystemException(ErrorType.ACUIRER_DOWN,
                        "Network Exception with CANARA NB"
                                + apiUrl);
            }
            while ((line = rd.readLine()) != null) {
                serverResponse.append(line);

            }
            rd.close();

        } catch (Exception e) {
            logger.error("Exception in HTTP call CANARA NB :" + apiUrl, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return serverResponse.toString();
    }

    public static void main1(String[] args) {

        String params = "fldTxnId=VRF&fldClientCode=CLIENTCODE&fldClientAccount=&fldMerchCode=PAY10&fldTxnCurr=INR&fldTxnAmt=12.00&fldTxnScAmt=0&fldMerchRefNbr=1049130129174808&fldSucStatFlg=N&fldFailStatFlg=N&fldDatTimeTxn=29/01/2023 18:26:33&fldOrgDatTimeTxn=29/01/2023 18:26:33&fldBankRefNo=&checksum=F0770B06B3B2A110D3FE9BCD82CA6787A1CE55D15A97FC804C527D817F084EBF";
        String encryptedRequest = CanaraUtil.encryptString(params, "vgdai3wlncw&*bai", "d7bjew^nkwqj*jRH");
        System.out.println("encryptedRequest" + encryptedRequest);

    }

    public static void main3(String[] args) {

        String resposne = "<? Xml version='1.0' encoding='UTF-8'?>" +
                "<VerifyOutput>" +
                "<Output>" +
                "ffjPAdhKfzlPOr%2FevPFMdDV3TbWUvoKb19w%2FNPuBNmM3MnoA3b92zdbo1T4LRoVc%2FDewyed8yaQHUSBAEureQYU1uU9IZBhDY81lY%2B7flmee9oMsV%2FYTq4gUKvSVcVQ0" +
                "</Output>" +
                "</VerifyOutput>";
        System.out.println(getTagValue(resposne, "VerifyOutput"));
        System.out.println(getTagValue(resposne, "Output"));
        String decryptedResponse = CanaraUtil.decryptString(getTagValue(resposne, "Output"), "vgdai3wlncw&*bai", "d7bjew^nkwqj*jRH");
        logger.info("Canara NB bank decryptedResponse1:{}", decryptedResponse);
        String[] paramatersResp = decryptedResponse.split("~");
        Map<String, String> paramMapResp = new HashMap<String, String>();
        for (String param : paramatersResp) {
            String[] parameterPair = param.split("=");
            if (parameterPair.length > 1) {
                paramMapResp.put(parameterPair[0], parameterPair[1]);
                logger.info("paramMapResp.put(parameterPair[0], parameterPair[1] :" + paramMapResp.put(parameterPair[0], parameterPair[1]));

            }
        }
    }

    public static void main2(String[] args) {
        String GET_URL = "https://testunnat.canarabank.in/entry/merchantverify";
        String params = "fldTxnId=VRF&fldClientCode=CLIENTCODE&fldClientAccount=&fldMerchCode=PAY10&fldTxnCurr=INR&fldTxnAmt=10000&fldTxnScAmt=0&fldMerchRefNbr=1090021214143915&fldSucStatFlg=N&fldFailStatFlg=N&fldDatTimeTxn=16/12/2022 15:21:57&fldOrgDatTimeTxn=16/12/2022 15:21:57&fldBankRefNo=null";
        // String resposne =  callRESTApi(GET_URL,params,"GET");
        String resposne = "<? Xml version='1.0' encoding='UTF-8'?>\n" +
                "<VerifyOutput>\n" +
                " <ClientAccount></ClientAccount>\n" +
                " \n" +
                " <MerchRefNbr>ABCD007</MerchRefNbr>\n" +
                " <OrgDatTimeTxn>03/09/2006 10:10:32</OrgDattimeTxn>\n" +
                " <BankRefNo>133298</BankRefNo>\n" +
                " <TxnAmt>100</TxnAmt>\n" +
                " <Status>\n" +
                " <ReturnCode>0</ReturnCode>\n" +
                " <VerifyStatus>0</VerifyStatus>\n" +
                " </Status>\n" +
                "</VerifyOutput>";
        System.out.println(getTagValue(resposne, "VerifyOutput"));
        System.out.println(getTagValue(resposne, "MerchRefNbr"));
        System.out.println(getTagValue(resposne, "BankRefNo"));
        System.out.println(getTagValue(resposne, "ReturnCode"));
        // System.out.println(dualVerification(resposne));

    }

    public static String getTagValue(String xml, String tagName) {
        return xml.split("<" + tagName + ">")[1].split("</" + tagName + ">")[0];
    }

    public static void main(String[] args) throws Exception {
        String GET_URL = "https://testunnat.canarabank.in/entry/merchantverify";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        System.out.println("After Formatting: " + formatDateTime);
        URL obj = new URL(GET_URL);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        String params = "B743D9ECCC9C7A6DBAC2A736D3757654F1D201D9948CD2511219187A106E387201C0B7FF33624C35467D2BCBF08476965D427BD18FC1FBC8D77FC632C8CE82791110CB45B078C477F6082C4C09787E1374B40D445AA1424D730AB739CE7B79E3E56A5CA14372DB2146EA613DE884B38362E9E2FFF67699EF6B7261063301BC2FD0D1EEEAAA7F0A96E2E368E3F53C34E3E2769C125AEDEB46F1869598CE14DC5C6496BA71BC8D2C1099E2A5F99FF9BCF4FEF4BC8667361D640EC28ACD72A07B78B926D44A74DBBD5B5104C8AB95D43F805DCAC0B1918257E4094EA77384ED5CC816F5EB16CBFBFDE416821FB18C4151887C374E112FD6D2A1CF485FACDF0E38698A85E85CA502BF1E4C81B7ACBFBEE044C2A174300A39DA903212D5B56AFD9F28F483D4AC4F39A6F589404DA8FB5F41CDA65481996FBB5F6CCC47AC1D3BB085EAFEBA4839AA6C7D69DADFF123DE0E85C44C5023A06D8A6F25635DFCD8072716AB";
        // String params = "fldTxnId=VRF&fldClientCode=CLIENTCODE&fldClientAccount=&fldMerchCode=PAY10&fldTxnCurr=INR&fldTxnAmt=10000&fldTxnScAmt=0&fldMerchRefNbr=1090021214143915&fldSucStatFlg=N&fldFailStatFlg=N&fldDatTimeTxn=16/12/2022 15:21:57&fldOrgDatTimeTxn=16/12/2022 15:21:57&fldBankRefNo=null";
        os.write(params.getBytes());
        os.flush();
        os.close();
        //  con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        // con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request did not work.");
        }
    }

}
