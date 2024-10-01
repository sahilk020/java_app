package com.crmws.service;

import org.springframework.http.ResponseEntity;

import com.crmws.entity.ResponseMessageExceptionList;
import com.pay10.commons.dto.PoFRM;

public interface PayoutService {

	public ResponseEntity<ResponseMessageExceptionList> saveFrm(PoFRM frm);
	
}
