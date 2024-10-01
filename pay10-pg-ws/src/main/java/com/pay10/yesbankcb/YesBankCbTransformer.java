package com.pay10.yesbankcb;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.YesbankCbUpiResultType;

/**
 * @author VJ
 *
 */
@Service("yesBankCbTransformer")
public class YesBankCbTransformer {
	private static Logger logger = LoggerFactory.getLogger(YesBankCbTransformer.class.getName());
	private Transaction transaction = null;

	@Autowired
	private FieldsDao fieldsDao;
	
	public YesBankCbTransformer(Transaction transactionResponse) {
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

	public void updateResponse(Fields fields, Transaction transactionResponse) throws SystemException {
		try {
			logger.info(" inside YESBANKCB  YesBankCbTransformer in  updateResponse method response code is ==  "
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
						YesbankCbUpiResultType resultInstance = YesbankCbUpiResultType
								.getInstanceFromName(transactionResponse.getStatus());
						logger.info(" inside YESBANKCB Transformer  in  updateResponse method resultInstance is : == "
								+ resultInstance);

						if (resultInstance != null) {
							if (resultInstance.getiPayCode() != null) {
								logger.info(
										" inside YESBANKCB Transformer  in  updateResponse method resultInstance is for collect transaction ==  "
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
					if (transactionResponse.getStatus().equalsIgnoreCase(Constants.YES_UPI_RESPONSE)) {
						status = StatusType.CAPTURED.getName();
						errorType = ErrorType.SUCCESS;
						pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
					} else {
						if (StringUtils.isNotBlank(transactionResponse.getStatus())) {
							YesbankCbUpiResultType resultInstance = YesbankCbUpiResultType
									.getInstanceFromName(transactionResponse.getResponse());
							logger.info(
									" inside YESBANKCB Transformer  in  updateResponse method resultInstance is : == "
											+ resultInstance);
							if (resultInstance != null) {
								if (resultInstance.getiPayCode() != null) {
									logger.info(
											" inside YESBANKCB Transformer action in  updateStatusResponse method resultInstance is  for refund transaction ==  "
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
						if (transactionResponse.getResponse().equalsIgnoreCase(Constants.YES_UPI_ENQUIRY_CODE)
								&& transactionResponse.getStatus()
										.equalsIgnoreCase(Constants.YES_UPI_ENQUIRYRESPONSE)) {
							status = StatusType.CANCELLED.getName();
							errorType = ErrorType.CANCELLED;
							pgTxnMsg = transactionResponse.getResponseMessage();
						} else {
							if (StringUtils.isNotBlank(transactionResponse.getStatus())) {
								if (transactionResponse.getStatus().equalsIgnoreCase(Constants.YES_ENQUIRY_SUCCESS_MESSAGE)
										&& transactionResponse.getResponse()
										.equalsIgnoreCase(Constants.YES_ENQUIRY_SUCCESS_CODE)){
									status = StatusType.CAPTURED.getName();
									errorType = ErrorType.SUCCESS;
									pgTxnMsg = transactionResponse.getResponseMessage();
								}
								
								else if (transactionResponse.getStatus()
										.equalsIgnoreCase(Constants.YES_REFUND_ENQUIRY_FAILURE_MESSAGE)){
									status = StatusType.FAILED.getName();
									errorType = ErrorType.FAILED;
									pgTxnMsg = transactionResponse.getResponseMessage();
								}
								else if (transactionResponse.getStatus().equalsIgnoreCase(Constants.YES_UPI_ENQUIRY_PENDING)){
									status = StatusType.PENDING.getName();
									errorType = ErrorType.PENDING;
									pgTxnMsg = transactionResponse.getResponseMessage();
								} else {
									YesbankCbUpiResultType resultInstance = YesbankCbUpiResultType
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
			//fields.put(FieldType.RESPONSE_MESSAGE.getName(), transactionResponse.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.ACQ_ID.getName(), transactionResponse.getAcq_id());
			fields.put(FieldType.RRN.getName(), transactionResponse.getRrn());
			fields.put(FieldType.PG_RESP_CODE.getName(), transactionResponse.getResponse());
			fields.put(FieldType.PG_TXN_STATUS.getName(), transactionResponse.getStatus());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
			fields.put(FieldType.UDF1.getName(), transactionResponse.getMerchantVpa());
			fields.put(FieldType.PG_DATE_TIME.getName(), transactionResponse.getDateTime());
			fields.put(FieldType.TXN_ID.getName(),TransactionManager.getNewTransactionId());
			if(StringUtils.isNotBlank(transactionResponse.getTrantRef())) {
			fields.put(FieldType.ACCT_ID.getName(),transactionResponse.getTrantRef());
			}
			fields.put(FieldType.UDF7.getName(), transactionResponse.getAccNo());
			fields.put(FieldType.UDF8.getName(), transactionResponse.getCreditTxn());
			
		} catch (Exception e) {
			logger.error("Unknown Exception :" + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in UpdateResponse method for yes upi in YesBankCbTransformer");
		}

	}

}
