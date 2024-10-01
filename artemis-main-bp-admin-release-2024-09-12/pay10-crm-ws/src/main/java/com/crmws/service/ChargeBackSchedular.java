package com.crmws.service;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.repository.DMSEntity;
import com.pay10.commons.repository.DMSRepository;
import com.pay10.commons.user.Status;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ChargeBackSchedular {
    @Autowired
    private DMSRepository dmsrepo;
    private static final Logger logger = LoggerFactory.getLogger(ChargeBackSchedular.class.getName());
    public String closeChargeback() {

        try(Session session = HibernateSessionProvider.getSession();) {
            String query="FROM DMSEntity WHERE cbDdlineDate >'"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"' AND NOT status ='CLOSED'";
            Query query1 =session.createQuery(query);
            List<DMSEntity>dmsEntities1=query1.getResultList();
            logger.info("Chargeback list size"+ dmsEntities1.size());
            for (DMSEntity objects : dmsEntities1) {
                Transaction transaction=session.beginTransaction();
                objects.setStatus(Status.CLOSED);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                objects.setCbDdlineDate(simpleDateFormat.format(new Date()));
                session.save(objects);
                transaction.commit();
            }

            return "TOTAL "+dmsEntities1.size()+" CHARGEBACK CLOSED";
        } catch (Exception e) {
            logger.error("Exception Occur in closeChargeback() ",e);
            e.printStackTrace();
            return "TOTAL "+0+" CHARGEBACK CLOSED";
        }

    }
}

