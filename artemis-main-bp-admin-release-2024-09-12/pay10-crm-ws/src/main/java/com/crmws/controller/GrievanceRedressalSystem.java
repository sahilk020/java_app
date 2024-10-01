package com.crmws.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.entity.ResponseMessage;
import com.crmws.entity.ResponseMessageForGRS;
import com.crmws.service.impl.GrievanceRedressalSystemServiceImpl;
import com.pay10.commons.dto.GrievanceRedressalSystemDto;
import com.pay10.commons.dto.GrsIssueHistoryDto;

@CrossOrigin("*")
@RestController

@RequestMapping("GRS")
public class GrievanceRedressalSystem {

	private static final Logger logger = LoggerFactory.getLogger(GrievanceRedressalSystem.class.getName());

	@Autowired
	private GrievanceRedressalSystemServiceImpl grievanceRedressalSystemServiceImpl;

	@PostMapping("/UploadDoc/{grsId}/{emailId}")
	public ResponseMessage uploadFile(@PathVariable String grsId, @PathVariable String emailId,
			@RequestParam("file") MultipartFile file) {
		return grievanceRedressalSystemServiceImpl.uploadDoc(grsId, emailId, file);

	}

	@PostMapping("/SaveGrievance")
	public ResponseMessage saveGrievance(@RequestBody GrievanceRedressalSystemDto dto) {
		return grievanceRedressalSystemServiceImpl.saveGrievance(dto);

	}

	@PostMapping("/SaveGrievanceOther")
	public ResponseMessage saveGrievanceOther(@RequestBody GrievanceRedressalSystemDto dto) {
		return grievanceRedressalSystemServiceImpl.saveGrievanceOther(dto);
	}

	@PostMapping("/SaveGrievanceWebsite")
	public ResponseMessage saveGrievanceWebsite(@RequestBody GrievanceRedressalSystemDto dto) {
		return grievanceRedressalSystemServiceImpl.saveGrievanceWebsite(dto);

	}

	@PostMapping("/closeGrievance")
	public ResponseMessage closeGrievance(@RequestBody GrievanceRedressalSystemDto dto) {
		return grievanceRedressalSystemServiceImpl.closeGrievance(dto);

	}

	@PostMapping("/reOpenGrievance")
	public ResponseMessage reOpenGrievance(@RequestBody GrievanceRedressalSystemDto dto) {
		return grievanceRedressalSystemServiceImpl.reOpenGrievance(dto);

	}

	@GetMapping("/findAllGRS/{payId}/{status}/{dateFrom}/{dateTo}")
	public ResponseMessageForGRS findAllGRS(@PathVariable String payId, @PathVariable String status,
			@PathVariable String dateFrom, @PathVariable String dateTo) {
		return grievanceRedressalSystemServiceImpl.findAllGRS(payId, status, dateFrom, dateTo);
	}

	@PostMapping("/inProcess")
	public ResponseMessage inProcess(@RequestBody GrievanceRedressalSystemDto dto) {
		return grievanceRedressalSystemServiceImpl.inProcess(dto);
	}

	@GetMapping("/findGRSHistoryById/{grsId}")
	public ResponseMessageForGRS findAllGRSHistoryById(@PathVariable String grsId) {
		return grievanceRedressalSystemServiceImpl.findGRSHistoryById(grsId);
	}

	@PostMapping("/SaveDescription")
	public ResponseMessage saveDescription(@RequestBody GrsIssueHistoryDto dto) {
		return grievanceRedressalSystemServiceImpl.saveDescription(dto);
	}

	@GetMapping("/getDescHistory")
	public List<GrsIssueHistoryDto> getDescHistory(@RequestParam("GRS_ID") String GRSID) {
		return grievanceRedressalSystemServiceImpl.getDescription(GRSID);
	}

}