package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.RouterConfigurationPayout;
import com.pay10.commons.user.RouterConfigurationPayoutDao;
import com.pay10.commons.user.RouterRule;
import com.pay10.commons.user.RouterRuleDaoPayout;
import com.pay10.commons.user.RouterRulePayout;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.TdrSettingPayoutDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.commons.util.TDRStatus;

public class RuleSetUpPayoutAction extends AbstractSecureAction {

	private static final long serialVersionUID = -5819377796996242126L;
	private static Logger logger = LoggerFactory.getLogger(RuleSetUpPayoutAction.class.getName());
	boolean Valsubmit;



	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	private List<RouterRulePayout> listData;


	private int num;
	
	private String token;
	
	@Autowired
	private RouterConfigurationPayoutDao routerConfigurationPayoutDao;
	
	@Autowired
	private RouterRuleDaoPayout RouterRuleDaoPayout;
	
	@Autowired
	private ChargingDetailsDao chargingdao;

	@Autowired
	private TdrSettingPayoutDao tdrSettingPayoutDao;
	
	@Autowired
	private MultCurrencyCodeDao currencyCodeDao;
	
	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private StaticDataProvider staticDataProvider;

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AuditTrailService auditTrailService;

	private String response;
	private User sessionUser = new User();

	private boolean flag;


	@SuppressWarnings("unchecked")
	@Override
	public String execute() {

		logger.info("RuleSetUpAction :::::");
		boolean validation = true;
		
		String sessionToken = (String) sessionMap.get("customToken");
		if (!StringUtils.equalsIgnoreCase(getToken(), sessionToken)) {
			return "invalid.token";
		}
		
		


		if (num == 1) {
//			validation = getvalidation();
			validation=true;
		}

		if (validation) {

			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			logger.info("sessionUser :::::"+sessionUser);
			try {

				Date currentDate = new Date();
				logger.info("Current DATE :::"+currentDate);
				RouterRulePayout routerRuleDb = null;

				logger.info("LIST DATA ::::"+ new Gson().toJson(listData) );
				String tempPayId = null;
				if(CollectionUtils.isNotEmpty(listData)){
					 tempPayId = listData.get(0).getMerchant();
					logger.info("tempPayId :::"+tempPayId);
					logger.info("tempPayId PayId::"+listData.get(0).getPayId());
				}




					for (RouterRulePayout routerRule : listData) {

						routerRule.setCurrency(routerRule.getCurrency());

						routerRule.setChannel(routerRule.getChannel());

						routerRuleDb = RouterRuleDaoPayout.getMatchingRule(routerRule);

						if (routerRuleDb != null) {
							logger.info("routerRuleDb in IF" + routerRuleDb);

							if (StringUtils.isEmpty(response)) {
								logger.info("RESPONSE :::" + response);
								setResponse(ErrorType.SOME_RULE_ALREADY_EXIST.getResponseMessage());
								addActionMessage(ErrorType.SOME_RULE_ALREADY_EXIST.getResponseMessage());
								flag = false;
							}
						} else {

							logger.info("ELSE CONDITION");
							if (routerRule.getId() != null) {
								logger.info("routerRule.getId() :::::" + routerRule.getId());
								removeAllPendingRulesAndConfig(routerRule.getId());
							}

							String currency = routerRule.getCurrency();
							logger.info("Currency Code ::" + currency);
							routerRule.setCreatedDate(currentDate);
							logger.info("Current Date :::" + currentDate);
							routerRule.setStatus(TDRStatus.ACTIVE);
							logger.info("TDR STATUS::::" + TDRStatus.ACTIVE);
							routerRule.setCurrency(currency);
							routerRule.setRequestedBy(sessionUser.getEmailId());
							logger.info("Email ID Session ::::" + sessionUser.getEmailId());
							routerRule.setApprovedBy(sessionUser.getEmailId());
							RouterRuleDaoPayout.create(routerRule);
							addRouterRuleConfiguration(routerRule);

							Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
									.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
							AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(routerRule), null,
									actionMessagesByAction.get("onusoffusRulesSetup"));
							auditTrailService.saveAudit(request, auditTrail);
						}
					}
				
				if (StringUtils.isEmpty(response)) {
					setResponse(ErrorType.ROUTER_RULE_CREATED.getResponseMessage());
					addActionMessage(ErrorType.ROUTER_RULE_CREATED.getResponseMessage());
					flag = true;
				}
			} catch (Exception exception) {
				setResponse(ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			}

		} else

		{
			setResponse(ErrorType.Acq_NotMap_with_paytype.getResponseMessage());
		}
		return SUCCESS;
	}

	private boolean getvalidation() {
		for (RouterRulePayout chargingDetail : listData) {

			String[] acquirermap1 = chargingDetail.getAcquirerMap().split(",");
			for (String acquirerString1 : acquirermap1) {
				String[] acquirerMapString1 = acquirerString1.split("-");
				String valaquirer = acquirerMapString1[1];

				String valmop = chargingDetail.getCurrency();
				String valpayment = chargingDetail.getChannel();
				String valpayid = chargingDetail.getMerchant();
				String Acqui=AcquirerType.getAcquirerName(valaquirer);
				logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+Acqui);

				boolean respCount = tdrSettingPayoutDao.getruleenginval(Acqui, valmop, valpayment, valpayid);
				Valsubmit = respCount;
				if (respCount == false) {
					break;
				}
			}
		}
		return Valsubmit;
	}


	public void addRouterRuleConfiguration(RouterRulePayout routerRule) {
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		try {

			String[] acquirerMap = routerRule.getAcquirerMap().split(",");
			// Present in YML file
			String slabAmountArrayString = propertiesManager.propertiesMap
					.get(Constants.SWITCH_ACQUIRER_AMOUNT.getValue());
			StringBuilder tempSlab = new StringBuilder();
			// If no slab is set for this merchant , create a default slab with 00 as ID and
			// limit from 0.01 to 5000000.00

			if (!slabAmountArrayString.contains(routerRule.getMerchant())) {
				slabAmountArrayString = "00-0.01-5000000.00-" + routerRule.getMerchant() + "-ALL";
			}

			// If any slab is found , check if slab is for this particular payment type
			else {

				String[] slabArray = slabAmountArrayString.split(",");
				for (String currentSlab : slabArray) {

					if (!currentSlab.contains(routerRule.getMerchant())) {
						continue;
					}

					String[] slabSplit = currentSlab.split("-");

					if (!StringUtils.isBlank(slabSplit[4])
							&& slabSplit[4].equalsIgnoreCase(routerRule.getChannel())) {

						tempSlab.append(currentSlab);
						tempSlab.append(",");
						continue;
					} else {
						continue;
					}
				}

			}

			if (StringUtils.isNotBlank(tempSlab.toString())) {
				slabAmountArrayString = tempSlab.toString();
			} else {
				slabAmountArrayString = "00-0.01-5000000.00-" + routerRule.getMerchant() + "-ALL";
			}

			String[] slabArray = slabAmountArrayString.split(",");

			for (String currentSlab : slabArray) {

				if (StringUtils.isBlank(currentSlab)) {
					continue;
				}

				if (!currentSlab.contains(routerRule.getMerchant())) {
					continue;
				}

				String[] slabSplit = currentSlab.split("-");
				String slabId = slabSplit[0];
				String minTransactionAmount = slabSplit[1];
				String maxTransactionAmount = slabSplit[2];
				String paymentType = "ALL";

				if (!StringUtils.isBlank(slabSplit[4])) {
					paymentType = slabSplit[4];
				}

				if (!paymentType.equalsIgnoreCase(routerRule.getChannel())) {

					slabId = "00";
					minTransactionAmount = "0.01";
					maxTransactionAmount = "5000000.00";

				}

				List<RouterConfigurationPayout> routerConfigurationList = new ArrayList<RouterConfigurationPayout>();

				Map<String, User> userMap = new HashMap<String, User>();

				for (String acquirerString : acquirerMap) {

					String identifier = routerRule.getMerchant() 
							+ routerRule.getChannel() + routerRule.getCurrency() + routerRule.getTransactionType()
							+ routerRule.getPaymentsRegion().toString() + routerRule.getCardHolderType().toString()
							+ slabId;

					String[] acquirerMapString = acquirerString.split("-");

					RouterConfigurationPayout routerConfiguration = new RouterConfigurationPayout();
					Date date = new Date();

					routerConfiguration.setIdentifier(identifier);
					routerConfiguration.setAcquirer(acquirerMapString[1]);
					routerConfiguration.setRulePriority(acquirerMapString[0]);
					routerConfiguration.setCurrency(routerRule.getCurrency());
					routerConfiguration.setChannel(routerRule.getChannel());
					routerConfiguration.setTransactionType(routerRule.getTransactionType());
					routerConfiguration.setMode("AUTO");
					routerConfiguration.setStatus(TDRStatus.ACTIVE);
					routerConfiguration.setAllowedFailureCount(5);
					routerConfiguration.setCreatedDate(date);
					routerConfiguration.setUpdatedDate(date);
					routerConfiguration.setMerchant(routerRule.getMerchant());
					routerConfiguration.setOnUsoffUs(routerRule.isOnUsFlag());
					routerConfiguration.setDown(false);
					routerConfiguration.setRequestedBy(sessionUser.getEmailId());
					routerConfiguration.setRetryMinutes("10");
					routerConfiguration.setFailureCount(0);
					routerConfiguration.setPaymentsRegion(routerRule.getPaymentsRegion());
					routerConfiguration.setCardHolderType(routerRule.getCardHolderType());
					routerConfiguration.setSlabId(slabId);
					routerConfiguration.setMinAmount(Double.valueOf(minTransactionAmount));
					routerConfiguration.setMaxAmount(Double.valueOf(maxTransactionAmount));
					routerConfiguration.setTotalTxn(5000000);
					routerConfiguration.setFailedCount(51);

					User merchant = null;
					if (propertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
							&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
									.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {

						merchant = staticDataProvider.getUserData(routerRule.getMerchant());

					}

					else {
						merchant = userDao.findPayId(routerRule.getMerchant());
					}

							// Will be fetched from new mapping platform , setting constant value for now
							routerConfiguration.setSurcharge("1.00");

						
					

					routerConfigurationList.add(routerConfiguration);

				}

			


				Comparator<RouterConfigurationPayout> comp = (RouterConfigurationPayout a, RouterConfigurationPayout b) -> {

					if (Double.valueOf(b.getSurcharge()) > Double.valueOf(a.getSurcharge())) {
						return -1;
					} else if (Double.valueOf(b.getSurcharge()) < Double.valueOf(a.getSurcharge())) {
						return 1;
					} else {
						if (Double.valueOf(b.getRulePriority()) > Double.valueOf(a.getRulePriority())) {
							return -1;
						} else {
							return 1;
						}
					}
				};

				Collections.sort(routerConfigurationList, comp);

				int count = 1;

				for (RouterConfigurationPayout entry : routerConfigurationList) {

					RouterConfigurationPayout routerConfigurationToSave = new RouterConfigurationPayout();
					routerConfigurationToSave = entry;

					routerConfigurationToSave.setPriority(String.valueOf(count));
					if (count == 1) {
						routerConfigurationToSave.setLoadPercentage(100);
						routerConfigurationToSave.setCurrentlyActive(true);
					}

					else {
						routerConfigurationToSave.setLoadPercentage(0);
						routerConfigurationToSave.setCurrentlyActive(false);
					}
					
					routerConfigurationToSave.setTotalTxn(5000000);
					routerConfigurationToSave.setFailedCount(51);
					routerConfigurationPayoutDao.create(routerConfigurationToSave);
					count++;
				}

			}
			// }

		}

		catch (Exception e) {

			logger.error("Error occured wile adding Router Configuration " + e.getMessage());
		}

	}

	public void removeAllPendingRulesAndConfig(Long id) {

		RouterRulePayout routerRule = RouterRuleDaoPayout.findRule(id);

		// Find pending Rules in Router Rule
		RouterRulePayout routerRulePending = RouterRuleDaoPayout.getPendingMatchingRule(routerRule);
		if (routerRulePending != null) {
			RouterRuleDaoPayout.delete(routerRulePending, sessionUser.getEmailId());
		}

		// Find pending Router Configuration for this router rule
		List<RouterConfigurationPayout> routerConfigurationPending = routerConfigurationPayoutDao
				.getPendingRulesByRouterRule(routerRule);

		if (routerConfigurationPending.size() > 0) {

			for (RouterConfigurationPayout routerConfiguration : routerConfigurationPending) {
				routerConfigurationPayoutDao.delete(routerConfiguration.getId());
			}

		}
	}

	public void onusoffusRulesSetup(Long id) {

	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<RouterRulePayout> getListData() {
		return listData;
	}

	public void setListData(List<RouterRulePayout> listData) {
		this.listData = listData;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}