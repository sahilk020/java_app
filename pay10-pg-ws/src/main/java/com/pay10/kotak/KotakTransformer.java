package com.pay10.kotak;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KotakTransformer {
	private static Logger logger = LoggerFactory.getLogger(KotakTransformer.class.getName());

	private Transaction transaction = null;

	public KotakTransformer(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateResponse(Fields fields) {
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;
		
		if (txnType.equals(TransactionType.SALE.getName())) {

			if ((StringUtils.isNotBlank(transaction.getAuthStatus())) && ((transaction.getAuthStatus()).equals("Y"))) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
				//errorCode = ErrorType.SUCCESS.getCode();

			} else {

				status = StatusType.FAILED.getName();
				errorType = ErrorType.FAILED;
				pgTxnMsg = ErrorType.FAILED.getResponseMessage();
				//errorCode = ErrorType.SUCCESS.getCode();

			}

		} else {}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		//fields.put(FieldType.AUTH_CODE.getName(), transaction.getAuthCode());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getBankReferenceNumber());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getAuthStatus());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getMerchantReference());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getAuthStatus());

	}

	public void updateResponseOld(Fields fields) {
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;
		if (txnType.equals(TransactionType.SALE.getName())) {

			if ((StringUtils.isNotBlank(transaction.getResponseCode()))
					&& ((transaction.getResponseCode()).equals("00"))) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

			} else {
				if (StringUtils.isNotBlank(transaction.getResponseCode())) {
					String mopType = fields.get(FieldType.MOP_TYPE.getName());
					if (mopType.equalsIgnoreCase(MopType.RUPAY.getName())) {
						KotakRupayResultType resultInstance = KotakRupayResultType
								.getInstanceFromName(transaction.getResponseCode());
						if ((resultInstance != null)) {
							status = resultInstance.getStatusName();
							errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
							pgTxnMsg = resultInstance.getMessage();
						} else {
							status = StatusType.FAILED_AT_ACQUIRER.getName();
							errorType = ErrorType.getInstanceFromCode("022");
							pgTxnMsg = "Transaction failed at acquirer";
						}
					} else {
						KotakVisaMasterResultType resultInstance = KotakVisaMasterResultType
								.getInstanceFromName(transaction.getResponseCode());
						if ((resultInstance != null)) {
							status = resultInstance.getStatusName();
							errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
							pgTxnMsg = resultInstance.getMessage();
						} else {
							status = StatusType.FAILED_AT_ACQUIRER.getName();
							errorType = ErrorType.getInstanceFromCode("022");
							pgTxnMsg = "Transaction failed at acquirer";
						}
					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

				}
			}
		} else {

			if (transaction.getStatus().equalsIgnoreCase("Success")) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			} else {
				if (StringUtils.isNotBlank(transaction.getResponseCode())) {
					String mopType = fields.get(FieldType.MOP_TYPE.getName());
					if (mopType.equalsIgnoreCase(MopType.RUPAY.getName())) {
						KotakRupayResultType resultInstance = KotakRupayResultType
								.getInstanceFromName(transaction.getStatus());
						if ((resultInstance != null)) {
							status = resultInstance.getStatusName();
							errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
							pgTxnMsg = resultInstance.getMessage();
						} else {
							status = StatusType.FAILED_AT_ACQUIRER.getName();
							errorType = ErrorType.getInstanceFromCode("022");
							pgTxnMsg = "Transaction failed at acquirer";
						}
					} else {
						KotakVisaMasterResultType resultInstance = KotakVisaMasterResultType
								.getInstanceFromName(transaction.getStatus());
						if ((resultInstance != null)) {
							status = resultInstance.getStatusName();
							errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
							pgTxnMsg = resultInstance.getMessage();
						} else {
							status = StatusType.FAILED_AT_ACQUIRER.getName();
							errorType = ErrorType.getInstanceFromCode("022");
							pgTxnMsg = "Transaction failed at acquirer";
						}
					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				}
			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		fields.put(FieldType.AUTH_CODE.getName(), transaction.getAuthCode());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getAcqId());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(), transaction.getAcqId());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());

	}
	
	
public void updateresponseKotakCard(Fields fields) {
		
		
		logger.info("Looking For Acquirer {} ErrorCode Mapping Status {}","KOTAKCARD",transaction.getResponseCode());
		ErrorMappingDTO errorMappingDTO = new ErrorMappingDAOImpl().getErrorMappingByAcqCode(transaction.getResponseCode(), "KOTAKCARD");
		logger.info("Error code mapping  : "+errorMappingDTO);
		
		if(null != errorMappingDTO) {
			fields.put(FieldType.STATUS.getName(), errorMappingDTO.getPgStatus());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMappingDTO.getPgMsg());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorMappingDTO.getPgCode());
			fields.put(FieldType.PG_RESP_CODE.getName(), errorMappingDTO.getAcqStatusCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), errorMappingDTO.getStatusMsg());
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorMappingDTO.getPgStatus());
			if(StringUtils.isNotBlank( transaction.getAcqId())) {
				fields.put(FieldType.RRN.getName(), transaction.getAcqId());
			}
				
		}else {
			logger.info("ErrorCodeMapping not fount for COSMOS Acquirer");
			
		}

		
	}
}
