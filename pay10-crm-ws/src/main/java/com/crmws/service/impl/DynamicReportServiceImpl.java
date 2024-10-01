package com.crmws.service.impl;

//import com.crmws.dto.DynamicReportKey;

import com.crmws.service.DynamicReportService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DynamicReportServiceImpl implements DynamicReportService {
	private static final Logger logger = LoggerFactory.getLogger(DynamicReportServiceImpl.class.getName());
	public static String COLLECTION_NAME = "collection_name";
	public static String PROJECTIONS = "projections";
	public static String FILTER_COLUMNS = "filter_columns";
	public static String FILTER_COLUMNS_VALUE = "filter_columns_value";
	public static String IS_GROUP_BY = "is_group_by";
	public static String GROUPED_COLUMNS = "grouped_columns";
	public static String IS_SORTING = "is_sorting";
	public static String SORTED_BY = "sorted_by";
	public static String IS_SORTING_ASC = "is_sorting_asc";
	public static String IS_DYNAMIC_FILTER = "is_dynamic_filter";
	public static String DATE_FILTER_COLUMN = "date_filter_column";
	public static String FROM_DATE = "from_date";
	public static String TO_DATE = "to_date";
	public static String GROUP_COLUMNS_SUM = "group_columns_sum";
	public static String IS_COUNT = "is_count";

	public static String REPORT_COL_HEADERS = "report_headers";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private MongoTemplate mongoTemplate;

	private List<Map<String, String>> getReportConfigs(String reportId) throws Exception {
		List<Map<String, String>> list = new ArrayList<>();
		DataSource ds = jdbcTemplate.getDataSource();
		Connection con = ds.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM report_master where id=" + reportId);
		while (rs.next()) {
			Map<String, String> map = new HashMap<>();
			map.put("id", "" + rs.getString("id"));
			map.put("collection_name", rs.getString("collection_name"));
			map.put("projections", rs.getString("projections"));
			map.put("filter_columns", rs.getString("filter_columns"));
			map.put("filter_columns_value", rs.getString("filter_columns_value"));
			map.put("is_group_by", "" + rs.getString("is_group_by"));
			map.put("grouped_columns", rs.getString("grouped_columns"));
			map.put("is_sorting", "" + rs.getString("is_sorting"));
			map.put("sorted_by", rs.getString("sorted_by"));
			map.put("is_sorting_asc", "" + rs.getString("is_sorting_asc"));
			map.put("is_dynamic_filter", "" + rs.getString("is_dynamic_filter"));
			map.put("date_filter_column", "" + rs.getString("date_filter_column"));
			map.put("from_date", rs.getString("from_date"));
			map.put("to_date", "" + rs.getString("to_date"));
			map.put("is_count", "" + rs.getString("is_count"));
			map.put("group_columns_sum", rs.getString("group_columns_sum"));
			map.put("created_on", "" + rs.getString("created_on"));
			map.put("is_mongodb", "" + rs.getInt("is_mongodb"));
			map.put(REPORT_COL_HEADERS, rs.getString("report_headers"));
			list.add(map);

		}
		stmt.close();
		con.close();

		return list;
	}

//    private List<Map> mergeListOld(List<Map> list1,String keys){
//
//        //key.getKeyMap().put()
//        System.out.println("list1 size: "+list1);
//        Map<DynamicReportKey,Map<String,String>> entries = new HashMap<>();
//        String[] keyArray = StringUtils.split(keys,",");
//        for(Map map : list1){
//            DynamicReportKey key = new DynamicReportKey();
//            for(String a: keyArray) {
//                key.getKeyMap().put(a, map.get(a).toString());
//                System.out.println("key : "+a+", value: "+map.get(a).toString());
//            }
//
//            Map<String,String> existing = entries.get(key);
//            if(existing == null){
//                System.out.println("-----------1");
//                existing = new HashMap<>();
//                entries.put(key,existing);
//            }
//            for(Object k : map.keySet()){
//                existing.put(k.toString(),map.get(k).toString());
//            }
//        }
//        System.out.println("entries size : "+entries.size());
//        List<Map> result = entries.values().stream().map(a -> {
//            Map<String,String> m = new HashMap<>();
//            a.entrySet().stream().forEach(b-> m.put(b.getKey(),a.get(b.getKey()).toString()) );
//            return m;
//        }).collect(Collectors.toList());
//        return result;
//    }
	private List<Map> fetchReport(Map<String, String> dynamicFilterValueMap, Map<String, String> configs) {

		if (configs != null) {
			String isMongoDB = configs.get("is_mongodb");
			// Below Condition can be converted into Interfaces : by Radhe
			return getMongoDBReportData(dynamicFilterValueMap, configs);
//            if("1".equalsIgnoreCase(isMongoDB)){
//                return getMongoDBReportData(dynamicFilterValueMap,configs);
//            }else if("0".equalsIgnoreCase(isMongoDB)){
//                return getMySqlReportData(dynamicFilterValueMap,configs);
//            }else
//            {
//                throw new RuntimeException("Not supported datasource");
//            }
		}
		return new ArrayList<>();
	}

	private List<Map> merge(List<Map> list1, List<Map> list2, String keys) {
		List<Map> mergedList = new ArrayList<>();
		for (Map m1 : list1) {
			for (Map m2 : list2) {
				boolean flag = false;
				for (String key : StringUtils.split(keys, "1")) {
					if (m1.get(key).toString().equalsIgnoreCase(m2.get(key).toString())) {
						flag = true;
					} else {
						flag = false;
					}
				}
				if (flag) {
					for (Object s : m2.keySet()) {
						m1.put(s.toString(), m2.get(s.toString()));
					}
					mergedList.add(m1);
				}
			}
		}
		return mergedList;
	}

	@Override
	public List<Map> getReportData(Map<String, String> dynamicFilterValueMap) throws Exception {
		String reportIds = dynamicFilterValueMap.get("report_ids");
		String[] ids = StringUtils.split(reportIds, ",");
		List<List<Map>> multipleReportList = new ArrayList<>();
		for (String reportId : ids) {
			dynamicFilterValueMap.put("reportId", reportId);
			List<Map<String, String>> configsLst = getReportConfigs(reportId);
			if (configsLst != null && configsLst.size() > 0) {
				if (ids.length == 1) {
					System.out.println("Single Report fetched.....................");
					return fetchReport(dynamicFilterValueMap, configsLst.get(0));
				} else {
					multipleReportList.add(fetchReport(dynamicFilterValueMap, configsLst.get(0)));
				}
			}
		}
		return merge(multipleReportList.get(0), multipleReportList.get(1), dynamicFilterValueMap.get("merge_keys"));
		// return result;
		// return new ArrayList<>();

	}

	private List<Map> getMySqlReportData(Map<String, String> dynamicFilterValueMap, Map<String, String> configs) {
		// for (Map<String, String> configs : configsLst)
		if (configs != null) {
			List<String> filterCoulmns = new ArrayList<>();
			List<String> filterCoulmnsVal = new ArrayList<>();
			if (configs.get(FILTER_COLUMNS) != null && configs.get(FILTER_COLUMNS).length() > 0)
				filterCoulmns = Arrays.stream(StringUtils.split(configs.get(FILTER_COLUMNS), ","))
						.collect(Collectors.toList());
			if (configs.get(FILTER_COLUMNS_VALUE) != null && configs.get(FILTER_COLUMNS_VALUE).length() > 0)
				filterCoulmnsVal = Arrays.stream(StringUtils.split(configs.get(FILTER_COLUMNS_VALUE), ","))
						.collect(Collectors.toList());
			List<String> sortedByLst = Arrays.stream(StringUtils.split(configs.get(SORTED_BY), ","))
					.collect(Collectors.toList());

			List<String> filterConditionValue = new ArrayList<>();
			StringBuilder SQLsb = new StringBuilder();

			if (configs.get(DATE_FILTER_COLUMN) != null) {

			}
			// Need to check is Dynamic or static report
			if ("1".equalsIgnoreCase(configs.get(IS_DYNAMIC_FILTER))) {
				for (int i = 0; i < filterCoulmns.size(); i++) {
					String val = dynamicFilterValueMap.get(filterCoulmnsVal.get(i));
					if (val != null && val.length() > 0) {
						filterConditionValue.add(filterCoulmns.get(i));
					}
				}
			} else {
				for (int i = 0; i < filterCoulmns.size(); i++) {
					filterConditionValue.add(filterCoulmnsVal.get(i));

				}
			}
			if ("0".equalsIgnoreCase(configs.get(IS_GROUP_BY))) {
				SQLsb.append("SELECT ").append(configs.get(PROJECTIONS)).append(" FROM  ")
						.append(configs.get(COLLECTION_NAME));
				if (filterCoulmns.size() > 0) {
					SQLsb.append(" WHERE ").append(configs.get(FILTER_COLUMNS));
				}

				SQLsb.append(" ORDER BY ").append(configs.get(SORTED_BY));
				if ("1".equalsIgnoreCase(configs.get(IS_SORTING))) {
					if ("1".equalsIgnoreCase(configs.get(IS_SORTING_ASC))) {
						SQLsb.append(" ASC");
					} else {
						SQLsb.append(" DESC");
					}

				}
				System.out.println(SQLsb.toString());
				System.out.println("filterConditionValue: " + filterConditionValue.toArray()[0]);
				List<Map<String, Object>> list = null;
				if (filterConditionValue.size() > 0) {
					list = jdbcTemplate.queryForList(SQLsb.toString(), filterConditionValue.toArray());
				} else {
					list = jdbcTemplate.queryForList(SQLsb.toString());
				}

				List<Map> result = list.stream().map(a -> {
					Map<String, String> m = new HashMap<>();
					a.entrySet().stream().forEach(b -> m.put(b.getKey(), a.get(b.getKey()).toString()));
					return m;
				}).collect(Collectors.toList());
				logger.info("Report Data for Report filter {}, Size: {}", dynamicFilterValueMap, result.size());
				return result;

			} else { // Group By Query
				System.out.println(">>>>>>>>>>>>>>>>************************************>>>>>>>>>>>>>>>>>>>>>");
				SQLsb.append("SELECT ").append(configs.get(GROUPED_COLUMNS));

				if ("1".equalsIgnoreCase(configs.get(IS_COUNT))) {
					SQLsb.append(" , COUNT(*)");
				}
				// Apply SUM on required columns
				if (configs.get(GROUP_COLUMNS_SUM) != null && configs.get(GROUP_COLUMNS_SUM).length() > 0) {
					String[] groupList = StringUtils.split(configs.get(GROUP_COLUMNS_SUM), ",");
					for (int i = 0; i < groupList.length; i++) {
						SQLsb.append(", SUM(").append(groupList[i]).append(") as ").append("sum_" + groupList[i]);
					}
				}
				SQLsb.append(" FROM  ").append(configs.get(COLLECTION_NAME));

				if (filterCoulmns.size() > 0) {
					SQLsb.append(" WHERE ").append(configs.get(FILTER_COLUMNS));
				}

				// Apply Group By & count

				SQLsb.append(" GROUP BY ").append(configs.get(GROUPED_COLUMNS)).append(" ORDER BY ")
						.append(configs.get(SORTED_BY));
				if ("1".equalsIgnoreCase(configs.get(IS_SORTING))) {
					if ("1".equalsIgnoreCase(configs.get(IS_SORTING_ASC))) {
						SQLsb.append(" ASC");
					} else {
						SQLsb.append(" DESC");
					}
				}

				List<Map<String, Object>> list = null;
				if (filterConditionValue.size() > 0) {
					list = jdbcTemplate.queryForList(SQLsb.toString(), filterConditionValue.toArray());
				} else {
					list = jdbcTemplate.queryForList(SQLsb.toString());
				}

				List<Map> result = list.stream().map(a -> {
					Map<String, String> m = new HashMap<>();
					a.entrySet().stream().forEach(b -> m.put(b.getKey(), a.get(b.getKey()).toString()));
					return m;
				}).collect(Collectors.toList());
				logger.info("Report Data for Report filter {}, Size: {}", dynamicFilterValueMap, result.size());
				return result;

			}
		}
		return new ArrayList<>();
	}

	private List<Map> getMongoDBReportData(Map<String, String> dynamicFilterValueMap, Map<String, String> configs) {

		// for (Map<String, String> configs : configsLst)
		if (configs != null) {
			List<String> filterCoulmns = Arrays.stream(StringUtils.split(configs.get(FILTER_COLUMNS), ","))
					.collect(Collectors.toList());
			List<String> filterCoulmnsVal = Arrays.stream(StringUtils.split(configs.get(FILTER_COLUMNS_VALUE), ","))
					.collect(Collectors.toList());
			List<String> sortedByLst = Arrays.stream(StringUtils.split(configs.get(SORTED_BY), ","))
					.collect(Collectors.toList());
			Criteria criteria = new Criteria();

//        if (configs.get(DATE_FILTER_COLUMN) != null) {
//          
//        }
			// Need to check is Dynamic or static report
			if ("1".equalsIgnoreCase(configs.get(IS_DYNAMIC_FILTER))) {
				for (int i = 0; i < filterCoulmns.size(); i++) {
					String val = dynamicFilterValueMap.get(filterCoulmnsVal.get(i));
					// System.out.println("val======"+val);

					if (val != null && val.length() > 0) {
						try {

						val = URLDecoder.decode(val,StandardCharsets.UTF_8.name() );
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(val.contains("#"))
						{
						
							
							String []valArr = val.replaceAll("#", "").split(",");
							criteria.and(filterCoulmns.get(i)).in(valArr);
							
						}else {
						criteria.and(filterCoulmns.get(i)).is(val);
						}
						
					}
				}
			} else {
				for (int i = 0; i < filterCoulmns.size(); i++) {
					criteria.and(filterCoulmns.get(i)).is(filterCoulmnsVal.get(i));
				}
			}

			if ("0".equalsIgnoreCase(configs.get(IS_GROUP_BY))) {

				Query query = new Query(criteria);
//            if(dynamicFilterValueMap.get("START_DATE")!=null && dynamicFilterValueMap.get("END_DATE")!=null &&configs.get(FILTER_COLUMNS)=="FIELD_TYPE") {
//            	
//            	query.addCriteria(Criteria.where(configs.get(DATE_FILTER_COLUMN)).gte(dynamicFilterValueMap.get("START_DATE") +" 00:00:00.0").lt(dynamicFilterValueMap.get("END_DATE")+" 23:59:59.0"));
////            	query.addCriteria(Criteria.where(configs.get(DATE_FILTER_COLUMN)).gte(dynamicFilterValueMap.get("START_DATE") +" 00:00:00")
////                        .and(configs.get(DATE_FILTER_COLUMN)).lt(dynamicFilterValueMap.get("END_DATE")+" 23:59:59"));
//            }
//				if (dynamicFilterValueMap.containsKey("END_DATE") && dynamicFilterValueMap.get("END_DATE") != null) {
					if (dynamicFilterValueMap.get("START_DATE")!=null && dynamicFilterValueMap.get("END_DATE") != null) {
					query.addCriteria(Criteria.where(configs.get(DATE_FILTER_COLUMN))
							.gte(dynamicFilterValueMap.get("START_DATE") + " 00:00:00")
							.lt(dynamicFilterValueMap.get("END_DATE") + " 23:59:59"));

				}
				if (dynamicFilterValueMap.get("DATE")!=null) {

					String START_DATE = dynamicFilterValueMap.get("DATE").replaceAll("-", "");
					query.addCriteria(Criteria.where(configs.get(DATE_FILTER_COLUMN)).is(START_DATE));
				}
				if (dynamicFilterValueMap.get("pgRefNumber") != null || dynamicFilterValueMap.get("orderID") != null
						|| dynamicFilterValueMap.get("rrn") != null) {

				}
				else {
					if (!dynamicFilterValueMap.containsKey("END_DATE")
							&& !dynamicFilterValueMap.containsKey("START_DATE")) {
						logger.info("END_DATE==="+dynamicFilterValueMap.containsKey("END_DATE"),"START_DATE=="+dynamicFilterValueMap.containsKey("START_DATE"));
						query.addCriteria(Criteria.where(configs.get(DATE_FILTER_COLUMN)).gte(configs.get(FROM_DATE))
								.lt(configs.get(TO_DATE)));
					}
					
					
					
				}
				if ("1".equalsIgnoreCase(configs.get(IS_SORTING))) {
					if ("1".equalsIgnoreCase(configs.get(IS_SORTING_ASC))) {
						query.with(Sort.by(sortedByLst.stream().map(it -> new Sort.Order(Sort.Direction.ASC, it))
								.collect(Collectors.toList())));
					} else {
						query.with(Sort.by(sortedByLst.stream().map(it -> new Sort.Order(Sort.Direction.DESC, it))
								.collect(Collectors.toList())));
					}

				}
				System.out.println(query.toString());
				Map<String, String> titleByFields = new LinkedHashMap<>();
				if (!"*".equalsIgnoreCase(configs.get(PROJECTIONS))) {
					List<String> projections = Arrays.stream(StringUtils.split(configs.get(PROJECTIONS), ","))
							.collect(Collectors.toList());
					projections.stream().forEach(s -> query.fields().include(s));
					List<String> reportColumnHeaders =
							Arrays.stream(StringUtils.split(configs.get(REPORT_COL_HEADERS), ","))
									.collect(Collectors.toList());
					for (int i=0; i < projections.size(); i++) {
						titleByFields.put(projections.get(i), reportColumnHeaders.get(i));
					}
				}
				List<Map> result = mongoTemplate.find(query, Map.class, configs.get(COLLECTION_NAME));
				result.add(titleByFields);
				// System.out.println(new Gson().toJson(result));
				logger.info("Report Data for reportId:, Report filter {}, Size: {}", dynamicFilterValueMap,
						result.size());

				return result;

			} else { // Group By Query
				System.out.println(">>>>>>>>>>>>>>>>************************************>>>>>>>>>>>>>>>>>>>>>");
				// Apply Group By & count
				GroupOperation groupOperation = Aggregation.group(StringUtils.split(configs.get(GROUPED_COLUMNS), ","));
				if ("1".equalsIgnoreCase(configs.get(IS_COUNT))) {
					groupOperation = groupOperation.count().as("totalCount");
				}
				// Apply SUM on required columns
				if (configs.get(GROUP_COLUMNS_SUM) != null && configs.get(GROUP_COLUMNS_SUM).length() > 0) {
					String[] groupList = StringUtils.split(configs.get(GROUP_COLUMNS_SUM), ",");
					for (int i = 0; i < groupList.length; i++) {
						groupOperation = groupOperation.sum(ConvertOperators.ToDecimal.toDecimal("$" + groupList[i]))
								.as("sum_" + groupList[i]);
					}
				}

				SortOperation sortOperation = null;
				if ("1".equalsIgnoreCase(configs.get(IS_SORTING))) {
					if ("1".equalsIgnoreCase(configs.get(IS_SORTING_ASC))) {
						sortOperation = Aggregation.sort(Sort.Direction.ASC,
								StringUtils.split(configs.get(SORTED_BY), ","));
					} else {
						sortOperation = Aggregation.sort(Sort.Direction.DESC,
								StringUtils.split(configs.get(SORTED_BY), ","));
					}
				} // End of sorting
				List<String> groupedSumList = Arrays.stream(StringUtils.split(configs.get(GROUP_COLUMNS_SUM), ","))
						.map(s -> "sum_" + s).collect(Collectors.toList());
				String[] groupedColumns = StringUtils.split(configs.get(GROUPED_COLUMNS), ",");// ArrayUtils.toStringArray(groupedList.toArray());
				String[] aggregatedColumns = ArrayUtils.toStringArray(groupedSumList.toArray());
				String[] projections = ArrayUtils.addAll(groupedColumns, aggregatedColumns);
				projections = ArrayUtils.addAll(projections, "totalCount");

				Aggregation agg = Aggregation.newAggregation(Aggregation.match(criteria), groupOperation, sortOperation,
						Aggregation.project(projections));

				// System.out.println(agg);

				List<Document> list = mongoTemplate.aggregate(agg, configs.get(COLLECTION_NAME), Document.class)
						.getMappedResults();
				List<Map> list1 = list.stream().map(a -> {
					Map<String, String> m = new HashMap<>();
					a.entrySet().stream().forEach(b -> m.put(b.getKey(), a.get(b.getKey()).toString()));
					return m;
				}).collect(Collectors.toList());
				logger.info("Aggregated Report Data for Report filter {}, Size: {}", dynamicFilterValueMap,
						list.size());

				return list1;

			}
		}
		return new ArrayList<>();
	}

	@Override
	public List<Map<String, Object>> getScreen(Map<String, String> inputMap) throws Exception {
		String SQL = "SELECT sc.*,s.title,s.report_ids,s.merge_keys FROM d_screen_control sc \n"
				+ "JOIN d_screen_control_mapping scm ON scm.control_id = sc.id\n"
				+ "JOIN d_screens s ON s.id=scm.screen_id\n" + "WHERE scm.screen_id=?";
		return jdbcTemplate.queryForList(SQL, new Object[] { inputMap.get("screenId") });

	}

	@Override
	public List<Map<String, Object>> getKeyValues(String SQL) {
		return jdbcTemplate.queryForList(SQL);
	}
}
