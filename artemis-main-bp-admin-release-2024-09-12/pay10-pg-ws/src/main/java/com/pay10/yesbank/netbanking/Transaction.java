package com.pay10.yesbank.netbanking;

import org.springframework.stereotype.Service;

@Service("yesbankNBTransaction")
public class Transaction {

    private String SALE_RETURN_URL;
    private String REFUND_REQUEST_URL;
    private String STATUS_ENQ_REQUEST_URL;
    private String clientCode;
    private String merchantCode;
    private String transactionCurrency;
    private String txnAmount;
    private String txnSurchargeAmount;
    private String merchantRefNumber;
    private String defaultSuccessFlag;
    private String defaultFailedFlag;
    private String merchantTimeStamp;
    private String exFd1;
    private String exFd3;
    private String exFd4;
    private String exFd5;
    private String exFd6;
    private String exFd7;
    private String exFd8;
    private String exFd9;
    private String exFd10;
    private String exFd11;
    private String subMerchantCode;
    private String returnURL;
    private String clientAccNumber;

    private String fldDate1;
    private String fldDate2;
    private String B1;
    private String checkSum;

    private String bankRefNo;
    private String Message;
    private String flgVerify;
    private String flgSuccess;

    public String getFlgVerify() {
        return flgVerify;
    }

    public void setFlgVerify(String flgVerify) {
        this.flgVerify = flgVerify;
    }

    public String getBankRefNo() {
        return bankRefNo;
    }

    public void setBankRefNo(String bankRefNo) {
        this.bankRefNo = bankRefNo;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getSALE_RETURN_URL() {
        return SALE_RETURN_URL;
    }

    public void setSALE_RETURN_URL(String sALE_RETURN_URL) {
        SALE_RETURN_URL = sALE_RETURN_URL;
    }

    public String getREFUND_REQUEST_URL() {
        return REFUND_REQUEST_URL;
    }

    public void setREFUND_REQUEST_URL(String rEFUND_REQUEST_URL) {
        REFUND_REQUEST_URL = rEFUND_REQUEST_URL;
    }

    public String getSTATUS_ENQ_REQUEST_URL() {
        return STATUS_ENQ_REQUEST_URL;
    }

    public void setSTATUS_ENQ_REQUEST_URL(String sTATUS_ENQ_REQUEST_URL) {
        STATUS_ENQ_REQUEST_URL = sTATUS_ENQ_REQUEST_URL;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(String txnAmount) {
        this.txnAmount = txnAmount;
    }

    public String getTxnSurchargeAmount() {
        return txnSurchargeAmount;
    }

    public void setTxnSurchargeAmount(String txnSurchargeAmount) {
        this.txnSurchargeAmount = txnSurchargeAmount;
    }

    public String getMerchantRefNumber() {
        return merchantRefNumber;
    }

    public void setMerchantRefNumber(String merchantRefNumber) {
        this.merchantRefNumber = merchantRefNumber;
    }

    public String getDefaultSuccessFlag() {
        return defaultSuccessFlag;
    }

    public void setDefaultSuccessFlag(String defaultSuccessFlag) {
        this.defaultSuccessFlag = defaultSuccessFlag;
    }

    public String getDefaultFailedFlag() {
        return defaultFailedFlag;
    }

    public void setDefaultFailedFlag(String defaultFailedFlag) {
        this.defaultFailedFlag = defaultFailedFlag;
    }

    public String getMerchantTimeStamp() {
        return merchantTimeStamp;
    }

    public void setMerchantTimeStamp(String merchantTimeStamp) {
        this.merchantTimeStamp = merchantTimeStamp;
    }

    public String getExFd1() {
        return exFd1;
    }

    public void setExFd1(String exFd1) {
        this.exFd1 = exFd1;
    }

    public String getExFd3() {
        return exFd3;
    }

    public void setExFd3(String exFd3) {
        this.exFd3 = exFd3;
    }

    public String getExFd4() {
        return exFd4;
    }

    public void setExFd4(String exFd4) {
        this.exFd4 = exFd4;
    }

    public String getExFd5() {
        return exFd5;
    }

    public void setExFd5(String exFd5) {
        this.exFd5 = exFd5;
    }

    public String getExFd6() {
        return exFd6;
    }

    public void setExFd6(String exFd6) {
        this.exFd6 = exFd6;
    }

    public String getExFd7() {
        return exFd7;
    }

    public void setExFd7(String exFd7) {
        this.exFd7 = exFd7;
    }

    public String getExFd8() {
        return exFd8;
    }

    public void setExFd8(String exFd8) {
        this.exFd8 = exFd8;
    }

    public String getExFd9() {
        return exFd9;
    }

    public void setExFd9(String exFd9) {
        this.exFd9 = exFd9;
    }

    public String getExFd10() {
        return exFd10;
    }

    public void setExFd10(String exFd10) {
        this.exFd10 = exFd10;
    }

    public String getExFd11() {
        return exFd11;
    }

    public void setExFd11(String exFd11) {
        this.exFd11 = exFd11;
    }

    public String getSubMerchantCode() {
        return subMerchantCode;
    }

    public void setSubMerchantCode(String subMerchantCode) {
        this.subMerchantCode = subMerchantCode;
    }

    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    public String getClientAccNumber() {
        return clientAccNumber;
    }

    public void setClientAccNumber(String clientAccNumber) {
        this.clientAccNumber = clientAccNumber;
    }

    public String getFldDate1() {
        return fldDate1;
    }

    public void setFldDate1(String fldDate1) {
        this.fldDate1 = fldDate1;
    }

    public String getFldDate2() {
        return fldDate2;
    }

    public void setFldDate2(String fldDate2) {
        this.fldDate2 = fldDate2;
    }

    public String getB1() {
        return B1;
    }

    public void setB1(String b1) {
        B1 = b1;
    }

    public String getFlgSuccess() {
		return flgSuccess;
	}

	public void setFlgSuccess(String flgSuccess) {
		this.flgSuccess = flgSuccess;
	}

	@Override
    public String toString() {
        return "Transaction [SALE_RETURN_URL=" + SALE_RETURN_URL + ", REFUND_REQUEST_URL=" + REFUND_REQUEST_URL
                + ", STATUS_ENQ_REQUEST_URL=" + STATUS_ENQ_REQUEST_URL + ", clientCode=" + clientCode
                + ", merchantCode=" + merchantCode + ", transactionCurrency=" + transactionCurrency + ", txnAmount="
                + txnAmount + ", txnSurchargeAmount=" + txnSurchargeAmount + ", merchantRefNumber=" + merchantRefNumber
                + ", defaultSuccessFlag=" + defaultSuccessFlag + ", defaultFailedFlag=" + defaultFailedFlag
                + ", merchantTimeStamp=" + merchantTimeStamp + ", exFd1=" + exFd1 + ", exFd3=" + exFd3 + ", exFd4="
                + exFd4 + ", exFd5=" + exFd5 + ", exFd6=" + exFd6 + ", exFd7=" + exFd7 + ", exFd8=" + exFd8 + ", exFd9="
                + exFd9 + ", exFd10=" + exFd10 + ", exFd11=" + exFd11 + ", subMerchantCode=" + subMerchantCode
                + ", returnURL=" + returnURL + ", clientAccNumber=" + clientAccNumber + ", fldDate1=" + fldDate1
                + ", fldDate2=" + fldDate2 + ", B1=" + B1 + ", checkSum=" + checkSum + ", bankRefNo=" + bankRefNo
                + ", Message=" + Message + "]";
    }

}
