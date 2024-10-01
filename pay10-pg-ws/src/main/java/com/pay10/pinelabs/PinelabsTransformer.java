package com.pay10.pinelabs;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;

@Service
public class PinelabsTransformer {
	private static Logger logger = LoggerFactory.getLogger(PinelabsTransformer.class.getName());
	private Transaction transaction = null;

	public PinelabsTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	/* Error code maping with db 
	
	public void updateResponse(Fields fields) {
		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getPineTxnStatus(), "PINELABS");
		logger.info("PINELABS : Error code mapping  : "+errorMappingDTO);
		fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getPineTxnId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getPineTxnId());
		fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
		fields.put(FieldType.RRN.getName(), transaction.getPineTxnId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());

	}
	
	public void updateStatusResponse(Fields fields) {
		
		String status_code = "";
		if (StringUtils.isBlank(transaction.getPineTxnStatus())) {
			status_code =transaction.getPpcPinePGTxnStatus();
		}else {
			status_code = transaction.getPineTxnStatus();
		}
		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(status_code, "PINELABS");
		logger.info("PINELABS : Error code mapping  : "+errorMappingDTO);
		
		fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getPineTxnId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getPineTxnId());

		if (StringUtils.isNoneBlank(transaction.getPineTxnStatus())) {
			fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
		} 

		fields.put(FieldType.PG_TXN_MESSAGE.getName(),errorMappingDTO.getStatusMsg());
		fields.put(FieldType.RRN.getName(), transaction.getPineTxnId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());

	}
	
	public void updateRefundResponse(Fields fields) {

		
		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getPineTxnStatus(), "PINELABS");
		logger.info("PINELABS : Error code mapping  : "+errorMappingDTO);
		
		fields.put(FieldType.STATUS.getName(),errorMappingDTO.getPgStatus());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getRefundId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getRefundId());

		if (StringUtils.isNoneBlank(transaction.getPineTxnStatus())) {
			fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
		}

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
		fields.put(FieldType.RRN.getName(), transaction.getRefundId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
	}
	
	Error code mapping from db */
	
	public void updateResponse(Fields fields) {
		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;
		if ((StringUtils.isNotBlank(transaction.getPineTxnStatus()))
				&& ((transaction.getPineTxnStatus()).equals("4"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else {
			if (StringUtils.isNotBlank(transaction.getPineTxnStatus())) {

				PinelabsResultType resultInstance = PinelabsResultType
						.getInstanceFromName(transaction.getPineTxnStatus());
				if ((resultInstance != null)) {
					status = resultInstance.getStatusName();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

					if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
						pgTxnMsg = transaction.getResponseMsg();
					} else {
						pgTxnMsg = resultInstance.getMessage();
					}

				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");

					if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
						pgTxnMsg = transaction.getResponseMsg();
					} else {
						pgTxnMsg = "Transaction failed at acquirer";
					}

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;

				if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
					pgTxnMsg = transaction.getResponseMsg();
				} else {
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getResponseMsg());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getPineTxnId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getPineTxnId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getPineTxnStatus());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getPineTxnId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getPineTxnStatus());

	}

	public void updateStatusResponse(Fields fields) {

		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getPineTxnStatus()))
				&& ((transaction.getPineTxnStatus()).equals("4"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		} else {
			if (StringUtils.isNotBlank(transaction.getPineTxnStatus())) {

				PinelabsResultType resultInstance = PinelabsResultType
						.getInstanceFromName(transaction.getPineTxnStatus());
				if ((resultInstance != null)) {
					status = resultInstance.getStatusName();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {

					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					pgTxnMsg = "Transaction failed at acquirer";

					if (StringUtils.isNoneBlank(transaction.getResponseMsg())) {
						pgTxnMsg = transaction.getResponseMsg();
					}
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				if (StringUtils.isNoneBlank(transaction.getResponseMsg())) {
					pgTxnMsg = transaction.getResponseMsg();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getResponseMsg());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getPineTxnId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getPineTxnId());

		if (StringUtils.isNoneBlank(transaction.getPineTxnStatus())) {
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getPineTxnStatus());
		} else {
			errorType.getResponseCode();
		}

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getPineTxnId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getPineTxnStatus());

	}

	public void updateRefundResponse(Fields fields) {

		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getPineTxnStatus()))
				&& ((transaction.getPineTxnStatus()).equalsIgnoreCase("6"))) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}else if ((StringUtils.isNotBlank(transaction.getPineTxnStatus()))
				&& ((transaction.getPineTxnStatus()).equalsIgnoreCase("1"))) {
			status = StatusType.PENDING.getName();
			errorType = ErrorType.PENDING;
			pgTxnMsg = ErrorType.PENDING.getResponseMessage();
		} else {
			if (StringUtils.isNotBlank(transaction.getPineTxnStatus())) {

				PinelabsResultType resultInstance = PinelabsResultType
						.getInstanceFromName(transaction.getPineTxnStatus());
				if ((resultInstance != null)) {
					status = resultInstance.getStatusName();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {

					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					pgTxnMsg = "Transaction failed at acquirer";

					if (StringUtils.isNoneBlank(transaction.getResponseMsg())) {
						pgTxnMsg = transaction.getResponseMsg();
					}
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				if (StringUtils.isNoneBlank(transaction.getResponseMsg())) {
					pgTxnMsg = transaction.getResponseMsg();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), transaction.getResponseMsg());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getRefundId());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getRefundId());

		if (StringUtils.isNoneBlank(transaction.getPineTxnStatus())) {
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getPineTxnStatus());
		} else {
			errorType.getResponseCode();
		}

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getRefundId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getPineTxnStatus());
	}

}
