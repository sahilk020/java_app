package com.pay10.pg.core.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;

@Service
public class AcquirerTxnAmountProvider {

	public String amountProvider(Fields fields) throws SystemException {

		AcquirerType acquirer = AcquirerType.getInstancefromCode(fields.get(FieldType.ACQUIRER_TYPE.getName()));
		String addSurchargeFlag = null;
		String amount = null;
		String refundFlag = null;
		String txnType = null;
		if (StringUtils.isNotBlank(fields.get(FieldType.SURCHARGE_FLAG.getName()))
				&& (fields.get(FieldType.SURCHARGE_FLAG.getName()).equals(Constants.Y_FLAG.getValue()))) {
			switch (acquirer) {
			case FSS:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.FSS_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.FSS_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case FEDERAL:
				addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.FEDERAL_ADD_SURCHARGE.getValue());
				amount = amountCalculator(fields, addSurchargeFlag);
				String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
				if (paymentType.equals(PaymentType.UPI.getCode())) {
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case ICICI_FIRSTDATA:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.FIRSTDATA_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.FIRSTDATA_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case ICICIBANK:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.ICICI_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.ICICI_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case FREECHARGE:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.FREECHARGE_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.FREECHARGE_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}

				break;
			case IDFC_FIRSTDATA:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.FIRSTDATA_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.FIRSTDATA_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case CANARANBBANK:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.CANARANBBANK_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.CANARANBBANK_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case IDFC:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.IDFC_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.IDFC_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case CITYUNIONBANK:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.CITYUNIONBANK_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.CITYUNIONBANK_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;

				case SHIVALIKNBBANK:
					txnType = fields.get(FieldType.TXNTYPE.getName());
					if (txnType.equals(TransactionType.REFUND.getName())) {
						refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
						if (refundFlag.equalsIgnoreCase("R")) {
							refundAmountComparator(fields);
							addSurchargeFlag = PropertiesManager.propertiesMap
									.get(Constants.SHIVALIKNMBANK_ADD_SURCHARGE.getValue());
							amount = amountCalculator(fields, addSurchargeFlag);
							amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
						} else {
							amount = fields.get(FieldType.AMOUNT.getName());
							fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
							amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
						}
					} else {
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.SHIVALIKNMBANK_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
					break;

				case TFP:
					txnType = fields.get(FieldType.TXNTYPE.getName());
					if (txnType.equals(TransactionType.REFUND.getName())) {
						refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
						if (refundFlag.equalsIgnoreCase("R")) {
							refundAmountComparator(fields);
							addSurchargeFlag = PropertiesManager.propertiesMap
									.get(Constants.TFP_ADD_SURCHARGE.getValue());
							amount = amountCalculator(fields, addSurchargeFlag);
							amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
						} else {
							amount = fields.get(FieldType.AMOUNT.getName());
							fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
							amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
						}
					} else {
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.TFP_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
					break;
			case JAMMU_AND_KASHMIR:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.JAMMUANDKISHMIR_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.JAMMUANDKISHMIR_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case IDFCUPI:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.IDFCUPI_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.IDFCUPI_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case AXISMIGS:
				addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.AXIS_MIGS_ADD_SURCHARGE.getValue());
				amount = amountCalculator(fields, addSurchargeFlag);
				break;
			case BOB:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.BOB_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.BOB_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case ATL:
				addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.ATL_ADD_SURCHARGE.getValue());
				amount = amountCalculator(fields, addSurchargeFlag);
				amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case KOTAK:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.KOTAK_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));

					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.KOTAK_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));

				}
				break;
			case COSMOS:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.COSMOS_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));


					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.COSMOS_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));


				}
				break;
			case ICICI_MPGS:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.ICICI_MPGS_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.ICICI_MPGS_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case IDBIBANK:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.IDBIBANK_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.IDBIBANK_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
				}
				break;

			case DIRECPAY:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.DIRECPAY_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.DIRECPAY_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;

			case ISGPAY:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.ISGPAY_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.ISGPAY_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
				}
				break;

			case MATCHMOVE:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.MATCHMOVE_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.MATCHMOVE_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case YESBANKCB:
				String paymentTyp = fields.get(FieldType.PAYMENT_TYPE.getName());
				if (paymentTyp.equals(PaymentType.UPI.getCode())) {
					txnType = fields.get(FieldType.TXNTYPE.getName());
					if (txnType.equals(TransactionType.REFUND.getName())) {
						refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
						if (refundFlag.equalsIgnoreCase("R")) {
							refundAmountComparator(fields);
							addSurchargeFlag = PropertiesManager.propertiesMap
									.get(Constants.YESBANKCB_UPI_ADD_SURCHARGE.getValue());
							amount = amountCalculator(fields, addSurchargeFlag);
							amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
						} else {
							amount = fields.get(FieldType.AMOUNT.getName());
							fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
							amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
						}
					} else {
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.YESBANKCB_UPI_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					txnType = fields.get(FieldType.TXNTYPE.getName());
					if (txnType.equals(TransactionType.REFUND.getName())) {
						refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
						if (refundFlag.equalsIgnoreCase("R")) {
							refundAmountComparator(fields);
							addSurchargeFlag = PropertiesManager.propertiesMap
									.get(Constants.YESBANKCB_ADD_SURCHARGE.getValue());
							amount = amountCalculator(fields, addSurchargeFlag);
							amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
						} else {
							amount = fields.get(FieldType.AMOUNT.getName());
							fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
							amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
						}
					} else {
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.YESBANKCB_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				}
				break;
			case HDFC:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.HDFC_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.FSS_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;

			case BILLDESK:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.BILLDESK_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.BILLDESK_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case SBI:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.SBI_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.SBI_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case SBICARD:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.SBI_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.SBI_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case SBINB:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.SBI_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.SBI_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case LYRA:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.LYRA_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.LYRA_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case EASEBUZZ:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.EASEBUZZ_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.EASEBUZZ_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;

			case CASHFREE:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.CASHFREE_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.CASHFREE_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case AGREEPAY:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.AGREEPAY_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.AGREEPAY_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case NB_FEDERAL:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.FEDERALNB_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.FEDERALNB_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case PINELABS:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.PINELABS_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.PINELABS_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;

			case YESBANKNB:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.YESBANKNB_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap
							.get(Constants.YESBANKNB_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;
			case CAMSPAY:
				txnType = fields.get(FieldType.TXNTYPE.getName());
				if (txnType.equals(TransactionType.REFUND.getName())) {
					refundFlag = fields.get(FieldType.REFUND_FLAG.getName());
					if (refundFlag.equalsIgnoreCase("R")) {
						refundAmountComparator(fields);
						addSurchargeFlag = PropertiesManager.propertiesMap
								.get(Constants.CAMSPAY_ADD_SURCHARGE.getValue());
						amount = amountCalculator(fields, addSurchargeFlag);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					} else {
						amount = fields.get(FieldType.AMOUNT.getName());
						fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
						amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
					}
				} else {
					addSurchargeFlag = PropertiesManager.propertiesMap.get(Constants.CAMSPAY_ADD_SURCHARGE.getValue());
					amount = amountCalculator(fields, addSurchargeFlag);
					amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
				}
				break;

			default:
				break;
			}

		} else {
			switch (acquirer) {
			case FSS:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case FEDERAL:
				if ((fields.get(FieldType.PAYMENT_TYPE.getName())).equals(PaymentType.UPI.getCode())) {
					amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
							fields.get(FieldType.CURRENCY_CODE.getName()));
				} else {
					amount = fields.get(FieldType.AMOUNT.getName());
				}
				break;
			case ICICI_FIRSTDATA:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case EASEBUZZ:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case IDFC_FIRSTDATA:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case IDFCUPI:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case AXISMIGS:
				amount = fields.get(FieldType.AMOUNT.getName());
				break;
			case FREECHARGE:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case ICICIBANK:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case BOB:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case ATL:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case KOTAK:
				// amount = fields.get(FieldType.AMOUNT.getName());
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case COSMOS:
				// amount = fields.get(FieldType.AMOUNT.getName());
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case ICICI_MPGS:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case IDBIBANK:
				amount = fields.get(FieldType.AMOUNT.getName());
				break;
			case DIRECPAY:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case ISGPAY:
				amount = fields.get(FieldType.AMOUNT.getName());
				break;
			case MATCHMOVE:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case YESBANKCB:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case HDFC:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case SBI:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case IDFC:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case JAMMU_AND_KASHMIR:

				// amount = fields.get(FieldType.AMOUNT.getName());
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
				case SHIVALIKNBBANK:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
				case CITYUNIONBANK:

					// amount = fields.get(FieldType.AMOUNT.getName());
					amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
							fields.get(FieldType.CURRENCY_CODE.getName()));
					break;
			case SBICARD:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case SBINB:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case BILLDESK:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case LYRA:
				amount = fields.get(FieldType.AMOUNT.getName());
				break;
			case CASHFREE:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case AGREEPAY:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case NB_FEDERAL:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case CANARANBBANK:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			case PINELABS:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;

			case YESBANKNB:
				amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));
				break;
			default:
				break;
			}

		}
		return amount;

	}

	public String amountCalculator(Fields fields, String addSurchargeFlag) {
		String amount = null;
		if (addSurchargeFlag.equals("Y")) {
			amount = fields.get(FieldType.TOTAL_AMOUNT.getName());
		} else {
			amount = fields.get(FieldType.AMOUNT.getName());
		}
		return amount;
	}

	public void refundAmountComparator(Fields fields) throws SystemException {
		String amount = fields.get(FieldType.AMOUNT.getName());
		String saleAmount = fields.get(FieldType.SALE_AMOUNT.getName());
		if (!saleAmount.equals(amount)) {
			throw new SystemException(ErrorType.REFUND_FLAG_AMOUNT_NOT_MATCH, "Error processing refund");
		}

	}

	public static void main(String[] args) {
		System.out.println(Amount.toDecimal("100", "356"));
	}
}
