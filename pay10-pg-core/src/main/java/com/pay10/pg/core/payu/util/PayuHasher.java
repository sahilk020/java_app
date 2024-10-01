package com.pay10.pg.core.payu.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import org.owasp.esapi.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

public class PayuHasher {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PayuHasher.class.getName());
	
	private static String digestHash(String str) {
		StringBuilder hash = new StringBuilder();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
			messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
			byte[] mdbytes = messageDigest.digest();
			for (byte hashByte : mdbytes) {
				hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
			}
			return hash.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String payuSaleRequestHash(TreeMap<String, String> paytmParams) {

		String template = "%s|%s|%s|%s|%s|%s|||||||||||%s";
		String str = String.format(template, paytmParams.get("key"), paytmParams.get("txnid"),
				paytmParams.get("amount"), paytmParams.get("productinfo"), paytmParams.get("firstname"),
				paytmParams.get("email"), paytmParams.get("salt"));
		
		logger.info("str "+str);
		return digestHash(str);

	}

	public static String payuTokenCreateHash(Fields params) {
		String command = "save_payment_instrument";
		String template = "%s|%s|%s|%s";

		String str = String.format(template,  params.get(FieldType.TXN_KEY.getName()) , command,
				params.get(FieldType.TXN_KEY.getName()) +":" +params.get(FieldType.CUST_EMAIL.getName()),params.get(FieldType.ADF2.getName()));
		return digestHash(str);

	}
	
	public static String payuTokenDetailsHash(Fields params) {
		String command = "get_payment_instrument";
		String template = "%s|%s|%s|%s";

		String str = String.format(template,  params.get(FieldType.TXN_KEY.getName()), command,
				 params.get(FieldType.TXN_KEY.getName()) +":"+params.get(FieldType.CUST_EMAIL.getName()), params.get(FieldType.ADF2.getName()));

		return digestHash(str);

	}
	
	public static String payuTokenDeleteHash(Fields params) {
		String command = "delete_payment_instrument";
		String template = "%s|%s|%s|%s";

		String str = String.format(template,  params.get(FieldType.TXN_KEY.getName()) , command,
			params.get(FieldType.TXN_KEY.getName()) +":"+params.get(FieldType.CUST_EMAIL.getName()),params.get(FieldType.ADF2.getName()));

		return digestHash(str);

	}

	

	public static String payuRefundAndStatusEnqHash(TreeMap<String, String> paytmParams) {
		// sha512(key|command|var1|var2|var3|salt)
		String template = "%s|%s|%s|%s";
		String str = String.format(template, paytmParams.get("key"), paytmParams.get("command"),
				paytmParams.get("var1"), paytmParams.get("salt"));
		return digestHash(str);

	}

	public static String payuStatusEnqHash(Fields paytmParams) {
		String command = "verify_payment";
		// sha512(key|command|var1|var2|var3|salt)
		String template = "%s|%s|%s|%s";
		String str = String.format(template, paytmParams.get(FieldType.TXN_KEY.getName()), command,
				paytmParams.get(FieldType.PG_REF_NUM.getName()), paytmParams.get(FieldType.ADF3.getName()));
		return digestHash(str);

	}

	public static String payuResponseHash(Map<String, String> reqMap) {
		// sha512(SALT|status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|amount|txnid|key)
		String productInfo = Constants.PAY10_PRODUCT;
		
		String template = "%s|%s|||||||||||%s|%s|%s|%s|%s|%s";
		String str = String.format(template,reqMap.get("salt") , reqMap.get("status"),
				reqMap.get("email"), reqMap.get("firstname"), productInfo, reqMap.get("amount"),
				reqMap.get("txnid"), reqMap.get("key"));
		return digestHash(str);

	}
	
	
	public static  String payuResponseRefundstatus(String mid,String requestid,Fields fields) {
		
		String str= mid+"|check_action_status|"+requestid+"|"+fields.get(FieldType.ADF2.getName());
		return digestHash(str);

	}
		
	


	public PayuHasher() {

	}
	
	public static void main(String[] args) {
		
		String str = "ZnvP1v|1012120117130123|10.00|Pay10 Product|sonu|Pay10.payu@gmail.com|||||||||||8gEIZ7xb";
		System.out.println("Checksumn : "+digestHash(str));
	}
}
