package com.crmws.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class CommentDto {


	@JsonProperty(value = "comment_id",access = Access.READ_ONLY)
	private long commentId;
	
	@JsonProperty("caseId")
	private String caseId;
	
	@JsonProperty("comment")
	private String comment;
	
	@JsonProperty("createdOn")
	private String createdOn;
	
	@JsonProperty("createdBy")
	private String createdBy;
	
}
