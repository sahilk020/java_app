package com.pay10.nodal.payout;

import java.util.Map;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.nodal.payout.yesBankNodalFT3.YesBankFT3NodalApi;
import com.pay10.pg.core.util.Processor;

/**
 * @author Rahul
 *
 */
@Service
public class NodalRequestHandler {

	private static Logger logger = LoggerFactory.getLogger(YesBankFT3NodalApi.class.getName());

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private YesBankCBNodalApi yesBankNodalApi;

	@Autowired
	private KotakBankNodalApi kotakBankNodalApi;
	
	@Autowired
	private YesBankFT3NodalApi yesBankFT3NodalApi;
	
	@Autowired
	private FieldsDao fieldsDao;

	public Map<String, String> settlementProcess(Fields fields) throws SystemException, SOAPException {

		//generalValidator.validate(fields);
		logger.info("Nodal Settlement Request : " + fields.getFieldsAsString());
		String acquire = fields.get(FieldType.NODAL_ACQUIRER.getName());
		if (acquire.equalsIgnoreCase(AcquirerType.YESBANKCB.getCode())) {
			yesBankNodalApi.process(fields);
		} 
		else if(acquire.equalsIgnoreCase(AcquirerType.KOTAK.getCode())) {
			kotakBankNodalApi.process(fields);
		}
		else if(acquire.equalsIgnoreCase(AcquirerType.YESBANKFT3.getCode())) {
			yesBankFT3NodalApi.settlementProcess(fields);
		}
		else {
			logger.error("Invalid acquirer : " + acquire);
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.ACQUIRER_NOT_FOUND.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.ACQUIRER_NOT_FOUND.getResponseMessage());
			logger.info("Nodal Settlement Reponse : " + fields.getFieldsAsString());
			return fields.getFields();
		}

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(SettlementTransactionType.FUND_TRANSFER.getName())) {
			fieldsDao.insertSettlementTransaction(fields);
		}
		else if ( txnType.equals(SettlementTransactionType.STATUS.getName())) {
			fieldsDao.updateSettlementTransaction(fields);
		}
		logger.info("Nodal Settlement Reponse : " + fields.getFieldsAsString());
		return fields.getFields();
	}


}
