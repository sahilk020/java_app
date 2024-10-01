package com.pay10.crm.action;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.BinCountryMapperType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.States;
import com.pay10.crm.actionBeans.CommanCsvReader;
import com.pay10.crm.chargeback_new.util.ChargebackUtilities;

public class MerchantOnboardUploadFile extends AbstractSecureAction{

	private static final long serialVersionUID = 1776920255101097001L;
	private static Logger logger = LoggerFactory.getLogger(MerchantOnboardUploadFile.class.getName());

	// File upload
	private File fileUpload;
	private String fileUploadContentType;
	private String fileUploadFileName;
	private String activePage;
	private String onBoardDocList;
	
	private String organisationtype;
	private String fileNameKey;
	private String fileDescription;
	
	private User user = new User();
	
	private static List<String> orgTypeKeys = new ArrayList<>(Arrays.asList("pvtPubLtdOrg", "parLlpOrg", "proIndOrg", "tseigngoOrg", "freelancersOrg"));
	@Autowired
	UserDao userDao;
	
	private String responseMsg;
	@Override
	public String execute() {
		user = (User) sessionMap.get(Constants.USER.getValue());
		setActivePage("DocumentsUploads");

		String oldDocList = "{}";
		if(user.getOnBoardDocList() != null) {
			oldDocList = new String(user.getOnBoardDocList());
		}
		
		user.setOnBoardDocListString(oldDocList);
		user.setOnBoardDocListString(DataEncoder.encodeUserOnBoardDocList(user.getOnBoardDocList()));
		
		String filePath = PropertiesManager.propertiesMap.get(Constants.MERCHANT_ONBOARD_DOC_UPLOAD_PATH.getValue());
		String docExtensions = PropertiesManager.propertiesMap.get(Constants.MERCHANT_ONBOARD_DOC_UPLOAD_EXTENSION.getValue()).toString();
		
		if(!ChargebackUtilities.isValidFileType(getFileUpload())) {
			logger.error("Invalid File.");
			setResponseMsg("Invalid File");
			return SUCCESS;
		}
		// Get extension.
		
		List<String> fileTypes = Arrays.asList(docExtensions.split(","));
		String extension = getFileUploadFileName().substring(getFileUploadFileName().lastIndexOf('.'), getFileUploadFileName().length());
		if(!(fileTypes.contains(extension.toLowerCase()))){
			logger.error("Invalid file extension : " + extension);
			setResponseMsg("Invalid File Type");
			return SUCCESS;
		}
		String tempName = getFileUploadFileName().substring(0,getFileUploadFileName().lastIndexOf('.'));
		long fileSize = getFileUpload().length();
		// File size : 100KB - 2MB.
		if(!(fileSize >= 100000 && fileSize <= 2100000)) {
			logger.error("Invalid File size.");
			setResponseMsg("Invalid file size.");
			return SUCCESS;
		}
		
		filePath += user.getPayId() + "/docs/";
		
		// Check whether folder exists.
		File destFile = new File(filePath);
		if(!destFile.exists()) {
			destFile.mkdirs();
		}

		Date date = new Date();
		String fileName = user.getBusinessName() + "_" + tempName + "_" +  (new SimpleDateFormat("ddMMyyyyHHmmss").format(date)) + extension;
		
		JSONObject fileDetails = new JSONObject();
		fileDetails.put("filename", getFileUploadFileName());
		fileDetails.put("completefilename", fileName);
		fileDetails.put("description", getFileDescription());
		fileDetails.put("filesize", fileSize);
		fileDetails.put("timestamp", (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a").format(date)));
		
		
		destFile = new File(filePath + fileName);
		destFile.setExecutable(false);
		destFile.setWritable(false);
		try {
			FileUtils.copyFile(getFileUpload(), destFile);
			JSONObject resultJsonObject = new JSONObject(oldDocList);
			JSONObject orgTypeJsonObject = null;
			
			// if organisation type is not present. then add it.
			if(!resultJsonObject.has(getOrganisationtype())) {
				JSONObject fileNameJsonObject = new JSONObject();
				fileNameJsonObject.put(getFileNameKey(), fileDetails); // put file details.
				resultJsonObject.put(getOrganisationtype(), fileNameJsonObject); // add a json array.
			}
			// If org type is present then get it's value and move on bro.
			else if(resultJsonObject.has(getOrganisationtype())) {
				orgTypeJsonObject = resultJsonObject.getJSONObject(getOrganisationtype());
				
				// check whether orgtypejsonobject contains filename key. If not then push new values. 
				if(!orgTypeJsonObject.has(getFileNameKey())) {
					orgTypeJsonObject.put(getFileNameKey(), fileDetails); // put file details.
//					resultJsonObject.put(getOrganisationtype(), orgTypeJsonObject); // add a json array.
				}
				// check whether orgtypejsonobject contains filename key. If present then delete previous file and push new object.
				else if(orgTypeJsonObject.has(getFileNameKey())) {
					JSONObject fileNameJsonObject = orgTypeJsonObject.getJSONObject(getFileNameKey());
					File oldFile = new File(filePath + fileNameJsonObject.getString("completefilename"));
					if(oldFile.exists() && oldFile.delete()) {
						logger.error("Failed to delete previous file : " + oldFile);
					}
					orgTypeJsonObject.put(getFileNameKey(), fileDetails); // put file details.
				}
			}
			user.setOnBoardDocList(resultJsonObject.toString().getBytes());
			user.setOnBoardDocListString(null);
			userDao.update(user);
			user.setOnBoardDocListString(DataEncoder.encodeUserOnBoardDocList(resultJsonObject.toString().getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Failed to upload file : " + getFileUploadFileName());
			setResponseMsg("Failed to upload file");
			return SUCCESS;
		}
		setResponseMsg("Successfully uploaded file");
		return SUCCESS;
	}
	
	// to provide default state
	public String getDefaultState(){
		if(StringUtils.isBlank(user.getState())){
			return States.SELECT_STATE.getName();
		}else{
			return States.getStatesNames().contains(user.getState().toString()) ? user.getState().toString() : States.SELECT_STATE.getName();
		}
	}
	
	// to provide default country
	public String getDefaultCountry() {
		if (StringUtils.isBlank(user.getCountry())) {
			return BinCountryMapperType.INDIA.getName();
		} else {
			return user.getCountry();
		}
	}

	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public File getFileUpload() {
		return fileUpload;
	}


	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}


	public String getFileUploadContentType() {
		return fileUploadContentType;
	}


	public void setFileUploadContentType(String fileUploadContentType) {
		this.fileUploadContentType = fileUploadContentType;
	}


	public String getFileUploadFileName() {
		return fileUploadFileName;
	}


	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}


	public String getActivePage() {
		return activePage;
	}


	public void setActivePage(String activePage) {
		this.activePage = activePage;
	}


	@Override
	public String toString() {
		return "MechantOnboardUploadFile [fileUpload=" + fileUpload + ", fileUploadContentType=" + fileUploadContentType
				+ ", fileUploadFileName=" + fileUploadFileName + ", activePage=" + activePage + ", onBoardDocList="
				+ onBoardDocList + ", organisationtype=" + organisationtype + ", FileNameKey=" + fileNameKey
				+ ", userDao=" + userDao + ", responseMsg=" + responseMsg + "]";
	}


	public String getOnBoardDocList() {
		return onBoardDocList;
	}


	public void setOnBoardDocList(String onBoardDocList) {
		this.onBoardDocList = onBoardDocList;
	}


	public String getResponseMsg() {
		return responseMsg;
	}


	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public String getOrganisationtype() {
		return organisationtype;
	}

	public void setOrganisationtype(String organisationtype) {
		this.organisationtype = organisationtype;
	}

	public String getFileNameKey() {
		return fileNameKey;
	}

	public void setFileNameKey(String fileNameKey) {
		this.fileNameKey = fileNameKey;
	}

	public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

}
