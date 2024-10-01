package com.pay10.pg.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;

import lw.bouncycastle.openpgp.PGPUtil;
import sbi.mer.pgp.dto.PGPDto;
import sbi.mer.pgp.main.PGPEncDecUtility;




@Service("sbiUpiUtils")
public class SBIUpiUtils {
	private static Logger logger = LoggerFactory.getLogger(SBIUpiUtils.class.getName());
	public String encryption(String encryptedRequest, Fields fields, String encryption_type) throws SystemException {

		PGPDto pgpDto = new PGPDto();
		pgpDto.setPrivateKeyPath(PropertiesManager.propertiesMap.get(SBIUpiConstants.PRIVATE_CERT));
		pgpDto.setPublicKeyPath(PropertiesManager.propertiesMap.get(SBIUpiConstants.PUBLIC_CERT));
		pgpDto.setPrivateKeyPass(PropertiesManager.propertiesMap.get(SBIUpiConstants.PRIVATE_KEY_PASSWORD));
		pgpDto.setPayload(encryptedRequest);
		logger.info("SBI UPI : METHOD : encryption : PG_REF_NUM : "
				+ fields.get(FieldType.PG_REF_NUM.getName()) + "   ENCRYPTION REQUEST  : " + encryptedRequest);
		String encryptedString = "";
		try {
			if (encryption_type.equals(SBIUpiConstants.ENCRYPTION_TYPE)) {
				encryptedString = PGPEncDecUtility.getPGPEncryptedPayload(pgpDto);
			} else {
				encryptedString = PGPEncDecUtility.getPGPDecryptedPayload(pgpDto);
			}
		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : encryption : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   EXCEPTION ENCRYPTION  : " + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in encrypt method for sbi upi in TransactionConverter");
		}
		
		logger.info("SBI UPI : METHOD : encryption : PG_REF_NUM : "
				+ fields.get(FieldType.PG_REF_NUM.getName()) + "   ENCRYPTION RESPONSE  : " + encryptedString);
		return encryptedString;

	}

		
}