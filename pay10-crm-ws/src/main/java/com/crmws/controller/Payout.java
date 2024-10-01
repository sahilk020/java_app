package com.crmws.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmws.entity.ResponseMessageExceptionList;
import com.crmws.service.impl.PayoutServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pay10.commons.dao.WhiteListedIpDao;
import com.pay10.commons.dto.PoFRM;
import com.pay10.commons.dto.SettlementDTO;
import com.pay10.commons.mongo.SettlementPORepository;
import com.pay10.commons.user.MerchantKeySalt;
import com.pay10.commons.user.MerchantKeySaltDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.TransactionManager;

@CrossOrigin
@RestController
@RequestMapping("Payout")
public class Payout {
	private static final Logger logger = LoggerFactory.getLogger(Payout.class.getName());
	@Autowired
	private PayoutServiceImpl impl;
	@Autowired
	private SettlementPORepository settlementPORepository;
	@Autowired
	private WhiteListedIpDao whiteListedIpDao;
	
	@Autowired
	private MerchantKeySaltDao merchantKeySaltDao;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	@Autowired
	private UserDao userDao;

	@PostMapping("/FRM")
	public ResponseEntity<ResponseMessageExceptionList> RNS(@RequestBody PoFRM frm) {

		ResponseMessageExceptionList message = new ResponseMessageExceptionList();

		if (StringUtils.isBlank(frm.getPayId())) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Select Merchant");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}

		if (StringUtils.isBlank(frm.getChannel())) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Select Channel");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}

		if (StringUtils.isBlank(frm.getCurrencyCode())) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Select CurrencyCode");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}

		if (frm.getMinTicketSize()<1) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Enter Min Ticket Size is more than 1 or equal to 1");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}

		if (frm.getMaxTicketSize()<1) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Enter Max Ticket Size is more than 1 or equal to 1");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		
		}
		
		if (frm.getMaxTicketSize()<frm.getMinTicketSize()) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Max Ticket Should Greater Than Min Ticket Size");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		
		}

		if (frm.getDailyLimit()<1) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Enter daily limit is more than 1 or equal to 1");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}

		if (frm.getWeeklyLimit()<1) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Enter Weekly limit is more than 1 or equal to 1");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}

		if (frm.getMonthlyLimit()<1) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Enter Monthly limit is more than 1 or equal to 1");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}

		if (frm.getDailyVolume()<1) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Enter daily volume is more than 1 or equal to 1");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}

		if (frm.getWeeklyVolume()<1) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Enter weekly volume is more than 1 or equal to 1");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}

		if (frm.getMonthlyVolume()<1) {
			message.setHttpStatus(HttpStatus.BAD_REQUEST);
			message.setRespmessage("Please Enter Monthly volume is more than 1 or equal to 1");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}
	
		return impl.saveFrm(frm);
	}
	
	@PostMapping("/settlementRequest")
	public ResponseEntity<Map<String, Object>> merchantSettlementRequestApi(@RequestParam String payId,@RequestBody String data,HttpServletRequest request){
		logger.info("merchantSettlementRequestApi : " + payId + "\n" + data);
		Map<String, Object> responseMap=new HashMap<>();
		logger.info("Internal_Cust_IP for X-Forwarded-For: " + request.getHeader("X-Forwarded-For"));
		logger.info("Internal_Cust_IP for Remote Addr: " + request.getRemoteAddr());

		String ip = request.getHeader("X-Forwarded-For");
		logger.info("Info : " + ip);
		boolean isIpWhiteListed = whiteListedIpDao.checkIpWhitelisted(payId, ip!=null?ip:request.getRemoteAddr());

		if (!isIpWhiteListed) {
			responseMap.put("status", "0001");
			responseMap.put("message", "IP not whitelisted");
			return ResponseEntity.ok(responseMap); 
		}
		
		if(StringUtils.isBlank(data)) {
			responseMap.put("status", "0002");
			responseMap.put("message", "Please provide request data");
			return ResponseEntity.ok(responseMap); 
		}
		
		JSONObject jo=new JSONObject(data);
		String hash=jo.optString("hash");
		
		if(StringUtils.isBlank(hash)) {
			responseMap.put("status", "0003");
			responseMap.put("message", "hash data not found");
			return ResponseEntity.ok(responseMap); 
		}
		
		MerchantKeySalt merchantKeySalt= merchantKeySaltDao.find(payId);
		
		HashMap<String, Object> tree_map = new LinkedHashMap<String, Object>();
		tree_map.put("payId", payId);
		tree_map.put("currency", jo.getString("currency"));
		tree_map.put("amount", jo.getString("amount"));
		tree_map.put("salt", merchantKeySalt.getSalt());
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(tree_map);
		System.out.println("A : : : : " + jsonString);
		
		String generatedHash=generatetHashString(jsonString);
		
		if(!hash.equals(generatedHash)) {
			responseMap.put("status", "0004");
			responseMap.put("message", "invalid hash");
			return ResponseEntity.ok(responseMap); 
		}
		
		SettlementDTO settlementDTO=new SettlementDTO();
		settlementDTO.setAmount(jo.getString("amount"));
		settlementDTO.setPayId(payId);
		settlementDTO.setCurrency(jo.getString("currency"));
		
		String actualResponse=settlementPORepository.createSettlementFromPanel(settlementDTO, request);
		
		responseMap.put("status", "0000");
		responseMap.put("message", actualResponse);
		return ResponseEntity.ok(responseMap); 
		
	}
	
	public static String generatetHashString(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			StringBuilder hashtext = new StringBuilder(no.toString(16));
			while (hashtext.length() < 64) {
				hashtext.insert(0, '0');
			}
			return hashtext.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/payoutRequest")
	public ResponseEntity<Map<String, Object>> merchantPayoutRequestApi(@RequestParam String payId,@RequestBody String data,HttpServletRequest request){
		logger.info("merchantPayoutRequestApi : " + payId + "\n" + data);
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> responseMap=new HashMap<>();
		logger.info("Internal_Cust_IP for X-Forwarded-For: " + request.getHeader("X-Forwarded-For"));
		logger.info("Internal_Cust_IP for Remote Addr: " + request.getRemoteAddr());

		String ip = request.getHeader("X-Forwarded-For");
		logger.info("Info : " + ip);
		boolean isIpWhiteListed = whiteListedIpDao.checkIpWhitelisted(payId, ip!=null?ip:request.getRemoteAddr());

		if (!isIpWhiteListed) {
			responseMap.put("status", "0001");
			responseMap.put("message", "IP not whitelisted");
			return ResponseEntity.ok(responseMap); 
		}
		
		if(StringUtils.isBlank(data)) {
			responseMap.put("status", "0002");
			responseMap.put("message", "Please provide request data");
			return ResponseEntity.ok(responseMap); 
		}
		
		JSONObject jo=new JSONObject(data);
		String hash=jo.optString("hash");
		
		if(StringUtils.isBlank(hash)) {
			responseMap.put("status", "0003");
			responseMap.put("message", "hash data not found");
			return ResponseEntity.ok(responseMap); 
		}
		
		MerchantKeySalt merchantKeySalt= merchantKeySaltDao.find(payId);
		
		HashMap<String, Object> tree_map = new LinkedHashMap<String, Object>();
		tree_map.put("payId", payId);
		tree_map.put("mode", jo.getString("mode"));
		tree_map.put("holderName",jo.getString("holderName"));
		tree_map.put("accountNo", jo.getString("accountNo"));
		tree_map.put("bankName",jo.getString("bankName"));
		tree_map.put("ifsc", jo.getString("ifsc"));
		tree_map.put("bankCode", jo.getString("bankCode"));
		tree_map.put("currency", jo.getString("currency"));
		tree_map.put("amount", jo.getString("amount"));
		tree_map.put("channel", jo.getString("channel"));
		tree_map.put("city", jo.getString("city"));
		tree_map.put("bankBranch", jo.getString("bankBranch"));
		tree_map.put("email", jo.getString("email"));
		tree_map.put("phone", jo.getString("phone"));
		tree_map.put("salt", merchantKeySalt.getSalt());
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(tree_map);
		System.out.println("A : : : : " + jsonString);
		
		String generatedHash=generatetHashString(jsonString);
		
		if(!hash.equals(generatedHash)) {
			responseMap.put("status", "0004");
			responseMap.put("message", "invalid hash");
			return ResponseEntity.ok(responseMap); 
		}
		
		User user=userDao.findByPayId(payId);
		SettlementDTO settlementDTO=new SettlementDTO();
		settlementDTO.setPayId(payId);
		settlementDTO.setUid(TransactionManager.getNewTransactionId());
		settlementDTO.setMerchantName(user.getBusinessName());
		settlementDTO.setMode(jo.getString("mode"));
		settlementDTO.setAccountHolderName(jo.getString("holderName"));
		settlementDTO.setAccountNo(jo.getString("accountNo"));
		settlementDTO.setBankName(jo.getString("bankName"));
		settlementDTO.setIfsc(jo.getString("ifsc"));
		settlementDTO.setCity(jo.getString("city"));
		settlementDTO.setAccountProvince(jo.getString("city"));
		settlementDTO.setBankBranch(jo.getString("bankBranch"));
		settlementDTO.setCurrency(jo.getString("currency"));
		settlementDTO.setEmail(jo.getString("email"));
		settlementDTO.setPhone(jo.getString("phone"));
		settlementDTO.setChannel(jo.getString("channel"));
		settlementDTO.setStatus("PENDING");
		settlementDTO.setSettlementDate(dateFormat1.format(new Date()));
		settlementDTO.setSubmitDate(dateFormat1.format(new Date()));
		
		String actualResponse=settlementPORepository.createSettlementFromPanelAPI(settlementDTO, request);
		
		responseMap.put("status", "0000");
		responseMap.put("message", actualResponse);
		return ResponseEntity.ok(responseMap); 
	
	}

    @PostMapping("/getPayoutFrm")
    public ResponseEntity<Object> getPayoutFRM(@RequestBody PoFRM poFRM) {
        ResponseMessageExceptionList message = new ResponseMessageExceptionList();
        if (StringUtils.isBlank(poFRM.getPayId())) {
            message.setHttpStatus(HttpStatus.BAD_REQUEST);
            message.setRespmessage("Please Select Merchant");
            return ResponseEntity.status(message.getHttpStatus()).body(message);
        }
        if (StringUtils.isBlank(poFRM.getCurrencyCode())) {
            message.setHttpStatus(HttpStatus.BAD_REQUEST);
            message.setRespmessage("Please Select CurrencyCode");
            return ResponseEntity.status(message.getHttpStatus()).body(message);
        }
        if (StringUtils.isBlank(poFRM.getChannel())) {
            message.setHttpStatus(HttpStatus.BAD_REQUEST);
            message.setRespmessage("Please Select Channel");
            return ResponseEntity.status(message.getHttpStatus()).body(message);
        }
        return ResponseEntity.ok(impl.getPayoutFrmDetails(poFRM.getPayId(), poFRM.getCurrencyCode(), poFRM.getChannel()));
    }

}
