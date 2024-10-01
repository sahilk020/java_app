package com.pay10.notification.sms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.commons.user.ResponseObject;
import com.pay10.notification.sms.sendSms.SmsSenderApi;
import com.pay10.notification.sms.smsCreater.GenerateInvoiceNumberService;
import com.pay10.notification.sms.smsCreater.SendPerformanceSmsServiceSMS;
import com.pay10.notification.sms.smsCreater.SmsRequestObject;
import com.pay10.notification.sms.smsCreater.UpdateCaptureDataService;
import com.pay10.notification.sms.smsCreater.UpdateSettlementDataService;

import java.util.Map;

@RestController
public class SmsSenderController {
	
	@Autowired
	private SmsSenderApi smsSender;
	
	@Autowired
	private SendPerformanceSmsServiceSMS sendPerformanceSmsService;
	
	@Autowired
	private UpdateCaptureDataService updateCaptureDataService;
	
	@Autowired
	private UpdateSettlementDataService updateSettlementDataService;
	
	@Autowired
	private GenerateInvoiceNumberService generateInvoiceNumberService;
	
	private static Logger logger = LoggerFactory.getLogger(SmsSenderController.class.getName());
	
	@RequestMapping(method = RequestMethod.POST,value = "/sendSMS")
	public String  sendSms(@RequestBody ResponseObject responseObject) throws Exception{
		smsSender.sendSMS(responseObject.getEmail(), responseObject.getResponseMessage());
		logger.info("SMS sent");
		return "success";
	}

	@RequestMapping(method = RequestMethod.POST,value = "/sendMonitoringSMS")
	public String  sendMonitoringSms(@RequestBody Map<String, String> reqmap) throws Exception{
		smsSender.sendSMS(reqmap.get("mobileNo"), reqmap.get("message"));
		logger.info("SMS sent");
		return "success";
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/sendDailySMS")
	public String  sendDailySms(@RequestBody ResponseObject responseObject) throws Exception{
		
		sendPerformanceSmsService.sendSms(responseObject.getEmail(), responseObject.getResponseMessage());
		logger.info("SMS sent");
		return "success";
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/checkTxnSms")
	public String  checkTransactionSMS(@RequestBody SmsRequestObject responseObject) throws Exception{
		sendPerformanceSmsService.checkLastTxn(responseObject.getPayId(),responseObject.getDuration());
		return "success";
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/updateCaptureData")
	public String  updateCaptureDataService(@RequestBody SmsRequestObject responseObject) throws Exception{
		
		logger.info("Updating capture data for date "+ responseObject.getFromDate() +" to "+responseObject.getToDate());    
		updateCaptureDataService.updateData(responseObject.getFromDate(),responseObject.getToDate());
		return "success";
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/updatePostSettledData")
	public String  updatePostSettledService(@RequestBody SmsRequestObject responseObject) throws Exception{
		
		logger.info("Updating update Post Settled Capture Data for date "+ responseObject.getFromDate() +" to "+responseObject.getToDate());        
		updateCaptureDataService.updateSettlementData(responseObject.getFromDate(),responseObject.getToDate());
		return "success";
	}
	
	
	@RequestMapping(method = RequestMethod.POST,value = "/udpateSettlement")
	public String  udpateSettlementDate(@RequestBody SmsRequestObject responseObject) throws Exception{
		
	    logger.info("Udpate Settlement date for captured transactions , Start date = " + responseObject.getFromDate() +" End Date = "+responseObject.getToDate()  );    
	    updateSettlementDataService.updateSettledData(responseObject.getFromDate(),responseObject.getToDate());
	  
		return "success";
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/findDuplicate")
	public String  findDuplicateOrderId(@RequestBody SmsRequestObject responseObject) throws Exception{
		
	    logger.info("findDuplicate , Start date = " + responseObject.getFromDate() +" End Date = "+responseObject.getToDate()  );    
		updateCaptureDataService.findDuplicates(responseObject.getFromDate(),responseObject.getToDate());
		return "success";
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/addDateIndex")
	public String  addDateIndex(@RequestBody SmsRequestObject responseObject) throws Exception{
		
	    logger.info("Udpate date index for All transactions , Start date = " + responseObject.getFromDate() +" End Date = "+responseObject.getToDate()  );    
	    updateSettlementDataService.updateDateIndex(responseObject.getFromDate(),responseObject.getToDate(),responseObject.getMessage());
	  
		return "success";
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/fixDateIndexForNull")
	public String  fixDateIndexForNull(@RequestBody SmsRequestObject responseObject) throws Exception{
		
	    logger.info("Fix date index for All transactions will DATE_INDEX = null, Start date = " + responseObject.getFromDate() +" End Date = "+responseObject.getToDate()  );    
	    updateSettlementDataService.fixDateIndexForNull(responseObject.getFromDate(),responseObject.getToDate(),responseObject.getMessage());
	  
		return "success";
	}
	
	
	@RequestMapping(method = RequestMethod.POST,value = "/addPgDateTimeIndex")
	public String  addPgDateTimeIndex(@RequestBody SmsRequestObject responseObject) throws Exception{
	    logger.info("Udpate date index for All transactions , Start date = " + responseObject.getFromDate() +" End Date = "+responseObject.getToDate()  );    
	    updateSettlementDataService.updatePgDateTimeIndexNew(responseObject.getFromDate(),responseObject.getToDate(),responseObject.getMessage());
	  
		return "success";
	}
	
	
	@RequestMapping(method = RequestMethod.POST,value = "/addCaptureDateIndex")
	public String  addCaptureDateIndex(@RequestBody SmsRequestObject responseObject) throws Exception{
		
	    logger.info("Udpate date index for All transactions , Start date = " + responseObject.getFromDate() +" End Date = "+responseObject.getToDate()  );    
	    updateSettlementDataService.updateCaptureDateIndex(responseObject.getFromDate(),responseObject.getToDate(),responseObject.getMessage());
	  
		return "success";
	}
	
	
	@RequestMapping(method = RequestMethod.POST,value = "/updateACQIdMpgs")
	public String  updateAcqIdMPGS(@RequestBody SmsRequestObject responseObject) throws Exception{
		
	    logger.info("Udpate ACQ ID for MPGS , Start date = " + responseObject.getFromDate() +" End Date = "+responseObject.getToDate());    
	    updateSettlementDataService.updateAcqIdMPGS(responseObject.getFromDate(),responseObject.getToDate(),responseObject.getMessage());
	  
		return "success";
	}
	

	@RequestMapping(method = RequestMethod.POST,value = "/dashboardPopulator")
	public String  dashboardPopulatorService(@RequestBody SmsRequestObject responseObject) throws Exception{
		
	    logger.info("Udpate Dashboard for start date  = " + responseObject.getFromDate() +" End Date = "+responseObject.getToDate());    
	    updateSettlementDataService.dashboardPopulator(responseObject.getFromDate(),responseObject.getToDate());
		return "success";
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/insertSettlement")
	public String  insertSettlementData(@RequestBody SmsRequestObject responseObject) throws Exception{
		
	    logger.info("Insert Settlement data from main collection to settlement collection using PG_REF_NUM , Start date = " + responseObject.getFromDate() +" End Date = "+responseObject.getToDate()  );    
	    String pgRefNum = "4722990626232439,6903290626142444,6253290626232208,9686090626143727,4726090626145313,7522990626162454,9133290626193450,7303290626202904,7323290626165721,7026090626135712,4072990626181342,3773290626181540,6062990626154849,8852990626155831,9312990626165742,3376090626212533,1793290626165239,8986090626142400,7966090626221433,4783290626175348,1626090626222855,5153290626231127,9693290626134828,5486090626202622,2646090626141309,3116090626225750,2066090626174245,2736090626225016,1913290626191312,8866090626151721,2462990626151136,6433290626151736,3036090626185800,8786090626212301,1866090626164951,4193290626203317,5732990626211324,6633290626183936,7752990626161814,8322990626181712,3413290626135702,5613290626180957,006090626135306,1683290626194332,8392990626134459,2283290626221909,8233290626140008,3763290626210011,7566090626231900,1166090626145725,7443290626080851,1036090626082653,6026090626083801,5092990626091345,3796090626092751,6256090626093644,7166090626093736,7443290626094756,3966090626100112,1843290626100144,6512990626100247,5912990626110555,9113290626080209,2083290626111100,7843290626132222,8423290626110543,6752990626080606,7126090626080419,7333290626110851,8442990626110133,5433290626110302,1573290626094325,7553290626110407,9786090626091831,1672990626110300,1453290626110241,1882990626110203,9052990626084611,1362990626134017,5553290626133949,5043290626131454,3143290626122035,5553290626120727,4996090626120619,7713290626115954,8046090626115901,9432990626114945,2742990626114556,6562990626114809,2083290626083703,9803290626110238,6212990626103452,4346090626080622,4372990626114700,7366090626110811,4553290626114400,4593290626120657,4676090626112939,1966090626110528,8156090626112706,8976090626110952,9936090626110836,7436090626110812,7632990626105028,3142990626110359,1026090626133456,6182990626110322,7122990626080335,9042990626113955,3756090626110320,4102990626110311,1286090626110306,7963290626110227,1406090626102256,5902990626102025,9563290626071817,3743290626072449,3546090626073646,3386090626075040,2186090626080056,9003290626080100,7642990626080127,3732990626073135,2246090626062606,9006090626032008,1323290626065914,7812990626073923";
	    String[] strArr = pgRefNum.split(",");
	    int cnt =0;
	    for(String str : strArr) {
	    	cnt++;
	    	logger.info("Count : " + String.valueOf(cnt) + " With PG_REF_NUM : " + str);
	    	updateSettlementDataService.insertSettledData(responseObject.getFromDate(),responseObject.getToDate(),str);
	    }
	    
		return "success";
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/generateInvoiceNumber")
	public String  generateInvoiceNumber(@RequestBody SmsRequestObject responseObject) throws Exception{
	    logger.info("Udpate date index for All transactions , Start date = " + responseObject.getFromDate() +" End Date = "+responseObject.getToDate()  );    
	    generateInvoiceNumberService.generateInvoiceNumber(responseObject.getFromDate(),responseObject.getToDate(),responseObject.getMessage());	  
		return "success";
	}
	
}
