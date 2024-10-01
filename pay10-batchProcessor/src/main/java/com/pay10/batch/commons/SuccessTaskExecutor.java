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
 * This step will be executed if recoBatchStep step successfully completed.
 * 
 * @param contribution mutable state to be passed back to update the current step execution.
 * @param chunkContext attributes shared between invocations but not between restarts.
 * @exception On failure throws an exception.
 * @return an RepeatStatus.FINISHED if no exception.
 */
public class SuccessTaskExecutor implements Tasklet {

	private static final Logger logger = LoggerFactory.getLogger(SuccessTaskExecutor.class);
	
	private FileLocker fileLocker;

	private String successDrive;

	public void setFileLocker(FileLocker fileLocker) {
		this.fileLocker = fileLocker;
	}

	public void setSuccessDrive(String successDrive) {
		this.successDrive = successDrive;
	}

	/*Moving file from Inbound directory to Success directory*/
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String readLocation = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters()
				.getString(Constants.INPUT_FILE_NAME.getValue());
		if (readLocation != null) {
			logger.info("File: " + readLocation + " is successfully processed, moving it to success location.");
			File successLocation = new File(successDrive);
			if (!successLocation.exists()) {
				successLocation.mkdirs();
			}
			File read = new File(readLocation);
			File write = new File(successDrive, read.getName());
			try {
				Files.move(read.toPath(), write.toPath(), StandardCopyOption.REPLACE_EXISTING);
				fileLocker.deleteLockFileFromDirectory(readLocation);
				logger.info("Moved file to success Location:  ---  " + write);
			} 
			catch (IOException ioException) {
				logger.error("Exception while moving successfully read file: " + read.getName() + " to success Location: " + successLocation);
				throw ioException;
			}
		}
		return RepeatStatus.FINISHED;
	}

}
