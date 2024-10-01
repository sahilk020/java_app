package com.pay10.pg.core.ingenico.util;

import java.util.HashMap;
import java.util.Map;

public final class RequestValidate
{
    private static Map<String, String> errorMap;
    
    static {
        (RequestValidate.errorMap = new HashMap<String, String>()).put("rqst_amnt", "ERROR003");
        RequestValidate.errorMap.put("rqst_crncy", "ERROR005");
        RequestValidate.errorMap.put("clnt_rqst_meta", "ERROR006");
        RequestValidate.errorMap.put("rtrn_url", "ERROR009");
        RequestValidate.errorMap.put("s2s_url", "ERROR022");
        RequestValidate.errorMap.put("rqst_rqst_dtls", "ERROR023");
        RequestValidate.errorMap.put("tpsl_clnt_cd", "ERROR027");
        RequestValidate.errorMap.put("tpsl_txn_id", "ERROR033");
        RequestValidate.errorMap.put("clnt_dt_tm", "ERROR035");
        RequestValidate.errorMap.put("tpsl_bank_cd", "ERROR039");
        RequestValidate.errorMap.put("clnt_txn_ref", "ERROR066");
        RequestValidate.errorMap.put("rqst_kit_vrsn", "ERROR066");
        RequestValidate.errorMap.put("clnt_txn_ref_or_tpsl_txn_id", "ERROR066");
        RequestValidate.errorMap.put("invalid_key", "ERROR067");
        RequestValidate.errorMap.put("invalid_iv", "ERROR068");
        RequestValidate.errorMap.put("blank_pg_response", "ERROR069");
        RequestValidate.errorMap.put("jar_exception", "ERROR000");
        RequestValidate.errorMap.put("web_service_time_out", "ERROR091");
        RequestValidate.errorMap.put("soap_calling_exception", "ERROR092");
        RequestValidate.errorMap.put("soap_url_connection_exception", "ERROR093");
        RequestValidate.errorMap.put("soap_msg_exception", "ERROR0114");
        RequestValidate.errorMap.put("reading_xml_exception", "ERROR0115");
    }
    
    public static String validateReqParam(final String pReqType, final String pMerCode, final byte[] pEncKey, final byte[] pEncIv) {
        if (!isBlankOrNull(pReqType)) {
            return "ERROR008";
        }
        if (!pReqType.equals("T") && !pReqType.equals("S") && !pReqType.equals("O") && !pReqType.equals("R") && !pReqType.equals("TNR") && !pReqType.equals("TCI") && !pReqType.equals("TWC") && !pReqType.equals("TRC") && !pReqType.equals("TCC") && !pReqType.equals("TWI") && !pReqType.equals("TIC") && !pReqType.equals("TIO") && !pReqType.equals("TWD") && !pReqType.equals("TRD") && !pReqType.equals("TCD") && !pReqType.equals("TEM") && !pReqType.equals("TKN") && !pReqType.equals("TRS") && !pReqType.equals("TCS") && !pReqType.equals("TND")) {
            return "ERROR002";
        }
        if (!isBlankOrNull(pMerCode)) {
            return RequestValidate.errorMap.get("tpsl_clnt_cd");
        }
        if (pEncKey == null || pEncKey.length <= 0) {
            return RequestValidate.errorMap.get("invalid_key");
        }
        if (pEncIv == null || pEncIv.length <= 0) {
            return RequestValidate.errorMap.get("invalid_iv");
        }
        return "";
    }
    
    public static String validateRespParam(final String pRes, final byte[] pEncKey, final byte[] pEncIv) {
        if (!isBlankOrNull(pRes)) {
            return RequestValidate.errorMap.get("blank_pg_response");
        }
        if (pEncKey == null || pEncKey.length <= 0) {
            return RequestValidate.errorMap.get("invalid_key");
        }
        if (pEncIv == null || pEncIv.length <= 0) {
            return RequestValidate.errorMap.get("invalid_iv");
        }
        return "";
    }
    
    public static boolean isBlankOrNull(final String pParam) {
        return pParam != null && !pParam.equals("") && !pParam.equals("NA");
    }
}
