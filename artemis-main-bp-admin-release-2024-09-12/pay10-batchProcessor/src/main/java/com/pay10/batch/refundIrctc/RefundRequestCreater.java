package com.pay10.batch.refundIrctc;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Amount;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.TransactionType;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundRequestCreater {	
	
	/*@Autowired
	@Qualifier("systemProperties")
	private SystemProperties systemProperties;*/

	@Autowired
	private Amount amount;

	private static final Logger logger = LoggerFactory.getLogger(RefundRequestCreater.class);

	// Set required fields for RefundIrctc Transaction
	public JSONObject createNewRefundIrctcObject(Fields fields) throws JSONException, SystemException {
		JSONObject value = new JSONObject();
		value.put(FieldType.RECO_ORDER_ID.getName(), fields.get(FieldType.RECO_ORDER_ID.getName()));
		value.put(FieldType.REFUND_FLAG.getName(), fields.get(FieldType.REFUND_FLAG.getName()));
		value.put(FieldType.RECO_AMOUNT.getName(), (amount.formatAmount(fields.get(FieldType.RECO_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()))));
		//value.put(FieldType.RECO_TOTAL_AMOUNT.getName(), fields.get(FieldType.RECO_REFUNDAMOUNT.getName()));
		value.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		value.put(FieldType.REFUND_ORDER_ID.getName(), fields.get(FieldType.REFUND_ORDER_ID.getName()));
		value.put(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()));
		value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName());
		value.put(FieldType.RECO_PAY_ID.getName(), fields.get(FieldType.RECO_PAY_ID.getName()));

		value.put(FieldType.ACQUIRER_TDR_SC.getName(), fields.get(FieldType.ACQUIRER_TDR_SC.getName()));
		value.put(FieldType.ACQUIRER_GST.getName(), fields.get(FieldType.ACQUIRER_GST.getName()));
		value.put(FieldType.PG_TDR_SC.getName(), fields.get(FieldType.PG_TDR_SC.getName()));
		value.put(FieldType.PG_GST.getName(), fields.get(FieldType.PG_GST.getName()));
		//value.put(FieldType.REQUEST_DATE.getName(), DateCreater.dateformatCreater(fields.get(FieldType.REFUND_DATE_TIME.getName())));
		value.put(FieldType.HASH.getName(), Hasher.getHash(fields.get(FieldType.TXN_ID.getName())));
		return value;
	}
}
