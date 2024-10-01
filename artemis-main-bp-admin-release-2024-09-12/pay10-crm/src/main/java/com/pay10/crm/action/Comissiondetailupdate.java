package com.pay10.crm.action;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Resellercommision;
import com.pay10.commons.user.resellercommisiondao;


public class Comissiondetailupdate extends AbstractSecureAction  {

	

	private static Logger logger = LoggerFactory.getLogger(Comissiondetailupdate.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;
	
	@Autowired
	resellercommisiondao  resellerdao;
		
	//private Resellercommision resellercommision = new Resellercommision();
	private String emailId;
	private String merchant_mdr;
	private String baserate;
	private boolean commissiontype;
	private String commission_percent;
	private String commission_amount;
	private String response;
	private String added_by;

	private long id;
	 //List<Resellercommision> resellercommisiondata = new ArrayList<Resellercommision>();


	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {

						logger.info("emailId........"+emailId);
						logger.info("merchant_mdr......"+merchant_mdr);
						logger.info("baserate........"+baserate);
						logger.info("commissiontype......"+commissiontype);
						logger.info("commission_percent....."+commission_percent);
						logger.info("commission_amount......"+commission_amount);
						logger.info("id....."+id);

						Resellercommision resellercommisio	=resellerdao.findByPayId(id);
						logger.info("resellercommision......"+resellercommisio.toString());
						resellercommisio.setResellerpayid(emailId);
						resellercommisio.setMerchant_mdr(merchant_mdr);
						resellercommisio.setBaserate(baserate);
						resellercommisio.setCommissiontype(commissiontype);
						resellercommisio.setCommission_amount(commission_amount);
						resellercommisio.setCommission_percent(commission_percent);
						resellercommisio.setAdded_by(added_by);
						resellercommisio.setId(id);
						
						resellerdao.saveandUpdate1(resellercommisio);

						
						
						
			/*
			 * if(chargingDetails.getMerchantTDR() == 0.0) {
			 * chargingDetails.setResponse("proper values not entered"); return SUCCESS; }
			 */
			
//			ChargingDetails prevChargingDetailsFromDb = chargingDetailsDao.find(chargingDetails.getId());
//			String prevValue = prevChargingDetailsFromDb != null ? mapper.writeValueAsString(prevChargingDetailsFromDb)
//					: null;
//			logger.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa"+chargingDetails.toString());
//			
//			editChargingDetails.editChargingDetail(emailId, acquirer, chargingDetails, userType, loginUserEmailId,
//					isDomestic, isConsumer);
//
//			if (prevChargingDetailsFromDb == null) {
//				prevChargingDetailsFromDb = chargingDetailsDao.find(chargingDetails.getId());
//			}
//			ChargingDetails latestActivechargingDetailFromDb = chargingDetailsDao.findActiveChargingDetail(
//					prevChargingDetailsFromDb.getMopType(), prevChargingDetailsFromDb.getPaymentType(),
//					prevChargingDetailsFromDb.getTransactionType(), prevChargingDetailsFromDb.getAcquirerName(),
//					prevChargingDetailsFromDb.getCurrency(), prevChargingDetailsFromDb.getPayId());
//			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
//					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
//			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(latestActivechargingDetailFromDb),
//					prevValue, actionMessagesByAction.get("editChargingDetail"));
//			auditTrailService.saveAudit(request, auditTrail);
//
//			if (null != latestActivechargingDetailFromDb) {
//				Long id = latestActivechargingDetailFromDb.getId();
//				chargingDetails.setId(id);
//			}

		} catch (Exception exception) {
			setResponse(ErrorType.CHARGINGDETAIL_NOT_SAVED.getResponseMessage());
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	
public String getEmailId() {
		return emailId;
	}


	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMerchant_mdr() {
		return merchant_mdr;
	}


	public void setMerchant_mdr(String merchant_mdr) {
		this.merchant_mdr = merchant_mdr;
	}

	public String getBaserate() {
		return baserate;
	}


	public void setBaserate(String baserate) {
		this.baserate = baserate;
	}

	public boolean isCommissiontype() {
		return commissiontype;
	}


	public void setCommissiontype(boolean commissiontype) {
		this.commissiontype = commissiontype;
	}


	public String getCommission_percent() {
		return commission_percent;
	}

public void setCommission_percent(String commission_percent) {
		this.commission_percent = commission_percent;
	}

	public String getCommission_amount() {
		return commission_amount;
	}

	public void setCommission_amount(String commission_amount) {
		this.commission_amount = commission_amount;
	}


	


	public String getAdded_by() {
		return added_by;
	}


	public void setAdded_by(String added_by) {
		this.added_by = added_by;
	}


	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}


}
