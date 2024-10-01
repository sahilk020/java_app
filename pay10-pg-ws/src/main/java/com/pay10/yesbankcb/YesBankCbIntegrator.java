package com.pay10.yesbankcb;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author VJ
 */
@Service
public class YesBankCbIntegrator {
    private static Logger logger = LoggerFactory.getLogger(YesBankCbIntegrator.class.getName());

    @Autowired
    @Qualifier("yesBankCbTransactionConverter")
    private TransactionConverter converter;

    @Autowired
    @Qualifier("yesBankCbFactory")
    private TransactionFactory transactionFactory;

    @Autowired
    @Qualifier("yesBankCbTransformer")
    private YesBankCbTransformer upiTransformer;

    @Autowired
    @Qualifier("yesBankCbTransactionCommunicator")
    private TransactionCommunicator communicator;

    public void process(Fields fields) throws SystemException {

        String vpaFlag = PropertiesManager.propertiesMap.get(Constants.VPA_VAIDATION_FLAG);

        if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
            fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
        }
        String transactionType = fields.get(FieldType.TXNTYPE.getName());
        transactionFactory.getInstance(fields);
        if (!StringUtils.equalsIgnoreCase(transactionType, TransactionType.ENQUIRY.getName())) {
            logIntent(fields);
        }

        if (vpaFlag.equalsIgnoreCase(Constants.Y_FLAG) && transactionType.equals(TransactionType.SALE.getName())) {
            String vpaStatus = vpaValidation(fields);
            if (StringUtils.isNotBlank(vpaStatus)) {
                if (vpaStatus.contains(Constants.VPA_FAILURE_RES)) {
                    send(fields);
                } else if (vpaStatus.equalsIgnoreCase(Constants.VPA_SUCCESSFULLY_STATUS_CODE)) {
                    send(fields);
                } else if (vpaStatus.equalsIgnoreCase(Constants.VPA_INVALID_STATUS_CODE)) {
                    upiTransformer.updateInvalidVpaResponse(fields, vpaStatus);
                } else {
                    upiTransformer.updateInvalidVpaResponse(fields, vpaStatus);
                }
            }
        } else {
            send(fields);
        }
    }

    public String vpaValidation(Fields fields) throws SystemException {
        String vpaStatus = "";
        JSONObject vpaRequest = converter.vpaValidatorRequest(fields);
        String encryptedVpaResponse = communicator.getVpaResponse(vpaRequest, fields);
        logger.info("vpa validation API response" + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
                + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedVpaResponse);
        if (StringUtils.isNotBlank(encryptedVpaResponse)) {
            logger.info("Collect API  VPA Response, if response is decrypted " + fields.get(FieldType.TXNTYPE.getName())
                    + " " + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedVpaResponse);
            if (encryptedVpaResponse.contains(Constants.VPA_FAILURE_RES)) {
                vpaStatus = Constants.VPA_FAILURE_RES;
            } else {
                vpaStatus = converter.toVpaTransaction(encryptedVpaResponse, fields);
                logger.info("vpa validation API response code" + fields.get(FieldType.TXNTYPE.getName()) + " "
                        + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + vpaStatus);

            }

        } else {
            vpaStatus = Constants.VPA_SUCCESSFULLY_STATUS_CODE;

        }

        return vpaStatus;
    }

    private void logIntent(Fields fields) throws SystemException {
        JSONObject logIntentReq = converter.logIntentRequest(fields);
        String encryptedRes = communicator.getLogIntentResponse(logIntentReq, fields);
        String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
        logger.info("logIntent:: pgRefNo={}, txnType={}, response={}", pgRefNo,
                fields.get(FieldType.TXNTYPE.getName()), encryptedRes);
        Transaction response = converter.toTransactionLogIntent(encryptedRes, fields);
        if (StringUtils.equalsAnyIgnoreCase(response.getStatus(), Constants.YES_UPI_RESPONSE)) {
            logger.info("logIntent:: Success. pgRefNo={}, txnType={}, status={}", pgRefNo,
                    fields.get(FieldType.TXNTYPE.getName()),
                    response.getStatus());
            return;
        }
        logger.error("logIntent:: failed. pgRefNo={}, txnType={}, status={}",
                pgRefNo, fields.get(FieldType.TXNTYPE.getName()), response.getStatus());
        throw new SystemException(ErrorType.NOT_APPROVED_FROM_ACQUIRER, "Log intent API  failed " +
                "pgRefNo=" + pgRefNo);
    }

    public void send(Fields fields) throws SystemException {
        Transaction transactionResponse = new Transaction();
        JSONObject request = converter.perpareRequest(fields);
        String encryptedResponse = communicator.getResponse(request, fields);

        switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
            case SALE:
                if (StringUtils.isNotBlank(encryptedResponse)) {
                    if (encryptedResponse.contains(Constants.COLLECT_FAILURE_RES)) {
                        logger.info("Collect API  collect Response, if response is decrypted=  "
                                + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
                                + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedResponse);
                        transactionResponse = converter.toTransactionCollectFailureRes(encryptedResponse, fields);
                        upiTransformer.updateResponse(fields, transactionResponse);
                        break;
                    } else {
                        transactionResponse = converter.toTransaction(encryptedResponse, fields);
                        upiTransformer.updateResponse(fields, transactionResponse);
                        break;
                    }

                } else {
                    logger.info(
                            "Collect API  Collect Response, if response is blank " + fields.get(FieldType.TXNTYPE.getName())
                                    + " " + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedResponse);
                    upiTransformer.updateResponse(fields, transactionResponse);
                    break;
                }

            case REFUND:
                if (StringUtils.isNotBlank(encryptedResponse)) {
                    if (encryptedResponse.contains(Constants.REFUND_FAILURE_RES)
                            || encryptedResponse.contains(Constants.REFUND_FAILURE)
                            || encryptedResponse.contains(Constants.REFUND_RESCODE_MC04)) {
                        logger.info("Collect API  REFUND Response, if response is decrypted :  "
                                + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
                                + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedResponse);
                        transactionResponse = converter.toTransactionFailureRes(encryptedResponse, fields);
                        upiTransformer.updateResponse(fields, transactionResponse);
                        break;
                    } else {
                        if (encryptedResponse.contains(Constants.REFUND_FAILURE_RES_PENDING)) {
                            transactionResponse = converter.toTransactionRefundFai(encryptedResponse, fields);
                            logger.info("Collect API request REFUND Response = " + fields.get(FieldType.TXNTYPE.getName())
                                    + " " + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " Response  = "
                                    + transactionResponse.getResponse() + " Response Message =  "
                                    + transactionResponse.getResponseMessage());
                            upiTransformer.updateResponse(fields, transactionResponse);
                            break;
                        } else {
                            transactionResponse = converter.toTransaction(encryptedResponse, fields);
                            logger.info("Collect API request REFUND Response = " + fields.get(FieldType.TXNTYPE.getName())
                                    + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " Tranaction Response = "
                                    + transactionResponse.getResponse() + "Tranaction Response Message = "
                                    + transactionResponse.getResponseMessage());
                            upiTransformer.updateResponse(fields, transactionResponse);
                            break;
                        }
                    }
                } else {
                    logger.info("Collect API  REFUND Response, if response is blank = "
                            + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
                            + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedResponse);
                    upiTransformer.updateResponse(fields, transactionResponse);
                    break;
                }

            case ENQUIRY:
                if (StringUtils.isNotBlank(encryptedResponse)) {
                    logger.info("Collect API  ENQUIRY Response, if response is decrypted :  "
                            + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
                            + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedResponse);
                    transactionResponse = converter.toTransactionStatusEnquiry(encryptedResponse, fields);
                    upiTransformer.updateResponse(fields, transactionResponse);
                    break;
                } else {
                    logger.info("Collect API  Status enquiry Response, if response is blank = "
                            + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
                            + fields.get(FieldType.TXN_ID.getName()) + " " + encryptedResponse);
                    upiTransformer.updateResponse(fields, transactionResponse);
                    break;
                }
            default:
                break;
        }
    }
}
