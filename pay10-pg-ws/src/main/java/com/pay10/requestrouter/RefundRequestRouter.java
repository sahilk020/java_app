package com.pay10.requestrouter;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.user.RefundConfiguration;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class RefundRequestRouter {
	private static Logger logger = LoggerFactory.getLogger(RefundRequestRouter.class.getName());
	
	@Autowired
	@Qualifier("securityProcessor")
	private Processor securityProcessor;

	@Autowired
	@Qualifier("historyProcessor")
	private Processor historyProcessor;
	
	@Autowired
	@Qualifier("fraudProcessor")
	private Processor fraudProcessor;
	
	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;
	
	
	@Autowired
	UserDao userDao;
	
	Fields fields = null;

	public RefundRequestRouter(Fields fields) {
		this.fields = fields;
	}
	
	public Map<String, String> route(Fields fields) {

		logger.info("Request For Refund Router Fields = {} ",fields.getFieldsAsString());
		// Process security
		ProcessManager.flow(securityProcessor, fields, false);

		logger.info("After Security-Processor Refund Router Fields = {} ",fields.getFieldsAsString());
		// fraud Prevention processor
		ProcessManager.flow(fraudProcessor, fields, false);
		
		logger.info("After Fraud-Processor Refund Router Fields = {} ",fields.getFieldsAsString());
		// History Processor
		ProcessManager.flow(historyProcessor, fields, false);
		
		logger.info("After History-Processor Refund Router Fields = {} ",fields.getFieldsAsString());
		
		if(null !=fields.get(FieldType.STATUS.getName()) &&  fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Pending")) {
			fields.put(FieldType.STATUS.getName(),"REFUND_INITIATED");
			fields.put(FieldType.RESPONSE_CODE.getName(),"000");
		}
		logger.info("************************************************************");
		RefundConfiguration refundInfo = userDao.getRefundMode(fields.get(FieldType.PAY_ID.getName()), fields.get(FieldType.ACQUIRER_TYPE.getName()), 
				fields.get(FieldType.PAYMENT_TYPE.getName()), fields.get(FieldType.MOP_TYPE.getName()));
		logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		logger.info("refundInfo = {}",refundInfo);
		if(null != refundInfo) {
			fields.put("REFUND_MODE", refundInfo.getRefundMode());
			fields.put("REFUND_PROCESS", "P");
		}else {
			fields.put("REFUND_MODE", "OFFLINE");
			fields.put("REFUND_PROCESS", "P");
		}
		
		logger.info("Before Update-Processor Refund Router Fields = {} ",fields.getFieldsAsString());
		
		// MongoDB Update Processor
		ProcessManager.flow(updateProcessor, fields, true);
		
		logger.info("After Update-Processor Refund Router Fields = {} ",fields.getFieldsAsString());

		return fields.getFields();
	}
}
