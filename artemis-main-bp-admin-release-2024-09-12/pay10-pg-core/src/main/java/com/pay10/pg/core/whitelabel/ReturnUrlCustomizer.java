package com.pay10.pg.core.whitelabel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.user.User;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StaticDataProvider;

@Service
public class ReturnUrlCustomizer {

	@Autowired
	private StaticDataProvider staticDataProvider;

	public String customizeReturnUrl(Fields fields, String returnUrl) {
		String payId = fields.get(FieldType.PAY_ID.getName());
		User user = staticDataProvider.getUserData(payId);
		if (user.isEnableWhiteLabelUrl()) {
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_USE_WHITE_LABEL_URL.getName()))) {
				if (fields.get(FieldType.INTERNAL_USE_WHITE_LABEL_URL.getName()).equals(Constants.Y.getValue())) {
					returnUrl = returnUrl.replace(ConfigurationConstants.DOMAIN_NAME.getValue(), user.getWhiteLabelUrl());
				}
			}
		}
		return returnUrl;
	}

}
