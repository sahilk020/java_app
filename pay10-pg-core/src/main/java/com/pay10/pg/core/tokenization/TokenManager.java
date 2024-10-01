package com.pay10.pg.core.tokenization;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.pay10.commons.dao.TokenDao;
import com.pay10.commons.dto.TokenCardResponseDto;
import com.pay10.commons.dto.TokenResponseDto;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Token;
import com.pay10.commons.user.User;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TokenFactory;
import com.pay10.commons.util.TokenStatus;

/**
 * @author Sunil
 *
 */
@Service
public class TokenManager {

	@Autowired
	private TokenDao tokenDao;

	@Autowired
	private TokenService tokenService;

	public static final String MASK_START_CHARS = "-XXXX-XXXX-XXXX-";

	private static Logger logger = LoggerFactory.getLogger(TokenManager.class.getName());
	// Add new token

	public Token addToken(Fields fields, User user) throws SystemException {

		Token token = tokenDao.getCardNumber(maskCardNumber(fields.get(FieldType.CARD_NUMBER.getName())),
				fields.get(FieldType.PAY_ID.getName()), fields.get(user.getCardSaveParam()));
		if (token == null) {
			TokenResponseDto responseDto = tokenService.createToken(fields);
			if (ObjectUtils.isEmpty(responseDto) || responseDto.getStatus() < 1) {
				logger.error("------Error Creating Token---------");
				return token;
			}
			token = new Token();
			token.setId(responseDto.getCardToken());
			token.setMopType(fields.get(FieldType.MOP_TYPE.getName()));
			token.setPaymentType(fields.get(FieldType.PAYMENT_TYPE.getName()));
			token.setCustomerName(fields.get(FieldType.CARD_HOLDER_NAME.getName()));
			token.setStatus(TokenStatus.ACTIVE);
			token.setCardSaveParam(fields.get(user.getCardSaveParam()));
			token.setCardMask(responseDto.getCardNumber());
			token.setUserCardMask(maskCardNumber(fields.get(FieldType.CARD_NUMBER.getName())));
			token.setCardLabel(fields.get(FieldType.CARD_HOLDER_NAME.getName()));
			token.setCardToken(responseDto.getCardToken());
			token.setPayId(fields.get(FieldType.PAY_ID.getName()));
			token.setNetworkToken(responseDto.getNetworkToken());
			token.setIssuerToken(responseDto.getIssuerToken());
			token.setPaymentsRegion(fields.get(FieldType.PAYMENTS_REGION.getName()));
			token.setCardHolderType(fields.get(FieldType.CARD_HOLDER_TYPE.getName()));
			tokenDao.create(token);
			logger.info("New token added successfully");
		} else {
			logger.info("Token exist for the same card");
		}
		return token;
	}

	public String maskCardNumber(String cardNumber) {
		StringBuilder mask = new StringBuilder();
		//mask.append(cardNumber.subSequence(0, 4));
		mask.append(MASK_START_CHARS);
		mask.append(cardNumber.substring(cardNumber.length() - 4));

		return mask.toString();
	}

	// Remove Save card for given tokenID
	public void removeSavedCard(Fields fields) {
		try {
			Token token = TokenFactory.instanceDelete(fields);
			logger.info("Removing token from DB with Id = " + token.getId());
			tokenDao.delete(token);
			TokenResponseDto cardResponseDto = tokenService.deleteToken(fields);
			logger.info("Removing token from Token Store with Id = " + token.getId());
			if (cardResponseDto != null && cardResponseDto.getStatus() > 0) {
				logger.info("Removed token ");
			}
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}
	}

	// Remove all tokens matching the cardHash
	public int removeTokensForCard(Fields fields) {
		// logger.info("Removing all tokens for card");

		return 0;
	}

	public Map<String, Token> getAll(Fields fields) {
		logger.info("Get all tokens with email = " + fields.get(FieldType.CUST_EMAIL.getName()) + "and PayId ="
				+ fields.get(FieldType.PAY_ID.getName()));
		try {
			TokenCardResponseDto cardResponseDto = tokenService.getUserCards(fields);
			if (cardResponseDto != null) {

				cardResponseDto.getUserCards().entrySet().stream().forEach(token -> {

					if (token.getValue().getExpired() > 0) {
						logger.info("Deleting expired card token = " + token.getKey());
						tokenDao.deleteByIdPayIdSaveParam(token.getKey(), fields.get(FieldType.PAY_ID.getName()),
								fields.get(FieldType.CUST_EMAIL.getName()));
					}
				});
			}
		} catch (Exception e) {
			logger.error("Exception whiel deleting token", e);
		}
		Map<String, Token> token = tokenDao.getAll(fields.get(FieldType.PAY_ID.getName()),
				fields.get(FieldType.CUST_EMAIL.getName()));

		return token;
	}

	public void removeSavedCard(Fields fields, User user) {
		try {
			Token token = TokenFactory.instanceDelete(fields);
			tokenDao.delete(token);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}
	}

	public Token getToken(Fields fields) {
		logger.info("Get Token with ID = " + fields.get(FieldType.TOKEN_ID.getName()));

		Token token = null;
		try {
			token = tokenDao.getToken(fields.get(FieldType.TOKEN_ID.getName()));
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return token;

	}

	public Token getTokenMap(String tokenId) {
		// logger.info("Get Token with ID = " +
		// fields.get(FieldType.TOKEN_ID.getName()));

		Token token = null;
		try {
			token = tokenDao.getToken(tokenId);
		} catch (Exception exception) {
			// logger.error("Exception", exception);
		}
		return token;

	}

	public TokenDao getTokenDao() {
		return tokenDao;
	}

	public void setTokenDao(TokenDao tokenDao) {
		this.tokenDao = tokenDao;
	}

	/*
	 * public static Logger getLogger() { return logger; }
	 * 
	 * public static void setLogger(Logger logger) { TokenManager.logger = logger; }
	 */
}
