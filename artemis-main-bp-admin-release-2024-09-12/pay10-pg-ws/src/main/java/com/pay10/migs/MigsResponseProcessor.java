package com.pay10.migs;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;

@Service
public class MigsResponseProcessor {
	private static Logger logger = LoggerFactory.getLogger(MigsResponseProcessor.class.getName());


	@Autowired
	@Qualifier("resProcessor")
	private MigsAxisResponseProcessor responseProcessor;

	public Map<String, String> processMigsResponse(Fields fields) throws SystemException {
		logger.info("Generating ResponseProcessor enquiry transaction for Migs.");
		
		
		responseProcessor.preProcess(fields);
		if (fields.isValid()) {
			responseProcessor.process(fields);
		}
		responseProcessor.postProcess(fields);
		return fields.getFields();

	}
}
