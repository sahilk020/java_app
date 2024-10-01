package com.pay10.batch;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.FieldsDao;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.commons.util.StepExecutor;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.ErrorType;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.StatusType;

/** Performing reco file data validations. */
public abstract class FileSettleValidator {

	private static final Logger logger = LoggerFactory.getLogger(FileSettleValidator.class);

	@Autowired
	private Amount amount;

	@Autowired
	private FieldsDao fieldsDao;

	/* Check if data in file is valid against the data in DB */
	protected abstract boolean isDataValid(Fields fields) throws DatabaseException;

	
	/*
	 * Validating Status if matcheded or not code and then validate
	 */

	protected boolean isStatusMatched(Fields fields, String status) throws DatabaseException {
		Fields previous = fields.getPrevious();
		
		logger.info("current status :: " + status);
		logger.info("previous status :: " + previous.get(FieldType.STATUS.getName()));
		if (previous != null) {
			if (status != null
					&& previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName())
					&& status.equalsIgnoreCase(previous.get(FieldType.STATUS.getName()))) {
				return true;
			}
		}
		
		
		
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.MISMATCH_STATUS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.MISMATCH_STATUS.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(), Constants.BANK.getValue());
		return false;
	}
	/*
	 * Validating Amount Converting the amount in decimal value for the currency
	 * code and then validate
	 */
	protected boolean isAmountValid(Fields fields, String currAmount) throws DatabaseException {
		Fields previous = fields.getPrevious();
		String currency = previous.get(FieldType.CURRENCY_CODE.getName());
		String previousAmount = "0.00";
		if (previous != null) {
			/*
			 * if((previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.
			 * REFUND.getName())) &&
			 * (previous.get(FieldType.STATUS.getName()).equals(StatusType.PENDING.getName()
			 * ))) { List<Fields> lstFields = new ArrayList<Fields>(); lstFields =
			 * fieldsDao.getPreviousSaleCapturedForOid(previous.get(FieldType.OID.getName())
			 * ); if(lstFields.size()>0) { previousAmount =
			 * amount.formatAmount(lstFields.get(0).get(FieldType.RECO_TOTAL_AMOUNT.getName(
			 * )), currency); } else { previousAmount =
			 * amount.formatAmount(previous.get(FieldType.RECO_TOTAL_AMOUNT.getName()),
			 * currency); }
			 * 
			 * } else {
			 */
			previousAmount = amount.formatAmount(previous.get(FieldType.RECO_TOTAL_AMOUNT.getName()), currency);
			// }
			currAmount = amount.formatAmount(currAmount, currency);
			if (previousAmount != null && previousAmount.equals(currAmount)) {
				return true;
			}
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RECO_AMOUNT.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RECO_AMOUNT.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(), Constants.BANK.getValue());
		return false;
	}

	/* Validating Bank Settlement Id */
	protected boolean isBankSettlementIdValid(Fields fields, String currAcqId) {
		Fields previous = fields.getPrevious();

		if (previous != null && previous.get(FieldType.RECO_ACQ_ID.getName()) != null
				&& previous.get(FieldType.RECO_ACQ_ID.getName()).equalsIgnoreCase(currAcqId)) {
			return true;
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RECO_ACQ_ID.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RECO_ACQ_ID.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(), Constants.BANK.getValue());
		return false;
	}

	/* Validating Pg_Ref_Num */
	protected boolean isTransactionValid(Fields fields) {
		Fields previous = fields.getPrevious();
		if (previous != null
				&& fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName()))) {
			return true;
		}
		/*
		 * if(Boolean.parseBoolean(fields.get(FieldType.IS_SALE_CAPTURED.getName())) ==
		 * true) { return true; }
		 */
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RECO_TXN_ID.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RECO_TXN_ID.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(), Constants.BANK.getValue());
		return false;
	}

	/* Validating Acquirer Type */
	protected boolean isAcquirerTypeValid(Fields fields) {
		Fields previous = fields.getPrevious();

		String mprAcquirerType = "";
		String fileName = StepExecutor.getFileName();
		String name = fileName != null
				? fileName.substring(fileName.lastIndexOf(Constants.FORWARD_SLASH_SEPERATOR.getValue()) + 1)
				: null;
		if (name.contains(Constants.IDBI_BANK_CDC.getValue())) {
			mprAcquirerType = AcquirerType.IDBIBANK.getCode();
		} else if (name.contains(Constants.BOB_BANK_CDC.getValue())) {
			mprAcquirerType = AcquirerType.BOB.getCode();
		} else if (name.contains(Constants.KOTAK_BANK_CDC.getValue())) {
			mprAcquirerType = AcquirerType.KOTAK.getCode();
		} else if (name.contains(Constants.YES_BANK_CDC.getValue())) {
			mprAcquirerType = AcquirerType.YESBANKCB.getCode();
		} else if (name.contains(Constants.YES_BANK_UPI.getValue())) {
			mprAcquirerType = AcquirerType.YESBANKCB.getCode();
		} /*
			 * else if(name.contains(Constants.FEDERAL_BANK_CDC.getValue())) {
			 * mprAcquirerType = AcquirerType.FEDERAL.getCode(); }
			 */
		else if (name.contains(Constants.FSS_CDC.getValue())) {
			mprAcquirerType = AcquirerType.FSS.getCode();
		} else if (name.contains(Constants.ISGPAY_CDC.getValue())) {
			mprAcquirerType = AcquirerType.ISGPAY.getCode();
		} else if (name.contains(Constants.DIRECPAY_CDC.getValue())) {
			mprAcquirerType = AcquirerType.DIRECPAY.getCode();
		} else if (name.contains(Constants.ATOM_CDC.getValue())) {
			mprAcquirerType = AcquirerType.ATOM.getCode();
		} else if (name.contains(Constants.AXISBANK_CDC.getValue())) {
			mprAcquirerType = AcquirerType.AXISBANK.getCode();
		} else if (name.contains(Constants.AXISBANK_UPI.getValue())) {
			mprAcquirerType = AcquirerType.AXISBANK.getCode();
		} else if (name.contains(Constants.APBL_CDC.getValue())) {
			mprAcquirerType = AcquirerType.APBL.getCode();
		} else if (name.contains(Constants.APBL_UPI.getValue())) {
			mprAcquirerType = AcquirerType.APBL.getCode();
		} else if (name.contains(Constants.APBL_WL.getValue())) {
			mprAcquirerType = AcquirerType.APBL.getCode();
		} else if (name.contains(Constants.HDFC_CDC.getValue())) {
			mprAcquirerType = AcquirerType.HDFC.getCode();
		} else if (name.contains(Constants.PAYTM_WL.getValue())) {
			mprAcquirerType = AcquirerType.PAYTM.getCode();
		} else if (name.contains(Constants.PAYTM_CDC.getValue())) {
			mprAcquirerType = AcquirerType.PAYTM.getCode();
		} else if (name.contains(Constants.MOBIKWIK_WL.getValue())) {
			mprAcquirerType = AcquirerType.MOBIKWIK.getCode();
		} else if (name.contains(Constants.MOBIKWIK_CDC.getValue())) {
			mprAcquirerType = AcquirerType.MOBIKWIK.getCode();
		} else if (name.contains(Constants.INGENICO_CDC.getValue())) {
			mprAcquirerType = AcquirerType.INGENICO.getCode();
		} else if (name.contains(Constants.PHONEPE_CDC.getValue())) {
			mprAcquirerType = AcquirerType.PHONEPE.getCode();
		} else if (name.contains(Constants.PHONEPE_WL.getValue())) {
			mprAcquirerType = AcquirerType.PHONEPE.getCode();
		} else if (name.contains(Constants.PAYU_CDC.getValue())) {
			mprAcquirerType = AcquirerType.PAYU.getCode();
		} else if (name.contains(Constants.BILLDESK_CDC.getValue())) {
			mprAcquirerType = AcquirerType.BILLDESK.getCode();
		} else if (name.contains(Constants.CASHFREE_CDC.getValue())) {
			mprAcquirerType = AcquirerType.CASHFREE.getCode();
		} else if (name.contains(Constants.CASHFREE_UPI.getValue())) {
			mprAcquirerType = AcquirerType.CASHFREE.getCode();
		} else if (name.contains(Constants.CASHFREE_NB.getValue())) {
			mprAcquirerType = AcquirerType.CASHFREE.getCode();
		} else if (name.contains(Constants.CASHFREE_WL.getValue())) {
			mprAcquirerType = AcquirerType.CASHFREE.getCode();
		} else if (name.contains(Constants.AGREEPAY_CDC.getValue())) {
			mprAcquirerType = AcquirerType.AGREEPAY.getCode();
		} else if (name.contains(Constants.AGREEPAY_UPI.getValue())) {
			mprAcquirerType = AcquirerType.AGREEPAY.getCode();
		} else if (name.contains(Constants.AGREEPAY_NB.getValue())) {
			mprAcquirerType = AcquirerType.AGREEPAY.getCode();
		} else if (name.contains(Constants.AGREEPAY_WL.getValue())) {
			mprAcquirerType = AcquirerType.AGREEPAY.getCode();
		} else if (name.contains(Constants.YESBANKCB_CDC.getValue())) {
			mprAcquirerType = AcquirerType.YESBANKCB.getCode();
		} else if (name.contains(Constants.YESBANKCB_UPI.getValue())) {
			mprAcquirerType = AcquirerType.YESBANKCB.getCode();
		} else if (name.contains(Constants.BILLDESK_CDC.getValue())) {
			mprAcquirerType = AcquirerType.BILLDESK.getCode();
		} else if (name.contains(Constants.SBI_CDC.getValue())) {
			mprAcquirerType = AcquirerType.SBI.getCode();
		} else if (name.contains(Constants.SBI_NB.getValue())) {
			mprAcquirerType = AcquirerType.SBI.getCode();
		}
		// ----- added by sonu 06-06-2022 -----------//
		else if (name.contains(Constants.SBINB_NB.getValue())) {
			mprAcquirerType = AcquirerType.SBINB.getCode();
		} else if (name.contains(Constants.SBINB_CDC.getValue())) {
			mprAcquirerType = AcquirerType.SBINB.getCode();
		} else if (name.contains(Constants.SBICARD_CDC.getValue())) {
			mprAcquirerType = AcquirerType.SBICARD.getCode();
		}else if (name.contains(Constants.TFP_CDC.getValue())) {
			mprAcquirerType = AcquirerType.TFP.getCode();
		}  
		
		else if (name.contains(Constants.NBFEDERAL_NB.getValue())) {
			mprAcquirerType = AcquirerType.NB_FEDERAL.getCode();
		} else if (name.contains(Constants.NBFEDERAL_CDC.getValue())) {
			mprAcquirerType = AcquirerType.NB_FEDERAL.getCode();
		}else if (name.contains(Constants.CANARANBBANK_CDC.getValue())) {
			mprAcquirerType = AcquirerType.CANARANBBANK.getCode();
		}
		else if (name.contains(Constants.CANARA_NB.getValue())) {
			mprAcquirerType = AcquirerType.CANARANBBANK.getCode();
		}
//added by vijay
		else if (name.contains(Constants.TFP.getValue())) {
			mprAcquirerType = AcquirerType.TFP.getCode();
		}
		else if (name.contains(Constants.TMB_NB.getValue())) {
			mprAcquirerType = AcquirerType.TMBNB.getCode();
		}else if (name.contains(Constants.SHIVALIK_NB.getValue())) {
			mprAcquirerType = AcquirerType.SHIVALIKNBBANK.getCode();
		}
		else if (name.contains(Constants.FEDERAL_NB.getValue())) {
			mprAcquirerType = AcquirerType.FEDERAL.getCode();
		} else if (name.contains(Constants.FEDERAL_CDC.getValue())) {
			mprAcquirerType = AcquirerType.FEDERAL.getCode();
		} else if (name.contains(Constants.FEDERAL_UPI.getValue())) {
			mprAcquirerType = AcquirerType.FEDERAL.getCode();
		} else if (name.contains(Constants.FEDERAL_WL.getValue())) {
			mprAcquirerType = AcquirerType.FEDERAL.getCode();
		} else if (name.contains(Constants.PINELABS_NB.getValue())) {
			mprAcquirerType = AcquirerType.PINELABS.getCode();
		} else if (name.contains(Constants.PINELABS_CDC.getValue())) {
			mprAcquirerType = AcquirerType.PINELABS.getCode();
		} else if (name.contains(Constants.PINELABS_UPI.getValue())) {
			mprAcquirerType = AcquirerType.PINELABS.getCode();
		} else if (name.contains(Constants.PINELABS_WL.getValue())) {
			mprAcquirerType = AcquirerType.PINELABS.getCode();
		} else if (name.contains(Constants.YESBANKNB_NB.getValue())) {
			mprAcquirerType = AcquirerType.YESBANKNB.getCode();
		} else if (name.contains(Constants.YESBANKNB_CDC.getValue())) {
			mprAcquirerType = AcquirerType.YESBANKNB.getCode();
		} else if (name.contains(Constants.AXISBANK_NB.getValue())) {
			mprAcquirerType = AcquirerType.AXISBANK.getCode();
		} else if (name.contains(Constants.AXISBANK_WL.getValue())) {
			mprAcquirerType = AcquirerType.AXISBANK.getCode();
		} else if (name.contains(Constants.PAYU_NB.getValue())) {
			mprAcquirerType = AcquirerType.PAYU.getCode();
		}

		else if (name.contains(Constants.PAYU_UPI.getValue())) {
			mprAcquirerType = AcquirerType.PAYU.getCode();
		} else if (name.contains(Constants.PAYU_WL.getValue())) {
			mprAcquirerType = AcquirerType.PAYU.getCode();
		} else if (name.contains(Constants.ATOM_NB.getValue())) {
			mprAcquirerType = AcquirerType.ATOM.getCode();
		} else if (name.contains(Constants.ATOM_UPI.getValue())) {
			mprAcquirerType = AcquirerType.ATOM.getCode();
		} else if (name.contains(Constants.ATOM_WL.getValue())) {
			mprAcquirerType = AcquirerType.ATOM.getCode();
		}

		else if (name.contains(Constants.AXIS_BANKS_CDC.getValue())) {
			mprAcquirerType = AcquirerType.AXISBANK.getCode();
		}
		else if (name.contains(Constants.IDFC_NB.getValue())) {
			mprAcquirerType = AcquirerType.IDFC.getCode();
		}
		else if (name.contains(Constants.COSMOS_UPI.getValue())) {
			mprAcquirerType = AcquirerType.COSMOS.getCode();
		}
		else if (name.contains(Constants.JAMMUANDKASHMIR_NB.getValue())) {
			mprAcquirerType = AcquirerType.JAMMU_AND_KASHMIR.getCode();
		}
		else if (name.contains(Constants.JAMMUANDKASHMIR.getValue())) {
			mprAcquirerType = AcquirerType.JAMMU_AND_KASHMIR.getCode();
		}else if(name.contains(Constants.DEMO.getValue())) {
			mprAcquirerType = AcquirerType.DEMO.getCode();
		}


		else if (name.contains(Constants.EASEBUZZ_CDC.getValue())) {
			mprAcquirerType = AcquirerType.EASEBUZZ.getCode();
		} else if (name.contains(Constants.KOTAK_BANKS_CDC.getValue())) {
			mprAcquirerType = AcquirerType.KOTAK.getCode();
		} else if (name.contains(Constants.CAMSPAY_CDC.getValue())) {
			mprAcquirerType = AcquirerType.CAMSPAY.getCode();
		} else if (name.contains(Constants.CAMSPAY_NB.getValue())) {
			mprAcquirerType = AcquirerType.CAMSPAY.getCode();
		} else if (name.contains(Constants.CAMSPAY_UPI.getValue())) {
			mprAcquirerType = AcquirerType.CAMSPAY.getCode();
		}
		else if(name.contains(Constants.SBI_CDC.getValue())){
			mprAcquirerType = AcquirerType.SBI.getCode();
		}
		else if(name.contains(Constants.CITYUNIONBANK_NB.getValue())){
			mprAcquirerType = AcquirerType.CITYUNIONBANK.getCode();
		}
		else if(name.contains(Constants.SBI_NB.getValue())){
			mprAcquirerType = AcquirerType.SBI.getCode();
		}
		//----- added by sonu 06-06-2022 -----------//
		else if(name.contains(Constants.SBINB_NB.getValue())){
			mprAcquirerType = AcquirerType.SBINB.getCode();
		}
		else if(name.contains(Constants.SBINB_CDC.getValue())){
			mprAcquirerType = AcquirerType.SBINB.getCode();
		}
		else if(name.contains(Constants.SBICARD_CDC.getValue())){
			mprAcquirerType = AcquirerType.SBICARD.getCode();
		}
		else if(name.contains(Constants.TFP_CDC.getValue())){
			mprAcquirerType = AcquirerType.TFP.getCode();
		}
		else if(name.contains(Constants.NBFEDERAL_NB.getValue())){
			mprAcquirerType = AcquirerType.NB_FEDERAL.getCode();
		}
		else if(name.contains(Constants.NBFEDERAL_CDC.getValue())){
			mprAcquirerType = AcquirerType.NB_FEDERAL.getCode();
		}
		
		else if (name.contains(Constants.FEDERAL_NB.getValue())) {
			mprAcquirerType = AcquirerType.FEDERAL.getCode();
		}
		else if(name.contains(Constants.FEDERAL_CDC.getValue())){
			mprAcquirerType = AcquirerType.FEDERAL.getCode();
		}
		else if(name.contains(Constants.FEDERAL_UPI.getValue())){
			mprAcquirerType = AcquirerType.FEDERAL.getCode();
		}
		else if(name.contains(Constants.FEDERAL_WL.getValue())){
			mprAcquirerType = AcquirerType.FEDERAL.getCode();
		}
		else if(name.contains(Constants.PINELABS_NB.getValue())){
			mprAcquirerType = AcquirerType.PINELABS.getCode();
		}
		else if(name.contains(Constants.PINELABS_CDC.getValue())){
			mprAcquirerType = AcquirerType.PINELABS.getCode();
		}
		else if(name.contains(Constants.PINELABS_UPI.getValue())){
			mprAcquirerType = AcquirerType.PINELABS.getCode();
		}
		else if(name.contains(Constants.PINELABS_WL.getValue())){
			mprAcquirerType = AcquirerType.PINELABS.getCode();
		}
		else if(name.contains(Constants.YESBANKNB_NB.getValue())){
			mprAcquirerType = AcquirerType.YESBANKNB.getCode();
		}
		else if(name.contains(Constants.YESBANKNB_CDC.getValue())){
			mprAcquirerType = AcquirerType.YESBANKNB.getCode();
		}
		else if(name.contains(Constants.AXISBANK_NB.getValue())){
			mprAcquirerType = AcquirerType.AXISBANK.getCode();
		}
		else if(name.contains(Constants.AXISBANK_WL.getValue())){
			mprAcquirerType = AcquirerType.AXISBANK.getCode();
		}
		else if(name.contains(Constants.PAYU_NB.getValue())){
			mprAcquirerType = AcquirerType.PAYU.getCode();
		}
		
		else if(name.contains(Constants.PAYU_UPI.getValue())){
			mprAcquirerType = AcquirerType.PAYU.getCode();
		}
		else if(name.contains(Constants.PAYU_WL.getValue())){
			mprAcquirerType = AcquirerType.PAYU.getCode();
		}
		else if(name.contains(Constants.ATOM_NB.getValue())){
			mprAcquirerType = AcquirerType.ATOM.getCode();
		}
		else if(name.contains(Constants.ATOM_UPI.getValue())){
			mprAcquirerType = AcquirerType.ATOM.getCode();
		}
		else if(name.contains(Constants.ATOM_WL.getValue())){
			mprAcquirerType = AcquirerType.ATOM.getCode();
		}
		
		else if(name.contains(Constants.AXIS_BANKS_CDC.getValue())){
			mprAcquirerType = AcquirerType.AXISBANK.getCode();
		}
		
		else if(name.contains(Constants.EASEBUZZ_CDC.getValue())){
			mprAcquirerType = AcquirerType.EASEBUZZ.getCode();
		}
		else if(name.contains(Constants.KOTAK_BANKS_CDC.getValue())){
			mprAcquirerType = AcquirerType.KOTAK.getCode();
		} else if (name.contains(Constants.QUOMO_CDC.getValue())) {
			mprAcquirerType = AcquirerType.QUOMO.getCode();
		} else if (name.contains(Constants.QUOMO_NB.getValue())) {
			mprAcquirerType = AcquirerType.QUOMO.getCode();
		}
		
		else {
			mprAcquirerType = "";
		}

		logger.info("mprAcquirerType :: " + mprAcquirerType);
		logger.info("previous :: " + previous.getFieldsAsString());

		if (previous != null && mprAcquirerType.equals(previous.get(FieldType.RECO_ACQUIRER_TYPE.getName()))) {
			return true;
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.MISMATCH_ACQUIRER_TYPE.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.MISMATCH_ACQUIRER_TYPE.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(), Constants.BANK.getValue());
		return false;
	}
}