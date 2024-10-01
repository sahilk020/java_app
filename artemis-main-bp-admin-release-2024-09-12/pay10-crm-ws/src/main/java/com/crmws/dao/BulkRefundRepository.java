package com.crmws.dao;

import com.crmws.dto.BulkRefundCountDto;
import com.crmws.entity.BulkRefundEntity;
import com.crmws.entity.IrctcRefundEntity;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class BulkRefundRepository extends HibernateAbstractDao {
	private static final Logger logger = LoggerFactory.getLogger(BulkRefundRepository.class.getName());

	@Autowired
	MongoTemplate mongoTemplate;

	public void saveBulkRefund(BulkRefundEntity bulkRefundEntity) {
		mongoTemplate.insert(bulkRefundEntity);

	}
	
	
	public boolean findBulkRefundCheck(BulkRefundEntity bulkRefundEntity) {

		Query qry = null;

		Criteria c2 = Criteria.where("status").nin("REFUND_INITIATED", "Pending");
		Criteria payId = Criteria.where("payId").is(bulkRefundEntity.getPayId());
		Criteria Pgref = Criteria.where("pgRefNO").is(bulkRefundEntity.getPgRefNO());
		Criteria query = new Criteria().andOperator(payId, Pgref);

		logger.info("Inside Bulkrefund (bulkRefundEntity): " + bulkRefundEntity.toString());
		logger.info("query for bulk search" + c2);
		qry = Query.query(query);
		logger.info("query for bulk search" + qry);

		List<BulkRefundEntity> result = mongoTemplate.find(qry, BulkRefundEntity.class);
		logger.info("query for bulk refund size" + result.size());

		if (result.size() > 0) {

			AtomicDouble bulkPendAmt = new AtomicDouble(0);
			result.stream().forEach(data -> {
				bulkPendAmt.addAndGet(Double.valueOf(data.getAmount()));

			});

			Criteria c3 = Criteria.where("ORDER_ID").is(bulkRefundEntity.getOrderId());
			// qry = Query.query(c2);

			MatchOperation match5 = match(c3);

			Aggregation aggregation1 = Aggregation.newAggregation(match5);

			List<Map> resulttxn = mongoTemplate.aggregate(aggregation1, "transaction", Map.class).getMappedResults();

			logger.info("query for bulk refund size" + resulttxn.size());
			AtomicBoolean saleCaptured = new AtomicBoolean(true);
			AtomicDouble saleAmount = new AtomicDouble(0);
			AtomicDouble refundAmount = new AtomicDouble(0);

			logger.info("111111111111111111" + new Gson().toJson(resulttxn));
			resulttxn.stream().forEach(map -> {
				if (saleCaptured.get()
						&& map.get(FieldType.TXNTYPE.getName()).toString()
								.equalsIgnoreCase(TransactionType.SALE.getName())
						&& map.get(FieldType.STATUS.getName()).toString()
								.equalsIgnoreCase(StatusType.CAPTURED.getName())) {
					saleAmount.addAndGet(Double.valueOf(map.get(FieldType.TOTAL_AMOUNT.getName()).toString()));
					saleCaptured.set(false);

				}

				if (saleCaptured.get()
						&& map.get(FieldType.TXNTYPE.getName()).toString()
								.equalsIgnoreCase(TransactionType.RECO.getName())
						&& map.get(FieldType.STATUS.getName()).toString()
								.equalsIgnoreCase(StatusType.CAPTURED.getName())) {
					saleAmount.addAndGet(Double.valueOf(map.get(FieldType.TOTAL_AMOUNT.getName()).toString()));
					saleCaptured.set(false);

				}

				if (saleCaptured.get()
						&& map.get(FieldType.TXNTYPE.getName()).toString()
								.equalsIgnoreCase(TransactionType.RECO.getName())
						&& map.get(FieldType.STATUS.getName()).toString()
								.equalsIgnoreCase(StatusType.FORCE_CAPTURED.getName())) {
					saleAmount.addAndGet(Double.valueOf(map.get(FieldType.TOTAL_AMOUNT.getName()).toString()));
					saleCaptured.set(false);

				}

				if (saleCaptured.get()
						&& map.get(FieldType.TXNTYPE.getName()).toString()
								.equalsIgnoreCase(TransactionType.RECO.getName())
						&& map.get(FieldType.STATUS.getName()).toString()
								.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())) {
					saleAmount.addAndGet(Double.valueOf(map.get(FieldType.TOTAL_AMOUNT.getName()).toString()));
					saleCaptured.set(false);

				}

				if (map.get(FieldType.TXNTYPE.getName()).toString().equalsIgnoreCase(TransactionType.REFUND.getName())
						&& map.get(FieldType.STATUS.getName()).toString()
								.equalsIgnoreCase(StatusType.CAPTURED.getName())) {
					refundAmount.addAndGet(Double.valueOf("" + map.get(FieldType.AMOUNT.getName())));
				}

			});

			// refundAmount.addAndGet(bulkPendAmt.get());
			refundAmount
					.addAndGet(Double.valueOf(Amount.toDecimal(bulkRefundEntity.getAmount().replace(".0", ""), "356")));
			logger.info("refund amount and sale amount" + refundAmount + "   " + saleAmount);
			if ((refundAmount.get() > saleAmount.get())) {
				return false;
			} else {
				return true;
			}

		}

		return true;

	}

	public boolean findBulkRefundCheckStatus(BulkRefundEntity bulkRefundEntity) {

		Criteria c3 = Criteria.where("ORDER_ID").is(bulkRefundEntity.getOrderId());
		// qry = Query.query(c2);

		MatchOperation match5 = match(c3);

		Aggregation aggregation1 = Aggregation.newAggregation(match5);

		List<Map> resulttxn = mongoTemplate.aggregate(aggregation1, "transactionStatus", Map.class).getMappedResults();

		logger.info("query for bulk refund size" + resulttxn.size());
		AtomicBoolean saleCaptured = new AtomicBoolean(true);
		AtomicDouble saleAmount = new AtomicDouble(0);
		AtomicDouble refundAmount = new AtomicDouble(0);

		logger.info("111111111111111111" + new Gson().toJson(resulttxn));
		resulttxn.stream().forEach(map -> {
			if (saleCaptured.get()
					&& map.get(FieldType.TXNTYPE.getName()).toString().equalsIgnoreCase(TransactionType.SALE.getName())
					&& map.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(StatusType.CAPTURED.getName())) {
				saleAmount.addAndGet(Double.valueOf(map.get(FieldType.TOTAL_AMOUNT.getName()).toString()));
				saleCaptured.set(false);

			}

			if (saleCaptured.get()
					&& map.get(FieldType.TXNTYPE.getName()).toString().equalsIgnoreCase(TransactionType.RECO.getName())
					&& map.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(StatusType.CAPTURED.getName())) {
				saleAmount.addAndGet(Double.valueOf(map.get(FieldType.TOTAL_AMOUNT.getName()).toString()));
				saleCaptured.set(false);

			}

			if (saleCaptured.get()
					&& map.get(FieldType.TXNTYPE.getName()).toString().equalsIgnoreCase(TransactionType.RECO.getName())
					&& map.get(FieldType.STATUS.getName()).toString()
							.equalsIgnoreCase(StatusType.FORCE_CAPTURED.getName())) {
				saleAmount.addAndGet(Double.valueOf(map.get(FieldType.TOTAL_AMOUNT.getName()).toString()));
				saleCaptured.set(false);

			}

			if (saleCaptured.get()
					&& map.get(FieldType.TXNTYPE.getName()).toString().equalsIgnoreCase(TransactionType.RECO.getName())
					&& map.get(FieldType.STATUS.getName()).toString()
							.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())) {
				saleAmount.addAndGet(Double.valueOf(map.get(FieldType.TOTAL_AMOUNT.getName()).toString()));
				saleCaptured.set(false);

			}

			if (map.get(FieldType.TXNTYPE.getName()).toString().equalsIgnoreCase(TransactionType.REFUND.getName())
					&& (map.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(StatusType.CAPTURED.getName())
							|| map.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase("REFUND_INITIATED"))) {
				refundAmount.addAndGet(Double.valueOf("" + map.get(FieldType.AMOUNT.getName())));
			}

		});

		// refundAmount.addAndGet(bulkPendAmt.get());
		refundAmount.addAndGet(Double.valueOf(Amount.toDecimal(bulkRefundEntity.getAmount().replace(".0", ""), "356")));
		logger.info("refund amount and sale amount" + refundAmount + "   " + saleAmount);
		if ((refundAmount.get() > saleAmount.get())) {
			return false;
		} else {
			return true;
		}

	}

	public List<BulkRefundEntity> fetchBulkRefundData(String fromdate, String toDate, String fileName, String status) {
		Query qry = null;
		Criteria c = null;
		Criteria c2 = null;
		Criteria c1 = null;
		logger.info("data for bulk search fromdate:" + fromdate + " todate : " + toDate + " filename  :" + fileName
				+ " status: " + status);

		if (status.equalsIgnoreCase("ALL") && StringUtils.isNotBlank(fileName)) {
			c = Criteria.where("fileName").is(fileName);
		} else if (!(status.equalsIgnoreCase("ALL")) && StringUtils.isBlank(fileName)) {
			if (status.equalsIgnoreCase("Failed")) {
				c2 = Criteria.where("status").nin("REFUND_INITIATED", "Pending");
			} else {
				c2 = Criteria.where("status").is(status);
			}
			c1 = Criteria.where("createDate").gte(fromdate);
			Criteria c3 = Criteria.where("createDate").lte(toDate);
			Criteria c4 = new Criteria().andOperator(c1, c2);
			c = new Criteria().andOperator(c4, c2);
		} else if (!(status.equalsIgnoreCase("ALL")) && StringUtils.isNotBlank(fileName)) {
			c1 = Criteria.where("fileName").is(fileName);
			if (status.equalsIgnoreCase("Failed")) {
				c2 = Criteria.where("status").nin("REFUND_INITIATED", "Pending");

			} else {
				c2 = Criteria.where("status").is(status);

			}
			c = new Criteria().andOperator(c1, c2);

		} else {

			c1 = Criteria.where("createDate").gte(fromdate);
			c2 = Criteria.where("createDate").lte(toDate);
			c = new Criteria().andOperator(c1, c2);
		}

		logger.info("query for bulk search" + c);
		qry = Query.query(c);
		logger.info("query for bulk search" + qry);

		List<BulkRefundEntity> bulkList = mongoTemplate.find(qry, BulkRefundEntity.class);
		
		for(int i=0; i<bulkList.size(); i++){
			String amount = Amount.toDecimal(bulkList.get(i).getAmount(), "356");
			bulkList.get(i).setAmount(amount);
		}
		return bulkList;
	}

	public List<BulkRefundCountDto> fetchBulkRefundDataCount(String fromdate, String toDate, String filename) {
		List<BulkRefundCountDto> response = new ArrayList<BulkRefundCountDto>();

		if (StringUtils.isNotBlank(filename) && !(filename == null)) {

			Criteria c5 = Criteria.where("fileName").is(filename);

			MatchOperation match5 = match(c5);

			Aggregation aggregation1 = Aggregation.newAggregation(match5,
					group("status").count().as("count"));
			logger.info("query for count " + aggregation1);

			List<Map> result1 = mongoTemplate.aggregate(aggregation1, "bulkRefundEntity", Map.class).getMappedResults();
			logger.info("result for status count" + new Gson().toJson(result1));
			BulkRefundCountDto bulkRefundCountDto = new BulkRefundCountDto();

			if(result1.size()>0){
			AtomicLong failCount = new AtomicLong(0);
			AtomicLong totalCount = new AtomicLong(0);

			result1.stream().forEach(count -> {
				
				
				if (count.get("_id").equals("REFUND_INITIATED")) {
					bulkRefundCountDto.setSuccessTXN(String.valueOf((Integer) count.get("count")));
				} else if (count.get("_id").equals("Pending")) {
					bulkRefundCountDto.setPending(String.valueOf((Integer) count.get("count")));
				}

				else {
					failCount.getAndAdd((Integer) count.get("count"));
					bulkRefundCountDto.setFailedTXN(String.valueOf(failCount));

				}
				
				totalCount.getAndAdd((Integer) count.get("count"));
				bulkRefundCountDto.setTotalTxn(String.valueOf(totalCount));

				bulkRefundCountDto.setFileName(filename);

			});

			if (StringUtils.isBlank(bulkRefundCountDto.getPending()) || bulkRefundCountDto.getPending() == null) {
				bulkRefundCountDto.setPending("NA");
			}
			;
			if (StringUtils.isBlank(bulkRefundCountDto.getFailedTXN()) || bulkRefundCountDto.getFailedTXN() == null) {
				bulkRefundCountDto.setFailedTXN("NA");
			}
			;

			
			if (StringUtils.isBlank(bulkRefundCountDto.getSuccessTXN()) || bulkRefundCountDto.getSuccessTXN() == null) {
				bulkRefundCountDto.setSuccessTXN("NA");
			}
			;
			response.add(bulkRefundCountDto);
			}
			return response;
		}

		logger.info(toDate);
		logger.info(fromdate);
		Criteria c1 = Criteria.where("createDate").gte(fromdate);
		Criteria c2 = Criteria.where("createDate").lte(toDate);
		Criteria c = new Criteria().andOperator(c1, c2);
		logger.info("query for date  " + c);

		MatchOperation match = match(c);
		logger.info("match for bulk summary " + match);

		Aggregation aggregation = Aggregation.newAggregation(match, group("fileName"));
		logger.info("query for bulk summary " + aggregation);

		List<Map> result = mongoTemplate.aggregate(aggregation, "bulkRefundEntity", Map.class).getMappedResults();
		logger.info("result for file name  " + new Gson().toJson(result));

		result.stream().forEach(map -> {
			logger.info("file name " + map.get("_id"));

			Criteria c5 = Criteria.where("fileName").is(map.get("_id"));

			MatchOperation match5 = match(c5);

			Aggregation aggregation1 = Aggregation.newAggregation(match5,
					group("status").count().as("count"));
			logger.info("query for count " + aggregation1);

			List<Map> result1 = mongoTemplate.aggregate(aggregation1, "bulkRefundEntity", Map.class).getMappedResults();
			logger.info("result for status count" + new Gson().toJson(result1));
			BulkRefundCountDto bulkRefundCountDto = new BulkRefundCountDto();
			bulkRefundCountDto.setFileName(String.valueOf(map.get("_id")));

			AtomicLong failCount = new AtomicLong(0);
			AtomicLong totalCount = new AtomicLong(0);

			result1.stream().forEach(count -> {

				if (count.get("_id").equals("REFUND_INITIATED")) {
					bulkRefundCountDto.setSuccessTXN(String.valueOf((Integer) count.get("count")));
				} else if (count.get("_id").equals("Pending")) {
					bulkRefundCountDto.setPending(String.valueOf((Integer) count.get("count")));
				}

				else {
					failCount.getAndAdd((Integer) count.get("count"));
					bulkRefundCountDto.setFailedTXN(String.valueOf(failCount));

				}
				
				totalCount.getAndAdd((Integer) count.get("count"));
				bulkRefundCountDto.setTotalTxn(String.valueOf(totalCount));

			});

			if (StringUtils.isBlank(bulkRefundCountDto.getPending()) || bulkRefundCountDto.getPending() == null) {
				bulkRefundCountDto.setPending("NA");
			}
			;
			if (StringUtils.isBlank(bulkRefundCountDto.getFailedTXN()) || bulkRefundCountDto.getFailedTXN() == null) {
				bulkRefundCountDto.setFailedTXN("NA");
			}
			;

			if (StringUtils.isBlank(bulkRefundCountDto.getSuccessTXN()) || bulkRefundCountDto.getSuccessTXN() == null) {
				bulkRefundCountDto.setSuccessTXN("NA");
			}
			;
			response.add(bulkRefundCountDto);

			logger.info("bulkRefundCountDto" + bulkRefundCountDto.toString());

		});
		
		return response;
	}


	public void saveIrctcBulkRefund(IrctcRefundEntity irctcRefundEntity) {
		logger.info("INSIDE saveIrctcBulkRefund");
		Map transaction = findTransaction(irctcRefundEntity);
		irctcRefundEntity.setStatus("Failed");
		irctcRefundEntity.setResponseCode("022");
		if(transaction==null){
			irctcRefundEntity.setResponseMessage("No entry found for this transaction");
		}else if(isDuplicate(irctcRefundEntity)){
			irctcRefundEntity.setResponseMessage("Refund already created");
		}else{
			irctcRefundEntity.setPayId(String.valueOf(transaction.get("PAY_ID")));
			double transactionAmount = Double.parseDouble(transaction.getOrDefault("AMOUNT", "0").toString());
			double totalRefundAmount = getTotalRefundAmount(irctcRefundEntity)+Double.valueOf(irctcRefundEntity.getAmount());
			logger.info(totalRefundAmount + " "+transactionAmount);
			if(totalRefundAmount>transactionAmount){
				irctcRefundEntity.setResponseMessage("Refund amount more than transaction amount");
			}else{
				irctcRefundEntity.setStatus("Pending");
				irctcRefundEntity.setResponseCode("000");
				irctcRefundEntity.setResponseMessage("Refund is Pending State");
			}
		}
		mongoTemplate.insert(irctcRefundEntity);
	}

	private double getTotalRefundAmount(IrctcRefundEntity irctcRefundEntity) {
		MatchOperation match  = Aggregation.match(Criteria.where("orderId").is(irctcRefundEntity.getOrderId()).andOperator(Criteria.where("pgRefNO").is(irctcRefundEntity.getPgRefNO()), Criteria.where("ResponseCode").is("000")));

		ProjectionOperation project1 = Aggregation.project("orderId")
				.and(ConvertOperators.Convert.convertValueOf("amount").to("double")).as("amount");

		GroupOperation group = Aggregation.group()
				.sum("amount").as("amount");

		Aggregation agg = Aggregation.newAggregation(match, project1, group);


		AggregationResults<Document> results = mongoTemplate.aggregate(agg, "irctcRefundEntity", Document.class);
		Double total;
		if(!results.getMappedResults().isEmpty()) {
			total = results.getMappedResults().get(0).getDouble("amount");
		}else{
			total = 0d;
		}
		return total;
	}

	private boolean isDuplicate(IrctcRefundEntity irctcRefundEntity) {
		Query query = new Query();
		Criteria criteria = Criteria.where("pgRefNO").is(irctcRefundEntity.getPgRefNO());
		criteria.andOperator(Criteria.where("orderId").is(irctcRefundEntity.getOrderId()), Criteria.where("irctcRefundCancelledId").is(irctcRefundEntity.getIrctcRefundCancelledId()));
		query.addCriteria(criteria);
		long count = mongoTemplate.count(query, "irctcRefundEntity");
		return count>0;
	}

	private Map findTransaction(IrctcRefundEntity irctcRefundEntity){
		Query query = new Query();
		Criteria criteria = Criteria.where("PG_REF_NUM").is(irctcRefundEntity.getPgRefNO());
		criteria.andOperator(Criteria.where("ORDER_ID").is(irctcRefundEntity.getOrderId()), Criteria.where("TXNTYPE").is("SALE"));
		query.addCriteria(criteria);
		Map transaction = mongoTemplate.findOne(query, Map.class, "transaction");
		return transaction;
	}
	

}
