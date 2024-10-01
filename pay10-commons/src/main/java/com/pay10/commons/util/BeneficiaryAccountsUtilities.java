package com.pay10.commons.util;

import org.bson.Document;

import com.amazonaws.util.AWSRequestMetrics.Field;
import com.pay10.commons.user.BeneficiaryAccounts;

public class BeneficiaryAccountsUtilities {

	public static BeneficiaryAccounts getBeneficiaryFromDoc(Document doc) {
		BeneficiaryAccounts beneficiaryAccounts = new BeneficiaryAccounts();
//		beneficiaryAccounts.setStatus(AccountStatus.valueOf(doc.getString(CrmFieldType.BENE_STATUS.getName())));
		beneficiaryAccounts.setId(doc.getString(FieldType.BENE_ID.getName()));
		beneficiaryAccounts.setMerchantProvidedId(doc.getString(FieldType.BENE_MERCHANT_PROVIDED_ID.getName()));
		beneficiaryAccounts.setMerchantProvidedName(doc.getString(FieldType.BENE_MERCHANT_PROVIDED_NAME.getName()));
		beneficiaryAccounts.setMerchantPayId(doc.getString(FieldType.PAY_ID.getName()));
		beneficiaryAccounts.setStatus(AccountStatus.getInstancefromCode(doc.getString(FieldType.BENE_STATUS.getName())));
		beneficiaryAccounts.setCreatedDate(doc.getString(FieldType.CREATE_DATE.getName()));
		beneficiaryAccounts.setUpdatedDate(doc.getString(FieldType.UPDATE_DATE.getName()));
		beneficiaryAccounts.setRequestedBy(doc.getString(FieldType.BENE_REQUESTED_BY.getName()));
		beneficiaryAccounts.setProcessedBy(doc.getString(FieldType.BENE_PROCESSED_BY.getName()));
		beneficiaryAccounts.setCustId(doc.getString(FieldType.CUST_ID_BENEFICIARY.getName()));
		beneficiaryAccounts.setBeneficiaryCd(doc.getString(FieldType.BENEFICIARY_CD.getName()));
		beneficiaryAccounts.setSrcAccountNo(doc.getString(FieldType.SRC_ACCOUNT_NO.getName()));
		beneficiaryAccounts.setPaymentType(NodalPaymentTypes.getInstancefromCode(doc.getString(FieldType.PAYMENT_TYPE.getName())));
		beneficiaryAccounts.setBeneName(doc.getString(FieldType.BENE_NAME.getName()));
		beneficiaryAccounts.setBeneType(doc.getString(FieldType.BENE_TYPE.getName()));
		beneficiaryAccounts.setBeneExpiryDate(doc.getString(FieldType.BENE_EXPIRY_DATE.getName()));
		beneficiaryAccounts.setCurrencyCd(CurrencyTypes.getInstancefromCode(doc.getString(FieldType.CURRENCY_CD.getName())));
		beneficiaryAccounts.setTransactionLimit(doc.getString(FieldType.BENE_TRANSACTION_LIMIT.getName()));
		beneficiaryAccounts.setBankName(doc.getString(FieldType.BENE_BANK_NAME.getName()));
		beneficiaryAccounts.setIfscCode(doc.getString(FieldType.IFSC_CODE.getName()));
		beneficiaryAccounts.setBeneAccountNo(doc.getString(FieldType.BENE_ACCOUNT_NO.getName()));
		beneficiaryAccounts.setMobileNo(doc.getString(FieldType.BENE_MOBILE_NUMBER.getName()));
		beneficiaryAccounts.setEmailId(doc.getString(FieldType.BENE_EMAIL_ID.getName()));
		beneficiaryAccounts.setAddress1(doc.getString(FieldType.BENE_ADDRESS_1.getName()));
		beneficiaryAccounts.setAddress2(doc.getString(FieldType.BENE_ADDRESS_2.getName()));
		beneficiaryAccounts.setSwiftCode(doc.getString(FieldType.BENE_SWIFT_CODE.getName()));	// ??
//		beneficiaryAccounts.setActions(doc.getString("BENE_ACTIONS"));		// ??
		beneficiaryAccounts.setAcquirer(doc.getString(FieldType.ACQUIRER_TYPE.getName()));
		beneficiaryAccounts.setRrn(doc.getString(FieldType.RRN.getName()));
		beneficiaryAccounts.setResponseMessage(doc.getString(FieldType.RESPONSE_MESSAGE.getName())); // store as byte LOB
		beneficiaryAccounts.setAadharNo(doc.getString(FieldType.BENE_AADHAR_NO.getName()));
		beneficiaryAccounts.setMerchantBusinessName(doc.getString(FieldType.BENE_MERCHANT_BUSINESS_NAME.getName()));
		beneficiaryAccounts.setPgRespCode(doc.getString(FieldType.PG_RESP_CODE.getName()));
		beneficiaryAccounts.setPgTxnMessage(doc.getString(FieldType.PG_TXN_MESSAGE.getName()));
		
		return beneficiaryAccounts;
	}
	
	public static Document getDocFromBeneficiary(BeneficiaryAccounts beneficiaryAccounts) {
		Document docBuilder = new Document();
//		docBuilder.put(CrmFieldType.INVOICE_ADDRESS.getName(), beneficiaryAccounts.getAddress());
		docBuilder.put(FieldType.BENE_ID.getName(), beneficiaryAccounts.getId());
		docBuilder.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), beneficiaryAccounts.getMerchantProvidedId());
		docBuilder.put(FieldType.BENE_MERCHANT_PROVIDED_NAME.getName(), beneficiaryAccounts.getMerchantProvidedName());
		docBuilder.put(FieldType.PAY_ID.getName(), beneficiaryAccounts.getMerchantPayId());
		docBuilder.put(FieldType.BENE_STATUS.getName(), beneficiaryAccounts.getStatus().getCode());
		docBuilder.put(FieldType.CREATE_DATE.getName(), beneficiaryAccounts.getCreatedDate());
		docBuilder.put(FieldType.UPDATE_DATE.getName(), beneficiaryAccounts.getUpdatedDate());
		docBuilder.put(FieldType.BENE_REQUESTED_BY.getName(), beneficiaryAccounts.getRequestedBy());
		docBuilder.put(FieldType.BENE_PROCESSED_BY.getName(), beneficiaryAccounts.getProcessedBy());
		docBuilder.put(FieldType.CUST_ID_BENEFICIARY.getName(), beneficiaryAccounts.getCustId());
		docBuilder.put(FieldType.BENEFICIARY_CD.getName(), beneficiaryAccounts.getBeneficiaryCd());
		docBuilder.put(FieldType.SRC_ACCOUNT_NO.getName(), beneficiaryAccounts.getSrcAccountNo());
		docBuilder.put(FieldType.PAYMENT_TYPE.getName(), beneficiaryAccounts.getPaymentType().getCode());
		docBuilder.put(FieldType.BENE_NAME.getName(), beneficiaryAccounts.getBeneName());
		docBuilder.put(FieldType.BENE_TYPE.getName(), beneficiaryAccounts.getBeneType());
		docBuilder.put(FieldType.BENE_EXPIRY_DATE.getName(), beneficiaryAccounts.getBeneExpiryDate());
		docBuilder.put(FieldType.CURRENCY_CD.getName(), beneficiaryAccounts.getCurrencyCd().getCode());
		docBuilder.put(FieldType.BENE_TRANSACTION_LIMIT.getName(), beneficiaryAccounts.getTransactionLimit());
		docBuilder.put(FieldType.BENE_BANK_NAME.getName(), beneficiaryAccounts.getBankName());
		docBuilder.put(FieldType.IFSC_CODE.getName(), beneficiaryAccounts.getIfscCode());
		docBuilder.put(FieldType.BENE_ACCOUNT_NO.getName(), beneficiaryAccounts.getBeneAccountNo());
		docBuilder.put(FieldType.BENE_MOBILE_NUMBER.getName(), beneficiaryAccounts.getMobileNo());
		docBuilder.put(FieldType.BENE_EMAIL_ID.getName(), beneficiaryAccounts.getEmailId());
		docBuilder.put(FieldType.BENE_ADDRESS_1.getName(), beneficiaryAccounts.getAddress1());
		docBuilder.put(FieldType.BENE_ADDRESS_2.getName(), beneficiaryAccounts.getAddress2());
		docBuilder.put(FieldType.BENE_SWIFT_CODE.getName(), beneficiaryAccounts.getSwiftCode());
//		docBuilder.put("BENE_ACTIONS", beneficiaryAccounts.getActions());
		docBuilder.put(FieldType.ACQUIRER_TYPE.getName(), beneficiaryAccounts.getAcquirer());
		docBuilder.put(FieldType.RRN.getName(), beneficiaryAccounts.getRrn());
		docBuilder.put(FieldType.RESPONSE_MESSAGE.getName(), beneficiaryAccounts.getResponseMessage());
		docBuilder.put(FieldType.BENE_AADHAR_NO.getName(), beneficiaryAccounts.getAadharNo());
		docBuilder.put(FieldType.BENE_MERCHANT_BUSINESS_NAME.getName(), beneficiaryAccounts.getMerchantBusinessName());
		docBuilder.put(FieldType.PG_TXN_MESSAGE.getName(), beneficiaryAccounts.getPgTxnMessage());
		docBuilder.put(FieldType.PG_RESP_CODE.getName(), beneficiaryAccounts.getPgRespCode());
		
		return docBuilder;
	}


}
