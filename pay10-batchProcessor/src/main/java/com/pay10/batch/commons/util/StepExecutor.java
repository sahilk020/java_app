package com.pay10.batch.commons.util;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;

import com.pay10.commons.util.Constants;


public class StepExecutor {

	public static StepExecution getStepExecution() {
		StepContext context = StepSynchronizationManager.getContext();
		if (context == null) {
			return null;
		}
		return context.getStepExecution();
	}
	
	public static String getFileName() {
		StepExecution execution = getStepExecution();
		String fileName = null;
		if (execution != null) {
			fileName = execution.getJobParameters().getString(Constants.INPUT_FILE_NAME.getValue());
		}
		return fileName;
	}
}
