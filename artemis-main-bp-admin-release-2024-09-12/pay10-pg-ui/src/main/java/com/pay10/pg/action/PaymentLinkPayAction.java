package com.pay10.pg.action;

import java.io.PrintWriter;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.PaymentLink;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.pg.action.service.PaymentLinkService;

public class PaymentLinkPayAction extends AbstractSecureAction {

	private static final long serialVersionUID = -5586160453950123409L;
	private static Logger logger = LoggerFactory.getLogger(PaymentLinkPayAction.class.getName());

	@Autowired
	private PaymentLinkService paymentLinkService;
	
	@Autowired
	private UserDao userDao;
	
	private String pvalue;
	

	@Override
	public String execute() {

		PaymentLink paymentLink = null;
		try {
			// clean session
			sessionMap.invalidate();
			if(!StringUtils.isEmpty(pvalue))
			{
			paymentLink = paymentLinkService.getPaymentLink(pvalue);
			if (null == paymentLink) {
				return "invalidRequest";
			}
			User user = userDao.findPayId(paymentLink.getPayId());
			if (null == user || !user.getUserStatus().getStatus().equalsIgnoreCase("Active")) {
				return "invalidRequest";
			}
			ErrorType errorType=paymentLinkService.validatePaymentLinkStatus(paymentLink);
			if (!errorType.getResponseCode().equals(ErrorType.SUCCESS.getCode())) {
				return "invalidRequest";
			}
			String finalRequest = paymentLinkService.prepareFields(paymentLink);
			PrintWriter out = ServletActionContext.getResponse().getWriter();

			logger.info("Request  from payment link action to PG" + finalRequest);
			out.write(finalRequest);
			out.flush();
			out.close();

			return NONE;
			}else {
				return "invalidRequest";
			}
		} catch (Exception exception) {
			MDC.put(Constants.CRM_LOG_USER_PREFIX.getValue(), pvalue);
			logger.error("Exception posting request to PG ", exception);
			return ERROR;
		}
	}


	public String getPvalue() {
		return pvalue;
	}


	public void setPvalue(String pvalue) {
		this.pvalue = pvalue;
	}
	
	

}
