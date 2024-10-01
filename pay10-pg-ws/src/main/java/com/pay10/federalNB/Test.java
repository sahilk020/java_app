package com.pay10.federalNB;

import com.google.gson.JsonObject;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JsonObject jsonRequest = new JsonObject();
		jsonRequest.addProperty("abd", "AMOUNT_CLOSE_TAG");
		jsonRequest.addProperty("abda", "AMOUNT_CLOSE_TAG");
		jsonRequest.addProperty("abdaa", "AMOUNT_CLOSE_TAG");
		jsonRequest.addProperty("abdaaa", "AMOUNT_CLOSE_TAG");
		System.out.println(jsonRequest.toString());

	}

}
