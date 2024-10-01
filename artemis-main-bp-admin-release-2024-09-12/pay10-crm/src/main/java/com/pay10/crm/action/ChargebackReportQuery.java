package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.repository.DMSEntity;
import com.pay10.commons.repository.DMSRepository;
import com.pay10.commons.user.CbReportObject;
import com.pay10.commons.user.Status;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.crm.mongoReports.TxnReportForCb;

@Component
public class ChargebackReportQuery {

	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	private DMSRepository dmsrepo;
	@Autowired
	private PropertiesManager propertiesManager;
	@Autowired
	private UserDao userDao;
	@Autowired
	private TxnReportForCb txnReportForCb; 

	private static final String prefix = "MONGO_DB_";

	private static Logger logger = LoggerFactory.getLogger(ChargebackReportQuery.class.getName());

	public List<CbReportObject> downloadCbJourneyReport(String merchantPayId, String fromDate, String toDate,
			String cbCaseId, String pgRefNo) {
		List<String> statusList = Arrays.asList("Accepted","INITIATED", "CLOSED", "POD", "BANK_ACQ_FAVOUR", "MERCHANT_FAVOUR",
				"CHARGEBACK_INITIATED", "CHARGEBACK_REVERSAL");
		List<CbReportObject> listToReturn = new ArrayList<>();
		try {

			List<DMSEntity> entities = new ArrayList<>();
			
			if (StringUtils.isNotBlank(cbCaseId)) {
				entities = dmsrepo.findByCbCaseIdWithDate(cbCaseId,fromDate,toDate);
			}else if (StringUtils.isNotBlank(pgRefNo)) {
				entities = dmsrepo.findByPgRefNoWithDate(pgRefNo,fromDate,toDate);
			}else {
				entities = dmsrepo.findByCustomerNameWithDate(merchantPayId,fromDate,toDate);
			}
			
			if (entities.size() > 0) {
				for (DMSEntity entity : entities) {
					List<CbReportObject> cbReportObjects = new ArrayList<CbReportObject>();
					User user=userDao.findByPayId(entity.getMerchantPayId());
					
					CbReportObject report = new CbReportObject();
					BeanUtils.copyProperties(entity, report);
					report.setMarchantName(user.getBusinessName());
					if (report != null) {
						for (String status : statusList) {
							if (status.equalsIgnoreCase(Status.INITIATED.toString())
									&& report.getCbInitiatedDate() != null) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();
								reportTemp.setStatus(Status.INITIATED.toString());
								reportTemp.setDate(report.getCbInitiatedDate());
								cbReportObjects.add(reportTemp);
							}
							else if (status.equalsIgnoreCase(Status.ACCEPTED.toString())
									&& report.getCbAcceptDate() != null) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();
								reportTemp.setStatus(Status.ACCEPTED.toString());
								reportTemp.setDate(report.getCbAcceptDate());
								cbReportObjects.add(reportTemp);
							} else if (status.equalsIgnoreCase(Status.POD.toString())
									&& report.getCbPodDate() != null) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();
								reportTemp.setStatus(Status.POD.toString());
								reportTemp.setDate(report.getCbPodDate());
								cbReportObjects.add(reportTemp);

							} else if (status.equalsIgnoreCase(Status.CLOSED.toString())
									&& report.getCbCloseDate() != null) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();
								reportTemp.setStatus(Status.CLOSED.toString());
								reportTemp.setDate(report.getCbCloseDate());
								cbReportObjects.add(reportTemp);

							} else if (status.equalsIgnoreCase(StatusType.CHARGEBACK_INITIATED.getName())) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();

								Document doc = getCbByPgRefNoAndStatus(report.getPgRefNo(),
										StatusType.CHARGEBACK_INITIATED.getName());
								if (doc != null) {
									reportTemp.setStatus(StatusType.CHARGEBACK_INITIATED.getName());
									reportTemp.setDate(doc.getString("DATE_INDEX"));
									cbReportObjects.add(reportTemp);
								}

							} else if (status.equalsIgnoreCase(StatusType.CHARGEBACK_REVERSAL.getName())) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();

								Document doc = getCbByPgRefNoAndStatus(report.getPgRefNo(),
										StatusType.CHARGEBACK_REVERSAL.getName());
								if (doc != null) {
									reportTemp.setStatus(StatusType.CHARGEBACK_REVERSAL.getName());
									reportTemp.setDate(doc.getString("DATE_INDEX"));
									cbReportObjects.add(reportTemp);
								}

							}

						}
						listToReturn.addAll(cbReportObjects);
					}

				}
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Final Download Report Data : " + new Gson().toJson(listToReturn));
		return listToReturn;
		
//	List<DMSEntity> dmsEntities=txnReportForCb.chargeBackReportReport(merchantPayId, pgRefNo, cbCaseId, toDate, fromDate,  -1, -1);
//		List<CbReportObject> liCbReportObjects=new ArrayList<>();
//	dmsEntities.stream().forEach(entity->{
//		CbReportObject cbReportObject=new CbReportObject();
////		BeanUtils.copyProperties(entity, cbReportObject);
//		cbReportObject.setMerchantPayId(entity.getMerchantPayId());
//		cbReportObject.setMarchantName(entity.getBusinessName()!=null?entity.getBusinessName():"NA");
//		cbReportObject.setCbCaseId(entity.getCbCaseId()!=null?entity.getCbCaseId():"NA");
//		cbReportObject.setDtOfTxn(entity.getDtOfTxn()!=null?entity.getDtOfTxn():"NA");
//		cbReportObject.setTxnAmount(entity.getTxnAmount()!=null?entity.getTxnAmount():"NA");
//		cbReportObject.setPgCaseId(entity.getPgCaseId()!=null?entity.getPgCaseId():"NA");
//		cbReportObject.setMerchantTxnId(entity.getMerchantTxnId()!=null?entity.getMerchantTxnId():"NA");
//		cbReportObject.setOrderId(entity.getOrderId()!=null?entity.getOrderId():"NA");
//		cbReportObject.setPgRefNo(entity.getPgRefNo()!=null?entity.getPgRefNo():"NA");
//		cbReportObject.setBankTxnId(entity.getBankTxnId()!=null?entity.getBankTxnId():"NA");
//		cbReportObject.setCbAmount(entity.getCbAmount()!=null?entity.getCbAmount():"NA");
//		cbReportObject.setCbReason(entity.getCbReason()!=null?entity.getCbReason():"NA");
//		cbReportObject.setCbReasonCode(entity.getCbReasonCode()!=null?entity.getCbReasonCode():"NA");
//		cbReportObject.setCbIntimationDate(entity.getCbIntimationDate()!=null?entity.getCbIntimationDate():"NA");
//		cbReportObject.setCbDdlineDate(entity.getCbDdlineDate()!=null?entity.getCbDdlineDate():"NA");
//		cbReportObject.setModeOfPayment(entity.getModeOfPayment()!=null?entity.getModeOfPayment():"NA");
//		cbReportObject.setAcqName(entity.getAcqName()!=null?entity.getAcqName():"NA");
//		cbReportObject.setSettlemtDate(entity.getSettlemtDate()!=null?entity.getSettlemtDate():"NA");
//		cbReportObject.setCustomerName(entity.getCustomerName()!=null?entity.getCustomerName():"NA");
//		cbReportObject.setCustomerPhone(entity.getCustomerPhone()!=null?entity.getCustomerPhone():"NA");
//		cbReportObject.setEmail(entity.getEmail()!=null?entity.getEmail():"NA");
//		cbReportObject.setNemail(entity.getNemail()!=null?entity.getNemail():"NA");
//		cbReportObject.setStatus(entity.getStatus().toString());
//		cbReportObject.setDate(entity.getDtOfTxn()!=null?entity.getDtOfTxn():"NA");
//		
//		
//		liCbReportObjects.add(cbReportObject);
//		});
	//return liCbReportObjects;
	}
	
	public List<CbReportObject> downloadCbJourneyReportForAudiTrail(String merchantPayId, String fromDate, String toDate,
			String cbCaseId, String pgRefNo) {
		List<String> statusList = Arrays.asList("Accepted","INITIATED", "CLOSED", "POD", "BANK_ACQ_FAVOUR", "MERCHANT_FAVOUR",
				"CHARGEBACK_INITIATED", "CHARGEBACK_REVERSAL");
		List<CbReportObject> listToReturn = new ArrayList<>();
		try {

			List<DMSEntity> entities = new ArrayList<>();
			
			if (StringUtils.isNotBlank(cbCaseId)) {
				entities = dmsrepo.findByCbCaseId(cbCaseId);
			}else if (StringUtils.isNotBlank(pgRefNo)) {
				entities = dmsrepo.findByPgRefNo(pgRefNo);
			}else {
				entities = dmsrepo.findByCustomerName(merchantPayId);
			}
			
			if (entities.size() > 0) {
				for (DMSEntity entity : entities) {
					List<CbReportObject> cbReportObjects = new ArrayList<CbReportObject>();
					User user=userDao.findByPayId(entity.getMerchantPayId());
					
					CbReportObject report = new CbReportObject();
					BeanUtils.copyProperties(entity, report);
					report.setMarchantName(user.getBusinessName());
					if (report != null) {
						for (String status : statusList) {
							if (status.equalsIgnoreCase(Status.INITIATED.toString())
									&& report.getCbInitiatedDate() != null) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();
								reportTemp.setStatus(Status.INITIATED.toString());
								reportTemp.setDate(report.getCbInitiatedDate());
								cbReportObjects.add(reportTemp);
							}
							else if (status.equalsIgnoreCase(Status.ACCEPTED.toString())
									&& report.getCbAcceptDate() != null) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();
								reportTemp.setStatus(Status.ACCEPTED.toString());
								reportTemp.setDate(report.getCbAcceptDate());
								cbReportObjects.add(reportTemp);
							} else if (status.equalsIgnoreCase(Status.POD.toString())
									&& report.getCbPodDate() != null) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();
								reportTemp.setStatus(Status.POD.toString());
								reportTemp.setDate(report.getCbPodDate());
								cbReportObjects.add(reportTemp);

							} else if (status.equalsIgnoreCase(Status.CLOSED.toString())
									&& report.getCbCloseDate() != null) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();
								reportTemp.setStatus(Status.CLOSED.toString());
								reportTemp.setDate(report.getCbCloseDate());
								cbReportObjects.add(reportTemp);

							} else if (status.equalsIgnoreCase(StatusType.CHARGEBACK_INITIATED.getName())) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();

								Document doc = getCbByPgRefNoAndStatus(report.getPgRefNo(),
										StatusType.CHARGEBACK_INITIATED.getName());
								if (doc != null) {
									reportTemp.setStatus(StatusType.CHARGEBACK_INITIATED.getName());
									reportTemp.setDate(doc.getString("DATE_INDEX"));
									cbReportObjects.add(reportTemp);
								}

							} else if (status.equalsIgnoreCase(StatusType.CHARGEBACK_REVERSAL.getName())) {
								CbReportObject reportTemp = new CbReportObject();
								reportTemp = (CbReportObject) report.clone();

								Document doc = getCbByPgRefNoAndStatus(report.getPgRefNo(),
										StatusType.CHARGEBACK_REVERSAL.getName());
								if (doc != null) {
									reportTemp.setStatus(StatusType.CHARGEBACK_REVERSAL.getName());
									reportTemp.setDate(doc.getString("DATE_INDEX"));
									cbReportObjects.add(reportTemp);
								}

							}

						}
						listToReturn.addAll(cbReportObjects);
					}

				}
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Final Download Report Data : " + new Gson().toJson(listToReturn));
		return listToReturn;
	}

	private Document getCbByPgRefNoAndStatus(String pgRefNo, String status) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + pgRefNo);
		Document doc = null;
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNo.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			while (cursor.hasNext()) {

				doc = cursor.next();

			}
			cursor.close();
		} catch (Exception e) {
			logger.error("Exception occured in getTxnByPgRefNo", e);
			return null;
		}
		return doc;
	}
}
