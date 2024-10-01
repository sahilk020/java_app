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
@Table(name = "permission")
public class Permission implements Serializable {

	private static final long serialVersionUID = 8892142660805215907L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// action name of struts
	@Column
	private String permission;

	@Column
	private String description;

	@Column
	private String createdBy;

	@Column
	private long createdAt;

	@Column
	private String updatedBy;

	@Column
	private long updatedAt;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "menuId")
	private Menu menu;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "Permission{" +
				"id=" + id +
				", permission='" + permission + '\'' +
				", description='" + description + '\'' +
				", createdBy='" + createdBy + '\'' +
				", createdAt=" + createdAt +
				", updatedBy='" + updatedBy + '\'' +
				", updatedAt=" + updatedAt +
				", menu=" + menu +
				'}';
	}
}
