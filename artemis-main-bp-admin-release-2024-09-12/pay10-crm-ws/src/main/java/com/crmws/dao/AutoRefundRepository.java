package com.crmws.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.crmws.dto.AutorefundDto;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

@Repository
public class AutoRefundRepository extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(AutoRefundRepository.class.getName());

	@Autowired
	UserDao userdao;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	JdbcTemplate jdbcTamplate;

	public AutorefundDto GetMerchantPayidByPgRefNo(String pgrefNo) {
		AutorefundDto autorefundD = new AutorefundDto();

		Criteria criteria = new Criteria();
		if (StringUtils.isNotEmpty(pgrefNo) && !pgrefNo.equalsIgnoreCase("")) {
			criteria = Criteria.where("PG_REF_NUM").is(pgrefNo);
		}
		criteria.and("STATUS").is("Sent to Bank");

		Aggregation agg = Aggregation.newAggregation(
				Arrays.asList(Aggregation.match(criteria), Aggregation.project("PAY_ID", "CREATE_DATE")));
		logger.info("Query for Payid from Pgrefno ={} ", agg.toString());
		List<Map> list = mongoTemplate.aggregate(agg, "transaction", Map.class).getMappedResults();
		logger.info("size for list" + list.size());
		list.stream().map(a -> {

			autorefundD.setPayid(a.get("PAY_ID").toString());

			autorefundD.setCreateDate(a.get("CREATE_DATE").toString());
			System.out.println("Merchantpayid from mongobd by pgrefno  " + autorefundD.toString());
			return autorefundD;
		}).collect(Collectors.toList());
		return autorefundD;
	}

	boolean flag = false;

	@SuppressWarnings("unchecked")
	public boolean getRefundStatus(String payId, String createDate) {
		flag = false;
		// String sql = "SELECT * FROM User where userStatus='ACTIVE' and payId=" +
		// payId + "";
		// List<User> user = jdbcTamplate.query(sql,
		// BeanPropertyRowMapper.newInstance(User.class));
		User user =  userdao.findByPayId(payId);
		logger.info("User date by payid={}", user.toString());

		
			if (user.isAutoFlag()) {
				String diffdate = user.getAutoMin();
				String[] minAndHrs = diffdate.split(":");

				int Hrs = Integer.parseInt(minAndHrs[0]); // 012
				int Min = Integer.parseInt(minAndHrs[1]);
				logger.info(diffdate);

				try {
					String addedDate = adddate(createDate, Hrs, Min);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date();
					Date date1 = formatter.parse(formatter.format(date));
					Date date2 = formatter.parse(addedDate);
					logger.info("date and time ={} ={}", date1, date2);
					if ((date2.compareTo(date1) < 0) || (date1.equals(date2))) {
						flag = true;
						System.out.println("refund can be intaited ");
					} else {
						flag = false;
						System.out.println("refundcannot be intiated ");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				flag = false;
			}
	
		logger.info("******************" + flag);
		return flag;
	}

	public List<Map> refundintiated(String pgRef) {
		Criteria criteria = new Criteria();
		if (StringUtils.isNotEmpty(pgRef) && !pgRef.equalsIgnoreCase("")) {
			criteria = Criteria.where("PG_REF_NUM").is(pgRef);
		}
		criteria.and("STATUS").is("Captured");
		Aggregation agg = Aggregation.newAggregation(Arrays.asList(Aggregation.match(criteria),
				Aggregation.project("PAY_ID", "PG_REF_NUM", "ORDER_ID", "CURRENCY_CODE", "AMOUNT")));
		logger.info("Query for Payid from Pgrefno ={} ", agg.toString());
		return mongoTemplate.aggregate(agg, "transaction", Map.class).getMappedResults();
	}

	String adddate(String date, int Hrs, int min) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info("hrs and min ={},={}", min, Hrs);
		Date d = df.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MINUTE, min);
		cal.add(Calendar.HOUR, Hrs);

		String newTime = df.format(cal.getTime());

		return newTime;
	}
}
