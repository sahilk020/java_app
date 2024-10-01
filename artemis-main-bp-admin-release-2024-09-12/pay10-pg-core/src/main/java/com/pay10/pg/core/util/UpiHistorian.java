package com.pay10.pg.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class UpiHistorian {

	private static Logger logger = LoggerFactory.getLogger(UpiHistorian.class.getName());
	
	@Autowired
	private Fields field;

	public void findPrevious(Fields fields) throws SystemException {

				
		switch(AcquirerType.getInstancefromCode(fields.get(FieldType.ACQUIRER_TYPE.getName()))){
		case FEDERAL:
			findFieldsForFederal(fields);
			break;
		case HDFC:
			findFieldsForHdfc(fields);
			break;
		case YESBANKCB:
			findFieldsForYesBankCb(fields);
			break;
		case SBI:
			findFieldsForSBIUPI(fields);
			break;
		case IDFCUPI:
			findFieldsForIdfcUpi(fields);
			break;
		case KOTAK:
			findFieldsForKotak(fields);
			break;
		case AXISBANK:
			findFieldsForKotak(fields);
			break;
		case FREECHARGE:
			findFieldsForIdfcUpi(fields);
			break;
		case NB_ICICI_BANK:
			findFieldsForYesBankCb(fields);
			break;
		 case COSMOS:
             findFieldsForCosmos(fields);
             break;
		default:
			break;
		}
		
		populateFieldsFromPrevious(fields);
		logger.info("UpiHistorian :: findPrevious, Fields ="+fields.getFieldsAsString());
	}
	
	public void findPreviousForRefund(Fields fields) throws SystemException {

		
		switch(AcquirerType.getInstancefromCode(fields.get(FieldType.ACQUIRER_TYPE.getName()))){
		case FEDERAL:
			findFieldsForFederalRefund(fields);
			break;
		case FSS:
			findFieldsForHdfc(fields);
			break;
		case YESBANKCB:
			findFieldsForYesBankCb(fields);
			break;
		case IDFCUPI:
			findFieldsForIdfcUpi(fields);
			break;
		case KOTAK:
			findFieldsForKotak(fields);
			break;
		case AXISBANK:
			findFieldsForKotak(fields);
			break;
		case FREECHARGE:
			findFieldsForIdfcUpi(fields);
			break;
		default:
			break;
		}
		

		populateFieldsFromPrevious(fields);
	}
	
	public void findFieldsForCosmos(Fields fields) {

        String refId = fields.get(FieldType.PG_REF_NUM.getName());
        if (null != refId) {
            String txnType = fields.get(FieldType.TXNTYPE.getName());
            if (txnType.equals(TransactionType.SALE.getName())) {
                try {
                    field.refreshPreviousForAcquirerUpi(fields);
                } catch (SystemException e) {
                    logger.info("Exception in UpiHistorian " + e.getMessage());
                }
            } // if

        }

    }
	public void findFieldsForFederal(Fields fields){
		
		String refId = fields.get(FieldType.UDF5.getName());
		if (null != refId) {
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (txnType.equals(TransactionType.SALE.getName())) {
				try {
					field.refreshPreviousForFedUpi(fields);
				} catch (SystemException e) {
					logger.info("Exception in UpiHistorian "+e.getMessage());
				}
			} // if

		}
		
	}
	
	public void findFieldsForFederalRefund(Fields fields){
		
		String refId = fields.get(FieldType.UDF5.getName());
		if (null != refId) {
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (txnType.equals(TransactionType.REFUND.getName())) {
				try {
					field.refreshPreviousForFedUpiRefund(fields);
				} catch (SystemException e) {
					logger.info("Exception in UpiHistorian "+e.getMessage());
				}
			} // if

		}
		
	}
	
	public void findFieldsForHdfc(Fields fields){
		
		String refId = fields.get(FieldType.PG_REF_NUM.getName());
		if (null != refId) {
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (txnType.equals(TransactionType.SALE.getName())) {
				try {
					field.refreshPreviousForHdfcUpi(fields);
				} catch (SystemException e) {
					logger.info("Exception in UpiHistorian "+e.getMessage());
				}
			} // if

		}
		
	}

	public void findFieldsForYesBankCb(Fields fields) {

		String refId = fields.get(FieldType.PG_REF_NUM.getName());
		if (null != refId) {
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (txnType.equals(TransactionType.SALE.getName())) {
				try {
					field.refreshPreviousForHdfcUpi(fields);
				} catch (SystemException e) {
					logger.info("Exception in UpiHistorian " + e.getMessage());
				}
			} // if

		}

	}

	public void findFieldsForSBIUPI(Fields fields) {

		String refId = fields.get(FieldType.PG_REF_NUM.getName());
		if (null != refId) {
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (txnType.equals(TransactionType.SALE.getName())) {
				try {
					field.refreshPreviousForHdfcUpi(fields);
				} catch (SystemException e) {
					logger.info("Exception in UpiHistorian " + e.getMessage());
				}
			} // if

		}

	}

	
	public void findFieldsForIdfcUpi(Fields fields) {

		String refId = fields.get(FieldType.PG_REF_NUM.getName());
		if (null != refId) {
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (txnType.equals(TransactionType.SALE.getName())) {
				try {
					field.refreshPreviousForHdfcUpi(fields);
				} catch (SystemException e) {
					logger.info("Exception in UpiHistorian " + e.getMessage());
				}
			} // if

		}

	}

	public void findFieldsForKotak(Fields fields) {

		String refId = fields.get(FieldType.PG_REF_NUM.getName());
		if (null != refId) {
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (txnType.equals(TransactionType.SALE.getName())) {
				try {
					field.refreshPreviousForHdfcUpi(fields);
				} catch (SystemException e) {
					logger.info("Exception in UpiHistorian " + e.getMessage());
				}
			} // if

		}

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

			String CARD_MASK = previous.get(FieldType.CARD_MASK.getName());
			if (null != CARD_MASK) {
				logger.info("cardmask for yesbank upi "+CARD_MASK);

				fields.put(FieldType.CARD_MASK.getName(), CARD_MASK);
			}
			logger.info("cardmask for yesbank upi ");
			String INTERNAL_CUST_IP = previous.get(FieldType.INTERNAL_CUST_IP.getName());
			if (null != INTERNAL_CUST_IP) {
				logger.info("INTERNAL_CUST_IP for yesbank upi "+INTERNAL_CUST_IP);

				fields.put(FieldType.INTERNAL_CUST_IP.getName(), INTERNAL_CUST_IP);
			}
			// get PG_REF_NO for capture
			String pgRefNo = previous.get(FieldType.PG_REF_NUM.getName());
			if (null != pgRefNo) {
				fields.put(FieldType.PG_REF_NUM.getName(), pgRefNo);
			}

			String pspName = previous.get(FieldType.PSPNAME.getName());
			if (null != pspName) {
				fields.put(FieldType.PSPNAME.getName(), pspName);
			}
			
			String PG_TDR_SC = previous.get(FieldType.PG_TDR_SC.getName());
			if (null != PG_TDR_SC) {
				fields.put(FieldType.PG_TDR_SC.getName(), PG_TDR_SC);
			}
			String ACQUIRER_TDR_SC = previous.get(FieldType.ACQUIRER_TDR_SC.getName());
			if (null != ACQUIRER_TDR_SC) {
				fields.put(FieldType.ACQUIRER_TDR_SC.getName(), ACQUIRER_TDR_SC);
			}
			String PG_GST = previous.get(FieldType.PG_GST.getName());
			if (null != PG_GST) {
				fields.put(FieldType.PG_GST.getName(), PG_GST);
			}
			String ACQUIRER_GST = previous.get(FieldType.ACQUIRER_GST.getName());
			if (null != ACQUIRER_GST) {
				fields.put(FieldType.ACQUIRER_GST.getName(), ACQUIRER_GST);
			}
			// OID of original transaction
			String oid = previous.get(FieldType.OID.getName());
			if (null != oid) {
				fields.put(FieldType.OID.getName(), oid);
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
			}

			
			String CUST_NAME = previous.get(FieldType.CUST_NAME.getName());
			if (null != CUST_NAME) {
				fields.put(FieldType.CUST_NAME.getName(), CUST_NAME);
			}

			String internalOrigTxnType = previous.get(FieldType.ORIG_TXNTYPE.getName());
			if (null != paymentType) {
				fields.put(FieldType.ORIG_TXNTYPE.getName(), internalOrigTxnType);
			}

			String amount = previous.get(FieldType.AMOUNT.getName());
			if (null != amount) {
				fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(amount, currencyCode));
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

			String origTxnId = previous.get(FieldType.ORIG_TXN_ID.getName());
			if (null != origTxnId) {
				fields.put(FieldType.ORIG_TXN_ID.getName(), origTxnId);
			}
			
			String payId = previous.get(FieldType.PAY_ID.getName());
			if (null != payId) {
				fields.put(FieldType.PAY_ID.getName(), payId);
			}
			
			String acquirerType = previous.get(FieldType.ACQUIRER_TYPE.getName());
			if (null != acquirerType) {
				fields.put(FieldType.ACQUIRER_TYPE.getName(), acquirerType);
			}
			
			String udf1 = previous.get(FieldType.UDF1.getName());
			if (null != udf1) {
				fields.put(FieldType.UDF1.getName(), udf1);
			}
			
			String INTERNAL_REQUEST_FIELDS = previous.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
			if (null != INTERNAL_REQUEST_FIELDS) {
				fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), INTERNAL_REQUEST_FIELDS);
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
			
			String udf5 = previous.get(FieldType.UDF5.getName());
			if (null != udf5) {
				fields.put(FieldType.UDF5.getName(), udf5);
			}
			
			String udf6 = previous.get(FieldType.UDF6.getName());
			if (StringUtils.isNotBlank(udf6)) {
				fields.put(FieldType.UDF6.getName(), udf6);
			}
			
			String totalAmount = previous.get(FieldType.TOTAL_AMOUNT.getName());
			if (null != totalAmount) {
				fields.put(FieldType.TOTAL_AMOUNT.getName(),  Amount.formatAmount(totalAmount, currencyCode));
			}
			
			String surchargeFlag = previous.get(FieldType.SURCHARGE_FLAG.getName());
			if (null != surchargeFlag) {
				fields.put(FieldType.SURCHARGE_FLAG.getName(), surchargeFlag);
			}

			String routerConfigurationId = previous.get(FieldType.ROUTER_CONFIGURATION_ID.getName());
			if (null != routerConfigurationId) {
				fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(), routerConfigurationId);
			}

		}

	}
}
