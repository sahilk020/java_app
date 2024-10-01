package com.crmws.service;

import java.util.List;

import com.crmws.dto.CommentDto;
import com.crmws.entity.ResponseMessage;

public interface CommentService {

	ResponseMessage save(CommentDto commnetDto);
	
	List<CommentDto> findAll(String caseId);

}
