package com.pay10.crm.chargeback_new;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.MagicNumberFileFilter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Chargeback;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.ExecutableSignatures;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.action.MerchantOnboardUploadFile;
import com.pay10.crm.chargeback_new.action.beans.ChargebackDao;

/**
 * 
 * @author shubhamchauhan
 *
 */

public class ChargebackFileUploadAction extends AbstractSecureAction {

	private static final long serialVersionUID = 5502680679246346305L;
	private static Logger logger = LoggerFactory.getLogger(ChargebackFileUploadAction.class.getName());

	private File fileUpload;
	private String fileUploadContentType;
	private String fileUploadFileName;
	
	private String username;
	private String usertype;
	private String message;
	private String timestamp;
	private String cbId;
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
		
		if(!isValidFileType()) {
			logger.error("File not supported");
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
			setResponseMessage("Invalid file extension.");
			return SUCCESS;
		}
		
		Chargeback oldChargeback  = cbDao.findById(getCbId());
		if(oldChargeback == null) {
			logger.error("Unable to load previous chargeback.");
			return SUCCESS;
		}
		byte[] oldChat = oldChargeback.getChargebackChat();
		
		filePath += oldChargeback.getPayId() + "/" + getCbId() + "/";
		
		// Check whether folder exists.
		File destFile = new File(filePath);
		if(!destFile.exists()) {
			destFile.mkdirs();
		}
		
		long fileSize = getFileUpload().length();
		
		String fileName = oldChargeback.getPayId() + "_" + tempName + "_" +  (new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date())) + extension;
		JSONArray jsArray = new JSONArray((new String(oldChat)));
		String msgObj = "{\"usertype\" : \""+ getUsertype() +"\", \"message\" : \""+ getMessage() +"\", \"username\" : \""+ getUsername() +"\", \"timestamp\" : \"" +getTimestamp()+ "\" , \"filename\" : \"" + getFileUploadFileName() + "\", \"completefilename\" : \"" + fileName + "\", \"filesize\" : " + fileSize + "	}";
		JSONObject jsObject = new JSONObject(msgObj);
		
		if(getFileSize(jsArray, jsObject) != 200) {
			logger.error("File size or count exceeded.");
			setResponseMessage("File size or number of files exceeded.");
			return SUCCESS;
		}
		
		destFile = new File(filePath + fileName);
		destFile.setExecutable(false);
		destFile.setWritable(false);
		try {
			FileUtils.copyFile(getFileUpload(), destFile);
			jsArray.put(jsObject);
			oldChargeback.setChargebackChat(jsArray.toString().getBytes());
			cbDao.update(oldChargeback);
		} catch (IOException e) {
			logger.error("Unable to copy file.");
			setResponseMessage("Unknown error occured.");
			e.printStackTrace();
			return SUCCESS;
		}
		setResponseMessage("File uploaded successfully.");
		return SUCCESS;
	}
	
	private static int getFileSize(JSONArray jsArray, JSONObject jsObject) {
		JSONObject tempObj ;
		String fileName;
		Long fileSize;
		Long totalFileSize = 0L;
		int count = 0;
		for(int i = 0; i < jsArray.length(); ++i) {
			tempObj = (JSONObject) jsArray.get(i);
			if(tempObj.has("filename") && tempObj.has("filesize")) {
				 fileName = tempObj.getString("filename");
				 if(fileName != null && !(fileName.equalsIgnoreCase(""))) {
					 ++count;
					 fileSize = tempObj.getLong("filesize");
					 totalFileSize += fileSize;
				 }
			}
		}
		if(jsObject.has("filename") && jsObject.has("filesize")) {
			fileName = jsObject.getString("filename");
			if(fileName != null && !(fileName.equalsIgnoreCase(""))) {
				 ++count;
				 fileSize = jsObject.getLong("filesize");
				 totalFileSize += fileSize;
			 }
		}
		
		// "If File size limit or count exceed.";
		if((totalFileSize / (1024 * 1024)) > 10 || count > 10) {
			return 400;
		}
		return 200;
	}
	
	public boolean isValidFileType() {
		MagicNumberFileFilter mnff = null;
		File file = getFileUpload();

		for (ExecutableSignatures es : EnumSet.allOf(ExecutableSignatures.class)) {
			mnff = new MagicNumberFileFilter(es.getMagicNumbers(), es.getOffset());
			if (mnff.accept(file)) {
				// Can create these variables locally.
				this.magicNumber = es.getMagicNumbers();
				this.executableDescription = es.getDescription();
				return true;
			}
		}
		return false;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getCbId() {
		return cbId;
	}

	public void setCbId(String cbId) {
		this.cbId = cbId;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public String toString() {
		return "ChargebackFileUploadAction [fileUpload=" + fileUpload + ", fileUploadContentType="
				+ fileUploadContentType + ", fileUploadFileName=" + fileUploadFileName + ", username=" + username
				+ ", usertype=" + usertype + ", message=" + message + ", timestamp=" + timestamp + "]";
	}


}
