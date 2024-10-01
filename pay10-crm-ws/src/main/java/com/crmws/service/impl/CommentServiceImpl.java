package com.crmws.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.crmws.dto.CommentDto;
import com.crmws.entity.ResponseMessage;
import com.crmws.service.CommentService;
import com.pay10.commons.repository.Comment;
import com.pay10.commons.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepo;

	@Override
	public ResponseMessage save(CommentDto commnetDto) {
		Comment chargebackComment = new Comment();
		BeanUtils.copyProperties(commnetDto, chargebackComment);

		commentRepo.save(chargebackComment);
		return new ResponseMessage("comment saved successfully", HttpStatus.OK);
	}

	@Override
	public List<CommentDto> findAll(String caseId) {
		List<Comment> chargebackComments = commentRepo.findByCaseId(caseId);
		List<CommentDto> comments = new ArrayList<CommentDto>();
		chargebackComments.stream().forEach(comment -> {
			CommentDto dto = new CommentDto();
			BeanUtils.copyProperties(comment, dto);
			comments.add(dto);
		});
		
		Collections.reverse(comments);
		return comments;
	}

}
