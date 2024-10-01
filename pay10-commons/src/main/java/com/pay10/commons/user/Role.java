package com.pay10.commons.user;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "role")
public class Role implements Serializable {

	private static final long serialVersionUID = -8794117484789299407L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String roleName;

	@Column
	private String description;

	@Column
	private boolean isActive;

	@Column
	private String createdBy;

	@Column
	/* private long createdAt; */
	private String createdAt;

	@Column
	private String updatedBy;

	@Column
	/* private long updatedAt; */
	private String updatedAt;

	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "groupId")
	private UserGroup userGroup;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/*
	 * public long getCreatedAt() { return createdAt; }
	 * 
	 * public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
	 */
	
	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	
	
	/*
	 * public long getUpdatedAt() { return updatedAt; }
	 * 
	 * public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
	 */

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@Override
	public String toString() {
		return "Role{" +
				"id=" + id +
				", roleName='" + roleName + '\'' +
				", description='" + description + '\'' +
				", isActive=" + isActive +
				", createdBy='" + createdBy + '\'' +
				", createdAt='" + createdAt + '\'' +
				", updatedBy='" + updatedBy + '\'' +
				", updatedAt='" + updatedAt + '\'' +
				", userGroup=" + userGroup +
				'}';
	}
}
