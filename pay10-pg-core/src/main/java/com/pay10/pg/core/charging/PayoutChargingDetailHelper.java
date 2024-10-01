package com.pay10.pg.core.charging;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.PayoutTdrSettingDao;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.TdrSettingPayoutDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CurrencyNumber;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.commons.util.TransactionType;

@Service
public class PayoutChargingDetailHelper {
	private static Logger logger = LoggerFactory.getLogger(PayoutChargingDetailHelper.class.getName());
	@Autowired
	private PropertiesManager propertiesManager;
	@Autowired
	private StaticDataProvider staticDataProvider;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SurchargeDao surchargeDao;
	@Autowired
	private ChargingDetailsDao chargingDetailsDao;
	@Autowired
	private ServiceTaxDao serviceTaxDao;
	
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	private String payID;

	public String getPayID() {
		return payID;
	}

	public void setPayID(String payID) {
		this.payID = payID;
	}

	@Autowired
	private TdrSettingPayoutDao dao;
	
	
	public void addTransactionDataFieldsPayout(Fields fields) throws SystemException {

		logger.info("fields ---->> " + fields.getFieldsAsString());
		// Zero Surcharge / TDR for Refund Transactions
	
		// NO need to calculate charges in case of status enquiry by Merchant
		if ((fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName()))) {

			return;
		}

		// Calculate values only for Sale Transactions
		if (!(fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.ENROLL.getName()))) {
			if (!(fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName()))) {
				return;
			}

		}

		String payId = fields.get(FieldType.PAY_ID.getName());
		String channel = fields.get(FieldType.PAY_TYPE.getName());
		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		String currency = multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		String transactionType = fields.get(FieldType.TXNTYPE.getName());
		String paymentRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
		String cardHolderType = fields.get(FieldType.CARD_HOLDER_TYPE.getName());
		
	
		
		int decimal=Integer.parseInt(CurrencyNumber.getMultiplyerfromCode(fields.get(FieldType.CURRENCY_CODE.getName())));

		String[] tdr = null;
		try {
			tdr = dao.calculateTdrAndSurcharge(payId, channel, acquirer, "SALE",currency,
					String.valueOf((Double.valueOf(fields.get(FieldType.AMOUNT.getName()))/decimal)), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),paymentRegion,cardHolderType);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		
		
		BigDecimal acquirerTdr = new BigDecimal(tdr[0]);
		BigDecimal acquirerGst = new BigDecimal(tdr[1]);
		BigDecimal pgTdr = new BigDecimal(tdr[2]);
		BigDecimal pgGst = new BigDecimal(tdr[3]);

		if (tdr[4].equalsIgnoreCase("N")) {
			fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
		}

		fields.put(FieldType.ACQUIRER_TDR_SC.getName(),  acquirerTdr.toString());
		fields.put(FieldType.ACQUIRER_GST.getName(), acquirerGst.toString());

		fields.put(FieldType.PG_TDR_SC.getName(), pgTdr.toString());
		fields.put(FieldType.PG_GST.getName(),  pgGst.toString());

		fields.put(FieldType.SURCHARGE_FLAG.getName(), tdr[4]);
	}
	
	

	// To calculate txn charges breakup
//	public void addTransactionDataFields(Fields fields) throws SystemException {
//
//		logger.info("fields ---->> " + fields.getFieldsAsString());
//
//		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName()))
//			dao.calculateTdrAndSurcharge(fields);
//		
//	
//
//	}
//
//	public void frm(Fields fields) throws SystemException {
//		logger.info("fields ---->> " + fields.getFieldsAsString());
//
//		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName()))
//			dao.frm(fields);
//	} code by chaten
}
