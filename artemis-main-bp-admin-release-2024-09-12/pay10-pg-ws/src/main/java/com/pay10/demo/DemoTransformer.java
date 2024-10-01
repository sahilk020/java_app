package com.pay10.demo;

import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.ErrorMappingService;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;

@Service
public class DemoTransformer {
	private static Logger logger = LoggerFactory.getLogger(DemoTransformer.class.getName());

	private Transaction transaction = null;

	public DemoTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {
		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getTxStatus(), "DEMO");

		logger.info("Error code mapping  : " + errorMappingDTO);
		if (StringUtils.isNotBlank(fields.get(FieldType.PAY_ID.getName()))
				&& (fields.get(FieldType.PAY_ID.getName()).equalsIgnoreCase("1429130802122757") 
						|| fields.get(FieldType.PAY_ID.getName()).equalsIgnoreCase("1189130116131957"))) {
			fields.put(FieldType.STATUS.getName(), errorMappingDTO.getAcqStatusCode());
		} else {
			fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
		}
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
		fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
		fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getReferenceId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getReferenceId());
		fields.put(FieldType.RRN.getName(), transaction.getReferenceId());


	}


	public void updateResponseOld(Fields fields) {
		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getTxStatus())) && ((transaction.getTxStatus()).equals("SUCCESS"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else {
			if (StringUtils.isNotBlank(transaction.getTxStatus())) {

				DemoResultType resultInstance = DemoResultType.getInstanceFromName(transaction.getTxStatus());
				if ((resultInstance != null)) {
					status = resultInstance.getStatusName();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

					if (StringUtils.isNotBlank(transaction.getTxMsg())) {
						pgTxnMsg = transaction.getTxMsg();
					} else {
						pgTxnMsg = resultInstance.getMessage();
					}

				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");

					if (StringUtils.isNotBlank(transaction.getTxMsg())) {
						pgTxnMsg = transaction.getTxMsg();
					} else {
						pgTxnMsg = "Transaction failed at acquirer";
					}

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;

				if (StringUtils.isNotBlank(transaction.getTxMsg())) {
					pgTxnMsg = transaction.getTxMsg();
				} else {
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getReferenceId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getReferenceId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getTxStatus());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getReferenceId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getTxStatus());

	}

	public void updateStatusResponse(Fields fields) {

		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if (((StringUtils.isNotBlank(transaction.getOrderStatus()))
				&& ((transaction.getOrderStatus()).equalsIgnoreCase("PAID")))
				&& ((StringUtils.isNotBlank(transaction.getStatus()))
						&& ((transaction.getStatus()).equalsIgnoreCase("OK")))
				&& ((StringUtils.isNotBlank(transaction.getTxStatus()))
						&& ((transaction.getTxStatus()).equalsIgnoreCase("SUCCESS")))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else {
			if (StringUtils.isNotBlank(transaction.getOrderStatus())) {

				DemoResultType resultInstance = DemoResultType
						.getInstanceFromName(transaction.getOrderStatus());
				if ((resultInstance != null)) {
					status = resultInstance.getStatusName();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {

					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					pgTxnMsg = "Transaction failed at acquirer";

					if (StringUtils.isNoneBlank(transaction.getTxMsg())) {
						pgTxnMsg = transaction.getTxMsg();
					}
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				if (StringUtils.isNoneBlank(transaction.getTxMsg())) {
					pgTxnMsg = transaction.getTxMsg();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getReferenceId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getReferenceId());

		if (StringUtils.isNoneBlank(transaction.getTxStatus())) {
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getTxStatus());
		} else {
			errorType.getResponseCode();
		}

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getReferenceId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());

	}

	public void updateRefundResponse(Fields fields) {

		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getStatus()))
						&& ((transaction.getStatus()).equalsIgnoreCase("OK"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else {
			if (StringUtils.isNotBlank(transaction.getStatus())) {

				DemoResultType resultInstance = DemoResultType
						.getInstanceFromName(transaction.getStatus());
				if ((resultInstance != null)) {
					status = resultInstance.getStatusName();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {

					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					pgTxnMsg = "Transaction failed at acquirer";

					if (StringUtils.isNoneBlank(transaction.getMessage())) {
						pgTxnMsg = transaction.getMessage();
					}
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				if (StringUtils.isNoneBlank(transaction.getMessage())) {
					pgTxnMsg = transaction.getMessage();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getRefundId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getRefundId());

		if (StringUtils.isNoneBlank(transaction.getStatus())) {
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getStatus());
		} else {
			errorType.getResponseCode();
		}

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getRefundId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());
	}

}
