package com.pay10.crm.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.crm.menu.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gajera Jaykumar
 */

public class AdminAuthorizationInterceptor extends AbstractInterceptor {
    private static Logger logger = LoggerFactory.getLogger(AdminAuthorizationInterceptor.class.getName());
    private static final long serialVersionUID = 852707981164914179L;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MenuService menuService;

    @Override
    @SuppressWarnings("unchecked")
    public String intercept(ActionInvocation actionInvocation) {
        try {
            Map<String, Object> sessionMap = actionInvocation.getInvocationContext().getSession();
            Object userObject = sessionMap.get(Constants.USER.getValue());
            String accessibleActions = sessionMap.get(Constants.ACCESSIBLE_PERMISSION.getValue()) != null
                    ? sessionMap.get(Constants.ACCESSIBLE_PERMISSION.getValue()).toString()
                    : "";

            if (null == userObject) {
                return Action.LOGIN;
            }

            User user = (User) userObject;
            if (user.getUserType().equals(UserType.ADMIN)) {
                return actionInvocation.invoke();
            }

            Map<String, String> accessibleAction = mapper.readValue(accessibleActions, HashMap.class);
            String actionName = actionInvocation.getProxy().getActionName();
             List<String> actions = menuService.getAllActions();
            if(user.getUserType().equals(UserType.MERCHANT)){
                if(actionName.equalsIgnoreCase("refundTransactionSearch") || actionName.equalsIgnoreCase("saleTransactionSearch")||actionName.equalsIgnoreCase("settledTransactionSearch")){
                   // logger.info(actionName+" in  ");
                   return actionInvocation.invoke();
                }
            }
            if (!accessibleAction.containsKey(actionName) && actions.contains(actionName)) {
                logger.error(actionName+ "Action is not permitted for  permission list in Admin Authorization Interceptor {}",accessibleAction);

                return Action.LOGIN;
            }
            return actionInvocation.invoke();
        } catch (Exception exception) {
         //   logger.error("Exception", exception);
            return Action.ERROR;
        }
    }
}
