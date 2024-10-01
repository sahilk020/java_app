package com.pay10.crm.chargeback_new;

import javax.persistence.Lob;

import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Chargeback;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.chargeback_new.action.beans.ChargebackDao;
import com.pay10.crm.chargeback_new.util.ChargebackUtilities;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class ChargebackAddChatMessageAction extends AbstractSecureAction {

	private static final long serialVersionUID = -2907386520837954247L;

	@Autowired
	ChargebackDao cbDao;
	
	private String cbId;
	private String message;
	private int responseCode;
	
	@Lob
	private byte[] cbFile;
	private String cbFileFileName;
	private String user;
	private String cbFileContentType;
	private String cbChat;
	
	@Override
	public String execute() {
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		if(sessionUser.getUserType().equals(UserType.ADMIN)) {
			setUser(sessionUser.getUserType().toString().toLowerCase());
		}
		else if(sessionUser.getUserType().equals(UserType.MERCHANT)) {
			setUser(sessionUser.getUserType().toString().toLowerCase());
		}
		System.out.println(this);
		Chargeback oldChargeback = cbDao.findById(getCbId());
		byte[] oldChat = oldChargeback.getChargebackChat();
		if(oldChargeback != null) {
			JSONArray jsArray = new JSONArray((new String(oldChat)));
			JSONObject jsObject = new JSONObject(getMessage());
//			jsObject.put("message", ChargebackUtilities.encodeHtmlToString(jsObject.getString("message")));
			jsObject.put("message", jsObject.getString("message"));
			jsObject.put("filename", "");
			jsObject.put("completefilename", "");
			jsObject.put("filesize", "");
			jsArray.put(jsObject);
			oldChargeback.setChargebackChat(jsArray.toString().getBytes());
			cbDao.update(oldChargeback);
		}
		responseCode = 200;
		return SUCCESS;
	}
	
	public String getChargebackChat() {
		Chargeback oldChargeback = cbDao.findById(getCbId());
		if(oldChargeback == null) {
			System.out.println("unable to get chargeback chat.");
			setCbChat(null);
			return SUCCESS;
		}
		String chat = new String(oldChargeback.getChargebackChat());
		JSONArray jsArray = new JSONArray(chat);
		for(int i = 0; i < jsArray.length(); ++i) {
			JSONObject jsObj = jsArray.getJSONObject(i);
			jsObj.put("message", ChargebackUtilities.encodeHtmlToString(jsObj.getString("message")));
		}
		
		setCbChat(jsArray.toString());
		return SUCCESS;
	}
	
	@Override
	public void validate() {
	}

	public String getCbId() {
		return cbId;
	}

	public void setCbId(String cbId) {
		this.cbId = cbId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		System.out.println("Message : " + message);
		this.message = message;
	}


	public byte[] getCbFile() {
		return cbFile;
	}

	public void setCbFile(byte[] cbFile) {
		System.out.println("CB File : " + cbFile);
		this.cbFile = cbFile;
	}

	@Override
	public String toString() {
		return "SHChargebackAddChatMessageAction [cbId=" + cbId + ", message=" + message + ", cbFile=" + cbFile + "]";
	}

	public String getCbFileFileName() {
		return cbFileFileName;
	}

	public void setCbFileFileName(String cbFileFileName) {
		this.cbFileFileName = cbFileFileName;
	}

	public String getCbFileContentType() {
		return cbFileContentType;
	}

	public void setCbFileContentType(String cbFileContentType) {
		this.cbFileContentType = cbFileContentType;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setCbChat(String cbChat) {
		this.cbChat = cbChat;
	}

	public String getCbChat() {
		return cbChat;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	
}
