package com.pay10.crm.role.Dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.Column;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRoleDto {

    private long id;
    @JsonProperty("roleName")
    private String roleName;

    private String description;

    @JsonProperty("isActive")
    private boolean isActive=true;

    @JsonProperty("groupId")
    private Long groupId;

    @JsonProperty("applicationId")
    private Long applicationId;


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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public String toString() {
        return "UserRoleDto{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", groupId=" + groupId +
                ", applicationId=" + applicationId +
                '}';
    }
}
