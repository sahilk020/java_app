package com.pay10.pg.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.RouterConfigurationService;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.TxnType;
import com.pay10.commons.util.YesbankCbUpiResultType;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.security.Authenticator;
import com.pay10.pg.core.security.AuthenticatorFactory;
import com.pay10.pg.core.security.AuthenticatorImpl;

@Service("updateProcessor")
public class UpdateProcessor implements Processor {

	@Autowired
	private CryptoManager cryptoManager;
	
	@Autowired
	private GeneralValidator generalValidator;
	
	@Autowired
	FieldsDao fieldsDao;
	
	@Autowired
	private Fields field;
	
	@Autowired
	private RouterConfigurationService routerConfigurationService;
	
	@Autowired
	AuthenticatorImpl authenticator;
	
	private static Logger logger = LoggerFactory.getLogger(UpdateProcessor.class.getName());
	final String bsesPayid = PropertiesManager.propertiesMap.get(Constants.BSESPAYID.getValue());

	@Override
	public void preProcess(Fields fields) throws SystemException {
		cryptoManager.secure(fields);
	}

	@Override
	public void process(Fields fields) throws SystemException {
		String response = fields.get(FieldType.RESPONSE_CODE.getName());
		logger.info("inside updateProcessor in process method ==> " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + response);
		
		if(response.equals(ErrorType.VALIDATION_FAILED.getCode())){
			addOid(fields);
			prepareInvalidTransactionForStorage(fields);
			return;
		}
		
		if(StringUtils.isNotBlank(fields.get(FieldType.IS_MERCHANT_HOSTED.getName()))&&fields.get(FieldType.IS_MERCHANT_HOSTED.getName()).equals("Y")&&response.equals(ErrorType.DUPLICATE_ORDER_ID.getCode())) {
			logger.info("Duplicate order id found code here ");
			return;
		}

		
		if(response.equals(ErrorType.TRANSACTION_NOT_FOUND.getCode())) {
			return;
		}
		if(response.equals(ErrorType.NO_SUCH_TRANSACTION.getCode())) {
			return;
		}
		
		String merchantFlag = "N";
		if(StringUtils.isNotBlank(fields.get(FieldType.IS_MERCHANT_HOSTED.getName())) && fields.get(FieldType.IS_MERCHANT_HOSTED.getName()).equals("Y")) {
			merchantFlag = "Y";
		}
		
		if (fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName()) || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName()) || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.REFUNDRECO.getName()) || (fields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.INVALID_VPA.getResponseCode()) && merchantFlag.equalsIgnoreCase("N"))) {
			return;
		}
		// For Yes Upi Db entry(Enquiry) and check for the existing entry!!
		String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		
		if (null != pgRefNum && null != paymentType) {
			if ((fields.get(FieldType.PAYMENT_TYPE.getName()).equals(PaymentType.UPI.getCode()))
					&& ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.ENQUIRY.getName())))) {
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				if ((fields.get(FieldType.STATUS.getName()).equals(StatusType.CANCELLED.getName()))
						&& (YesbankCbUpiResultType.TRANSACTION_DECLINED_BY_CUSTOMER.getBankCode()
								.equals(fields.get(FieldType.PG_RESP_CODE.getName())))) {
					if ((field.createAllForYesUpiEnquiry(pgRefNum) == true)) {
						return;
					}
				} else {
					return;
				}

			} else if ((fields.get(FieldType.PAYMENT_TYPE.getName()).equals(PaymentType.UPI.getCode()))
					&& ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName())))) {
				if ((fields.get(FieldType.STATUS.getName()).equals(StatusType.CANCELLED.getName()))
						&& (YesbankCbUpiResultType.TRANSACTION_DECLINED_BY_CUSTOMER.getBankCode()
								.equals(fields.get(FieldType.PG_RESP_CODE.getName())))) {
					if ((field.createAllForYesUpiEnquiry(pgRefNum) == true)) {
						return;
					}
				}

			}
		}
		
		if((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName()))&&
				(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName())))&&(fields.get(FieldType.STATUS.getName()).equals(StatusType.FAILED.getName())||
						fields.get(FieldType.STATUS.getName()).equals(StatusType.FAILED_AT_ACQUIRER.getName())||fields.get(FieldType.STATUS.getName()).equals(StatusType.CAPTURED.getName())))
			{
				if(fieldsDao.findTransactionByPgRefforResponse(fields.get(FieldType.PG_REF_NUM.getName()))) {
					fields.put(FieldType.RESPONSE_CODE.getName(),ErrorType.DUPLICATE_RESPONSE.getCode());
					logger.error(ErrorType.DUPLICATE_RESPONSE.getResponseMessage(),fields.get(FieldType.PG_REF_NUM.getName()));

					return;
				}
			}

		addOid(fields);

		String transactionType = fields.get(FieldType.TXNTYPE.getName());
		String responseCode = fields.get(FieldType.RESPONSE_CODE.getName());

		if (responseCode.equalsIgnoreCase(ErrorType.ACUIRER_DOWN.getCode())) {
			routerConfigurationService.autoUdpateRouterConfiguration(fields, responseCode);
			logger.info("inside updateProcessor in process method  for .ACUIRER_DOWN.==> " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + response);
			
		}
		
		if (null == transactionType) {
			prepareInvalidTransactionForStorage(fields);
		} else if (responseCode.equals(ErrorType.VALIDATION_FAILED.getCode())) {
			if (!transactionType.equals(TransactionType.REFUND.getName())
					&& !transactionType.equals(TransactionType.STATUS.getName())
					&& !transactionType.equals(TransactionType.CAPTURE.getName())
					&& !StringUtils.isBlank(fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))) {
				fields.put(FieldType.TXNTYPE.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			}
			prepareInvalidTransactionForStorage(fields);
		} else if (responseCode.equals(ErrorType.DENIED_BY_FRAUD.getCode())){
			String pgrefNum = fields.get(FieldType.PG_REF_NUM.getName());
			if(pgrefNum == null){
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			}
			logger.info(".................1................");
			field.insert(fields);
		}
		else {
			if (bsesPayid.equalsIgnoreCase(fields.get(FieldType.PAY_ID.getName()))) {
				fields.put(FieldType.UDF9.getName(), fields.get(FieldType.UDF9.getName()));
				fields.put(FieldType.UDF10.getName(), fields.get(FieldType.UDF10.getName()));
				
				String udf10=fields.get(FieldType.UDF10.getName());
				
				String counterNo = PropertiesManager.propertiesMap.get(udf10.toUpperCase()+paymentType);
				fields.put(FieldType.UDF11.getName(), counterNo);
				
			}
			logger.info(".................2................");
			field.insert(fields);
			//fields.insert();
		}
		
		
	}

	public void prepareInvalidTransactionForStorage(Fields fields) throws SystemException {
		logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+fields.getFieldsAsString());
		String payId = fields.get(FieldType.PAY_ID.getName());
		if (!StringUtils.isEmpty(payId)) {
			
//			 Authenticator authenticator = AuthenticatorFactory.getAuthenticator();
			 authenticator.isUserExists(fields);

			String txnId = fields.get(FieldType.TXN_ID.getName());
			if (StringUtils.isEmpty(txnId)) {
				txnId = TransactionManager.getNewTransactionId();
			}
			String responseCode = fields.get(FieldType.RESPONSE_CODE.getName());
			String responseMessage = fields.get(FieldType.RESPONSE_MESSAGE.getName());
			String detailMessage = fields.get(FieldType.PG_TXN_MESSAGE.getName());
			String transactionType = fields.get(FieldType.TXNTYPE.getName());
			String returnUrl = fields.get(FieldType.RETURN_URL.getName());
			String orderId = fields.get(FieldType.ORDER_ID.getName());
			Fields internalFields = fields.removeInternalFields();
			String oid = fields.get(FieldType.OID.getName());
			String resellerId = fields.get(FieldType.RESELLER_ID.getName());
			if (StringUtils.isEmpty(oid)) {
				oid = txnId;
			}
			//Validate and insert fields
			if(StringUtils.isNotBlank(transactionType)){
				if(transactionType.equals(TxnType.REFUND.getName())) {
					fields.put(FieldType.ORIG_TXNTYPE.getName(), TxnType.REFUND.getName());
				}else {
					fields.put(FieldType.ORIG_TXNTYPE.getName(), TxnType.SALE.getName());
					fields.put(FieldType.TXNTYPE.getName(), TxnType.SALE.getName());
				}				
			}else {
				fields.put(FieldType.ORIG_TXNTYPE.getName(), TxnType.SALE.getName());
				fields.put(FieldType.TXNTYPE.getName(), TxnType.SALE.getName());
			}
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			fields.clear();
			fields.put(internalFields);
			fields.put(FieldType.TXNTYPE.getName(), txnType);
			fields.put(FieldType.TXN_ID.getName(), txnId);
			fields.put(FieldType.OID.getName(), oid);
			fields.put(FieldType.RESPONSE_CODE.getName(), responseCode);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), responseMessage);
			logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+fields.getFieldsAsString());
			if(fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.EKYC_VERIFICATION.getCode())) {
				logger.info("Ekyc Response Message and Response Code : " + fields.get(FieldType.RESPONSE_MESSAGE.getName()) + "\t" + fields.get(FieldType.RESPONSE_CODE.getName()));
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.EKYC_VERIFICATION.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_VERIFICATION.getResponseCode());
			}
			else if(fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.EKYC_DETAILS_NOT_FOUND.getCode())) {
				logger.info("Ekyc Response Message and Response Code : " + fields.get(FieldType.RESPONSE_MESSAGE.getName()) + "\t" + fields.get(FieldType.RESPONSE_CODE.getName()));
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.EKYC_DETAILS_NOT_FOUND.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_DETAILS_NOT_FOUND.getResponseCode());
			}
			else if(fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.EKYC_SERVICE_DOWN.getCode())) {
				logger.info("Ekyc Response Message and Response Code : " + fields.get(FieldType.RESPONSE_MESSAGE.getName()) + "\t" + fields.get(FieldType.RESPONSE_CODE.getName()));
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.EKYC_SERVICE_DOWN.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.EKYC_SERVICE_DOWN.getResponseCode());
			}
			else {
				fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			}
			
			logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+fields.getFieldsAsString());

			fields.put(FieldType.PAY_ID.getName(), payId);
			fields.put(FieldType.RETURN_URL.getName(), returnUrl);
			fields.put(FieldType.ORDER_ID.getName(), orderId);
			
			if (StringUtils.isNotBlank(resellerId)) {
				fields.put(FieldType.RESELLER_ID.getName(), resellerId);
			}
			
			try {
				generalValidator.validateField(FieldType.ORDER_ID, FieldType.ORDER_ID.getName() , fields);
			}catch(SystemException systemException) {
				fields.put(FieldType.ORDER_ID.getName(), Constants.SUCCESS_CODE.getValue());
			}
			
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), detailMessage);
			
			String pgrefNum = fields.get(FieldType.PG_REF_NUM.getName());
			if(pgrefNum == null){
				fields.put(FieldType.PG_REF_NUM.getName(), txnId);
			}
			logger.info("DATA33");
			field.insert(fields);
		}
	}

	public void addOid(Fields fields) {
		String oid = fields.get(FieldType.OID.getName());
		// Oid is already present, return
		if (!StringUtils.isEmpty(oid)) {
			return;
		}

		String transactionType = fields.get(FieldType.TXNTYPE.getName());
		if (null != transactionType && transactionType.equals(TransactionType.NEWORDER.getName())) {
			oid = fields.get(FieldType.TXN_ID.getName());
		} else {
			String origOid = fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName());
			if (!StringUtils.isEmpty(origOid)) {
				oid = origOid;
			} else {
				// to add OID for transactions that are through webservice or invalid
				oid = fields.get(FieldType.TXN_ID.getName());
			}
		}
		if (StringUtils.isEmpty(oid)) {
			logger.warn("Unable to add OID, this may cause some issues!");
		} else {
			fields.put(FieldType.OID.getName(), oid);
		}
	}

	@Override
	public void postProcess(Fields fields) {
		/*String transactionType = fields.get(FieldType.TXNTYPE.getName());
		if((transactionType.equals(TransactionType.SALE.getName())) || (transactionType.equals(TransactionType.REFUND.getName()))) {
			fields.put(FieldType.TXN_ID.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		}*/
		
	}
}
