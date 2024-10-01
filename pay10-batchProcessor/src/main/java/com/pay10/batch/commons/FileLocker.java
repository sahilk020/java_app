package com.pay10.batch.commons;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.locking.AbstractFileLockerFilter;
import org.springframework.integration.file.locking.NioFileLocker;

public class FileLocker extends AbstractFileLockerFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(FileLocker.class);
	
	private NioFileLocker nioLocker;
	
	private String lockExtension;
	
	//private static String fileName;

	public void setNioLocker(NioFileLocker nioLocker) {
		this.nioLocker = nioLocker;
	}

	public void setLockExtension(String lockExtension) {
		this.lockExtension = lockExtension;
	}

	/*public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String fileName) {
		FileLocker.fileName = fileName;
	}*/

	@Override
	public boolean lock(File file) {
		synchronized (file) {
			File fileToLock = checkIfLockFileExistInDirectory(file);
			logger.info("Obtaining a lock on file: " + fileToLock);
			boolean isLock = nioLocker.lock(fileToLock);
			if(!isLock) {
				unlock(fileToLock);
			} 
			//setFileName(file.getName());
			return isLock;
		}
	}

	public void deleteLockFileFromDirectory(String fileToLock) {
		String lockFileName = fileToLock + lockExtension;
		File lockFile = new File(lockFileName);
		try {
			logger.info("Unlock and Delete lock file for: " + fileToLock);
			unlock(lockFile);
			Files.deleteIfExists(lockFile.toPath());
		} catch (IOException e) {
			unlock(lockFile);
			logger.error("Error while deleting the lock file: " + lockFileName);
			e.printStackTrace();
		}
	}

	private File checkIfLockFileExistInDirectory(File fileToLock) {
		String lockFileName = fileToLock + lockExtension;
		logger.info("Checking if lock file exist for: " + fileToLock);
		File lockFile = new File(lockFileName);
		if (!Files.exists(lockFile.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			lockFile.deleteOnExit();
			try {
				logger.info("Create a new lock file as it was not present for: " + fileToLock);
				Files.createFile(lockFile.toPath());
			} catch (IOException e) {
				logger.error("Error while creating the lock file: " + lockFileName);
				e.printStackTrace();
			}
		}
		return lockFile;
	}
	
	private File createLockFile(File fileToLock) {
		String lockFileName = fileToLock + lockExtension;
		//logger.info("Checking if lock file exist for: " + fileToLock);
		File lockFile = new File(lockFileName);
		//if (!Files.exists(lockFile.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			lockFile.deleteOnExit();
			try {
				logger.info("Create a new lock file as it was not present for: " + fileToLock);
				Files.createFile(lockFile.toPath());
			} catch (IOException e) {
				logger.error("Error while creating the lock file: " + lockFileName);
				e.printStackTrace();
			}
		//}
		return lockFile;
	}

	@Override
	public boolean isLockable(File file) {
		String lockFileName = file + lockExtension;
		File lockFile = new File(lockFileName);
		return nioLocker.isLockable(lockFile);
	}

	@Override
	public void unlock(File fileToUnlock) {
		nioLocker.unlock(fileToUnlock);		
	}
}
