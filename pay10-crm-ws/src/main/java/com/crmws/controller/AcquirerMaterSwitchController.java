package com.crmws.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dao.AcquirerStatus;
import com.crmws.dto.AcquirerDTO;
import com.crmws.dto.AcquirerStatusDto;
import com.crmws.dto.AcquirerSwitchDto;
import com.crmws.dto.ApiResponse;
//import com.crmws.service.impl.GetAcquirervalidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.user.AcquirerDowntimeScheduling;
import com.pay10.commons.user.AcquirerDowntimeSchedulingDao;
import com.pay10.commons.user.AcquirerSwitchHistory;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PaymentTypeUI;
import com.pay10.commons.util.PropertiesManager;

@RestController
@RequestMapping("/AcquirerSwitch")
public class AcquirerMaterSwitchController {
	private static final Logger logger = LoggerFactory.getLogger(AcquirerMaterSwitchController.class.getName());

	@Autowired
	UserDao userdao;
	@Autowired
	RouterConfigurationDao routerConfigurationDao;
	
	@Autowired
	AcquirerDowntimeSchedulingDao acquirerDowntimeSchedulingDao;
	
	@Autowired
	AcquirerStatus acquirerStatus;
	
//	@Autowired
//	GetAcquirervalidation getAcquirervalidation;
	
	
	@PostMapping("/getMappedAcquirer")
	public ApiResponse getAcquirerMasterList(@RequestParam String acquirerType, @RequestParam String PaymentType) {
		ApiResponse apiResponse = new ApiResponse();
		List<AcquirerSwitchDto> rulesList = new ArrayList<AcquirerSwitchDto>();

		logger.info("acquirerType..={},PaymentType..={}",acquirerType,PaymentType);
		List<RouterConfiguration> list= routerConfigurationDao.getPayIdByEmailId(acquirerType,PaymentType);
		for(RouterConfiguration data:list){
			AcquirerSwitchDto acquirer=new AcquirerSwitchDto();	
		acquirer.setMopType( MopType.getInstanceUsingCode(data.getMopType()).getName());
		acquirer.setPayId(data.getMerchant());
		acquirer.setMerchantName(userdao.getBusinessNameByPayId(data.getMerchant()));
		rulesList.add(acquirer);	
		}
			
		if (!list.isEmpty()) {
			apiResponse.setMessage("Data Fetch Successfully");
			apiResponse.setData(rulesList);
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("No Data Found");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}
	
	@PostMapping("/getPaymentType")
	public ApiResponse getPaymentType(@RequestParam String acquirer) {
		ApiResponse apiResponse = new ApiResponse();
		logger.info("acquirer by payment type={}",acquirer);
		List<String> list= routerConfigurationDao.getpaymnetlistbyAcquirer(acquirer);
		List<AcquirerDTO> data= new ArrayList<AcquirerDTO>();
		for(String payment:list) {
			AcquirerDTO data1=new AcquirerDTO();
			data1.setCode(payment);
			data1.setName(PaymentType.getInstanceUsingCode(payment).getName());
			data.add(data1);

			
		}

		if (!list.isEmpty()) {
			apiResponse.setMessage("Data Fetch Successfully");
			apiResponse.setData(data);
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("No Data Found");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}
	@PostMapping("/getAcquirerDownTime")
	public ApiResponse getAcquirerDownTime() {
		ApiResponse apiResponse = new ApiResponse();

		List<AcquirerDowntimeScheduling> list= acquirerDowntimeSchedulingDao.getRuleList();
	
		if (!list.isEmpty()) {
			apiResponse.setMessage("Data Fetch Successfully");
			apiResponse.setData(list);
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("No Data Found");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}
	
	
	@PostMapping("/getAcquirerStatus")
	public ApiResponse getAcquirerStatus (@RequestParam String acquirer, @RequestParam String payment) {
		ApiResponse apiResponse = new ApiResponse();

		List<AcquirerStatusDto> list= acquirerStatus.getAcquirerStatusDtos(acquirer,payment);
	
		if (!list.isEmpty()) {
			apiResponse.setMessage("Data Fetch Successfully");
			apiResponse.setData(list);
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("No Data Found");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}
	
	

	@PostMapping("/getAcquirerValidate")
	public ApiResponse getAcquirerValidate (@RequestParam String acquirer, @RequestParam String paymentType) {
//		ApiResponse apiResponse = new ApiResponse();
//String message = getAcquirervalidation.getAcquirervalidate(acquirer,paymentType);
//
//		if (!message.isEmpty()) {
//			apiResponse.setMessage("Data Fetch Successfully");
//			apiResponse.setData(message);
//			apiResponse.setStatus(true);
//			return apiResponse;
//		} else {
//			apiResponse.setMessage("No Data Found");
//			apiResponse.setData(message);
//			apiResponse.setStatus(false);
//			return apiResponse;
//		}
		return null;
	}
	
	
	@PostMapping("/getAcquirerDownTimeReport")
	public ApiResponse getAcquirerDownTimeReport () {
		ApiResponse apiResponse = new ApiResponse();

		List<AcquirerSwitchHistory> 	list = acquirerDowntimeSchedulingDao.getAcquirerHistoryDEfault();
		List<AcquirerSwitchDto> rulesList = new ArrayList<AcquirerSwitchDto>();

	
		for(AcquirerSwitchHistory data:list){
			AcquirerSwitchDto acquirerlist=new AcquirerSwitchDto();	
			acquirerlist.setUpDateOn(data.getUpdateOn());
			acquirerlist.setUpDateBy(data.getUpDateBy());
			acquirerlist.setStatus(data.getStatus());
			acquirerlist.setPaymentType( PaymentTypeUI.getpaymentName(data.getPaymentType()));
			acquirerlist.setAcquirer(data.getAcquirerName());
			
		rulesList.add(acquirerlist);	
		}
			
		if (!list.isEmpty()) {
			apiResponse.setMessage("Data Fetch Successfully");
			apiResponse.setData(rulesList);
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("No Data Found");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}
	
	@PostMapping("/createAcquirerDownTime")
	public ApiResponse createAcquirerDownTime(@RequestParam String Acquirer, @RequestParam String PaymentType,@RequestParam String fromDate,@RequestParam String toDate, @RequestParam String Updateby) {
		try {	ApiResponse apiResponse = new ApiResponse();
		logger.info("acquirerType..={},PaymentType..={},fromDate..={},todate..={}",Acquirer,PaymentType,fromDate,toDate);
		AcquirerDowntimeScheduling createRule= new AcquirerDowntimeScheduling();
		createRule.setAcquirerName(Acquirer);
		createRule.setToDate(toDate.replace("T", " ").concat(":00"));
		createRule.setFromDate(fromDate.replace("T", " ").concat(":00"));
		createRule.setPaymentType(PaymentType);
		createRule.setStatus("ACTIVE");
		createRule.setCreatedBy(Updateby);
		createRule.setCreatedOn(getCurrentDate());
		
		int count=0;
	List<AcquirerDowntimeScheduling>	data=acquirerDowntimeSchedulingDao.checkTimeSlotAcquirer(createRule);
	for (AcquirerDowntimeScheduling result:data) {
		
		Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(result.getFromDate());
		Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(result.getToDate());
		
		Date date3=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createRule.getFromDate());
		Date date4=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createRule.getToDate());

		

		if(date3.after(date1)&&date3.before(date2)){
			count++;
        }
		
		if(date4.after(date1)&&date4.before(date2)){
			count++;	        }
		
		
	}
	
	if(count>0) {
		
		apiResponse.setMessage("Duplicate  Record are not created");
		apiResponse.setData("");
		apiResponse.setStatus(true);
		return apiResponse;
	}else {
		acquirerDowntimeSchedulingDao.createAndUpdate(createRule);
		
		createAcquirerDownTimeEmail(createRule);

			apiResponse.setMessage("Save Successful");
			apiResponse.setData("");
			apiResponse.setStatus(true);
			return apiResponse;
	}
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	

	@PostMapping("/deleteAcquirerDownTime")
	public ApiResponse deleteAcquirerDownTime(@RequestParam String id, @RequestParam String updateBy) {
		ApiResponse apiResponse = new ApiResponse();

		logger.info("id..={},updateBy..={},fromDate..={},todate..={}",id,updateBy);
		AcquirerDowntimeScheduling createRule= new AcquirerDowntimeScheduling();
		createRule=acquirerDowntimeSchedulingDao.findById(Long.parseLong(id));
		createRule.setId(Long.parseLong(id));
		createRule.setUpDateBy(updateBy);
		createRule.setStatus("INACTIVE");
		List<RouterConfiguration> list= routerConfigurationDao.getSchadularListForInactive(createRule.getAcquirerName(),createRule.getPaymentType());

		list.stream().forEach((k) -> {
			
			k.setCurrentlyActive(true);
			k.setAcquirerSwitch(false);
		
			routerConfigurationDao.deleteAcquirerMaster(k);
			
		});
		
		acquirerDowntimeSchedulingDao.createAndUpdate(createRule);
		createAcquirerDownTimeEmail(createRule);

		
		
			apiResponse.setMessage("Rule is Delete SuccessFull");
			apiResponse.setData("");
			apiResponse.setStatus(true);
			return apiResponse;
		
	}
	
	public String getCurrentDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
	
	@PostMapping("/changeStatusAcquirerMaster")
	public ApiResponse changeStatusForAcquirerMaster(@RequestParam String acquirer, @RequestParam String paymentType,@RequestParam String updateby) {
		ApiResponse apiResponse = new ApiResponse();

		logger.info("acquirerType..={},PaymentType..={}",acquirer,paymentType);
		List<RouterConfiguration> list= routerConfigurationDao.getPayIdByEmailId(acquirer,paymentType);
		AcquirerSwitchHistory acquirerSwitchHistory=new AcquirerSwitchHistory();
		acquirerSwitchHistory.setAcquirerName(acquirer);
		acquirerSwitchHistory.setPaymentType(paymentType);
		acquirerSwitchHistory.setUpDateBy(updateby);
		acquirerSwitchHistory.setUpdateOn(getCurrentDate());
		acquirerSwitchHistory.setStatus("INACTIVE");
		acquirerDowntimeSchedulingDao.createAndUpdate(acquirerSwitchHistory);

		list.stream().forEach((k) -> {
			
			k.setCurrentlyActive(false);
			k.setAcquirerSwitch(true);
			k.setAcUpdateBy(updateby);
			k.setAcUpdateOn(getCurrentDate());
			
			routerConfigurationDao.deleteAcquirerMaster(k);
			
		});
		acquirerMasterSwitchNotification( PaymentTypeUI.getpaymentName(paymentType),  acquirer,  updateby, "INACTIVE", getCurrentDate()) ;

		
			apiResponse.setMessage("Rule is Create SuccessFull");
			apiResponse.setData("");
			apiResponse.setStatus(true);
			return apiResponse;
		
	}
	
	@PostMapping("/changeStatusActive")
	public ApiResponse changeStatusActive(@RequestParam String acquirer, @RequestParam String paymentType,@RequestParam String updateby ) {
		ApiResponse apiResponse = new ApiResponse();

		logger.info("acquirerType..={},PaymentType..={}",acquirer,paymentType);
		List<RouterConfiguration> list= routerConfigurationDao.changeStatusActiveSwitch(acquirer,paymentType);
		AcquirerSwitchHistory acquirerSwitchHistory=new AcquirerSwitchHistory();

		acquirerSwitchHistory.setAcquirerName(acquirer);
		acquirerSwitchHistory.setPaymentType(paymentType);
		acquirerSwitchHistory.setUpDateBy(updateby);
		acquirerSwitchHistory.setUpdateOn(getCurrentDate());
		acquirerSwitchHistory.setStatus("ACTIVE");
		acquirerDowntimeSchedulingDao.createAndUpdate(acquirerSwitchHistory);
		
		if(list.size()>0) {
		list.stream().forEach((k) -> {
			
			k.setCurrentlyActive(true);
			k.setAcquirerSwitch(false);
			k.setAcUpdateBy(updateby);
			k.setAcUpdateOn(getCurrentDate());
			routerConfigurationDao.deleteAcquirerMaster(k);
			
		});
		acquirerMasterSwitchNotification( PaymentTypeUI.getpaymentName(paymentType),  acquirer,  updateby, "ACTIVE", getCurrentDate()) ;

		apiResponse.setMessage("Status Change SuccessFull");
		apiResponse.setData(list);
		apiResponse.setStatus(true);
		return apiResponse;
		
		
}else {
	
	apiResponse.setMessage("No Rule is found ");
	apiResponse.setData(list);
	apiResponse.setStatus(true);
	return apiResponse;
	
	
	
}
		
			
		
	}
	
	
	@PostMapping("/acquirerMasterSwitchList")
	public ApiResponse acquirerMasterSwitchList(@RequestParam String acquirer, @RequestParam String paymentType) {
		ApiResponse apiResponse = new ApiResponse();
		List<AcquirerSwitchDto> rulesList = new ArrayList<AcquirerSwitchDto>();

		logger.info("acquirerType..={},PaymentType..={}",acquirer,paymentType);
		List<AcquirerSwitchHistory> list= acquirerDowntimeSchedulingDao.getAcquirerHistory(acquirer,paymentType);
		for(AcquirerSwitchHistory data:list){
			AcquirerSwitchDto acquirerlist=new AcquirerSwitchDto();	
			acquirerlist.setUpDateOn(data.getUpdateOn());
			acquirerlist.setUpDateBy(data.getUpDateBy());
			acquirerlist.setStatus(data.getStatus());
			acquirerlist.setPaymentType( PaymentTypeUI.getpaymentName(data.getPaymentType()));
			acquirerlist.setAcquirer(data.getAcquirerName());
			
		rulesList.add(acquirerlist);	
		}
			
		apiResponse.setMessage("Status Change SuccessFull");
		apiResponse.setData(rulesList);
		apiResponse.setStatus(true);
		return apiResponse;
		
		
	
			
		
	}
	
	
	@PostMapping("/getChangeStatusSchadular")
	public ApiResponse getChangeStatusSchadular() {
		ApiResponse apiResponse = new ApiResponse();
		List<AcquirerSwitchDto> rulesList = new ArrayList<AcquirerSwitchDto>();

	
		List<AcquirerDowntimeScheduling> fromDateList= acquirerDowntimeSchedulingDao.getListFromDate();
		logger.info("getListFromDate"+fromDateList.toString());
	fromDateList.stream().forEach((listData)->{
		
			List<RouterConfiguration> list= routerConfigurationDao.getPayIdByEmailId(listData.getAcquirerName(),listData.getPaymentType());
			SchadularAcquirerDownTimeEmail(listData,"ACTIVE");

			list.stream().forEach((k) -> {
				
				k.setCurrentlyActive(false);
				k.setAcquirerSwitch(true);
			
				routerConfigurationDao.deleteAcquirerMaster(k);
				
			});

			
		});
	
	List<AcquirerDowntimeScheduling> toDateList= acquirerDowntimeSchedulingDao.getListtoDate();
	logger.info("getListFromDate"+toDateList.toString());
	
	toDateList.stream().forEach((listData)->{
		
		List<RouterConfiguration> list= routerConfigurationDao.getSchadularListForInactive(listData.getAcquirerName(),listData.getPaymentType());
		SchadularAcquirerDownTimeEmail(listData,"INACTIVE");

		list.stream().forEach((k) -> {
			
			k.setCurrentlyActive(true);
			k.setAcquirerSwitch(false);
		
			routerConfigurationDao.deleteAcquirerMaster(k);
			
		});

		
	});


		apiResponse.setMessage("Status Change SuccessFull");
		apiResponse.setData(fromDateList);
		apiResponse.setStatus(true);
		return apiResponse;
		
		
	
			
		
	}
	
	
	public void acquirerMasterSwitchNotification(String payment, String acquirer, String updateBy,String status,String updateOn) {

		String responseBody = "";
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			logger.info("send failedTxnsNotification");
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailAcquirerMasterSwitchNotificationURL");

			JSONObject json = new JSONObject();
			json.put("PAYMENTTYPE", payment);
			json.put("ACQUIRER", acquirer);
			json.put("UPDATEBY", updateBy);
			json.put("STATUS", status);
			json.put("UPDATEDON", updateOn);
			

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);

		} catch (Exception exception) {
			logger.error("error" + exception);
		}
	}
		
		
		public void createAcquirerDownTimeEmail(AcquirerDowntimeScheduling createRule) {

			String responseBody = "";
			Map<String, String> resMap = new HashMap<String, String>();
			try {
				logger.info("send failedTxnsNotification");
				String serviceUrl = PropertiesManager.propertiesMap.get("EmailAcquirerMasterSwitchCreateNotificationURL");

			

				JSONObject json = new JSONObject();
				json.put("PAYMENTTYPE",  PaymentTypeUI.getpaymentName(createRule.getPaymentType()));
				json.put("ACQUIRER", createRule.getAcquirerName());
				json.put("UPDATEBY", createRule.getCreatedBy());
				json.put("STATUS", createRule.getStatus());
				json.put("UPDATEDON", createRule.getCreatedOn());
				json.put("FROMDATE", createRule.getFromDate());
				json.put("TODATE", createRule.getToDate());

				

				CloseableHttpClient httpClient = HttpClientBuilder.create().build();
				HttpPost request = new HttpPost(serviceUrl);
				StringEntity params = new StringEntity(json.toString());
				request.addHeader("Content-type", "application/json");
				request.setEntity(params);
				CloseableHttpResponse resp = httpClient.execute(request);
				responseBody = EntityUtils.toString(resp.getEntity());
				final ObjectMapper mapper = new ObjectMapper();
				final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
				resMap = mapper.readValue(responseBody, type);

			} catch (Exception exception) {
				logger.error("error" + exception);
			}
		}
			public void SchadularAcquirerDownTimeEmail(AcquirerDowntimeScheduling createRule,String status) {

				String responseBody = "";
				Map<String, String> resMap = new HashMap<String, String>();
				try {
					logger.info("send failedTxnsNotification");
					String serviceUrl = PropertiesManager.propertiesMap.get("EmailAcquirerMasterSwitchSchadularNotificationURL");

				

					JSONObject json = new JSONObject();
					json.put("PAYMENTTYPE",  PaymentTypeUI.getpaymentName(createRule.getPaymentType()));
					json.put("ACQUIRER", createRule.getAcquirerName());
					json.put("UPDATEBY", createRule.getCreatedBy());
					json.put("STATUS", status);
					json.put("UPDATEDON", createRule.getCreatedOn());
					json.put("FROMDATE", createRule.getFromDate());
					json.put("TODATE", createRule.getToDate());

					

					CloseableHttpClient httpClient = HttpClientBuilder.create().build();
					HttpPost request = new HttpPost(serviceUrl);
					StringEntity params = new StringEntity(json.toString());
					request.addHeader("Content-type", "application/json");
					request.setEntity(params);
					CloseableHttpResponse resp = httpClient.execute(request);
					responseBody = EntityUtils.toString(resp.getEntity());
					final ObjectMapper mapper = new ObjectMapper();
					final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
					resMap = mapper.readValue(responseBody, type);

				} catch (Exception exception) {
					logger.error("error" + exception);
				}

	}
	
			

}
