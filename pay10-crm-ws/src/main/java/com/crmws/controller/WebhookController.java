package com.crmws.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmws.service.WebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pay10.commons.dto.WebhookRequest;
import com.pay10.commons.dto.WebhookResp;

@RestController
@RequestMapping(path = "/api/v1/webhook", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class WebhookController {

	@Autowired
	private WebhookService webhookService;

	@PostMapping("/saveWebhookDetails")
	public ResponseEntity<WebhookResp> saveWebhookDetails(@RequestBody String jsonString) {

		System.out.println(jsonString);

		JSONObject jo = new JSONObject(jsonString);
		List<WebhookRequest> request = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		String payId = jo.getString("pay_id");
		// Deserialize JSON array to list of PaymentDetails objects
		try {
			request = Arrays
					.asList(objectMapper.readValue(jo.get("webhook_details").toString(), WebhookRequest[].class));
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<WebhookRequest> savedData = new ArrayList<>();
		for (WebhookRequest webhookRequest : request) {
			webhookRequest.setPayId(payId);
			savedData = webhookService.saveData(webhookRequest);
		}

		WebhookResp response = new WebhookResp();
		response.setStatus(true);
		response.setResponseCode("000");
		response.setMessage("Webhook saved successfully");
		response.setData(savedData);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/webhookData")
	public ResponseEntity<WebhookResp> getWebhookData(@RequestParam(required = true) String payId) {
		List<WebhookRequest> getData = webhookService.getData(payId);

		WebhookResp response = new WebhookResp();
		response.setStatus(true);
		response.setResponseCode("000");
		response.setMessage("Webhook Details fetched successfully");
		response.setData(getData);
		return ResponseEntity.ok(response);
	}

}
