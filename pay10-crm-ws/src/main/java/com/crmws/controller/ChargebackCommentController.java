package com.crmws.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.CommentDto;
import com.crmws.entity.ResponseMessage;
import com.crmws.service.CommentService;

@CrossOrigin
@RestController
public class ChargebackCommentController {

	@Autowired
	private CommentService commentService;

	@PostMapping("/commentSave")
	public ResponseEntity<ResponseMessage> save(@RequestBody CommentDto commnetDto) {

		ResponseMessage resp = commentService.save(commnetDto);
		return ResponseEntity.status(resp.getHttpStatus()).body(resp);
	}

	@RequestMapping(path = "/commentlist", method = RequestMethod.GET)
	public ResponseEntity<List<CommentDto>> getDMSdto(@RequestParam String caseId) {

		List<CommentDto> commentList = commentService.findAll(caseId);
		return new ResponseEntity<List<CommentDto>>(commentList, HttpStatus.OK);
	}

}
