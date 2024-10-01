package com.pay10.nodal.payout.yesBankNodalFT3;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.BeneficiaryAccountsDao;
import com.pay10.commons.dao.NodalAccountDetailsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.NodalAccountDetails;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.SettlementStatusType;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.commons.util.YesBankFT3ResultType;
import com.pay10.nodal.payout.Transaction;

@Service
public class YesBankFT3TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(YesBankFT3TransactionConverter.class.getName());
	
	public static final String REQUEST_STATUS_KEY = "RequestStatus";
	public static final String RRN_OPEN_KEY = "ReqRefNo";
	
	@Autowired
	NodalAccountDetailsDao nodalAccountDetailsDao;
	
	@Autowired
	BeneficiaryAccountsDao beneficiaryAccountsDao;
	
	public String createAddBeneficiaryRequest(Fields fields) {
		// TODO get nodal details from crm.
		NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(fields.get(FieldType.NODAL_ACQUIRER.getName()));
		fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), nodalAccountDetails.getCustId());
		fields.put(FieldType.SRC_ACCOUNT_NO.getName(), nodalAccountDetails.getAccountNumber());
		String request = "{\"maintainBene\":{\"CustId\":\"" + nodalAccountDetails.getCustId() + "\","
						+ "\"BeneficiaryCd\": \""+ fields.get(FieldType.BENEFICIARY_CD.getName()) + "\","
						+ "\"SrcAccountNo\": \"" + nodalAccountDetails.getAccountNumber() + "\","
						+ "\"PaymentType\": \"" + fields.get(FieldType.PAYMENT_TYPE.getName()) + "\","
						+ "\"BeneName\": \"" + fields.get(FieldType.BENE_NAME.getName()) + "\","
						+ "\"BeneType\": \"" + fields.get(FieldType.BENE_TYPE.getName()) + "\","
						+ "\"CurrencyCd\": \"" + fields.get(FieldType.CURRENCY_CD.getName()) + "\","
						+ "\"TransactionLimit\":\"" + fields.get(FieldType.BENE_TRANSACTION_LIMIT.getName()) + "\","
						+ "\"BankName\": \"" + fields.get(FieldType.BANK_NAME.getName()) + "\","
						+ "\"IfscCode\": \"" + fields.get(FieldType.IFSC_CODE.getName()) + "\","
						+ "\"BeneAccountNo\": \"" + fields.get(FieldType.BENE_ACCOUNT_NO.getName()) + "\",";
		
//		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_MOBILE_NUMBER.getName()))) {
//			request += "\"MobileNo\": \"" + fields.get(FieldType.BENE_MOBILE_NUMBER.getName()) + "\",";
//		}
//		
//		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_EMAIL_ID.getName()))) {
//			request += "\"EmailId\": \"" + fields.get(FieldType.BENE_EMAIL_ID.getName()) + "\",";
//		}
//		
//		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_AADHAR_NO.getName()))) {
//			request += "\"AadhaarNo\": \"" + fields.get(FieldType.BENE_AADHAR_NO.getName()) + "\",";
//		}
//		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_ADDRESS_1.getName()))) {
//			request += "\"Address1\": \"" + fields.get(FieldType.BENE_ADDRESS_1.getName()) + "\",";
//		}
//		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_ADDRESS_2.getName()))) {
//			request += "\"Address2\": \"" + fields.get(FieldType.BENE_ADDRESS_2.getName()) + "\",";
//		}
		request += "\"Action\":\"" + SettlementTransactionType.ADD_BENEFICIARY.getCode() + "\"}}";
		return request;				
	}
	
	public String fundTransferRequest(Fields fields) {
		return "{\"Data\":{\"ConsentId\":\"" + fields.get(FieldType.CUST_ID_BENEFICIARY.getName()) + "\","
					+ "\"Initiation\":{\"InstructionIdentification\":\"" + fields.get(FieldType.TXN_ID.getName()) + "\","
					+ "\"EndToEndIdentification\":\"\","
					+ "\"InstructedAmount\":{\"Amount\":\"" + Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), FieldType.CURRENCY_CODE.getName()) + "\","
					+ "\"Currency\":\"" + fields.get(FieldType.CURRENCY_CD.getName()) + "\"},"
					+ "\"DebtorAccount\":{\"Identification\":\"" + fields.get(FieldType.SRC_ACCOUNT_NO.getName()) + "\","
					+ "\"SecondaryIdentification\":\"" + fields.get(FieldType.CUST_ID_BENEFICIARY.getName()) + "\"},"
					+ "\"CreditorAccount\":{\"BeneficiaryCode\":\"" + fields.get(FieldType.BENEFICIARY_CD.getName()) + "\"},"
					+ "\"RemittanceInformation\":{\"Unstructured\":{\"CreditorReferenceInformation\":\"" + fields.get(FieldType.PRODUCT_DESC.getName()) + "\"}},"
					+ "\"ClearingSystemIdentification\":\"" + fields.get(FieldType.PAYMENT_TYPE.getName()) + "\"}},"
					+ "\"Risk\":{\"PaymentContextCode\":\"NODAL\"}}";
	}
	
	public String fundConfirmationRequest(Fields fields) {
		return "{\"Data\":{\"DebtorAccount\":{\"ConsentId\":\"" + fields.get(FieldType.CUST_ID_BENEFICIARY.getName()) + "\","
					+ "\"Identification\":\"" + fields.get(FieldType.SRC_ACCOUNT_NO.getName()) + "\","
					+ "\"SecondaryIdentification\":\"" + fields.get(FieldType.CUST_ID_BENEFICIARY.getName()) + "\"}}}";
		
	}

	public String getStatusRequest(Fields fields) {
		// TODO get nodal details from crm.
		NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(fields.get(FieldType.NODAL_ACQUIRER.getName()));
		fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), nodalAccountDetails.getCustId());
		
		return "{\"Data\":{\"InstrId\":\"" + fields.get(FieldType.TXN_ID.getName()) + "\","
					+ "\"ConsentId\":\"" + fields.get(FieldType.CUST_ID_BENEFICIARY.getName()) + "\","
					+ "\"SecondaryIdentification\":\"" + fields.get(FieldType.CUST_ID_BENEFICIARY.getName()) + "\"}}";
	}
	
	public String createDisableBeneficiaryRequest(Fields fields) {
		// TODO get nodal details from crm.
		NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(fields.get(FieldType.NODAL_ACQUIRER.getName()));
		fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), nodalAccountDetails.getCustId());
		fields.put(FieldType.SRC_ACCOUNT_NO.getName(), nodalAccountDetails.getAccountNumber());
		
		return "{\"maintainBene\":{\"CustId\":\"" + nodalAccountDetails.getCustId() + "\","
						+ "\"BeneficiaryCd\": \""+ fields.get(FieldType.BENEFICIARY_CD.getName()) + "\","
						+ "\"SrcAccountNo\": \"" + nodalAccountDetails.getAccountNumber() + "\","
						+ "\"PaymentType\": \"" + fields.get(FieldType.PAYMENT_TYPE.getName()) + "\","
						+ "\"Action\":\"" + SettlementTransactionType.DISABLE_BENEFICIARY.getCode() + "\"}}";
	}

	public String createModifyBeneficiaryRequest(Fields fields) {
		// TODO get nodal details from crm.
		NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(fields.get(FieldType.NODAL_ACQUIRER.getName()));
		fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), nodalAccountDetails.getCustId());
		fields.put(FieldType.SRC_ACCOUNT_NO.getName(), nodalAccountDetails.getAccountNumber());
		String request = "{\"maintainBene\":{\"CustId\":\"" + nodalAccountDetails.getCustId() + "\","
						+ "\"BeneficiaryCd\": \""+ fields.get(FieldType.BENEFICIARY_CD.getName()) + "\","
						+ "\"SrcAccountNo\": \"" + nodalAccountDetails.getAccountNumber() + "\","
						+ "\"PaymentType\": \"" + fields.get(FieldType.PAYMENT_TYPE.getName()) + "\","
						+ "\"BeneName\": \"" + fields.get(FieldType.BENE_NAME.getName()) + "\","
						+ "\"BeneType\": \"" + fields.get(FieldType.BENE_TYPE.getName()) + "\","
						+ "\"CurrencyCd\": \"" + fields.get(FieldType.CURRENCY_CD.getName()) + "\","
						+ "\"TransactionLimit\":\"" + fields.get(FieldType.BENE_TRANSACTION_LIMIT.getName()) + "\","
						+ "\"BankName\": \"" + fields.get(FieldType.BANK_NAME.getName()) + "\","
						+ "\"IfscCode\": \"" + fields.get(FieldType.IFSC_CODE.getName()) + "\","
						+ "\"BeneAccountNo\": \"" + fields.get(FieldType.BENE_ACCOUNT_NO.getName()) + "\",";
		
//		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_MOBILE_NUMBER.getName()))) {
//			request += "\"MobileNo\": \"" + fields.get(FieldType.BENE_MOBILE_NUMBER.getName()) + "\",";
//		}
//		
//		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_EMAIL_ID.getName()))) {
//			request += "\"EmailId\": \"" + fields.get(FieldType.BENE_EMAIL_ID.getName()) + "\",";
//		}
//		
//		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_AADHAR_NO.getName()))) {
//			request += "\"AadhaarNo\": \"" + fields.get(FieldType.BENE_AADHAR_NO.getName()) + "\",";
//		}
//		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_ADDRESS_1.getName()))) {
//			request += "\"Address1\": \"" + fields.get(FieldType.BENE_ADDRESS_1.getName()) + "\",";
//		}
//		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_ADDRESS_2.getName()))) {
//			request += "\"Address2\": \"" + fields.get(FieldType.BENE_ADDRESS_2.getName()) + "\",";
//		}
		
		request += "\"Action\":\"" + SettlementTransactionType.MODIFY_BENEFICIARY.getCode() + "\"}}";
		return request;
	}

	public String createVerifyBeneficiaryRequest(Fields fields) {
		// TODO get nodal details from crm.
		NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(fields.get(FieldType.NODAL_ACQUIRER.getName()));
		fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), nodalAccountDetails.getCustId());
		fields.put(FieldType.SRC_ACCOUNT_NO.getName(), nodalAccountDetails.getAccountNumber());
		
		return "{\"maintainBene\":{\"CustId\":\"" + nodalAccountDetails.getCustId() + "\","
						+ "\"BeneficiaryCd\": \""+ fields.get(FieldType.BENEFICIARY_CD.getName()) + "\","
						+ "\"SrcAccountNo\": \"" + nodalAccountDetails.getAccountNumber() + "\","
						+ "\"PaymentType\": \"" + fields.get(FieldType.PAYMENT_TYPE.getName()) + "\","
						+ "\"BeneName\": \"" + fields.get(FieldType.BENE_NAME.getName()) + "\","
						+ "\"BeneType\": \"" + fields.get(FieldType.BENE_TYPE.getName()) + "\","
						+ "\"CurrencyCd\": \"" + fields.get(FieldType.CURRENCY_CD.getName()) + "\","
						+ "\"TransactionLimit\":\"" + fields.get(FieldType.BENE_TRANSACTION_LIMIT.getName()) + "\","
						+ "\"BankName\": \"" + fields.get(FieldType.BANK_NAME.getName()) + "\","
						+ "\"IfscCode\": \"" + fields.get(FieldType.IFSC_CODE.getName()) + "\","
						+ "\"BeneAccountNo\": \"" + fields.get(FieldType.BENE_ACCOUNT_NO.getName()) + "\","
						+ "\"Action\":\"" + SettlementTransactionType.VERIFY_BENEFICIARY.getCode() + "\"}}";
	}
	
	public Transaction addBeneficiaryToTransaction(Map<String, String> map) {
		Transaction transaction = new Transaction();
		
		String result = map.get("Result");
		
		if(result.equalsIgnoreCase("fault")) {
			// TODO use result type enum: but result type are incomplete
			transaction.setStatus(map.get("Result"));
			transaction.setResponeMessage(map.get("Reason"));
			transaction.setStatusCode(map.get("ErrorCode"));
			transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			return transaction;
		} else if(result.equalsIgnoreCase("SUCCESS")) {
			if (map.containsKey(RRN_OPEN_KEY)) {
				transaction.setUniqueResponseNo(map.get(RRN_OPEN_KEY));
			}
			transaction.setStatus(map.get(REQUEST_STATUS_KEY));
			transaction.setResponeMessage("SUCCESS");
			transaction.setStatusCode(ErrorType.SUCCESS.getCode());
			transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			return transaction;
		} 
		else if(result.equalsIgnoreCase("FAILURE")) {
			transaction.setStatus(map.get(REQUEST_STATUS_KEY));
			transaction.setResponeMessage(map.get("GeneralMsg"));
			transaction.setStatusCode(map.get("ErrorSubCode"));
			transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));

			return transaction;
		}
		
		else {
			logger.error("unknown status code in Beneificiary action");
			transaction.setStatus(ErrorType.UNKNOWN.getInternalMessage());
			transaction.setResponeMessage(YesBankFT3ResultType.UNKNOWN.getMessage());
			transaction.setStatusCode(YesBankFT3ResultType.UNKNOWN.getBankCode());
			transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
		} 
		return transaction;
	}

	public Transaction toTransaction(Map<String, String> map) {
		Transaction transaction = new Transaction();
		
		String result = map.get("Result");
		if(result.equalsIgnoreCase("fault")) {
			transaction.setStatus(map.get("Result"));
			YesBankFT3ResultType yesBankFT3ResultType = YesBankFT3ResultType.getInstanceFromCode(map.get("ErrorCode"));
			if(yesBankFT3ResultType == null) {
				yesBankFT3ResultType = YesBankFT3ResultType.UNKNOWN;
			}
			transaction.setResponeMessage(yesBankFT3ResultType.getMessage());
			transaction.setStatusCode(yesBankFT3ResultType.getBankCode());
			transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			return transaction;
		} else if(result.equalsIgnoreCase("SUCCESS")) {
			String status = map.get("Status");
			if(status.equalsIgnoreCase(YesBankFT3ResultType.RECEIVED.getStatusName())) {
				transaction.setStatus(SettlementStatusType.IN_PROCESS.getName());
				transaction.setResponeMessage(YesBankFT3ResultType.RECEIVED.getMessage());
				transaction.setStatusCode(YesBankFT3ResultType.RECEIVED.getBankCode());
				transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			} 
			else if(status.equalsIgnoreCase(YesBankFT3ResultType.DUPLICATE.getStatusName())) {
				transaction.setStatus(SettlementStatusType.FAILED.getName());
				transaction.setResponeMessage(YesBankFT3ResultType.DUPLICATE.getMessage());
				transaction.setStatusCode(YesBankFT3ResultType.DUPLICATE.getBankCode());
				transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			}
			else if(status.equalsIgnoreCase(YesBankFT3ResultType.FAILED.getStatusName())) {
				transaction.setStatus(SettlementStatusType.FAILED.getName());
				transaction.setResponeMessage(YesBankFT3ResultType.FAILED.getMessage());
				transaction.setStatusCode(YesBankFT3ResultType.FAILED.getBankCode());
				transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			} else {
				logger.error("unknown status code in fund transfer");
				transaction.setStatus(ErrorType.UNKNOWN.getInternalMessage());
				transaction.setResponeMessage(YesBankFT3ResultType.UNKNOWN.getMessage());
				transaction.setStatusCode(YesBankFT3ResultType.UNKNOWN.getBankCode());
				transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			}
		} 
		else {
			logger.error("unknown status code in fund transfer");
			transaction.setStatus(ErrorType.UNKNOWN.getInternalMessage());
			transaction.setResponeMessage(YesBankFT3ResultType.UNKNOWN.getMessage());
			transaction.setStatusCode(YesBankFT3ResultType.UNKNOWN.getBankCode());
			transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
		}
		return transaction;
	}
	
	public Transaction statusToTransaction(Map<String, String> map) {
		Transaction transaction = new Transaction();
		
		String result = map.get("Result");
		if(result.equalsIgnoreCase("fault")) {
			transaction.setStatus(map.get("Result"));
			
			YesBankFT3ResultType yesBankFT3ResultType = YesBankFT3ResultType.getInstanceFromCode(map.get("ErrorCode"));
			if(yesBankFT3ResultType == null) {
				yesBankFT3ResultType = YesBankFT3ResultType.UNKNOWN;
			}
			transaction.setResponeMessage(yesBankFT3ResultType.getMessage());
			transaction.setStatusCode(yesBankFT3ResultType.getBankCode());
			
			transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			return transaction;
		} 
		else if(result.equalsIgnoreCase("SUCCESS")) {
			String status = map.get("Status");
			
			if(map.containsKey("utr") && !(map.get("utr").equalsIgnoreCase("null"))) {
				transaction.setUniqueResponseNo(map.get("utr"));
			} else {
				transaction.setUniqueResponseNo("");
			}
			
			if(map.containsKey("acqId") && !(map.get("acqId").equalsIgnoreCase("null"))) {
				transaction.setBankReferenceNo(map.get("acqId"));
			} else {
				transaction.setBankReferenceNo("");
			}
			
			if(status.equalsIgnoreCase(YesBankFT3ResultType.PENDING.getStatusName()) 
					|| status.equalsIgnoreCase(YesBankFT3ResultType.ACCEPTED.getStatusName())) {
				transaction.setStatus(SettlementStatusType.IN_PROCESS.getName());
				transaction.setResponeMessage(YesBankFT3ResultType.PENDING.getMessage());
				transaction.setStatusCode(YesBankFT3ResultType.PENDING.getBankCode());
				transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			} 
			else if(status.equalsIgnoreCase(YesBankFT3ResultType.SETTLEMENT_REVERSED.getStatusName())) {
				transaction.setStatus(SettlementStatusType.SETTLEMENT_REVERSED.getName());
				transaction.setResponeMessage(YesBankFT3ResultType.SETTLEMENT_REVERSED.getMessage());
				transaction.setStatusCode(YesBankFT3ResultType.SETTLEMENT_REVERSED.getBankCode());
				transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			} 
			else if(status.equalsIgnoreCase(YesBankFT3ResultType.SETTLEMENT_IN_PROCESS.getStatusName())) {
				transaction.setStatus(SettlementStatusType.IN_PROCESS.getName());
				transaction.setResponeMessage(YesBankFT3ResultType.SETTLEMENT_IN_PROCESS.getMessage());
				transaction.setStatusCode(YesBankFT3ResultType.SETTLEMENT_IN_PROCESS.getBankCode());
				transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			} 
			else if(status.equalsIgnoreCase(YesBankFT3ResultType.SETTLEMENT_COMPLETED.getStatusName())) {
				transaction.setStatus(SettlementStatusType.SUCCESS.getName());
				transaction.setResponeMessage(YesBankFT3ResultType.SETTLEMENT_COMPLETED.getMessage());
				transaction.setStatusCode(YesBankFT3ResultType.SETTLEMENT_COMPLETED.getBankCode());
				transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			} 
			else if(status.equalsIgnoreCase(YesBankFT3ResultType.FAILED.getStatusName())) {
				transaction.setStatus(SettlementStatusType.FAILED.getName());
				transaction.setResponeMessage(YesBankFT3ResultType.FAILED.getMessage());
				transaction.setStatusCode(YesBankFT3ResultType.FAILED.getBankCode());
				transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			} 
			else {
				logger.error("Unknown status code in status enquiry.");
				transaction.setStatus(ErrorType.UNKNOWN.getInternalMessage());
				transaction.setResponeMessage(YesBankFT3ResultType.UNKNOWN.getMessage());
				transaction.setStatusCode(YesBankFT3ResultType.UNKNOWN.getBankCode());
				transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
			}
			
			if(StringUtils.isNotBlank(map.get("Reason")) && StringUtils.isNotBlank(map.get("ErrorCode"))) {
				transaction.setResponeMessage(map.get("Reason"));
				transaction.setStatusCode(map.get("ErrorCode"));
				transaction.setTransactionDate(map.get("Timestamp"));
				
			}
		}
		
		else {
			logger.error("unknown status code in fund transfer");
			transaction.setStatus(ErrorType.UNKNOWN.getInternalMessage());
			transaction.setResponeMessage(YesBankFT3ResultType.UNKNOWN.getMessage());
			transaction.setStatusCode(YesBankFT3ResultType.UNKNOWN.getBankCode());
			transaction.setTransactionDate(DateCreater.formatDateForDb(new Date()));
		}
		return transaction;
	}
	
	public String createProductionAddBeneficiaryRequest(Fields fields) {
		// TODO get nodal details from crm.
//		NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(fields.get(FieldType.NODAL_ACQUIRER.getName()));
//		System.out.println("Nodal Account details : " + nodalAccountDetails);
		fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), "8243053");
		fields.put(FieldType.SRC_ACCOUNT_NO.getName(), "010461100000100");
		String request = "{\"maintainBene\":{\"CustId\":\"" + fields.get(FieldType.CUST_ID_BENEFICIARY.getName())+ "\","
						+ "\"BeneficiaryCd\": \""+ fields.get(FieldType.BENEFICIARY_CD.getName()) + "\","
						+ "\"SrcAccountNo\": \"" + fields.get(FieldType.SRC_ACCOUNT_NO.getName()) + "\","
						+ "\"PaymentType\": \"" + fields.get(FieldType.PAYMENT_TYPE.getName()) + "\","
						+ "\"BeneName\": \"" + fields.get(FieldType.BENE_NAME.getName()) + "\","
						+ "\"BeneType\": \"" + fields.get(FieldType.BENE_TYPE.getName()) + "\","
						+ "\"CurrencyCd\": \"" + fields.get(FieldType.CURRENCY_CD.getName()) + "\","
						+ "\"TransactionLimit\":\"" + fields.get(FieldType.BENE_TRANSACTION_LIMIT.getName()) + "\","
						+ "\"BankName\": \"" + fields.get(FieldType.BANK_NAME.getName()) + "\","
						+ "\"IfscCode\": \"" + fields.get(FieldType.IFSC_CODE.getName()) + "\","
						+ "\"BeneAccountNo\": \"" + fields.get(FieldType.BENE_ACCOUNT_NO.getName()) + "\",";
		
		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_MOBILE_NUMBER.getName()))) {
			request += "\"MobileNo\": \"" + fields.get(FieldType.BENE_MOBILE_NUMBER.getName()) + "\",";
		}
		
		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_EMAIL_ID.getName()))) {
			request += "\"EmailId\": \"" + fields.get(FieldType.BENE_EMAIL_ID.getName()) + "\",";
		}
		
		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_AADHAR_NO.getName()))) {
			request += "\"AadhaarNo\": \"" + fields.get(FieldType.BENE_AADHAR_NO.getName()) + "\",";
		}
		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_ADDRESS_1.getName()))) {
			request += "\"Address1\": \"" + fields.get(FieldType.BENE_ADDRESS_1.getName()) + "\",";
		}
		if(!StringUtils.isEmpty(fields.get(FieldType.BENE_ADDRESS_2.getName()))) {
			request += "\"Address2\": \"" + fields.get(FieldType.BENE_ADDRESS_2.getName()) + "\",";
		}
		request += "\"Action\":\"" + SettlementTransactionType.ADD_BENEFICIARY.getCode() + "\"}}";
		return request;				
	}
	
	public String createProductionVerifyBeneficiaryRequest(Fields fields) {
		// TODO get nodal details from crm.
//		NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(fields.get(FieldType.NODAL_ACQUIRER.getName()));
//		System.out.println("Nodal Account details : " + nodalAccountDetails);
		fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), "8243053");
		fields.put(FieldType.SRC_ACCOUNT_NO.getName(), "010461100000100");
		
		return "{\"maintainBene\":{\"CustId\":\"" + fields.get(FieldType.CUST_ID_BENEFICIARY.getName()) + "\","
						+ "\"BeneficiaryCd\": \""+ fields.get(FieldType.BENEFICIARY_CD.getName()) + "\","
						+ "\"SrcAccountNo\": \"" + fields.get(FieldType.SRC_ACCOUNT_NO.getName()) + "\","
						+ "\"PaymentType\": \"" + fields.get(FieldType.PAYMENT_TYPE.getName()) + "\","
						+ "\"BeneName\": \"" + fields.get(FieldType.BENE_NAME.getName()) + "\","
						+ "\"BeneType\": \"" + fields.get(FieldType.BENE_TYPE.getName()) + "\","
						+ "\"CurrencyCd\": \"" + fields.get(FieldType.CURRENCY_CD.getName()) + "\","
						+ "\"TransactionLimit\":\"" + fields.get(FieldType.BENE_TRANSACTION_LIMIT.getName()) + "\","
						+ "\"BankName\": \"" + fields.get(FieldType.BANK_NAME.getName()) + "\","
						+ "\"IfscCode\": \"" + fields.get(FieldType.IFSC_CODE.getName()) + "\","
						+ "\"BeneAccountNo\": \"" + fields.get(FieldType.BENE_ACCOUNT_NO.getName()) + "\","
						+ "\"Action\":\"" + SettlementTransactionType.VERIFY_BENEFICIARY.getCode() + "\"}}";
	}
}
