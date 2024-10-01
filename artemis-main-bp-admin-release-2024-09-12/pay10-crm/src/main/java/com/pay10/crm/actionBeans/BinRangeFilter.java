package com.pay10.crm.actionBeans;

import org.springframework.stereotype.Service;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.BinRangeIssuingBank;
import com.pay10.commons.util.BinRangeMopType;
import com.pay10.commons.util.CardsType;
import com.pay10.commons.util.CrmFieldConstants;


@Service
public class BinRangeFilter {

	public String getPaymentType(User sessionUser, String cardType) {
		if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)) {
			if (cardType.equals(CrmFieldConstants.ALL.toString())) {
				return CrmFieldConstants.ALL.toString();
			} else {
				CardsType cardsType = CardsType.getInstance(cardType);
				return cardsType.toString();
			}

		}
		return null;
	}

	public String getMopType(User sessionUser, String mopType) {
		if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)) {
			if (mopType.equals(CrmFieldConstants.ALL.toString())) {
				return CrmFieldConstants.ALL.toString();
			} else {
				BinRangeMopType binRangeMopType = BinRangeMopType.getInstance(mopType);
				return binRangeMopType.toString();
			}
		}
		return null;

	}
	public String getIssuerName(User sessionUser, String issuerName) {
		if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)) {
			if (issuerName.equals(CrmFieldConstants.ALL.toString())) {
				return CrmFieldConstants.ALL.toString();
			} else {
				BinRangeIssuingBank binRangeIssuingBank = BinRangeIssuingBank.getInstance (issuerName);
				return binRangeIssuingBank.getCode();
			}
		}
		return null;
	}
}
