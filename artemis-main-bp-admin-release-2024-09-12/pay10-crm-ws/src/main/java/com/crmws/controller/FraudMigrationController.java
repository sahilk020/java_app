package com.crmws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.service.FraudPreventationService;

@RestController
@RequestMapping("/fraud-migration")
public class FraudMigrationController {

	@Autowired
	private FraudPreventationService fraudPreventationService;

	@PostMapping("/migrate-config")
	public void migrateFraudConfigurations() {
		fraudPreventationService.migrateConfigurations();
	}

	// this API is used to migrate existing rules to all merchants.
	// it will migrate rule only which have payId = ALL.
	@PostMapping("/migrate-rules")
	public void migrateExistingRules() {
		fraudPreventationService.migrateRules();
	}
}
