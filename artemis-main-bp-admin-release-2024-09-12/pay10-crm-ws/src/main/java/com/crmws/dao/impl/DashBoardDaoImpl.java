package com.crmws.dao.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pay10.commons.user.UserType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.crmws.dao.DashbordDao;
import com.crmws.dto.DashBoardTxnCountRequest;
import com.crmws.dto.DashboardTxnDetails;
import com.crmws.dto.GetPiChart;
import com.crmws.dto.HourlyChartData;
import com.crmws.dto.LineChartRespDto;
import com.crmws.dto.LineChartType;
import com.crmws.dto.PiChartPopup;
import com.crmws.dto.PieChart;
import com.crmws.dto.SettlementChartData;
import com.mongodb.BasicDBObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.StatusTypeReports;

@Repository
public class DashBoardDaoImpl implements DashbordDao {

	Logger logger = LoggerFactory.getLogger(DashBoardDaoImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserDao userDao;

	@Override
	public Optional<List<GetPiChart>> getPiChartQuery(String dateFrom, String dateTo, String acquirer) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		Criteria criteria = new Criteria();
		if (StringUtils.isNotEmpty(acquirer) && !acquirer.equalsIgnoreCase("ALL")) {
			criteria = Criteria.where("ACQUIRER_TYPE").is(acquirer);
		}
		if (StringUtils.isNotEmpty(dateFrom) && StringUtils.isNotEmpty(dateTo)) {
			dateFrom = dateFrom + " 00:00:00";
			dateTo = dateTo + " 23:59:59";
			LocalDateTime fromLDate = LocalDateTime.parse(dateFrom, formatter1);
			LocalDateTime toLDate = LocalDateTime.parse(dateTo, formatter1);
			criteria.and("CREATE_DATE").gte(formatter.format(fromLDate)).lte(formatter.format(toLDate));
		}
		criteria.and("PAYMENT_TYPE").ne(null);
		
		Aggregation agg = Aggregation.newAggregation(Aggregation.match(criteria),
				Aggregation.group("PAYMENT_TYPE").first("PAYMENT_TYPE").as("PAYMENT_TYPE").count().as("totalCount")
						.sum(ConvertOperators.ToDouble.toDouble("$TOTAL_AMOUNT")).as("totalAmount"),
				Aggregation.project("PAYMENT_TYPE", "totalCount", "totalAmount")

		);
		System.out.println(agg.toString());
		List<Map> list = mongoTemplate.aggregate(agg, "transactionStatus", Map.class).getMappedResults();
		System.out.println("List Size : " + list);
		List<GetPiChart> data = list.stream().map(a -> {
			GetPiChart p = new GetPiChart();
			p.setMopType(a.get("PAYMENT_TYPE").toString());
			p.setTxnCount(Integer.parseInt(a.get("totalCount").toString()));
			p.setTotalAmount(Double.parseDouble(a.get("totalAmount").toString()));
			return p;
		}).collect(Collectors.toList());
		return Optional.ofNullable(data);
	}

	@Override
	public Optional<List<PiChartPopup>> getPiChartPop(String dateFrom, String dateTo, String acquirer, String mopType) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		Criteria criteria = new Criteria();
		if (StringUtils.isNotEmpty(acquirer) && !acquirer.equalsIgnoreCase("ALL")) {
			criteria = Criteria.where("ACQUIRER_TYPE").is(acquirer);
		}
		if (StringUtils.isNotEmpty(dateFrom) && StringUtils.isNotEmpty(dateTo)) {
			dateFrom = dateFrom + " 00:00:00";
			dateTo = dateTo + " 23:59:59";
			LocalDateTime fromLDate = LocalDateTime.parse(dateFrom, formatter1);
			LocalDateTime toLDate = LocalDateTime.parse(dateTo, formatter1);
			criteria.and("CREATE_DATE").gte(formatter.format(fromLDate)).lte(formatter.format(toLDate));
		}
		criteria.and("PAYMENT_TYPE").is(mopType);
		criteria.and("STATUS").is("Captured");
		criteria.and("TXNTYPE").is("SALE");
		/*
		 * Query query = new Query(); query.addCriteria(criteria);
		 * System.out.println("Query : " + query.toString()); List<Map> list =
		 * mongoTemplate.find(query,Map.class);
		 */
		Aggregation agg = Aggregation.newAggregation(Arrays.asList(Aggregation.match(criteria),
				Aggregation.project("TXN_ID", "PG_REF_NUM", "TOTAL_AMOUNT", "CREATE_DATE", "MOP_TYPE"),
				Aggregation.sort(Sort.Direction.DESC, "MOP_TYPE")));
		System.out.println("Aggregation Query : " + agg.toString());
		List<Map> list = mongoTemplate.aggregate(agg, "transactionStatus", Map.class).getMappedResults();

		List<PiChartPopup> data = list.stream().map(a -> {
			PiChartPopup piChartPopup = new PiChartPopup();
			piChartPopup.setTxnID(a.get("TXN_ID").toString());
			piChartPopup.setPgRefNo(a.get("PG_REF_NUM").toString());
			piChartPopup.setDate(a.get("CREATE_DATE").toString());
			piChartPopup.setMopType(a.get("MOP_TYPE").toString());
			if (a.get("TOTAL_AMOUNT") == null) {
				piChartPopup.setAmount(0.0);
			} else {
				double totalAmt = Double.parseDouble(a.get("TOTAL_AMOUNT").toString());
				piChartPopup.setAmount(totalAmt);
			}
			return piChartPopup;
		}).collect(Collectors.toList());
		return Optional.ofNullable(data);
	}

	@Override
	public Optional<List<LineChartRespDto>> getLineChart(String dateFrom, String dateTo, String acquirer,
			String mopType, String paymentType, String status, String statusType, LineChartType chartType) {
		List<LineChartRespDto> list ;
		if(chartType.equals(LineChartType.STATUS_CHART) ){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
			// DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM/dd/yyyy
			// HH:mm:ss");
			List<String> dateArr = new ArrayList<>();
			Criteria matchCriteria1 = new Criteria();

			if (StringUtils.isNotEmpty(dateFrom) && StringUtils.isNotEmpty(dateTo)) {
				dateFrom = dateFrom + " 00:00:00";
				dateTo = dateTo + " 23:59:59";
				LocalDateTime fromLDate = LocalDateTime.parse(dateFrom, formatter);
				LocalDateTime startDate = LocalDateTime.parse(dateFrom, formatter);
				LocalDateTime toLDate = LocalDateTime.parse(dateTo, formatter);

				while (!startDate.isAfter(toLDate)) {
					dateArr.add(formatter2.format(startDate));
					startDate = startDate.plusDays(1);
				}
				matchCriteria1.and("CREATE_DATE").gte(formatter.format(fromLDate)).lte(formatter.format(toLDate));
			}

			matchCriteria1.and("TXNTYPE").is("SALE");
			MatchOperation matchOperation1 = Aggregation.match(matchCriteria1);

			GroupOperation facetGroupOperation1 = Aggregation.group("DATE_INDEX","PG_REF_NUM");

			ProjectionOperation facetProjectionOperation1 = Aggregation.project()
					.andExpression("_id.DATE_INDEX").as("DATE_INDEX")
					.andExpression("_id.PG_REF_NUM").as("PG_REF_NUM")
					.andExclude("_id");

			GroupOperation facetGroupOperation2 = Aggregation.group("DATE_INDEX")
					.count().as("TOTAL_COUNT");

			ProjectionOperation facetProjectionOperation2 = Aggregation.project("TOTAL_COUNT")
					.andExpression("_id").as("DATE_INDEX")
					.andExclude("_id");

			GroupOperation facetGroupOperation3 = Aggregation.group("DATE_INDEX","STATUS")
					.count().as("TOTAL_FIELD_COUNT");

			ProjectionOperation facetProjectionOperation3 = Aggregation.project("TOTAL_FIELD_COUNT")
					.andExpression("_id.DATE_INDEX").as("DATE_INDEX")
					.andExpression("_id.STATUS").as("STATUS")
					.andExclude("_id");

			FacetOperation facetOperation = Aggregation.facet(facetGroupOperation1,facetProjectionOperation1,
							facetGroupOperation2,facetProjectionOperation2).as("FCOUNT")
					.and(facetGroupOperation3,facetProjectionOperation3).as("FSTATUS");

			UnwindOperation unwindOperation1 = Aggregation.unwind("FCOUNT");


			AggregationOperation projectionOperation1 = new AggregationOperation() {

				@Override
				public Document toDocument(AggregationOperationContext context) {
					// TODO Auto-generated method stub
					return new Document("$project",
							new Document("DATE_INDEX", "$FCOUNT.DATE_INDEX")
									.append("TOTAL_COUNT", "$FCOUNT.TOTAL_COUNT")
									.append("FSTATUS",
											new Document("$filter",
													new Document("input", "$FSTATUS")
															.append("as", "this")
															.append("cond",
																	new Document("$eq", Arrays.asList("$FCOUNT.DATE_INDEX", "$$this.DATE_INDEX"))))))
							;
				}
			};

			

			UnwindOperation unwindOperation2 = Aggregation.unwind("FSTATUS");

			AggregationOperation projectionOperation2 = new AggregationOperation() {

				@Override
				public Document toDocument(AggregationOperationContext context) {
					return new Document("$project",new Document("DATE_INDEX",1)
							.append("TOTAL_COUNT",1)
							.append("TOTAL_FIELD_COUNT","$FSTATUS.TOTAL_FIELD_COUNT")
							.append("STATUS","$FSTATUS.STATUS"));
				}


			};



			Criteria matchCriteria2 = new Criteria();

			if ((!StringUtils.isEmpty(status) && !status.equalsIgnoreCase("ALL"))) {
				matchCriteria2.and(chartType.getField()).in(getDbStatus(status));
			}

			MatchOperation matchOperation2 = Aggregation.match(matchCriteria2);

			AggregationOperation projectionOperation3 = new AggregationOperation() {

				@Override
				public Document toDocument(AggregationOperationContext context) {
					return new Document("$project", new Document("DATE_INDEX", 1)
							.append("TOTAL_COUNT", 1)
							.append("TOTAL_FIELD_COUNT", 1)
							.append("STATUS", 1)
							.append("FIELD_MULTI", new Document("$multiply", Arrays.asList("$TOTAL_FIELD_COUNT",100))));
				}
			};



			AggregationOperation projectionOperation4 = new AggregationOperation() {

				@Override
				public Document toDocument(AggregationOperationContext context) {
					return new Document("$project", new Document("DATE_INDEX", 1)
							.append("TOTAL_COUNT", 1)
							.append("TOTAL_FIELD_COUNT", 1)
							.append("STATUS", 1)
							.append("PERCENTAGE", new Document("$divide", Arrays.asList("$FIELD_MULTI","$TOTAL_COUNT"))));
				}
			};



			AggregationOperation sortOperation1 = new AggregationOperation() {

				@Override
				public Document toDocument(AggregationOperationContext context) {
					return new Document("$sort", new Document("DATE_INDEX", 1)
					);
				}
			};


			AggregationOperation groupOperation1 = new AggregationOperation() {

				@Override
				public Document toDocument(AggregationOperationContext context) {
					return new Document("$group", new Document("_id","$STATUS")
							.append("data",new Document("$push",new Document("date", "$DATE_INDEX")
									.append("percentage", new Document("$round", Arrays.asList("$PERCENTAGE", 0)))
									.append("totalFieldCount", "$TOTAL_FIELD_COUNT").append("totalCount", "$TOTAL_COUNT")))
					);
				}
			};



			AggregationOperation projectionOperation5 = new AggregationOperation() {

				@Override
				public Document toDocument(AggregationOperationContext context) {
					return new Document("$project", new Document("data", 1)
							.append("label", "$_id")
							.append("_id", 0)
					);
				}
			};



			AggregationOperation sortOperation2 = new AggregationOperation() {

				@Override
				public Document toDocument(AggregationOperationContext context) {
					return new Document("$sort", new Document("label", 1)
					);
				}
			};


			AggregationOperation addFieldsOperation = new AggregationOperation() {

				@Override
				public Document toDocument(AggregationOperationContext context) {
					// TODO Auto-generated method stub
					return new Document("$addFields", new Document("dateArray", dateArr));
				}
			};

			Aggregation agg = Aggregation.newAggregation(Arrays.asList(matchOperation1, facetOperation, unwindOperation1,
					projectionOperation1, unwindOperation2, projectionOperation2, matchOperation2, projectionOperation3,
					projectionOperation4,sortOperation1, groupOperation1, projectionOperation5, sortOperation2, addFieldsOperation));
			System.out.println("Aggregation Query : " + agg.toString());
			list = mongoTemplate.aggregate(agg, "transactionStatus", LineChartRespDto.class)
					.getMappedResults();
		}else {
			list = getLineChartFilter(dateFrom, dateTo, acquirer,
					mopType, paymentType, status, statusType, chartType);
		}

		return Optional.ofNullable(list);
	}

	private List<String> getDbStatus(String uiStatus) {

		if (StringUtils.equalsIgnoreCase(uiStatus, "Failed")) {
			return Arrays.asList("Error","Authentication Failed","Rejected","Failed at Acquirer","Denied due to fraud","Cancelled by user","Initiated","Processing","Denied by risk","Acquirer down","Declined","Failed","FAILED");
		}
		if (StringUtils.equalsIgnoreCase(uiStatus, "Cancelled")) {
			return Arrays.asList("Cancelled,Cancelled By User");
		}
		return Arrays.asList(uiStatus);
	}

	private List<LineChartRespDto> getLineChartFilter(String dateFrom, String dateTo, String acquirer,
													  String mopType, String paymentType, String status, String statusType, LineChartType chartType)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
		// DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM/dd/yyyy
		// HH:mm:ss");
		List<String> dateArr = new ArrayList<>();
		Criteria matchCriteria1 = new Criteria();

		if (StringUtils.isNotEmpty(dateFrom) && StringUtils.isNotEmpty(dateTo)) {
			dateFrom = dateFrom + " 00:00:00";
			dateTo = dateTo + " 23:59:59";
			LocalDateTime fromLDate = LocalDateTime.parse(dateFrom, formatter);
			LocalDateTime startDate = LocalDateTime.parse(dateFrom, formatter);
			LocalDateTime toLDate = LocalDateTime.parse(dateTo, formatter);

			while (!startDate.isAfter(toLDate)) {
				dateArr.add(formatter2.format(startDate));
				startDate = startDate.plusDays(1);
			}
			matchCriteria1.and("CREATE_DATE").gte(formatter.format(fromLDate)).lte(formatter.format(toLDate));
		}
		matchCriteria1.and("TXNTYPE").is("SALE");
		if (chartType.equals(LineChartType.ACQUIRER_CHART)
				&& (StringUtils.isNotEmpty(acquirer) && !acquirer.equalsIgnoreCase("ALL"))) {
			matchCriteria1.and(chartType.getField()).is(acquirer);
		} else if (chartType.equals(LineChartType.PAYMENT_TYPE_CHART)
				&& (!StringUtils.isEmpty(paymentType) && !paymentType.equalsIgnoreCase("ALL"))) {
			matchCriteria1.and(chartType.getField()).is(paymentType);
		} else if (chartType.equals(LineChartType.MOP_TYPE_CHART)
				&& (!StringUtils.isEmpty(mopType) && !mopType.equalsIgnoreCase("ALL"))) {
			matchCriteria1.and(chartType.getField()).is(mopType);
		}

		MatchOperation matchOperation1 = Aggregation.match(matchCriteria1);

		GroupOperation groupOperation1 = Aggregation.group("DATE_INDEX",chartType.getField()).push("STATUS")
				.as("STATUS").count().as("TOTAL_COUNT")
				;


		UnwindOperation unwindOperation = Aggregation.unwind("STATUS");



		Criteria matchCriteria2 = new Criteria();

		if (!StringUtils.isEmpty(statusType)) {

			if (statusType.equalsIgnoreCase(StatusTypeReports.SUCCESS.getCode())) {
				matchCriteria2.and("STATUS")
						.in(Arrays.asList(StatusType.CAPTURED.getName(),StatusType.SETTLED_SETTLE.getName(),
								StatusType.SETTLED_RECONCILLED.getName()));

			} else {
				matchCriteria2.and("STATUS").nin(Arrays.asList(StatusType.SENT_TO_BANK.getName(),StatusType.PENDING.getName(),
						StatusType.CAPTURED.getName(),StatusType.SETTLED_RECONCILLED.getName(),
						StatusType.SETTLED_SETTLE.getName(),StatusType.USER_INACTIVE.getName()));
			}

		}

		MatchOperation matchOperation2 = Aggregation.match(matchCriteria2);

		ProjectionOperation projectionOperation1 = Aggregation.project("TOTAL_COUNT")
				.andExpression("_id.DATE_INDEX").as("DATE_INDEX")
				.andExpression("_id."+chartType.getField()).as(chartType.getField());

		GroupOperation groupOperation2 = Aggregation.group(chartType.getField(), "DATE_INDEX").count()
				.as("TOTAL_FIELD_COUNT").first("TOTAL_COUNT").as("TOTAL_COUNT");

		ProjectionOperation projectionOperation2 = Aggregation.project().andInclude("TOTAL_COUNT", "TOTAL_FIELD_COUNT")
				.andExpression("_id.DATE_INDEX").as("DATE_INDEX").andExpression("_id." + chartType.getField())
				.as(chartType.getField()).andExpression("_id." + chartType.getField()).as(chartType.getField())
				.and("TOTAL_FIELD_COUNT").multiply(100).as("FIELD_MULTI").andExclude("_id");

		ProjectionOperation projectionOperation3 = Aggregation.project().and("FIELD_MULTI").divide("TOTAL_COUNT")
				.as("PERCENTAGE").andInclude("DATE_INDEX", chartType.getField(), "TOTAL_COUNT", "TOTAL_FIELD_COUNT");

		SortOperation sortOperation1 = Aggregation.sort(Sort.Direction.ASC, "DATE_INDEX");

		GroupOperation groupOperation3 = Aggregation.group(chartType.getField())
				.push(new BasicDBObject("date", "$DATE_INDEX")
						.append("percentage", new BasicDBObject("$round", Arrays.asList("$PERCENTAGE", 0)))
						.append("totalFieldCount", "$TOTAL_FIELD_COUNT").append("totalCount", "$TOTAL_COUNT"))
				.as("data");

		ProjectionOperation projectionOperation4 = Aggregation.project().andInclude("data").and("_id").as("label")
				.andExclude("_id");

		SortOperation sortOperation2 = Aggregation.sort(Sort.Direction.ASC, "label");

		AggregationOperation addFieldsOperation = new AggregationOperation() {

			@Override
			public Document toDocument(AggregationOperationContext context) {
				// TODO Auto-generated method stub
				return new Document("$addFields", new Document("dateArray", dateArr));
			}
		};

		Aggregation agg = Aggregation.newAggregation(Arrays.asList(matchOperation1, groupOperation1, unwindOperation,
				matchOperation2, projectionOperation1, groupOperation2, projectionOperation2, projectionOperation3,
				sortOperation1, groupOperation3, projectionOperation4, sortOperation2, addFieldsOperation));
		System.out.println("Aggregation Query : " + agg.toString());
		List<LineChartRespDto> list = mongoTemplate.aggregate(agg, "transactionStatus", LineChartRespDto.class)
				.getMappedResults();
		return list;
	}

	/*
	 * @SuppressWarnings({ "rawtypes" })
	 * 
	 * @Override public Optional<DashboardTxnDetails>
	 * getDashboardTotalTxnDetails(DashBoardTxnCountRequest request) {
	 * DateTimeFormatter formatter =
	 * DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); DateTimeFormatter
	 * formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
	 * 
	 * // DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM/dd/yyyy //
	 * HH:mm:ss"); List<String> dateArr = new ArrayList<>(); Criteria matchCriteria
	 * = new Criteria();
	 * 
	 * String dateFrom = request.getDateFrom() + " 00:00:00"; String dateTo =
	 * request.getDateTo() + " 23:59:59"; LocalDateTime fromLDate =
	 * LocalDateTime.parse(dateFrom, formatter); LocalDateTime startDate =
	 * LocalDateTime.parse(dateFrom, formatter); LocalDateTime toLDate =
	 * LocalDateTime.parse(dateTo, formatter);
	 * 
	 * while (!startDate.isAfter(toLDate)) {
	 * dateArr.add(formatter2.format(startDate)); startDate = startDate.plusDays(1);
	 * } matchCriteria =
	 * Criteria.where("CREATE_DATE").gte(formatter.format(fromLDate)).lte(formatter.
	 * format(toLDate)); if (StringUtils.isNotBlank(request.getTxnType())) {
	 * matchCriteria.and("TXNTYPE").is(request.getTxnType()); } if
	 * (StringUtils.isNotBlank(request.getPaymentType())) {
	 * matchCriteria.and("PAYMENT_TYPE").is(request.getPaymentType()); } if
	 * (StringUtils.isNotBlank(request.getMopType())) {
	 * matchCriteria.and("MOP_TYPE").is(request.getMopType()); } if
	 * (StringUtils.isNotBlank(request.getAcquirer())) {
	 * matchCriteria.and("ACQUIRER_TYPE").is(request.getAcquirer()); }
	 * 
	 * if (StringUtils.isNotBlank(request.getEmailId())) { User user =
	 * userDao.findByEmailId(request.getEmailId());
	 * matchCriteria.and("PAY_ID").is(user.getPayId()); } Aggregation agg =
	 * Aggregation.newAggregation(Arrays.asList(Aggregation.match(matchCriteria),
	 * Aggregation .project("TXNTYPE", "STATUS", "TOTAL_AMOUNT", "AMOUNT",
	 * "CREATE_DATE", "MOP_TYPE", "PAYMENT_TYPE"))); List<Map> list =
	 * mongoTemplate.aggregate(agg, "transactionStatus",
	 * Map.class).getMappedResults(); DashboardTxnDetails pieChart = new
	 * DashboardTxnDetails(); double totalSalesAmount = 0; double totalRefundAmount
	 * = 0; double totalCancelledAmount = 0; double totalFailedAmount = 0; double
	 * totalFraudAmount = 0; double totalSettledAmount = 0; for (Map doc : list) {
	 * String totalAmount = String.valueOf(doc.getOrDefault("TOTAL_AMOUNT", 0));
	 * totalAmount = totalAmount == "null" ? "0" : totalAmount; double totalAmt =
	 * Double.parseDouble(totalAmount);
	 * 
	 * String amount = String.valueOf(doc.getOrDefault("AMOUNT", 0)); totalAmount =
	 * amount == "null" ? "0" : amount; double amt = Double.parseDouble(amount);
	 * 
	 * if (StringUtils.equalsIgnoreCase((String) doc.get("TXNTYPE"), "SALE") &&
	 * StringUtils.equalsIgnoreCase((String) doc.get("STATUS"), "Captured")) {
	 * pieChart.setTotalSaleCount(pieChart.getTotalSaleCount() + 1);
	 * totalSalesAmount = totalSalesAmount + totalAmt; } else if
	 * (StringUtils.equalsIgnoreCase((String) doc.get("TXNTYPE"), "REFUND") &&
	 * StringUtils.equalsIgnoreCase((String) doc.get("STATUS"), "Captured")) {
	 * pieChart.setTotalRefundCount(pieChart.getTotalRefundCount() + 1);
	 * totalRefundAmount = totalRefundAmount + totalAmt; } else if
	 * (StringUtils.equalsIgnoreCase((String) doc.get("STATUS"), "Settled")) {
	 * pieChart.setTotalSettleCount(pieChart.getTotalSettleCount() + 1);
	 * totalSettledAmount = totalSettledAmount + totalAmt; } else if
	 * (StringUtils.equalsIgnoreCase((String) doc.get("STATUS"),
	 * "Denied due to fraud")) {
	 * pieChart.setTotalFraudCount(pieChart.getTotalFraudCount() + 1);
	 * totalFraudAmount = totalFraudAmount + totalAmt; } else if
	 * (StringUtils.equalsAnyIgnoreCase((String) doc.get("STATUS"),
	 * StringUtils.split(StatusType.getInternalStatus("Failed"), ","))) {
	 * pieChart.setTotalFailedCount(pieChart.getTotalFailedCount() + 1);
	 * totalFailedAmount = totalFailedAmount + totalAmt; } else if
	 * (StringUtils.equalsAnyIgnoreCase((String) doc.get("STATUS"),
	 * StringUtils.split(StatusType.getInternalStatus("Cancelled"), ","))) {
	 * pieChart.setTotalCancelledCount(pieChart.getTotalCancelledCount() + 1);
	 * totalCancelledAmount = totalCancelledAmount + amt; } }
	 * pieChart.setTotalSaleAmount(String.valueOf(totalSalesAmount));
	 * pieChart.setTotalRefundAmount(String.valueOf(totalRefundAmount));
	 * pieChart.setTotalFraudAmount(String.valueOf(totalFraudAmount));
	 * pieChart.setTotalFailedAmount(String.valueOf(totalFailedAmount));
	 * pieChart.setTotalSettleAmount(String.valueOf(totalSettledAmount));
	 * pieChart.setTotalCancelledAmount(String.valueOf(totalCancelledAmount));
	 * return Optional.ofNullable(pieChart); }
	 */

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Optional<PieChart> getDashboardTotalTxnDetails(DashBoardTxnCountRequest request) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");

		// DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM/dd/yyyy
		// HH:mm:ss");
		List<String> dateArr = new ArrayList<>();
		Criteria matchCriteria = new Criteria();

		String dateFrom = request.getDateFrom() + " 00:00:00";
		String dateTo = request.getDateTo() + " 23:59:59";
		LocalDateTime fromLDate = LocalDateTime.parse(dateFrom, formatter);
		LocalDateTime startDate = LocalDateTime.parse(dateFrom, formatter);
		LocalDateTime toLDate = LocalDateTime.parse(dateTo, formatter);

		while (!startDate.isAfter(toLDate)) {
			dateArr.add(formatter2.format(startDate));
			startDate = startDate.plusDays(1);
		}
		matchCriteria = Criteria.where("CREATE_DATE").gte(formatter.format(fromLDate)).lte(formatter.format(toLDate));
		if (StringUtils.isNotBlank(request.getTxnType())) {
			matchCriteria.and("TXNTYPE").is(request.getTxnType());
		}
		if (StringUtils.isNotBlank(request.getPaymentType())) {
			matchCriteria.and("PAYMENT_TYPE").is(request.getPaymentType());
		}
		if (StringUtils.isNotBlank(request.getMopType())) {
			matchCriteria.and("MOP_TYPE").is(request.getMopType());
		}
		if (StringUtils.isNotBlank(request.getAcquirer())) {
			matchCriteria.and("ACQUIRER_TYPE").is(request.getAcquirer());
		}
		if (StringUtils.isNotBlank(request.getCurrency())) {
			matchCriteria.and("CURRENCY_CODE").is(request.getCurrency());
		}

		if (StringUtils.isNotBlank(request.getEmailId())) {
			User user = userDao.findByEmailId(request.getEmailId());
			if(user.getUserType().equals(UserType.SUBUSER)){
				matchCriteria.and("PAY_ID").is(user.getParentPayId());
			}else{
				matchCriteria.and("PAY_ID").is(user.getPayId());
			}
		}
		Aggregation agg = Aggregation.newAggregation(Arrays.asList(Aggregation.match(matchCriteria),
				Aggregation.project("TXNTYPE", "STATUS", "TOTAL_AMOUNT", "CREATE_DATE", "MOP_TYPE", "PAYMENT_TYPE")));
		List<Map> list = mongoTemplate.aggregate(agg, "transactionStatus", Map.class).getMappedResults();
		PieChart pieChart = new PieChart();
		double totalSalesAmount = 0;
		double totalRefundAmount = 0;
		double totalCancelledAmount = 0;
		double totalFailedAmount = 0;
		double totalFraudAmount = 0;
		double totalSettledAmount = 0;
		for (Map doc : list) {
			String status = String.valueOf(doc.get("STATUS"));
			String amount = String.valueOf(doc.getOrDefault("TOTAL_AMOUNT", 0));
			amount = amount == "null" ? "0" : amount;
			double totalAmt = Double.parseDouble(amount);
			if (StringUtils.equalsAnyIgnoreCase((String) doc.get("TXNTYPE"), "SALE", "RECO")
					&& (StringUtils.equalsAnyIgnoreCase(status, "Captured", "RNS", "Settled", "Force Captured"))) {
				pieChart.setTotalSaleCount(pieChart.getTotalSaleCount() + 1);
				totalSalesAmount = totalSalesAmount + totalAmt;
			} else if (StringUtils.equalsIgnoreCase((String) doc.get("TXNTYPE"), "REFUND")
					&& StringUtils.equalsIgnoreCase(status, "Captured")) {
				pieChart.setTotalRefundCount(pieChart.getTotalRefundCount() + 1);
				totalRefundAmount = totalRefundAmount + totalAmt;
			} else if (StringUtils.equalsIgnoreCase(status, "Denied due to fraud")) {
				pieChart.setTotalFraudCount(pieChart.getTotalFraudCount() + 1);
				totalFraudAmount = totalFraudAmount + totalAmt;
			} else if (StringUtils.equalsAnyIgnoreCase(status,
					StatusType.getInternalStatusList("Failed").toArray(new String[0]))) {
				pieChart.setTotalFailedCount(pieChart.getTotalFailedCount() + 1);
				totalFailedAmount = totalFailedAmount + totalAmt;
			} else if (StringUtils.equalsAnyIgnoreCase(status,
					StatusType.getInternalStatusList("Cancelled").toArray(new String[0]))) {
				pieChart.setTotalCancelledCount(pieChart.getTotalCancelledCount() + 1);
				totalCancelledAmount = totalCancelledAmount + totalAmt;
			}

			if (StringUtils.equalsIgnoreCase(status, "Settled")) {
				pieChart.setTotalSettleCount(pieChart.getTotalSettleCount() + 1);
				totalSettledAmount = totalSettledAmount + totalAmt;
			}
		}
		pieChart.setTotalSaleAmount(String.valueOf((double)Math.round(totalSalesAmount*100)/100));
		pieChart.setTotalRefundAmount(String.valueOf((double)Math.round(totalRefundAmount*100)/100));
		pieChart.setTotalFraudAmount(String.valueOf((double)Math.round(totalFraudAmount*100)/100));
		pieChart.setTotalFailedAmount(String.valueOf((double)Math.round(totalFailedAmount*100)/100));
		pieChart.setTotalSettleAmount(String.valueOf((double)Math.round(totalSettledAmount*100)/100));
		pieChart.setTotalCancelledAmount(String.valueOf((double)Math.round(totalCancelledAmount*100)/100));
		return Optional.ofNullable(pieChart);
	}

	@Override
	public Optional<List<PieChart>> getPieChartDetails(DashBoardTxnCountRequest request) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		Criteria criteria = new Criteria();
		setMopType(request);
		if (StringUtils.isNotEmpty(request.getDateFrom()) && StringUtils.isNotEmpty(request.getDateTo())) {
			String dateFrom = request.getDateFrom() + " 00:00:00";
			String dateTo = request.getDateTo() + " 23:59:59";
			LocalDateTime fromLDate = LocalDateTime.parse(dateFrom, formatter1);
			LocalDateTime toLDate = LocalDateTime.parse(dateTo, formatter1);
			criteria = Criteria.where("CREATE_DATE").gte(formatter.format(fromLDate)).lte(formatter.format(toLDate));
		}
		if (StringUtils.isNotEmpty(request.getAcquirer()) && !request.getAcquirer().equalsIgnoreCase("ALL")) {
			criteria = criteria.and("ACQUIRER_TYPE").is(request.getAcquirer());
		}
		criteria.and("STATUS").is("Captured");
		if (StringUtils.isNotBlank(request.getTxnType())) {
			criteria.and("TXNTYPE").is(request.getTxnType());
		}
		if (StringUtils.isNotBlank(request.getPaymentType())) {
			criteria.and("PAYMENT_TYPE").is(request.getPaymentType());
		}
		if (StringUtils.isNotBlank(request.getCurrency())) {
			criteria.and("CURRENCY_CODE").is(request.getCurrency());
		}
		if (StringUtils.isNotBlank(request.getEmailId())) {
			User user = userDao.findByEmailId(request.getEmailId());
			if(user.getUserType().equals(UserType.SUBUSER)){
				criteria.and("PAY_ID").is(user.getParentPayId());
			}else{
				criteria.and("PAY_ID").is(user.getPayId());
			}
		}

		Aggregation agg = Aggregation.newAggregation(Aggregation.match(criteria),
				Aggregation.group("PAYMENT_TYPE", "MOP_TYPE").first("PAYMENT_TYPE").as("PAYMENT_TYPE").count()
						.as("totalCount").first("MOP_TYPE").as("MOP_TYPE").count().as("totalCount"),
				Aggregation.project("PAYMENT_TYPE", "MOP_TYPE", "totalCount")

		);
		logger.info("Aggregations: {}", agg);
		List<Map> list = mongoTemplate.aggregate(agg, "transaction", Map.class).getMappedResults();
		Map<String, PieChart> paymentMopWiseData = new HashMap<>();

		list.forEach(a -> {
			String paymentType = PaymentType.getpaymentName(a.get("PAYMENT_TYPE").toString());
			String mopType = MopType.getmopName(a.get("MOP_TYPE").toString());
			String key = StringUtils.isBlank(request.getMopType()) ? paymentType : mopType;
			if (StringUtils.isNotBlank(request.getMopType())
					&& !StringUtils.equalsAnyIgnoreCase(key, StringUtils.split(request.getMopType(), ","))) {
				key = "Others";
			}
			logger.info("Payment Type: {}", paymentType);
			logger.info("Mop Type: {}", mopType);
			logger.info("Key: {}", key);
			if (!paymentMopWiseData.containsKey(key)) {
				PieChart p = new PieChart();
				paymentMopWiseData.put(key, p);
			}
			PieChart p = paymentMopWiseData.get(key);
			logger.info("Payment Mop Wise Data : {}", paymentMopWiseData);

			p.setLabel(key);
			p.setCount(p.getCount() + Integer.parseInt(a.get("totalCount").toString()));
			logger.info("PieChart : {}", p);
			paymentMopWiseData.put(key, p);
		});
		logger.info("Payment Mop Wise Data : {}", paymentMopWiseData);

		return Optional.ofNullable(new ArrayList<>(paymentMopWiseData.values()));
	}

	private void setMopType(DashBoardTxnCountRequest request) {
		if (StringUtils.isBlank(request.getMopType())) {
			return;
		}
		List<String> mopTypes = new ArrayList<>();
		for (String mopCode : StringUtils.split(request.getMopType(), ",")) {
			mopTypes.add(MopType.getmopName(mopCode));
		}
		String mopStr = StringUtils.join(mopTypes);
		request.setMopType(StringUtils.substring(mopStr, 1, mopStr.length() - 1).trim());
	}

	@Override
	public Optional<List<HourlyChartData>> getHourlyChartData(DashBoardTxnCountRequest request) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		Criteria matchCriteria = new Criteria();
		LocalDateTime fromLDate = LocalDateTime.parse(request.getDateFrom(), formatter);
		LocalDateTime toLDate = LocalDateTime.parse(request.getDateTo(), formatter);
		matchCriteria = Criteria.where("CREATE_DATE").gte(formatter1.format(fromLDate)).lte(formatter1.format(toLDate));
		if (StringUtils.isNotBlank(request.getTxnType())) {
			matchCriteria.and("TXNTYPE").is(request.getTxnType());
		}
		if (StringUtils.isNotBlank(request.getPaymentType())) {
			matchCriteria.and("PAYMENT_TYPE").is(request.getPaymentType());
		}
		if (StringUtils.isNotBlank(request.getMopType())) {
			matchCriteria.and("MOP_TYPE").is(request.getMopType());
		}
		if (StringUtils.isNotBlank(request.getAcquirer())) {
			matchCriteria.and("ACQUIRER_TYPE").is(request.getAcquirer());
		}
		if (StringUtils.isNotBlank(request.getCurrency())) {
			matchCriteria.and("CURRENCY_CODE").is(request.getCurrency());
		}

		if (StringUtils.isNotBlank(request.getEmailId())) {
			User user = userDao.findByEmailId(request.getEmailId());
			if(user.getUserType().equals(UserType.SUBUSER)){
				matchCriteria.and("PAY_ID").is(user.getParentPayId());
			}else{
				matchCriteria.and("PAY_ID").is(user.getPayId());
			}
		}
		matchCriteria.and("STATUS").ne("RNS");
		Aggregation agg = Aggregation.newAggregation(Arrays.asList(Aggregation.match(matchCriteria),
				Aggregation.project("STATUS", "CREATE_DATE", "TXNTYPE")));
		List<Map> list = mongoTemplate.aggregate(agg, "transactionStatus", Map.class).getMappedResults();
		DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("HH:mm:ss");
		DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("HH");

		Map<String, List<String>> statusWiseTime = new HashMap<>();
		list.forEach(hourlyData -> {
			String time = hourlyData.get("CREATE_DATE").toString().split(" ")[1];
			time = formatter4.format(formatter3.parse(time));
			String status = hourlyData.get("STATUS").toString();
			String txnType = hourlyData.get("TXNTYPE").toString();
			status = StringUtils.equalsIgnoreCase(txnType, "REFUND") ? "REFUND"
					: resolveStatus(status);
			status = StringUtils.equalsAnyIgnoreCase(status, "Pending", "Invalid") ? "Failed" : status;
			if (!statusWiseTime.containsKey(status)) {
				statusWiseTime.put(status, new ArrayList<>());
			}
			statusWiseTime.get(status).add(time);
		});

		List<HourlyChartData> hourlyDetails = new ArrayList<>();
		for (int i = 0; i < 24; i++) {
			String timeSlots = "";
			if (i < 10) {
				timeSlots = "0" + i;
			} else {
				timeSlots = String.valueOf(i);
			}

			HourlyChartData hourly = new HourlyChartData();
			hourly.setTxnDate(timeSlots);
			List<String> refundTimeSlots = statusWiseTime.get("REFUND");
			List<String> successTimeSlots = statusWiseTime.get("Captured");
			List<String> FailedTimeSlots = statusWiseTime.get("Failed");
			List<String> cancelledTimeSlots = statusWiseTime.get("Cancelled");
			if (CollectionUtils.isNotEmpty(refundTimeSlots)) {
				for (String refundTime : refundTimeSlots) {
					if (StringUtils.equalsIgnoreCase(refundTime, timeSlots)) {
						hourly.setTotalRefund(hourly.getTotalRefund() + 1);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(successTimeSlots)) {
				for (String successTime : successTimeSlots) {
					if (StringUtils.equalsIgnoreCase(successTime, timeSlots)) {
						hourly.setTotalSuccess(hourly.getTotalSuccess() + 1);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(FailedTimeSlots)) {
				for (String failedTime : FailedTimeSlots) {
					if (StringUtils.equalsIgnoreCase(failedTime, timeSlots)) {
						hourly.setTotalFailed(hourly.getTotalFailed() + 1);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(cancelledTimeSlots)) {
				for (String cancelledTime : cancelledTimeSlots) {
					if (StringUtils.equalsIgnoreCase(cancelledTime, timeSlots)) {
						hourly.setTotalCancelled(hourly.getTotalCancelled() + 1);
					}
				}
			}
			hourlyDetails.add(hourly);
		}
		return Optional.ofNullable(hourlyDetails);
	}


	public String resolveStatus(String status) {
		if (StringUtils.equalsAnyIgnoreCase(status,StatusType.CAPTURED.getName(), StatusType.SETTLED_RECONCILLED.getName(), StatusType.SETTLED_SETTLE.getName(), StatusType.FORCE_CAPTURED.getName())) {
			return StatusType.CAPTURED.getName();
		} else {
			StatusType statusType = StatusType.getInstanceFromName(status.toUpperCase());
			if(statusType!=null) {
				return statusType.getUiName();
			}else {
				return StatusType.FAILED.getUiName();
			}
		}
	}

	@Override
	public Optional<List<SettlementChartData>> getSettlementChartData(DashBoardTxnCountRequest request) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");

		List<String> dateArr = new ArrayList<>();
		Criteria matchCriteria = new Criteria();

		String dateFrom = request.getDateFrom() + " 00:00:00";
		String dateTo = request.getDateTo() + " 23:59:59";
		LocalDateTime fromLDate = LocalDateTime.parse(dateFrom, formatter);
		LocalDateTime startDate = LocalDateTime.parse(dateFrom, formatter);
		LocalDateTime toLDate = LocalDateTime.parse(dateTo, formatter);

		while (!startDate.isAfter(toLDate)) {
			dateArr.add(formatter2.format(startDate));
			startDate = startDate.plusDays(1);
		}
		matchCriteria = Criteria.where("CREATE_DATE").gte(formatter.format(fromLDate)).lte(formatter.format(toLDate));
//		if (StringUtils.isBlank(request.getTxnType())) {
//			request.setTxnType("SALE");
//		}
//		matchCriteria.and("TXNTYPE").is(request.getTxnType());
		if (StringUtils.isNotBlank(request.getPaymentType())) {
			matchCriteria.and("PAYMENT_TYPE").is(request.getPaymentType());
		}
		if (StringUtils.isNotBlank(request.getMopType())) {
			matchCriteria.and("MOP_TYPE").is(request.getMopType());
		}
		if (StringUtils.isNotBlank(request.getAcquirer())) {
			matchCriteria.and("ACQUIRER_TYPE").is(request.getAcquirer());
		}
		if (StringUtils.isNotBlank(request.getCurrency())) {
			matchCriteria.and("CURRENCY_CODE").is(request.getCurrency());
		}

		if (StringUtils.isNotBlank(request.getEmailId())) {
			User user = userDao.findByEmailId(request.getEmailId());
			if(user.getUserType().equals(UserType.SUBUSER)){
				matchCriteria.and("PAY_ID").is(user.getParentPayId());
			}else{
				matchCriteria.and("PAY_ID").is(user.getPayId());
			}
		}
		matchCriteria.and("STATUS").in(StatusType.CAPTURED.getName(), StatusType.SETTLED_SETTLE.getName());
		Aggregation agg = Aggregation.newAggregation(Aggregation.match(matchCriteria),
				Aggregation.group("DATE_INDEX", "STATUS").first("DATE_INDEX").as("txnDate").first("STATUS").as("STATUS")
						.sum(ConvertOperators.ToDouble.toDouble("$TOTAL_AMOUNT")).as("totalAmount"),
				Aggregation.project("DATE_INDEX", "STATUS", "totalAmount")

		);
		logger.info("Aggregations : {}", agg);
		List<Map> list = mongoTemplate.aggregate(agg, "transaction", Map.class).getMappedResults();
		Map<String, SettlementChartData> dateWiseData = new HashMap<>();
		list.forEach(a -> {
			String date = a.get("DATE_INDEX").toString();
			if (!dateWiseData.containsKey(date)) {
				SettlementChartData p = new SettlementChartData();
				String year = date.substring(0, 4);
				String month = date.substring(4, 6);
				String day = date.substring(6, 8);
				String txnDate = StringUtils.join(day, "-", month, "-", year);
				p.setTxnDate(txnDate);
				dateWiseData.put(date, p);
			}
			boolean settled = StringUtils.equalsIgnoreCase(a.get("STATUS").toString(),
					StatusType.SETTLED_SETTLE.getName());
			double amount = Double.valueOf(a.get("totalAmount").toString());
			SettlementChartData p = dateWiseData.get(date);
			if (settled) {
				p.setTotalSettledAmount((double)Math.round((p.getTotalSettledAmount() + amount)*100)/100);
			} else {
				p.setTotalCapturedAmount((double)Math.round((p.getTotalCapturedAmount() + amount)*100)/100);
			}
			dateWiseData.put(a.get("DATE_INDEX").toString(), p);
		});
		Comparator<SettlementChartData> comp = (SettlementChartData a, SettlementChartData b) -> {
			if (a.getTxnDate().compareTo(b.getTxnDate()) > 0) {
				return -1;
			} else if (a.getTxnDate().compareTo(b.getTxnDate()) < 0) {
				return 1;
			} else {
				return 0;
			}
		};
		List<SettlementChartData> settlementDetails = new ArrayList<>(dateWiseData.values());
		Collections.sort(settlementDetails, comp);
		return Optional.ofNullable(settlementDetails);
	}


	// Added By Sweety
		@Override
		public Optional<List<LineChartRespDto>> getFraudLineChart(String dateFrom, String dateTo, String acquirer,
				String mopType, String paymentType, String status, String statusType, LineChartType chartType,
				String payId) {

			List<LineChartRespDto> list;
			if (chartType.equals(LineChartType.STATUS_CHART)) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
				// DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM/dd/yyyy
				// HH:mm:ss");
				List<String> dateArr = new ArrayList<>();
				Criteria matchCriteria1 = new Criteria();

				if (StringUtils.isNotEmpty(dateFrom) && StringUtils.isNotEmpty(dateTo)) {
					dateFrom = dateFrom + " 00:00:00";
					dateTo = dateTo + " 23:59:59";
					LocalDateTime fromLDate = LocalDateTime.parse(dateFrom, formatter);
					LocalDateTime startDate = LocalDateTime.parse(dateFrom, formatter);
					LocalDateTime toLDate = LocalDateTime.parse(dateTo, formatter);

					while (!startDate.isAfter(toLDate)) {
						dateArr.add(formatter2.format(startDate));
						startDate = startDate.plusDays(1);
					}
					matchCriteria1.and("CREATE_DATE").gte(formatter.format(fromLDate)).lte(formatter.format(toLDate));
				}

//				matchCriteria1.and("TXNTYPE").is("SALE");
				MatchOperation matchOperation1 = Aggregation.match(matchCriteria1);

				GroupOperation facetGroupOperation1 = Aggregation.group("DATE_INDEX", "PG_REF_NUM");

				ProjectionOperation facetProjectionOperation1 = Aggregation.project().andExpression("_id.DATE_INDEX")
						.as("DATE_INDEX").andExpression("_id.PG_REF_NUM").as("PG_REF_NUM").andExclude("_id");

				GroupOperation facetGroupOperation2 = Aggregation.group("DATE_INDEX").count().as("TOTAL_COUNT");

				ProjectionOperation facetProjectionOperation2 = Aggregation.project("TOTAL_COUNT").andExpression("_id")
						.as("DATE_INDEX").andExclude("_id");

				GroupOperation facetGroupOperation3 = Aggregation.group("DATE_INDEX", "STATUS").count()
						.as("TOTAL_FIELD_COUNT");

				ProjectionOperation facetProjectionOperation3 = Aggregation.project("TOTAL_FIELD_COUNT")
						.andExpression("_id.DATE_INDEX").as("DATE_INDEX").andExpression("_id.STATUS").as("STATUS")
						.andExclude("_id");

				FacetOperation facetOperation = Aggregation
						.facet(facetGroupOperation1, facetProjectionOperation1, facetGroupOperation2,
								facetProjectionOperation2)
						.as("FCOUNT").and(facetGroupOperation3, facetProjectionOperation3).as("FSTATUS");

				UnwindOperation unwindOperation1 = Aggregation.unwind("FCOUNT");

				AggregationOperation projectionOperation1 = new AggregationOperation() {

					@Override
					public Document toDocument(AggregationOperationContext context) {
						// TODO Auto-generated method stub
						return new Document("$project",
								new Document("DATE_INDEX", "$FCOUNT.DATE_INDEX")
										.append("TOTAL_COUNT", "$FCOUNT.TOTAL_COUNT")
										.append("FSTATUS", new Document("$filter", new Document("input", "$FSTATUS")
												.append("as", "this").append("cond", new Document("$eq",
														Arrays.asList("$FCOUNT.DATE_INDEX", "$$this.DATE_INDEX"))))));
					}
				};

				UnwindOperation unwindOperation2 = Aggregation.unwind("FSTATUS");

				AggregationOperation projectionOperation2 = new AggregationOperation() {

					@Override
					public Document toDocument(AggregationOperationContext context) {
						return new Document("$project",
								new Document("DATE_INDEX", 1).append("TOTAL_COUNT", 1)
										.append("TOTAL_FIELD_COUNT", "$FSTATUS.TOTAL_FIELD_COUNT")
										.append("STATUS", "$FSTATUS.STATUS"));
					}

				};

				Criteria matchCriteria2 = new Criteria();

				if ((!StringUtils.isEmpty(status) && !status.equalsIgnoreCase("ALL"))) {
					matchCriteria2.and(chartType.getField()).in(getDbStatus(status));
				}

				MatchOperation matchOperation2 = Aggregation.match(matchCriteria2);

				AggregationOperation projectionOperation3 = new AggregationOperation() {

					@Override
					public Document toDocument(AggregationOperationContext context) {
						return new Document("$project",
								new Document("DATE_INDEX", 1).append("TOTAL_COUNT", 1).append("TOTAL_FIELD_COUNT", 1)
										.append("STATUS", 1).append("FIELD_MULTI",
												new Document("$multiply", Arrays.asList("$TOTAL_FIELD_COUNT", 100))));
					}
				};

				AggregationOperation projectionOperation4 = new AggregationOperation() {

					@Override
					public Document toDocument(AggregationOperationContext context) {
						return new Document("$project",
								new Document("DATE_INDEX", 1).append("TOTAL_COUNT", 1).append("TOTAL_FIELD_COUNT", 1)
										.append("STATUS", 1).append("PERCENTAGE",
												new Document("$divide", Arrays.asList("$FIELD_MULTI", "$TOTAL_COUNT"))));
					}
				};

				AggregationOperation sortOperation1 = new AggregationOperation() {

					@Override
					public Document toDocument(AggregationOperationContext context) {
						return new Document("$sort", new Document("DATE_INDEX", 1));
					}
				};

				AggregationOperation groupOperation1 = new AggregationOperation() {

					@Override
					public Document toDocument(AggregationOperationContext context) {
						return new Document("$group",
								new Document("_id", "$STATUS")
										.append("data",
												new Document("$push",
														new Document("date", "$DATE_INDEX")
																.append("percentage",
																		new Document("$round",
																				Arrays.asList("$PERCENTAGE", 0)))
																.append("totalFieldCount", "$TOTAL_FIELD_COUNT")
																.append("totalCount", "$TOTAL_COUNT"))));
					}
				};

				AggregationOperation projectionOperation5 = new AggregationOperation() {

					@Override
					public Document toDocument(AggregationOperationContext context) {
						return new Document("$project", new Document("data", 1).append("label", "$_id").append("_id", 0));
					}
				};

				AggregationOperation sortOperation2 = new AggregationOperation() {

					@Override
					public Document toDocument(AggregationOperationContext context) {
						return new Document("$sort", new Document("label", 1));
					}
				};

				AggregationOperation addFieldsOperation = new AggregationOperation() {

					@Override
					public Document toDocument(AggregationOperationContext context) {
						// TODO Auto-generated method stub
						return new Document("$addFields", new Document("dateArray", dateArr));
					}
				};

				Aggregation agg = Aggregation.newAggregation(Arrays.asList(matchOperation1, facetOperation,
						unwindOperation1, projectionOperation1, unwindOperation2, projectionOperation2, matchOperation2,
						projectionOperation3, projectionOperation4, sortOperation1, groupOperation1, projectionOperation5,
						sortOperation2, addFieldsOperation));
				System.out.println("Aggregation Query : " + agg.toString());
				list = mongoTemplate.aggregate(agg, "transactionStatus", LineChartRespDto.class).getMappedResults();
			} else {
				list = getFraudLineChartFilter(dateFrom, dateTo, acquirer, mopType, paymentType, status, statusType,
						chartType, payId);
			}

			return Optional.ofNullable(list);

		}
		
		private List<LineChartRespDto> getFraudLineChartFilter(String dateFrom, String dateTo, String acquirer,
				String mopType, String paymentType, String status, String statusType, LineChartType chartType,
				String payId) {

			User user = userDao.findByPayId(payId);
			if(user.getUserType().equals(UserType.SUBUSER)){
				payId = user.getParentPayId();
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
			List<String> dateArr = new ArrayList<>();
			Criteria matchCriteria1 = new Criteria();

			if (StringUtils.isNotEmpty(dateFrom) && StringUtils.isNotEmpty(dateTo)) {
				dateFrom = dateFrom + " 00:00:00";
				dateTo = dateTo + " 23:59:59";
				LocalDateTime fromLDate = LocalDateTime.parse(dateFrom, formatter);
				LocalDateTime startDate = LocalDateTime.parse(dateFrom, formatter);
				LocalDateTime toLDate = LocalDateTime.parse(dateTo, formatter);

				while (!startDate.isAfter(toLDate)) {
					dateArr.add(formatter2.format(startDate));
					startDate = startDate.plusDays(1);
				}
				matchCriteria1.and("CREATE_DATE").gte(formatter.format(fromLDate)).lte(formatter.format(toLDate));
			}
			matchCriteria1.and("TXNTYPE").is("SALE");
			if (chartType.equals(LineChartType.ACQUIRER_CHART)
					&& (StringUtils.isNotEmpty(acquirer) && !acquirer.equalsIgnoreCase("ALL"))) {
				matchCriteria1.and(chartType.getField()).is(acquirer);
			} else if (chartType.equals(LineChartType.PAYMENT_TYPE_CHART)
					&& (!StringUtils.isEmpty(paymentType) && !paymentType.equalsIgnoreCase("ALL"))) {
				matchCriteria1.and(chartType.getField()).is(paymentType);
			} else if (chartType.equals(LineChartType.MOP_TYPE_CHART)
					&& (!StringUtils.isEmpty(mopType) && !mopType.equalsIgnoreCase("ALL"))) {
				matchCriteria1.and(chartType.getField()).is(mopType);
			}

			MatchOperation matchOperation1 = Aggregation.match(matchCriteria1);

			GroupOperation groupOperation1 = Aggregation.group("DATE_INDEX", chartType.getField()).push("PAY_ID")
					.as("PAY_ID").count().as("TOTAL_COUNT");

			UnwindOperation unwindOperation = Aggregation.unwind("PAY_ID");

			Criteria matchCriteria2 = new Criteria();

			if (!StringUtils.isEmpty(payId))

				matchCriteria2.and("PAY_ID").is(payId);
			MatchOperation matchOperation2 = Aggregation.match(matchCriteria2);

			ProjectionOperation projectionOperation1 = Aggregation.project("TOTAL_COUNT").andExpression("_id.DATE_INDEX")
					.as("DATE_INDEX").andExpression("_id." + chartType.getField()).as(chartType.getField());

			GroupOperation groupOperation2 = Aggregation.group(chartType.getField(), "DATE_INDEX").count()
					.as("TOTAL_FIELD_COUNT").first("TOTAL_COUNT").as("TOTAL_COUNT");

			ProjectionOperation projectionOperation2 = Aggregation.project().andInclude("TOTAL_COUNT", "TOTAL_FIELD_COUNT")
					.andExpression("_id.DATE_INDEX").as("DATE_INDEX").andExpression("_id." + chartType.getField())
					.as(chartType.getField()).andExpression("_id." + chartType.getField()).as(chartType.getField())
					.and("TOTAL_FIELD_COUNT").multiply(100).as("FIELD_MULTI").andExclude("_id");

			ProjectionOperation projectionOperation3 = Aggregation.project().and("FIELD_MULTI").divide("TOTAL_COUNT")
					.as("PERCENTAGE").andInclude("DATE_INDEX", chartType.getField(), "TOTAL_COUNT", "TOTAL_FIELD_COUNT");

			SortOperation sortOperation1 = Aggregation.sort(Sort.Direction.ASC, "DATE_INDEX");

			GroupOperation groupOperation3 = Aggregation.group(chartType.getField())
					.push(new BasicDBObject("date", "$DATE_INDEX")
							.append("percentage", new BasicDBObject("$round", Arrays.asList("$PERCENTAGE", 0)))
							.append("totalFieldCount", "$TOTAL_FIELD_COUNT").append("totalCount", "$TOTAL_COUNT"))
					.as("data");

			ProjectionOperation projectionOperation4 = Aggregation.project().andInclude("data").and("_id").as("label")
					.andExclude("_id");

			SortOperation sortOperation2 = Aggregation.sort(Sort.Direction.ASC, "label");

			AggregationOperation addFieldsOperation = new AggregationOperation() {

				@Override
				public Document toDocument(AggregationOperationContext context) {
					return new Document("$addFields", new Document("dateArray", dateArr));
				}
			};

			Aggregation agg = Aggregation.newAggregation(Arrays.asList(matchOperation1, groupOperation1, unwindOperation,
					matchOperation2, projectionOperation1, groupOperation2, projectionOperation2, projectionOperation3,
					sortOperation1, groupOperation3, projectionOperation4, sortOperation2, addFieldsOperation));
			System.out.println("Aggregation Query : " + agg.toString());
			List<LineChartRespDto> list = mongoTemplate.aggregate(agg, "transactionStatus", LineChartRespDto.class)
					.getMappedResults();
			return list;
		}

}
