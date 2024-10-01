package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.SmsSender;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.SurchargeDetailsDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;

/**
 * @author Shaiwal
 *
 */
public class GenerateVerificationOtpAction extends AbstractSecureAction {

	private static final long serialVersionUID = 6818947178144759312L;

	private static Logger logger = LoggerFactory.getLogger(GenerateVerificationOtpAction.class.getName());

	private String dataParam;
	private String otp;
	private User sessionUser = new User();
	private String response;
	private String idArray;
	
	
	
	@Autowired
	private UserDao userdao;

	@Autowired
	private SmsSender smsSender;
	
	@Autowired
	private ServiceTaxDao serviceTaxDao;
	
	@Autowired
	private SurchargeDetailsDao surchargeDetailsDao;
	
	@Autowired
	private SurchargeDao surchargeDao;
	
	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	@Override
	public String execute() {
		
		logger.info("Inside GenerateVerificationOtpAction Class, In execute method !!");

		sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		try {

			if (dataParam.equalsIgnoreCase("surchargeDetails")) {

				Random rnd = new Random();
				int otpValue = 100000 + rnd.nextInt(900000);
				
				String [] idArraySplit = idArray.split(",");
				
				List<SurchargeDetails> pendingSurchargeDetList = new ArrayList<SurchargeDetails>();
				
				for (String id :idArraySplit ) {
					SurchargeDetails SurchargeDetails  = surchargeDetailsDao.find(Long.valueOf(id));
					
					if (SurchargeDetails != null) {
						pendingSurchargeDetList.add(SurchargeDetails);
					}
				}
				
				int addMinuteTime = 15;
				Date targetTime = new Date(); //now
				targetTime = DateUtils.addMinutes(targetTime, addMinuteTime); //add minute
				
				for (SurchargeDetails pendingSurchargeDetails : pendingSurchargeDetList) {
					
					try {

						Session session = HibernateSessionProvider.getSession();
						Transaction tx = session.beginTransaction();
						Long id = pendingSurchargeDetails.getId();
						session.load(pendingSurchargeDetails, pendingSurchargeDetails.getId());
						SurchargeDetails updatedSurchargeDetails = session.get(SurchargeDetails.class, id);

						updatedSurchargeDetails.setOtp(String.valueOf(otpValue));
						updatedSurchargeDetails.setOtpExpiryTime(targetTime);
						session.update(updatedSurchargeDetails);
						tx.commit();
						session.close();
					} catch (HibernateException e) {

						logger.error("surchargeDetails  edit failed");
						setResponse("Unable to sent OTP ");
						return SUCCESS;
					}
					
				}
				
				
				String mobile = sessionUser.getMobile();

				StringBuilder smsBody = new StringBuilder();
				smsBody.append("OTP for Approving Merchant Surcharge Change Request is " + otpValue+" . OTP is Valid For 15 Minutes");

				if (StringUtils.isNotBlank(mobile)) {
					
					String mobileMask  = mobile.substring(0,4)+"XXXX"+ mobile.substring(8,10);
					smsSender.sendSMS(mobile, smsBody.toString());
					logger.info("Merchant Surcharge update OTP sent successfully to " + sessionUser.getBusinessName());
					setResponse("OTP sent to mobile number "+mobileMask);
					return SUCCESS;
				} else {
					setResponse("Unable to sent OTP ");
					return SUCCESS;
				}
			}

			if (dataParam.equalsIgnoreCase("surcharge")) {
				AccountCurrencyRegion acr = null;
				Random rnd = new Random();
				int otpValue = 100000 + rnd.nextInt(900000);
				String mobile = sessionUser.getMobile();
				
				String[] idSplit = idArray.split(";");
				List<Surcharge> surchargePendingList = new ArrayList<Surcharge>();
				for (String idSplitUnit : idSplit) {
					
					String[] idSplitArray = idSplitUnit.split(",");
					
					String paymentType = idSplitArray[0];
					String payId = idSplitArray[1];
					String mopType = idSplitArray[2];
					String acquirer = idSplitArray[3];
					String paymentsRegion = idSplitArray[4];
					
					
					if (paymentsRegion.equalsIgnoreCase("INTERNATIONAL")) {
						acr = AccountCurrencyRegion.INTERNATIONAL;
					} else {
						acr = AccountCurrencyRegion.DOMESTIC;
					}

					
					List<Surcharge> surchargeToUpdateList = surchargeDao.findPendingSurchargeListByPayIdAcquirerNameRegion(payId, paymentType,
							acquirer, mopType, acr);
					surchargePendingList.addAll(surchargeToUpdateList);
				}
				
				int addMinuteTime = 15;
				Date targetTime = new Date(); //now
				targetTime = DateUtils.addMinutes(targetTime, addMinuteTime); //add minute
				
				for (Surcharge pendingSurcharge : surchargePendingList) {
					
					try {

						Session session = HibernateSessionProvider.getSession();
						Transaction tx = session.beginTransaction();
						Long id = pendingSurcharge.getId();
						session.load(pendingSurcharge, pendingSurcharge.getId());
						Surcharge updatedSurcharge = session.get(Surcharge.class, id);

						updatedSurcharge.setOtp(String.valueOf(otpValue));
						updatedSurcharge.setOtpExpiryTime(targetTime);
						session.update(updatedSurcharge);
						tx.commit();
						session.close();
					} catch (HibernateException e) {

						logger.error("surcharge  edit failed");
						setResponse("Unable to sent OTP ");
						return SUCCESS;
					}
					
				}
				
				
				StringBuilder smsBody = new StringBuilder();
				String mobileMask  = mobile.substring(0,4)+"XXXX"+ mobile.substring(8,10);
				smsBody.append("OTP for Approving Bank Surcharge Change Request is " + otpValue+" . OTP is Valid For 15 Minutes");

				if (StringUtils.isNotBlank(mobile)) {
					smsSender.sendSMS(mobile, smsBody.toString());
					logger.info("Bank Surcharge update OTP sent successfully to " + sessionUser.getBusinessName());
					setResponse("OTP sent to mobile number "+mobileMask);
				} else {
					setResponse("Failed to send OTP");
				}
			}

			else if (dataParam.equalsIgnoreCase("smartRouter")) {

				Random rnd = new Random();
				int otpValue = 100000 + rnd.nextInt(900000);
				
				String [] idArraySplit = idArray.split(",");
				
				List<RouterConfiguration> pendingRouterConfigurationList = new ArrayList<RouterConfiguration>();
				
				for (String id :idArraySplit ) {
					List<RouterConfiguration> pendingRouterConfigList = new ArrayList<RouterConfiguration>();
					
					pendingRouterConfigList = routerConfigurationDao.findPendingRulesByIdentifier(id);
					if (pendingRouterConfigList.size() > 0) {
						pendingRouterConfigurationList.addAll(pendingRouterConfigList);
					}
				}
				
				int addMinuteTime = 15;
				Date targetTime = new Date(); //now
				targetTime = DateUtils.addMinutes(targetTime, addMinuteTime); //add minute
				
				for (RouterConfiguration pendingRouterConfiguration : pendingRouterConfigurationList) {
					
					try {

						Session session = HibernateSessionProvider.getSession();
						Transaction tx = session.beginTransaction();
						Long id = pendingRouterConfiguration.getId();
						session.load(pendingRouterConfiguration, pendingRouterConfiguration.getId());
						RouterConfiguration updatedRouterConfiguration = session.get(RouterConfiguration.class, id);

						updatedRouterConfiguration.setOtp(String.valueOf(otpValue));
						updatedRouterConfiguration.setOtpExpiryTime(targetTime);
						session.update(updatedRouterConfiguration);
						tx.commit();
						session.close();
					} catch (HibernateException e) {

						logger.error("Router Config  edit failed");
						setResponse("Unable to sent OTP ");
						return SUCCESS;
					}
					
				}
				
				
				String mobile = sessionUser.getMobile();

				StringBuilder smsBody = new StringBuilder();
				smsBody.append("OTP for Approving Smart Router Change Request is " + otpValue +" . OTP is Valid For 15 Minutes");

				if (StringUtils.isNotBlank(mobile)) {
					
					String mobileMask  = mobile.substring(0,4)+"XXXX"+ mobile.substring(8,10);
					smsSender.sendSMS(mobile, smsBody.toString());
					logger.info("GST update OTP sent successfully to " + sessionUser.getBusinessName());
					setResponse("OTP sent to mobile number "+mobileMask);
					return SUCCESS;
				} else {
					setResponse("Unable to sent OTP ");
					return SUCCESS;
				}
			}

			else if (dataParam.equalsIgnoreCase("gst")) {

				Random rnd = new Random();
				int otpValue = 100000 + rnd.nextInt(900000);
				
				String [] idArraySplit = idArray.split(",");
				
				List<ServiceTax> pendingServiceTaxList = new ArrayList<ServiceTax>();
				
				for (String id :idArraySplit ) {
					ServiceTax serviceTax  = serviceTaxDao.find(Long.valueOf(id));
					
					if (serviceTax != null) {
						pendingServiceTaxList.add(serviceTax);
					}
				}
				
				int addMinuteTime = 15;
				Date targetTime = new Date(); //now
				targetTime = DateUtils.addMinutes(targetTime, addMinuteTime); //add minute
				
				for (ServiceTax pendingServiceTax : pendingServiceTaxList) {
					
					try {

						Session session = HibernateSessionProvider.getSession();
						Transaction tx = session.beginTransaction();
						Long id = pendingServiceTax.getId();
						session.load(pendingServiceTax, pendingServiceTax.getId());
						ServiceTax updatedServiceTaxDetails = session.get(ServiceTax.class, id);

						updatedServiceTaxDetails.setOtp(String.valueOf(otpValue));
						updatedServiceTaxDetails.setOtpExpiryTime(targetTime);
						session.update(updatedServiceTaxDetails);
						tx.commit();
						session.close();
					} catch (HibernateException e) {

						logger.error("ServiceTax  edit failed");
						setResponse("Unable to sent OTP ");
						return SUCCESS;
					}
					
				}
				
				
				String mobile = sessionUser.getMobile();

				StringBuilder smsBody = new StringBuilder();
				smsBody.append("OTP for Approving GST Change Request is " + otpValue+" . OTP is Valid For 15 Minutes");

				if (StringUtils.isNotBlank(mobile)) {
					
					String mobileMask  = mobile.substring(0,4)+"XXXX"+ mobile.substring(8,10);
					smsSender.sendSMS(mobile, smsBody.toString());
					logger.info("GST update OTP sent successfully to " + sessionUser.getBusinessName());
					setResponse("OTP sent to mobile number "+mobileMask);
					return SUCCESS;
				} else {
					setResponse("Unable to sent OTP ");
					return SUCCESS;
				}
			}
			
			else {
				setResponse("Failed to sent OTP");
			}
		}

		catch (Exception exception) {
			setResponse("Failed");
			logger.error("Inside GenerateVerificationOtpAction Class, in execute method GST  : ", exception);
		}
		return SUCCESS;
	}

	public String getDataParam() {
		return dataParam;
	}

	public void setDataParam(String dataParam) {
		this.dataParam = dataParam;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getIdArray() {
		return idArray;
	}

	public void setIdArray(String idArray) {
		this.idArray = idArray;
	}

}
