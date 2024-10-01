package com.pay10.programs;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.PermissionType;
import com.pay10.commons.user.Permissions;
import com.pay10.commons.user.Roles;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.SaltFactory;
import com.pay10.commons.util.SaltFileManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.UserStatusType;

public class CreateSuperAdmin {

	private static Logger logger = LoggerFactory.getLogger(CreateSuperAdmin.class.getName());
	private static UserDao userDao = new UserDao();

	private static SaltFileManager saltFileManager = new SaltFileManager();

	public static void main(String[] args) {

		// timer1.scheduleAtFixedRate(test, 10000, 1000);
		// createSuperAdmin();
		createAdmin();
		/*
		 * createFSSAcq(); createISGPAYAcq(); createDirecpayAcquirer();
		 * createBOBAcquirer(); createHDFCAcquirer(); createFSSAcq(); createCitrusAcq();
		 * createPaytmAcq(); createBarclayAcquirer(); createDirecpayAcquirer();
		 * 
		 * createAMEXAcquirer(); createMobikwikAcquirer();
		 * createAmexEzeeClickAcquirer(); createYesbankAcquirer();
		 * createKotakAcquirer(); createFirstDataAcquirer(); createFederalAcquirer();
		 * 
		 * createSBIAcquirer(); createNBICICIAcquirer(); createNBVIJAYAAcquirer();
		 * createNBKARNATAKAAcquirer(); createNBSOUTHINDIANAcquirer();
		 * createMigsAcquirer(); createBOBAcquirer(); createATLAcquirer();
		 * createGooglePayAcquirer(); createYesbankcbAcquirer();
		 */
	}

	private static void createMigsAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User migsData = new User();

		migsData.setEmailId("axisMigs@pay10.com");
		migsData.setUserStatus(UserStatusType.ACTIVE);
		migsData.setUserType(UserType.ACQUIRER);
		migsData.setPayId("55");
		migsData.setPassword(hasher(salt));
		migsData.setFirstName("AXISMIGS");
		migsData.setBusinessName("AXISMIGS");

		userDao.create(migsData);
		boolean isSaltInserted = saltFileManager.insertSalt(migsData.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(migsData);
		}
	}

	private static void createNBVIJAYAAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User sbiData = new User();

		sbiData.setEmailId("ipayvijaya@pay10.com");
		sbiData.setUserStatus(UserStatusType.ACTIVE);
		sbiData.setUserType(UserType.ACQUIRER);
		sbiData.setPayId("42");
		sbiData.setPassword(hasher(salt));
		sbiData.setFirstName("NBVIJAYABANK");
		sbiData.setBusinessName("VIJAYA BANK NETBANKING");

		userDao.create(sbiData);
		boolean isSaltInserted = saltFileManager.insertSalt(sbiData.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(sbiData);
		}

	}

	private static void createNBSOUTHINDIANAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User sbiData = new User();

		sbiData.setEmailId("ipaysouthindian@pay10.com");
		sbiData.setUserStatus(UserStatusType.ACTIVE);
		sbiData.setUserType(UserType.ACQUIRER);
		sbiData.setPayId("43");
		sbiData.setPassword(hasher(salt));
		sbiData.setFirstName("NBSOUTHINDIANBANK");
		sbiData.setBusinessName("SOUTH INDIAN BANK NETBANKING");

		userDao.create(sbiData);
		boolean isSaltInserted = saltFileManager.insertSalt(sbiData.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(sbiData);
		}

	}

	private static void createNBKARNATAKAAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User sbiData = new User();

		sbiData.setEmailId("ipaykarnataka@pay10.com");
		sbiData.setUserStatus(UserStatusType.ACTIVE);
		sbiData.setUserType(UserType.ACQUIRER);
		sbiData.setPayId("44");
		sbiData.setPassword(hasher(salt));
		sbiData.setFirstName("NBKARNATAKABANK");
		sbiData.setBusinessName("KARNATAKA BANK NETBANKING");

		userDao.create(sbiData);
		boolean isSaltInserted = saltFileManager.insertSalt(sbiData.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(sbiData);
		}

	}

	private static void createSBIAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User sbiData = new User();

		sbiData.setEmailId("ipaysbi@pay10.com");
		sbiData.setUserStatus(UserStatusType.ACTIVE);
		sbiData.setUserType(UserType.ACQUIRER);
		sbiData.setPayId("40");
		sbiData.setPassword(hasher(salt));
		sbiData.setFirstName("NBSBI");
		sbiData.setBusinessName("SBI NETBANKING");

		userDao.create(sbiData);
		boolean isSaltInserted = saltFileManager.insertSalt(sbiData.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(sbiData);
		}

	}

	private static void createNBICICIAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User sbiData = new User();

		sbiData.setEmailId("ipayicici@pay10.com");
		sbiData.setUserStatus(UserStatusType.ACTIVE);
		sbiData.setUserType(UserType.ACQUIRER);
		sbiData.setPayId("41");
		sbiData.setPassword(hasher(salt));
		sbiData.setFirstName("NBICICIBANK");
		sbiData.setBusinessName("ICICI BANK NETBANKING");

		userDao.create(sbiData);
		boolean isSaltInserted = saltFileManager.insertSalt(sbiData.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(sbiData);
		}

	}

	public static void createSuperAdmin() {
		String salt = SaltFactory.generateRandomSalt();
		User user = new User();
		user.setPassword(hasher(salt));
		user.setUserType(UserType.SUPERADMIN);
		user.setUserStatus(UserStatusType.ACTIVE);
		user.setPayId(TransactionManager.getNewTransactionId());
		user.setBusinessName("MMAD SuperAdmin");
		user.setCompanyName("MMAD payment Solutions");
		user.setFirstName("MMAD");
		user.setLastName("Payments communication");
		user.setEmailId("superadmin@pay10.com");
		user.setContactPerson("Neeraj");
		user.setAddress("105 Competent House, F - 14,Middle Circle, Connaught Place,New Delhi - 110001");
		user.setCity("Delhi");
		user.setState("Delhi");
		user.setCountry("India");
		user.setWebsite("secure.pay10.com");
		userDao.create(user);
		boolean isSaltInserted = saltFileManager.insertSalt(user.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(user);
		}
	}

	public static void createFirstDataAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User firstData = new User();

		firstData.setEmailId("icici@pay10.com");
		firstData.setUserStatus(UserStatusType.ACTIVE);
		firstData.setUserType(UserType.ACQUIRER);
		firstData.setPayId("10");
		firstData.setPassword(hasher(salt));
		firstData.setFirstName("FIRSTDATA");
		firstData.setBusinessName("ICICI Bank");

		userDao.create(firstData);
		boolean isSaltInserted = saltFileManager.insertSalt(firstData.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(firstData);
		}

	}

	public static void createFederalAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User federal = new User();

		federal.setEmailId("federal@pay10.com");
		federal.setUserStatus(UserStatusType.ACTIVE);
		federal.setUserType(UserType.ACQUIRER);
		federal.setPayId("25");
		federal.setPassword(hasher(salt));
		federal.setFirstName("FEDERAL");
		federal.setBusinessName("FEDERAL Bank");

		userDao.create(federal);
		boolean isSaltInserted = saltFileManager.insertSalt(federal.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(federal);
		}

	}

	public static void createBOBAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User bob = new User();

		bob.setEmailId("bob@pay10.com");
		bob.setUserStatus(UserStatusType.ACTIVE);
		bob.setUserType(UserType.ACQUIRER);
		bob.setPayId("48");
		bob.setPassword(hasher(salt));
		bob.setFirstName("BOB");
		bob.setBusinessName("Bank of Baroda");

		userDao.create(bob);
		boolean isSaltInserted = saltFileManager.insertSalt(bob.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(bob);
		}

	}

	public static void createHDFCAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User hdfc = new User();

		hdfc.setEmailId("hdfc@pay10.com");
		hdfc.setUserStatus(UserStatusType.ACTIVE);
		hdfc.setUserType(UserType.ACQUIRER);
		hdfc.setPayId(TransactionManager.getNewTransactionId());
		hdfc.setPassword(hasher(salt));
		hdfc.setFirstName("HDFC");
		hdfc.setLastName("HDFC");
		hdfc.setBusinessName("HDFC Bank");

		userDao.create(hdfc);
		boolean isSaltInserted = saltFileManager.insertSalt(hdfc.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(hdfc);
		}

	}

	public static void createATLAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User atl = new User();

		atl.setEmailId("atl@pay10.com");
		atl.setUserStatus(UserStatusType.ACTIVE);
		atl.setUserType(UserType.ACQUIRER);
		atl.setPayId("50");
		atl.setPassword(hasher(salt));
		atl.setFirstName("ATL");
		atl.setBusinessName("AutoDebit");

		userDao.create(atl);
		boolean isSaltInserted = saltFileManager.insertSalt(atl.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(atl);
		}

	}

	public static void createAdmin() {
		String salt = SaltFactory.generateRandomSalt();
		User user = new User();
		Permissions permission1 = new Permissions();
		Permissions permission2 = new Permissions();
		Permissions permission3 = new Permissions();

		Set<Permissions> permissions = new HashSet<Permissions>();

		permission1.setPermissionType(PermissionType.CREATEUSER);
		permission2.setPermissionType(PermissionType.DELETEUSER);
		permission3.setPermissionType(PermissionType.LOGIN);
		permissions.add(permission1);
		permissions.add(permission2);
		permissions.add(permission3);

		Set<Roles> roles = new HashSet<Roles>();
		Roles role = new Roles();

		role.setPermissions(permissions);
		role.setName(UserType.SUPERADMIN.name());
		roles.add(role);

		user.setRoles(roles);
		user.setPassword(hasher(salt));
		user.setUserType(UserType.ADMIN);
		user.setUserStatus(UserStatusType.ACTIVE);
		user.setPayId(TransactionManager.getNewTransactionId());
		user.setBusinessName("Admin");
		user.setCompanyName("Pay10 payment Solutions");
		user.setFirstName("Test");
		user.setLastName("Payments");
		user.setEmailId("admin@pay10.com");
		user.setContactPerson("Shaiwal");
		user.setAddress("C-94 Saraswati Kunj");
		user.setCity("Gurgaon");
		user.setState("Haryana");
		user.setCountry("India");
		user.setWebsite("secure.pay10.com");

		userDao.create(user);

		boolean isSaltInserted = saltFileManager.insertSalt(user.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(user);
		}
	}

	public static void createFSSAcq() {
		String salt = SaltFactory.generateRandomSalt();
		User fss = new User();

		fss.setEmailId("fss@pay10.com");
		fss.setUserStatus(UserStatusType.ACTIVE);
		fss.setUserType(UserType.ACQUIRER);
		fss.setPayId(TransactionManager.getNewTransactionId());
		fss.setPassword(hasher(salt));
		fss.setFirstName("FSS");
		fss.setLastName("FSS");
		fss.setBusinessName("FSS");

		userDao.create(fss);
		boolean isSaltInserted = saltFileManager.insertSalt(fss.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(fss);
		}

	}

	public static void createISGPAYAcq() {
		String salt = SaltFactory.generateRandomSalt();
		User isg = new User();

		isg.setEmailId("isg@pay10.com");
		isg.setUserStatus(UserStatusType.ACTIVE);
		isg.setUserType(UserType.ACQUIRER);
		isg.setPayId(TransactionManager.getNewTransactionId());
		isg.setPassword(hasher(salt));
		isg.setFirstName("ISGPAY");
		isg.setLastName("ISGPAY");
		isg.setBusinessName("ISGPAY");

		userDao.create(isg);
		boolean isSaltInserted = saltFileManager.insertSalt(isg.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(isg);
		}

	}

	public static void createGooglePayAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User googlePay = new User();

		googlePay.setEmailId("googlePay@pay10.com");
		googlePay.setUserStatus(UserStatusType.ACTIVE);
		googlePay.setUserType(UserType.ACQUIRER);
		googlePay.setPayId("54");
		googlePay.setPassword(hasher(salt));
		googlePay.setFirstName("GOOGLEPAY");
		googlePay.setBusinessName("GOOGLE PAY");

		userDao.create(googlePay);
		boolean isSaltInserted = saltFileManager.insertSalt(googlePay.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(googlePay);
		}

	}

	public static void createCitrusAcq() {
		User citrus_pay = new User();
		String salt = SaltFactory.generateRandomSalt();

		citrus_pay.setEmailId("citrus@pay10.com");
		citrus_pay.setUserStatus(UserStatusType.ACTIVE);
		citrus_pay.setUserType(UserType.ACQUIRER);
		citrus_pay.setPayId("2");
		citrus_pay.setPassword(hasher(salt));
		citrus_pay.setFirstName("CITRUS");
		citrus_pay.setBusinessName("Yes Bank");

		userDao.create(citrus_pay);
		boolean isSaltInserted = saltFileManager.insertSalt(citrus_pay.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(citrus_pay);
		}

	}

	public static void createPaytmAcq() {
		String salt = SaltFactory.generateRandomSalt();
		User paytm = new User();

		paytm.setEmailId("paytm@pay10.com");
		paytm.setUserStatus(UserStatusType.ACTIVE);
		paytm.setUserType(UserType.ACQUIRER);
		paytm.setPayId("3");
		paytm.setPassword(hasher(salt));
		paytm.setFirstName("PAYTM");
		paytm.setBusinessName("WALLET PAYTM");

		userDao.create(paytm);
		boolean isSaltInserted = saltFileManager.insertSalt(paytm.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(paytm);
		}
	}

	public static void createBarclayAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User barclay = new User();

		barclay.setEmailId("barclay@pay10.com");
		barclay.setUserStatus(UserStatusType.ACTIVE);
		barclay.setUserType(UserType.ACQUIRER);
		barclay.setPayId("4");
		barclay.setPassword(hasher(salt));
		barclay.setFirstName("BARCLAY");
		barclay.setBusinessName("Barclay Bank");

		userDao.create(barclay);
		boolean isSaltInserted = saltFileManager.insertSalt(barclay.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(barclay);
		}
	}

	public static void createDirecpayAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User direcpay = new User();

		direcpay.setEmailId("direcpay@pay10.com");
		direcpay.setUserStatus(UserStatusType.ACTIVE);
		direcpay.setUserType(UserType.ACQUIRER);
		direcpay.setPayId(TransactionManager.getNewTransactionId());
		direcpay.setPassword(hasher(salt));
		direcpay.setFirstName("DIRECPAY");
		direcpay.setLastName("DIRECPAY");
		direcpay.setBusinessName("DIRECPAY");

		userDao.create(direcpay);
		boolean isSaltInserted = saltFileManager.insertSalt(direcpay.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(direcpay);
		}
	}

	public static void createAMEXAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User amex = new User();

		amex.setEmailId("amex@pay10.com");
		amex.setUserStatus(UserStatusType.ACTIVE);
		amex.setUserType(UserType.ACQUIRER);
		amex.setPayId("6");
		amex.setPassword(hasher(salt));
		amex.setFirstName("AMEX");
		amex.setBusinessName("AMERICAN EXPRESS");

		userDao.create(amex);
		boolean isSaltInserted = saltFileManager.insertSalt(amex.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(amex);
		}
	}

	public static void createAmexEzeeClickAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User amex = new User();

		amex.setEmailId("amexezeeclick@pay10.com");
		amex.setUserStatus(UserStatusType.ACTIVE);
		amex.setUserType(UserType.ACQUIRER);
		amex.setPayId("66");
		amex.setPassword(hasher(salt));
		amex.setFirstName("EZEECLICCK");
		amex.setBusinessName("AMEX EZEECLICK");

		userDao.create(amex);
		boolean isSaltInserted = saltFileManager.insertSalt(amex.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(amex);
		}
	}

	public static void createMobikwikAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User amex = new User();

		amex.setEmailId("mobikwik@pay10.com");
		amex.setUserStatus(UserStatusType.ACTIVE);
		amex.setUserType(UserType.ACQUIRER);
		amex.setPayId("7");
		amex.setPassword(hasher(salt));
		amex.setFirstName("MOBIKWIK");
		amex.setBusinessName("WALLET MOBIKWIK");

		userDao.create(amex);
		boolean isSaltInserted = saltFileManager.insertSalt(amex.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(amex);
		}
	}

	public static void createYesbankcbAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User yesbankcb = new User();

		yesbankcb.setEmailId("yesbankcb@pay10.com");
		yesbankcb.setUserStatus(UserStatusType.ACTIVE);
		yesbankcb.setUserType(UserType.ACQUIRER);
		yesbankcb.setPayId("8");
		yesbankcb.setPassword(hasher(salt));
		yesbankcb.setFirstName("YESBANKCB");
		yesbankcb.setBusinessName("YES Bank CB");

		userDao.create(yesbankcb);
		boolean isSaltInserted = saltFileManager.insertSalt(yesbankcb.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(yesbankcb);
		}
	}

	public static void createKotakAcquirer() {
		String salt = SaltFactory.generateRandomSalt();
		User kotak = new User();

		kotak.setEmailId("kotak@pay10.com");
		kotak.setUserStatus(UserStatusType.ACTIVE);
		kotak.setUserType(UserType.ACQUIRER);
		kotak.setPayId("9");
		kotak.setPassword(hasher(salt));
		kotak.setFirstName("KOTAK");
		kotak.setBusinessName("NETBANKING KOTAK Bank");

		userDao.create(kotak);
		boolean isSaltInserted = saltFileManager.insertSalt(kotak.getPayId(), salt);
		if (!isSaltInserted) {
			// Rollback user creation
			userDao.delete(kotak);
		}
	}

	public static String hasher(String salt) {
		String password = null;
		try {
			password = Hasher.getHash("Admin@123".concat(salt));
		} catch (SystemException systemException) {
			logger.error("Error Creating hash for password", systemException);
		}
		return password;
	}

}
