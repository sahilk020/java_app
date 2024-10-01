package com.pay10.payout.btsepay;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import org.json.JSONObject;

import com.itextpdf.text.log.SysoCounter;

public class HmacSha384 {

    public static void main(String[] args) {
        // Step 1: Create your JSON payload
    	String jsonString= "/payment/pay-api/v1/withdraw/crypto"+getUNIXTS()+generateJSON();

        // Step 2: Generate an HMAC-SHA384 signature
        String secretKey = "83a2bf610dea35ad90ff8fd6f827bdbdbb16b58bd3098cb0136c2eb74698368d";
        
        
        
        String signatureBase64 = generateHmacSha384Signature(jsonString, secretKey);

        // Step 3: Include the signature in your JSON payload
//        JSONObject signedPayload = new JSONObject();
//        signedPayload.put("data", payloadData);
//        signedPayload.put("signature", signatureBase64);
//
//        String finalJsonPayload = signedPayload.toString();
        
        System.out.println(signatureBase64);
    }

    static String generateHmacSha384Signature(String data, String key) {
        try {
            Mac sha384Hmac = Mac.getInstance("HmacSHA384");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA384");
            sha384Hmac.init(secretKey);

            byte[] hmacData = sha384Hmac.doFinal(data.getBytes());
System.out.println(hmacData.toString());
            return Base64.getEncoder().encodeToString(hmacData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String generateJSON() {
    	String JSON="{\r\n"
    			+ "    \"coin\": \"USDT\",\r\n"
    			+ "    \"protocol\": \"ERC20\",\r\n"
    			+ "    \"feeType\": 1,\r\n"
    			+ "    \"amount\": 10.9,\r\n"
    			+ "    \"address\": \"0xed2Ee49EE15662222AE4667E5123cdfw19d13e63\",\r\n"
    			+ "    \"clientOrderId\": \"outside_transaction_id\",\r\n"
    			+ "    \"referenceId\": \"janedoe\",\r\n"
    			+ "    \"extra\": \"\"\r\n"
    			+ "}";
    	System.out.println(JSON);
    	return JSON;
    	
    	
    }
    public static String getUNIXTS() {
        // Get current Unix timestamp in milliseconds
        long unixTimestampMillis = System.currentTimeMillis();

        System.out.println("Unix Timestamp in milliseconds: " + unixTimestampMillis);

        // Convert to seconds (optional)
        long unixTimestampSeconds = unixTimestampMillis / 1000L;
        System.out.println("Unix Timestamp in seconds: " + unixTimestampSeconds);
        return unixTimestampMillis+"";
     
    }
    
    
}

