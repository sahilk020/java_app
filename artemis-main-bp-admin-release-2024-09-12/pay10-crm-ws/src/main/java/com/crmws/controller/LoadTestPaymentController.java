package com.crmws.controller;

import java.util.Date;
import java.util.Random;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.loadtest.CustomHasher;
import com.crmws.loadtest.RequestDTO;

@RestController
@RequestMapping("/loadtest")
public class LoadTestPaymentController {

	@PostMapping
	public String getRequestLoad(@RequestBody RequestDTO requestInfo) throws Exception {
		System.out.println("RequestInfo received for getResponse along with orderId :: " + requestInfo.toString());
		String orderId = String.valueOf(new Date().getTime()) + "-" + new Random().nextInt(99999);
		System.out.println("orderId " + orderId);

		// StringBuilder resposne = null;
		String resposne = getResponse(requestInfo);
		System.out.println("Response String :: " + resposne);

		String generatedHash = CustomHasher.getHash(resposne.toString());
		System.out.println("Generated Hash : " + generatedHash);
		String encString = resposne + "~" + generatedHash;
		System.out.println("Data Before Encrypted : " + generatedHash);
		String encData = CustomHasher.encrypt(requestInfo.getPayId(), requestInfo.getKeySalt(), encString);
		System.out.println("Data After Encrypted : " + encData);

		StringBuilder finalResposne = new StringBuilder();
		finalResposne.append("PAY_ID");
		finalResposne.append(":");
		finalResposne.append(requestInfo.getPayId());
		finalResposne.append("&");
		finalResposne.append("ENCDATA");
		finalResposne.append(":");
		finalResposne.append(encData);

		System.out.println("Final Resposne : " + finalResposne.toString());
		return finalResposne.toString();
	}

	private String getResponse(RequestDTO requestInfo) {

		String orderId = String.valueOf(new Date().getTime()) + "-" + new Random().nextInt(99999);

		StringBuilder sb = new StringBuilder();
		sb.append("SALT");
		sb.append("~");
		sb.append(requestInfo.getKeySalt());
		sb.append("PAY_ID");
		sb.append("~");
		sb.append(requestInfo.getPayId());
		sb.append("ORDER_ID");
		sb.append("~");
		sb.append(orderId);
		sb.append("PAYMENT_TYPE");
		sb.append("~");
		sb.append(requestInfo.getPaymentType());

		if (requestInfo.getPaymentType().equalsIgnoreCase("CARD")) {
			sb.append("CARD_NUMBER");
			sb.append("~");
			sb.append(requestInfo.getPaymentType());
			sb.append("CARD_EXP_DT");
			sb.append("~");
			sb.append(requestInfo.getPaymentType());
			sb.append("CVV");
			sb.append("~");
			sb.append(requestInfo.getPaymentType());
		}
		sb.append("MOP_TYPE");
		sb.append("~");
		sb.append(requestInfo.getMopTyep());
		sb.append("AMOUNT");
		sb.append("~");
		sb.append(requestInfo.getAmount());
		sb.append("CURRENCY_CODE");
		sb.append("~");
		sb.append(requestInfo.getCurrencyCode());
		sb.append("CUST_NAME");
		sb.append("~");
		sb.append(requestInfo.getCustName());
		sb.append("CUST_EMAIL");
		sb.append("~");
		sb.append(requestInfo.getCustEmail());
		sb.append("CUST_PHONE");
		sb.append("~");
		sb.append(requestInfo.getCustPhone());
		sb.append("RETURN_URL");
		sb.append("~");
		sb.append(requestInfo.getReturnURL());
		return sb.toString();
	}

}
