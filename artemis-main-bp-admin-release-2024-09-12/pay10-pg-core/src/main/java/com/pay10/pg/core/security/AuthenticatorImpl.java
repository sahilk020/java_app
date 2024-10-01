package com.pay10.pg.core.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pay10.commons.user.PendingMappingRequest;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.PendingMappingRequestDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentTypeProvider;
import com.pay10.commons.util.PaymentTypeTransactionProvider;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.UserStatusType;

@Service
public class AuthenticatorImpl implements Authenticator {

	private static Logger logger = LoggerFactory.getLogger(AuthenticatorImpl.class.getName());
	//private static Map<String, User> userMap = new HashMap<String, User>();

	@Autowired
	@Qualifier("paymentTypeProvider")
	private PaymentTypeProvider paymentTypeProvider;

	@Autowired
	private StaticDataProvider staticDataProvider;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PendingMappingRequestDao pendingMappingRequestDao;

	public AuthenticatorImpl() {
	}

	@Override
	public ErrorType checkLogin(String userId, String password) {
		ErrorType errorType = ErrorType.UNKNOWN;
		return errorType;
	}

	@Override
	public User getUserFromPayId(Fields fields) throws SystemException {
		User user = new UserDao().findPayId(fields.get(FieldType.PAY_ID.getName()));
		return user;
	}

	@Override
	public void authenticate(Fields fields) throws SystemException {

		String payId = fields.get(FieldType.PAY_ID.getName());
		User user_ = getUser(fields);

		// Check if user is found
		if (null == user_) {
			throw new SystemException(ErrorType.USER_NOT_FOUND, "No such user, PayId=" + payId);
		}

		// Check transaction status of user
		UserStatusType userStatus = user_.getUserStatus();
		if (userStatus.equals(UserStatusType.PENDING) || userStatus.equals(UserStatusType.TRANSACTION_BLOCKED)
				|| userStatus.equals(UserStatusType.TERMINATED)) {
			if (!(fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.STATUS.getName()))) {
				fields.put(FieldType.STATUS.getName(), StatusType.AUTHENTICATION_FAILED.getName());
				logger.error("Merchant is not active with payId = " + payId);
				throw new SystemException(ErrorType.PERMISSION_DENIED, "User not allowed to transact, PayId=" + payId);
			}
		}
		String merchantHostedFlag = fields.get(FieldType.IS_MERCHANT_HOSTED.getName());

		User user = getUser(fields);

		if (!StringUtils.isBlank(merchantHostedFlag) && merchantHostedFlag.equals(Constants.Y.getValue())) {
			if (!user.isMerchantHostedFlag()) {
				fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), "Payment option not supported!!");
				logger.error("Merchant not allowed to perform direct transactoin payId=  " + payId);
				throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED,
						"Merchant not allowed to perform direct transactoin payId= " + payId);
			}
		}
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.NEWORDER.getName())) {
			validateCurrency(fields);
		}

	}

	@Override
	public void validatePaymentOptions(Fields fields) throws SystemException {

		User user = getUser(fields);
		PaymentTypeTransactionProvider paymentTypeTransactionProvider = paymentTypeProvider
				.setSupportedPaymentOptions((user.getPayId()));
		List<ChargingDetails> chargingDetailsList = paymentTypeTransactionProvider.getChargingDetailsList();
		List<ChargingDetails> supportedChargingDetailsList = new ArrayList<ChargingDetails>();

		String paymentTypeCode = fields.get(FieldType.PAYMENT_TYPE.getName());
		String mopTypeCode = fields.get(FieldType.MOP_TYPE.getName());

		for (ChargingDetails chargingDetails : chargingDetailsList) {
			if (chargingDetails.getPaymentType().getCode().equals(paymentTypeCode)
					&& chargingDetails.getMopType().getCode().equals(mopTypeCode)) {
				supportedChargingDetailsList.add(chargingDetails);
			}
		}
		if (supportedChargingDetailsList.isEmpty()) {
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(),"Payment option not supported!!");
			logger.error("Merchant not supported for this transactoin type payId= "
					+ fields.get(FieldType.PAY_ID.getName()));
			throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED,
					"Merchant not supported for this transactoin type payId= "
							+ fields.get(FieldType.PAY_ID.getName()));
		}
		// setSupportedChargingDetailsList(supportedChargingDetailsList);
	}

	public void validateCurrency(Fields fields) throws SystemException {
		User user = getUser(fields);
		String accountCurrencySet = pendingMappingRequestDao.findActiveMappingByEmailId(user.getEmailId());
		logger.info("accountCurrencySet:{}", accountCurrencySet);
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		JSONArray jsonArray = new JSONArray(accountCurrencySet);
		boolean flag = false;
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject accountCurrency = jsonArray.getJSONObject(i);
			if (currencyCode.equals(accountCurrency.getString("currencyCode"))) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "Currency not supported!!");
			logger.error("This currency not mapped with merchant, currency code = {}", currencyCode);
			throw new SystemException(ErrorType.CURRENCY_NOT_SUPPORTED,
					"Merchant not supported for this currency type payId= " + fields.get(FieldType.PAY_ID.getName()));
		}
	}

	@Override
	public void isUserExists(Fields fields) throws SystemException {
		String payId = fields.get(FieldType.PAY_ID.getName());
		User user_ = getUser(fields);

		// if user is found
		if (null == user_) {
			logger.error("No such user found with PayId = " + payId);
			throw new SystemException(ErrorType.USER_NOT_FOUND, "No such user, PayId=" + payId);

		}
	}

	@Override
	public User getUser(Fields fields) {
		User user = null;

		if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
				&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
						.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
			user = staticDataProvider.getUserData(fields.get(FieldType.PAY_ID.getName()));

		} else {
			user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		}

		return user;
	}

	@Override
	public void setUser(User user) {
		
        if (staticDataProvider.getUserData(user.getPayId()) == null) {
        	staticDataProvider.setUserData(user);
        
        }
            
	}

	@Override
	public void setUpdatedUser(User user) {
		staticDataProvider.setUserData(user);
	}

}
