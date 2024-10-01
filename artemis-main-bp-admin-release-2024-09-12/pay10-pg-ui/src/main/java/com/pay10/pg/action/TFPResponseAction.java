package com.pay10.pg.action;

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
import java.util.Map.Entry;

public class TFPResponseAction extends AbstractSecureAction implements ServletRequestAware {
    private static final Logger logger = LoggerFactory.getLogger(TFPResponseAction.class.getName());
    private static final long serialVersionUID = 2382298172065463916L;

    private HttpServletRequest httpRequest;

    @Override
    public void setServletRequest(HttpServletRequest hReq) {
        this.httpRequest = hReq;
    }
    
    @Autowired
    private ResponseCreator responseCreator;

    @Autowired
    TransactionControllerServiceProvider transactionControllerServiceProvider;

    @Autowired
    private FieldsDao fieldsDao;

    public TFPResponseAction() {
    }

    @Override
    public String execute() {
        try {
            Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
            Map<String, String> requestMap = new HashMap<String, String>();

            for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
                try {
                    requestMap.put(entry.getKey(), entry.getValue()[0]);
                } catch (ClassCastException classCastException) {
                    logger.error("Exception", classCastException);
                }
            }

            logger.info("Response From TFP Json  :: {}", requestMap);
            logger.info("TFP TFPResponseAction jsonObject:{}", requestMap);
            logger.info("TFP ResponseAction jsonObject======== :{}", requestMap);

            Fields fields = new Fields();
            String pgRefNum = requestMap.get("ORDER_ID");
            logger.info("pgRefNum======== :{}", pgRefNum);
            String postingMethodFlag = "";
            if (StringUtils.isNotBlank(pgRefNum)) {
                logger.info("Get Fields Data From DB For TFP, PG_REF_NUM2 : {}", pgRefNum);
                fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);
                logger.info("fields ==================================:{}", fields);

                String internalRequestFields = fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
                logger.info("TFP ResponseAction internalRequestFields ==================================:{}", internalRequestFields);

                String[] paramaters = internalRequestFields.split("~");
                Map<String, String> paramMap = new HashMap<String, String>();

                for (String param : paramaters) {
                    String[] parameterPair = param.split("=");
                    if (parameterPair.length > 1) {
                        if (parameterPair[0].trim().equalsIgnoreCase("RETURN_URL")) {
                            String[] tempArrayRetURL = param.split("=", 2);
                            logger.info(" Split_RETURN_URL : {}, {}", tempArrayRetURL[0].trim(), tempArrayRetURL[1].trim());
                            paramMap.put(tempArrayRetURL[0].trim(), tempArrayRetURL[1].trim());
                        } else {
                            paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
                        }
                    }
                }

                fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
                if (StringUtils.isBlank(fields.get(FieldType.INTERNAL_CUST_IP.getName()))) {
                    String ipAddress = fieldsDao.getIpFromSTB(fields.get(FieldType.OID.getName()));
                    if (StringUtils.isBlank(ipAddress)) {
                        ipAddress = fieldsDao.getIPFromInitiateRequest(fields.get(FieldType.OID.getName()));
                    }

                    fields.put(FieldType.INTERNAL_CUST_IP.getName(), ipAddress);
                    logger.info(" cust ipaddress in TFP = {}", ipAddress);
                }

                fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));

                if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
                    logger.info("IS_MERCHANT_HOSTED flag found for ORDER ID {} in TFP BANK", paramMap.get(FieldType.ORDER_ID.getName()));
                    fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
                            paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));

                } else {
                    logger.info("IS_MERCHANT_HOSTED not found for ORDER ID {} in TFP BANK", paramMap.get(FieldType.ORDER_ID.getName()));
                }
                postingMethodFlag = paramMap.get(FieldType.POSTING_METHOD_FLAG.getName());
            }
            fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.TFP.getCode());
            fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
            fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
            fields.put(FieldType.TFP_FINAL_RESPONSE.getName(), requestMap.toString());
            fields.logAllFields("TFP Response Received :");
            fields.logAllFields("TFP response = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "OrderId = " + fields.get(FieldType.ORDER_ID.getName()));

            Map<String, String> response = transactionControllerServiceProvider.transact(fields,
                    Constants.TFP_PROCESSOR.getValue());

            logger.info("response :{}", response);
            Fields responseMap = new Fields(response);

            String crisFlag = fields.get(FieldType.INTERNAL_IRCTC_YN.getName());
            if (StringUtils.isNotBlank(crisFlag)) {
                responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
            }

            if (StringUtils.isNotBlank(fields.get(FieldType.RETURN_URL.getName()))) {
                fields.put(FieldType.RETURN_URL.getName(), fields.get(FieldType.RETURN_URL.getName()));
            }
            responseMap.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
                    fields.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
            fields.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(), Constants.Y_FLAG.getValue());

            responseMap.remove(FieldType.HASH.getName());
            responseMap.remove(FieldType.TXN_KEY.getName());
            responseMap.remove(FieldType.ACQUIRER_TYPE.getName());
            responseMap.remove(FieldType.IS_INTERNAL_REQUEST.getName());
            responseCreator.create(responseMap);
            // set RETURN_URL http method from BestPay to merchant
            if(StringUtils.isNotEmpty(postingMethodFlag)) {
                responseMap.put(FieldType.POSTING_METHOD_FLAG.getName(), postingMethodFlag);
            }
            responseCreator.ResponsePost(responseMap);

        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }
        return Action.NONE;
    }
}