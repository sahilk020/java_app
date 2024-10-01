package com.crmws.worker.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crmws.worker.constant.ParameterDataType;
import com.crmws.worker.dto.Parameter;
import com.crmws.worker.service.BaseWorker;
import com.crmws.worker.service.OrderToMerchantIdService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderToMerchantIdFunction extends BaseWorker<String>{
	
	@Autowired
	private OrderToMerchantIdService orderToMerchantIdService;
	
	@Override
	public String identifierName() {
		// TODO Auto-generated method stub
		return "Order To MerchantId";
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "Function to fetch merchant id from orderid";
	}

	@Override
	public List<Parameter> defineParameters() {
		
		List<Parameter> parameters = new ArrayList<>();
		parameters.add(ParameterStore.MERCHANT_TRACKID);
		parameters.add(ParameterStore.ACQUIRER_TYPE);
		return parameters;
	}
	
	@Override
	protected String process(Map<String, Object> params) {
		
		params.entrySet().forEach(x->{
			log.info("Param :"+x.getKey() +": "+x.getValue());
		});
		
		// TODO Auto-generated method stub
		return orderToMerchantIdService.getMIDfromOrderAndAcq((String)params.get(ParameterStore.MERCHANT_TRACKID.getKey()),
				(String)params.get(ParameterStore.ACQUIRER_TYPE.getKey()));
	}
	
	
	interface ParameterStore {
		
	    Parameter MERCHANT_TRACKID =
	    		Parameter.initialize(
	            "MERCHANT_TRACKID",
	            true,
	            "",
	            ParameterDataType.TEXT);
	    
	    Parameter ACQUIRER_TYPE =
	    		Parameter.initialize(
	            "ACQUIRER_TYPE",
	            true,
	            "",
	            ParameterDataType.TEXT);
	    
	}

}
