package com.pay10.webhook.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "subscriber_config")
public class SubscriberConfig {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="subscriber_id")
	private Long subscriberId;
	
	@Column(name="pay_in_url")
	private String payInUrl;

	@Column(name="pay_Out_url")
	private String payOutUrl;

	@Column(name="association_id")
	private String associationId;
	
	@Column(name="active")
	private boolean active;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "subscriber_event",
            joinColumns = {@JoinColumn(name = "subscriber_id")},
            inverseJoinColumns = {@JoinColumn(name = "event_id")}
    )
	private List<Events> events;
	
	@Column(name="created_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
	
	@Column(name="updated_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;

}
