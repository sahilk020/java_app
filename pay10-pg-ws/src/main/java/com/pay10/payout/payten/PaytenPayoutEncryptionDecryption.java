package com.pay10.payout.payten;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PaytenPayoutEncryptionDecryption {
	public static String generatetHashString(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			StringBuilder hashtext = new StringBuilder(no.toString(16));
			while (hashtext.length() < 64) {
				hashtext.insert(0, '0');
			}
			return hashtext.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
