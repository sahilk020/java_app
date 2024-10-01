package com.pay10.commons.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pay10.commons.entity.NeutrinoDetails;
import com.pay10.commons.exception.DataAccessLayerException;

@Service
public class NeutrinoDetailsDao extends HibernateAbstractDao {

	public void create(NeutrinoDetails neutrinoDetails) throws DataAccessLayerException {
		super.save(neutrinoDetails);
	}

	public void delete(NeutrinoDetails neutrinoDetails) throws DataAccessLayerException {
		super.delete(neutrinoDetails);
	}

	public NeutrinoDetails find(Long id) throws DataAccessLayerException {
		return (NeutrinoDetails) super.find(NeutrinoDetails.class, id);
	}

	@SuppressWarnings("unchecked")
	public NeutrinoDetails find() throws DataAccessLayerException {
		List<NeutrinoDetails> details = super.findAll(NeutrinoDetails.class);
		return details.size() > 0 ? details.get(0) : null;
	}

	public void update(NeutrinoDetails neutrinoDetails) throws DataAccessLayerException {
		super.saveOrUpdate(neutrinoDetails);
	}
}
