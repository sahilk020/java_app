package com.pay10.commons.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "menu")
public class Menu implements Serializable,Comparable<Menu> {

	private static final long serialVersionUID = 4105329010448682199L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String menuName;

	@Column
	private String actionName;

	@Column
	private String description;

	@Column
	private boolean isActive;

	@Column
	private long parentId;

	@Column
	private String createdBy;

	@Column
	private long createdAt;

	@Column
	private String updatedBy;

	@Column
	private long updatedAt;

	private String icon;
	private boolean needToShowInProfile;

	@Transient
	private Set<Permission> permissions;

	@Transient
	private Set<Menu> subMenus = new HashSet<>();
	
	@Column
	private int displayOrder;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
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

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isNeedToShowInProfile() {
		return needToShowInProfile;
	}

	public void setNeedToShowInProfile(boolean needToShowInProfile) {
		this.needToShowInProfile = needToShowInProfile;
	}

	public Set<Menu> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(Set<Menu> subMenus) {
		this.subMenus = subMenus;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	@Override
	public int compareTo(Menu o) {
		return Integer.compare(getDisplayOrder(), o.getDisplayOrder());
	}

	@Override
	public String toString() {
		return "Menu{" +
				"id=" + id +
				", menuName='" + menuName + '\'' +
				", actionName='" + actionName + '\'' +
				", description='" + description + '\'' +
				", isActive=" + isActive +
				", parentId=" + parentId +
				", createdBy='" + createdBy + '\'' +
				", createdAt=" + createdAt +
				", updatedBy='" + updatedBy + '\'' +
				", updatedAt=" + updatedAt +
				", icon='" + icon + '\'' +
				", needToShowInProfile=" + needToShowInProfile +
				", permissions=" + permissions +
				", subMenus=" + subMenus +
				", displayOrder=" + displayOrder +
				'}';
	}
}
