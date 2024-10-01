package com.pay10.commons.user;



import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "subscriber_config")
public class SubscriberConfig {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="subscriber_id")
	private Long subscriberId;


	@Column(name="pay_in_url")
	private String payInUrl;

	@Column(name="pay_out_url")
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
	private List<Events> event;
	
	@Column(name="created_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
	
	@Column(name="updated_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getPayInUrl() {
		return payInUrl;
	}

	public void setPayInUrl(String payInUrl) {
		this.payInUrl = payInUrl;
	}


	public String getPayOutUrl() {
		return payOutUrl;
	}

	public void setPayOutUrl(String payOutUrl) {
		this.payOutUrl = payOutUrl;
	}

	public String getAssociationId() {
		return associationId;
	}

	public void setAssociationId(String associationId) {
		this.associationId = associationId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Events> getEvent() {
		return event;
	}

	public void setEvent(List<Events> event) {
		this.event = event;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
}
