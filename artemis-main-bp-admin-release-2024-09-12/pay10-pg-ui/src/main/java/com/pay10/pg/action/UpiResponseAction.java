package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.action.beans.SearchTransactionActionBean;
import com.pay10.pg.action.beans.TransactionStatusCheckBean;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.ResponseCreator;

/**
 * @author Shaiwal
 *
 */
public class UpiResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(UpiResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;

	@Autowired
	private SearchTransactionActionBean searchTransactionActionBean;

	@Autowired
	private TransactionStatusCheckBean transactionStatusCheckBean;
	
	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public UpiResponseAction() {
	}

	public String execute() {
		try {
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> requestMap = new HashMap<String, String>();

			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey(), ((String[]) entry.getValue())[0]);

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}

			
			String pgRefNum = requestMap.get(FieldType.PG_REF_NUM.getName());
			
			String returnUrl = requestMap.get(FieldType.RETURN_URL.getName());
			Map<String, String> responseMap = new HashMap<String, String>();
			
			String acquirerType = (String) sessionMap.get(FieldType.ACQUIRER_TYPE.getName());
			Fields sessionFields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
			
			responseMap = searchTransactionActionBean.searchPayment(pgRefNum);

			String currencyCode = responseMap.get(FieldType.CURRENCY_CODE.getName());
			if (responseMap.get(FieldType.AMOUNT.getName()) != null) {
				String amount = responseMap.get(FieldType.AMOUNT.getName());
				responseMap.put(FieldType.AMOUNT.getName(), Amount.formatAmount(amount, currencyCode));
			}
			if (responseMap.get(FieldType.TOTAL_AMOUNT.getName()) != null) {
				String upTotalAmount = responseMap.get(FieldType.TOTAL_AMOUNT.getName());
				responseMap.put(FieldType.TOTAL_AMOUNT.getName(), Amount.formatAmount(upTotalAmount, currencyCode));
			}

			String isMerchantHosted = (String) sessionMap.get(FieldType.IS_MERCHANT_HOSTED.getName());
			
			Fields fields = new Fields(responseMap);
			
			if (StringUtils.isNotBlank(isMerchantHosted) && isMerchantHosted.equalsIgnoreCase("Y")) {
				fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), isMerchantHosted);
			}
			fields.put(FieldType.RETURN_URL.getName(), returnUrl);
			responseMap.put(FieldType.IS_INTERNAL_REQUEST.getName(), "N");
			responseCreator.create(fields);
			responseCreator.ResponsePost(fields);
			sessionMap.invalidate();
			return Action.NONE;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}
}
