package com.pay10.crm.action;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import com.pay10.commons.user.User;

import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.crm.actionBeans.GlobalTransactionFieldType;


public class GlobalTransactionSearchAdmin extends AbstractSecureAction {
	

	

	private static Logger logger = LoggerFactory.getLogger(GlobalTransactionSearchAdmin.class.getName());
	private static final long serialVersionUID = -5990800125330748024L;
	

	private User sessionUser = null;
	List<GlobalTransactionFieldType>fieldType=new ArrayList<>();
	
	
	
	
	public List<GlobalTransactionFieldType> getFieldType() {
		return fieldType;
	}




	public void setFieldType(List<GlobalTransactionFieldType> fieldType) {
		this.fieldType = fieldType;
	}




	@Override
	public String execute() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if(sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN) || sessionUser.getUserType().equals(UserType.SUPERADMIN) || sessionUser.getUserType().equals(UserType.SUBSUPERADMIN)) {
			
			List<GlobalTransactionFieldType>globalTransactionFieldTypes=new ArrayList<>();
				
			globalTransactionFieldTypes.add(new GlobalTransactionFieldType("Txn Id",FieldType.TXN_ID.getName()));
			globalTransactionFieldTypes.add(new GlobalTransactionFieldType("Pg Ref Num",FieldType.PG_REF_NUM.getName()));
			globalTransactionFieldTypes.add(new GlobalTransactionFieldType("Order Id",FieldType.ORDER_ID.getName()));
			globalTransactionFieldTypes.add(new GlobalTransactionFieldType("Refund Order Id",FieldType.REFUND_ORDER_ID.getName()));
			globalTransactionFieldTypes.add(new GlobalTransactionFieldType("Customer Email",FieldType.CUST_EMAIL.getName()));
			globalTransactionFieldTypes.add(new GlobalTransactionFieldType("Customer Ph Number",FieldType.CUST_PHONE.getName()));
			globalTransactionFieldTypes.add(new GlobalTransactionFieldType("Acquirer Type",FieldType.ACQUIRER_TYPE.getName()));
			globalTransactionFieldTypes.add(new GlobalTransactionFieldType("Ip address",FieldType.INTERNAL_CUST_IP.getName()));
			globalTransactionFieldTypes.add(new GlobalTransactionFieldType("cards Mask",FieldType.CARD_MASK.getName()));
			globalTransactionFieldTypes.add(new GlobalTransactionFieldType("RRN Number",FieldType.RRN.getName()));
			
			setFieldType(globalTransactionFieldTypes);
			}
		
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

		return INPUT;
	}

	

	

}
