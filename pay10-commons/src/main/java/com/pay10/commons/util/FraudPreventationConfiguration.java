package com.pay10.commons.util;

public class FraudPreventationConfiguration {

	private String id;
	private boolean onIpBlocking;
	private boolean onIssuerCountriesBlocking;
	private boolean onUserCountriesBlocking;
	private boolean onEmailBlocking;
	private boolean onLimitTxnAmtBlocking;
	private boolean onCardRangeBlocking;
	private boolean onCardMaskBlocking;
	private boolean onLimitCardTxnBlocking;
	private boolean onPhoneNoBlocking;
	private boolean onTxnAmountVelocityBlocking;
	private boolean onMacBlocking;
	private boolean onNotifyRepeatedMopType;
	private boolean onStateBlocking;
	private boolean onCityBlocking;
	private boolean onBlockRepeatedMopTypeForSameDetails;
	private boolean onVpaAddressBlocking;

	public FraudPreventationConfiguration() {
	}

	public FraudPreventationConfiguration(String id, boolean onIpBlocking, boolean onIssuerCountriesBlocking,
			boolean onUserCountriesBlocking, boolean onEmailBlocking, boolean onLimitTxnAmtBlocking,
			boolean onCardRangeBlocking, boolean onCardMaskBlocking, boolean onLimitCardTxnBlocking,
			boolean onPhoneNoBlocking, boolean onTxnAmountVelocityBlocking, boolean onMacBlocking,
			boolean onNotifyRepeatedMopType, boolean onStateBlocking, boolean onCityBlocking,
			boolean onBlockRepeatedMopTypeForSameDetails, boolean onVpaAddressBlocking ) {
		super();
		this.id = id;
		this.onIpBlocking = onIpBlocking;
		this.onIssuerCountriesBlocking = onIssuerCountriesBlocking;
		this.onUserCountriesBlocking = onUserCountriesBlocking;
		this.onEmailBlocking = onEmailBlocking;
		this.onLimitTxnAmtBlocking = onLimitTxnAmtBlocking;
		this.onCardRangeBlocking = onCardRangeBlocking;
		this.onCardMaskBlocking = onCardMaskBlocking;
		this.onLimitCardTxnBlocking = onLimitCardTxnBlocking;
		this.onPhoneNoBlocking = onPhoneNoBlocking;
		this.onTxnAmountVelocityBlocking = onTxnAmountVelocityBlocking;
		this.onMacBlocking = onMacBlocking;
		this.onNotifyRepeatedMopType = onNotifyRepeatedMopType;
		this.onStateBlocking = onStateBlocking;
		this.onCityBlocking = onCityBlocking;
		this.onBlockRepeatedMopTypeForSameDetails = onBlockRepeatedMopTypeForSameDetails;
		this.onVpaAddressBlocking=onVpaAddressBlocking;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isOnIpBlocking() {
		return onIpBlocking;
	}

	public void setOnIpBlocking(boolean onIpBlocking) {
		this.onIpBlocking = onIpBlocking;
	}

	public boolean isOnIssuerCountriesBlocking() {
		return onIssuerCountriesBlocking;
	}

	public void setOnIssuerCountriesBlocking(boolean onIssuerCountriesBlocking) {
		this.onIssuerCountriesBlocking = onIssuerCountriesBlocking;
	}

	public boolean isOnUserCountriesBlocking() {
		return onUserCountriesBlocking;
	}

	public void setOnUserCountriesBlocking(boolean onUserCountriesBlocking) {
		this.onUserCountriesBlocking = onUserCountriesBlocking;
	}

	public boolean isOnEmailBlocking() {
		return onEmailBlocking;
	}

	public void setOnEmailBlocking(boolean onEmailBlocking) {
		this.onEmailBlocking = onEmailBlocking;
	}

	public boolean isOnLimitTxnAmtBlocking() {
		return onLimitTxnAmtBlocking;
	}

	public void setOnLimitTxnAmtBlocking(boolean onLimitTxnAmtBlocking) {
		this.onLimitTxnAmtBlocking = onLimitTxnAmtBlocking;
	}

	public boolean isOnCardRangeBlocking() {
		return onCardRangeBlocking;
	}

	public void setOnCardRangeBlocking(boolean onCardRangeBlocking) {
		this.onCardRangeBlocking = onCardRangeBlocking;
	}

	public boolean isOnCardMaskBlocking() {
		return onCardMaskBlocking;
	}

	public void setOnCardMaskBlocking(boolean onCardMaskBlocking) {
		this.onCardMaskBlocking = onCardMaskBlocking;
	}

	public boolean isOnLimitCardTxnBlocking() {
		return onLimitCardTxnBlocking;
	}

	public void setOnLimitCardTxnBlocking(boolean onLimitCardTxnBlocking) {
		this.onLimitCardTxnBlocking = onLimitCardTxnBlocking;
	}

	public boolean isOnPhoneNoBlocking() {
		return onPhoneNoBlocking;
	}

	public void setOnPhoneNoBlocking(boolean onPhoneNoBlocking) {
		this.onPhoneNoBlocking = onPhoneNoBlocking;
	}

	public boolean isOnTxnAmountVelocityBlocking() {
		return onTxnAmountVelocityBlocking;
	}

	public void setOnTxnAmountVelocityBlocking(boolean onTxnAmountVelocityBlocking) {
		this.onTxnAmountVelocityBlocking = onTxnAmountVelocityBlocking;
	}

	public boolean isOnMacBlocking() {
		return onMacBlocking;
	}

	public void setOnMacBlocking(boolean onMacBlocking) {
		this.onMacBlocking = onMacBlocking;
	}

	public boolean isOnNotifyRepeatedMopType() {
		return onNotifyRepeatedMopType;
	}

	public void setOnNotifyRepeatedMopType(boolean onNotifyRepeatedMopType) {
		this.onNotifyRepeatedMopType = onNotifyRepeatedMopType;
	}

	public boolean isOnStateBlocking() {
		return onStateBlocking;
	}

	public void setOnStateBlocking(boolean onStateBlocking) {
		this.onStateBlocking = onStateBlocking;
	}

	public boolean isOnCityBlocking() {
		return onCityBlocking;
	}

	public void setOnCityBlocking(boolean onCityBlocking) {
		this.onCityBlocking = onCityBlocking;
	}

	public boolean isOnBlockRepeatedMopTypeForSameDetails() {
		return onBlockRepeatedMopTypeForSameDetails;
	}

	public void setOnBlockRepeatedMopTypeForSameDetails(boolean onBlockRepeatedMopTypeForSameDetails) {
		this.onBlockRepeatedMopTypeForSameDetails = onBlockRepeatedMopTypeForSameDetails;
	}
	
	public boolean isOnVpaAddressBlocking() {
		return onVpaAddressBlocking;
	}

	public void setOnVpaAddressBlocking(boolean onVpaAddressBlocking) {
		this.onVpaAddressBlocking = onVpaAddressBlocking;
	}

	public static String getKeyByRuleType(FraudRuleType ruleType) {
		if (ruleType == FraudRuleType.BLOCK_CARD_BIN)
			return "onCardRangeBlocking";
		if (ruleType == FraudRuleType.BLOCK_CARD_ISSUER_COUNTRY)
			return "onIssuerCountriesBlocking";
		if (ruleType == FraudRuleType.BLOCK_CARD_NO)
			return "onCardMaskBlocking";
		if (ruleType == FraudRuleType.BLOCK_CARD_TXN_THRESHOLD)
			return "onLimitCardTxnBlocking";
		if (ruleType == FraudRuleType.BLOCK_EMAIL_ID)
			return "onEmailBlocking";
		if (ruleType == FraudRuleType.BLOCK_IP_ADDRESS)
			return "onIpBlocking";
		if (ruleType == FraudRuleType.BLOCK_MACK_ADDRESS)
			return "onMacBlocking";
		if (ruleType == FraudRuleType.BLOCK_PHONE_NUMBER)
			return "onPhoneNoBlocking";
		if (ruleType == FraudRuleType.BLOCK_TXN_AMOUNT)
			return "onLimitTxnAmtBlocking";
		if (ruleType == FraudRuleType.BLOCK_TXN_AMOUNT_VELOCITY)
			return "onTxnAmountVelocityBlocking";
		if (ruleType == FraudRuleType.BLOCK_USER_COUNTRY)
			return "onUserCountriesBlocking";
		if (ruleType == FraudRuleType.REPEATED_MOP_TYPES)
			return "onNotifyRepeatedMopType";
		if (ruleType == FraudRuleType.BLOCK_USER_STATE)
			return "onStateBlocking";
		if (ruleType == FraudRuleType.BLOCK_USER_CITY)
			return "onCityBlocking";
		if (ruleType == FraudRuleType.BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL)
			return "onBlockRepeatedMopTypeForSameDetails";
		if (ruleType == FraudRuleType.BLOCK_VPA_ADDRESS)
			return "onVpaAddressBlocking";
		return null;
		
	}

	@Override
	public String toString() {
		return "FraudPreventationConfiguration{" +
				"id='" + id + '\'' +
				", onIpBlocking=" + onIpBlocking +
				", onIssuerCountriesBlocking=" + onIssuerCountriesBlocking +
				", onUserCountriesBlocking=" + onUserCountriesBlocking +
				", onEmailBlocking=" + onEmailBlocking +
				", onLimitTxnAmtBlocking=" + onLimitTxnAmtBlocking +
				", onCardRangeBlocking=" + onCardRangeBlocking +
				", onCardMaskBlocking=" + onCardMaskBlocking +
				", onLimitCardTxnBlocking=" + onLimitCardTxnBlocking +
				", onPhoneNoBlocking=" + onPhoneNoBlocking +
				", onTxnAmountVelocityBlocking=" + onTxnAmountVelocityBlocking +
				", onMacBlocking=" + onMacBlocking +
				", onNotifyRepeatedMopType=" + onNotifyRepeatedMopType +
				", onStateBlocking=" + onStateBlocking +
				", onCityBlocking=" + onCityBlocking +
				", onBlockRepeatedMopTypeForSameDetails=" + onBlockRepeatedMopTypeForSameDetails +
				", onVpaAddressBlocking=" + onVpaAddressBlocking +
				'}';
	}

}
