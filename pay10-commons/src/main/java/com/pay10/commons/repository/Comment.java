package com.pay10.commons.repository;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

/**
 * Sweety
 */

@Entity
@Proxy(lazy = false)
@Table(name = "comment")
public class Comment implements Serializable {

	
	private static final long serialVersionUID = 5620301053190940319L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long commentId;
	public long getCommentId() {
		return commentId;
	}

	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	private String caseId;
	private String comment;
	private String createdOn;
	private String createdBy;
	
	public Comment() {
	
	}

	public Comment(long commentId, String caseId, String comment, String createdOn, String createdBy) {
		
		this.commentId = commentId;
		this.caseId = caseId;
		this.comment = comment;
		this.createdOn = createdOn;
		this.createdBy = createdBy;
	}

	@Override
	public String toString() {
		return "Comment [commentId=" + commentId + ", caseId=" + caseId + ", comment=" + comment + ", createdOn="
				+ createdOn + ", createdBy=" + createdBy + "]";
	}


		
 }
