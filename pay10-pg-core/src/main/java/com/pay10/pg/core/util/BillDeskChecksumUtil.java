
package com.pay10.pg.core.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BillDeskChecksumUtil {
	private static final String HASH_ALGORITHM = "HmacSHA256";
	private static Logger logger = LoggerFactory.getLogger(BillDeskChecksumUtil.class.getName());

	public String getHash(String msg2, String Checksumkey) {

		String result = "";
		try {
			byte[] keyBytes = Checksumkey.getBytes();
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HASH_ALGORITHM);

			Mac mac = Mac.getInstance(HASH_ALGORITHM);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(msg2.getBytes());

			byte[] hexBytes = new Hex().encode(rawHmac);
			result = new String(hexBytes, "UTF-8");
			return result.toUpperCase();
		} catch (Exception e) {

			logger.error("Error in getting hash",e);
		}
		return result;

	}

	/*
	 * public boolean compareResponseHash(Fields fields, String responseString)
	 * throws SystemException { String Checksumkey =
	 * ConfigurationConstants.BILLDESK_CHECKSUM_KEY.getValue(); String
	 * responsewithouthash = (responseString.substring(0,
	 * responseString.lastIndexOf('|'))); String calculatedHash =
	 * BillDeskChecksumUtil.main(responsewithouthash, Checksumkey);
	 * 
	 * String[] responseArray = responseString.split("\\|"); String receivedHash =
	 * responseArray[25];
	 * 
	 * return calculatedHash.equals(receivedHash);
	 * 
	 * }
	 */

	/*
	 * public boolean compareResponseStatusHash(Fields fields, String
	 * responseString) throws SystemException { String Checksumkey =
	 * ConfigurationConstants.BILLDESK_CHECKSUM_KEY.getValue(); String
	 * responsewithouthash = (responseString.substring(0,
	 * responseString.lastIndexOf('|'))); String calculatedHash =
	 * BillDeskChecksumUtil.main(responsewithouthash, Checksumkey);
	 * 
	 * String[] responseArray = responseString.split("\\|"); String receivedHash =
	 * responseArray[32];
	 * 
	 * return calculatedHash.equals(receivedHash);
	 * 
	 * }
	 */

	/*
	 * public boolean compareResponseRefundHash(Fields fields, String
	 * responseString) throws SystemException { String Checksumkey =
	 * ConfigurationConstants.BILLDESK_CHECKSUM_KEY.getValue(); String
	 * responsewithouthash = (responseString.substring(0,
	 * responseString.lastIndexOf('|'))); String calculatedHash =
	 * BillDeskChecksumUtil.main(responsewithouthash, Checksumkey);
	 * 
	 * String[] responseArray = responseString.split("\\|"); String receivedHash =
	 * responseArray[13];
	 * 
	 * return calculatedHash.equals(receivedHash);
	 * 
	 * }
	 */
}
