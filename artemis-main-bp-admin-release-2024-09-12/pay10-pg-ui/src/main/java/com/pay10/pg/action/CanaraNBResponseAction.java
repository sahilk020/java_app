package com.pay10.pg.action;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.*;
import com.pay10.pg.core.util.CanaraUtil;
import com.pay10.pg.core.util.ResponseCreator;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CanaraNBResponseAction extends AbstractSecureAction implements ServletRequestAware {
    private static Logger logger = LoggerFactory.getLogger(CanaraNBResponseAction.class.getName());
    private static final long serialVersionUID = 6155942791032490231L;
    @Autowired
    private FieldsDao fieldsDao;
    @Autowired
    private ResponseCreator responseCreator;
    @Autowired
    TransactionControllerServiceProvider transactionControllerServiceProvider;
    private Fields responseMap = null;
    private HttpServletRequest httpRequest;

    @Override
    public String execute() {
        try {
            Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
            Map<String, String> requestMap = new HashMap<String, String>();
            String Key = PropertiesManager.propertiesMap.get("CANARA_NB_ENC_KEY");
            String ivKey = PropertiesManager.propertiesMap.get("CANARA_NB_ENC_IV");

            for (Map.Entry<String, String[]> entry : fieldMapObj.entrySet()) {
                try {
                    requestMap.put(entry.getKey(), entry.getValue()[0]);

                } catch (ClassCastException classCastException) {
                    logger.error("Exception", classCastException);
                }
            }
            String decryptedResponse = "";
            Fields fields = new Fields();
            fields.put(FieldType.ADF1.getName(), Key);
             fields.put(FieldType.ADF2.getName(), ivKey);
            logger.info("Canara Bank NetBanking Encrypted Response Received From CanaraBank : {}", requestMap.toString());


            String encdata = requestMap.get("encdata");
            logger.info("Canara NB bank encdata:{}", encdata);

            // String salt = (String) sessionMap.get(FieldType.PASSWORD.getName());
            // CanaraUtil.decryptString(encdata, "vgdai3wlncw&*bai", "d7bjew^nkwqj*jRH");

            if (StringUtils.isNotEmpty(Key) && StringUtils.isNotEmpty(ivKey)) {
                decryptedResponse = CanaraUtil.decryptString(encdata, Key, ivKey);
                logger.info("Canara NB bank decryptedResponse1:{}", decryptedResponse);
            }

            logger.info("CANARA NetBanking Decrypted Response Received From CANARA Bank :{}", decryptedResponse);
            /*
            1- convert decrypted response in MAP (hm.put(str[0],str[1])
            2- then get from map and set into field PGREF = hm.get("MerchRefNo");

             */
            String[] paramatersRespo = decryptedResponse.split("&");
            Map<String, String> paramMapRespo = new HashMap<String, String>();

            //db
            for (String param : paramatersRespo) {
                String[] parameterPair = param.split("=");
                if (parameterPair.length > 1) {
                    paramMapRespo.put(parameterPair[0], parameterPair[1]);
                    logger.info("paramMapRespo.put(parameterPair[0], parameterPair[1] :" + paramMapRespo.put(parameterPair[0], parameterPair[1]));

                }
            }
            logger.info("paramMapRespo======== :" + paramMapRespo);

            String pgRefNum = paramMapRespo.get("MerchRefNo");
            String MerchantCode = paramMapRespo.get("MerchantCode");//MerchantCode
            String Message = paramMapRespo.get("Message");
            String TxnAmount = paramMapRespo.get("TxnAmount");
            String fldBankRefNo = paramMapRespo.get("fldBankRefNo");
            logger.info("TxnAmount :" + TxnAmount);

            String ClientCode = paramMapRespo.get("ClientCode");
            String TxnCurrency = paramMapRespo.get("TxnCurrency");


            //fields.put(FieldType.TRA.getName(), Message);//fields.put(FieldType.C.getName(), Message);

            if (StringUtils.isNotBlank(pgRefNum)) {
                logger.info("Get Fields Data From DB For CANARANB, PG_REF_NUM2 : " + pgRefNum);
                fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);
                logger.info("fields ==================================:{}", fields);

                String internalRequestFields = fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
                logger.info("internalRequestFields ==================================:{}", internalRequestFields);
                // fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
                String[] paramaters = internalRequestFields.split("~");
                Map<String, String> paramMap = new HashMap<String, String>();

                //db
                for (String param : paramaters) {
                    String[] parameterPair = param.split("=");
                    if (parameterPair.length > 1) {
                        if (parameterPair[0].trim().equalsIgnoreCase("RETURN_URL")) {
                            String[] tempArrayRetURL = param.split("=", 2);
                            logger.info(" Split_RETURN_URL : " + tempArrayRetURL[0].trim() + "  :     " + tempArrayRetURL[1].trim());
                            paramMap.put(tempArrayRetURL[0].trim(), tempArrayRetURL[1].trim());
                        } else {
                            paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
                        }

                    }
                }

                fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));

               /* fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
                fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());*/
                if (StringUtils.isBlank(fields.get(FieldType.INTERNAL_CUST_IP.getName()))) {
                    String ipAddress = fieldsDao.getIpFromSTB(fields.get(FieldType.OID.getName()));
                    if (StringUtils.isBlank(ipAddress)) {
                        ipAddress = fieldsDao.getIPFromInitiateRequest(fields.get(FieldType.OID.getName()));
                    }

                    fields.put(FieldType.INTERNAL_CUST_IP.getName(), ipAddress);
                    logger.info(" cust ipaddress in CANARA Bank-NB Acquirer = {}", ipAddress);
                }

                fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
                // sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
                //sessionMap.put(FieldType.INTERNAL_CUST_IP.getName(),ipAddress);

                if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
                    logger.info("IS_MERCHANT_HOSTED flag found for ORDER ID "
                            + paramMap.get(FieldType.ORDER_ID.getName()) + " in CANARA BANK");
                    fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
                            paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
                  /*  sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
                            paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));*/
                } else {
                    logger.info("IS_MERCHANT_HOSTED not found for ORDER ID "
                            + paramMap.get(FieldType.ORDER_ID.getName()) + " in CANARA BANK");
                }

            }

            fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.CANARANBBANK.getCode());
            fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
            fields.put(FieldType.MERCHANT_ID.getName(), MerchantCode);
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), Message);
           // fields.put(FieldType.TOTAL_AMOUNT.getName(), TxnAmount);
            fields.put(FieldType.RRN.getName(), fldBankRefNo);
            fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());

            fields.put(FieldType.CANARABANK_NB_RESPONSE_FIELD.getName(), decryptedResponse);
            fields.logAllFields("CANARA Net Banking Response Recieved :");


            fields.logAllFields("CANARA Net Banking response = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "OrderId = " + fields.get(FieldType.ORDER_ID.getName()));
            fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.CANARANBBANK.getCode());
            // fields.put(FieldType.TXNTYPE.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
            fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
            //fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
            // tranact.java ka REST API
            Map<String, String> response = transactionControllerServiceProvider.transact(fields,
                    Constants.CANARA_NB_PROCESSOR.getValue());

            responseMap = new Fields(response);

            String crisFlag = fields.get(FieldType.INTERNAL_IRCTC_YN.getName());
            if (StringUtils.isNotBlank(crisFlag)) {
                responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
            }

            if (StringUtils.isNotBlank(fields.get(FieldType.RETURN_URL.getName()))) {
                fields.put(FieldType.RETURN_URL.getName(), fields.get(FieldType.RETURN_URL.getName()));
            }
            responseMap.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
                    fields.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
            if (fields != null) {
                fields.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(), Constants.Y_FLAG.getValue());

            }
            responseMap.remove(FieldType.HASH.getName());
            responseMap.remove(FieldType.TXN_KEY.getName());
            responseMap.remove(FieldType.ACQUIRER_TYPE.getName());
            responseMap.remove(FieldType.IS_INTERNAL_REQUEST.getName());
            logger.info("Canara NB bank fields.get(FieldType.ADF1.getName():{}", fields.get(FieldType.ADF1.getName()));logger.info("Canara NB bank fields.get(FieldType.ADF2.getName():{}", fields.get(FieldType.ADF2.getName()));

            responseCreator.create(responseMap);
            responseCreator.ResponsePost(responseMap);

        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }
        return Action.NONE;
    }

    public static String getPgRefNumFronResponse(String response) {

        String responseArray[] = response.split("&");

        for (String data : responseArray) {

            if (data.contains("PRN")) {
                String dataArray[] = data.split("=");
                return dataArray[1];
            }
        }
        return null;
    }

    @Override
    public void setServletRequest(HttpServletRequest hReq) {
        this.httpRequest = hReq;
    }

}



