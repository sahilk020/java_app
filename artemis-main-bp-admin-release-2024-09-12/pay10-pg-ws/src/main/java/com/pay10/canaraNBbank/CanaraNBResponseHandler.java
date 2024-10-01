package com.pay10.canaraNBbank;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.*;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;
import com.pay10.pg.core.util.CanaraUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.pay10.canaraNBbank.CanaraNBProcessor.getTagValue;


@Service
public class CanaraNBResponseHandler {
    private static Logger logger = LoggerFactory.getLogger(CanaraNBResponseHandler.class.getName());
    @Autowired
    CanaraNBProcessor canaraNBProcessor;
    @Autowired
    private Validator generalValidator;
    @Autowired
    private UserDao userDao;
    @Autowired
    @Qualifier("updateProcessor")
    private Processor updateProcessor;

    public Map<String, String> process(Fields fields) throws SystemException {


        String decryptedResponse = "";

        String newTxnId = TransactionManager.getNewTransactionId();
        fields.put(FieldType.TXN_ID.getName(), newTxnId);
        logger.info("Canara NB Response Handler:{}"+fields.getFieldsAsString());

        String Response = fields.get(FieldType.CANARABANK_NB_RESPONSE_FIELD.getName());

        String[] paramatersRespo = Response.split("&");
        Map<String, String> paramMapRespo = new HashMap<String, String>();
        logger.info("Fields In CANARABankNBResponseHandler2:{}"+fields.getFieldsAsString());
        for (String param : paramatersRespo) {
            String[] parameterPair = param.split("=");
            if (parameterPair.length > 1) {
                paramMapRespo.put(parameterPair[0], parameterPair[1]);


            }
        }

        String pgRefNum = paramMapRespo.get("MerchRefNo");
        String Message = paramMapRespo.get("Message");
        String Date = paramMapRespo.get("Date");
        String TxnAmount = paramMapRespo.get("TxnAmount");
        String ackStaticFlg = paramMapRespo.get("AckStaticFlag");
        String ResponseStaticFlg = paramMapRespo.get("ResponseStaticFlag");
        String MerchantCode = paramMapRespo.get("MerchantCode");
        String fldTxnId = paramMapRespo.get("fldTxnId");
        String fldBankRefNo = paramMapRespo.get("fldBankRefNo");

        fields.put(FieldType.MERCHANT_ID.getName(), paramMapRespo.get("MerchantCode"));
        fields.put(FieldType.RRN.getName(), paramMapRespo.get("fldBankRefNo"));

        logger.info("fieldsin response action -------->>>" + fields.getFieldsAsString());

        Map<String, String> keyMap = getTxnKey(fields);
        fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
        fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
        logger.info("fieldsin response action -------->>>" + fields.getFieldsAsString());
        String dualVerificationResponse = canaraNBProcessor.statusEnquiry(fields,true);

       // String dualVerificationResponse = canaraNBProcessor.statusEnquiry(fields);
        logger.info("Canara Bank NB dualVerificationResponse:{}"+dualVerificationResponse);

        fields.put(FieldType.ACQ_ID.getName(), fields.get(FieldType.RRN.getName()));
        String encryptedDualResp = getTagValue(dualVerificationResponse,"Output");

        if (StringUtils.isNotEmpty(fields.get(FieldType.ADF1.getName())) && StringUtils.isNotEmpty(fields.get(FieldType.ADF2.getName()))) {
            decryptedResponse = CanaraUtil.decryptString(encryptedDualResp, fields.get(FieldType.ADF1.getName()), fields.get(FieldType.ADF2.getName()));
            logger.info("Canara NB dual verification Response :{}", decryptedResponse);
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
      //  ClientAccount=~BankRefNo=151054847185927~TxnAmt=2.00~OrgDattimeTxn=31/03/2021 11:49:16~MerchRefNbr=UCNB0000000060~ReturnCode=0~VerifyStatus=0~CheckSum=87A92346913866745DA506CA0B228186BBD598D5BA61D292124CEB1A19BC7249
/*
returncode=0
verifiyStatus=0  tabhi Captured
returnCode=0
verifu
00=Captured
01= fail
 */
        String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));

        logger.info("Canara NB Callback response in MAP: " + paramMapRespo);
        logger.info("Canara NB Dual verification response in MAP:" + doubleVerifResponseMap);
        ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(doubleVerifResponseMap.get("ReturnCode")+doubleVerifResponseMap.get("VerifyStatus"), "CANARANBBANK");
        logger.info("Error code mapping CANARA BANK NB:{}", errorMappingDTO);
        if (null != errorMappingDTO) {
            if (doubleVerifResponseMap.get("ReturnCode").equalsIgnoreCase("0") && doubleVerifResponseMap.get("MerchRefNbr").equalsIgnoreCase(pgRefNum)
                    && doubleVerifResponseMap.get("TxnAmt").equalsIgnoreCase(amount)
                    && doubleVerifResponseMap.get("VerifyStatus").equalsIgnoreCase("0")) {

                 fields.put(FieldType.STATUS.getName(), "Captured");
            } else
            fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
            //fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), Message);
            fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
            fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
            fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
            fields.put(FieldType.ACQ_ID.getName(), fields.get(FieldType.RRN.getName()));
            fields.put(FieldType.PG_TXN_STATUS.getName(), ErrorType.ACQUIRER_ERROR.getCode());
        } else {
            logger.info("ErrorCodeMapping not fount for Canara NB bank Acquirer");
            //fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.UNKNOWN.getResponseMessage());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), Message);
            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.UNKNOWN.getCode());
            fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
            fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.UNKNOWN.getResponseMessage());
            fields.put(FieldType.ACQ_ID.getName(), fields.get(FieldType.RRN.getName()));
            fields.put(FieldType.PG_TXN_STATUS.getName(), ErrorType.ACQUIRER_ERROR.getCode());
        }
           /* fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
            logger.info("Canara Bank NB errorType2 : " + errorType);*/


        logger.info("Canara Bank NB errorType3 : ");
        fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
        fields.remove(FieldType.CANARABANK_NB_RESPONSE_FIELD.getName());
      //  String amount2 = Amount.formatAmount(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356");
        //logger.info("amount2:+++++++++++++++++++++:{}"+amount2);
        //fields.put(FieldType.TOTAL_AMOUNT.getName(), amount2);
        logger.info("Fields before call updateProcessor in CARANA NB Response Handler:{}"+fields.getFieldsAsString());
        logger.info("Fields before call updateProcessor in CARANA NB updateProcessor :{}"+updateProcessor.toString());
        logger.info("Fields before call fields in CARANA NB fields:{}"+fields.getFieldsAsString());
        ProcessManager.flow(updateProcessor, fields, true);
        return fields.getFields();

    }
    public HashMap<String, String> parseStatusEnquiry(String xmlResponse) {
        HashMap<String, String> map = new HashMap<>();
        String VerifyOutput = getTagValue(xmlResponse, "VerifyOutput");
        String Output = getTagValue(xmlResponse, "Output");
        map.put("Output", Output);
       map.put("VerifyOutput", VerifyOutput);
        return map;
    }

    public HashMap<String, String> dualVerification(String xmlResponse) {
        HashMap<String, String> map = new HashMap<>();
     String VerifyOutput = getTagValue(xmlResponse, "VerifyOutput");
        String ClientAccount = getTagValue(xmlResponse, "ClientAccount");
        String MerchRefNbr = getTagValue(xmlResponse, "MerchRefNbr");
        String OrgDatTimeTxn = getTagValue(xmlResponse, "OrgDattimeTxn");
        String BankRefNo = getTagValue(xmlResponse, "BankRefNo");
        String TxnAmt = getTagValue(xmlResponse, "TxnAmt");
        String ReturnCode = getTagValue(xmlResponse, "ReturnCode");
        String MerchantCode = getTagValue(xmlResponse, "MerchantCode");
        //String Status = getTagValue(xmlResponse, "Status");
        //  String VerifyStatus = getTagValue(xmlResponse, "VerifyStatus");
        if ("0".equalsIgnoreCase(ReturnCode)) {
            map.put("VerifyStatus", getTagValue(xmlResponse, "VerifyStatus"));
        }
        map.put("MerchantCode", MerchantCode);
        map.put("ClientAccount", ClientAccount);
        map.put("MerchRefNbr", MerchRefNbr);
        map.put("OrgDattimeTxn", OrgDatTimeTxn);
        map.put("BankRefNo", BankRefNo);
        map.put("TxnAmt", TxnAmt);
        map.put("ReturnCode", ReturnCode);

        return map;
        //  }
        //   return map;
    }

    public static String getTagValue(String xml, String tagName) {
        return xml.split("<" + tagName + ">")[1].split("</" + tagName + ">")[0];
    }

    public Map<String, String> getTxnKey(Fields fields) throws SystemException {

        Map<String, String> keyMap = new HashMap<String, String>();

        User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
        Account account = null;
        Set<Account> accounts = user.getAccounts();

        if (accounts == null || accounts.size() == 0) {
            logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
                    + fields.get(FieldType.ORDER_ID.getName()));
        } else {
            for (Account accountThis : accounts) {
                if (accountThis.getAcquirerName()
                        .equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.CANARANBBANK.getCode()).getName())) {
                    account = accountThis;
                    break;
                }
            }
        }

        AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

        keyMap.put("ADF1", accountCurrency.getAdf1());
        keyMap.put("ADF2", accountCurrency.getAdf2());

        return keyMap;

    }
}


