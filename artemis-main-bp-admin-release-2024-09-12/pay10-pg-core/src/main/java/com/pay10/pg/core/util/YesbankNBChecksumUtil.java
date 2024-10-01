package com.pay10.pg.core.util;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class YesbankNBChecksumUtil {

    private static final String HASH_ALGORITHM = "SHA-512";
    private static Logger logger = LoggerFactory.getLogger(YesbankNBChecksumUtil.class.getName());

    public String getHash(JsonObject jsonRequest, String secretKey) {

        StringBuffer signature = new StringBuffer();
        try {
            Map<String, String> postData = new HashMap<String, String>();
            Set<Map.Entry<String, JsonElement>> entries = jsonRequest.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                String key = entry.getKey();
                postData.put(key, jsonRequest.get(key).toString());
            }
            /*
             * for (String key : jsonRequest.keySet()) { postData.put(key,
             * jsonRequest.get(key).toString().replace("\"", "")); }
             */
            String data = "6d11bbaa9f0a642619dba0ea9eaa4efd1e91323c";
            SortedSet<String> keys = new TreeSet<String>(postData.keySet());

            for (String key : keys) {
                data = data + "|" + postData.get(key);
            }
            data = data.replaceAll("\"", "");
            System.out.println("final hashing string ========>" + data);
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(data.getBytes("UTF-8"));
            byte byteData[] = md.digest();

            // convert the byte to hex format method 1

            for (int i = 0; i < byteData.length; i++) {
                signature.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return signature.toString().toUpperCase();

        } catch (Exception e) {

            logger.error("Error in getting hash", e);
        }
        return signature.toString();

    }

    public String checkSaleResponseHash(JSONObject jsonRequest, String secretKey) {

        String signature = "";
        try {
            JsonObject jsonReq = new JsonObject();
            Iterator<String> keys = jsonRequest.keys();
            do {
                String val = keys.next().toString();
                if (!jsonRequest.get(val).toString().equals(""))
                    jsonReq.addProperty(val, jsonRequest.get(val).toString());
            } while (keys.hasNext());

            signature = getHash(jsonReq, secretKey);

        } catch (Exception e) {

            logger.error("Error in getting sale response hash", e);
        }
        return signature;

    }

    public String getHashRefund(String para, String salt) {
        StringBuffer signature = new StringBuffer();
        try {
            String data = "";
            salt = "6d11bbaa9f0a642619dba0ea9eaa4efd1e91323c";

            data = salt + "|" + para;
            System.out.println("final hashing string Refund ========>" + data);
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(data.getBytes("UTF-8"));
            byte byteData[] = md.digest();

            // convert the byte to hex format method 1

            for (int i = 0; i < byteData.length; i++) {
                signature.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return signature.toString().toUpperCase();

        } catch (Exception e) {

            logger.error("Error in getting refund hash", e);
        }
        return signature.toString();

    }

}
