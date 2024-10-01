package com.pay10.crm.fraudPrevention.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.icu.text.SimpleDateFormat;
import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.FraudPreventionObj;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.actionBeans.BatchResponseObject;
import com.pay10.crm.actionBeans.CommanCsvReader;
import com.pay10.crm.chargeback_new.util.ChargebackUtilities;

/**
 * @author Rajendra
 *
 */

public class TenantDocsAddorDeleteAction extends AbstractSecureAction {


	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(TenantDocsAddorDeleteAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;
	
	@Autowired
	private CompanyProfileDao companyProfileDao;
	
	private String emailId;
	private String docCategoryType;
	
	private String fileName;
	private String response;
	
	private String name;
	private String extension;
	
	
	private File actualfile;
	
	
	private CompanyProfile companyProfile = new CompanyProfile();
	
	@Override
	public String execute() {
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN)) {
				companyProfile = companyProfileDao.getCompanyProfileByEmailId(getEmailId());
				
				String filePath = PropertiesManager.propertiesMap.get(Constants.TENANT_DOC_UPLOAD_PATH.getValue());
				String docExtensions = PropertiesManager.propertiesMap.get(Constants.TENANT_DOC_UPLOAD_EXTENSION.getValue()).toString();
				String docExtensionsforPglog = PropertiesManager.propertiesMap.get(Constants.TENANT_PGLOGO_UPLOAD_EXTENSION.getValue()).toString();
				
				File actualfile = new File(fileName);
								
				if(getDocCategoryType().equalsIgnoreCase("PGLOGO")) {							
					List<String> fileTypes = Arrays.asList(docExtensionsforPglog.split(","));
					if(!(fileTypes.contains(extension.toLowerCase()))){
						logger.error("Invalid file extension : " + extension);
						addActionMessage("Invalid File Type");
						setResponse("Invalid File Type");			
						return SUCCESS;
					}
				}else {
					List<String> fileTypes = Arrays.asList(docExtensions.split(","));
					if(!(fileTypes.contains(extension.toLowerCase()))){
						logger.error("Invalid file extension : " + extension);
						addActionMessage("Invalid File Type");
						setResponse("Invalid File Type");			
						return SUCCESS;
					}					
				}
				
				filePath += companyProfile.getTenantId()+"/";
				
				// Check whether folder exists.
				File destFile = new File(filePath);
				if(!destFile.exists()) {
					destFile.mkdirs();
				}

				Date date = new Date();
				String fileNameActual = name+"_"+(new SimpleDateFormat("ddMMyyyyHHmmss").format(date))+extension;
				
				
				destFile = new File(filePath + fileNameActual);
				destFile.setExecutable(false);
				destFile.setWritable(false);
				try {
					FileUtils.copyFile(actualfile, destFile);
					
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Failed to upload file : " + fileName);
					setResponse("Failed to upload file");
					return SUCCESS;
				}
				
				switch(getDocCategoryType()) {
					case "PGLOGO":
						companyProfile.setPgLogo(fileNameActual);
						break;
					case "HEADERTEXTLOGO":
						companyProfile.setHeaderTextLogo(fileNameActual);
						break;
					case "HEADERICONLOGO":
						companyProfile.setHeaderIconLogo(fileNameActual);
						break;
					case "FOOTERLOGO":
						companyProfile.setFooterLogo(fileNameActual);
						break;
					case "LOGOUTLOGO":
						companyProfile.setLogoutLogo(fileNameActual);
						break;
					case "THANKYOULOGO":
						companyProfile.setThankYouLogo(fileNameActual);
						break;
					case "RECEIPTICONLOGO":
						companyProfile.setReceiptIconLogo(fileNameActual);
						break;
					case "RECEIPTTEXTLOGO":
						companyProfile.setReceiptTextLogo(fileNameActual);
						break;
					case "CRMLOGO":
						companyProfile.setCrmLogo(fileNameActual);
						break;						
					case "COMPANYPANIMAGE":
						companyProfile.setCompanyPanImage(fileNameActual);
						break;
					case "COMPANYGSTIMAGE":
						companyProfile.setCompanyGstImage(fileNameActual);
						break;
					case "COMPANYTANIMAGE":
						companyProfile.setCompanyTanImage(fileNameActual);
						break;
					case "COMPANYAOAIMAGE":
						companyProfile.setCompanyAoaImage(fileNameActual);
						break;
				}
				
					companyProfileDao.update(companyProfile);				
					setResponse("Tenant Docs are uploaded Successfully");
					addActionMessage(response);
			}

		} catch (Exception exception) {
			setResponse("Tenant Docs are failed to upload");
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	public String tenantDocsDelete() {
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN)) {
				companyProfile = companyProfileDao.getCompanyProfileByEmailId(getEmailId());
				String fileNameActual = null;
				String filePath = PropertiesManager.propertiesMap.get(Constants.TENANT_DOC_UPLOAD_PATH.getValue());
		
				switch(getDocCategoryType()) {
					case "PGLOGO":
						fileNameActual = companyProfile.getPgLogo();
						companyProfile.setPgLogo("");
						break;
					case "HEADERTEXTLOGO":
						fileNameActual = companyProfile.getHeaderTextLogo();
						companyProfile.setHeaderTextLogo("");
						break;
					case "HEADERICONLOGO":
						fileNameActual = companyProfile.getHeaderIconLogo();
						companyProfile.setHeaderIconLogo("");
						break;
					case "FOOTERLOGO":
						fileNameActual = companyProfile.getFooterLogo();
						companyProfile.setFooterLogo("");
						break;
					case "LOGOUTLOGO":
						fileNameActual = companyProfile.getLogoutLogo();
						companyProfile.setLogoutLogo("");
						break;
					case "THANKYOULOGO":
						fileNameActual = companyProfile.getThankYouLogo();
						companyProfile.setThankYouLogo("");
						break;
					case "RECEIPTICONLOGO":
						fileNameActual = companyProfile.getReceiptIconLogo();
						companyProfile.setReceiptIconLogo("");
						break;
					case "RECEIPTTEXTLOGO":
						fileNameActual = companyProfile.getReceiptTextLogo();
						companyProfile.setReceiptTextLogo("");
						break;
					case "CRMLOGO":
						fileNameActual = companyProfile.getCrmLogo();
						companyProfile.setCrmLogo("");
						break;
						
					case "COMPANYPANIMAGE":
						fileNameActual = companyProfile.getCompanyPanImage();
						companyProfile.setCompanyPanImage("");
						break;
					case "COMPANYGSTIMAGE":
						fileNameActual = companyProfile.getCompanyGstImage();
						companyProfile.setCompanyGstImage("");
						break;
					case "COMPANYTANIMAGE":
						fileNameActual = companyProfile.getCompanyTanImage();
						companyProfile.setCompanyTanImage("");
						break;
					case "COMPANYAOAIMAGE":
						fileNameActual = companyProfile.getCompanyAoaImage();
						companyProfile.setCompanyAoaImage("");
						break;
				}
				
				companyProfileDao.update(companyProfile);
				
				if(!StringUtils.isBlank(fileNameActual)) {
					filePath += companyProfile.getTenantId()+"/";
					
					File destFile = new File(filePath + fileNameActual);
					destFile.setExecutable(false);
					destFile.setWritable(false);
					
					if(destFile.exists()) {
						destFile.deleteOnExit();
					}
					
					setResponse("Tenant Docs are deleted Successfully");					
					addActionMessage(response);
				}else {
					setResponse("Tenant Docs are Not exist !!");					
					addActionMessage(response);
				}

			}

		} catch (Exception exception) {
			setResponse("Tenant Docs are failed to deleted");
			addActionMessage(response);
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	@Override
	public void validate() {

		if (validator.validateBlankField(getEmailId())) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.isValidEmailId(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}

	}


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

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getDocCategoryType() {
		return docCategoryType;
	}

	public void setDocCategoryType(String docCategoryType) {
		this.docCategoryType = docCategoryType;
	}

	public File getActualfile() {
		return actualfile;
	}

	public void setActualfile(File actualfile) {
		this.actualfile = actualfile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}


}
