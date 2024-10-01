package com.crmws.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.crmws.controller.AcquirerSuccessRatioController;
import com.crmws.dto.AcquirerSuccessRationDto;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.util.StatusType;

@Repository

public class SuccessRatioDao extends HibernateAbstractDao {
	private static final Logger logger = LoggerFactory.getLogger(SuccessRatioDao.class.getName());

	@Autowired
	MongoTemplate mongoTemplate;

	public String getSuccessRationData(String fromDate, String todate, String Acquirer) throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date date2 = new Date();
		Date date1 = formatter.parse(fromDate);

		if (date1.compareTo(date2) == 1) {
			return "-10.00";
		}

		AcquirerSuccessRationDto data = new AcquirerSuccessRationDto();
		Criteria Paymentc = new Criteria();

		Criteria txnType = Criteria.where("TXNTYPE").is("SALE");

		Criteria date = Criteria.where("CREATE_DATE").gte(fromDate).lte(todate);
		Criteria status = Criteria.where("STATUS").nin("RNS", "Force Captured", "Settled");
		Criteria query = new Criteria();
		if (StringUtils.isNoneBlank(Acquirer)) {
			Criteria Acquirerc = Criteria.where("ACQUIRER_TYPE").is(Acquirer);

			query = new Criteria().andOperator(Acquirerc, txnType, status, date);
		} else {
			query = new Criteria().andOperator(txnType, status, date);

		}

		AggregationOperation match = Aggregation.match(query);
		AggregationOperation group = Aggregation.group("STATUS").count().as("total");

		Aggregation agg = Aggregation.newAggregation(match, match, group);
		int capturedlist = 0;
		int totallist = 0;

		logger.info("Aggregation = " + agg);
		List<Map> list = mongoTemplate.aggregate(agg, "transactionStatus", Map.class).getMappedResults();
		logger.info("TOTAL DATAT" + new Gson().toJson(list));
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				if (((String) map.get("_id")).equalsIgnoreCase(StatusType.CAPTURED.getName())) {
					capturedlist = Integer.valueOf(map.get("total").toString());

				}

				totallist = totallist + Integer.valueOf(map.get("total").toString());
			}

		}

		if (totallist > 0 && capturedlist > 0) {
			float data1 = ((float) capturedlist / (float) totallist) * 100;

			return String.format("%.2f", data1);
		} else {
			return "0.00";
		}

	}

}
