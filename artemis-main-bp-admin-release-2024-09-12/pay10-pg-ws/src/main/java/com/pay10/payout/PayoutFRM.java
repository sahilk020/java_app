package com.pay10.payout;

import java.security.Key;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.PayoutTdrSettingDao;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.charging.PayoutChargingDetailHelper;
import com.pay10.pg.core.util.Processor;

@Service("payoutFRM")
public class PayoutFRM implements Processor {
	private static Logger logger = LoggerFactory.getLogger(PayoutFRM.class.getName());
	static Key secretKey = null;

	@Autowired
	private PayoutTdrSettingDao chargingDetailHelper;

	public void preProcess(Fields fields) throws SystemException {

	}

	@Override
	public void process(Fields fields) throws SystemException {
		
		logger.info("Enter PayoutFRM processor : "+fields.getFieldsAsString());
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		logger.info("Pay10 pgRefNo++process+++++++++++++++++++++++++++++++++++++++++++" + pgRefNo);
		if ((StringUtils.isNoneBlank(fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())) && fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals("PO_STATUS"))) {
            logger.info("process:: return without initializing integrator.");
            return;
        }
		
		chargingDetailHelper.frm(fields);
		
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {

	}

}