package com.pay10.pg.action.service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.*;
import com.pay10.pg.action.AbstractSecureAction;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.SessionMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("prepareRequestParemeterService")
public class PrepareRequestParemeterService extends AbstractSecureAction {

	private static final long serialVersionUID = -2061696886175322587L;
	private static Logger logger = LoggerFactory.getLogger(PrepareRequestParemeterService.class.getName());

	@Autowired
	private PaymentTypeProvider paymentTypeProvider;

	@Autowired
	PropertiesManager propertiesManager;

	private Map<String, Object> supportedPaymentTypeMap = new HashMap<String, Object>();

	public JSONObject prepareRequestParameter(Fields fields, User user, SessionMap sessionMap) throws SystemException {
		String currency = fields.get(FieldType.CURRENCY_CODE.getName()) != null ? fields.get(FieldType.CURRENCY_CODE.getName()) : "356";
		PaymentTypeTransactionProvider paymentTypeTransactionProvider = paymentTypeProvider
				.setSupportedPaymentOptionsNew(user.getPayId(),currency);
		setSupportedPaymentTypeMap(paymentTypeTransactionProvider.getSupportedPaymentTypeMap());
		sessionMap.put(Constants.PAYMENT_TYPE_MOP.getValue(),
				paymentTypeTransactionProvider.getSupportedPaymentTypeMap());
		JSONObject requestParameterJson = new JSONObject();

		requestParameterJson.put(Constants.ADS.getValue(), true);
		requestParameterJson.put(Constants.PAYMENT_ADSIMG_URL.getValue(),
				PropertiesManager.propertiesMap.get(Constants.PAYMENT_ADSIMG_URL.getValue()));
		requestParameterJson.put(Constants.PAYMENT_ADSIMG_LINK_URL.getValue(),
				PropertiesManager.propertiesMap.get(Constants.PAYMENT_ADSIMG_LINK_URL.getValue()));

		// StringBuilder request = new StringBuilder();

		String paymentTypeMops = sessionMap.get(Constants.PAYMENT_TYPE_MOP.getValue()).toString();
		Object merchantPaymentTypeObject = sessionMap.get(FieldType.MERCHANT_PAYMENT_TYPE.getName());

		String merchantPaymentType = "";
		if (null != merchantPaymentTypeObject) {
			merchantPaymentType = (String) merchantPaymentTypeObject;
		}

		if (merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue())
				&& (!paymentTypeMops.contains(Constants.PAYMENT_TYPE_AD.getValue()))) {
			throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
		}
		if (merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_UPI.getValue())
				&& (!paymentTypeMops.contains(Constants.PAYMENT_TYPE_UP.getValue()))) {
			throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
		}
		if (merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_CARD.getValue())
				&& ((!paymentTypeMops.contains(Constants.PAYMENT_TYPE_CC.getValue()))
						|| (!paymentTypeMops.contains(Constants.PAYMENT_TYPE_DC.getValue())
								|| (!paymentTypeMops.contains(Constants.PAYMENT_TYPE_PC.getValue()))))) {
			throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
		}
		if (merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_WL.getValue())
				&& (!paymentTypeMops.contains(Constants.PAYMENT_TYPE_WL.getValue()))) {
			throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
		}

		
		// Check TDR or Surcharge Mode

				if (StringUtils.isNotBlank(fields.get(FieldType.SURCHARGE_FLAG.getName())) &&
						fields.get(FieldType.SURCHARGE_FLAG.getName()).equalsIgnoreCase("Y")) {
					
					requestParameterJson.put("isSurcharge", true);
				}
				else {
					requestParameterJson.put("isSurcharge", false);
				}

		// end
				
		// changes by shivanand


		if (merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_NB.getValue())
				&& (!paymentTypeMops.contains(Constants.PAYMENT_TYPE_NB.getValue()))) {
			throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
		}

		// end

		if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_AD.getValue())
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_UPI.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_CARD.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_WL.getValue()))) {
			requestParameterJson.put("autoDebit", true);
		} else {
			requestParameterJson.put("autoDebit", false);
		}
		if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_WL.getValue())
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_UPI.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_CARD.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue()))) {
			requestParameterJson.put("iMudra", true);
		} else {
			requestParameterJson.put("iMudra", false);
		}

		if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_CC.getValue())
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_UPI.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_WL.getValue()))) {
			requestParameterJson.put("creditCard", true);
		} else {
			requestParameterJson.put("creditCard", false);
		}
		

		// Changes by shivanand

		if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_NB.getValue())
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_UPI.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_WL.getValue()))) {
			requestParameterJson.put("netBanking", true);
		} else {
			requestParameterJson.put("netBanking", false);
		}

		// end

		if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_PC.getValue())
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_UPI.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_WL.getValue()))) {
			requestParameterJson.put("prepaidCard", true);
		} else {
			requestParameterJson.put("prepaidCard", false);
		}

		requestParameterJson.put("currencyCode", sessionMap.get(FieldType.CURRENCY_CODE.getName()));

		if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_DC.getValue())
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_UPI.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_WL.getValue()))) {
			requestParameterJson.put("debitCard", true);
		} else {
			requestParameterJson.put("debitCard", false);
		}

		// for International card
		if ((!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_UPI.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue()))) {

			requestParameterJson.put("internationalCard", true);
		} else {
			requestParameterJson.put("internationalCard", false);
		}
		if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_DP.getValue())
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_UPI.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_WL.getValue()))) {
			requestParameterJson.put("debitWithPin", true);
		} else {
			requestParameterJson.put("debitWithPin", false);
		}

		/*
		 * String custEmailId = fields.get(FieldType.CUST_EMAIL.getName()); if
		 * (custEmailId != null && !String.valueOf(custEmailId).trim().equals("")) {
		 * requestParameterJson.put("emailId", true); } else {
		 * requestParameterJson.put("emailId", false); }
		 */

		if (StringUtils.isBlank(user.getCardSaveParam())) {
			requestParameterJson.put("express_pay", false);
		} else if (StringUtils.isBlank(fields.get(user.getCardSaveParam()))) {
			requestParameterJson.put("express_pay", false);
		} else {
			Object expressFlag = sessionMap.get(Constants.EXPRESS_PAY_FLAG.getValue());
			if (null != expressFlag) {
				expressFlag = expressFlag.toString();
				if (expressFlag.equals(Constants.TRUE_STRING.getValue())) {
					requestParameterJson.put("express_pay", true);
				} else {
					requestParameterJson.put("express_pay", false);
				}
			} else {
				requestParameterJson.put("express_pay", false);
			}
		}

		Object autoDebitSurcharge = sessionMap.get(FieldType.AD_SURCHARGE.getName());
		if (autoDebitSurcharge != null) {
			requestParameterJson.put("surcharge_ad", sessionMap.get(FieldType.AD_SURCHARGE.getName()).toString());
		} else {
			requestParameterJson.put("surcharge_ad", false);
		}

		Object ccSurcharge = sessionMap.get(FieldType.CC_SURCHARGE.getName());
		if (ccSurcharge != null) {
			requestParameterJson.put("surcharge_cc", sessionMap.get(FieldType.CC_SURCHARGE.getName()).toString());
		} else {
			requestParameterJson.put("surcharge_cc", false);
		}

		Object dcSurcharge = sessionMap.get(FieldType.DC_SURCHARGE.getName());
		if (dcSurcharge != null) {
			requestParameterJson.put("surcharge_dc", sessionMap.get(FieldType.DC_SURCHARGE.getName()).toString());
		} else {
			requestParameterJson.put("surcharge_dc", false);
		}

		// changes by shivanand

		Object nbSurcharge = sessionMap.get(FieldType.NB_SURCHARGE.getName());
		if (nbSurcharge != null) {
			requestParameterJson.put("surcharge_nb", sessionMap.get(FieldType.NB_SURCHARGE.getName()).toString());
		} else {
			requestParameterJson.put("surcharge_nb", false);
		}
		// end

		Object pcSurcharge = sessionMap.get(FieldType.PC_SURCHARGE.getName());
		if (pcSurcharge != null) {
			requestParameterJson.put("surcharge_pc", sessionMap.get(FieldType.PC_SURCHARGE.getName()).toString());
		} else {
			requestParameterJson.put("surcharge_pc", false);
		}
		Object qrSurcharge = sessionMap.get(FieldType.QR_SURCHARGE.getName());
		logger.info(">>>>>>>>>>>....");
		if (qrSurcharge != null) {
			logger.info(">>>>>>>>>>>....");

			requestParameterJson.put("surcharge_qr", sessionMap.get(FieldType.QR_SURCHARGE.getName()).toString());
		} else {
			logger.info(">>>>>>>>>>>....");

			requestParameterJson.put("surcharge_qr", false);
		}
		Object upSurcharge = sessionMap.get(FieldType.UP_SURCHARGE.getName());
		if (upSurcharge != null) {
			requestParameterJson.put("surcharge_up", sessionMap.get(FieldType.UP_SURCHARGE.getName()).toString());
		} else {
			requestParameterJson.put("surcharge_up", false);
		}
		requestParameterJson.put("surcharge_emi", false);
		

		Object imudraSurcharge = sessionMap.get(FieldType.WL_SURCHARGE.getName());
		if (imudraSurcharge != null) {
			requestParameterJson.put("surcharge_wl", sessionMap.get(FieldType.WL_SURCHARGE.getName()).toString());
		} else {
			requestParameterJson.put("surcharge_wl", false);
		}

		if (sessionMap.get(Constants.TOKEN.getValue()).toString().equals(Constants.NA.getValue())) {
			requestParameterJson.put("tokenAvailable", false);
		} else {
			requestParameterJson.put("tokenAvailable", true);
		}

		if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_UP.getValue())
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_CARD.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue()))) {
			requestParameterJson.put("upi", true);
		} else {
			requestParameterJson.put("upi", false);
		}
		if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_QR.getValue())
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_CARD.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue()))) {
			requestParameterJson.put("QRCODE", true);
		} else {
			requestParameterJson.put("QRCODE", false);
		}
		if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_EMI.getValue())
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_CARD.getValue()))
				&& (!merchantPaymentType.equals(Constants.MERCHANT_PAYMENT_AD.getValue()))) {
			requestParameterJson.put("EMI", true);
		} else {
			requestParameterJson.put("EMI", false);
		}

		StringBuilder ccMopType = new StringBuilder();
		Object ccMop = paymentTypeTransactionProvider.getSupportedPaymentTypeMap().get("CC");
		if (ccMop != null) {
			if (ccMop.toString().contains(Constants.RUPAY.getValue())) {
				ccMopType.append(Constants.MOP_RUPAY.getValue());
			}
			if (ccMop.toString().contains(Constants.VISA.getValue())) {
				if (!ccMopType.toString().isEmpty()) {
					ccMopType.append(Constants.MOP_COMMA.getValue());
				}
				ccMopType.append(Constants.MOP_VISA.getValue());
			}
			if (ccMop.toString().contains(Constants.MASTERCARD.getValue())) {
				if (!ccMopType.toString().isEmpty()) {
					ccMopType.append(Constants.MOP_COMMA.getValue());
				}
				ccMopType.append(Constants.MOP_MASTERCARD.getValue());
			}
			if (ccMop.toString().contains(Constants.AMEX.getValue())) {
				if (!ccMopType.toString().isEmpty()) {
					ccMopType.append(Constants.MOP_COMMA.getValue());
				}
				ccMopType.append(Constants.MOP_AMEX.getValue());
			}
			if (ccMop.toString().contains(Constants.DISCOVER.getValue())) {
				if (!ccMopType.toString().isEmpty()) {
					ccMopType.append(Constants.MOP_COMMA.getValue());
				}
				ccMopType.append(Constants.MOP_DISCOVER.getValue());
			}
			if (ccMop.toString().contains(Constants.DINERS.getValue())) {
				if (!ccMopType.toString().isEmpty()) {
					ccMopType.append(Constants.MOP_COMMA.getValue());
				}
				ccMopType.append(Constants.MOP_DINNER.getValue());
			}
		} else {
			requestParameterJson.put("ccMopTypes", false);

		}
		requestParameterJson.put("ccMopTypes", ccMopType);

		
		// Wallet Mop Types
		StringBuilder wlMopType = new StringBuilder();
		Object wlMop = paymentTypeTransactionProvider.getSupportedPaymentTypeMap().get("WL");
		if (wlMop != null) {
			if (wlMop.toString().contains(Constants.PAYTM.getValue())) {
				wlMopType.append(MopType.PAYTM_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.MOBIKWIK.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.MOBIKWIK_WALLET.getCode());
			}
			
			if (wlMop.toString().contains(Constants.OLAMONEY.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.OLAMONEY_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.MATCHMOVE.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.MATCHMOVE_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.AMAZONPAY.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.AMAZON_PAY_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.AIRTELPAY.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.AIRTEL_PAY_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.FREECHARGE.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.FREECHARGE_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.GOOGLEPAY.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.GOOGLE_PAY_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.ITZCASH.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.ITZ_CASH_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.JIOMONEY.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.JIO_MONEY_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.MPESA.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.M_PESA_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.OXYZEN.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.OXYZEN_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.PHONEPE.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.PHONE_PE_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.SBIBUDDY.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.SBI_BUDDY_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.ZIPCASH.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.ZIP_CASH_WALLET.getCode());
			}
			//For THB Currency
			if (wlMop.toString().contains(Constants.GCASH.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.GCASH_WALLET.getCode());
			}
			if (wlMop.toString().contains(Constants.MAYA.getValue())) {
				if (!wlMopType.toString().isEmpty()) {
					wlMopType.append(Constants.MOP_COMMA.getValue());
				}
				wlMopType.append(MopType.MAYA_WALLET.getCode());
			}
		} else {
			requestParameterJson.put("wlMopTypes", false);

		}
		requestParameterJson.put("wlMopTypes", wlMopType);

		
		
		
		
		StringBuilder pcMopType = new StringBuilder();
		Object pcMop = paymentTypeTransactionProvider.getSupportedPaymentTypeMap().get("PC");
		if (pcMop != null) {
			if (pcMop.toString().contains(Constants.RUPAY.getValue())) {
				pcMopType.append(Constants.MOP_RUPAY.getValue());
			}
			if (pcMop.toString().contains(Constants.VISA.getValue())) {
				if (!pcMopType.toString().isEmpty()) {
					pcMopType.append(Constants.MOP_COMMA.getValue());
				}
				pcMopType.append(Constants.MOP_VISA.getValue());
			}
			if (pcMop.toString().contains(Constants.MASTERCARD.getValue())) {
				if (!pcMopType.toString().isEmpty()) {
					pcMopType.append(Constants.MOP_COMMA.getValue());
				}
				pcMopType.append(Constants.MOP_MASTERCARD.getValue());
			}
			if (pcMop.toString().contains(Constants.AMEX.getValue())) {
				if (!pcMopType.toString().isEmpty()) {
					pcMopType.append(Constants.MOP_COMMA.getValue());
				}
				pcMopType.append(Constants.MOP_AMEX.getValue());
			}
			if (pcMop.toString().contains(Constants.DINERS.getValue())) {
				if (!pcMopType.toString().isEmpty()) {
					pcMopType.append(Constants.MOP_COMMA.getValue());
				}
				pcMopType.append(Constants.MOP_DINNER.getValue());
			}
		} else {
			requestParameterJson.put("pcMopTypes", false);

		}
		requestParameterJson.put("pcMopTypes", pcMopType);

		StringBuilder dcMopType = new StringBuilder();
		Object dcMop = paymentTypeTransactionProvider.getSupportedPaymentTypeMap().get("DC");
		if (dcMop != null) {
			if (dcMop.toString().contains(Constants.RUPAY.getValue())) {
				dcMopType.append(Constants.MOP_RUPAY.getValue());
			}
			if (dcMop.toString().contains(Constants.VISA.getValue())) {
				if (!dcMopType.toString().isEmpty()) {
					dcMopType.append(Constants.MOP_COMMA.getValue());
				}
				dcMopType.append(Constants.MOP_VISA.getValue());
			}
			
			
			if (dcMop.toString().contains(Constants.DISCOVER.getValue())) {
				if (!dcMopType.toString().isEmpty()) {
					dcMopType.append(Constants.MOP_COMMA.getValue());
				}
				dcMopType.append(Constants.MOP_DISCOVER.getValue());
			}
			if (dcMop.toString().contains(Constants.MASTERCARD.getValue())) {
				if (!dcMopType.toString().isEmpty()) {
					dcMopType.append(Constants.MOP_COMMA.getValue());
				}
				dcMopType.append(Constants.MOP_MASTERCARD.getValue());
			}
			if (dcMop.toString().contains(Constants.MAESTRO.getValue())) {
				if (!dcMopType.toString().isEmpty()) {
					dcMopType.append(Constants.MOP_COMMA.getValue());
				}
				dcMopType.append(Constants.MOP_MAESTRO.getValue());
			}
		} else {
			requestParameterJson.put("dcMopTypes", false);
		}
		requestParameterJson.put("dcMopTypes", dcMopType);

		List<String> nbMopTypeList = new ArrayList<String>();
		Object nbMop = paymentTypeTransactionProvider.getSupportedPaymentTypeMap().get("NB");
		if (nbMop != null) {
			
			try {
				String mopListArray [] = nbMop.toString().split(",");
				Object mopTypeListAll [] = Class.forName("com.pay10.commons.util.MopType").getEnumConstants();
				
				for (String mopName : mopListArray) {
					
					mopName = mopName.replace("[", "");
					mopName = mopName.replace("]", "");
					mopName = mopName.replace(" ", "");
					for (Object mopTypeObject : mopTypeListAll) {
						
						if (mopName.equalsIgnoreCase(String.valueOf(mopTypeObject))){
							MopType mopType = (MopType) mopTypeObject;
							nbMopTypeList.add(mopType.getCode());
						}
						
					}
				}
			}
			
			catch(Exception e) {
				logger.error("Error while fetching mop List for Net Banking",e);
			}
			
		}
			
		else {
			requestParameterJson.put("nbMopType", false);
		}
		// sorting bank name in alphabetical order
		StringBuilder nbMope = new StringBuilder();
		String nbMopeType = "";
		List<String> bankNameList = nbMopTypeList.stream().sorted(Comparator.comparing(n -> n.toString()))
				.collect(Collectors.toList());
		for (String bankName : bankNameList) {
			nbMope.append(bankName);
			nbMope.append(Constants.MOP_COMMA.getValue());
		}
		if (nbMope.toString().endsWith(",")) {
			nbMopeType = nbMope.toString().substring(0, nbMope.length() - 1);
		}
		requestParameterJson.put("nbMopType", nbMopeType);
		// end

		requestParameterJson.put("merchantType", user.getBusinessName());

		if (user.isIframePaymentFlag()) {
			requestParameterJson.put("iframeOpt", true);
		} else {
			requestParameterJson.put("iframeOpt", false);
		}

		Object gpMop = paymentTypeTransactionProvider.getSupportedPaymentTypeMap().get("UP");
		if (gpMop != null) {
			if (gpMop.toString().contains(Constants.MOP_GOOGLEPAY_PARAMETER.getValue())) {
				requestParameterJson.put("googlePay", true);
			} else {
				requestParameterJson.put("googlePay", false);
			}
		} else {
			requestParameterJson.put("googlePay", false);
		}

		requestParameterJson.put("paymentSlab", user.getPaymentMessageSlab());
		logger.info("Final supported parameter request " + requestParameterJson.toString());
		return requestParameterJson;

	}

	public Map<String, Object> getSupportedPaymentTypeMap() {
		return supportedPaymentTypeMap;
	}

	public void setSupportedPaymentTypeMap(Map<String, Object> supportedPaymentTypeMap) {
		this.supportedPaymentTypeMap = supportedPaymentTypeMap;
	}

}
