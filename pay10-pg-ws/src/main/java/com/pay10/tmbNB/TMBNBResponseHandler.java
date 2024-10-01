package com.pay10.tmbNB;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.*;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TMBNBResponseHandler {
    private static Logger logger = LoggerFactory.getLogger(TMBNBResponseHandler.class.getName());
    @Autowired
    TMBNBProcessor tmbnbProcessor;
    @Autowired
    private Validator generalValidator;

    @Autowired
    @Qualifier("updateProcessor")
    private Processor updateProcessor;

    public Map<String, String> process(Fields fields) throws SystemException {

        //  HashMap<String, String> doubleVerifResponse = new HashMap<>();

        String newTxnId = TransactionManager.getNewTransactionId();
        fields.put(FieldType.TXN_ID.getName(), newTxnId);
        String Response = fields.get(FieldType.TMB_NB_RESPONSE_FIELD.getName());
        logger.info("TMB NB Response : " + Response.toString());
        String[] paramatersRespo = Response.split("&");
        Map<String, String> paramMapRespo = new HashMap<String, String>();
        logger.info("TMB NB fields Handler : " + fields.getFieldsAsString());
        for (String param : paramatersRespo) {
            String[] parameterPair = param.split("=");
            if (parameterPair.length > 1) {
                paramMapRespo.put(parameterPair[0], parameterPair[1]);
            }
        }
        String status = paramMapRespo.get("STATUS");
        String bid = paramMapRespo.get("BID");
        String md = paramMapRespo.get("MD");
        String TxnAmount = paramMapRespo.get("AMT");
        String MID = paramMapRespo.get("PID");
        String pgRefNum = paramMapRespo.get("PRN");
        String itc = paramMapRespo.get("ITC");
        fields.put(FieldType.RRN.getName(), bid);
        fields.put(FieldType.ADF5.getName(), itc);
        logger.info("dual verification Bid: " + bid);

        String dualVerificationResponse = tmbnbProcessor.statusEnquiry(fields, true);
        logger.info("TMB NB Dual verification response with pgRefNum: {},{}" ,pgRefNum, dualVerificationResponse);
        String str = dualVerificationResponse.replaceAll("<.*?>", " ");

        Map<String, String> newmap = splitString(str);
        logger.info("dual verification newmap: " + newmap);
        String Bid = newmap.get("BID");
        logger.info("dual verification Bid: " + Bid);

        if (status.equalsIgnoreCase(newmap.get("STATUS"))) {//&& bid.equalsIgnoreCase(newmap.get("BID"))) {
            ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(newmap.get("STATUS"), "TMBNB");
            logger.info("Error code mapping TMB NB : " + errorMappingDTO);
            if (null != errorMappingDTO) {
                fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
                //fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
                fields.put(FieldType.RESPONSE_MESSAGE.getName(), status);
                fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
                fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
                fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
                fields.put(FieldType.ACQ_ID.getName(), Bid);
                fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
            }
        } else {
            fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
            //fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), status);
        }
        fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
        fields.remove(FieldType.TMB_NB_RESPONSE_FIELD.getName());

        logger.info("UpdateProcessor in TMB NB Response Handler " + fields.getFieldsAsString());

        ProcessManager.flow(updateProcessor, fields, true);
        return fields.getFields();

    }

    public static Map<String, String> splitString(String s) {
        String[] split = s.split("[= ? &]+");
        int length = split.length;
        Map<String, String> maps = new HashMap<>();

        for (int i = 0; i < length; i += 2) {
            maps.put(split[i], split[i + 1]);
        }

        return maps;

    }
}
