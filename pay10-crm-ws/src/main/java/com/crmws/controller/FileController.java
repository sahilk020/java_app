package com.crmws.controller;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.service.FileService;
import com.crmws.service.FileStorageService;

@CrossOrigin
@RestController
public class FileController {

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	FileService fileService;

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/uploadFile")
	public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("payId") String payId,
			@RequestParam("caseId") String caseId, @RequestParam long id) {
		fileStorageService.storeFile(file, payId, caseId, id);
		return "File uploaded successfully";
	}

	@PostMapping("/uploadMultipleFiles")
	public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("payId") String payId,
			@RequestParam("caseId") String caseId, @RequestParam long id) {
		Arrays.asList(files).forEach(file -> fileStorageService.storeFile(file, payId, caseId, id));
		return "File uploaded successfully";
	}
	@PostMapping("/uploadFile/uploadPod/{userType}")
	public String uploadPODFile(@RequestParam("Podfile") MultipartFile file,@PathVariable String userType, @RequestParam("payId") String payId,
			@RequestParam("caseId") String caseId, @RequestParam long id) {
		fileStorageService.storeFileDMS(file, payId, caseId, id,"POD",userType);
		return "File uploaded successfully";
	}
	
	@PostMapping("/uploadFile/uploadInvoice/{userType}")
	public String uploadInvoiceFile(@RequestParam("invoiceFile") MultipartFile file,@PathVariable String userType, @RequestParam("payId") String payId,
			@RequestParam("caseId") String caseId, @RequestParam long id) {
		fileStorageService.storeFileDMS(file, payId, caseId, id,"Invoice",userType);
		return "File uploaded successfully";
	}
	
	@PostMapping("/uploadFile/uploadOthers/{userType}")
	public String uploadOthersFile(@RequestParam("file") MultipartFile file,@PathVariable String userType, @RequestParam("payId") String payId,
			@RequestParam("caseId") String caseId, @RequestParam long id) {
		fileStorageService.storeFileDMS(file, payId, caseId, id,"Others",userType);
		return "File uploaded successfully";
	}

	@GetMapping("/downloadFile")
	public ResponseEntity<Resource> downloadFile(@RequestParam String fileName, @RequestParam long id,
			HttpServletRequest request) {

		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName, id);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	@GetMapping("/downloadDoc/{cbCaseId}")
	public ResponseEntity<Resource> downloadFileDoc(@PathVariable String cbCaseId,
			HttpServletRequest request) {

		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResourceNew(cbCaseId);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	
	@GetMapping("/removeFile")
	public String deleteFile(@RequestParam String fileName, @RequestParam long id ,@RequestParam String userEmail) {
		fileStorageService.deletePathInDb(id, fileName,userEmail);
		return "File deleted successfully";
	}
	
}