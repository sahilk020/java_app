package com.pay10.commons.service.impl;

import com.pay10.commons.service.intf.ResetMerchantKeyService;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.dao.ResetMerchantKeyDao;
import com.pay10.commons.dto.ResetMerchantKeyDTO;
import com.pay10.commons.entity.ResetMerchantKey;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.SaltFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResetMerchantKeyImpl implements ResetMerchantKeyService {
    @Autowired
    ResetMerchantKeyDao resetMerchantKeyDao;

    Logger logger = LoggerFactory.getLogger(ResetMerchantKeyImpl.class.getName());
    @Override
    public List<ResetMerchantKeyDTO> getMerchantResetEncryptionKeyList(String payId) {
        List<ResetMerchantKeyDTO> resetMerchantKeys=resetMerchantKeyDao.findListById(payId).stream().map((rmk)->{
            ResetMerchantKeyDTO resetMerchantKeyDTO = new ResetMerchantKeyDTO();
            resetMerchantKeyDTO.setId(rmk.getId());
            resetMerchantKeyDTO.setPayId(rmk.getPayId());

            try {
                resetMerchantKeyDTO.setEncryptionKey(Hasher.getHash(rmk.getKeySalt()+rmk.getPayId()).substring(0,32));
            } catch (SystemException e) {
                throw new RuntimeException(e);
            };
            resetMerchantKeyDTO.setKeySalt(rmk.getKeySalt());
            resetMerchantKeyDTO.setSalt(rmk.getSalt());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            resetMerchantKeyDTO.setStartDate(dateFormat.format(rmk.getStartDate()));
            resetMerchantKeyDTO.setEndDate(rmk.getEndDate()==null? "" : dateFormat.format(rmk.getEndDate()));
            resetMerchantKeyDTO.setStatus(rmk.getStatus());
            return resetMerchantKeyDTO;
        }).collect(Collectors.toList());
        logger.info("{}",resetMerchantKeys);
        return resetMerchantKeys;
    }

    @Override
    public void saveResetEncryptionkeyList(String payId,String encyKey, String salt,String keySalt,String date, String time) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dateFinal;
        String dateWithTime=date+" "+time;
        try {
            dateFinal= simpleDateFormat.parse(dateWithTime);
        } catch (ParseException e) {
            throw new RuntimeException("Date Time Format: "+e);
        }
        ResetMerchantKey resetMerchantKey=new ResetMerchantKey();
        resetMerchantKey.setPayId(payId);
        resetMerchantKey.setEncryptionKey(encyKey);
        resetMerchantKey.setSalt(salt);
        resetMerchantKey.setKeySalt(keySalt);
        resetMerchantKey.setStartDate(dateFinal);
        resetMerchantKey.setCreatedBy(resetMerchantKey.getPayId());
        resetMerchantKey.setCreatedOn(new Date());
        resetMerchantKey.setStatus("Pending");
        resetMerchantKeyDao.saveOrUpdate(resetMerchantKey);
    }


    @Override
    public List<String> getEncryptionKey(String payId) {
        String newEncryptionKey="";
        String salt=SaltFactory.generateRandomSalt();
        String keySalt=SaltFactory.generateRandomSalt();
         try {
            logger.info(keySalt);
            newEncryptionKey=Hasher.getHash(keySalt+payId).substring(0,32);
             logger.info(newEncryptionKey);
        } catch (SystemException e) {
            logger.info(""+e);
            throw new RuntimeException(e);
        }
        List<String>saltAndEncryptionKey=new ArrayList<>();
        saltAndEncryptionKey.add(salt);
        saltAndEncryptionKey.add(keySalt);
        saltAndEncryptionKey.add(newEncryptionKey);
         return saltAndEncryptionKey;
    }




}
