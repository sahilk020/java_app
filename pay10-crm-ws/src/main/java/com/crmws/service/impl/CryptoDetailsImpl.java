package com.crmws.service.impl;

import com.crmws.service.CryptoDetailsService;
import com.pay10.commons.dao.CryptoDetailsDao;
import com.pay10.commons.dto.CryptoDetailsDTO;
import com.pay10.commons.entity.CryptoDetails;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CryptoDetailsImpl implements CryptoDetailsService {
    Logger logger= LoggerFactory.getLogger(CryptoDetailsDao.class.getName());
    @Autowired
    CryptoDetailsDao cryptoDetailsDao;

    @Autowired
    UserDao userDao;
    @Autowired
    MultCurrencyCodeDao multCurrencyCodeDao;

    @Override
    public String save(CryptoDetailsDTO cryptoDetailsDTO) {
        logger.info("Save Details for "+cryptoDetailsDTO.getId()+" "+cryptoDetailsDTO.getPayId()+" "+cryptoDetailsDTO.getAddress()+" "+cryptoDetailsDTO.getBlockchain()+" "+cryptoDetailsDTO.getCurrency());
        LocalDateTime localDateTime=LocalDateTime.now();
        CryptoDetails cryptoDetails=new CryptoDetails();
        cryptoDetails.setId(cryptoDetailsDTO.getId());
        cryptoDetails.setPayId(cryptoDetailsDTO.getPayId());
        cryptoDetails.setCurrency(cryptoDetailsDTO.getCurrency());
        cryptoDetails.setAddress(cryptoDetailsDTO.getAddress());
        cryptoDetails.setBlockchain(cryptoDetailsDTO.getBlockchain());
        cryptoDetails.setCreatedBy(cryptoDetailsDTO.getPayId());
        cryptoDetails.setCreatedOn(localDateTime);
        logger.info("Save : {}", cryptoDetails);
        return cryptoDetailsDao.setCryptoDetails(cryptoDetails);
    }

    @Override
    public List<CryptoDetailsDTO> cryptoDetailsList(String payId, String currency) {
        List<CryptoDetails> cryptoDetailsList = cryptoDetailsDao.getCryptoDetails(payId,currency);
        List<CryptoDetailsDTO> cryptoDetailsDTOS = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < cryptoDetailsList.size(); i++) {
            CryptoDetailsDTO cryptoDetailsDTO = new CryptoDetailsDTO();
            cryptoDetailsDTO.setId(cryptoDetailsList.get(i).getId());
            cryptoDetailsDTO.setPayId(cryptoDetailsList.get(i).getPayId());
            cryptoDetailsDTO.setCurrency(cryptoDetailsList.get(i).getCurrency());
            cryptoDetailsDTO.setAddress(cryptoDetailsList.get(i).getAddress());
            cryptoDetailsDTO.setBlockchain(cryptoDetailsList.get(i).getBlockchain());
            cryptoDetailsDTO.setCreatedBy(cryptoDetailsList.get(i).getCreatedBy());
            cryptoDetailsDTO.setCreatedOn(dateTimeFormatter.format(cryptoDetailsList.get(i).getCreatedOn()));
            cryptoDetailsDTOS.add(cryptoDetailsDTO);
        }
        logger.info("Size of CryptoDetails Lise: "+cryptoDetailsDTOS.size());
        return cryptoDetailsDTOS;
    }

    @Override
    public String delete(long id,String currency) {
      logger.info("Id Deleted: "+id);
         return  cryptoDetailsDao.deleteCryptoDetails(id,currency);

    }

    @Override
    public Map<String, String> getCurrencyList(String payId) {
            List<String> currencyList=userDao.findCurrencyByPayId(payId);
            Map<String ,String> finalList=new HashMap<>();
            for(String currencyCode: currencyList){
                finalList.put(currencyCode,multCurrencyCodeDao.getCurrencyNamebyCode(currencyCode));
            }
        return finalList;
    }

//    @Override
//    public String checkCurrencyMappingStatus(String payId) {
//        List<String> currencyList=userDao.findCurrencyByPayId(payId);
//        for(String currency:currencyList){
//            logger.info("Currency from user Dao for PayId: "+payId+"Currency: "+currency);
//            if(cryptoDetailsDao.checkBankMappingStatus(payId,currency).equalsIgnoreCase(currency)){
//                return String.format("Crypt Details are missing for %s",currency) ;
//            }
//        }
//        return "SUCCESS";
//    }
}
