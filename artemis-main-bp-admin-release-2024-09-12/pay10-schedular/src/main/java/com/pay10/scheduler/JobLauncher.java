package com.pay10.scheduler;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.pay10.scheduler.commons.ConfigurationProvider;
import com.pay10.scheduler.jobs.TransactionStatusEnquiryJob;
import com.pay10.commons.util.Constants;

//@Service
public class JobLauncher {

	@Autowired
	private ConfigurationProvider configurationProvider;

	@Bean
	public JobDetail transactionStatusEnquiryJobDetails() {
		if (configurationProvider.getTransactionStatusEnquiry().equalsIgnoreCase("1")) {
			return JobBuilder.newJob(TransactionStatusEnquiryJob.class).withIdentity("statusEnquiry")
					.usingJobData(Constants.RESPONSE_MESSAGE_TEXT.getValue(), "transactionStatusEnquiry").storeDurably()
					.build();
		} else {
			return null;
		}

	}

	@Bean
	public Trigger transactionStatusEnquiryJobTrigger() {

		if (configurationProvider.getTransactionStatusEnquiry().equalsIgnoreCase("1")) {

			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
					.cronSchedule(configurationProvider.getTransactionBankStatusEnquiryCron());

			return TriggerBuilder.newTrigger().withSchedule(scheduleBuilder)
					.forJob(transactionStatusEnquiryJobDetails()).withIdentity("statusEnquiryJobTrigger").build();
		} else {
			return null;
		}

	}


}
