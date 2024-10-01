package com.pay10.scheduler.jobs;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.util.TransactionManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pay10.commons.mongo.WalletHistoryRepository;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.scheduler.commons.ConfigurationProvider;
import com.pay10.scheduler.commons.RnsSettlementProvider;

@Service
public class RnsSettlmentScheduler {
	private static final Logger logger = LoggerFactory.getLogger(RnsSettlmentScheduler.class);

	@Autowired
	private ConfigurationProvider configurationProvider;

	@Autowired
	RnsSettlementProvider rnsSettlementProvider;

	@Autowired
	private WalletHistoryRepository walletHistoryRepository;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	@Autowired
	private MerchantWalletPODao merchantWalletPODao;

	@Scheduled(cron = "0 */2 * * * *")
	private void process_captured_to_rns() {
		try {

			logger.info("start process captured to rns status");
			List<Document> refundData = rnsSettlementProvider.fetchCapturedTransactionData();

			for (Document doc : refundData) {
				logger.info("mark rns from captured" + doc.toString());
				boolean insertFlag = rnsSettlementProvider.makeRnsFromCaptured(doc);
				logger.info("mark rns entry from captured, response=" + insertFlag);
				boolean updateFlag = false;
				if (insertFlag) {
					updateFlag = rnsSettlementProvider.updateRnsFromCaptured(doc);
					logger.info("update captured entry to RNS, response=" + updateFlag);
				}
				
				String aggregatorCommissionAMT = null;
				String acquirerCommissionAMT = null;
				Double amtPayableToMerch = 0.00;
				Double amtAddInAcquirerWallet = 0.00;
				amtPayableToMerch = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				amtAddInAcquirerWallet = amtPayableToMerch;
				if (insertFlag && updateFlag) {
					logger.info(
							"process_captured_to_rns, request receive for update merchant amount, PG_REF_NUM ={}, AMOUNT={}, TOTAL_AMOUNT={} ",
							doc.get(FieldType.PG_REF_NUM.getName()), doc.get(FieldType.AMOUNT.getName()),
							doc.get(FieldType.TOTAL_AMOUNT.getName()));
					if (doc.getString(FieldType.PG_TDR_SC.toString()) != null
							&& doc.getString(FieldType.PG_GST.toString()) != null) {
						aggregatorCommissionAMT = String
								.valueOf(Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))
										+ Double.valueOf(doc.getString(FieldType.PG_GST.toString())));
					}
					if (doc.getString(FieldType.ACQUIRER_TDR_SC.toString()) != null
							&& doc.getString(FieldType.ACQUIRER_GST.toString()) != null) {
						acquirerCommissionAMT = String
								.valueOf(Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
										+ Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString())));
					}
					if (aggregatorCommissionAMT != null) {
						amtPayableToMerch -= Double.valueOf(aggregatorCommissionAMT);
					}
					if (acquirerCommissionAMT != null) {
						amtPayableToMerch -= Double.valueOf(acquirerCommissionAMT);
					}
					
					if(acquirerCommissionAMT != null) {
						amtAddInAcquirerWallet -= Double.valueOf(acquirerCommissionAMT);
					}
					
					amtPayableToMerch = Double.valueOf(amountFormatting(String.valueOf(amtPayableToMerch)));
					amtAddInAcquirerWallet = Double.valueOf(amountFormatting(String.valueOf(amtAddInAcquirerWallet)));
					logger.info("process_captured_to_rns, PG_REF_NUM ={}, aggregatorCommissionAMT={}, acquirerCommissionAMT={}, amtPayableToMerch={}, amtAddInAcquirerWallet={}",
							doc.get(FieldType.PG_REF_NUM.getName()), aggregatorCommissionAMT, acquirerCommissionAMT,
							amtPayableToMerch, amtAddInAcquirerWallet);
					
					String currency = multCurrencyCodeDao.getCurrencyNamebyCode(String.valueOf(doc.get(FieldType.CURRENCY_CODE.getName())));
					Map<String, Object> merchantWalletResp = walletHistoryRepository.findMerchantFundByPayId(String.valueOf(doc.get(FieldType.PAY_ID.getName())), currency);
					if (merchantWalletResp.size() == 0) {
						// add new entry
						walletHistoryRepository.createMerchantWalletByPayIdAndCurrencyNameWithValues(String.valueOf(doc.get(FieldType.PAY_ID.getName())), currency, String.valueOf(amtPayableToMerch), "0.00", "0.00", String.valueOf(amtPayableToMerch));
					} else {
						// update entry
						logger.info("process_captured_to_rns, merchantWalletResp ................." + merchantWalletResp.toString());
						String amount = String.valueOf(merchantWalletResp.get("totalBalance"));
						String merchantAMT = String.valueOf(amtPayableToMerch);
						amtPayableToMerch += Double.valueOf(amountFormatting(amount));
						logger.info("process_captured_to_rns,,amtPayableToMerch .................amtPayableToMerch={}, amount={}",amountFormatting(String.valueOf(amtPayableToMerch)), amount);
						walletHistoryRepository.updateMerchantWallet(String.valueOf(doc.get(FieldType.PAY_ID.getName())), currency, amountFormatting(String.valueOf(amtPayableToMerch)), "credit", merchantAMT, true);
					}

					Map<String, Object> acquirerWalletResp = walletHistoryRepository.findAcquirerWalletByAcquirerName(String.valueOf(doc.get(FieldType.ACQUIRER_TYPE.getName())));
					if(merchantWalletResp.size() > 0) {
						logger.info("process_captured_to_rns, merchantWalletResp ................." + acquirerWalletResp.toString());
						String amount = String.valueOf(acquirerWalletResp.get("finalBalance"));
						amtAddInAcquirerWallet += Double.valueOf(amountFormatting(amount));
						logger.info("process_captured_to_rns, merchantWalletResp .................amtAddInAcquirerWallet={}, amount={}",amountFormatting(String.valueOf(amtAddInAcquirerWallet)), amount);
						walletHistoryRepository.updateAcquirerWallet(String.valueOf(doc.get(FieldType.ACQUIRER_TYPE.getName())), amountFormatting(String.valueOf(amtAddInAcquirerWallet)));
					
					} 
				} else {
					logger.info("merchant amount not updated for wallet, PG_REF_NUM ={}, insertFlag={}, updateFlag={} ",
							doc.get(FieldType.PG_REF_NUM.getName()), insertFlag, updateFlag);
				}
			}

			logger.info("Completed process captured to rns status");
		} catch (Exception e) {
			logger.error("Exception in process_captured_to_rns from scheduler", e);
		}

	}

	@Scheduled(cron = "0 */5 * * * *")
	private void process_rns_to_settled() {
		try {

			logger.info("start process rns to settled status");
			List<Document> rnsRequestDoc = rnsSettlementProvider.fetchRNSTransactionData();

			for (Document doc : rnsRequestDoc) {
				logger.info("mark settled from rns=" + doc.toString());
				String rrn = rnsSettlementProvider.makeSettedFromRns(doc);
				logger.info("mark settled entry from rns, response=" + rrn);
				boolean updateFlag = false;
				if (rrn!=null) {
					updateFlag = rnsSettlementProvider.updateSettedFromRns(doc,rrn);
					logger.info("update rns entry to settled, response=" + updateFlag);
				}

				String aggregatorCommissionAMT = null;
				String acquirerCommissionAMT = null;
				Double amtPayableToMerch = 0.00;
				amtPayableToMerch = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				if (rrn!=null && updateFlag) {
					logger.info(
							"process_rns_to_settled, request receive for update merchant amount, PG_REF_NUM ={}, AMOUNT={}, TOTAL_AMOUNT={} ",
							doc.get(FieldType.PG_REF_NUM.getName()), doc.get(FieldType.AMOUNT.getName()),
							doc.get(FieldType.TOTAL_AMOUNT.getName()));
					if (doc.getString(FieldType.PG_TDR_SC.toString()) != null
							&& doc.getString(FieldType.PG_GST.toString()) != null) {
						aggregatorCommissionAMT = String
								.valueOf(Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))
										+ Double.valueOf(doc.getString(FieldType.PG_GST.toString())));
					}
					if (doc.getString(FieldType.ACQUIRER_TDR_SC.toString()) != null
							&& doc.getString(FieldType.ACQUIRER_GST.toString()) != null) {
						acquirerCommissionAMT = String
								.valueOf(Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
										+ Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString())));
					}
					if (aggregatorCommissionAMT != null) {
						amtPayableToMerch -= Double.valueOf(aggregatorCommissionAMT);
					}
					if (acquirerCommissionAMT != null) {
						amtPayableToMerch -= Double.valueOf(acquirerCommissionAMT);
					}
					
					amtPayableToMerch = Double.valueOf(amountFormatting(String.valueOf(amtPayableToMerch)));
					logger.info("process_rns_to_settled, PG_REF_NUM ={}, aggregatorCommissionAMT={}, acquirerCommissionAMT={}, amtPayableToMerch={}",
							doc.get(FieldType.PG_REF_NUM.getName()), aggregatorCommissionAMT, acquirerCommissionAMT,
							amtPayableToMerch);
					
					String currency = multCurrencyCodeDao.getCurrencyNamebyCode(String.valueOf(doc.get(FieldType.CURRENCY_CODE.getName())));
					Map<String, Object> merchantWalletResp = walletHistoryRepository.findMerchantFundByPayId(String.valueOf(doc.get(FieldType.PAY_ID.getName())), currency);

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					PassbookPODTO passbookPODTO = new PassbookPODTO();
					passbookPODTO.setPayId(String.valueOf(doc.get(FieldType.PAY_ID.getName())));
					passbookPODTO.setType("CREDIT");
					passbookPODTO.setNarration("PAYIN");
					passbookPODTO.setCreateDate(sdf.format(new Date()));
					passbookPODTO.setTxnId(TransactionManager.getNewTransactionId());
					passbookPODTO.setAmount(String.valueOf(amtPayableToMerch));
					passbookPODTO.setCurrency(currency);
					passbookPODTO.setRespTxn(String.valueOf(doc.get(FieldType.ORDER_ID.getName())));
					logger.info("Save the Transaction History in passbook {}", passbookPODTO);
					merchantWalletPODao.savePassbookDetailsByPassbook(passbookPODTO);


					if (merchantWalletResp.size() == 0) {
						// add new entry
						walletHistoryRepository.createMerchantWalletByPayIdAndCurrencyNameWithValues(String.valueOf(doc.get(FieldType.PAY_ID.getName())), currency, String.valueOf(amtPayableToMerch), "0.00", String.valueOf(amtPayableToMerch), "0.00");
					} else {
						// update entry
						logger.info("process_rns_to_settled, merchantWalletResp ................." + merchantWalletResp.toString());
						String amount = String.valueOf(merchantWalletResp.get("finalBalance"));
						String merchantAMT = String.valueOf(amtPayableToMerch);
						amtPayableToMerch += Double.valueOf(amountFormatting(amount));
						logger.info("process_rns_to_settled, amtPayableToMerch .................amtPayableToMerch={}, amount={}",amountFormatting(String.valueOf(amtPayableToMerch)), amount);
						walletHistoryRepository.updateMerchantWallet(String.valueOf(doc.get(FieldType.PAY_ID.getName())), currency, amountFormatting(String.valueOf(amtPayableToMerch)), "credit", merchantAMT,false);
					}



				} else {
					logger.info("process_rns_to_settled, merchant amount not updated for wallet, PG_REF_NUM ={}, rrn={}, updateFlag={} ",
							doc.get(FieldType.PG_REF_NUM.getName()), rrn, updateFlag);
				}

			}
			logger.info("Completed process rns to settled status");

		} catch (Exception e) {
			logger.error("Exception in process_rns_to_settled from scheduler", e);
		}

	}
	
	public static String amountFormatting(String amount) {

		if (amount.contains(".")) {
			String rupess = amount.split("\\.")[0];
			String decimal = amount.split("\\.")[1];

			if (decimal.length() > 2) {
				return (rupess + "." + (decimal.substring(0,2)));
			} else {
				return amount;
			}
		} else {
			return amount;
		}
	}
	
	public static void main(String[] args) {
		Double amtPayableToMerch = 10.1200000000009;
		System.out.println("----"+amountFormatting("10"));
		System.out.println("++++"+Double.valueOf(amountFormatting(String.valueOf(amtPayableToMerch))));
	}
}
