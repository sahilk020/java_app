package com.pay10.commons.dao;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.Menu;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class MenuDao extends HibernateAbstractDao {

    private static final String GET_ALL_ACTION_MENU = "SELECT actionName FROM Menu";
    Logger logger= LoggerFactory.getLogger(MenuDao.class.getName());

    public void save(Menu menu) throws DataAccessLayerException {
        super.save(menu);
    }

    @SuppressWarnings("unchecked")
    public List<Menu> getMenus() {
        return super.findAll(Menu.class);
    }


    @SuppressWarnings("unchecked")
    public List<Menu> getMenusById(Set<Long> ids) {
        Session session = HibernateSessionProvider.getSession();
        try {
            String hql = "FROM Menu m where m.id in (?1)";
            List<Menu> menus = session.createQuery(hql).setParameter(1, ids).getResultList();
            session.close();
            return menus;
        } finally {
            session.close();
        }
    }

    public Menu getParentIdById( long menuId){

        try(Session session=HibernateSessionProvider.getSession()){
            Query<Menu> query=session.createQuery("FROM Menu where id=:id",Menu.class)
                    .setParameter("id",menuId);
            Menu menu=query.getSingleResult();
             if(menu.getParentId()==0){
                logger.info("Parent Id{} ",menu.getParentId());
                return menu;
            }
            return new Menu();
        }
    }

    public Menu getMenu(long id) {
        return (Menu) super.find(Menu.class, id);
    }

    public void update(Menu menu) {
        super.saveOrUpdate(menu);
    }

    public void delete(long id) {
        super.delete(getMenu(id));
    }

    public List<String> getAllActionName() {
        return super.findAllBy(GET_ALL_ACTION_MENU);
    }
}
