package com.pay10.pg.core.util;

import org.apache.commons.lang.StringUtils;

import java.security.MessageDigest;
import java.util.ArrayList;

public class TFPUtil {


    public static String generateHash(ArrayList<String> requestData, String saltKey) throws Exception {

        String respHashData = "";
        for (int i = 0; i < requestData.size(); i++) {
            if (StringUtils.isBlank(requestData.get(i)))
                continue;
            else
                respHashData += requestData.get(i) + "~";
        }
        respHashData = respHashData.substring(0, respHashData.length() - 1);
        //respHashData = respHashData.
        respHashData = respHashData + saltKey;
        System.out.println("respHashData " + respHashData);
        return hashCalculate(respHashData);
    }

    public static String hashCalculate(String str) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(str.getBytes("UTF-8"));
        byte byteData[] = md.digest();
        StringBuffer hashCodeBuffer = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return hashCodeBuffer.toString().toUpperCase();
    }

    public static void main(String[] args) {
       // ArrayList<String> requestData = new ArrayList<>();
        String respHashData = "vsxbkscjb,,";
       /* for (int i = 0; i < requestData.size(); i++) {
            if (StringUtils.isBlank(requestData.get(i)))
                continue;
            else
                respHashData += requestData.get(i) + "~";
        }*/
        respHashData = respHashData.substring(0, respHashData.length() - 2);
        //respHashData = respHashData.
        respHashData = respHashData + "klhggy79hjkg";
        System.out.println("respHashData ==========" + respHashData);
    }

}
