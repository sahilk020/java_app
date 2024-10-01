package com.pay10.pg.action.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.pg.action.RequestAction;

@Service
public class RequestUrlValidator {

	@Autowired
	private StaticDataProvider staticDataProvider;
	
	private static Logger logger = LoggerFactory.getLogger(RequestUrlValidator.class.getName());

	
	public void validateRequestUrl(Fields fields, HttpServletRequest req) throws SystemException {
		User user = staticDataProvider.getUserData(fields.get(FieldType.PAY_ID.getName()));
		logger.info("req :"+req);
		String requestUrl = getRequestUrl(req);
		logger.info("requestUrl :"+requestUrl);
		//If url different than base URL
		if(!requestUrl.contains(ConfigurationConstants.DOMAIN_NAME.getValue())) {
			//If white label is enabled
			if ((user.isEnableWhiteLabelUrl()) && (requestUrl.contains(user.getWhiteLabelUrl()))) {
					fields.put(FieldType.INTERNAL_USE_WHITE_LABEL_URL.getName(), (Constants.Y.getValue()));
				}else {
					fields.put(FieldType.PG_TXN_MESSAGE.getName(), "User Not allowed to use this request URL");

					throw new SystemException(ErrorType.INVALID_REQUEST_URL, "User Not allowed to use this request URL");
				}
		}		
	}
	
	
	public String getRequestUrl( HttpServletRequest req){
	       final String scheme = req.getScheme();
	       final int port = req.getServerPort();
	       final StringBuilder url = new StringBuilder(256);
	       url.append(scheme);
	       url.append("://");
	       url.append(req.getServerName());
	       if(!(("http".equals(scheme) && (port == 0 || port == 80))
	               || ("https".equals(scheme) && port == 443))){
	           url.append(':');
	           url.append(port);
	       }
	       url.append(req.getRequestURI());
	       final String qs = req.getQueryString();
	       if(qs != null){
	           url.append('?');
	           url.append(qs);
	       }
	       logger.info("URL of incoming request   " + url);
	       final String result = url.toString();
	       return result;
	   }

}
