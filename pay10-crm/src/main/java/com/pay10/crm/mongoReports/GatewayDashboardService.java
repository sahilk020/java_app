package com.pay10.crm.mongoReports;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.crm.actionBeans.GateWayDashboardBean;

@Service
@Scope("prototype")
public class GatewayDashboardService {
	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	private PropertiesManager propertiesManager;
	private static final String prefix = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(GatewayDashboardService.class.getName());

	public String getGatewayDashboardDetails(String acquirer) {
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		List<GateWayDashboardBean> finalbean=null;
		List<GateWayDashboardBean> beans = new ArrayList<>();
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		try {
			Document match = new Document();
			match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			List<Document> query = Arrays.asList(new Document("$match", match),
					new Document("$sort", new Document("CREATE_DATE", -1L)), new Document("$limit", 25L));
			logger.info("GatewayDashboardService in getGatewayDashboardDetails() Query : " + query);
			MongoCursor<Document> cursor = collection.aggregate(query).iterator();
			
			//Add Result into List according to above queries
			while (cursor.hasNext()) {
				Document document = (Document) cursor.next();
				String payment_Type = document.getString(FieldType.PAYMENT_TYPE.getName());
				String create_date = document.getString(FieldType.CREATE_DATE.getName());
				String status = document.getString(FieldType.STATUS.getName());
				if (payment_Type != null && create_date != null && status != null) {
					GateWayDashboardBean gateWayDashboardBeans = new GateWayDashboardBean();
					gateWayDashboardBeans.setPaymentType(payment_Type);
					gateWayDashboardBeans.setLastTrnReceived(create_date);
					gateWayDashboardBeans.setStatus(status);
					beans.add(gateWayDashboardBeans);
				}
			}
			
			//Getting Mop type from Application Properties Files by send acquirer name
			String payment_type[] = propertiesManager.propertiesMap.get(acquirer).split(",");
			finalbean = new ArrayList<>();
			int i = 1;
			
			// Consolidating the Result according to the acquirer mop type
			for (String mop : payment_type) {
				GateWayDashboardBean dashboardBean = new GateWayDashboardBean();
				dashboardBean.setPaymentType(mop);
				for (GateWayDashboardBean bean : beans) {
					if (mop.equalsIgnoreCase(bean.getPaymentType())) {
						if (bean.getStatus().equalsIgnoreCase(StatusType.CAPTURED.getName()) || bean.getStatus().equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName()) || bean.getStatus().equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
							dashboardBean.setSuccess(dashboardBean.getSuccess() + i);
						} else if (bean.getStatus().equalsIgnoreCase(StatusType.SENT_TO_BANK.getName()) || bean.getStatus().equalsIgnoreCase(StatusType.PENDING.getName()) || bean.getStatus().equalsIgnoreCase(StatusType.DENIED.getName())) {
								dashboardBean.setInqueue(dashboardBean.getInqueue() + i);
						} else {
							dashboardBean.setFailed(dashboardBean.getFailed() + i);
						}
						if (dashboardBean.getLastTrnReceived().equalsIgnoreCase("N/A")) {
							dashboardBean.setLastTrnReceived(bean.getLastTrnReceived());
						}
					}
					
				}
				finalbean.add(dashboardBean);
			}
			
			//Calculate Success Ratio and Status of Consolidated Result
			for (GateWayDashboardBean gateWayDashboardBean : finalbean) {
				gateWayDashboardBean
						.setSuccessRatio(Double.valueOf(decimalFormat.format(gateWayDashboardBean.getSuccess() == 0 ? 0
								: (((double) gateWayDashboardBean.getSuccess() * (double) 100)
										/ (double) (gateWayDashboardBean.getSuccess() + gateWayDashboardBean.getFailed()
												+ gateWayDashboardBean.getInqueue())))));
				gateWayDashboardBean.setStatus(gateWayDashboardBean.getSuccessRatio() > 0 ? "UP" : "Down");
			}
		} catch (Exception e) {
			logger.info("Exception occur in GatewayDashboardService in getGatewayDashboardDetails() : " + e.getMessage());
			e.printStackTrace();
		}
		return new Gson().toJson(finalbean);

	}
}
