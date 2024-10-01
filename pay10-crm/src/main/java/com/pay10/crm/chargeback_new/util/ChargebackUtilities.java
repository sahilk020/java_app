package com.pay10.crm.chargeback_new.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumSet;

import org.apache.commons.io.filefilter.MagicNumberFileFilter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.ExecutableSignatures;
import com.pay10.commons.util.PropertiesManager;

public class ChargebackUtilities {
	
	public static String getChargebackTempChat(String payId, boolean encodeHtml) {
		String cbTempChat = "";
		String cbChatFileName = "cbChat.txt";
		String filePath = PropertiesManager.propertiesMap.get(Constants.CHARGEBACK_DOC_UPLOAD_PATH.getValue());
		filePath += payId + "/temp/";
		
		File cbFile = new File(filePath + cbChatFileName);
		if(!(cbFile.exists())) {
			return "[]";
		}
		try {
			cbTempChat = new String(Files.readAllBytes(Paths.get(filePath + cbChatFileName)));
			if(!encodeHtml) {
				return cbTempChat;
			}
			JSONArray jsArray = new JSONArray(cbTempChat);
			for(int i = 0; i < jsArray.length(); ++i) {
				JSONObject jsObj = jsArray.getJSONObject(i);
				jsObj.put("message", encodeHtmlToString(jsObj.getString("message")));
			}
			cbTempChat = jsArray.toString();
		} catch (IOException e) {
			System.out.println("Exception while reading temp chat file.");
			return "[]";
		}
		return cbTempChat;
	}
	
	public static int getFileSize(JSONArray jsArray, JSONObject jsObject) {
		// TODO Auto-generated method stub
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
		
		if((totalFileSize / (1024 * 1024)) > 10 || count > 10) {
			System.out.println("File size : " + (totalFileSize / (1024 * 1024)) + " : " + count);
			return 400; // "File size limit or count exceed.";
		}
		return 200; // "valid";
	}
	
	public static String encodeHtmlToString(String data) {
		return ESAPI.encoder().encodeForHTML(data);
	}
	
	public static boolean isValidFileType(File file) {
		MagicNumberFileFilter mnff = null;

		for (ExecutableSignatures es : EnumSet.allOf(ExecutableSignatures.class)) {
			mnff = new MagicNumberFileFilter(es.getMagicNumbers(), es.getOffset());
			if (mnff.accept(file)) {
				// Can create these variables locally.
//				this.magicNumber = es.getMagicNumbers();
//				this.executableDescription = es.getDescription();
				return true;
			}
		}
		return false;
	}
	
	public static void removeDirectory(File dir) {
	    if (dir.isDirectory()) {
	        File[] files = dir.listFiles();
	        if (files != null && files.length > 0) {
	            for (File aFile : files) {
	                removeDirectory(aFile);
	            }
	        }
	        dir.delete();
	    } else {
	        dir.delete();
	    }
	}

}
