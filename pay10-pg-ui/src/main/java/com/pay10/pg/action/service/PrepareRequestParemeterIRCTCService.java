package com.pay10.pg.action.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.SessionMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentTypeProvider;
import com.pay10.commons.util.PaymentTypeTransactionProvider;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.action.AbstractSecureAction;

@Service("prepareRequestParemeterIRCTCService")
public class PrepareRequestParemeterIRCTCService extends AbstractSecureAction {

	private static final long serialVersionUID = -2061696886175322587L;
	private static Logger logger = LoggerFactory.getLogger(PrepareRequestParemeterIRCTCService.class.getName());

	@Autowired
	private PaymentTypeProvider paymentTypeProvider;

	@Autowired
	PropertiesManager propertiesManager;

	private Map<String, Object> supportedPaymentTypeMap = new HashMap<String, Object>();

	public JSONObject prepareRequestParameter(Fields fields, User user, SessionMap sessionMap) throws SystemException {

		PaymentTypeTransactionProvider paymentTypeTransactionProvider = paymentTypeProvider.setSupportedPaymentOptions(user.getPayId());
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

		Object upSurcharge = sessionMap.get(FieldType.UP_SURCHARGE.getName());
		if (upSurcharge != null) {
			requestParameterJson.put("surcharge_up", sessionMap.get(FieldType.UP_SURCHARGE.getName()).toString());
		} else {
			requestParameterJson.put("surcharge_up", false);
		}

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

		// Changes by vijaya for Net-banking dropDown list
		List<String> nbMopTypeList = new ArrayList<String>();
		Object nbMop = paymentTypeTransactionProvider.getSupportedPaymentTypeMap().get("NB");
		if (nbMop != null) {
			if (nbMop.toString().contains(Constants.AXIS_BANK.getValue())) {
				nbMopTypeList.add(Constants.AXISBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.BANK_OF_BAHRAIN_AND_KUWAIT.getValue())) {
				
				nbMopTypeList.add(Constants.BANK_OFBAHRAIN_AND_KUWAIT.getValue());
			}
			if (nbMop.toString().contains(Constants.BOI.getValue())) {
				
				nbMopTypeList.add(Constants.B_OI.getValue());
			}
			if (nbMop.toString().contains(Constants.BANK_OF_MAHARASHTRA.getValue())) {
				
				nbMopTypeList.add(Constants.BANK_OFMAHARASHTRA.getValue());
			}
			if (nbMop.toString().contains(Constants.CANARA_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.CANARABANK.getValue());
			}
			if (nbMop.toString().contains(Constants.CENTRAL_BANK_OF_INDIA.getValue())) {
				
				nbMopTypeList.add(Constants.CENTRAL_BANK_OFINDIA.getValue());
			}
			if (nbMop.toString().contains(Constants.CITIBANK.getValue())) {
				
				nbMopTypeList.add(Constants.CITI_BANK.getValue());
			}
			if (nbMop.toString().contains(Constants.CITY_UNION_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.CITY_UNIONBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.CORPORATION_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.CORPORATIONBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.DEUTSCHE_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.DEUTSCHEBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.DEVELOPMENT_CREDIT_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.DEVELOPMENT_CREDITBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.FEDERAL_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.FEDERALBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.HDFC_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.HDFCBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.ICICI_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.ICICIBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.INDIAN_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.INDIANBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.YESBANK_CB.getValue())) {
				
				nbMopTypeList.add(Constants.YESBANKCB.getValue());
			}
			if (nbMop.toString().contains(Constants.INDIAN_OVERSEAS_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.INDIANOVERSEAS_BANK.getValue());
			}
			if (nbMop.toString().contains(Constants.INDUSIND_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.INDUSINDBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.INDUSTRIAL_DEVELOPMENT_BANK_OF_INDIA.getValue())) {
				
				nbMopTypeList.add(Constants.INDUSTRIAL_DEVELOPMENTBANK_OF_INDIA.getValue());
			}
			if (nbMop.toString().contains(Constants.ING_VYSYA_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.ING_VYSYABANK.getValue());
			}
			if (nbMop.toString().contains(Constants.JAMMU_AND_KASHMIR_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.JAMMU_AND_KASHMIRBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.KARNATAKA_BANK_LTD.getValue())) {
				
				nbMopTypeList.add(Constants.KARNATAKA_BANKLTD.getValue());
			}
			if (nbMop.toString().contains(Constants.KARUR_VYSYA_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.KARUR_VYSYABANK.getValue());
			}
			if (nbMop.toString().contains(Constants.KOTAK_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.KOTAKBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.ORIENTAL_BANK_OF_COMMERCE.getValue())) {
				
				nbMopTypeList.add(Constants.ORIENTAL_BANK_OFCOMMERCE.getValue());
			}
			if (nbMop.toString().contains(Constants.RATNAKAR_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.RATNAKARBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.SOUTH_INDIAN_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.SOUTH_INDIANBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.STATE_BANK_OF_BIKANER_AND_JAIPUR.getValue())) {
				
				nbMopTypeList.add(Constants.STATE_BANK_OFBIKANER_AND_JAIPUR.getValue());
			}
			if (nbMop.toString().contains(Constants.STATE_BANK_OF_HYDERABAD.getValue())) {
				
				nbMopTypeList.add(Constants.STATE_BANK_OFHYDERABAD.getValue());
			}
			if (nbMop.toString().contains(Constants.STATE_BANK_OF_INDIA.getValue())) {
				
				nbMopTypeList.add(Constants.STATE_BANK_OFINDIA.getValue());
			}
			if (nbMop.toString().contains(Constants.STATE_BANK_OF_MYSORE.getValue())) {
				
				nbMopTypeList.add(Constants.STATE_BANK_OFMYSORE.getValue());
			}
			if (nbMop.toString().contains(Constants.STATE_BANK_OF_PATIALA.getValue())) {
				
				nbMopTypeList.add(Constants.STATE_BANK_OFPATIALA.getValue());
			}
			if (nbMop.toString().contains(Constants.STATE_BANK_OF_TRAVANCORE.getValue())) {
				
				nbMopTypeList.add(Constants.STATE_BANK_OFTRAVANCORE.getValue());
			}
			if (nbMop.toString().contains(Constants.TAMILNAD_MERCANTILE_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.TAMILNAD_MERCANTILEBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.UNION_BANK_OF_INDIA.getValue())) {
				
				nbMopTypeList.add(Constants.UNION_BANK_OFINDIA.getValue());
			}
			if (nbMop.toString().contains(Constants.UNITED_BANK_OF_INDIA.getValue())) {
				
				nbMopTypeList.add(Constants.UNITED_BANK_OFINDIA.getValue());
			}
			if (nbMop.toString().contains(Constants.VIJAYA_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.VIJAYABANK.getValue());
			}
			if (nbMop.toString().contains(Constants.YES_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.YESBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.IDFCUPI_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.IDFCUPIBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.ANDHRA_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.ANDHRABANK.getValue());
			}
			if (nbMop.toString().contains(Constants.BANK_OF_BARODA_CORPORATE_ACCOUNTS.getValue())) {
				
				nbMopTypeList.add(Constants.BANK_OF_BARODACORPORATE_ACCOUNTS.getValue());
			}
			if (nbMop.toString().contains(Constants.BANK_OF_BARODA_RETAIL_ACCOUNTS.getValue())) {
				
				nbMopTypeList.add(Constants.BANK_OF_BARODARETAIL_ACCOUNTS.getValue());
			}
			if (nbMop.toString().contains(Constants.CATHOLIC_SYRIAN_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.CATHOLIC_SYRIANBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.LAKSHMI_VILAS_BANK_NETBANKING.getValue())) {
				
				nbMopTypeList.add(Constants.LAKSHMI_VILAS_BANKNETBANKING.getValue());
			}
			if (nbMop.toString().contains(Constants.PUNJAB_NATIONAL_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.PUNJAB_NATIONALBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.STANDARD_CHARTERED_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.STANDARD_CHARTEREDBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.SYNDICATE_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.SYNDICATEBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.AXIS_CORPORATE_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.AXIS_CORPORATEBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.ICICI_CORPORATE_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.ICICI_CORPORATEBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.PNB_CORPORATE_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.PNB_CORPORATEBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.HSBC_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.HSBCBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.UCO_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.UCOBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.COSMOS_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.COSMOSBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.ALLAHABAD_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.ALLAHABADBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.BANK_OF_BARODA.getValue())) {
				
				nbMopTypeList.add(Constants.BANK_OFBARODA.getValue());
			}
			if (nbMop.toString().contains(Constants.DHANLAXMI_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.DHANLAXMIBANK.getValue());
			}
			if (nbMop.toString().contains(Constants.PUNJAB_AND_SIND_BANK.getValue())) {
				
				nbMopTypeList.add(Constants.PUNJAB_AND_SINDBANK.getValue());
			}
		} else {
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
