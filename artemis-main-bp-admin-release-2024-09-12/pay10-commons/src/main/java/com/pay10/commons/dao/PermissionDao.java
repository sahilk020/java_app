package com.pay10.commons.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.Menu;
import com.pay10.commons.user.Permission;

@Service
public class PermissionDao extends HibernateAbstractDao {

	public void create(Permission permission) throws DataAccessLayerException {
		super.save(permission);
	}

	public void delete(Permission permission) throws DataAccessLayerException {
		super.delete(permission);
	}

	public Permission find(Long id) throws DataAccessLayerException {
		return (Permission) super.find(Permission.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Permission> findAll() throws DataAccessLayerException {
		return super.findAll(Permission.class);
	}

	public void update(Permission permission) throws DataAccessLayerException {
		super.saveOrUpdate(permission);
	}

	public Map<Long, Set<Permission>> getPermissionsByMenuId() {
		List<Permission> permissions = findAll();
		Map<Long, Set<Permission>> permissionsByMenu = new HashMap<>();
		permissions.forEach(permission -> {
			Menu menu = permission.getMenu();
			if (!permissionsByMenu.containsKey(menu.getId())) {
				permissionsByMenu.put(menu.getId(), new HashSet<>());
			}
			permissionsByMenu.get(menu.getId()).add(permission);
		});
		return permissionsByMenu;
	}
}
