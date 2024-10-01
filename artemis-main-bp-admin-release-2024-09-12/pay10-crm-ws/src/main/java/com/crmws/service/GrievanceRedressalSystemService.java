package com.crmws.service;



import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.crmws.entity.ResponseMessage;
import com.crmws.entity.ResponseMessageForGRS;
import com.pay10.commons.dto.GrievanceRedressalSystemDto;
import com.pay10.commons.dto.GrsIssueHistoryDto;

public interface GrievanceRedressalSystemService {
	
	public ResponseMessage uploadDoc(String grsId,String emailId,MultipartFile file);
	public ResponseMessage saveGrievance(GrievanceRedressalSystemDto dto );
	//public ResponseMessage saveGrievanceOther(GrievanceRedressalSystemDto dto );
	public ResponseMessage saveGrievanceWebsite(GrievanceRedressalSystemDto dto );
	public ResponseMessage closeGrievance(GrievanceRedressalSystemDto dto );
	public ResponseMessage reOpenGrievance(GrievanceRedressalSystemDto dto );
	public ResponseMessageForGRS findAllGRS(String payId, String status, String dateFrom, String dateTo);
	public ResponseMessage inProcess(GrievanceRedressalSystemDto dto );
	public ResponseMessageForGRS findGRSHistoryById(String grsID);
	public ResponseMessage saveGrievanceOther(GrievanceRedressalSystemDto dto);
	public ResponseMessage saveDescription(GrsIssueHistoryDto dto) throws IOException;
	public List<GrsIssueHistoryDto> getDescription(String GRSID );
}
