package com.pay10.errormapping.Impl;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.errormapping.ErrorMappingDAO;
import com.pay10.errormapping.ErrorMappingDTO;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ErrorMappingDAOImpl extends HibernateAbstractDao implements ErrorMappingDAO {
    private static Logger logger = LoggerFactory.getLogger(ErrorMappingDAOImpl.class.getName());

    @Override
  	@SuppressWarnings("unchecked")

      public ErrorMappingDTO getErrorMappingByPGCode(String pgCode, String acquier) {
      	 String query = "select ac.acquirer,ac.acq_status_code,ac.status_msg,ac.pg_code,pg.pg_msg,pg.pg_status" +
                   " from AcquirerCodeMapping ac join PgCodeMapping pg on ac.pg_code = pg.pg_code" +
                   " where ac.acq_status_code = '"+pgCode+"' and ac.acquirer = '"+acquier+"'";
           logger.info("Error Mapping query : "+query);
          ErrorMappingDTO errorMappingDTO = null;
          Session session = HibernateSessionProvider.getSession();
          Transaction tx = session.beginTransaction();
          try {

  			List<Object[]> rows = session.createQuery(query).getResultList();
                     

              for (Object[] row : rows) {
                  errorMappingDTO = new ErrorMappingDTO();
                  errorMappingDTO.setAcquirer(row[0].toString());
                  errorMappingDTO.setAcqStatusCode(row[1].toString());
                  errorMappingDTO.setStatusMsg(row[2].toString());
                  errorMappingDTO.setPgCode(row[3].toString());
                  errorMappingDTO.setPgMsg(row[4].toString());
                  errorMappingDTO.setPgStatus(row[5].toString());


              }
              logger.info("ERROR MAPPING DETAIS : " + errorMappingDTO);
              tx.commit();
          } catch (ObjectNotFoundException objectNotFound) {
              handleException(objectNotFound, tx);
          } catch (HibernateException hibernateException) {
              handleException(hibernateException, tx);
          } finally {
              autoClose(session);

          }
          return errorMappingDTO;

      }

     
      @SuppressWarnings("unchecked")
      @Override
      public ErrorMappingDTO getErrorMappingByAcqCode(String acquirerStatusCode, String acquier) {
          String query = "select ac.acquirer,ac.acq_status_code,ac.status_msg,ac.pg_code,pg.pg_msg,pg.pg_status" +
                  " from AcquirerCodeMapping ac join PgCodeMapping pg on ac.pg_code = pg.pg_code" +
                  " where ac.acq_status_code = '"+acquirerStatusCode+"' and ac.acquirer = '"+acquier+"'";
          logger.info("Error Mapping query : "+query);

          ErrorMappingDTO errorMappingDTO = null;
          Session session = HibernateSessionProvider.getSession();
          Transaction tx = session.beginTransaction();
          try {
             
  			List<Object[]> rows = session.createQuery(query).setCacheable(true).getResultList();

              logger.info("Error Mapping found : {}, {}",rows.size(),rows.toArray());

              for (Object[] row : rows) {
                  errorMappingDTO = new ErrorMappingDTO();
                  errorMappingDTO.setAcquirer(row[0].toString());
                  errorMappingDTO.setAcqStatusCode(row[1].toString());
                  errorMappingDTO.setStatusMsg(row[2].toString());
                  errorMappingDTO.setPgCode(row[3].toString());
                  errorMappingDTO.setPgMsg(row[4].toString());
                  errorMappingDTO.setPgStatus(row[5].toString());

              }


              logger.info(" ERROR MAPPING DETAILS : " + errorMappingDTO);

              tx.commit();
          } catch (ObjectNotFoundException objectNotFound) {
          	objectNotFound.printStackTrace();
              handleException(objectNotFound, tx);
          } catch (HibernateException hibernateException) {
          	hibernateException.printStackTrace();
              handleException(hibernateException, tx);
          }catch (Exception exception) {
          	exception.printStackTrace();
             // handleException(exception, tx);
          } finally {
              autoClose(session);
              // return responseUser;
              return errorMappingDTO;


          }


      }


}
