package com.pay10.pg.core.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.util.ConstantsIrctc;

@Service("irctcTransactionConverter")
public class TransactionConverterIrctc {

	public StringBuilder mapChecksum(StringBuilder requestfields) throws SystemException {
		String checksum = Hasher.getHash(requestfields.toString());

		requestfields.append(ConstantsIrctc.IPAY_SEPARATOR);
		requestfields.append(ConstantsIrctc.CHECKSUM).append(ConstantsIrctc.EQUATOR).append(checksum);

		return requestfields;
	}

	public StringBuilder mapSaleFields(Fields fields) {
		StringBuilder request = new StringBuilder();

		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), currencyCode);
		String status = fields.get(FieldType.STATUS.getName());
		String responseCode = fields.get(FieldType.RESPONSE_CODE.getName());
		if ((status.equals(StatusType.CAPTURED.getName()) && (responseCode.equals(ErrorType.SUCCESS.getCode())))) {
			status = ConstantsIrctc.SUCCESS;
		} else {
			status = ConstantsIrctc.FAILURE;
		}

		String statusDesc = fields.get(FieldType.PG_TXN_MESSAGE.getName());
		if (StringUtils.isBlank(statusDesc)) {
			statusDesc = fields.get(FieldType.RESPONSE_MESSAGE.getName());
		}

		String countryCode = fields.get(FieldType.CARD_ISSUER_COUNTRY.getName());
		if (StringUtils.isBlank(countryCode)) {
			countryCode = "";
		}

		String bankName = fields.get(FieldType.CARD_ISSUER_BANK.getName());
		if (StringUtils.isBlank(bankName)) {
			bankName = "";
		}

		String binNo = fields.get(FieldType.CARD_MASK.getName());
		if (binNo != null && StringUtils.isNotBlank(binNo)) {
			binNo = binNo.substring(0, 6);
		} else {
			binNo = "";
		}
		
		String paymentMode = fields.get(FieldType.PAYMENT_TYPE.getName());
		if (StringUtils.isBlank(paymentMode)) {
			paymentMode = "";
		} else if (paymentMode.equals(PaymentType.UPI.getCode())) {
			paymentMode = PaymentType.UPI.getName();
		}
		
		request.append(ConstantsIrctc.MERCHANT_CODE).append(ConstantsIrctc.EQUATOR)
				.append(fields.get(FieldType.PAY_ID.getName()));
		request.append(ConstantsIrctc.IPAY_SEPARATOR);
		request.append(ConstantsIrctc.RESERVATION_ID).append(ConstantsIrctc.EQUATOR)
				.append(fields.get(FieldType.ORDER_ID.getName()));
		request.append(ConstantsIrctc.IPAY_SEPARATOR);
		request.append(ConstantsIrctc.BANK_TXN_ID).append(ConstantsIrctc.EQUATOR)
				.append(fields.get(FieldType.PG_REF_NUM.getName()));
		request.append(ConstantsIrctc.IPAY_SEPARATOR);
		request.append(ConstantsIrctc.TXN_AMOUNT).append(ConstantsIrctc.EQUATOR).append(amount);
		request.append(ConstantsIrctc.IPAY_SEPARATOR);
		request.append(ConstantsIrctc.STATUS).append(ConstantsIrctc.EQUATOR).append(status);
		request.append(ConstantsIrctc.IPAY_SEPARATOR);
		request.append(ConstantsIrctc.STATUS_DESC).append(ConstantsIrctc.EQUATOR).append(statusDesc);
		request.append(ConstantsIrctc.IPAY_SEPARATOR);
		request.append(ConstantsIrctc.PAYMENT_MODE).append(ConstantsIrctc.EQUATOR)
				.append(paymentMode);
		request.append(ConstantsIrctc.IPAY_SEPARATOR);
		request.append(ConstantsIrctc.BANK_NAME).append(ConstantsIrctc.EQUATOR).append(bankName);
		request.append(ConstantsIrctc.IPAY_SEPARATOR);
		request.append(ConstantsIrctc.COUNTRY_CODE).append(ConstantsIrctc.EQUATOR).append(countryCode);
		request.append(ConstantsIrctc.IPAY_SEPARATOR);
		request.append(ConstantsIrctc.BINNO).append(ConstantsIrctc.EQUATOR).append(binNo);

		return request;
	}

	public StringBuilder mapDoubleVerificationFields(Fields fields) {
		String statusDesc = fields.get(FieldType.PG_TXN_MESSAGE.getName());
		if (statusDesc != null && StringUtils.isNotBlank(statusDesc)) {
		} else {
			statusDesc = fields.get(FieldType.RESPONSE_MESSAGE.getName());
		}
		String status = fields.get(FieldType.STATUS.getName());
		String responseCode = fields.get(FieldType.RESPONSE_CODE.getName());
		if ((status.equals(StatusType.CAPTURED.getName()) && (responseCode.equals(ErrorType.SUCCESS.getCode())))) {
			status = ConstantsIrctc.SUCCESS;
		} else {
			status = ConstantsIrctc.FAILURE;
			statusDesc = ErrorType.NO_SUCH_TRANSACTION.getResponseMessage();
		}

		
		StringBuilder response = new StringBuilder();
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), currencyCode);

		response.append(ConstantsIrctc.MERCHANT_CODE).append(ConstantsIrctc.EQUATOR)
				.append(fields.get(FieldType.PAY_ID.getName()));
		response.append(ConstantsIrctc.IPAY_SEPARATOR);
		response.append(ConstantsIrctc.RESERVATION_ID).append(ConstantsIrctc.EQUATOR)
				.append(fields.get(FieldType.ORDER_ID.getName()));
		response.append(ConstantsIrctc.IPAY_SEPARATOR);
		response.append(ConstantsIrctc.BANK_TXN_ID).append(ConstantsIrctc.EQUATOR)
				.append(fields.get(FieldType.PG_REF_NUM.getName()));
		response.append(ConstantsIrctc.IPAY_SEPARATOR);
		response.append(ConstantsIrctc.TXN_AMOUNT).append(ConstantsIrctc.EQUATOR).append(amount);
		response.append(ConstantsIrctc.IPAY_SEPARATOR);
		response.append(ConstantsIrctc.STATUS).append(ConstantsIrctc.EQUATOR).append(status);
		response.append(ConstantsIrctc.IPAY_SEPARATOR);
		response.append(ConstantsIrctc.STATUS_DESC).append(ConstantsIrctc.EQUATOR).append(statusDesc);

		return response;
	}
	
}
