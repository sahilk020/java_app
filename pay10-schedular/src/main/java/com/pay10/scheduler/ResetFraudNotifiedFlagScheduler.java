package com.pay10.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.util.FraudPreventionObj;

@Component
public class ResetFraudNotifiedFlagScheduler {

	private static final Logger logger = LoggerFactory.getLogger(ResetFraudNotifiedFlagScheduler.class.getName());

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	@Scheduled(cron = "0 0 0 * * *")
	public void resetNotifiedFlag() {

		logger.info("resetNotifiedFlag:: Initialize reset fraud notification flag scheduler.");

		resetFlag(fraudPreventionMongoService.findNotifiedRules());
	}

	private void resetFlag(List<FraudPreventionObj> fraudDatas) {
		fraudDatas.forEach(rule -> {
			rule.setNotified(false);
			fraudPreventionMongoService.updateAdminFraudRule(rule);
		});

		logger.info("resetFlag:: completed. count={}", fraudDatas.size());
	}
}
