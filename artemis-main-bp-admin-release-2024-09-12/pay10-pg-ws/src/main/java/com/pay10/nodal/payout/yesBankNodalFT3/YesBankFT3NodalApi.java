package com.pay10.nodal.payout.yesBankNodalFT3;

import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.BeneficiaryAccountsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.util.AccountStatus;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.nodal.payout.SettlementTransformer;
import com.pay10.nodal.payout.Transaction;

@Service
public class YesBankFT3NodalApi {


	@Autowired
	private YesBankFT3TransactionCommunicator yesBankFT3TransactionCommunicator;
	
	@Autowired
	private YesBankFT3TransactionConverter yesBankFT3TransactionConverter;
	
	@Autowired
	private YesBankFT3ResponseHandler yesBankFT3ResponseHandler;
	
	@Autowired
	private BeneficiaryAccountsDao beneficiaryAccountsDao;
	
	private static Logger logger = LoggerFactory.getLogger(YesBankFT3NodalApi.class.getName());
	
	private SettlementTransformer settlementTransformer = null;
	
	public Fields settlementProcess(Fields fields) throws SystemException, SOAPException {
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		fields.put(FieldType.TXNTYPE.getName(), txnType); // Unnecessary
		if (txnType.equalsIgnoreCase(SettlementTransactionType.FUND_TRANSFER.getName())) {
			fundTransferIntegrator(fields);
			logger.info("Status for fund transfer : " + fields.get(FieldType.STATUS.getName()));
			if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.PROCESSING.getName())) {
				fields.put(FieldType.TXNTYPE.getName(), SettlementTransactionType.STATUS.getName());
				settlementStatusEnqIntegrator(fields);
				fields.put(FieldType.TXNTYPE.getName(), SettlementTransactionType.FUND_TRANSFER.getName());
			}
		} else if (txnType.equalsIgnoreCase(SettlementTransactionType.ADD_BENEFICIARY.getName())) {
			addBeneficiaryIntegrator(fields);
		} else if (txnType.equalsIgnoreCase(SettlementTransactionType.VERIFY_BENEFICIARY.getName())) {
			verifyBeneficiaryIntegrator(fields);
		} else if (txnType.equalsIgnoreCase(SettlementTransactionType.MODIFY_BENEFICIARY.getName())) {
			modifyBeneficiaryIntegrator(fields);
		} else if (txnType.equalsIgnoreCase(SettlementTransactionType.DISABLE_BENEFICIARY.getName())) {
			disableBeneficiaryIntegrator(fields);
		} else if (txnType.equalsIgnoreCase(SettlementTransactionType.STATUS.getName())) {
			settlementStatusEnqIntegrator(fields);
		} else if (txnType.equalsIgnoreCase(SettlementTransactionType.FUND_CONFIRMATION.getName())) {
			fundConfirmationIntegrator(fields);
		}
		return fields;
	}
	
	private Fields disableBeneficiaryIntegrator(Fields fields) {
		// TODO validate request
		
		Transaction transactionResponse = null;
		String request = yesBankFT3TransactionConverter.createDisableBeneficiaryRequest(fields);
		fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), request);
		String response = yesBankFT3TransactionCommunicator.getYesBankFT3Response(request, "Beneficiary", PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_BENE_MAINTENANCE.getValue()));
		fields.put(FieldType.INTERNAL_RESPONSE_FIELDS.getName(), response);
		if(response == null) {
			logger.error("Unable to get response from yes bank");
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
			return fields;
		}
		Map<String, String> responseMap = yesBankFT3ResponseHandler.handleAddBeneResponse(response);
		transactionResponse = yesBankFT3TransactionConverter.addBeneficiaryToTransaction(responseMap);
		settlementTransformer = new SettlementTransformer(transactionResponse);
		settlementTransformer.updateResponse(fields);
		if(transactionResponse.getStatus().equalsIgnoreCase("SUCCESS") || (transactionResponse.getStatus().equalsIgnoreCase("FAILURE") && transactionResponse.getResponeMessage().equalsIgnoreCase("Record already disabled."))) {
			List<BeneficiaryAccounts> list = beneficiaryAccountsDao.findByBeneficiaryCdAndStatusAndPaymentTypeAndPayId(fields.get(FieldType.BENEFICIARY_CD.getName()), fields.get(FieldType.PAY_ID.getName()), fields.get(FieldType.PAYMENT_TYPE.getName()), AccountStatus.ACTIVE.getName());
			if(list.size() != 1) {
				logger.error("Active Beneficiaries records are more or less than 1.");
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.DATABASE_ERROR.getInternalMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DATABASE_ERROR.getCode());
				return fields;
			}
			
			BeneficiaryAccounts beneAccounts = list.get(0);
			if(beneAccounts == null) {
				logger.error("No record found in DB");
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DATABASE_ERROR.getResponseMessage());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.DATABASE_ERROR.getCode());
				return fields;
			}
			logger.info("Updating DB.");
			if(!(StringUtils.isEmpty(transactionResponse.getUniqueResponseNo())))
				beneAccounts.setRrn(transactionResponse.getUniqueResponseNo());
			beneAccounts.setStatus(AccountStatus.INACTIVE);
			
			if(StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName()))) {
				beneAccounts.setBankRequest(fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName()));
			}
			
			if(StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_RESPONSE_FIELDS.getName()))) {
				beneAccounts.setBankResponse(fields.get(FieldType.INTERNAL_RESPONSE_FIELDS.getName()));
			}
			
			beneficiaryAccountsDao.update(beneAccounts);
		}
		fields.logAllFields("Disable beneficiary integrator fields : ");
		return fields;
	}

	private Fields modifyBeneficiaryIntegrator(Fields fields) {
		// TODO validate request
		Transaction transactionResponse;
		String request = yesBankFT3TransactionConverter.createModifyBeneficiaryRequest(fields);
		fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), request);
		String response = yesBankFT3TransactionCommunicator.getYesBankFT3Response(request, "Beneficiary", PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_BENE_MAINTENANCE.getValue()));
		fields.put(FieldType.INTERNAL_RESPONSE_FIELDS.getName(), response);
		if(response == null) {
			logger.error("Unable to get response from yes bank");
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
			return fields;
		}
		Map<String, String> responseMap = yesBankFT3ResponseHandler.handleAddBeneResponse(response);
		transactionResponse = yesBankFT3TransactionConverter.addBeneficiaryToTransaction(responseMap);
//		fields.put("Response", response.toString());
		settlementTransformer = new SettlementTransformer(transactionResponse);
		settlementTransformer.updateResponse(fields);
		if(transactionResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
			List<BeneficiaryAccounts> list = beneficiaryAccountsDao.findByBeneficiaryCdAndStatusAndPaymentTypeAndPayId(fields.get(FieldType.BENEFICIARY_CD.getName()), fields.get(FieldType.PAY_ID.getName()), fields.get(FieldType.PAYMENT_TYPE.getName()), "ALL");
			
			if(list.size() != 1) {
				logger.error("Active Beneficiaries records are more or less than 1.");
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.DATABASE_ERROR.getInternalMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DATABASE_ERROR.getCode());
				return fields;
			}
			
			BeneficiaryAccounts beneAccounts = list.get(0);
			if(beneAccounts == null) {
				logger.error("No record found in DB");
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.DATABASE_ERROR.getInternalMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DATABASE_ERROR.getCode());
				return fields;
			}
			
			logger.info("Updating DB.");
			beneAccounts.setRrn(transactionResponse.getUniqueResponseNo());
			beneAccounts.setStatus(AccountStatus.ACTIVE);
			if(StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName()).toString())) {
				beneAccounts.setBankRequest(fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName()));
			}
			if(StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_RESPONSE_FIELDS.getName()).toString())) {
				beneAccounts.setBankResponse(fields.get(FieldType.INTERNAL_RESPONSE_FIELDS.getName()));
			}
			beneficiaryAccountsDao.update(beneAccounts);
		}
		fields.logAllFields("Modify beneficiary integrator fields : ");
		return fields;
	}

	private Fields verifyBeneficiaryIntegrator(Fields fields) {
		// TODO validate request
		Transaction transactionResponse = null;
		String request = yesBankFT3TransactionConverter.createVerifyBeneficiaryRequest(fields);
		fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), request);
		String response = yesBankFT3TransactionCommunicator.getYesBankFT3Response(request, "Beneficiary", PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_BENE_MAINTENANCE.getValue()));
		fields.put(FieldType.INTERNAL_RESPONSE_FIELDS.getName(), response);
		if(response == null) {
			logger.error("Unable to get response from yes bank");
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
			return fields;
		}
		Map<String, String> responseMap = yesBankFT3ResponseHandler.handleAddBeneResponse(response);
		transactionResponse = yesBankFT3TransactionConverter.addBeneficiaryToTransaction(responseMap);
		settlementTransformer = new SettlementTransformer(transactionResponse);
		settlementTransformer.updateResponse(fields);
		return fields;
	}
	
	public Fields addBeneficiaryIntegrator(Fields fields) throws SystemException {
		// TODO: Validate request
		Transaction transactionResponse;
		String request = yesBankFT3TransactionConverter.createAddBeneficiaryRequest(fields);
		fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), request);
		String response = yesBankFT3TransactionCommunicator.getYesBankFT3Response(request, "Beneficiary", PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_BENE_MAINTENANCE.getValue()));
		fields.put(FieldType.INTERNAL_RESPONSE_FIELDS.getName(), response);
		if(response == null) {
			logger.error("Unable to get response from yes bank");
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
			return fields;
		}
		Map<String, String> responseMap = yesBankFT3ResponseHandler.handleAddBeneResponse(response);
		transactionResponse = yesBankFT3TransactionConverter.addBeneficiaryToTransaction(responseMap);
		settlementTransformer = new SettlementTransformer(transactionResponse);
		settlementTransformer.updateResponse(fields);
		fields.logAllFields("Add Beneficiary integrator fields : ");
		return fields;
	}

	public Fields settlementStatusEnqIntegrator(Fields fields) throws SystemException {
		Transaction transactionResponse;
		String request = yesBankFT3TransactionConverter.getStatusRequest(fields);
		fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), request);
		String response = yesBankFT3TransactionCommunicator.getYesBankFT3Response(request, "", PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_PAYMENT_DETAILS.getValue()));
		fields.put(FieldType.INTERNAL_RESPONSE_FIELDS.getName(), response);
		Map<String, String> responseMap = yesBankFT3ResponseHandler.handleStatusEnquiryResponse(response);
		transactionResponse = yesBankFT3TransactionConverter.statusToTransaction(responseMap);
		settlementTransformer = new SettlementTransformer(transactionResponse);
		settlementTransformer.updateResponse(fields);
		fields.logAllFields("Nodal Settlement Status enquiry integrator fields : ");
		return fields;
	}


	
	public void fundConfirmationIntegrator(Fields fields) throws SystemException {
		String request = yesBankFT3TransactionConverter.fundConfirmationRequest(fields);
		String response = yesBankFT3TransactionCommunicator.getYesBankFT3Response(request, "", PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_FUND_CONFIRMATION.getValue()));
		Map<String, String> responseMap = yesBankFT3ResponseHandler.handleFundConfirmationResponse(response);
		// TODO Add currency code and handle response properly.
		fields.put("response", responseMap.get("Result"));
		if(responseMap.containsKey("balance")) {
			fields.put("balance", responseMap.get("balance"));
		}
	}

	
	public void fundTransferIntegrator(Fields fields) throws SystemException {
		Transaction transactionResponse;
		
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		fields.put(FieldType.OID.getName(), newTxnId);
		
		String request = yesBankFT3TransactionConverter.fundTransferRequest(fields);
		fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), request);
		String response = yesBankFT3TransactionCommunicator.getYesBankFT3Response(request, "", PropertiesManager.propertiesMap.get(Constants.YES_BANK_NODAL_FT3_DOMESTIC_PAYMENT.getValue()));
		fields.put(FieldType.INTERNAL_RESPONSE_FIELDS.getName(), response);
		Map<String, String> responseMap = yesBankFT3ResponseHandler.handleFundTransferResponse(response);
		// TODO handle response for recieved/duplicate and error code
		transactionResponse = yesBankFT3TransactionConverter.toTransaction(responseMap);
		settlementTransformer = new SettlementTransformer(transactionResponse);
		settlementTransformer.updateResponse(fields);
		fields.logAllFields("Fund Transfer Intrgrator Fields : ");
	}
}
