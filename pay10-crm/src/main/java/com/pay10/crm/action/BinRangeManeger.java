package com.pay10.crm.action;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.BinRangeDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.util.BinRange;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.actionBeans.BatchResponseObject;
import com.pay10.crm.actionBeans.CommanCsvReader;

public class BinRangeManeger extends AbstractSecureAction {

	@Autowired
	private BinRangeDao binRangeDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private AuditTrailService auditTrailService;

	private static final long serialVersionUID = -4486785339968262228L;
	private static Logger logger = LoggerFactory.getLogger(BinRangeManeger.class.getName());
	private String fileName;
	private String response;
	private List<Merchants> merchantList = new LinkedList<Merchants>();
	private Map<String, String> currencyMap = new HashMap<String, String>();

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		BatchResponseObject batchResponseObject = new BatchResponseObject();
		CommanCsvReader commanCsvReader = new CommanCsvReader();
		try {
			// batchFile read line by line
			batchResponseObject = commanCsvReader.csvReaderForBinRange(fileName);
			if (batchResponseObject.getBinRangeResponseList().isEmpty()) {
				addActionMessage(ErrorType.INVALID_FIELD.getResponseMessage());
			} else {
				List<BinRange> binListObj = batchResponseObject.getBinRangeResponseList();
				response = binRangeDao.insertAll(binListObj);
				Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
						.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
				AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(binListObj), null,
						actionMessagesByAction.get("binRangeManeger"));
				auditTrailService.saveAudit(request, auditTrail);
			}
			if (!(StringUtils.isBlank(response))) {
//				setResponse((CrmFieldConstants.PROCESS_INITIATED_SUCCESSFULLY.getValue()));
				if (response.contains("Request processed successfully")) {
					addActionMessage("Request processed successfully");
				} else {
					addActionMessage("Sorry!! You can not proceed as Bin already exists");
				}

			}

		} catch (Exception exception) {
			logger.error("Error while processing binRange:" + exception);
			addActionMessage("Error while processing Binranges:" + exception);
		}
		return INPUT;
	}
	/*
	 * public void validate(){ if ((validator.validateBlankField(getFileName()))) {
	 * addFieldError(CrmFieldType.FIRSTNAME.getName(),
	 * validator.getResonseObject().getResponseMessage()); } else if
	 * (!(validator.validateField(CrmFieldType.FIRSTNAME, getFileName()))) {
	 * addFieldError(CrmFieldType.FIRSTNAME.getName(),
	 * validator.getResonseObject().getResponseMessage()); }
	 * 
	 * if ((validator.validateBlankField(getResponse()))) {
	 * addFieldError(CrmFieldType.RESPONSE.getName(),
	 * validator.getResonseObject().getResponseMessage()); } else if
	 * (!(validator.validateField(CrmFieldType.RESPONSE, getResponse()))) {
	 * addFieldError(CrmFieldType.RESPONSE.getName(),
	 * validator.getResonseObject().getResponseMessage()); } }
	 */

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

}
