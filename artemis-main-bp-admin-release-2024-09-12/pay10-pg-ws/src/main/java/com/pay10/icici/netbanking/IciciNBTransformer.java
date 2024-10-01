package com.pay10.icici.netbanking;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.axisbank.netbanking.AxisBankNBResultType;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class IciciNBTransformer {

	private Transaction transaction = null;

	public IciciNBTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateSaleResponse(Fields fields) {

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getPaid()))
				&& ((transaction.getPaid()).equalsIgnoreCase("Y")))
		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else {
			if ((StringUtils.isNotBlank(transaction.getPaid()))) {

				IciciNBResultType resultInstance = IciciNBResultType
						.getInstanceFromName(transaction.getPaid());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					pgTxnMsg = "Transaction failed at acquirer";
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getPrn());

		fields.put(FieldType.RRN.getName(), transaction.getFedId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getBid());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getStatFlg());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getPaid());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

	public void updateResponse(Fields fields, Map<String, String> responseMap) {

		StatusType status = getRefundTrackingStatusType(responseMap.get("STATUS"), responseMap.get("RevStatus"));
		ErrorType errorType = getRefundTrackingErrorType(responseMap.get("STATUS"), responseMap.get("RevStatus"));

		fields.put(FieldType.STATUS.getName(), status.getName());
		fields.put(FieldType.RRN.getName(), responseMap.get("BID"));
		fields.put(FieldType.ACQ_ID.getName(), responseMap.get("RevId"));
		fields.put(FieldType.PG_DATE_TIME.getName(), responseMap.get("RevDate"));
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), responseMap.get("RevStatus"));
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

	}

	private ErrorType getRefundTrackingErrorType(String respCode, String revStatus) {
		ErrorType errorType;

		if (null == respCode) {
			errorType = ErrorType.FAILED;
		} else if (respCode.equalsIgnoreCase("Success") && revStatus.equalsIgnoreCase("In Progress")) {
			errorType = ErrorType.PENDING;
		} else if (respCode.equalsIgnoreCase("Success")
				&& (revStatus.equalsIgnoreCase("Success") || revStatus.contains("Success"))) {
			errorType = ErrorType.SUCCESS;
		} else {
			errorType = ErrorType.FAILED;
		}

		return errorType;
	}

	private StatusType getRefundTrackingStatusType(String respCode, String revStatus) {
		StatusType status;

		if (null == respCode) {
			status = StatusType.FAILED;
		} else if (respCode.equalsIgnoreCase("Success") && revStatus.equalsIgnoreCase("In Progress")) {
			status = StatusType.PENDING;
		} else if (respCode.equalsIgnoreCase("Success")
				&& (revStatus.equalsIgnoreCase("Success") || revStatus.contains("Success"))) {
			status = StatusType.CAPTURED;
		} else {
			status = StatusType.FAILED;
		}

		return status;
	}

}
