package com.crmws.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface IrctcReundService {

	boolean validateIrctcRefundFile(MultipartFile file, String email, StringBuffer respCode, String startsWith, StringBuffer fileDate);

	boolean validateIrctcNgetRefundFile(MultipartFile file, String email, String respMsg);

	boolean fileStore(MultipartFile file, String filename, String irctcRefundFileLocation);

	List<Map> refundSearch(String dateFrom, String FileName, String refundType, String canorderId,String orderId,String pgRefNum, String string);
}
