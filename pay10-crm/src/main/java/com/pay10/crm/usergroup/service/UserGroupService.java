package com.pay10.crm.usergroup.service;

import java.util.List;

import com.pay10.commons.user.UserGroup;

public interface UserGroupService {

	public void create(UserGroup userGroup);

	public List<UserGroup> getUserGroups();

	public UserGroup getUserGroup(long id);

	public void update(UserGroup userGroup);

	public void delete(long id);
}
