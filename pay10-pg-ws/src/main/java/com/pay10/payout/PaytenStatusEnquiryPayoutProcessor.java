package com.pay10.payout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pay10.atom.AtomStatusEnquiryProcessor;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.WalletHistoryRepository;
import com.pay10.commons.user.AccountCurrencyPayout;
import com.pay10.commons.user.AccountCurrencyPayoutDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.payout.payten.PaytenPayoutEncryptionDecryption;
import com.pay10.payout.payten.PaytenResponse;

@Service
public class PaytenStatusEnquiryPayoutProcessor {

    private static Logger logger = LoggerFactory.getLogger(AtomStatusEnquiryProcessor.class.getName());
    @Autowired
    AccountCurrencyPayoutDao accountCurrencyPayoutDao;
    @Autowired
    private TransactionConverter transactionConverter;
    @Autowired
    private WalletHistoryRepository walletHistoryRepository;

    public void enquiryProcessor(Fields fields) {
        String response = statusEnquiryRequest(fields);
        JSONObject resp = new JSONObject(response);
        if (( resp.getString("status")).equalsIgnoreCase("ACCEPTED") && ( resp.getString("code")).equalsIgnoreCase("300")) {
            fields.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
            fields.put(FieldType.RRN.getName(), (String) resp.getString("transId"));
            //update acquirer wallet for success txn
            updateAcquirerWallet(true,fields);
        } else if (( resp.getString("status")).equalsIgnoreCase("REQUEST ACCEPTED") && ( resp.getString("code")).equalsIgnoreCase("300")) {

        } else {

            fields.put(FieldType.STATUS.getName(), StatusType.FAILED_AT_ACQUIRER.getName());
            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.FAILED.getResponseCode());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.FAILED.getResponseMessage());
            //update acquirer wallet for failed txn
            updateAcquirerWallet(false,fields);
        }
    }
    
    private void updateAcquirerWallet(boolean status,Fields fields) {
		logger.info("update Acquirer wallet after payout : " + fields.getFieldsAsString() + " Status : " + status);
		
		String amount=fields.get(FieldType.AMOUNT.getName()).toString();
		logger.info("Final Amount : " + amount);
		
		//find by acquirerName
		Map<String, Object> map=walletHistoryRepository.findAcquirerWalletByAcquirerName(fields.get(FieldType.ACQUIRER_TYPE.getName()));
		logger.info("Map Data find acquirer : " + map);

		Object obj=map.get("finalBalance");
		logger.info("AcquirereWallet balance fetch : " + obj.toString());
		double finalAcquirerWalletBalance=0.0;
		if(status) {
			finalAcquirerWalletBalance = Double.parseDouble(obj.toString()) - (Double.parseDouble(amount) / 100)
					- Double.parseDouble(fields.get(FieldType.ACQUIRER_TDR_SC.getName()))
					- Double.parseDouble(fields.get(FieldType.PG_TDR_SC.getName()));
		}else {
			
			finalAcquirerWalletBalance= Double.parseDouble(obj.toString()) + (Double.parseDouble(amount) / 100)
			+ Double.parseDouble(fields.get(FieldType.ACQUIRER_TDR_SC.getName()))
			+ Double.parseDouble(fields.get(FieldType.PG_TDR_SC.getName()));
		}
		
		logger.info("FinalAcquirereWallet balance update : " + finalAcquirerWalletBalance);
		boolean status1=walletHistoryRepository.updateAcquirerWallet(map.get("acquirerName").toString(), String.valueOf(finalAcquirerWalletBalance));
		logger.info("walletHistory update for acquirer : " + status1);
		
	}

    private String statusEnquiryRequest(Fields fields) {
        PaytenResponse transactionENCResponse = null;
        Gson gson = new Gson();
        String response = "";
        String encRequest = null;

        String request;
        AccountCurrencyPayout accountCurrencyRequest = accountCurrencyPayoutDao.getAccountCurrencyPayoutDetail(
                fields.get(FieldType.ACQUIRER_TYPE.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));

        try {
            logger.info("field " + fields.getFieldsAsString());
            HashMap<String, Object> tree_map = new LinkedHashMap<>();
            tree_map.put("aggregatorId", accountCurrencyRequest.getMerchantId());
            tree_map.put("secretKey", accountCurrencyRequest.getAdf1());// put hard coded key

            tree_map.put("transid", fields.get(FieldType.PG_REF_NUM.getName()));// put hard coded reseller id

            logger.info("request for payout " + tree_map);

            gson = new GsonBuilder().disableHtmlEscaping().create();
            String jsonString = gson.toJson(tree_map);
            String hash = PaytenPayoutEncryptionDecryption.generatetHashString(jsonString);
            tree_map.put("hash", hash);

            tree_map.remove("secretKey");
            logger.info("request for payout " + tree_map);

            encRequest = gson.toJson(tree_map);
            logger.info("request for payout " + encRequest);

            response = transactionConverter.callRestAPi(fields, encRequest, accountCurrencyRequest.getAdf4());
            return response;

        } catch (Exception e) {
            logger.error("Error for commuction for pay10 payout " + e);
        }
        return response;

    }
}
