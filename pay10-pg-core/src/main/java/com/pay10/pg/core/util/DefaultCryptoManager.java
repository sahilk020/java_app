package com.pay10.pg.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Service
public class DefaultCryptoManager implements CryptoManager {
	public static final String MASK_START_CHARS = "-XXXX-XXXX-";
	//TODO:
//	private static final KeyProvider keyProvider = KeyProviderFactory.getKeyProvider();
	
	@Autowired
	AWSEncryptDecryptService awsEncryptDecryptService ;
	

	public DefaultCryptoManager() {
	}

	@Override
	public void secure(Fields fields) throws SystemException {
		try {
			hashCardDetails(fields);
			
		} finally {

			removeSecureFields(fields);
		}
	}

	@Override
	public void hashCardDetails(Fields fields) throws SystemException {
		String cardNumber = fields.get(FieldType.CARD_NUMBER.getName());
		if (null != cardNumber) {
			fields.put(FieldType.H_CARD_NUMBER.getName(),
					Hasher.getHash(cardNumber));
		}
	}

	@Override
	public void removeSecureFields(Fields fields) {
		// Remove CVV, if present - Do not ever store CVV
		fields.remove(FieldType.CVV.getName());

		fields.remove(FieldType.CARD_EXP_DT.getName());
		
		CryptoUtil.truncateCardNumber(fields);
	}

	@Override
	public void encryptCardDetails(Fields fields) {
		
		// Encrypt Card number
		String cardNumber = fields.get(FieldType.CARD_NUMBER.getName());
		if (null != cardNumber) {
			cardNumber = awsEncryptDecryptService.encrypt(cardNumber);
			fields.put(FieldType.S_CARD_NUMBER.getName(), cardNumber);
		}

		// Encrypt Expiry date
		String expiryDate = fields.get(FieldType.CARD_EXP_DT.getName());
		if (null != expiryDate) {
			expiryDate = awsEncryptDecryptService.encrypt(expiryDate);
			fields.put(FieldType.S_CARD_EXP_DT.getName(), expiryDate);
		}
	}
	
	@Override
	public String maskCardNumber(String cardNumber){
		StringBuilder mask = new StringBuilder();
		mask.append(cardNumber.subSequence(0, 4));
		mask.append(MASK_START_CHARS);
		mask.append(cardNumber.substring(cardNumber.length() - 4));
		
		return mask.toString();
	}
}
