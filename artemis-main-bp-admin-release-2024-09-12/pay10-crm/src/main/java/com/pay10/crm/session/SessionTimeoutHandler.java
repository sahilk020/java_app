package com.pay10.crm.session;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.util.PostBackCreator;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.TimeOutProcessor;
@Service
public class SessionTimeoutHandler {

	private static final String transactionTypes = "NEWORDER-SALE-AUTHORISE-ENROLL";
	private static Logger logger = LoggerFactory.getLogger(SessionTimeoutHandler.class
			.getName());
	@Autowired
	private PostBackCreator postBackCreator;
	public void handleTimeOut(Fields fields) {
		
		String status = fields.get(FieldType.STATUS.getName());
		if (transactionTypes.contains(fields.get(FieldType.TXNTYPE.getName()))
				&& status.equals(StatusType.PENDING.getName())
				|| status.equals(StatusType.SENT_TO_BANK.getName())
				|| status.equals(StatusType.ENROLLED.getName())) {
			// call timeout processor
			try {
				Processor processor = new TimeOutProcessor();
				processor.preProcess(fields);
				processor.process(fields);
				processor.postProcess(fields);
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
				// send post back
			/*if(TaskSchedulerListener.postBackFlag){
				postBackCreator.sendPostBack(fields);
			}else{
				System.out.println("postback not run");
			}*/
		}
	}
}
