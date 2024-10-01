package com.pay10.commons.util;

import java.util.HashSet;
import java.util.Set;

import com.pay10.commons.user.MopTransaction;

public enum SettlementTransactionType {

	ADD_BENEFICIARY(1, "ADD BENEFICIARY", "ADD", false), // Also using for Yes bank FT3
	FUND_TRANSFER(2, "FUND TRANSFER", "FUND", false), STATUS(3, "STATUS", "STAT", false),
	MODIFY_BENEFICIARY(4, "MODIFY BENEFICIARY", "MODIFY", false), // Created for Yes bank FT3
	VERIFY_BENEFICIARY(5, "VERIFY BENEFICIARY", "VERIFY", false), // Created for Yes bank FT3
	DISABLE_BENEFICIARY(6, "DISABLE BENEFICIARY", "DISABLE", false),
	ENABLE_BENEFICIARY(7, "ENABLE BENEFICIARY", "ENABLE", false), // Created for Yes bank FT3
	FUND_CONFIRMATION(8, "FUND CONFIRMATION", "FUND CONFIRMATION", false), // Created for Yes bank FT3

	;
	private final int id;
	private final String name;
	private final String code;
	private final boolean isInternal;

	private SettlementTransactionType(int id, String name, String code, boolean isInternal) {
		this.name = name;
		this.code = code;
		this.id = id;
		this.isInternal = isInternal;

	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public int getId() {
		return id;
	}

	public boolean isInternal() {
		return isInternal;
	}

	public static SettlementTransactionType getInstance(String name) {
		SettlementTransactionType[] transactionTypes = SettlementTransactionType.values();
		for (SettlementTransactionType transactionType : transactionTypes) {
			if (transactionType.getName().equals(name)) {
				return transactionType;
			}
		}

		return null;
	}

	public static SettlementTransactionType getInstanceFromCode(String code) {
		SettlementTransactionType[] transactionTypes = SettlementTransactionType.values();
		for (SettlementTransactionType transactionType : transactionTypes) {
			if (transactionType.getCode().equals(code)) {
				return transactionType;
			}
		}

		return null;
	}

	public static Set<MopTransaction> makeMopTxnSet(String[] txns) {
		Set<MopTransaction> moptxns = new HashSet<MopTransaction>();

		for (String txnType : txns) {
			MopTransaction moptxn = new MopTransaction();
			moptxn.setTransactionType(TransactionType.getInstance(txnType));
			moptxns.add(moptxn);
		}
		return moptxns;
	}
}
