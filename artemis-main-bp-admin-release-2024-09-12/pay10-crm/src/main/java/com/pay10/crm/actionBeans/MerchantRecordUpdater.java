package com.pay10.crm.actionBeans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.MerchantStatusLog;
import com.pay10.commons.user.MerchantStatusLogDao;
import com.pay10.commons.user.PendingUserApproval;
import com.pay10.commons.user.PendingUserApprovalDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Rahul
 *
 */
@Service
public class MerchantRecordUpdater {

	@Autowired
	PendingUserApprovalDao pendingUserApprovalDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	MerchantLogUpdater merchantLogUpdater;

	@Autowired
	AWSEncryptDecryptService awsEncryptDecryptService;

	//	private NotificationEmailer notificationEmailerDB = null;
	private Set<Account> accountSetDB = null;
	private String actionMessage;
	User dbuser = null;
	private User userFromDB = null;
	private List<User> userList;

	public Map<String, User> updateUserPendingDetails(User userFE, User sessionUser, List<Account> newAccounts,
													  List<AccountCurrency> accountCurrencyList) {

		User user = updateUserDetails(userFE, sessionUser, newAccounts, accountCurrencyList);
		Map<String, User> returnMap = new HashMap<String, User>();
		returnMap.put(actionMessage, user);
		return returnMap;
	}



	public User updateUserDetails(User userFE, User sessionUser, List<Account> newAccounts,
								  List<AccountCurrency> accountCurrencyList) {
		Date date = new Date();
		userFromDB = userDao.findPayId(userFE.getPayId());
		//String previousStatus=userFromDB.getUserStatus().getStatus();
		User userDB = (User) SerializationUtils.clone(userFromDB);
		// Set details of user for edit
		userFromDB.setModeType(userFE.getModeType());
		userFromDB.setComments(userFE.getComments());
		userFromDB.setWhiteListIpAddress(userFE.getWhiteListIpAddress());
		userFromDB.setUserStatus(userFE.getUserStatus());

		userFromDB.setBusinessName(userFE.getBusinessName());
		userFromDB.setFirstName(userFE.getFirstName());
		userFromDB.setLastName(userFE.getLastName());
		userFromDB.setCompanyName(userFE.getCompanyName());
		userFromDB.setWebsite(userFE.getWebsite());
		userFromDB.setContactPerson(userFE.getContactPerson());
		userFromDB.setEmailId(userFE.getEmailId());
		userFromDB.setRegistrationDate(userFE.getRegistrationDate());
		Date alreadyActiveUser = userFromDB.getActivationDate();
		if (alreadyActiveUser != null) {
			userFromDB.setActivationDate(userFromDB.getActivationDate());
		} else {
			userFromDB.setActivationDate(userFE.getActivationDate());
		}
		userFromDB.setMerchantType(userFE.getMerchantType());
		userFromDB.setNoOfTransactions(userFE.getNoOfTransactions());
		userFromDB.setAmountOfTransactions(userFE.getAmountOfTransactions());
		userFromDB.setResellerId(userFE.getResellerId());
		userFromDB.setProductDetail(userFE.getProductDetail());

		userFromDB.setMobile(userFE.getMobile());
		userFromDB.setTransactionSmsFlag(userFE.isTransactionSmsFlag());
		userFromDB.setTelephoneNo(userFE.getTelephoneNo());
		userFromDB.setFax(userFE.getFax());
		userFromDB.setAddress(userFE.getAddress());
		userFromDB.setCity(userFE.getCity());
		userFromDB.setState(userFE.getState());
		userFromDB.setCountry(userFE.getCountry());
		userFromDB.setPostalCode(userFE.getPostalCode());

		userFromDB.setBankName(userFE.getBankName());
		userFromDB.setIfscCode(userFE.getIfscCode());
		userFromDB.setAccHolderName(userFE.getAccHolderName());
		userFromDB.setCurrency(userFE.getCurrency());
		userFromDB.setBranchName(userFE.getBranchName());
		userFromDB.setPanCard(userFE.getPanCard());
		userFromDB.setAccountNo(userFE.getAccountNo());

		userFromDB.setOrganisationType(userFE.getOrganisationType());
		userFromDB.setWebsite(userFE.getWebsite());
		userFromDB.setMultiCurrency(userFE.getMultiCurrency());
		userFromDB.setBusinessModel(userFE.getBusinessModel());
		userFromDB.setOperationAddress(userFE.getOperationAddress());
		userFromDB.setOperationState(userFE.getOperationState());
		userFromDB.setOperationCity(userFE.getOperationCity());
		userFromDB.setOperationPostalCode(userFE.getOperationPostalCode());
		userFromDB.setDateOfEstablishment(userFE.getDateOfEstablishment());
		userFromDB.setAutoFlag(userFE.isAutoFlag());
		userFromDB.setAutoMin(userFE.getAutoMin());
		userFromDB.setCin(userFE.getCin());
		userFromDB.setPan(userFE.getPan());
		userFromDB.setPanName(userFE.getPanName());
		userFromDB.setNoOfTransactions(userFE.getNoOfTransactions());
		userFromDB.setAmountOfTransactions(userFE.getAmountOfTransactions());
		userFromDB.setTransactionEmailerFlag(userFE.isTransactionEmailerFlag());
		userFromDB.setTransactionEmailId(userFE.getTransactionEmailId());
		userFromDB.setExpressPayFlag(userFE.isExpressPayFlag());
		userFromDB.setMerchantHostedFlag(userFE.isMerchantHostedFlag());
		userFromDB.setIframePaymentFlag(userFE.isIframePaymentFlag());
		userFromDB.setSurchargeFlag(userFE.isSurchargeFlag());
		userFromDB.setTransactionAuthenticationEmailFlag(userFE.isTransactionAuthenticationEmailFlag());
		userFromDB.setTransactionCustomerEmailFlag(userFE.isTransactionCustomerEmailFlag());
		userFromDB.setRefundTransactionCustomerEmailFlag(userFE.isRefundTransactionCustomerEmailFlag());
		userFromDB.setRefundTransactionMerchantEmailFlag(userFE.isRefundTransactionMerchantEmailFlag());
		userFromDB.setTransactionFailedAlertFlag(userFE.isTransactionFailedAlertFlag());
		userFromDB.setNotificationApiEnableFlag(userFE.isNotificationApiEnableFlag());
		userFromDB.setNotificaionApi(userFE.getNotificaionApi());
		userFromDB.setPaymentLink(userFE.getPaymentLink());

		userFromDB.setRetryTransactionCustomeFlag(userFE.isRetryTransactionCustomeFlag());
		userFromDB.setAttemptTrasacation(userFE.getAttemptTrasacation());
		userFromDB.setExtraRefundLimit(userFE.getExtraRefundLimit());
		userFromDB.setUpdateDate(date);
		userFromDB.setDefaultCurrency(userFE.getDefaultCurrency());
		userFromDB.setmCC(userFE.getmCC());
		userFromDB.setAmexSellerId(userFE.getAmexSellerId());
		userFromDB.setDefaultLanguage(userFE.getDefaultLanguage());
		userFromDB.setIndustryCategory(userFE.getIndustryCategory());
		userFromDB.setIndustrySubCategory(userFE.getIndustrySubCategory());
		userFromDB.setUpdatedBy(sessionUser.getEmailId());
		userFromDB.setTransactionSms(userFE.getTransactionSms());
		userFromDB.setSkipOrderIdForRefund(userFE.isSkipOrderIdForRefund());
		userFromDB.setAllowDuplicateOrderId(userFE.getAllowDuplicateOrderId());
		userFromDB.setPaymentMessageSlab(userFE.getPaymentMessageSlab());
		userFromDB.setCardSaveParam(userFE.getCardSaveParam());
		userFromDB.setAllowSaleDuplicate(userFE.isAllowSaleDuplicate());
		userFromDB.setAllowRefundDuplicate(userFE.isAllowRefundDuplicate());
		userFromDB.setAllowSaleInRefund(userFE.isAllowSaleInRefund());
		userFromDB.setAllowRefundInSale(userFE.isAllowRefundInSale());
		userFromDB.setMerchantGstNo(userFE.getMerchantGstNo());
		userFromDB.setBankVerificationStatus(userFE.isBankVerificationStatus());
		userFromDB.setSplitPaymentStatus(userFE.isSplitPaymentStatus());
		userFromDB.setEkycFlag(userFE.isEkycFlag());
		userFromDB.setPOFlag(userFE.getPOFlag());
		userFromDB.setSettlementDays(userFE.getSettlementDays());

		userFromDB.setTncStatus(userFE.isTncStatus());
		userFromDB.setPayText(userFE.getPayText());
		userFromDB.setInvoiceText(userFE.getInvoiceText());
		// Update white label URL.
		userFromDB.setEnableWhiteLabelUrl(userFE.isEnableWhiteLabelUrl());
		userFromDB.setWhiteLabelUrl(userFE.getWhiteLabelUrl().toLowerCase());
		userFromDB.setEnableAutoRefundPostSettlement(userFE.isEnableAutoRefundPostSettlement());

		userFromDB.setCallBackFlag(userFE.isCallBackFlag());
		userFromDB.setCallBackUrl(userFE.getCallBackUrl());
		userFromDB.setMerchantS2SFlag(userFE.isMerchantS2SFlag());
		// Added By Sweety for WebStoreAPI
		userFromDB.setWebStoreApiEnableFlag(userFE.isWebStoreApiEnableFlag());
		System.out.println("userFE.getUuId()....." + userFE.getUuId());
		userFromDB.setUuId(userFE.getUuId());
		userFromDB.setWebStoreApiToken(userFE.getWebStoreApiToken());
		userFromDB.setDefaultCrypto(userFE.getDefaultCrypto());
		userFromDB.setCryptoAddress(userFE.getCryptoAddress());
		userFromDB.setFiatAllowed(userFE.isFiatAllowed());
		userFromDB.setBankCountry(userFE.getBankCountry());
		userFromDB.setCryptoAllowed(userFE.isCryptoAllowed());
		userFromDB.setTfaFlag(userFE.isTfaFlag());
		// Check Settlement Naming Convention & Refund Naming Convention values
//		@SuppressWarnings("unchecked")
//		List<User> userList = userDao.findAll();
//		if (userList != null) {
//			for (User user : userList) {
//
//				if (!user.getPayId().equals(userFE.getPayId())) {
//					if (StringUtils.isNotEmpty(userFE.getSettlementNamingConvention().trim())) {
//						if (userFE.getSettlementNamingConvention().replaceAll(",", "").trim()
//								.equals(user.getSettlementNamingConvention())) {
//							setActionMessage(CrmFieldConstants.SETTLEMENT_NAMING_MESSAGE.getValue());
//							return userFromDB;
//						} else {
//							userFromDB.setSettlementNamingConvention(
//									userFE.getSettlementNamingConvention().replaceAll(",", "").trim());
//						}
//					} else {
//						userFromDB.setSettlementNamingConvention(userFE.getSettlementNamingConvention());
//					}
//					if (StringUtils.isNotEmpty(userFE.getRefundValidationNamingConvention().trim())) {
//						if (userFE.getRefundValidationNamingConvention().replaceAll(",", "").trim()
//								.equals(user.getRefundValidationNamingConvention())) {
//							setActionMessage(CrmFieldConstants.REFUND_VALIDATION_NAMING_MESSAGE.getValue());
//							return userFromDB;
//						} else {
//							userFromDB.setRefundValidationNamingConvention(
//									userFE.getRefundValidationNamingConvention().replaceAll(",", "").trim());
//						}
//
//					} else {
//						userFromDB.setRefundValidationNamingConvention(userFE.getRefundValidationNamingConvention());
//					}
//
//				}
//			}
//		}

		userFromDB.setSettlementNamingConvention(
				userFE.getSettlementNamingConvention().replaceAll(",", "").trim());
		userFromDB.setRefundValidationNamingConvention(
				userFE.getRefundValidationNamingConvention().replaceAll(",", "").trim());
		userDao.update(userFromDB);
		// Add by Vijaylaxmi
		//userFromDB = userDao.findPayId(userDB.getPayId());
		merchantLogUpdater.updateValue(userFE, sessionUser, newAccounts, accountCurrencyList, userDB);

		setActionMessage(CrmFieldConstants.USER_DETAILS_UPDATED.getValue());
		return userFromDB;
	}

	public PendingUserApproval createPendingApprovalFields(User userFE, User sessionUser) {
		Date date = new Date();
		User dbuser = userDao.findPayId(userFE.getPayId());

		// Set details of user for edit
		PendingUserApproval pua = new PendingUserApproval();
		pua.setModeType(userFE.getModeType());
		pua.setComments(userFE.getComments());
		pua.setWhiteListIpAddress(userFE.getWhiteListIpAddress());
		pua.setUserStatus(userFE.getUserStatus());
		pua.setBusinessName(userFE.getBusinessName());
		pua.setFirstName(userFE.getFirstName());
		pua.setLastName(userFE.getLastName());
		pua.setCompanyName(userFE.getCompanyName());
		pua.setWebsite(userFE.getWebsite());
		pua.setContactPerson(userFE.getContactPerson());
		pua.setEmailId(userFE.getEmailId());
		pua.setRegistrationDate(userFE.getRegistrationDate());

		pua.setMerchantType(userFE.getMerchantType());
		pua.setNoOfTransactions(userFE.getNoOfTransactions());
		pua.setAmountOfTransactions(userFE.getAmountOfTransactions());
		pua.setResellerId(userFE.getResellerId());
		pua.setProductDetail(userFE.getProductDetail());

		pua.setMobile(userFE.getMobile());
		pua.setTransactionSmsFlag(userFE.isTransactionSmsFlag());
		pua.setTelephoneNo(userFE.getTelephoneNo());
		pua.setFax(userFE.getFax());
		pua.setAddress(userFE.getAddress());
		pua.setCity(userFE.getCity());
		pua.setState(userFE.getState());
		pua.setCountry(userFE.getCountry());
		pua.setPostalCode(userFE.getPostalCode());

		pua.setBankName(userFE.getBankName());
		pua.setIfscCode(userFE.getIfscCode());
		pua.setAccHolderName(userFE.getAccHolderName());
		pua.setCurrency(userFE.getCurrency());
		pua.setBranchName(userFE.getBranchName());
		pua.setPanCard(userFE.getPanCard());
		pua.setAccountNo(userFE.getAccountNo());

		pua.setOrganisationType(userFE.getOrganisationType());
		pua.setWebsite(userFE.getWebsite());
		pua.setMultiCurrency(userFE.getMultiCurrency());
		pua.setBusinessModel(userFE.getBusinessModel());
		pua.setOperationAddress(userFE.getOperationAddress());
		pua.setOperationState(userFE.getOperationState());
		pua.setOperationCity(userFE.getOperationCity());
		pua.setOperationPostalCode(userFE.getOperationPostalCode());
		pua.setDateOfEstablishment(userFE.getDateOfEstablishment());

		pua.setCin(userFE.getCin());
		pua.setPan(userFE.getPan());
		pua.setPanName(userFE.getPanName());
		pua.setNoOfTransactions(userFE.getNoOfTransactions());
		pua.setAmountOfTransactions(userFE.getAmountOfTransactions());
		pua.setTransactionEmailerFlag(userFE.isTransactionEmailerFlag());
		pua.setTransactionEmailId(userFE.getTransactionEmailId());
		pua.setExpressPayFlag(userFE.isExpressPayFlag());
		pua.setMerchantHostedFlag(userFE.isMerchantHostedFlag());
		pua.setIframePaymentFlag(userFE.isIframePaymentFlag());
		pua.setSurchargeFlag(userFE.isSurchargeFlag());
		pua.setTransactionAuthenticationEmailFlag(userFE.isTransactionAuthenticationEmailFlag());
		pua.setTransactionCustomerEmailFlag(userFE.isTransactionCustomerEmailFlag());
		pua.setRefundTransactionCustomerEmailFlag(userFE.isRefundTransactionCustomerEmailFlag());
		pua.setRefundTransactionMerchantEmailFlag(userFE.isRefundTransactionMerchantEmailFlag());
		pua.setTransactionFailedAlertFlag(userFE.isTransactionFailedAlertFlag());
		pua.setNotificationApiEnableFlag(userFE.isNotificationApiEnableFlag());
		pua.setNotificaionApi(userFE.getNotificaionApi());
		pua.setPaymentLink(userFE.getPaymentLink());
		pua.setRetryTransactionCustomeFlag(userFE.isRetryTransactionCustomeFlag());
		pua.setAttemptTrasacation(userFE.getAttemptTrasacation());
		pua.setExtraRefundLimit(userFE.getExtraRefundLimit());
		pua.setUpdateDate(date);
		pua.setDefaultCurrency(userFE.getDefaultCurrency());
		pua.setMCC(userFE.getmCC());
		pua.setAmexSellerId(userFE.getAmexSellerId());
		pua.setDefaultLanguage(userFE.getDefaultLanguage());
		pua.setIndustryCategory(userFE.getIndustryCategory());
		pua.setIndustrySubCategory(userFE.getIndustrySubCategory());
		pua.setRequestedBy(sessionUser.getEmailId());
		pua.setPayId(userFE.getPayId());
		pua.setRequestStatus(TDRStatus.PENDING.toString());
		pua.setTransactionSms(userFE.getTransactionSms());
		pua.setAllowDuplicateOrderId(userFE.getAllowDuplicateOrderId());
		pua.setTransactionSms(userFE.getTransactionSms());
		pua.setCardSaveParam(userFE.getCardSaveParam());
		pua.setAllowSaleDuplicate(userFE.isAllowSaleDuplicate());
		pua.setAllowRefundDuplicate(userFE.isAllowRefundDuplicate());
		pua.setAllowSaleInRefund(userFE.isAllowSaleInRefund());
		pua.setAllowRefundInSale(userFE.isAllowRefundInSale());
		pua.setCallBackFlag(userFE.isCallBackFlag());
		pua.setCallBackUrl(userFE.getCallBackUrl());
		return pua;

	}

	// merchant end code
	// update profile from user end when merchant is nor activated
	public User updateUserProfile(User userFE) {

		userFromDB = userDao.findPayId(userFE.getPayId());
		//String previousStatus=userFromDB.getUserStatus().getStatus();

		// Set details of user for edit
		userFromDB.setBusinessName(userFE.getBusinessName());
		userFromDB.setFirstName(userFE.getFirstName());
		userFromDB.setLastName(userFE.getLastName());
		userFromDB.setEmailId(userFE.getEmailId());
		userFromDB.setCompanyName(userFE.getCompanyName());
		userFromDB.setTelephoneNo(userFE.getTelephoneNo());
		userFromDB.setMobile(userFE.getMobile());
		userFromDB.setAddress(userFE.getAddress());
		userFromDB.setCity(userFE.getCity());
		userFromDB.setState(userFE.getState());
		userFromDB.setCountry(userFE.getCountry());
		userFromDB.setPostalCode(userFE.getPostalCode());
		userFromDB.setAutoFlag(userFE.isAutoFlag());
		userFromDB.setAutoMin(userFE.getAutoMin());
		userFromDB.setOrganisationType(userFE.getOrganisationType());
		userFromDB.setWebsite(userFE.getWebsite());
		userFromDB.setMultiCurrency(userFE.getMultiCurrency());
		userFromDB.setBusinessModel(userFE.getBusinessModel());

		userFromDB.setAddress(userFE.getAddress());
		userFromDB.setState(userFE.getState());
		userFromDB.setCity(userFE.getCity());
		userFromDB.setPostalCode(userFE.getPostalCode());
		userFromDB.setOperationAddress(userFE.getOperationAddress());
		userFromDB.setOperationState(userFE.getOperationState());
		userFromDB.setOperationCity(userFE.getOperationCity());
		userFromDB.setOperationPostalCode(userFE.getOperationPostalCode());

		userFromDB.setBankName(userFE.getBankName());
		userFromDB.setIfscCode(userFE.getIfscCode());
		userFromDB.setAccHolderName(userFE.getAccHolderName());
		userFromDB.setCurrency(userFE.getCurrency());
		userFromDB.setBranchName(userFE.getBranchName());
		userFromDB.setPanCard(userFE.getPanCard());
		userFromDB.setAccountNo(userFE.getAccountNo());

		userFromDB.setDateOfEstablishment(userFE.getDateOfEstablishment());
		userFromDB.setCin(userFE.getCin());
		userFromDB.setPan(userFE.getPan());
		userFromDB.setPanName(userFE.getPanName());
		userFromDB.setNoOfTransactions(userFE.getNoOfTransactions());
		userFromDB.setAmountOfTransactions(userFE.getAmountOfTransactions());
		userFromDB.setTransactionEmailerFlag(userFE.isTransactionEmailerFlag());
		userFromDB.setTransactionEmailId(userFE.getTransactionEmailId());
		userFromDB.setDefaultCurrency(userFE.getDefaultCurrency());
		userFromDB.setTransactionSms(userFE.getTransactionSms());
		userFromDB.setAllowDuplicateOrderId(userFE.getAllowDuplicateOrderId());
		userFromDB.setSkipOrderIdForRefund(userFE.isSkipOrderIdForRefund());
		userFromDB.setPaymentMessageSlab(userFE.getPaymentMessageSlab());
		userFromDB.setCardSaveParam(userFE.getCardSaveParam());
		userFromDB.setAllowSaleDuplicate(userFE.isAllowSaleDuplicate());
		userFromDB.setAllowRefundDuplicate(userFE.isAllowRefundDuplicate());
		userFromDB.setAllowSaleInRefund(userFE.isAllowSaleInRefund());
		userFromDB.setAllowRefundInSale(userFE.isAllowRefundInSale());
		userFromDB.setMerchantGstNo(userFE.getMerchantGstNo());
		userFromDB.setCallBackFlag(userFE.isCallBackFlag());
		userFromDB.setCallBackUrl(userFE.getCallBackUrl());
		userFromDB.setBankVerificationStatus(userFE.isBankVerificationStatus());
		userFromDB.setSplitPaymentStatus(userFE.isSplitPaymentStatus());
		userFromDB.setEkycFlag(userFE.isEkycFlag());
		userFromDB.setPOFlag(userFE.getPOFlag());
		userFromDB.setMerchantS2SFlag(userFE.isMerchantS2SFlag());
		// Added By Sweety for WebStoreAPI
		userFromDB.setWebStoreApiEnableFlag(userFE.isWebStoreApiEnableFlag());
		System.out.println("userFE.getUuId()....." + userFE.getUuId());
		userFromDB.setUuId(userFE.getUuId());
		userFromDB.setWebStoreApiToken(userFE.getWebStoreApiToken());
		userFromDB.setCryptoAddress(userFE.getCryptoAddress());
		userFromDB.setCryptoAllowed(userFE.isCryptoAllowed());
		userFromDB.setFiatAllowed(userFE.isFiatAllowed());
		userFromDB.setBankCountry(userFE.getBankCountry());
		userFromDB.setDefaultCrypto(userFE.getDefaultCrypto());
		userFromDB.setTfaFlag(userFE.isTfaFlag());
		userDao.update(userFromDB);

//		System.out.println("merchantlog updateUserProfile() : " + previousStatus + "\t" + userFE.getUserStatus().getStatus());
//		if(!previousStatus.equalsIgnoreCase(userFE.getUserStatus().getStatus())) {
//			//add to log
//			MerchantStatusLog merchantStatusLog=new MerchantStatusLog();
//			merchantStatusLog.setEmailId(userFE.getEmailId());
//			merchantStatusLog.setTimeStamp(sdf.format(new Date()));
//			merchantStatusLog.setMessage("Merchant " + userFE.getUserStatus().getStatus());
//			merchantStatusLog.setStatus(userFE.getUserStatus().getStatus());
//			merchantStatusLogDao.saveMerchantStatusLog(merchantStatusLog);
//		}

		return userFromDB;
	}

	public void updateAccount(List<Account> newAccounts, List<AccountCurrency> accountCurrencyList) {
		accountSetDB = userFromDB.getAccounts();

		for (Account accountFE : newAccounts) {
			for (Account accountDB : accountSetDB) {
				if (accountFE.getAcquirerPayId().equals(accountDB.getAcquirerPayId())) {
					accountDB.setPrimaryStatus(accountFE.isPrimaryStatus());

					accountDB.setPrimaryNetbankingStatus(accountFE.isPrimaryNetbankingStatus());
					Set<AccountCurrency> accountCurrencySetDB = accountDB.getAccountCurrencySet();

					// accountDB.setNetbankingPrimaryStatus(accountFE.isNetbankingPrimaryStatus());
					for (AccountCurrency accountCurrencyDB : accountCurrencySetDB) {
						for (AccountCurrency accountCurrencyFE : accountCurrencyList) {
							if (accountCurrencyFE.getCurrencyCode().equals(accountCurrencyDB.getCurrencyCode())
									&& accountCurrencyFE.getAcqPayId().equals(accountCurrencyDB.getAcqPayId())) {
								accountCurrencyDB.setMerchantId(accountCurrencyFE.getMerchantId());
								accountCurrencyDB.setTxnKey(accountCurrencyFE.getTxnKey());
								accountCurrencyDB.setPassword(accountCurrencyFE.getPassword());
								accountCurrencyDB.setDirectTxn(accountCurrencyFE.isDirectTxn());

								accountCurrencyDB.setAdf1(accountCurrencyFE.getAdf1());
								accountCurrencyDB.setAdf2(accountCurrencyFE.getAdf2());
								accountCurrencyDB.setAdf3(accountCurrencyFE.getAdf3());
								accountCurrencyDB.setAdf4(accountCurrencyFE.getAdf4());
								accountCurrencyDB.setAdf5(accountCurrencyFE.getAdf5());
								accountCurrencyDB.setAdf8(accountCurrencyFE.getAdf8());
								accountCurrencyDB.setAdf9(accountCurrencyFE.getAdf9());
								accountCurrencyDB.setAdf10(accountCurrencyFE.getAdf10());
								accountCurrencyDB.setAdf11(accountCurrencyFE.getAdf11());

								if (!StringUtils.isAnyEmpty(accountCurrencyFE.getPassword())) {
									if (!StringUtils.isAnyEmpty(accountCurrencyFE.getPassword().trim())) {
										accountCurrencyDB.setPassword(
												awsEncryptDecryptService.encrypt(accountCurrencyFE.getPassword()));
									}

								}

								/*
								 * if (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf1())) { if
								 * (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf1().trim())) {
								 * accountCurrencyDB.setAdf1(
								 * awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf1())); }
								 *
								 * }
								 *
								 * if (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf2())) { if
								 * (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf2().trim())) {
								 * accountCurrencyDB.setAdf2(
								 * awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf2())); }
								 *
								 * }
								 *
								 * if (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf3())) { if
								 * (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf3().trim())) {
								 * accountCurrencyDB.setAdf3(
								 * awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf3())); }
								 *
								 * }
								 *
								 * if (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf4())) { if
								 * (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf4().trim())) {
								 * accountCurrencyDB.setAdf4(
								 * awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf4())); }
								 *
								 * }
								 *
								 * if (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf5())) { if
								 * (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf5().trim())) {
								 * accountCurrencyDB.setAdf5(
								 * awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf5())); }
								 *
								 * }
								 */

								boolean camsPay = false;
								if (StringUtils.isNotBlank(accountCurrencyFE.getAdf6())
										|| StringUtils.isNotBlank(accountCurrencyFE.getAdf7())) {
									User user = userDao.findByPayId(accountCurrencyFE.getAcqPayId());
									camsPay = StringUtils.equalsIgnoreCase(user.getBusinessName(),
											AcquirerType.CAMSPAY.getCode());
								}
								if (StringUtils.isNotBlank(accountCurrencyFE.getAdf6())) {
									String adf6 = camsPay ? accountCurrencyFE.getAdf6()
											: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf6());
									accountCurrencyDB.setAdf6(adf6);

								}

								if (StringUtils.isNotBlank(accountCurrencyFE.getAdf7())) {
									String adf7 = camsPay ? accountCurrencyFE.getAdf7()
											: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf7());
									accountCurrencyDB.setAdf7(adf7);

								}

								/*
								 * if (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf8())) { if
								 * (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf8().trim())) {
								 * accountCurrencyDB.setAdf8(
								 * awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf8())); }
								 *
								 * }
								 *
								 *
								 * if (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf9())) { if
								 * (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf9().trim())) {
								 * accountCurrencyDB.setAdf9(
								 * awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf9())); }
								 *
								 * }
								 *
								 *
								 * if (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf10())) { if
								 * (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf10().trim())) {
								 * accountCurrencyDB.setAdf10(
								 * awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf10())); }
								 *
								 * }
								 *
								 *
								 * if (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf11())) { if
								 * (!StringUtils.isAnyEmpty(accountCurrencyFE.getAdf11().trim())) {
								 * accountCurrencyDB.setAdf11(
								 * awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf11())); }
								 *
								 * }
								 */

							}
						}
					}
				}
			}
		}
	}

	public User updateResellerDetails(User user) {
		Date date = new Date();
		userFromDB = userDao.findPayId(user.getPayId());
		userFromDB.setModeType(user.getModeType());
		userFromDB.setComments(user.getComments());
		userFromDB.setWhiteListIpAddress(user.getWhiteListIpAddress());
		userFromDB.setUserStatus(user.getUserStatus());

		userFromDB.setBusinessName(user.getBusinessName());
		userFromDB.setFirstName(user.getFirstName());
		userFromDB.setLastName(user.getLastName());
		userFromDB.setCompanyName(user.getCompanyName());
		userFromDB.setWebsite(user.getWebsite());
		userFromDB.setContactPerson(user.getContactPerson());
		userFromDB.setEmailId(user.getEmailId());
		userFromDB.setRegistrationDate(user.getRegistrationDate());
		userFromDB.setActivationDate(user.getActivationDate());

		userFromDB.setMerchantType(user.getMerchantType());
		userFromDB.setNoOfTransactions(user.getNoOfTransactions());
		userFromDB.setAmountOfTransactions(user.getAmountOfTransactions());
		userFromDB.setResellerId(user.getResellerId());
		userFromDB.setProductDetail(user.getProductDetail());

		userFromDB.setMobile(user.getMobile());
		userFromDB.setTransactionSmsFlag(user.isTransactionSmsFlag());
		userFromDB.setTelephoneNo(user.getTelephoneNo());
		userFromDB.setFax(user.getFax());
		userFromDB.setAddress(user.getAddress());
		userFromDB.setCity(user.getCity());
		userFromDB.setState(user.getState());
		userFromDB.setCountry(user.getCountry());
		userFromDB.setPostalCode(user.getPostalCode());

		userFromDB.setBankName(user.getBankName());
		userFromDB.setIfscCode(user.getIfscCode());
		userFromDB.setAccHolderName(user.getAccHolderName());
		userFromDB.setCurrency(user.getCurrency());
		userFromDB.setBranchName(user.getBranchName());
		userFromDB.setPanCard(user.getPanCard());
		userFromDB.setAccountNo(user.getAccountNo());

		userFromDB.setOrganisationType(user.getOrganisationType());
		userFromDB.setWebsite(user.getWebsite());
		userFromDB.setMultiCurrency(user.getMultiCurrency());
		userFromDB.setBusinessModel(user.getBusinessModel());
		userFromDB.setOperationAddress(user.getOperationAddress());
		userFromDB.setOperationState(user.getOperationState());
		userFromDB.setOperationCity(user.getOperationCity());
		userFromDB.setOperationPostalCode(user.getOperationPostalCode());
		userFromDB.setDateOfEstablishment(user.getDateOfEstablishment());

		userFromDB.setCin(user.getCin());
		userFromDB.setPan(user.getPan());
		userFromDB.setPanName(user.getPanName());
		userFromDB.setNoOfTransactions(user.getNoOfTransactions());
		userFromDB.setAmountOfTransactions(user.getAmountOfTransactions());
		userFromDB.setTransactionEmailerFlag(user.isTransactionEmailerFlag());
		userFromDB.setTransactionEmailId(user.getTransactionEmailId());
		userFromDB.setExpressPayFlag(user.isExpressPayFlag());
		userFromDB.setMerchantHostedFlag(user.isMerchantHostedFlag());
		userFromDB.setIframePaymentFlag(user.isIframePaymentFlag());
		userFromDB.setTransactionAuthenticationEmailFlag(user.isTransactionAuthenticationEmailFlag());
		userFromDB.setTransactionCustomerEmailFlag(user.isTransactionCustomerEmailFlag());
		userFromDB.setRetryTransactionCustomeFlag(user.isRetryTransactionCustomeFlag());
		userFromDB.setAttemptTrasacation(user.getAttemptTrasacation());
		userFromDB.setUpdateDate(date);
		userFromDB.setDefaultCurrency(user.getDefaultCurrency());
		userFromDB.setTransactionSms(user.getTransactionSms());
		userFromDB.setAllowDuplicateOrderId(user.getAllowDuplicateOrderId());
		userFromDB.setSkipOrderIdForRefund(user.isSkipOrderIdForRefund());
		userFromDB.setPaymentMessageSlab(user.getPaymentMessageSlab());
		userFromDB.setCardSaveParam(user.getCardSaveParam());
		userFromDB.setAllowSaleDuplicate(user.isAllowSaleDuplicate());
		userFromDB.setAllowRefundDuplicate(user.isAllowRefundDuplicate());
		userFromDB.setAllowSaleInRefund(user.isAllowSaleInRefund());
		userFromDB.setAllowRefundInSale(user.isAllowRefundInSale());
		userFromDB.setCycle(user.getCycle());
		userFromDB.setTfaFlag(user.isTfaFlag());
		userDao.update(userFromDB);

		return userFromDB;
	}

	public String getActionMessage() {
		return actionMessage;
	}

	public void setActionMessage(String actionMessage) {
		this.actionMessage = actionMessage;
	}

	/*
	 * public NotificationEmailer updateNotificationEmail(NotificationEmailer
	 * userFE, String payId) { notificationEmailerDB =
	 * userDao.findByEmailerByPayId(payId); if(notificationEmailerDB ==null){
	 * userFE.setPayId(payId); userDao.createEmailerFalg(userFE); }else{
	 * notificationEmailerDB.setTransactionEmailerFlag(userFE.
	 * isTransactionEmailerFlag());
	 * notificationEmailerDB.setRefundTransactionCustomerEmailFlag(userFE.
	 * isRefundTransactionCustomerEmailFlag());
	 * notificationEmailerDB.setTransactionCustomerEmailFlag(userFE.
	 * isTransactionCustomerEmailFlag());
	 * notificationEmailerDB.setRefundTransactionMerchantEmailFlag(userFE.
	 * isRefundTransactionMerchantEmailFlag());
	 * notificationEmailerDB.setSendMultipleEmailer(userFE.getSendMultipleEmailer())
	 * ; notificationEmailerDB.setTransactionAuthenticationEmailFlag(userFE.
	 * isTransactionAuthenticationEmailFlag());
	 * notificationEmailerDB.setTransactionCustomerEmailFlag(userFE.
	 * isTransactionCustomerEmailFlag());
	 * notificationEmailerDB.setTransactionSmsFlag(userFE.isTransactionSmsFlag());
	 * notificationEmailerDB.setSurchargeFlag(userFE.isSurchargeFlag());
	 * notificationEmailerDB.setExpressPayFlag(userFE.isExpressPayFlag());
	 * notificationEmailerDB.setIframePaymentFlag(userFE.isIframePaymentFlag());
	 * notificationEmailerDB.setMerchantHostedFlag(userFE.isMerchantHostedFlag());
	 * userDao.updateNotificationEamiler(notificationEmailerDB);
	 *
	 * } return notificationEmailerDB; }
	 */

	/*
	 * public NotificationEmailer updateNotificationEmail(NotificationEmailer[]
	 * userFE, String payId) { notificationEmailerDB =
	 * userDao.findByEmailerByPayId(payId); if(notificationEmailerDB ==null){ for
	 * (NotificationEmailer notificationEmailer : userFE) {
	 * notificationEmailer.setPayId(payId); userDao.createEmailerFalg(userFE); }
	 * }else{ for (NotificationEmailer notificationEmailer : userFE) {
	 * notificationEmailerDB.setTransactionEmailerFlag(notificationEmailer.
	 * isTransactionEmailerFlag());
	 * notificationEmailerDB.setRefundTransactionCustomerEmailFlag(
	 * notificationEmailer.isRefundTransactionCustomerEmailFlag());
	 * notificationEmailerDB.setTransactionCustomerEmailFlag(notificationEmailer.
	 * isTransactionCustomerEmailFlag());
	 * notificationEmailerDB.setSurchargeFlag(notificationEmailer.isSurchargeFlag())
	 * ; userDao.updateNotificationEamiler(notificationEmailerDB); } } return
	 * notificationEmailerDB; }
	 */

}
