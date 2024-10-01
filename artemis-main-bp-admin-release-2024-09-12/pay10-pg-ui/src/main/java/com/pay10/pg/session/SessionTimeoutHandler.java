package com.pay10.pg.session;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.pg.core.util.Processor;


@Service
public class SessionTimeoutHandler {

	private static final String transactionTypes = "NEWORDER-SALE-AUTHORISE-ENROLL";
	private static Logger logger = LoggerFactory.getLogger(SessionTimeoutHandler.class
			.getName());
	
	@Autowired
	@Qualifier("timeoutProcessor")
	private Processor timeoutProcessor;
	
	public void handleTimeOut(Fields fields) {
		
		String status = fields.get(FieldType.STATUS.getName());
		if (status == null){
			status = StatusType.TIMEOUT.getName();
		}
		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		//Do not update transactions which are redirected to Acquirer
		if(StringUtils.isNotBlank(acquirer)) {
			return;
		}
		if (transactionTypes.contains(fields.get(FieldType.TXNTYPE.getName()))
				&& status.equals(StatusType.PENDING.getName())
				|| status.equals(StatusType.SENT_TO_BANK.getName())
				|| status.equals(StatusType.TIMEOUT.getName())
				|| status.equals(StatusType.ENROLLED.getName())) {
			
			fields.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
			// call timeout processor
			try {
				timeoutProcessor.preProcess(fields);
				timeoutProcessor.process(fields);
				timeoutProcessor.postProcess(fields);
				/*if(TaskSchedulerListener.postBackFlag){
					postBackCreator.sendPostBack(fields);
				}else{
					System.out.println("postback not run");
				}*/
			} catch (SystemException systemException) {
				logger.error("Error handling timeout and updating transaction" + systemException);
			} catch (Exception exception) {
				logger.error("Unmapped exception handling timeout" + exception);
			}
		} else {			
				//Do nothing
		}
	}
}
