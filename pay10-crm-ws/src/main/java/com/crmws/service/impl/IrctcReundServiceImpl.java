package com.crmws.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.service.IrctcReundService;
import com.pay10.commons.dao.FieldsDao;

@Service
public class IrctcReundServiceImpl implements IrctcReundService {
	private static final Logger logger = LoggerFactory.getLogger(IrctcReundServiceImpl.class.getName());

	@Autowired
	FieldsDao fieldsDao;
	
	@Autowired
    MongoTemplate mongoTemplate;

	@Override
	public boolean validateIrctcRefundFile(MultipartFile file, String email, StringBuffer respMsg, String startsWith, StringBuffer fileDate) {

		if (file.isEmpty()) {
			respMsg.append("irct refund file should not be empty");
			logger.info(respMsg.toString());
			return true;
		} else if (fieldsDao.findIrctcRefundFileName(file.getOriginalFilename())) {
			respMsg.append("The same file has been uploaded already, Please try with another!");
			logger.info(respMsg.toString());
			return true;
		} else if (file.getOriginalFilename().lastIndexOf(".") > 0) {

			int index = file.getOriginalFilename().lastIndexOf(".");

			if (!file.getOriginalFilename().substring(index + 1).equalsIgnoreCase("txt")) {
				respMsg.append("Please Select Only `.txt` File Format");
				logger.info(respMsg.toString());
				return true;
			}

		}else if(!file.getOriginalFilename().startsWith(startsWith)){
			respMsg.append("Invalid file name");
			return true;
		}
		String[] names = file.getOriginalFilename().split("_");
		if(names.length-2<0){
			respMsg.append("Invalid file name");
			return true;
		}else{
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			String date = names[names.length-2];
			try {
				df.parse(names[names.length-2]);
			} catch (ParseException e) {
				respMsg.append("Invalid date");
				return true;
			}
			fileDate.append(date);
		}
		return false;
	}

	@Override
	public boolean validateIrctcNgetRefundFile(MultipartFile file, String email, String respMsg) {
		if (file.isEmpty()) {
			respMsg = "irct nget refund file should not be empty";
			return true;
		} else if (fieldsDao.findIrctcRefundFileName(file.getOriginalFilename())) {
			respMsg = "The same file has been uploaded already, Please try with another!";
			return true;
		} else if (file.getOriginalFilename().lastIndexOf(".") > 0) {

			int index = file.getOriginalFilename().lastIndexOf(".");

			if (!file.getOriginalFilename().substring(index + 1).equalsIgnoreCase("txt")) {
				respMsg = "Please Select Only text File Format";
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean fileStore(MultipartFile file, String fileName, String irctcRefundFileLocation) {
		logger.info("Request Received For IRCTC Refund File Upload "+irctcRefundFileLocation+", "+fileName);
		
		Path path = Paths.get(irctcRefundFileLocation);
		try {
			Files.createDirectories(path);
			Path targetLocation = path.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			logger.info("storeFile:: file stored successfully.fileName={}", fileName);
			return true;
		} catch (IOException e) {
			logger.info("IOException ", e);
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Map> refundSearch(String createDate, String fileName, String refundFlag, String irctcRefundCancelledId,String orderId,String pgRefNo, String flag) {
		Criteria criteria = new Criteria();
//        String pgRefNo = params.getOrDefault("pgRefNo", "");
//        String orderId = params.getOrDefault("orderId", "");
//        String irctcRefundCancelledId = params.getOrDefault("irctcRefundCancelledId", "");
//        String refundFlag = params.getOrDefault("refundFlag", "");
//        String createDate = params.getOrDefault("createDate", "");
//        String fileName = params.getOrDefault("fileName", "");
		logger.info(pgRefNo+createDate+fileName);
        List<Criteria> criteriaList = new ArrayList<>();
        if(!pgRefNo.isEmpty()){
        	logger.info(pgRefNo);
            criteriaList.add(Criteria.where("pgRefNO").is(pgRefNo));
        }
        if(!orderId.isEmpty()){
            criteriaList.add(Criteria.where("orderId").is(orderId));
        }
        if(!irctcRefundCancelledId.isEmpty()){
            criteriaList.add(Criteria.where("RefundOrderId").is(irctcRefundCancelledId));
        }
        if(!refundFlag.isEmpty()){
            criteriaList.add(Criteria.where("refundFlag").is(refundFlag));
        }
        if(!createDate.isEmpty()){
            //criteriaList.add(Criteria.where("createDate").gte(createDate+" 00:00:00").lte(createDate+" 23:59:59"));
        	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    		Date txnDate;
			try {
				txnDate = new SimpleDateFormat("yyyy-MM-dd").parse(createDate);
				String transactionDate = dateFormat.format(txnDate);
	        	criteriaList.add(Criteria.where("createDate").is(transactionDate));
			} catch (ParseException e) {
				logger.error("ParseException "+e);
			}  
    		
        }
        if(!fileName.isEmpty()){
            criteriaList.add(Criteria.where("fileName").is(fileName));
        }
        criteriaList.add(Criteria.where("refundFileType").is(flag));
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        List<Map> result = mongoTemplate.find(query, Map.class, "irctcRefundEntity");
        return result;
	}

}
