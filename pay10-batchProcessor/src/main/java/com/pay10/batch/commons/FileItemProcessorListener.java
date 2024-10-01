package com.pay10.batch.commons;

import com.pay10.batch.commons.util.StepExecutor;
import com.pay10.batch.reco.RecoBatchProcessor;
import com.pay10.commons.util.FieldType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
	
/**
 * Listener for the processing of an item. Notified before and after an item is
 * passed to the {@link RecoBatchProcessor} and in the event of any exceptions
 * thrown by the processor.
 */
public class FileItemProcessorListener implements ItemProcessListener<Fields, Fields> {

	private static final Logger logger = LoggerFactory.getLogger(FileItemProcessorListener.class);

	@Override
	public void beforeProcess(Fields item) {
		// Not Required
	}

	@Override
	public void afterProcess(Fields item, Fields result) {
		// Not Required
	}

	/*Called if an error occurs while trying to process the file.*/
	@Override
	public void onProcessError(Fields item, Exception e) {
		String lNumber = item.get(FieldType.FILE_LINE_NO.getName());
		String lData = item.get(FieldType.FILE_LINE_DATA.getName());
		String errorMsg = "Validation error: " + e.getMessage() + " at line: " + lNumber + ", input=[" + lData + "]";
		String fileName = StepExecutor.getFileName();
		if (fileName != null) {
			errorMsg = errorMsg + ", in resource=[" + fileName + "]";
		}
		logger.error(errorMsg);
	}

}