package com.pay10.commons.repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Status;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TDRStatus;
import com.pay10.commons.util.TransactionManager;

@Component("dmsrepo")
public class DMSRepository extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(DMSRepository.class.getName());

	@Autowired
	private MongoInstance mongoInstance;
	private static final String prefix = "MONGO_DB_";

	public void save(DMSEntity dmsEntity) throws DataAccessLayerException {
		super.save(dmsEntity);
	}

	@SuppressWarnings("unchecked")
	public List<DMSEntity> findAll() {
		return super.findAll(DMSEntity.class);
	}

	public DMSEntity findById(long id) {
		logger.info("id..." + id);
		return (DMSEntity) super.find(DMSEntity.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<DMSEntity> findByPayId(String payId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createQuery("FROM DMSEntity WHERE merchantPayId=:payId").setParameter("payId", payId)
					.getResultList();
		} finally {
			autoClose(session);
		}
	}

	@SuppressWarnings("unchecked")
	public List<DMSEntity> findByCbCaseId(String cbCaseId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (List<DMSEntity>) session.createQuery("FROM DMSEntity WHERE cbCaseId=:cbCaseId")
					.setParameter("cbCaseId", cbCaseId).list();
		} finally {
			autoClose(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DMSEntity> findByCbCaseIdWithDate(String cbCaseId,String fromDate,String toDate) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (List<DMSEntity>) session.createQuery("FROM DMSEntity WHERE cbCaseId=:cbCaseId AND cbIntimationDate BETWEEN :fromDate AND :toDate")
					.setParameter("cbCaseId", cbCaseId).
					setParameter("fromDate", fromDate).
					setParameter("toDate", toDate).
					getResultList();
		} finally {
			autoClose(session);
		}
	}
	
	public DMSEntity findObjectByCbCaseId(String cbCaseId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (DMSEntity) session.createQuery("FROM DMSEntity WHERE cbCaseId=:cbCaseId")
					.setParameter("cbCaseId", cbCaseId).getSingleResult();
		} finally {
			autoClose(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DMSEntity> findByCbCaseIdForClosed(String cbCaseId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (List<DMSEntity>) session.createQuery("FROM DMSEntity WHERE cbCaseId=:cbCaseId and status='CLOSED'")
					.setParameter("cbCaseId", cbCaseId).list();
		} finally {
			autoClose(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DMSEntity> findByPgRefNo(String pgRefNo) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (List<DMSEntity>) session.createQuery("FROM DMSEntity WHERE pgRefNo=:pgRefNo")
					.setParameter("pgRefNo", pgRefNo).list();
		} finally {
			autoClose(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DMSEntity> findByPgRefNoWithDate(String pgRefNo,String fromDate,String toDate) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (List<DMSEntity>) session.createQuery("FROM DMSEntity WHERE pgRefNo=:pgRefNo AND cbIntimationDate BETWEEN :fromDate AND :toDate")
					.setParameter("pgRefNo", pgRefNo).
					setParameter("fromDate", fromDate).
					setParameter("toDate", toDate).
					getResultList();
		} finally {
			autoClose(session);
		}
	}

	public int updateStatusByCaseId(String caseId) {
		
		int status = 0;
		try (Session session = HibernateSessionProvider.getSession();) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Transaction tx = session.beginTransaction();
			Query q = session.createQuery("update DMSEntity set status=:status,cbPodDate=:cbPodDate,merchantDocUploadFlag=:merchantDocUploadFlag  where cbCaseId=:cbCaseId");
 			q.setParameter("status", Status.POD);
 			q.setParameter("cbCaseId", caseId);
 			q.setParameter("cbPodDate", simpleDateFormat.format(new Date()));
			q.setParameter("merchantDocUploadFlag", true);
			status = q.executeUpdate();
			System.out.println(status);
			tx.commit();
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	public List<DMSEntity> findByCustomerName(String merchantPayId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (List<DMSEntity>) session.createQuery("FROM DMSEntity WHERE merchantPayId=:merchantPayId")
					.setParameter("merchantPayId", merchantPayId).list();
		} finally {
			autoClose(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DMSEntity> findByCustomerNameWithDate(String merchantPayId,String fromDate,String toDate) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (List<DMSEntity>) session.createQuery("FROM DMSEntity WHERE merchantPayId=:merchantPayId AND cbIntimationDate BETWEEN :fromDate AND :toDate")
					.setParameter("merchantPayId", merchantPayId).
					setParameter("fromDate", fromDate).
					setParameter("toDate", toDate)
					.getResultList();
		} finally {
			autoClose(session);
		}
	}

	public DMSEntity findByPgRefNoAndCbCaseId(String pgRefNo, String cbCaseId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (DMSEntity) session.createQuery("FROM DMSEntity WHERE pgRefNo=:pgRefNo and cbCaseId=:cbCaseId")
					.setParameter("pgRefNo", pgRefNo).setParameter("pgRefNo", pgRefNo);
		} finally {
			autoClose(session);
		}
	}

	public void update(DMSEntity dms) {
		dms.setId(dms.getId());
		super.saveOrUpdate(dms);
	}

	public void saveAll(List<DMSEntity> dmsEntities) {
		dmsEntities.forEach(dmsEntity -> {
			save(dmsEntity);
		});
	}

	public Map<String, String> insertChargeBackStatusUpdate(Map<String, String> csu) {
		Map<String, String> response = new HashMap<String, String>();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.CHARGEBACK_STATUS_UPDATE.getValue()));
			if (csu.size() > 0 && csu != null) {
				for (Map.Entry<String, String> entry : csu.entrySet()) {
					String cbCaseId = entry.getKey();
					String favour = entry.getValue();
					if (!StringUtils.isBlank(cbCaseId) && !StringUtils.isBlank(favour)) {
						response.put(cbCaseId, favour);

						Document document = new Document();
						document.append(FieldType.CB_CASE_ID.getName(), cbCaseId);
						document.append(FieldType.FAVOUR.getName(), favour);
						document.append(FieldType.STATUS.getName(), "0");
						document.append(FieldType.ERROR_MESSAGE.getName(), "");
						document.append(FieldType.DATE.getName(), formatter.format(date));
						document.append(FieldType.DATE_INDEX.getName(), formatter1.format(date));
						String id = TransactionManager.getNewTransactionId();
						document.put("_id", id);
						collection.insertOne(document);
					} else {
						logger.info("Cannot insert this entry in chargebackStatusUpdate collection \n CB_CASE_ID : "
								+ cbCaseId + "\tfavour : " + favour);
					}

				}
			}
		} catch (Exception e) {
			logger.error("Exception occur in insertChargeBackStatusUpdate : ", e);
			e.printStackTrace();
		}
		return response;
	}

	public long updateChargeBackStatusUpdate(String cbCaseId,String message) {
		long status = 0;
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.CHARGEBACK_STATUS_UPDATE.getValue()));

			BasicDBObject newDocument = new BasicDBObject();
			newDocument.append("$set", new BasicDBObject().append(FieldType.STATUS.getName(), "1").append(FieldType.ERROR_MESSAGE.getName(), message));
			
			BasicDBObject searchQuery = new BasicDBObject().append(FieldType.CB_CASE_ID.getName(), cbCaseId);

			UpdateResult result = collection.updateOne(searchQuery, newDocument);
			status = result.getModifiedCount();

		} catch (Exception e) {
			logger.error("Exception occur in updateChargeBackStatusUpdate : ", e);
			e.printStackTrace();
		}
		return status;
	}

	public List<DMSEntity> listChargebackStatus(String payId) {

		Session session = HibernateSessionProvider.getSession();
		try {
			if (payId != null && payId.length() > 0) {
				return session.createQuery(
						"FROM DMSEntity WHERE merchantPayId=:payId  and cbClosedInFavorBank is null and cbClosedInFavorMerchant is null and status='CLOSED'")
						.setParameter("payId", payId).getResultList();
			} else {
				return session
						.createQuery(
								"FROM DMSEntity WHERE cbClosedInFavorBank is null and cbClosedInFavorMerchant is null and status='CLOSED'")
						.getResultList();

			}

		} finally {
			autoClose(session);
		}

	}

	public Long totalCount(String payId, String acquirer,String dateFrom,String dateTo) {

		try (Session session = HibernateSessionProvider.getSession();) {

			StringBuilder builder = new StringBuilder();
			builder.append("SELECT COUNT(*) as count from DMSEntity WHERE ");

				if(!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) {
					builder.append("dtOfTxn BETWEEN '"+dateFrom+"' AND '"+dateTo+"' ");
				}
			
				if (!payId.equalsIgnoreCase("All")) {
					builder.append("AND merchantPayId='" + payId + "'");
					
				}

				if (!acquirer.equalsIgnoreCase("All")) {
					builder.append("AND acqName='" + acquirer + "'");
				}
			

			logger.info("Query for totalCount : " + builder.toString());

			return session.createQuery(builder.toString(), Long.class).getSingleResult();

		} catch (Exception e) {
			logger.error("Exception occure in totalCount ,", e);
			e.printStackTrace();
		}
		return (long) 0;
	}

	public Long recoveredFromMerchant(String payId, String acquirer,String dateFrom,String dateTo) {

		try (Session session = HibernateSessionProvider.getSession();) {

			StringBuilder builder = new StringBuilder();
			builder.append(
					"SELECT COUNT(*) as count from DMSEntity WHERE cbClosedInFavorBank='BANK_ACQ_FAVOUR' ");

			if(!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) {
				builder.append(" AND dtOfTxn BETWEEN '"+dateFrom+"' AND '"+dateTo+"' ");
			}
			
			if (!payId.equalsIgnoreCase("All")) {
				builder.append(" AND merchantPayId='" + payId + "'");
				;
			}

			if (!acquirer.equalsIgnoreCase("All")) {
				builder.append(" AND acqName='" + acquirer + "'");
			}

			logger.info("Query for recoveredFromMerchant : " + builder.toString());

			return session.createQuery(builder.toString(), Long.class).getSingleResult();

		} catch (Exception e) {
			logger.error("Exception occure in recoveredFromMerchant ,", e);
			e.printStackTrace();
		}
		return (long) 0;
	}

	public Long debitedFromBank(String payId, String acquirer,String dateFrom,String dateTo) {

		try (Session session = HibernateSessionProvider.getSession();) {

			StringBuilder builder = new StringBuilder();
			builder.append(
					"SELECT COUNT(*) as count from DMSEntity WHERE (status IN ('INITIATED','POD') OR cbClosedInFavorBank='BANK_ACQ_FAVOUR')  ");

			if(!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) {
				builder.append(" AND (dtOfTxn BETWEEN '"+dateFrom+"' AND '"+dateTo+"') ");
			}
			
			if (!payId.equalsIgnoreCase("All")) {
				builder.append("AND merchantPayId='" + payId + "'");
				
			}

			if (!acquirer.equalsIgnoreCase("All")) {
				builder.append("AND acqName='" + acquirer + "'");
			}

			logger.info("Query for debitedFromBank : " + builder.toString());

			return session.createQuery(builder.toString(), Long.class).getSingleResult();

		} catch (Exception e) {
			logger.error("Exception occure in debitedFromBank ,", e);
			e.printStackTrace();
		}
		return (long) 0;
	}

	public Long defended(String payId, String acquirer,String dateFrom,String dateTo) {

		try (Session session = HibernateSessionProvider.getSession();) {

			StringBuilder builder = new StringBuilder();
			builder.append("SELECT COUNT(*) as count from DMSEntity WHERE status='POD'  ");

			if(!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) {
				builder.append(" AND dtOfTxn BETWEEN '"+dateFrom+"' AND '"+dateTo+"' ");
			}
			
			if (!payId.equalsIgnoreCase("All")) {
				builder.append("AND merchantPayId='" + payId + "'");
				
			}

			if (!acquirer.equalsIgnoreCase("All")) {
				builder.append("AND acqName='" + acquirer + "'");
			}

			logger.info("Query for defended : " + builder.toString());

			return session.createQuery(builder.toString(), Long.class).getSingleResult();

		} catch (Exception e) {
			logger.error("Exception occure in defended ,", e);
			e.printStackTrace();
		}
		return (long) 0;
	}

	public Long accepted(String payId, String acquirer,String dateFrom,String dateTo) {

		try (Session session = HibernateSessionProvider.getSession();) {

			StringBuilder builder = new StringBuilder();
			builder.append("SELECT COUNT(*) as count from DMSEntity WHERE status='ACCEPTED'  ");

			if(!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) {
				builder.append(" AND dtOfTxn BETWEEN '"+dateFrom+"' AND '"+dateTo+"' ");
			}
			
			if (!payId.equalsIgnoreCase("All")) {
				builder.append("AND merchantPayId='" + payId + "'");
				
			}

			if (!acquirer.equalsIgnoreCase("All")) {
				builder.append("AND acqName='" + acquirer + "'");
			}

			logger.info("Query for accepted : " + builder.toString());

			return session.createQuery(builder.toString(), Long.class).getSingleResult();

		} catch (Exception e) {
			logger.error("Exception occure in accepted ,", e);
			e.printStackTrace();
		}
		return (long) 0;
	}

}
