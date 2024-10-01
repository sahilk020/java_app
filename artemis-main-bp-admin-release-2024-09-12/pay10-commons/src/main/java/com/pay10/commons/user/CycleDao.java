package com.pay10.commons.user;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;

@Component
public class CycleDao extends HibernateAbstractDao {

	@SuppressWarnings("unchecked")
	public List<CycleMaster> findAll() {
		return super.findAll(CycleMaster.class);
	}

}
