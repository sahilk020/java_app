package com.pay10.pg.core.ingenico.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TransactionResponseBean
{
	
	private static Logger logger = LoggerFactory.getLogger(TransactionResponseBean.class.getName());
	
    private String responsePayload;
    private byte[] key;
    private byte[] iv;
    private String logPath;
    
    public TransactionResponseBean() {
        this.responsePayload = "";
        this.logPath = "";
    }
    
    public String getLogPath() {
        return this.logPath;
    }
    
    public void setLogPath(final String logPath) {
        this.logPath = logPath;
    }
    
    public String getResponsePayload() {
        String decryptRes = "";
        try {
            final String resp = RequestValidate.validateRespParam(this.responsePayload, this.key, this.iv);
            if (!resp.equals("")) {
                return resp;
            }
            decryptRes = AESFinalNew.decrypt(this.responsePayload, this.iv, this.key);
            final String resDataString = decryptRes.substring(0, decryptRes.lastIndexOf("|"));
            final String hashCodeString = decryptRes.substring(decryptRes.lastIndexOf("|") + 1);
            final String resHashValue = hashCodeString.split("=")[1];
            final String generatedHash = GenericCheckSums.ToHexStr(GenericCheckSums.getSHA1DigestString(resDataString));
            if (generatedHash.equals(resHashValue)) {
                return decryptRes;
            }
            return "ERROR064";
        }
        catch (Exception e) {
        	logger.error("Exception",e);
            return "ERROR037";
        }
    }
    
    public String getS2SResponsePayload() {
        String resp = "";
        this.responsePayload = this.responsePayload.substring(this.responsePayload.indexOf("=") + 1, this.responsePayload.lastIndexOf("&"));
        resp = this.getResponsePayload();
        return resp;
    }
    
    public void setResponsePayload(final String responsePayload) {
        this.responsePayload = responsePayload;
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
    
    @Override
    public String toString() {
        return String.valueOf(this.responsePayload) + "|" + this.iv + "|" + this.key;
    }
}
