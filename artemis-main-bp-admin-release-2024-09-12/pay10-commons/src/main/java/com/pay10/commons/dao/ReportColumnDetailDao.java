package com.pay10.commons.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pay10.commons.user.ReportColumnDetail;

@Service
public class ReportColumnDetailDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(ReportColumnDetailDao.class.getName());

	@SuppressWarnings("unchecked")
	public Map<String, String> findByTagName(String tagName) {
		Map<String, String> map = new HashMap<String, String>();
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM ReportColumnDetail where tagName='" + tagName + "'";
			List<ReportColumnDetail> reportColumnDetails = session.createQuery(hql).getResultList();
			session.close();
			if (reportColumnDetails.size() > 0) {
				JSONObject jo = new JSONObject(new Gson().toJson(reportColumnDetails.get(0)));
				try {
					map = new ObjectMapper().readValue(jo.getString("mapValue"), Map.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} finally {
			session.close();
		}
		logger.info("Column Data : " + map);
		return map;

	}

}
