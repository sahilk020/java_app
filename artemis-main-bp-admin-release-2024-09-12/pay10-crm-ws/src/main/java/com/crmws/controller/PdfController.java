package com.crmws.controller;

import com.crmws.dto.PDFDTO;
import com.crmws.dto.ResponseEnvelope;
import com.crmws.service.PdfService;
import com.crmws.service.impl.PdfServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/pdf")
@CrossOrigin("*")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/mergePDF")
    public  ResponseEnvelope write(@RequestParam("file1") MultipartFile file1,
                                                  @RequestParam("file2") MultipartFile file2 , @RequestParam ("name") String name) {
        try {
            if (file1 != null && file2 != null) {
                PDFDTO pdfdto = new PDFDTO();
                pdfdto.setPdf1(file1);
                pdfdto.setPdf2(file2);
                pdfdto.setUser(name);
                return pdfService.mergePdf(pdfdto);

            } else if (file1 == null) {
                return new ResponseEnvelope(HttpStatus.BAD_REQUEST, "File1 can not be null!");
            } else {
                return new ResponseEnvelope(HttpStatus.BAD_REQUEST, "File2 can not be null!");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEnvelope(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());

        }

    }

    @GetMapping("/getAllMergedFiles/{name}")
    public ResponseEnvelope findAllMergerPDf(@PathVariable("name") String uname) {
       return (pdfService.findAllMergedFiles(uname));

    }

    @GetMapping("/downloadFile/{fileCode}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileCode") String fileCode,HttpServletRequest request) {
     
       
        	// Load file as Resource
    		Resource resource =  pdfService.getFileAsResource(fileCode);

    		// Try to determine file's content type
    		String contentType = null;
    		try {
    			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    		} catch (IOException ex) {
    		}

    		// Fallback to the default content type if type could not be determined
    		if (contentType == null) {
    			contentType = "application/octet-stream";
    		}

    		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
    				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
    				.body(resource);
    		
    		        }
    
    @GetMapping("/deleteFile/{fileName}")
    
   public ResponseEnvelope removeSelectedFile(@PathVariable String fileName,HttpServletRequest request ) {
    	return pdfService.removeSelectedFile(fileName ,"");
    	
    }
    
    @GetMapping("/test")
    public String testPdfController() {
        return "Service is running fine!!!!!!!!!!!!";

    }
}
