package com.pay10.crm.actionBeans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.BatchTransactionObj;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MacUtil;
import com.pay10.commons.util.TransactionType;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.jmstemplate.SendTransactionEmail;

/**
 * @author Rahul
 *
 */

@Service
public class RefundProcessor extends AbstractSecureAction{
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SendTransactionEmail sendTransactionEmail;
	
	@Autowired
	private MacUtil macUtil;
	
	private static final long serialVersionUID = 6259051505547066954L;
	private static Logger logger = LoggerFactory.getLogger(RefundProcessor.class.getName());

	private StringBuilder responseMessage = new StringBuilder();
	public String processAll(List<BatchTransactionObj> refundList, User sessionUser, String ipAddress) throws SystemException{
		for(BatchTransactionObj transactionObject:refundList){
			try{
				process(transactionObject, sessionUser, ipAddress);
			}catch (SystemException systemException) {
				if(systemException.getErrorType().getResponseCode().equals(ErrorType.REFUND_FAILED.getResponseCode())){
					throw systemException;
				}
				responseMessage.append(ErrorType.REFUND_NOT_SUCCESSFULL.getResponseMessage());
				responseMessage.append(transactionObject.getOrderId());
				responseMessage.append("\n");
			}catch(Exception exception){
				responseMessage.append(ErrorType.REFUND_NOT_SUCCESSFULL.getResponseMessage());
				responseMessage.append(transactionObject.getOrderId());
				responseMessage.append("\n");
				logger.error("Error while processing refund transaction: " + exception);
			}
		}
		if(!StringUtils.isEmpty(responseMessage.toString())){
			responseMessage.deleteCharAt(responseMessage.length()-1).toString();
		}
		return responseMessage.toString();
	}

	public void process(BatchTransactionObj batchOperationObj, User sessionUser, String ipAddress) throws Exception{

			Fields responseMap = null;
			Map<String, String> requestMap = new HashMap<String, String>();
			// format amount first
			requestMap.put(FieldType.AMOUNT.getName(), Amount.formatAmount(batchOperationObj.getAmount(), batchOperationObj.getCurrencyCode()));
			requestMap.put(FieldType.PAY_ID.getName(), batchOperationObj.getPayId());
			User user = new User();
			if(!sessionUser.getUserType().equals(UserType.MERCHANT)){
				user = userDao.findPayId(batchOperationObj.getPayId());
			}else{
				user = sessionUser;
			}
			// Check Refund Amount
			RefundChecker refundChecker = new RefundChecker();
			Boolean refundFlag = refundChecker.setAllRefundValidation(
					batchOperationObj.getCurrencyCode(), requestMap, user,
					batchOperationObj.getOrigTxnId());
			if (refundFlag == true) {
				if(sessionUser.getUserType().equals(UserType.MERCHANT)){
					throw new SystemException(ErrorType.REFUND_FAILED,ErrorType.REFUND_FAILED.getResponseMessage());
				}
				responseMessage.append(ErrorType.REFUND_FAILED.getResponseMessage());
				responseMessage.append(FieldType.ORDER_ID.getName());
				responseMessage.append(batchOperationObj.getOrderId());
				responseMessage.append("\n");
				return;
			}
			requestMap.put(FieldType.ORIG_TXN_ID.getName(),
					batchOperationObj.getOrigTxnId());
			requestMap.put(FieldType.PAY_ID.getName(),
					batchOperationObj.getPayId());
			requestMap.put(FieldType.TXNTYPE.getName(),
					TransactionType.REFUND.getName());
			requestMap.put(FieldType.CURRENCY_CODE.getName(),
					batchOperationObj.getCurrencyCode());
			requestMap.put(FieldType.CUST_EMAIL.getName(),
					batchOperationObj.getCustEmail());
			requestMap.put(FieldType.INTERNAL_USER_EMAIL.getName(), sessionUser.getEmailId());
			requestMap.put(FieldType.HASH.getName(),
							"1234567890123456789012345678901234567890123456789012345678901234");
			requestMap.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			// Preparing fields
			Fields fields = new Fields(requestMap);
			String mac = StringUtils.isBlank(ipAddress) ? "NA" : macUtil.getMackByIp(ipAddress);
			fields.put((FieldType.INTERNAL_CUST_IP.getName()), ipAddress);
			fields.put((FieldType.INTERNAL_CUST_MAC.getName()), mac);
			fields.logAllFields("All request fields :");
			//RequestRouter router = new RequestRouter(fields);
			//responseMap = new Fields(router.route());

			String responseCode = responseMap.get(FieldType.RESPONSE_CODE.getName());
			// TODO.................... send SMS
			sendTransactionEmail.sendSms(fields);

			// Sending Email for Transaction Status to merchant or customer
			if (responseCode.equals("000")) {
				
				sendTransactionEmail.sendEmail(responseMap);
				
				
				/*if (user.isRefundTransactionCustomerEmailFlag()) {
					
					
					emailBuilder.transactionRefundEmail(responseMap,
							CrmFieldConstants.CUSTOMER.toString(),
							fields.get(FieldType.CUST_EMAIL.getName()),
							user.getBusinessName());
				}
				if (user.isRefundTransactionMerchantEmailFlag()) {
					emailBuilder.transactionRefundEmail(responseMap,
							UserType.MERCHANT.toString(),
							user.getTransactionEmailId(),
							user.getBusinessName());
				}*/
			}
			if (null == responseCode || !responseCode.equals(ErrorType.SUCCESS.getCode())) {
				responseMessage.append(ErrorType.REFUND_NOT_SUCCESSFULL
						.getResponseMessage());
				responseMessage.append(batchOperationObj.getOrderId());
				responseMessage.append("\n");
				return;
			}
			// Refund Limit Update
			RefundLimitUpdater refundLimitUpdater = new RefundLimitUpdater();
			refundLimitUpdater.extraRefundLimitUpdate(
					batchOperationObj.getCurrencyCode(), requestMap, user, batchOperationObj.getOrigTxnId(),
					batchOperationObj.getPayId(), refundChecker.getTodayRefundedAmount(), refundChecker.getTodayTotalCapturedAmount());
	}
}
