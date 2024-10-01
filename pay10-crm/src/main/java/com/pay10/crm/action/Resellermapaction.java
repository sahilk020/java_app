package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.Resellercommision;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.resellercommisiondao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;

public class Resellermapaction extends AbstractSecureAction {
	private static final long serialVersionUID = -6879974923614009981L;
	private static Logger logger = LoggerFactory.getLogger(Resellermapaction.class.getName());

	@Autowired
	private UserDao userDao;

	@Autowired
	private resellercommisiondao commisiondao;
	
	
	@Autowired
	private TdrSettingDao tdrSettingDao ;

	@Autowired
	private ChargingDetailsDao chargingdetaildao;

	private List<Merchants> listReseller = new ArrayList<Merchants>();
	private List<String> verfiy = new ArrayList<String>();

	public List<Merchants> listMerchant = new ArrayList<Merchants>();

	String ResellerPayId;
	String MechantpayId;
	private Resellercommision commision = new Resellercommision();
	private User sessionUser = null;
	private long count=0;

	@SuppressWarnings("unchecked")
	public String execute() {

		try {

			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			setListReseller(userDao.getResellerList());
			setListMerchant(userDao.getActiveMerchant(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
					sessionUser.getRole().getId()));
			
			  //setCount(userDao.isMerchantMapped(getMechantpayId(),getResellerPayId()));
			 // logger.info("count...={}",getCount());
			 
			User user = userDao.findByPayId(MechantpayId);
			user.setResellerId(ResellerPayId);
			userDao.saveandUpdate(user);

//			List<Object[]> chargingDetails = chargingdetaildao.findDistinctMopDetails(MechantpayId);
//			logger.info("mop type that saved in commission table={}",chargingDetails.toString());
			
			List<Object[]> tdrDetails = tdrSettingDao.findDistinctMopDetails(MechantpayId);
			logger.info("mop type that saved in commission table={}",tdrDetails.toString());
			Map<String, List<Object[]>> mopByPaymentType = tdrDetails.stream()
					.collect(Collectors.groupingBy(doc -> (String) doc[1]));
			logger.info("mopByPaymentType....={}",new Gson().toJson(mopByPaymentType));

			commision.setResellerpayid(ResellerPayId);
			commision.setMerchantpayid(MechantpayId);
			commision.setBaserate("00.00");
			commision.setCommission_amount("00.00");
			commision.setCommission_percent("00.00");
			commision.setMerchant_mdr("00.00");

			String defaultEntrys = PropertiesManager.propertiesMap
					.get(Constants.RESELLER_COMMISSION_DEFAULT_ENTRY.getValue());
//			if (StringUtils.isNotBlank(defaultEntrys)) {
//				List<String> defaultEntry = Arrays.asList(defaultEntrys.split(","));
//				defaultEntry.forEach(entry -> {
//					commision.setMop(entry);
//					commision.setTransactiontype(entry);
//					commision.setCurrency(entry);
//					verfiy = commisiondao.mopamdpayment(MechantpayId, ResellerPayId, entry, entry,entry);
//					if (verfiy.size() == 0) {
//						commisiondao.saveandUpdate(commision);
//					}
//				});
//			}

			mopByPaymentType.entrySet().forEach(entry -> {
				String paymentType = PaymentType.getInstanceUsingStringValue(entry.getKey()).getCode();
				entry.getValue().forEach(mopTypeDetails -> {
					String mopType = MopType.getInstanceUsingStringValue1((String) mopTypeDetails[0]).getCode();
					String currency =(String) mopTypeDetails[2];
					verfiy = commisiondao.mopamdpayment(MechantpayId, ResellerPayId, paymentType, mopType,currency);
					if (verfiy.size() == 0) {
						commision.setMop(mopType);
						commision.setTransactiontype(paymentType);
						commision.setCurrency(currency);
						commisiondao.saveandUpdate(commision);
					}
				});
			});
			addActionMessage("Merchant Mapped Successfully");
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			addActionMessage("Error While Mapping Merchant");
			return ERROR;
		}
	}

	// To display page without using token
	@SuppressWarnings("unchecked")
	public String displayList() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setListReseller(userDao.getResellerList());
		setListMerchant(userDao.getActiveMerchant(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
				sessionUser.getRole().getId()));
		/*
		 * setCount(userDao.isMerchantMapped(getMechantpayId(),getResellerPayId()));
		 * logger.info("count...={}",getCount());
		 */
		return SUCCESS;
	}

	public List<Merchants> getListMerchant() {
		return listMerchant;
	}

	public void setListMerchant(List<Merchants> listMerchant) {
		this.listMerchant = listMerchant;
	}

	public List<Merchants> getListReseller() {
		return listReseller;
	}

	public void setListReseller(List<Merchants> listReseller) {
		this.listReseller = listReseller;
	}

	public String getResellerPayId() {
		return ResellerPayId;
	}

	public void setResellerPayId(String resellerPayId) {
		ResellerPayId = resellerPayId;
	}

	public String getMechantpayId() {
		return MechantpayId;
	}

	public void setMechantpayId(String mechantpayId) {
		MechantpayId = mechantpayId;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
