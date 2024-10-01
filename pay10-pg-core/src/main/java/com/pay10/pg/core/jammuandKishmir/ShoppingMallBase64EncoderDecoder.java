package com.pay10.pg.core.jammuandKishmir;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.springframework.stereotype.Component;

import com.infosys.feba.tools.shoppingmallencryption.ShoppingMallSymmetricCipherHelper;

@Component
public final class ShoppingMallBase64EncoderDecoder {

	private ShoppingMallBase64EncoderDecoder() {
	}

	public static  String jammuandKismhmirenc(String request, String key) throws Exception {

		String encryptedVal = null;

		encryptedVal = ShoppingMallSymmetricCipherHelper.encrypt(request, key, "AES");
		encryptedVal = URLEncoder.encode(encryptedVal, "UTF-8");


		return encryptedVal;

	}

	public  String jammuandKismhmirdec(String response, String key) throws Exception {

		String encryptedVal = null;
		encryptedVal = URLDecoder.decode(response, "UTF-8");

		encryptedVal = ShoppingMallSymmetricCipherHelper.decrypt(response, key, "AES");
	

		return encryptedVal;

	}
}