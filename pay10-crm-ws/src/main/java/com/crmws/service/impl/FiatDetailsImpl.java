package com.crmws.service.impl;

import com.pay10.commons.dao.FiatDetailsDao;
import com.pay10.commons.dto.FiatDetailsDTO;
import com.pay10.commons.entity.FiatDetails;
import com.crmws.service.FiatDetailService;
import com.pay10.commons.user.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FiatDetailsImpl implements FiatDetailService {


    @Autowired
    FiatDetailsDao fiatDetailsDao;
    @Autowired
    UserDao userDao;
    Logger logger= LoggerFactory.getLogger(FiatDetailsImpl.class.getName());

    @Override
    public void save(FiatDetailsDTO fiatDetailsDTO) {
        fiatDetailsDTO.setCreatedOn(new Date());

        FiatDetails fiatDetails=new FiatDetails();
        BeanUtils.copyProperties(fiatDetailsDTO,fiatDetails);
    logger.info("Value saving for{}: ",fiatDetails);
    fiatDetailsDao.saveOrUpdate(fiatDetails);
    }

    @Override
    public void delete(long id) {
        FiatDetails fiatDetails=new FiatDetails();
        fiatDetails.setId(id);
        fiatDetailsDao.delete(fiatDetails);
    }

    @Override
    public List<FiatDetailsDTO> getFiatDetails(String payId,String currency) {
        return fiatDetailsDao.getFiatList(payId,currency);

    }

//    @Override
//    public String checkMappingStatusForBank(String payId) {
//       List<String> currencyList=userDao.findCurrencyByPayId(payId);
//        for(String currency:currencyList){
//            if(fiatDetailsDao.checkCurrencyAccountMapping(payId,currency).equalsIgnoreCase(currency)){
//                return String.format("Fiat Details Mapping is missing for currency %s",currency);
//            }
//        }
//        return "SUCCESS";
//    }


}
