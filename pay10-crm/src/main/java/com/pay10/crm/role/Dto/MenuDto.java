package com.pay10.crm.role.Dto;

import com.pay10.commons.user.Menu;
import com.pay10.commons.user.Permission;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

public class MenuDto {

    private long id;

    private String menuName;

    private String actionName;

    private String description;

    private long parentId;

    private String createdBy;

    private long createdAt;

    private String updatedBy;

    private long updatedAt;

    private String icon;
    private boolean needToShowInProfile;

    @Transient
    private Set<PermissionDto> permissions;

    @Transient
    private Set<MenuDto> subMenus = new HashSet<>();

    private int displayOrder;
    private long applicationId;

    private String permission;

    private String applicationName;


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

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String toString() {
        return "MenuDto{" +
                "id=" + id +
                ", menuName='" + menuName + '\'' +
                ", actionName='" + actionName + '\'' +
                ", description='" + description + '\'' +
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
                ", applicationId=" + applicationId +
                ", permission='" + permission + '\'' +
                ", applicationName='" + applicationName + '\'' +
                '}';
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
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

    public Set<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionDto> permissions) {
        this.permissions = permissions;
    }

    public Set<MenuDto> getSubMenus() {
        return subMenus;
    }

    public void setSubMenus(Set<MenuDto> subMenus) {
        this.subMenus = subMenus;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }


    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

}
