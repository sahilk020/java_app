package com.crmws.service;

import java.io.File;
import java.util.List;

import com.crmws.dto.PDFDTO;
import com.crmws.dto.ResponseEnvelope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

public interface PdfService {


    ResponseEnvelope  mergePdf(PDFDTO pdfdto);
    ResponseEnvelope findAllMergedFiles(String name);
    ResponseEnvelope removeSelectedFile(String fileName,String path);
    Resource getFileAsResource(String fileCode);
}
