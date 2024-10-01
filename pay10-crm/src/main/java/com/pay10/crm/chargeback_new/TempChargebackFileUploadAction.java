package com.pay10.crm.chargeback_new;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.action.MerchantOnboardUploadFile;
import com.pay10.crm.chargeback_new.action.beans.ChargebackDao;
import com.pay10.crm.chargeback_new.util.ChargebackUtilities;

/**
 * 
 * @author shubhamchauhan
 *
 */

public class TempChargebackFileUploadAction extends AbstractSecureAction {

	private static final long serialVersionUID = -8118865581514260916L;
	private static Logger logger = LoggerFactory.getLogger(TempChargebackFileUploadAction.class.getName());

	
	private File fileUpload;
	private String fileUploadContentType;
	private String fileUploadFileName;
	private String usertype;
	private String username;
	
	private String payId;
	private String timestamp;
	private String cbTempChat;
	private String message;
	private int responseCode;
	private String responseMessage;
	
	@Autowired
	ChargebackDao cbDao;
	
	// Can create these variables locally.
	private byte[] magicNumber;
	private String executableDescription;
	
	@Autowired
	PropertiesManager propertiesManager;

	@Override
	public String execute() {
		String filePath = PropertiesManager.propertiesMap.get(Constants.CHARGEBACK_DOC_UPLOAD_PATH.getValue());
		if(!ChargebackUtilities.isValidFileType(getFileUpload())) {
			logger.error("File not supported.");
			setResponseMessage("File not supported.");
			return SUCCESS;
		}
		// Get extension.
		String docExtensions = PropertiesManager.propertiesMap.get(Constants.CHARGEBACK_DOC_UPLOAD_EXTENSION.getValue()).toString();
		
		List<String> fileTypes = Arrays.asList(docExtensions.split(","));
		
		String extension = getFileUploadFileName().substring(getFileUploadFileName().lastIndexOf('.'), getFileUploadFileName().length());
		String tempName = getFileUploadFileName().substring(0,getFileUploadFileName().lastIndexOf('.'));
		if(!(fileTypes.contains(extension.toLowerCase()))){
			logger.error("Invalid file extension : " + extension);
			setResponseMessage("Invalid File extension.");
			return SUCCESS;
		}
		cbTempChat = ChargebackUtilities.getChargebackTempChat(getPayId(), false);
		String oldChat = cbTempChat;
		
		filePath += getPayId() + "/temp/";
		
		// Check whether folder exists.
		File destFile = new File(filePath);
		if(!destFile.exists()) {
			destFile.mkdirs();
		}
		
		long fileSize = getFileUpload().length();
		String fileName = getPayId() + "_" + tempName + "_" +  (new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date())) + extension;
		JSONArray jsArray = new JSONArray((new String(oldChat)));
		String msgObj = "{\"usertype\" : \""+ getUsertype() +"\", \"message\" : \""+ getMessage() +"\", \"username\" : \""+ getUsername() +"\", \"timestamp\" : \"" +getTimestamp()+ "\" , \"filename\" : \"" + getFileUploadFileName() + "\", \"completefilename\" : \"" + fileName + "\", \"filesize\" : " + fileSize + "	}";
		JSONObject jsObject = new JSONObject(msgObj);
		
		if(ChargebackUtilities.getFileSize(jsArray, jsObject) != 200) {
			logger.error("File size or number of files exceeded.");
			setResponseMessage("File size or number of files exceeded.");
			return SUCCESS;
		}
		destFile = new File(filePath + fileName);
		destFile.setExecutable(false);
		destFile.setWritable(false);
		try {
			FileUtils.copyFile(getFileUpload(), destFile);
			jsArray.put(jsObject);
			String cbChatFileName = "cbChat.txt";
			FileWriter fw = new FileWriter(filePath + cbChatFileName);
			fw.write(jsArray.toString());
			fw.close();
		} catch (IOException e) {
			logger.error("Unable to copy file.");
			setResponseMessage("Unknown error occured.");
			e.printStackTrace();
			return SUCCESS;
		}
		setResponseMessage("Successfully uploaded file.");
		return SUCCESS;
	}
	
	public String getChargebackTempChat() {
		cbTempChat = ChargebackUtilities.getChargebackTempChat(getPayId(), true);
		return SUCCESS;
	}
	
	// Method will clear temp folder with payId.
	public String clearTempFolder() {
		if(getPayId() == null || getPayId().equals("")) {
			logger.error("Invalid payId");
			return SUCCESS;
		}
		String filePath = PropertiesManager.propertiesMap.get(Constants.CHARGEBACK_DOC_UPLOAD_PATH.getValue());
		filePath += getPayId() + "/temp/";
		
		File file = new File(filePath);
		if(file.exists()) {
			ChargebackUtilities.removeDirectory(file);
		}
		return SUCCESS;
	}
	
	public String tempAddMessageToChat() {
		if(getPayId() == null || getPayId().equals("")) {
			logger.error("Invalid PayId");
			return SUCCESS;
		}
		String cbChatFileName = "cbChat.txt";
		String filePath = PropertiesManager.propertiesMap.get(Constants.CHARGEBACK_DOC_UPLOAD_PATH.getValue());
		filePath += getPayId() + "/temp/";
		
		File cbFile = new File(filePath);
		if(!cbFile.exists()) {
			cbFile.mkdirs();
		}
		
		cbFile = new File(filePath + cbChatFileName);
		
		if(!(cbFile.exists())) {
			cbTempChat = "[]";
			try {
				cbFile.createNewFile();
			} catch (IOException e1) {
				logger.error("Exeption while creating temporary chat file.");
				e1.printStackTrace();
				return SUCCESS;
			}
			FileWriter fw;
			try {
				fw = new FileWriter(cbFile);
				fw.write("[]");
				fw.close();
			} catch (IOException e) {
				logger.error("Exception while writing temporary chat file.");
				e.printStackTrace();
				return SUCCESS;
			}
			
		}
		
		String oldChat = "[]";
		try {
			oldChat = new String(Files.readAllBytes(Paths.get(filePath + cbChatFileName)));
		} catch (IOException e1) {
			logger.error("Exception while reading temporary chat file.");
			e1.printStackTrace();
			return SUCCESS;
		}
		JSONArray jsArray = new JSONArray((new String(oldChat)));
		JSONObject jsObject = new JSONObject(getMessage());
		jsObject.put("message", jsObject.getString("message"));
		jsObject.put("filename", "");
		jsObject.put("completefilename", "");
		jsObject.put("filesize", "");
		jsArray.put(jsObject);
		FileWriter fw;
		try {
			fw = new FileWriter(cbFile);
			fw.write(jsArray.toString());
			fw.close();
		} catch (IOException e) {
			logger.error("Exception while writing temporary chat file.");
			e.printStackTrace();
			return SUCCESS;
		}
		responseCode = 200;
		return SUCCESS;
	
	}
	
	public String clearChargebackTempFolder() {
		return "";
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

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getCbTempChat() {
		return cbTempChat;
	}

	public void setCbTempChat(String cbTempChat) {
		this.cbTempChat = cbTempChat;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public String toString() {
		return "TempChargebackFileUploadAction [fileUpload=" + fileUpload + ", fileUploadContentType="
				+ fileUploadContentType + ", fileUploadFileName=" + fileUploadFileName + ", payId=" + payId
				+ ", timestamp=" + timestamp + "]";
	}

}

