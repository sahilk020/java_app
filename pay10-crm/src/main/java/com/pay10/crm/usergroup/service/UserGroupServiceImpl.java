package com.pay10.crm.usergroup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.UserGroup;

@Service
public class UserGroupServiceImpl implements UserGroupService {

	@Autowired
	private UserGroupDao userGroupDao;

	@Override
	public void create(UserGroup userGroup) {
		userGroupDao.save(userGroup);
	}

	@Override
	public List<UserGroup> getUserGroups() {
		return userGroupDao.getUserGroups();
	}

	@Override
	public UserGroup getUserGroup(long id) {
		return userGroupDao.getUserGroup(id);
	}

	@Override
	public void update(UserGroup userGroup) {
		userGroupDao.update(userGroup);
	}

	@Override
	public void delete(long id) {
		userGroupDao.delete(id);
	}
}
