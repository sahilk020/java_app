package com.pay10.pg.action;

import com.infosys.feba.tools.shoppingmallencryption.ShoppingMallSymmetricCipherHelper;
import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.util.*;
import com.pay10.pg.core.util.ResponseCreator;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class TMBNBResponseAction extends AbstractSecureAction implements ServletRequestAware {
    private static Logger logger = LoggerFactory.getLogger(TMBNBResponseAction.class.getName());
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


            for (Map.Entry<String, String[]> entry : fieldMapObj.entrySet()) {
                try {
                    requestMap.put(entry.getKey(), entry.getValue()[0]);

                } catch (ClassCastException classCastException) {
                    logger.error("Exception", classCastException);
                }
            }
            String tmbResponse = requestMap.toString();
            tmbResponse = tmbResponse.substring(0,tmbResponse.length() - 2); // due to map.toString() extra = comes

           String Key = PropertiesManager.propertiesMap.get("TMB_NB_ENC_KEY");
           logger.info("TMB Bank NetBanking Encrypted Response Received From TMB :{}",tmbResponse);

            String decryptedResponse = "";
            Fields fields = new Fields();
            fields.put(FieldType.TXN_KEY.getName(), Key);

          /* String decodedUrl= URLDecoder.decode(requestMap.toString(), "UTF-8");
           logger.info("TMB NB bank decodedUrl:{}", decodedUrl);*/
            String decodedUrl = tmbResponse;

            if (StringUtils.isNotEmpty(Key)){

                decryptedResponse = ShoppingMallSymmetricCipherHelper.decrypt(decodedUrl, Key, "AES");
                logger.info("TMB NB bank decryptedResponse:{}", decryptedResponse);
            }

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

            String status = paramMapRespo.get("STATUS");
            String bid = paramMapRespo.get("BID");//MerchantCode
            String md = paramMapRespo.get("MD");
            String TxnAmount = paramMapRespo.get("AMT");
            String MID = paramMapRespo.get("PID");
            String pgRefNum = paramMapRespo.get("PRN");
            String itc = paramMapRespo.get("ITC");

            if (StringUtils.isNotBlank(pgRefNum)) {
                logger.info("Get Fields Data From DB For TMB, PG_REF_NUM2 : " + pgRefNum);
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
                    logger.info(" cust ipaddress in TMB Bank-NB Acquirer = {}", ipAddress);
                }

                fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
                // sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
                //sessionMap.put(FieldType.INTERNAL_CUST_IP.getName(),ipAddress);

                if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
                    logger.info("IS_MERCHANT_HOSTED flag found for ORDER ID "
                            + paramMap.get(FieldType.ORDER_ID.getName()) + " in TMB BANK");
                    fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
                            paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
                  /*  sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
                            paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));*/
                } else {
                    logger.info("IS_MERCHANT_HOSTED not found for ORDER ID "
                            + paramMap.get(FieldType.ORDER_ID.getName()) + " in TMB BANK");
                }

            } //End of IF for PgRef Null check

            fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.TMBNB.getCode());
            fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
            fields.put(FieldType.MERCHANT_ID.getName(), MID);
            fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
            fields.put(FieldType.ADF5.getName(),itc);
            fields.put(FieldType.RRN.getName(), bid);
            fields.put(FieldType.TMB_NB_RESPONSE_FIELD.getName(), decryptedResponse);
            fields.logAllFields("TMB Net Banking Response Recieved :");


            fields.logAllFields("TMB Net Banking response = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "OrderId = " + fields.get(FieldType.ORDER_ID.getName()));

            Map<String, String> response = transactionControllerServiceProvider.transact(fields,
                    Constants.TMB_NB_PROCESSOR.getValue());

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




