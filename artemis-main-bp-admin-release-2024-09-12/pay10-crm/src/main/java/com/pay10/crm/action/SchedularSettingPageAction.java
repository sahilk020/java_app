package com.pay10.crm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedularSettingPageAction extends AbstractSecureAction {

    private static Logger logger = LoggerFactory.getLogger(SchedularSettingPageAction.class.getName());
    private static final long serialVersionUID = -6879974923614009981L;

    @Override
    @SuppressWarnings("unchecked")
    public String execute() {

        return INPUT;
    }

    // To display page without using token
    @SuppressWarnings("unchecked")
    public String displayList() {
        return INPUT;
    }
}
