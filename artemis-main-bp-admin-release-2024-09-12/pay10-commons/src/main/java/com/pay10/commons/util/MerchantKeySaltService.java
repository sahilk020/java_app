package com.pay10.commons.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.MerchantKeySalt;
import com.pay10.commons.user.MerchantKeySaltDao;

@Service("merchantKeySaltService")
public class MerchantKeySaltService {
	private static Logger logger = LoggerFactory.getLogger(MerchantKeySaltService.class.getName());
	public static Map<String, MerchantKeySalt> merchantKeySaltMap = new HashMap<String, MerchantKeySalt>();

	@Autowired
	MerchantKeySaltDao merchantKeySaltDao;

	@SuppressWarnings("unchecked")
	public MerchantKeySalt getMerchantKeySalt(String payId) {
		try {
			if (merchantKeySaltMap.size() < 1) {
				List<MerchantKeySalt> merchantKeySaltList = merchantKeySaltDao.findAll();
				merchantKeySaltList.forEach(merchantKeySalt -> {
					try {
						merchantKeySalt.setEncryptionKey(Hasher
								.getHash(merchantKeySalt.getKeySalt() + merchantKeySalt.getPayId()).substring(0, 32));
					} catch (SystemException e) {
						logger.error("GetMerchantKeySalt Exception : " + e);
						e.printStackTrace();
					}
					merchantKeySaltMap.put(merchantKeySalt.getPayId(), merchantKeySalt);
				});
			}

			if (merchantKeySaltMap.containsKey(payId)) {
				return merchantKeySaltMap.get(payId);
			} else {
				MerchantKeySalt merchantKeySalt = merchantKeySaltDao.find(payId);
				if (null != merchantKeySalt) {
					merchantKeySalt.setEncryptionKey(
							Hasher.getHash(merchantKeySalt.getKeySalt() + merchantKeySalt.getPayId()).substring(0, 32));
					merchantKeySaltMap.put(payId, merchantKeySalt);

					return merchantKeySalt;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Salt not found " + e);
		}

		return new MerchantKeySalt();
	}


	public String getSalt(String payId) {

		try {
			String salt = merchantKeySaltMap.get(payId) !=null ? merchantKeySaltMap.get(payId).getSalt() : null ;
			if (null == salt || salt.isEmpty()) {
				MerchantKeySalt merchantKeySalt = new MerchantKeySaltDao().find(payId);
				if (null != merchantKeySalt) {
					merchantKeySalt.setEncryptionKey(
							Hasher.getHash(merchantKeySalt.getKeySalt() + merchantKeySalt.getPayId()).substring(0, 32));
					merchantKeySaltMap.put(payId, merchantKeySalt);
					return merchantKeySalt.getSalt();
				}

			}
			return salt;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Salt not found ");
		}

		return null;
	}
}
