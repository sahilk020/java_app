package com.pay10.pg.history;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.ModeType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.service.RefundValidationService;

@Service
public class Historian {

	@Autowired
	private Fields field;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RefundValidationService refundValidationService;

	@Autowired
	private StaticDataProvider staticDataProvider;

	@Autowired
	private PropertiesManager propertiesManager;

	// private static Map<String, User> userMap = new HashMap<String, User>();

	private static Logger logger = LoggerFactory.getLogger(Historian.class.getName());

	public void findPrevious(Fields fields) throws SystemException {
		// Previous check already done, return

		Fields previous = fields.getPrevious();
		if (null != previous && previous.size() > 0) {
			return;
		}
		String requestTxnType = fields.get(FieldType.TXNTYPE.getName());
		if (requestTxnType.equals(TransactionType.REFUND.getName())) {
			String pgRefId = fields.get(FieldType.PG_REF_NUM.getName());
			fields.put(FieldType.ORIG_TXN_ID.getName(), pgRefId);

			fields.put(FieldType.ORIG_TXNTYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		}
		// Check if a previous related transaction exists in system
		String origTxnId = fields.get(FieldType.ORIG_TXN_ID.getName());
		if (null != origTxnId) {
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (txnType.equals(TransactionType.CAPTURE.getName()) || txnType.equals(TransactionType.STATUS.getName())) {
				field.refreshPrevious(fields);
			} // if
			else if (txnType.equals(TransactionType.REFUND.getName())) {
				refundValidationService.checkRefund(fields);
			} else if (txnType.equals(TransactionType.SALE.getName())) {
				// Add previous for webservice based transactions(without
				// session)
				if (StringUtils.isEmpty(fields.get(FieldType.ACQUIRER_TYPE.getName()))) {
					field.refreshPrevious(fields);
				}
			} // else if
		} // if
	

	
	}

	public void validateDuplicateOrderId(Fields fields) throws SystemException {
		User user = null;

		if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
				&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
						.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
			user = staticDataProvider.getUserData(fields.get(FieldType.PAY_ID.getName()));
		} else {
			user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		}
		
		if((fields.contains(FieldType.IS_MERCHANT_HOSTED.getName())) && !(StringUtils.isEmpty(fields.get(FieldType.IS_MERCHANT_HOSTED.getName()))) && (fields.get(FieldType.IS_MERCHANT_HOSTED.getName()).equals("Y"))) {
			if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.ENROLL.getName())) {
				if (!user.isAllowSaleDuplicate()) {
					field.validateDupicateSaleOrderId(fields);
				}
				if (!user.isAllowRefundInSale()) {
					field.validateDupicateRefundOrderIdInSale(fields);
				}
			} else {
				if (!user.isAllowRefundDuplicate()) {
					field.validateDupicateRefundOrderId(fields);
				}
				if (!user.isAllowSaleInRefund()) {
					field.validateDupicateSaleOrderIdInRefund(fields);
				}

			}
		}

		else if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())) {
			if (!user.isAllowSaleDuplicate()) {
				field.validateDupicateSaleOrderId(fields);
			}
			if (!user.isAllowRefundInSale()) {
				field.validateDupicateRefundOrderIdInSale(fields);
			}
		} else {
			if (!user.isAllowRefundDuplicate()) {
				field.validateDupicateRefundOrderId(fields);
			}
			if (!user.isAllowSaleInRefund()) {
				field.validateDupicateSaleOrderIdInRefund(fields);
			}

		}
		
		/*
		 * String allowDupicate = user.getAllowDuplicateOrderId().getName(); if
		 * (allowDupicate.equals(OrderIdType.NEVER.getName())) {
		 * field.validateDupicateOrderId(fields); } else if
		 * (allowDupicate.equals(OrderIdType.ALLOW_DUPLICATE_INDIVIDUALLY.getName())) {
		 * String txnType = fields.get(FieldType.TXNTYPE.getName()); if
		 * (txnType.equals(TransactionType.NEWORDER.getName())) {
		 * field.validateDupicateSaleOrderId(fields); } else {
		 * field.validateDupicateRefundOrderId(fields); } }
		 */
	}

	public void validateDuplicateSubmit(Fields fields) throws SystemException {
		field.validateDupicateSubmit(fields);
	}

	public void findPreviousForStatus(Fields fields) throws SystemException {
		// Previous check already done, return
		Fields previous = fields.getPrevious();
		if (null != previous && previous.size() > 0) {
			return;
		}

		field.refreshPreviousForStatus(fields);

	}
	
	public void findPreviousForStatusOfRefund(Fields fields) throws SystemException {
		// Previous check already done, return
		Fields previous = fields.getPrevious();
		if (null != previous && previous.size() > 0) {
			return;
		}
		field.refreshPreviousForRefundStatus(fields);
	}

	public void findPreviousForVerify(Fields fields) throws SystemException {
		// Previous check already done, return
		Fields previous = fields.getPrevious();
		if (null != previous && previous.size() > 0) {
			return;
		}

		field.refreshPreviousForVerify(fields);

	}

	public void findPreviousForSettlement(Fields fields) throws SystemException {
		// Previous check already done, return
		Fields previous = fields.getPrevious();
		if (null != previous && previous.size() > 0) {
			return;
		}
		// Add new for kotak
		if (fields.get(FieldType.NODAL_ACQUIRER.getName()).equalsIgnoreCase("KOTAK")) {
			field.refreshPreviousForsettlementKotak(fields);
			// populateFieldsFromPreviousSettlementKotak(fields);
		} else {
			field.refreshPreviousForsettlement(fields);
		}
		populateFieldsFromPreviousSettlement(fields);

	}

	public void findPreviousForReco(Fields fields) throws SystemException {
		// Previous check already done, return

		Fields previous = fields.getPrevious();
		if (null != previous && previous.size() > 0) {
			return;
		}

		field.refreshPreviousForReco(fields);

	}

	public void populateFieldsFromPrevious(Fields fields) throws SystemException {
		Fields previous = fields.getPrevious();
		if (null != previous && previous.size() > 0) {

			// ORDER_ID in this request is required from previous request, this
			// will allow to link support
			// transactions to link to original transactions
			fields.put(FieldType.ORDER_ID.getName(), previous.get(FieldType.ORDER_ID.getName()));

			// Currency Code is required to process amount formating in support
			// transactions
			String currencyCode = previous.get(FieldType.CURRENCY_CODE.getName());
			if (null != currencyCode) {
				fields.put(FieldType.CURRENCY_CODE.getName(), currencyCode);
			}

			String is_Status_Final = previous.get(FieldType.IS_STATUS_FINAL.getName());
			if (null != is_Status_Final) {
				fields.put(FieldType.IS_STATUS_FINAL.getName(), is_Status_Final);
			}

			// Acquirer of original transaction
			String acquirerType = previous.get(FieldType.ACQUIRER_TYPE.getName());
			if (null != acquirerType) {
				fields.put(FieldType.ACQUIRER_TYPE.getName(), acquirerType);
			}

			// get PG_REF_NO for capture
			String pgRefNo = previous.get(FieldType.PG_REF_NUM.getName());
			if (null != pgRefNo) {
				fields.put(FieldType.PG_REF_NUM.getName(), pgRefNo);
			}

			// OID of original transaction
			String oid = previous.get(FieldType.OID.getName());
			if (null != oid) {
				fields.put(FieldType.OID.getName(), oid);
			}

			// Acquirer of original transaction
			String acq_id = previous.get(FieldType.ACQ_ID.getName());
			if (null != acq_id && !(acq_id.equals("0") || StringUtils.isBlank(acq_id))) {
				fields.put(FieldType.ACQ_ID.getName(), acq_id);
			}

			// Mop type of original transaction
			String mopType = previous.get(FieldType.MOP_TYPE.getName());
			if (null != mopType) {
				fields.put(FieldType.MOP_TYPE.getName(), mopType);
			}

			// Payment type of original transaction
			String paymentType = previous.get(FieldType.PAYMENT_TYPE.getName());
			if (null != paymentType) {
				fields.put(FieldType.PAYMENT_TYPE.getName(), paymentType);
				if (paymentType.equals(PaymentType.UPI.getCode())) {
					String udf1 = previous.get(FieldType.UDF1.getName());
					if (null != udf1) {
						fields.put(FieldType.UDF1.getName(), udf1);
					}
					String udf2 = previous.get(FieldType.UDF2.getName());
					if (null != udf2) {
						fields.put(FieldType.UDF2.getName(), udf2);
					}
					String udf3 = previous.get(FieldType.UDF3.getName());
					if (null != udf3) {
						fields.put(FieldType.UDF3.getName(), udf3);
					}
					String udf4 = previous.get(FieldType.UDF4.getName());
					if (null != udf4) {
						fields.put(FieldType.UDF4.getName(), udf4);
					}
				}
			}

			// Card mask of original transaction for inserting into sale
			// transaction in case of webservice
			String cardMask = previous.get(FieldType.CARD_MASK.getName());
			if (null != cardMask) {
				fields.put(FieldType.CARD_MASK.getName(), cardMask);
			}
			// Date & Time of original transaction
			String pgDateTime = previous.get(FieldType.PG_DATE_TIME.getName());
			if (null != pgDateTime) {
				fields.put(FieldType.PG_DATE_TIME.getName(), pgDateTime);
			}
			// TODO.. use only for yes bank status enquiry
			// Amount of original transaction
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if ((txnType.equals(TransactionType.STATUS.getName())) || (txnType.equals(TransactionType.VERIFY.getName()))
					|| (txnType.equals(TransactionType.RECO.getName()))
					|| (txnType.equals(TransactionType.REFUNDRECO.getName()))) {
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), txnType);

				String preTxnType = previous.get(FieldType.ORIG_TXNTYPE.getName());
				if ((StringUtils.isNotBlank(preTxnType)) && (preTxnType.equals(TransactionType.RECO.getName())
						|| preTxnType.equals(TransactionType.REFUND.getName())
						|| preTxnType.equals(TransactionType.REFUNDRECO.getName()))) {
					fields.put(FieldType.TXNTYPE.getName(), preTxnType);
				} else {
					// User user = authenticator.getUserFromPayId(fields);
					User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
					ModeType previoustxnType = user.getModeType();
					if (null != previoustxnType) {
						fields.put(FieldType.TXNTYPE.getName(), previoustxnType.toString());
					}
				}

				String amount = previous.get(FieldType.AMOUNT.getName());
				if (null != amount) {
					fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(amount, currencyCode));
				}

				String txnMsg = previous.get(FieldType.PG_TXN_MESSAGE.getName());
				if (null != txnMsg) {
					fields.put(FieldType.PG_TXN_MESSAGE.getName(), txnMsg);
				}

				String responseCode = previous.get(FieldType.RESPONSE_CODE.getName());
				if (null != responseCode) {
					fields.put(FieldType.RESPONSE_CODE.getName(), responseCode);
				}

				String previoustxnId = previous.get(FieldType.PG_REF_NUM.getName());
				if (null != previoustxnId) {
					fields.put(FieldType.TXN_ID.getName(), previoustxnId);
				}

				String rrn = previous.get(FieldType.RRN.getName());
				if (null != rrn) {
					fields.put(FieldType.RRN.getName(), rrn);
				}

				String responseTime = previous.get(FieldType.CREATE_DATE.getName());
				if (null != responseTime) {
					fields.put(FieldType.RESPONSE_DATE_TIME.getName(), responseTime);
				}

				String responseMessage = previous.get(FieldType.RESPONSE_MESSAGE.getName());
				if (null != responseMessage) {
					fields.put(FieldType.RESPONSE_MESSAGE.getName(), responseMessage);
				}

				String authCode = previous.get(FieldType.AUTH_CODE.getName());
				if (null != authCode) {
					fields.put(FieldType.AUTH_CODE.getName(), authCode);
				}

				String custPhone = previous.get(FieldType.CUST_PHONE.getName());
				if (null != custPhone) {
					fields.put(FieldType.CUST_PHONE.getName(), custPhone);
				}

				String desc = previous.get(FieldType.PRODUCT_DESC.getName());
				if (null != desc) {
					fields.put(FieldType.PRODUCT_DESC.getName(), desc);
				}

				String email = previous.get(FieldType.CUST_EMAIL.getName());
				if (null != email) {
					fields.put(FieldType.CUST_EMAIL.getName(), email);
				}

				String name = previous.get(FieldType.CARD_HOLDER_NAME.getName());
				if (null != name) {
					fields.put(FieldType.CARD_HOLDER_NAME.getName(), name);
				}

				String returnUrl = previous.get(FieldType.RETURN_URL.getName());
				if (null != returnUrl) {
					fields.put(FieldType.RETURN_URL.getName(), returnUrl);
				}

				String avr = previous.get(FieldType.AVR.getName());
				if (null != avr) {
					fields.put(FieldType.AVR.getName(), avr);
				}

				String status = previous.get(FieldType.STATUS.getName());
				if (null != status) {
					fields.put(FieldType.STATUS.getName(), status);
				}

				String totalAmount = previous.get(FieldType.TOTAL_AMOUNT.getName());
				if (null != totalAmount) {
					fields.put(FieldType.TOTAL_AMOUNT.getName(), Amount.formatAmount(totalAmount, currencyCode));
				}

			} else {
				String rrn = previous.get(FieldType.RRN.getName());
				if (null != rrn) {
					fields.put(FieldType.RRN.getName(), rrn);
				}

				String email = previous.get(FieldType.CUST_EMAIL.getName());
				if (null != email) {
					fields.put(FieldType.CUST_EMAIL.getName(), email);
				}
				
				String phone = previous.get(FieldType.CUST_PHONE.getName());
				if (null != phone) {
					fields.put(FieldType.CUST_PHONE.getName(), phone);
				}

				String name = previous.get(FieldType.CARD_HOLDER_NAME.getName());
				if (null != name) {
					fields.put(FieldType.CARD_HOLDER_NAME.getName(), name);
				}

				String totalAmount = previous.get(FieldType.TOTAL_AMOUNT.getName());
				if (null != totalAmount) {
					fields.put(FieldType.TOTAL_AMOUNT.getName(), Amount.formatAmount(totalAmount, currencyCode));
				}

				String saleAmount = previous.get(FieldType.AMOUNT.getName());
				if (null != saleAmount) {
					fields.put(FieldType.SALE_AMOUNT.getName(), Amount.formatAmount(saleAmount, currencyCode));
				}

				String saleTotalAmount = previous.get(FieldType.TOTAL_AMOUNT.getName());
				if (null != saleTotalAmount) {
					fields.put(FieldType.SALE_TOTAL_AMOUNT.getName(),
							Amount.formatAmount(saleTotalAmount, currencyCode));
				}

				String cardHolderType = previous.get(FieldType.CARD_HOLDER_TYPE.getName());
				fields.put(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType);

				String transactionsRegion = previous.get(FieldType.PAYMENTS_REGION.getName());
				fields.put(FieldType.PAYMENTS_REGION.getName(), transactionsRegion);

				String surchargeFlag = previous.get(FieldType.SURCHARGE_FLAG.getName());
				fields.put(FieldType.SURCHARGE_FLAG.getName(), surchargeFlag);

				if (StringUtils.isNotBlank(previous.get(FieldType.AUTH_CODE.getName()))) {
					fields.put(FieldType.AUTH_CODE.getName(), previous.get(FieldType.AUTH_CODE.getName()));
				}

			}
			// TODO.. use only for first data status enquiry
			// Orig txnId of original transaction
			String origTxnId = previous.get(FieldType.ORIG_TXN_ID.getName());
			if (null != origTxnId) {
				fields.put(FieldType.ORIG_TXN_ID.getName(), origTxnId);
			}
		}

	}

	public void populateFieldsFromPreviousSettlement(Fields fields) throws SystemException {
		Fields previous = fields.getPrevious();

		String appId = previous.get(FieldType.APP_ID.getName());
		if (null != appId) {
			fields.put(FieldType.APP_ID.getName(), appId);
		}
		String beneCd = previous.get(FieldType.BENEFICIARY_CD.getName());
		if (null != beneCd) {
			fields.put(FieldType.BENEFICIARY_CD.getName(), beneCd);
		}
		String beneAcc = previous.get(FieldType.BENE_ACCOUNT_NO.getName());
		if (null != beneAcc) {
			fields.put(FieldType.BENE_ACCOUNT_NO.getName(), beneAcc);
		}
		String beneName = previous.get(FieldType.BENE_NAME.getName());
		if (null != beneName) {
			fields.put(FieldType.BENE_NAME.getName(), beneName);
		}
		String srcAC = previous.get(FieldType.SRC_ACCOUNT_NO.getName());
		if (null != srcAC) {
			fields.put(FieldType.SRC_ACCOUNT_NO.getName(), srcAC);
		}
		String currencyCode = previous.get(FieldType.CURRENCY_CODE.getName());
		if (null != currencyCode) {
			fields.put(FieldType.CURRENCY_CODE.getName(), currencyCode);
		}
		String amount = previous.get(FieldType.AMOUNT.getName());
		if (null != amount) {
			fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(amount, currencyCode));
		}

		String desc = previous.get(FieldType.PRODUCT_DESC.getName());
		if (null != desc) {
			fields.put(FieldType.PRODUCT_DESC.getName(), desc);
		}

		String paymentType = previous.get(FieldType.PAYMENT_TYPE.getName());
		if (null != paymentType) {
			fields.put(FieldType.PAYMENT_TYPE.getName(), paymentType);
		}

	}

	public void validateSupportTransaction(Fields fields) throws SystemException {
		String origTxnId = fields.get(FieldType.ORIG_TXN_ID.getName());
		if (null != origTxnId) {

			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (txnType.equals(TransactionType.REFUND.getName()) || txnType.equals(TransactionType.CAPTURE.getName())) {
				Fields previous = fields.getPrevious();
				if (null == previous || previous.size() < 1) {
					fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
					throw new SystemException(ErrorType.NO_SUCH_TRANSACTION, "Previous transaction not found");
				}
			} // if
		} // if
	}

	public void duplicateProcessor(Fields fields) throws SystemException {
		fields.checkDuplicate();
		String duplicate = fields.get(FieldType.DUPLICATE_YN.getName());
		if (null != duplicate && duplicate.equals("Y")) {
			fields.setValid(false);
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DUPLICATE.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.DUPLICATE.getResponseMessage());
			fields.put(FieldType.STATUS.getName(), StatusType.DUPLICATE.getName());
		}
	}

	public boolean isOrderIdBasedDuplicateAllowed(Fields fields) {
		boolean isAllowed = false;
		String flag = ConfigurationConstants.DUPLICATE_ON_ORDER_ID.getValue();
		if (StringUtils.isEmpty(flag)) {
			isAllowed = true;
		}

		if (flag.equals("0")) {
			isAllowed = false;
		} else {
			isAllowed = true;
		}

		// If configuration based check is done
		if (!isAllowed) {
			String duplicateYn = fields.get(FieldType.DUPLICATE_YN.getName());
			if (null != duplicateYn && duplicateYn.equals("Y")) {
				isAllowed = true;
			}
		}

		return isAllowed;
	}

	public void detectDuplicate(Fields fields) throws SystemException {
		if (!isOrderIdBasedDuplicateAllowed(fields)) {
			return;
		}

		TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
		switch (transactionType) {
		case AUTHORISE:
			duplicateProcessor(fields);
			break;
		case SALE:
			duplicateProcessor(fields);
			break;
		case NEWORDER:
			duplicateProcessor(fields);
			break;
		default:
			break;
		}
	}
	
	public void addPreviousSaleFields(Fields fields) throws SystemException {
		findPreviousForSale(fields);
		populateFieldsFromPreviousSale(fields);
	}
	
	public void findPreviousForSale(Fields fields) throws SystemException {
		// Previous check already done, return
		Fields previous = fields.getPrevious();
		if (null != previous && previous.size() > 0) {
			return;
		}

		field.refreshPreviousForSale(fields);

	}

	public void populateFieldsFromPreviousSale(Fields fields) throws SystemException {
		Fields previous = fields.getPrevious();
		if (null != previous && previous.size() > 0) {

			String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
			if (paymentType.equalsIgnoreCase(PaymentType.UPI.getCode())) {
				fields.put(FieldType.PAYER_ADDRESS.getName(), previous.get(FieldType.PAYER_ADDRESS.getName()));
			}
		}

	}
}
