package com.pay10.bindb.core;

import java.util.HashMap;
import java.util.Map;

import com.pay10.bindb.service.BinRangeDTO;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.BinRange;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class BinRangeParser {
	private static Logger logger = LoggerFactory.getLogger(BinRangeParser.class.getName());

	public JSONObject getBinParser(StringBuilder response) throws ParseException {
		Object obj;
		JSONParser parser = new JSONParser();
		obj = parser.parse(response.toString());
		JSONObject jObject = (JSONObject) obj;
		return jObject;
	}

	public Map<String, String> parseToMap(BinRange binRange) {
		Map<String, String> binMap = new HashMap<String, String>();
		logger.info("bin mop" +  binRange.getMopType());
		logger.info("bin mop code" +  binRange.getMopType().getCode());
		
		binMap.put(FieldType.MOP_TYPE.getName(), binRange.getMopType().getCode());
		binMap.put(FieldType.PAYMENT_TYPE.getName(), binRange.getCardType().getCode());
		binMap.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(), binRange.getIssuerBankName());
		binMap.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(), binRange.getIssuerCountry());
		binMap.put(FieldType.PAYMENTS_REGION.getName(), binRange.getRfu1());
		binMap.put(FieldType.CARD_HOLDER_TYPE.getName(), binRange.getRfu2());
		
		return binMap;
	}
	
	@Deprecated
	public static BinRange createBinRangeViaAPI(Map<String, String> binMap) {
		BinRange binRange = new BinRange();
		if(StringUtils.isBlank(binMap.get("bin").toString())) {
			return binRange;
		}
		binRange.setBinCodeLow(binMap.get("bin").toString());
		binRange.setBinCodeHigh(binMap.get("bin").toString()+"000");
		binRange.setBinRangeLow(binMap.get("bin").toString()+"000");
		binRange.setBinRangeHigh(binMap.get("bin").toString()+"999");
		
		if (binMap.get("card").equalsIgnoreCase("VISA")) {
			binRange.setMopType(MopType.VISA);
		}
		else if  (binMap.get("card").equalsIgnoreCase("MASTER")) {
			binRange.setMopType(MopType.MASTERCARD);
		}
		else if  (binMap.get("card").equalsIgnoreCase("MASTERCARD")) {
			binRange.setMopType(MopType.MASTERCARD);
		}
		else if  (binMap.get("card").equalsIgnoreCase("RUPAY")) {
			binRange.setMopType(MopType.RUPAY);
		}
		else if  (binMap.get("card").equalsIgnoreCase("AMEX")) {	
			binRange.setMopType(MopType.AMEX);
		}
		
		if (binMap.get("type").equalsIgnoreCase("CREDIT")) {
			binRange.setCardType(PaymentType.CREDIT_CARD);
		}
		else {
			binRange.setCardType(PaymentType.DEBIT_CARD);
		}
		
		binRange.setIssuerBankName(binMap.get("bank"));
		binRange.setIssuerCountry(binMap.get("countrycode"));
		if (binMap.get("countrycode").equalsIgnoreCase("IN")) {
			binRange.setRfu1("DOMESTIC");
		}
		else {
			binRange.setRfu1("INTERNATIONAL");
		}
		
		if (binMap.get("level").equalsIgnoreCase("BUSINESS")) {
			binRange.setRfu2("COMMERCIAL");
		}
		else {
			binRange.setRfu2("CONSUMER");
		}
		
		return binRange;
	}
	public static BinRange createBinRangeNeutrinoAPI(BinRangeDTO dto) {
		BinRange binRange = new BinRange();
		if(StringUtils.isBlank(dto.getBinNumber())) {
			return binRange;
		}
		binRange.setBinCodeLow(dto.getBinNumber());
		binRange.setBinCodeHigh(dto.getBinNumber()+"000");
		binRange.setBinRangeLow(dto.getBinNumber()+"000");
		binRange.setBinRangeHigh(dto.getBinNumber()+"999");

		if (dto.getCardBrand().equalsIgnoreCase("VISA")) {
			binRange.setMopType(MopType.VISA);
		}
		else if  (dto.getCardBrand().equalsIgnoreCase("MASTER")) {
			binRange.setMopType(MopType.MASTERCARD);
		}
		else if  (dto.getCardBrand().equalsIgnoreCase("MASTERCARD")) {
			binRange.setMopType(MopType.MASTERCARD);
		}
		else if  (dto.getCardBrand().equalsIgnoreCase("RUPAY")) {
			binRange.setMopType(MopType.RUPAY);
		}
		
		else if  (dto.getCardBrand().equalsIgnoreCase("DISCOVER")) {
			binRange.setMopType(MopType.DISCOVER);
		}
		else if  (dto.getCardBrand().equalsIgnoreCase("AMEX")) {
			binRange.setMopType(MopType.AMEX);
		}

		if (dto.getCardType().equalsIgnoreCase("CREDIT")) {
			binRange.setCardType(PaymentType.CREDIT_CARD);
		}
		else {
			binRange.setCardType(PaymentType.DEBIT_CARD);
		}

		binRange.setIssuerBankName(dto.getIssuer());
		binRange.setIssuerCountry(dto.getCountry());
		if (dto.getCountryCode().equalsIgnoreCase("IN")) {
			binRange.setRfu1("DOMESTIC");
		}
		else {
			binRange.setRfu1("INTERNATIONAL");
		}

		if (dto.isCommercial()) {
			binRange.setRfu2("COMMERCIAL");
		}
		else {
			binRange.setRfu2("CONSUMER");
		}

		return binRange;
	}

	
}
