package com.crmws.worker;

import com.pay10.commons.service.intf.ResetMerchantKeyService;
import com.pay10.commons.dao.ResetMerchantKeyDao;
import com.pay10.commons.entity.ResetMerchantKey;
import com.pay10.commons.user.MerchantKeySalt;
import com.pay10.commons.user.MerchantKeySaltDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ResetMerchantApiKeyScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ResetMerchantApiKeyScheduler.class.getName());
    @Autowired
    ResetMerchantKeyService resetMerchantKeyService;
    @Autowired
    MerchantKeySaltDao merchantKeySaltDao;
    @Autowired
    ResetMerchantKeyDao resetMerchantKeyDao;

   @Scheduled(cron = "0 0 0 * * *")
    public void resetMerchantAPI() {
        logger.info("Initialization for API Reset Key start");
        resetMerchantAPIKey();
    }

    public void resetMerchantAPIKey() {
        Date date = new Date();
        List<ResetMerchantKey> updatingList = resetMerchantKeyDao.getUpdatingList(date);
        MerchantKeySalt merchantKeySalt = new MerchantKeySalt();
//        if(!updatingList.isEmpty()){
//            for(ResetMerchantKey resetKey: updatingList){
//                resetKey.setStatus("inactive");
//                resetMerchantKeyDao.updateRestMerchantEncryptionKeyStatus(resetKey,"Active");
//            }
//        }
        for (ResetMerchantKey key : updatingList) {
            resetMerchantKeyDao.updateStatus(key.getPayId(), "Active", "Inactive", "Admin");
            merchantKeySalt.setPayId(key.getPayId());
            merchantKeySalt.setSalt(key.getSalt());
            merchantKeySalt.setKeySalt(key.getKeySalt());
            merchantKeySalt.setEncryptionKey(key.getEncryptionKey());
            merchantKeySalt.setUpdatedBy("Admin");
            merchantKeySalt.setUpdatedOn(date);
            merchantKeySaltDao.saveOrUpdate(merchantKeySalt);
//            resetMerchantKeyDao.updateRestMerchantEncryptionKeyStatus(key, "Active");
//            updating status Inactive
//            resetMerchantKey.setPayId(payId);
//            resetMerchantKey.setEncryptionKey(encryptionKey);
//            resetMerchantKey.setSalt(salt);
//            resetMerchantKey.setKeySalt(keySalt);
            key.setStatus("Active");
            key.setUpdatedBy("Admin");
            key.setUpdatedOn(date);
            resetMerchantKeyDao.saveOrUpdate(key);
//          resetMerchantKey.setStatus("Active");
        }
    }
}