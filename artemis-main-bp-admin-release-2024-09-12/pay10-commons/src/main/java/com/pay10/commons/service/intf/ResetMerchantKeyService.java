package com.pay10.commons.service.intf;

import com.pay10.commons.dao.ResetMerchantKeyDao;
import com.pay10.commons.dto.ResetMerchantKeyDTO;
import com.pay10.commons.entity.ResetMerchantKey;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

public interface ResetMerchantKeyService {

    List<ResetMerchantKeyDTO>getMerchantResetEncryptionKeyList(String payId);
    void saveResetEncryptionkeyList(String payId, String encyKey, String salt, String keySalt, String date, String time);
    List<String> getEncryptionKey(String payId);

}
