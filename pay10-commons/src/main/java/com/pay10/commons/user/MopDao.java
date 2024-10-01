package com.pay10.commons.user;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.exception.DataAccessLayerException;

@Component
public class MopDao extends HibernateAbstractDao {
	
	public MopDao() {
        super();
    }

	public void create(Mop mop) throws DataAccessLayerException {
        super.save(mop);
    }
	
	public void delete(Mop mop) throws DataAccessLayerException {
        super.delete(mop);
    }
	
	public void update(Mop mop) throws DataAccessLayerException {
        super.saveOrUpdate(mop);
    }
	
	@SuppressWarnings("rawtypes")
	public  List findAll() throws DataAccessLayerException{
	    return super.findAll(Mop.class);
	}
	 
	public Mop find(Long id) throws DataAccessLayerException {
	    return (Mop) super.find(Mop.class, id);
	}
	 
	public Mop find(String name) throws DataAccessLayerException {
	    return (Mop) super.find(Mop.class, name);
	}
	
}
