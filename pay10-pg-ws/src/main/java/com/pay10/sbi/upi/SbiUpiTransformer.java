package com.pay10.sbi.upi;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.SBIUpiResultType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service("sbiUpiTransformer")
public class SbiUpiTransformer {
	private static Logger logger = LoggerFactory.getLogger(SbiUpiTransformer.class.getName());
	private Transaction transaction = null;

	
	public SbiUpiTransformer(Transaction transactionResponse) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void updateInvalidVpaResponse(Fields fields, String response) {

		fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_VPA.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.INVALID_VPA.getResponseMessage());
		// fields.remove(FieldType.ACQUIRER_TYPE.getName());
	}

	public void updateInvalidHandshakeResponse(Fields fields, String response) {

		fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.AUTHENTICATION_UNAVAILABLE.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.AUTHENTICATION_UNAVAILABLE.getResponseMessage());
		
		// fields.remove(FieldType.ACQUIRER_TYPE.getName());
	}

	
	public void updateResponse(Fields fields, Transaction transactionResponse) throws SystemException {
		try {
			logger.info(" inside SBI UPI  SBIUPITransformer in  updateResponse method response code is ==  "
					+ transactionResponse.getStatus());

			String txnType = fields.get(FieldType.TXNTYPE.getName());
			String status = "";
			ErrorType errorType = null;
			String pgTxnMsg = "";
			if (txnType.equals(TransactionType.SALE.getName())) {
				if (StringUtils.isNotBlank(transactionResponse.getStatus())
						&& transactionResponse.getStatus().equals(Constants.SUCCESS_RESPONSE)) {
					status = StatusType.SENT_TO_BANK.getName();
					errorType = ErrorType.SUCCESS;
					pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
				} else {
					if (StringUtils.isNotBlank(transactionResponse.getStatus())) {
						SBIUpiResultType resultInstance = SBIUpiResultType
								.getInstanceFromName(transactionResponse.getStatus());
						logger.info(" inside SBI UPI Transformer  in  updateResponse method resultInstance is : == "
								+ resultInstance);

						if (resultInstance != null) {
							if (resultInstance.getiPayCode() != null) {
								logger.info(
										" inside SBI UPI Transformer  in  updateResponse method resultInstance is for collect transaction ==  "
												+ resultInstance.getStatusName() + (resultInstance.getiPayCode()));
								status = resultInstance.getStatusName();
								errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
								pgTxnMsg = resultInstance.getMessage();
							} else {
								status = StatusType.REJECTED.getName();
								errorType = ErrorType.REJECTED;
								pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

							}

						} else {
							status = StatusType.REJECTED.getName();
							errorType = ErrorType.REJECTED;
							pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
						}
					} else {
						status = StatusType.FAILED_AT_ACQUIRER.getName();
						errorType = ErrorType.REJECTED;
						pgTxnMsg = Constants.NO_RES_RECEIVED_FROM_MERCHANT;

					}

				}
			} else if (txnType.equals(TransactionType.REFUND.getName())) {
				if (StringUtils.isNotBlank(transactionResponse.getStatus())) {
					if (transactionResponse.getStatus().equalsIgnoreCase(Constants.SBI_UPI_RESPONSE)) {
						status = StatusType.CAPTURED.getName();
						errorType = ErrorType.SUCCESS;
						pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
					} else {
						if (StringUtils.isNotBlank(transactionResponse.getStatus())) {
							SBIUpiResultType resultInstance = SBIUpiResultType
									.getInstanceFromName(transactionResponse.getResponse());
							logger.info(
									" inside SBI UPI Transformer  in  updateResponse method resultInstance is : == "
											+ resultInstance);
							if (resultInstance != null) {
								if (resultInstance.getiPayCode() != null) {
									logger.info(
											" inside SBI UPI Transformer action in  updateStatusResponse method resultInstance is  for refund transaction ==  "
													+ resultInstance.getStatusName() + (resultInstance.getiPayCode()));
									status = resultInstance.getStatusName();
									errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
									pgTxnMsg = resultInstance.getMessage();
								} else {
									status = StatusType.REJECTED.getName();
									errorType = ErrorType.REJECTED;
									pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

								}

							} else {
								status = StatusType.REJECTED.getName();
								errorType = ErrorType.REJECTED;
								pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
							}

						} else {
							status = StatusType.REJECTED.getName();
							errorType = ErrorType.REJECTED;
							pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
						}
					}
				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					pgTxnMsg = Constants.NO_RES_RECEIVED_FROM_MERCHANT;
				}

			} else if (txnType.equals(TransactionType.STATUS.getName()) || txnType.equals(TransactionType.ENQUIRY.getName())) {
				
					if (StringUtils.isNotBlank(transactionResponse.getStatus())) {
						if (transactionResponse.getStatus().equalsIgnoreCase(Constants.SBI_UPI_ENQUIRYRESPONSE)) {
							status = StatusType.CANCELLED.getName();
							errorType = ErrorType.CANCELLED;
							pgTxnMsg = transactionResponse.getResponseMessage();
						} else {
							if (StringUtils.isNotBlank(transactionResponse.getStatus())) {
								if (transactionResponse.getStatus().equalsIgnoreCase(Constants.SBI_ENQUIRY_SUCCESS_MESSAGE)){
									status = StatusType.CAPTURED.getName();
									errorType = ErrorType.SUCCESS;
									pgTxnMsg = transactionResponse.getResponseMessage();
								}
								
								else if (transactionResponse.getStatus()
										.equalsIgnoreCase(Constants.SBI_REFUND_ENQUIRY_FAILURE_MESSAGE)){
									status = StatusType.FAILED.getName();
									errorType = ErrorType.FAILED;
									pgTxnMsg = transactionResponse.getResponseMessage();
								}
								else if (transactionResponse.getStatus().equalsIgnoreCase(Constants.SBI_UPI_ENQUIRY_PENDING)){
									status = StatusType.PENDING.getName();
									errorType = ErrorType.PENDING;
									pgTxnMsg = transactionResponse.getResponseMessage();
								} else {
									SBIUpiResultType resultInstance = SBIUpiResultType
											.getInstanceFromName(transactionResponse.getResponse());
									if (resultInstance != null) {
										if (resultInstance.getiPayCode() != null) {
											status = resultInstance.getStatusName();
											errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
											pgTxnMsg = resultInstance.getMessage();
										} else {
											status = StatusType.REJECTED.getName();
											errorType = ErrorType.REJECTED;
											pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

										}

									} else {
										status = StatusType.REJECTED.getName();
										errorType = ErrorType.REJECTED;
										pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
									}
								}

							} else {
								status = StatusType.REJECTED.getName();
								errorType = ErrorType.REJECTED;
								pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
							}
						}
					} else {
						status = StatusType.REJECTED.getName();
						errorType = ErrorType.REJECTED;
						pgTxnMsg = Constants.NO_RES_RECEIVED_FROM_MERCHANT;
					}
				
			
			}
			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.toString().replaceAll("_", ""));
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.ACQ_ID.getName(), transactionResponse.getNpciTransId());
			fields.put(FieldType.RRN.getName(), transactionResponse.getCustRefNo());
			fields.put(FieldType.AUTH_CODE.getName(), transactionResponse.getUpiTransRefNo());//getCustRefNo()
			fields.put(FieldType.PG_RESP_CODE.getName(), transactionResponse.getStatus());
			fields.put(FieldType.PG_TXN_STATUS.getName(), transactionResponse.getStatusDesc());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
			fields.put(FieldType.UDF1.getName(), transactionResponse.getPayeeVPA());
			fields.put(FieldType.PAYER_ADDRESS.getName(), transactionResponse.getPayerVPA());
			fields.put(FieldType.PAYEE_ADDRESS.getName(), transactionResponse.getPayeeVPA());
			fields.put(FieldType.PG_DATE_TIME.getName(), transactionResponse.getDateTime());
			
			
		} catch (Exception e) {
			logger.error("Unknown Exception :" + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in UpdateResponse method for yes upi in SBI UPITransformer");
		}

	}

}