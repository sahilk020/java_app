package com.pay10.pg.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public class MobikwikEncDecUtil

{
	public MobikwikEncDecUtil() {
	}

	private String toHex(byte[] bytes) {
		StringBuilder buffer = new StringBuilder(bytes.length * 2);

		byte[] arrayOfByte = bytes;
		int j = bytes.length;
		for (int i = 0; i < j; i++) {
			Byte b = Byte.valueOf(arrayOfByte[i]);
			String str = Integer.toHexString(b.byteValue());
			int len = str.length();
			if (len == 8) {
				buffer.append(str.substring(6));
			} else if (str.length() == 2) {
				buffer.append(str);
			} else {
				buffer.append("0" + str);
			}
		}
		return buffer.toString();
	}

	public String calculateChecksum(String secretKey, String allParamValue) throws Exception {
		byte[] dataToEncryptByte = allParamValue.getBytes();
		byte[] keyBytes = secretKey.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(secretKeySpec);
		byte[] checksumByte = mac.doFinal(dataToEncryptByte);
		String checksum = toHex(checksumByte);

		return checksum;
	}

	public boolean verifyChecksum(String secretKey, String allParamVauleExceptChecksum, String checksumReceived)
			throws Exception {
		byte[] dataToEncryptByte = allParamVauleExceptChecksum.getBytes();
		byte[] keyBytes = secretKey.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(secretKeySpec);
		byte[] checksumCalculatedByte = mac.doFinal(dataToEncryptByte);
		String checksumCalculated = toHex(checksumCalculatedByte);
		if (checksumReceived.equals(checksumCalculated)) {
			return true;
		}
		return false;
	}

	public String getAllNotEmptyParamValue(HttpServletRequest request) {
		Enumeration<String> en = request.getParameterNames();
		List<String> list = Collections.list(en);
		List<String> checksumsequence = new ArrayList<>(Arrays.asList("amount", "bankid", "buyerAddress", "buyerCity",
				"buyerCountry", "buyerEmail", "buyerFirstName", "buyerLastName", "buyerPhoneNumber", "buyerPincode",
				"buyerState", "currency", "debitorcredit", "merchantIdentifier", "merchantIpAddress", "mode", "orderId",
				"product1Description", "product2Description", "product3Description", "product4Description",
				"productDescription", "productInfo", "purpose", "returnUrl", "shipToAddress", "shipToCity",
				"shipToCountry", "shipToFirstname", "shipToLastname", "shipToPhoneNumber", "shipToPincode",
				"shipToState", "showMobile", "txnDate", "txnType", "zpPayOption"));
		List<String> Parameterinseq = new ArrayList<>();
		for (String s : checksumsequence) {
			if (list.contains(s)) {
				Parameterinseq.add(s);
			}
		}

		String allNonEmptyParamValue = "";
		for (int i = 0; i < Parameterinseq.size(); i++) {
			String paramName = Parameterinseq.get(i);
			String paramValue = "";
			// if(request.getParameter(paramName)==""){
			if (paramName.equals("returnUrl")) {
				paramValue = MobikwikParamSanitizer.SanitizeURLParam(request.getParameter(paramName));
			} else
				paramValue = MobikwikParamSanitizer.sanitizeParam(request.getParameter(paramName));
			if (!paramValue.equals("")) {
				allNonEmptyParamValue = allNonEmptyParamValue + paramName + "=" + paramValue + "&";
			}
		}
		System.out.println(allNonEmptyParamValue);
		return allNonEmptyParamValue;
	}

	public static String getAllNotEmptyParamValueUpdate(HttpServletRequest request) {
		Enumeration<String> en = request.getParameterNames();
		List<String> list = Collections.list(en);

		String allNonEmptyParamValue = "";
		for (int i = 0; i < list.size(); i++) {
			String paramName = list.get(i);
			String paramValue = "";
			if (paramName.equals("returnUrl")) {
			} else if (paramValue != null) {
				allNonEmptyParamValue = allNonEmptyParamValue + "'" + paramValue + "'";
			}
		}

		return allNonEmptyParamValue;
	}

}
