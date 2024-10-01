package com.pay10.pg.action;

import java.io.PrintWriter;
import java.util.Date;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.PayinPaymentS2S;
import com.pay10.commons.user.PaymentLink;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.pg.action.service.PayinPaymentS2SService;

public class PayinPaymentS2SAction extends AbstractSecureAction {
	private static final long serialVersionUID = -5586160453950123409L;
	private static Logger logger = LoggerFactory.getLogger(PayinPaymentS2SAction.class.getName());

	@Autowired
	private PayinPaymentS2SService payinPaymentS2SService;

	@Autowired
	private UserDao userDao;

	private String paymentOrderId;

	@Override
	public String execute() {

		logger.info("Request Received For PayinPaymentS2SAction ::"+new Date());
		PayinPaymentS2S paymentLink = null;
		try {
			// clean session
			sessionMap.invalidate();
			if (!StringUtils.isEmpty(paymentOrderId)) {
				paymentLink = payinPaymentS2SService.getPaymentLink(paymentOrderId);
				if (null == paymentLink) {
					return "invalidRequest";
				}
				User user = userDao.findPayId(paymentLink.getPayId());
				if (null == user || !user.getUserStatus().getStatus().equalsIgnoreCase("Active")) {
					return "invalidRequest";
				}
				ErrorType errorType = payinPaymentS2SService.validatePaymentLinkStatus(paymentLink);
				if (!errorType.getResponseCode().equals(ErrorType.SUCCESS.getCode())) {
					return "invalidRequest";
				}
				String finalRequest = payinPaymentS2SService.prepareFields(paymentLink);
				PrintWriter out = ServletActionContext.getResponse().getWriter();

				logger.info("Request from intend payment link action to PG" + finalRequest);
				out.write(finalRequest);
				out.flush();
				out.close();

				return NONE;
			} else {
				return "invalidRequest";
			}
		} catch (Exception exception) {
			MDC.put(Constants.CRM_LOG_USER_PREFIX.getValue(), paymentOrderId);
			logger.error("Exception posting request to PG ", exception);
			return ERROR;
		}
	}

	public String getPaymentOrderId() {
		return paymentOrderId;
	}

	public void setPaymentOrderId(String paymentOrderId) {
		this.paymentOrderId = paymentOrderId;
	}

	

}
