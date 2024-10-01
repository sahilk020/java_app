package com.pay10.commons.util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.dao.SuccessRateThresholdSetting;
import com.pay10.commons.dto.DashboardReportCount;
import com.pay10.commons.dto.ReportingCollection;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.user.CustomerAddress;
import com.pay10.commons.user.ExceptionReport;
import com.pay10.commons.user.GstRSaleReport;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.LoginHistory;
import com.pay10.commons.user.Menu;
import com.pay10.commons.user.MerchantDetails;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.NodalAmount;
import com.pay10.commons.user.RefundPreview;
import com.pay10.commons.user.SearchTransaction;
import com.pay10.commons.user.SearchUser;
import com.pay10.commons.user.StatusEnquiryObject;
import com.pay10.commons.user.TDRBifurcationReportDetails;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.TransactionSummaryReport;

/**
 * @author Puneet, Neeraj
 *
 */
@Service
public class DataEncoder {

	private static Logger logger = LoggerFactory.getLogger(DataEncoder.class.getName());

	public static String encodeString(String data) {
		return ESAPI.encoder().encodeForHTML(data);
	}

	public byte[] getByteStream(String data) throws SystemException {
		try {
			return data.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Encoding error");
		}
	}

	public List<MerchantDetails> encodeMerchantDetailsObj(List<MerchantDetails> merchants) {
		logger.info("encodeMerchantDetailsObj"+merchants);
		// TODO.. code commented for live deployment
		for (MerchantDetails merchant : merchants) {
			merchant.setBusinessName(encodeString(merchant.getBusinessName()));
			merchant.setPayId(encodeString(merchant.getPayId()));
			// merchant.setResellerId(encodeString(merchant.getResellerId()));
			merchant.setEmailId(encodeString(merchant.getEmailId()));
			merchant.setMobile(encodeString(merchant.getMobile()));
			// merchant.setUserType(encodeString(merchant.getUserType()));
		}
		return merchants;
	}

	public List<Merchants> encodeMerchantObj(List<Merchants> merchants) {
		// TODO.. code commented for live deployment
		for (Merchants merchant : merchants) {
			merchant.setBusinessName(encodeString(merchant.getBusinessName()));
			merchant.setPayId(encodeString(merchant.getPayId()));
			// merchant.setResellerId(encodeString(merchant.getResellerId()));
			merchant.setEmailId(encodeString(merchant.getEmailId()));
			merchant.setMobile(encodeString(merchant.getMobile()));
			// merchant.setUserType(encodeString(merchant.getUserType()));
		}
		return merchants;
	}

	public List<SuccessRateThresholdSetting> encodeSuccessRateThresholdSettingObj(
			List<SuccessRateThresholdSetting> settings) {
		for (SuccessRateThresholdSetting setting : settings) {

			setting.setAcquirerName(encodeString(setting.getAcquirerName()));
			setting.setPaymentType(encodeString(setting.getPaymentType()));
			setting.setMopType(encodeString(setting.getMopType()));
			setting.setSuccessRate(Integer.parseInt(encodeString(String.valueOf(setting.getSuccessRate()))));
		}
		return settings;
	}

	public List<GstRSaleReport> encodegstRSaleReport(List<GstRSaleReport> gstRsaleactions) {

		for (GstRSaleReport gstRsaleaction : gstRsaleactions) {
			// gstRsaleaction.setAcquirer(encodeString(gstRsaleaction.getac));
			/*
			 * transaction.setApprovedAmount(encodeString(transaction
			 * .getApprovedAmount())); transaction.setBusinessName(encodeString(transaction
			 * .getBusinessName())); transaction.setCaptureTxnId(encodeString(transaction
			 * .getCaptureTxnId()));
			 * transaction.setCardNo(encodeString(transaction.getCardNo()));
			 * transaction.setChargebackAmount(encodeString(transaction
			 * .getChargebackAmount()));
			 * transaction.setCurrencyCode(encodeString(transaction .getCurrencyCode()));
			 * transaction.setCustomerEmail(encodeString(transaction .getCustomerEmail()));
			 * transaction.setCustomerName(encodeString(transaction .getCustomerName()));
			 * transaction.setInternalRequestDesc(encodeString(transaction
			 * .getInternalRequestDesc()));
			 * transaction.setMopType(encodeString(transaction.getMopType()));
			 * transaction.setNetAmount(encodeString(transaction.getNetAmount()));
			 * transaction.setOid(encodeString(transaction.getOid()));
			 * transaction.setOrderId(encodeString(transaction.getOrderId()));
			 * transaction.setOrigTransactionId(encodeString(transaction
			 * .getOrigTransactionId()));
			 * transaction.setOrigTxnDate(encodeString(transaction .getOrigTxnDate()));
			 * transaction.setPayId(encodeString(transaction.getPayId()));
			 * transaction.setPaymentMethod(encodeString(transaction .getPaymentMethod()));
			 * transaction.setProductDesc(encodeString(transaction .getProductDesc()));
			 * transaction .setRefundDate(encodeString(transaction.getRefundDate()));
			 * transaction.setRefundedAmount(encodeString(transaction
			 * .getRefundedAmount())); transaction.setResponseMsg(encodeString(transaction
			 * .getResponseMsg())); transaction
			 * .setServiceTax(encodeString(transaction.getServiceTax())); transaction
			 * .setSettleDate(encodeString(transaction.getSettleDate()));
			 * transaction.setStatus(encodeString(transaction.getStatus()));
			 * transaction.setTdr(encodeString(transaction.getTdr()));
			 * transaction.setTransactionId(encodeString(transaction .getTransactionId()));
			 * transaction.setTxnDate(encodeString(transaction.getTxnDate()));
			 * transaction.setTxnType(encodeString(transaction.getTxnType()));
			 * transaction.setInternalCardIssusserBank(encodeString(transaction.
			 * getInternalCardIssusserBank()));
			 * transaction.setInternalCardIssusserCountry(encodeString(transaction.
			 * getInternalCardIssusserCountry()));
			 * transaction.setPgTxnMessage(encodeString(transaction.getPgTxnMessage()));
			 * transaction.setPayId(encodeString(transaction.getPayId()));
			 * transaction.setAggregatorName(encodeString(transaction.getAggregatorName()));
			 */

		}
		return gstRsaleactions;
	}

	public List<TransactionSummaryReport> encodeTransactionSummary(List<TransactionSummaryReport> transactions) {

		for (TransactionSummaryReport transaction : transactions) {
			transaction.setAcquirer(encodeString(transaction.getAcquirer()));
			transaction.setApprovedAmount(encodeString(transaction.getApprovedAmount()));
			transaction.setBusinessName(encodeString(transaction.getBusinessName()));
			transaction.setCaptureTxnId(encodeString(transaction.getCaptureTxnId()));
			transaction.setCardNo(encodeString(transaction.getCardNo()));
			transaction.setChargebackAmount(encodeString(transaction.getChargebackAmount()));
			transaction.setCurrencyCode(encodeString(transaction.getCurrencyCode()));
			transaction.setCustomerEmail(encodeString(transaction.getCustomerEmail()));
			transaction.setCustomerName(encodeString(transaction.getCustomerName()));
			transaction.setInternalRequestDesc(encodeString(transaction.getInternalRequestDesc()));
			transaction.setMopType(encodeString(transaction.getMopType()));
			transaction.setNetAmount(encodeString(transaction.getNetAmount()));
			transaction.setOid(encodeString(transaction.getOid()));
			transaction.setOrderId(encodeString(transaction.getOrderId()));
			transaction.setOrigTransactionId(encodeString(transaction.getOrigTransactionId()));
			transaction.setOrigTxnDate(encodeString(transaction.getOrigTxnDate()));
			transaction.setPayId(encodeString(transaction.getPayId()));
			transaction.setPaymentMethod(encodeString(transaction.getPaymentMethod()));
			transaction.setProductDesc(encodeString(transaction.getProductDesc()));
			transaction.setRefundDate(encodeString(transaction.getRefundDate()));
			transaction.setRefundedAmount(encodeString(transaction.getRefundedAmount()));
			transaction.setResponseMsg(encodeString(transaction.getResponseMsg()));
			transaction.setServiceTax(encodeString(transaction.getServiceTax()));
			transaction.setSettleDate(encodeString(transaction.getSettleDate()));
			transaction.setStatus(encodeString(resolveStatus(transaction.getStatus())));
			transaction.setTdr(encodeString(transaction.getTdr()));
			transaction.setTransactionId(encodeString(transaction.getTransactionId()));
			transaction.setTxnDate(encodeString(transaction.getTxnDate()));
			transaction.setTxnType(encodeString(transaction.getTxnType()));
			transaction.setInternalCardIssusserBank(encodeString(transaction.getInternalCardIssusserBank()));
			transaction.setInternalCardIssusserCountry(encodeString(transaction.getInternalCardIssusserCountry()));
			transaction.setPgTxnMessage(encodeString(transaction.getPgTxnMessage()));
			transaction.setPayId(encodeString(transaction.getPayId()));
			transaction.setAggregatorName(encodeString(transaction.getAggregatorName()));
		}
		return transactions;
	}

	public List<TransactionSearch> encodeTransactionSearchObj(List<TransactionSearch> transactions) {

		try {
			for (TransactionSearch transaction : transactions) {
				transaction.setTransactionIdString(encodeString(transaction.getTransactionIdString()));
				transaction.setAmount(encodeString(transaction.getAmount()));
				transaction.setCardNumber(encodeString(transaction.getCardNumber()));
				transaction.setCurrency(encodeString(transaction.getCurrency()));
				transaction.setCustomerEmail(encodeString(transaction.getCustomerEmail()));
				transaction.setCustomerPhone(encodeString(transaction.getCustomerPhone()));
				transaction.setCustomerName(encodeString(transaction.getCustomerName()));
				transaction.setMerchants(encodeString(transaction.getMerchants()));
				transaction.setOrderId(encodeString(transaction.getOrderId()));
				transaction.setPayId(encodeString(transaction.getPayId()));
				transaction.setPaymentMethods(encodeString(transaction.getPaymentMethods()));
				transaction.setProductDesc(encodeString(transaction.getProductDesc()));
				transaction.setStatus(encodeString(resolveStatus(transaction.getStatus())));
				transaction.setTxnType(encodeString(transaction.getTxnType()));
				transaction.setMopType(encodeString(transaction.getMopType()));
				transaction.setInternalCardIssusserBank(transaction.getInternalCardIssusserBank());
				transaction.setInternalCardIssusserCountry(transaction.getInternalCardIssusserCountry());
				transaction.setPgTdrSc(transaction.getPgTdrSc());

			}
		}

		catch (Exception e) {

			logger.error("Exception in encodeTransactionSearchObj , Exception =  ", e);
		}

		return transactions;
	}

	public String resolveStatus(String status) {
//		logger.info("resolveStatus : {}",status);
		if (StringUtils.isNotBlank(status) &&  (status.equalsIgnoreCase(StatusType.CAPTURED.getName()) || status.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName()) || status.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())  || status.equalsIgnoreCase(StatusType.FAILED.getName())|| status.equalsIgnoreCase(StatusType.CHARGEBACK_INITIATED.getName())
				|| status.equalsIgnoreCase(StatusType.CHARGEBACK_REVERSAL.getName()))) {
//			logger.info("resolveStatus Return: {}", status);
			return status;
		} else if(StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.DENIED.getName()))){
//			logger.info("resolveStatus Return: {}", "REQUEST ACCEPTED");
			return "REQUEST ACCEPTED";
		}else if(StringUtils.isNotBlank(status) && (status.equalsIgnoreCase("REQUEST ACCEPTED") || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName()))){
//			logger.info("resolveStatus Return: {}", "REQUEST ACCEPTED");
			return "REQUEST ACCEPTED";
		}else {
//			logger.info("resolveStatus Return: {}",StatusType.FAILED.getName());
			return StatusType.FAILED.getName();
		}
	}

	public List<TDRBifurcationReportDetails> tdrBifurcationreport(List<TDRBifurcationReportDetails> transactions) {

		try {
			for (TDRBifurcationReportDetails transaction : transactions) {
				transaction.setTxnId(encodeString(transaction.getTxnId()));
				transaction.setPgRefNo(encodeString(transaction.getPgRefNo()));
				transaction.setMerchantName(encodeString(transaction.getMerchantName()));
				transaction.setAcquirer(encodeString(transaction.getAcquirer()));
				transaction.setDate(encodeString(transaction.getDate()));
				transaction.setSettlementDate(encodeString(transaction.getSettlementDate()));
				transaction.setOrderId(encodeString(transaction.getOrderId()));
				transaction.setPaymentMethod(encodeString(transaction.getPaymentMethod()));
				transaction.setTxnType(encodeString(transaction.getTxnType()));
				transaction.setStatus(encodeString(resolveStatus(transaction.getStatus())));
				transaction.setTransactionRegion(encodeString(transaction.getTransactionRegion()));
				transaction.setBaseAmount(encodeString(transaction.getBaseAmount()));
				transaction.setTotalAmount(encodeString(transaction.getTotalAmount()));
				transaction.setAcqId(encodeString(transaction.getAcqId()));
				transaction.setRRN(encodeString(transaction.getRRN()));
				transaction.setRefundOrderId(encodeString(transaction.getRefundOrderId()));
				transaction.setRefundFlag(encodeString(transaction.getRefundFlag()));
				transaction.setMopType(encodeString(transaction.getMopType()));
				//transaction.setAcquirerTDRFixed(encodeString(transaction.getAcquirerTDRFixed()));
				//transaction.setAcquirerTDRPercentage(encodeString(transaction.getAcquirerTDRPercentage()));
				//transaction.setAcquirerTDRAmontTotal(encodeString(transaction.getAcquirerTDRAmontTotal()));
				//transaction.setGstOnAcquirerTDR(encodeString(transaction.getGstOnAcquirerTDR()));
				//transaction.setPgTDRFixed(encodeString(transaction.getPgTDRFixed()));
				//transaction.setPgTDRPercentage(encodeString(transaction.getPgTDRPercentage()));
				//transaction.setPgTDRAmontTotal(encodeString(transaction.getPgTDRAmontTotal()));
				//transaction.setGstOnPgTDR(encodeString(transaction.getGstOnPgTDR()));
				//transaction.setMerchantTDRFixed(encodeString(transaction.getMerchantTDRFixed()));
				//transaction.setMerchantTDRPercentage(encodeString(transaction.getMerchantTDRPercentage()));
				//transaction.setTdr(encodeString(transaction.getTdr()));
				
				transaction.setMerchantPreference(encodeString(transaction.getMerchantPreference()));
				transaction.setMerchantTDR(encodeString(transaction.getMerchantTDR()));
				transaction.setMerchantMinTdramount(encodeString(transaction.getMerchantMinTdramount()));
				transaction.setMerchantMaxTdramount(encodeString(transaction.getMerchantMaxTdramount()));
				transaction.setBankPreference(encodeString(transaction.getBankPreference()));
				transaction.setBankTDR(encodeString(transaction.getBankTDR()));
				transaction.setBankMinTdrAmount(encodeString(transaction.getBankMinTdrAmount()));
				transaction.setBankMaxTdrAmount(encodeString(transaction.getBankMaxTdrAmount()));
				transaction.setBankTdrInAMOUNT(encodeString(transaction.getBankTdrInAMOUNT()));
				transaction.setBankGstInAmount(encodeString(transaction.getBankGstInAmount()));
				transaction.setPgTdrInAmount(encodeString(transaction.getPgTdrInAmount()));
				transaction.setPgGstInAmount(encodeString(transaction.getPgGstInAmount()));
				
				
				transaction.setIgst18(encodeString(transaction.getIgst18()));
				transaction.setCgst9(encodeString(transaction.getCgst9()));
				transaction.setSgst9(encodeString(transaction.getSgst9()));
				transaction.setSurchargeFlag(encodeString(transaction.getSurchargeFlag()));
				transaction.setAmountPaybleToMerchant(encodeString(transaction.getAmountPaybleToMerchant()));
				transaction.setAmountreceivedInNodal(encodeString(transaction.getAmountreceivedInNodal()));
				transaction.setAmountReceivedNodalBank(encodeString(transaction.getAmountReceivedNodalBank()));
				transaction.setAmountReceivedNodalBank(encodeString(transaction.getSettlementTat()));
				transaction.setAccountHolderName(encodeString(transaction.getAccountHolderName()));
				transaction.setAccountNumber(encodeString(transaction.getAccountNumber()));
				transaction.setIfscCode(encodeString(transaction.getIfscCode()));
				transaction.setTransactionIdentifer(encodeString(transaction.getTransactionIdentifer()));
				transaction.setLiabilityHoldRemakrs(encodeString(transaction.getLiabilityHoldRemakrs()));
				transaction.setLiabilityReleaseRemakrs(encodeString(transaction.getLiabilityReleaseRemakrs()));
				transaction.setUtrNumber(encodeString(transaction.getUtrNumber()));
				transaction.setSettlementPeriod(encodeString(transaction.getSettlementPeriod()));

			}
		}catch (Exception e) {

			logger.error("Exception in encodeTransactionSearchObj , Exception =  " , e);
			e.printStackTrace();
		}

		return transactions;
	}

	public List<TransactionSearchNew> encodeTransactionSearchObjNew(List<TransactionSearchNew> transactions) {

		try {
			for (TransactionSearchNew transaction : transactions) {
//				transaction.setTransactionIdString(encodeString(transaction.getTransactionIdString()));
				transaction.setAmount(encodeString(transaction.getAmount()));
				transaction.setCardNumber(encodeString(transaction.getCardNumber()));
				transaction.setCurrency(encodeString(transaction.getCurrency()));
				transaction.setCustomerEmail(encodeString(transaction.getCustomerEmail()));
				transaction.setCustomerPhone(encodeString(transaction.getCustomerPhone()));
				transaction.setCustomerName(encodeString(transaction.getCustomerName()));
				transaction.setMerchants(encodeString(transaction.getMerchants()));
				transaction.setOrderId(encodeString(transaction.getOrderId()));
				transaction.setPayId(encodeString(transaction.getPayId()));
				transaction.setPaymentMethods(encodeString(transaction.getPaymentMethods()));
				transaction.setStatus(encodeString(resolveStatus(transaction.getStatus())));
				transaction.setTxnType(encodeString(transaction.getTxnType()));
				transaction.setMopType(encodeString(transaction.getMopType()));
				transaction.setIpaddress(encodeString(transaction.getIpaddress()));
				transaction.setUpdatedAt(encodeString(transaction.getUpdatedAt()));
				transaction.setUpdatedBy(encodeString(transaction.getUpdatedBy()));
			}
		}

		catch (Exception e) {

			logger.error("Exception in encodeTransactionSearchObj , Exception =  " , e);
		}

		return transactions;
	}

	public DashboardReportCount encodeDashboardData(DashboardReportCount transactions) {

		try {

			transactions.setExceptionCount(Double.parseDouble(encodeString("" + transactions.getExceptionCount())));
			transactions
					.setForceCaptureCount(Double.parseDouble(encodeString("" + transactions.getForceCaptureCount())));
			transactions.setRnsCount(Double.parseDouble(encodeString("" + transactions.getRnsCount())));

		}

		catch (Exception e) {

			logger.error("Exception in encodeTransactionSearchObj , Exception =  " , e);
		}

		return transactions;
	}

	public List<Invoice> encodeInvoiceSearchObj(List<Invoice> invoices) {
		for (Invoice invoice : invoices) {
			invoice.setAddress(encodeString(invoice.getAddress()));
			invoice.setAmount(encodeString(invoice.getAmount()));
			invoice.setBusinessName(encodeString(invoice.getBusinessName()));
			invoice.setCountry(encodeString(invoice.getCountry()));
			invoice.setCurrencyCode(encodeString(invoice.getCurrencyCode()));
			invoice.setEmail(encodeString(invoice.getEmail()));
			invoice.setExpiresDay(encodeString(invoice.getExpiresDay()));
			invoice.setExpiresHour(encodeString(invoice.getExpiresDay()));
			invoice.setInvoiceId(encodeString(invoice.getInvoiceId()));
			invoice.setInvoiceNo(encodeString(invoice.getInvoiceNo()));
			invoice.setName(encodeString(invoice.getName()));
			invoice.setPayId(encodeString(invoice.getPayId()));
			invoice.setPhone(encodeString(invoice.getPhone()));
			invoice.setProductDesc(encodeString(invoice.getProductDesc()));
			invoice.setProductName(encodeString(invoice.getProductName()));
			invoice.setQuantity(encodeString(invoice.getQuantity()));
			invoice.setReturnUrl(encodeString(invoice.getReturnUrl()));
			invoice.setSaltKey(encodeString(invoice.getSaltKey()));
			invoice.setServiceCharge(encodeString(invoice.getServiceCharge()));
			invoice.setState(encodeString(invoice.getState()));
			invoice.setTotalAmount(encodeString(invoice.getTotalAmount()));
			invoice.setZip(encodeString(invoice.getZip()));
			invoice.setQuantity(encodeString(invoice.getQuantity()));
			invoice.setCity(encodeString(invoice.getCity()));
		}
		return invoices;
	}

	public List<LoginHistory> encodeLoginHistoryObj(List<LoginHistory> histories) {
		for (LoginHistory loginHistory : histories) {
			String loginDate = loginHistory.getTimeStamp();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date changedate = null;
			try {
				changedate = simpleDateFormat.parse(loginDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String formattedDate = sdf.format(changedate);

			loginHistory.setBrowser(encodeString(loginHistory.getBrowser()));
			loginHistory.setBusinessName(encodeString(loginHistory.getBusinessName()));
			loginHistory.setEmailId(encodeString(loginHistory.getEmailId()));
			loginHistory.setFailureReason(encodeString(loginHistory.getFailureReason()));
			loginHistory.setIp(encodeString(loginHistory.getIp()));
			loginHistory.setOs(encodeString(loginHistory.getOs()));
		//	loginHistory.setTimeStamp(formattedDate);
			loginHistory.setTimeStamp(loginHistory.getTimeStamp());
			loginHistory.setId(loginHistory.getId());
		}
		return histories;
	}

	public List<Merchants> encodeMerchantsObj(List<Merchants> merchants) {
		for (Merchants merchant : merchants) {
			merchant.setBusinessName(encodeString(merchant.getBusinessName()));
			merchant.setEmailId(encodeString(merchant.getEmailId()));
			merchant.setFirstName(encodeString(merchant.getFirstName()));
			merchant.setLastName(encodeString(merchant.getLastName()));
			merchant.setMobile(encodeString(merchant.getMobile()));
			merchant.setPayId(encodeString(merchant.getPayId()));
		}
		return merchants;
	}

	public List<SubAdmin> encodeAgentsObj(List<SubAdmin> agents) {
		for (SubAdmin subAdmin : agents) {

			subAdmin.setAgentEmailId(encodeString(subAdmin.getAgentEmailId()));
			subAdmin.setAgentFirstName(encodeString(subAdmin.getAgentFirstName()));
			subAdmin.setAgentLastName(encodeString(subAdmin.getAgentLastName()));
			subAdmin.setAgentMobile(encodeString(subAdmin.getAgentMobile()));
			subAdmin.setPayId(encodeString(subAdmin.getPayId()));
		}
		return agents;
	}

	public List<Tenant> encodeTenantsObj(List<Tenant> tenants) {
		for (Tenant tenant : tenants) {

			tenant.setTenantId(encodeString(tenant.getTenantId()));
			tenant.setTenantCompanyName(encodeString(tenant.getTenantCompanyName()));
			tenant.setTenantMobile(encodeString(tenant.getTenantMobile()));
			tenant.setTenantEmailId(encodeString(tenant.getTenantEmailId()));
			tenant.setTenantPgUrl(encodeString(tenant.getTenantPgUrl()));
			tenant.setTenantStatus(encodeString(tenant.getTenantStatus()));
		}
		return tenants;
	}

	public List<SubSuperAdmin> encodeSubSuperAdminsObj(List<SubSuperAdmin> agents) {
		for (SubSuperAdmin subAdmin : agents) {

			subAdmin.setAgentEmailId(encodeString(subAdmin.getAgentEmailId()));
			subAdmin.setAgentFirstName(encodeString(subAdmin.getAgentFirstName()));
			subAdmin.setAgentLastName(encodeString(subAdmin.getAgentLastName()));
			subAdmin.setAgentMobile(encodeString(subAdmin.getAgentMobile()));
			subAdmin.setPayId(encodeString(subAdmin.getPayId()));
		}
		return agents;
	}

	public List<Agent> encodenewAgentsObj(List<Agent> agents) {
		for (Agent agent : agents) {

			agent.setAgentEmailId(encodeString(agent.getAgentEmailId()));
			agent.setAgentFirstName(encodeString(agent.getAgentFirstName()));
			agent.setAgentLastName(encodeString(agent.getAgentLastName()));
			agent.setAgentMobile(encodeString(agent.getAgentMobile()));
			agent.setPayId(encodeString(agent.getPayId()));
		}
		return agents;
	}

	public List<Acquirer> encodeAcquirersObj(List<Acquirer> acquirers) {
		for (Acquirer acquirer : acquirers) {

			acquirer.setAcquirerEmailId(encodeString(acquirer.getAcquirerEmailId()));
			acquirer.setAcquirerFirstName(encodeString(acquirer.getAcquirerFirstName()));
			acquirer.setAcquirerLastName(encodeString(acquirer.getAcquirerLastName()));
			acquirer.setAcquirerBusinessName(encodeString(acquirer.getAcquirerBusinessName()));
			acquirer.setPayId(encodeString(acquirer.getPayId()));
			acquirer.setAcquirerAccountNo(encodeString(acquirer.getAcquirerAccountNo()));
		}
		return acquirers;
	}

	public List<NodalAmount> encodeNodalObj(List<NodalAmount> nodalList) {
		for (NodalAmount nodal : nodalList) {

			nodal.setAcquirer(nodal.getAcquirer());
			nodal.setPaymentType(nodal.getPaymentType());
			nodal.setNodalCreditAmount(nodal.getNodalCreditAmount());
			nodal.setReconDate((nodal.getReconDate()));
			nodal.setCreateDate(nodal.getCreateDate());

		}
		return nodalList;
	}

	public NodalAmount encodeNodalObjData(NodalAmount nodalAmount) {

		NodalAmount nodal = new NodalAmount();
		nodal.setAcquirer(nodalAmount.getAcquirer());
		nodal.setPaymentType(nodalAmount.getPaymentType());
		nodal.setNodalCreditAmount(nodalAmount.getNodalCreditAmount());
		nodal.setReconDate((nodalAmount.getReconDate()));

		return nodal;
	}

	public List<SearchUser> encodeSearchUserObj(List<SearchUser> users) {
		for (SearchUser searchUser : users) {
			searchUser.setEmailId(encodeString(searchUser.getEmailId()));
			searchUser.setFirstName(encodeString(searchUser.getFirstName()));
			searchUser.setLastName(encodeString(searchUser.getLastName()));
			searchUser.setPhone(encodeString(searchUser.getPhone()));
		}
		return users;
	}

	public CustomerAddress encodeCustomerAddressObj(CustomerAddress customerAddress) {

		customerAddress.setCustCity(encodeString(customerAddress.getCustCity()));
		customerAddress.setCustCountry(encodeString(customerAddress.getCustCountry()));
		customerAddress.setCustName(encodeString(customerAddress.getCustName()));
		customerAddress.setCustPhone(encodeString(customerAddress.getCustPhone()));
		customerAddress.setCustShipCity(encodeString(customerAddress.getCustShipCity()));
		customerAddress.setCustShipName(encodeString(customerAddress.getCustShipName()));
		customerAddress.setCustShipCountry(encodeString(customerAddress.getCustShipCountry()));
		customerAddress.setCustShipState(encodeString(customerAddress.getCustShipState()));
		customerAddress.setCustShipStreetAddress1(encodeString(customerAddress.getCustShipStreetAddress1()));
		customerAddress.setCustShipStreetAddress2(encodeString(customerAddress.getCustShipStreetAddress2()));
		customerAddress.setCustShipZip(encodeString(customerAddress.getCustShipZip()));
		customerAddress.setCustState(encodeString(customerAddress.getCustState()));
		customerAddress.setCustStreetAddress1(encodeString(customerAddress.getCustStreetAddress1()));
		customerAddress.setCustStreetAddress2(encodeString(customerAddress.getCustStreetAddress2()));
		customerAddress.setCustZip(encodeString(customerAddress.getCustZip()));

		String cardMask = null;
		try {
			cardMask = maskString(customerAddress.getCardMask(), 4, customerAddress.getCardMask().length() - 4, '*');
			logger.info("customerAddress.getCardMask()=========" + customerAddress.getCardMask() + "cardMask========"
					+ cardMask);
			customerAddress.setCardMask(cardMask);
		} catch (Exception e) {
			e.printStackTrace();
		}

		customerAddress.setAcquirerType(encodeString(customerAddress.getAcquirerType()));
		customerAddress.setPgTdr(encodeString(customerAddress.getPgTdr()));
		customerAddress.setPgGst(encodeString(customerAddress.getPgGst()));
		customerAddress.setAcquirerTdr(encodeString(customerAddress.getAcquirerTdr()));
		customerAddress.setAcquirerGst(encodeString(customerAddress.getAcquirerGst()));
		customerAddress.setCustZip(encodeString(customerAddress.getCustZip()));
		customerAddress.setIssuer(encodeString(customerAddress.getIssuer()));
		customerAddress.setMopType(encodeString(customerAddress.getMopType()));
		customerAddress.setPgTxnMsg(encodeString(customerAddress.getPgTxnMsg()));
		return customerAddress;

	}

	private static String maskString(String strText, int start, int end, char maskChar) throws Exception {

		if (strText == null || strText.equals(""))
			return "";

		if (start < 0)
			start = 0;

		if (end > strText.length())
			end = strText.length();

		if (start > end)
			throw new Exception("End index cannot be greater than start index");

		int maskLength = end - start;

		if (maskLength == 0)
			return strText;

		StringBuilder sbMaskString = new StringBuilder(maskLength);

		for (int i = 0; i < maskLength; i++) {
			sbMaskString.append(maskChar);
		}
		return strText.substring(0, start) + sbMaskString.toString() + strText.substring(start + maskLength);
	}

	public List<BinRange> encodeBinRange(List<BinRange> binRangDisplay) {
		for (BinRange binRange : binRangDisplay) {

			binRange.setBinCodeLow(encodeString(binRange.getBinCodeLow()));
			binRange.setBinCodeHigh(encodeString(binRange.getBinCodeHigh()));
			binRange.setBinRangeLow(encodeString(binRange.getBinRangeLow()));
			binRange.setBinRangeHigh(encodeString(binRange.getBinRangeHigh()));
			// binRange.setCardType(encodeString(binRange.getCardType().toString()));
			binRange.setGroupCode(encodeString(binRange.getGroupCode()));
			binRange.setIssuerBankName(encodeString(binRange.getIssuerBankName()));
			binRange.setIssuerCountry(encodeString(binRange.getIssuerCountry()));
			// binRange.setMopType(encodeString(binRange.getMopType()));
			binRange.setProductName(encodeString(binRange.getProductName()));
			// binRange.setRfu1(encodeString(binRange.getRfu1()));
		}
		return binRangDisplay;
	}

	public List<ExceptionReport> encodeExceptionReportObj(List<ExceptionReport> exceptionReports) {
		for (ExceptionReport exceptionReport : exceptionReports) {
			exceptionReport.setPgRefNo(encodeString(exceptionReport.getPgRefNo()));
			// exceptionReport.setTxnId(encodeString(exceptionReport.getTxnId()));
			exceptionReport.setOrderId(encodeString(exceptionReport.getOrderId()));
			exceptionReport.setAcqId(encodeString(exceptionReport.getAcqId()));
		}
		return exceptionReports;
	}

	public List<RefundPreview> encodeRefundPreviewObj(List<RefundPreview> refundPreviews) {
		for (RefundPreview refundPreview : refundPreviews) {
			refundPreview.setPgRefNo(encodeString(refundPreview.getPgRefNo()));
			refundPreview.setRefundFlag(encodeString(refundPreview.getRefundFlag()));
			refundPreview.setAmount(encodeString(refundPreview.getAmount()));
			refundPreview.setOrderId(encodeString(refundPreview.getOrderId()));
			refundPreview.setPayId(encodeString(refundPreview.getPayId()));
			refundPreview.setOid(encodeString(refundPreview.getOid()));
		}
		return refundPreviews;
	}

	public List<SearchTransaction> encodeSearchTransactionObj(List<SearchTransaction> SearchTxn) {

		for (SearchTransaction transaction : SearchTxn) {
			transaction.setTransactionId(transaction.getTransactionId());
			transaction.setPgRefNum(encodeString(transaction.getPgRefNum()));
			transaction.setMerchant(encodeString(transaction.getMerchant()));
			transaction.setOrderId(encodeString(transaction.getOrderId()));
			transaction.settDate(encodeString(transaction.gettDate()));
			transaction.setPaymentType(encodeString(transaction.getPaymentType()));
			transaction.setMopType(encodeString(transaction.getMopType()));
			transaction.setTxnType(encodeString(transaction.getTxnType()));
			transaction.setCardNum(encodeString(transaction.getCardNum()));
			transaction.setStatus(encodeString(resolveStatus(transaction.getStatus())));
			transaction.setAmount(encodeString(transaction.getAmount()));
			transaction.setTotalAmount(encodeString(transaction.getTotalAmount()));
			transaction.setCustName(encodeString(transaction.getCustName()));
			transaction.setRrn(encodeString(transaction.getRrn()));
			transaction.setAcqId(encodeString(transaction.getAcqId()));
			transaction.setIpayResponseMessage(encodeString(transaction.getIpayResponseMessage()));
			transaction.setAcquirerTxnMessage(encodeString(transaction.getAcquirerTxnMessage()));
			transaction.setRefund_txn_id(encodeString(transaction.getRefund_txn_id()));
			transaction.setResponseCode(encodeString(transaction.getResponseCode()));
			transaction.setAcquirer(encodeString(transaction.getAcquirer()));
			transaction.setArn(encodeString(transaction.getArn()));
			transaction.setIssuerBank(encodeString(transaction.getIssuerBank()));
			transaction.setRequestDate(encodeString(transaction.getRequestDate()));
			transaction.setCustomerEmail(encodeString(transaction.getCustomerEmail()));
			transaction.setCustomerPhone(encodeString(transaction.getCustomerPhone()));

			transaction.setCustName(encodeString(transaction.getCustName()));
			transaction.setCardMask(encodeString(transaction.getCardMask()));
		}
		return SearchTxn;
	}

	public List<StatusEnquiryObject> encodeEnquiryTransactionObj(List<StatusEnquiryObject> enquiryStatus) {

		for (StatusEnquiryObject status : enquiryStatus) {
			status.setCreateDate(status.getCreateDate());
			status.setTotalProcess(status.getTotalProcess());
			status.setTotalCapture(status.getTotalCapture());
			status.setTotalPending(status.getTotalPending());
			status.setTotalOthers(status.getTotalOthers());

		}
		return enquiryStatus;
	}

	public static String encodeUserOnBoardDocList(byte[] onBoardList) {
		String oldDocList = "{}";
		if (onBoardList != null) {
			oldDocList = new String(onBoardList);
			oldDocList = encodeString(oldDocList);
//			oldDocList = oldDocList.replace("'", "&#39;");
		}
		return oldDocList;
	}

	public List<BeneficiaryAccounts> encodeBeneficiaryAccountObj(List<BeneficiaryAccounts> beneficiaryAccountsList) {
		for (BeneficiaryAccounts beneficiaryAccount : beneficiaryAccountsList) {
			beneficiaryAccount.setId(encodeString(beneficiaryAccount.getId()));
			beneficiaryAccount.setCreatedDate(encodeString(beneficiaryAccount.getCreatedDate()));
			beneficiaryAccount.setUpdatedDate(encodeString(beneficiaryAccount.getUpdatedDate()));
			beneficiaryAccount.setRequestedBy(encodeString(beneficiaryAccount.getRequestedBy()));
			beneficiaryAccount.setProcessedBy(encodeString(beneficiaryAccount.getProcessedBy()));
			beneficiaryAccount.setCustId(encodeString(beneficiaryAccount.getCustId()));
			beneficiaryAccount.setBeneficiaryCd(encodeString(beneficiaryAccount.getBeneficiaryCd()));
			beneficiaryAccount.setSrcAccountNo(encodeString(beneficiaryAccount.getSrcAccountNo()));
			beneficiaryAccount.setBeneName(encodeString(beneficiaryAccount.getBeneName()));
			beneficiaryAccount.setBeneType(encodeString(beneficiaryAccount.getBeneType()));
			beneficiaryAccount.setBeneExpiryDate(encodeString(beneficiaryAccount.getBeneExpiryDate()));
			beneficiaryAccount.setTransactionLimit(encodeString(beneficiaryAccount.getTransactionLimit()));
			beneficiaryAccount.setBankName(encodeString(beneficiaryAccount.getBankName()));
			beneficiaryAccount.setIfscCode(encodeString(beneficiaryAccount.getIfscCode()));
			beneficiaryAccount.setBeneAccountNo(encodeString(beneficiaryAccount.getBeneAccountNo()));
			beneficiaryAccount.setUpiHandle(encodeString(beneficiaryAccount.getUpiHandle()));
			beneficiaryAccount.setMobileNo(encodeString(beneficiaryAccount.getMobileNo()));
			beneficiaryAccount.setEmailId(encodeString(beneficiaryAccount.getEmailId()));
			beneficiaryAccount.setAddress1(encodeString(beneficiaryAccount.getAddress1()));
			beneficiaryAccount.setAddress2(encodeString(beneficiaryAccount.getAddress2()));
			beneficiaryAccount.setSwiftCode(encodeString(beneficiaryAccount.getSwiftCode()));
			beneficiaryAccount.setAcquirer(encodeString(beneficiaryAccount.getAcquirer()));
			beneficiaryAccount.setActions(encodeString(beneficiaryAccount.getActions()));
			beneficiaryAccount.setResponseMessage(encodeString(beneficiaryAccount.getResponseMessage()));
			beneficiaryAccount.setMerchantProvidedId(encodeString(beneficiaryAccount.getMerchantProvidedId()));
			beneficiaryAccount.setMerchantPayId(encodeString(beneficiaryAccount.getMerchantPayId()));
			beneficiaryAccount.setMerchantProvidedName(encodeString(beneficiaryAccount.getMerchantProvidedName()));
			beneficiaryAccount.setMerchantBusinessName(encodeString(beneficiaryAccount.getMerchantBusinessName()));
			beneficiaryAccount.setAadharNo(encodeString(beneficiaryAccount.getAadharNo()));
		}
		return beneficiaryAccountsList;
	}

	public List<Menu> encodeMenu(List<Menu> menus) {
		for (Menu menu : menus) {
			menu.setMenuName(encodeString(menu.getMenuName()));
			menu.setDescription(encodeString(menu.getDescription()));
			menu.setActionName(encodeString(menu.getActionName()));
		}
		return menus;
	}

	public List<AuditTrail> encodeAuditTrail(List<AuditTrail> auditTrails) {
		for (AuditTrail auditTrail : auditTrails) {
			auditTrail.setEmailId(encodeString(auditTrail.getEmailId()));
			auditTrail.setFirstName(encodeString(auditTrail.getFirstName()));
			auditTrail.setActionMessageByAction(encodeActionMessage(auditTrail.getActionMessageByAction()));
			auditTrail.setBrowser(encodeString(auditTrail.getBrowser()));
			auditTrail.setIp(encodeString(auditTrail.getIp()));
			auditTrail.setOs(encodeString(auditTrail.getOs()));
			auditTrail.setPayload(auditTrail.getPayload());
			auditTrail.setPreviousValue(auditTrail.getPreviousValue());
			auditTrail.setDiffValue(auditTrail.getDiffValue());
			auditTrail.setTimestamp(encodeString(auditTrail.getTimestamp()));
		}
		return auditTrails;
	}

	private ActionMessageByAction encodeActionMessage(ActionMessageByAction action) {
		action.setAction(encodeString(action.getAction()));
		action.setActionMessage(encodeString(action.getActionMessage()));
		return action;
	}

//	public List<ReportingCollection> encodeReportingCollection(List<ReportingCollection> dashboardExceptionReport) {
//		for (ReportingCollection reportingCollection : dashboardExceptionReport) {
//			reportingCollection.set_id(encodeString(reportingCollection.get_id()));
//			reportingCollection.setcREATE_DATE(encodeString(reportingCollection.getcREATE_DATE()));
//			reportingCollection.setdB_ACQ_ID(encodeString(reportingCollection.getdB_ACQ_ID()));
//			reportingCollection.set
//		}
//		return dashboardExceptionReport;
//	}
}
