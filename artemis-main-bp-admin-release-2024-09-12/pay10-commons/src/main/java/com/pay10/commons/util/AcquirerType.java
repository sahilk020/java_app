package com.pay10.commons.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.dao.RouterRuleDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.RouterRule;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

/**
 * @author R
 *
 */
public enum AcquirerType {

	// Credit Card
	AMEX("AMEX", "AMERICAN EXPRESS"), 
	CITRUS_PAY("CITRUS", "Yes Bank"), 
	EZEECLICK("EZEECLICK", "AMEX EZEECLICK"), 
	HDFC("HDFC", "HDFC Bank"), 
	ICICI_FIRSTDATA("ICICIFIRSTDATA", "FD ICICI Bank"), 
	FEDERAL("FEDERAL","FEDERAL Bank"), 
	AXISMIGS("AXISMIGS", "AXIS Bank MIGS"), 
	BOB("BOB", "Bank of Baroda"),
	KOTAK("KOTAK", "KOTAK"),
	AXIS_CB("AXISCB","AXIS Bank CB"), 
	AXISBANK("AXISBANK","Axis Bank"),
	IDFC_FIRSTDATA("IDFCFIRSTDATA", "IDFC Bank"), 
	IDBIBANK("IDBIBANK","IDBI Bank"), 
	ICICI_MPGS("ICICIMPGS", "ICICI Bank MPGS"),
	FSS("FSS", "FSS"),
	ATOM("ATOM", "ATOM"),
	PAYU ("PAYU","PAYU"),
	LYRA ("LYRA","LYRA"),
	SBI ("SBI", "SBI"),
	SBINB ("SBINB", "SBINB"),
	SBICARD ("SBICARD", "SBICARD"),
	FREECHARGE("FREECHARGE", "FREECHARGE"),
	ICICIBANK("ICICIBANK","ICICIBANK"),
	KOTAK_CARD("KOTAKCARD","KOTAKCARD"),

	// Wallet
	WL_MOBIKWIK("WLMOBIKWIK", "MOBIKWIK WALLET"),
	PAYTM("PAYTM", "PAYTM"),
	WL_OLAMONEY("WLOLAMONEY", "OLAMONEY WALLET"), 
    MATCHMOVE("MATCHMOVE", "MatchMove Wallet"),
    CASHFREE("CASHFREE","CASHFREE"),
    EASEBUZZ("EASEBUZZ","EASEBUZZ"),
    AGREEPAY("AGREEPAY","AGREEPAY"),
    PINELABS("PINELABS","PINELABS"),
    YESBANKNB("YESBANKNB","YESBANKNB"),
	// UPI
	YESBANKCB("YESBANKCB", "YES Bank CB"),
	BILLDESK("BILLDESK", "BILLDESK"),
	IDFCUPI("IDFCUPI", "IDFCUPI Bank"),
	// KOTAK("KOTAK", "NETBANKING KOTAK Bank"),
	ISGPAY("ISGPAY", "ISGPAY"),
	JAMMU_AND_KASHMIR("JAMMUANDKASHMIR", "JAMMUANDKASHMIR"),
	IDFC("IDFC", "IDFC"),

	// NetBanking in aggregation mode
	DIRECPAY("DIRECPAY", "DIRECPAY"),
	NB_FEDERAL("NBFEDERAL", "NBFEDERAL"),
	// NetBanking for IRCTC as it is common for all banks
	NB_SBI("NBSBI", "SBI NETBANKING"),
	NB_ALLAHABAD_BANK("NBALLAHABADBANK", "ALLAHABAD BANK NETBANKING"),
	NB_VIJAYA_BANK("NBVIJAYABANK", "VIJAYA BANK NETBANKING"),
	NB_AXIS_BANK("NBAXISBANK", "AXIS BANK NETBANKING"),
	NB_ICICI_BANK("NBICICIBANK", "ICICI BANK NETBANKING"),
	NB_CORPORATION_BANK("NBCORPORATIONBANK", "CORPORATION BANK NETBANKING"),
	NB_SOUTH_INDIAN_BANK("NBSOUTHINDIANBANK", "SOUTH INDIAN BANK NETBANKING"),
	NB_KARUR_VYSYA_BANK("NBKARURVYSYABANK", "KARUR VYSYA BANK NETBANKING"),
	NB_KARNATAKA_BANK("NBKARNATAKABANK", "KARNATAKA BANK NETBANKING"),
	// AutoDebit
	ATL("ATL", "ATL"),
	APBL("APBL", "APBL"),
	MOBIKWIK("MOBIKWIK", "MOBIKWIK"),
	INGENICO("INGENICO", "INGENICO"),
	PHONEPE("PHONEPE", "PHONEPE"),
	YESBANKFT3("YESBANKFT3", "YES Bank FT3"),
	CAMSPAY("CAMSPAY", "CAMSPAY"),
	CANARANBBANK("CANARANBBANK","CANARANBBANK"),
	TMBNB("TMBNB", "TMBNB"),
	DEMO("DEMO","DEMO"),
	SHIVALIKNBBANK("SHIVALIKNBBANK", "SHIVALIKNBBANK"),
	TFP("TFP", "TFP"),
	CITYUNIONBANK("CITYUNIONBANK","CITYUNIONBANK"),

	COSMOS("COSMOS", "COSMOS"),
	QUOMO("QUOMO", "QUOMO"),
	PAYMENTAGE("PAYMENTAGE", "PAYMENTAGE"),
	PAY10("PAY10", "PAY10"),

	HTPAY("HTPAY", "HTPAY");
	// GOOGLE PAY Added for dummy record
	//GOOGLEPAY("GOOGLEPAY", "GOOGLEPAY");
	
	private final String code;
	private final String name;

	private AcquirerType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static AcquirerType getInstancefromCode(String acquirerCode) {
		AcquirerType acquirerType = null;

		for (AcquirerType acquirer : AcquirerType.values()) {

			if (acquirerCode.equals(acquirer.getCode().toString())) {
				acquirerType = acquirer;
				break;
			}
		}

		return acquirerType;
	}

	public static String getAcquirerName(String acquirerCode) {
		String acquirertype = null;
		if (null != acquirerCode) {
			for (AcquirerType acquirer : AcquirerType.values()) {
				if (acquirerCode.equals(acquirer.getCode().toString())) {
					acquirertype = acquirer.getName();
					break;
				}
			}
		}
		return acquirertype;
	}
	
	public static String getAcquirerCode(String acquirerName) {
		String acquirertype = null;
		if (null != acquirerName) {
			for (AcquirerType acquirer : AcquirerType.values()) {
				if (acquirerName.equals(acquirer.name().toString())) {
					acquirertype = acquirer.getCode();
					break;
				}
			}
		}
		return acquirertype;
	}

	public static AcquirerType getInstancefromName(String acquirerName) {
		AcquirerType acquirerType = null;

		for (AcquirerType acquirer : AcquirerType.values()) {

			if (acquirerName.equals(acquirer.getName())) {
				acquirerType = acquirer;
				break;
			}
		}

		return acquirerType;
	}

	public static AcquirerType getDefault(Fields fields) throws SystemException {
		User user = new User();
		UserDao userDao = new UserDao();
		// user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
		List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
		chargingDetailsList = new ChargingDetailsDao().getAllActiveChargingDetails(user.getPayId());

		return getAcquirer(fields.getFields(), user, chargingDetailsList);
	}

	public static AcquirerType getDefault(Fields fields, User user) throws SystemException {
		List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
		chargingDetailsList = new ChargingDetailsDao().getAllActiveChargingDetails(user.getPayId());
		return getAcquirer(fields.getFields(), user, chargingDetailsList);
	}

	public static AcquirerType getDefault(Fields fields, User user, List<ChargingDetails> paymentOptions)
			throws SystemException {
		return getAcquirer(fields.getFields(), user, paymentOptions);
	}

	private static int getRandomNumber() {
		Random rnd = new Random();
		int randomNumber = (rnd.nextInt(100)) + 1;
		return randomNumber;
	}

	private static AcquirerType getAcquirer(Map<String, String> fields, User user, List<ChargingDetails> paymentOptions)
			throws SystemException {
		String acquirerName = "";
		PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
		String mopType = fields.get(FieldType.MOP_TYPE.getName());
		String currency = fields.get(FieldType.CURRENCY_CODE.getName());
		String paymentTypeCode = fields.get(FieldType.PAYMENT_TYPE.getName());
		String payId = user.getPayId();
		String transactionType = user.getModeType().toString();

		String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
		String cardHolderType = fields.get(FieldType.CARD_HOLDER_TYPE.getName());

		String slabId = "";

		boolean paymentTypeFound = false;
		
		String checkAmountString = PropertiesManager.propertiesMap.get(Constants.SWITCH_ACQUIRER_AMOUNT.getValue());

		if (!checkAmountString.contains(payId)) {
			paymentTypeFound = false;
		}
		else {
			String[] checkAmountArray = checkAmountString.split(",");
			BigDecimal transactionAmount = new BigDecimal(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));

			for (String amountSlab : checkAmountArray) {

				if (!amountSlab.contains(payId)) {
					continue;
				}

				String[] amountSlabArray = amountSlab.split("-");

				BigDecimal minTxnAmount = new BigDecimal(amountSlabArray[1]);
				BigDecimal maxTxnAmount = new BigDecimal(amountSlabArray[2]);

				if (!StringUtils.isBlank(amountSlabArray[4])) {

					if (amountSlabArray[4].equalsIgnoreCase(paymentType.getCode())) {

						minTxnAmount = new BigDecimal(amountSlabArray[1]);
						maxTxnAmount = new BigDecimal(amountSlabArray[2]);

						if (transactionAmount.compareTo(minTxnAmount) >= 0
								&& transactionAmount.compareTo(maxTxnAmount) < 1) {
							slabId = amountSlabArray[0];
							paymentTypeFound = true;
							break;
						} else {
							paymentTypeFound = false;
						}

					} else {
						paymentTypeFound = false;
					}
				}

			}
			
		}

		
		if (!paymentTypeFound) {
			slabId = "00";
		}

		if (StringUtils.isEmpty(fields.get(FieldType.PAYMENT_TYPE.getName())) || StringUtils.isEmpty(mopType)
				|| StringUtils.isEmpty(currency)) {
			return null;
		}

		String identifier = payId + currency + paymentTypeCode + mopType + transactionType + paymentsRegion
				+ cardHolderType + slabId;
		RouterConfigurationDao routerConfigurationDao = new RouterConfigurationDao();
		switch (paymentType) {

		case CREDIT_CARD:
		case DEBIT_CARD:
		case PREPAID_CARD:
			List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
			// need a sorted list according to priority
			rulesList = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);

			int randomNumber = getRandomNumber();
			if (rulesList.size() > 1) {

				int min = 1;
				int max = 0;
				for (RouterConfiguration routerConfiguration : rulesList) {
					int loadPercentage = routerConfiguration.getLoadPercentage();
					min = 1 + max;
					max = max + loadPercentage;
					if (randomNumber >= min && randomNumber < max) {
						acquirerName = getAcquirerName(routerConfiguration.getAcquirer());
						break;
					}
				}
			} else {

				for (RouterConfiguration routerConfiguration : rulesList) {
					acquirerName = getAcquirerName(routerConfiguration.getAcquirer());

				}
			}
			break;

		case NET_BANKING:
			for (ChargingDetails detail : paymentOptions) {
				if (!detail.getPaymentType().getCode().equals(PaymentType.NET_BANKING.getCode())) {
					continue;
				}
				if (mopType.equals(detail.getMopType().getCode())) {
					acquirerName = detail.getAcquirerName();
					break;
				}
			}
			break;
		case WALLET:
			if (mopType.equals(MopType.MOBIKWIK_WALLET.getCode())) {
				acquirerName = AcquirerType.WL_MOBIKWIK.getName();
			} else if (mopType.equals(MopType.PAYTM_WALLET.getCode())) {
				acquirerName = AcquirerType.PAYTM.getName();
			} else if (mopType.equals(MopType.OLAMONEY_WALLET.getCode())) {
				acquirerName = AcquirerType.WL_OLAMONEY.getName();
			}
			break;
//		case EMI:
//			acquirerName = AcquirerType.CITRUS_PAY.getName();
//			break;
//		case RECURRING_PAYMENT:
//			acquirerName = AcquirerType.CITRUS_PAY.getName();
//			break;
		case UPI:

			List<RouterConfiguration> rulesListUpi = new ArrayList<RouterConfiguration>();
			// need a sorted list according to priority
			rulesListUpi = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);

			int randomNumberUpi = getRandomNumber();
			if (rulesListUpi.size() > 1) {

				int min = 1;
				int max = 0;
				for (RouterConfiguration routerConfiguration : rulesListUpi) {
					int loadPercentage = routerConfiguration.getLoadPercentage();
					min = 1 + max;
					max = max + loadPercentage;
					if (randomNumberUpi >= min && randomNumberUpi < max) {
						acquirerName = getAcquirerName(routerConfiguration.getAcquirer());
						break;
					}
				}
			} else {

				for (RouterConfiguration routerConfiguration : rulesListUpi) {
					acquirerName = getAcquirerName(routerConfiguration.getAcquirer());

				}
			}
			break;

//		case AD:
//			RouterRuleDao ruleDaoAtl = new RouterRuleDao();
//			RouterRule atlRules = ruleDaoAtl.findRuleByFieldsByPayId(payId, paymentTypeCode, mopType, currency,
//					transactionType);
//			if (atlRules == null) {
//				String allPayId = "ALL MERCHANTS";
//				atlRules = ruleDaoAtl.findRuleByFieldsByPayId(allPayId, paymentTypeCode, mopType, currency,
//						transactionType);
//			}
//			if (atlRules == null) {
//				throw new SystemException(ErrorType.ROUTER_RULE_NOT_FOUND,
//						ErrorType.ROUTER_RULE_NOT_FOUND.getResponseCode());
//			}
//			String atlAcquirerList = atlRules.getAcquirerMap();
//			Collection<String> atlAcqList = Helper.parseFields(atlAcquirerList);
//			Map<String, String> atlAcquirerMap = new LinkedHashMap<String, String>();
//			for (String atlAcquirer : atlAcqList) {
//				String[] acquirerPreference = atlAcquirer.split("-");
//				atlAcquirerMap.put(acquirerPreference[0], acquirerPreference[1]);
//			}
//
//			String atlPrimaryAcquirer = atlAcquirerMap.get("1");
//			// String primaryAcquirer ="FIRSTDATA";
//			acquirerName = getInstancefromCode(atlPrimaryAcquirer).getName();
//			break;
		default:
			break;
		}
		if (StringUtils.isEmpty(acquirerName)) {
			throw new SystemException(ErrorType.ACQUIRER_NOT_FOUND, ErrorType.ACQUIRER_NOT_FOUND.getResponseCode());
		}
		fields.put(FieldType.ACQUIRER_TYPE.getName(), getInstancefromName(acquirerName).getCode());
		return getInstancefromName(acquirerName);
	}
	
	public static void main(String[] args) throws Exception {
		
		String code="Axis Bank";
		
		AcquirerType data =getInstancefromName(code);
		System.out.println("acccccccccccccccdscds"+data);
		String name="AXISBANK";
		String data1 =getAcquirerName(name);
		System.out.println("acccccccccccccccdscds"+data1);
		
		
	}
	

}
