package com.crmws.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.crmws.dto.ChargeBackAutoPopulateData;
import com.crmws.dto.DMSdto;
import com.crmws.entity.ResponseMessage;
import com.crmws.exception.DateValidationException;
import com.pay10.commons.repository.DMSEntity;

public interface DMSService {
	public List<String> getPgRefNosByPayId(String payId);

	public ChargeBackAutoPopulateData getByPgRefNo(String pgRefNo);

	public ResponseMessage save(DMSdto dto);

	public List<DMSdto> listAll(String payId);

	public ResponseMessage validation(DMSdto dto);

	public ResponseMessage update(DMSdto dto, long id);

	public DMSdto getDMSdtoById(long id);

	public List<DMSEntity> getAllEntity();

	public String save(MultipartFile file) throws DateValidationException;
	
	public ResponseMessage accept(DMSdto dto, long id);
	public ResponseMessage close(DMSdto dto, long id);
	public List<DMSdto> listChargebackStatus(String payId);
	
	
	public ResponseMessage updateCbFavourMerchant(long id);
	public ResponseMessage updateCbFavourBankAcquirer(long id);
	public String chargebackStatusBulkUpload(MultipartFile  formData);

//public ResponseMessage updateCbDebit(long id);
//	public ResponseMessage updateCbCredit(long id);

}
