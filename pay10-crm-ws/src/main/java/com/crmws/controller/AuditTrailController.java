package com.crmws.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.service.AuditTrailsService;

@RestController
@RequestMapping("/audittrails")
public class AuditTrailController {

	@Autowired
	private AuditTrailsService auditTrailsService;

	@GetMapping("highlightdiff/{auditId}")
	public ResponseEntity<String> highlightDiff(@PathVariable long auditId) {
		return ResponseEntity.ok(auditTrailsService.diffHighlight(auditId));
	}

	@GetMapping("highlightdiff/pdf/{auditId}")
	public ResponseEntity<Resource> generateErrorExcelFile(@PathVariable long auditId) {
		String filename = StringUtils.join("diff-", auditId, ".pdf");
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/pdf"))
				.body(new InputStreamResource(auditTrailsService.generatePdf(auditId)));
	}
}
