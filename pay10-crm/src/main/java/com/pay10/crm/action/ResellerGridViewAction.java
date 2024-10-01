package com.pay10.crm.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.MerchantGridViewService;
import com.pay10.commons.user.MerchantDetails;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;

/**
 * @author Shaiwal
 *
 */
public class ResellerGridViewAction extends AbstractSecureAction {

	private static final long serialVersionUID = 53071970759044799L;

	@Autowired
	private MerchantGridViewService merchantGridViewService;

	@Autowired
	private DataEncoder encoder;

	private static Logger logger = LoggerFactory.getLogger(ResellerGridViewAction.class.getName());
	private List<MerchantDetails> aaData;

	public String execute() {
		try {
			aaData = encoder.encodeMerchantDetailsObj(merchantGridViewService.getAllReseller());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public String resellerList() {
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			aaData = encoder.encodeMerchantDetailsObj(
					merchantGridViewService.getAllReselerMerchants(sessionUser.getResellerId()));
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public List<MerchantDetails> getAaData() {
		return aaData;
	}

	public void setAaData(List<MerchantDetails> aaData) {
		this.aaData = aaData;
	}

}