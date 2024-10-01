package com.crmws.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dao.SuccessRatioDao;
import com.crmws.dto.AcquirerSuccessRationDto;
import com.crmws.dto.AcquirerSwitchDto;
import com.crmws.dto.ApiResponse;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.user.AcquirerDownTimeConfiguration;
import com.pay10.commons.util.Acquirer;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.AcquirerTypeUI;

@RestController
@RequestMapping("/AcquirerSuccess")
public class AcquirerSuccessRatioController {
	private static final Logger logger = LoggerFactory.getLogger(AcquirerSuccessRatioController.class.getName());

	
	@Autowired
	FieldsDao fieldsDao;
	
	@Autowired
	SuccessRatioDao successRatioDao;
	
	
	@PostMapping("/Ratio")
	public ApiResponse getAcquirerSuccessData (@RequestParam String dateFrom,@RequestParam String acquirer , @RequestParam String time1) throws ParseException {
		logger.info("Request Received For Add acqDownTimeConfigurationList ");
		String fromTime ,toTime;
		ApiResponse apiResponse = new ApiResponse();
		
		

		
		List<AcquirerSuccessRationDto> acquirerDTList =new ArrayList<AcquirerSuccessRationDto>();
		if(StringUtils.isNotBlank(acquirer)) {
			String[] acq=acquirer.split(",");
			for(int i =0;i<acq.length;i++) {
				
				AcquirerSuccessRationDto data = new AcquirerSuccessRationDto();
				
				
				data.setA1201(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 00:00:00"): dateFrom.concat(" 12:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 00:59:59") : dateFrom.concat(" 12:59:59"), acq[i]));
				data.setA0102(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 01:00:00"): dateFrom.concat(" 13:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 01:59:59") : dateFrom.concat(" 13:59:59"), acq[i]));
				data.setA0203(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 02:00:00"): dateFrom.concat(" 14:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 02:59:59") : dateFrom.concat(" 14:59:59"), acq[i]));
				data.setA0304(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 03:00:00"): dateFrom.concat(" 15:00:00"),time1.equalsIgnoreCase("AM")? dateFrom.concat(" 03:59:59") : dateFrom.concat(" 15:59:59"), acq[i]));
				data.setA0405(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 04:00:00"): dateFrom.concat(" 16:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 04:59:59") : dateFrom.concat(" 16:59:59"), acq[i]));
				data.setA0506(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 05:00:00"): dateFrom.concat(" 17:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 05:59:59") : dateFrom.concat(" 17:59:59"), acq[i]));
				data.setA0607(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 06:00:00"): dateFrom.concat(" 18:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 06:59:59") : dateFrom.concat(" 18:59:59"), acq[i]));
				data.setA0708(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 07:00:00"): dateFrom.concat(" 19:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 07:59:59") : dateFrom.concat(" 19:59:59"), acq[i]));
				data.setA0809(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 08:00:00"): dateFrom.concat(" 20:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 08:59:59") : dateFrom.concat(" 20:59:59"), acq[i]));
				data.setA0910(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 09:00:00"): dateFrom.concat(" 21:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 09:59:59") : dateFrom.concat(" 21:59:59"), acq[i]));
				data.setA1011(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 10:00:00"): dateFrom.concat(" 22:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 10:59:59") : dateFrom.concat(" 22:59:59"), acq[i]));
				data.setA1112(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 11:00:00"): dateFrom.concat(" 23:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 11:59:59") : dateFrom.concat(" 23:59:59"), acq[i]));
				data.setAcquirer(acq[i]);
				acquirerDTList.add(data);

			}
			
			
			
			
			
			
		}else {
			
			AcquirerTypeUI []acquirerTypeUIs=AcquirerTypeUI.values();

			for (AcquirerTypeUI acquirer1 : acquirerTypeUIs) {

		AcquirerSuccessRationDto data = new AcquirerSuccessRationDto();
		
		
		
		
		data.setA1201(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 00:00:00"): dateFrom.concat(" 12:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 00:59:59") : dateFrom.concat(" 12:59:59"), acquirer1.getName()));
		data.setA0102(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 01:00:00"): dateFrom.concat(" 13:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 01:59:59") : dateFrom.concat(" 13:59:59"), acquirer1.getName()));
		data.setA0203(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 02:00:00"): dateFrom.concat(" 14:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 02:59:59") : dateFrom.concat(" 14:59:59"), acquirer1.getName()));
		data.setA0304(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 03:00:00"): dateFrom.concat(" 15:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 03:59:59") : dateFrom.concat(" 15:59:59"), acquirer1.getName()));
		data.setA0405(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 04:00:00"): dateFrom.concat(" 16:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 04:59:59") : dateFrom.concat(" 16:59:59"), acquirer1.getName()));
		data.setA0506(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 05:00:00"): dateFrom.concat(" 17:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 05:59:59") : dateFrom.concat(" 17:59:59"), acquirer1.getName()));
		data.setA0607(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 06:00:00"): dateFrom.concat(" 18:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 06:59:59") : dateFrom.concat(" 18:59:59"), acquirer1.getName()));
		data.setA0708(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 07:00:00"): dateFrom.concat(" 19:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 07:59:59") : dateFrom.concat(" 19:59:59"), acquirer1.getName()));
		data.setA0809(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 08:00:00"): dateFrom.concat(" 20:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 08:59:59") : dateFrom.concat(" 20:59:59"), acquirer1.getName()));
		data.setA0910(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 09:00:00"): dateFrom.concat(" 21:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 09:59:59") : dateFrom.concat(" 21:59:59"), acquirer1.getName()));
		data.setA1011(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 10:00:00"): dateFrom.concat(" 22:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 10:59:59") : dateFrom.concat(" 22:59:59"), acquirer1.getName()));
		data.setA1112(successRatioDao.getSuccessRationData(time1.equalsIgnoreCase("AM")?  dateFrom.concat(" 11:00:00"): dateFrom.concat(" 23:00:00"), time1.equalsIgnoreCase("AM")? dateFrom.concat(" 11:59:59") : dateFrom.concat(" 23:59:59"), acquirer1.getName()));
		data.setAcquirer(acquirer1.getName());

		
		
		acquirerDTList.add(data);
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		


		if (!acquirerDTList.isEmpty()) {
			apiResponse.setMessage("Data Fetch Successfully");
			apiResponse.setData(acquirerDTList);
			apiResponse.setStatus(true);
			return apiResponse;
		} else {
			apiResponse.setMessage("No Data Found");
			apiResponse.setData("");
			apiResponse.setStatus(false);
			return apiResponse;
		}
	}
	
	
	public static void main(String[] args) {
	String	time1="AM";
	System.out.println(	time1=="AM"?  " 00:00:00": " 12:00:00");
	}
	
	
}
