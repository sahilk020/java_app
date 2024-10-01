package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.TransactionResponser;

@Service
public class IPayResponseAction extends AbstractSecureAction implements ServletRequestAware {
	
	private static Logger logger = LoggerFactory.getLogger(IPayResponseAction.class.getName());

	private static final long serialVersionUID = 2382298172065463916L;
	
	private HttpServletRequest httpRequest;
	
	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;
	
	@Autowired
	private ResponseCreator responseCreator;
	
	@Autowired
	private TransactionResponser transactionResponser;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		httpRequest = request;
	}
	
	@Override
	public String execute() {
		try {
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> responseMap = new HashMap<String, String>();
		
			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					responseMap.put(entry.getKey().trim(),
							entry.getValue()[0].trim());
				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}
			
			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
		
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
			
			String encrypted = responseMap.get("encdata");
			fields.put(FieldType.IPAY_FINAL_ENC_RESPONSE.getName(), encrypted);
			fields.put(FieldType.TXN_KEY.getName(), (String)sessionMap.get(FieldType.TXN_KEY.getName()));
			fields.put(FieldType.PASSWORD.getName(), (String)sessionMap.get(FieldType.PASSWORD.getName()));
			
			Map<String, String> processedResponse = transactionControllerServiceProvider.transact(fields, Constants.TXN_WS_IPAY_RETURN_URL.getValue());
			Fields response = new Fields(processedResponse);
			responseCreator.create(response);
			transactionResponser.removeInvalidResponseFields(response);
			responseCreator.ResponsePost(response);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Action.NONE;
	}

}
