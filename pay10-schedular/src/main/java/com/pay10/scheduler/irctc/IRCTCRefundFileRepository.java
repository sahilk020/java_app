package com.pay10.scheduler.irctc;

import com.pay10.commons.dao.HibernateAbstractDao;
import org.springframework.stereotype.Repository;

@Repository
public class IRCTCRefundFileRepository extends HibernateAbstractDao {
    @Override
    public void save(Object obj) {
        super.save(obj);
    }
}
