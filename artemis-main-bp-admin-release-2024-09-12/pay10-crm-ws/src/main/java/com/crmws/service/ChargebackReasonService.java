package com.crmws.service;

import java.util.List;

import com.crmws.dto.ChargeBackReasonDTO;
import com.crmws.entity.ResponseMessage;
import com.pay10.commons.entity.ChargebackReasonEntity;

public interface ChargebackReasonService {
	public ResponseMessage saveChargeBackReason(ChargeBackReasonDTO chargeBackReasonDTO);
	public List<ChargeBackReasonDTO> getAllChargebackReasons();
	public ChargebackReasonEntity getcbReasonDescriptionFromcbReasonCode(String cbReasonCode);
	public boolean deleteChargebackReasons(long id);
	
}
