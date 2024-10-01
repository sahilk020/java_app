package com.pay10.pg.action;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MacUtil;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.util.ResponseCreator;

public class VpaVerifyAction extends AbstractSecureAction {

	private static final long serialVersionUID = -862716535985091306L;
	private static Logger logger = LoggerFactory.getLogger(VpaVerifyAction.class.getName());

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	private Map<String, String> responseFields;


	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private MacUtil macUtil;

	private String vpaPhone;
	private String mopType;
	private String paymentType;
	private String payid;
	private String status;
	private String Vpaname;


	@Override
	public String execute() {

		Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
		if (null != fields) {
		} else {
			logger.info("session fields lost");
			return ERROR;
		}
		try {
			logger.info(mopType+vpaPhone+paymentType+payid);
			Fields fields1 = (Fields) sessionMap.get(Constants.FIELDS.getValue());
			fields.put(FieldType.PAY_ID.getName(),payid);
			fields.put(FieldType.CURRENCY_CODE.getName(),"356");
			fields.put(FieldType.ORDER_ID.getName(), fields1.get(FieldType.ORDER_ID.getName()));
			fields.put(FieldType.PAYMENT_TYPE.getName(),"UP");
			fields.put(FieldType.MOP_TYPE.getName(),"UP");
			fields.put(FieldType.TXNTYPE.getName(),"SALE");
			fields.put(FieldType.TXNTYPE.getName(),"SALE");
			fields.put(FieldType.PAYER_ADDRESS.getName(),getVpaPhone());
			String vpaFlag = PropertiesManager.propertiesMap.get("VPA_VERIFY_FLAG");
			if(StringUtils.isNotBlank(vpaFlag)&&vpaFlag.equals("N")) {
				setStatus("Success");
				setVpaname("Your VPA Verify SuccessFul ");

				return SUCCESS;

			}


			responseFields = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_INTERNAL_VPA_VALIDATE.getValue());

			setStatus(responseFields.get(FieldType.STATUS.getName()));
			if(StringUtils.isNotBlank(responseFields.get(FieldType.STATUS.getName()))) {
				setVpaname(responseFields.get(FieldType.VPA_CUST_NAME.getName()));
			}

		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return SUCCESS;
	}


	public static Logger getLogger() {
		return logger;
	}


	public static void setLogger(Logger logger) {
		VpaVerifyAction.logger = logger;
	}




	public Map<String, String> getResponseFields() {
		return responseFields;
	}


	public void setResponseFields(Map<String, String> responseFields) {
		this.responseFields = responseFields;
	}



	public String getVpaPhone() {
		return vpaPhone;
	}



	public String getVpaname() {
		return Vpaname;
	}


	public void setVpaname(String vpaname) {
		Vpaname = vpaname;
	}


	public void setVpaPhone(String vpaPhone) {
		this.vpaPhone = vpaPhone;
	}


	public String getMopType() {
		return mopType;
	}


	public void setMopType(String mopType) {
		this.mopType = mopType;
	}


	public String getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public String getPayid() {
		return payid;
	}


	public void setPayid(String payid) {
		this.payid = payid;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}



}