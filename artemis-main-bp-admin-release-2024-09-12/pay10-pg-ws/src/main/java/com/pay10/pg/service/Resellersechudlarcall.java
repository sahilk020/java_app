package com.pay10.pg.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.Resellerdailyupdatedao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Resellercommision;
import com.pay10.commons.user.Resellerdailyupdate;
import com.pay10.commons.user.UserCommision;
import com.pay10.commons.user.resellercommisiondao;
import com.pay10.commons.user.UserCommissionDao;

@Service
public class Resellersechudlarcall {
	private static Logger logger = LoggerFactory.getLogger(Resellersechudlarcall.class.getName());

	@Autowired
	resellercommisiondao Resellercommisiondao;

	@Autowired
	Resellerdailyupdatedao resellerdailyupdatedao;

	private String date;

	@Autowired
	UserCommissionDao UserCommissionDao;
	
	public String process() throws SystemException {

		//List<Resellercommision> resellercomisisonlist = Resellercommisiondao.findAll();
		List<UserCommision> resellercomisisonlist = UserCommissionDao.findAll();

//		Map<String, List<Resellercommision>> commissionByMerchant = resellercomisisonlist.stream()
//				.collect(Collectors.groupingBy(Resellercommision::getMerchantpayid));
		
		Map<String, List<UserCommision>> commissionByMerchant = resellercomisisonlist.stream()
				.collect(Collectors.groupingBy(UserCommision::getMerchantPayId));

		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
// 		Need to change after deployment
//		cal.add(Calendar.DATE, -1);
		cal.add(Calendar.DATE, 0);
		date = dateFormat.format(cal.getTime());
		String startDate = StringUtils.join(dateFormat.format(cal.getTime()), " 00:00:00");
		String endDate = StringUtils.join(dateFormat.format(cal.getTime()), " 23:59:59");
		commissionByMerchant.entrySet().forEach(entry -> {
			List<Document> docs = resellerdailyupdatedao.getByPayId(entry.getKey(), startDate, endDate);
			Map<String, Map<String, List<Document>>> docByMop = docs.stream().collect(Collectors.groupingBy(
					doc -> doc.getString("PAYMENT_TYPE"), Collectors.groupingBy(doc -> doc.getString("MOP_TYPE"))));
			docByMop.entrySet().forEach(docsEntry -> {
				docsEntry.getValue().entrySet().forEach(docByPaymentType -> {
					String paymentType = docsEntry.getKey();
					String mopType = docByPaymentType.getKey();
					entry.getValue().forEach(commission -> {
						if (StringUtils.equalsIgnoreCase(commission.getMop(), mopType)
								&& StringUtils.equalsIgnoreCase(commission.getTransactiontype(), paymentType)) {
//							logger.info(
//									"process:: reseller commission calculation start for payId={}, mopType={}, paymentType={}",
//									commission.getMerchantpayid(), commission.getTransactiontype(),
//									commission.getMop());
							logger.info(
									"process:: reseller commission calculation start for payId={}, mopType={}, paymentType={},currency={}",
									commission.getMerchantPayId(), commission.getTransactiontype(),
									commission.getMop(),commission.getCurrency());
							Map<String, Object> commissionDetails = getCommissionAmount(docByPaymentType.getValue(),
									commission);
							createEntry(commissionDetails, commission);
						}
					});
				});
			});
		});
		return null;
	}

	private Map<String, Object> getCommissionAmount(List<Document> docs, UserCommision resellerCommision) {
		Map<String, List<Document>> docByTxnType = docs.stream()
				.collect(Collectors.groupingBy(doc -> doc.getString("TXNTYPE")));
		Map<String, Object> commissionDetail = new HashMap<>();
		double combineSMAFinalCommissionAmountSale = 0;
		double combineSMAFinalCommissionAmountRefund = 0;
		double combineMAFinalCommissionAmountSale = 0;
		double combineMAFinalCommissionAmountRefund = 0;
		double combineAgentFinalCommissionAmountSale = 0;
		double combineAgentFinalCommissionAmountRefund = 0;
		for (Document doc : docs) {
			
			double txnAmount = Double.valueOf(doc.getString("AMOUNT"));
			//for sma calculation
			double smacommissionAmount = StringUtils.isNotBlank(resellerCommision.getSma_commission_amount())
					? Double.valueOf(resellerCommision.getSma_commission_amount())
					: 0;
			double smacommissionAmtForPercentage = getCommissionAmountForPercentage(
					resellerCommision.getSma_commission_percent(), txnAmount);
			
			double smafinalCommission = getCommissionAmountBaseonConfig(resellerCommision.isCommissiontype(),
					smacommissionAmount, smacommissionAmtForPercentage);
			
			//for ma calculation
			double macommissionAmount = StringUtils.isNotBlank(resellerCommision.getMa_commission_amount())
					? Double.valueOf(resellerCommision.getMa_commission_amount())
					: 0;
			double macommissionAmtForPercentage = getCommissionAmountForPercentage(
					resellerCommision.getMa_commission_percent(), txnAmount);
			
			double mafinalCommission = getCommissionAmountBaseonConfig(resellerCommision.isCommissiontype(),
					macommissionAmount, macommissionAmtForPercentage);
			
			//for agent calculation
			
			double agentcommissionAmount = StringUtils.isNotBlank(resellerCommision.getAgent_commission_amount())
					? Double.valueOf(resellerCommision.getAgent_commission_amount())
					: 0;
					
			double agentcommissionAmtForPercentage = getCommissionAmountForPercentage(
					resellerCommision.getAgent_commission_percent(), txnAmount);
			
			double agentfinalCommission = getCommissionAmountBaseonConfig(resellerCommision.isCommissiontype(),
					agentcommissionAmount, agentcommissionAmtForPercentage);
			
			if (StringUtils.equalsIgnoreCase(doc.getString("TXNTYPE"), "SALE")) {
				combineSMAFinalCommissionAmountSale = combineSMAFinalCommissionAmountSale + smafinalCommission;
				combineMAFinalCommissionAmountSale= combineMAFinalCommissionAmountSale + mafinalCommission;
				combineAgentFinalCommissionAmountSale= combineAgentFinalCommissionAmountSale + agentfinalCommission;
				continue;
			}
			combineSMAFinalCommissionAmountRefund = combineSMAFinalCommissionAmountRefund + combineSMAFinalCommissionAmountSale;
			combineMAFinalCommissionAmountRefund = combineMAFinalCommissionAmountRefund + combineMAFinalCommissionAmountSale;
			combineAgentFinalCommissionAmountRefund = combineAgentFinalCommissionAmountRefund + combineAgentFinalCommissionAmountSale;

		}
		commissionDetail.put("smaCommissionAmount", combineSMAFinalCommissionAmountSale - combineSMAFinalCommissionAmountRefund);
		commissionDetail.put("maCommissionAmount", combineMAFinalCommissionAmountSale - combineMAFinalCommissionAmountRefund);
		commissionDetail.put("agentCommissionAmount", combineAgentFinalCommissionAmountSale - combineAgentFinalCommissionAmountRefund);
		
		double totalSaleAmount = 0;
		double totalRefundAmount = 0;
		for (Map.Entry<String, List<Document>> entry : docByTxnType.entrySet()) {
			if (StringUtils.equalsIgnoreCase(entry.getKey(), "SALE")) {
				totalSaleAmount = entry.getValue().stream()
						.collect(Collectors.summingDouble(doc -> Double.valueOf(doc.getString("AMOUNT"))));
			}
			if (StringUtils.equalsIgnoreCase(entry.getKey(), "REFUND")) {
				totalRefundAmount = entry.getValue().stream()
						.collect(Collectors.summingDouble(doc -> Double.valueOf(doc.getString("AMOUNT"))));
			}
		}
		commissionDetail.put("saleAmount", totalSaleAmount - totalRefundAmount);
		commissionDetail.put("totalSaleAmount", totalSaleAmount);
		commissionDetail.put("totalRefundAmount", totalRefundAmount);
		logger.info("getUserCommissionAmount:: payId={}, totalSmaCommissionAmount={},totalMaCommissionAmount={},totalAgentCommissionAmount={} paymentType={}, mopType={}",
				resellerCommision.getMerchantPayId(), commissionDetail.get("smaCommissionAmount"),commissionDetail.get("maCommissionAmount"),commissionDetail.get("agentCommissionAmount"),
				resellerCommision.getTransactiontype(), resellerCommision.getMop());
		return commissionDetail;
	}

	private double getCommissionAmountForPercentage(String commissionPercentage, double txnAmount) {
		double commissionAmtForPercentage = 0;
		if (StringUtils.isNotBlank(commissionPercentage)) {
			commissionAmtForPercentage = txnAmount * Double.valueOf(commissionPercentage) / 100;
		}
		return commissionAmtForPercentage;
	}

	private double getCommissionAmountBaseonConfig(boolean isHigher, double amount, double percentageAmount) {
		if (isHigher) {
			return percentageAmount > amount ? percentageAmount : amount;
		}
		return percentageAmount < amount ? percentageAmount : amount;
	}

	private void createEntry(Map<String, Object> commissionDetails, UserCommision commission) {
		if ((double) commissionDetails.getOrDefault("smaCommissionAmount", 0) <= 0 || (double) commissionDetails.getOrDefault("maCommissionAmount", 0) <= 0
				|| (double) commissionDetails.getOrDefault("agentCommissionAmount", 0) <= 0 ) {
			return;
		}
		Resellerdailyupdate resellerdailyupdate = new Resellerdailyupdate();
		resellerdailyupdate.setMerchant_payId(commission.getMerchantPayId());
		resellerdailyupdate.setTransDate(date);
		resellerdailyupdate.setMOP(commission.getMop());
		resellerdailyupdate.setTransType(commission.getTransactiontype());
		resellerdailyupdate.setId(getId());
		resellerdailyupdate.setAddedOn(LocalDateTime.now().toString("yyyy-MM-dd"));
		resellerdailyupdate.setSaleamount((double) commissionDetails.getOrDefault("totalSaleAmount", 0));
		//resellerdailyupdate.setReseller_payId(commission.getResellerpayid());
		resellerdailyupdate.setSMA_payId(commission.getSmaPayId());
		resellerdailyupdate.setMA_payId(commission.getMaPayId());
		resellerdailyupdate.setAgent_payId(commission.getAgentPayId());
		//resellerdailyupdate.setCommisionamount((double) commissionDetails.getOrDefault("commissionAmount", 0));
		resellerdailyupdate.setSmacommisionamount((double) commissionDetails.getOrDefault("smaCommissionAmount", 0));
		resellerdailyupdate.setMacommisionamount((double) commissionDetails.getOrDefault("maCommissionAmount", 0));
		resellerdailyupdate.setAgentcommisionamount((double) commissionDetails.getOrDefault("agentCommissionAmount", 0));
		resellerdailyupdate.setAmount((double) commissionDetails.getOrDefault("saleAmount", 0));
		resellerdailyupdate.setTotalRefund((double) commissionDetails.getOrDefault("totalRefundAmount", 0));
		resellerdailyupdate.setTotalChargeback(0d);
		resellerdailyupdate.setCurrency(commission.getCurrency());
		resellerdailyupdatedao.create(resellerdailyupdate);
	}

	private static long getId() {
		return System.currentTimeMillis();
	}
}