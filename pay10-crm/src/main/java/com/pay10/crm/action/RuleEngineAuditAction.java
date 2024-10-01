package com.pay10.crm.action;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RouterRuleDao;
import com.pay10.commons.user.RouterRule;
import com.pay10.commons.user.SmartRouterAuditObject;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

@SuppressWarnings("serial")
public class RuleEngineAuditAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(RuleEngineAuditAction.class.getName());

	private String merchantPayId;
	private String paymentType;
	private int draw;
	private int length;
	private int start;
	private String cardHolderType;
	private BigInteger recordsTotal;
	public BigInteger recordsFiltered;

	private List<SmartRouterAuditObject> aaData;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RouterRuleDao routerRuleDao;

	@Override
	public String execute() {

		logger.info("Inside RuleEngineAuditAction");

		List<SmartRouterAuditObject> ruleEngineList = new ArrayList<SmartRouterAuditObject>();

		if (StringUtils.isBlank(merchantPayId) || StringUtils.isBlank(paymentType)) {
			setAaData(ruleEngineList);
			return SUCCESS;
		}

		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			PaymentType payType = PaymentType.getInstanceIgnoreCase(paymentType);
			List<RouterRule> routerConfigList = routerRuleDao.getActiveRulesByAcquirer(merchantPayId, payType.getCode(),
					start, length, cardHolderType);
			setRecordsTotal(BigInteger.valueOf(routerRuleDao.getActiveRulesCountByAcquirer(merchantPayId,
					payType.getCode(), start, length, cardHolderType)));
			recordsFiltered = BigInteger.valueOf(routerConfigList.size());
			recordsFiltered = recordsTotal;
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			for (RouterRule routerConfiguration : routerConfigList) {

				SmartRouterAuditObject smartRouterAuditObject = new SmartRouterAuditObject();

				smartRouterAuditObject.setId(String.valueOf(routerConfiguration.getId()));
				smartRouterAuditObject.setMerchantName(routerConfiguration.getMerchant());
				smartRouterAuditObject.setPaymentType(routerConfiguration.getPaymentType());
				// smartRouterAuditObject.setMopType(routerConfiguration.getMopType());
				if (routerConfiguration.getPaymentType().equalsIgnoreCase("NB")) {
					smartRouterAuditObject.setMopType(MopType.getmopName(routerConfiguration.getMopType()));
				} else {
					smartRouterAuditObject.setMopType(routerConfiguration.getMopType());
				}
				smartRouterAuditObject.setRegion(String.valueOf(routerConfiguration.getPaymentsRegion()));
				smartRouterAuditObject.setTxnType(String.valueOf(routerConfiguration.getCardHolderType()));
				smartRouterAuditObject.setAcquirer(routerConfiguration.getAcquirerMap());
				smartRouterAuditObject.setStatus(routerConfiguration.getStatus().getName());
//			smartRouterAuditObject.setCreatedDate(String.valueOf(routerConfiguration.getCreatedDate()));
				smartRouterAuditObject.setCreatedDate(
						routerConfiguration.getCreatedDate() != null ? df.format(routerConfiguration.getCreatedDate())
								: "");
				smartRouterAuditObject.setUpdatedDate(String.valueOf(routerConfiguration.getUpdatedDate()));
				smartRouterAuditObject.setRequestedBy(routerConfiguration.getRequestedBy());
				smartRouterAuditObject.setOnOffFlag(String.valueOf(routerConfiguration.isOnUsFlag()));
				smartRouterAuditObject.setCurrency(String.valueOf(routerConfiguration.getCurrency()));
				ruleEngineList.add(smartRouterAuditObject);
			}
		} catch (Exception e) {
			logger.error("Exception in RuleEngineAuditAction " + e);
			return SUCCESS;
		}
		setAaData(ruleEngineList);
		return SUCCESS;

	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public List<SmartRouterAuditObject> getAaData() {
		return aaData;
	}

	public void setAaData(List<SmartRouterAuditObject> aaData) {
		this.aaData = aaData;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public BigInteger getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(BigInteger recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public BigInteger getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(BigInteger recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

}
