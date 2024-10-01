package com.pay10.commons.audittrail;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

/**
 * @author Jay gajera
 *
 */
@Service
public class ActionMessageByActionDao extends HibernateAbstractDao {

	public void save(ActionMessageByAction actionMessageByAction) throws DataAccessLayerException {
		super.save(actionMessageByAction);
	}

	@SuppressWarnings("unchecked")
	public List<ActionMessageByAction> getActionMessagesByAction() {
		return super.findAll(ActionMessageByAction.class);
	}

	public ActionMessageByAction getActionMessageByAction(long id) {
		return (ActionMessageByAction) super.find(ActionMessageByAction.class, id);
	}

	public void update(ActionMessageByAction actionMessageByAction) {
		super.saveOrUpdate(actionMessageByAction);
	}

	public void delete(long id) {
		super.delete(getActionMessageByAction(id));
	}

	@SuppressWarnings("unchecked")
	public ActionMessageByAction getByAction(String action) {
		Session session = HibernateSessionProvider.getSession();
		try {
			String query = "FROM ActionMessageByAction A where A.action=:action";
			List<ActionMessageByAction> rolesByGroup = session.createQuery(query).setParameter("action", action)
					.getResultList();
			return CollectionUtils.isNotEmpty(rolesByGroup) ? rolesByGroup.get(0) : null;
		} finally {
			session.close();
		}
	}
}
