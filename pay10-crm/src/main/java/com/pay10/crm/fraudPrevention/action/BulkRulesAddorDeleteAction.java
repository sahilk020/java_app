package com.pay10.crm.fraudPrevention.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.FraudPreventionObj;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.actionBeans.BatchResponseObject;
import com.pay10.crm.actionBeans.CommanCsvReader;

/**
 * @author Rajendra, Jay
 *
 */

public class BulkRulesAddorDeleteAction extends AbstractSecureAction{

	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(BulkRulesAddorDeleteAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	private String payId;
	private String rule;
	private String currency;
	private List<String> ruleIdList = new ArrayList<String>();

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	@Autowired
	private AuditTrailService auditTrailService;

	private String fileName;
	private String response;

	private File file;
	//HttpServletRequest request;
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public String execute() {
	
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.MERCHANT)) {
               logger.info("fileName...={}",fileName);
              logger.info("file...={}",file);
               
             //  StrutsUploadedFile strutsUploadedFile=new StrutsUploadedFile(fileName);
           	//HttpServletRequest request = ServletActionContext.getRequest();
    		//boolean status1 = ServletFileUpload.isMultipartContent(request);
               
				BatchResponseObject batchResponseObject = new BatchResponseObject();
				CommanCsvReader commanCsvReader = new CommanCsvReader();
				// batchFile read line by line
				String createdBy = sessionUser.getEmailId();
				int fileSize = commanCsvReader.fileSizecount(file);
				logger.info("Bulk Upload Started for Currency"+getCurrency());
				
				if (fileSize <= 150) {
					batchResponseObject =commanCsvReader.csvReaderForFraudPrevention(sessionUser, file, payId,
							rule,getCurrency());
					if (batchResponseObject.getFraudPreventionObjList().isEmpty()) {
						addActionMessage(ErrorType.INVALID_FIELD.getResponseMessage());
					} else {
						List<FraudPreventionObj> FraudPreventionListObj = batchResponseObject
								.getFraudPreventionObjList();
						response = fraudPreventionMongoService.insertAll(FraudPreventionListObj, payId, rule, createdBy,
								sessionUser,getCurrency());
						addAuditTrailEntry("bulkRulesAddAction", FraudPreventionListObj);
						if(response.contains("already")) {
							setResponse("Fraud Rules already exist");
						}else {
							setResponse("Fraud Rules are Added Successfully");	
						}
						
						
						addActionMessage(response);
					}

				} else {
					setResponse("Maximum 150 Entries are allowed to upload");
					addActionMessage(response);
					return SUCCESS;
				}
//				if (!(StringUtils.isBlank(response))) {
//					if (response.contains("Fraud Rule added successfully")) {
//						setResponse("Fraud Rules are added successfully.");
//					} else if (response.contains("Fraud rule already exist")) {
//						setResponse("Fraud rules are Already Exists.");
//					} else {
//						setResponse("Invalid CSV file, please try again");
//					}
//
//					addActionMessage(response);
//				} else {
//					setResponse("Invalid CSV file, please try again");
//				}
//				setPayId(payId);
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	private void addAuditTrailEntry(String action, Object payload) throws JsonProcessingException {
		Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
				.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
		AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(payload), null,
				actionMessagesByAction.get(action));
		auditTrailService.saveAudit(request, auditTrail);
	}

	public String deleteBulkRules() {
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.MERCHANT)) {
				// ruleIdList
				FraudPreventionObj fraudPrevention = new FraudPreventionObj();

				if (sessionUser.getUserType().equals(UserType.ADMIN)
						|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
					fraudPrevention.setPayId(sessionUser.getPayId());
				} else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
					fraudPrevention.setPayId(payId);
				}

				fraudPrevention.setPayId(sessionUser.getPayId());
				if (ruleIdList.size() > 0) {
					ruleIdList = Arrays.asList(ruleIdList.get(0).toString().split(","));
				}
				boolean adminAllRules = false;

				if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
					for (int i = 0; i < ruleIdList.size(); i++) {
						String ruleId = ruleIdList.get(i);
						fraudPrevention.setId(ruleId);
						
						fraudPrevention.setUpdatedBy(sessionUser.getEmailId());
						FraudPreventionObj fpDbObj = fraudPreventionMongoService.activefpRulefindById(payId,ruleId,currency);
						if (null != fpDbObj) {
							if (null != fpDbObj.getCreatedBy()
									&& fpDbObj.getCreatedBy().equalsIgnoreCase(sessionUser.getEmailId())
									&& null != fpDbObj.getUpdatedBy()
									&& fpDbObj.getUpdatedBy().equalsIgnoreCase(sessionUser.getEmailId())) {
								fraudPrevention.setPayId(fpDbObj.getPayId());
								fraudPrevention.setCurrency(getCurrency());
								fraudPrevention.setFraudType(fpDbObj.getFraudType());
								fraudPreventionMongoService.update(fraudPrevention);
							} else {
								adminAllRules = true;
							}

						}
					}

					if (adminAllRules) {
						setResponse("This restriction(s) was set from the admin side, you can not delete it");
					} else {
						setResponse("Fraud Rules are deleted Successfully");
					}

				} else {
					for (int i = 0; i < ruleIdList.size(); i++) {
						String ruleId = ruleIdList.get(i);
						logger.info("if user is not merchant then update "+getCurrency());
						FraudPreventionObj fpDbObj = fraudPreventionMongoService.activefpRulefindById(payId,ruleId,currency);
						fraudPrevention.setId(ruleId);
						fraudPrevention.setUpdatedBy(sessionUser.getEmailId());
						fraudPrevention.setPayId(fpDbObj.getPayId());
						fraudPrevention.setCurrency(getCurrency());
						fraudPrevention.setFraudType(fpDbObj.getFraudType());
						fraudPreventionMongoService.update(fraudPrevention);
						addAuditTrailEntry("bulkRulesDeleteAction", fpDbObj);
					}
					setResponse("Fraud Rules are deleted Successfully");
				}
				
				
				
				addActionMessage(response);
				setPayId(payId);
			}

		} catch (Exception exception) {
			setResponse((CrmFieldConstants.PENDING_REQUEST_EXIST.getValue()));
			addActionMessage(response);
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

//	@Override
//	public void validate() {
//		if (!getPayId().equalsIgnoreCase("ALL")) {
//			if (validator.validateBlankField(getPayId())) {
//			} else if (!validator.validateField(CrmFieldType.PAY_ID, getPayId())) {
//				addFieldError(CrmFieldType.PAY_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
//			}
//		}
//
//	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String> getRuleIdList() {
		return ruleIdList;
	}

	public void setRuleIdList(List<String> ruleIdList) {
		this.ruleIdList = ruleIdList;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	//	public void setServletRequest(HttpServletRequest request) {
//		this.request = request;
//	}
//
//	public HttpServletRequest getServletRequest() {
//		return this.request;
//	}

//	public MultipartFile getFile() {
//		return file;
//	}
//
//	public void setFile(MultipartFile file) {
//		this.file = file;
//	}

		
	
}
