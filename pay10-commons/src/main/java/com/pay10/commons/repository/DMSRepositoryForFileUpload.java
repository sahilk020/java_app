package com.pay10.commons.repository;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Component("dmsrepofileupload")
public class DMSRepositoryForFileUpload extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(DMSRepositoryForFileUpload.class.getName());

	public void save(DMSFileData dmsFileEntity) throws DataAccessLayerException {
		super.save(dmsFileEntity);
	}

	@SuppressWarnings("unchecked")
	public List<DMSFileData> findAll() {
		return super.findAll(DMSFileData.class);
	}

	public DMSFileData findById(long id) {
		logger.info("id..." + id);
		return (DMSFileData) super.find(DMSFileData.class, id);
	}

	
	@SuppressWarnings("unchecked")
	public List<DMSFileData> findAllByCaseId(String caseId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createQuery("FROM DMSFileData WHERE  cbCaseId=:cbCaseId and activeFlag is true").setParameter("cbCaseId", caseId)
					.getResultList();
		} finally {
			autoClose(session);
		}
	}
	@SuppressWarnings("unchecked")
	public List<DMSFileData> findByPayId(String payId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createQuery("FROM DMSFileData WHERE merchantPayId=:payId").setParameter("payId", payId)
					.getResultList();
		} finally {
			autoClose(session);
		}
	}

	
	public DMSFileData findByCbCaseIdAndFile(String cbCaseId,String file) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (DMSFileData) session.createQuery("FROM DMSFileData WHERE cbCaseId=:cbCaseId and filePaths=:file and activeFlag=true")
					.setParameter("cbCaseId", cbCaseId).setParameter("file", file).getSingleResult();
		} finally {
			autoClose(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DMSFileData> findByCbCaseId(String cbCaseId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return  session.createQuery("FROM DMSFileData WHERE cbCaseId=:cbCaseId and activeFlag is true")
					.setParameter("cbCaseId", cbCaseId).getResultList();
		} finally {
			autoClose(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	public DMSFileData findFileByCbCaseId(String cbCaseId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return  (DMSFileData)session.createQuery("FROM DMSFileData WHERE cbCaseId=:cbCaseId and activeFlag is true")
					.setParameter("cbCaseId", cbCaseId).getSingleResult();
		} finally {
			autoClose(session);
		}
	}

	public void update(DMSFileData dmsFileEntity) {
		dmsFileEntity.setId(dmsFileEntity.getId());
		super.saveOrUpdate(dmsFileEntity);
	}

	public void saveAll(List<DMSFileData> dmsFileEntitys) {
		dmsFileEntitys.forEach(dmsFileEntity -> {
			save(dmsFileEntity);
		});
	}
}
