package com.crmws.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.Mop;
import com.pay10.commons.user.MopInternational;
import com.pay10.commons.user.MopTransaction;
import com.pay10.commons.user.MopTransactionInternational;
import com.pay10.commons.user.Payment;
import com.pay10.commons.user.PaymentInternational;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.User;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TDRStatus;
import com.pay10.commons.util.TransactionType;

/**
 * @author Puneet
 *
 */
@Service
public class AccountFactory {

	@Autowired
	AWSEncryptDecryptService awsEncryptDecryptService;

	private static Logger logger = LoggerFactory.getLogger(AccountFactory.class.getName());

	private ChargingDetailsMaintainer maintainChargingDetails = new ChargingDetailsMaintainer();
	private Account account = new Account();
	private Set<Payment> savedPaymentSet = new HashSet<Payment>();
	
	private Payment presentPayment = null;
	private Mop presentMop = null;
	private MopTransaction presentMoptxn = null;
	
	private Set<PaymentInternational> savedPaymentSetInternational = new HashSet<PaymentInternational>();
	private PaymentInternational presentPaymentInternational = null;
	private MopInternational presentMopInternational = null;
	private MopTransactionInternational presentMoptxnInternational = null;

	public AccountFactory() {

	}

	public Account editAccount(Account oldAccount, String mapString, String payId, boolean international,
			boolean domestic) {
		this.account = oldAccount;
		String acquirerName = oldAccount.getAcquirerName();
		List<String> mappedCurrency = getCurrencyList();
		savedPaymentSet = account.getPayments();
		savedPaymentSetInternational = account.getPaymentsInternational();
		
		if (mapString.equals("")) {
			String[] dummytokens = { "" };
			removeMapping(dummytokens, acquirerName, payId, account.getId(), international, domestic);
			removeMappingInternational(dummytokens, acquirerName, payId, account.getId(), international, domestic);
			return account;
		}
		String[] tokens = mapString.split(",");
		// remove old mappings
		removeMapping(tokens, acquirerName, payId, account.getId(), international, domestic);
		removeMappingInternational(tokens, acquirerName, payId, account.getId(), international, domestic);
		
		// new mappings added
		addMapping(tokens, acquirerName, payId, mappedCurrency, international, domestic);
		addMappingInternational(tokens, acquirerName, payId, mappedCurrency, international, domestic);
		return account;
	}

	public void removeMapping(String[] tokens, String acquirer, String payId, Long accountId, boolean international,
			boolean domestic) {
		
		
			Set<Payment> paymentSet = account.getPayments();
			String[] savedTokens = account.getMappedString().split(CrmFieldConstants.COMMA.getValue());

			for (String savedToken : savedTokens) {
				boolean isPresent = false;
				for (String token : tokens) {
					if (token.equals(savedToken)) {
						isPresent = true;
					}
				}
				// disable charging detail
				if (!isPresent) {
					removeToken(paymentSet, savedToken);
					
						account.disableTdrSettingDomestic(savedToken);
					
				}
			}
				
	}

	public void removeMappingInternational(String[] tokens, String acquirer, String payId, Long accountId, boolean international,
			boolean domestic) {
		Set<PaymentInternational> paymentSetInternational = account.getPaymentsInternational();
		String[] savedTokensInternational = account.getMappedStringInternational().split(CrmFieldConstants.COMMA.getValue());

		for (String savedToken : savedTokensInternational) {
			boolean isPresent = false;
			for (String token : tokens) {
				if (token.equals(savedToken)) {
					isPresent = true;
				}
			}
			// disable charging detail
			if (!isPresent) {
				removeTokenInternational(paymentSetInternational, savedToken);
				
					account.disableTdrSettingInterNational(savedToken);
				
			}
		
	}
	
	}
	
	public void removeToken(Set<Payment> paymentSet, String savedToken) {
		String[] splittedToken = savedToken.split("-");

		if (checkPaymentType(paymentSet, savedToken)) {
			if (checkMopType(savedToken)) {
				if (splittedToken.length == 3) {
					if (checkTxnType(savedToken)) {
						presentMop.removeTransactionType(presentMoptxn);
						if (presentMop.getMopTransactionTypes().isEmpty()) {
							presentPayment.removeMop(presentMop);
						}
					}
				} else {
					// IN case of WL and NB
					presentPayment.removeMop(presentMop);
				}
			}
			if (presentPayment.getMops().isEmpty()) {
				account.removePayment(presentPayment);
			}
		}
	}
	
	public void removeTokenInternational(Set<PaymentInternational> paymentSet, String savedToken) {
		String[] splittedToken = savedToken.split("-");

		if (checkPaymentTypeInternational(paymentSet, savedToken)) {
			if (checkMopType(savedToken)) {
				if (splittedToken.length == 3) {
					if (checkTxnType(savedToken)) {
						presentMopInternational.removeTransactionType(presentMoptxnInternational);
						if (presentMopInternational.getMopTransactionTypes().isEmpty()) {
							presentPaymentInternational.removeMop(presentMopInternational);
						}
					}
				} else {
					// IN case of WL and NB
					presentPaymentInternational.removeMop(presentMopInternational);
				}
			}
			if (presentPaymentInternational.getMops().isEmpty()) {
				account.removePaymentsInternational(presentPaymentInternational);
			}
		}
	}

	public void addMapping(String[] tokens, String acquirerName, String payId, List<String> mappedCurrency,
			boolean international, boolean domestic) {
		for (String token : tokens) {
			String[] splitedToken = token.split("-");
			if (!checkPaymentType(savedPaymentSet, token)) {

				Payment payment = new Payment();
				payment.setPaymentType(PaymentType.getInstance(splitedToken[0]));

				Mop newMop = new Mop();
				newMop.setMopType(MopType.getmop(splitedToken[1]));

				payment.addMop(newMop);

				if (splitedToken.length == 3) {
					MopTransaction newMopTxn = new MopTransaction();
					newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[2]));
					newMop.addMopTransaction(newMopTxn);
				}
				account.addPayment(payment);
				addChargingDetail(acquirerName, payId, token, mappedCurrency);
				continue;
			}
			// if mop not present
			if (!checkMopType(token)) {

				Mop newMop = new Mop();
				if (splitedToken.length == 3) {
					MopTransaction newMopTxn = new MopTransaction();
					newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[2]));
					newMop.addMopTransaction(newMopTxn);
				}

				newMop.setMopType(MopType.getmop(splitedToken[1]));
				presentPayment.addMop(newMop);
				addChargingDetail(acquirerName, payId, token, mappedCurrency);
				continue;
			}

			if (!checkTxnType(token)) { // if txntype not present
				if (splitedToken.length == 2) {
					continue;
				}
				MopTransaction newMopTxn = new MopTransaction();
				newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[2]));
				presentMop.addMopTransaction(newMopTxn);
				addChargingDetail(acquirerName, payId, token, mappedCurrency);
			}
		}
	}
	public void addMappingInternational(String[] tokens, String acquirerName, String payId, List<String> mappedCurrency,
			boolean international, boolean domestic) {
		for (String token : tokens) {
			String[] splitedToken = token.split("-");
			if (!checkPaymentTypeInternational(savedPaymentSetInternational, token)) {

				PaymentInternational payment = new PaymentInternational();
				payment.setPaymentType(PaymentType.getInstance(splitedToken[0]));

				MopInternational newMop = new MopInternational();
				newMop.setMopType(MopType.getmop(splitedToken[1]));

				payment.addMop(newMop);

				if (splitedToken.length == 3) {
					MopTransactionInternational newMopTxn = new MopTransactionInternational();
					newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[2]));
					newMop.addMopTransaction(newMopTxn);
				}
				account.addPaymentsInternational(payment);
				
				addChargingDetailInternational(acquirerName, payId, token, mappedCurrency);
				continue;
			}
			// if mop not present
			if (!checkMopTypeInternational(token)) {

				MopInternational newMop = new MopInternational();
				if (splitedToken.length == 3) {
					MopTransactionInternational newMopTxn = new MopTransactionInternational();
					newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[2]));
					newMop.addMopTransaction(newMopTxn);
				}

				newMop.setMopType(MopType.getmop(splitedToken[1]));
				presentPaymentInternational.addMop(newMop);
				addChargingDetailInternational(acquirerName, payId, token, mappedCurrency);
				continue;
			}

			if (!checkTxnTypeInternational(token)) { // if txntype not present
				if (splitedToken.length == 2) {
					continue;
				}
				MopTransactionInternational newMopTxn = new MopTransactionInternational();
				newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[2]));
				presentMopInternational.addMopTransaction(newMopTxn);
				addChargingDetailInternational(acquirerName, payId, token, mappedCurrency);
			}
		}
	}

	public void addChargingDetail(String acquirerName, String payId, String token, List<String> selectedCurrency) {
		for (String currencyCode : selectedCurrency) {
			logger.info("impl/AccountFactory 1- ADD TDR");
			account.addTdrSetting(maintainChargingDetails.createTDRDetail(acquirerName, payId, token, currencyCode));
			account.addTdrSetting(
					maintainChargingDetails.createTDRDetailCommercial(acquirerName, payId, token, currencyCode));
		}
	}

	public void addChargingDetailInternational(String acquirerName, String payId, String token,
			List<String> selectedCurrency) {
		for (String currencyCode : selectedCurrency) {
			logger.info("impl/AccountFactory 2- ADD TDR");
			account.addTdrSetting(maintainChargingDetails.createTDRDetailInternationalConsumer(acquirerName, payId,
					token, currencyCode));
			account.addTdrSetting(maintainChargingDetails.createTDRDetailInternationalCommercial(acquirerName, payId,
					token, currencyCode));

		}
	}

	public boolean checkPaymentType(Set<Payment> paymentSet, String token) {
		boolean isPresent = false;

		Iterator<Payment> paymentItr = paymentSet.iterator();
		String[] splittedToken = token.split("-");
		while (paymentItr.hasNext()) {
			Payment currentPayment = paymentItr.next();
			if (currentPayment.getPaymentType().getName().equals(splittedToken[0])) {
				isPresent = true;
				this.presentPayment = currentPayment;
			}
		}
		return isPresent;
	}
	public boolean checkPaymentTypeInternational(Set<PaymentInternational> paymentSet, String token) {
		boolean isPresent = false;

		Iterator<PaymentInternational> paymentItr = paymentSet.iterator();
		String[] splittedToken = token.split("-");
		while (paymentItr.hasNext()) {
			PaymentInternational currentPayment = paymentItr.next();
			if (currentPayment.getPaymentType().getName().equals(splittedToken[0])) {
				isPresent = true;
				this.presentPaymentInternational = currentPayment;
			}
		}
		return isPresent;
	}

	public boolean checkMopType(String token) {
		boolean isPresent = false;
		String[] splittedToken = token.split("-");
		Set<Mop> presentMopSet = presentPayment.getMops();
		Iterator<Mop> mopItr = presentMopSet.iterator();

		while (mopItr.hasNext()) {
			Mop currentMop = mopItr.next();
			if (currentMop.getMopType().getCode().equals(splittedToken[1])) {
				isPresent = true;
				this.presentMop = currentMop;
			}
		}
		return isPresent;
	}
	
	public boolean checkMopTypeInternational(String token) {
		boolean isPresent = false;
		String[] splittedToken = token.split("-");
		Set<MopInternational> presentMopSet = presentPaymentInternational.getMops();
		Iterator<MopInternational> mopItr = presentMopSet.iterator();

		while (mopItr.hasNext()) {
			MopInternational currentMop = mopItr.next();
			if (currentMop.getMopType().getCode().equals(splittedToken[1])) {
				isPresent = true;
				this.presentMopInternational = currentMop;
			}
		}
		return isPresent;
	}

	public boolean checkTxnType(String token) {
		boolean isPresent = false;
		String[] splittedToken = token.split("-");

		Set<MopTransaction> presentMopTxnSet = presentMop.getMopTransactionTypes();
		Iterator<MopTransaction> mopTxnItr = presentMopTxnSet.iterator();

		while (mopTxnItr.hasNext()) {
			MopTransaction currentMopTxn = mopTxnItr.next();
			if (currentMopTxn.getTransactionType().getCode().equals(splittedToken[2])) {
				isPresent = true;
				this.presentMoptxn = currentMopTxn;
			}
		}
		return isPresent;
	}
	
	public boolean checkTxnTypeInternational(String token) {
		boolean isPresent = false;
		String[] splittedToken = token.split("-");

		Set<MopTransactionInternational> presentMopTxnSet = presentMopInternational.getMopTransactionTypes();
		Iterator<MopTransactionInternational> mopTxnItr = presentMopTxnSet.iterator();

		while (mopTxnItr.hasNext()) {
			MopTransactionInternational currentMopTxn = mopTxnItr.next();
			if (currentMopTxn.getTransactionType().getCode().equals(splittedToken[2])) {
				isPresent = true;
				this.presentMoptxnInternational = currentMopTxn;
			}
		}
		return isPresent;
	}

	public Account addAccountCurrency(Account account, AccountCurrency[] selectedAccountCurrency, User acquirer,
			String payId) {
		if (null == selectedAccountCurrency || selectedAccountCurrency.length == 0) {
			return account;
		}
		Set<AccountCurrency> accountCurrencySet = account.getAccountCurrencySet();
		for (AccountCurrency accountCurrencyFE : selectedAccountCurrency) {
			boolean flag = false;
			Iterator<AccountCurrency> accountCurrencySetItrator = account.getAccountCurrencySet().iterator();
			while (accountCurrencySetItrator.hasNext()) {
				AccountCurrency accountCurrency = accountCurrencySetItrator.next();
				if (accountCurrency.getCurrencyCode().equals(accountCurrencyFE.getCurrencyCode())) {
					flag = true;
					// edit password and other details
					accountCurrency.setMerchantId(accountCurrencyFE.getMerchantId());
					accountCurrency.setTxnKey(accountCurrencyFE.getTxnKey());
					accountCurrency.setAdf1(accountCurrencyFE.getAdf1());
					accountCurrency.setAdf2(accountCurrencyFE.getAdf2());
					accountCurrency.setAdf3(accountCurrencyFE.getAdf3());
					accountCurrency.setAdf4(accountCurrencyFE.getAdf4());
					accountCurrency.setAdf5(accountCurrencyFE.getAdf5());
					accountCurrency.setAdf8(accountCurrencyFE.getAdf8());
					accountCurrency.setAdf9(accountCurrencyFE.getAdf9());
					accountCurrency.setAdf10(accountCurrencyFE.getAdf10());
					accountCurrency.setAdf11(accountCurrencyFE.getAdf11());

					// Set values for Encrypted Fields
					if (accountCurrencyFE.getPassword() != null && !accountCurrencyFE.getPassword().trim().isEmpty()) {
						accountCurrency.setPassword(awsEncryptDecryptService.encrypt(accountCurrencyFE.getPassword()));
					} else {
						accountCurrency.setPassword("");
					}

					boolean camsPay = StringUtils.equalsIgnoreCase(acquirer.getBusinessName(),
							AcquirerType.CAMSPAY.getCode());
					if (StringUtils.isNotBlank(accountCurrencyFE.getAdf6())) {
						String adf6 = camsPay ? accountCurrencyFE.getAdf6()
								: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf6());
						accountCurrency.setAdf6(adf6);
					} else {
						accountCurrency.setAdf6("");
					}

					if (StringUtils.isNotBlank(accountCurrencyFE.getAdf7())) {
						String adf7 = camsPay ? accountCurrencyFE.getAdf7()
								: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf7());
						accountCurrency.setAdf7(adf7);
					} else {
						accountCurrency.setAdf7("");
					}

					accountCurrency.setDirectTxn(accountCurrencyFE.isDirectTxn());
				}
			}
			if (!flag) {
				AccountCurrency newAccountCurrency = new AccountCurrency();
				newAccountCurrency.setCurrencyCode(accountCurrencyFE.getCurrencyCode());

				newAccountCurrency.setMerchantId(accountCurrencyFE.getMerchantId());
				newAccountCurrency.setTxnKey(accountCurrencyFE.getTxnKey());
				newAccountCurrency.setAdf1(accountCurrencyFE.getAdf1());
				newAccountCurrency.setAdf2(accountCurrencyFE.getAdf2());
				newAccountCurrency.setAdf3(accountCurrencyFE.getAdf3());
				newAccountCurrency.setAdf4(accountCurrencyFE.getAdf4());
				newAccountCurrency.setAdf5(accountCurrencyFE.getAdf5());
				newAccountCurrency.setAdf8(accountCurrencyFE.getAdf8());
				newAccountCurrency.setAdf9(accountCurrencyFE.getAdf9());
				newAccountCurrency.setAdf10(accountCurrencyFE.getAdf10());
				newAccountCurrency.setAdf11(accountCurrencyFE.getAdf11());

				// Set values for Encrypted Fields
				if (accountCurrencyFE.getPassword() != null && !accountCurrencyFE.getPassword().trim().isEmpty()) {
					newAccountCurrency.setPassword(awsEncryptDecryptService.encrypt(accountCurrencyFE.getPassword()));
				} else {
					newAccountCurrency.setPassword("");
				}

				boolean camsPay = StringUtils.equalsIgnoreCase(acquirer.getBusinessName(),
						AcquirerType.CAMSPAY.getCode());
				if (StringUtils.isNotBlank(accountCurrencyFE.getAdf6())) {
					String adf6 = camsPay ? accountCurrencyFE.getAdf6()
							: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf6());
					newAccountCurrency.setAdf6(adf6);
				} else {
					newAccountCurrency.setAdf6("");
				}

				if (StringUtils.isNotBlank(accountCurrencyFE.getAdf7())) {
					String adf7 = camsPay ? accountCurrencyFE.getAdf7()
							: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf7());
					newAccountCurrency.setAdf7(adf7);
				} else {
					newAccountCurrency.setAdf7("");
				}

				newAccountCurrency.setDirectTxn(accountCurrencyFE.isDirectTxn());
				newAccountCurrency.setAcqPayId(acquirer.getPayId());
				accountCurrencySet.add(newAccountCurrency);
				// add charging detail
				// get existing charging details and add them to account
				Set<Payment> paymentSet = account.getPayments();

				for (Payment payment : paymentSet) {
					Set<Mop> mops = payment.getMops();
					for (Mop mop : mops) {
						if ((payment.getPaymentType().equals(PaymentType.WALLET))) {
							Set<MopTransaction> mopTxnSet = mop.getMopTransactionTypes();
							for (MopTransaction mopTxn : mopTxnSet) {
								ChargingDetails newChargingDetails = new ChargingDetails();
								newChargingDetails = maintainChargingDetails.createChargingDetail(
										payment.getPaymentType(), mop.getMopType(), acquirer.getBusinessName(), payId,
										accountCurrencyFE.getCurrencyCode());
								newChargingDetails.setTransactionType(mopTxn.getTransactionType());
								account.addChargingDetail(newChargingDetails);
							}
						} // changes for mapping for net-banking by vijaya
						else if (payment.getPaymentType().equals(PaymentType.NET_BANKING)) {
							ChargingDetails newChargingDetails = new ChargingDetails();
							newChargingDetails = maintainChargingDetails.createChargingDetail(payment.getPaymentType(),
									mop.getMopType(), acquirer.getBusinessName(), payId,
									accountCurrencyFE.getCurrencyCode());
							newChargingDetails.setTransactionType(TransactionType.SALE);
							account.addChargingDetail(newChargingDetails);
						}
						// end
						else {
							Set<MopTransaction> mopTxnSet = mop.getMopTransactionTypes();
							for (MopTransaction mopTxn : mopTxnSet) {
								ChargingDetails newChargingDetails = new ChargingDetails();
								newChargingDetails = maintainChargingDetails.createChargingDetail(
										payment.getPaymentType(), mop.getMopType(), acquirer.getBusinessName(), payId,
										accountCurrencyFE.getCurrencyCode());
								newChargingDetails.setTransactionType(mopTxn.getTransactionType());
								account.addChargingDetail(newChargingDetails);
							}
						}
					}
				}
			}
		}
		return account;
	}

	public Account addAccountCurrencyForNewTdr(Account account, AccountCurrency[] selectedAccountCurrency,
			User acquirer, String payId, boolean international, boolean domestic) {
		if (null == selectedAccountCurrency || selectedAccountCurrency.length == 0) {
			return account;
		}
		Set<AccountCurrency> accountCurrencySet = account.getAccountCurrencySet();
		for (AccountCurrency accountCurrencyFE : selectedAccountCurrency) {
			boolean flag = false;
			Iterator<AccountCurrency> accountCurrencySetItrator = account.getAccountCurrencySet().iterator();
			while (accountCurrencySetItrator.hasNext()) {
				AccountCurrency accountCurrency = accountCurrencySetItrator.next();
				if (accountCurrency.getCurrencyCode().equals(accountCurrencyFE.getCurrencyCode())) {
					flag = true;
					// edit password and other details
					accountCurrency.setMerchantId(accountCurrencyFE.getMerchantId());
					accountCurrency.setTxnKey(accountCurrencyFE.getTxnKey());
					accountCurrency.setAdf1(accountCurrencyFE.getAdf1());
					accountCurrency.setAdf2(accountCurrencyFE.getAdf2());
					accountCurrency.setAdf3(accountCurrencyFE.getAdf3());
					accountCurrency.setAdf4(accountCurrencyFE.getAdf4());
					accountCurrency.setAdf5(accountCurrencyFE.getAdf5());
					accountCurrency.setAdf8(accountCurrencyFE.getAdf8());
					accountCurrency.setAdf9(accountCurrencyFE.getAdf9());
					accountCurrency.setAdf10(accountCurrencyFE.getAdf10());
					accountCurrency.setAdf11(accountCurrencyFE.getAdf11());

					// Set values for Encrypted Fields
					if (accountCurrencyFE.getPassword() != null && !accountCurrencyFE.getPassword().trim().isEmpty()) {
						accountCurrency.setPassword(awsEncryptDecryptService.encrypt(accountCurrencyFE.getPassword()));
					} else {
						accountCurrency.setPassword("");
					}

					boolean camsPay = StringUtils.equalsIgnoreCase(acquirer.getBusinessName(),
							AcquirerType.CAMSPAY.getCode());
					if (StringUtils.isNotBlank(accountCurrencyFE.getAdf6())) {
						String adf6 = camsPay ? accountCurrencyFE.getAdf6()
								: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf6());
						accountCurrency.setAdf6(adf6);
					} else {
						accountCurrency.setAdf6("");
					}

					if (StringUtils.isNotBlank(accountCurrencyFE.getAdf7())) {
						String adf7 = camsPay ? accountCurrencyFE.getAdf7()
								: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf7());
						accountCurrency.setAdf7(adf7);
					} else {
						accountCurrency.setAdf7("");
					}

					accountCurrency.setDirectTxn(accountCurrencyFE.isDirectTxn());
				}
			}
			if (!flag) {
				AccountCurrency newAccountCurrency = new AccountCurrency();
				newAccountCurrency.setCurrencyCode(accountCurrencyFE.getCurrencyCode());

				newAccountCurrency.setMerchantId(accountCurrencyFE.getMerchantId());
				newAccountCurrency.setTxnKey(accountCurrencyFE.getTxnKey());
				newAccountCurrency.setAdf1(accountCurrencyFE.getAdf1());
				newAccountCurrency.setAdf2(accountCurrencyFE.getAdf2());
				newAccountCurrency.setAdf3(accountCurrencyFE.getAdf3());
				newAccountCurrency.setAdf4(accountCurrencyFE.getAdf4());
				newAccountCurrency.setAdf5(accountCurrencyFE.getAdf5());
				newAccountCurrency.setAdf8(accountCurrencyFE.getAdf8());
				newAccountCurrency.setAdf9(accountCurrencyFE.getAdf9());
				newAccountCurrency.setAdf10(accountCurrencyFE.getAdf10());
				newAccountCurrency.setAdf11(accountCurrencyFE.getAdf11());

				// Set values for Encrypted Fields
				if (accountCurrencyFE.getPassword() != null && !accountCurrencyFE.getPassword().trim().isEmpty()) {
					newAccountCurrency.setPassword(awsEncryptDecryptService.encrypt(accountCurrencyFE.getPassword()));
				} else {
					newAccountCurrency.setPassword("");
				}

				boolean camsPay = StringUtils.equalsIgnoreCase(acquirer.getBusinessName(),
						AcquirerType.CAMSPAY.getCode());
				if (StringUtils.isNotBlank(accountCurrencyFE.getAdf6())) {
					String adf6 = camsPay ? accountCurrencyFE.getAdf6()
							: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf6());
					newAccountCurrency.setAdf6(adf6);
				} else {
					newAccountCurrency.setAdf6("");
				}

				if (StringUtils.isNotBlank(accountCurrencyFE.getAdf7())) {
					String adf7 = camsPay ? accountCurrencyFE.getAdf7()
							: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf7());
					newAccountCurrency.setAdf7(adf7);
				} else {
					newAccountCurrency.setAdf7("");
				}

				newAccountCurrency.setDirectTxn(accountCurrencyFE.isDirectTxn());
				newAccountCurrency.setAcqPayId(acquirer.getPayId());
				accountCurrencySet.add(newAccountCurrency);
				// add charging detail
				// get existing charging details and add them to account
				Set<Payment> paymentSet = account.getPayments();
				Set<PaymentInternational> paymentSetInternational = account.getPaymentsInternational();

				for (Payment payment : paymentSet) {
					Set<Mop> mops = payment.getMops();
					for (Mop mop : mops) {
						if ((payment.getPaymentType().equals(PaymentType.WALLET))) {
							Set<MopTransaction> mopTxnSet = mop.getMopTransactionTypes();
							for (MopTransaction mopTxn : mopTxnSet) {
								TdrSetting newChargingDetails = new TdrSetting();
								TdrSetting newChargingDetails1 = new TdrSetting();

								

								newChargingDetails = maintainChargingDetails.createChargingDetailForNewTdrSetting(
										payment.getPaymentType(), mop.getMopType(), acquirer.getBusinessName(), payId,
										accountCurrencyFE.getCurrencyCode());

								newChargingDetails1 = maintainChargingDetails
										.createChargingDetailForNewTdrSettingCommercial(payment.getPaymentType(),
												mop.getMopType(), acquirer.getBusinessName(), payId,
												accountCurrencyFE.getCurrencyCode());

								

								newChargingDetails.setTransactionType(mopTxn.getTransactionType().getName());
								newChargingDetails1.setTransactionType(mopTxn.getTransactionType().getName());
								logger.info("AddTdrSetting in  method 1");
								account.addTdrSetting(newChargingDetails);

								logger.info("AddTdrSetting in same method 1");
								account.addTdrSetting(newChargingDetails1);

								
							}
						} // changes for mapping for net-banking by vijaya
						else if (payment.getPaymentType().equals(PaymentType.NET_BANKING)) {
							TdrSetting newChargingDetails = new TdrSetting();
							TdrSetting newChargingDetails1 = new TdrSetting();
							

							newChargingDetails = maintainChargingDetails.createChargingDetailForNewTdrSetting(
									payment.getPaymentType(), mop.getMopType(), acquirer.getBusinessName(), payId,
									accountCurrencyFE.getCurrencyCode());
							newChargingDetails1 = maintainChargingDetails
									.createChargingDetailForNewTdrSettingCommercial(payment.getPaymentType(),
											mop.getMopType(), acquirer.getBusinessName(), payId,
											accountCurrencyFE.getCurrencyCode());

							

							newChargingDetails.setTransactionType(TransactionType.SALE.getName());
							newChargingDetails1.setTransactionType(TransactionType.SALE.getName());
							logger.info("AddTdrSetting in  method 2");
							account.addTdrSetting(newChargingDetails);

							logger.info("AddTdrSetting in same method 2");
							account.addTdrSetting(newChargingDetails1);

							
						}
						// end
						else {
							Set<MopTransaction> mopTxnSet = mop.getMopTransactionTypes();
							for (MopTransaction mopTxn : mopTxnSet) {
								TdrSetting newChargingDetails = new TdrSetting();
								TdrSetting newChargingDetails1 = new TdrSetting();
								
								
								newChargingDetails = maintainChargingDetails.createChargingDetailForNewTdrSetting(
										payment.getPaymentType(), mop.getMopType(), acquirer.getBusinessName(), payId,
										accountCurrencyFE.getCurrencyCode());
								newChargingDetails1 = maintainChargingDetails
										.createChargingDetailForNewTdrSettingCommercial(payment.getPaymentType(),
												mop.getMopType(), acquirer.getBusinessName(), payId,
												accountCurrencyFE.getCurrencyCode());
								
								
								
								
								newChargingDetails.setTransactionType(mopTxn.getTransactionType().getName());
								newChargingDetails1.setTransactionType(mopTxn.getTransactionType().getName());
								logger.info("AddTdrSetting in  method 3");
								account.addTdrSetting(newChargingDetails);
								logger.info("AddTdrSetting in same method 3");
								account.addTdrSetting(newChargingDetails1);
								
							}
						}
					}
				}
				
				for (PaymentInternational payment : paymentSetInternational) {
					Set<MopInternational> mops = payment.getMops();
					for (MopInternational mop : mops) {
						if ((payment.getPaymentType().equals(PaymentType.WALLET))) {
							Set<MopTransactionInternational> mopTxnSet = mop.getMopTransactionTypes();
							for (MopTransactionInternational mopTxn : mopTxnSet) {
								

								TdrSetting newChargingDetails2 = new TdrSetting();

								TdrSetting newChargingDetails3 = new TdrSetting();

								

								newChargingDetails2 = maintainChargingDetails
										.createChargingDetailForNewTdrSettingInternational(payment.getPaymentType(),
												mop.getMopType(), acquirer.getBusinessName(), payId,
												accountCurrencyFE.getCurrencyCode());

								newChargingDetails3 = maintainChargingDetails
										.createChargingDetailForNewTdrSettingInternationalCommercial(
												payment.getPaymentType(), mop.getMopType(), acquirer.getBusinessName(),
												payId, accountCurrencyFE.getCurrencyCode());

								
								newChargingDetails2.setTransactionType(mopTxn.getTransactionType().getName());
								newChargingDetails3.setTransactionType(mopTxn.getTransactionType().getName());

								logger.info("AddTdrSetting in  method 4");
								account.addTdrSetting(newChargingDetails2);

								logger.info("AddTdrSetting in same method 4");
								account.addTdrSetting(newChargingDetails3);
							}
						} // changes for mapping for net-banking by vijaya
						else if (payment.getPaymentType().equals(PaymentType.NET_BANKING)) {
							
							TdrSetting newChargingDetails2 = new TdrSetting();
							TdrSetting newChargingDetails3 = new TdrSetting();

						

							newChargingDetails2 = maintainChargingDetails
									.createChargingDetailForNewTdrSettingInternational(payment.getPaymentType(),
											mop.getMopType(), acquirer.getBusinessName(), payId,
											accountCurrencyFE.getCurrencyCode());
							newChargingDetails3 = maintainChargingDetails
									.createChargingDetailForNewTdrSettingInternationalCommercial(
											payment.getPaymentType(), mop.getMopType(), acquirer.getBusinessName(),
											payId, accountCurrencyFE.getCurrencyCode());
							newChargingDetails2.setTransactionType(TransactionType.SALE.getName());
							newChargingDetails3.setTransactionType(TransactionType.SALE.getName());

							logger.info("AddTdrSetting in  method 5");
							account.addTdrSetting(newChargingDetails2);

							logger.info("AddTdrSetting in same method 5");
							account.addTdrSetting(newChargingDetails3);
						}
						// end
						else {
							Set<MopTransactionInternational> mopTxnSet = mop.getMopTransactionTypes();
							for (MopTransactionInternational mopTxn : mopTxnSet) {
								
								TdrSetting newChargingDetails2 = new TdrSetting();
								TdrSetting newChargingDetails3 = new TdrSetting();
								
							
								
								newChargingDetails2 = maintainChargingDetails.createChargingDetailForNewTdrSettingInternational(
										payment.getPaymentType(), mop.getMopType(), acquirer.getBusinessName(), payId,
										accountCurrencyFE.getCurrencyCode());
								newChargingDetails3 = maintainChargingDetails
										.createChargingDetailForNewTdrSettingInternationalCommercial(payment.getPaymentType(),
												mop.getMopType(), acquirer.getBusinessName(), payId,
												accountCurrencyFE.getCurrencyCode());
								newChargingDetails2.setTransactionType(mopTxn.getTransactionType().getName());
								newChargingDetails3.setTransactionType(mopTxn.getTransactionType().getName());

								logger.info("AddTdrSetting in  method 6");
								account.addTdrSetting(newChargingDetails2);

								logger.info("AddTdrSetting in same method 6");
								account.addTdrSetting(newChargingDetails3);
							}
						}
					}
				}
			}
		}
		return account;
	}

	// remove code
	public Account removeAccountCurrency(Account account, AccountCurrency[] selectedAccountCurrency) {
		Iterator<AccountCurrency> accountCurrencySetItrator = account.getAccountCurrencySet().iterator();
		Set<ChargingDetails> chargingDetails = account.getChargingDetails();

		if (null == selectedAccountCurrency || selectedAccountCurrency.length == 0) {
			selectedAccountCurrency = new AccountCurrency[] {};
		}
		while (accountCurrencySetItrator.hasNext()) {
			boolean flag = false;
			AccountCurrency accountCurrency = accountCurrencySetItrator.next();
			for (AccountCurrency accountCurrencyFE : selectedAccountCurrency) {
				if (accountCurrency.getCurrencyCode().equals(accountCurrencyFE.getCurrencyCode())) {
					flag = true;
				}
			}
			if (!flag) {
				for (ChargingDetails chargingDetail : chargingDetails) {
					if (chargingDetail.getStatus().equals(TDRStatus.ACTIVE)
							&& chargingDetail.getCurrency().equals(accountCurrency.getCurrencyCode())) {
						chargingDetail.setStatus(TDRStatus.INACTIVE);
						chargingDetail.setUpdatedDate(new Date());
					}
				}
				accountCurrencySetItrator.remove();
			}
		}
		return account;
	}

	public Account removeAccountCurrencyForNewTdrSetting(Account account, AccountCurrency[] selectedAccountCurrency,
			boolean international, boolean domestic) {
		Iterator<AccountCurrency> accountCurrencySetItrator = account.getAccountCurrencySet().iterator();
		Set<TdrSetting> chargingDetails = account.getTdrSetting();

		if (null == selectedAccountCurrency || selectedAccountCurrency.length == 0) {
			selectedAccountCurrency = new AccountCurrency[] {};
		}
		while (accountCurrencySetItrator.hasNext()) {
			boolean flag = false;
			AccountCurrency accountCurrency = accountCurrencySetItrator.next();
			for (AccountCurrency accountCurrencyFE : selectedAccountCurrency) {
				if (accountCurrency.getCurrencyCode().equals(accountCurrencyFE.getCurrencyCode())) {
					flag = true;
				}
			}
			if (!flag) {
				for (TdrSetting chargingDetail : chargingDetails) {
					if (chargingDetail.getStatus().equals(TDRStatus.ACTIVE.toString())
							&& chargingDetail.getCurrency().equals(accountCurrency.getCurrencyCode())) {
						chargingDetail.setStatus(TDRStatus.INACTIVE.toString());
						// chargingDetail.setFromDate(new Date());
					}
					
				}
				accountCurrencySetItrator.remove();
			}
		}
		return account;
	}

	public List<String> getCurrencyList() {
		List<String> mappedCurrency = new ArrayList<String>();
		Set<AccountCurrency> accountCurrencySet = account.getAccountCurrencySet();
		for (AccountCurrency accountCurrency : accountCurrencySet) {
			mappedCurrency.add(accountCurrency.getCurrencyCode());
		}
		return mappedCurrency;
	}
}
