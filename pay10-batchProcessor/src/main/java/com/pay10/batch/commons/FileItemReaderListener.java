package com.pay10.batch.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.item.ItemReader;

/** Listener for the reading of an item from file.*/
public class FileItemReaderListener implements ItemReadListener<Object> {

	private static final Logger logger = LoggerFactory.getLogger(FileItemReaderListener.class);

	@Override
	public void beforeRead() {
		// Not Required
	}

	@Override
	public void afterRead(Object item) {
		// Not Required
	}

	/**
	 * Called if an error occurs while trying to read from file.
	 * @param ex thrown from {@link ItemReader}
	 */
	@Override
	public void onReadError(Exception ex) {
			logger.error("Encounter exception: " + ex.getMessage());
	}
}
