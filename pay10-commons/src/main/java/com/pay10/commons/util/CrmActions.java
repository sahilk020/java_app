package com.pay10.commons.util;

/**
 * @author Harpreet
 *
 */
public enum CrmActions {

	ADMIN_MERCHANT_LIST				 ("merchantList"),
	ADMIN_RESELLER_LISTS			 ("adminResellers"),				// old action name ("resellerLists"),
	ADMIN_MOP_SET_UP_ACTION			 ("mopSetUpAction"),
	ADMIN_RESELLER_MAPPING_ACTION	 ("resellerMappingAction"),
	ADMIN_CHARGING_PLATFORM			 ("chargingPlatform"),
	ADMIN_MERCHANT_CRM_SIGNUP		 ("merchantCrmSignup"),
	ADMIN_ANALYTICS					 ("analytics"),
	ADMIN_KOTAK_REFUND				 ("kotakRefund"),
	ADMIN_YES_BANK_REFUND			 ("yesBankRefund"),
	ADMIN_ADD_REMITTANCE			 ("addRemittance"),
	ADMIN_RESTRICTIONS				 ("adminRestrictions"),
	
	
	//common actions for MERCHANT and ADMIN
	HOME							 ("home"),
	AUTHORIZE_TRANSACTION			 ("authorizeTransaction"),
	CAPTURE_TRANSACTION				 ("captureTransaction"),
	INCOMPLETE_TRANSACTION			 ("incompleteTransaction"),
	FAILED_TRANSACTION				 ("failedTransaction"),
	CANCEL_TRANSACTION				 ("cancelTransaction"),
	INVALID_TRANSACTION				 ("invalidTransaction"),
	MERACHANT_ANALYTICS				 ("merachantAnalytics"),
	TRANSACTION_SEARCH				 ("transactionSearch"),
	SNAPSHOT_REPORT					 ("snapshotReport"),
	SUMMARY_REPORT					 ("summaryReport"),
	REFUND_REPORT					 ("refundReport"),
	VIEW_REMITTANCE					 ("viewRemittance"),
	INVOICE							 ("invoice"),
	INVOICE_EVENT					 ("invoiceEvent"),
	INVOICE_SEARCH					 ("invoiceSearch"),
	ADD_USER						 ("addUser"),
	SEARCH_USER						 ("searchUser"),
	LOGIN_HISTORY_REDIRECT_USER		 ("loginHistoryRedirectUser"),
	MERCHANT_PROFILE				 ("merchantProfile"),
	LOGIN_HISTORY_REDIRECT			 ("loginHistoryRedirect"),
	PASSWORD_CHANGE					 ("passwordChangesummary"),
	SUMMARY							 ("summary"),
	PAYMENT_PAGE_SETTING			 ("paymentPageSetting"),
	CHANGE_PASSWORD					 ("passwordChange"),
	SETTLEMENT_REPORT				 ("settlementReport"),
	WEEKLY_ANALYTICS				 ("weeklyAnalytics"),
	MIS_REPORTS				         ("misReports");
	
	private final String value;
	
	private CrmActions(String value){
		this.value = value;
	}
	 
	public String getValue() {
		return value;
	}
	
}
