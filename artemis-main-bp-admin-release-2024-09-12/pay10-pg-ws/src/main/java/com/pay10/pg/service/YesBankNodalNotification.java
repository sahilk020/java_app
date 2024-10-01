package com.pay10.pg.service;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.YBNNotificationObj;
import com.pay10.commons.util.YesNodalNotificationApiRequestObj;
import com.pay10.commons.util.YesNodalValidationApiRequestObj;
import com.pay10.pg.security.VirtualAccountRequestValidator;
import com.pay10.pg.security.YBNApiAuthenticator;

@RestController
public class YesBankNodalNotification {

	private static Logger logger = LoggerFactory.getLogger(YesBankNodalNotification.class.getName());

	@Autowired
	private VirtualAccountRequestValidator virtualAccountRequestValidator;
	@Autowired
	private YBNApiAuthenticator yBNApiAuthenticator;

	@RequestMapping(method = RequestMethod.POST, value = "/yesbank/creditValidate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String validation(
			@RequestBody YesNodalValidationApiRequestObj yesNodalNotificationApiRequestObj, HttpServletRequest req) {
		YBNNotificationObj yesNodalNotificationApiObj = yesNodalNotificationApiRequestObj.getValidate();
		String beneAccountNumber = yesNodalNotificationApiObj.getBene_account_no();
		String response = "";
		try {
			yBNApiAuthenticator.authenticateToken(req);
			String customerCode = yesNodalNotificationApiObj.getCustomer_code();
			if(!customerCode.equals("DINERO")) {
				throw new SystemException(ErrorType.AUTHENTICATION_UNAVAILABLE, "Invalid customer code " + customerCode);
			}
			switch (beneAccountNumber) {
			case "DINERO83478399462643":
				// bad request 400
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");

			case "DINERO83478399462644":
				// Unauthorized 401
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");

			case "DINERO83478399462645":
				// internal server error 500
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
			case "DINERO83478399462646":
				// pending
				virtualAccountRequestValidator.validateBasicDetails(yesNodalNotificationApiObj);
				response = "{ \"validateResponse\": { \"decision\": \"pending\"	}}";
				break;
			case "DINERO83478399462647":
			case "DINERO83478399462649":
				// pass
				virtualAccountRequestValidator.validateBasicDetails(yesNodalNotificationApiObj);
				response = "{ \"validateResponse\": { \"decision\": \"pass\"}}";
				break;
			case "DINERO83478399462648":
				// reject
				response = "{ \"validateResponse\": { \"decision\": \"reject\",\"reject_reason\": \"invalid request\"	}}";
				break;

			default:
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
			}
		}

		catch (SystemException systemException) {
			// throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal
			// Server Error");
			response = "{ \"validateResponse\": { \"decision\": \"reject\",\"reject_reason\": \"invalid request \"	}}";
		} catch (ResponseStatusException responseStatusException) {
			throw responseStatusException;
		} catch (Exception exception) {
			logger.error("Unknown exception processing request: ", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		return response;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/yesbank/creditNotification", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String notification(
			@RequestBody YesNodalNotificationApiRequestObj yesNodalNotificationApiRequestObj,  HttpServletRequest req) {
		YBNNotificationObj yesNodalNotificationApiObj = yesNodalNotificationApiRequestObj.getNotify();
		String beneAccountNumber = yesNodalNotificationApiObj.getBene_account_no();
		
			
		String response = "";
		try {
			yBNApiAuthenticator.authenticateToken(req);
			String customerCode = yesNodalNotificationApiObj.getCustomer_code();
			if(!customerCode.equals("DINERO")) {
				throw new SystemException(ErrorType.AUTHENTICATION_UNAVAILABLE, "Invalid customer code " + customerCode);
			}
			switch (beneAccountNumber) {
			case "DINERO83478399462643":
				// bad request 400
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");

			case "DINERO83478399462644":
				// Unauthorized 401
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");

			case "DINERO83478399462645":
				// internal server error 500
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");

			case "DINERO83478399462649":
				// pending
				virtualAccountRequestValidator.validateBasicDetails(yesNodalNotificationApiObj);
				if(Integer.parseInt(yesNodalNotificationApiObj.getAttempt_no()) > 4) {
					response = "{ \"validateResponse\": { \"decision\": \"reject\",\"reject_reason\": \"Retry limit exceeded\"	}}";
				}else {
					response = "{ \"notifyResult\": { \"result\": \"retry\"	}}";
				}
				break;
			case "DINERO83478399462647":
				// pass
				virtualAccountRequestValidator.validateBasicDetails(yesNodalNotificationApiObj);
				response = "{ \"notifyResult\": { \"result\": \"ok\"	}}";
				break;

			default:
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
			}
		} catch (SystemException systemException) {
			// throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal
			// Server Error");
			response = "{ \"validateResponse\": { \"decision\": \"reject\",\"reject_reason\": \"invalid request \"	}}";
		} catch (ResponseStatusException responseStatusException) {
			throw responseStatusException;
		} catch (Exception exception) {
			logger.error("Unknown exception processing request: ", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		return response;

	}
}
