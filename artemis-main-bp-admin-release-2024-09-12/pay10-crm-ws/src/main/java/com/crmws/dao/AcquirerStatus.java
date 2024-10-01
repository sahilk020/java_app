package com.crmws.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.crmws.dto.AcquirerStatusDto;
import com.crmws.entity.BulkRefundEntity;
import com.google.gson.Gson;
import com.mongodb.client.model.Aggregates;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.user.AcquirerDowntimeScheduling;
import com.pay10.commons.util.FieldType;

@Repository
public class AcquirerStatus extends HibernateAbstractDao {
	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	RouterConfigurationDao routerConfigurationDao;

	private static final Logger logger = LoggerFactory.getLogger(AcquirerStatus.class.getName());

	public List<AcquirerStatusDto> getAcquirerStatusDtos(String acquirer, String payment) {

		Query qry = null;

		Criteria Paymentc = new Criteria();
		Criteria Acquirerc = Criteria.where("ACQUIRER_TYPE").is(acquirer);
		List<AcquirerStatusDto> acquirerStatusDto=	new ArrayList<AcquirerStatusDto>();
		Criteria statusSuccess = Criteria.where("STATUS").is("Captured");
		Criteria tnxRYpe = Criteria.where("TXNTYPE").is("SALE");
		if (payment.equalsIgnoreCase("ALL")) {
			List<String> data=routerConfigurationDao.getpaymnetlistbyAcquirer(acquirer);
			for(String datat1:data) {
				

				AcquirerStatusDto listDat=new AcquirerStatusDto();
			 Paymentc = Criteria.where("PAYMENT_TYPE").is(datat1);

			
			
		


			Criteria query = new Criteria().andOperator(Acquirerc, Paymentc);
			Criteria querysuccess = new Criteria().andOperator(Acquirerc, Paymentc,statusSuccess,tnxRYpe);
			AggregationOperation matchsucess = Aggregation.match(querysuccess);

			AggregationOperation match = Aggregation.match(query);
			AggregationOperation sort = Aggregation.sort(Sort.Direction.DESC, "UPDATE_DATE");
			AggregationOperation limit = Aggregation.limit(1);
			logger.info("Inside Bulkrefund (bulkRefundEntity):={} ", query);
			logger.info("query for bulk search={}", qry);
			Aggregation aggsuccess = Aggregation.newAggregation(matchsucess, sort, limit);

			Aggregation agg = Aggregation.newAggregation(match, sort, limit);
			System.out.println("Aggregation = " + agg);
			List<Map> listsuccess = mongoTemplate.aggregate(aggsuccess, "transaction", Map.class).getMappedResults();

			List<Map> list = mongoTemplate.aggregate(agg, "transaction", Map.class).getMappedResults();
			if(list.size()>0) {
			if(list.size()>0){
				listDat.setLastTxnTime(list.get(0).get("UPDATE_DATE").toString());
				}else {
				listDat.setLastTxnTime("NA");
			}
			
			if(listsuccess.size()>0){
				listDat.setLastSuccessTime(listsuccess.get(0).get("UPDATE_DATE").toString());
				}else {
				listDat.setLastSuccessTime("NA");
			}
			listDat.setAcquirer(acquirer);
			listDat.setPaymentType(list.get(0).get("PAYMENT_TYPE").toString());
			listDat.setStatus("NA");
			
			
			acquirerStatusDto.add(listDat);
			}
				
				
				
				
				
			}
			
			Paymentc = Criteria.where("PAYMENT_TYPE").in(routerConfigurationDao.getpaymnetlistbyAcquirer(acquirer));
			
  		} else {
		
		
			AcquirerStatusDto listDat=new AcquirerStatusDto();
		 Paymentc = Criteria.where("PAYMENT_TYPE").is(payment);

		
		
	


		Criteria query = new Criteria().andOperator(Acquirerc, Paymentc);
		Criteria querysuccess = new Criteria().andOperator(Acquirerc, Paymentc,statusSuccess,tnxRYpe);
		AggregationOperation matchsucess = Aggregation.match(querysuccess);

		AggregationOperation match = Aggregation.match(query);
		AggregationOperation sort = Aggregation.sort(Sort.Direction.DESC, "UPDATE_DATE");
		AggregationOperation limit = Aggregation.limit(1);
		logger.info("Inside Bulkrefund (bulkRefundEntity):={} ", query);
		logger.info("query for bulk search={}", qry);
		Aggregation aggsuccess = Aggregation.newAggregation(matchsucess, sort, limit);

		Aggregation agg = Aggregation.newAggregation(match, sort, limit);
		System.out.println("Aggregation = " + agg);
		List<Map> listsuccess = mongoTemplate.aggregate(aggsuccess, "transaction", Map.class).getMappedResults();

		List<Map> list = mongoTemplate.aggregate(agg, "transaction", Map.class).getMappedResults();
		
		if(list!=null&& list.size()>0){
			listDat.setLastTxnTime(list.get(0).get("UPDATE_DATE").toString());
			}else {
			listDat.setLastTxnTime("NA");
		}
		
		if(listsuccess!=null&&listsuccess.size()>0){
			listDat.setLastSuccessTime(listsuccess.get(0).get("UPDATE_DATE").toString());
			}else {
			listDat.setLastSuccessTime("NA");
		}
		listDat.setAcquirer(acquirer);
		listDat.setPaymentType(payment);
		listDat.setStatus("NA");
		
		
		acquirerStatusDto.add(listDat);
		
		}

		return acquirerStatusDto;
	}

}
