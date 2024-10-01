package com.pay10.notification.sms.smsCreater;

import java.util.Map;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
@Component
public class SmsMessageCreater {
	private static Logger logger = LoggerFactory.getLogger(SmsMessageCreater.class
			.getName());

	public String createSmsText(Map<String,String> fields, User user) {
		StringBuilder message = new StringBuilder();
		try {
			message.append("Greetings from MMAD Payments");
			message.append("\n");
			message.append("MERCHANT- ");
			message.append(user.getBusinessName());
			message.append("\n");
			message.append("TXN ID- ");
			message.append(fields.get(FieldType.TXN_ID.getName()));
			message.append("\n");
			message.append("ORDER ID- ");
			message.append(fields.get(FieldType.ORDER_ID.getName()));
			message.append("\n");
			message.append("AMOUNT- ");
			message.append(Amount.toDecimal(
					fields.get(FieldType.AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));
			message.append(" ");
			message.append(Currency.getAlphabaticCode(fields
					.get(FieldType.CURRENCY_CODE.getName())));
			message.append("\n");
			message.append("TXN TYPE- ");
			message.append(fields.get(FieldType.TXNTYPE.getName()));
			message.append("\n");
			message.append("STATUS- ");
			message.append(fields.get(FieldType.STATUS.getName()));
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return message.toString();
	}

	public String createPromoSms(String url, Invoice invoice) {
		StringBuilder message = new StringBuilder();
		try {
			message.append(invoice.getMessageBody());
			message.append("\n");
			message.append("Payment Link - ");
			message.append(url);
			message.append("\n");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return message.toString();
	}
}
