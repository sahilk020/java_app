package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.SmartRouterAuditObject;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

@SuppressWarnings("serial")
public class SmartRouterAuditAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(SmartRouterAuditAction.class.getName());

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
	private RouterConfigurationDao routerConfigurationDao ;

	@Override
	public String execute() {

		logger.info("Inside SmartRouterAuditAction");
		
		List<SmartRouterAuditObject> smartRouterList = new ArrayList<SmartRouterAuditObject>();
		
		if (StringUtils.isBlank(merchantPayId) || StringUtils.isBlank(paymentType)) {
			setAaData(smartRouterList);
			return SUCCESS;
		}
		
		try {

			PaymentType payType = PaymentType.getInstanceIgnoreCase(paymentType);
			List<RouterConfiguration> routerConfigList = routerConfigurationDao.getActiveRulesByAcquirer(merchantPayId, payType.getCode(),start,length,cardHolderType);
			setRecordsTotal(BigInteger.valueOf(routerConfigurationDao.getActiveRulesCountByAcquirer(merchantPayId, payType.getCode(),start,length,cardHolderType)));
			recordsFiltered = BigInteger.valueOf(routerConfigList.size());
			recordsFiltered = recordsTotal;
			if(getLength()==-1){
				setLength(getRecordsTotal().intValue());
			}
			for (RouterConfiguration routerConfiguration:routerConfigList) {
				
			SmartRouterAuditObject smartRouterAuditObject = new SmartRouterAuditObject();
			
			smartRouterAuditObject.setId(String.valueOf(routerConfiguration.getId()));
			smartRouterAuditObject.setMerchantName(routerConfiguration.getMerchant());
			smartRouterAuditObject.setPaymentType(routerConfiguration.getPaymentType());
			//smartRouterAuditObject.setMopType(routerConfiguration.getMopType());
			if(routerConfiguration.getPaymentType().equalsIgnoreCase("NB")) {
				smartRouterAuditObject.setMopType(MopType.getmopName(routerConfiguration.getMopType()));
			}else {
				smartRouterAuditObject.setMopType(routerConfiguration.getMopType());	
			}
			smartRouterAuditObject.setRegion(String.valueOf(routerConfiguration.getPaymentsRegion()));
			smartRouterAuditObject.setTxnType(String.valueOf(routerConfiguration.getCardHolderType()));
			smartRouterAuditObject.setAcquirer(routerConfiguration.getAcquirer());
			smartRouterAuditObject.setLoadPercent(String.valueOf(routerConfiguration.getLoadPercentage()));
			smartRouterAuditObject.setStatus(routerConfiguration.getStatus().getName());
			smartRouterAuditObject.setMinValue(String.valueOf(routerConfiguration.getMinAmount()));
			smartRouterAuditObject.setMaxValue(String.valueOf(routerConfiguration.getMaxAmount()));
			smartRouterAuditObject.setCreatedDate(String.valueOf(routerConfiguration.getCreatedDate()));
			smartRouterAuditObject.setUpdatedDate(String.valueOf(routerConfiguration.getUpdatedDate()));
			smartRouterAuditObject.setRequestedBy(routerConfiguration.getRequestedBy());
			smartRouterList.add(smartRouterAuditObject);
			}
		} catch (Exception e) {
			logger.error("Exception in SmartRouterAuditAction "+e);
			return SUCCESS;
		}
		setAaData(smartRouterList);
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
