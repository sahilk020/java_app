package com.pay10.crm.role.service;

import java.util.List;

import com.pay10.commons.user.Role;

public interface RoleService {

	public void create(Role role);

	public List<Role> getRoles(String createdBy);
	public List<Role> getRoleByEmailId(String emailId);

	public Role getRole(long id);

	public void update(Role role, List<Long> permissionIds);

	public void delete(long id);

	public List<Long> getAccessPermission(long roleId);
	public List<Role>getAllRole();
	public long getRoleIdByName(String roleName);
}
