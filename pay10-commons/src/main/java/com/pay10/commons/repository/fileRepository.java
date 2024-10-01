package com.pay10.commons.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.exception.DataAccessLayerException;
@Component("filepro")
public class fileRepository  extends HibernateAbstractDao{
	private static Logger logger = LoggerFactory.getLogger(DMSRepository.class.getName());

	public void save(Fileentitiy fileentitiy) throws DataAccessLayerException {
		logger.info("********************************************************"+fileentitiy.toString());
		super.save(fileentitiy);
	}

}
