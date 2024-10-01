package com.crmws.service.impl;

import com.crmws.service.PaymentMethodService;
import com.pay10.commons.dao.CurrencyCodeDao;
import com.pay10.commons.dto.PaymentMethod;
import com.pay10.commons.entity.CurrencyCode;
import com.pay10.commons.util.PaymentTypeUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class PaymentTypeServiceImpl  implements PaymentMethodService {
	private static final Logger logger = LoggerFactory.getLogger(PaymentTypeServiceImpl.class.getName());
	@Autowired
	private CurrencyCodeDao codeDao;

	@Override
	public ResponseEntity<PaymentMethod> getAllPaymentType(String channelType) {
		PaymentMethod method = new PaymentMethod();
		try {

			Map<String, String> treeMap = new TreeMap<>();

			if (channelType.equalsIgnoreCase("All")) {
				for (PaymentTypeUI paymentType : PaymentTypeUI.values()) {
					treeMap.put(paymentType.getCode(), paymentType.getName());
				}

				List<CurrencyCode> countries = codeDao.findByAll();

				countries.forEach(x -> treeMap.put(x.getCode(), x.getName()));

			} else if (channelType.equalsIgnoreCase("FIAT")) {
				for (PaymentTypeUI paymentType : PaymentTypeUI.values()) {
					treeMap.put(paymentType.getCode(), paymentType.getName());
				}
			} else {
				List<CurrencyCode> countries = codeDao.findByAll();

				countries.forEach(x -> treeMap.put(x.getCode(), x.getName()));

			}
			method.setHttpStatus(HttpStatus.OK);
			method.setRespmessage("Successfully");
			method.setMultipleResponse(treeMap);
			return ResponseEntity.status(method.getHttpStatus()).body(method);
		} catch (Exception e) {
			logger.error("getAllPaymentType () exception occur ", e);
			method.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			method.setRespmessage("Exception");
			return ResponseEntity.status(method.getHttpStatus()).body(method);
		}

	}

}
