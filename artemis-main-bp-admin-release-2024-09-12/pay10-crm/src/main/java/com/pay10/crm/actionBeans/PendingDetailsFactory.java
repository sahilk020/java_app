package com.pay10.crm.actionBeans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pay10.commons.dao.PendingMappingRequestDao;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.dao.RouterRuleDao;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.MapList;
import com.pay10.commons.user.MerchantCurrencyPopulator;
import com.pay10.commons.user.MerchantMopPopulator;
import com.pay10.commons.user.PendingMappingRequest;
import com.pay10.commons.user.PendingResellerMappingApproval;
import com.pay10.commons.user.PendingResellerMappingDao;
import com.pay10.commons.user.PendingUserApproval;
import com.pay10.commons.user.PendingUserApprovalDao;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.RouterRule;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.SurchargeDetailsDao;
import com.pay10.commons.user.SurchargeMappingPopulator;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TDRStatus;
import com.pay10.commons.util.TransactionType;

/**
 * @author Rahul, Shaiwal
 *
 */
@Service
public class PendingDetailsFactory {

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private ChargingDetailsDao chargingDetailsDao;

	@Autowired
	private PendingResellerMappingDao pendingResellerMappingDao;

	@Autowired
	private PendingUserApprovalDao pendingUserApprovalDao;

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private SurchargeDetailsDao surchargeDetailsDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PendingMappingRequestDao pendingMappingRequestDao;

	@Autowired
	private RouterRuleDao routerRuleDao;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	public Map<String, List<SurchargeDetails>> getPendingSurchargeDetails() {
		List<SurchargeDetails> pendingSurchargeDetails = null;

		pendingSurchargeDetails = surchargeDetailsDao.findPendingDetails();
		for (SurchargeDetails surchargeDet : pendingSurchargeDetails) {
			String merchantName = userDao.findPayId(surchargeDet.getPayId()).getBusinessName();
			surchargeDet.setMerchantName(merchantName);
			surchargeDet.setCode("1");
		}

		Map<String, List<SurchargeDetails>> detailsMap = new HashMap<String, List<SurchargeDetails>>();

		detailsMap.put("Merchant Surcharge", pendingSurchargeDetails);
		return detailsMap;
	}

	public Map<String, List<SurchargeMappingPopulator>> getPendingPGSurchargeDetails() {

		List<Surcharge> pendingSurchargeList = null;
		ArrayList<SurchargeMappingPopulator> details = new ArrayList<SurchargeMappingPopulator>();
		Map<String, List<SurchargeMappingPopulator>> detailsMap = new HashMap<String, List<SurchargeMappingPopulator>>();

		pendingSurchargeList = surchargeDao.findPendingSurchargeList();
		for (Surcharge surcharge : pendingSurchargeList) {

			List<Surcharge> tempList = new ArrayList<Surcharge>();
			for (Surcharge surchargeComp : pendingSurchargeList) {
				if (surchargeComp.getPayId().equals(surcharge.getPayId())
						&& surchargeComp.getPaymentType().equals(surcharge.getPaymentType())
						&& surchargeComp.getMopType().equals(surcharge.getMopType())
						&& surchargeComp.getAcquirerName().equals(surcharge.getAcquirerName())
						&& surchargeComp.getPaymentsRegion().equals(surcharge.getPaymentsRegion())) {
					tempList.add(surchargeComp);

				}
			}
			if (tempList.size() == 1) {
				SurchargeMappingPopulator smp = new SurchargeMappingPopulator();
				for (Surcharge srch : tempList) {
					smp = generateSurchargeMap(srch, false, BigDecimal.ZERO, BigDecimal.ZERO);
					details.add(smp);
				}
			} else {
				SurchargeMappingPopulator smp = new SurchargeMappingPopulator();
				for (Surcharge srch : tempList) {
					if (srch.getOnOff().equals("1")) {
						smp.setAcquirerName(srch.getAcquirerName());
						smp.setMopType(srch.getMopType().getName());
						smp.setPaymentType(srch.getPaymentType().getName());
						smp.setStatus(srch.getStatus().getName());

						smp.setBankSurchargeAmountOnCommercial(srch.getBankSurchargeAmountCommercial());
						smp.setBankSurchargePercentageOnCommercial(srch.getBankSurchargePercentageCommercial());
						smp.setBankSurchargeAmountOnCustomer(srch.getBankSurchargeAmountCustomer());
						smp.setBankSurchargePercentageOnCustomer(srch.getBankSurchargePercentageCustomer());

						smp.setPaymentsRegion(srch.getPaymentsRegion());
						smp.setPayId(srch.getPayId());
						smp.setPaymentType(srch.getPaymentType().getName());
						smp.setAcquirerName(srch.getAcquirerName());
						smp.setMerchantName(userDao.getMerchantNameByPayId(srch.getPayId()));
						smp.setAllowOnOff(true);
						smp.setRequestedBy(srch.getRequestedBy());
					} else {
						smp.setBankSurchargeAmountOffCommercial(srch.getBankSurchargeAmountCommercial());
						smp.setBankSurchargePercentageOffCommercial(srch.getBankSurchargePercentageCommercial());
						smp.setBankSurchargeAmountOffCustomer(srch.getBankSurchargeAmountCustomer());
						smp.setBankSurchargePercentageOffCustomer(srch.getBankSurchargePercentageCustomer());
					}
				}
				details.add(smp);
			}
		}

		List<SurchargeMappingPopulator> uniques = new ArrayList<SurchargeMappingPopulator>();
		uniques = removeDuplicateFromList(details);

		detailsMap.put("Bank Surcharge", uniques);
		return detailsMap;
	}

	public Map<String, List<ChargingDetails>> getPendingChargingDetails() {

		Map<String, List<ChargingDetails>> chargingDetailsMap = new HashMap<String, List<ChargingDetails>>();
		List<ChargingDetails> data = new ArrayList<ChargingDetails>();

		data = chargingDetailsDao.getPendingChargingDetailList();

		if (data.size() > 0) {
			for (PaymentType paymentType : PaymentType.values()) {
				List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
				String paymentName = paymentType.getName();

				for (ChargingDetails cDetail : data) {
					if (cDetail.getPaymentType().getName().equals(paymentName)) {
						String businessName = userDao.getBusinessNameByPayId(cDetail.getPayId());
						cDetail.setBusinessName(businessName);
						chargingDetailsList.add(cDetail);
					}
				}

				if (chargingDetailsList.size() != 0) {
					Collections.sort(chargingDetailsList);
					chargingDetailsMap.put(paymentName, chargingDetailsList);
				}
			}
		}
		return chargingDetailsMap;
	}

	public SurchargeMappingPopulator generateSurchargeMap(Surcharge surcharge, boolean allowOnOff,
			BigDecimal bankSurchargeAmountOff, BigDecimal bankSurchargePercentageOff) {

		SurchargeMappingPopulator smp = new SurchargeMappingPopulator();
		smp.setAcquirerName(surcharge.getAcquirerName());
		smp.setMopType(surcharge.getMopType().getName());
		smp.setPaymentType(surcharge.getPaymentType().getName());
		smp.setStatus(surcharge.getStatus().getName());

		smp.setBankSurchargeAmountOffCommercial(bankSurchargeAmountOff);
		smp.setBankSurchargeAmountOnCommercial(surcharge.getBankSurchargeAmountCommercial());
		smp.setBankSurchargePercentageOffCommercial(bankSurchargePercentageOff);
		smp.setBankSurchargePercentageOnCommercial(surcharge.getBankSurchargePercentageCommercial());

		smp.setBankSurchargeAmountOffCustomer(bankSurchargeAmountOff);
		smp.setBankSurchargeAmountOnCustomer(surcharge.getBankSurchargeAmountCustomer());
		smp.setBankSurchargePercentageOffCustomer(bankSurchargePercentageOff);
		smp.setBankSurchargePercentageOnCustomer(surcharge.getBankSurchargePercentageCustomer());

		smp.setPayId(surcharge.getPayId());
		smp.setPaymentType(surcharge.getPaymentType().getName());
		smp.setAcquirerName(surcharge.getAcquirerName());
		smp.setAllowOnOff(allowOnOff);
		smp.setPaymentsRegion(surcharge.getPaymentsRegion());
		smp.setMerchantName(userDao.getBusinessNameByPayId(surcharge.getPayId()));
		smp.setRequestedBy(surcharge.getRequestedBy());
		smp.setPaymentsRegion(surcharge.getPaymentsRegion());
		return smp;
	}

	public List<SurchargeMappingPopulator> removeDuplicateFromList(List<SurchargeMappingPopulator> list) {
		int s = 0;
		List<SurchargeMappingPopulator> list2 = new ArrayList<SurchargeMappingPopulator>();
		for (SurchargeMappingPopulator us1 : list) {
			for (SurchargeMappingPopulator us2 : list2) {
				if (us1.getPayId().equals(us2.getPayId()) && us1.getPaymentType().equals(us2.getPaymentType())
						&& us1.getMopType().equals(us2.getMopType())
						&& us1.getAcquirerName().equals(us2.getAcquirerName())) {
					s = 1;
				} else {
					s = 0;
				}

			}
			if (s == 0) {
				list2.add(us1);
			}

		}
		return list2;
	}

	public Map<String, List<ServiceTax>> getPendingServiceTax() {
		List<ServiceTax> pendingList = serviceTaxDao.getPendingServiceTaxList();
		Map<String, List<ServiceTax>> pendingServiceTaxMap = new HashMap<String, List<ServiceTax>>();
		if (pendingList.size() > 0) {
			pendingServiceTaxMap.put("Service Tax", pendingList);

		}

		return pendingServiceTaxMap;

	}

	public Map<String, List<RouterRule>> getPendingRouterRule() {
		List<RouterRule> pendingList = routerRuleDao.getPendingRules();
		List<RouterRule> pendingListMerchant = new ArrayList<RouterRule>();
		
		for (RouterRule routerRule : pendingList ) {
			
			User user = userDao.findPayId(routerRule.getMerchant());
			routerRule.setMerchantName(user.getBusinessName());
			pendingListMerchant.add(routerRule);
			
		}
		
		
		Map<String, List<RouterRule>> pendingServiceTaxMap = new HashMap<String, List<RouterRule>>();
		if (pendingListMerchant.size() > 0) {
			pendingServiceTaxMap.put("Router Rule", pendingListMerchant);

		}

		return pendingServiceTaxMap;

	}

	public Map<String, List<RouterConfiguration>> getPendingRouterConfiguration() {
		List<RouterConfiguration> pendingListMerchant = new ArrayList<RouterConfiguration>();
		
		List<RouterConfiguration> pendingList = routerConfigurationDao.getPendingRouterConfiguration();
		
		Set<String> uniqueIdentifierSet  = new HashSet<String>();
		
		for (RouterConfiguration routerConfiguration : pendingList) {
			uniqueIdentifierSet.add(routerConfiguration.getIdentifier());
		}
		
		
		for (String identifier : uniqueIdentifierSet ) {
			
			List<RouterConfiguration> pendingListByIdentifier = routerConfigurationDao.findPendingRulesByIdentifier(identifier);
			
			if (pendingListByIdentifier.size() > 0) {
				
				RouterConfiguration rc = pendingListByIdentifier.get(0);
				StringBuffer loadString = new StringBuffer();
				
				for (RouterConfiguration config : pendingListByIdentifier) {

					loadString.append(config.getAcquirer());
					loadString.append(" - ");
					loadString.append(config.getLoadPercentage());
					loadString.append(" % ");
					loadString.append(" , ");
				}
			
				rc.setLoadPercent(loadString.toString().substring(0, loadString.toString().length()-2));
				rc.setMerchantName(userDao.getBusinessNameByPayId(rc.getMerchant()));
				pendingListMerchant.add(rc);
			}
			
		}

		Map<String, List<RouterConfiguration>> pendingServiceTaxMap = new HashMap<String, List<RouterConfiguration>>();
		if (pendingListMerchant.size() > 0) {
			pendingServiceTaxMap.put("Smart Router", pendingListMerchant);

		}

		return pendingServiceTaxMap;

	}

	public Map<String, List<MapList>> getTestData() {
		Map<String, List<MapList>> pendingServiceTaxMap = new HashMap<String, List<MapList>>();

		List<PendingMappingRequest> pendingMappingRequestList = new ArrayList<PendingMappingRequest>();
		pendingMappingRequestList = pendingMappingRequestDao.getPendingMappingRequest();

		for (PendingMappingRequest pendingrequest : pendingMappingRequestList) {

			List<MapList> pendingList = new ArrayList<MapList>();
			MapList mapList = new MapList();
			List<MerchantMopPopulator> mmpList = new ArrayList<MerchantMopPopulator>();
			List<MerchantCurrencyPopulator> mcpList = new ArrayList<MerchantCurrencyPopulator>();

			String merchantEmailId = pendingrequest.getMerchantEmailId();
			String acquirer = pendingrequest.getAcquirer();
			String accountCurrencySet = pendingrequest.getAccountCurrencySet();
			String mapString = pendingrequest.getMapString();
			String businessName = userDao.getBusinessNameByEmailId(merchantEmailId);

			Gson gson = new Gson();
			AccountCurrency[] accountCurrencies = gson.fromJson(accountCurrencySet, AccountCurrency[].class);
			for (AccountCurrency accountCurrency : accountCurrencies) {

				MerchantCurrencyPopulator mcp = new MerchantCurrencyPopulator();
				mcp.setAcquirer(AcquirerType.getInstancefromCode(acquirer).getName());
				mcp.setBusinessType(merchantEmailId);
				mcp.setCurrency(accountCurrency.getCurrencyCode());
				mcp.setMerchantId(accountCurrency.getMerchantId());
				mcp.setPassword(accountCurrency.getPassword());
				mcp.setStatus(TDRStatus.PENDING);
				mcp.setTxnKey(accountCurrency.getTxnKey());
				mcp.setNon3ds(accountCurrency.isDirectTxn());

				mcpList.add(mcp);
			}

			List<String> mapStringlist = new ArrayList<String>(Arrays.asList(mapString.split(",")));

			for (String mapStrings : mapStringlist) {

				MerchantMopPopulator mmp = new MerchantMopPopulator();

				String[] tokens = mapStrings.split("-");

				switch (tokens[0]) {

				case "Credit Card":

					String txnTypeCC = TransactionType.getInstanceFromCode(tokens[2]).getName();
					String mopCC = MopType.getmopName(tokens[1]);
					boolean foundCCEntry = false;

					if (mmpList.size() > 0) {
						for (MerchantMopPopulator m : mmpList) {
							if (m.getPaymentType().equalsIgnoreCase("Credit Card")
									&& m.getMopType().equalsIgnoreCase(mopCC)) {

								if (txnTypeCC.equalsIgnoreCase("AUTHORISE")) {
									m.setAuth(true);
								} else if (txnTypeCC.equalsIgnoreCase("SALE")) {
									m.setSale(true);
								}

								else if (txnTypeCC.equalsIgnoreCase("REFUND")) {
									m.setRefund(true);
								}

								foundCCEntry = true;
							}
						}
					}

					if (foundCCEntry) {
						break;
					}

					mmp.setPaymentType("Credit Card");
					mmp.setMopType(mopCC);

					if (txnTypeCC.equalsIgnoreCase("AUTHORISE")) {
						mmp.setAuth(true);
					} else if (txnTypeCC.equalsIgnoreCase("SALE")) {
						mmp.setSale(true);
					}

					else if (txnTypeCC.equalsIgnoreCase("REFUND")) {
						mmp.setRefund(true);
					}
					mmp.setStatus(TDRStatus.PENDING);
					mmpList.add(mmp);

					break;
				case "Debit Card":

					String txnTypeDC = TransactionType.getInstanceFromCode(tokens[2]).getName();
					String mopDC = MopType.getmopName(tokens[1]);
					boolean foundDCEntry = false;

					if (mmpList.size() > 0) {
						for (MerchantMopPopulator m : mmpList) {
							if (m.getPaymentType().equalsIgnoreCase("Debit Card")
									&& m.getMopType().equalsIgnoreCase(mopDC)) {

								if (txnTypeDC.equalsIgnoreCase("AUTHORISE")) {
									m.setAuth(true);
								} else if (txnTypeDC.equalsIgnoreCase("SALE")) {
									m.setSale(true);
								}

								else if (txnTypeDC.equalsIgnoreCase("REFUND")) {
									m.setRefund(true);
								}
								foundDCEntry = true;
							}
						}
					}

					if (foundDCEntry) {
						break;
					}

					mmp.setPaymentType("Debit Card");
					mmp.setMopType(mopDC);

					if (txnTypeDC.equalsIgnoreCase("AUTHORISE")) {
						mmp.setAuth(true);
					} else if (txnTypeDC.equalsIgnoreCase("SALE")) {
						mmp.setSale(true);
					}

					else if (txnTypeDC.equalsIgnoreCase("REFUND")) {
						mmp.setRefund(true);
					}
					mmp.setStatus(TDRStatus.PENDING);
					mmpList.add(mmp);
					break;

				case "UPI":

					String txnTypeUPI = TransactionType.getInstanceFromCode(tokens[2]).getName();
					String mopUP = MopType.getmopName(tokens[1]);
					boolean foundUPIEntry = false;

					if (mmpList.size() > 0) {
						for (MerchantMopPopulator m : mmpList) {
							if (m.getPaymentType().equalsIgnoreCase("UPI") && m.getMopType().equalsIgnoreCase(mopUP)) {

								if (txnTypeUPI.equalsIgnoreCase("AUTHORISE")) {
									m.setAuth(true);
								} else if (txnTypeUPI.equalsIgnoreCase("SALE")) {
									m.setSale(true);
								}

								else if (txnTypeUPI.equalsIgnoreCase("REFUND")) {
									m.setRefund(true);
								}
								foundDCEntry = true;
							}
						}
					}

					if (foundUPIEntry) {
						break;
					}

					mmp.setPaymentType("UPI");
					mmp.setMopType(mopUP);

					if (txnTypeUPI.equalsIgnoreCase("AUTHORISE")) {
						mmp.setAuth(true);
					} else if (txnTypeUPI.equalsIgnoreCase("SALE")) {
						mmp.setSale(true);
					}

					else if (txnTypeUPI.equalsIgnoreCase("REFUND")) {
						mmp.setRefund(true);
					}
					mmp.setStatus(TDRStatus.PENDING);
					mmpList.add(mmp);
					break;

				case "Net Banking":
					mmp.setPaymentType("Net Banking");
					mmp.setStatus(TDRStatus.PENDING);
					mmp.setNbBank(MopType.getmopName(tokens[1]));
					mmpList.add(mmp);
					break;

				}

			}

			mapList.setMcpList(mcpList);
			mapList.setMmpList(mmpList);
			pendingList.add(mapList);

			if (pendingList.size() > 0) {
				pendingServiceTaxMap.put(businessName + " --- " + AcquirerType.getInstancefromCode(acquirer).getName(),
						pendingList);
			}
		}

		return pendingServiceTaxMap;

	}

	public Map<String, User> getPendingUserProfile() {
		List<PendingUserApproval> pendingList = pendingUserApprovalDao.getPendingUserProfileList();
		Map<String, User> pendingUserProfileMap = new HashMap<String, User>();
		if (pendingList == null) {
			return pendingUserProfileMap;
		}

		for (PendingUserApproval obj : pendingList) {
			User newObj = new User();
			newObj.setModeType(obj.getModeType());
			newObj.setComments(obj.getComments());
			newObj.setWhiteListIpAddress(obj.getWhiteListIpAddress());
			newObj.setUserStatus(obj.getUserStatus());

			newObj.setBusinessName(obj.getBusinessName());
			newObj.setFirstName(obj.getFirstName());
			newObj.setLastName(obj.getLastName());
			newObj.setCompanyName(obj.getCompanyName());
			newObj.setWebsite(obj.getWebsite());
			newObj.setContactPerson(obj.getContactPerson());
			newObj.setEmailId(obj.getEmailId());
			newObj.setRegistrationDate(obj.getRegistrationDate());

			newObj.setMerchantType(obj.getMerchantType());
			newObj.setNoOfTransactions(obj.getNoOfTransactions());
			newObj.setAmountOfTransactions(obj.getAmountOfTransactions());
			newObj.setResellerId(obj.getResellerId());
			newObj.setProductDetail(obj.getProductDetail());

			newObj.setMobile(obj.getMobile());
			newObj.setTransactionSmsFlag(obj.isTransactionSmsFlag());
			newObj.setTelephoneNo(obj.getTelephoneNo());
			newObj.setFax(obj.getFax());
			newObj.setAddress(obj.getAddress());
			newObj.setCity(obj.getCity());
			newObj.setState(obj.getState());
			newObj.setCountry(obj.getCountry());
			newObj.setPostalCode(obj.getPostalCode());

			newObj.setBankName(obj.getBankName());
			newObj.setIfscCode(obj.getIfscCode());
			newObj.setAccHolderName(obj.getAccHolderName());
			newObj.setCurrency(obj.getCurrency());
			newObj.setBranchName(obj.getBranchName());
			newObj.setPanCard(obj.getPanCard());
			newObj.setAccountNo(obj.getAccountNo());

			newObj.setOrganisationType(obj.getOrganisationType());
			newObj.setMultiCurrency(obj.getMultiCurrency());
			newObj.setBusinessModel(obj.getBusinessModel());
			newObj.setOperationAddress(obj.getOperationAddress());
			newObj.setOperationState(obj.getOperationState());
			newObj.setOperationCity(obj.getOperationCity());
			newObj.setOperationPostalCode(obj.getOperationPostalCode());
			newObj.setDateOfEstablishment(obj.getDateOfEstablishment());

			newObj.setCin(obj.getCin());
			newObj.setPan(obj.getPan());
			newObj.setPanName(obj.getPanName());
			newObj.setTransactionEmailerFlag(obj.isTransactionEmailerFlag());
			newObj.setTransactionEmailId(obj.getTransactionEmailId());
			newObj.setExpressPayFlag(obj.isExpressPayFlag());
			newObj.setMerchantHostedFlag(obj.isMerchantHostedFlag());
			newObj.setIframePaymentFlag(obj.isIframePaymentFlag());
			newObj.setSurchargeFlag(obj.isSurchargeFlag());
			newObj.setTransactionAuthenticationEmailFlag(obj.isTransactionAuthenticationEmailFlag());
			newObj.setTransactionCustomerEmailFlag(obj.isTransactionCustomerEmailFlag());
			newObj.setRefundTransactionCustomerEmailFlag(obj.isRefundTransactionCustomerEmailFlag());
			newObj.setRefundTransactionMerchantEmailFlag(obj.isRefundTransactionMerchantEmailFlag());
			newObj.setTransactionFailedAlertFlag(obj.isTransactionFailedAlertFlag());
			newObj.setNotificationApiEnableFlag(obj.isNotificationApiEnableFlag());
			newObj.setNotificaionApi(obj.getNotificaionApi());
			newObj.setPaymentLink(obj.getPaymentLink());
			newObj.setTransactionFailedAlertFlag(obj.isTransactionFailedAlertFlag());
			
			newObj.setRetryTransactionCustomeFlag(obj.isRetryTransactionCustomeFlag());
			newObj.setAttemptTrasacation(obj.getAttemptTrasacation());
			newObj.setExtraRefundLimit(obj.getExtraRefundLimit());
			newObj.setUpdateDate(obj.getUpdateDate());
			newObj.setDefaultCurrency(obj.getDefaultCurrency());
			newObj.setmCC(obj.getMCC());
			newObj.setAmexSellerId(obj.getAmexSellerId());
			newObj.setDefaultLanguage(obj.getDefaultLanguage());
			newObj.setIndustryCategory(obj.getIndustryCategory());
			newObj.setIndustrySubCategory(obj.getIndustrySubCategory());
			newObj.setRequestedBy(obj.getRequestedBy());
			newObj.setPayId(obj.getPayId());

			String payId = String.valueOf(obj.getPayId());
			User userFromDB = userDao.findPayId(payId);
			String merchantName = userFromDB.getBusinessName();

			pendingUserProfileMap.put(merchantName, userFromDB);
			pendingUserProfileMap.put(merchantName, newObj);
		}

		/*
		 * if (pendingUserProfileMap.size() == 0){ String testUsr = ""; User usr = new
		 * User(); pendingUserProfileMap.put(testUsr, usr); }
		 */
		return pendingUserProfileMap;

	}

	public Map<String, User> getPendingResellerMapping() {
		List<PendingResellerMappingApproval> pendingList = pendingResellerMappingDao.getPendingResellerMappingList();
		Map<String, User> pendingUserProfileMap = new HashMap<String, User>();

		for (PendingResellerMappingApproval obj : pendingList) {
			User newObj = new User();
			newObj.setResellerId(obj.getResellerId());
			newObj.setEmailId(obj.getMerchantEmailId());
			newObj.setRequestedBy(obj.getRequestedBy());
			String payId = String.valueOf(obj.getMerchantPayId());
			User userFromDB = userDao.findPayId(payId);
			String merchantName = userFromDB.getBusinessName();

			pendingUserProfileMap.put(merchantName, userFromDB);
			pendingUserProfileMap.put(merchantName, newObj);
		}

		/*
		 * if (pendingUserProfileMap.size() == 0){ String testUsr = ""; User usr = new
		 * User(); pendingUserProfileMap.put(testUsr, usr); }
		 */
		return pendingUserProfileMap;

	}
}
