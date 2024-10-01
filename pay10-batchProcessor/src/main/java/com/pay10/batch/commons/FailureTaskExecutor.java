package com.pay10.batch.commons;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pay10.commons.util.Constants;


/**
 * This step will be executed if recoBatchStep step fails.
 * 
 * @param contribution mutable state to be passed back to update the current step execution.
 * @param chunkContext attributes shared between invocations but not between restarts.
 * @exception On failure throws an exception.
 * @return an RepeatStatus.FINISHED if no exception.
 */
public class FailureTaskExecutor implements Tasklet {

	private static final Logger logger = LoggerFactory.getLogger(FailureTaskExecutor.class);
	
	private FileLocker fileLocker;

	private String errorDrive;

	public void setFileLocker(FileLocker fileLocker) {
		this.fileLocker = fileLocker;
	}

	public void setErrorDrive(String errorDrive) {
		this.errorDrive = errorDrive;
	}

	/*Moving file from Inbound directory to Failure/Error directory*/
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String readLocation = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters()
				.getString(Constants.INPUT_FILE_NAME.getValue());
		if (readLocation != null) {
			logger.info("File: " + readLocation + " has issues in processing, moving it to failure location.");
			File errorLocation = new File(errorDrive);
			if (!errorLocation.exists()) {
				errorLocation.mkdirs();
			}
			File read = new File(readLocation);
			File write = new File(errorDrive, read.getName());
			try {
				Files.move(read.toPath(), write.toPath(), StandardCopyOption.REPLACE_EXISTING);
				fileLocker.deleteLockFileFromDirectory(readLocation);
				logger.info("Moved file to failure Location:  ---  " + write);
			} catch (IOException ioException) {
				logger.error("Exception while moving file: " + read.getName() + " with errors to Failure Location: "
						+ errorLocation);
				throw ioException;
			}
		}
		return RepeatStatus.FINISHED;
	}

}
