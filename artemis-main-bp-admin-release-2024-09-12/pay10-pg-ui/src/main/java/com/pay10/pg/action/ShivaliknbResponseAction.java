package com.pay10.pg.action;

import com.infosys.feba.tools.shoppingmallencryption.ShoppingMallSymmetricCipherHelper;
import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.*;
import com.pay10.pg.core.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShivaliknbResponseAction extends AbstractSecureAction implements ServletRequestAware {
    private static Logger logger = LoggerFactory.getLogger(ShivaliknbResponseAction.class.getName());
    private static final long serialVersionUID = 2382298172065463916L;

    private HttpServletRequest httpRequest;

    @Autowired
    private ResponseCreator responseCreator;
    @Autowired
    TransactionControllerServiceProvider transactionControllerServiceProvider;

    @Autowired
    private UserDao userDao;

    @Autowired
    private FieldsDao fieldsDao;
    private Fields responseMap = null;

    public ShivaliknbResponseAction() {
    }

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
            String shivalikResponse = requestMap.toString();
           // tmbResponse = tmbResponse.substring(0,tmbResponse.length() - 2); // due to map.toString() extra = comes
          //  String secretKey = requestMap.get(FieldType.ADF4.getName());
           // secretKey: "1c30f76d3ec045e18af930c09cf104ae"

            String secretKey = PropertiesManager.propertiesMap.get("secretKey");
            logger.info("Shivalik Bank NetBanking Encrypted Response Received From shivalik:{}",shivalikResponse);
           /* JSONObject Datajson = new JSONObject(shivalikResponse);
            logger.info("Datajson=======",Datajson);
            String data = null;
            if (Datajson != null && Datajson.getString("api_key") != null) {

                data = Datajson.getString("api_key");
            }
*/
            String decryptedResponse = "";
            Fields fields = new Fields();

            String encrypted_data = requestMap.get("encrypted_data");
            String api_key = requestMap.get("api_key");
            decryptedResponse = ShivalikNBUtil.decrypt(encrypted_data, secretKey);
            logger.info("Shivalik NB bank decryptedResponse:{}", decryptedResponse);
            JSONObject Datajson = new JSONObject(decryptedResponse);
            logger.info("Datajson======== :" + Datajson);
            String pgRefNum = Datajson.getString("order_id");
            logger.info("pgRefNum======== :" + pgRefNum);

           /* String[] paramatersRespo = decryptedResponse.split("&");
            Map<String, String> paramMapRespo = new HashMap<String, String>();

            //db
            for (String param : paramatersRespo) {
                String[] parameterPair = param.split("=");
                if (parameterPair.length > 1) {
                    paramMapRespo.put(parameterPair[0], parameterPair[1]);
                    logger.info("paramMapRespo.put(parameterPair[0], parameterPair[1] :" + paramMapRespo.put(parameterPair[0], parameterPair[1]));

                }
            }
            */

            //TODO PAY10-563 : get PGREFNO from paramMapRespo and set into pgRefNum parameter.

            if (StringUtils.isNotBlank(pgRefNum)) {
                logger.info("Get Fields Data From DB For shivalik, PG_REF_NUM2 : " + pgRefNum);
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
                    logger.info(" cust ipaddress in Shivalik Bank-NB Acquirer = {}", ipAddress);
                }

                fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
                // sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
                //sessionMap.put(FieldType.INTERNAL_CUST_IP.getName(),ipAddress);

                if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
                    logger.info("IS_MERCHANT_HOSTED flag found for ORDER ID "
                            + paramMap.get(FieldType.ORDER_ID.getName()) + " in Shivalik BANK");
                    fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
                            paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
                  /*  sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
                            paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));*/
                } else {
                    logger.info("IS_MERCHANT_HOSTED not found for ORDER ID "
                            + paramMap.get(FieldType.ORDER_ID.getName()) + " in Shivalik BANK");
                }

            } //End of IF for PgRef Null check

            //TODO  PAY10-563 : If Required below details then get values from response & fields Obj and set the same into below variable.
            fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.SHIVALIKNBBANK.getCode());
            fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
          //  fields.put(FieldType.MERCHANT_ID.getName(), MID); //TODO PAY10-563 :If required then set otherwise remove it.
            fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
            // fields.put(FieldType.ADF5.getName(),itc); //TODO PAY10-563 :If required then set otherwise remove it.
           // fields.put(FieldType.RRN.getName(), bid); //TODO PAY10-563 :If required then set otherwise remove it.
            fields.put(FieldType.SHIVALIK_NB_RESPONSE_FIELD.getName(), decryptedResponse);
            fields.logAllFields("Shivalik Net Banking Response Recieved :");


            fields.logAllFields("Shivalik Net Banking response = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "OrderId = " + fields.get(FieldType.ORDER_ID.getName()));

            Map<String, String> response = transactionControllerServiceProvider.transact(fields,
                    Constants.SHIVALIK_NB_PROCESSOR.getValue());

            logger.info("response "+response);
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




