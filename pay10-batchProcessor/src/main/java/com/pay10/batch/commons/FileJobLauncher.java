package com.pay10.batch.commons;

import java.io.File;

import com.pay10.batch.commons.util.FileProcessing;
import com.pay10.commons.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

/**
 *  Encapsulating a Job(recoBatchJob) & its JobParameters(filename) to form a request for a job to be launched.
 *  Job is launched for a JobLaunchingMessageHandler which actually execute Spring Batch jobs by calling launch().
 */
public class FileJobLauncher {

	private static final Logger logger = LoggerFactory.getLogger(FileJobLauncher.class);

	private Job job;
	private String fileParameterName;

	public void setFileParameterName(String fileParameterName) {
		this.fileParameterName = fileParameterName;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	/* Will create a Job Launch Request
	 * There is no default Job launcher, so customized it for our use*/
	@Transformer
	public JobLaunchRequest toRequest(Message<File> message) {


		String fileName = message.getPayload().getAbsolutePath();
		JobLaunchRequest jobLaunchRequest ;
		String[] fullPath = fileName.split("/");
		String fName = fullPath[fullPath.length-1];

		MDC.put("FILE_NAME",fName);


		logger.info("Transforming input file to a job launcher with filename as its parameter. " + fileName);

		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

		jobParametersBuilder.addString(fileParameterName, message.getPayload().getAbsolutePath());
		jobParametersBuilder.addLong(Constants.DATE_TIME_STAMP.getValue(), System.currentTimeMillis());

		jobLaunchRequest = new JobLaunchRequest(job, jobParametersBuilder.toJobParameters());
		
		return jobLaunchRequest;
	}
}
