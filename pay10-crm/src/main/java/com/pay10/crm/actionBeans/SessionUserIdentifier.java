package com.pay10.crm.actionBeans;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.CrmFieldConstants;

@Service
public class SessionUserIdentifier {
	
	@Autowired
	private UserDao userDao;

	public String getMerchantPayId(User user, String merchantEmailId) {		
		switch (user.getUserType()) {
			case ADMIN:
			case SUPERADMIN:
			case SUBADMIN:	
			case ACQUIRER:
			case RESELLER:
			case SMA:
			case MA:
			case Agent:
			case SUBACQUIRER:
				if ((null == merchantEmailId) || merchantEmailId.equals(CrmFieldConstants.ALL.toString())) {
					return CrmFieldConstants.ALL.toString();
				}else {
					User merchant = userDao.findPayIdByEmail(merchantEmailId);
					return merchant.getPayId();
				}
			case MERCHANT:
				return user.getPayId();
			case SUBUSER:
				return user.getParentPayId();
			default:
				break;
			}
			return null;	
	}

	public List<Merchants> getMerchantPayId1(User user, String merchantName) {
		List<Merchants> merchantsList = new ArrayList<Merchants>();
		if (user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.MERCHANT) || user.getUserType().equals(UserType.SUBADMIN)
				|| user.getUserType().equals(UserType.SUPERADMIN)
				|| user.getUserType().equals(UserType.RESELLER) || user.getUserType().equals(UserType.ACQUIRER)
				|| user.getUserType().equals(UserType.SUBACQUIRER)) {
			if ((null == merchantName) || merchantName.equals(CrmFieldConstants.ALL.toString())) {
				merchantsList = userDao.getMerchantActive(merchantName);
			} else {
				merchantsList = userDao.getMerchantActive(merchantName);
			}
		} /*else if (user.getUserType().equals(UserType.SUBUSER)) {
			return user.getParentPayId();
		} else {
			return user.getPayId();
		}*/
		return merchantsList;
	}

	
	
	// to use payId securely for further operations
	public static String getUserPayId(User user, String payId) {
		switch (user.getUserType()) {
		case ADMIN:
		case SUPERADMIN:
		case ACQUIRER:
		case RESELLER:
		case SUBACQUIRER:
			return payId;
		case SUBADMIN:
		case MERCHANT:
			return user.getPayId();
		case SUBUSER:
			return user.getParentPayId();
		}
		return null;
	}
}