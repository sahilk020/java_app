package com.pay10.crm.role.Dto;

public class UserGroupDto {

    private long id;

    private String group;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UserGroupDto{" +
                "id=" + id +
                ", group='" + group + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
