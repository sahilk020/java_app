package com.pay10.crm.action;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class WhiteListIpAction extends AbstractSecureAction {

    public List<Merchants> listMerchant = new ArrayList<Merchants>();

    @Autowired
    UserDao userDao;

    private User sessionUser = null;

        private static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(WhiteListIpAction.class.getName());

        public String execute() {

            sessionUser = (User) sessionMap.get(Constants.USER.getValue());
            logger.info("WhiteListed IP Action :: ");

            setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));

            return INPUT;
        }

    public List<Merchants> getListMerchant() {
        return listMerchant;
    }

    public void setListMerchant(List<Merchants> listMerchant) {
        this.listMerchant = listMerchant;
    }
}
