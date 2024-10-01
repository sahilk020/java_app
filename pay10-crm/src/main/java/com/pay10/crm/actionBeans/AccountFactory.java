package com.pay10.crm.actionBeans;

import java.util.*;
import java.util.stream.Collectors;

import com.pay10.commons.dao.PendingMappingRequestDao;
import com.pay10.commons.user.*;
import com.pay10.crm.action.MerchantMappingAction;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.owasp.esapi.util.CollectionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.kms.AWSEncryptDecryptService;
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

	@Autowired
	PendingMappingRequestDao pendingMappingRequestDao;

	@Autowired
	UserDao userDao;
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

	public Account editAccount(Account oldAccount, String mapString, String payId) {
		this.account = oldAccount;
		logger.info("mapString"+mapString);
		String acquirerName = oldAccount.getAcquirerName();
		savedPaymentSet = account.getPayments();
		logger.info("savedPaymentSet in editAccount :::"+savedPaymentSet);
		savedPaymentSetInternational = account.getPaymentsInternational();
		logger.info("savedPaymentSetInternational in editAccount :::"+savedPaymentSetInternational);

		if (mapString.equals("")) {
			String[] dummytokens = { "" };
			removeMapping(dummytokens, acquirerName, payId, account.getId());
			removeMappingInternational(dummytokens, acquirerName, payId, account.getId());
			return account;
		}
		String[] tokens = mapString.split(",");
		// remove old mappings
		removeMapping(tokens, acquirerName, payId, account.getId());
		removeMappingInternational(tokens, acquirerName, payId, account.getId());

		// new mappings added

		addMapping(tokens, acquirerName, payId);

		logger.info("tokens ::: " + Arrays.toString(tokens) + "acquirerName :::" +acquirerName+ "payId :::" +payId);
		addMappingInternational(tokens, acquirerName, payId);

		return account;
	}

	public void removeMapping(String[] tokens, String acquirer, String payId, Long accountId) {

		PendingMappingRequest pendingMappingRequest = pendingMappingRequestDao.findActiveMappingRequest(userDao.getEmailIdByPayId(payId), acquirer);
		Set<Payment> paymentSet = account.getPayments();
		logger.info("PaymentSet in removeMapping :::: "+paymentSet);
		String[] savedTokens = pendingMappingRequest.getMapString().split(CrmFieldConstants.COMMA.getValue());

		logger.info("Get MappingString in removeMapping Method via PMR ::::"+pendingMappingRequest.getMapString());
		logger.info("Saved Tokens ::::"+ Arrays.toString(savedTokens));
		for (String savedToken : savedTokens) {
			boolean isPresent = false;
			for (String token : tokens) {
				if (token.equals(savedToken)) {
					isPresent = true;
				}
			}
			// disable charging detail
			logger.info("Is Present ::::"+isPresent);
			if (!isPresent) {
				removeToken(paymentSet, savedToken);
				logger.info("Payment Set ::::"+paymentSet);
				account.disableTdrSettingDomestic(savedToken);

			}
		}

	}

	public void removeMappingInternational(String[] tokens, String acquirer, String payId, Long accountId) {
		PendingMappingRequest pendingMappingRequest = pendingMappingRequestDao.findActiveMappingRequest(userDao.getEmailIdByPayId(payId), acquirer);
		Set<PaymentInternational> paymentSetInternational = account.getPaymentsInternational();
		logger.info("PaymentSet in removeMapping :::: "+paymentSetInternational);
		String[] savedTokensInternational = pendingMappingRequest.getMapString().split(CrmFieldConstants.COMMA.getValue());

		logger.info("Get MappingString in removeMapping Method via PMR ::::"+pendingMappingRequest.getMapString());
		logger.info("Saved Tokens ::::"+ Arrays.toString(savedTokensInternational));
		for (String savedToken : savedTokensInternational) {
			boolean isPresent = false;
			for (String token : tokens) {
				if (token.equals(savedToken)) {
					isPresent = true;
				}
			}
			// disable charging detail
			logger.info("Is Present ::::"+isPresent);
			if (!isPresent) {
				removeTokenInternational(paymentSetInternational, savedToken);
				logger.info("PaymentSetInternational  :::::"+paymentSetInternational);

				account.disableTdrSettingInterNational(savedToken);

			}

		}

	}

	public void removeToken(Set<Payment> paymentSet, String savedToken) {
		String[] splittedToken = savedToken.split("-");
//		logger.info("Saved Token in removeToken Method ::::"+savedToken);

		if (checkPaymentType(paymentSet, savedToken)) {
			if (checkMopType(savedToken)) {
				if (splittedToken.length == 4) {
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
				if (splittedToken.length == 4) {
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

	public void addMapping(String[] tokens, String acquirerName, String payId) {
		logger.info("Tokens in addMapping" + Arrays.toString(tokens));
		try{
			for (String token : tokens) {
				String[] splitedToken = token.split("-");
				String mappedCurrency = splitedToken[0];

				if (!checkPaymentType(savedPaymentSet, token)) {
					
					Payment payment = new Payment();
					payment.setPaymentType(PaymentType.getInstance(splitedToken[1]));
					logger.info("PaymentType splited by 1 ::::" + PaymentType.getInstance(splitedToken[1]));
//				payment.setPaymentType(PaymentType.getInstance(splitedToken[1]));
					logger.info("Payment ::::::" + payment);

					logger.info("Mapped Currency ::::::::" + mappedCurrency);

					Mop newMop = new Mop();
					newMop.setMopType(MopType.getmop(splitedToken[2]));
					logger.info("NEW MOP in addMapping Method :::::" + newMop);
					payment.addMop(newMop);

					if (splitedToken.length == 4) {
						MopTransaction newMopTxn = new MopTransaction();
						newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[3]));
						newMop.addMopTransaction(newMopTxn);
					}
					account.addPayment(payment);
					if (account.getAccountCurrency(mappedCurrency)!=null && account.getAccountCurrency(mappedCurrency).getId()!=null && account.getAccountCurrency(mappedCurrency).getId()>0) {
						logger.info("addChargingDetail Domestic 1");
						addChargingDetail(acquirerName, payId, token, mappedCurrency);
					}
					continue;
				}
				// if mop not present
				if (!checkMopType(token)) {

					Mop newMop = new Mop();
					if (splitedToken.length == 4) {
						MopTransaction newMopTxn = new MopTransaction();
						newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[3]));
						newMop.addMopTransaction(newMopTxn);
					}

					newMop.setMopType(MopType.getmop(splitedToken[2]));
					presentPayment.addMop(newMop);
					if (account.getAccountCurrency(mappedCurrency)!=null && account.getAccountCurrency(mappedCurrency).getId()!=null && account.getAccountCurrency(mappedCurrency).getId()>0) {
						logger.info("addChargingDetail Domestic 2");
						addChargingDetail(acquirerName, payId, token, mappedCurrency);
					}
					continue;
				}

				if (!checkTxnType(token)) { // if txntype not present
					if (splitedToken.length == 3) {
						continue;
					}
					MopTransaction newMopTxn = new MopTransaction();
					newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[3]));
					presentMop.addMopTransaction(newMopTxn);
					if (account.getAccountCurrency(mappedCurrency)!=null && account.getAccountCurrency(mappedCurrency).getId()!=null && account.getAccountCurrency(mappedCurrency).getId()>0) {
						logger.info("addChargingDetail Domestic 3");
						addChargingDetail(acquirerName, payId, token, mappedCurrency);
					}
				}

			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void addMappingInternational(String[] tokens, String acquirerName, String payId) {
		try {
			for (String token : tokens) {
				logger.info("addMappingInternational in token :::" + token);
				String[] splitedToken = token.split("-");
				String mappedCurrency = splitedToken[0];

				if (!checkPaymentTypeInternational(savedPaymentSetInternational, token)) {

					PaymentInternational payment = new PaymentInternational();
					payment.setPaymentType(PaymentType.getInstance(splitedToken[1]));

					MopInternational newMop = new MopInternational();
					newMop.setMopType(MopType.getmop(splitedToken[2]));

					payment.addMop(newMop);

					if (splitedToken.length == 4) {
						MopTransactionInternational newMopTxn = new MopTransactionInternational();
						newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[3]));
						newMop.addMopTransaction(newMopTxn);
					}
					account.addPaymentsInternational(payment);
					if (account.getAccountCurrency(mappedCurrency)!=null && account.getAccountCurrency(mappedCurrency).getId() != null && account.getAccountCurrency(mappedCurrency).getId() > 0) {
						logger.info("addChargingDetail International 1");
						addChargingDetailInternational(acquirerName, payId, token);
					}
					continue;
				}
				// if mop not present
				if (!checkMopTypeInternational(token)) {

					MopInternational newMop = new MopInternational();
					if (splitedToken.length == 4) {
						MopTransactionInternational newMopTxn = new MopTransactionInternational();
						newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[3]));
						newMop.addMopTransaction(newMopTxn);
					}

					newMop.setMopType(MopType.getmop(splitedToken[2]));
					presentPaymentInternational.addMop(newMop);
					if (account.getAccountCurrency(mappedCurrency)!=null && account.getAccountCurrency(mappedCurrency).getId() != null && account.getAccountCurrency(mappedCurrency).getId() > 0) {
						logger.info("addChargingDetail International 2");

						addChargingDetailInternational(acquirerName, payId, token);
					}
					continue;
				}

				if (!checkTxnTypeInternational(token)) { // if txntype not present
//				logger.info("checkTxnTypeInternational token :::"+token);
					if (splitedToken.length == 3) {
						continue;
					}
					MopTransactionInternational newMopTxn = new MopTransactionInternational();
					newMopTxn.setTransactionType(TransactionType.getInstanceFromCode(splitedToken[3]));
					presentMopInternational.addMopTransaction(newMopTxn);
					if (account.getAccountCurrency(mappedCurrency)!=null && account.getAccountCurrency(mappedCurrency).getId() != null && account.getAccountCurrency(mappedCurrency).getId() > 0) {
						logger.info("addChargingDetail International 3");

						addChargingDetailInternational(acquirerName, payId, token);
					}
				}
			}

		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addChargingDetail(String acquirerName, String payId, String token, String currencyCode) {

		account.addTdrSetting(maintainChargingDetails.createTDRDetail(acquirerName, payId, token, currencyCode));
		logger.info("Add TDR FOR Domestic Consumer in Edit");

		account.addTdrSetting(maintainChargingDetails.createTDRDetailCommercial(acquirerName, payId, token, currencyCode));
		logger.info("Add TDR FOR Domestic Commercial in Edit");

	}

	public void addChargingDetailInternational(String acquirerName, String payId, String token) {

		account.addTdrSetting(maintainChargingDetails.createTDRDetailInternationalConsumer(acquirerName, payId, token));
		logger.info("Add TDR FOR International Consumer in Edit");

		account.addTdrSetting(maintainChargingDetails.createTDRDetailInternationalCommercial(acquirerName, payId, token));
		logger.info("Add TDR FOR International Commercial in Edit");

	}

	public boolean checkPaymentType(Set<Payment> paymentSet, String token) {
		boolean isPresent = false;
		logger.info("paymentSet in checkPaymentType"+paymentSet);
		Iterator<Payment> paymentItr = paymentSet.iterator();

		String[] splittedToken = token.split("-");
		while (paymentItr.hasNext()) {
			Payment currentPayment = paymentItr.next();

			logger.info("currentPayment 1::::"+currentPayment);
			logger.info("splittedToken[1] checkPaymentType"+splittedToken[1]);
			if (currentPayment.getPaymentType().getName().equals(splittedToken[1])) {
				isPresent = true;
				logger.info("splitted payment type::::"+splittedToken[1]);
				logger.info("current payment type::::"+currentPayment.getPaymentType().getName());
				this.presentPayment = currentPayment;
				logger.info("present payment:::::"+presentPayment);
			}
		}
		logger.info("Is Present in return :::::::"+isPresent);
		return isPresent;
	}
	public boolean checkPaymentTypeInternational(Set<PaymentInternational> paymentSet, String token) {
		boolean isPresent = false;

		logger.info("paymentSet in checkPaymentTypeInternational"+paymentSet);
		Iterator<PaymentInternational> paymentItr = paymentSet.iterator();
		String[] splittedToken = token.split("-");
		while (paymentItr.hasNext()) {
			PaymentInternational currentPayment = paymentItr.next();
			logger.info("currentPayment 1::::"+currentPayment);
			logger.info("splittedToken[1] checkPaymentTypeInternational"+splittedToken[1]);
			if (currentPayment.getPaymentType().getName().equals(splittedToken[1])) {
				isPresent = true;
				logger.info("splitted payment type::::"+splittedToken[1]);
				logger.info("current payment type::::"+currentPayment.getPaymentType().getName());
				this.presentPaymentInternational = currentPayment;
				logger.info("present payment:::::"+presentPayment);
			}
		}
		logger.info("Is Present in return :::::::"+isPresent);
		return isPresent;
	}

	public boolean checkMopType(String token) {
		boolean isPresent = false;
		String[] splittedToken = token.split("-");
		Set<Mop> presentMopSet = presentPayment.getMops();
		Iterator<Mop> mopItr = presentMopSet.iterator();

		while (mopItr.hasNext()) {
			Mop currentMop = mopItr.next();
			if (currentMop.getMopType().getCode().equals(splittedToken[2])) {
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
			if (currentMop.getMopType().getCode().equals(splittedToken[2])) {
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
			if (currentMopTxn.getTransactionType().getCode().equals(splittedToken[3])) {
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
			if (currentMopTxn.getTransactionType().getCode().equals(splittedToken[3])) {
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
					accountCurrency.setInternational(accountCurrencyFE.isInternational());
					accountCurrency.setDomestic(accountCurrencyFE.isDomestic());

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
				newAccountCurrency.setInternational(accountCurrencyFE.isInternational());
				newAccountCurrency.setDomestic(accountCurrencyFE.isDomestic());

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
											   User acquirer, String payId, boolean international, boolean domestic, String mappingString) {
		if (null == selectedAccountCurrency || selectedAccountCurrency.length == 0) {
			return account;
		}
		int length = selectedAccountCurrency.length;
		logger.info("Length :::"+length);
		int count = 0;
		logger.info("Selected Account Currency ::::"+ Arrays.toString(selectedAccountCurrency));
		Set<AccountCurrency> accountCurrencySet = account.getAccountCurrencySet();
		logger.info("Account Currency Set ::::"+accountCurrencySet);
		logger.info("mappingString :::"+mappingString);
		
		for (AccountCurrency accountCurrencyFE : selectedAccountCurrency) {

			count++;
			logger.info("Count :::" + count);
			boolean flag = false;
			Iterator<AccountCurrency> accountCurrencySetItrator = account.getAccountCurrencySet().iterator();
			logger.info("Account Currency Set Iterator ::::" + accountCurrencySetItrator);
			while (accountCurrencySetItrator.hasNext()) {
				logger.info("AccountCurrencyFE.getCurrencyCode() ::" + accountCurrencyFE.getCurrencyCode());
				AccountCurrency accountCurrency = accountCurrencySetItrator.next();
				logger.info("AccountCurrency.getCurrencyCode() ::" + accountCurrency.getCurrencyCode());

				if (accountCurrency.getCurrencyCode().equals(accountCurrencyFE.getCurrencyCode())) {
					flag = true;
					logger.info("Flag 1 :::" + flag);
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
					accountCurrency.setInternational(accountCurrencyFE.isInternational());
					accountCurrency.setDomestic(accountCurrencyFE.isDomestic());

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
				logger.info("Flag 2 :::" + flag);
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
				newAccountCurrency.setInternational(accountCurrencyFE.isInternational());
				newAccountCurrency.setDomestic(accountCurrencyFE.isDomestic());

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

			}
		}
	

		//Mapping Set for FrontEnd Entry
		HashSet<String> mapFE = new HashSet<>();
		for (String mapStringFE : mappingString.split(CrmFieldConstants.COMMA.getValue())){
			logger.info("mapStringFE in addAccountCurrencyForNewTdr :::"+mapStringFE);
			String data = mapStringFE.split("-")[1];
			logger.info("Splitting MapString ::"+data);
			if(data.equalsIgnoreCase("Net Banking")){
				String data1 = mapStringFE+"-SALE";
				mapFE.add(data1);
			}
			else {
				mapFE.add(mapStringFE);
			}
		}
		logger.info("mapFE in addAccountCurrencyForNewTdr :::::"+mapFE);

		//Mapping Set for BackEnd Entry
		Set<TdrSetting> chargingDetails = account.getTdrSetting();
		HashSet<String> mapBE = new HashSet<>();
		for (TdrSetting tdrSettingBE : chargingDetails){
			String mopBE = tdrSettingBE.getCurrency()+"-"+PaymentType.getInstanceUsingStringValue(tdrSettingBE.getPaymentType()).getName()+"-"+MopType.getCodeusingInstance(tdrSettingBE.getMopType())+"-"+tdrSettingBE.getTransactionType();
			mapBE.add(mopBE);
		}
		logger.info("mapBE in addAccountCurrencyForNewTdr:::::"+mapBE);

		//Removing entry from BackEnd mappingString for adding new one
		mapFE.removeAll(mapBE);

		logger.info("After removing mapFE from mapBE in addAccountCurrencyForNewTdr ::::"+mapFE);


		//Checking the Set for Adding new TDR
		if(!mapFE.isEmpty()) {
			logger.info("ADDING TDR ENTRY <<<<<>>>>>>");
			logger.info("TDR entry for this Mapping String :::"+mapFE);

			for(String entryNewTDR : mapFE){
				String[] splittedMappedString = entryNewTDR.split("-");
				String currencyCode = splittedMappedString[0];
				String paymentType = splittedMappedString[1];
				String mopType = splittedMappedString[2];
				logger.info("currencyCode: "+currencyCode+ "paymentType: "+paymentType+ "mopType: "+mopType);

				TdrSetting newChargingDetails1 = new TdrSetting();
				TdrSetting newChargingDetails2 = new TdrSetting();
				TdrSetting newChargingDetails3 = new TdrSetting();
				TdrSetting newChargingDetails4 = new TdrSetting();

				//TDR FOR Domestic Consumer
				newChargingDetails1 = maintainChargingDetails.createChargingDetailForNewTdrSetting(
										PaymentType.getInstance(paymentType),
										MopType.getInstanceUsingCode(mopType),
										acquirer.getBusinessName(),
										payId, currencyCode);
				newChargingDetails1.setTransactionType("SALE");

				logger.info("Add TDR FOR Domestic Consumer");
				account.addTdrSetting(newChargingDetails1);


				//TDR FOR Domestic Commercial
				newChargingDetails2 = maintainChargingDetails.createChargingDetailForNewTdrSettingCommercial(
										PaymentType.getInstance(paymentType),
										MopType.getInstanceUsingCode(mopType),
										acquirer.getBusinessName(),
										payId, currencyCode);
				newChargingDetails2.setTransactionType("SALE");

				logger.info("Add TDR FOR Domestic Commercial");
				account.addTdrSetting(newChargingDetails2);


				//TDR FOR International Consumer
				newChargingDetails3 = maintainChargingDetails.createChargingDetailForNewTdrSettingInternational(
										PaymentType.getInstance(paymentType),
										MopType.getInstanceUsingCode(mopType),
										acquirer.getBusinessName(),
										payId, currencyCode);
				newChargingDetails3.setTransactionType("SALE");

				logger.info("Add TDR FOR International Consumer");
				account.addTdrSetting(newChargingDetails3);


				//TDR FOR International Commercial
				newChargingDetails4 = maintainChargingDetails.createChargingDetailForNewTdrSettingInternationalCommercial(
										PaymentType.getInstance(paymentType),
										MopType.getInstanceUsingCode(mopType),
										acquirer.getBusinessName(),
										payId, currencyCode);
				newChargingDetails4.setTransactionType("SALE");

				logger.info("Add TDR FOR International Commercial");
				account.addTdrSetting(newChargingDetails4);
			}



//			for (AccountCurrency accountCurrencyFE : selectedAccountCurrency) {
//
//				count++;
//				logger.info("Count :::" + count);
//				boolean flag = false;
//				Iterator<AccountCurrency> accountCurrencySetItrator = account.getAccountCurrencySet().iterator();
//				logger.info("Account Currency Set Iterator ::::" + accountCurrencySetItrator);
//				while (accountCurrencySetItrator.hasNext()) {
//					logger.info("AccountCurrencyFE.getCurrencyCode() ::" + accountCurrencyFE.getCurrencyCode());
//					AccountCurrency accountCurrency = accountCurrencySetItrator.next();
//					logger.info("AccountCurrency.getCurrencyCode() ::" + accountCurrency.getCurrencyCode());
//
//					if (accountCurrency.getCurrencyCode().equals(accountCurrencyFE.getCurrencyCode())) {
//						flag = true;
//						logger.info("Flag 1 :::" + flag);
//						// edit password and other details
//						accountCurrency.setMerchantId(accountCurrencyFE.getMerchantId());
//						accountCurrency.setTxnKey(accountCurrencyFE.getTxnKey());
//						accountCurrency.setAdf1(accountCurrencyFE.getAdf1());
//						accountCurrency.setAdf2(accountCurrencyFE.getAdf2());
//						accountCurrency.setAdf3(accountCurrencyFE.getAdf3());
//						accountCurrency.setAdf4(accountCurrencyFE.getAdf4());
//						accountCurrency.setAdf5(accountCurrencyFE.getAdf5());
//						accountCurrency.setAdf8(accountCurrencyFE.getAdf8());
//						accountCurrency.setAdf9(accountCurrencyFE.getAdf9());
//						accountCurrency.setAdf10(accountCurrencyFE.getAdf10());
//						accountCurrency.setAdf11(accountCurrencyFE.getAdf11());
//						accountCurrency.setInternational(accountCurrencyFE.isInternational());
//						accountCurrency.setDomestic(accountCurrencyFE.isDomestic());
//
//						// Set values for Encrypted Fields
//						if (accountCurrencyFE.getPassword() != null && !accountCurrencyFE.getPassword().trim().isEmpty()) {
//							accountCurrency.setPassword(awsEncryptDecryptService.encrypt(accountCurrencyFE.getPassword()));
//						} else {
//							accountCurrency.setPassword("");
//						}
//
//						boolean camsPay = StringUtils.equalsIgnoreCase(acquirer.getBusinessName(),
//								AcquirerType.CAMSPAY.getCode());
//						if (StringUtils.isNotBlank(accountCurrencyFE.getAdf6())) {
//							String adf6 = camsPay ? accountCurrencyFE.getAdf6()
//									: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf6());
//							accountCurrency.setAdf6(adf6);
//						} else {
//							accountCurrency.setAdf6("");
//						}
//
//						if (StringUtils.isNotBlank(accountCurrencyFE.getAdf7())) {
//							String adf7 = camsPay ? accountCurrencyFE.getAdf7()
//									: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf7());
//							accountCurrency.setAdf7(adf7);
//						} else {
//							accountCurrency.setAdf7("");
//						}
//
//						accountCurrency.setDirectTxn(accountCurrencyFE.isDirectTxn());
//					}
//				}
//				if (!flag) {
//					logger.info("Flag 2 :::" + flag);
//					AccountCurrency newAccountCurrency = new AccountCurrency();
//					newAccountCurrency.setCurrencyCode(accountCurrencyFE.getCurrencyCode());
//
//					newAccountCurrency.setMerchantId(accountCurrencyFE.getMerchantId());
//					newAccountCurrency.setTxnKey(accountCurrencyFE.getTxnKey());
//					newAccountCurrency.setAdf1(accountCurrencyFE.getAdf1());
//					newAccountCurrency.setAdf2(accountCurrencyFE.getAdf2());
//					newAccountCurrency.setAdf3(accountCurrencyFE.getAdf3());
//					newAccountCurrency.setAdf4(accountCurrencyFE.getAdf4());
//					newAccountCurrency.setAdf5(accountCurrencyFE.getAdf5());
//					newAccountCurrency.setAdf8(accountCurrencyFE.getAdf8());
//					newAccountCurrency.setAdf9(accountCurrencyFE.getAdf9());
//					newAccountCurrency.setAdf10(accountCurrencyFE.getAdf10());
//					newAccountCurrency.setAdf11(accountCurrencyFE.getAdf11());
//					newAccountCurrency.setInternational(accountCurrencyFE.isInternational());
//					newAccountCurrency.setDomestic(accountCurrencyFE.isDomestic());
//
//					// Set values for Encrypted Fields
//					if (accountCurrencyFE.getPassword() != null && !accountCurrencyFE.getPassword().trim().isEmpty()) {
//						newAccountCurrency.setPassword(awsEncryptDecryptService.encrypt(accountCurrencyFE.getPassword()));
//					} else {
//						newAccountCurrency.setPassword("");
//					}
//
//					boolean camsPay = StringUtils.equalsIgnoreCase(acquirer.getBusinessName(),
//							AcquirerType.CAMSPAY.getCode());
//					if (StringUtils.isNotBlank(accountCurrencyFE.getAdf6())) {
//						String adf6 = camsPay ? accountCurrencyFE.getAdf6()
//								: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf6());
//						newAccountCurrency.setAdf6(adf6);
//					} else {
//						newAccountCurrency.setAdf6("");
//					}
//
//					if (StringUtils.isNotBlank(accountCurrencyFE.getAdf7())) {
//						String adf7 = camsPay ? accountCurrencyFE.getAdf7()
//								: awsEncryptDecryptService.encrypt(accountCurrencyFE.getAdf7());
//						newAccountCurrency.setAdf7(adf7);
//					} else {
//						newAccountCurrency.setAdf7("");
//					}
//
//					newAccountCurrency.setDirectTxn(accountCurrencyFE.isDirectTxn());
//					newAccountCurrency.setAcqPayId(acquirer.getPayId());
//					accountCurrencySet.add(newAccountCurrency);
//
//				}
//			}
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
														 boolean international, boolean domestic, String mappingString) {
		Iterator<AccountCurrency> accountCurrencySetItrator = account.getAccountCurrencySet().iterator();
		logger.info(accountCurrencySetItrator.toString());
		Set<TdrSetting> chargingDetails = account.getTdrSetting();
//		logger.info("Charging Details ::::"+chargingDetails);
		logger.info("Account ::::::"+account);
		logger.info("Selected Account Currency ::::"+ Arrays.toString(selectedAccountCurrency));
		if (null == selectedAccountCurrency || selectedAccountCurrency.length == 0) {
			selectedAccountCurrency = new AccountCurrency[] {};
			logger.info("Condition IF");
		}

		//Incoming mapped string from frontend
		logger.info("mappingString :::"+mappingString);

		//Creating set for frontend mapped string
		HashSet<String> mapFE = new HashSet<>();
		for (String mapStringFE : mappingString.split(CrmFieldConstants.COMMA.getValue())){
			logger.info("mapStringFE :::"+mapStringFE);
			String data = mapStringFE.split("-")[1];
			logger.info("Splitting MapString ::"+data);
			if(data.equalsIgnoreCase("Net Banking")){
				String data1 = mapStringFE+"-SALE";
				mapFE.add(data1);
			}
			else {
				mapFE.add(mapStringFE);
			}
		}
		logger.info("mapFE :::::"+mapFE);

		//Creating set for backend mapped string
		HashSet<String> mapBE = new HashSet<>();
		for (TdrSetting tdrSettingBE : chargingDetails){
			String mopBE = tdrSettingBE.getCurrency()+"-"+PaymentType.getInstanceUsingStringValue(tdrSettingBE.getPaymentType()).getName()+"-"+MopType.getCodeusingInstance(tdrSettingBE.getMopType())+"-"+tdrSettingBE.getTransactionType();
			mapBE.add(mopBE);
		}
		logger.info("mapBE :::::"+mapBE);

		//removing frontend set from backend set
		mapBE.removeAll(mapFE);

		//Remove TDR is present in this set
		logger.info("After removing mapFE from mapBE ::::"+mapBE);

		while (accountCurrencySetItrator.hasNext()) {
			boolean flag = false;
			AccountCurrency accountCurrency = accountCurrencySetItrator.next();
			logger.info("AccountCurrency ::::"+accountCurrency);

			//If the set is empty that means nothing to be removed from TDR
			if (mapBE.isEmpty()) {
				flag = true;
				logger.info("FlAG FOR REMOVE TDR::::"+flag);
			}

			//If set having some set that means we have to remove those string from TDR
			if (!flag) {
				logger.info("FlAG FOR REMOVE TDR::::"+flag);
				for (TdrSetting chargingDetail : chargingDetails) {
					logger.info("Inside For loop for remove TDR");
					String removeTDR = chargingDetail.getCurrency() + "-" + PaymentType.getInstanceUsingStringValue(chargingDetail.getPaymentType()).getName() + "-" + MopType.getCodeusingInstance(chargingDetail.getMopType())+"-"+chargingDetail.getTransactionType();
					logger.info("REMOVE TDR :::"+removeTDR);
					if (mapBE.contains(removeTDR)) {
						if (chargingDetail.getStatus().equals(TDRStatus.ACTIVE.toString())
								&& chargingDetail.getCurrency().equals(accountCurrency.getCurrencyCode())) {
							logger.info("Inside IF CONDITION for remove TDR");
							chargingDetail.setStatus(TDRStatus.INACTIVE.toString());
							chargingDetail.setTdrStatus(TDRStatus.INACTIVE.toString());
							chargingDetail.setFromDate(new Date());
						}


					}
				}

//				accountCurrencySetItrator.remove();
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
