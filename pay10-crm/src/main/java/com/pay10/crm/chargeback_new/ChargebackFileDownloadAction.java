package com.pay10.crm.chargeback_new;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Chargeback;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.chargeback_new.action.beans.ChargebackDao;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class ChargebackFileDownloadAction extends AbstractSecureAction  {

	@Autowired
	ChargebackDao cbDao;
	
	private String cbId;
	private static final long serialVersionUID = 2945243500024800923L;
	private String fileName;
	private File file;
	byte fileContent[];
	
	private InputStream fileInputStream;
	
	public InputStream getFileInputStream() {
		return fileInputStream;
	}
	
	@Override
	public String execute() {
	    try {
			String filePath = PropertiesManager.propertiesMap.get(Constants.CHARGEBACK_DOC_UPLOAD_PATH.getValue());
			
			// get old chargeback...
			Chargeback oldCb = cbDao.findById(getCbId());
			filePath += oldCb.getPayId() + "/" + getCbId() + "/";
			File file = new File(filePath + getFileName());
			if(!file.exists()) {
				System.out.println("File doesn't exists.");
				System.out.println(file.getAbsolutePath());
				return SUCCESS;
			}
			
			fileInputStream = new FileInputStream(file);
			// Close the stream.
		} catch (FileNotFoundException e) {
			System.out.println("Error in Chargeback File Download");
			e.printStackTrace();
		}
	    return SUCCESS;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getFile() {
		return file;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public String getCbId() {
		return cbId;
	}

	public void setCbId(String cbId) {
		this.cbId = cbId;
	}

	@Override
	public String toString() {
		return "ChargebackFileDownloadAction [fileName=" + fileName + "]";
	}
}
