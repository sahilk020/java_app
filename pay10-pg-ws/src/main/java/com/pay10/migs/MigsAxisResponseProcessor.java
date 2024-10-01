package com.pay10.migs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.axis.AxisMigsIntegrator;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.security.SecurityProcessor;

@Service("resProcessor")
public class MigsAxisResponseProcessor implements Processor {

	private Transaction responseObject;

	@Autowired
	private ResponseFactory responseFactory;
	@Autowired
	private ResponseCreator responseCreator;
	/*
	 * @Autowired private Validator generalValidator;
	 */
	@Autowired
	private SecurityProcessor securityProcessor;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	@Qualifier("migsTransformer")
	private MigsTransformer migsTransformer;

	@Autowired
	private AxisMigsIntegrator axisMigsIntegrator;

	@Autowired
	private HashComparator hashComparator;

	/*@Autowired
	private GeneralValidator generalValidator;*/


	private static final String zero = "0";

	@Override
	public void preProcess(Fields fields) throws SystemException {
		// TODO....validation work will be done after discussion
		// generalValidator.validate(fields);

		try {
			// Hash Calculator

			hashComparator.compareHash(fields);
			responseObject = responseFactory.createResponse(fields);

			securityProcessor.authenticate(fields);
			securityProcessor.addAcquirerFields(fields);
			fields.put(FieldType.INTERNAL_INVALID_HASH_YN.getName(), "N");

		} catch (SystemException systemException) {
			fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
			fields.put(FieldType.TXNTYPE.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
			String newTxnId = TransactionManager.getNewTransactionId();
			fields.put(FieldType.TXN_ID.getName(), newTxnId);
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			ProcessManager.flow(updateProcessor, fields, true);
			fields.setValid(false);
		}
	}

	@Override
	public void process(Fields fields) throws SystemException {
		String responseCodeFromAmex = responseObject.getResponseCode();
		if (!responseCodeFromAmex.equals(zero)) {
			
			updateFailedResponse(fields);
			ProcessManager.flow(updateProcessor, fields, true);
			return;
		}
		// If 3ds enrolled status is Y and status is A or Y then success
		if ((responseObject.getThreeDSenrolled().equals(Constants.Y)
				&& responseObject.getThreeDSstatus().equals(Constants.Y))
				|| (responseObject.getThreeDSenrolled().equals(Constants.Y)
						&& responseObject.getThreeDSstatus().equals(Constants.A))) {

			String transactionType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());

			// auth mode and TID is for auth-capture mode
			if (!transactionType.equals(TransactionType.SALE.getName())) {
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName());
				// send request for auth
				axisMigsIntegrator.process(fields);
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName());
				// fields insert into DB
				ProcessManager.flow(updateProcessor, fields, true);
			} // sale mode with TID mapped in purchase mode at acquirer end
			else if (transactionType.equals(TransactionType.SALE.getName())) {
				fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
				fields.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());

				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
				axisMigsIntegrator.process(fields);
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());

				ProcessManager.flow(updateProcessor, fields, true);

			}
		} else {
			updateFailedResponse(fields);
		}
	}

	private void updateFailedResponse(Fields fields) throws SystemException {
		// return to user after UPDATING transaction: NEW ORDER for error
		//migsTransformer.updateResponse(responseObject, fields);
		fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), responseObject.getMessage());
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		fields.put(FieldType.PG_REF_NUM.getName(), newTxnId);
		fields.put(FieldType.TXNTYPE.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
		// clean fields
		fields.removeVpcFields();
		fields.remove(FieldType.OID.getName());
		fields.remove(FieldType.VPC_LOCALE.getName());
		fields.remove(FieldType.H_CARD_NUMBER.getName());
		fields.remove(FieldType.MERCHANT_ID.getName());
		fields.remove(FieldType.TXN_KEY.getName());
		fields.remove(FieldType.ACQUIRER_TYPE.getName());
		fields.remove(FieldType.MIGS_FINAL_REQUEST.getName());

	}

}
