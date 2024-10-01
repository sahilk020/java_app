package com.pay10.pg.core.fraudPrevention.util;

import org.springframework.stereotype.Component;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.SystemConstants;

/**
 * @author Harpreet
 *
 */
@Component
public class FraudPreventionUtil {
	// to create truncated/masked card no
	public static String makeCardMask(Fields fields){
		String reqCardNo = fields.get(FieldType.CARD_NUMBER.getName());
		StringBuilder sb = new StringBuilder();
		//sb.append(reqCardNo.substring(0, SystemConstants.CARD_BIN_LENGTH ));
		sb.append(Constants.CARD_STARS.getValue());
		sb.append(reqCardNo.subSequence(reqCardNo.length() - SystemConstants.CARD_BIN_LENGTH + 2, reqCardNo.length()));
		return sb.toString();
	}




	public static String makeCardMask1(Fields fields){
		String reqCardNo = fields.get(FieldType.CARD_NUMBER.getName());
		StringBuilder sb = new StringBuilder();
		sb.append(reqCardNo.substring(0, SystemConstants.CARD_BIN_LENGTH ));
		sb.append(Constants.CARD_STAR.getValue());
		sb.append(
				reqCardNo.subSequence(
						reqCardNo.length() - SystemConstants.CARD_BIN_LENGTH + 2, reqCardNo.length()));
		return sb.toString();
	}

	public static String makeCardMaskIRCTC(Fields fields){
		String reqCardNo = fields.get(FieldType.CARD_NUMBER.getName());
		StringBuilder sb = new StringBuilder();
		//sb.append(reqCardNo.substring(0, SystemConstants.CARD_BIN_LENGTH ));
		sb.append(reqCardNo.subSequence(0,9));

		int a=reqCardNo.length()-13;
		for(int i=0; i<a;i++){
			sb.append("*");
		}
		sb.append(reqCardNo.subSequence(reqCardNo.length() - SystemConstants.CARD_BIN_LENGTH + 2, reqCardNo.length()));
		return sb.toString();
	}
}