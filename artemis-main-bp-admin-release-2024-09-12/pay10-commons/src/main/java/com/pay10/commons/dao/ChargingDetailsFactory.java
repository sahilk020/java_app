package com.pay10.commons.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TDRStatus;
import com.pay10.commons.util.TransactionType;

/**
 * @author Puneet
 *
 */

@Component
public class ChargingDetailsFactory {
	private static Logger logger = LoggerFactory.getLogger(ChargingDetailsFactory.class.getName());

	@Autowired
	private UserDao userDao;

	@Autowired
	private ServiceTaxDao serviceTaxDao;
	
	@Autowired
	private CurrencyCodeDao currencyCodeDao;
	

	public Map<String, List<ChargingDetails>> getChargingDetailsMap(String merchantEmailId, String acquirerCode) {

		logger.info("inside ChargingDetailsFactory merchantEmailId = " + merchantEmailId + " acquirerCode =   "
				+ acquirerCode);
		Map<String, List<ChargingDetails>> chargingDetailsMap = new HashMap<String, List<ChargingDetails>>();
		Session session = null;
		try {
			User user = userDao.find(merchantEmailId);

			Account account = null;
			Set<Account> accounts = user.getAccounts();

			if (accounts == null || accounts.size() == 0) {
				logger.info("No account found for Pay ID = " + user.getPayId());
			} else {
				for (Account accountThis : accounts) {
					// logger.info(" acquirerCode "+acquirerCode +" ----------- accountThis
					// ------------- "+accountThis.toString());
					if (accountThis.getAcquirerName()
							.equalsIgnoreCase(AcquirerType.getInstancefromCode(acquirerCode).getName())) {
						account = accountThis;
						break;
					}
				}
			}
			// Get the charging details

			if (null != account) {

				session = HibernateSessionProvider.getSession();
				Transaction tx = session.beginTransaction();
				session.load(account, account.getId());
				Set<ChargingDetails> data = account.getChargingDetails();

				for (PaymentType paymentType : PaymentType.values()) {

					List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
					String paymentName = paymentType.getName();
					for (ChargingDetails cDetail : data) {

						if (cDetail.getStatus().equals(TDRStatus.ACTIVE)
								&& cDetail.getPaymentType().getName().equals(paymentName)) {

							cDetail.setMerchantServiceTax(
									serviceTaxDao.findServiceTaxByPayId(cDetail.getPayId()).longValue());
							// cDetail.setMerchantFixCharge(cDetail.getBankFixCharge() +
							// cDetail.getPgFixCharge());
							// cDetail.setMerchantTDR(cDetail.);
							chargingDetailsList.add(cDetail);
						}
					}

					if (chargingDetailsList.size() != 0) {

						Collections.sort(chargingDetailsList);
						chargingDetailsMap.put(paymentName, chargingDetailsList);
					}
				}
				tx.commit();
			}
		}

		finally {
			HibernateSessionProvider.closeSession(session);
		}

		return chargingDetailsMap;
	}

	public Map<String, List<TdrSetting>> getTdrSettingMap(String merchantEmailId, String acquirerCode,
			String paymentRegion, String cardHolderType,String currency) {

		logger.info("inside ChargingDetailsFactory merchantEmailId = " + merchantEmailId + " acquirerCode =   "
				+ acquirerCode + " currency =   "
				+ currency);
		// Map<String, List<ChargingDetails>> chargingDetailsMap = new HashMap<String,
		// List<ChargingDetails>>();
		Map<String, List<TdrSetting>> tdrSetting = new HashMap<String, List<TdrSetting>>();
		Session session = null;
		try {
			User user = userDao.find(merchantEmailId);

			Account account = null;
			Set<Account> accounts = user.getAccounts();

			if (accounts == null || accounts.size() == 0) {
				logger.info("No account found for Pay ID = " + user.getPayId());
			} else {
				for (Account accountThis : accounts) {
					// logger.info(" acquirerCode "+acquirerCode +" ----------- accountThis
					// ------------- "+accountThis.toString());
					if (accountThis.getAcquirerName()
							.equalsIgnoreCase(AcquirerType.getInstancefromCode(acquirerCode).getName())) {
						account = accountThis;
						break;
					}
				}
			}
			// Get the charging details

			if (null != account) {
				session = HibernateSessionProvider.getSession();
//				session = HibernateSessionProvider.getSession();
//				Transaction tx = session.beginTransaction();
//				session.load(account, account.getId());
				Set<TdrSetting> data = account.getTdrSetting();
//				logger.info("tdr Setting details.....={}",data);
				for (PaymentType paymentType : PaymentType.values()) {
					
					List<TdrSetting> chargingDetailsList = new ArrayList<TdrSetting>();
					String paymentName = paymentType.toString();
					for (TdrSetting cDetail : data) {
						cDetail.setIgst(serviceTaxDao.findServiceTaxByPayId(cDetail.getPayId()).doubleValue());
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
						Date fromDate = cDetail.getFromDate();
						if (cDetail.getStatus().equals(TDRStatus.ACTIVE.toString())
								&& cDetail.getPaymentType().equals(paymentName)
								&& cDetail.getPaymentRegion().equalsIgnoreCase(paymentRegion)
								&&cDetail.getCurrency().equals(currency)
								&& cDetail.getType().equalsIgnoreCase(cardHolderType)) {
							
							cDetail.setCurrency(currencyCodeDao.getCurrencyNamebyCode(currency));
							if (fromDate != null) {
								cDetail.setfDate(dateFormat.format(fromDate));
								chargingDetailsList.add(cDetail);
							} else {

								chargingDetailsList.add(cDetail);
							}
						}
					}
					
					
					if (chargingDetailsList.size() != 0) {

						Collections.sort(chargingDetailsList);
						tdrSetting.put(paymentName, chargingDetailsList);
					}

//				tx.commit();
				}
				System.out.println(tdrSetting.size());
			}

//		finally {
//			HibernateSessionProvider.closeSession(session);
//		}
		} catch (Exception e) {
			logger.error("Exception Occur inside :", e);
			e.printStackTrace();
		}
		return tdrSetting;
	}

	public ChargingDetails getSingleChargingDetail(Account account, Long charginDetailId) {
		for (ChargingDetails cDetail : account.getChargingDetails()) {
			if (cDetail.getId().equals(charginDetailId)) {
				return cDetail;
			}
		}
		return null;
	}

	public TdrSetting getSingleTdrSetting(Account account, Long charginDetailId) {
		for (TdrSetting cDetail : account.getTdrSetting()) {
			if (cDetail.getId().equals(charginDetailId)) {
				return cDetail;
			}
		}
		return null;
	}

	// supply names of payment type and mops and code of acquirer
	public ChargingDetails getChargingDetail(String date, String payId, String acquirer, String paymentType,
			String mopType, String txnType, String currencyCode) {
		ChargingDetails detail = null;

		List<ChargingDetails> chargingDetailsList = new ChargingDetailsDao().findDetail(date, payId,
				AcquirerType.getInstancefromCode(acquirer).getName(),
				PaymentType.getInstanceUsingCode(paymentType).toString(), MopType.getmopName(mopType), currencyCode);

		Iterator<ChargingDetails> chargingDetailsItr = chargingDetailsList.iterator();

		while (chargingDetailsItr.hasNext()) {
			if (paymentType.equals(PaymentType.NET_BANKING.getName())
					|| paymentType.equals(PaymentType.WALLET.getName())) {
				detail = chargingDetailsItr.next();
				break;
			} else {
				detail = chargingDetailsItr.next();
				if (detail.getTransactionType().toString().equals(txnType)) {
					break;
				}
			}
		}
		return detail;
	}

	public ChargingDetails getChargingDetailForReport(String date, String payId, String acquirer, String paymentType,
			String mopType, String txnType, String currencyCode) {
		ChargingDetails detail = null;

		List<ChargingDetails> chargingDetailsList = new ChargingDetailsDao().findDetail(date, payId,
				AcquirerType.getInstancefromCode(acquirer).getName(),
				PaymentType.getInstanceUsingCode(paymentType).toString(), MopType.getmop(mopType).toString(),
				currencyCode, txnType);

		Iterator<ChargingDetails> chargingDetailsItr = chargingDetailsList.iterator();

		while (chargingDetailsItr.hasNext()) {
			if (paymentType.equals(PaymentType.NET_BANKING.getName())
					|| paymentType.equals(PaymentType.WALLET.getName())) {
				detail = chargingDetailsItr.next();
				break;
			} else {
				detail = chargingDetailsItr.next();
				if (detail.getTransactionType().toString().equals(txnType)) {
					break;
				}
			}
		}
		return detail;
	}

	public static void main(String[] args) {
		System.out.println(AcquirerType.getInstancefromCode("KOTAK").getName());
	}
}
