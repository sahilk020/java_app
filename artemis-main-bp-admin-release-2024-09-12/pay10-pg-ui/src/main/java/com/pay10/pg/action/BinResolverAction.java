package com.pay10.pg.action;

import com.opensymphony.xwork2.ActionContext;
import com.pay10.commons.api.BindbControllerServiceProvider;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.*;
import com.pay10.pg.action.service.PrepareRequestParemeterService;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.SessionMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shashi
 */
public class BinResolverAction extends AbstractSecureAction {

    private static final long serialVersionUID = -793834678003594132L;
    private static final Logger log = LoggerFactory.getLogger(BinResolverAction.class);

    private BinRange binRangeObj;
    private String bin;
    private String mopType;
    private String paymentType;
    private String issuerBankName;
    private String issuerCountry;
    private String cardHolderType;
    private String paymentsRegion;
    private String ccsurchargeAmount;
    private String ccsurchargeTotal;
    private Boolean internationalCard = Boolean.FALSE;

    @Autowired
    private BindbControllerServiceProvider bindbControllerServiceProvider;
    @Autowired
    private TdrSettingDao tdrSettingDao;
    @Autowired
    private PrepareRequestParemeterService prepareRequestParemeterService;
    @Autowired
    private UserDao userDao;

    @Override
    public String execute() {

        try {
            Map<String, String> binRangeResponseMap;
            // call bindb service
            binRangeResponseMap = bindbControllerServiceProvider.binfind(bin);
            mopType = binRangeResponseMap.get(FieldType.MOP_TYPE.getName());
            paymentType = binRangeResponseMap.get(FieldType.PAYMENT_TYPE.getName());
            issuerBankName = binRangeResponseMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
            issuerCountry = binRangeResponseMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName());
            cardHolderType = binRangeResponseMap.get(FieldType.CARD_HOLDER_TYPE.getName());
            paymentsRegion = binRangeResponseMap.get(FieldType.PAYMENTS_REGION.getName());
            sessionMap = (SessionMap<String, Object>) ActionContext.getContext().getSession();
            sessionMap.put(Constants.BIN.getValue(), binRangeResponseMap);

            Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
            fields.logAllFields("Fields in BinResolver");
            String convertedAmount = sessionMap.get(FieldType.AMOUNT.getName()).toString();
            log.info("convertedAmount:{}", convertedAmount);
            String currency = sessionMap.get(FieldType.CURRENCY_CODE.getName()).toString();
            log.info("currency:{}", currency);
            String amount = Amount.toDecimal(convertedAmount, currency);
            log.info("converted amount:{}", amount);
            BigDecimal bigDecimalAmount;
            BigDecimal tempAmount = new BigDecimal(amount);
            BigDecimal ccTransSurcharge;
            BigDecimal surchargeCCAmount;
            String paymentTypeMops = sessionMap.get(Constants.PAYMENT_TYPE_MOP.getValue()).toString();
            log.info("paymentTypeMops:{}", paymentTypeMops);
            if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_CC.getValue())) {
                ccTransSurcharge = tdrSettingDao.getCheckSurcharge("CC", Double.valueOf(amount), fields.get(FieldType.PAY_ID.getName()), new Date(), paymentsRegion, currency, MopType.getmop(mopType).getName().toUpperCase());
                log.info("ccTransSurcharge:{}", ccTransSurcharge);
                ccsurchargeAmount = ccTransSurcharge.toString();
                surchargeCCAmount = tempAmount.add(ccTransSurcharge);
                log.info("surchargeCCAmount:{}", surchargeCCAmount);
                bigDecimalAmount = surchargeCCAmount;
                sessionMap.put(FieldType.CC_TOTAL_AMOUNT.getName(), surchargeCCAmount);
                sessionMap.put(FieldType.CC_TOTAL_AMOUNT_INTERNATIONAL.getName(), surchargeCCAmount);
                sessionMap.put(FieldType.CC_SURCHARGE.getName(), ccTransSurcharge);
                if (ccTransSurcharge.doubleValue() != 0) {
                    sessionMap.computeIfPresent(FieldType.SURCHARGE_FLAG.getName(), (k, v) -> Constants.Y_FLAG.getValue());
                    sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), bigDecimalAmount.toString());
                    ccsurchargeTotal = bigDecimalAmount.toString();
                }
                User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
                JSONObject requestParameterJson = prepareRequestParemeterService.prepareRequestParameter(fields, user, sessionMap);
                sessionMap.put(FieldType.SUPPORTED_PAYMENT_TYPE.getName(), requestParameterJson.toString());
                if (StringUtils.isNoneBlank(paymentsRegion) && paymentsRegion.equals("INTERNATIONAL")) {
                    internationalCard = Boolean.TRUE;
                }
            }
            return SUCCESS;
        } catch (Exception exception) {
            return SUCCESS;
        }
    }

    public BinRange getBinRangeObj() {
        return binRangeObj;
    }

    public void setBinRangeObj(BinRange binRangeObj) {
        this.binRangeObj = binRangeObj;
    }

    public String getBin() {
        return bin;
    }

    public String getMopType() {
        return mopType;
    }

    public void setMopType(String mopType) {
        this.mopType = mopType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getIssuerBankName() {
        return issuerBankName;
    }

    public void setIssuerBankName(String issuerBankName) {
        this.issuerBankName = issuerBankName;
    }

    public String getIssuerCountry() {
        return issuerCountry;
    }

    public void setIssuerCountry(String issuerCountry) {
        this.issuerCountry = issuerCountry;
    }

    public String getCardHolderType() {
        return cardHolderType;
    }

    public void setCardHolderType(String cardHolderType) {
        this.cardHolderType = cardHolderType;
    }

    public String getPaymentsRegion() {
        return paymentsRegion;
    }

    public void setPaymentsRegion(String paymentsRegion) {
        this.paymentsRegion = paymentsRegion;
    }

    public String getCcsurchargeAmount() {
        return ccsurchargeAmount;
    }

    public void setCcsurchargeAmount(String ccsurchargeAmount) {
        this.ccsurchargeAmount = ccsurchargeAmount;
    }

    public String getCcsurchargeTotal() {
        return ccsurchargeTotal;
    }

    public void setCcsurchargeTotal(String ccsurchargeTotal) {
        this.ccsurchargeTotal = ccsurchargeTotal;
    }

    public Boolean getInternationalCard() {
        return internationalCard;
    }

    public void setInternationalCard(Boolean internationalCard) {
        this.internationalCard = internationalCard;
    }
}
