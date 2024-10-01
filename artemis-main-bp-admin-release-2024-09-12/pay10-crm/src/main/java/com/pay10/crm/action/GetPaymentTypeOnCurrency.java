package com.pay10.crm.action;

import java.util.*;

import com.google.gson.Gson;
import com.pay10.commons.user.AcquirerMasterDB;
import com.pay10.commons.user.AcquirerMasterDBDao;
import com.pay10.commons.user.Mop;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.FraudRuleType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GetPaymentTypeOnCurrency extends AbstractSecureAction{

	/**
	 * Sweety
	 */
	private static final long serialVersionUID = -1803376825321091732L;
	private static Logger logger = LoggerFactory.getLogger(GetPaymentTypeOnCurrency.class.getName());
	
	private String payid;
	private String currency;
	private String paymentType;
	private List<String> paymentTypeData = new ArrayList<>();
	 private HashMap<String,List<Object>> mopList=new HashMap<>();
	@Autowired
	private UserDao userDao;
	@Autowired
	private AcquirerMasterDBDao acquirerMasterDBDao;

	@Override
	public String execute() {

		logger.info("get PayId : ={},currency:={}" , payid,currency);
		Map<String, Set<String>> mapData = new HashMap<>();
		List<String> objectDao = userDao.findPayTypeByPayIdAndCurrencyType(payid,currency);
		//logger.info("function call {}",objectDao);
		setPaymentTypeData(objectDao);
		 return SUCCESS;
	}
	public String getMopTypeDetailsList(){

//		String newPaymentType = PaymentType.getPaymentCodeFromInstance(paymentType);
		List<Object[]>mopDetailsObject = userDao.findMopDetailsByPayIdCurrencyAndPayType(payid,currency,paymentType);
		logger.info("MOP Details{} ",mopDetailsObject);
		HashMap<String, List<Object>>mopDetailsMap=new HashMap<>();
        for (Object[] obj : mopDetailsObject) {
            logger.info("object {}", obj);
            String mopKey = String.valueOf(obj[0]);
            String mopTypeKey = MopType.getNameFromInstance(mopKey);
            String acqName = String.valueOf(obj[1]);
            List<AcquirerMasterDB> acqCode = acquirerMasterDBDao.getAcquirerCodeByName(acqName);
            obj[1] = acqCode.get(0).getName();
            logger.info("Key " + mopKey);
            if (!mopDetailsMap.containsKey(mopTypeKey)) {
                List<Object> listData = new ArrayList<>();
                mopDetailsMap.put(mopTypeKey, listData);
            }
            mopDetailsMap.get(mopTypeKey).add(obj);
        }

		logger.info("Pay Type{}: ",mopDetailsMap);
 		 setMopList(mopDetailsMap);
		 return SUCCESS;
	}

	public String getPayid() {
		return payid;
	}

	public void setPayid(String payid) {
		this.payid = payid;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public List<String> getPaymentTypeData() {
		return paymentTypeData;
	}

	public void setPaymentTypeData(List<String> paymentTypeData) {
		this.paymentTypeData = paymentTypeData;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public HashMap<String, List<Object>> getMopList() {
		return mopList;
	}

	public void setMopList(HashMap<String, List<Object>> mopList) {
		this.mopList = mopList;
	}
}
