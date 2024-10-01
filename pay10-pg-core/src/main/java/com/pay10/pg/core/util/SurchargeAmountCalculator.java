package com.pay10.pg.core.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.SurchargeDetailsDao;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StaticDataProvider;

/**
 * This class used to calculate surcharge amount.
 * 
 * @author Gajera Jay
 *
 */
@Service
public class SurchargeAmountCalculator {

	private static Logger logger = LoggerFactory.getLogger(SurchargeAmountCalculator.class.getName());

	@Autowired
	private SurchargeDetailsDao surchargeDetailsDao;

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private StaticDataProvider staticDataProvider;
	@Autowired
	private PropertiesManager propertiesManager;

	private DecimalFormat df = new DecimalFormat("0.000");
	private BigDecimal percentagevalue = new BigDecimal(100);

	public BigDecimal[] fetchCCSurchargeDetails(String amount, String payId, String paymentsRegion, String gst)
			throws SystemException {
		BigDecimal actualTransactionAmount = new BigDecimal(amount);

		try {
			logger.info("Getting fetchCCSurchargeDetails");
			String paymentType = PaymentType.CREDIT_CARD.getName();
			SurchargeDetails surchargeDetailsFromDb = null;

			if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
					&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
							.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
				surchargeDetailsFromDb = staticDataProvider.getSurchargeDetailsData(payId, paymentType, paymentsRegion);

			} else {
				surchargeDetailsFromDb = surchargeDetailsDao.findDetailsByRegion(payId, paymentType, paymentsRegion);
			}

			BigDecimal transSurchargeAmount = calculate(surchargeDetailsFromDb, amount, gst);
			logger.info("transSurchargeAmount ={}", transSurchargeAmount);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = transSurchargeAmount;
			surchargeDetails[1] = actualTransactionAmount.add(transSurchargeAmount);
			return surchargeDetails;
		} catch (Exception exception) {
			logger.error("Exception occured in fetchCCSurchargeDetails , " + exception);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = BigDecimal.ZERO;
			surchargeDetails[1] = actualTransactionAmount;
			return surchargeDetails;
		}

	}

	private BigDecimal calculate(SurchargeDetails surchargeDetails, String amount, String gst) {
		BigDecimal surchargeAmount = BigDecimal.ZERO;
		BigDecimal surchargePercentage = BigDecimal.ZERO;
		BigDecimal finalSurcharge = BigDecimal.ZERO;
		// calculate surcharge amount as per preference.
		if (surchargeDetails.getSurchargeAmount() != null && surchargeDetails.getSurchargeAmount().doubleValue() > 0) {
			finalSurcharge = calculateSurchargeAmount(surchargeDetails, new BigDecimal(amount), gst);
			surchargeAmount = finalSurcharge;
		}
		if (surchargeDetails.getSurchargePercentage() != null
				&& surchargeDetails.getSurchargePercentage().doubleValue() > 0) {
			finalSurcharge = calculateSurchargeAmountByPercentage(surchargeDetails, new BigDecimal(amount), gst);
			surchargePercentage = finalSurcharge;
		}
		if (surchargeDetails.getIsHigher() == null) {
			return finalSurcharge;
		}

		// in case of both amount and percentage are available then apply any one base
		// on configuration of higher/lower
		return getSurchargeByFlag(surchargeDetails.getIsHigher(), surchargeAmount, surchargePercentage);
	}

	private BigDecimal calculateSurchargeAmount(SurchargeDetails surchargeDetails, BigDecimal amount, String gst) {

		if (surchargeDetails == null) {
			return BigDecimal.ZERO;
		}

		if (surchargeDetails.getSurchargeAmount() != null && surchargeDetails.getSurchargeAmount().doubleValue() == 0) {
			return BigDecimal.ZERO;
		}

		// check that transaction amount is > minAmount.
		if (surchargeDetails.getMinTransactionAmount() != null
				&& surchargeDetails.getMinTransactionAmount().compareTo(amount) >= 0) {
			return BigDecimal.ZERO;
		}
		BigDecimal finalSurchargeAmt = surchargeDetails.getSurchargeAmount();
		BigDecimal serviceTax = getCalculatedGst(surchargeDetails, amount, gst, finalSurchargeAmt);
		return new BigDecimal(df.format(finalSurchargeAmt.add(serviceTax)));
	}

	private BigDecimal getCalculatedGst(SurchargeDetails surchargeDetails, BigDecimal amount, String gst,
			BigDecimal finalSurchargeAmt) {
		BigDecimal serviceTax = BigDecimal.ZERO;
		boolean skipgst = (skipGstAddition(amount, surchargeDetails.getPaymentType()));
		if (skipgst) {
			return serviceTax;
		}
		if (StringUtils.isBlank(gst) || StringUtils.equals(gst, "0")) {
			serviceTax = (finalSurchargeAmt.multiply(getServiceTax(surchargeDetails.getPayId()))
					.divide(percentagevalue));
		}
		return serviceTax;
	}

	private BigDecimal calculateSurchargeAmountByPercentage(SurchargeDetails surchargeDetails, BigDecimal amount,
			String gst) {

		if (surchargeDetails == null) {
			return BigDecimal.ZERO;
		}

		if (surchargeDetails.getSurchargePercentage() != null
				&& surchargeDetails.getSurchargePercentage().doubleValue() == 0) {
			return BigDecimal.ZERO;
		}

		// check that transaction amount is > minAmount.
		if (surchargeDetails.getMinTransactionAmount() != null
				&& surchargeDetails.getMinTransactionAmount().compareTo(amount) >= 0) {
			return BigDecimal.ZERO;
		}
		BigDecimal finalSurchargeAmt = (amount.multiply(surchargeDetails.getSurchargePercentage())
				.divide(percentagevalue));
		BigDecimal serviceTax = getCalculatedGst(surchargeDetails, amount, gst, finalSurchargeAmt);
		return new BigDecimal(df.format(finalSurchargeAmt.add(serviceTax)));
	}

	private static BigDecimal getSurchargeByFlag(Boolean flag, BigDecimal amount, BigDecimal percentageAmt) {
		if (amount.compareTo(percentageAmt) == 0) {
			return amount;
		}
		if (BooleanUtils.isFalse(flag)) {
			return amount.compareTo(percentageAmt) < 0 ? amount : percentageAmt;
		}
		return amount.compareTo(percentageAmt) > 0 ? amount : percentageAmt;
	}

	public BigDecimal[] fetchDCSurchargeDetails(String amount, String payId, String paymentsRegion, String gst) {

		BigDecimal actualTransactionAmount = new BigDecimal(amount);
		try {
			String paymentType = PaymentType.DEBIT_CARD.getName();
			SurchargeDetails surchargeDetailsFromDb = new SurchargeDetails();

			if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
					&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
							.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {

				surchargeDetailsFromDb = staticDataProvider.getSurchargeDetailsData(payId, paymentType, paymentsRegion);
			}

			else {
				surchargeDetailsFromDb = surchargeDetailsDao.findDetailsByRegion(payId, paymentType, paymentsRegion);
			}

			BigDecimal transSurchargeAmount = calculate(surchargeDetailsFromDb, amount, gst);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = transSurchargeAmount;
			surchargeDetails[1] = actualTransactionAmount.add(transSurchargeAmount);
			return surchargeDetails;
		} catch (Exception exception) {
			logger.error("Exception occured in fetchDCSurchargeDetails , " + exception);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = BigDecimal.ZERO;
			surchargeDetails[1] = actualTransactionAmount;
			return surchargeDetails;
		}

	}

	public BigDecimal[] fetchPCSurchargeDetails(String amount, String payId, String paymentsRegion, String gst) {

		BigDecimal actualTransactionAmount = new BigDecimal(amount);
		try {
			String paymentType = PaymentType.PREPAID_CARD.getName();

			SurchargeDetails surchargeDetailsFromDb = new SurchargeDetails();

			if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
					&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
							.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
				surchargeDetailsFromDb = staticDataProvider.getSurchargeDetailsData(payId, paymentType, paymentsRegion);

			} else {
				surchargeDetailsFromDb = surchargeDetailsDao.findDetailsByRegion(payId, paymentType, paymentsRegion);
			}

			BigDecimal transSurchargeAmount = calculate(surchargeDetailsFromDb, amount, gst);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = transSurchargeAmount;
			surchargeDetails[1] = actualTransactionAmount.add(transSurchargeAmount);
			return surchargeDetails;
		} catch (Exception exception) {
			logger.error("Exception occured in fetchPCSurchargeDetails , " + exception);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = BigDecimal.ZERO;
			surchargeDetails[1] = actualTransactionAmount;
			return surchargeDetails;
		}
	}

	public BigDecimal[] fetchQRSurchargeDetails(String amount, String payId, String paymentsRegion, String gst) {

		BigDecimal actualTransactionAmount = new BigDecimal(amount);
		try {
			String paymentType = PaymentType.QRCODE.getName();;
			SurchargeDetails surchargeDetailsFromDb = surchargeDetailsDao.findDetailsByRegion(payId, paymentType,
					paymentsRegion);

			BigDecimal transSurchargeAmount = calculate(surchargeDetailsFromDb, amount, gst);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = transSurchargeAmount;
			surchargeDetails[1] = actualTransactionAmount.add(transSurchargeAmount);
			return surchargeDetails;
		} catch (Exception exception) {
			logger.error("Exception occured in fetchQrSurchargeDetails , " + exception);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = BigDecimal.ZERO;
			surchargeDetails[1] = actualTransactionAmount;
			return surchargeDetails;
		}

	}
	public BigDecimal[] fetchNBSurchargeDetails(String amount, String payId, String paymentsRegion, String gst) {

		BigDecimal actualTransactionAmount = new BigDecimal(amount);
		try {
			String paymentType = "Net Banking";
			SurchargeDetails surchargeDetailsFromDb = surchargeDetailsDao.findDetailsByRegion(payId, paymentType,
					paymentsRegion);

			BigDecimal transSurchargeAmount = calculate(surchargeDetailsFromDb, amount, gst);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = transSurchargeAmount;
			surchargeDetails[1] = actualTransactionAmount.add(transSurchargeAmount);
			return surchargeDetails;
		} catch (Exception exception) {
			logger.error("Exception occured in fetchNBSurchargeDetails , " + exception);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = BigDecimal.ZERO;
			surchargeDetails[1] = actualTransactionAmount;
			return surchargeDetails;
		}

	}

	public BigDecimal[] fetchUPSurchargeDetails(String amount, String payId, String paymentsRegion, String gst) {

		BigDecimal actualTransactionAmount = new BigDecimal(amount);
		try {
			String paymentType = PaymentType.UPI.getName();
			SurchargeDetails surchargeDetailsFromDb = null;
			if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
					&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
							.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
				surchargeDetailsFromDb = staticDataProvider.getSurchargeDetailsData(payId, paymentType, paymentsRegion);
			} else {
				surchargeDetailsFromDb = surchargeDetailsDao.findDetailsByRegion(payId, paymentType, paymentsRegion);
			}
			BigDecimal transSurchargeAmount = calculate(surchargeDetailsFromDb, amount, gst);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = transSurchargeAmount;
			surchargeDetails[1] = actualTransactionAmount.add(transSurchargeAmount);
			logger.info("fetchUPSurchargeDetails:: amount = {}, transSurchargeAmount={}" , amount, transSurchargeAmount);
			return surchargeDetails;
		} catch (Exception exception) {
			logger.error("Exception occured in fetchUPSurchargeDetails , " + exception);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = BigDecimal.ZERO;
			surchargeDetails[1] = actualTransactionAmount;
			return surchargeDetails;
		}
	}

	public BigDecimal[] fetchWLSurchargeDetails(String amount, String payId, String paymentsRegion, String gst)
			throws SystemException {

		BigDecimal actualTransactionAmount = new BigDecimal(amount);
		try {
			logger.info("Getting fetchWLSurchargeDetails");
			String paymentType = PaymentType.WALLET.getName();
			SurchargeDetails surchargeDetailsFromDb = null;

			if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
					&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
							.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
				surchargeDetailsFromDb = staticDataProvider.getSurchargeDetailsData(payId, paymentType, paymentsRegion);
			} else {
				surchargeDetailsFromDb = surchargeDetailsDao.findDetailsByRegion(payId, paymentType, paymentsRegion);
			}

			BigDecimal transSurchargeAmount = calculate(surchargeDetailsFromDb, amount, gst);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = transSurchargeAmount;
			surchargeDetails[1] = actualTransactionAmount.add(transSurchargeAmount);
			return surchargeDetails;
		} catch (Exception exception) {
			logger.error("Exception occured in fetchWLSurchargeDetails , " + exception);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = BigDecimal.ZERO;
			surchargeDetails[1] = actualTransactionAmount;
			return surchargeDetails;
		}

	}

	public BigDecimal[] fetchADSurchargeDetails(String amount, String payId, String paymentsRegion, String gst) {

		BigDecimal actualTransactionAmount = new BigDecimal(amount);

		try {
			//String paymentType = PaymentType.AD.getName();
			SurchargeDetails surchargeDetailsFromDb = null;
			if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
					&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
							.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
			//	surchargeDetailsFromDb = staticDataProvider.getSurchargeDetailsData(payId, paymentType, paymentsRegion);
			} else {
				//surchargeDetailsFromDb = surchargeDetailsDao.findDetailsByRegion(payId, paymentType, paymentsRegion);
			}

			BigDecimal transSurchargeAmount = calculate(surchargeDetailsFromDb, amount, gst);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = transSurchargeAmount;
			surchargeDetails[1] = actualTransactionAmount.add(transSurchargeAmount);
			return surchargeDetails;
		} catch (Exception exception) {
			logger.error("Exception occured in fetchADSurchargeDetails , " + exception);
			BigDecimal surchargeDetails[] = new BigDecimal[2];
			surchargeDetails[0] = BigDecimal.ZERO;
			surchargeDetails[1] = actualTransactionAmount;
			return surchargeDetails;
		}
	}

	public BigDecimal getServiceTax(String payid) {
		try {

			BigDecimal servicetax = null;

			if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
					&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
							.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {

				User user = staticDataProvider.getUserData(payid);
				servicetax = staticDataProvider.getServiceTaxData(user.getIndustryCategory());

			} else {
				servicetax = serviceTaxDao.findServiceTaxByPayId(payid);
			}

			if (servicetax == null) {
				logger.info("Service tax found not for payId = " + payid);
			}
			return servicetax;
		} catch (Exception exception) {
			logger.error("Exception in getServiceTax " + exception);
			return null;
		}

	}
	
	@SuppressWarnings("static-access")
	private boolean skipGstAddition(BigDecimal amount, String paymentType) {

		// Check min amount to charge GST
		BigDecimal minTxnAmtForGst = BigDecimal.ZERO;
		if (StringUtils.isNotBlank(propertiesManager.propertiesMap.get("minTxnAmtForGst"))) {
			minTxnAmtForGst = new BigDecimal(propertiesManager.propertiesMap.get("minTxnAmtForGst"));
		}
		if (amount.compareTo(minTxnAmtForGst) <= 0) {
			if ((paymentType.equals(PaymentType.DEBIT_CARD.getName()))
					|| (paymentType.equals(PaymentType.CREDIT_CARD.getName()))
					|| (paymentType.equals(PaymentType.PREPAID_CARD.getName()))
					|| (paymentType.equals(PaymentType.NET_BANKING.getName()))
					|| (paymentType.equals(PaymentType.UPI.getName()))
					|| (paymentType.equals(PaymentType.WALLET.getName()))) {
				return true;
			}
			return false;
		}
		return false;
	}

}
