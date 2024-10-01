package com.pay10.crm.actionBeans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.pay10.commons.user.*;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.ChargingDetailsFactory;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.CurrencyTypes;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PendingRequestEmailProcessor;
import com.pay10.commons.util.TDRStatus;
import com.pay10.commons.util.TransactionType;

@Service
public class ChargingDetailsMaintainer {

	@Autowired
	private UserDao userDao;

	@Autowired
	private ChargingDetailsDao chargingDetailsDao;

	@Autowired
	private TdrSettingDao tdrSettingDao;

	@Autowired
	private ChargingDetailsFactory chargingDetailProvider;

	@Autowired
	private PendingRequestEmailProcessor pendingRequestEmailProcessor;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	private static Logger logger = LoggerFactory.getLogger(ChargingDetailsMaintainer.class.getName());

	public ChargingDetailsMaintainer() {

	}

	public void editChargingDetail(String emailId, String acquirer, ChargingDetails chargingDetail, String userType,
			String loginUserEmailId, boolean isDomestic, boolean isConsumer) throws SystemException {
		Session session = null;
		try {
			User user = userDao.find(emailId);
			Account account = user.getAccountUsingAcquirerCode(acquirer);
			if (null == account) {
				throw new SystemException(ErrorType.ACQUIRER_NOT_FOUND,
						ErrorType.ACQUIRER_NOT_FOUND.getResponseMessage());
			}

			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			session.load(account, account.getId());

			ChargingDetails chargingDetailFromDb = chargingDetailProvider.getSingleChargingDetail(account,
					chargingDetail.getId());

			if (null == chargingDetailFromDb) {
				throw new SystemException(ErrorType.CHARGINGDETAIL_NOT_FETCHED,
						ErrorType.CHARGINGDETAIL_NOT_FETCHED.getResponseMessage());
			}

			if (chargingDetailFromDb.getCreatedDate() != null) { // deactivate current and add new charging details
				logger.info("deactivate current and add new charging details");
				editExistingChargingDetails(account, chargingDetailFromDb, chargingDetail, userType, loginUserEmailId,
						isDomestic, isConsumer);
				session.saveOrUpdate(account);
				tx.commit();
			} else { // Edit blank charging detail
				logger.info("Edit blank charging detail");
				chargingDetail = editBlankChargingDetails(account, chargingDetailFromDb, chargingDetail, userType,
						loginUserEmailId);
				chargingDetailsDao.update(chargingDetail);

				if (userType.equalsIgnoreCase("SUBADMIN")) {

					User loggedInUser = userDao.findPayIdByEmail(loginUserEmailId);
					String merchantBusinessName = userDao.getBusinessNameByPayId(chargingDetail.getPayId());
					// pendingRequestEmailProcessor.processTDRRequestEmail(chargingDetail.getStatus().getName(),
					// loginUserEmailId, userType, merchantBusinessName, chargingDetail.getPayId());

				}
			}
		} finally {
			HibernateSessionProvider.closeSession(session);
		}
	}

	public String editTdrSetting(TdrSetting tdrSetting, String emailId, String userType, String loginUserEmailId)
			throws SystemException {
		String responseMessage = "Successfully Saved";
		Session session = null;
		try {

			User user = userDao.find(emailId);
			Account account = user.getAccountUsingAcquirerCode(tdrSetting.getAcquirerName());
			if (null == account) {
				throw new SystemException(ErrorType.ACQUIRER_NOT_FOUND,
						ErrorType.ACQUIRER_NOT_FOUND.getResponseMessage());
			}
			try {
				Integer.parseInt(tdrSetting.getCurrency());
			}catch (NumberFormatException e){
				tdrSetting.setCurrency(multCurrencyCodeDao.getCurrencyCodeByName(tdrSetting.getCurrency()));
			}
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			
			session.load(account, account.getId());

			TdrSetting chargingDetailFromDb = chargingDetailProvider.getSingleTdrSetting(account, tdrSetting.getId());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				if (null == chargingDetailFromDb) {
					if (tdrSettingDao.findTdrByPaymentTypeAndPayidAndMinTransactionAmountAndMaxTransaction(
							tdrSetting.getPaymentType(), tdrSetting.getPayId(), tdrSetting.getMopType(),
							dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))),
							tdrSetting.getEnableSurcharge(), tdrSetting.getMinTransactionAmount(),
							tdrSetting.getMaxTransactionAmount(), tdrSetting.getPaymentRegion(),
							
							tdrSetting.getType(),tdrSetting.getAcquirerName() ,tdrSetting.getCurrency())) {
						responseMessage = "You Cannot Insert Same Detail On Same Min and Max Amount Per Transaction";
					} else {
						if (tdrSetting.getEnableSurcharge() == true) {
							if (tdrSettingDao.findSurcharge(tdrSetting.getPayId(), tdrSetting.getPaymentType(),
									dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))),
									tdrSetting.getEnableSurcharge(), tdrSetting.getMinTransactionAmount(),
									tdrSetting.getMaxTransactionAmount(), tdrSetting.getMerchantTdr(),
									tdrSetting.getPaymentRegion(), tdrSetting.getType(),tdrSetting.getCurrency())) {
								responseMessage = "You Cannot Enable Surcharge on Same PaymentType with different merchant Mdr and Min and Max Amount Per Transaction";
							} else {
								TdrSetting active = new TdrSetting();
								active.setAcquirerName(tdrSetting.getAcquirerName());
								active.setBankMaxTdrAmt(tdrSetting.getBankMaxTdrAmt());
								active.setBankMinTdrAmt(tdrSetting.getBankMinTdrAmt());
								active.setBankTdr(tdrSetting.getBankTdr());
								active.setCurrency(tdrSetting.getCurrency());
								active.setEnableSurcharge(tdrSetting.getEnableSurcharge());
								active.setFromDate(
										dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))));
								active.setIgst(tdrSetting.getIgst());
								active.setMaxTransactionAmount(tdrSetting.getMaxTransactionAmount());
								active.setMinTransactionAmount(tdrSetting.getMinTransactionAmount());
								active.setMerchantMaxTdrAmt(tdrSetting.getMerchantMaxTdrAmt());
								active.setMerchantMinTdrAmt(tdrSetting.getMerchantMinTdrAmt());
								active.setMerchantTdr(tdrSetting.getMerchantTdr());
								active.setMopType(tdrSetting.getMopType());
								active.setPayId(tdrSetting.getPayId());
								active.setPaymentRegion(tdrSetting.getPaymentRegion());
								active.setPaymentType(tdrSetting.getPaymentType());
								active.setBankPreference(tdrSetting.getBankPreference());
								active.setMerchantPreference(tdrSetting.getMerchantPreference());
								active.setTdrStatus(TDRStatus.ACTIVE.toString());
								active.setStatus(TDRStatus.ACTIVE.toString());

								active.setTransactionType(TransactionType.SALE.getName());
								active.setType(tdrSetting.getType());
								active.setUpdatedBy(loginUserEmailId);
								active.setUpdatedAt(new Date());
								logger.info("action-CDM 1 ADD TDR");
								account.addTdrSetting(active);
								session.saveOrUpdate(account);
								tx.commit();
							}
						} else {
							TdrSetting active = new TdrSetting();
							active.setAcquirerName(tdrSetting.getAcquirerName());
							active.setBankMaxTdrAmt(tdrSetting.getBankMaxTdrAmt());
							active.setBankMinTdrAmt(tdrSetting.getBankMinTdrAmt());
							active.setBankTdr(tdrSetting.getBankTdr());
							active.setCurrency(tdrSetting.getCurrency());
							active.setEnableSurcharge(tdrSetting.getEnableSurcharge());
							active.setFromDate(
									dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))));
							active.setIgst(tdrSetting.getIgst());
							active.setMaxTransactionAmount(tdrSetting.getMaxTransactionAmount());
							active.setMinTransactionAmount(tdrSetting.getMinTransactionAmount());
							active.setMerchantMaxTdrAmt(tdrSetting.getMerchantMaxTdrAmt());
							active.setMerchantMinTdrAmt(tdrSetting.getMerchantMinTdrAmt());
							active.setMerchantTdr(tdrSetting.getMerchantTdr());
							active.setMopType(tdrSetting.getMopType());
							active.setPayId(tdrSetting.getPayId());
							active.setPaymentRegion(tdrSetting.getPaymentRegion());
							active.setPaymentType(tdrSetting.getPaymentType());
							active.setBankPreference(tdrSetting.getBankPreference());
							active.setMerchantPreference(tdrSetting.getMerchantPreference());
							active.setTdrStatus(TDRStatus.ACTIVE.toString());
							active.setStatus(TDRStatus.ACTIVE.toString());

							active.setTransactionType(TransactionType.SALE.getName());
							active.setType(tdrSetting.getType());
							active.setUpdatedBy(loginUserEmailId);
							active.setUpdatedAt(new Date());
							logger.info("action-CDM 2 ADD TDR");
							account.addTdrSetting(active);
							session.saveOrUpdate(account);
							tx.commit();
						}
					}
				} else if (chargingDetailFromDb.getFromDate() != null) { // deactivate current and add new charging
																		// details
					logger.info("deactivate current and add new charging details");
					chargingDetailFromDb.setUpdatedBy(loginUserEmailId);
					chargingDetailFromDb.setTdrStatus(TDRStatus.INACTIVE.toString());
					chargingDetailFromDb.setStatus(TDRStatus.INACTIVE.toString());
					tdrSettingDao.update(chargingDetailFromDb);
					tx.commit();
					

					if (tdrSettingDao.findTdrByPaymentTypeAndPayidAndMinTransactionAmountAndMaxTransaction(
							tdrSetting.getPaymentType(), tdrSetting.getPayId(), tdrSetting.getMopType(),
							dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))),
							tdrSetting.getEnableSurcharge(), tdrSetting.getMinTransactionAmount(),
							tdrSetting.getMaxTransactionAmount(), tdrSetting.getPaymentRegion(),
							tdrSetting.getType(),tdrSetting.getAcquirerName(),tdrSetting.getCurrency())) {
						responseMessage = "You Cannot Insert Same Detail On Same Min and Max Amount Per Transaction";
						Transaction tx10 = session.beginTransaction();
						
						logger.info("Activate current charging details");
						chargingDetailFromDb.setUpdatedBy(loginUserEmailId);
						chargingDetailFromDb.setTdrStatus(TDRStatus.INACTIVE.toString());
						chargingDetailFromDb.setStatus(TDRStatus.INACTIVE.toString());
						tdrSettingDao.update(chargingDetailFromDb);
						tx10.commit();
					} else {
						if (tdrSetting.getEnableSurcharge() == true) {
							if (tdrSettingDao.findSurcharge(tdrSetting.getPayId(), tdrSetting.getPaymentType(),
									dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))),
									tdrSetting.getEnableSurcharge(), tdrSetting.getMinTransactionAmount(),
									tdrSetting.getMaxTransactionAmount(), tdrSetting.getMerchantTdr(),
									tdrSetting.getPaymentRegion(), tdrSetting.getType(),tdrSetting.getCurrency())) {
								responseMessage = "You Cannot Enable Surcharge on Same PaymentType with different merchant Mdr and Min and Max Amount Per Transaction";
								Transaction tx11 = session.beginTransaction();
								logger.info("Activate current charging details");
								chargingDetailFromDb.setUpdatedBy(loginUserEmailId);
								chargingDetailFromDb.setTdrStatus(TDRStatus.INACTIVE.toString());
								chargingDetailFromDb.setStatus(TDRStatus.INACTIVE.toString());
								tdrSettingDao.update(chargingDetailFromDb);
								tx11.commit();
							} else {
								
								
								
								logger.info("Edit blank charging detail");
								Transaction tx1 = session.beginTransaction();
								TdrSetting active = new TdrSetting();
								active.setAcquirerName(tdrSetting.getAcquirerName());
								active.setBankMaxTdrAmt(tdrSetting.getBankMaxTdrAmt());
								active.setBankMinTdrAmt(tdrSetting.getBankMinTdrAmt());
								active.setBankTdr(tdrSetting.getBankTdr());
								active.setCurrency(tdrSetting.getCurrency());
								active.setEnableSurcharge(tdrSetting.getEnableSurcharge());
								active.setIgst(tdrSetting.getIgst());
								active.setMaxTransactionAmount(tdrSetting.getMaxTransactionAmount());
								active.setMinTransactionAmount(tdrSetting.getMinTransactionAmount());
								active.setMerchantMaxTdrAmt(tdrSetting.getMerchantMaxTdrAmt());
								active.setMerchantMinTdrAmt(tdrSetting.getMerchantMinTdrAmt());
								active.setMerchantTdr(tdrSetting.getMerchantTdr());
								active.setMopType(tdrSetting.getMopType());
								active.setPayId(tdrSetting.getPayId());
								active.setPaymentRegion(tdrSetting.getPaymentRegion());
								active.setPaymentType(tdrSetting.getPaymentType());
								active.setBankPreference(tdrSetting.getBankPreference());
								active.setMerchantPreference(tdrSetting.getMerchantPreference());
								active.setTdrStatus(TDRStatus.ACTIVE.toString());
								active.setStatus(TDRStatus.ACTIVE.toString());
								active.setFromDate(
										dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))));
								active.setTransactionType(TransactionType.SALE.getName());
								active.setType(tdrSetting.getType());
								active.setUpdatedBy(loginUserEmailId);
								active.setUpdatedAt(new Date());
								logger.info("action-CDM 3 ADD TDR");
								account.addTdrSetting(active);
								session.save(account);
								tx1.commit();
							}
						} else {
							Transaction tx1 = session.beginTransaction();
							TdrSetting active = new TdrSetting();
							active.setAcquirerName(tdrSetting.getAcquirerName());
							active.setBankMaxTdrAmt(tdrSetting.getBankMaxTdrAmt());
							active.setBankMinTdrAmt(tdrSetting.getBankMinTdrAmt());
							active.setBankTdr(tdrSetting.getBankTdr());
							active.setCurrency(tdrSetting.getCurrency());
							active.setEnableSurcharge(tdrSetting.getEnableSurcharge());
							active.setIgst(tdrSetting.getIgst());
							active.setMaxTransactionAmount(tdrSetting.getMaxTransactionAmount());
							active.setMinTransactionAmount(tdrSetting.getMinTransactionAmount());
							active.setMerchantMaxTdrAmt(tdrSetting.getMerchantMaxTdrAmt());
							active.setMerchantMinTdrAmt(tdrSetting.getMerchantMinTdrAmt());
							active.setMerchantTdr(tdrSetting.getMerchantTdr());
							active.setMopType(tdrSetting.getMopType());
							active.setPayId(tdrSetting.getPayId());
							active.setPaymentRegion(tdrSetting.getPaymentRegion());
							active.setPaymentType(tdrSetting.getPaymentType());
							active.setBankPreference(tdrSetting.getBankPreference());
							active.setMerchantPreference(tdrSetting.getMerchantPreference());

							active.setTdrStatus(TDRStatus.ACTIVE.toString());
							active.setStatus(TDRStatus.ACTIVE.toString());
							active.setFromDate(
									dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))));
							active.setTransactionType(TransactionType.SALE.getName());
							active.setType(tdrSetting.getType());
							active.setUpdatedBy(loginUserEmailId);
							active.setUpdatedAt(new Date());
							logger.info("action-CDM 4 ADD TDR");
							account.addTdrSetting(active);
							session.save(account);
							tx1.commit();
						}
					}
				} else { // Edit blank charging detail

					if (tdrSettingDao.findTdrByPaymentTypeAndPayidAndMinTransactionAmountAndMaxTransaction(
							tdrSetting.getPaymentType(), tdrSetting.getPayId(), tdrSetting.getMopType(),
							dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))),
							tdrSetting.getEnableSurcharge(), tdrSetting.getMinTransactionAmount(),
							tdrSetting.getMaxTransactionAmount(), tdrSetting.getPaymentRegion(),
							tdrSetting.getType(),tdrSetting.getAcquirerName(),tdrSetting.getCurrency())) {
						responseMessage = "You Cannot Insert Same Detail On Same Min and Max Amount Per Transaction";
					} else {
						if (tdrSetting.getEnableSurcharge() == true) {
							if (tdrSettingDao.findSurcharge(tdrSetting.getPayId(), tdrSetting.getPaymentType(),
									dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))),
									tdrSetting.getEnableSurcharge(), tdrSetting.getMinTransactionAmount(),
									tdrSetting.getMaxTransactionAmount(), tdrSetting.getMerchantTdr(),
									tdrSetting.getPaymentRegion(), tdrSetting.getType(),tdrSetting.getCurrency())) {
								responseMessage = "You Cannot Enable Surcharge on Same PaymentType with different merchant Mdr and Min and Max Amount Per Transaction";
							} else {
								logger.info("Edit blank charging detail");
								tdrSetting.setId(chargingDetailFromDb.getId());
								tdrSetting.setCurrency(tdrSetting.getCurrency());
								tdrSetting.setFromDate(
										dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))));
								tdrSetting.setTransactionType(TransactionType.SALE.getName());
								tdrSetting.setUpdatedBy(loginUserEmailId);
								tdrSetting.setTdrStatus(TDRStatus.ACTIVE.toString());
								tdrSetting.setStatus(TDRStatus.ACTIVE.toString());
								tdrSetting.setUpdatedAt(new Date());
								tdrSettingDao.update(tdrSetting);
								tx.commit();
							}
						} else {
							logger.info("Edit blank charging detail");
							tdrSetting.setId(chargingDetailFromDb.getId());
							tdrSetting.setCurrency(tdrSetting.getCurrency());
							tdrSetting.setFromDate(
									dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))));
							tdrSetting.setTransactionType(TransactionType.SALE.getName());
							tdrSetting.setUpdatedBy(loginUserEmailId);
							tdrSetting.setTdrStatus(TDRStatus.ACTIVE.toString());
							tdrSetting.setStatus(TDRStatus.ACTIVE.toString());
							tdrSetting.setUpdatedAt(new Date());
							tdrSettingDao.update(tdrSetting);
							tx.commit();
						}
					}

				}
			} catch (Exception e) {
				logger.error("Exception Occur inside editTdrSetting() :", e);
				e.printStackTrace();
			}
		} finally {
			HibernateSessionProvider.closeSession(session);
		}
		return responseMessage;
	}

	public String editTdrSettingAll(TdrSetting tdrSetting, String emailId, String userType, String loginUserEmailId)
			throws SystemException {
		String responseMessage = "Successfully Saved";
		int count=0;
		Session session = null;
		try {
			User user = userDao.find(emailId);
			Account account = user.getAccountUsingAcquirerCode(tdrSetting.getAcquirerName());
			if (null == account) {
				throw new SystemException(ErrorType.ACQUIRER_NOT_FOUND,
						ErrorType.ACQUIRER_NOT_FOUND.getResponseMessage());
			}

			session = HibernateSessionProvider.getSession();
			
			session.load(account, account.getId());
			
			List<TdrSetting>tdrSettings=tdrSettingDao.getAllTdrByPayIdAndAcquirerTypeAndPaymentTypeAndPaymentRegionAndType(tdrSetting.getPayId(),tdrSetting.getAcquirerName(),tdrSetting.getPaymentType(),tdrSetting.getPaymentRegion(),tdrSetting.getType(),tdrSetting.getCurrency());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				for (TdrSetting setting : tdrSettings) {
					TdrSetting chargingDetailFromDb = chargingDetailProvider.getSingleTdrSetting(account, setting.getId());
					if (chargingDetailFromDb.getTdrStatus().equalsIgnoreCase("PENDING") && chargingDetailFromDb.getStatus().equalsIgnoreCase("ACTIVE")) {
						Transaction tx = session.beginTransaction();
						chargingDetailFromDb.setMinTransactionAmount(tdrSetting.getMinTransactionAmount());
						chargingDetailFromDb.setMaxTransactionAmount(tdrSetting.getMaxTransactionAmount());
						
						
						chargingDetailFromDb.setBankPreference(tdrSetting.getBankPreference());
						chargingDetailFromDb.setBankTdr(tdrSetting.getBankTdr());
						chargingDetailFromDb.setBankMinTdrAmt(tdrSetting.getBankMinTdrAmt());
						chargingDetailFromDb.setBankMaxTdrAmt(tdrSetting.getBankMaxTdrAmt());
						
						chargingDetailFromDb.setMerchantPreference(tdrSetting.getMerchantPreference());
						chargingDetailFromDb.setMerchantTdr(tdrSetting.getMerchantTdr());
						chargingDetailFromDb.setMerchantMinTdrAmt(tdrSetting.getMerchantMinTdrAmt());
						chargingDetailFromDb.setMerchantMaxTdrAmt(tdrSetting.getMerchantMaxTdrAmt());
						
						chargingDetailFromDb.setFromDate(
								dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))));

						chargingDetailFromDb.setCurrency(tdrSetting.getCurrency());
						chargingDetailFromDb.setEnableSurcharge(tdrSetting.getEnableSurcharge());
						chargingDetailFromDb.setIgst(tdrSetting.getIgst());
						
					
						
						
						
						
						chargingDetailFromDb.setPaymentRegion(tdrSetting.getPaymentRegion());
						chargingDetailFromDb.setPaymentType(tdrSetting.getPaymentType());
						chargingDetailFromDb.setUpdatedAt(new Date());
						

						chargingDetailFromDb.setTdrStatus(TDRStatus.ACTIVE.toString());
						chargingDetailFromDb.setStatus(TDRStatus.ACTIVE.toString());
						
						chargingDetailFromDb.setTransactionType(TransactionType.SALE.getName());
						chargingDetailFromDb.setType(tdrSetting.getType());
						chargingDetailFromDb.setUpdatedBy(loginUserEmailId);
						
						tdrSettingDao.update(chargingDetailFromDb);
						tx.commit();
						count++;
					}else if(chargingDetailFromDb.getTdrStatus().equalsIgnoreCase("ACTIVE") && chargingDetailFromDb.getStatus().equalsIgnoreCase("ACTIVE")) {
							Transaction tx = session.beginTransaction();
							chargingDetailFromDb.setTdrStatus(TDRStatus.INACTIVE.toString());
							chargingDetailFromDb.setStatus(TDRStatus.INACTIVE.toString());
							
							tdrSettingDao.update(chargingDetailFromDb);
							tx.commit();
							
							Transaction tx1 = session.beginTransaction();
							
							TdrSetting newTdrSetting=new TdrSetting();
							
							newTdrSetting.setMinTransactionAmount(tdrSetting.getMinTransactionAmount());
							newTdrSetting.setMaxTransactionAmount(tdrSetting.getMaxTransactionAmount());
							
							
							newTdrSetting.setBankPreference(tdrSetting.getBankPreference());
							newTdrSetting.setBankTdr(tdrSetting.getBankTdr());
							newTdrSetting.setBankMinTdrAmt(tdrSetting.getBankMinTdrAmt());
							newTdrSetting.setBankMaxTdrAmt(tdrSetting.getBankMaxTdrAmt());
							
							newTdrSetting.setMerchantPreference(tdrSetting.getMerchantPreference());
							newTdrSetting.setMerchantTdr(tdrSetting.getMerchantTdr());
							newTdrSetting.setMerchantMinTdrAmt(tdrSetting.getMerchantMinTdrAmt());
							newTdrSetting.setMerchantMaxTdrAmt(tdrSetting.getMerchantMaxTdrAmt());
							
							newTdrSetting.setFromDate(
									dateFormat1.parse(dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))));

							newTdrSetting.setCurrency(tdrSetting.getCurrency());
							newTdrSetting.setEnableSurcharge(tdrSetting.getEnableSurcharge());
							newTdrSetting.setIgst(tdrSetting.getIgst());
							
						
							
							
							newTdrSetting.setAcquirerName(chargingDetailFromDb.getAcquirerName());
							newTdrSetting.setPayId(chargingDetailFromDb.getPayId());
							newTdrSetting.setPaymentType(chargingDetailFromDb.getPaymentType());
							newTdrSetting.setMopType(chargingDetailFromDb.getMopType());
							
							newTdrSetting.setPaymentRegion(tdrSetting.getPaymentRegion());
							newTdrSetting.setPaymentType(tdrSetting.getPaymentType());
							
							

							newTdrSetting.setTdrStatus(TDRStatus.ACTIVE.toString());
							newTdrSetting.setStatus(TDRStatus.ACTIVE.toString());
							newTdrSetting.setUpdatedAt(new Date());
							newTdrSetting.setTransactionType(TransactionType.SALE.getName());
							newTdrSetting.setType(tdrSetting.getType());
							newTdrSetting.setUpdatedBy(loginUserEmailId);
						logger.info("action-CDM 5 ADD TDR");
							account.addTdrSetting(newTdrSetting);
							session.save(account);
							tx1.commit();
							count++;
						}
					}
					
			
				
				
			} catch (Exception e) {
				logger.error("Exception Occur inside editTdrSetting() :", e);
				e.printStackTrace();
			}
		} finally {
			HibernateSessionProvider.closeSession(session);
		}
		return responseMessage+"\t"+String.valueOf(count)+" Setting Save";
	}
	public void editAllChargingDetails(String userEmail, String acquirer, ChargingDetails newChargingDetails,
			String userType, String userBy) throws SystemException {
		Session session = null;
		Date currentDate = new Date();
		try {
			User user = userDao.find(userEmail);
			Account account = user.getAccountUsingAcquirerCode(acquirer);
			if (null == account) {
				throw new SystemException(ErrorType.ACQUIRER_NOT_FOUND,
						ErrorType.ACQUIRER_NOT_FOUND.getResponseMessage());
			}
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			session.load(account, account.getId());
			Set<ChargingDetails> chargingDetailList = account.getChargingDetails();

			List<ChargingDetails> newChargingDetailsList = new ArrayList<ChargingDetails>();
			for (ChargingDetails chargingDetailFromDb : chargingDetailList) {
				if (chargingDetailFromDb.getStatus().getName().equals(TDRStatus.ACTIVE.getName())
						&& chargingDetailFromDb.getPaymentType().getCode().equals(PaymentType.NET_BANKING.getCode())) {

					// edit only net-banking charging details
					if (!(chargingDetailFromDb.getMerchantTDR() == 0.0)) { // deactivate current and add new charging
																			// details
						// editExistingChargingDetails(account, chargingDetailFromDb,
						// newChargingDetails);
						chargingDetailFromDb.setStatus(TDRStatus.INACTIVE);
						chargingDetailFromDb.setUpdatedDate(currentDate);

						ChargingDetails newChargingDetail = SerializationUtils.clone(newChargingDetails);
						newChargingDetail.setAcquirerName(chargingDetailFromDb.getAcquirerName());
						newChargingDetail.setPayId(chargingDetailFromDb.getPayId());
						newChargingDetail.setPgServiceTax(newChargingDetails.getMerchantServiceTax());
						newChargingDetail.setBankServiceTax(newChargingDetails.getMerchantServiceTax());

						/*
						 * if (userType.equals(UserType.ADMIN.toString())) {
						 * newChargingDetail.setStatus(TDRStatus.ACTIVE);
						 * newChargingDetail.setTdrStatus(TDRStatus.ACTIVE); // change for tdr } else {
						 * newChargingDetail.setStatus(TDRStatus.PENDING); }
						 */

						newChargingDetail.setStatus(TDRStatus.ACTIVE);
						newChargingDetail.setTdrStatus(TDRStatus.ACTIVE);

						newChargingDetail.setCreatedDate(currentDate);
						newChargingDetail.setMopType(chargingDetailFromDb.getMopType());
						newChargingDetail.setId(null);
						newChargingDetail.setUpdateBy(userBy);
						newChargingDetail.setUpdatedDate(new Date());
						newChargingDetailsList.add(newChargingDetail);
					} else {// Edit blank charging detail

						chargingDetailFromDb.setAllowFixCharge(newChargingDetails.isAllowFixCharge());
						chargingDetailFromDb.setFixChargeLimit(newChargingDetails.getFixChargeLimit());
						chargingDetailFromDb.setBankFixCharge(newChargingDetails.getBankFixCharge());
						chargingDetailFromDb.setBankFixChargeAFC(newChargingDetails.getBankFixChargeAFC());
						chargingDetailFromDb.setBankServiceTax(newChargingDetails.getBankServiceTax());
						chargingDetailFromDb.setBankTDR(newChargingDetails.getBankTDR());
						chargingDetailFromDb.setBankTDRAFC(newChargingDetails.getBankTDRAFC());
						chargingDetailFromDb.setMerchantFixCharge(newChargingDetails.getMerchantFixCharge());
						chargingDetailFromDb.setMerchantFixChargeAFC(newChargingDetails.getMerchantFixChargeAFC());
						chargingDetailFromDb.setMerchantServiceTax(newChargingDetails.getMerchantServiceTax());
						chargingDetailFromDb.setMerchantTDR(newChargingDetails.getMerchantTDR());
						chargingDetailFromDb.setMerchantTDRAFC(newChargingDetails.getMerchantTDRAFC());
						chargingDetailFromDb.setPgFixCharge(newChargingDetails.getPgFixCharge());
						chargingDetailFromDb.setPgFixChargeAFC(newChargingDetails.getPgChargeAFC());
						chargingDetailFromDb.setPgServiceTax(newChargingDetails.getPgServiceTax());
						chargingDetailFromDb.setPgTDR(newChargingDetails.getPgTDR());
						chargingDetailFromDb.setPgTDRAFC(newChargingDetails.getPgTDRAFC());
						chargingDetailFromDb.setCreatedDate(new Date());
						chargingDetailFromDb.setUpdateBy(userBy);
						chargingDetailFromDb.setUpdatedDate(new Date());
						chargingDetailFromDb.setTdrStatus(TDRStatus.ACTIVE); //// change for tdr
					}
				}
			}
			for (ChargingDetails newChargingDetailFromList : newChargingDetailsList) {
				account.addChargingDetail(newChargingDetailFromList);
			}
			session.saveOrUpdate(account);
			tx.commit();
		} finally {
			HibernateSessionProvider.closeSession(session);
		}
	}

	public void editExistingChargingDetails(Account account, ChargingDetails chargingDetailFromDb,
			ChargingDetails newChargingDetails, String userType, String loginUserEmailId, boolean isDomestic,
			boolean isConsumer) {
		Date currentDate = new Date();

		logger.info("chargingDetailFromDb " + chargingDetailFromDb.toString());
		logger.info("newChargingDetails " + newChargingDetails.toString());

		if (chargingDetailFromDb.getCreatedDate() != null) {
			chargingDetailFromDb.setStatus(TDRStatus.INACTIVE);
			chargingDetailFromDb.setUpdatedDate(currentDate);
			chargingDetailFromDb.setUpdateBy(loginUserEmailId);

			ChargingDetails pendingChargingDetail = new ChargingDetails();

			pendingChargingDetail = chargingDetailsDao.findPendingChargingDetail(chargingDetailFromDb.getMopType(),
					chargingDetailFromDb.getPaymentType(), chargingDetailFromDb.getTransactionType(),
					chargingDetailFromDb.getAcquirerName(), chargingDetailFromDb.getCurrency(),
					chargingDetailFromDb.getPayId());

			if (null != pendingChargingDetail) {
				Session session = null;
				if (pendingChargingDetail != null) {
					Long id = pendingChargingDetail.getId();
					session = HibernateSessionProvider.getSession();
					Transaction tx = session.beginTransaction();
					session.load(pendingChargingDetail, pendingChargingDetail.getId());
					try {
						ChargingDetails pendingChargingDetails = session.get(ChargingDetails.class, id);
						pendingChargingDetails.setStatus(TDRStatus.CANCELLED);
						pendingChargingDetails.setUpdatedDate(currentDate);
						pendingChargingDetail.setUpdateBy(loginUserEmailId);
						session.update(pendingChargingDetails);
						tx.commit();
					} catch (HibernateException e) {
						if (tx != null)
							tx.rollback();
						e.printStackTrace();
					} finally {
						session.close();
					}
				}
			}
			ChargingDetails newChargingDetail = SerializationUtils.clone(chargingDetailFromDb);
			newChargingDetail.setAcquirerName(chargingDetailFromDb.getAcquirerName());
			newChargingDetail.setPgServiceTax(newChargingDetails.getMerchantServiceTax());
			newChargingDetail.setBankServiceTax(newChargingDetails.getMerchantServiceTax());

			newChargingDetail.setStatus(TDRStatus.ACTIVE);
			newChargingDetail.setTdrStatus(TDRStatus.ACTIVE);
			newChargingDetail.setUpdateBy(loginUserEmailId);
			newChargingDetail.setRequestedBy(loginUserEmailId);
			if (isDomestic && isConsumer) {
				newChargingDetail.setBankTDR(newChargingDetails.getBankTDR());
				newChargingDetail.setBankFixCharge(newChargingDetails.getBankFixCharge());
				newChargingDetail.setBankFixChargeAFC(newChargingDetails.getBankFixChargeAFC());
				newChargingDetail.setBankTDRAFC(newChargingDetails.getBankTDRAFC());
				newChargingDetail.setMerchantTDR(newChargingDetails.getMerchantTDR());
				newChargingDetail.setMerchantFixCharge(newChargingDetails.getMerchantFixCharge());
				newChargingDetail.setMerchantFixChargeAFC(newChargingDetails.getMerchantFixChargeAFC());
				newChargingDetail.setMerchantTDRAFC(newChargingDetails.getMerchantTDRAFC());
				newChargingDetail.setPgTDR(newChargingDetails.getPgTDR());
				newChargingDetail.setPgFixCharge(newChargingDetails.getPgFixCharge());
				newChargingDetail.setPgFixChargeAFC(newChargingDetails.getPgFixChargeAFC());
				newChargingDetail.setPgTDRAFC(newChargingDetails.getPgTDRAFC());
			} else if (isDomestic && !isConsumer) {

				newChargingDetail.setBankTDRDomComm(newChargingDetails.getBankTDRDomComm());
				newChargingDetail.setBankFixChargeDomComm(newChargingDetails.getBankFixChargeDomComm());
				newChargingDetail.setBankFixChargeAFCDomComm(newChargingDetails.getBankFixChargeAFCDomComm());
				newChargingDetail.setBankTDRAFCDomComm(newChargingDetails.getBankTDRAFCDomComm());
				newChargingDetail.setMerchantTDRDomComm(newChargingDetails.getMerchantTDRDomComm());
				newChargingDetail.setMerchantFixChargeDomComm(newChargingDetails.getMerchantFixChargeDomComm());
				newChargingDetail.setMerchantFixChargeAFCDomComm(newChargingDetails.getMerchantFixChargeAFCDomComm());
				newChargingDetail.setMerchantTDRAFCDomComm(newChargingDetails.getMerchantTDRAFCDomComm());
				newChargingDetail.setPgTDRDomComm(newChargingDetails.getPgTDRDomComm());
				newChargingDetail.setPgFixChargeDomComm(newChargingDetails.getPgFixChargeDomComm());
				newChargingDetail.setPgFixChargeAFCDomComm(newChargingDetails.getPgFixChargeAFCDomComm());
				newChargingDetail.setPgTDRAFCDomComm(newChargingDetails.getPgTDRAFCDomComm());

			} else if (!isDomestic && isConsumer) {
				newChargingDetail.setBankTDRIntCons(newChargingDetails.getBankTDRIntCons());
				newChargingDetail.setBankFixChargeIntCons(newChargingDetails.getBankFixChargeIntCons());
				newChargingDetail.setBankFixChargeAFCIntCons(newChargingDetails.getBankFixChargeAFCIntCons());
				newChargingDetail.setBankTDRAFCIntCons(newChargingDetail.getBankTDRAFCIntCons());
				newChargingDetail.setMerchantTDRIntCons(newChargingDetails.getMerchantTDRIntCons());
				newChargingDetail.setMerchantFixChargeIntCons(newChargingDetails.getMerchantFixChargeIntCons());
				newChargingDetail.setMerchantFixChargeAFCIntCons(newChargingDetails.getMerchantFixChargeAFCIntCons());
				newChargingDetail.setMerchantTDRAFCIntCons(newChargingDetails.getMerchantTDRAFCIntCons());
				newChargingDetail.setPgTDRIntCons(newChargingDetails.getPgTDRIntCons());
				newChargingDetail.setPgFixChargeIntCons(newChargingDetails.getPgFixChargeIntCons());
				newChargingDetail.setPgFixChargeAFCIntCons(newChargingDetails.getPgFixChargeAFCIntCons());
				newChargingDetail.setPgTDRAFCIntCons(newChargingDetails.getPgTDRAFCIntCons());

			} else if (!isDomestic && !isConsumer) {
				newChargingDetail.setBankTDRIntComm(newChargingDetails.getBankTDRIntComm());
				newChargingDetail.setBankFixChargeIntComm(newChargingDetails.getBankFixChargeIntComm());
				newChargingDetail.setBankFixChargeAFCIntComm(newChargingDetails.getBankFixChargeAFCIntComm());
				newChargingDetail.setBankTDRAFCIntComm(newChargingDetails.getBankTDRAFCIntComm());
				newChargingDetail.setMerchantTDRIntComm(newChargingDetails.getMerchantTDRIntComm());
				newChargingDetail.setMerchantFixChargeIntComm(newChargingDetails.getMerchantFixChargeIntComm());
				newChargingDetail.setMerchantFixChargeAFCIntComm(newChargingDetails.getMerchantFixChargeAFCIntComm());
				newChargingDetail.setMerchantTDRAFCIntComm(newChargingDetails.getMerchantTDRAFCIntComm());
				newChargingDetail.setPgTDRIntComm(newChargingDetails.getPgTDRIntComm());
				newChargingDetail.setPgFixChargeIntComm(newChargingDetails.getPgFixChargeIntComm());
				newChargingDetail.setPgFixChargeAFCIntComm(newChargingDetails.getPgFixChargeAFCIntComm());
				newChargingDetail.setPgTDRAFCIntComm(newChargingDetails.getPgTDRAFCIntComm());

			}

			if (isDomestic && isConsumer) {
				newChargingDetail.setBankTDR(newChargingDetails.getBankTDR());
				newChargingDetail.setBankFixCharge(newChargingDetails.getBankFixCharge());
				newChargingDetail.setBankFixChargeAFC(newChargingDetails.getBankFixChargeAFC());
				newChargingDetail.setBankTDRAFC(newChargingDetails.getBankTDRAFC());
				newChargingDetail.setMerchantTDR(newChargingDetails.getMerchantTDR());
				newChargingDetail.setMerchantFixCharge(newChargingDetails.getMerchantFixCharge());
				newChargingDetail.setMerchantFixChargeAFC(newChargingDetails.getMerchantFixChargeAFC());
				newChargingDetail.setMerchantTDRAFC(newChargingDetails.getMerchantTDRAFC());
				newChargingDetail.setPgTDR(newChargingDetails.getPgTDR());
				newChargingDetail.setPgFixCharge(newChargingDetails.getPgFixCharge());
				newChargingDetail.setPgFixChargeAFC(newChargingDetails.getPgFixChargeAFC());
				newChargingDetail.setPgTDRAFC(newChargingDetails.getPgTDRAFC());
			} else if (isDomestic && !isConsumer) {

				newChargingDetail.setBankTDRDomComm(newChargingDetails.getBankTDRDomComm());
				newChargingDetail.setBankFixChargeDomComm(newChargingDetails.getBankFixChargeDomComm());
				newChargingDetail.setBankFixChargeAFCDomComm(newChargingDetails.getBankFixChargeAFCDomComm());
				newChargingDetail.setBankTDRAFCDomComm(newChargingDetails.getBankTDRAFCDomComm());
				newChargingDetail.setMerchantTDRDomComm(newChargingDetails.getMerchantTDRDomComm());
				newChargingDetail.setMerchantFixChargeDomComm(newChargingDetails.getMerchantFixChargeDomComm());
				newChargingDetail.setMerchantFixChargeAFCDomComm(newChargingDetails.getMerchantFixChargeAFCDomComm());
				newChargingDetail.setMerchantTDRAFCDomComm(newChargingDetails.getMerchantTDRAFCDomComm());
				newChargingDetail.setPgTDRDomComm(newChargingDetails.getPgTDRDomComm());
				newChargingDetail.setPgFixChargeDomComm(newChargingDetails.getPgFixChargeDomComm());
				newChargingDetail.setPgFixChargeAFCDomComm(newChargingDetails.getPgFixChargeAFCDomComm());
				newChargingDetail.setPgTDRAFCDomComm(newChargingDetails.getPgTDRAFCDomComm());

			} else if (!isDomestic && isConsumer) {
				newChargingDetail.setBankTDRIntCons(newChargingDetails.getBankTDRIntCons());
				newChargingDetail.setBankFixChargeIntCons(newChargingDetails.getBankFixChargeIntCons());
				newChargingDetail.setBankFixChargeAFCIntCons(newChargingDetails.getBankFixChargeAFCIntCons());
				newChargingDetail.setBankTDRAFCIntCons(newChargingDetail.getBankTDRAFCIntCons());
				newChargingDetail.setMerchantTDRIntCons(newChargingDetails.getMerchantTDRIntCons());
				newChargingDetail.setMerchantFixChargeIntCons(newChargingDetails.getMerchantFixChargeIntCons());
				newChargingDetail.setMerchantFixChargeAFCIntCons(newChargingDetails.getMerchantFixChargeAFCIntCons());
				newChargingDetail.setMerchantTDRAFCIntCons(newChargingDetails.getMerchantTDRAFCIntCons());
				newChargingDetail.setPgTDRIntCons(newChargingDetails.getPgTDRIntCons());
				newChargingDetail.setPgFixChargeIntCons(newChargingDetails.getPgFixChargeIntCons());
				newChargingDetail.setPgFixChargeAFCIntCons(newChargingDetails.getPgFixChargeAFCIntCons());
				newChargingDetail.setPgTDRAFCIntCons(newChargingDetails.getPgTDRAFCIntCons());

			} else if (!isDomestic && !isConsumer) {
				newChargingDetail.setBankTDRIntComm(newChargingDetails.getBankTDRIntComm());
				newChargingDetail.setBankFixChargeIntComm(newChargingDetails.getBankFixChargeIntComm());
				newChargingDetail.setBankFixChargeAFCIntComm(newChargingDetails.getBankFixChargeAFCIntComm());
				newChargingDetail.setBankTDRAFCIntComm(newChargingDetails.getBankTDRAFCIntComm());
				newChargingDetail.setMerchantTDRIntComm(newChargingDetails.getMerchantTDRIntComm());
				newChargingDetail.setMerchantFixChargeIntComm(newChargingDetails.getMerchantFixChargeIntComm());
				newChargingDetail.setMerchantFixChargeAFCIntComm(newChargingDetails.getMerchantFixChargeAFCIntComm());
				newChargingDetail.setMerchantTDRAFCIntComm(newChargingDetails.getMerchantTDRAFCIntComm());
				newChargingDetail.setPgTDRIntComm(newChargingDetails.getPgTDRIntComm());
				newChargingDetail.setPgFixChargeIntComm(newChargingDetails.getPgFixChargeIntComm());
				newChargingDetail.setPgFixChargeAFCIntComm(newChargingDetails.getPgFixChargeAFCIntComm());
				newChargingDetail.setPgTDRAFCIntComm(newChargingDetails.getPgTDRAFCIntComm());

			}
			newChargingDetail.setCreatedDate(currentDate);
			newChargingDetail.setId(null);
			newChargingDetail.setCurrency(newChargingDetails.getCurrency());
			newChargingDetail.setStatus(TDRStatus.ACTIVE);
			chargingDetailFromDb.setStatus(TDRStatus.INACTIVE);
			newChargingDetail.setFixChargeLimit(newChargingDetails.getFixChargeLimit());
			newChargingDetail.setAllowFixCharge(newChargingDetails.isAllowFixCharge());
			newChargingDetail.setMerchantServiceTax(newChargingDetails.getMerchantServiceTax());
			account.addChargingDetail(newChargingDetail);

		} else { // Edit current detail
			newChargingDetails.setAcquirerName(chargingDetailFromDb.getAcquirerName());
			newChargingDetails.setPayId(chargingDetailFromDb.getPayId());
			newChargingDetails.setPgServiceTax(newChargingDetails.getMerchantServiceTax());
			newChargingDetails.setBankServiceTax(newChargingDetails.getMerchantServiceTax());
			newChargingDetails.setStatus(TDRStatus.ACTIVE);
			newChargingDetails.setTdrStatus(TDRStatus.ACTIVE);
			newChargingDetails.setCreatedDate(currentDate);
			account.addChargingDetail(newChargingDetails);
		}
	}

	public ChargingDetails editBlankChargingDetails(Account account, ChargingDetails chargingDetailFromDb,
			ChargingDetails newChargingDetails, String userType, String loginUserEmailId) {
		Date currentDate = new Date();
		newChargingDetails.setAcquirerName(chargingDetailFromDb.getAcquirerName());
		newChargingDetails.setPayId(chargingDetailFromDb.getPayId());
		newChargingDetails.setPgServiceTax(newChargingDetails.getMerchantServiceTax());
		newChargingDetails.setBankServiceTax(newChargingDetails.getMerchantServiceTax());
		newChargingDetails.setStatus(TDRStatus.ACTIVE);
		newChargingDetails.setTdrStatus(TDRStatus.ACTIVE);
		newChargingDetails.setCreatedDate(currentDate);
		newChargingDetails.setUpdateBy(loginUserEmailId);
		return newChargingDetails;
	}

	public ChargingDetails createChargingDetail(String acquirerName, String payId, String token, String currencyCode) {
		ChargingDetails chargingDetail = new ChargingDetails();
		String[] splittedToken = token.split("-");

		chargingDetail.setAcquirerName(acquirerName);
		chargingDetail.setPayId(payId);
		chargingDetail.setMopType(MopType.getmop(splittedToken[1]));
		chargingDetail.setPaymentType(PaymentType.getInstance(splittedToken[0]));
		// to add txnType for net-banking by vijaya
		String paymentType = chargingDetail.getPaymentType().getCode().toString();
		if (StringUtils.isNotBlank(paymentType) && paymentType.equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {
			chargingDetail.setTransactionType(TransactionType.SALE);
		}
		// end
		chargingDetail.setStatus(TDRStatus.ACTIVE);
		chargingDetail.setTdrStatus(TDRStatus.PENDING); // added by sonu for TDR Restriction merchant Activation
		chargingDetail.setCurrency(currencyCode);
		if (splittedToken.length == 3) {
			chargingDetail.setTransactionType(TransactionType.getInstanceFromCode(splittedToken[2]));
		}

		return chargingDetail;
	}

	public TdrSetting createTDRDetail(String acquirerName, String payId, String token, String currencyCode) {
		TdrSetting chargingDetail = new TdrSetting();
		String[] splittedToken = token.split("-");

		chargingDetail.setAcquirerName(acquirerName);
		chargingDetail.setPayId(payId);
		chargingDetail.setMopType(MopType.getmop(splittedToken[2]).toString());
		chargingDetail.setPaymentType(PaymentType.getInstance(splittedToken[1]).toString());
		// to add txnType for net-banking by vijaya
		String paymentType = chargingDetail.getPaymentType();
		if (StringUtils.isNotBlank(paymentType) && paymentType.equalsIgnoreCase(PaymentType.NET_BANKING.toString())) {
			chargingDetail.setTransactionType(TransactionType.SALE.toString());
		}
		// end
		chargingDetail.setPaymentRegion("DOMESTIC");
		chargingDetail.setType("CONSUMER");
		chargingDetail.setStatus(TDRStatus.ACTIVE.toString());
		chargingDetail.setTdrStatus(TDRStatus.PENDING.toString()); // added by sonu for TDR Restriction merchant
																	// Activation
		chargingDetail.setCurrency(currencyCode);
		if (splittedToken.length == 4) {
			chargingDetail.setTransactionType(TransactionType.getInstanceFromCode(splittedToken[3]).toString());
		}

		return chargingDetail;
	}
	public TdrSetting createTDRDetailCommercial(String acquirerName, String payId, String token, String currencyCode) {
		TdrSetting chargingDetail = new TdrSetting();
		String[] splittedToken = token.split("-");

		chargingDetail.setAcquirerName(acquirerName);
		chargingDetail.setPayId(payId);
		chargingDetail.setMopType(MopType.getmop(splittedToken[2]).toString());
		chargingDetail.setPaymentType(PaymentType.getInstance(splittedToken[1]).toString());
		// to add txnType for net-banking by vijaya
		String paymentType = chargingDetail.getPaymentType();
		if (StringUtils.isNotBlank(paymentType) && paymentType.equalsIgnoreCase(PaymentType.NET_BANKING.toString())) {
			chargingDetail.setTransactionType(TransactionType.SALE.toString());
		}
		// end
		chargingDetail.setPaymentRegion("DOMESTIC");
		chargingDetail.setType("COMMERCIAL");
		chargingDetail.setStatus(TDRStatus.ACTIVE.toString());
		chargingDetail.setTdrStatus(TDRStatus.PENDING.toString()); // added by sonu for TDR Restriction merchant
																	// Activation
		chargingDetail.setCurrency(currencyCode);
		if (splittedToken.length == 4) {
			chargingDetail.setTransactionType(TransactionType.getInstanceFromCode(splittedToken[3]).toString());
		}

		return chargingDetail;
	}

	public TdrSetting createTDRDetailInternationalConsumer(String acquirerName, String payId, String token) {
		TdrSetting chargingDetail = new TdrSetting();
		String[] splittedToken = token.split("-");

		chargingDetail.setAcquirerName(acquirerName);
		chargingDetail.setPayId(payId);
		chargingDetail.setMopType(MopType.getmop(splittedToken[2]).toString());
		chargingDetail.setPaymentType(PaymentType.getInstance(splittedToken[1]).toString());
		// to add txnType for net-banking by vijaya
		String paymentType = chargingDetail.getPaymentType();
		if (StringUtils.isNotBlank(paymentType) && paymentType.equalsIgnoreCase(PaymentType.NET_BANKING.toString())) {
			chargingDetail.setTransactionType(TransactionType.SALE.toString());
		}
		// end
		chargingDetail.setPaymentRegion(AccountCurrencyRegions.INTERNATIONAL.name());
		chargingDetail.setType("CONSUMER");
		chargingDetail.setStatus(TDRStatus.ACTIVE.toString());
		chargingDetail.setTdrStatus(TDRStatus.PENDING.toString()); // added by sonu for TDR Restriction merchant

		// Activation
		String currencyCode = splittedToken[0];
		chargingDetail.setCurrency(currencyCode);
		if (splittedToken.length == 4) {
			chargingDetail.setTransactionType(TransactionType.getInstanceFromCode(splittedToken[3]).toString());
		}

		return chargingDetail;
	}
	public TdrSetting createTDRDetailInternationalCommercial(String acquirerName, String payId, String token) {
		TdrSetting chargingDetail = new TdrSetting();
		String[] splittedToken = token.split("-");

		chargingDetail.setAcquirerName(acquirerName);
		chargingDetail.setPayId(payId);
		chargingDetail.setMopType(MopType.getmop(splittedToken[2]).toString());
		chargingDetail.setPaymentType(PaymentType.getInstance(splittedToken[1]).toString());
		// to add txnType for net-banking by vijaya
		String paymentType = chargingDetail.getPaymentType();
		if (StringUtils.isNotBlank(paymentType) && paymentType.equalsIgnoreCase(PaymentType.NET_BANKING.toString())) {
			chargingDetail.setTransactionType(TransactionType.SALE.toString());
		}
		// end
		chargingDetail.setPaymentRegion(AccountCurrencyRegions.INTERNATIONAL.name());
		chargingDetail.setType("COMMERCIAL");
		chargingDetail.setStatus(TDRStatus.ACTIVE.toString());
		chargingDetail.setTdrStatus(TDRStatus.PENDING.toString()); // added by sonu for TDR Restriction merchant
																	// Activation
		String currencyCode = splittedToken[0];
		chargingDetail.setCurrency(currencyCode);
		if (splittedToken.length == 4) {
			chargingDetail.setTransactionType(TransactionType.getInstanceFromCode(splittedToken[3]).toString());
		}

		return chargingDetail;
	}
	public ChargingDetails createChargingDetail(PaymentType paymentType, MopType mopType, String acquirerName,
			String payId, String accountCurrencyCode) {

		ChargingDetails newChargingDetails = new ChargingDetails();

		newChargingDetails.setPaymentType(paymentType);
		newChargingDetails.setMopType(mopType);
		newChargingDetails.setAcquirerName(acquirerName);
		newChargingDetails.setPayId(payId);
		newChargingDetails.setStatus(TDRStatus.ACTIVE);
		newChargingDetails.setTdrStatus(TDRStatus.PENDING); // change for MDR Restriction
		newChargingDetails.setCurrency(accountCurrencyCode);

		return newChargingDetails;
	}

	public TdrSetting createChargingDetailForNewTdrSetting(PaymentType paymentType, MopType mopType,
			String acquirerName, String payId, String accountCurrencyCode) {

		TdrSetting newChargingDetails = new TdrSetting();

		newChargingDetails.setPaymentType(paymentType.toString());
		newChargingDetails.setMopType(mopType.toString());
		newChargingDetails.setAcquirerName(acquirerName);
		newChargingDetails.setPayId(payId);
		newChargingDetails.setPaymentRegion("DOMESTIC");
		newChargingDetails.setType("CONSUMER");
		newChargingDetails.setStatus(TDRStatus.ACTIVE.toString());
		newChargingDetails.setTdrStatus(TDRStatus.PENDING.toString()); // change for MDR Restriction
		newChargingDetails.setCurrency(accountCurrencyCode);

		return newChargingDetails;
	}
	public TdrSetting createChargingDetailForNewTdrSettingCommercial(PaymentType paymentType, MopType mopType,
			String acquirerName, String payId, String accountCurrencyCode) {

		TdrSetting newChargingDetails = new TdrSetting();

		newChargingDetails.setPaymentType(paymentType.toString());
		newChargingDetails.setMopType(mopType.toString());
		newChargingDetails.setAcquirerName(acquirerName);
		newChargingDetails.setPayId(payId);
		newChargingDetails.setPaymentRegion("DOMESTIC");
		newChargingDetails.setType("COMMERCIAL");
		newChargingDetails.setStatus(TDRStatus.ACTIVE.toString());
		newChargingDetails.setTdrStatus(TDRStatus.PENDING.toString()); // change for MDR Restriction
		newChargingDetails.setCurrency(accountCurrencyCode);

		return newChargingDetails;
	}
	public TdrSetting createChargingDetailForNewTdrSettingInternational(PaymentType paymentType, MopType mopType,
			String acquirerName, String payId, String accountCurrencyCode) {

		TdrSetting newChargingDetails = new TdrSetting();

		newChargingDetails.setPaymentType(paymentType.toString());
		newChargingDetails.setMopType(mopType.toString());
		newChargingDetails.setAcquirerName(acquirerName);
		newChargingDetails.setPayId(payId);
		newChargingDetails.setPaymentRegion(AccountCurrencyRegions.INTERNATIONAL.name());
		newChargingDetails.setType("CONSUMER");
		newChargingDetails.setStatus(TDRStatus.ACTIVE.toString());
		newChargingDetails.setTdrStatus(TDRStatus.PENDING.toString()); // change for MDR Restriction
		newChargingDetails.setCurrency(accountCurrencyCode);

		return newChargingDetails;
	}
	public TdrSetting createChargingDetailForNewTdrSettingInternationalCommercial(PaymentType paymentType, MopType mopType,
			String acquirerName, String payId, String accountCurrencyCode) {

		TdrSetting newChargingDetails = new TdrSetting();

		newChargingDetails.setPaymentType(paymentType.toString());
		newChargingDetails.setMopType(mopType.toString());
		newChargingDetails.setAcquirerName(acquirerName);
		newChargingDetails.setPayId(payId);
		newChargingDetails.setPaymentRegion(AccountCurrencyRegions.INTERNATIONAL.name());
		newChargingDetails.setType("COMMERCIAL");
		newChargingDetails.setStatus(TDRStatus.ACTIVE.toString());
		newChargingDetails.setTdrStatus(TDRStatus.PENDING.toString()); // change for MDR Restriction
		newChargingDetails.setCurrency(accountCurrencyCode);

		return newChargingDetails;
	}

	public void updateServiceTax(ChargingDetails oldChargingDetail, double newServiceTax) {
		Date currentDate = new Date();

		ChargingDetails newChargingDetail = SerializationUtils.clone(oldChargingDetail);
		// create new TDR
		newChargingDetail.setPgServiceTax(newServiceTax);
		newChargingDetail.setBankServiceTax(newServiceTax);
		newChargingDetail.setMerchantServiceTax(newServiceTax);
		newChargingDetail.setStatus(TDRStatus.ACTIVE);
		newChargingDetail.setCreatedDate(currentDate);
		newChargingDetail.setId(null);
		// deactivate old TDR
		oldChargingDetail.setStatus(TDRStatus.INACTIVE);
		oldChargingDetail.setUpdatedDate(currentDate);
	}
}
