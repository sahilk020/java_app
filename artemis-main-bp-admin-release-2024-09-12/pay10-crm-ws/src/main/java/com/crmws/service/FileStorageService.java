package com.crmws.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.service.impl.ChargebackBulkMailServiceImpl;
import com.pay10.commons.repository.DMSEntity;
import com.pay10.commons.repository.DMSFileData;
import com.pay10.commons.repository.DMSRepository;
import com.pay10.commons.repository.DMSRepositoryForFileUpload;
import com.pay10.commons.user.Status;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.PropertiesManager;

@Service
public class FileStorageService {
	private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

	@Autowired
	private DMSRepository dmsRepository;
	@Autowired
	private DMSRepositoryForFileUpload dmsRepositoryForFileUpload;
	@Autowired
	private ChargebackBulkMailServiceImpl chargebackBulkMailServiceImpl;
	@Autowired
	private UserDao userDao;

	public String storeFile(MultipartFile file, String payId, String caseId, long id) {
		// Normalize file name
		String apppath = PropertiesManager.propertiesMap.get("DMS_PATH");

		System.out.println("apppath :   " + apppath);
		String fileName = null;
		Path path = Paths.get(apppath, payId, "chargeBack-" + caseId);
		try {
			Files.createDirectories(path);
			fileName = StringUtils.cleanPath(file.getOriginalFilename());
			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = path.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			logger.info("storeFile:: file stored successfully. payId={}, caseId={}, fileName={}", payId, caseId,
					fileName);
			Path filePath = Paths.get(path.toString(), fileName);
			updatePathInDb(filePath.toString(), payId, caseId,"");
			return fileName;
		} catch (IOException e) {
			e.printStackTrace();
			return fileName;
		}
	}

	public String storeFileDMS(MultipartFile file, String payId, String caseId, long id, String type,String userType) {
		// Normalize file name
		String apppath = PropertiesManager.propertiesMap.get("DMS_PATH");

		System.out.println("apppath :   " + apppath);
		String fileName = null;
		if(userType.equalsIgnoreCase("SUBUSER")) {
			User user=userDao.findByPayId(payId);
			payId=user.getParentPayId();
		}
		Path path = Paths.get(apppath, payId, "chargeBack-" + caseId);
		try {
			Files.createDirectories(path);
			fileName = StringUtils.cleanPath(file.getOriginalFilename());
			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = path.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			logger.info("storeFile:: file stored successfully. payId={}, caseId={}, fileName={}", payId, caseId,
					fileName);
			Path filePath = Paths.get(path.toString(), fileName);
			updatePathInDb(filePath.toString(), payId, caseId, type);
			
			if(!userType.equalsIgnoreCase("ADMIN") && !userType.equalsIgnoreCase("Sub Admin") && !userType.equalsIgnoreCase("Risk")) {
			int status=dmsRepository.updateStatusByCaseId(caseId);
			new Thread() {
				public void run() {
					DMSEntity dmsEntity= dmsRepository.findObjectByCbCaseId(caseId);
					chargebackBulkMailServiceImpl.sendChargeBackMail(dmsEntity);
				}
			}.start();
			logger.info("POD STATUS : "+status);
			}
			return fileName;
		} catch (IOException e) {
			e.printStackTrace();
			return fileName;
		}
	}

	private void updatePathInDb(String filePath, String payId, String caseId, String type) {
		// DMSEntity dmsEntity = dmsRepository.findById(id);

//		if (org.apache.commons.lang3.StringUtils.isNotBlank(dmsEntity.getFilePaths()) && Arrays
//				.asList(org.apache.commons.lang3.StringUtils.split(dmsEntity.getFilePaths(), ",")).contains(filePath)) {
//			return;
//		}
		// String updatedFilePaths =
		// org.apache.commons.lang3.StringUtils.isBlank(dmsEntity.getFilePaths()) ?
		// filePath
		// : org.apache.commons.lang3.StringUtils.join(dmsEntity.getFilePaths(), ",",
		// filePath);

		DMSFileData dmsFileEntity = new DMSFileData();

		dmsFileEntity.setFilePaths(filePath);
		dmsFileEntity.setCbCaseId(caseId);
		dmsFileEntity.setCreateDate(new Date().toGMTString());
		dmsFileEntity.setActiveFlag(true);
		dmsFileEntity.setCreatedBy(payId);
		dmsFileEntity.setDocType(type);
		// logger.info("Status..."+ dmsEntity.getStatus());
		dmsRepositoryForFileUpload.update(dmsFileEntity);
		logger.info("updatePathInDb:: file path updated in db. id={}, filePath={}, updatedFilePath={}", payId, filePath,
				caseId);
	}

	public void deletePathInDb(long id, String filePath, String userEmail) {

		logger.info("Case Id..." + id);
		logger.info("File Path..." + filePath);
		try {
			DMSEntity entity = dmsRepository.findById(id);
			List<DMSFileData> dmsfileEntityList = dmsRepositoryForFileUpload.findByCbCaseId(entity.getCbCaseId());
			if (dmsfileEntityList.size() > 0) {
				for (DMSFileData dmsfileEntity : dmsfileEntityList) {
					System.out.println(dmsfileEntity.toString());
					if (dmsfileEntity.getFilePaths().contains(filePath)) {
						System.out.println(dmsfileEntity.toString());
						dmsfileEntity.setUpdatedBy(userEmail);
						dmsfileEntity.setUpdatedDate(new Date().toGMTString());
						dmsfileEntity.setActiveFlag(false);
						dmsRepositoryForFileUpload.update(dmsfileEntity);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		DMSFileData dmsfileEntity = dmsRepositoryForFileUpload.findByCbCaseIdAndFile(String.valueOf(id), filePath);
//		if (dmsfileEntity != null) {
//			dmsfileEntity.setUpdatedBy(userEmail);
//			dmsfileEntity.setUpdatedDate(new Date().toGMTString());
//			dmsfileEntity.setActiveFlag(false);
//			dmsRepositoryForFileUpload.update(dmsfileEntity);
//		}
		// DMSEntity dmsEntity = dmsRepository.findById(id);

//		if (org.apache.commons.lang3.StringUtils.isBlank(dmsEntity.getFilePaths()) ) {
//			return;
//		}

		/*
		 * List<String> filePaths = new LinkedList<String>(Arrays
		 * .asList(org.apache.commons.lang3.StringUtils.split(dmsEntity.getFilePaths(),
		 * ","))); Optional<String> file = filePaths.stream().filter(path -> {
		 * logger.info("File Name..."+
		 * path.substring(path.lastIndexOf("/")+1,path.length())); return
		 * filePath.equalsIgnoreCase(path.substring(path.lastIndexOf("/")+1,path.length(
		 * ))); }).findFirst();
		 * 
		 * if(!file.isPresent()) { return; } logger.info("File Paths In Db..."+
		 * dmsEntity.getFilePaths()); logger.info("File..."+ file.get());
		 * filePaths.remove(file.get()); String updatedFilePaths = "";
		 * if(!CollectionUtils.isEmpty(filePaths)) { updatedFilePaths =
		 * org.apache.commons.lang3.StringUtils.join(filePaths, ",");
		 * logger.info("File Paths After Removal..."+ updatedFilePaths);
		 * dmsEntity.setFilePaths(updatedFilePaths); }else {
		 * logger.info("Empty File Paths After Removal Setting status as open");
		 * dmsEntity.setFilePaths(null); dmsEntity.setStatus(Status.OPEN); }
		 * 
		 * logger.info("Status..."+ dmsEntity.getStatus());
		 * //dmsRepository.update(dmsEntity);
		 */
		logger.info("removedPathInDb:: file path updated in db. id={}, filePath={}, removedFilePath={}", id, filePath);
	}

	public Resource loadFileAsResource(String fileName, long id) {
		try {

			DMSEntity entity = dmsRepository.findById(id);

			List<DMSFileData> dmsFileDataList = dmsRepositoryForFileUpload.findByCbCaseId(entity.getCbCaseId());
			String path = null;
			if (dmsFileDataList.size() > 0) {
				for (DMSFileData dmsFileData : dmsFileDataList) {
					if (dmsFileData.getFilePaths().contains(fileName)) {
						path = dmsFileData.getFilePaths();
					}

				}
			}

			System.out.println("Download File Path : " + path);
			Path filePath = Paths.get(path);
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}
	
	public Resource loadFileAsResourceNew(String cbCaseId) {
		try {

			DMSFileData dmsFileData = dmsRepositoryForFileUpload.findFileByCbCaseId(cbCaseId);
			String path = null;
			if (dmsFileData!=null) {
						path = dmsFileData.getFilePaths()!=null?dmsFileData.getFilePaths():null;
			}

			System.out.println("Download File Path : " + path);
			if(path!=null) {
				Path filePath = Paths.get(path);
				Resource resource = new UrlResource(filePath.toUri());
				if (resource.exists()) {
					return resource;
				} else {
					throw new MyFileNotFoundException("File not found " + path);
				}
			}else {
				throw new MyFileNotFoundException("File not found " + path);
			}
			
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + ex);
		}
	}
	
}