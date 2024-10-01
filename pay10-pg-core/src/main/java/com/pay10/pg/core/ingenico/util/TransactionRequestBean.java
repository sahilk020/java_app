package com.pay10.pg.core.ingenico.util;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class TransactionRequestBean
{
	private static Logger logger = LoggerFactory.getLogger(TransactionRequestBean.class.getName());
	
    private final String tilda = "~";
    private final String separator = "|";
    private String strRequestType;
    private String strMerchantCode;
    private String MerchantTxnRefNumber;
    private String strITC;
    private String strAmount;
    private String strCurrencyCode;
    private String strUniqueCustomerId;
    private String strReturnURL;
    private String strS2SReturnURL;
    private String strTPSLTxnID;
    private String strShoppingCartDetails;
    private String TxnDate;
    private String strEmail;
    private String strMobileNumber;
    private String strSocialMediaIdentifier;
    private String strBankCode;
    private String strCustomerName;
    private StringBuffer strReqst;
    private String webServiceLocator;
    private String strMMID;
    private String strOTP;
    private byte[] key;
    private byte[] iv;
    static String currDate;
    private static int rqst_kit_vrsn;
    private String custID;
    private String cardID;
    private String cardNo;
    private String cardName;
    private String cardCVV;
    private String cardExpMM;
    private String cardExpYY;
    private String strTimeOut;
    private int intTimeOut;
    private String strTxnType;
    private String strTxnSubType;
    private String strToCardNumber;
    private String accountNo;
    private String additionalInfo;
    
    static {
        TransactionRequestBean.rqst_kit_vrsn = 108;
    }
    
    public TransactionRequestBean() {
        this.strRequestType = "";
        this.strMerchantCode = "";
        this.MerchantTxnRefNumber = "";
        this.strITC = "";
        this.strAmount = "";
        this.strCurrencyCode = "";
        this.strUniqueCustomerId = "";
        this.strReturnURL = "";
        this.strS2SReturnURL = "";
        this.strTPSLTxnID = "";
        this.strShoppingCartDetails = "";
        this.TxnDate = "";
        this.strEmail = "";
        this.strMobileNumber = "";
        this.strSocialMediaIdentifier = "";
        this.strBankCode = "";
        this.strCustomerName = "";
        this.strReqst = null;
        this.webServiceLocator = "NA";
        this.strMMID = "";
        this.strOTP = "";
        this.custID = "";
        this.cardID = "";
        this.cardNo = "";
        this.cardName = "";
        this.cardCVV = "";
        this.cardExpMM = "";
        this.cardExpYY = "";
        this.strTimeOut = "1000";
        this.intTimeOut = 1000;
        this.accountNo = "";
        this.additionalInfo = "";
    }
    
    public String getStrToCardNumber() {
        return this.strToCardNumber;
    }
    
    public void setStrToCardNumber(final String strToCardNumber) {
        this.strToCardNumber = strToCardNumber;
    }
    
    public String getStrTxnType() {
        return this.strTxnType;
    }
    
    public void setStrTxnType(final String strTxnType) {
        this.strTxnType = strTxnType;
    }
    
    public String getStrTxnSubType() {
        return this.strTxnSubType;
    }
    
    public void setStrTxnSubType(final String strTxnSubType) {
        this.strTxnSubType = strTxnSubType;
    }
    
    public int getIntTimeOut() {
        return this.intTimeOut;
    }
    
    public void setIntTimeOut(final int intTimeOut) {
        this.intTimeOut = intTimeOut;
    }
    
    public String getStrTimeOut() {
        return this.strTimeOut;
    }
    
    public void setStrTimeOut(final String strTimeOut) {
        this.intTimeOut = ((strTimeOut == null || "".equals(strTimeOut)) ? this.intTimeOut : (Integer.parseInt(strTimeOut) * 1000));
        this.intTimeOut = Math.abs(this.intTimeOut);
    }
    
    public String getCardID() {
        return this.cardID;
    }
    
    public void setCardID(final String cardID) {
        this.cardID = cardID;
    }
    
    public String getCardNo() {
        return this.cardNo;
    }
    
    public void setCardNo(final String cardNo) {
        this.cardNo = cardNo;
    }
    
    public String getCardName() {
        return this.cardName;
    }
    
    public void setCardName(final String cardName) {
        this.cardName = cardName;
    }
    
    public String getCardCVV() {
        return this.cardCVV;
    }
    
    public void setCardCVV(final String cardCVV) {
        this.cardCVV = cardCVV;
    }
    
    public String getCardExpMM() {
        return this.cardExpMM;
    }
    
    public void setCardExpMM(final String cardExpMM) {
        this.cardExpMM = cardExpMM;
    }
    
    public String getCardExpYY() {
        return this.cardExpYY;
    }
    
    public void setCardExpYY(final String cardExpYY) {
        this.cardExpYY = cardExpYY;
    }
    
    public String getCustID() {
        return this.custID;
    }
    
    public void setCustID(final String custID) {
        this.custID = custID;
    }
    
    public String getStrMMID() {
        return this.strMMID;
    }
    
    public void setStrMMID(final String strMMID) {
        this.strMMID = strMMID;
    }
    
    public String getStrOTP() {
        return this.strOTP;
    }
    
    public void setStrOTP(final String strOTP) {
        this.strOTP = strOTP;
    }
    
    public String getAdditionalInfo() {
        return this.additionalInfo;
    }
    
    public void setAdditionalInfo(final String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.strRequestType) + "|" + this.strMerchantCode + "|" + this.MerchantTxnRefNumber + "|" + this.strITC + "|" + this.strAmount + "|" + this.strCurrencyCode + "|" + this.strUniqueCustomerId + "|" + this.strReturnURL + "|" + this.strS2SReturnURL + "|" + this.strTPSLTxnID + "|" + this.strShoppingCartDetails + "|" + this.TxnDate + "|" + this.strEmail + "|" + this.strMobileNumber + "|" + this.strSocialMediaIdentifier + "|" + this.strBankCode + "|" + this.strCustomerName + "|" + this.cardID + "|" + this.custID + "|" + this.cardNo + "|" + this.cardName + "|" + this.cardCVV + "|" + this.cardExpMM + "|" + this.cardExpYY + "|" + this.strTxnType + "|" + this.strTxnSubType + "|" + this.strToCardNumber;
    }
    
    public String getRequestMessage() {
        final String reqMessage = String.valueOf(this.strRequestType) + "|" + this.strMerchantCode + "|" + this.MerchantTxnRefNumber + "|" + this.strITC + "|" + this.strAmount + "|" + this.strCurrencyCode + "|" + this.strUniqueCustomerId + "|" + this.strReturnURL + "|" + this.strS2SReturnURL + "|" + this.strTPSLTxnID + "|" + this.strShoppingCartDetails + "|" + this.TxnDate + "|" + this.strEmail + "|" + this.strMobileNumber + "|" + this.strSocialMediaIdentifier + "|" + this.strBankCode + "|" + this.strCustomerName + "|" + this.cardID + "|" + this.custID + "|" + this.cardNo + "|" + this.cardName + "|" + this.cardCVV + "|" + this.cardExpMM + "|" + this.cardExpYY + "|" + this.strTxnType + "|" + this.strTxnSubType + "|" + this.strToCardNumber;
        return reqMessage;
    }
    
    public String getMerchantTxnRefNumber() {
        return this.MerchantTxnRefNumber;
    }
    
    public String getWebServiceLocator() {
        return this.webServiceLocator;
    }
    
    public void setWebServiceLocator(final String webServiceLocator) {
        if (webServiceLocator != "" && webServiceLocator != null) {
            this.webServiceLocator = webServiceLocator;
        }
    }
    
    public byte[] getKey() {
        return this.key;
    }
    
    public void setKey(final byte[] key) {
        this.key = key;
    }
    
    public byte[] getIv() {
        return this.iv;
    }
    
    public void setIv(final byte[] iv) {
        this.iv = iv;
    }
    
    public void setMerchantTxnRefNumber(final String MerchantTxnRefNumber) {
        this.MerchantTxnRefNumber = MerchantTxnRefNumber;
    }
    
    public String getTxnDate() {
        return this.TxnDate;
    }
    
    public void setTxnDate(final String TxnDate) {
        this.TxnDate = TxnDate;
    }
    
    public String getStrAmount() {
        return this.strAmount;
    }
    
    public void setStrAmount(final String strAmount) {
        this.strAmount = strAmount;
    }
    
    public String getStrBankCode() {
        return this.strBankCode;
    }
    
    public void setStrBankCode(final String strBankCode) {
        this.strBankCode = strBankCode;
    }
    
    public String getStrCurrencyCode() {
        return this.strCurrencyCode;
    }
    
    public void setStrCurrencyCode(final String strCurrencyCode) {
        this.strCurrencyCode = strCurrencyCode;
    }
    
    public String getStrCustomerName() {
        return this.strCustomerName;
    }
    
    public void setStrCustomerName(final String strCustomerName) {
        this.strCustomerName = ((strCustomerName == null || "".equalsIgnoreCase(strCustomerName.trim())) ? "" : strCustomerName);
    }
    
    public String getStrEmail() {
        return this.strEmail;
    }
    
    public void setStrEmail(final String strEmail) {
        this.strEmail = strEmail;
    }
    
    public String getStrITC() {
        return this.strITC;
    }
    
    public void setStrITC(final String strITC) {
        this.strITC = strITC;
    }
    
    public String getStrMerchantCode() {
        return this.strMerchantCode;
    }
    
    public void setStrMerchantCode(final String strMerchantCode) {
        this.strMerchantCode = strMerchantCode;
    }
    
    public String getStrMobileNumber() {
        return this.strMobileNumber;
    }
    
    public void setStrMobileNumber(final String strMobileNumber) {
        this.strMobileNumber = strMobileNumber;
    }
    
    public String getStrRequestType() {
        return this.strRequestType;
    }
    
    public void setStrRequestType(final String strRequestType) {
        this.strRequestType = strRequestType;
    }
    
    public String getStrReturnURL() {
        return this.strReturnURL;
    }
    
    public void setStrReturnURL(final String strReturnURL) {
        this.strReturnURL = strReturnURL;
    }
    
    public String getStrS2SReturnURL() {
        return this.strS2SReturnURL;
    }
    
    public void setStrS2SReturnURL(final String strS2SReturnURL) {
        this.strS2SReturnURL = strS2SReturnURL;
    }
    
    public String getStrShoppingCartDetails() {
        return this.strShoppingCartDetails;
    }
    
    public void setStrShoppingCartDetails(final String strShoppingCartDetails) {
        this.strShoppingCartDetails = strShoppingCartDetails;
    }
    
    public String getStrSocialMediaIdentifier() {
        return this.strSocialMediaIdentifier;
    }
    
    public void setStrSocialMediaIdentifier(final String strSocialMediaIdentifier) {
        this.strSocialMediaIdentifier = strSocialMediaIdentifier;
    }
    
    public String getStrTPSLTxnID() {
        return this.strTPSLTxnID;
    }
    
    public void setStrTPSLTxnID(final String strTPSLTxnID) {
        this.strTPSLTxnID = strTPSLTxnID;
    }
    
    public String getStrUniqueCustomerId() {
        return this.strUniqueCustomerId;
    }
    
    public void setStrUniqueCustomerId(final String strUniqueCustomerId) {
        this.strUniqueCustomerId = strUniqueCustomerId;
    }
    
    public String getEncData() throws Exception {
        final StringBuffer strClientMetaData = new StringBuffer();
        if (RequestValidate.isBlankOrNull(this.strITC)) {
            strClientMetaData.append("{itc:");
            strClientMetaData.append(this.strITC);
            strClientMetaData.append("}");
        }
        if (RequestValidate.isBlankOrNull(this.strEmail)) {
            strClientMetaData.append("{email:");
            strClientMetaData.append(this.strEmail);
            strClientMetaData.append("}");
        }
        if (RequestValidate.isBlankOrNull(this.strMobileNumber)) {
            strClientMetaData.append("{mob:");
            strClientMetaData.append(this.strMobileNumber);
            strClientMetaData.append("}");
        }
        if (RequestValidate.isBlankOrNull(this.strUniqueCustomerId)) {
            strClientMetaData.append("{custid:");
            strClientMetaData.append(this.strUniqueCustomerId);
            strClientMetaData.append("}");
        }
        if (RequestValidate.isBlankOrNull(this.strCustomerName)) {
            strClientMetaData.append("{custname:");
            strClientMetaData.append(this.strCustomerName);
            strClientMetaData.append("}");
        }
        this.strReqst = new StringBuffer();
        if (RequestValidate.isBlankOrNull(this.strRequestType)) {
            this.strReqst.append("rqst_type=");
            this.strReqst.append(this.strRequestType);
        }
        this.strReqst.append("|rqst_kit_vrsn=9.0." + TransactionRequestBean.rqst_kit_vrsn);
        if (RequestValidate.isBlankOrNull(this.strMerchantCode)) {
            this.strReqst.append("|tpsl_clnt_cd=");
            this.strReqst.append(this.strMerchantCode);
        }
        if (RequestValidate.isBlankOrNull(this.MerchantTxnRefNumber)) {
            this.strReqst.append("|clnt_txn_ref=");
            this.strReqst.append(this.MerchantTxnRefNumber);
        }
        if (RequestValidate.isBlankOrNull(strClientMetaData.toString())) {
            this.strReqst.append("|clnt_rqst_meta=");
            this.strReqst.append(strClientMetaData.toString());
        }
        if (RequestValidate.isBlankOrNull(this.strAmount)) {
            this.strReqst.append("|rqst_amnt=");
            this.strReqst.append(this.strAmount);
        }
        if (RequestValidate.isBlankOrNull(this.strCurrencyCode)) {
            this.strReqst.append("|rqst_crncy=");
            this.strReqst.append(this.strCurrencyCode);
        }
        if (RequestValidate.isBlankOrNull(this.strReturnURL)) {
            this.strReqst.append("|rtrn_url=");
            this.strReqst.append(this.strReturnURL);
        }
        if (RequestValidate.isBlankOrNull(this.strS2SReturnURL)) {
            this.strReqst.append("|s2s_url=");
            this.strReqst.append(this.strS2SReturnURL);
        }
        if (RequestValidate.isBlankOrNull(this.strShoppingCartDetails)) {
            this.strReqst.append("|rqst_rqst_dtls=");
            this.strReqst.append(this.strShoppingCartDetails);
        }
        if (RequestValidate.isBlankOrNull(this.TxnDate)) {
            this.strReqst.append("|clnt_dt_tm=");
            this.strReqst.append(this.TxnDate);
        }
        if (RequestValidate.isBlankOrNull(this.strBankCode)) {
            this.strReqst.append("|tpsl_bank_cd=");
            this.strReqst.append(this.strBankCode);
        }
        if (RequestValidate.isBlankOrNull(this.strTPSLTxnID)) {
            this.strReqst.append("|tpsl_txn_id=");
            this.strReqst.append(this.strTPSLTxnID);
        }
        if (RequestValidate.isBlankOrNull(this.custID)) {
            this.strReqst.append("|cust_id=");
            this.strReqst.append(this.custID);
        }
        if (RequestValidate.isBlankOrNull(this.cardID)) {
            this.strReqst.append("|card_id=");
            this.strReqst.append(this.cardID);
        }
        if (RequestValidate.isBlankOrNull(this.strMobileNumber)) {
            this.strReqst.append("|mob=");
            this.strReqst.append(this.strMobileNumber);
        }
        if (RequestValidate.isBlankOrNull(this.strTxnType)) {
            this.strReqst.append("|TxnType=");
            this.strReqst.append(this.strTxnType);
        }
        if (RequestValidate.isBlankOrNull(this.strTxnSubType)) {
            this.strReqst.append("|TxnSubType=");
            this.strReqst.append(this.strTxnSubType);
        }
        if (RequestValidate.isBlankOrNull(this.accountNo)) {
            this.strReqst.append("|accountNo=");
            this.strReqst.append(this.accountNo);
        }
        if (RequestValidate.isBlankOrNull(this.additionalInfo)) {
            this.strReqst.append("|additionalInfo=");
            this.strReqst.append(this.additionalInfo);
        }
        if ("TWC".equalsIgnoreCase(this.strRequestType) || "TRC".equalsIgnoreCase(this.strRequestType) || "TIC".equalsIgnoreCase(this.strRequestType) || "TWD".equalsIgnoreCase(this.strRequestType) || "TRD".equalsIgnoreCase(this.strRequestType) || "TRS".equalsIgnoreCase(this.strRequestType)) {
            final StringBuffer CardInfoBuff = new StringBuffer();
            CardInfoBuff.append("card_Hname=");
            CardInfoBuff.append(this.cardName);
            CardInfoBuff.append("|card_no=");
            CardInfoBuff.append(this.cardNo);
            CardInfoBuff.append("|card_Cvv=");
            CardInfoBuff.append(this.cardCVV);
            CardInfoBuff.append("|exp_mm=");
            CardInfoBuff.append(this.cardExpMM);
            CardInfoBuff.append("|exp_yy=");
            CardInfoBuff.append(this.cardExpYY);
            if (RequestValidate.isBlankOrNull(this.strToCardNumber)) {
                CardInfoBuff.append("|ToCardNumber=");
                CardInfoBuff.append(this.strToCardNumber);
            }
            String cardInfo = AESFinalNew.encryptHx(CardInfoBuff.toString(), this.iv, this.key);
            cardInfo = AESFinalNew.encryptHx(cardInfo, this.iv, this.key);
            this.strReqst.append("|card_details=");
            this.strReqst.append(cardInfo);
        }
        if ("TCC".equalsIgnoreCase(this.strRequestType) || "TCD".equalsIgnoreCase(this.strRequestType) || "TCS".equalsIgnoreCase(this.strRequestType)) {
            final StringBuffer CardInfoBuff = new StringBuffer();
            CardInfoBuff.append("|card_Cvv=");
            CardInfoBuff.append(this.cardCVV);
            String cardInfo = AESFinalNew.encryptHx(CardInfoBuff.toString(), this.iv, this.key);
            cardInfo = AESFinalNew.encryptHx(cardInfo, this.iv, this.key);
            this.strReqst.append("|card_details=");
            this.strReqst.append(cardInfo);
        }
        if ("TWI".equalsIgnoreCase(this.strRequestType)) {
            final StringBuffer impsInfoBuff = new StringBuffer();
            impsInfoBuff.append("mmid=");
            impsInfoBuff.append(this.strMMID);
            impsInfoBuff.append("|mob_no=");
            impsInfoBuff.append(this.strMobileNumber);
            impsInfoBuff.append("|otp=");
            impsInfoBuff.append(this.strOTP);
            String impsInfo = AESFinalNew.encryptHx(impsInfoBuff.toString(), this.iv, this.key);
            impsInfo = AESFinalNew.encryptHx(impsInfo, this.iv, this.key);
            this.strReqst.append("|imps_details=");
            this.strReqst.append(impsInfo);
        }
        if ("TIO".equalsIgnoreCase(this.strRequestType)) {
            this.strReqst.append("|otp=");
            this.strReqst.append(this.strOTP);
        }
        this.strReqst.append("|hash=" + GenericCheckSums.ToHexStr(GenericCheckSums.getSHA1DigestString(this.strReqst.toString())));
        final String encData = AESFinalNew.encrypt(this.strReqst.toString(), this.iv, this.key);
        return encData;
    }
    
    public String getTransactionToken() {
        String resToken = "ERROR000";
        SoapWSCall objSoap = null;
        Map map = new HashMap();
        String url = null;
        String data = null;
        try {
            if (this.webServiceLocator != null && !"".equals(this.webServiceLocator) && !"NA".equals(this.webServiceLocator)) {
                try {
                    objSoap = new SoapWSCall();
                    final String resp = RequestValidate.validateReqParam(this.strRequestType, this.strMerchantCode, this.key, this.iv);
                    if (!resp.equals("")) {
                        return resp;
                    }
                    data = this.getEncData();
                    data = URLEncoder.encode(data, "UTF-8");
                    if (this.webServiceLocator.contains("Live") || this.webServiceLocator.contains("UAT")) {
                        if (this.strRequestType.charAt(0) == 'T') {
                            url = getRedirectUrl(this.webServiceLocator);
                            url = String.valueOf(url) + "?encData=" + data + "|" + this.strMerchantCode + "~";
                            map.put("getTransactionTokenReturn", url);
                        }
                        else {
                            url = getRedirectUrlS2S(this.webServiceLocator);
                            map = objSoap.getTransactionToken(String.valueOf(this.getEncData()) + "|" + this.strMerchantCode + "~", url, this.intTimeOut, this.strRequestType);
                        }
                    }
                    else if (this.strRequestType.charAt(0) == 'T') {
                        url = getRedirectUrl(this.webServiceLocator);
                        url = String.valueOf(url) + "?encData=" + data + "|" + this.strMerchantCode + "~";
                        map.put("getTransactionTokenReturn", url);
                    }
                    else {
                        map = objSoap.getTransactionToken(String.valueOf(this.getEncData()) + "|" + this.strMerchantCode + "~", this.webServiceLocator, this.intTimeOut, this.strRequestType);
                    }
                    if (map.get("ERROR") != null) {
                        resToken = (String) map.get("ERROR");
                    }
                    else {
                        resToken = ((map.get("getTransactionTokenReturn") == null) ? "ERROR091" : (String) map.get("getTransactionTokenReturn"));
                    }
                }
                catch (Exception e) {
                	logger.error("Exception ",e);
                    return resToken;
                }
                return resToken;
            }
            resToken = "ERROR065";
            return resToken;
        }
        catch (Exception ex) {
        	logger.error("Exception ",ex);
            return resToken;
        }
    }
    
    public String getTransactionToken_old() {
        String resToken = "ERROR000";
        SoapWSCall objSoap = null;
        Map map = new HashMap();
        try {
            if (this.webServiceLocator != null && !"".equals(this.webServiceLocator) && !"NA".equals(this.webServiceLocator)) {
                try {
                    objSoap = new SoapWSCall();
                    final String resp = RequestValidate.validateReqParam(this.strRequestType, this.strMerchantCode, this.key, this.iv);
                    if (!resp.equals("")) {
                        return resp;
                    }
                    map = objSoap.getTransactionToken(String.valueOf(this.getEncData()) + "|" + this.strMerchantCode + "~", this.webServiceLocator, this.intTimeOut, this.strRequestType);
                    if (map.get("ERROR") != null) {
                        resToken = (String) map.get("ERROR");
                    }
                    else {
                        resToken = ((map.get("getTransactionTokenReturn") == null) ? "ERROR091" : (String) map.get("getTransactionTokenReturn"));
                    }
                }
                catch (Exception e) {
                	logger.error("Exception ",e);
                    return resToken;
                }
                return resToken;
            }
            resToken = "ERROR065";
            return resToken;
        }
        catch (Exception ex) {
        	logger.error("Exception ",ex);
            return resToken;
        }
    }
    
    public static String getRedirectUrlS2S(final String data) {
        String actualURL = null;
        if (data.contains("Live")) {
            actualURL = TPSLConstant.LIVE_S2S;
        }
        return actualURL;
    }
    
    public static String getRedirectUrl(final String data) {
        String actualURL = null;
        if (data.contains("Live") || data.contains("tpsl-india")) {
            actualURL = TPSLConstant.LIVE_REDIRECTION;
        }
        return actualURL;
    }
    
    
    public String getAccountNo() {
        return this.accountNo;
    }
    
    public void setAccountNo(final String tpvAccountNo) {
        this.accountNo = tpvAccountNo;
    }
}
