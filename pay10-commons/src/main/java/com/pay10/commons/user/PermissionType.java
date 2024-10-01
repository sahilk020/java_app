package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.List;

public enum PermissionType {

	LOGIN(1,"login", false, false),
	CREATEUSER(3,"create", false, false),
	DELETEUSER(4,"delete",false, false),
	VOID_REFUND(8,"Void/Refund", true, false),
	VIEW_INVOICE(9,"View Invoice", true, true),
	CREATE_INVOICE(10,"Create Invoice", true, true),
	VIEW_REMITTANCE(11,"View Remittance", true, false),
	//CREATE_TICKET(12, "Create Ticket", true, true),
	VIEW_MERCHANT_SETUP(13, "View MerchantSetup", true, false), 
	VIEW_MERCHANT_BILLING(14, "View Merchant Billing", true, false), 
	VIEW_SEARCH_PAYMENT(16, "Search Payment", true, true), 
	CREATE_BATCH_OPERATION(18, "Create BatchOperation", true, false), 
	FRAUD_PREVENTION(19, "Fraud Prevention", true, false),
	CREATE_BULK_EMAIL(20, "Create BulkEmail", true, false),
	VIEW_CHARGEBACK(21, "View ChargeBack", true, true),
	CREATE_CHARGEBACK(22, "Create ChargeBack", true, false),
	CREATE_MAPPING(28, "Create Mapping", true, false),
	CREATE_SURCHARGE(23, "Create Surcharge", true, false),
	CREATE_TDR(24, "Create TDR", true, false),
	CREATE_SERVICE_TAX(25, "Create Service Tax", true, false),
	CREATE_MERCHANT_MAPPING(26, "Create Merchant Mapping", true, false),
	CREATE_RESELLER_MAPPING(27, "Create Reseller Mapping", true, false),
	MERCHANT_EDIT(47, "Edit Merchant Details", true, false),
	VIEW_TRANSACTION_REPORTS(29, "View Transaction Reports", true, true),
	VIEW_REPORTS(30, "View Reports", true, false),
	VIEW_ACCOUNT_AND_FINANCE_REPORTS(31, "View Accounts and Finance Reports", true, false),
	VIEW_ANALYTICS(32, "View Analytics", true, false),
	REFRESH_DATA(34, "Settlement Data Refresh", true, false),
	AGENT_SEARCH(35, "Agent Search", true, true),
	VIEW_SURCHARGE(36, "View Surcharge", true, false),
	SMART_ROUTER(37, "Smart Router", true, false),
	RULE_ENGINE(38, "Rule Engine", true, false),
	MANAGE_USERS(39, "Manage Users", true, false),
	MANAGE_ACQUIRERS(40, "Manage Acquirers", true, false),
	NOTIFICATION_ENGINE(41, "Notifcation Engine", true, false),
//	SMART_ROUTER_AUDIT(39, "SmartRouter and RuleEngine Audit", true, false);
//	BULK_TRANSACTION_SEARCH(40, "Bulk Transaction Search", true, false);
	CREATE_MERCHANT(42, "Create Merchant", true, false),
	VIEW_MERCHANT_ACCOUNT(43, "View Merchant Account", true, false),
	SMART_ROUTER_AUDIT_TRAIL(44, "Smart Router Audit Trail", true, false),
	RULE_ENGINE_AUDIT_TRAILT(45, "Rule Engine Audit Trail", true, false),
	PENDING_REQUEST(46, "Pending Request", true, false),
	VIEW_DASH_BOARD(47, "View Dashboard", true, true),
	
	// Below permissions not applicable for Sub Admin , only applicable to sub user 
	VIEW_SNAPSHOT(48, "View Snapshot", false, true),
	VIEW_MONTHLY_TXNS(49, "View Monthly Transactions", false, true),
	VIEW_HOURLY_TXNS(50, "View Hourly Transactions", false, true),
	VIEW_PAYMENT_COMPARISON(51, "View Payment type Comparison", false, true),
	VIEW_HITS_CAPTURED(52, "View Hits Vs Captured", false, true),
	VIEW_SETTLEMENT(53, "View Settlement", false, true),
	
	// Below permissions applicable for sub super admin, not for admin,  
	VIEW_SEARCHPAYMENT_SA (54, "View Search Payment", false, false),
	CREATE_ADMIN_SA 	  (55, "Create Admin", false, false),
	VIEW_ADMINLIST_SA     (56, "View Admin List", false, false),
	EDIT_ADMIN_SA         (57, "Edit Admin", false, false),
	VIEW_TENANTLIST_SA    (58, "View Tenant List", false, false),

	// below permissions  applicable for Sub Admin  
	MAINTAIN_BENEFICIARY(64, "Maintain Beneficiary", true, false),
	NODAL_PAYOUTS(65, "Nodal Payouts", true, false),
	NODAL_TRANSACTION_HISTORY (66, "Nodal Transaction History", true, false);
	


	
	private final String permission;
	private final int id;
	private final boolean isInternal;
	private final boolean isInternalValue;
	
	private PermissionType(int id,String permission,boolean isInternal,boolean isInternalValue){
		this.id = id;
		this.permission = permission;
		this.isInternal = isInternal;
		this.isInternalValue = isInternalValue;
	}
	
	public int getId() {
		return id;
	}
	public String getPermission() {
		return permission;			
	}
	
	public boolean isInternal() {
		return isInternal;
	}
	
	
	public boolean isInternalValue() {
		return isInternalValue;
	}

	public static List<PermissionType> getPermissionType(){
		List<PermissionType> permissionTypes = new ArrayList<PermissionType>();						
		for(PermissionType permissionType:PermissionType.values()){
			if(permissionType.isInternal())
				permissionTypes.add(permissionType);
		}
	  return permissionTypes;
	}
	
	public static List<PermissionType> getPermissionTypeSuperAdmin(){
		List<PermissionType> permissionTypes = new ArrayList<PermissionType>();						
		for(PermissionType permissionType:PermissionType.values()){
			if(!permissionType.isInternal() && !permissionType.isInternalValue() && !permissionType.getPermission().equalsIgnoreCase("LOGIN") && !permissionType.getPermission().equalsIgnoreCase("create") && !permissionType.getPermission().equalsIgnoreCase("delete"))
				permissionTypes.add(permissionType);
		}
	  return permissionTypes;
	}
	
	public static PermissionType getInstanceFromName(String code){
		PermissionType[] permissionTypes = PermissionType.values();
		for(PermissionType permissionType : permissionTypes){
			if(String.valueOf(permissionType.getPermission()).toUpperCase().equals(code.toUpperCase())){
				return permissionType;
			}
		}		
		return null;
	}
	
	public static List<PermissionType> getSubAcquirerPermissionType(){
		List<PermissionType> permissionTypes = new ArrayList<PermissionType>();						
		for(PermissionType permissionType:PermissionType.values()){
			if(permissionType.isInternalValue())
				permissionTypes.add(permissionType);
		}
	  return permissionTypes;
	}
	
	
	public static List<PermissionType> getSubUserPermissionType(){
		List<PermissionType> permissionTypes = new ArrayList<PermissionType>();						
		for(PermissionType permissionType:PermissionType.values()){
			if(permissionType.isInternalValue())
				permissionTypes.add(permissionType);
		}
	  return permissionTypes;
	}
	
}
