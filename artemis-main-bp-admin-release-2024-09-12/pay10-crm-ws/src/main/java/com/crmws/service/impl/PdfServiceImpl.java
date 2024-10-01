package com.crmws.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.crmws.dto.PDFDTO;
import com.crmws.dto.ResponseEnvelope;
import com.crmws.service.PdfService;
import com.pay10.commons.util.PropertiesManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class PdfServiceImpl implements PdfService {
	private static String BASE_PDF_DIR = PropertiesManager.propertiesMap.get("PDF_FILES_BASE_PATH");
	private static String BASE_PDF_DOWNLOAD_DIR = PropertiesManager.propertiesMap.get("PDF_MERGE_PATH");

	@SuppressWarnings("deprecation")
	@Override
	public ResponseEnvelope mergePdf(PDFDTO pdfdto) {
		try {
			String fileName1 = uploadFile(pdfdto.getPdf1(), "PDF1");
			String fileName2 = uploadFile(pdfdto.getPdf2(), "PDF2");
			File file1 = new File(fileName1);
			File file2 = new File(fileName2);
			boolean b = false;
			String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss'.pdf'").format(new Date());
			PDFMergerUtility PDFmerger = new PDFMergerUtility();
			String location = BASE_PDF_DOWNLOAD_DIR + pdfdto.getUser() + "-" + fileName;
			log.info(location);
			File file = new File(location);
			if (file.createNewFile()) {
				PDFmerger.setDestinationFileName(location);
				PDFmerger.addSource(file1);
				PDFmerger.addSource(file2);
				PDFmerger.mergeDocuments();
				File des = new File(location);
				des.setReadable(true); //read
				des.setWritable(true); //write
				des.setExecutable(true);
				if (des.exists()) {
					removeSelectedFile(file1.getName(), BASE_PDF_DIR);
					removeSelectedFile(file2.getName(), BASE_PDF_DIR);
					return new ResponseEnvelope<>();
				} else {
					log.info("PDF Documents not merged");
					return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST, "PDF Documents not merged");
				}
			} else {
				log.info("File is already There");
				return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST, "File is already There");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return new ResponseEnvelope<>(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
		}
	}

	private String uploadFile(MultipartFile file, String fileName) {
		String returnFileName = null;
		try {
			Path path = Paths.get(BASE_PDF_DIR);
			Files.createDirectories(path);
			fileName += "_" + String.valueOf(System.currentTimeMillis()) + ".pdf";
			returnFileName = BASE_PDF_DIR + fileName;
			fileName = StringUtils.cleanPath(fileName);
			Path targetLocation = path.resolve(fileName);

			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return returnFileName;
	}

	// Download
	@Override
	public Resource getFileAsResource(String fileCode) {
		Resource resource = null;
		try {
			System.out.println("Download File Path : " + fileCode);
			Path filePath = Paths.get(BASE_PDF_DOWNLOAD_DIR + fileCode);
			resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resource;
	}

	@Override
	public ResponseEnvelope findAllMergedFiles(String name) {
		List<String> allFiles = new ArrayList<>();
		try {
			File folder = new File(BASE_PDF_DOWNLOAD_DIR);
			for (File file : folder.listFiles()) {
				if (file.getName().contains(name)) {
					ResponseEnvelope responseMessageForAllFiles = new ResponseEnvelope();
					allFiles.add(file.getName().toString());
				}
			}
			if (allFiles != null && allFiles.size() > 0) {
				Collections.sort(allFiles, (a, b) -> {
					return b.compareTo(a);
				});
				return new ResponseEnvelope<>(HttpStatus.OK, "Files found", allFiles);
			} else {
				return new ResponseEnvelope<>(HttpStatus.NOT_FOUND, "Files not found for the logged in user!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEnvelope<>(HttpStatus.INTERNAL_SERVER_ERROR, "Files not found for the logged in user!");
		}
	}

	// Delete file or directory if it exists
	public ResponseEnvelope removeSelectedFile(String fileName, String pathTofile) {
		try {
			Path path = null;
			if (pathTofile != null && pathTofile.length() > 0)
				path = Paths.get(pathTofile + fileName);
			else
				path = Paths.get(BASE_PDF_DOWNLOAD_DIR + fileName);
			if (Files.exists(path)) {
				Files.delete(path);
				return new ResponseEnvelope<>();
			} else {
				return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST, "File not found!");
			}
		} catch (Exception e) {
			return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST, "File not found!");
		}
	}
}
