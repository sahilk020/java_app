package com.pay10.webhook.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "events")
public class Events implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="event_id")
	private Long eventId;
	
	@Column(name="event_name")
	private String eventName;
	
	@Column(name="event_type")
	private String eventType;
	
	@Column(name="version")
	private String version;
	
	@Column(name="display_name")
	private String displayName;
	
	@Column(name="description")
	private String description;
	
	
	@Column(name="created_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
	
	@Column(name="updated_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;
	
	
	
	

}
