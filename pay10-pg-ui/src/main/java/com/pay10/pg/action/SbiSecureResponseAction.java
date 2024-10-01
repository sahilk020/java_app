package com.pay10.pg.action;

import bsh.This;
import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.*;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.ResponseCreator;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

public class SbiSecureResponseAction extends AbstractSecureAction implements ServletRequestAware {

    private static final Logger logger = LoggerFactory.getLogger(This.class.getName());

    private Fields responseMap = null;
    private HttpServletRequest httpRequest;

    @Autowired
    private FieldsDao fieldsDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RetryTransactionProcessor retryTransactionProcessor;

    @Autowired
    TransactionControllerServiceProvider transactionControllerServiceProvider;

    @Autowired
    private ResponseCreator responseCreator;

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.httpRequest = request;
    }

    public SbiSecureResponseAction() {
        logger.info("SbiSecureResponseAction:: Initialized the component");
    }

    public String execute() {
        Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
        Map<String, String> requestMap = new HashMap<String, String>();
        logger.info("execute:: requestAsPerHTTP={}, sessionMap={}", fieldMapObj, sessionMap);
        fieldMapObj.entrySet().forEach(entry -> {
            try {
                requestMap.put(entry.getKey(), ((String[]) entry.getValue())[0]);
            } catch (ClassCastException classCastException) {
                logger.error("execute:: failed. ", classCastException);
            }
        });
        logger.info("execute:: response={}", requestMap);
        String res = requestMap.get("cres");
        logger.info("execute:: response in key res={}", res);

        Fields fields = new Fields();
        Object fieldsObj = sessionMap.get("FIELDS");
        if (null != fieldsObj) {
            fields.put((Fields) fieldsObj);
        }
        String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
        logger.info("execute:: session. pgRefNo={}", pgRefNo);
        try {
            fields = fieldsDao.getPreviousStatusForPgRefNum(pgRefNo);
            fields.put(FieldType.CARD_NUMBER.getName(),
                    sessionMap.get(FieldType.CARD_NUMBER.getName()).toString());
            fields.put(FieldType.CVV.getName(), sessionMap.get(FieldType.CVV.getName()).toString());
            fields.put(FieldType.CARD_HOLDER_NAME.getName(),
                    sessionMap.get(FieldType.CARD_HOLDER_NAME.getName()).toString());
            
            	 fields.put(FieldType.CARD_EXP_DT.getName(),
                         sessionMap.get(FieldType.CARD_EXP_DT.getName()).toString());	
           
            
           
            fields.put(FieldType.SBI_CARD_FINAL_RES.getName(), res);
            User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
            setConfigurationFromDb(user, fields);
            Map<String, String> response = transactionControllerServiceProvider.transact(fields,
                    Constants.SBI_PA_RQ_FINAL_RESPONSE_HANDLER.getValue());
            responseMap = new Fields(response);
            String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
            if (StringUtils.isNotBlank(crisFlag)) {
                responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
            }
            String isMerchantHosted = (String) sessionMap.get(FieldType.IS_MERCHANT_HOSTED.getName());
            if (StringUtils.isNotBlank(isMerchantHosted)) {
                responseMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), isMerchantHosted);
            }
            // Retry Transaction Block Start
            if (!responseMap.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.SUCCESS.getCode())) {
                if (retryTransactionProcessor.retryTransaction(responseMap, sessionMap, user, fields)) {
                    sessionMap.put(Constants.RETRY_MESSAGE.getValue(), CrmFieldConstants.RETRY_TRANSACTION.getValue());
                    return "surchargePaymentPage";
                }
            }

            fields.put(FieldType.RETURN_URL.getName(), (String) sessionMap.get(FieldType.RETURN_URL.getName()));
            responseMap.put(FieldType.RETURN_URL.getName(),
                    (String) sessionMap.get(FieldType.RETURN_URL.getName()));
            logger.info("execute:: returnUrl={}", fields.get(FieldType.RETURN_URL.getName()));
            String cardIssuerBank = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
            String cardIssuerCountry = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName());
            if (StringUtils.isNotBlank(cardIssuerBank)) {
                responseMap.put(FieldType.CARD_ISSUER_BANK.getName(), cardIssuerBank);
            }
            if (StringUtils.isNotBlank(cardIssuerCountry)) {
                responseMap.put(FieldType.CARD_ISSUER_COUNTRY.getName(), cardIssuerCountry);
            }

            responseMap.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
                    (String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
            if (sessionMap != null) {
                sessionMap.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(), Constants.Y_FLAG.getValue());
                sessionMap.invalidate();
            }
            responseMap.remove(FieldType.HASH.getName());
            responseMap.remove(FieldType.TXN_KEY.getName());
            responseMap.remove(FieldType.PASSWORD.getName());
            responseMap.remove(FieldType.ACQUIRER_TYPE.getName());
            responseMap.remove(FieldType.IS_INTERNAL_REQUEST.getName());
            responseCreator.create(responseMap);
            responseCreator.ResponsePost(responseMap);
            return Action.NONE;
        } catch (Exception ex) {
            logger.error("execute:: failed. pgRefNo={}", pgRefNo, ex);
            return ERROR;
        }
    }

    private void setConfigurationFromDb(User user, Fields fields) throws SystemException {
        Set<Account> accounts = user.getAccounts() == null ? new HashSet<>() : user.getAccounts();
        List<Account> accountList = accounts.stream()
                .filter(accountsFromSet -> StringUtils.equalsIgnoreCase(accountsFromSet.getAcquirerName(),
                        AcquirerType.getInstancefromCode(AcquirerType.SBICARD.getCode()).getName()))
                .collect(Collectors.toList());
        Account account = accountList != null && accountList.size() > 0 ? accountList.get(0) : null;
        AccountCurrency accountCurrency =
                account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
        fields.put(FieldType.MERCHANT_ID.getName(), accountCurrency.getMerchantId());
        fields.put(FieldType.TXN_KEY.getName(), accountCurrency.getTxnKey());
        fields.put(FieldType.ADF1.getName(), accountCurrency.getAdf1());
        fields.put(FieldType.ADF2.getName(), accountCurrency.getAdf2());
        fields.put(FieldType.ADF3.getName(), accountCurrency.getAdf3());
        fields.put(FieldType.ADF4.getName(), accountCurrency.getAdf4());
        fields.put(FieldType.ADF8.getName(), accountCurrency.getAdf8());
        fields.put(FieldType.ADF9.getName(), accountCurrency.getAdf9());
        fields.put(FieldType.ADF10.getName(), accountCurrency.getAdf10());
        fields.put(FieldType.ADF11.getName(), accountCurrency.getAdf11());
    }
}
