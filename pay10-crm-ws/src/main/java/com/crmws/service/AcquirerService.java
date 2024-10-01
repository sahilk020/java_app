package com.crmws.service;

import java.util.List;
import org.json.JSONObject;

import com.crmws.dto.AcquirerDTO;

public interface AcquirerService {

	public List<AcquirerDTO> getAcquirers();

	public List<String> getAcquirerList(String payId);
	
	public List<String> getMappedAcquirerForTdr(String payId);
	
	
}
