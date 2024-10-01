package com.pay10.yesbank.netbanking;

public class Constants {

    public static final String YESBANK_SALE_RETURN_URL = "YESBANKNBReturnUrl";
    public static final String YESBANK_REFUND_REQUEST_URL = "YESBANKNBRefundUrl";
    public static final String YESBANK_STATUS_ENQ_REQUEST_URL = "YESBANK_STATUS_ENQ_REQUEST_URL";

    // (default->String)value DATATYPE, *=Mandatory
    public static final String clientCode = "fldClientCode";// String(40) *
    public static final String merchantCode = "fldMerchCode";// String(12) *
    public static final String txnCurrency = "fldTxnCurr";// char(4) *
    public static final String txnAmount = "fldTxnAmt";// Num(15) *
    public static final String txnSurchargeAmount = "fldTxnScAmt";// Num(15) *
    public static final String merchantRefNumber = "fldMerchRefNbr";// String(19)*
    public static final String defaultSuccessFlag = "fldSucStatFlg"; // boolean
    public static final String defaultFailedFlag = "fldFailStatFlg"; // boolean
    public static final String merchantTimeStamp = "fldDatTimeTxn";// DateTime--DD/MM/YYYY
                                                                   // hh24:mm:ss(24hourformat)e.g.16/08/2019 11:53:40 *
    public static final String exFd1 = "fldRef1";

    public static final String exFd3 = "fldRef3";
    public static final String exFd4 = "fldRef4";
    public static final String exFd5 = "fldRef5";
    public static final String exFd6 = "fldRef6";
    public static final String exFd7 = "fldRef7";
    public static final String exFd8 = "fldRef8";
    public static final String exFd9 = "fldRef9";
    public static final String exFd10 = "fldRef10";
    public static final String exFd11 = "fldRef11";

    public static final String fldDate1 = "fldDate1";
    public static final String fldDate2 = "fldDate1";
    public static final String B1 = "B1";
    public static final String checkSum = "CHECKSUM";

    public static final String subMerchantCode = "fldRef2";// String(40)*
    public static final String returnURL = "RU";// *
    public static final String clientAccNumber = "fldClientAcctNo";// Num(15) *

    public static final String YESBANK_NB_EQUATOR = "=";
    public static final String YESBANK_NB_AND = "&";

    // response fields
    public static final String bankRefNo = "BankRefNo";
    public static final String message = "Message";
    public static final String flagVerify = "flgVerify";
    public static final String flgSuccess = "flgSuccess";
}
